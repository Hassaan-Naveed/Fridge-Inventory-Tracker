package com.aad.implementation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
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

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Objects;

public class StaffLoginActivity extends AppCompatActivity {

    private EditText usernameField;
    private EditText passwordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Create instance of activity and set view
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_login);

        //Set toolbar title
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Staff Login");

        //Find GUI inputs elements
        usernameField = findViewById(R.id.usernameLoginTextField);
        passwordField = findViewById(R.id.passwordLoginTextField);
    }

    private void showMessage() {
        //Show error message at bottom of screen
        Toast toast = Toast.makeText(this, "Username or password incorrect!", Toast.LENGTH_SHORT);
        toast.show();
    }

    public void openMainScreen(String accountType, String username) {
        //Open relevant screen depending on the account type
        switch (accountType) {
            case "Sys Admin": {
                //System admin screen
                Intent intent = new Intent(this, SysAdminActivity.class);
                startActivity(intent);
                break;
            }
            case "Head Chef": {
                //Head chef screen
                Intent intent = new Intent(this, HeadChefScreenActivity.class);
                startActivity(intent);
                break;
            }
            case "Chef": {
                //Chef screen (also send username to new activity)
                Intent intent = new Intent(this, ChefScreenActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
                break;
            }
            case "HS Officer": {
                //Health and Safety officer screen
                Intent intent = new Intent(this, HSOfficerActivity.class);
                startActivity(intent);
                break;
            }
        }
    }

    public void login(View view) {
        //Get database reference
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://fridge-app-9c3d5-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference db = database.getReference();

        //Get inputs
        String username = usernameField.getText().toString();
        String password = passwordField.getText().toString();

        //Create listener to query data from database
        db.child("Users").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                //Display error if data is inaccessible
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    //Get snapshot of database
                    DataSnapshot dataSnapshot = task.getResult();
                    assert dataSnapshot != null;

                    //Ensure that user exists within database
                    boolean userFound = false;
                    boolean passFound = false;

                    //Check that username exists in database
                    for (DataSnapshot userType : dataSnapshot.getChildren()) {
                        for (DataSnapshot user : userType.getChildren()) {
                            if (username.equals(user.getKey())){
                                userFound = true;
                                try {
                                    //Hash inputted password with SHA-256
                                    MessageDigest digest = MessageDigest.getInstance("SHA-256");
                                    String hash = Arrays.toString(digest.digest(password.getBytes(StandardCharsets.UTF_8)));

                                    //Check that hashed passwords match for that user
                                    if (hash.equals(user.child("password").getValue())) {
                                        passFound = true;

                                        //Open relevant screen for that user type
                                        openMainScreen(Objects.requireNonNull(userType.getKey()), username);
                                    }
                                } catch (NoSuchAlgorithmException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    //If the username or password are not found, display error message
                    if (!userFound || !passFound) {
                        showMessage();
                    }
                }
            }
        });
    }
}