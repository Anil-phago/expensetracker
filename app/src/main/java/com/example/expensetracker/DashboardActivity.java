package com.example.expensetracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.AppCompatActivity;


public class DashboardActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private BottomNavigationView bottomNavigationView;

    Button button_logout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        auth = FirebaseAuth.getInstance();


        // this button is used to logout
//        button_logout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(auth.getUid()!= null){
//                    auth.signOut();
//                    Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
//                    startActivity(intent);
//                    finish();
//                }
//            }
//        });

        Intent intent = new Intent(DashboardActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }


    public void initView(){
//        button_logout = findViewById(R.id.button_logout);
        bottomNavigationView = findViewById(R.id.bottomNavigationbar);
    }
}