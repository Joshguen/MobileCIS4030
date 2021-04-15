 package com.example.version1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.*;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

import static java.sql.DriverManager.println;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_CODE = 1000;
    private static final int IMAGE_CAPTURE_CODE = 1001;

    final Context context = this;

    AllReceipts receiptList = AllReceipts.getInstance();
    AllUsers userList = AllUsers.getInstance();

    String allText = "";

    Button mCaptureBtn;
    TextView mImageView1;
    TextView mImageView2;
    TextView mImageView3;
    TextView mImageView4;
    TextView mImageView5;
    TextView mImageView6;


    Uri image_uri;
    TextView parsedText;

    public void doThing(){
        mImageView1 = findViewById(R.id.image_view1);
        mImageView2 = findViewById(R.id.image_view2);
        mImageView3 = findViewById(R.id.image_view3);
        mImageView4 = findViewById(R.id.image_view4);
        mImageView5 = findViewById(R.id.image_view5);
        mImageView6 = findViewById(R.id.image_view6);

        mCaptureBtn = findViewById(R.id.capture_image_btn);

        //testParse();

        //com.example.version1.Receipt testReceipt1 = new com.example.version1.Receipt();

        mCaptureBtn.setOnClickListener(v -> {
            //check permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED ||
                            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                        String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        requestPermissions(permission, PERMISSION_CODE);
                    } else {
                        //permissions granted
                        openCamera();
                    }
                }
            }
            else {
                //older than marshmallow
                openCamera();
            }
        });
    }

    private void openPreviousReceipt(int receiptVal) {
        System.out.println("Sent ID: " + receiptList.receiptList.get(receiptVal).ID);
        if (receiptVal <= receiptList.receiptList.size()) {

            Receipt currentReceipt = receiptList.receiptList.get(receiptVal);
            Intent intent = new Intent(MainActivity.this, ReceiptView.class);
            intent.putExtra("receipt", currentReceipt);
            startActivity(intent);
        }

    }

    @Override
    protected void onRestart(){
        super.onRestart();
        setContentView(R.layout.activity_main);
        doThing();
        int len = receiptList.receiptList.size();
        for(int i = 0; i < len; i++){
            switch(i) {
                case 0:
                    mImageView1.setVisibility(View.VISIBLE);
                    mImageView1.setText(receiptList.receiptList.get(i).rName);
                    break;
                case 1:
                    mImageView2.setVisibility(View.VISIBLE);
                    mImageView2.setText(receiptList.receiptList.get(i).rName);
                    break;
                case 2:
                    mImageView3.setVisibility(View.VISIBLE);
                    mImageView3.setText(receiptList.receiptList.get(i).rName);
                    break;
                case 3:
                    mImageView4.setVisibility(View.VISIBLE);
                    mImageView4.setText(receiptList.receiptList.get(i).rName);
                    break;
                case 4:
                    mImageView5.setVisibility(View.VISIBLE);
                    mImageView5.setText(receiptList.receiptList.get(i).rName);
                    break;
                case 5:
                    mImageView6.setVisibility(View.VISIBLE);
                    mImageView6.setText(receiptList.receiptList.get(i).rName);
                    break;
            }
        }
        mImageView1.setOnClickListener(v -> {
            openPreviousReceipt(0);
        });
        mImageView2.setOnClickListener(v -> {
            openPreviousReceipt(1);
        });
        mImageView3.setOnClickListener(v -> {
            openPreviousReceipt(2);
        });
        mImageView4.setOnClickListener(v -> {
            openPreviousReceipt(3);
        });
        mImageView5.setOnClickListener(v -> {
            openPreviousReceipt(4);
        });
        mImageView6.setOnClickListener(v -> {
            openPreviousReceipt(5);
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        doThing();
    }

    private void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera");
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        //Camera intent
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE);
    }

    //handling permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //this method is called, when user presses Allow or Deny from Permission Request Popup
        switch (requestCode){
            case PERMISSION_CODE:{
                if (grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED){
                    //permission from popup was granted
                    openCamera();
                }
                else {
                    //permission from popup was denied
                    Toast.makeText(this, "Permission denied...", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //called when image was captured from camera
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            //set the image captured to our ImageView
//            mImageView1.setImageURI(image_uri);

//            Bundle bundle = data.getExtras();
//            //from bundle, extract the image
//            Bitmap bitmap = (Bitmap) bundle.get("data");

            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), image_uri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            //Parsing using Firebase
            //Make a FirebaseVisionTextRecognizer from FirebaseVision, from FirebaseVisionImage, from Bitmap, from Bundle
            FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(bitmap);
            FirebaseVision firebaseVision = FirebaseVision.getInstance();
            FirebaseVisionTextRecognizer firebaseVisionTextRecognizer = firebaseVision.getCloudTextRecognizer();

            //Making task to process
            Task<FirebaseVisionText> task = firebaseVisionTextRecognizer.processImage(firebaseVisionImage);

            task.addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                @Override
                public void onSuccess(FirebaseVisionText firebaseVisionText) {
                    String s = firebaseVisionText.getText();
                    allText += s;
                    //allText += "0391230safds PENIL PUMP Iguana Iguana DON BUONsecks BILLY'S DOCTOR HMRJb 10.01\nTHIS SHOULD WORK 4.20\n123123123123123LOOLZ MRJ506.69\n";
                    //writeToFile(s, getApplicationContext());

                    //??You done fucker code here
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    openCamera();
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    //splitting string to new lines
                                    allText = receiptItem(allText);
                                    String lines[] = allText.split("\\n");
                                    int len = receiptList.receiptList.size()+1;
                                    Receipt receipt = new Receipt(len);
                                    for (int i = 0; i < lines.length; i++){
                                        //splitting on ":" to seperate items and prices
                                        String[] strSplit = lines[i].split(":",2);
                                        ReceiptItem item = new ReceiptItem(strSplit[0],Double.parseDouble(strSplit[1]));
                                        receipt.addItem(item);
                                    }

                                    Intent intent = new Intent(MainActivity.this, ReceiptView.class);
                                    intent.putExtra("receipt", receipt);
                                    //startActivityForResult(intent, 1);
                                    startActivity(intent);
                                    //parsedText.setText(s);
                                    allText = "";

                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Is there more to the receipt?").setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();


                }
            });

            task.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

        }
    }

    //TODO send in array list
    private String receiptItem(String str){

        //converting string to string array
        String lines[] = str.split("\\n");

        Stack itemStack = new Stack();
        Stack priceStack = new Stack();
        Stack otherStack = new Stack();

        Queue<String> pQ = new LinkedList<>();
        Queue<String> iQ = new LinkedList<>();

        int countPrice = 0;
        int countItem = 0;

        //Stack completeStack = new Stack();
        String rtn = "";
        String temp = "";

        for(String s: lines){
            String item = "";
            String price = "";

            item = extractText(s);
            price = extractPrice(s);

            if(item != "" && price != ""){
                //completeStack.push(item+":"+price);
                rtn = rtn + item +":"+price+"\n";
            }
            else if(item == "" && price == ""){
                otherStack.push(s);
            }else{
                if(item != ""){
                    iQ.add(item);
                    if(pQ.size() > 0){
                        temp = iQ.remove();
                        rtn = rtn + temp + ":";
                        temp = pQ.remove();
                        rtn = rtn + temp + "\n";
                    }
                    //store in item stack
                }else if(price != "." && price != ""){
                    pQ.add(price);
                    if(iQ.size() > 0){
                        temp = iQ.remove();
                        rtn = rtn + temp + ":";
                        temp = pQ.remove();
                        rtn = rtn + temp + "\n";
                    }
                    //store in price stack
                }
            }
        }

        return rtn;
    }

    private String extractText(String s){
        String rtn = "";
        //splitting the string to extra all caps characters
        String[] substrings = s.split("[^A-Z]+");
        for (String str : substrings) {
            if(!str.isEmpty()){
                if(garbageCheck(str)){
                    //appending to create new string
                    rtn = rtn+str;
                }
            }
        }
        return rtn;
    }

    private boolean garbageCheck(String s){
        if(s.equals("HMRJ") || s.contains("ZEHRS") || s.equals("MRJ") || s.equals("TM") || s.equals("RQ") || s.equals("HRJ")){
            return false;
        }
        return true;
    }

    private String extractPrice(String s){
        String rtn = "";
        //replace all non "." and numeric characters with space
        s = s.replaceAll("[^\\d.]", " ");
        //parse the new string on space
        String[] substrings = s.split(" ");
        for(String str : substrings){
            if(str.contains(".")){
                try{
                    Double.parseDouble(str);
                    //the one containing the "." is the price
                    rtn = str;
                }catch(NumberFormatException e){
                    //not a double
                }
            }
        }
        return rtn;
    }

    private void writeToFile(String data,Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("rawData.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

}
