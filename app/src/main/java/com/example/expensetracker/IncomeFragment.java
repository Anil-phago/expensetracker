package com.example.expensetracker;

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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link IncomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class IncomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public IncomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment IncomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static IncomeFragment newInstance(String param1, String param2) {
        IncomeFragment fragment = new IncomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private Data incomeData;
    public ArrayList<Data> incomeDatas = new ArrayList<>();
    private IncomeAdapter adapter;

    //FireBase Database
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = database.getReference();

    private TextView income_txt_result;
    private RecyclerView recyclerViewIncome;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

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
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                incomeDatas.clear();
                for (DataSnapshot data : snapshot.child("IncomeData").child(uid).getChildren()) {
                    if(data.getKey() != null){
                        incomeData = data.getValue(Data.class);
                        incomeData.setId(data.getKey());
                        incomeDatas.add(incomeData);
                    }
                }

                Collections.reverse(incomeDatas);
                FragmentManager fm = getFragmentManager();
                adapter = new IncomeAdapter(getContext(), fm);

                int total = 0;
                for (Data d: incomeDatas){
                    total += d.getAmount();
                }

                income_txt_result.setText("Rs."+String.valueOf(total));

                recyclerViewIncome.setAdapter(adapter);
                recyclerViewIncome.setLayoutManager(new LinearLayoutManager(getContext()));
                adapter.setDatas(incomeDatas);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initView(View view){
        recyclerViewIncome = view.findViewById(R.id.recycler_id_income);
        income_txt_result = view.findViewById(R.id.income_txt_result);
    }
}