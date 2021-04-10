package com.example.version1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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
import java.util.Stack;

import static java.sql.DriverManager.println;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_CODE = 1000;
    private static final int IMAGE_CAPTURE_CODE = 1001;

    Button mCaptureBtn;
    ImageView mImageView;

    Uri image_uri;
    TextView parsedText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageView = findViewById(R.id.image_view);
        mCaptureBtn = findViewById(R.id.capture_image_btn);

        //testParse();

        com.example.version1.Receipt testReceipt1 = new com.example.version1.Receipt();


        mCaptureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });

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

    private void openReceipt() {
        Intent switchActivityIntent = new Intent(this, ReceiptView.class);
        startActivity(switchActivityIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //called when image was captured from camera
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            //set the image captured to our ImageView
            mImageView.setImageURI(image_uri);

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
                    //String s = firebaseVisionText.getText();

                    //writeToFile(s, getApplicationContext());

                    String s = "0391230safds PENIL PUMP Iguana Iguana DON BUONsecks BILLY'S DOCTOR HMRJ10.01\nTHIS SHOULD WORK 4.20\n123123123123123LOOLZ MRJ506.69\n";
                    //splitting string to new lines
                    s = receiptItem(s);
                    String lines[] = s.split("\\n");
                    Log.d("here","here");

                    Intent intent = new Intent(MainActivity.this, ReceiptView.class);
                    intent.putExtra("lines",lines);
                    startActivity(intent);
                    //parsedText.setText(s);
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

    private void testParse(){
        ArrayList<String> s = new ArrayList<String>();
        s.add("0391230safds PENIL PUMP Iguana Iguana DON BUONsecks BILLY'S DOCTOR HMRJ10.01");
        s.add("THIS SHOULD WORK 4.20");
        s.add("123123123123123LOOLZ MRJ506.69");

        //receiptItem(s);
    }

    //TODO send in array list
    private String receiptItem(String str){

        String lines[] = str.split("\\n");

        //converting to arraylist
        /*ArrayList<String> s = new ArrayList<String>(
                Arrays.asList(str));*/


        Stack itemStack = new Stack();
        Stack priceStack = new Stack();
        Stack otherStack = new Stack();

        Stack completeStack = new Stack();

        for(String s: lines){
            String item = "";
            String price = "";

            //TODO run for each loop putting array items into proper stack
            item = extractText(s);
            price = extractPrice(s);

            if(item != "" && price != ""){
                //TODO create receipt item
                completeStack.push(item+":"+price);
            }
            else if(item == "" && price == ""){
                otherStack.push(s);
            }else{
                if(item != ""){
                    itemStack.push(item);
                    //store in item stack
                }else if(price != ""){
                    priceStack.push(price);
                    //store in price stack
                }
            }
        }
        Log.d("newStack",completeStack + "\n" + itemStack + "\n" + priceStack + "\n" +otherStack + "\n" );

        String rtn = "";
        for(int i = 0; i < completeStack.size();i++){
            rtn =  rtn + completeStack.get(i).toString() + "\n";
        }
        System.out.println(rtn);
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
        if(s.equals("HMRJ") || s.contains("ZEHRS") || s.equals("MRJ") || s.equals("TM") || s.equals("RQ")){
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
        int count = 0;
        for(String str : substrings){
            if(substrings[count].contains(".")){
                //the one containing the "." is the price
                rtn = substrings[count];
            }
            count++;
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
