package com.example.expensetracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class HomeActivity extends AppCompatActivity {
private BottomNavigationView bottomNavigationView;
private NavigationView navigationView;
private DashBoardFragment dashBoardFragment = new DashBoardFragment();
private  IncomeFragment  incomeFragment = new IncomeFragment();
private  ExpenseFragment expenseFragment = new ExpenseFragment();

private FirebaseAuth auth = FirebaseAuth.getInstance();
private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar=findViewById(R.id.my_toolbar);
        toolbar.setTitle("Expense Manager");
        setSupportActionBar(toolbar);

        DrawerLayout drawerLayout=findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                HomeActivity.this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        );

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView=findViewById(R.id.naView);
        bottomNavigationView=findViewById(R.id.bottomNavigationbar);
//        navigationView.setNavigationItemSelectedListener(this);

        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame,new ProgressView());
        bottomNavigationView.setVisibility(View.GONE);
        toolbar.setVisibility(View.GONE);
        fragmentTransaction.commit();

        getUserName(new UserCallback() {
            @Override
            public void onCallback(String user) {
                if(user != null){
                    FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.main_frame,new DashBoardFragment());
                    bottomNavigationView.setVisibility(View.VISIBLE);
                    toolbar.setVisibility(View.VISIBLE);
                    fragmentTransaction.commit();
                }
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.dashboard:
                        setFragment (dashBoardFragment);
                        bottomNavigationView.setItemBackgroundResource(R.color.dashboard_color);
                        return true;

                    case R.id.expense:
                        setFragment (expenseFragment);
                        bottomNavigationView.setItemBackgroundResource(R.color.expense_color);
                        return true;

                    case R.id.income:
                        setFragment (incomeFragment);
                        bottomNavigationView.setItemBackgroundResource(R.color.income_color);
                        return true;

                    default:
                        return  false;
                }
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;

                switch (item.getItemId()) {
                    case R.id.navdashboard:
                        fragment = dashBoardFragment;
                        bottomNavigationView.setItemBackgroundResource(R.color.dashboard_color);
                        break;
                    case R.id.navincome:
                        fragment = incomeFragment;
                        bottomNavigationView.setItemBackgroundResource(R.color.income_color);
                        break;
                    case R.id.navexpense:
                        fragment = expenseFragment;
                        bottomNavigationView.setItemBackgroundResource(R.color.expense_color);
                        break;
                    case R.id.logout:
                        if (auth.getUid() != null) {
                            auth.signOut();
                            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                            break;
                        }
                }
                if(fragment!=null){
                    FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.main_frame,fragment).commit();
                }
                DrawerLayout drawerLayout=findViewById(R.id.drawer_layout);
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame,fragment);
        fragmentTransaction.commit();
    }

    private void getUserName(UserCallback callback){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(auth.getUid() != null){
                    String username = snapshot.child("Users").child(auth.getUid())
                            .child("username").getValue(String.class);
                    callback.onCallback(username);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public interface UserCallback{
        void onCallback(String user);
    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawerLayout=findViewById(R.id.drawer_layout);
        if(drawerLayout.isDrawerOpen(GravityCompat.END)){
            drawerLayout.closeDrawer(GravityCompat.END);
        }
        else
        {
            super.onBackPressed();
        }
    }
}