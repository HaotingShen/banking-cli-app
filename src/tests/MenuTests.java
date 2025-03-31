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
    void testDepositAndWithdraw() throws Exception {
        User testUser = new User("Test", "password", 100.00);
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
    }

    @Test
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
        User testUser = new User("Test", Authenticator.hashPassword("password"), 100.0);
        this.menu.getDataHandler().createUser(testUser);
        this.menu.authenticateUserPass("Test", "password");

        assertEquals(100.0, testUser.getBalance(), 0.01);
    }

    @Test
    void testDeposit() {
        User testUser = new User("Test", Authenticator.hashPassword("password"), 0);
        this.menu.getDataHandler().createUser(testUser);
        this.menu.authenticateUserPass("Test", "password");

        Transaction depositTransaction = testUser.deposit(200.0);
        assertNotNull(depositTransaction);
        assertEquals(200.0, testUser.getBalance(), 0.01);
    }

    @Test
    void testWithdraw() {
        User testUser = new User("Test", Authenticator.hashPassword("password"), 500);
        this.menu.getDataHandler().createUser(testUser);
        this.menu.authenticateUserPass("Test", "password");

        Transaction withdrawTransaction = testUser.withdraw(100.0);
        assertNotNull(withdrawTransaction);
        assertEquals(400.0, testUser.getBalance(), 0.01);
    }

    @Test
    void testWithdrawFailsForInsufficientBalance() {
        User testUser = new User("Test", Authenticator.hashPassword("password"), 50);
        this.menu.getDataHandler().createUser(testUser);
        this.menu.authenticateUserPass("Test", "password");

        Transaction withdrawTransaction = testUser.withdraw(100.0);
        assertNull(withdrawTransaction);
        assertEquals(50.0, testUser.getBalance(), 0.01);
    }

    @Test
    void testIssueCharge() {
        User testUser = new User("Test", Authenticator.hashPassword("password"), 500);
        this.menu.getDataHandler().createUser(testUser);
        this.menu.authenticateUserPass("Test", "password");

        testUser.issueCharge(50.0, "Service Fee");

        assertEquals(450.0, testUser.getBalance(), 0.01);
    }

    @Test
    void testIssueChargeValidAmount() {
        User userA = new User("UserA", Authenticator.hashPassword("password"), 500);
        this.menu.getDataHandler().createUser(userA);

        Transaction chargeTransaction = userA.issueCharge(100.0, "Test charge");

        assertNotNull(chargeTransaction);
        assertEquals(-100.0, chargeTransaction.getAmount(), 0.01);
        assertEquals(400.0, userA.getBalance(), 0.01);
    }

    @Test
    void testIssueChargeInsufficientBalance() {
        User userB = new User("UserB", Authenticator.hashPassword("password"), 50);
        this.menu.getDataHandler().createUser(userB);

        Transaction failedCharge = userB.issueCharge(100.0, "Overdrawn");

        assertNull(failedCharge);
        assertEquals(50.0, userB.getBalance(), 0.01);
    }

    @Test
    void testIssueChargeInvalidAmount() {
        User userC = new User("UserC", Authenticator.hashPassword("password"), 200);
        this.menu.getDataHandler().createUser(userC);

        Transaction zeroCharge = userC.issueCharge(0.0, "Zero");
        Transaction negativeCharge = userC.issueCharge(-50.0, "Negative");

        assertNull(zeroCharge);
        assertNull(negativeCharge);
        assertEquals(200.0, userC.getBalance(), 0.01);
    }

    @Test
    void testPrintStatementIncludesCorrectTransactions() {
        User userD = new User("UserD", Authenticator.hashPassword("password"), 0);
        this.menu.getDataHandler().createUser(userD);
        this.menu.authenticateUserPass("UserD", "password");

        Transaction t1 = userD.deposit(100.0);
        Transaction t2 = userD.issueCharge(50.0, "Test Service");

        this.menu.getDataHandler().addUserTransaction(userD.getUsername(), t1);
        this.menu.getDataHandler().addUserTransaction(userD.getUsername(), t2);

        assertEquals(50.0, userD.getBalance(), 0.01);
        assertEquals(2, this.menu.getDataHandler().getUserTransaction(userD.getUsername()).size());
        assertEquals("Deposit", this.menu.getDataHandler().getUserTransaction(userD.getUsername()).get(0).getDescription());
        assertTrue(this.menu.getDataHandler().getUserTransaction(userD.getUsername()).get(1).getDescription().contains("Test Service"));
    }


}

    
