package com.coolcool.moviecool.utils;

/**
 * Created by yanshili on 2016/4/2.
 */
public class PasswordUtils {

    public static String encodePassword(String rawPassword, Object seed) {
        if (rawPassword == null||seed == null) return null;
        rawPassword = rawPassword.replaceAll(" ", "");
        seed=seed.toString().replaceAll(" ","");
        if (seed.equals("") || rawPassword.equals("")) return null;

        long currentTime = Long.parseLong(seed.toString());
        long p = Long.parseLong(rawPassword) + currentTime * 3;
        return String.valueOf(p);
    }

    public static String decodePassword(String encodedPassword, Object seed) {
        if (encodedPassword == null||seed == null) return null;
        encodedPassword = encodedPassword.replaceAll(" ", "");
        seed=seed.toString().replaceAll(" ","");
        if (encodedPassword.equals("") || seed.equals("")) return null;

        long pa = Long.parseLong(encodedPassword);
        long currentTime = Long.parseLong(seed.toString());
        long password = pa - currentTime * 3;
        return String.valueOf(password);
    }

}
