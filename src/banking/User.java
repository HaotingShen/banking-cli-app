package banking;
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import java.io.Serializable;
import java.util.*;

public class User implements Serializable {

    private String username;
    private String hashedPassword;
    private double balance;
    private String accountNumber;
    
    public User(String username, String hashedPassword, double balance) {
        this.username = username;
        this.hashedPassword = hashedPassword;
        this.balance = balance;
        this.accountNumber = UUID.randomUUID().toString();
    }
    
    public String getAccountNumber() {
		return accountNumber;
    }
    
    public Object getHashedPassword() {
        return hashedPassword;
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
               hashedPassword.equals(user.hashedPassword);
    }

    //override user hashcode
    @Override
    public int hashCode() {
        return Objects.hash(username, hashedPassword, balance);
    }
    
    //issue a charge
    public Transaction issueCharge(User target, double amount, String description) {
        if (this.accountNumber.equals(target.accountNumber)) {
            System.out.println("You cannot charge yourself.");
            return null;
        }
        if (amount <= 0) {
            System.out.println("Charge amount invalid.");
            return null;
        }
        if (target.balance < amount) {
            System.out.println("Target user has insufficient funds.");
            return null;
        }
        target.balance -= amount;
        this.balance += amount;
        System.out.println("Charge issued successfully. You received $" + String.format("%.2f", amount));
        return new Transaction(amount, "Charge received from " + target.username + " - " + description);
    }

    //create charge record for the target user
    public Transaction issueChargeRecord(double amount, String issuerUsername, String description) {
        return new Transaction(-amount, "Charged by " + issuerUsername + " - " + description);
    }    
    
    //deposit amount
    public Transaction deposit(double amount) {
    	if (amount > 0) {
            balance += amount;
            Transaction newTransaction = new Transaction(amount, "Deposit");
            System.out.println("Deposit successful. New balance: " + String.format("%.2f", balance));
            return newTransaction;
        } else {
            System.out.println("Deposit amount must be positive.");
        }
    	return null;
    }

    //withdraw amount
    public Transaction withdraw(double amount) {
    	if (amount > 0 && balance >= amount) {
            balance -= amount;
            Transaction newTransaction = new Transaction(-amount, "Withdraw");
            System.out.println("Withdrawal successful. New balance: " + String.format("%.2f", balance));
            return newTransaction;
        } else if (amount <= 0) {
            System.out.println("Withdrawal amount must be positive.");
        } else {
            System.out.println("Insufficient balance for withdrawal.");
        }
    	return null;
    }

    //Request statement
    public void printStatement(List<Transaction> transactions) {
        System.out.println("\n--- Account Statement ---");
        if(transactions != null) {
            for (Transaction t : transactions) {
                System.out.printf("[%s] %s: $%.2f\n", 
                    t.getDate(), t.getDescription(), t.getAmount());
            }
        }
        System.out.printf("Current Balance: $%.2f\n", balance);
    }
    
}
