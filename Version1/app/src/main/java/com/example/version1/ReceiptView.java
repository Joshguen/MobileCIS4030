package com.example.version1;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class ReceiptView extends AppCompatActivity {

    private static final int PERMISSION_CODE = 1000;
    private static final int IMAGE_CAPTURE_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button babyGotBack;
        babyGotBack = (Button) findViewById(R.id.test_button);

        ReceiptItem testItem1 = new ReceiptItem("penis", 2000);
        ReceiptItem testItem2 = new ReceiptItem("dildo", 200);
        Receipt testReceipt1 = new Receipt();
        testReceipt1.addItem(testItem1);
        testReceipt1.addItem(testItem2);

        babyGotBack.setOnClickListener(v -> {
            System.out.println(testReceipt1.getTotal());
            finish();
        });
    }
}