package helpers;

import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class PasswordHelper {

    private static final String CONSTANT_SALT = "SomeFixedSaltValue12345";

    public static String hashPassword(String password) {
        try {
            int iterations = 10000;
            int keyLength = 256;
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), CONSTANT_SALT.getBytes(), iterations, keyLength);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

            byte[] hash = keyFactory.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            return null;
        }
    }
}
