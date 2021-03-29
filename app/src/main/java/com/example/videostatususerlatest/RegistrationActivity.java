package com.example.videostatususerlatest;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.videostatususerlatest.Dashboard.Dashboard;
import com.example.videostatususerlatest.Models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class RegistrationActivity extends AppCompatActivity {
    EditText userName,password,password1, fullName;
    TextView AccountExists;
    Button register;
    private FirebaseAuth mAuth;//Used for firebase authentication
    private ProgressDialog loadingBar;//Used to show the progress of the registration process
    String pwd, email, userFullName;
    FirebaseAuth auth;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        initializeUI();
    }

    private void initializeUI() {
        mAuth = FirebaseAuth.getInstance();
        userName = (EditText) findViewById(R.id.username2);
        password = (EditText) findViewById(R.id.Password2);
        password1 = (EditText) findViewById(R.id.pass2);
        register = (Button) findViewById(R.id.submit_btn);
        AccountExists = (TextView) findViewById(R.id.Already_link);
        loadingBar = new ProgressDialog(this);
        fullName = (EditText) findViewById(R.id.etUserName);

        //When user has  an account already he should be sent to login activity.
        AccountExists.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendUserToLoginActivity();
            }
        });

        //When user clicks on register create a new account for user
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewAccount();
            }
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    private void sendUserToLoginActivity() {

            //This is to send user to Login Activity.
            Intent loginIntent = new Intent(RegistrationActivity.this, LoginActivity.class);
            startActivity(loginIntent);
    }

    /*
        This method creates new account for new users.
     */
    private void createNewAccount() {
        email = userName.getText().toString().trim();
        userFullName = fullName.getText().toString().trim();
        pwd = password.getText().toString();
        String cmp = password1.getText().toString();

        SharedPreferences userEmailPassword = getSharedPreferences("email_password", MODE_PRIVATE);
        SharedPreferences.Editor editor = userEmailPassword.edit();
        editor.putString("EMAIL", email);
        editor.putString("PASS", pwd);
        editor.putString("NAME", userFullName);
        editor.apply();

        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(RegistrationActivity.this,"Please enter email id", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(userFullName))
        {
            Toast.makeText(RegistrationActivity.this,"Please enter your name", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(pwd))
        {
            Toast.makeText(RegistrationActivity.this,"Please enter password", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(cmp))
        {
            Toast.makeText(RegistrationActivity.this,"Please enter password", Toast.LENGTH_SHORT).show();
        }
        else if(!pwd.equals(cmp))
        {
            Toast.makeText(RegistrationActivity.this,"Passwords doesn't match!", Toast.LENGTH_SHORT).show();
        }
        else if(!cmp.equals(pwd))
        {
            Toast.makeText(RegistrationActivity.this,"Passwords doesn't match!", Toast.LENGTH_SHORT).show();
        }
        else if (!(Patterns.EMAIL_ADDRESS.matcher(email).matches())) {
            Toast.makeText(this, "Please enter valid email", Toast.LENGTH_SHORT).show();
        }
        else
        {
            //When both email and password are available create a new accountToast.makeText(RegisterActivity.this,"Please enter password",Toast.LENGTH_SHORT).show();
            //Show the progress on Progress Dialog
            loadingBar.setTitle("Creating New Account");
            loadingBar.setMessage("Please wait, we are creating new Account");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();
            mAuth.createUserWithEmailAndPassword(email,pwd)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())//If account creation successful print message and send user to Login Activity
                            {

                                insertInRealtimeDatabase(userFullName, email, pwd);
                                loadingBar.dismiss();
                            }
                            else//Print the error message incase of failure
                            {
                                String msg = task.getException().toString();
                                Toast.makeText(RegistrationActivity.this,"Error: "+msg, Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
        }
    }

    private void insertInRealtimeDatabase(String fullName, String email, String password) {
        UserModel user = new UserModel(fullName, email, password);
        database.getReference("users").child(auth.getCurrentUser().getUid()).setValue(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            loadingBar.dismiss();
                            Toast.makeText(RegistrationActivity.this, "User created successfully", Toast.LENGTH_SHORT).show();
                            sendUserToDashboardActivity();
                        }
                        else
                        {
                            loadingBar.dismiss();
                            Toast.makeText(RegistrationActivity.this, "Registration error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }

    /*
        After successfull registration send user to Login page.
     */
    private void sendUserToDashboardActivity() {
        //This is to send user to Login Activity.
        Intent loginIntent = new Intent(RegistrationActivity.this, Dashboard.class);
        startActivity(loginIntent);
    }
}