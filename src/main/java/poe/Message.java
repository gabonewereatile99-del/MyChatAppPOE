package poe;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


public class Message {

    public enum Flag { SENT, DISREGARDED, STORED }

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String JSON_FILE = "storedMessages.json";

    // Shared state across all Message instances for the life of the app
    
    private static final List<Message> sentMessages = new ArrayList<>();
    private static final List<Message> disregardedMessages = new ArrayList<>();
    private static final List<Message> storedMessages = new ArrayList<>();
    private static final List<String> messageHashes = new ArrayList<>();
    private static final List<String> messageIDs = new ArrayList<>();
    private static int totalMessagesSent = 0;

    private final String messageID;
    private String messageHash;
    private final String recipient;
    private final String messageText;
    private Flag flag;

    public Message(String recipient, String messageText) {
        this.messageID = generateMessageID();
        this.recipient = recipient;
        this.messageText = messageText;
    }

    // Validators

    /** Message ID must never be more than ten characters. */
    
    public boolean checkMessageID() {
        return messageID != null && messageID.length() <= 10;
    }

    /**
     * Recipient cell number: no more than ten characters and must start
     * with an international code (same rule as Login.checkCellPhoneNumber,
     * reused here per the brief's hint to "reuse the method and tests
     * you used in the last task").
     */
    public String checkRecipientCell() {
        String regex = "^\\+27\\d{7,9}$";
        if (recipient != null && Pattern.matches(regex, recipient) && recipient.length() <= 12) {
            return "Cell phone number successfully captured.";
        }
        return "Cell phone number is incorrectly formatted or does not contain "
                + "an international code. Please correct the number and try again.";
    }

    public boolean isMessageTooLong() {
        return messageText != null && messageText.length() > 250;
    }

    
    // Hash + ID generation

