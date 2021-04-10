package com.example.version1;

import java.util.ArrayList;

public class AllUsers {
    public ArrayList<User> userList = new ArrayList<User>();

    private static AllUsers instance;

    public static AllUsers getInstance() {
        if (instance == null)
            instance = new AllUsers();
        return instance;
    }
    private AllUsers() {}
}
