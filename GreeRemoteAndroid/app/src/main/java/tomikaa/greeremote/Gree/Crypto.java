package tomikaa.greeremote.Gree;

import android.util.Base64;
import android.util.Log;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by tomikaa on 2017. 11. 26..
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
