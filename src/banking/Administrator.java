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
