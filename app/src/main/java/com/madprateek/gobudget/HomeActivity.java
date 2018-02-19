package com.madprateek.gobudget;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class HomeActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToogle;
    private NavigationView navigationView;
    private Toolbar mToolbar;
    private FirebaseAuth mAuth;
    private ImageView mSetBudget;
    private DatabaseReference budgetDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
    private ProgressDialog mProgressDialog;
    private TextView mbudgetText,mNavEmail,mNavName;
    private Spinner mContentSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.homeAppBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("GoBudget");

        mSetBudget = (ImageView) findViewById(R.id.setBudgetBtn);
        mProgressDialog = new ProgressDialog(HomeActivity.this);
        mbudgetText = (TextView) findViewById(R.id.budgetText);
        mNavEmail = (TextView) findViewById(R.id.navEmail);
        mNavName = (TextView) findViewById(R.id.navName);
        mContentSpinner = (Spinner) findViewById(R.id.contentSpinner);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mToogle = new ActionBarDrawerToggle(this,mDrawerLayout,R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        mDrawerLayout.addDrawerListener(mToogle);
        mToogle.syncState();

        mAuth = FirebaseAuth.getInstance();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setNavDrawer();


        mSetBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setBudget();
            }
        });



    }

    public Spinner initSpinner(Spinner s, int content_array) {

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,content_array,R.layout.spinner_style);
        adapter.setDropDownViewResource(R.layout.spinner_style);
        s.setAdapter(adapter);
        return s;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    private void setBudget() {

        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_dialog,null);

        builder.setCancelable(true);
        builder.setView(dialogView);
        Button setBudget = (Button) dialogView.findViewById(R.id.setBudget);
        final EditText budgetText = (EditText) dialogView.findViewById(R.id.dialogEditText);

        final AlertDialog dialog = builder.create();
        setBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String budget = budgetText.getText().toString();
                FirebaseUser currentUser = mAuth.getCurrentUser();
                String uid = currentUser.getUid();

                mProgressDialog.setTitle("Setting Up Budget");
                mProgressDialog.setMessage("Please Wait");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();
                budgetDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Budget");
                budgetDatabase.setValue(budget).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()){

                            dialog.cancel();
                            mProgressDialog.dismiss();
                            Toast.makeText(HomeActivity.this,"Budget added Successfully",Toast.LENGTH_SHORT).show();
                            updateText();
                        }
                    }
                });
            }
        });
        dialog.show();
    }


   private void updateText() {

       FirebaseUser currentUser = mAuth.getCurrentUser();
       String uid = currentUser.getUid();
       budgetDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        budgetDatabase.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String text = dataSnapshot.child("Budget").getValue().toString();
                if (text!=null)
                    mbudgetText.setText("Rs. " + text);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                String db = databaseError.getMessage();
                Log.d("database error",db);
            }
        });
   }


    private void setNavDrawer() {

        navigationView = (NavigationView) findViewById(R.id.navView);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);

                mDrawerLayout.closeDrawers();

                switch (item.getItemId()){

                    case R.id.navSignOutBtn:
                        mAuth.signOut();
                        startActivity(new Intent(HomeActivity.this,MainActivity.class));
                        finish();
                        break;

                    case R.id.navViewprofile:
                        startActivity(new Intent(HomeActivity.this,UpdateProfile.class));
                        finish();
                        break;

                }
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToogle.onOptionsItemSelected(item)){
            return  true;
        }

        else if (item.getItemId() == R.id.menuAddIcon){

            AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.custom_transaction_dialog,null);
            mContentSpinner = dialogView.findViewById(R.id.contentSpinner);
            mContentSpinner = initSpinner(mContentSpinner, R.array.content_array);

            builder.setCancelable(true);
            builder.setView(dialogView);

            final AlertDialog dialog = builder.create();
            dialog.show();

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


}
