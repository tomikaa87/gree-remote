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

#include "GreeLib.h"

#include "Cryptography/Cryptography.h"

namespace Gree
{
    void init()
    {
        Cryptography::init();
    }

    void cleanUp()
    {
        Cryptography::cleanUp();
    }

    bool test()
    {
        Cryptography::ErrorCode ec{};

        const std::string originalText{ "0123456789012345" };

        const auto cipherText = Cryptography::encrypt(originalText, "a3K8Bx%2r8Y7#xDh", ec);

        if (ec != Cryptography::ErrorCode::NoError) {
            return false;
        }

        const auto plainText = Cryptography::decrypt(cipherText, "a3K8Bx%2r8Y7#xDh", ec);

        if (ec != Cryptography::ErrorCode::NoError) {
            return false;
        }

        if (originalText != plainText) {
            return false;
        }

        return true;
    }
}