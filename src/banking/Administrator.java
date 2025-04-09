package banking;

public class Administrator extends User {

    private int authLevel;

    public Administrator(String username, String hashedPassword, double balance, int authLevel) {
        super(username, hashedPassword, balance);
        this.authLevel = authLevel;
    }
    
}
