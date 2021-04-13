package com.example.version1;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
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

import java.util.ArrayList;

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
        //ArrayList<String> lines = new ArrayList<String>(Arrays.asList(getIntent().getStringArrayExtra("lines")));
        //Log.d("Testing", "Testing");
        //System.out.println(lines);

        //Intent i = getIntent();
        Receipt receipt = (Receipt) getIntent().getSerializableExtra("receipt");
        /*for(int i = 0; i < receipt.getLength(); i++){
            System.out.print(receipt.items.get(i).name);
            //System.out.println(receipt.items.get(i).claims.get(0).name);
        }*/

        /*for (int i = 0; i < lines.size(); i++){
            //splitting on ":" to seperate items and prices
            String[] strSplit = lines.get(i).split(":",2);
            ReceiptItem item = new ReceiptItem(strSplit[0],Double.parseDouble(strSplit[1]));
            receipt.addItem(item);
        }*/


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

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("Changed");
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
                    currentUser[0] = userList.userList.get(position);
                    System.out.println(currentUser[0].ID);
                    for (int i = 0; i < receipt.getLength(); i++) {
                        boolean checkFlag = false;
                        for (Integer user: receipt.items.get(i).claims) {
                            System.out.println(user);
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

                }
            }
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        dropdown.setAdapter(adapter);

        //creating userReceipts
        Receipt userReceipt = new Receipt(-1);
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
        for (int i = 0; i < receipt.getLength(); i++) {

            for (Integer user: receipt.items.get(i).claims) {
                if (user == currentUser[0].ID) {
                    listOfReceiptItems.setItemChecked(i, true);
                }
            }
        }

        listOfReceiptItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (int i = 0; i < receipt.getLength(); i++) {
                    if (listOfReceiptItems.isItemChecked(i)) {
                        userReceipt.addItem(receipt.items.get(i));
                    } else {
                        userReceipt.removeItem(receipt.items.get(i));
                    }
                }
                TextView runningTotal = findViewById(R.id.running_total);
                runningTotal.setText("Total: $" + String.valueOf(userReceipt.getTotal()));
            }
        });
        final int[] changeFlag = {-1};
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
                                        arrayAdapter.notifyDataSetChanged();
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
                return false;
            }
        });

        babyGotBack.setOnClickListener(v -> {
            finish();
        });

        doneButton.setOnClickListener(v -> {
            //TODO
            int flag = 0;
            
            for (int i = 0; i < receipt.getLength(); i++) {
                if(listOfReceiptItems.isItemChecked(i)){
                    currentUser[0].claimItem(receipt.items.get(i));
                }else{
                    if(receipt.items.get(i).claims.size() > 0){
                        currentUser[0].unclaimItem(receipt.items.get(i));

                        System.out.println("unclaiming " + receipt.items.get(i).claims.size());
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
                System.out.println(receiptList.receiptList.get(i).ID);

            }
            System.out.println(receipt.ID);

            if(flag == 0){
                receiptList.receiptList.add(receipt);
            }
            finish();
        });
    }
}