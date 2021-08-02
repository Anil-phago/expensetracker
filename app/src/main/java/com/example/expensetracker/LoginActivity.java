package com.example.expensetracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
   Button button_login;
   TextView registeration;
   EditText email_login, password_login;
   private FirebaseAuth auth;
    private static final String TAG = "LoginActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();

        if(auth.getCurrentUser() != null){
            Intent intent = new Intent(LoginActivity.this,DashboardActivity.class);
            startActivity(intent);
            finish();
        }
        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = email_login.getText().toString();
                String  password = password_login.getText().toString();
                if(email.isEmpty() ||password.isEmpty())
                {
                    Toast.makeText(LoginActivity.this, "Some inputField missing!!", Toast.LENGTH_SHORT).show();
                }
                else{
                    performLogin(email,password);
                }
            }
        });

        registeration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              Intent i=new Intent(LoginActivity.this,RegistrationActivity.class);
              startActivity(i);
              finish();
            }
        });


    }


    public void performLogin(String email, String password)
    {

        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    Intent intent = new Intent(LoginActivity.this,DashboardActivity.class);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });




    }



    public void initView()
    {
        registeration=findViewById(R.id.Signup_reg);
        button_login = findViewById(R.id.button_login);
        email_login = findViewById(R.id.email_login);
        password_login = findViewById(R.id.password_login);
        auth = FirebaseAuth.getInstance();
    }
}