    private static String generateMessageID() {
        StringBuilder id = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            id.append(RANDOM.nextInt(10));
        }
        return id.toString();
    }

    /**
     * Builds "XX:N:FIRSTLASTWORD" — first two digits of the message ID,
     * the message's position in the running list (0-based), and the
     * first + last word of the message text in capitals.
     */
    public String createMessageHash() {
        String idPrefix = messageID.substring(0, 2);
        int position = totalMessagesSent; // 0-based counter at time of sending

        String[] words = messageText.trim().split("\\s+");
        String firstWord = words[0].replaceAll("[^A-Za-z]", "").toUpperCase();
        String lastWord = words[words.length - 1].replaceAll("[^A-Za-z]", "").toUpperCase();

        this.messageHash = idPrefix + ":" + position + ":" + firstWord + lastWord;
        return this.messageHash;
    }

    // Send / disregard / store a message

    /**
     * @param choice 1 = send, 2 = disregard, 3 = store for later
     */
    public String sentMessage(int choice) {
        if (isMessageTooLong()) {
            return "Please enter a message of less than 250 characters.";
        }

        createMessageHash();

        switch (choice) {
            case 1:
                this.flag = Flag.SENT;
                sentMessages.add(this);
                messageHashes.add(this.messageHash);
                messageIDs.add(this.messageID);
                totalMessagesSent++;
                return "Message successfully sent.";
            case 2:
                this.flag = Flag.DISREGARDED;
                disregardedMessages.add(this);
                return "Press 0 to delete the message.";
            case 3:
                this.flag = Flag.STORED;
                storeMessage();
                return "Message successfully stored.";
            default:
                return "Invalid option selected.";
        }
    }

    // Part 3 — JSON storage

    /** Appends this message to storedMessages.json (creating it if needed). */
    public void storeMessage() {
        storedMessages.add(this);
        JSONArray array = readJsonArray();

        JSONObject obj = new JSONObject();
        obj.put("messageID", messageID);
        obj.put("messageHash", messageHash);
        obj.put("recipient", recipient);
        obj.put("message", messageText);
        array.put(obj);

        try (FileWriter writer = new FileWriter(JSON_FILE)) {
            writer.write(array.toString(2));
        } catch (IOException e) {
            System.out.println("Could not write to " + JSON_FILE + ": " + e.getMessage());
        }
    }

    private static JSONArray readJsonArray() {
        try {
            Path path = Path.of(JSON_FILE);
            if (Files.exists(path)) {
                String content = Files.readString(path);
                if (!content.isBlank()) {
                    return new JSONArray(content);
                }
            }
        } catch (IOException e) {
            System.out.println("Could not read " + JSON_FILE + ": " + e.getMessage());
        }
        return new JSONArray();
    }

    /** Loads storedMessages.json back into the in-memory storedMessages array. */
    public static void loadStoredMessagesFromJson() {
        storedMessages.clear();
        JSONArray array = readJsonArray();
        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            Message m = new Message(obj.getString("recipient"), obj.getString("message"));
            // overwrite the randomly generated ID/hash with the persisted ones
            m.overwriteIdAndHash(obj.getString("messageID"), obj.getString("messageHash"));
            m.flag = Flag.STORED;
            storedMessages.add(m);
        }
    }

    private String overwrittenId;

    private void overwriteIdAndHash(String id, String hash) {
        this.overwrittenId = id;
        this.messageHash = hash;
    }

    private String effectiveId() {
        return overwrittenId != null ? overwrittenId : messageID;
    }

    // Reporting (Part 2 + Part 3)

    /** All messages sent while the program has been running (Part 2). */
    public static String printMessages() {
        StringBuilder sb = new StringBuilder();
        for (Message m : sentMessages) {
            sb.append(m.messageText).append("\n");
        }
        return sb.toString().trim();
    }

    public static int returnTotalMessages() {
        return totalMessagesSent;
    }

    /** Part 3.2.a — sender/recipient of every stored message. */
    public static String displayStoredSendersAndRecipients() {
        StringBuilder sb = new StringBuilder();
        for (Message m : storedMessages) {
            sb.append("Recipient: ").append(m.recipient).append("\n");
        }
        return sb.toString().trim();
    }

    /** Part 3.2.b — the longest message currently stored. */
    public static String displayLongestStoredMessage() {
        return storedMessages.stream()
                .max((a, b) -> Integer.compare(a.messageText.length(), b.messageText.length()))
                .map(m -> m.messageText)
                .orElse("No stored messages.");
    }

    /** Part 3.2.c — find a stored message by its message ID. */
    public static String searchByMessageID(String id) {
        for (Message m : storedMessages) {
            if (m.effectiveId().equals(id)) {
                return "\"" + m.messageText + "\"";
            }
        }
        return "No message found with ID " + id;
    }

    /** Part 3.2.d — all stored messages sent to a particular recipient. */
    public static String searchByRecipient(String recipientNumber) {
        StringBuilder sb = new StringBuilder();
        for (Message m : storedMessages) {
            if (m.recipient.equals(recipientNumber)) {
                sb.append("\"").append(m.messageText).append("\" ");
            }
        }
        return sb.toString().trim();
    }

    /** Part 3.2.e — delete a stored message by its hash. */
    public static String deleteByHash(String hash) {
        for (Message m : storedMessages) {
            if (hash.equals(m.messageHash)) {
                storedMessages.remove(m);
                return "Message: \"" + m.messageText + "\" successfully deleted.";
            }
        }
        return "No message found with hash " + hash;
    }

    /** Part 3.2.f — full report of every stored message. */
    public static String displayReport() {
        StringBuilder sb = new StringBuilder();
        for (Message m : storedMessages) {
            sb.append("Message Hash: ").append(m.messageHash).append("\n")
                    .append("Recipient: ").append(m.recipient).append("\n")
                    .append("Message: ").append(m.messageText).append("\n\n");
        }
        return sb.toString().trim();
    }

    // ---------------------------------------------------------------
    // Getters used by tests
    // ---------------------------------------------------------------

    public String getMessageID() { return messageID; }
    public String getMessageHash() { return messageHash; }
    public String getRecipient() { return recipient; }
    public String getMessageText() { return messageText; }
    public Flag getFlag() { return flag; }

    public static List<Message> getSentMessages() { return sentMessages; }
    public static List<Message> getDisregardedMessages() { return disregardedMessages; }
    public static List<Message> getStoredMessages() { return storedMessages; }
    public static List<String> getMessageHashes() { return messageHashes; }
    public static List<String> getMessageIDs() { return messageIDs; }

    /** Test-only helper: resets all shared state between JUnit tests. */
    static void resetAllForTesting() {
        sentMessages.clear();
        disregardedMessages.clear();
        storedMessages.clear();
        messageHashes.clear();
        messageIDs.clear();
        totalMessagesSent = 0;
    }
}
