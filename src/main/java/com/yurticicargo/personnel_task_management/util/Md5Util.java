package com.yurticicargo.personnel_task_management.util;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Component
public class Md5Util {

    public String hash(String input) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] hashedBytes = messageDigest.digest(input.getBytes(StandardCharsets.UTF_8));

            StringBuilder hashedValue = new StringBuilder();

            for (byte b : hashedBytes) {
                hashedValue.append(String.format("%02x", b));
            }

            return hashedValue.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm could not be found", e);
        }
    }
}