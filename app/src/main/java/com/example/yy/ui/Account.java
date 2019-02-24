package com.example.yy.ui;

public class Account {
    private int accountNum;
    private String userName;
    private String password;

    public Account(int accountNum,String userName,String password){
        this.accountNum = accountNum;
        this.userName = userName;
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public int getAccountNum() {
        return accountNum;
    }

    public String getPassword() {
        return password;
    }
}
