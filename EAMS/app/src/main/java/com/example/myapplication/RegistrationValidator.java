package com.example.myapplication;

public class RegistrationValidator {

    public static boolean isValidFirstName(String firstName) {
        return firstName != null && firstName.trim().length() >= 2;
    }

    public static boolean isValidLastName(String lastName) {
        return lastName != null && lastName.trim().length() >= 2;
    }

    public static boolean isValidEmail(String email) {
        return email != null && email.contains("@");
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 8;
    }

    public static boolean isPasswordMatching(String password, String confirmPassword) {
        return password != null && password.equals(confirmPassword);
    }

    public static boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber != null && phoneNumber.matches("^[0-9]+$");
    }

    public static boolean isValidAddress(String address) {
        return address != null && !address.trim().isEmpty();
    }

    public static boolean isValidOrganizationName(String organizationName) {
        return organizationName != null && !organizationName.trim().isEmpty();
    }
}

