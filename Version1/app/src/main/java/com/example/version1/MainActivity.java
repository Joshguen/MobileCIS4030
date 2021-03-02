package com.example.version1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button testBut;
        testBut = (Button) findViewById(R.id.test_button);

        ReceiptItem testItem1 = new ReceiptItem("penis", 1000);
        ReceiptItem testItem2 = new ReceiptItem("dildo", 100);
        Receipt testReceipt1 = new Receipt();
        testReceipt1.addItem(testItem1);
        testReceipt1.addItem(testItem2);

        testBut.setOnClickListener(v -> {
            System.out.println(testReceipt1.getTotal());
        });
    }
}