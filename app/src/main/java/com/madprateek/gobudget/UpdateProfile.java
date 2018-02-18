package com.madprateek.gobudget;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;


public class UpdateProfile extends AppCompatActivity {

    private Toolbar mToolbar;
    private EditText mName,mEmail;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private Button mUpdateBtn;
    private TextView mNavEmail,mNavName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        mToolbar = (Toolbar) findViewById(R.id.profileAppBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Update Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mName = (EditText) findViewById(R.id.FnameValue);
        mEmail = (EditText) findViewById(R.id.emailValue);
        mNavEmail = (TextView) findViewById(R.id.navEmail);
        mNavName = (TextView) findViewById(R.id.navName);
        mUpdateBtn = (Button) findViewById(R.id.updateBtn);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        final String uid = currentUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                String email = dataSnapshot.child("email").getValue().toString();

                mName.setText(name);
                mEmail.setText(email);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.EditProfile){

            mUpdateBtn.setVisibility(View.VISIBLE);
            mName.setEnabled(true);
            mName.setCursorVisible(true);

            mUpdateBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Users").child(uid);

                    String name = mName.getText().toString();
                    String email = mEmail.getText().toString();

                    mNavEmail.setText(email);
                    mNavName.setText(name);
                    Map info = new HashMap();
                    info.put("name",name);
                    mDatabase.updateChildren(info, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if (databaseError == null){
                                Toast.makeText(UpdateProfile.this,"Profile Updated Successfully",Toast.LENGTH_LONG).show();
                                mUpdateBtn.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }
}
