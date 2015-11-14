package com.wanted.util;

/**
 * Created by xlin2 on 11/13/2015.
 * Referred to AndreBTS
 */
public class ValidateUserInfo {
    public static boolean isEmailValid(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

    public static boolean isPasswordValid(String password) {
        return password.length() >= 6 && password.length() <= 14;
    }
}
