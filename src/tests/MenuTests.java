package tests;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Banking.Menu;
import Banking.User;

public class MenuTests {
    
    @BeforeEach
    void setup() {
        this.menu = new Menu()
    }

    @Test
    void test_authentication() {
        User testUser = new User("Test","password",0);
        this.menu.datahandler.create_user(testUser);
        assertTrue(this.menu.authenticate_user_pass("Test","password"));
    }
}
