package com.example.assignment3;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class DataActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);

        String res = getIntent().getStringExtra("Data");
        TextView dataTextView = findViewById(R.id.data_textview);
        dataTextView.setText(res);
    }
}
