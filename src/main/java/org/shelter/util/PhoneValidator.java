package org.shelter.util;

import java.util.regex.Pattern;

public class PhoneValidator {
    // Пример маски: +7-9XX-XXX-XXXX
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+7-9\\d{2}-\\d{3}-\\d{4}");

    public static boolean isValid(String phone) {
        if (phone == null) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone).matches();
    }
}
