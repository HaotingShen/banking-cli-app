package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import banking.Database;
import banking.Transaction;
import banking.User;

public class DatabaseTest {
    private Database myDatabase;
    private String testUsername;
    private User testUser;
    private String usersFile = "users.ser";
    private String transactionsFile = "transactions.ser";

    @BeforeEach
    public void setUp() throws Exception {
        myDatabase = new Database();  // Load existing data
        testUsername = "testPersistence";
        testUser = new User(testUsername, "password", 0);
    }

    @Test
    void testCheckNonexistingUser() {
        assertFalse(myDatabase.doesUserExist("Test112233"));
    }

    @Test
    void testCreateUser() throws Exception {
        myDatabase.createUser(testUser);
        assertTrue(myDatabase.doesUserExist(testUsername));

        // Reload database and ensure persistence
        Database reloadedDb = new Database();
        assertTrue(reloadedDb.doesUserExist(testUsername));
        myDatabase.deleteUser(testUsername);
    }

    @Test
    void testDeleteUser() throws Exception {
        myDatabase.createUser(testUser);
        myDatabase.deleteUser(testUsername);
        assertFalse(myDatabase.doesUserExist(testUsername));
    }
    
    @Test
    void testGetUser() throws Exception {
        myDatabase.createUser(testUser);
        assertEquals(testUser, myDatabase.getUserData(testUsername));
        myDatabase.deleteUser(testUsername);
    }

    @Test
    void testDeleteNonExistingUser() {
        assertThrows(RuntimeException.class, () -> {
            myDatabase.deleteUser("UnknownUser");
        });
    }

    @Test
    void testSetUserTransactionForNonexistentUser() {
        List<Transaction> transactions = new ArrayList<Transaction>();
        transactions.add((new Transaction(100.0, "Rent Payment")));

        myDatabase.setUserTransaction("NonexistentUser", transactions);

        assertNull(myDatabase.getUserTransaction("NonexistentUser"));
    }

    @Test
    void testSetUserTransactionForExistingUser() throws Exception {
        myDatabase.createUser(testUser);

        List<Transaction> transactions = new ArrayList<>(List.of(
    	    new Transaction(30.0, "Dinner"),
    	    new Transaction(200.0, "New Laptop")
    	));

        myDatabase.setUserTransaction(testUsername, transactions);

        // Reload and verify persistence
        Database reloadedDb = new Database();
        List<Transaction> retrievedTransactions = reloadedDb.getUserTransaction(testUsername);

        assertNotNull(retrievedTransactions);
        assertEquals(2, retrievedTransactions.size());
        assertEquals(30.0, retrievedTransactions.get(0).getAmount(),0.1);
        assertEquals("Dinner", retrievedTransactions.get(0).getDescription());
        assertEquals(200.0, retrievedTransactions.get(1).getAmount(),0.1);
        assertEquals("New Laptop", retrievedTransactions.get(1).getDescription());

        myDatabase.deleteUser(testUsername);
    }

    @Test
    void testGetUserTransactionForNonexistentUser() {
        assertNull(myDatabase.getUserTransaction("NonexistentUser"));
    }

    @Test
    void testGetUserTransactionForExistingUserWithoutTransactions() throws Exception {
        myDatabase.createUser(testUser);
        List<Transaction> transactions = myDatabase.getUserTransaction(testUsername);
        assertNotNull(transactions);
        assertTrue(transactions.isEmpty());
        myDatabase.deleteUser(testUsername);
    }

    @Test
    void testAddUserTransaction() throws Exception {
        myDatabase.createUser(testUser);

        Transaction transaction = new Transaction(50.0, "Amazon Purchase");
        myDatabase.addUserTransaction(testUsername, transaction);

        // Reload and check persistence
        Database reloadedDb = new Database();
        List<Transaction> transactions = reloadedDb.getUserTransaction(testUsername);

        assertNotNull(transactions);
        assertEquals(1, transactions.size());
        assertEquals(50.0, transactions.get(0).getAmount(), 0.1);
        assertEquals("Amazon Purchase", transactions.get(0).getDescription());

        // Clear transactions and user
        myDatabase.deleteUser(testUsername);
    }

