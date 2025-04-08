package banking;
import banking.SafeInput;
import java.security.MessageDigest; //future class, handle reading from our files for persistence
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import banking.Authenticator;
import banking.QRCodeGenerator;

public class Menu {
    
    private Database dataHandler;
    private User activeUser;
    private List<Option> publicOptions;
    private List<Option> privateOptions;
    private boolean running;
    private SafeInput keyboardInput;

    public Menu(Database dataHandler, SafeInput keyboardInput) {
        this.dataHandler = dataHandler;
        this.keyboardInput = keyboardInput;
        this.activeUser = null;
        this.publicOptions = new ArrayList<>();
        publicOptions.add(new Option("Login to account", this::login));
        publicOptions.add(new Option("Create account", this::signUp));
        publicOptions.add(new Option("Reset Password", this::recoverAccount));
        publicOptions.add(new Option("Exit",this::shutDown));
        this.privateOptions = new ArrayList<>();
        privateOptions.add(new Option("Check Balance",this::getBalance));
        privateOptions.add(new Option("View Account Number",this::getAccountNumber));
        privateOptions.add(new Option("Deposit",this::deposit));
        privateOptions.add(new Option("Withdraw",this::withdraw));
        privateOptions.add(new Option("Transfer Money", this::transferMoney));
        privateOptions.add(new Option("Issue Charge",this::issueCharge));
        privateOptions.add(new Option("Print Statement",this::printStatement));
        privateOptions.add(new Option("Change Password", this::changePassword));
        privateOptions.add(new Option("Change Username", this::changeUsername));
        privateOptions.add(new Option("Enable 2FA Recovery", this::enable2FA));
        privateOptions.add(new Option("Logout",this::logOut));
        this.running = false;
    }
    
    public Database getDataHandler() {
    	return dataHandler;
    }

    public void run() {
        this.running = true;
        while (this.running) {
            printScopedMenu();
        }
    }

    public void shutDown() {
        this.running = false;
    }

    public void printScopedMenu() {
        if (this.activeUser != null) {
            this.printMenu(privateOptions);
        } else {
            this.printMenu(publicOptions);
        }
    }

    public void getBalance() {
        System.out.printf("Your balance is currently: %.2f%n", activeUser.getBalance());//added precision for double
    }
    
    public void getAccountNumber() {
        System.out.println("Your account number is: " + activeUser.getAccountNumber());
    }

    public void issueCharge() {
        // Since scanner returns Strings by default, just return any passed input.
        String accountNumber = keyboardInput.getSafeInput("Who would you like to charge (their account number): ","",Function.identity());

        User userToCharge = dataHandler.getUserByAccountNumber(accountNumber);

        if (userToCharge == null) {
            System.out.println("No user found with the provided account number.");
            return;
        }

        double chargeAmount = keyboardInput.getSafeInput("Charge amount: ","Invalid amount. Please enter a number.",Double::parseDouble);


        String chargeDesc = keyboardInput.getSafeInput("Charge description: ","",Function.identity());
        Transaction issuerTransaction = activeUser.issueCharge(userToCharge, chargeAmount, chargeDesc);
        if (issuerTransaction != null) {
            dataHandler.addUserTransaction(activeUser.getUsername(), issuerTransaction);
            dataHandler.addUserTransaction(userToCharge.getUsername(), userToCharge.issueChargeRecord(chargeAmount, activeUser.getUsername(), chargeDesc));
        }
        else {
            System.out.println("Charge failed.");
        }
    }

    public void printStatement() {
        if(activeUser != null) {
            List<Transaction> transactions = dataHandler.getUserTransaction(
                activeUser.getUsername()
            );
            activeUser.printStatement(transactions);
        }
    }

    public void deposit() {
        double amount = keyboardInput.getSafeInput("How much would you like to deposit?","Invalid amount. Please enter a number.",Double::parseDouble);
        Transaction newTransaction = activeUser.deposit(amount);
        if(newTransaction!=null) {
        	dataHandler.addUserTransaction(activeUser.getUsername(), newTransaction);
        }
        else {
        	System.out.println("Invalid amount deposited!");
        }
    }

    public void withdraw() {
        double amount = keyboardInput.getSafeInput("How much would you like to withdraw?","Invalid amount. Please enter a number.",Double::parseDouble);
        Transaction newTransaction = activeUser.withdraw(amount);
        if (newTransaction!=null) {
            dataHandler.addUserTransaction(activeUser.getUsername(), newTransaction);
        }
    }

    public void transferMoney() {
        String accountNumber = keyboardInput.getSafeInput("Enter recipient's account number:", "", Function.identity());
        User recipient = dataHandler.getUserByAccountNumber(accountNumber);
    
        if (recipient == null) {
            System.out.println("No user found with that account number.");
            return;
        }
    
        double amount = keyboardInput.getSafeInput("Enter amount to transfer:", "Invalid amount. Please enter a number.", Double::parseDouble);
        String description = keyboardInput.getSafeInput("Enter transfer description:", "", Function.identity());
    
        Transaction senderTransaction = activeUser.transferTo(recipient, amount, description);
        if (senderTransaction != null) {
            Transaction recipientTransaction = recipient.receiveTransfer(amount, activeUser.getUsername(), description);
            dataHandler.addUserTransaction(activeUser.getUsername(), senderTransaction);
            dataHandler.addUserTransaction(recipient.getUsername(), recipientTransaction);
        }
    }

