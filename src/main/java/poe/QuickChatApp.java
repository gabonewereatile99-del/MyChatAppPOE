package poe;

import java.util.Scanner;

public class QuickChatApp {

    private static final Scanner SCANNER = new Scanner(System.in);
    private static final Login LOGIN = new Login();

    public static void main(String[] args) {
        Message.loadStoredMessagesFromJson();

        registerFlow();
        if (!loginFlow()) {
            System.out.println("Too many failed login attempts. Exiting.");
            return;
        }

        System.out.println("Welcome to QuickChat.");
        int numMessages = askForMessageLimit();
        int messagesEnteredThisSession = 0;

        boolean running = true;
        while (running) {
            printMenu();
            int option = readInt();
            switch (option) {
                case 1:
                    if (messagesEnteredThisSession >= numMessages) {
                        System.out.println("You have reached your message limit for this session.");
                        break;
                    }
                    sendMessageFlow();
                    messagesEnteredThisSession++;
                    break;
                case 2:
                    System.out.println("Coming Soon.");
                    break;
                case 3:
                    storedMessagesFlow();
                    break;
                case 4:
                    running = false;
                    System.out.println("Total messages sent: " + Message.returnTotalMessages());
                    System.out.println("Goodbye!");
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

 
    // Part 1 flows

    private static void registerFlow() {
        System.out.println("=== Register ===");
        System.out.print("First name: ");
        String firstName = SCANNER.nextLine();
        System.out.print("Last name: ");
        String lastName = SCANNER.nextLine();
        System.out.print("Username: ");
        String username = SCANNER.nextLine();
        System.out.print("Password: ");
        String password = SCANNER.nextLine();
        System.out.print("Cell phone number (e.g. +27821234567): ");
        String cell = SCANNER.nextLine();

        String result = LOGIN.registerUser(username, password, cell, firstName, lastName);
        System.out.println(result);
    }

    private static boolean loginFlow() {
        for (int attempt = 0; attempt < 3; attempt++) {
            System.out.println("=== Login ===");
            System.out.print("Username: ");
            String username = SCANNER.nextLine();
            System.out.print("Password: ");
            String password = SCANNER.nextLine();

            boolean success = LOGIN.loginUser(username, password);
            System.out.println(LOGIN.returnLoginStatus(success));
            if (success) {
                return true;
            }
        }
        return false;
    }

    // Part 2 flow


    private static int askForMessageLimit() {
        System.out.print("How many messages would you like to enter this session? ");
        return readInt();
    }

    private static void printMenu() {
        System.out.println("\n1) Send Messages\n2) Show recently sent messages\n3) Stored Messages\n4) Quit");
        System.out.print("Choose an option: ");
    }

    private static void sendMessageFlow() {
        System.out.print("Recipient (e.g. +27821234567): ");
        String recipient = SCANNER.nextLine();
        System.out.print("Message: ");
        String text = SCANNER.nextLine();

        Message message = new Message(recipient, text);

        String cellCheck = message.checkRecipientCell();
        System.out.println(cellCheck);
        if (!cellCheck.startsWith("Cell phone number successfully")) {
            return;
        }

        System.out.println("1) Send Message  2) Disregard Message  3) Store Message to send later");
        int choice = readInt();
        System.out.println(message.sentMessage(choice));
    }

    // Part 3 flow

    private static void storedMessagesFlow() {
        System.out.println("\na) Senders/recipients  b) Longest message  c) Search by ID  "
                + "d) Search by recipient  e) Delete by hash  f) Full report");
        System.out.print("Choose an option: ");
        String choice = SCANNER.nextLine();

        switch (choice) {
            case "a" -> System.out.println(Message.displayStoredSendersAndRecipients());
            case "b" -> System.out.println(Message.displayLongestStoredMessage());
            case "c" -> {
                System.out.print("Message ID: ");
                System.out.println(Message.searchByMessageID(SCANNER.nextLine()));
            }
            case "d" -> {
                System.out.print("Recipient number: ");
                System.out.println(Message.searchByRecipient(SCANNER.nextLine()));
            }
            case "e" -> {
                System.out.print("Message hash: ");
                System.out.println(Message.deleteByHash(SCANNER.nextLine()));
            }
            case "f" -> System.out.println(Message.displayReport());
            default -> System.out.println("Invalid option.");
        }
    }

    // Helpers

    private static int readInt() {
        while (true) {
            String line = SCANNER.nextLine();
            try {
                return Integer.parseInt(line.trim());
            } catch (NumberFormatException e) {
                System.out.print("Please enter a number: ");
            }
        }
    }
}
