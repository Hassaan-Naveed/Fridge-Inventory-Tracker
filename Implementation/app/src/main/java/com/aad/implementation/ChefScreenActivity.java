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

public class ChefScreenActivity extends AppCompatActivity {

    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Create instance of activity and set view
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chef_screen);

        //Set toolbar title
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Home");

        //Get username from previous activity
        Bundle extras = getIntent().getExtras();
        username = extras.getString("username");
    }

    private void showMessage(String message) {
        //Display message
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    private interface PermissionCallback {
        //Callback interface for getting data from database query listener
        void isAllowed(boolean access);
    }

    private void checkPermissions(PermissionCallback permissionCallback) {
        //Get reference to database
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://fridge-app-9c3d5-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference db = database.getReference();

        //Create listener to query database
        db.child("Users").child("Chef").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                //Display error message if data is inaccessible
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    //Get snapshot of database
                    DataSnapshot dataSnapshot = task.getResult();
                    assert dataSnapshot != null;

                    //Find username within database and check that they have fridge access and send data back using callback interface
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        if (Objects.equals(snapshot.getKey(), username)) {
                            permissionCallback.isAllowed(Objects.requireNonNull(
                                    snapshot.child("fridgeAccess").getValue()).toString().equals("true"));
                        }
                    }
                }
            }
        });
    }

    private interface OpenCallback {
        //Callback interface for getting data from database query listener
        void isOpen(boolean open);
    }

    private void checkOpen(OpenCallback openCallback) {
        //Get reference to database
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://fridge-app-9c3d5-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference db = database.getReference();

        //Create listener to query database
        db.child("Delivery").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                //Display error message if data is inaccessible
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
        //Check that user has access to the fridge
        checkPermissions(new PermissionCallback() {
            @Override
            public void isAllowed(boolean access) {
                if (access){

                    //Check that fridge back door is not open
                    checkOpen(new OpenCallback() {
                        @Override
                        public void isOpen(boolean open) {
                            if (!open) {

                                //Open view item screen
                                Intent intent = new Intent(ChefScreenActivity.this, ViewItemActivity.class);
                                startActivity(intent);
                            }
                            else {
                                showMessage("Back door is open");
                            }
                        }
                    });
                }
                else {
                    showMessage("Access Denied");
                }
            }
        });
    }

    public void addItem(View view) {
        //Check that user has access to the fridge
        checkPermissions(new PermissionCallback() {
            @Override
            public void isAllowed(boolean access) {
                if (access){

                    //Check that fridge back door is not open
                    checkOpen(new OpenCallback() {
                        @Override
                        public void isOpen(boolean open) {
                            if (!open) {

                                //Open view item screen
                                Intent intent = new Intent(ChefScreenActivity.this, AddItemActivity.class);
                                startActivity(intent);
                            }
                            else {
                                showMessage("Back door is open");
                            }
                        }
                    });
                }
                else {
                    showMessage("Access Denied");
                }
            }
        });
    }

    public void removeItem(View view) {
        //Check that user has access to the fridge
        checkPermissions(new PermissionCallback() {
            @Override
            public void isAllowed(boolean access) {
                if (access){

                    //Check that fridge back door is not open
                    checkOpen(new OpenCallback() {
                        @Override
                        public void isOpen(boolean open) {
                            if (!open) {

                                //Open view item screen
                                Intent intent = new Intent(ChefScreenActivity.this, RemoveItemActivity.class);
                                startActivity(intent);
                            }
                            else {
                                showMessage("Back door is open");
                            }
                        }
                    });
                }
                else {
                    showMessage("Access Denied");
                }
            }
        });
    }
}