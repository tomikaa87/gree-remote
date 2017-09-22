#-------------------------------------------------
#
# Project created by QtCreator 2017-09-18T14:29:43
#
#-------------------------------------------------

QT       += core gui network

greaterThan(QT_MAJOR_VERSION, 4): QT += widgets

TARGET = GreeRemote
TEMPLATE = app

# The following define makes your compiler emit warnings if you use
# any feature of Qt which has been marked as deprecated (the exact warnings
# depend on your compiler). Please consult the documentation of the
# deprecated API in order to know how to port your code away from it.
DEFINES += QT_DEPRECATED_WARNINGS

# You can also make your code fail to compile if you use deprecated APIs.
# In order to do so, uncomment the following line.
# You can also select to disable deprecated APIs only up to a certain version of Qt.
DEFINES += QT_DISABLE_DEPRECATED_BEFORE=0x060000    # disables all the APIs deprecated before Qt 6.0.0

SOURCES += \
    src/crypto.cpp \
    src/device.cpp \
    src/devicefinder.cpp \
    src/main.cpp \
    src/mainwindow.cpp \
    src/protocolutils.cpp

HEADERS += \
    ../../../Downloads/cryptopp565/3way.h \
    ../../../Downloads/cryptopp565/adler32.h \
    ../../../Downloads/cryptopp565/aes.h \
    ../../../Downloads/cryptopp565/algebra.h \
    ../../../Downloads/cryptopp565/algparam.h \
    ../../../Downloads/cryptopp565/arc4.h \
    ../../../Downloads/cryptopp565/argnames.h \
    ../../../Downloads/cryptopp565/asn.h \
    ../../../Downloads/cryptopp565/authenc.h \
    ../../../Downloads/cryptopp565/base32.h \
    ../../../Downloads/cryptopp565/base64.h \
    ../../../Downloads/cryptopp565/basecode.h \
    ../../../Downloads/cryptopp565/bench.h \
    ../../../Downloads/cryptopp565/blake2.h \
    ../../../Downloads/cryptopp565/blowfish.h \
    ../../../Downloads/cryptopp565/blumshub.h \
    ../../../Downloads/cryptopp565/camellia.h \
    ../../../Downloads/cryptopp565/cast.h \
    ../../../Downloads/cryptopp565/cbcmac.h \
    ../../../Downloads/cryptopp565/ccm.h \
    ../../../Downloads/cryptopp565/chacha.h \
    ../../../Downloads/cryptopp565/channels.h \
    ../../../Downloads/cryptopp565/cmac.h \
    ../../../Downloads/cryptopp565/config.h \
    ../../../Downloads/cryptopp565/cpu.h \
    ../../../Downloads/cryptopp565/crc.h \
    ../../../Downloads/cryptopp565/cryptlib.h \
    ../../../Downloads/cryptopp565/default.h \
    ../../../Downloads/cryptopp565/des.h \
    ../../../Downloads/cryptopp565/dh.h \
    ../../../Downloads/cryptopp565/dh2.h \
    ../../../Downloads/cryptopp565/dll.h \
    ../../../Downloads/cryptopp565/dmac.h \
    ../../../Downloads/cryptopp565/dsa.h \
    ../../../Downloads/cryptopp565/eax.h \
    ../../../Downloads/cryptopp565/ec2n.h \
    ../../../Downloads/cryptopp565/eccrypto.h \
    ../../../Downloads/cryptopp565/ecp.h \
    ../../../Downloads/cryptopp565/elgamal.h \
    ../../../Downloads/cryptopp565/emsa2.h \
    ../../../Downloads/cryptopp565/eprecomp.h \
    ../../../Downloads/cryptopp565/esign.h \
    ../../../Downloads/cryptopp565/factory.h \
    ../../../Downloads/cryptopp565/fhmqv.h \
    ../../../Downloads/cryptopp565/files.h \
    ../../../Downloads/cryptopp565/filters.h \
    ../../../Downloads/cryptopp565/fips140.h \
    ../../../Downloads/cryptopp565/fltrimpl.h \
    ../../../Downloads/cryptopp565/gcm.h \
    ../../../Downloads/cryptopp565/gf256.h \
    ../../../Downloads/cryptopp565/gf2_32.h \
    ../../../Downloads/cryptopp565/gf2n.h \
    ../../../Downloads/cryptopp565/gfpcrypt.h \
    ../../../Downloads/cryptopp565/gost.h \
    ../../../Downloads/cryptopp565/gzip.h \
    ../../../Downloads/cryptopp565/hex.h \
    ../../../Downloads/cryptopp565/hkdf.h \
    ../../../Downloads/cryptopp565/hmac.h \
    ../../../Downloads/cryptopp565/hmqv.h \
    ../../../Downloads/cryptopp565/hrtimer.h \
    ../../../Downloads/cryptopp565/ida.h \
    ../../../Downloads/cryptopp565/idea.h \
    ../../../Downloads/cryptopp565/integer.h \
    ../../../Downloads/cryptopp565/iterhash.h \
    ../../../Downloads/cryptopp565/keccak.h \
    ../../../Downloads/cryptopp565/lubyrack.h \
    ../../../Downloads/cryptopp565/luc.h \
    ../../../Downloads/cryptopp565/mars.h \
    ../../../Downloads/cryptopp565/md2.h \
    ../../../Downloads/cryptopp565/md4.h \
    ../../../Downloads/cryptopp565/md5.h \
    ../../../Downloads/cryptopp565/mdc.h \
    ../../../Downloads/cryptopp565/mersenne.h \
    ../../../Downloads/cryptopp565/misc.h \
    ../../../Downloads/cryptopp565/modarith.h \
    ../../../Downloads/cryptopp565/modes.h \
    ../../../Downloads/cryptopp565/modexppc.h \
    ../../../Downloads/cryptopp565/mqueue.h \
    ../../../Downloads/cryptopp565/mqv.h \
    ../../../Downloads/cryptopp565/nbtheory.h \
    ../../../Downloads/cryptopp565/network.h \
    ../../../Downloads/cryptopp565/nr.h \
    ../../../Downloads/cryptopp565/oaep.h \
    ../../../Downloads/cryptopp565/oids.h \
    ../../../Downloads/cryptopp565/osrng.h \
    ../../../Downloads/cryptopp565/ossig.h \
    ../../../Downloads/cryptopp565/panama.h \
    ../../../Downloads/cryptopp565/pch.h \
    ../../../Downloads/cryptopp565/pkcspad.h \
    ../../../Downloads/cryptopp565/polynomi.h \
    ../../../Downloads/cryptopp565/pssr.h \
    ../../../Downloads/cryptopp565/pubkey.h \
    ../../../Downloads/cryptopp565/pwdbased.h \
    ../../../Downloads/cryptopp565/queue.h \
    ../../../Downloads/cryptopp565/rabin.h \
    ../../../Downloads/cryptopp565/randpool.h \
    ../../../Downloads/cryptopp565/rc2.h \
    ../../../Downloads/cryptopp565/rc5.h \
    ../../../Downloads/cryptopp565/rc6.h \
    ../../../Downloads/cryptopp565/rdrand.h \
    ../../../Downloads/cryptopp565/resource.h \
    ../../../Downloads/cryptopp565/rijndael.h \
    ../../../Downloads/cryptopp565/ripemd.h \
    ../../../Downloads/cryptopp565/rng.h \
    ../../../Downloads/cryptopp565/rsa.h \
    ../../../Downloads/cryptopp565/rw.h \
    ../../../Downloads/cryptopp565/safer.h \
    ../../../Downloads/cryptopp565/salsa.h \
    ../../../Downloads/cryptopp565/seal.h \
    ../../../Downloads/cryptopp565/secblock.h \
    ../../../Downloads/cryptopp565/seckey.h \
    ../../../Downloads/cryptopp565/seed.h \
    ../../../Downloads/cryptopp565/serpent.h \
    ../../../Downloads/cryptopp565/serpentp.h \
    ../../../Downloads/cryptopp565/sha.h \
    ../../../Downloads/cryptopp565/sha3.h \
    ../../../Downloads/cryptopp565/shacal2.h \
    ../../../Downloads/cryptopp565/shark.h \
    ../../../Downloads/cryptopp565/simple.h \
    ../../../Downloads/cryptopp565/skipjack.h \
    ../../../Downloads/cryptopp565/smartptr.h \
    ../../../Downloads/cryptopp565/socketft.h \
    ../../../Downloads/cryptopp565/sosemanuk.h \
    ../../../Downloads/cryptopp565/square.h \
    ../../../Downloads/cryptopp565/stdcpp.h \
    ../../../Downloads/cryptopp565/strciphr.h \
    ../../../Downloads/cryptopp565/tea.h \
    ../../../Downloads/cryptopp565/tiger.h \
    ../../../Downloads/cryptopp565/trap.h \
    ../../../Downloads/cryptopp565/trdlocal.h \
    ../../../Downloads/cryptopp565/trunhash.h \
    ../../../Downloads/cryptopp565/ttmac.h \
    ../../../Downloads/cryptopp565/twofish.h \
    ../../../Downloads/cryptopp565/validate.h \
    ../../../Downloads/cryptopp565/vmac.h \
    ../../../Downloads/cryptopp565/wait.h \
    ../../../Downloads/cryptopp565/wake.h \
    ../../../Downloads/cryptopp565/whrlpool.h \
    ../../../Downloads/cryptopp565/winpipes.h \
    ../../../Downloads/cryptopp565/words.h \
    ../../../Downloads/cryptopp565/xtr.h \
    ../../../Downloads/cryptopp565/xtrcrypt.h \
    ../../../Downloads/cryptopp565/zdeflate.h \
    ../../../Downloads/cryptopp565/zinflate.h \
    ../../../Downloads/cryptopp565/zlib.h \
    src/crypto.h \
    src/device.h \
    src/devicedescriptor.h \
    src/devicefinder.h \
    src/mainwindow.h \
    src/protocolutils.h

FORMS += \
    src/mainwindow.ui

RESOURCES += \
    resources/res.qrc

LIBS += \
    -L/Users/tomikaa/Downloads/cryptopp565 \
    -lcryptopp

INCLUDEPATH += \
    /Users/tomikaa/Downloads/cryptopp565

macx: QMAKE_INFO_PLIST = Info.plist
