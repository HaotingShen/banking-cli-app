package banking;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.security.MessageDigest;

public class Database {
    private Map<String, User> mapToUser;
    private Map<String, List<Transaction>> mapToTransactions;
    
    public Database(){
        mapToUser = new HashMap<>();
        mapToTransactions = new HashMap<>();

    }
    
    //check if this user already exists in our database
    public boolean doesUserExist(String username){
        return mapToUser.containsKey(username);
    }

    //get user data (User object) from the database
    public User getUserData(String username){
        if(mapToUser.containsKey(username))return mapToUser.get(username);
        return null;
    }

    //create user in the database
    public User createUser(User newUser){
        mapToUser.put(newUser.getUsername(), newUser);
        return newUser;


    }
    
    //delete user from the database
    public void deleteUser(String username) throws Exception {
        if (mapToUser.containsKey(username)) {
            mapToUser.remove(username);
        } else {
            throw new RuntimeException("Username: '" + username + "' not found in the database.");
        }
    }
    
    public List<Transaction> getUserTransaction(String username){
    	//if user exists but no transaction has been created so far, create it here
    	if(mapToUser.containsKey(username)) {
    		mapToTransactions.putIfAbsent(username, new ArrayList<>());
    		return mapToTransactions.get(username);
    	}
        return null;//only when the user does not exists, we return null
    }
    
    public void addUserTransaction(String username, Transaction transaction) {
    	//initialize the transactions if not yet
    	mapToTransactions.putIfAbsent(username, new ArrayList<>());
    	List<Transaction> transactionHistory = mapToTransactions.get(username);
    	transactionHistory.add(transaction);
    }
    
    public void setUserTransaction(String username, List<Transaction> transactions) {
    	//initialize the transactions if not yet
    	if(mapToUser.containsKey(username)) mapToTransactions.put(username, transactions);
    	
    }

    
}
