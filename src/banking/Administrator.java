package banking;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Administrator extends User {

    private int authLevel;

    public Administrator(String username, String hashedPassword, double balance, int authLevel) {
        super(username, hashedPassword, balance);
        this.authLevel = authLevel;
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
    
    
}
