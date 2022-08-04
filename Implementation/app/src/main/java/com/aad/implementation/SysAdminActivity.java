package com.aad.implementation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.Objects;

public class SysAdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Create instance of activity and set view
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sys_admin);

        //Set toolbar title
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Home");
    }

    public void viewUsers(View view) {
        //Open view users activity
        Intent intent = new Intent(this, ViewUsersActivity.class);
        startActivity(intent);
    }

    public void managePermissions(View view) {
        //Open manage permissions activity
        Intent intent = new Intent(this, PermissionsActivity.class);
        startActivity(intent);
    }

    public void deleteUser(View view) {
        //Open delete users activity
        Intent intent = new Intent(this, DeleteUserActivity.class);
        startActivity(intent);
    }

    public void changeHeadChef(View view) {
        //Open change head chef activity
        Intent intent = new Intent(this, ChangeHeadChefActivity.class);
        startActivity(intent);
    }

    public void changePIN(View view) {
        //Open change pin activity
        Intent intent = new Intent(this, ChangePinActivity.class);
        startActivity(intent);
    }
}