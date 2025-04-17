package banking;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public class Transaction implements Serializable {
    private Date date;
    private double amount;
    private String description;
    private String transactionID;
    

    public Transaction(double amount, String description) {
        this.date = new Date();
        this.amount = amount;
        this.description = description;
        this.transactionID = UUID.randomUUID().toString();
        
    }
    
    public Transaction(double amount, String description, String transactionID) {
        this.date = new Date();
        this.amount = amount;
        this.description = description;
        this.transactionID = transactionID;
        
    }

    public Date getDate() { 
        return date; 
    }
    public double getAmount() { 
        return amount; 
    }
    public String getDescription() { 
        return description; 
    }
    public String getTransactionID() {
    	return transactionID;
    }

}

