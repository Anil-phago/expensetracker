package com.example.expensetracker;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import Model.Data;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ExpenseFragment extends Fragment {

    private Data expenseData;

    public ArrayList<Data> expenseDatas = new ArrayList<>();
    private ExpensesAdapter adapter;

    //FireBase Database
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = database.getReference();


    private TextView expense_txt_result, noExpenseTxt;
    private RecyclerView recyclerViewExpense;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
      {
        View view = inflater.inflate(R.layout.fragment_expense, container, false);
        FirebaseUser mUser = mAuth.getCurrentUser();
        String  uid= mUser.getUid();
        initView(view);
        getDataFromFirebase(uid);
        return view;
    }

    private void getDataFromFirebase(String uid){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                expenseDatas.clear();
                for (DataSnapshot data : snapshot.child("ExpenseData").child(uid).getChildren()) {
                    if(data.getKey() != null) {
                        expenseData = data.getValue(Data.class);
                        expenseData.setId(data.getKey());
                        expenseDatas.add(expenseData);
                    }
                }

                Collections.reverse(expenseDatas);
                if(!expenseDatas.isEmpty()){
                    recyclerViewExpense.setVisibility(View.VISIBLE);
                    noExpenseTxt.setVisibility(View.GONE);
                    FragmentManager fm = getFragmentManager();
                    adapter = new ExpensesAdapter(getContext(), fm);

                    int total = 0;
                    for (Data d: expenseDatas){
                        total += d.getAmount();
                    }

                    expense_txt_result.setText("Rs."+ total);

                    recyclerViewExpense.setAdapter(adapter);
                    recyclerViewExpense.setLayoutManager(new LinearLayoutManager(getContext()));
                    adapter.setuserArraylist(expenseDatas);
                }else{
                    noExpenseTxt.setVisibility(View.VISIBLE);
                    recyclerViewExpense.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void initView(View view){
        noExpenseTxt = view.findViewById(R.id.noExpenseTxt);
        recyclerViewExpense = view.findViewById(R.id.recycler_id_expense);
        expense_txt_result = view.findViewById(R.id.expense_txt_result);
    }
}