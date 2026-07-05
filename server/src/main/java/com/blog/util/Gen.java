package com.blog.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class Gen {
    public static void main(String[] args) {
        if (args.length == 0 || args[0].isBlank()) {
            throw new IllegalArgumentException("Usage: Gen <password>");
        }
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        System.out.println(encoder.encode(args[0]));
    }
}
