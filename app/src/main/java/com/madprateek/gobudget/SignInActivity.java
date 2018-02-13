package com.madprateek.gobudget;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignInActivity extends AppCompatActivity {

    private TextInputLayout mSignInEmail,mSignInPass;
    private Button mSubmitBtn;
    private Toolbar mToolbar;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mToolbar = (Toolbar) findViewById(R.id.signInAppBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Sign In");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSubmitBtn = (Button) findViewById(R.id.submitBtn);
        mSignInEmail = (TextInputLayout) findViewById(R.id.signInEmail);
        mSignInPass = (TextInputLayout) findViewById(R.id.signInpassword);
        mProgressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();

        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = mSignInEmail.getEditText().getText().toString();
                String pass = mSignInPass.getEditText().getText().toString();
                if(!TextUtils.isEmpty(email) || !TextUtils.isEmpty(pass)){
                    mProgressDialog.setTitle("Logging In");
                    mProgressDialog.setMessage("Please Wait");
                    mProgressDialog.setCanceledOnTouchOutside(false);
                    mProgressDialog.show();
                    loginUser(email,pass);
                }
            }
        });
    }

    private void loginUser(String email, String pass) {
        mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
               if (task.isSuccessful()){
                   mProgressDialog.dismiss();
                   Intent homeActivityIntent = new Intent(SignInActivity.this,HomeActivity.class);
                   startActivity(homeActivityIntent);
                   finish();
               }
            }
        });
    }


}
