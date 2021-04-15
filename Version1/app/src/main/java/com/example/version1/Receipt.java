package com.example.version1;

import com.example.version1.ReceiptItem;

import java.io.Serializable;
import java.util.ArrayList;

public class Receipt implements Serializable {
    public ArrayList<ReceiptItem> items = new ArrayList<ReceiptItem>();
    public int ID;
    public String rName;

    public void addItem(ReceiptItem item) {
        items.add(item);
    }

    public void removeItem(ReceiptItem item) {
        items.remove(item);
    }

    public int getLength() {
        return (items.size());
    }

    public Receipt(int ID){
        this.ID = ID;
        this.rName = "New Receipt";
    }

}
