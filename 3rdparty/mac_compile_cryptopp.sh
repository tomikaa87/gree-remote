#!/bin/sh

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )/cryptopp"
make -C "$DIR" distclean
make -j9 -C "$DIR" CXX="c++ -mmacosx-version-min=10.9"
