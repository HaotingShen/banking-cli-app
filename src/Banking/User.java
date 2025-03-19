package banking;
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import java.util.Objects;

class User {

    private String username;
    private String password;
    private double balance;
    User(String username, String password, double balance) {
        this.username = username;
        this.password = password;
        this.balance = balance;
    }
    public Object getHashedPassword() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getHashedPassword'");
    }

    public String getUsername() {
        return username;
    }

    public double getBalance() {
        return balance;
    }
    //to check if two users are the same
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return Double.compare(user.balance, balance) == 0 &&
               username.equals(user.username) &&
               password.equals(user.password);
    }

    //override user hashcode
    @Override
    public int hashCode() {
        return Objects.hash(username, password, balance);
    }
}
