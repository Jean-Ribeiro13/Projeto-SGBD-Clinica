package br.edu.clinica.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HexFormat;

import org.springframework.security.crypto.password.PasswordEncoder;

/** Compatível com a representação salt + SHA-256 criada por sp_criar_usuario. */
public class SaltedSha256PasswordEncoder implements PasswordEncoder {
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final HexFormat HEX = HexFormat.of();

    @Override
    public String encode(CharSequence rawPassword) {
        byte[] saltBytes = new byte[16];
        RANDOM.nextBytes(saltBytes);
        String salt = HEX.formatHex(saltBytes);
        return salt + "$" + hash(rawPassword.toString(), salt);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String storedPassword) {
        if (rawPassword == null || storedPassword == null) return false;
        String[] parts = storedPassword.split("\\$", -1);
        if (parts.length != 2) return false;
        byte[] expected = parts[1].getBytes(StandardCharsets.US_ASCII);
        byte[] actual = hash(rawPassword.toString(), parts[0]).getBytes(StandardCharsets.US_ASCII);
        return MessageDigest.isEqual(expected, actual);
    }

    private String hash(String password, String salt) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest((password + salt).getBytes(StandardCharsets.UTF_8));
            return HEX.formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 não está disponível nesta JVM.", e);
        }
    }
}
