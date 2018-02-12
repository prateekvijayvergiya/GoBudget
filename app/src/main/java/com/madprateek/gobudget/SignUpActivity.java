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


public class SignUpActivity extends AppCompatActivity {

    private TextInputLayout mSignUpEmail,mSignUpPass,mSignUpName;
    private Button mSubmitBtn;
    private Toolbar mToolbar;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mToolbar = (Toolbar) findViewById(R.id.signUpAppBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Sign Up");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSignUpEmail = (TextInputLayout) findViewById(R.id.signUpEmail);
        mSignUpPass = (TextInputLayout) findViewById(R.id.signUpPassword);
        mSignUpName = (TextInputLayout) findViewById(R.id.signUpName);
        mSubmitBtn = (Button) findViewById(R.id.signUpSubmitBtn);
        mProgressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();

        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mSignUpEmail.getEditText().getText().toString();
                String pass = mSignUpPass.getEditText().getText().toString();

                if(!TextUtils.isEmpty(email) || !TextUtils.isEmpty(pass)){
                    mProgressDialog.setTitle("Registering User");
                    mProgressDialog.setMessage("Please Wait");
                    mProgressDialog.setCanceledOnTouchOutside(false);
                    mProgressDialog.show();
                    registerUser(email,pass);
                }
            }
        });
    }

    private void registerUser(String email, String pass) {

        mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){
                    mProgressDialog.dismiss();
                    Intent homeActivityIntent = new Intent(SignUpActivity.this,HomeActivity.class);
                    startActivity(homeActivityIntent);
                    finish();
                }
            }
        });
    }
}
