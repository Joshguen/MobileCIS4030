package com.example.version1;

import java.util.ArrayList;

public class AllReceipts {
    public ArrayList<Receipt> receiptList = new ArrayList<Receipt>();

    private static AllReceipts instance;


    public static AllReceipts getInstance() {
        if (instance == null)
            instance = new AllReceipts();
        return instance;
    }
    private AllReceipts() {}
}
