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

#include "../Cryptography/Cryptography.h"

#include <nlohmann/json.hpp>

#include <vector>

#define DEBUG_PACK_PLAIN_TEXT 1

namespace Gree
{
    class MessageBuilder
    {
    public:
        [[nodiscard]] std::string build() const
        {
            auto messageJson = _json;
            if (!_pack.empty()) {
                Cryptography::ErrorCode ec;

                auto cipherText = Cryptography::encrypt(
                    _pack.dump(),
                    _encryptionKey,
                    ec
                );

                if (ec != Cryptography::ErrorCode::NoError) {
                    return {};
                }

#if DEBUG_PACK_PLAIN_TEXT == 1
                messageJson["pack"] = _pack.dump();
#else
                messageJson["pack"] = std::string{
                    reinterpret_cast<const char*>(cipherText.data()),
                    cipherText.size()
                };
#endif
            }

            auto serialized = messageJson.dump();

            return serialized;
        }

        void setEncryptionKey(std::string key)
        {
            _encryptionKey = std::move(key);
        }

        template <typename ValueType>
        MessageBuilder& addField(const std::string& name, ValueType&& value)
        {
            _json[name] = std::forward<ValueType>(value);
            return *this;
        }

        template<typename ValueType>
        MessageBuilder& addPackField(const std::string& name, ValueType&& value)
        {
            _pack[name] = value;
            return *this;
        }

    private:
        nlohmann::json _json;
        nlohmann::json _pack;
        std::string _encryptionKey{ GenericEncryptionKey };
    };

    class GenericRequestBuilder : public MessageBuilder
    {
    public:
        explicit GenericRequestBuilder(const std::string& id, const std::string& packType, const int i = 0)
        {
            addField("cid", "app");
            addField("i", i);
            addField("t", "pack");
            addField("uid", 0);
            addField("tcid", id);

            addPackField("t", packType);
        }
    };

    namespace Messages
    {
        class Scan : public MessageBuilder
        {
        public:
            Scan()
            {
                addField("t", "scan");
            }
        };

        class BindRequest : public GenericRequestBuilder
        {
        public:
            explicit BindRequest(const std::string& id)
                : GenericRequestBuilder{ id, "bind", 1 }
            {
                addPackField("mac", id);
                addPackField("uid", 0);
            }
        };

        class GetParamsRequest : public GenericRequestBuilder
        {
        public:
            GetParamsRequest(const std::string& id, const std::string& encryptionKey)
                : GenericRequestBuilder{ id, "status", 0 }
            {
                setEncryptionKey(encryptionKey);
                addPackField("uid", 0);
            }

            GetParamsRequest& addParams(const std::vector<std::string>& names)
            {
                addPackField("cols", names);
                return *this;
            }
        };
    }
}