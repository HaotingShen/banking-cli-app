package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import banking.Database;
import banking.Transaction;
import banking.User;

public class DatabaseTest {
    private Database myDatabase;

    @BeforeEach
    public void setUp() {
        myDatabase = new Database();
    }

    @Test
    void testCheckNonexistingUser() {
        assertEquals(false, myDatabase.doesUserExist("Test"));
    }

    @Test
    void testCreateUser() {
    	User testUser = new User("Test", "123", "password", 0);
        myDatabase.createUser(testUser);
        assertEquals(true, myDatabase.doesUserExist("Test"));
    }

    @Test
    void testGetUser() {
    	User testUser = new User("Test","123", "password", 0);
        User curUser = myDatabase.createUser(testUser);
        assertEquals(curUser, myDatabase.getUserData("Test"));
    }

    @Test
    void testDeleteUser() throws Exception {
    	User testUser = new User("Test", "123", "password", 0);
        User curUser = myDatabase.createUser(testUser);
        myDatabase.deleteUser("Test");
        assertEquals(false, myDatabase.doesUserExist("Test"));
    }

    @Test
    void testDeleteNonExistingUser() {
        assertThrows(RuntimeException.class, () -> {
            myDatabase.deleteUser("UnknownUser");
        });
    }
    
    @Test
    void testGetUserTransactionForNonexistentUser() {
        assertNull(myDatabase.getUserTransaction("NonexistentUser"));
    }

    @Test
    void testGetUserTransactionForExistingUserWithoutTransactions() {
        User testUser = new User("Test", "password", 0);
        myDatabase.createUser(testUser);
        
        List<Transaction> transactions = myDatabase.getUserTransaction("Test");

        assertNotNull(transactions);
        assertTrue(transactions.isEmpty());
    }

    @Test
    void testAddUserTransaction() {
        User testUser = new User("Test", "password", 0);
        myDatabase.createUser(testUser);

        Transaction transaction = new Transaction(50.0, "Amazon Purchase");
        myDatabase.addUserTransaction("Test", transaction);

        List<Transaction> transactions = myDatabase.getUserTransaction("Test");

        assertNotNull(transactions);
        assertEquals(1, transactions.size());
        assertEquals(50.0, transactions.get(0).getAmount());
        assertEquals("Amazon Purchase", transactions.get(0).getDescription());
    }

    @Test
    void testAddMultipleUserTransactions() {
        User testUser = new User("Test", "password", 0);
        myDatabase.createUser(testUser);

        Transaction t1 = new Transaction(25.0, "Grocery Shopping");
        Transaction t2 = new Transaction(75.0, "Online Order");

        myDatabase.addUserTransaction("Test", t1);
        myDatabase.addUserTransaction("Test", t2);

        List<Transaction> transactions = myDatabase.getUserTransaction("Test");

        assertNotNull(transactions);
        assertEquals(2, transactions.size());
        assertEquals(25.0, transactions.get(0).getAmount());
        assertEquals("Grocery Shopping", transactions.get(0).getDescription());
        assertEquals(75.0, transactions.get(1).getAmount());
        assertEquals("Online Order", transactions.get(1).getDescription());
    }
    
    @Test
    void testSetUserTransactionForNonexistentUser() {
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction(100.0, "Rent Payment"));

        myDatabase.setUserTransaction("NonexistentUser", transactions);

        // Since user does not exist, transactions should not be set
        assertNull(myDatabase.getUserTransaction("NonexistentUser"));
    }

    @Test
    void testSetUserTransactionForExistingUser() {
        User testUser = new User("Test", "password", 0);
        myDatabase.createUser(testUser);

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction(30.0, "Dinner"));
        transactions.add(new Transaction(200.0, "New Laptop"));

        myDatabase.setUserTransaction("Test", transactions);

        List<Transaction> retrievedTransactions = myDatabase.getUserTransaction("Test");

        assertNotNull(retrievedTransactions);
        assertEquals(2, retrievedTransactions.size());
        assertEquals(30.0, retrievedTransactions.get(0).getAmount());
        assertEquals("Dinner", retrievedTransactions.get(0).getDescription());
        assertEquals(200.0, retrievedTransactions.get(1).getAmount());
        assertEquals("New Laptop", retrievedTransactions.get(1).getDescription());
    }
}
