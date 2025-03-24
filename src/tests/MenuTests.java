package tests;
import static org.junit.Assert.assertTrue;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import banking.Menu;
import banking.User;

public class MenuTests {
    
    private Menu menu;
    MessageDigest md;

	@BeforeEach
    void setup() throws Exception {
        this.menu = new Menu();
        
        md = MessageDigest.getInstance("SHA-256");
    }

    @Test
    void testAuthentication() throws Exception {
    	
        String password = "password";
        byte[] hashBytes = md.digest(password.getBytes()); // Use UTF-8 for consistency

        // Convert hash bytes to hex string
        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }
        String hashedPassword = sb.toString();

        User testUser = new User("Test", hashedPassword,0);
        
        this.menu.getDataHandler().createUser(testUser);
        try {
            assertTrue(this.menu.authenticateUserPass("Test","password"));
        }
        catch (Exception e) {
            this.menu.getDataHandler().deleteUser("Test");
        }
        
    }

    @Test
    void testUserCreation() throws Exception {
    	
        boolean success = this.menu.createUser("Test","password",0);
        try {
            assertTrue(success);
            this.menu.getDataHandler().deleteUser("Test");
        } catch (Exception e) {
            this.menu.getDataHandler().deleteUser("Test");
        }
    }
}
