package com.example.expensetracker;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;

import Model.Data;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DashBoardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DashBoardFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DashBoardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DashBoardFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DashBoardFragment newInstance(String param1, String param2) {
        DashBoardFragment fragment = new DashBoardFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

        }

    }
//floating button

    private FloatingActionButton fab_main_btn;
    private  FloatingActionButton fab_income_btn;
    private  FloatingActionButton fab_expense_btn;

    //floatting button text view
    private TextView fab_income_txt;
    private TextView fab_expense_txt;
    //boolena
    private  boolean  isOpen=false;

    //animation
    private Animation FadeOpen,FadeClose;


    //dashboard income and expense result
    private  TextView totalIncomeResult;
    private TextView totalExpenseResult;

//firebae

    private FirebaseAuth mAuth;
    private DatabaseReference mIncomeDatabase;
    private DatabaseReference mExpenceDatabase;

    //Recycler view
    private RecyclerView mRecyclerIncome ;
    private RecyclerView getmRecyclerExpense;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myview= inflater.inflate(R.layout.fragment_dash_board, container, false);


        //firebase work
mAuth = FirebaseAuth.getInstance();
FirebaseUser mUser=mAuth.getCurrentUser();
String uid=mUser.getUid();

mIncomeDatabase= FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);
mExpenceDatabase=FirebaseDatabase.getInstance().getReference().child("ExpenseData").child(uid);

        //connect floating button to layout
        fab_main_btn=myview.findViewById(R.id.fb_main_plus_btn);
        fab_income_btn=myview.findViewById(R.id.income_ft_btn);
        fab_expense_btn=myview.findViewById(R.id.expense_ft_btn);

        //connect floating text
        fab_income_txt=myview.findViewById(R.id.income_ft_text);
        fab_expense_txt=myview.findViewById(R.id.expense_ft_text);

        //total income and expense result set
        totalIncomeResult = myview.findViewById(R.id.income_set_result);
        totalExpenseResult = myview.findViewById(R.id.expense_set_result);
        //Recycler

        mRecyclerIncome = myview.findViewById(R.id.recycler_income);
        getmRecyclerExpense = myview.findViewById(R.id.recycler_expense);


        //animation connectivity
        FadeOpen= AnimationUtils.loadAnimation(getActivity(),R.anim.fade_open);
        FadeClose=AnimationUtils.loadAnimation(getActivity(),R.anim.fade_close);



        fab_main_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addData();
                if(isOpen)
                {
                  fab_income_btn.startAnimation(FadeClose);
                  fab_expense_btn.startAnimation(FadeClose);
                  fab_income_btn.setClickable(false);
                  fab_expense_btn.setClickable(false);
                  fab_income_txt.startAnimation(FadeClose);
                  fab_expense_txt.startAnimation(FadeClose);
                  fab_income_txt.setClickable(false);
                  fab_expense_txt.setClickable(false);
                  isOpen=false;
                }
                else{
                    fab_income_btn.startAnimation(FadeOpen);
                    fab_expense_btn.startAnimation(FadeOpen);
                    fab_income_btn.setClickable(true);
                    fab_expense_btn.setClickable(true);
                    fab_income_txt.startAnimation(FadeOpen);
                    fab_expense_txt.startAnimation(FadeOpen);
                    fab_income_txt.setClickable(true);
                    fab_expense_txt.setClickable(true);
                    isOpen=true;
                }

            }
        });

        //caculate

     mExpenceDatabase.addValueEventListener(new ValueEventListener() {
         @Override
         public void onDataChange(@NonNull DataSnapshot snapshot) {
             float totalSum =0;
             for (DataSnapshot mysnap:snapshot.getChildren())
             {
                 Data data = mysnap.getValue(Data.class);
                 totalSum+=data.getAmount();
                 String stResult = String.valueOf(totalSum);
                 totalExpenseResult.setText(stResult);
             }
         }

         @Override
         public void onCancelled(@NonNull DatabaseError error) {

         }

     });

        mIncomeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                float totalSum =0;
                for (DataSnapshot mysnap:snapshot.getChildren())
                {
                    Data data = mysnap.getValue(Data.class);
                    totalSum+=data.getAmount();
                    String stResult = String.valueOf(totalSum);
                    totalIncomeResult.setText(stResult);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
//Recycler
        LinearLayoutManager layoutManagerIncome = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        layoutManagerIncome.setStackFromEnd(true);
        layoutManagerIncome.setReverseLayout(true);
        mRecyclerIncome.setHasFixedSize(true);
        mRecyclerIncome.setLayoutManager(layoutManagerIncome);


        LinearLayoutManager layoutManagerExpense = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        layoutManagerExpense.setStackFromEnd(true);
        layoutManagerExpense.setReverseLayout(true);
        getmRecyclerExpense.setHasFixedSize(true);
        getmRecyclerExpense.setLayoutManager(layoutManagerExpense);


        return myview;
    }


    //floating animation

    private  void ftAnimation(){
        if(isOpen)
        {
            fab_income_btn.startAnimation(FadeClose);
            fab_expense_btn.startAnimation(FadeClose);
            fab_income_btn.setClickable(false);
            fab_expense_btn.setClickable(false);
            fab_income_txt.startAnimation(FadeClose);
            fab_expense_txt.startAnimation(FadeClose);
            fab_income_txt.setClickable(false);
            fab_expense_txt.setClickable(false);
            isOpen=false;
        }
        else{
            fab_income_btn.startAnimation(FadeOpen);
            fab_expense_btn.startAnimation(FadeOpen);
            fab_income_btn.setClickable(true);
            fab_expense_btn.setClickable(true);
            fab_income_txt.startAnimation(FadeOpen);
            fab_expense_txt.startAnimation(FadeOpen);
            fab_income_txt.setClickable(true);
            fab_expense_txt.setClickable(true);
            isOpen=true;
        }



    }

    private void addData(){
        //fab button income...
        fab_income_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                incomeDataInsert();


            }
        });

        fab_expense_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
        expenseDataInsert();
            }
        });

    }

    public void incomeDataInsert(){
        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater=LayoutInflater.from(getActivity());
        View myviewm= inflater.inflate(R.layout.custom_layout_for_insertdatas,null);

        mydialog.setView(myviewm);
      final  AlertDialog dialog = mydialog.create();
      dialog.setCancelable(false);


        EditText edtAmount = myviewm.findViewById(R.id.amount_edt);
        EditText edtType = myviewm.findViewById(R.id.type_edt);
        EditText edtNote = myviewm.findViewById(R.id.note_edt);

        Button btnSave=myviewm.findViewById(R.id.btn_save);
        Button   btnCancel=myviewm.findViewById(R.id.btn_cancel);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String type=edtType.getText().toString().trim();
                String amount=edtAmount.getText().toString().trim();
                String note=edtNote.getText().toString().trim();



                if(TextUtils.isEmpty(amount)){
                    edtAmount.setError("Required Field");
                    return;

                }

                if(TextUtils.isEmpty(type)){
                    edtType.setError("Required Field");
                    return;

                }


                int ouramountint=Integer.parseInt(amount);

                if(TextUtils.isEmpty(note)){
                    edtNote.setError("Required Field");
                    return;

                }
                String id=mIncomeDatabase.push().getKey();
                String mDate = DateFormat.getDateInstance().format(new Date());
                Data data = new Data(ouramountint,type,note,id,mDate);
                mIncomeDatabase.child(id).setValue(data);
                Toast.makeText(getActivity(), "data addedd seucessfully", Toast.LENGTH_SHORT).show();
                ftAnimation();
                dialog.dismiss();
            }
        });

