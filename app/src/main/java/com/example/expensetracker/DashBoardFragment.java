package com.example.expensetracker;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.graphics.Color;
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

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import Model.Data;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

public class DashBoardFragment extends Fragment {

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

    int expensesResult = 0, incomeResult = 0;

    private ArrayList<Data> ourPieData = new ArrayList<>();
    private PieChart expensePieChart, incomePieChart;
    //dashboard income and expense result
    private TextView balance, txtBalance, username, totalPieExpense, totalPieIncome,
                    noIncomePie, noExpensePie;

//firebae

    private FirebaseAuth mAuth;
    private DatabaseReference mIncomeDatabase;
    private DatabaseReference mExpenceDatabase;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myview= inflater.inflate(R.layout.fragment_dash_board, container, false);
        //total income and expense result set
        balance = myview.findViewById(R.id.balance);
        txtBalance = myview.findViewById(R.id.txtBalance);

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

        //pieChart view
        expensePieChart = myview.findViewById(R.id.expensePieChart);
        incomePieChart = myview.findViewById(R.id.incomePieChart);

        totalPieExpense = myview.findViewById(R.id.totalPieExpense);
        totalPieIncome = myview.findViewById(R.id.totalPieIncome);

        username = myview.findViewById(R.id.username);

        noIncomePie = myview.findViewById(R.id.noIncomePie);
        noExpensePie = myview.findViewById(R.id.noExpensePie);

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

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(mAuth.getUid() != null){
                    String myUser = snapshot.child("Users").child(mAuth.getUid())
                            .child("username").getValue(String.class);
                    username.setText("Welcome " + myUser);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        getTotalIncome();
        getTotalExpense();
        getBalance(new Mycallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onCallback(int a, int b) {
                expensesResult = a;
                incomeResult = b;

                if(getContext() != null){
                    if(incomeResult>expensesResult){
                        int cal = incomeResult - expensesResult;
                        balance.setText("Rs. " + cal);
                        balance.setTextColor(ContextCompat.getColor(getContext(), R.color.income_color));
                        txtBalance.setTextColor(ContextCompat.getColor(getContext(), R.color.income_color));
                    }

                    if(expensesResult>incomeResult){
                        int cal = expensesResult - incomeResult;
                        balance.setText("Rs. " + cal);
                        balance.setTextColor(ContextCompat.getColor(getContext(), R.color.expense_color));
                        txtBalance.setTextColor(ContextCompat.getColor(getContext(), R.color.expense_color));
                    }
                }
            }
        });

        return myview;
    }

    private void getBalance(Mycallback mycallback){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int expenseTotal = 0, incomeTotal = 0;
                if(mAuth.getUid() != null){
                    for(DataSnapshot e: snapshot.child("ExpenseData").child(mAuth.getUid())
                            .getChildren()){
                    Data eData = e.getValue(Data.class);
                    if(eData != null){
                        expenseTotal += eData.getAmount();
                    }
                }
                for(DataSnapshot i: snapshot.child("IncomeData").child(mAuth.getUid())
                        .getChildren()){
                    Data iData = i.getValue(Data.class);
                    if(iData != null){
                        incomeTotal += iData.getAmount();
                    }
                }
                if(expenseTotal!=0 || incomeTotal!=0){
                    totalPieExpense.setText("Rs. " + expenseTotal);
                    totalPieIncome.setText("Rs. " + incomeTotal);
                    mycallback.onCallback(expenseTotal, incomeTotal);
                }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getTotalExpense(){
        mExpenceDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ourPieData.clear();
                for (DataSnapshot mysnap:snapshot.getChildren()) {
                    Data data = mysnap.getValue(Data.class);
                    if(data != null){
                        ourPieData.add(data);
                    }
                }
                if(!ourPieData.isEmpty()){
                    expensePieChart.setVisibility(View.VISIBLE);
                    noExpensePie.setVisibility(View.GONE);
                    totalPieExpense.setVisibility(View.VISIBLE);
                    PieData pieData = generatePieChartData(ourPieData);
                    setPieChart(pieData, expensePieChart);
                }else{
                    totalPieExpense.setVisibility(View.GONE);
                    expensePieChart.setVisibility(View.GONE);
                    noExpensePie.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
    }

    private void getTotalIncome(){
        mIncomeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ourPieData.clear();
                for (DataSnapshot mysnap:snapshot.getChildren()) {
                    Data data = mysnap.getValue(Data.class);
                    if(data != null){
                        ourPieData.add(data);
                    }
                }
                if(!ourPieData.isEmpty()){
                    incomePieChart.setVisibility(View.VISIBLE);
                    noIncomePie.setVisibility(View.GONE);
                    totalPieIncome.setVisibility(View.VISIBLE);
                    PieData pieData = generatePieChartData(ourPieData);
                    setPieChart(pieData, incomePieChart);
                }else{
                    incomePieChart.setVisibility(View.GONE);
                    noIncomePie.setVisibility(View.VISIBLE);
                    totalPieIncome.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
    }

    private PieData generatePieChartData(ArrayList<Data> data){

        ArrayList<PieEntry> entries = new ArrayList<>();

        for(Data d: data){
            entries.add(new PieEntry((float)d.getAmount(), d.getType()));
        }

        PieDataSet ds = new PieDataSet(entries, "");
        ds.setColors(ColorTemplate.COLORFUL_COLORS);
        ds.setSliceSpace(2f);
        ds.setValueTextColor(Color.WHITE);
        ds.setValueTextSize(20f);

        return new PieData(ds);
    }

    private void setPieChart(PieData data, PieChart pieChart){
        pieChart.getDescription().setEnabled(false);
        pieChart.setHoleRadius(30f);
        pieChart.setTransparentCircleRadius(40f);
        pieChart.setData(data);
        Legend legend = pieChart.getLegend();
        legend.setEnabled(false);
        pieChart.animateXY(1000, 1000);
        pieChart.setUsePercentValues(true);
        pieChart.setClickable(true);
        pieChart.invalidate();
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

    public interface Mycallback{
        void onCallback(int a, int b);
    }
}