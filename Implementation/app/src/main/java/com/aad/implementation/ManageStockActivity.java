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

public class ManageStockActivity extends AppCompatActivity {

    private EditText itemNameText;
    private EditText thresholdText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Create instance of activity and set view
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_stock);

        //Set toolbar title
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Manage Stock");

        //Get GUI elements
        itemNameText = findViewById(R.id.stockItemNameTextField);
        thresholdText = findViewById(R.id.stockThresholdTextField);
    }

    private void showMessage(String message) {
        //Show message at bottom of screen
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    public void setThreshold(View view) {
        //Get database reference
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://fridge-app-9c3d5-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference db = database.getReference();

        //Get user inputs
        String name = itemNameText.getText().toString();
        String threshold = thresholdText.getText().toString();

        //Ensure input is above 0
        if (Integer.parseInt(threshold) < 0) {
            showMessage("Error! Must be a positive number!");
        }
        else {
            //Update RestockNo field in database
            db.child("Fridge").child(name).child("RestockNo").setValue(threshold);

            itemNameText.setText("");
            thresholdText.setText("");

            showMessage("Threshold Set!");
        }
    }
}