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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;


public class HomeActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToogle;
    private NavigationView navigationView;
    private Toolbar mToolbar;
    private FirebaseAuth mAuth;
    private String content;
    private Button mAddTransactionBtn;
    private ImageView mSetBudget;
    private EditText mRemarksText,mAmountText;
    DatabaseReference budgetDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
    private ProgressDialog mProgressDialog;
    private TextView mbudgetText,mNavEmail,mNavName;
    private Spinner mContentSpinner;
    DatabaseReference contentDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
    RecyclerView contentRecycler;
    DatabaseReference pushDatabase;
    String mPushId;

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
        contentRecycler = (RecyclerView) findViewById(R.id.contentList);
        contentRecycler.setLayoutManager(new LinearLayoutManager(this));
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


        getDetails();




    }

    // To set the details by firebase recycler adapter
    private void getDetails() {
        String uid = mAuth.getCurrentUser().getUid();


        final DatabaseReference dbref =contentDatabase.child(uid+"/Transactions");
        final FirebaseRecyclerAdapter<Details,ContentViewHolder> adapter = new FirebaseRecyclerAdapter<Details, ContentViewHolder>(Details.class,R.layout.custom_recycler,ContentViewHolder.class,dbref) {
            @Override
            protected void populateViewHolder(final ContentViewHolder viewHolder, Details model, int position) {
                        final String pushId =getRef(position).getKey();
                        viewHolder.setDate();

                        dbref.child(pushId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.hasChildren()) {
                                    final String money = dataSnapshot.child("Amount").getValue().toString();
                                    String type = dataSnapshot.child("Type").getValue().toString();

                                    viewHolder.setContent(type);
                                    viewHolder.setAmount(money);
                                    viewHolder.cancel.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            dbref.child(pushId).removeValue();
                                            increaseDatabase(money);
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        viewHolder.cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        });
            }
        };

        contentRecycler.setAdapter(adapter);

    }

    private void increaseDatabase(final String money) {

        FirebaseUser currentUser = mAuth.getCurrentUser();
        final String uid = currentUser.getUid();
        budgetDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        budgetDatabase.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String rawBudget = dataSnapshot.child("Budget").getValue().toString();
                int a = Integer.parseInt(rawBudget);
                int b = Integer.parseInt(money);
                int c = a+b;

                budgetDatabase.child(uid+"/Budget").setValue(Integer.toString(c));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    public static class ContentViewHolder extends RecyclerView.ViewHolder {

        public View view;
        public ImageButton cancel;

        public ContentViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            cancel = view.findViewById(R.id.closeImage);
        }

        public  void setContent(String type) {

            ImageView contentImage = view.findViewById(R.id.iconImage);

            switch ((type)){
                case "Transportation":
                    contentImage.setImageResource(R.drawable.transport_icon);
                    break;

                case "Fuel":
                    contentImage.setImageResource(R.drawable.petrol_icon);
                    break;

                case "Food":
                    contentImage.setImageResource(R.drawable.food_icon);
                    break;

                case "Grocery":
                    contentImage.setImageResource(R.drawable.household_icon);
                    break;

                case "Household":
                    contentImage.setImageResource(R.drawable.household_icon);
                    break;

                case "Clothing":
                    contentImage.setImageResource(R.drawable.clothes_icon);
                    break;

            }
        }

        public void setDate(){

            TextView dateView = (TextView) view.findViewById(R.id.dateText);
            Calendar c = Calendar.getInstance();

            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
            String date = df.format(c.getTime());
            dateView.setText(date);

        }

        public  void setAmount(String amount) {
            TextView contentAmount = (TextView) view.findViewById(R.id.contentText);
            contentAmount.setText("Rs. " + amount);
        }
    }



    // Start Spinner Method
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

    @Override
    protected void onStart() {

        String uid = mAuth.getCurrentUser().getUid();
        budgetDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        budgetDatabase.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild("Budget")){

                    String text = dataSnapshot.child("Budget").getValue().toString();
                    if (text!=null)
                        mbudgetText.setText("Rs. " + text);
                }

                else{
                    mbudgetText.setText("Rs.0");
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                String db = databaseError.getMessage();
                Log.d("database error",db);
            }
        });


        super.onStart();
    }

    // For Setting the Budget Method
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
                final String budget = budgetText.getText().toString();
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
                            updateText(budget);
                        }
                    }
                });
            }
        });
        dialog.show();
    }


    // Update Text Method
   private void updateText(final String budget) {

       mbudgetText.setText("Rs. " + budget);
      /* FirebaseUser currentUser = mAuth.getCurrentUser();
       String uid = currentUser.getUid();
       budgetDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        budgetDatabase.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String text = dataSnapshot.child("Budget").getValue().toString();
                if (text!=null)
                    mbudgetText.setText("Rs. " + budget);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                String db = databaseError.getMessage();
                Log.d("database error",db);
            }
        });*/
   }

    //Set Drawer Method
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


        View v = navigationView.getHeaderView(0);
        mNavName = v.findViewById(R.id.navName);
        mNavEmail = v.findViewById(R.id.navEmail);

        String uid = mAuth.getCurrentUser().getUid();

        DatabaseReference db = contentDatabase.child(uid);
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                String email = dataSnapshot.child("email").getValue().toString();
                mNavEmail.setText(email);
                mNavName.setText(name);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    // For selecting the options from app bar
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
            mAddTransactionBtn = dialogView.findViewById(R.id.transactionAddBtn);
            mRemarksText =(EditText) dialogView.findViewById(R.id.remarksText);
            mAmountText = (EditText) dialogView.findViewById(R.id.amountText);



            mContentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    content = parent.getItemAtPosition(position).toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    content = parent.getSelectedItem().toString();

                }
            });

            builder.setCancelable(true);
            builder.setView(dialogView);

            final AlertDialog dialog = builder.create();
            dialog.show();


            mAddTransactionBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String amount = mAmountText.getText().toString();
                    String remarks = mRemarksText.getText().toString();

                    if (amount != null && remarks != null){

                        setDetails(amount,remarks,content);
                        dialog.dismiss();
                    }

                    else{
                        dialog.dismiss();
                        Toast.makeText(HomeActivity.this,"Please Enter Amount",Toast.LENGTH_LONG).show();
                    }

                }
            });

        }
        return super.onOptionsItemSelected(item);
    }


    // for setting the budget contents
    private void setDetails(final String amount, final String remarks,final String content) {


        mProgressDialog.setTitle("Setting Up Details");
        mProgressDialog.setMessage("Please Wait");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        String uid = mAuth.getCurrentUser().getUid();

        budgetDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid+"/Transactions");
        pushDatabase = budgetDatabase.push();
        mPushId = pushDatabase.getKey();


        HashMap<String,String> contentDetails = new HashMap<String, String>();
        contentDetails.put("Type",content);
        contentDetails.put("Amount",amount);
        contentDetails.put("Remarks",remarks);

        pushDatabase.setValue(contentDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){
                    mProgressDialog.dismiss();
                }
            }
        });

        decreaseDatabase(amount);
    }

    private void decreaseDatabase(final String amount) {

        FirebaseUser currentUser = mAuth.getCurrentUser();
        final String uid = currentUser.getUid();
        budgetDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        if (amount != null){

            budgetDatabase.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String rawBudget = dataSnapshot.child("Budget").getValue().toString();
                    int a = Integer.parseInt(rawBudget);
                    int b = Integer.parseInt(amount);
                    int c = a-b;

                    budgetDatabase.child(uid+"/Budget").setValue(Integer.toString(c));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

        else{
            Toast.makeText(HomeActivity.this,"Please Enter Amount",Toast.LENGTH_LONG).show();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


}
