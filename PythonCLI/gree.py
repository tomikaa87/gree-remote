import argparse
import base64
import sys

from cryptography.hazmat.primitives.ciphers import Cipher, algorithms, modes
from cryptography.hazmat.backends import default_backend
import json
import socket


GENERIC_KEY = "a3K8Bx%2r8Y7#xDh"


class ScanResult:
    ip = ''
    port = 0
    id = ''
    name = '<unknown>'

    def __init__(self, ip, port, id, name=''):
        self.ip = ip
        self.port = port
        self.id = id
        self.name = name


def send_data(ip, port, data):
    if args.verbose:
        print(f'send_data: ip={ip}, port={port}, data={data}')

    s = socket.socket(type=socket.SOCK_DGRAM, proto=socket.IPPROTO_UDP)
    s.settimeout(5)
    s.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    if hasattr(args, 'socket_interface') and args.socket_interface:
        s.setsockopt(socket.SOL_SOCKET, socket.SO_BINDTODEVICE, args.socket_interface.encode('ascii'))
    s.sendto(data, (ip, port))
    return s.recv(1024)


def create_request(tcid, pack_encrypted, i=0):
    return '{"cid":"app","i":' + str(i) + ',"t":"pack","uid":0,"tcid":"' + tcid + '","pack":"' + pack_encrypted + '"}'


def create_status_request_pack(tcid):
    return '{"cols":["Pow","Mod","SetTem","WdSpd","Air","Blo","Health","SwhSlp","Lig","SwingLfRig","SwUpDn","Quiet",' \
           '"Tur","StHt","TemUn","HeatCoolType","TemRec","SvSt"],"mac":"' + tcid + '","t":"status"}'


def add_pkcs7_padding(data):
    length = 16 - (len(data) % 16)
    padded = data + chr(length) * length
    return padded


def create_cipher(key):
    return Cipher(algorithms.AES(key.encode('utf-8')), modes.ECB(), backend=default_backend())


def decrypt(pack_encoded, key):
    decryptor = create_cipher(key).decryptor()
    pack_decoded = base64.b64decode(pack_encoded)
    pack_decrypted = decryptor.update(pack_decoded) + decryptor.finalize()
    pack_unpadded = pack_decrypted[0:pack_decrypted.rfind(b'}') + 1]
    return pack_unpadded.decode('utf-8')


def decrypt_generic(pack_encoded):
    return decrypt(pack_encoded, GENERIC_KEY)


def encrypt(pack, key):
    encryptor = create_cipher(key).encryptor()
    pack_padded = add_pkcs7_padding(pack)
    pack_encrypted = encryptor.update(bytes(pack_padded, encoding='utf-8')) + encryptor.finalize()
    pack_encoded = base64.b64encode(pack_encrypted)
    return pack_encoded.decode('utf-8')


def encrypt_generic(pack):
    return encrypt(pack, GENERIC_KEY)


def search_devices():
    print('Searching for devices using broadcast address: %s' % args.broadcast)

    s = socket.socket(type=socket.SOCK_DGRAM, proto=socket.IPPROTO_UDP)
    s.settimeout(5)
    s.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    s.setsockopt(socket.SOL_SOCKET, socket.SO_BROADCAST, 1)
    if hasattr(args, 'socket_interface') and args.socket_interface:
        s.setsockopt(socket.SOL_SOCKET, socket.SO_BINDTODEVICE, args.socket_interface.encode('ascii'))
    s.sendto(b'{"t":"scan"}', (args.broadcast, 7000))

    results = []

    while True:
        try:
            (data, address) = s.recvfrom(1024)

            if len(data) == 0:
                continue

            raw_json = data[0:data.rfind(b"}") + 1]

            if args.verbose:
                print(f'search_devices: data={data}, raw_json={raw_json}')

            resp = json.loads(raw_json)
            pack = json.loads(decrypt_generic(resp['pack']))

            cid = pack['cid'] if 'cid' in pack and len(pack['cid']) > 0 else \
                resp['cid'] if 'cid' in resp else '<unknown-cid>'

            results.append(ScanResult(address[0], address[1], cid, pack['name'] if 'name' in pack else '<unknown>'))

            if args.verbose:
                print(f'search_devices: pack={pack}')

        except socket.timeout:
            print('Search finished, found %d device(s)' % len(results))
            break

    if len(results) > 0:
        for r in results:
            bind_device(r)


