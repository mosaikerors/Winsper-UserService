package com.mosaiker.userservice.utils;

import java.util.Random;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utils {
    public static String randomNumber(int num) {
        if (num <= 0) {
            return null;
        }
        Random random = new Random();
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < num; i++) {
            result.append(random.nextInt(10));
        }
        return result.toString();
    }

    public static String statusToRole(int status) {
        if (status < 0) {
            return "BANNED";
        }
        switch (status) {
            case 1:
                return  "USER";
            case 2:
                return  "SUPERUSER";
            case 100:
                return "ADMIN";
            default:
                return "UNKNOWN";
        }
    }

    public static String getFullSecret(String password, int status, String commonSecret) {
        if (status > 0) {
            return password + commonSecret;
        } else {
            return commonSecret + password;
        }
    }

    public static String encryptPassword(String input) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md5.digest(input.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            String hashText = no.toString(16);
            while (hashText.length() < 32) {
                hashText = "0" + hashText;
            }
            return hashText;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
