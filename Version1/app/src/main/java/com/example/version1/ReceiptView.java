package com.example.version1;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ReceiptView extends AppCompatActivity {

    private static final int PERMISSION_CODE = 1000;
    private static final int IMAGE_CAPTURE_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.receipt_view);

        TextView babyGotBack = (TextView) findViewById(R.id.back_text);
        ListView listOfReceiptItems = (ListView) findViewById(R.id.receipt);

        User testUser = new User("Test User", 1);
        ReceiptItem testItem1 = new ReceiptItem("penis", 2000);
        ReceiptItem testItem2 = new ReceiptItem("dildo", 200);
        Receipt testReceipt1 = new Receipt();
        testReceipt1.addItem(testItem1);
        testReceipt1.addItem(testItem2);

        ArrayList<String> items = new ArrayList<String>();
        for (ReceiptItem item : testReceipt1.items) {
            String details = item.name + ":  $" + item.price;
            items.add(details);
        }
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_multiple_choice, items);
        listOfReceiptItems.setAdapter(arrayAdapter);
        listOfReceiptItems.setChoiceMode(listOfReceiptItems.CHOICE_MODE_MULTIPLE);

        listOfReceiptItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String receiptItem = (String) parent.getItemAtPosition(position);
                System.out.println("you got; " + receiptItem);
                Toast.makeText(getApplicationContext(), "Clicked on Row: " + receiptItem, Toast.LENGTH_LONG).show();
            }
        });

        babyGotBack.setOnClickListener(v -> {
            System.out.println();
            Toast.makeText(getApplicationContext(), "Total: " + testReceipt1.getTotal(), Toast.LENGTH_LONG).show();
            finish();
        });
    }
}