def bind_device(search_result):
    print('Binding device: %s (%s, ID: %s)' % (search_result.ip, search_result.name, search_result.id))

    pack = '{"mac":"%s","t":"bind","uid":0}' % search_result.id
    pack_encrypted = encrypt_generic(pack)

    request = create_request(search_result.id, pack_encrypted, 1)
    result = send_data(search_result.ip, 7000, bytes(request, encoding='utf-8'))

    response = json.loads(result)
    if response["t"] == "pack":
        pack = response["pack"]

        pack_decrypted = decrypt_generic(pack)

        bind_resp = json.loads(pack_decrypted)

        if args.verbose:
            print(f'bind_device: resp={bind_resp}')

        if 't' in bind_resp and bind_resp["t"].lower() == "bindok":
            key = bind_resp['key']
            print('Bind to %s succeeded, key = %s' % (search_result.id, key))


def get_param():
    print(f'Getting parameters: {", ".join(args.params)}')

    cols = ','.join(f'"{i}"' for i in args.params)

    pack = f'{{"cols":[{cols}],"mac":"{args.id}","t":"status"}}'
    pack_encrypted = encrypt(pack, args.key)

    request = '{"cid":"app","i":0,"pack":"%s","t":"pack","tcid":"%s","uid":0}' \
              % (pack_encrypted, args.id)

    result = send_data(args.client, 7000, bytes(request, encoding='utf-8'))

    response = json.loads(result)

    if args.verbose:
        print(f'get_param: response={response}')

    if response["t"] == "pack":
        pack = response["pack"]

        pack_decrypted = decrypt(pack, args.key)
        pack_json = json.loads(pack_decrypted)

        if args.verbose:
            print(f'get_param: pack={pack}, json={pack_json}')

        for col, dat in zip(pack_json['cols'], pack_json['dat']):
            print('%s = %s' % (col, dat))


def set_param():
    kv_list = [i.split('=') for i in args.params]
    errors = [i for i in kv_list if len(i) != 2]

    if len(errors) > 0:
        print(f'Invalid parameters detected: {errors}')
        exit(1)

    print(f'Setting parameters: {", ".join("=".join(i) for i in kv_list)}')

    opts = ','.join(f'"{i[0]}"' for i in kv_list)
    ps = ','.join(i[1] for i in kv_list)

    pack = f'{{"opt":[{opts}],"p":[{ps}],"t":"cmd"}}'
    print(pack)
    pack_encrypted = encrypt(pack, args.key)

    request = '{"cid":"app","i":0,"pack":"%s","t":"pack","tcid":"%s","uid":0}' \
              % (pack_encrypted, args.id)

    result = send_data(args.client, 7000, bytes(request, encoding='utf-8'))

    response = json.loads(result)

    if args.verbose:
        print(f'set_param: response={response}')

    if response["t"] == "pack":
        pack = response["pack"]

        pack_decrypted = decrypt(pack, args.key)
        pack_json = json.loads(pack_decrypted)

        if args.verbose:
            print(f'set_param: pack={pack}')

        if pack_json['r'] != 200:
            print('Failed to set parameter')


if __name__ == '__main__':
    parser = argparse.ArgumentParser()

    parser.add_help = True
    parser.add_argument('command', help='You can use the following commands: search, get, set')
    parser.add_argument('-c', '--client', help='IP address of the client device')
    parser.add_argument('-b', '--broadcast', help='Broadcast IP address of the network the devices connecting to')
    parser.add_argument('-i', '--id', help='Unique ID of the device')
    parser.add_argument('-k', '--key', help='Unique encryption key of the device')
    parser.add_argument('--verbose', help='Enable verbose logging', action='store_true')
    if sys.platform == 'linux':
        parser.add_argument('--socket-interface', help='Bind the socket to a specific network interface')
    parser.add_argument('params', nargs='*', default=None, type=str)

    args = parser.parse_args()

    command = args.command.lower()
    if command == 'search':
        if args.broadcast is None:
            print('Error: search command requires a broadcast IP address')
            exit(1)
        search_devices()
    elif command == 'get':
        if args.params is None or len(args.params) == 0 or args.client is None or args.id is None or args.key is None:
            print('Error: get command requires a parameter name, a client IP (-c), a device ID (-i) and a device key '
                  '(-k)')
            exit(1)
        get_param()
    elif command == 'set':
        if args.params is None or len(args.params) == 0 or args.client is None or args.id is None or args.key is None:
            print('Error: set command requires at least one key=value pair, a client IP (-c), a device ID (-i) and a '
                  'device key (-k)')
            exit(1)
        set_param()
    else:
        print('Error: unknown command "%s"' % args.command)
        exit(1)
