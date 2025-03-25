package tests;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import banking.Database;
import banking.Menu;
import banking.Transaction;
import banking.User;

public class MenuTests {
    
    private Menu menu;
    MessageDigest md;

	@BeforeEach
    void setup() throws Exception {
        Scanner keyboardInput = new Scanner(System.in);
        Database dataHandler = new Database();
        this.menu = new Menu(keyboardInput,dataHandler);
        
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

        User testUser = new User("Test", "123",hashedPassword,0);
        
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
    	
        boolean success = this.menu.createUser("Test","123","password",0);
        try {
            assertTrue(success);
            this.menu.getDataHandler().deleteUser("Test");
        } catch (Exception e) {
            this.menu.getDataHandler().deleteUser("Test");
        }
    }
    
    @Test
    void testDepositAndWithdraw() throws Exception {
        User testUser = new User("Test", "123", "password", 100.00);
        this.menu.getDataHandler().createUser(testUser);
        

        this.menu.authenticateUserPass("Test", "password");

        double depositAmount = 50.00;
        testUser.deposit(depositAmount);
        assertTrue(testUser.getBalance() == 150.00);

        double withdrawAmount = 30.00;
        testUser.withdraw(withdrawAmount);
        assertTrue(testUser.getBalance() == 120.00);

        testUser.withdraw(999.00);
        assertTrue(testUser.getBalance() == 120.00);

        this.menu.getDataHandler().deleteUser("Test");

    void testUserCreationFailsForExistingUser() {
        this.menu.createUser("Test", "password", 0);
        boolean success = this.menu.createUser("Test", "password", 0);
        assertTrue(!success);
    }
    
    @Test
    void testLogOut() {
        this.menu.createUser("Test", "password", 0);
        this.menu.logOut();
        assertNull(menu.getActiveUser()); // Active user should be null after logout
    }
    
    @Test
    void testGetBalance() {
        User testUser = new User("Test", Menu.hashPassword("password"), 100.0);
        this.menu.getDataHandler().createUser(testUser);
        this.menu.authenticateUserPass("Test", "password");

        assertEquals(100.0, testUser.getBalance(), 0.01);
    }

    @Test
    void testDeposit() {
        User testUser = new User("Test", Menu.hashPassword("password"), 0);
        this.menu.getDataHandler().createUser(testUser);
        this.menu.authenticateUserPass("Test", "password");

        Transaction depositTransaction = testUser.deposit(200.0);
        assertNotNull(depositTransaction);
        assertEquals(200.0, testUser.getBalance(), 0.01);
    }

    @Test
    void testWithdraw() {
        User testUser = new User("Test", Menu.hashPassword("password"), 500);
        this.menu.getDataHandler().createUser(testUser);
        this.menu.authenticateUserPass("Test", "password");

        Transaction withdrawTransaction = testUser.withdraw(100.0);
        assertNotNull(withdrawTransaction);
        assertEquals(400.0, testUser.getBalance(), 0.01);
    }

    @Test
    void testWithdrawFailsForInsufficientBalance() {
        User testUser = new User("Test", Menu.hashPassword("password"), 50);
        this.menu.getDataHandler().createUser(testUser);
        this.menu.authenticateUserPass("Test", "password");

        Transaction withdrawTransaction = testUser.withdraw(100.0);
        assertNull(withdrawTransaction);
        assertEquals(50.0, testUser.getBalance(), 0.01);
    }

    @Test
    void testIssueCharge() {
        User testUser = new User("Test", Menu.hashPassword("password"), 500);
        this.menu.getDataHandler().createUser(testUser);
        this.menu.authenticateUserPass("Test", "password");

        testUser.issueCharge(50.0, "Service Fee");

        assertEquals(450.0, testUser.getBalance(), 0.01);
    }

}

    
