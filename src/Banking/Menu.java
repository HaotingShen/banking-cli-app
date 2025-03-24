package banking;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Menu {
    
    private Database dataHandler;
    private User activeUser;

    public Menu() {
        this.dataHandler = new Database();
        this.activeUser = null;
    }
    
    public Database getDataHandler() {
    	return dataHandler;
    }

    public boolean createUser(String username, String password, int balance) {
        if (dataHandler.doesUserExist(username)) {
            return false; // user already exists
        }
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(password.getBytes()); // Use UTF-8 for consistency

            // Convert hash bytes to hex string
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            String hashedPassword = sb.toString();
            User newUser = new User(username, hashedPassword, balance);
            dataHandler.createUser(newUser);
            this.activeUser = newUser;
            return true;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return false; // Error occurred during hashing
        }
    }


    public boolean authenticateUserPass(String username, String password) {
        // check if user exists
        if (dataHandler.doesUserExist(username)) {
            // get user data
            User requestedAccount = dataHandler.getUserData(username);
            // check if password matches hashes
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                byte[] hashBytes = md.digest(password.getBytes());

                // Convert hash bytes to hex string
                StringBuilder sb = new StringBuilder();
                for (byte b : hashBytes) {
                    sb.append(String.format("%02x", b));
                }

                String hashedPassword = sb.toString();
                if (requestedAccount.getHashedPassword().equals(hashedPassword)) {
                    this.activeUser = requestedAccount;
                    return true;
                }

            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException("SHA-256 algorithm not found.");
            }
    
        }
        return false;
    }
}