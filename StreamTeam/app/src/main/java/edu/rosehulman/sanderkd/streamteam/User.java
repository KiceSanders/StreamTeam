package edu.rosehulman.sanderkd.streamteam;

import java.util.ArrayList;

/**
 * Created by sanderkd on 2/16/2016.
 */
public class User {
    public String username;
    public int friends;
    public ArrayList<Account> type;

    public User(String uname, int f){
        this.username = uname;
        this.friends = f;
        this.type = new ArrayList<>();
    }

    public String getUsername(){
        return this.username;
    }

    public int getFriends(){
        return friends;
    }

    public ArrayList<Account> getAccounts(){
        return type;
    }

    public void addAccount(Account account){
        type.add(account);
    }
}
