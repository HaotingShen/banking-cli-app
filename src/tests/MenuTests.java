package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.security.MessageDigest;
import java.util.Scanner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import banking.Database;
import banking.Menu;
import banking.User;
import banking.SafeInput;
import banking.Authenticator;

public class MenuTests {
    
    private Menu menu;
    MessageDigest md;

	@BeforeEach
    void setup() throws Exception {
        SafeInput keyboardInput = new SafeInput(new Scanner(System.in));
        Database dataHandler = new Database();
        this.menu = new Menu(dataHandler,keyboardInput);
        
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

        User testUser = new User("Test",hashedPassword,0);
        
        this.menu.getDataHandler().createUser(testUser);
        try {
            assertTrue(this.menu.authenticateUserPass("Test","password"));
            this.menu.getDataHandler().deleteUser("Test");
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

    @Test
    void testUserCreationFailsForExistingUser() throws Exception {
        this.menu.createUser("Test", "password", 0);
        boolean success = this.menu.createUser("Test", "password", 0);
        assertTrue(!success);
        this.menu.getDataHandler().deleteUser("Test");
    }
    
    @Test
    void testLogOut() throws Exception {
        this.menu.createUser("Test", "password", 0);
        this.menu.logOut();
        assertNull(menu.getActiveUser()); // Active user should be null after logout
        this.menu.getDataHandler().deleteUser("Test");
    }
    
    @Test
    void testGetBalance() throws Exception {
        User testUser = new User("Test", Authenticator.hashPassword("password"), 100.0);

        this.menu.getDataHandler().createUser(testUser);
        this.menu.authenticateUserPass("Test", "password");

        assertEquals(100.0, testUser.getBalance(), 0.01);
        this.menu.getDataHandler().deleteUser("Test");
    }

}