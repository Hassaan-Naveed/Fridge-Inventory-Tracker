package com.aad.implementation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class CreateAccountActivity extends AppCompatActivity {

    private EditText username;
    private EditText password;
    private Spinner accountType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Create instance of activity and set view
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        //Set toolbar title
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Create Account");

        //Find GUI items
        username = findViewById(R.id.usernameCreateTextField);
        password = findViewById(R.id.passwordCreateTextField);
        accountType = (Spinner) findViewById(R.id.accountTypeCreateSpinner);

        //Populate spinner with options
        ArrayList<String> typeList = new ArrayList<>();
        typeList.add("Account Type");
        typeList.add("Sys Admin");
        typeList.add("Head Chef");
        typeList.add("Chef");
        typeList.add("HS Officer");

        ArrayAdapter<String> adp = new ArrayAdapter<> (this,android.R.layout.simple_spinner_dropdown_item, typeList);
        accountType.setAdapter(adp);
    }

    private void showMessage(String message) {
        //Create message at bottom of screen
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    public void createAccount(View view) throws NoSuchAlgorithmException {
        //Get text from GUI input fields
        String user = username.getText().toString();
        String pass = password.getText().toString();
        String type = accountType.getSelectedItem().toString();

        //Check that fields have not been left blank
        if (!type.equals("Account Type") && !user.equals("") && !pass.equals("")) {

            //Create database reference
            FirebaseDatabase database = FirebaseDatabase.getInstance("https://fridge-app-9c3d5-default-rtdb.europe-west1.firebasedatabase.app");
            DatabaseReference db = database.getReference();

            //Hash password with SHA-256 algorithm
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String hash = Arrays.toString(digest.digest(pass.getBytes(StandardCharsets.UTF_8)));

            //Add user account to database
            db.child("Users").child(type).child(user).child("password").setValue(hash);

            //If user account is chef, allow them access to the fridge by default
            if (type.equals("Chef")) {
                db.child("Users").child(type).child(user).child("fridgeAccess").setValue("true");
            }

            //Reset GUI input fields and show success message
            username.setText("");
            password.setText("");
            accountType.setSelection(0);

            showMessage("Account Successfully Created");
        }
        else {
            //Show failure message
            showMessage("Fields must not be left blank!");
        }
    }
}