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

#include "Utils.h"

namespace Gree
{
    namespace Utils
    {
        std::vector<uint8_t> padString(const std::string& input, const std::size_t blockSize)
        {
            const std::size_t inputSize = input.size();
            const std::size_t remainder = inputSize % blockSize;
            const std::size_t length = remainder > 0 ? inputSize + blockSize - remainder : inputSize;

            std::vector<uint8_t> output;
            output.resize(length);
            std::copy(
                std::cbegin(input),
                std::cend(input),
                std::begin(output)
            );
            
            for (auto it = std::begin(output) + inputSize; it != std::end(output); ++it) {
                *it = length - inputSize;
            }

            return output;
        }

        std::string unpadString(const std::vector<uint8_t>& input, const std::size_t blockSize)
        {
            auto it = std::crbegin(input);
            
            if (it == std::crend(input)) {
                return {};
            }

            if (*it < blockSize) {
                const auto padBytes = *it;
                auto padCountMismatch = false;
                for (auto i = 0; i < padBytes - 1; ++i) {
                    ++it;
                    if (*it != padBytes) {
                        padCountMismatch = true;
                        break;
                    }
                }

                if (!padCountMismatch) {
                    return std::string(reinterpret_cast<const char*>(input.data()), input.size() - padBytes);
                }
            }

            return std::string(reinterpret_cast<const char*>(input.data()), input.size());
        }
    }
}