    public void printMenu(List<Option> items) {
        int i = 1;
        for (Option item : items) {
            System.out.println(i + ". " + item.getOptionName());
            i++;
        }
        // passes a lambda which imposes the additional valid input range restriction. 
        int userChoice = keyboardInput.getSafeInput("Enter a number [1-"+items.size()+"]: ","Invalid selection. Please enter a number between 1 and "+items.size(), input -> {
            int value = Integer.parseInt(input);
            if (value < 1 || value > items.size()) {
                throw new IllegalArgumentException("Out of range");
            }
            return value;
        });
        i = 1;
        for (Option item : items) {
            if (i == userChoice) {
                item.execute();
            }
            i++;
        }
    }

    private void login() {
        String username = keyboardInput.getSafeInput("Enter username: ","",Function.identity());
    
        String password = keyboardInput.getSafeInput("Enter password: ","",Function.identity());
    
        if (dataHandler.doesUserExist(username)) {
            if(authenticateUserPass(username, password)) {
                System.out.println("Login successful!");
            } else {
                System.out.println("Login failed: password hashes do not match");
            }
        } else {
            System.out.println("Login failed: user does not exist");
        }
    }

    public boolean authenticateUserPass(String username,String password) {
        User requestedAccount = dataHandler.getUserData(username);
        if (requestedAccount.getHashedPassword().equals(Authenticator.hashPassword(password))) {
            this.activeUser = requestedAccount;
            return true;
        }
        return false;
    }
    
    public void signUp() {
        String username = keyboardInput.getSafeInput("Enter new username: ","",Function.identity());
    
        String password = keyboardInput.getSafeInput("Enter password: ","",Function.identity());

        String passwordConfirmation = keyboardInput.getSafeInput("Confirm password: ","",Function.identity());

        if(!password.equals(passwordConfirmation)) {
            System.out.println("Passwords do not match, exiting...");
        } else {
            if (createUser(username, passwordConfirmation, 0)) {
                System.out.println("Account created successfully!");
            } else {
                System.out.println("Account already exists.");
            }
        }
    }

    public void enable2FA() {
        String userSecret = Authenticator.generateSecureSecret();
        this.activeUser.setSecret(userSecret);
        System.out.println("Add the following code to your 2FA application (authy, google authenticatr, etc): "+userSecret);
        QRCodeGenerator.printQRCodeFromSecret(this.activeUser.getUsername(),this.activeUser.getSecret());
    }

    public void recoverAccount() {
        String username = keyboardInput.getSafeInput("Please enter the username of the account you want to reset: ","",Function.identity());
        if (!dataHandler.doesUserExist(username)) {
            System.out.println("Username does not exist.");
            return;
        }
        User requestedUser = dataHandler.getUserData(username);
        if (requestedUser.getSecret() == null) {
            System.out.println("You have not enabled 2FA.");
            return;
        }
        int currentCode = keyboardInput.getSafeInput("Please enter your 6 digit 2FA code","Please enter a valid 6 digit code",input->{
            if (input.length() == 6) {
                return Integer.parseInt(input);
            } else {
                throw new IllegalArgumentException("Invalid Length");
            }
        });
        if (Authenticator.validateTOTP(requestedUser.getSecret(),currentCode)) {
            resetPassword(requestedUser);
        } else {
            System.out.println("Invalid 2FA code.");
        }
    }

    public void resetPassword(User target) {
        String password = keyboardInput.getSafeInput("Enter new password: ","",Function.identity());
        String passwordConfirmation = keyboardInput.getSafeInput("Confirm password: ","",Function.identity());
        if(!password.equals(passwordConfirmation)) {
            System.out.println("Passwords do not match, exiting...");
        } else {
            target.resetPassword(Authenticator.hashPassword(password));
            System.out.println("Your password has been succesfully reset!");
        }
    }

    public void changePassword() {
        // just a wrapper since the option class takes in a function with no inputs or return type.
        resetPassword(this.activeUser);
        dataHandler.updateUserInfo();
    }
    
    public void changeUsername() {
        String newUsername = keyboardInput.getSafeInput("Enter new username: ", "", Function.identity());

        // checking if the new username is already taken
        if (dataHandler.doesUserExist(newUsername)) {
            System.out.println("Username already taken. Please choose another one.");
            return;
        }

        String oldUsername = activeUser.getUsername();

        activeUser.changeUsername(newUsername);
        dataHandler.updateUsername(oldUsername, newUsername, activeUser);

        System.out.println("Username changed successfully to: " + newUsername);
    }

    public boolean createUser(String username, String password, double balance) {
        if (!dataHandler.doesUserExist(username)) {
            User userToRegister = new User(username, Authenticator.hashPassword(password), balance);
            this.activeUser = dataHandler.createUser(userToRegister);
            return true;
        }
        return false;
    }

    public void logOut() {
        this.activeUser = null;
        System.out.println("Logged out Succesfully");
    }
    
    public User getActiveUser() {
    	return activeUser;
    }

}