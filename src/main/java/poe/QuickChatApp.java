package poe;

import java.util.Scanner;

/**
 * QuickChatApp.java - console entry point for QuickChat.
 *
 * The user registers and logs in first. Only after a successful
 * login do they see the welcome message and the numeric menu, which keeps
 * running until the user chooses to quit.
 */
public class QuickChatApp {

    private static final Scanner SCANNER = new Scanner(System.in);
    private static final Login LOGIN = new Login();

    public static void main(String[] args) {
        registerFlow();
        if (!loginFlow()) {
            System.out.println("Too many failed login attempts. Exiting.");
            return;
        }

        System.out.println("Welcome to QuickChat.");
        int messageLimit = askForMessageLimit();
        boolean messagesEntered = false;

        boolean running = true;
        while (running) {
            printMenu();
            int option = readInt();

            switch (option) {
                case 1:
                    if (messagesEntered) {
                        System.out.println("You have already entered your " + messageLimit
                                + " message(s) for this session.");
                    } else {
                        enterMessages(messageLimit);
                        messagesEntered = true;
                    }
                    break;
                case 2:
                    System.out.println("Coming Soon.");
                    break;
                case 3:
                    running = false;
                    System.out.println("Goodbye!");
                    break;
                default:
                    System.out.println("Invalid option. Please choose 1, 2 or 3.");
            }
        }
    }


    // Registration and login

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

        System.out.println(LOGIN.registerUser(username, password, cell, firstName, lastName));
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

    // Menu and messages

    private static int askForMessageLimit() {
        System.out.print("How many messages do you want to enter? ");
        return readInt();
    }

    private static void printMenu() {
        System.out.println("\n1) Send Messages");
        System.out.println("2) Show recently sent messages");
        System.out.println("3) Quit");
        System.out.print("Choose an option: ");
    }

    /** Lets the user enter exactly messageLimit messages, one loop pass per message. */
    private static void enterMessages(int messageLimit) {
        for (int messageNumber = 0; messageNumber < messageLimit; messageNumber++) {
            System.out.println("\n--- Message " + (messageNumber + 1) + " of " + messageLimit + " ---");

            Message message = readValidMessage(messageNumber);
            System.out.println(message.getMessageIDStatus());

            int choice = readSendChoice();
            System.out.println(message.sentMessage(choice));

            if (choice == 1) {
                System.out.println(message.getDetails());
            } else if (choice == 2) {
                String answer = SCANNER.nextLine().trim();
                System.out.println(answer.equals("0") ? "Message deleted." : "Message disregarded.");
            }
        }

        System.out.println("\nTotal messages sent: " + Message.returnTotalMessages());
    }

    /** Keeps asking for the recipient and message until both are valid. */
    private static Message readValidMessage(int messageNumber) {
        while (true) {
            System.out.print("Recipient (e.g. +27821234567): ");
            String recipient = SCANNER.nextLine().trim();
            System.out.print("Message (max 250 characters): ");
            String text = SCANNER.nextLine();

            Message message = new Message(messageNumber, recipient, text);
            System.out.println(message.checkRecipientCell());
            System.out.println(message.checkMessageLength());

            if (message.isRecipientValid() && message.isMessageLengthValid()) {
                return message;
            }
            System.out.println("Please try again.");
        }
    }

    private static int readSendChoice() {
        while (true) {
            System.out.println("1) Send Message  2) Disregard Message  3) Store Message to send later");
            System.out.print("Choose an option: ");
            int choice = readInt();
            if (choice >= 1 && choice <= 3) {
                return choice;
            }
            System.out.println("Invalid option selected.");
        }
    }

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