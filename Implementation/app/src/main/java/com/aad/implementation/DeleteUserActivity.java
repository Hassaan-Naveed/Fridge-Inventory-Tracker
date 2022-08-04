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

public class DeleteUserActivity extends AppCompatActivity {

    private EditText username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Create instance of activity and set view
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_user);

        //Set toolbar title
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Delete User");

        //Get GUI elements
        username = findViewById(R.id.deleteUserTextField);
    }

    private void showMessage(String message) {
        //Show message at bottom of screen
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    public void deleteUser (View view) {
        //Get database reference
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://fridge-app-9c3d5-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference db = database.getReference();

        //Get user inputs
        String name = username.getText().toString();

        //Add listener to get query database
        db.child("Users").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
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
                    for (DataSnapshot userType : dataSnapshot.getChildren()) {
                        for (DataSnapshot user : userType.getChildren()) {
                            if (Objects.equals(user.getKey(), name)){
                                userFound = true;

                                //Remove user details from database
                                db.child("Users").child(Objects.requireNonNull(userType.getKey())).child(name).removeValue();
                                username.setText("");

                                showMessage("Account Deleted!");
                            }
                        }
                    }

                    if (!userFound) {
                        showMessage("Account not found!");
                    }
                }
            }
        });
    }
}