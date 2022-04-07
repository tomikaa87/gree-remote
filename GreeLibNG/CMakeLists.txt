#    GreeLib-NG - Next generation C++ library for Gree devices
#    Copyright (C) 2022  Tamas Karpati <tomikaa87@gmail.com>
#
#    This program is free software: you can redistribute it and/or modify
#    it under the terms of the GNU General Public License as published by
#    the Free Software Foundation, either version 3 of the License, or
#    (at your option) any later version.
#
#    This program is distributed in the hope that it will be useful,
#    but WITHOUT ANY WARRANTY; without even the implied warranty of
#    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#    GNU General Public License for more details.
#
#    You should have received a copy of the GNU General Public License
#    along with this program.  If not, see <http://www.gnu.org/licenses/>.

cmake_minimum_required(VERSION 3.20)

project(greelib-ng)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED TRUE)

set(SSL_USE_WOLFSSL TRUE)

if(SSL_USE_WOLFSSL)
    add_compile_definitions(
        HAVE_AES_ECB
        WOLFSSL_AES_DIRECT
    )

    if(APPLE)
        add_subdirectory(3rdparty/wolfssl)
    endif()
endif()

add_library(greelib-ng
    src/GreeLib.cpp
    src/Cryptography/Cryptography.h
    src/Cryptography/Utils.cpp
    src/Cryptography/Utils.h
)

target_include_directories(greelib-ng
    PUBLIC
        include
)

target_sources(greelib-ng
    PUBLIC
        include/GreeLib.h
)

if(SSL_USE_WOLFSSL)
    target_sources(greelib-ng
        PRIVATE
            src/Cryptography/WolfSslCryptography.h
    )

    target_compile_definitions(greelib-ng
        PRIVATE
            SSL_USE_WOLFSSL
    )

    target_include_directories(greelib-ng
        PRIVATE
            3rdparty/wolfssl/wolfssl
    )

    target_link_directories(greelib-ng
        PRIVATE
            ${CMAKE_BINARY_DIR/3rdparty/wolfssl}
    )

    target_link_libraries(greelib-ng
        PRIVATE
            wolfssl
    )
endif()

add_executable(test src/test_main.cpp)
target_link_libraries(test greelib-ng)