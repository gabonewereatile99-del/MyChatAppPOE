package poe;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * One Message object represents one chat message. It validates the
 * recipient and the message length, builds its own ten-digit message ID
 * and message hash, and can be sent, disregarded or stored in a JSON file.
 *
 * The messages sent while the program is running are kept in a static list
 * so the app can show them and report the running total.
 */
public class Message {

    private static final int MAX_MESSAGE_LENGTH = 250;
    private static final int MESSAGE_ID_LENGTH = 10;
    private static final Random RANDOM = new Random();

    // Shared by every Message and what has been sent so far and where stored messages go.
    private static final List<Message> sentMessages = new ArrayList<>();
    private static int totalMessagesSent = 0;
    private static String jsonFilePath = "storedMessages.json";

    private final String messageID;
    private final int messageNumber;
    private final String recipient;
    private final String messageText;
    private final String messageHash;

    /** Creates a message with a randomly generated message ID. */
    public Message(int messageNumber, String recipient, String messageText) {
        this(generateMessageID(), messageNumber, recipient, messageText);
    }

    /** Creates a message with a known message ID,used by the unit tests. */
    public Message(String messageID, int messageNumber, String recipient, String messageText) {
        this.messageID = messageID;
        this.messageNumber = messageNumber;
        this.recipient = recipient;
        this.messageText = messageText;
        this.messageHash = createMessageHash();
    }

    // Validation

    /** The message ID must not be longer than ten characters. */
    public boolean checkMessageID() {
        return messageID != null && messageID.length() <= MESSAGE_ID_LENGTH;
    }

    /** True when the recipient number has the international code and the right length. */
    public boolean isRecipientValid() {
        // Same rule as the cell number check used at registration in Part 1
        return new Login().checkCellPhoneNumber(recipient);
    }

    /** True when the message is 250 characters or fewer. */
    public boolean isMessageLengthValid() {
        return messageText != null && messageText.length() <= MAX_MESSAGE_LENGTH;
    }

    /** Returns the success or failure message for the recipient number. */
    public String checkRecipientCell() {
        if (isRecipientValid()) {
            return "Cell phone number successfully captured.";
        }
        return "Cell phone number is incorrectly formatted or does not contain "
                + "an international code. Please correct the number and try again.";
    }

    /** Returns the success or failure message for the message length. */
    public String checkMessageLength() {
        if (isMessageLengthValid()) {
            return "Message ready to send.";
        }
        int excess = messageText.length() - MAX_MESSAGE_LENGTH;
        return "Message exceeds 250 characters by " + excess + "; please reduce the size.";
    }

    // Message ID and hash

    /** Builds a random ten-digit ID one digit at a time, so leading zeros are kept. */
    private static String generateMessageID() {
        StringBuilder id = new StringBuilder();
        for (int i = 0; i < MESSAGE_ID_LENGTH; i++) {
            id.append(RANDOM.nextInt(10));
        }
        return id.toString();
    }

    public String getMessageIDStatus() {
        return "Message ID generated: " + messageID;
    }

    /**
     * Builds the hash as: first two digits of the message ID, a colon, the
     * message number, a colon, then the first and last word of the message
     * in capitals (for example 00:0:HITONIGHT).
     */
    public String createMessageHash() {
        String idPrefix = messageID.substring(0, 2);

        String[] words = messageText.trim().split("\\s+");
        String firstWord = lettersOnlyUpperCase(words[0]);
        String lastWord = lettersOnlyUpperCase(words[words.length - 1]);

        return idPrefix + ":" + messageNumber + ":" + firstWord + lastWord;
    }

    /** Removes punctuation such as the "?" and capitalises the word. */
    private static String lettersOnlyUpperCase(String word) {
        return word.replaceAll("[^A-Za-z]", "").toUpperCase();
    }

    // Send, disregard or store

    /**
     * @param choice 1 = send, 2 = disregard, 3 = store to send later
     * @return the message to show the user for that choice
     */
    public String sentMessage(int choice) {
        switch (choice) {
            case 1:
                sentMessages.add(this);
                totalMessagesSent++;
                return "Message successfully sent.";
            case 2:
                return "Press 0 to delete the message.";
            case 3:
                storeMessage();
                return "Message successfully stored.";
            default:
                return "Invalid option selected.";
        }
    }

    // JSON storage

    /**
     * Adds this message to the JSON file, keeping any messages already stored.
     *
     * The org.json library is used to build and read the JSON. Approach based on the
     * library's documentation: JSON-java (org.json), https://github.com/stleary/JSON-java
     * (accessed 17 October 2026).
     */
    public void storeMessage() {
        JSONArray storedMessages = readStoredMessages();

        JSONObject entry = new JSONObject();
        entry.put("messageID", messageID);
        entry.put("messageHash", messageHash);
        entry.put("recipient", recipient);
        entry.put("message", messageText);
        storedMessages.put(entry);

        try (FileWriter writer = new FileWriter(jsonFilePath)) {
            writer.write(storedMessages.toString(2));
        } catch (IOException e) {
            System.out.println("Could not store the message: " + e.getMessage());
        }
    }

    /** Reads the JSON file if it exists, otherwise starts with an empty list. */
    private static JSONArray readStoredMessages() {
        try {
            Path file = Path.of(jsonFilePath);
            if (Files.exists(file)) {
                String content = Files.readString(file);
                if (!content.isBlank()) {
                    return new JSONArray(content);
                }
            }
        } catch (IOException e) {
            System.out.println("Could not read the stored messages: " + e.getMessage());
        }
        return new JSONArray();
    }

    // Reporting

    /** Full details of this message: ID, hash, recipient, then the message. */
    public String getDetails() {
        return "Message ID: " + messageID + "\n"
                + "Message Hash: " + messageHash + "\n"
                + "Recipient: " + recipient + "\n"
                + "Message: " + messageText;
    }

    /** Details of every message sent while the program has been running. */
    public static String printMessages() {
        if (sentMessages.isEmpty()) {
            return "No messages have been sent yet.";
        }
        StringBuilder all = new StringBuilder();
        for (Message message : sentMessages) {
            all.append(message.getDetails()).append("\n\n");
        }
        return all.toString().trim();
    }

    public static int returnTotalMessages() {
        return totalMessagesSent;
    }

    // Getters and test helpers

    public String getMessageID() { return messageID; }
    public String getMessageHash() { return messageHash; }
    public String getRecipient() { return recipient; }
    public String getMessageText() { return messageText; }

    /** Points JSON storage at a different file (the tests use a temporary one). */
    public static void setJsonFilePath(String path) {
        jsonFilePath = path;
    }

    /** Clears the sent messages and the running total. */
    public static void clearSentMessages() {
        sentMessages.clear();
        totalMessagesSent = 0;
    }
}