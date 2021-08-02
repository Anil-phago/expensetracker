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
 * Use the {@link ExpenseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExpenseFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ExpenseFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ExpenseFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ExpenseFragment newInstance(String param1, String param2) {
        ExpenseFragment fragment = new ExpenseFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private Data expenseData;

    public ArrayList<Data> expenseDatas = new ArrayList<>();
    private ExpensesAdapter adapter;

    //FireBase Database
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = database.getReference();


    private TextView expense_txt_result;
    private RecyclerView recyclerViewExpense;

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
                FragmentManager fm = getFragmentManager();
                adapter = new ExpensesAdapter(getContext(), fm);

                int total = 0;
                for (Data d: expenseDatas){
                    total += d.getAmount();
                }

                expense_txt_result.setText("Rs."+String.valueOf(total));

                recyclerViewExpense.setAdapter(adapter);
                recyclerViewExpense.setLayoutManager(new LinearLayoutManager(getContext()));
                adapter.setuserArraylist(expenseDatas);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void initView(View view){

        recyclerViewExpense = view.findViewById(R.id.recycler_id_expense);
        expense_txt_result = view.findViewById(R.id.expense_txt_result);
    }
}