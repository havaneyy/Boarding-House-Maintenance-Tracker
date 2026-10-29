import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public abstract class User {

    private static final int ITERATIONS = 65536;
    private static final int KEY_BITS = 256;

    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String passwordHash;   // stored as "salt$hash" (never the plain password)

    // The last parameter must already be a hash created by hashPassword().
    public User(String id, String firstName, String lastName,
                String email, String username, String passwordHash) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.username = username;
        this.passwordHash = passwordHash;
    }

    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    // Returns the stored hash (used only when saving to the file).
    public String getPasswordHash() {
        return passwordHash;
    }

    // Compares what the user typed against the stored hash.
    public boolean checkPassword(String plain) {
        if (!isHashed(passwordHash)) return false;
        try {
            String[] parts = passwordHash.split("\\$");
            byte[] salt = fromHex(parts[0]);
            byte[] expected = fromHex(parts[1]);
            return MessageDigest.isEqual(expected, derive(plain, salt));
        } catch (GeneralSecurityException e) {
            return false;
        }
    }

    // Creates a new salted hash for a plain password.
    public static String hashPassword(String plain) {
        try {
            byte[] salt = new byte[16];
            new SecureRandom().nextBytes(salt);
            return toHex(salt) + "$" + toHex(derive(plain, salt));
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Password hashing is not available on this system.", e);
        }
    }

    // True if the text looks like "32 hex chars $ 64 hex chars".
    public static boolean isHashed(String stored) {
        return stored != null && stored.matches("[0-9a-f]{32}\\$[0-9a-f]{64}");
    }

    private static byte[] derive(String plain, byte[] salt) throws GeneralSecurityException {
        PBEKeySpec spec = new PBEKeySpec(plain.toCharArray(), salt, ITERATIONS, KEY_BITS);
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } finally {
            spec.clearPassword();
        }
    }

    private static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) sb.append(String.format("%02x", bytes[i]));
        return sb.toString();
    }

    private static byte[] fromHex(String hex) {
        byte[] out = new byte[hex.length() / 2];
        for (int i = 0; i < out.length; i++) out[i] = (byte) Integer.parseInt(hex.substring(i * 2, i * 2 + 2), 16);
        return out;
    }

    public abstract void displayDetails();
}
