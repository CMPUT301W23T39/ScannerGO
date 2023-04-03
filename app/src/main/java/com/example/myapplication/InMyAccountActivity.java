package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class InMyAccountActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.in_my_account);
        Button profileButton = findViewById(R.id.profile_button);
        Button QRListButton = findViewById(R.id.QRList_button);
        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InMyAccountActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InMyAccountActivity.this,ProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });

        QRListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InMyAccountActivity.this,QRListActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
