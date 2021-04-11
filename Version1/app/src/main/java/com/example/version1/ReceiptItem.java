package com.example.version1;

import java.io.Serializable;
import java.util.ArrayList;

public class ReceiptItem implements Serializable {
    public String name;
    public double price;
    public ArrayList<Integer> claims = new ArrayList<Integer>();

    //Constructor
    public ReceiptItem(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public void addClaim(User user) {
        claims.add(user.ID);
    }

    public void removeClaim(User user) {
        claims.remove(user.ID);
    }
}
