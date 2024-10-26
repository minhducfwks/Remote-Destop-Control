package remote.common;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerConfiguration implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(ServerConfiguration.class.getName());

    private final byte[] hashedPassword;
    private final byte[] salt; // Salt for securing the hash
    private final int TCPPORT;
    private final int UDPPORT;

    // Constructor that accepts an IP address and password, and hashes the password
    public ServerConfiguration() {
        this.TCPPORT = 7070;
        this.UDPPORT = 9090;
        this.salt = generateSalt(); // Generate a random salt
        this.hashedPassword = hashPassword("defaultwassword", salt);
    }

    // Getters (no setters to make the class immutable)
    public int getTCPPORT() {
        return TCPPORT;
    }

    public int getUDPPORT() {
        return UDPPORT;
    }

    public byte[] getHashedPassword() {
        return hashedPassword;
    }

    public byte[] getSalt() {
        return salt;
    }

    // Verifies if the input password matches the hashed password
    public boolean verifyPassword(String inputPassword) {
        byte[] hashedInput = hashPassword(inputPassword, this.salt);
        return Arrays.equals(hashedInput, this.hashedPassword);
    }

    // Hash the password using SHA-256 and the provided salt
    private byte[] hashPassword(String password, byte[] salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(salt); // Add salt to the hash
            return digest.digest(password.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException ex) {
            logger.log(Level.SEVERE, "Error hashing password using SHA-256", ex);
            throw new IllegalStateException("SHA-256 algorithm not available", ex);
        }
    }

    // Generate a random salt for hashing
    private byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16]; // 128-bit salt
        random.nextBytes(salt);
        return salt;
    }

    @Override
    public String toString() {
        // Do NOT include sensitive information like passwords in toString()
        return "ServerConfiguration{" +
                "TCPPORT=" + TCPPORT +
                ", UDPPORT=" + UDPPORT +
                ", hashedPassword=" + Base64.getEncoder().encodeToString(hashedPassword) +
                '}';
    }
}
