package com.aad.implementation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ViewItemActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Create instance of activity and set view
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_item);

        //Set toolbar title
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("View Fridge");

        getItems();
    }

    private void getItems() {
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
                }
                else {
                    //Get snapshot of database
                    DataSnapshot dataSnapshot = task.getResult();
                    assert dataSnapshot != null;

                    //Search through database and add any items that are in stock to internal list
                    List<List<String>> items = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String itemName = snapshot.getKey();
                        String expiryDate = Objects.requireNonNull(snapshot.child("Date").getValue()).toString();
                        String quantity = Objects.requireNonNull(snapshot.child("Quantity").getValue()).toString();

                        if (Integer.parseInt(quantity) > 0) {
                            List<String> item = new ArrayList<>();
                            item.add(itemName);
                            item.add(expiryDate);
                            item.add(quantity);

                            items.add(item);
                        }
                    }
                    updateGUI(items);
                }
            }
        });
    }

    private void updateGUI(List<List<String>> items) {
        //Display all items within list on GUI
        LinearLayout layout = (LinearLayout) findViewById(R.id.viewItemsLinearLayout);

        for (List<String> item : items) {
            TextView text = new TextView(this);

            String t = String.format("%s, %s | %s", item.get(0), item.get(2), item.get(1));
            text.setText(t);

            layout.addView(text);
        }
    }
}