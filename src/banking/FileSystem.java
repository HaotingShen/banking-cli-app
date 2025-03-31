package banking;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

public class FileSystem {

    private File userFile;
    private File transactionFile;
    
    public FileSystem(){
    	File userfile = new File("userMap");
    	File transactionFile = new File("transactionMap");
    }
    
  //TODO: make user serializable
	//consider having a separate class for reading and loading
    public void saveUsersToFile(Map<String, User> userMap) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(userFile))) {
            oos.writeObject(userMap);
            System.out.println("User Data saved successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // Load the database from a file
    public Map<String, User> loadUsersFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(userFile))) {
        	System.out.println("User Data loaded successfully.");
            return (Map<String, User>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new HashMap<>(); // Return a new instance if loading fails
        }
    }
    
}
