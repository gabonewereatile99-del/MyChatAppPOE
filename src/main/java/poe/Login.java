package poe;

import java.util.regex.Pattern;

/**
 * Login.java — Part 1: Registration and Login feature.
 *
 * Handles creating a single user account (username, password, SA cell
 * number, first/last name) and verifying login attempts against that account.
 * Each validation rule (username, password, cellphone) has its own
 * Boolean-returning check method, kept separate so each one can be
 * unit tested individually. registerUser() then runs them in sequence
 * and returns the message for whichever rule fails first, or a success
 * message if all three pass.
 */

public class Login {

    private String storedUsername;
    private String storedPassword;
    private String storedCellphone;
    private String storedFirstName;
    private String storedLastName;
    private boolean registered = false;

    
    // Individual field validators

    /**
     * Username must contain an underscore and be no more than five
     * characters long in total.
     */
    
    public Boolean checkUserName(String username) {
        return username != null
                && username.contains("_")
                && username.length() <= 5;
    }

    /**
     * Password must be at least 8 characters, contain a capital letter,
     * a number, and a special character.
     */
    
    public boolean checkPasswordComplexity(String password) {
        if (password == null) {
            return false;
        }
        String regex = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[^A-Za-z0-9]).{8,}$";
        return Pattern.matches(regex, password);
    }

    /**
     * South African cell number check -the international E.164 format
     * approach (+27 followed by subscriber digits) was researched from:
     * "How to call South Africa: Phone Number Format, Area Codes & Validation Guide" - https://sent.dm/en/resources/phone-number-standards/za (accessed 16 September 2026).
     * code), e.g. "+27838968976" -> valid, "08966553" -> invalid.
     */
    
    public boolean checkCellPhoneNumber(String cellphone) {
        if (cellphone == null) {
            return false;
        }
        String regex = "^\\+27\\d{7,9}$";
        return Pattern.matches(regex, cellphone);
    }

    // Registration

    /**
     * Checks are run in order and the first failing rule's message is returned.
     * 
     */
    
    public String registerUser(String username, String password, String cellphone,
                                String firstName, String lastName) {

        if (!checkUserName(username)) {
            return "Username is not correctly formatted; please ensure that your "
                    + "username contains an underscore and is no more than five "
                    + "characters in length.";
        }

        if (!checkPasswordComplexity(password)) {
            return "Password is not correctly formatted; please ensure that the "
                    + "password contains at least eight characters, a capital "
                    + "letter, a number, and a special character.";
        }

        if (!checkCellPhoneNumber(cellphone)) {
            return "Cell phone number is incorrectly formatted or does not "
                    + "contain" + "international code.";
        }

        this.storedUsername = username;
        this.storedPassword = password;
        this.storedCellphone = cellphone;
        this.storedFirstName = firstName;
        this.storedLastName = lastName;
        this.registered = true;

        return "Username successfully captured. Password successfully captured. "
                + "Cell phone number successfully added. You have been "
                + "registered successfully.";
    }

    // Login

    /**
     * Verifies that the supplied credentials match the account created
     * during registerUser.
     */
    
    public boolean loginUser(String username, String password) {
        return registered
                && username != null
                && password != null
                && username.equals(storedUsername)
                && password.equals(storedPassword);
    }

    /**
     * Returns the message the console should print after a login attempt.
     */
    
    public String returnLoginStatus(boolean loginSuccessful) {
        if (loginSuccessful) {
            return "Welcome " + storedFirstName + ", " + storedLastName 
                    + " it is great to see you again.";
        }
        return "Username or password incorrect, please try again.";
    }


    public String getStoredUsername() {
        return storedUsername;
    }

    public String getStoredFirstName() {
        return storedFirstName;
    }

    public String getStoredLastName() {
        return storedLastName;
    }

    public boolean isRegistered() {
        return registered;
    }

}
