package utility;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

// One way hashing

public class Hasher {
    public static String hash(String string) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(string.getBytes());
            
            // convert byte array to hex string
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
}
