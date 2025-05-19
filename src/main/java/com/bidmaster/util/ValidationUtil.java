package com.bidmaster.util;

import java.util.regex.Pattern;

/**
 * Utility class for validating user input
 */
public class ValidationUtil {
    
    // Email validation regex pattern
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    
    // Username validation pattern (alphanumeric and underscore, 3-50 chars)
    private static final String USERNAME_REGEX = "^[a-zA-Z0-9_]{3,50}$";
    
    // Contact number validation (supports international format)
    private static final String CONTACT_REGEX = "^\\+?[0-9]{10,15}$";
    
    // Password minimum length
    private static final int MIN_PASSWORD_LENGTH = 8;
    
    /**
     * Validates email format
     * 
     * @param email The email to validate
     * @return true if email is valid, false otherwise
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return Pattern.compile(EMAIL_REGEX).matcher(email).matches();
    }
    
    /**
     * Validates username format
     * 
     * @param username The username to validate
     * @return true if username is valid, false otherwise
     */
    public static boolean isValidUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        return Pattern.compile(USERNAME_REGEX).matcher(username).matches();
    }
    
    /**
     * Validates password strength
     * 
     * @param password The password to validate
     * @return true if password is valid, false otherwise
     */
    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= MIN_PASSWORD_LENGTH;
    }
    
    /**
     * Validates contact number format
     * 
     * @param contactNo The contact number to validate
     * @return true if contact number is valid, false otherwise
     */
    public static boolean isValidContactNo(String contactNo) {
        if (contactNo == null || contactNo.trim().isEmpty()) {
            return true; // Contact number is optional
        }
        return Pattern.compile(CONTACT_REGEX).matcher(contactNo).matches();
    }
    
    /**
     * Validates if a string is not null or empty
     * 
     * @param value The string to check
     * @return true if string has content, false otherwise
     */
    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }
    
    /**
     * Validates if a number is positive
     * 
     * @param value The number to check
     * @return true if number is positive, false otherwise
     */
    public static boolean isPositive(int value) {
        return value > 0;
    }
    
    /**
     * Validates if a number is positive
     * 
     * @param value The number to check
     * @return true if number is positive, false otherwise
     */
    public static boolean isPositive(double value) {
        return value > 0;
    }
}
