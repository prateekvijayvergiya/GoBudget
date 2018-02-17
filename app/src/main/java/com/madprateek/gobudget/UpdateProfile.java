package com.madprateek.gobudget;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;


public class UpdateProfile extends AppCompatActivity {

    private Toolbar mToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        mToolbar = (Toolbar) findViewById(R.id.profileAppBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Update Profile");
    }
}
