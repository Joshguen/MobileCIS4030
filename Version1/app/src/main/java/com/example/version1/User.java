package com.example.version1;

import java.io.Serializable;

public class User implements Serializable {
    public String name;
    public int ID;
    public Receipt claimedItems = new Receipt();

    //Constructor
    public User(String name, int ID) {
        this.name = name;
        this.ID = ID;
    }

    //adds a copy of an item to the user's claimedItems
    public void claimItem(ReceiptItem item) {
        ReceiptItem toAdd = new ReceiptItem(item.name, item.price);
        claimedItems.addItem(toAdd);
        item.addClaim(this);
    }

    //adds an item to receipt of the user for easier management
    public void unclaimItem(ReceiptItem item) {
        claimedItems.removeItem(item);
        item.removeClaim(this);
    }
}
