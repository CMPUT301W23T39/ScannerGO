package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MyQRActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myqr);
        TextView QRCodeName = findViewById(R.id.qrcode_name);
        Button backButton = findViewById(R.id.back_button2);
        Button deleteButton = findViewById(R.id.delete_button);
        Intent intent = getIntent();
        String QRCode = intent.getStringExtra("QRCode");
        QRCodeName.setText("Name: "+QRCode);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyQRActivity.this, QRListActivity.class);
                startActivity(intent);
                finish();
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyQRActivity.this,QRListActivity.class);
                intent.putExtra("DeleteCode",QRCode);
                DB.delQRCodeInDB(QRCode);
                startActivity(intent);
                finish();
            }
        });
    }
}
