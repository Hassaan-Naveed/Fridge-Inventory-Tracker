package com.aad.implementation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CompleteOrderActivity extends AppCompatActivity {

    private final List<List<String>> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Create instance of activity and set view
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_order);

        //Set toolbar title
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Complete Order");

        addItems();
    }

    private void showMessage(String message) {
        //Show message at bottom of screen
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void addItems(){
        //Get database reference
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://fridge-app-9c3d5-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference db = database.getReference();

        //Add listener to get query database
        db.child("Fridge").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                //Check that data is accessible
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    //Get snapshot of database
                    DataSnapshot dataSnapshot = task.getResult();
                    assert dataSnapshot != null;

                    //Search through database and get each food item
                    for (DataSnapshot item : dataSnapshot.getChildren()) {
                        String name = item.getKey();
                        int quantity = Integer.parseInt(Objects.requireNonNull(item.child("Quantity").getValue()).toString());
                        int restockNo = Integer.parseInt(Objects.requireNonNull(item.child("RestockNo").getValue()).toString());

                        //Check if the item needs restocking
                        if (quantity <= restockNo) {
                            //Add item to GUI
                            int amountToOrder = restockNo * 2;
                            String t = String.format("%s: %s", name, amountToOrder);
                            updateGUI(t);

                            //Add item to internal list
                            List<String> orderItem = new ArrayList<>();
                            orderItem.add(name);
                            orderItem.add(String.valueOf(amountToOrder));

                            items.add(orderItem);
                        }
                    }
                }
            }
        });
    }

    private void updateGUI(String message) {
        //Create new text layout and add it to the GUI
        LinearLayout layout = (LinearLayout) findViewById(R.id.completeOrderLinearLayout);

        TextView text = new TextView(this);
        text.setText(message);

        layout.addView(text);
    }

    public void complete(View view) {
        //Get reference to database
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://fridge-app-9c3d5-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference db = database.getReference();

        //Get current day and check if it is monday
        String day = LocalDate.now().getDayOfWeek().toString();
        if (day.equals("MONDAY")) {
            //Update database report with new report
            db.child("Delivery").child("Report").removeValue();
            for (List<String> item : items) {
                db.child("Delivery").child("Report").child(item.get(0)).setValue(item.get(1));
            }

            showMessage("Order Completed!");
        }
        else {
            showMessage("You may only confirm orders on mondays!");
        }
    }
}