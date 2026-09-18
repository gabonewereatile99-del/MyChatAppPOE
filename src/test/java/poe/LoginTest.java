package poe;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * LoginTest.java — matches the "Test: (assertEquals)" and
 * "Test (assertTrue/False)" tables from Part 1 of the brief.
 */
class LoginTest {

    private Login login;

    @BeforeEach
    void setUp() {
        login = new Login();
    }

    // ---- Username ----

    @Test
    void testUsername_correctlyFormatted() {
        assertTrue(login.checkUserName("kyl_1"));
    }

    @Test
    void testUsername_incorrectlyFormatted() {
        assertFalse(login.checkUserName("kyle!!!!!!"));
    }

    // ---- Password ----

    @Test
    void testPassword_meetsComplexityRequirements() {
        assertTrue(login.checkPasswordComplexity("Ch&&sec@ke99!"));
    }

    @Test
    void testPassword_doesNotMeetComplexityRequirements() {
        assertFalse(login.checkPasswordComplexity("password"));
    }

    // ---- Cell phone number ----

    @Test
    void testCellPhoneNumber_correctlyFormatted() {
        assertTrue(login.checkCellPhoneNumber("+27838968976"));
    }

    @Test
    void testCellPhoneNumber_incorrectlyFormatted() {
        assertFalse(login.checkCellPhoneNumber("08966553"));
    }

    // ---- registerUser() aggregate messages ----

    @Test
    void testRegisterUser_usernameIncorrect() {
        String result = login.registerUser("kyle!!!!!!", "Ch&&sec@ke99!", "+27838968976", "Kyle", "Daniels");
        assertTrue(result.startsWith("Username is not correctly formatted"));
    }

    @Test
    void testRegisterUser_passwordIncorrect() {
        String result = login.registerUser("kyl_1", "password", "+27838968976", "Kyle", "Daniels");
        assertTrue(result.startsWith("Password is not correctly formatted"));
    }

    @Test
    void testRegisterUser_success() {
        String result = login.registerUser("kyl_1", "Ch&&sec@ke99!", "+27838968976", "Kyle", "Daniels");
        assertTrue(result.contains("registered successfully"));
    }

    // ---- Login success/failure ----

    @Test
    void testLoginSuccessful() {
        login.registerUser("kyl_1", "Ch&&sec@ke99!", "+27838968976", "Kyle", "Daniels");
        assertTrue(login.loginUser("kyl_1", "Ch&&sec@ke99!"));
    }

    @Test
    void testLoginFailed() {
        login.registerUser("kyl_1", "Ch&&sec@ke99!", "+27838968976", "Kyle", "Daniels");
        assertFalse(login.loginUser("kyl_1", "wrongPassword1!"));
    }

    @Test
    void testReturnLoginStatus_welcomeMessage() {
        login.registerUser("kyl_1", "Ch&&sec@ke99!", "+27838968976", "Kyle", "Daniels");
        String status = login.returnLoginStatus(true);
        assertEquals("Welcome Kyle, Daniels it is great to see you again.", status);
    }

    @Test
    void testReturnLoginStatus_failureMessage() {
        String status = login.returnLoginStatus(false);
        assertEquals("Username or password incorrect, please try again.", status);
    }
}
