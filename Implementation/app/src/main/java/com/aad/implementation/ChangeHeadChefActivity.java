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

public class ChangeHeadChefActivity extends AppCompatActivity {

    private EditText username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Create instance of activity and set view
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_head_chef);

        //Set toolbar title
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Change Head Chef");

        //Get GUI elements
        username = findViewById(R.id.changeHeadChefTextField);
    }

    private void showMessage(String message) {
        //Show message at bottom of screen
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    public void promote(View view) {
        //Get database reference
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://fridge-app-9c3d5-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference db = database.getReference();

        //Get user inputs
        String name = username.getText().toString();

        //Add listener to get query database
        db.child("Users").child("Chef").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
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

                    //Find user in database
                    boolean userFound = false;
                    for (DataSnapshot user : dataSnapshot.getChildren()) {
                        if (Objects.equals(user.getKey(), name)){
                            userFound = true;

                            //If user has been found, move their account details to 'Head Chef'
                            db.child("Users").child("Head Chef").child(name).child("password")
                                    .setValue(user.child("password").getValue());
                            db.child("Users").child("Chef").child(name).removeValue();

                            username.setText("");
                            showMessage("Account Promoted!");
                        }
                    }

                    if (!userFound) {
                        //If user has not been found, display error message
                        showMessage("Chef account not found!");
                    }
                }
            }
        });
    }

    public void demote(View view) {
        //Get database reference
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://fridge-app-9c3d5-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference db = database.getReference();

        //Get user inputs
        String name = username.getText().toString();

        //Add listener to get query database
        db.child("Users").child("Head Chef").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
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

                    //Find user in database
                    boolean userFound = false;
                    for (DataSnapshot user : dataSnapshot.getChildren()) {
                        if (Objects.equals(user.getKey(), name)){
                            userFound = true;

                            //If user has been found, move their account details to 'Chef'
                            db.child("Users").child("Chef").child(name).child("password")
                                    .setValue(user.child("password").getValue());
                            db.child("Users").child("Chef").child(name).child("fridgeAccess")
                                    .setValue("true");

                            db.child("Users").child("Head Chef").child(name).removeValue();

                            username.setText("");
                            showMessage("Account Demoted!");
                        }
                    }

                    if (!userFound) {
                        //If user has not been found, display error message
                        showMessage("Head chef account not found!");
                    }
                }
            }
        });
    }


}