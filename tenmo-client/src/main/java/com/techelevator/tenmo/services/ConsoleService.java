package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.UserCredentials;
import com.techelevator.tenmo.model.User;
import java.math.BigDecimal;
import java.util.Scanner;

public class ConsoleService {
    public static final String BLACK_BOLD = "\033[1;30m";  // BLACK
    public static final String RED_BOLD = "\033[1;31m";    // RED
    public static final String YELLOW_BOLD = "\033[1;33m"; // YELLOW
    public static final String ANSI_RESET = "\u001B[0m";
    private final Scanner scanner = new Scanner(System.in);

    public int promptForMenuSelection(String prompt) {
        int menuSelection;
        System.out.print(prompt);
        try {
            menuSelection = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            menuSelection = -1;
        }
        return menuSelection;
    }

    public void printGreeting() {
        System.out.println("*********************");
        System.out.println("* Welcome to TEnmo! *");
        System.out.println("*********************");
    }

    public void printLoginMenu() {
        System.out.println();
        System.out.println("1: Register");
        System.out.println("2: Login");
        System.out.println("0: Exit");
        System.out.println();
    }

    public void printMainMenu() {
        System.out.println();
        System.out.println("1: View your current balance");
        System.out.println("2: View your past transfers");
        System.out.println("3: View your pending requests");
        System.out.println("4: Send TE bucks");
        System.out.println("5: Request TE bucks");
        System.out.println("0: Exit");
        System.out.println();
    }


    public UserCredentials promptForCredentials() {
        String username = promptForString("Username: ");
        String password = promptForString("Password: ");
        return new UserCredentials(username, password);
    }

    public String promptForString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    public int promptForInt(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
            }
        }
    }

    public BigDecimal promptForBigDecimal(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                return new BigDecimal(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a decimal number.");
            }
        }
    }

    public void pause() {
        System.out.println("\n   Press Enter to continue...");
        scanner.nextLine();
    }

    public void printErrorMessage() {
        System.out.println("An error occurred. Check the log for details.");
    }

    public void printTransferDetails(int id, String from, String to, String type, String status, BigDecimal amount) {
        System.out.println("-------------------------------");
        System.out.println("Transfer Details");
        System.out.println("-------------------------------");
        System.out.println("Id:  " +RED_BOLD+id+ANSI_RESET);
        System.out.println("From: " +RED_BOLD+ from+ANSI_RESET);
        System.out.println("To: " +RED_BOLD+ to+ANSI_RESET);
        System.out.println("Type: " +RED_BOLD+ type+ANSI_RESET);
        System.out.println("Status: " +RED_BOLD+ status+ANSI_RESET);
        System.out.println("Amount: TE " +RED_BOLD+ amount+ANSI_RESET);
        System.out.println("-------------------------------");
    }

    public void printTransfers(int transferId, String fromOrTo, BigDecimal amount) {
        System.out.println(RED_BOLD+transferId+ANSI_RESET + "     " + fromOrTo + "          TE " +RED_BOLD+amount+ANSI_RESET);
    }
    public void printListUsers(User[] users) {
        System.out.println("-------------------------------------------");
        System.out.println(RED_BOLD+"Users-ID"+ANSI_RESET +"               Name");
        System.out.println("-------------------------------------------");
        for(User user: users) {
            System.out.println();
            System.out.println(YELLOW_BOLD+user.getId() +ANSI_RESET + "        " + user.getUsername());
        }
        System.out.println("-------------------------------------------");

    }
    public void printPendindOptions() {
        System.out.println("-------------------------------------------");
        System.out.println(""+RED_BOLD+"1"+ANSI_RESET+": Approve");

        System.out.println(""+RED_BOLD+"2"+ANSI_RESET+": Reject");
        System.out.println(""+RED_BOLD+"0"+ANSI_RESET+": Don't approve or reject");
        System.out.println("-------------------------------------------");
    }
}

