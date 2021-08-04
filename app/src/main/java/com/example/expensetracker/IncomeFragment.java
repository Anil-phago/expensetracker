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

public class IncomeFragment extends Fragment {
    private Data incomeData;
    public ArrayList<Data> incomeDatas = new ArrayList<>();
    private IncomeAdapter adapter;

    //FireBase Database
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = database.getReference();

    private TextView income_txt_result, noIncomeTxt;
    private RecyclerView recyclerViewIncome;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_income, container, false);

        FirebaseUser mUser = mAuth.getCurrentUser();
        String  uid= mUser.getUid();

        initView(view);

        getDataFromFirebase(uid);
        // Inflate the layout for this fragment
        return view;
    }

    private void getDataFromFirebase(String uid){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                incomeDatas.clear();
                for (DataSnapshot data : snapshot.child("IncomeData").child(uid).getChildren()) {
                    if(data != null){
                        if(data.getKey() != null){
                            incomeData = data.getValue(Data.class);
                            if(incomeData != null){
                                incomeData.setId(data.getKey());
                                incomeDatas.add(incomeData);
                            }
                        }
                    }
                }

                Collections.reverse(incomeDatas);
                if(!incomeDatas.isEmpty()){
                    noIncomeTxt.setVisibility(View.GONE);
                    recyclerViewIncome.setVisibility(View.VISIBLE);
                    FragmentManager fm = getFragmentManager();
                    adapter = new IncomeAdapter(getContext(), fm);

                    int total = 0;
                    for (Data d: incomeDatas){
                        total += d.getAmount();
                    }

                    income_txt_result.setText("Rs."+ total);

                    recyclerViewIncome.setAdapter(adapter);
                    recyclerViewIncome.setLayoutManager(new LinearLayoutManager(getContext()));
                    adapter.setDatas(incomeDatas);
                }else{
                    noIncomeTxt.setVisibility(View.VISIBLE);
                    recyclerViewIncome.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initView(View view){
        recyclerViewIncome = view.findViewById(R.id.recycler_id_income);
        income_txt_result = view.findViewById(R.id.income_txt_result);
        noIncomeTxt = view.findViewById(R.id.noIncomeTxt);
    }
}