package banking;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Menu {
    
    private Database datahandler;
    private User activeUser;

    public Menu() {
        this.datahandler = new Database();
        this.activeUser = null;
    }

    public boolean authenticateUserPass(String username, String password) {
        // check if user exists
        if (datahandler.doesUserExist(username)) {
            // get user data
            User requestedAccount = datahandler.getUserData(username);
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