package banking;
import banking.Database; //future class, handle reading from our files for persistence
import banking.Option;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Scanner;
import java.util.ArrayList;

public class Menu {
    
    private Database dataHandler;
    private User activeUser;
    private List<Option> publicOptions;
    private List<Option> privateOptions;
    private Scanner keyboardInput;
    private boolean running;


    public Menu(Scanner keyboardInput, Database dataHandler) {
        this.dataHandler = dataHandler;
        this.keyboardInput = keyboardInput;
        this.activeUser = null;
        this.publicOptions = new ArrayList<>();
        publicOptions.add(new Option("Login to account", this::login));
        publicOptions.add(new Option("Create account", this::signUp));
        publicOptions.add(new Option("Exit",this::shutDown));
        this.privateOptions = new ArrayList<>();
        publicOptions.add(new Option("Check Balance",this::getBalance));
        publicOptions.add(new Option("Deposit",this::deposit));
        publicOptions.add(new Option("Withdraw",this::withdraw));
        publicOptions.add(new Option("Logout",this::logOut));
        this.running = false;
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
        System.out.print("Your balance is currently : "+ activeUser.getBalance());
    }

    public void deposit() {
        System.out.println("How much would you like to deposit?");
        double amount = keyboardInput.nextDouble();
        activeUser.deposit(amount);
        System.out.println("Success! Your new balance is: " + activeUser.getBalance());
    }

    public void withdraw() {
        System.out.println("How much would you like to withdraw?");
        double amount = keyboardInput.nextDouble();
        if (activeUser.getBalance() >= amount) {
            activeUser.withdraw(amount);
            System.out.println("Successfully withdrew" + amount + "! Here is your cash: $$$");
        } else {
            System.out.println("There is insufficient balance in your account to cover the withdraw...");
        }
    }

    public void printMenu(List<Option> items) {
        int i = 1;
        for (Option item : items) {
            System.out.println(i + ". " + item.getOptionName());
            i++;
        }
        int userChoice = keyboardInput.nextInt();
        i = 1;
        for (Option item : items) {
            if (i == userChoice) {
                item.execute();
            }
            i++;
        }
    }

    private void login() {
        System.out.print("Enter username: ");
        String username = keyboardInput.nextLine();
    
        System.out.print("Enter password: ");
        String password = keyboardInput.nextLine();
    
        if (dataHandler.doesUserExist(username)) {
            User requestedAccount = dataHandler.getUserData(username);
            if (requestedAccount.getHashedPassword().equals(Menu.hashPassword(password))) {
                this.activeUser = requestedAccount;
                System.out.println("Login successful!");
            } else {
                System.out.println("Login failed: password hashes do not match");
            }
        } else {
            System.out.println("Login failed: user does not exist");
        }
    }
    
    private void signUp() {
        System.out.print("Enter new username: ");
        String username = keyboardInput.nextLine();
    
        System.out.print("Enter password: ");
        String password = keyboardInput.nextLine();

        System.out.print("Confirm password: ");
        String passwordConfirmation = keyboardInput.nextLine();

        if(!password.equals(passwordConfirmation)) {
            System.out.println("Passwords do not match, exiting...");
        } else {
            if (dataHandler.doesUserExist(username)) {
                dataHandler.createUser(username,password,0);
                System.out.println("Account created successfully!");
            } else {
                System.out.println("Account already exists.");
            }
        }
    }

    public void logOut() {
        this.activeUser = null;
        System.out.println("Logged out Succesfully");
    }

    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(password.getBytes());

            // Convert hash bytes to hex string
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }

            String hashedPassword = sb.toString();
            return hashedPassword;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found.");
        }
}

}