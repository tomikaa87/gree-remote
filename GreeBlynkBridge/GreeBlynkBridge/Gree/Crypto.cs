namespace GreeBlynkBridge.Gree
{
    using System;
    using System.Security.Cryptography;
    using System.Text;

    internal static class Crypto
    {
        public static readonly string GenericKey = "a3K8Bx%2r8Y7#xDh";

        private static readonly byte[] CbcKey = new byte[]
        {
                (byte)17,
                (byte)/*-101*/0x9b,
                (byte)/*-16*/0xf0,
                (byte)/*-50*/0xce,

                (byte)16,
                (byte)88,
                (byte)114,
                (byte)75,

                (byte)31,
                (byte)18,
                (byte)/*-84*/0xac,
                (byte)/*-87*/0xa9,

                (byte)51,
                (byte)/*-17*/0xef,
                (byte)16,
                (byte)69
        };

        private static readonly byte[] CbcIv = new byte[] 
        {
                (byte)86,
                (byte)33,
                (byte)23,
                (byte)0x99,

                (byte)109,
                (byte)9,
                (byte)61,
                (byte)40,

                (byte)0xdd,
                (byte)0xb3,
                (byte)0xba,
                (byte)105,

                (byte)90,
                (byte)46,
                (byte)111,
                (byte)88
        };

        public static string EncryptGenericData(string input)
        {
            return EncryptData(input, GenericKey);
        }

        public static string EncryptData(string input, string key)
        {
            try
            {
                var aes = CreateAes(key);
                var encryptor = aes.CreateEncryptor();
                var inputBuffer = Encoding.UTF8.GetBytes(input);
                var encrypted = encryptor.TransformFinalBlock(inputBuffer, 0, inputBuffer.Length);
                return Convert.ToBase64String(encrypted, Base64FormattingOptions.None);
            }
            catch (Exception e)
            {
                Console.WriteLine($"Failed to encrypt data. Exception: {e}");
                return null;
            }
        }

        public static string DecryptGenericData(string input)
        {
            return DecryptData(input, GenericKey);
        }

        public static string DecryptData(string input, string key)
        {
            try
            {
                var encrypted = Convert.FromBase64String(input);
                var aes = CreateAes(key);
                var decryptor = aes.CreateDecryptor();
                var decrypted = decryptor.TransformFinalBlock(encrypted, 0, encrypted.Length);
                return Encoding.UTF8.GetString(decrypted);
            }
            catch (Exception e)
            {
                Console.WriteLine($"Failed to decrypt data. Exception: {e}");
                return null;
            }
        }

        public static string DecryptCbcData(string input)
        {
            try
            {
                var encrypted = Convert.FromBase64String(input);
                var aes = CreateAesCbc();
                var decryptor = aes.CreateDecryptor();
                var decrypted = decryptor.TransformFinalBlock(encrypted, 0, encrypted.Length);
                return Encoding.UTF8.GetString(decrypted);
            }
            catch (Exception e)
            {
                Console.WriteLine($"Failed to decrypt data. Exception: {e}");
                return null;
            }
        }

        private static Aes CreateAes(string key)
        {
            var aes = Aes.Create();

            aes.BlockSize = 128;
            aes.KeySize = 256;
            aes.Key = Encoding.ASCII.GetBytes(key);
            aes.Mode = CipherMode.ECB;
            aes.Padding = PaddingMode.PKCS7;

            return aes;
        }

        private static Aes CreateAesCbc()
        {
            var aes = Aes.Create();

            aes.BlockSize = 128;
            aes.KeySize = 256;
            aes.Key = CbcKey;
            aes.IV = CbcIv;
            aes.Mode = CipherMode.CBC;
            aes.Padding = PaddingMode.PKCS7;

            return aes;
        }
    }
}
