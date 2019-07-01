package com.mosaiker.userservice.utils;

import java.util.Random;

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
}
