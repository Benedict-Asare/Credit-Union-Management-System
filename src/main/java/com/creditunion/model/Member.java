package com.creditunion.model;

public class Member {

    private int memberId;
    private String name;
    private double balance;

    public Member(int memberId, String name, double balance) {
        this.memberId = memberId;
        this.name = name;
        this.balance = balance;
    }

    public int getMemberId() {
        return memberId;
    }

    public String getName() {
        return name;
    }

    public double getBalance() {
        return balance;
    }

    public void deposit(double amount) {
        balance += amount;
    }
}