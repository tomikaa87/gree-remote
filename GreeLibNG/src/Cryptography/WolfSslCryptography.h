/*
    GreeLib-NG - Next generation C++ library for Gree devices
    Copyright (C) 2022  Tamas Karpati <tomikaa87@gmail.com>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

#pragma once

#ifdef SSL_USE_WOLFSSL

#include <wolfssl/wolfcrypt/aes.h>
#include <wolfssl/wolfcrypt/coding.h>
#include <wolfssl/wolfcrypt/error-crypt.h>

#include <string>
#include <vector>

namespace Gree
{
    struct WolfSslAesEncryption
    {
        static constexpr auto KeySize = AES_128_KEY_SIZE;
        static constexpr auto BlockSize = AES_BLOCK_SIZE;

        static void init()
        {
            wolfCrypt_Init();
        }

        static void cleanUp()
        {
            wolfCrypt_Cleanup();
        }

        static std::vector<uint8_t> encrypt(const std::vector<uint8_t>& message, const std::string& key)
        {
            if (message.size() % AES_BLOCK_SIZE != 0) {
                return {};
            }

            static const auto AesDeleter = [](Aes* p) {
                wc_AesFree(p);
                delete p;
            };
            const auto crypt = std::unique_ptr<Aes, decltype(AesDeleter)>(new Aes, AesDeleter);

            if (const auto result = wc_AesInit(crypt.get(), nullptr, INVALID_DEVID); result != 0) {
                return {};
            }

            if (const auto result = wc_AesSetKey(
                crypt.get(),
                reinterpret_cast<const uint8_t*>(key.data()),
                key.length(),
                nullptr,
                AES_ENCRYPTION
            ); result != 0) {
                return {};
            }

            std::vector<uint8_t> cipherText;
            cipherText.resize(message.size());

            if (const auto result = wc_AesEcbEncrypt(
                crypt.get(),
                cipherText.data(),
                message.data(),
                message.size()
            ); result != 0) {
                return {};
            }

            return cipherText;
        }

        static std::vector<uint8_t> decrypt(const std::vector<uint8_t>& cipherText, const std::string& key)
        {
            if (cipherText.size() % AES_BLOCK_SIZE != 0) {
                return {};
            }

            static const auto AesDeleter = [](Aes* p) {
                wc_AesFree(p);
                delete p;
            };
            const auto crypt = std::unique_ptr<Aes, decltype(AesDeleter)>(new Aes, AesDeleter);

            if (const auto result = wc_AesInit(crypt.get(), nullptr, INVALID_DEVID); result != 0) {
                return {};
            }

            if (const auto result = wc_AesSetKey(
                crypt.get(),
                reinterpret_cast<const uint8_t*>(key.data()),
                key.length(),
                nullptr,
                AES_DECRYPTION
            ); result != 0) {
                return {};
            }

            std::vector<uint8_t> message;
            message.resize(cipherText.size());

            if (const auto result = wc_AesEcbDecrypt(
                crypt.get(),
                message.data(),
                cipherText.data(),
                cipherText.size()
            ); result != 0) {
                return {};
            }

            return message;
        }

    private:
        WolfSslAesEncryption() = default;
    };

    struct WolfSslBase64Encoding
    {
        static std::vector<uint8_t> encode(const std::vector<uint8_t>& input)
        {
            std::vector<uint8_t> output;
            output.resize(input.size() * 2);

            uint32_t outputLength = output.size();

            const auto result = Base64_Encode(
                input.data(),
                input.size(),
                output.data(),
                &outputLength
            );

            if (result != 0) {
                return {};
            }

            output.resize(outputLength);

            return output;
        }

        static std::vector<uint8_t> decode(const std::vector<uint8_t>& input)
        {
            std::vector<uint8_t> output;
            output.resize(input.size());

            uint32_t outputLength = input.size();

            if (const auto result = Base64_Decode(
                input.data(),
                input.size(),
                output.data(),
                &outputLength
            ); result != 0) {
                return {};
            }

            output.resize(outputLength);

            return output;
        }

    private:
        WolfSslBase64Encoding() = default;
    };
}

#endif