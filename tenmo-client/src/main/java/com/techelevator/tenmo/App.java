package com.techelevator.tenmo;

import com.techelevator.tenmo.model.*;
import com.techelevator.tenmo.services.*;
import java.math.BigDecimal;
import java.util.UUID;


public class App {
    public static final String BLACK_BOLD = "\033[1;30m";  // BLACK
    public static final String RED_BOLD = "\033[1;31m";    // RED
    public static final String GREEN_BOLD = "\033[1;32m";  // GREEN
    public static final String YELLOW_BOLD = "\033[1;33m"; // YELLOW
    public static final String ANSI_RESET = "\u001B[0m";

    private static final String API_BASE_URL = "http://localhost:8080/";
    private final ConsoleService consoleService = new ConsoleService();
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);

    private AuthenticatedUser currentUser;
    private ConsoleService console = new ConsoleService();
    private AccountService accountService = new AccountService(API_BASE_URL);

    private UserService userService = new UserService();
    private TransferTypeService transferTypeService = new TransferTypeService(API_BASE_URL);
    private TransferStatusService transferStatusService = new TransferStatusService(API_BASE_URL);
    private TransfersService transferService = new TransfersService(API_BASE_URL);

    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    private void run() {
        consoleService.printGreeting();
        loginMenu();
        if (currentUser != null) {
            mainMenu();
        }
    }
    private void loginMenu() {
        int menuSelection = -1;
        while (menuSelection != 0 && currentUser == null) {
            consoleService.printLoginMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                handleRegister();
            } else if (menuSelection == 2) {
                handleLogin();
            } else if (menuSelection != 0) {
                System.out.println("Invalid Selection");
                consoleService.pause();
            }
        }
    }
    private void handleRegister() {
        System.out.println("Please register a new user account");
        UserCredentials credentials = consoleService.promptForCredentials();
        if (authenticationService.register(credentials)) {
            System.out.println("Registration successful. You can now login.");
        } else {
            consoleService.printErrorMessage();
        }
    }

    private void handleLogin() {
        UserCredentials credentials = consoleService.promptForCredentials();
        currentUser = authenticationService.login(credentials);
        if (currentUser == null) {
            consoleService.printErrorMessage();
        }
    }

    private void mainMenu() {
        int menuSelection = -1;
        while (menuSelection != 0) {
            consoleService.printMainMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                viewCurrentBalance();
            } else if (menuSelection == 2) {
                viewTransferHistory();
            } else if (menuSelection == 3) {
                viewPendingRequests();
            } else if (menuSelection == 4) {
                sendBucks();
            } else if (menuSelection == 5) {
                requestBucks();
            } else if (menuSelection == 0) {
                continue;
            } else {
                System.out.println("Invalid Selection");
            }
            consoleService.pause();
        }
    }

	private void viewCurrentBalance() {
		// TODO Auto-generated method stub

      BigDecimal balance = accountService.getBalance(currentUser);
        System.out.println("Your current account balance is:  TE " +RED_BOLD+balance+ANSI_RESET );
        }

	private void viewTransferHistory() {
		// TODO Auto-generated method stub
        Transfer[] transfers = transferService.getTransfersFromUserId(currentUser, currentUser.getUser().getId());

        System.out.println("---------------------------------------------");
        System.out.println("              < Transfers >");
        System.out.println(RED_BOLD+"  ID             From | To          Amount"+ANSI_RESET);
        System.out.println("---------------------------------------------");

        for(Transfer transfer: transfers) {
            TransferInfoToFrom(currentUser, transfer);
        }
        System.out.println("---------------------------------------------\n");
        int i = console.promptForInt("\n               Please enter transfer ID to view details (0 to cancel)");
        Transfer transfer = TransferIdVerification(i, transfers, currentUser);
        if(transfer != null) {
            transferDetails(currentUser, transfer);
        }

    }
    private Transfer TransferIdVerification(int transferIdChoice, Transfer[] transfers, AuthenticatedUser currentUser) {
        if (transferIdChoice == 0) {
            return null;
        }
        for (Transfer transfer : transfers) { //  for loop looking for a matching existing transfer ID
            if (transfer.getTransferId() == transferIdChoice) {
                return transfer;
            }
        }
        System.out.println("\n       Invalid transfer ID choice, please try again .");
        return null;
    }
	private void viewPendingRequests() {
		// TODO Auto-generated method stub
        Transfer[] transfers = transferService.getPendingTransfersUserId(currentUser);
        System.out.println("-------------------------------");
        System.out.println("      Pending Transfers");
        System.out.println(RED_BOLD+"ID          To          Amount"+ANSI_RESET);
        System.out.println("-------------------------------");

        for (Transfer transfer : transfers) {
            TransferInfoToFrom(currentUser, transfer);
        }
        int i = console.promptForInt("Enter transfer ID to approve/reject or (0 to cancel): ");

        Transfer transferChoice = TransferIdVerification(i, transfers, currentUser);

        if (transferChoice != null) {
            approveOrReject(transferChoice, currentUser);
        }
	}

    private void approveOrReject(Transfer pending, AuthenticatedUser authenticatedUser) {
        console.listPendindChoices();

        int choice = console.promptForInt("Enter 1 to approve, 2 to reject or (0) to cancel: ");

        if (choice == 1) {
            TransferStatus approvedStatus = transferStatusService.getTransferStatus(authenticatedUser, "Approved");
            pending.setTransferStatusId(approvedStatus.getTransferStatusId());
            transferService.updateTransfer(authenticatedUser, pending);

            System.out.println("Transfer approved successfully.");
        } else if (choice == 2) {
            TransferStatus rejectedStatus = transferStatusService.getTransferStatus(authenticatedUser, "Rejected");
            pending.setTransferStatusId(rejectedStatus.getTransferStatusId());
            transferService.updateTransfer(authenticatedUser, pending);

            System.out.println("Transfer rejected successfully.");
        } else if (choice == 0) {
            System.out.println("Operation cancelled.");
        } else {
            System.out.println("Invalid choice.");
        }
        viewCurrentBalance();
    }


	private void sendBucks() {
		// TODO Auto-generated method stub
        User[] users = userService.getAllUsers(currentUser);
        printUserListOptions(currentUser, users);

        int n = console.promptForInt("        Enter ID of user you are sending to or (0) to cancel");

        if (validateUserChoice(n, users, currentUser)) {
            String amount = console.promptForString("       Enter amount  ===>   ");
            int num = Integer.parseInt(amount);
            if (num <= 0) {
                System.out.println("Amount must be greater than 0 try again by Enter a Valid amout ");
                mainMenu();
            }
            Transfer transfer =   createTransfer(n, amount, "Send", "Approved");

            System.out.println(currentUser.getUser().getUsername());
            System.out.println("Transaction of "+RED_BOLD+amount+ANSI_RESET+" TE completed"+RED_BOLD+"succesfully"+ANSI_RESET+" Balance updated\n");
            viewCurrentBalance();
//
        }
	}

	private void requestBucks() {
		// TODO Auto-generated method stub
        User[] users = userService.getAllUsers(currentUser);
        printUserListOptions(currentUser, users);


        int i = console.promptForInt("        Enter ID of user you are requesting from or (0) to cancel");

        if (validateUserChoice(i, users, currentUser)) {
            String amount = console.promptForString("       Enter amount   ===>   ");
            int num = Integer.parseInt(amount);
            if (num <= 0) {
                System.out.println("Amount must be greater than 0 try again by Enter a Valid amout");
                mainMenu();
            }
            Transfer transfer= createTransfer(i, amount, "Request", "Pending");


            System.out.println(currentUser.getUser().getUsername()  );
            System.out.println("Transaction of "+RED_BOLD+amount+ANSI_RESET+" TE completed"+RED_BOLD+"succesfully"+ANSI_RESET+" Balance updated\n");
            viewCurrentBalance();}


	}

    private void TransferInfoToFrom(AuthenticatedUser authenticatedUser, Transfer transfer) {
        String fromOrTo = "";
        Account accountFrom = accountService.getAccountById(currentUser, transfer.getAccountFrom());
        Account accountTo = accountService.getAccountById(currentUser, transfer.getAccountTo());

        if (accountTo.getUserId() == authenticatedUser.getUser().getId()) {
            String userFromName = userService.getUserByUserId(currentUser, accountFrom.getUserId()).getUsername();
            fromOrTo = "From: " + userFromName;
        } else {
            String userToName = userService.getUserByUserId(currentUser, accountTo.getUserId()).getUsername();
            fromOrTo = "To: " + userToName;
        }

        console.printTransfers(transfer.getTransferId(), fromOrTo, transfer.getAmount());
    }
    private void transferDetails(AuthenticatedUser authenticatedUser, Transfer transfer) {

        int id = transfer.getTransferId();
        BigDecimal amount = transfer.getAmount();
        int fromAccountId = transfer.getAccountFrom();
        int toAccountId = transfer.getAccountTo();
        int transactionTypeId = transfer.getTransferTypeId();
        int transactionStatusId = transfer.getTransferStatusId();

        Account fromAccount = accountService.getAccountById(authenticatedUser, fromAccountId);
        String fromUserName = userService.getUserByUserId(authenticatedUser, fromAccount.getUserId()).getUsername();
        Account toAccount = accountService.getAccountById(authenticatedUser, toAccountId);
        String toUserName = userService.getUserByUserId(authenticatedUser, toAccount.getUserId()).getUsername();

        TransferType transferType = transferTypeService.getTransferTypeFromId(authenticatedUser, transactionTypeId);
        String transactionType = transferType.getTransferTypeDescription();

        TransferStatus transferStatus = transferStatusService.getTransferStatusById(authenticatedUser, transactionStatusId);
        String transactionStatus = transferStatus.getTransferStatusDesc();

        console.printTransferDetails(id, fromUserName, toUserName, transactionType, transactionStatus, amount);
    }

    private void printUserListOptions(AuthenticatedUser authenticatedUser, User[] users) {
        console.printListUsers(users);
    }

    private Transfer createTransfer(int accountChoiceUserId, String amountString, String transferType, String status) {

        // Get the IDs of the transfer type and status
        int transferTypeId = transferTypeService.getTransferType(currentUser, transferType).getTransferTypeId();
        int transferStatusId = transferStatusService.getTransferStatus(currentUser, status).getTransferStatusId();

        // Determine the sender and receiver accounts based on the transfer type
        int senderAccountId, receiverAccountId;
        if (transferType.equals("Send")) {
            senderAccountId = accountService.getAccountByUserId(currentUser, currentUser.getUser().getId()).getAccountId();
            receiverAccountId = accountService.getAccountByUserId(currentUser, accountChoiceUserId).getAccountId();
        } else {
            senderAccountId = accountService.getAccountByUserId(currentUser, accountChoiceUserId).getAccountId();
            receiverAccountId = accountService.getAccountByUserId(currentUser, currentUser.getUser().getId()).getAccountId();
        }

        // Generate a unique transfer ID
        UUID uniqueId = UUID.randomUUID();
        int transferId = uniqueId.hashCode();

        BigDecimal amount = new BigDecimal(amountString);

        Transfer transfer = new Transfer();
        transfer.setAccountFrom(senderAccountId);
        transfer.setAccountTo(receiverAccountId);
        transfer.setAmount(amount);
        transfer.setTransferStatusId(transferStatusId);
        transfer.setTransferTypeId(transferTypeId);
        transfer.setTransferId(transferId);

        transferService.createTransfer(currentUser, transfer);

        return transfer;
    }
    private boolean validateUserChoice(int userIdChoice, User[] users, AuthenticatedUser currentUser) {
        if (userIdChoice != 0) {
            for (User user : users) {
                if (user.getId() == userIdChoice) {
                    if (userIdChoice == currentUser.getUser().getId()) {
                        System.out.println("\n        Cannot send money to yourself. Please choose a different user.");
                        return false;
                    }
                    return true;
                }
            }
            System.out.println("\n        Invalid user ID. Please choose a different user.");
            return false;
        }
        return false;
    }
}



