package com.aad.implementation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class ChangePinActivity extends AppCompatActivity {

    private EditText newPIN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Create instance of activity and set view
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pin);

        //Set toolbar title
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Change PIN");

        //Get GUI elements
        newPIN = findViewById(R.id.newPinTextField);
    }

    public void pinChange(View view) {
        //Get database reference
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://fridge-app-9c3d5-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference db = database.getReference();

        //Get user inputs
        String pin = newPIN.getText().toString();

        //Set pin to new value and display success message
        db.child("Delivery").child("Pin").setValue(pin);

        Toast toast = Toast.makeText(this, "PIN set!", Toast.LENGTH_SHORT);
        toast.show();
    }
}