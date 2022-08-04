package com.aad.implementation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class PermissionsActivity extends AppCompatActivity {

    private EditText usernameField;
    private SwitchCompat permissionToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Create instance of activity and set view
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permissions);

        //Set toolbar title
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Manage Permissions");

        //Get GUI elements
        usernameField = findViewById(R.id.usernamePermsTextField);
        permissionToggle = findViewById(R.id.permissionToggleSwitch);
    }

    public void submitPermission(View view) {
        //Get database reference
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://fridge-app-9c3d5-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference db = database.getReference();

        //Get user inputs
        String username = usernameField.getText().toString();
        String toggle = String.valueOf(permissionToggle.isChecked());

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

                    //Search through all Chef users
                    for (DataSnapshot user : dataSnapshot.child("Chef").getChildren()) {
                        if (username.equals(user.getKey())){
                            //Set fridge access to new value
                            db.child("Users").child("Chef").child(username).child("fridgeAccess").setValue(toggle);

                            usernameField.setText("");
                        }
                    }
                }
            }
        });
    }
}