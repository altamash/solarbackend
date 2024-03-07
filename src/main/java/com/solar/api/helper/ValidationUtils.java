package com.solar.api.helper;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Locale;

public class ValidationUtils {

    public static boolean isValidDate(String date, String format) {
        try {
            new SimpleDateFormat(format).parse(date);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

    public static boolean isValidEmail(String email) {
        try {
            new InternetAddress(email).validate();
        } catch (AddressException e) {
            return false;
        }
        return true;
    }

    public static boolean isValidLanguageCode(String code) {
        return Arrays.asList(Locale.getISOLanguages()).contains(code);
    }

    public static boolean isNumeric(String string) {
        try {
            Double.parseDouble(string);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public static boolean isLong(String string) {
        try {
            Long.parseLong(string);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public  static  boolean isValidPhoneNumber(String field){
        String phoneNumber = field;
        boolean isValid = phoneNumber.matches("^[0-9]{3}[0-9]{3}[0-9]{2,4}$");
        return isValid;
    }
    public  static  boolean isValidCountryCode(String field){
        String countryCode = "(+"+field+")";
        boolean isValid = countryCode.matches("^[(][+][0-9]{1,3}[)]$");
        return isValid;
    }
}
