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
            printTransferStubDetails(currentUser, transfer);
        }
        System.out.println("---------------------------------------------\n");

        int i = console.promptForInt("               Please enter transfer ID to view details (0 to cancel)");
        Transfer transferChoice = validateTransferIdChoice(i, transfers, currentUser);
        if(transferChoice != null) {
            printTransferDetails(currentUser, transferChoice);
        }

    }
    private Transfer validateTransferIdChoice(int transferIdChoice, Transfer[] transfers, AuthenticatedUser currentUser) {

        Transfer transferChoice = null;
        if(transferIdChoice != 0) {
            boolean validTransferIdChoice = false;
            for (Transfer transfer : transfers) {
                if (transfer.getTransferId() == transferIdChoice) {
                    validTransferIdChoice = true;
                    transferChoice = transfer;
                    break;
                }
            }
            if (!validTransferIdChoice) {
mainMenu();
            }
        }
        return transferChoice;
    }
	private void viewPendingRequests() {
		// TODO Auto-generated method stub
        Transfer[] transfers = transferService.getPendingTransfersUserId(currentUser);
        System.out.println("-------------------------------");
        System.out.println("      Pending Transfers");
        System.out.println(RED_BOLD+"ID          To          Amount"+ANSI_RESET);
        System.out.println("-------------------------------");

        for (Transfer transfer : transfers) {
            printTransferStubDetails(currentUser, transfer);
        }
        // TODO ask to view details
        int transferIdChoice = console.promptForInt("          Please enter transfer"+RED_BOLD+ "ID"+ANSI_RESET+ "to approve/reject or (0 to cancel)");
        Transfer choice = validateTransferIdChoice(transferIdChoice, transfers, currentUser);
        if (choice != null) {
            approveOrReject(choice, currentUser);
        }
	}
    private void approveOrReject(Transfer pending, AuthenticatedUser authenticatedUse) {

        console.printPendindOptions();
        int choice = console.promptForInt("      Please choose a pending option ");

        if(choice != 0) {
            if(choice == 1) {
                int transferStatusId = transferStatusService.getTransferStatus(currentUser, "Approved").getTransferStatusId();
                pending.setTransferStatusId(transferStatusId);
            } else if (choice == 2) {
                int transferStatusId = transferStatusService.getTransferStatus(currentUser, "Rejected").getTransferStatusId();
                pending.setTransferStatusId(transferStatusId);
            } else {
                System.out.println("error.");
            }
            transferService.updateTransfer(currentUser, pending);


            System.out.println(currentUser.getUser().getUsername()  );
            viewCurrentBalance();

        }


    }
	private void sendBucks() {
		// TODO Auto-generated method stub
        User[] users = userService.getAllUsers(currentUser);
        printUserListOptions(currentUser, users);

        int n = console.promptForInt("        Enter ID of user you are sending to or (0 to cancel)");

        if (validateUserChoice(n, users, currentUser)) {
            String amount = console.promptForString("   Enter amount  ===>   ");
            int num = Integer.parseInt(amount);
            if(num>0){
            createTransfer(n, amount, "Send", "Approved");
            System.out.println("Transaction of "+RED_BOLD+amount+ANSI_RESET+" TE completed"+RED_BOLD+"succesfully"+ANSI_RESET+" Balance updated\n");
            System.out.println(currentUser.getUser().getUsername()  );
            viewCurrentBalance();}
            else System.out.println("    Enter a Valid amout ");
        }
	}

	private void requestBucks() {
		// TODO Auto-generated method stub
        User[] users = userService.getAllUsers(currentUser);
        printUserListOptions(currentUser, users);


        int userIdChoice = console.promptForInt("Enter ID of user you are requesting from or (0 to cancel)");
        if (validateUserChoice(userIdChoice, users, currentUser)) {
            String amount = console.promptForString("Enter amount   ===>   ");
            int num = Integer.parseInt(amount);
            if(num>0){
            createTransfer(userIdChoice, amount, "Request", "Pending");

            System.out.println("Transaction of "+RED_BOLD+amount+ANSI_RESET+" TE completed"+RED_BOLD+"succesfully"+ANSI_RESET+" Balance updated\n");
            System.out.println(currentUser.getUser().getUsername()  );
            viewCurrentBalance();}
            else System.out.println("    Enter a Valid amout ");
        }
	}

    private void printTransferStubDetails(AuthenticatedUser authenticatedUser, Transfer transfer) {
        String fromOrTo = "";

        int accountFrom = transfer.getAccountFrom();
        int accountTo = transfer.getAccountTo();
        if (accountService.getAccountById(currentUser, accountTo).getUserId() == authenticatedUser.getUser().getId()) {
            int accountFromUserId = accountService.getAccountById(currentUser, accountFrom).getUserId();
            String userFromName = userService.getUserByUserId(currentUser, accountFromUserId).getUsername();
            fromOrTo = "From: " + userFromName;
        } else {
            int accountToUserId = accountService.getAccountById(currentUser, accountTo).getUserId();
            String userToName = userService.getUserByUserId(currentUser, accountToUserId).getUsername();
            fromOrTo = "To: " + userToName;
        }

        console.printTransfers(transfer.getTransferId(), fromOrTo, transfer.getAmount());
    }
    private void printTransferDetails(AuthenticatedUser currentUser, Transfer transferChoice) {
        int id = transferChoice.getTransferId();
        BigDecimal amount = transferChoice.getAmount();
        int fromAccount = transferChoice.getAccountFrom();
        int toAccount = transferChoice.getAccountTo();
        int transactionTypeId = transferChoice.getTransferTypeId();
        int transactionStatusId = transferChoice.getTransferStatusId();

        int fromUserId = accountService.getAccountById(currentUser, fromAccount).getUserId();
        String fromUserName = userService.getUserByUserId(currentUser, fromUserId).getUsername();
        int toUserId = accountService.getAccountById(currentUser, toAccount).getUserId();
        String toUserName = userService.getUserByUserId(currentUser, toUserId).getUsername();
        String transactionType = transferTypeService.getTransferTypeFromId(currentUser, transactionTypeId).getTransferTypeDescription();
        String transactionStatus = transferStatusService.getTransferStatusById(currentUser, transactionStatusId).getTransferStatusDesc();

        console.printTransferDetails(id, fromUserName, toUserName, transactionType, transactionStatus, amount);
    }

    private void printUserListOptions(AuthenticatedUser authenticatedUser, User[] users) {
        console.printListUsers(users);
    }

    private Transfer createTransfer(int accountChoiceUserId, String amountString, String transferType, String status) {

        int transferTypeId = transferTypeService.getTransferType(currentUser, transferType).getTransferTypeId();
        int transferStatusId = transferStatusService.getTransferStatus(currentUser, status).getTransferStatusId();

        int acctToId,accFromId;;

        if (transferType.equals("Send")) {
            acctToId = accountService.getAccountByUserId(currentUser, accountChoiceUserId).getAccountId();
            accFromId = accountService.getAccountByUserId(currentUser, currentUser.getUser().getId()).getAccountId();
        } else {
            acctToId = accountService.getAccountByUserId(currentUser, currentUser.getUser().getId()).getAccountId();
            accFromId = accountService.getAccountByUserId(currentUser, accountChoiceUserId).getAccountId();
        }

        // Generate Unique transfer ID for Primary key transfer_id
        UUID UniqNum = UUID.randomUUID();
        int intValue = UniqNum.hashCode();

        BigDecimal amount = new BigDecimal(amountString);

        Transfer transfer = new Transfer();
        transfer.setAccountFrom(accFromId);
        transfer.setAccountTo(acctToId);
        transfer.setAmount(amount);
        transfer.setTransferStatusId(transferStatusId);
        transfer.setTransferTypeId(transferTypeId);
        transfer.setTransferId(intValue);

        transferService.createTransfer(currentUser, transfer);

        return transfer;
    }

    private boolean validateUserChoice(int userIdChoice, User[] users, AuthenticatedUser currentUser) {
        if (userIdChoice != 0) {
            boolean validUserIdChoice = false;

            for (User user : users) {
                if (userIdChoice == currentUser.getUser().getId()) {
                    System.out.println("\n        Cannot send money to yourself. Please choose a different user.");
                    mainMenu();

                }
                if (user.getId() == userIdChoice) {
                    validUserIdChoice = true;
                    break;
                }
            }
            if (validUserIdChoice == false) {
                System.out.println("Error ");
            }
            return true;
        }
        return false;
    }



        }



