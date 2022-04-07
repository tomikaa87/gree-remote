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

#include "Utils.h"

#ifdef SSL_USE_WOLFSSL
#include "WolfSslCryptography.h"
#endif

namespace Gree
{
    template <typename AesEncryption, typename Base64Encoding>
    struct CryptographyImpl
    {
        static void init()
        {
            AesEncryption::init();
        }

        static void cleanUp()
        {
            AesEncryption::cleanUp();
        }

        enum class ErrorCode
        {
            NoError,
            InvalidKeyLength,
            PaddingFailed,
            EncryptionFailed,
            EncodingFailed
        };

        static std::vector<uint8_t> encrypt(const std::string& plainText, const std::string& key, ErrorCode& ec)
        {
            if (key.size() != AesEncryption::KeySize) {
                ec = ErrorCode::InvalidKeyLength;
                return {};
            }

            const auto message = Utils::padString(plainText, AesEncryption::BlockSize);

            if (message.empty()) {
                ec = ErrorCode::PaddingFailed;
                return {};
            }

            const auto cipherText = AesEncryption::encrypt(message, key);

            if (cipherText.empty()) {
                ec = ErrorCode::EncryptionFailed;
                return {};
            }

            const auto encodedText = Base64Encoding::encode(cipherText);

            if (encodedText.empty()) {
                ec = ErrorCode::EncodingFailed;
                return {};
            }
            
            return encodedText;
        }

        static std::string decrypt(const std::vector<uint8_t>& encodedCipherText, const std::string& key, ErrorCode& ec)
        {
            if (key.size() != AesEncryption::KeySize) {
                ec = ErrorCode::InvalidKeyLength;
                return {};
            }

            const auto cipherText = Base64Encoding::decode(encodedCipherText);

            if (cipherText.empty()) {
                ec = ErrorCode::EncodingFailed;
                return {};
            }

            const auto message = AesEncryption::decrypt(cipherText, key);

            if (message.empty()) {
                ec = ErrorCode::EncryptionFailed;
                return {};
            }

            const auto unpaddedMessage = Utils::unpadString(message, AesEncryption::BlockSize);

            if (unpaddedMessage.empty()) {
                ec = ErrorCode::PaddingFailed;
                return {};
            }

            return unpaddedMessage;
        }

    private:
        CryptographyImpl() = default;
    };

#ifdef SSL_USE_WOLFSSL
    using Cryptography = CryptographyImpl<WolfSslAesEncryption, WolfSslBase64Encoding>;
#else
    #error Unsupported SSL configuration for Cryptograpy
#endif
}