package com.blog.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Password hashing helper.
 *
 * New passwords use BCrypt. Existing 32-character MD5 hashes remain readable so
 * older local data can still log in and then be migrated on successful login.
 */
public class PasswordUtil {

    private static final BCryptPasswordEncoder BCRYPT = new BCryptPasswordEncoder();

    private PasswordUtil() {}

    public static String hash(String rawPassword) {
        return BCRYPT.encode(rawPassword);
    }

    public static boolean matches(String rawPassword, String storedHash) {
        if (rawPassword == null || storedHash == null || storedHash.isBlank()) {
            return false;
        }

        if (storedHash.startsWith("$2a$") || storedHash.startsWith("$2b$") || storedHash.startsWith("$2y$")) {
            return BCRYPT.matches(rawPassword, storedHash);
        }

        if (storedHash.matches("^[a-fA-F0-9]{32}$")) {
            return MD5Util.verify(rawPassword, storedHash);
        }

        return false;
    }

    public static boolean needsRehash(String storedHash) {
        return storedHash == null || !storedHash.startsWith("$2");
    }
}
