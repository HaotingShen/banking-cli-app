package banking;
import java.util.HashMap;
import java.util.Map;

public class Database {
    Map<String, User> mapToUser;
    public Database(){
        mapToUser = new HashMap<>();

    }
    public boolean doesUserExist(String username){
        return mapToUser.containsKey(username);
    }

    public User getUserData(String username){
        if(mapToUser.containsKey(username))return mapToUser.get(username);
        return null;
    }

    public User createUser(String username, String password, double balance){
        User newUser = new User(username, password, balance);
        mapToUser.put(username, newUser);
        return newUser;


    }
    
    public void deleteUser(String username) throws Exception {
        if (mapToUser.containsKey(username)) {
            mapToUser.remove(username);
        } else {
            throw new Exception("Username: '" + username + "' not found in the database.");
        }
    }
    

    
}
