package poe;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * MessageTest.java - unit tests for the Message class, using the
 * two sets of test data from the brief.
 */
class MessageTest {

    @TempDir
    Path tempDir;

    private Message messageOne;
    private Message messageTwo;

    @BeforeEach
    void setUp() {
        Message.clearSentMessages();
        Message.setJsonFilePath(tempDir.resolve("storedMessages.json").toString());

        // Test data for message 1 and message 2 (IDs are fixed so the hash can be checked)
        messageOne = new Message("0012345678", 0, "+27718693002",
                "Hi Mike, can you join us for dinner tonight?");
        messageTwo = new Message("0087654321", 1, "08575975889",
                "Hi Keegan, did you receive the payment?");
    }

    // ---- Message length ----

    @Test
    void testMessageLength_success() {
        assertEquals("Message ready to send.", messageOne.checkMessageLength());
    }

    @Test
    void testMessageLength_failure() {
        Message tooLong = new Message("0012345678", 0, "+27718693002", "A".repeat(260));
        assertEquals("Message exceeds 250 characters by 10; please reduce the size.",
                tooLong.checkMessageLength());
    }

    // ---- Recipient number ----

    @Test
    void testRecipientCell_success() {
        assertEquals("Cell phone number successfully captured.", messageOne.checkRecipientCell());
    }

    @Test
    void testRecipientCell_failure() {
        assertEquals("Cell phone number is incorrectly formatted or does not contain "
                + "an international code. Please correct the number and try again.",
                messageTwo.checkRecipientCell());
    }

    // ---- Message ID ----

    @Test
    void testMessageID_createdWithTenDigits() {
        Message random = new Message(0, "+27718693002", "Hi Mike, can you join us for dinner tonight?");
        assertTrue(random.checkMessageID());
        assertEquals(10, random.getMessageID().length());
        assertTrue(random.getMessageID().matches("\\d{10}"));
    }

    @Test
    void testMessageID_status() {
        assertEquals("Message ID generated: 0012345678", messageOne.getMessageIDStatus());
    }

    // ---- Message hash ----

    @Test
    void testMessageHash_messageOne() {
        assertEquals("00:0:HITONIGHT", messageOne.createMessageHash());
    }

    @Test
    void testMessageHash_messageTwo() {
        assertEquals("00:1:HIPAYMENT", messageTwo.createMessageHash());
    }

    // ---- Send, disregard, store ----

    @Test
    void testSendMessage() {
        assertEquals("Message successfully sent.", messageOne.sentMessage(1));
    }

    @Test
    void testDisregardMessage() {
        assertEquals("Press 0 to delete the message.", messageTwo.sentMessage(2));
    }

    @Test
    void testStoreMessage() {
        assertEquals("Message successfully stored.", messageOne.sentMessage(3));
    }

    // ---- Totals and reporting ----

    @Test
    void testTotalMessages_onlyCountsSentMessages() {
        messageOne.sentMessage(1);
        messageTwo.sentMessage(2);
        assertEquals(1, Message.returnTotalMessages());
    }

    @Test
    void testPrintMessages_showsSentMessages() {
        messageOne.sentMessage(1);
        assertTrue(Message.printMessages().contains("Hi Mike, can you join us for dinner tonight?"));
    }

    @Test
    void testDetails_inCorrectOrder() {
        String expected = "Message ID: 0012345678\n"
                + "Message Hash: 00:0:HITONIGHT\n"
                + "Recipient: +27718693002\n"
                + "Message: Hi Mike, can you join us for dinner tonight?";
        assertEquals(expected, messageOne.getDetails());
    }

    // ---- JSON storage ----

    @Test
    void testStoreMessage_writesJsonFile() throws IOException {
        messageOne.storeMessage();
        String json = Files.readString(tempDir.resolve("storedMessages.json"));
        assertTrue(json.contains("0012345678"));
        assertTrue(json.contains("00:0:HITONIGHT"));
        assertTrue(json.contains("+27718693002"));
    }
}