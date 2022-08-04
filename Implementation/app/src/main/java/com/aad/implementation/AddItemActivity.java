package com.aad.implementation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class AddItemActivity extends AppCompatActivity {

    private EditText itemNameText;
    private EditText expiryDateText;
    private EditText quantityText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Create instance of activity and set view
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        //Set toolbar title
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Add Items");

        //Get GUI elements
        itemNameText = findViewById(R.id.addItemNameTextField);
        expiryDateText = findViewById(R.id.addItemDateTextField);
        quantityText = findViewById(R.id.addItemQuantityTextField);
    }

    private void showMessage(String message) {
        //Show message at bottom of screen
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    public void submitItem(View view) {
        //Get database reference
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://fridge-app-9c3d5-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference db = database.getReference();

        //Get user inputs
        String name = itemNameText.getText().toString();
        String date = expiryDateText.getText().toString();
        String quantity = quantityText.getText().toString();

        //Add listener to get query database
        db.child("Fridge").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                //Check that data is accessible
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    //Get snapshot of database
                    DataSnapshot dataSnapshot = task.getResult();
                    assert dataSnapshot != null;

                    //Get total quantity of all items currently in database
                    int count = 0;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        count += Integer.parseInt(Objects.requireNonNull(
                                snapshot.child("Quantity").getValue()).toString());
                    }

                    //Check that adding new items will not exceed fridge capacity
                    if (count + Integer.parseInt(quantity) > 440) {
                        showMessage("Exceeds Fridge Capacity!");
                    }
                    else {
                        //Add item to database and reset input fields
                        db.child("Fridge").child(name).child("Date").setValue(date);
                        db.child("Fridge").child(name).child("Quantity").setValue(quantity);
                        db.child("Fridge").child(name).child("RestockNo").setValue(quantity);

                        itemNameText.setText("");
                        expiryDateText.setText("");
                        quantityText.setText("");

                        showMessage("Item Added!");
                    }
                }
            }
        });
    }
}