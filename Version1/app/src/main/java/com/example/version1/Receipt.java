package com.example.version1;

import com.example.version1.ReceiptItem;

import java.util.ArrayList;

public class Receipt {
    public ArrayList<ReceiptItem> items = new ArrayList<ReceiptItem>();

    public void addItem(ReceiptItem item) {
        items.add(item);
    }

    public void removeItem(ReceiptItem item) {
        items.remove(item);
    }

    public int getLength() {
        return (items.size());
    }

    public ReceiptItem searchItem(String name, double price) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).name.equals(name) && items.get(i).price == price) {
                return items.get(i);
            }
        }
        return null;
    }

    /**
     * Returns the total of all of the items on the receipt
     */
    public double getTotal() {
        double total = 0;
        for (int i = 0; i < items.size(); i++) {
            total += ( items.get(i).price);
        }
        return total;
    }

    /**
     * Returns the tax of the receipt
     * @param taxPercent A double depicting the amount, where 10 percent would simply be input as: 10
     */
    public double getTax(double taxPercent) {
        double tax = getTotal() * taxPercent * 0.01;
        return tax;
    }
}
