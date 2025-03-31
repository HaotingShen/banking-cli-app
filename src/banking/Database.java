package banking;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.MessageDigest;
import banking.Transaction;


public class Database implements Serializable{
    private Map<String, User> mapToUser;
    private Map<String, List<Transaction>> mapToTransactions;
    private FileSystem fileSystem;
    
    public Database(){
        mapToUser = new HashMap<>();
        mapToTransactions = new HashMap<>();
        fileSystem = new FileSystem();
        mapToUser = fileSystem.loadUsersFromFile();
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

    //create user in the database and store in file
    public User createUser(User newUser){
        mapToUser.put(newUser.getUsername(), newUser);
        fileSystem.saveUsersToFile(mapToUser);
        return newUser;


    }
    
    //delete user from the database
    public void deleteUser(String username) throws Exception {
        if (mapToUser.containsKey(username)) {
            mapToUser.remove(username);
            fileSystem.saveUsersToFile(mapToUser);
        } else {
            throw new RuntimeException("Username: '" + username + "' not found in the database.");
        }
    }
    

    public void setUserTransaction(String username, List<Transaction> transactions) {
    	//initialize the transactions if not yet
    	if(mapToUser.containsKey(username)) mapToTransactions.put(username, transactions);
    	
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

    
}