btnCancel.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
ftAnimation();
        dialog.dismiss();
    }
});
dialog.show();

    }
    public void expenseDataInsert(){
        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());

        View myview=inflater.inflate(R.layout.custom_layout_for_insertdatas,null);
        mydialog.setView(myview);
         final   AlertDialog dialog = mydialog.create();
        dialog.setCancelable(false);
        EditText amount = myview.findViewById(R.id.amount_edt);
        EditText type = myview.findViewById(R.id.type_edt);
        EditText note=myview.findViewById(R.id.note_edt);
        Button btnSave=myview.findViewById(R.id.btn_save);
        Button btnCancel = myview.findViewById(R.id.btn_cancel);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String tmAmount = amount.getText().toString().trim();
                String tmType =type.getText().toString().trim();
                String tmNOte = note.getText().toString().trim();

                if(TextUtils.isEmpty(tmAmount))
                {
                    amount.setError("Required Field");
                    return;
                }
                int inamount=Integer.parseInt(tmAmount);

                if(TextUtils.isEmpty(tmType))
                {
                    type.setError("Required Field");
                    return;
                }
                if(TextUtils.isEmpty(tmNOte))
                {
                    note.setError("Required Field");
                    return;
                }

                String id= mExpenceDatabase.push().getKey();
                String mDate= DateFormat.getDateInstance().format(new Date());
                Data data = new Data(inamount,tmType,tmNOte,id,mDate);
                mExpenceDatabase.child(id).setValue(data);
                Toast.makeText(getActivity(), "Data Added", Toast.LENGTH_SHORT).show();
                ftAnimation();
                dialog.dismiss();

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ftAnimation();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void onStart() {
        super.onStart();
//.....................................................................................


// .....................................................................................
    }
    public  static  class  IncomeViewHolder extends  RecyclerView.ViewHolder{

        View mIncomeView;

        public IncomeViewHolder(@NonNull View itemView) {
            super(itemView);
            mIncomeView = itemView;
        }
        public void setmIncomeType(String type){
            TextView mtype = mIncomeView.findViewById(R.id.type_Income_ds);
            mtype.setText(type);

        }

        public void setIncomeAmout(float amount){

            TextView mAmount = mIncomeView.findViewById(R.id.amount_Income_ds);
            String strAmount = String.valueOf(amount);
            mAmount.setText(strAmount);
        }


        public  void setmIncomeDate(String date){

            TextView mDate = mIncomeView.findViewById(R.id.date_Income_ds);
            mDate.setText(date);
        }
    }
}