    @Test
    void testAddMultipleUserTransactions() throws Exception {
        myDatabase.createUser(testUser);

        Transaction t1 = new Transaction(25.0, "Grocery Shopping");
        Transaction t2 = new Transaction(75.0, "Online Order");

        myDatabase.addUserTransaction(testUsername, t1);
        myDatabase.addUserTransaction(testUsername, t2);

        // Reload and check persistence
        Database reloadedDb = new Database();
        List<Transaction> transactions = reloadedDb.getUserTransaction(testUsername);

        assertNotNull(transactions);
        assertEquals(2, transactions.size());
        assertEquals(25.0, transactions.get(0).getAmount(), 0.1);
        assertEquals("Grocery Shopping", transactions.get(0).getDescription());
        assertEquals(75.0, transactions.get(1).getAmount(), 0.1);
        assertEquals("Online Order", transactions.get(1).getDescription());

        // Clear transactions and user
        myDatabase.deleteUser(testUsername);
    }
    
    
    @Test
    void testUserPersistence() throws Exception {
        myDatabase.createUser(testUser);

        // Reload from file
        myDatabase = new Database();
        User loaded = myDatabase.getUserData(testUsername);

        assertNotNull(loaded);
        assertEquals("password", loaded.getHashedPassword());
        assertEquals(testUsername, loaded.getUsername());
        myDatabase.deleteUser(testUsername);
    }

    @Test
    void testTransactionPersistence() throws Exception {
        myDatabase.createUser(testUser);

        Transaction t1 = new Transaction(100.0, "deposit");
        Transaction t2 = new Transaction(50.0, "withdraw");

        myDatabase.addUserTransaction(testUsername, t1);
        myDatabase.addUserTransaction(testUsername, t2);

        // Reload from file
        myDatabase = new Database();
        List<Transaction> txList = myDatabase.getUserTransaction(testUsername);

        assertNotNull(txList);
        assertEquals(2, txList.size());
        assertEquals(100.0, txList.get(0).getAmount(),0.1);
        assertEquals("deposit", txList.get(0).getDescription());
        myDatabase.deleteUser(testUsername);
    }

    @Test
    void testDeleteUserAndPersistence() throws Exception {
        myDatabase.createUser(testUser);

        myDatabase.addUserTransaction(testUsername, new Transaction(50.0,"deposit"));
        assertTrue(myDatabase.doesUserExist(testUsername));

        // Delete and reload
        assertDoesNotThrow(() -> myDatabase.deleteUser(testUsername));
        myDatabase = new Database();

        assertNull(myDatabase.getUserData(testUsername));
        assertNull(myDatabase.getUserTransaction(testUsername));

    }
    
    @Test
    void testRecallTransaction() throws Exception {
        myDatabase.createUser(testUser);

        Transaction t1 = new Transaction(15.0, "Subscription");
        Transaction t2 = new Transaction( 40.0, "Book Purchase");
        String ID1 = t1.getTransactionID();
        String ID2 = t2.getTransactionID();
        
        myDatabase.addUserTransaction(testUsername, t1);
        myDatabase.addUserTransaction(testUsername, t2);

        List<Transaction> beforeRecall = myDatabase.getUserTransaction(testUsername);
        assertEquals(2, beforeRecall.size());

        var recalled = myDatabase.recallTransaction(ID2);

        assertEquals(1, recalled.size());
        assertTrue(recalled.containsKey(testUser));
        assertEquals(ID2, recalled.get(testUser).getTransactionID());

        List<Transaction> afterRecall = myDatabase.getUserTransaction(testUsername);
        assertEquals(1, afterRecall.size());
        assertEquals(ID1, afterRecall.get(0).getTransactionID());

        myDatabase.deleteUser(testUsername);
    }

    @Test
    void testRecallNonexistentTransaction() throws Exception {
        Transaction invalidTransaction = new Transaction(5.0, "Nonmatch" );
        String invalid_ID = invalidTransaction.getTransactionID();

        HashMap<User, Transaction> recalled = myDatabase.recallTransaction(invalid_ID);

        assertTrue(recalled.isEmpty());

    }


}
