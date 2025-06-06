package banking;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Administrator extends User {

    public Administrator(User linkedUser) {
        super(linkedUser.getUsername(), linkedUser.getHashedPassword(), linkedUser.getBalance());
    }
    
    public void recallTransactions(HashMap<User, Transaction> userTransactions) {
    	for (Map.Entry<User, Transaction> entry: userTransactions.entrySet()) {
    		User user = entry.getKey();
    		Transaction t = entry.getValue();
    		user.recallTransaction(t);
    		System.out.printf("Transaction: [%s] %s: $%.2f [ID: %s] Recalled Successfully!\n", 
                    t.getDate(), t.getDescription(), t.getAmount(), t.getTransactionID());
    		
    	}
    }
    
    public void printAllTransactions(List<Transaction> transactions) {
        System.out.println("\n--- All Transactions ---");
        if(transactions != null) {
            for (Transaction t : transactions) {
                System.out.printf("[%s] %s: $%.2f [ID: %s] \n", 
                    t.getDate(), t.getDescription(), t.getAmount(), t.getTransactionID());
            }
        }
    }
    
    public void showAllLoansWithStatus(Map<String, List<Transaction>> allTransactions) {
        System.out.println("\n--- Pending Loans ---");
        for (Map.Entry<String, List<Transaction>> entry : allTransactions.entrySet()) {
            String username = entry.getKey();
            for (Transaction t : entry.getValue()) {
                if (t instanceof Loan loan && !loan.isApproved()) {
                    System.out.printf("(%s): %s: $%.2f [ID: %s] [Approved=%b, Paid=%.2f/%.2f]\n",
                        username, loan.getDescription(), loan.getAmount(), loan.getTransactionID(), 
                        loan.isApproved(), loan.getAmountPaid(), loan.getAmount());
                }
            }
        }
    
        System.out.println("\n--- Approved Loans ---");
        for (Map.Entry<String, List<Transaction>> entry : allTransactions.entrySet()) {
            String username = entry.getKey();
            for (Transaction t : entry.getValue()) {
                if (t instanceof Loan loan && loan.isApproved()) {
                    System.out.printf("(%s): %s: $%.2f [ID: %s] [Approved=%b, Paid=%.2f/%.2f]\n",
                        username, loan.getDescription(), loan.getAmount(), loan.getTransactionID(), 
                        loan.isApproved(), loan.getAmountPaid(), loan.getAmount());
                }
            }
        }
    }
    
    public boolean approveLoanById(String transactionID, Map<String, List<Transaction>> allTransactions, Database db) {
        for (Map.Entry<String, List<Transaction>> entry : allTransactions.entrySet()) {
            String username = entry.getKey();
            List<Transaction> txList = entry.getValue();
            for (Transaction t : txList) {
                if (t instanceof Loan loan && t.getTransactionID().equals(transactionID)) {
                    if (loan.isApproved()) {
                        System.out.println("This loan has already been approved.");
                        return false;
                    }
                    loan.approve();
                    User loanUser = db.getUserData(username);
                    if (loanUser != null) {
                        loanUser.depositSilently(loan.getAmount());
                        System.out.println("Loan approved successfully.");
                    }
                    return true;
                }
            }
        }
        System.out.println("No matching loan found for the given ID.");
        return false;
    }
    
    public void freezeUserAccount(User user) {
        user.freezeAccount();
        System.out.println("Account for user '" + user.getUsername() + "' has been frozen.");
    }

    public void unfreezeUserAccount(User user, String password) {
        user.unfreezeAccount(password);
    }

    
}