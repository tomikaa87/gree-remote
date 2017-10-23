package tomikaa.greeremote.device;

import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;

import java.lang.reflect.GenericSignatureFormatError;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import tomikaa.greeremote.device.packets.BindPacket;
import tomikaa.greeremote.device.packets.DeviceRequestPacket;
import tomikaa.greeremote.device.packets.ScanPacket;

/**
 * Created by tomikaa on 2017. 10. 23..
 */

public class ProtocolUtils {
    public static final String GENERIC_AES_KEY = "a3K8Bx%2r8Y7#xDh";

    public static String encryptPack(String packJson, String key) {
        try {
            Cipher c = Cipher.getInstance("AES/ECB/PKCS5Padding");
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes("ASCII"), "AES");
            c.init(Cipher.ENCRYPT_MODE, keySpec);

            byte[] encrypted = c.doFinal(packJson.getBytes("UTF-8"));
            String encoded = new String(Base64.encode(encrypted, Base64.DEFAULT));

            return encoded;
        } catch (Exception e) {
            Log.e("ProtocolUtils", "Pack encryption failed. Error: " + e.getMessage());
        }
        return "";
    }

    public static String decryptPack(byte[] packEncrypted, String key) {
        try {
            byte[] decoded = Base64.decode(packEncrypted, Base64.DEFAULT);

            Cipher c = Cipher.getInstance("AES/ECB/PKCS5Padding");
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes("ASCII"), "AES");
            c.init(Cipher.DECRYPT_MODE, keySpec);

            byte[] decrypted = c.doFinal(decoded);
            return new String(decrypted);
        } catch (Exception e) {
            Log.e("ProtocolUtils", "Pack encryption failed. Error: " + e.getMessage());
        }
        return "";
    }

    public static String createScanPacket() {
        Gson gson = new Gson();
        return gson.toJson(new ScanPacket());
    }

    public static String createBindPacket(String id) {
        Gson gson = new Gson();
        return gson.toJson(new BindPacket(id));
    }

    public static String createDeviceRequestPacket(String encPackBase64, int i) {
        Gson gson = new Gson();
        return gson.toJson(new DeviceRequestPacket(encPackBase64, i));
    }
}
