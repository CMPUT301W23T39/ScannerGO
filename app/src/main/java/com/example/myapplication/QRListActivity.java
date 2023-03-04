package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class QRListActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_list);
        ListView qrList = findViewById(R.id.qr_list);
        ArrayList<String> datalist;
        datalist = new ArrayList<>();
        datalist.add("Item 1");
        datalist.add("Item 2");
        datalist.add("Item 3");
        Button backButton = findViewById(R.id.back_button4);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, datalist);
        qrList.setAdapter(adapter);
        qrList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(QRListActivity.this, MyQRActivity.class);
                String QRCode = (String) parent.getItemAtPosition(position);
                intent.putExtra("QRCode",QRCode);
                startActivity(intent);
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QRListActivity.this, InMyAccountActivity.class);
                startActivity(intent);
                finish();
            }
        });
        Intent intent = getIntent();
        String deleteItem = intent.getStringExtra("DeleteCode");
        int pos = adapter.getPosition(deleteItem);
        adapter.remove(deleteItem);
        adapter.notifyDataSetChanged();
    }
}
