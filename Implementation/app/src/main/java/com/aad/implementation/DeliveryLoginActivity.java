package com.aad.implementation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class DeliveryLoginActivity extends AppCompatActivity {

    private EditText pinField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Create instance of activity and set view
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_login);

        //Set toolbar title
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Delivery Login");

        //Locate GUI input elements
        pinField = findViewById(R.id.pinTextField);

        //Get reference to database and ensure fridge door status is closed
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://fridge-app-9c3d5-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference db = database.getReference();

        db.child("Delivery").child("FridgeStatus").setValue("closed");
    }

    private void openDeliveryScreen() {
        //Open delivery screen
        Intent intent = new Intent(this, DeliveryScreenActivity.class);
        startActivity(intent);
    }

    public void login(View view) {
        //Get reference to database
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://fridge-app-9c3d5-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference db = database.getReference();

        //Get text from GUI input elements
        String inputPin = pinField.getText().toString();

        //Create listener to query data from database
        db.child("Delivery").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                //Display error if database is inaccessible
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    //Get snapshot of database
                    DataSnapshot dataSnapshot = task.getResult();
                    assert dataSnapshot != null;

                    //Get input from GUI input field
                    String pin = Objects.requireNonNull(dataSnapshot.child("Pin").getValue()).toString();

                    //Check that inputted pin is correct
                    if (inputPin.equals(pin)) {
                        openDeliveryScreen();
                    }
                }
            }
        });
    }
}