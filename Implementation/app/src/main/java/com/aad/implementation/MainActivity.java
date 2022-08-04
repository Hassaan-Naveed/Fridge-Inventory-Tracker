package com.aad.implementation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Create instance of activity and set view
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set toolbar title
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Login");
    }

    public void openStaffLogin(View view) {
        //Open staff login screen
        Intent intent = new Intent(this, StaffLoginActivity.class);
        startActivity(intent);
    }

    public void openDeliveryLogin(View view) {
        //Open delivery login screen
        Intent intent = new Intent(this, DeliveryLoginActivity.class);
        startActivity(intent);
    }

    public void createAccount(View view) {
        //Open create account screen
        Intent intent = new Intent(this, CreateAccountActivity.class);
        startActivity(intent);
    }
}