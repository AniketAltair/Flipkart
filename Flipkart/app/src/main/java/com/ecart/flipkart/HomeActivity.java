package com.ecart.flipkart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toast.makeText(HomeActivity.this,"Please write Your PhoneNumber", Toast.LENGTH_LONG).show();

        Intent intent = new Intent(HomeActivity.this, NewHomeActivity.class);
        startActivity(intent);


    }
}