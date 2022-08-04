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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class NotificationsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Create instance of activity and set view
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        //Set toolbar title
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Notifications");

        getNotifications();
    }

    private void getNotifications() {
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

                    //Search through items in the fridge
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        //Get name of item, current date, quantity, and restocking number
                        String name = snapshot.getKey();

                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                        LocalDate date = LocalDate.parse(Objects.requireNonNull(snapshot.child("Date").getValue()).toString(), formatter);
                        LocalDate now = LocalDate.now();

                        int quantity = Integer.parseInt(Objects.requireNonNull(snapshot.child("Quantity").getValue()).toString());
                        int restockNo = Integer.parseInt(Objects.requireNonNull(snapshot.child("RestockNo").getValue()).toString());

                        //Check if the item needs restocking
                        if (quantity < restockNo) {
                            String notification = String.format("%s: Quantity (%s) below restocking threshold (%s)", name, quantity, restockNo);
                            addNotifications(notification);
                        }
                        //Check if the item is about to expire
                        if (now.plusDays(3).isAfter(date)) {
                            String notification = String.format("%s: Expiring soon (%s)", name, date);
                            addNotifications(notification);
                        }
                    }
                }
            }
        });
    }

    private void addNotifications(String notification) {
        //Create new text layout and add to GUI
        LinearLayout layout = (LinearLayout) findViewById(R.id.notificationsLinearLayout);

        TextView text = new TextView(this);
        text.setText(notification);

        layout.addView(text);

    }
}