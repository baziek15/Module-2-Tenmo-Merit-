package com.techelevator.tenmo.model;



import java.math.BigDecimal;

public class Account  {

    private int accountId;
    private int userId;
    private BigDecimal balance;

public Account(int accountId, int userId, BigDecimal balance) {
    this.accountId = accountId;
    this.userId = userId;
    this.balance = balance;
}

    public int getAccountId() {
        return accountId;
    }
    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }
    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
    public BigDecimal getBalance() {
        return balance;
    }
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

public void send (BigDecimal amount){

    if (amount == null) {
        System.out.println("Enter a valid Amount ");
        return;
    }
    if (balance.compareTo(amount) < 0) {
        System.out.println("You don't have enough funds");
        return;
    }
    balance = new BigDecimal(String.valueOf(balance)).subtract(amount);
}

    public void receive (BigDecimal amount){
        if (amount == null) {
            System.out.println("Amount cannot be null");
            return;
        }
        balance =new BigDecimal(String.valueOf(balance)).add(amount);
    }
}
