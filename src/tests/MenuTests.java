package tests;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
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
    void testDepositAndWithdraw() throws Exception {
        User testUser = new User("Test", Authenticator.hashPassword("password"), 100.00);

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

    @Test
    void testDeposit() throws Exception {
        User testUser = new User("Test", Authenticator.hashPassword("password"), 0);

        this.menu.getDataHandler().createUser(testUser);
        this.menu.authenticateUserPass("Test", "password");

        Transaction depositTransaction = testUser.deposit(200.0);
        assertNotNull(depositTransaction);
        assertEquals(200.0, testUser.getBalance(), 0.01);
        this.menu.getDataHandler().deleteUser("Test");
    }

    @Test
    void testWithdraw() throws Exception {
        User testUser = new User("Test", Authenticator.hashPassword("password"), 500);

        this.menu.getDataHandler().createUser(testUser);
        this.menu.authenticateUserPass("Test", "password");

        Transaction withdrawTransaction = testUser.withdraw(100.0);
        assertNotNull(withdrawTransaction);
        assertEquals(400.0, testUser.getBalance(), 0.01);
        this.menu.getDataHandler().deleteUser("Test");
    }

    @Test
    void testWithdrawFailsForInsufficientBalance() throws Exception {
        User testUser = new User("Test", Authenticator.hashPassword("password"), 50);

        this.menu.getDataHandler().createUser(testUser);
        this.menu.authenticateUserPass("Test", "password");

        Transaction withdrawTransaction = testUser.withdraw(100.0);
        assertNull(withdrawTransaction);
        assertEquals(50.0, testUser.getBalance(), 0.01);
        this.menu.getDataHandler().deleteUser("Test");
    }

    @Test
    void testSuccessfulTransfer() throws Exception {
        User sender = new User("Sender", Menu.hashPassword("password"), 500);
        User receiver = new User("Receiver", Menu.hashPassword("password"), 300);
        this.menu.getDataHandler().createUser(sender);
        this.menu.getDataHandler().createUser(receiver);

        Transaction transfer = sender.transferTo(receiver, 200.0, "Rent payment");
        assertNotNull(transfer);
        this.menu.getDataHandler().addUserTransaction(sender.getUsername(), transfer);

        Transaction receive = receiver.receiveTransfer(200.0, sender.getUsername(), "Rent payment");
        this.menu.getDataHandler().addUserTransaction(receiver.getUsername(), receive);

        assertEquals(300.0, sender.getBalance(), 0.01);
        assertEquals(500.0, receiver.getBalance(), 0.01);
        this.menu.getDataHandler().deleteUser("Sender");
        this.menu.getDataHandler().deleteUser("Receiver");
    }

    @Test
    void testTransferFailsForInsufficientFunds() throws Exception {
        User sender = new User("Sender", Menu.hashPassword("password"), 100);
        User receiver = new User("Receiver", Menu.hashPassword("password"), 300);
        this.menu.getDataHandler().createUser(sender);
        this.menu.getDataHandler().createUser(receiver);

        Transaction transfer = sender.transferTo(receiver, 200.0, "Large transfer");
        assertNull(transfer);
        assertEquals(100.0, sender.getBalance(), 0.01);
        assertEquals(300.0, receiver.getBalance(), 0.01);
        this.menu.getDataHandler().deleteUser("Sender");
        this.menu.getDataHandler().deleteUser("Receiver");
    }

    @Test
    void testTransferToSelfFails() throws Exception {
        User sender = new User("SelfUser", Menu.hashPassword("password"), 500);
        this.menu.getDataHandler().createUser(sender);

        Transaction transfer = sender.transferTo(sender, 100.0, "Trying to send to self");
        assertNull(transfer);
        assertEquals(500.0, sender.getBalance(), 0.01);

        this.menu.getDataHandler().deleteUser("SelfUser");
    }

    @Test
    void testTransferWithInvalidAmount() throws Exception {
        User sender = new User("Sender", Menu.hashPassword("password"), 500);
        User receiver = new User("Receiver", Menu.hashPassword("password"), 500);
        this.menu.getDataHandler().createUser(sender);
        this.menu.getDataHandler().createUser(receiver);

        Transaction zeroTransfer = sender.transferTo(receiver, 0.0, "Zero transfer");
        Transaction negativeTransfer = sender.transferTo(receiver, -50.0, "Negative transfer");
        
        assertNull(zeroTransfer);
        assertNull(negativeTransfer);
        assertEquals(500.0, sender.getBalance(), 0.01);
        assertEquals(500.0, receiver.getBalance(), 0.01);
        this.menu.getDataHandler().deleteUser("Sender");
        this.menu.getDataHandler().deleteUser("Receiver");
    }

    @Test
    void testIssueChargeValidAmount() throws Exception {
        User issuer = new User("UserA", Authenticator.hashPassword("password"), 500);
        User target = new User("UserB", Authenticator.hashPassword("password"), 500);
        this.menu.getDataHandler().createUser(issuer);
        this.menu.getDataHandler().createUser(target);

        Transaction chargeTransaction = issuer.issueCharge(target, 100.0, "Test charge");
        assertNotNull(chargeTransaction);
        assertEquals(600.0, issuer.getBalance(), 0.01);
        assertEquals(400.0, target.getBalance(), 0.01);
        this.menu.getDataHandler().deleteUser("UserA");
        this.menu.getDataHandler().deleteUser("UserB");
    }

    @Test
    void testIssueChargeInsufficientBalance() throws Exception {
        User issuer = new User("UserA", Authenticator.hashPassword("password"), 500);
        User target = new User("UserB", Authenticator.hashPassword("password"), 50);
        this.menu.getDataHandler().createUser(issuer);
        this.menu.getDataHandler().createUser(target);

        Transaction failedCharge = issuer.issueCharge(target, 100.0, "Test charge");
        assertNull(failedCharge);
        assertEquals(500.0, issuer.getBalance(), 0.01);
        assertEquals(50.0, target.getBalance(), 0.01);
        this.menu.getDataHandler().deleteUser("UserA");
        this.menu.getDataHandler().deleteUser("UserB");
    }

    @Test
    void testIssueChargeInvalidAmount() throws Exception {
        User issuer = new User("UserA", Authenticator.hashPassword("password"), 500);
        User target = new User("UserB", Authenticator.hashPassword("password"), 500);
        this.menu.getDataHandler().createUser(issuer);
        this.menu.getDataHandler().createUser(target);

        Transaction zeroCharge = issuer.issueCharge(target, 0.0, "Zero charge");
        Transaction negativeCharge = issuer.issueCharge(target, -50.0, "Negative charge");
      
        assertNull(zeroCharge);
        assertNull(negativeCharge);
        assertEquals(500.0, issuer.getBalance(), 0.01);
        assertEquals(500.0, target.getBalance(), 0.01);
        this.menu.getDataHandler().deleteUser("UserA");
        this.menu.getDataHandler().deleteUser("UserB");
    }

    @Test
    void testIssuerStatementIncludesChargeTransaction() throws Exception {
        User issuer = new User("Issuer", Authenticator.hashPassword("password"), 500);
        User target = new User("Target", Authenticator.hashPassword("password"), 500);
        this.menu.getDataHandler().createUser(issuer);
        this.menu.getDataHandler().createUser(target);

        Transaction deposit = issuer.deposit(100.0);
        Transaction charge = issuer.issueCharge(target, 50.0, "Test Service");
        this.menu.getDataHandler().addUserTransaction(issuer.getUsername(), deposit);
        this.menu.getDataHandler().addUserTransaction(issuer.getUsername(), charge);

        List<Transaction> issuerTransactions = this.menu.getDataHandler().getUserTransaction(issuer.getUsername());
        assertEquals(2, issuerTransactions.size());
        assertEquals("Deposit", issuerTransactions.get(0).getDescription());
        assertTrue(issuerTransactions.get(1).getDescription().contains("Test Service"));
        this.menu.getDataHandler().deleteUser("Issuer");
        this.menu.getDataHandler().deleteUser("Target");
    }

    @Test
    void testTargetStatementIncludesChargeReceived() throws Exception {
        User issuer = new User("Issuer", Authenticator.hashPassword("password"), 500);
        User target = new User("Target", Authenticator.hashPassword("password"), 500);
        this.menu.getDataHandler().createUser(issuer);
        this.menu.getDataHandler().createUser(target);

        issuer.issueCharge(target, 50.0, "Test Service");
        Transaction chargeRecord = target.issueChargeRecord(50.0, issuer.getUsername(), "Test Service");
        this.menu.getDataHandler().addUserTransaction(target.getUsername(), chargeRecord);

        List<Transaction> targetTransactions = this.menu.getDataHandler().getUserTransaction(target.getUsername());
        assertEquals(1, targetTransactions.size());
        assertTrue(targetTransactions.get(0).getDescription().contains("Issuer"));
        this.menu.getDataHandler().deleteUser("Issuer");
        this.menu.getDataHandler().deleteUser("Target");
    }

}