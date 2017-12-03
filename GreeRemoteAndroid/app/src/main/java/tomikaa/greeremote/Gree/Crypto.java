package tomikaa.greeremote.Gree;

import android.util.Base64;
import android.util.Log;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/*
 * This file is part of GreeRemoteAndroid.
 *
 * GreeRemoteAndroid is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GreeRemoteAndroid is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GreeRemoteAndroid. If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * Created by tomikaa87 <https://github.com/tomikaa87> on 2017-11-26.
 */

public class Crypto {
    private static final String LOG_TAG = "Crypto";
    public static String GENERIC_KEY = "a3K8Bx%2r8Y7#xDh";

    public static String encrypt(String plainText, String key) {
        try {
            Cipher c = Cipher.getInstance("AES/ECB/PKCS5Padding");
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes("ASCII"), "AES");
            c.init(Cipher.ENCRYPT_MODE, keySpec);

            byte[] encrypted = c.doFinal(plainText.getBytes("UTF-8"));
            String encoded = new String(Base64.encode(encrypted, Base64.DEFAULT));

            return encoded;
        } catch (Exception e) {
            Log.e(LOG_TAG, "Pack encryption failed. Error: " + e.getMessage());
        }

        return "";
    }

    public static String decrypt(String encrypted, String key) {
        try {
            byte[] decoded = Base64.decode(encrypted, Base64.DEFAULT);

            Cipher c = Cipher.getInstance("AES/ECB/PKCS5Padding");
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes("ASCII"), "AES");
            c.init(Cipher.DECRYPT_MODE, keySpec);

            byte[] decrypted = c.doFinal(decoded);
            return new String(decrypted);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Pack decryption failed. Error: " + e.getMessage());
        }

        return "";
    }
}
