package com.aad.implementation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class HeadChefScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Create instance of activity and set view
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_head_chef_screen);

        //Set toolbar title
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Home");
    }

    private void showMessage() {
        //Show message at bottom of screen
        Toast toast = Toast.makeText(this, "Back door is open", Toast.LENGTH_SHORT);
        toast.show();
    }

    private interface OpenCallback {
        void isOpen(boolean open);
    }

    private void checkOpen(OpenCallback openCallback) {
        //Get database reference
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://fridge-app-9c3d5-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference db = database.getReference();

        //Add listener to get query database
        db.child("Delivery").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
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

                    //Check that fridge back door is not currently open and send data back using callback interface
                    openCallback.isOpen(Objects.requireNonNull(
                            dataSnapshot.child("FridgeStatus").getValue()).toString().equals("open"));
                }
            }
        });
    }

    public void viewItems(View view) {
        //Check fridge door status
        checkOpen(new OpenCallback() {
            @Override
            public void isOpen(boolean open) {
                if (!open) {
                    //Open view item screen
                    Intent intent = new Intent(HeadChefScreenActivity.this, ViewItemActivity.class);
                    startActivity(intent);
                }
                else {
                    showMessage();
                }
            }
        });
    }

    public void addItem(View view) {
        //Check fridge door status
        checkOpen(new OpenCallback() {
            @Override
            public void isOpen(boolean open) {
                if (!open) {
                    //Open add item activity
                    Intent intent = new Intent(HeadChefScreenActivity.this, AddItemActivity.class);
                    startActivity(intent);
                }
                else {
                    showMessage();
                }
            }
        });
    }

    public void removeItem(View view) {
        //Check fridge door status
        checkOpen(new OpenCallback() {
            @Override
            public void isOpen(boolean open) {
                if (!open) {
                    //Open remove item activity
                    Intent intent = new Intent(HeadChefScreenActivity.this, RemoveItemActivity.class);
                    startActivity(intent);
                }
                else {
                    showMessage();
                }
            }
        });
    }

    public void managePermissions(View view) {
        //Open permissions screen activity
        Intent intent = new Intent(this, PermissionsActivity.class);
        startActivity(intent);
    }

    public void manageStock(View view) {
        //Open manage stock activity
        Intent intent = new Intent(this, ManageStockActivity.class);
        startActivity(intent);
    }

    public void completeOrder(View view) {
        //Open complete order activity
        Intent intent = new Intent(this, CompleteOrderActivity.class);
        startActivity(intent);
    }

    public void notifications(View view) {
        //Open notifications activity
        Intent intent = new Intent(this, NotificationsActivity.class);
        startActivity(intent);
    }
}