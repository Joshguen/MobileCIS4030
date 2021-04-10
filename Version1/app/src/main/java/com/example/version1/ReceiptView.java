package com.example.version1;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;
import java.util.concurrent.BlockingDeque;

public class ReceiptView extends AppCompatActivity {

    final Context context = this;

    private static final int PERMISSION_CODE = 1000;
    private static final int IMAGE_CAPTURE_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AllUsers userList = AllUsers.getInstance();
        AllReceipts receiptList = AllReceipts.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.receipt_view);

        Button doneButton = findViewById(R.id.done_button);

        TextView babyGotBack = (TextView) findViewById(R.id.back_text);
        ListView listOfReceiptItems = (ListView) findViewById(R.id.receipt);
        final User[] currentUser = {new User("", -1)};

        //getting array list from MainActivity and creating the receiptItems
        ArrayList<String> lines = new ArrayList<String>(Arrays.asList(getIntent().getStringArrayExtra("lines")));
        Log.d("Testing", "Testing");
        System.out.println(lines);
        Receipt receipt = new Receipt();
        for (int i = 0; i < lines.size(); i++){
            //splitting on ":" to seperate items and prices
            String[] strSplit = lines.get(i).split(":",2);
            ReceiptItem item = new ReceiptItem(strSplit[0],Double.parseDouble(strSplit[1]));
            receipt.addItem(item);
        }


        //creating the user list and the drop down
        Spinner dropdown = findViewById(R.id.spinner1);
        ArrayList<String> temp = new ArrayList<String>();
        if(userList.userList.size() == 0){
            User toAdd = new User("Guest", 0);
            userList.userList.add(toAdd);
        }
        for (User user: userList.userList) {
            temp.add(user.name);
        }
        temp.add("Add User");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, temp);
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(parent.getItemAtPosition(position).toString().equals("Add User")) {
                    LayoutInflater li = LayoutInflater.from(context);
                    View promptsView = li.inflate(R.layout.prompts, null);

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            context);

                    // set prompts.xml to alertdialog builder
                    alertDialogBuilder.setView(promptsView);

                    final EditText userInput = (EditText) promptsView
                            .findViewById(R.id.editTextDialogUserInput);

                    // set dialog message
                    alertDialogBuilder
                            .setCancelable(false)
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,int id) {
                                            // get user input and add it to list
                                            User toAdd = new User(userInput.getText().toString(), userList.userList.size());
                                            userList.userList.add(toAdd);
                                            finish();
                                            startActivity(getIntent());
                                        }
                                    })
                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,int id) {
                                            dialog.cancel();
                                        }
                                    });

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();
                }
                else if (position >= 0) {
                    System.out.println(position);
                    currentUser[0] = userList.userList.get(position);
                }
            }
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        dropdown.setAdapter(adapter);

        //creating userReceipts
        Receipt userReceipt = new Receipt();
        double runningTot = 0;
        ArrayList<String> items = new ArrayList<String>();
        for (ReceiptItem item : receipt.items) {
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
                System.out.println("claimed item " + position + " as " + currentUser[0].name);
                if(listOfReceiptItems.isItemChecked(position)){
                    userReceipt.addItem(receipt.items.get(position));
                }else{
                    userReceipt.removeItem(receipt.items.get(position));
                }
                TextView runningTotal = findViewById(R.id.running_total);
                runningTotal.setText("Total: $" + String.valueOf(userReceipt.getTotal()));
            }
        });

        babyGotBack.setOnClickListener(v -> {
            finish();
        });

        doneButton.setOnClickListener(v -> {
            //TODO
            receiptList.receiptList.add(receipt);
            finish();
        });
    }
}