package poe;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * MessageTest.java — matches the Part 2 / Part 3 test data tables
 * (Test Data Message 1-5, Sent Messages array, longest message, search,
 * delete-by-hash, and report tests).
 */
class MessageTest {

    @BeforeEach
    void setUp() {
        Message.resetAllForTesting();
    }

    // ---- Field-level validation ----

    @Test
    void testMessageID_neverMoreThanTenCharacters() {
        Message m = new Message("+27834557896", "Did you get the cake?");
        assertTrue(m.checkMessageID());
        assertTrue(m.getMessageID().length() <= 10);
    }

    @Test
    void testRecipientCell_correctlyFormatted() {
        Message m = new Message("+27718693002", "Hi Mike, can you join us for dinner tonight?");
        assertEquals("Cell phone number successfully captured.", m.checkRecipientCell());
    }

    @Test
    void testRecipientCell_incorrectlyFormatted() {
        Message m = new Message("08966553", "Hi Mike, can you join us for dinner tonight?");
        assertTrue(m.checkRecipientCell().startsWith("Cell phone number is incorrectly formatted"));
    }

    @Test
    void testMessage_exceeds250Characters() {
        String longText = "A".repeat(251);
        Message m = new Message("+27718693002", longText);
        assertEquals("Please enter a message of less than 250 characters.", m.sentMessage(1));
    }

    // ---- Sending / storing / disregarding ----

    @Test
    void testSendMessage_success() {
        Message m = new Message("+27718693002", "Hi Mike, can you join us for dinner tonight?");
        assertEquals("Message successfully sent.", m.sentMessage(1));
        assertEquals(1, Message.returnTotalMessages());
    }

    @Test
    void testDisregardMessage() {
        Message m = new Message("+27718693002", "Hi Mike, can you join us for dinner tonight?");
        assertEquals("Press 0 to delete the message.", m.sentMessage(2));
    }

    @Test
    void testStoreMessage() {
        Message m = new Message("+27718693002", "Hi Mike, can you join us for dinner tonight?");
        assertEquals("Message successfully stored.", m.sentMessage(3));
        assertEquals(1, Message.getStoredMessages().size());
    }

    // ---- Sent messages array populated correctly (Test Data Message 1-4) ----

    @Test
    void testSentMessagesArrayCorrectlyPopulated() {
        Message m1 = new Message("+27834557896", "Did you get the cake?");
        m1.sentMessage(1);
        Message m2 = new Message("+27838884567", "Where are you? You are late! I have asked you to be on time.");
        m2.sentMessage(3); // stored, per Test Data Message 2
        Message m4 = new Message("0838884567", "It is dinner time!");
        m4.sentMessage(1);

        assertEquals("Did you get the cake?\nIt is dinner time!", Message.printMessages());
    }

    @Test
    void testDisplayLongestMessage() {
        Message m1 = new Message("+27834557896", "Did you get the cake?");
        m1.sentMessage(3);
        Message m2 = new Message("+27838884567", "Where are you? You are late! I have asked you to be on time.");
        m2.sentMessage(3);

        assertEquals("Where are you? You are late! I have asked you to be on time.",
                Message.displayLongestStoredMessage());
    }

    @Test
    void testSearchAllMessagesForRecipient() {
        Message m2 = new Message("+27838884567", "Where are you? You are late! I have asked you to be on time.");
        m2.sentMessage(3);
        Message m5 = new Message("+27838884567", "Ok, I am leaving without you.");
        m5.sentMessage(3);

        String result = Message.searchByRecipient("+27838884567");
        assertTrue(result.contains("Where are you?"));
        assertTrue(result.contains("Ok, I am leaving without you."));
    }

    @Test
    void testDeleteMessageUsingHash() {
        Message m = new Message("+27838884567", "Where are you? You are late! I have asked you to be on time.");
        m.sentMessage(3);
        String hash = m.getMessageHash();

        String result = Message.deleteByHash(hash);
        assertTrue(result.endsWith("successfully deleted."));
        assertEquals(0, Message.getStoredMessages().size());
    }

    @Test
    void testDisplayReport_containsExpectedFields() {
        Message m = new Message("+27834557896", "Did you get the cake?");
        m.sentMessage(3);

        String report = Message.displayReport();
        assertTrue(report.contains("Message Hash:"));
        assertTrue(report.contains("Recipient: +27834557896"));
        assertTrue(report.contains("Message: Did you get the cake?"));
    }
}
