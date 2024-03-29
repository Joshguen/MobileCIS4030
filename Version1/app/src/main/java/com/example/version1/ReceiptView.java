package com.example.version1;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.version1.AllReceipts;
import com.example.version1.AllUsers;
import com.example.version1.R;
import com.example.version1.Receipt;
import com.example.version1.ReceiptItem;
import com.example.version1.User;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ReceiptView extends AppCompatActivity {

    final Context context = this;
    DecimalFormat df = new DecimalFormat("#.00");

    private static final int PERMISSION_CODE = 1000;
    private static final int IMAGE_CAPTURE_CODE = 1001;
    public double runningTot = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AllUsers userList = AllUsers.getInstance();
        AllReceipts receiptList = AllReceipts.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.receipt_view);

        Button doneButton = findViewById(R.id.done_button);
        EditText nameText = findViewById(R.id.name_text);

        TextView babyGotBack = (TextView) findViewById(R.id.back_text);
        ListView listOfReceiptItems = (ListView) findViewById(R.id.receipt);
        final User[] currentUser = {new User("", -1)};
        Receipt receipt = (Receipt) getIntent().getSerializableExtra("receipt");
        if(!receipt.rName.equals("New Receipt")){
            nameText.setText(receipt.rName);
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
        //sets default user to guest
        currentUser[0] = userList.userList.get(0);

        //This is the user OnClick
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
                //on user change
                else {
                    currentUser[0] = userList.userList.get(position);
                    for (int i = 0; i < receipt.getLength(); i++) {
                        boolean checkFlag = false;
                        for (Integer user: receipt.items.get(i).claims) {
                            if (user == currentUser[0].ID) {
                                checkFlag = true;
                            }
                        }
                        if (checkFlag) {
                            listOfReceiptItems.setItemChecked(i, true);
                        }else {
                            listOfReceiptItems.setItemChecked(i, false);
                        }
                    }
                    runningTot = 0;
                    double netTotal = 0;
                    for (int i = 0; i < receipt.getLength(); i++) {
                        netTotal += receipt.items.get(i).price;
                        if(listOfReceiptItems.isItemChecked(i)){
                            if (receipt.items.get(i).claims.contains(currentUser[0].ID)) {
                                runningTot += receipt.items.get(i).price / (receipt.items.get(i).claims.size());
                            } else {
                                runningTot += receipt.items.get(i).price / (receipt.items.get(i).claims.size() + 1);
                            }
                        }
                        TextView runningTotal = findViewById(R.id.running_total);
                        runningTotal.setText("Net Total: $" + df.format(netTotal) + ". Total: $" + df.format(runningTot));
                    }
                }
            }
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        dropdown.setAdapter(adapter);
        if(userList.userList.size() != 0){
            dropdown.setSelection(userList.userList.size()-1);
        }

        ArrayList<String> items = new ArrayList<String>();
        for (ReceiptItem item : receipt.items) {
            String details = item.name + ":  $" + item.price;
            items.add(details);
        }
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_multiple_choice, items);
        listOfReceiptItems.setAdapter(arrayAdapter);
        listOfReceiptItems.setChoiceMode(listOfReceiptItems.CHOICE_MODE_MULTIPLE);
        runningTot = 0;
        double netTotal = 0;
        for (int i = 0; i < receipt.getLength(); i++) {
            netTotal += receipt.items.get(i).price;
            for (Integer user: receipt.items.get(i).claims) {
                if (user == currentUser[0].ID) {
                    listOfReceiptItems.setItemChecked(i, true);
                }
            }
            if(listOfReceiptItems.isItemChecked(i)){
                if (receipt.items.get(i).claims.contains(currentUser[0].ID)) {
                    runningTot += receipt.items.get(i).price / (receipt.items.get(i).claims.size());
                } else {
                    runningTot += receipt.items.get(i).price / (receipt.items.get(i).claims.size() + 1);
                }
            }
            TextView runningTotal = findViewById(R.id.running_total);
            runningTotal.setText("Net Total: $" + df.format(netTotal) + ". Total: $" + df.format(runningTot));

        }

        //THis is the receipt onclick
        listOfReceiptItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                runningTot = 0;
                double netTotal = 0;
                for (int i = 0; i < receipt.getLength(); i++) {
                    netTotal += receipt.items.get(i).price;
                    if(listOfReceiptItems.isItemChecked(i)){
                        if (receipt.items.get(i).claims.contains(currentUser[0].ID)) {
                            runningTot += receipt.items.get(i).price / (receipt.items.get(i).claims.size());
                        } else {
                            runningTot += receipt.items.get(i).price / (receipt.items.get(i).claims.size() + 1);
                        }
                    }
                    TextView runningTotal = findViewById(R.id.running_total);
                    runningTotal.setText("Net Total: $" + df.format(netTotal) + ". Total: $" + df.format(runningTot));
                }
            }
        });

        //Edit prices onclick
        final String[] toChange = {""};
        listOfReceiptItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.edit_text_prompts, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);

                // set edit_text_prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                final EditText userInput = (EditText) promptsView
                        .findViewById(R.id.editTextDialogUserInput2);

                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        toChange[0] = userInput.getText().toString();
                                        receipt.items.get(position).price = Double.valueOf(userInput.getText().toString());
                                        ArrayAdapter adapter = (ArrayAdapter ) listOfReceiptItems.getAdapter();
                                        String item = receipt.items.get(position).name + ":  $" + Double.valueOf(toChange[0]);
                                        items.set(position, item);
                                        adapter.notifyDataSetChanged();
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
                runningTot = 0;
                double netTotal = 0;
                for (int i = 0; i < receipt.getLength(); i++) {
                    netTotal += receipt.items.get(i).price;
                    if(listOfReceiptItems.isItemChecked(i)){
                        if (receipt.items.get(i).claims.contains(currentUser[0].ID)) {
                            runningTot += receipt.items.get(i).price / (receipt.items.get(i).claims.size());
                        } else {
                            runningTot += receipt.items.get(i).price / (receipt.items.get(i).claims.size() + 1);
                        }
                    }
                    TextView runningTotal = findViewById(R.id.running_total);
                    runningTotal.setText("Net Total: $" + df.format(netTotal) + ". Total: $" + df.format(runningTot));
                }
                return false;
            }
        });

        babyGotBack.setOnClickListener(v -> {
            finish();
        });

        doneButton.setOnClickListener(v -> {
            int flag = 0;

            String name = "New Receipt";
            name = nameText.getText().toString();
            System.out.println("Name: " +name);
            receipt.rName = name;

            for (int i = 0; i < receipt.getLength(); i++) {
                //checks if each item is checked and isn't already claimed by that user
                if(listOfReceiptItems.isItemChecked(i)) {
                    if (!receipt.items.get(i).claims.contains(currentUser[0].ID)) {
                        currentUser[0].claimItem(receipt.items.get(i));
                    }
                }else{
                    if(receipt.items.get(i).claims.size() > 0){
                        currentUser[0].unclaimItem(receipt.items.get(i));
                    }
                }

            }

//            currentUser[0].claimedItems.items.remove(0);
            //this only tells which receipt to use
            for (int i = 0; i < receiptList.receiptList.size(); i++) {
                if(receiptList.receiptList.get(i).ID == receipt.ID){
                    receiptList.receiptList.set(i,receipt);
                    flag = 1;
                }

            }

            if(flag == 0){
                receiptList.receiptList.add(receipt);
            }
            finish();
        });


    }
}