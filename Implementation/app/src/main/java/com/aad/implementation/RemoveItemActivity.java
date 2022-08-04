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

public class RemoveItemActivity extends AppCompatActivity {

    private EditText itemNameText;
    private EditText quantityText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Create instance of activity and set view
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_item);

        //Set toolbar title
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Remove Item");

        //Get GUI elements
        itemNameText = findViewById(R.id.stockItemNameTextField);
        quantityText = findViewById(R.id.stockThresholdTextField);
    }

    private void showMessage(String message){
        //Show message at bottom of screen
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    public void removeItem(View view) {
        //Get database reference
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://fridge-app-9c3d5-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference db = database.getReference();

        //Get user inputs
        String name = itemNameText.getText().toString();
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

                    //Find item within database
                    boolean itemFound = false;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (Objects.requireNonNull(snapshot.getKey()).equals(name)) {
                            itemFound = true;

                            //Get new quantity of item after removal
                            int originalQuantity = Integer.parseInt(Objects.requireNonNull(
                                    dataSnapshot.child(name).child("Quantity").getValue()).toString());
                            int inputQuantity = Integer.parseInt(quantity);

                            int newQuantity = originalQuantity - inputQuantity;

                            //Check that new quantity is above 0
                            if (newQuantity > 0) {
                                //Update database
                                db.child("Fridge").child(name).child("Quantity").setValue(String.valueOf(newQuantity));

                                itemNameText.setText("");
                                quantityText.setText("");

                                showMessage("Item Removed!");
                            }
                            else {
                                showMessage("Not enough items!" );
                            }
                        }
                    }

                    if (!itemFound) {
                        showMessage("Item not found!");
                    }
                }
            }
        });
    }
}