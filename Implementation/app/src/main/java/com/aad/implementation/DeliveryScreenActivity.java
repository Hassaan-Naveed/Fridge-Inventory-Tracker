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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DeliveryScreenActivity extends AppCompatActivity {

    private EditText itemNameText;
    private EditText expiryDateText;
    private EditText quantityText;

    private final List<List<String>> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Create instance of activity and set view
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_screen);

        //Set toolbar title
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Make Delivery");

        //Locate GUI input elements
        itemNameText = findViewById(R.id.deliveryItemTextField);
        expiryDateText = findViewById(R.id.deliveryDateTextField);
        quantityText = findViewById(R.id.deliveryQuantityTextField);

        //Get reference to database and set fridge door to open
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://fridge-app-9c3d5-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference db = database.getReference();

        db.child("Delivery").child("FridgeStatus").setValue("open");
    }

    private void showMessage(String message) {
        //Display message at bottom of screen
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    public void submitItem(View view) {
        //Get user input
        String name = itemNameText.getText().toString();
        String date = expiryDateText.getText().toString();
        String quantity = quantityText.getText().toString();

        //Add user input items to a list
        List<String> item = new ArrayList<>();
        item.add(name);
        item.add(date);
        item.add(quantity);

        //Add list containing single item to list of all items
        items.add(item);

        //Reset GUI fields and display success message
        itemNameText.setText("");
        expiryDateText.setText("");
        quantityText.setText("");

        showMessage("Item Added!");
    }

    public void completeDelivery(View view) {
        //Get database reference
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://fridge-app-9c3d5-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference db = database.getReference();

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

                    //Create list of all items within order
                    List<List<String>> report = new ArrayList<>();

                    //Get all items within order and add to list
                    for (DataSnapshot snapshot : dataSnapshot.child("Report").getChildren()) {
                        //Create list of singular item in order
                        List<String> reportItems = new ArrayList<>();

                        //Get item name and required quantity
                        String reportName = snapshot.getKey();
                        String reportQuantity = Objects.requireNonNull(snapshot.getValue()).toString();

                        //Add to list
                        reportItems.add(reportName);
                        reportItems.add(reportQuantity);

                        report.add(reportItems);
                    }

                    //Algorithm for checking for discrepancies between order and inputted items
                    boolean discrepancyFlag = false;
                    for (List<String> reportItem : report) {
                        boolean itemFound = false;
                        boolean quantityCorrect = false;

                        /* For each item in the report, go through the list and set boolean
                         values to true if the item is found and its quantity is correct */
                        for (List<String> deliveryItem : items) {
                            if (deliveryItem.get(0).equals(reportItem.get(0))) {
                                itemFound = true;

                                if (deliveryItem.get(2).equals(reportItem.get(1))) {
                                    quantityCorrect = true;
                                }
                            }
                        }

                        /* If the item has not been found or its quantity is incorrect, set flag to
                            true, clear the inputted items, and display error message to
                            prompt user to start again */
                        if (!itemFound || !quantityCorrect) {
                            discrepancyFlag = true;
                            items.clear();

                            showMessage("Error, discrepancy found, please start again.");
                            break;
                        }
                    }

                    //If a discrepancy has not been found, add each item within the list to the fridge
                    //Set the items' restock number to it's inputted quantity
                    if (!discrepancyFlag) {
                        for (List<String> deliveryItem : items) {
                            String name = deliveryItem.get(0);
                            String date = deliveryItem.get(1);
                            String quantity = deliveryItem.get(2);

                            db.child("Fridge").child(name).child("Date").setValue(date);
                            db.child("Fridge").child(name).child("Quantity").setValue(quantity);
                            db.child("Fridge").child(name).child("RestockNo").setValue(quantity);
                        }

                        //Display success message and close the fridge
                        showMessage("Delivery Completed!");
                        db.child("Delivery").child("FridgeStatus").setValue("closed");

                        finish();
                    }
                }
            }
        });
    }
}