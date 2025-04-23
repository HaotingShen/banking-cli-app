package tests;

import banking.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class LoanTests {

    private Database db;
    private User user;

    @BeforeEach
    void setUp() {
        db = new Database();
        user = new User("loanUser", Authenticator.hashPassword("pass123"), 0.0);
        db.createUser(user);
    }

    @Test
    void testLoanRequestIsRecorded() throws Exception {
        Loan loan = user.requestLoan(200.0, "New Laptop");
        assertNotNull(loan);
        assertFalse(loan.isApproved());
        db.addUserTransaction(user.getUsername(), loan);

        List<Transaction> tx = db.getUserTransaction(user.getUsername());
        assertEquals(1, tx.size());
        assertTrue(tx.get(0) instanceof Loan);
        assertEquals(200.0, tx.get(0).getAmount());
        db.deleteUser(user.getUsername());
    }

}
