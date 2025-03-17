package Banking;
import Banking.Database; //future class, handle reading from our files for persistence
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Menu {
    
    private Database datahandler;
    private User activeUser;

    public Menu() {
        this.datahandler = new Database();
        this.activeUser = null;
    }

    public boolean authenticate_user_pass(String username, String password) {
        // check if user exists
        if (datahandler.does_userexist(username)) {
            // get user data
            User requestedAccount = datahandler.get_userdata(username);
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
                if (requestedAccount.get_hashed_password().equals(hashedPassword)) {
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