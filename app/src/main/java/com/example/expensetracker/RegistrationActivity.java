package com.example.expensetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationActivity extends AppCompatActivity {
//firebase classes
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    //UI Elements
    TextView login_Here;
    Button button_register;
    EditText username_signUp, email_signUp, password_signUp, password_signUp_confirm;

    private static final String TAG = "RegistrationActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        initView();

        button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: SignUp Clicked");
                if(!username_signUp.getText().toString().isEmpty() || !email_signUp.getText().toString().isEmpty() || !password_signUp.getText().toString().isEmpty()|| !password_signUp_confirm.getText().toString().isEmpty())
                {
                    if(password_signUp.getText().toString().equals(password_signUp_confirm.getText().toString()) )
                    {
                        Log.d(TAG, "onClick: UsernameMan : " + username_signUp.getText().toString());
                        performSignUp();
                    }else{
                        Toast.makeText(RegistrationActivity.this, "confirm password doest not match!!", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(RegistrationActivity.this, "Some input field missing", Toast.LENGTH_SHORT).show();
                }
            }
        });
        login_Here.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(RegistrationActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }

        });
    }

    public void performSignUp(){
        auth.createUserWithEmailAndPassword(email_signUp.getText().toString(), password_signUp.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    //create user object
                    Users user = new Users(username_signUp.getText().toString(),email_signUp.getText().toString());
                    String id = task.getResult().getUser().getUid();
                    databaseReference.child("Users").child(id).setValue(user);
                    Toast.makeText(RegistrationActivity.this, "User Account created..", Toast.LENGTH_SHORT).show();
                    if(auth.getCurrentUser()!= null){
                        Intent intent = new Intent(RegistrationActivity.this,DashboardActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
                else
                {
                    Toast.makeText(RegistrationActivity.this, "Fail to create Account", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void initView(){
        //UI elements
        login_Here = findViewById(R.id.login_here);
        button_register = findViewById(R.id.button_signUp);
        email_signUp = findViewById(R.id.email_signUp);
        password_signUp = findViewById(R.id.password_signUp);
        password_signUp_confirm = findViewById(R.id.password_signUp_confirm);
        username_signUp = findViewById(R.id.username_signUp);

        //    firebase init
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        databaseReference = database.getReference();

    }
}