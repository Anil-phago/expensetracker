package com.example.expensetracker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import java.text.DateFormat;
import java.util.Date;

import Model.Data;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class UpdateData extends DialogFragment{

    //FireBase Database
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = database.getReference();

    private EditText amount_edit, note_edit, type_edit;
    private Button btn_update, btn_delete;
    private Data data;
    private String expenseId, incomeId;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.update_data_item, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setView(view)
                .setTitle("Update Information");
        initView(view);



        if(getArguments() != null){
            expenseId = getArguments().getString("expense_id");
            if(expenseId != null){
                getIndividualExpenseFromFirebase(expenseId, new MyCallback() {
                    @Override
                    public void onCallBack(Data d) {
                        data = d;
                    }
                });
            }

            incomeId = getArguments().getString("income_id");
            if(incomeId != null){
                getIndividualIncomeFromFirebase(incomeId, new MyCallback(){
                    @Override
                    public void onCallBack(Data d) {
                        data = d;
                    }
                });
            }
        }

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(expenseId != null){
                    AlertDialog.Builder expenseTerminator = new AlertDialog.Builder(getContext())
                            .setTitle("Want To Delete " + data.getType() + "?")
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //should be empty
                                }
                            }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //Delete From Firebase
                                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                            if(mAuth.getUid() != null){
                                                for(DataSnapshot result : snapshot.child("ExpenseData").child(mAuth.getUid()).getChildren()){
                                                    String myKey = result.getKey();
                                                    if(myKey != null && myKey.equals(expenseId)){
                                                        result.getRef().removeValue();
                                                        dismiss();
                                                    }
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                        }
                                    });
                                }
                            });
                    expenseTerminator.create().show();
                }

                if(incomeId != null){
                    AlertDialog.Builder incomeTerminator = new AlertDialog.Builder(getContext())
                            .setTitle("Want To Delete " + data.getType() + "?")
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //should be empty
                                }
                            }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //Delete From Firebase
                                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                            if(mAuth.getUid() != null){
                                                for(DataSnapshot result : snapshot.child("IncomeData").child(mAuth.getUid()).getChildren()){
                                                    String myKey = result.getKey();
                                                    if(myKey != null && myKey.equals(incomeId)){
                                                        result.getRef().removeValue();
                                                        dismiss();
                                                    }
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                        }
                                    });
                                }
                            });
                    incomeTerminator.create().show();
                }
            }
        });

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mAuth.getUid() != null){
                    if(expenseId != null){
                        if(note_edit.getText().toString().isEmpty() || amount_edit.getText().toString().isEmpty() || type_edit.getText().toString().isEmpty()){
                            Toast.makeText(getContext(), "Fields cannot be empty", Toast.LENGTH_SHORT).show();
                        } else {
                            int changedAmount = Integer.parseInt(amount_edit.getText().toString());
                            String changedType = type_edit.getText().toString();
                            String changedNote = note_edit.getText().toString();
                            String mDate = DateFormat.getDateInstance().format(new Date());
                            Data newData = new Data(changedAmount, changedType, changedNote, mDate);
                            databaseReference.child("ExpenseData").child(mAuth.getUid()).child(expenseId).setValue(newData);
                            Toast.makeText(getContext(), "Data Updated Successfully", Toast.LENGTH_SHORT).show();
                            dismiss();
                        }
                    }

                    if(incomeId != null){
                        if(note_edit.getText().toString().isEmpty() || amount_edit.getText().toString().isEmpty() || type_edit.getText().toString().isEmpty()){
                            Toast.makeText(getContext(), "Fields cannot be empty", Toast.LENGTH_SHORT).show();
                        } else {
                            int changedAmount = Integer.parseInt(amount_edit.getText().toString());
                            String changedType = type_edit.getText().toString();
                            String changedNote = note_edit.getText().toString();
                            String mDate = DateFormat.getDateInstance().format(new Date());
                            Data newData = new Data(changedAmount, changedType, changedNote, mDate);
                            databaseReference.child("IncomeData").child(mAuth.getUid()).child(incomeId).setValue(newData);
                            Toast.makeText(getContext(), "Data Updated Successfully", Toast.LENGTH_SHORT).show();
                            dismiss();
                        }
                    }
                }
            }
        });

        return builder.create();
    }

    private void getIndividualIncomeFromFirebase(String incomeId, MyCallback callback){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(mAuth.getUid() != null){
                    Data myData;
                    myData = snapshot.child("IncomeData").child(mAuth.getUid()).child(incomeId).getValue(Data.class);
                    if(myData != null){
                        amount_edit.setText(String.valueOf(myData.getAmount()));
                        note_edit.setText(myData.getNote());
                        type_edit.setText(myData.getType());
                        callback.onCallBack(myData);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getIndividualExpenseFromFirebase(String expenseId, MyCallback callback){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(mAuth.getUid() != null){
                   Data myData;
                   myData = snapshot.child("ExpenseData").child(mAuth.getUid()).child(expenseId).getValue(Data.class);
                   if(myData != null){
                       amount_edit.setText(String.valueOf(myData.getAmount()));
                       note_edit.setText(myData.getNote());
                       type_edit.setText(myData.getType());
                       callback.onCallBack(myData);
                   }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initView(View view){
        amount_edit = view.findViewById(R.id.amount_edt);
        note_edit = view.findViewById(R.id.note_edt);
        type_edit = view.findViewById(R.id.type_edt);
        btn_delete = view.findViewById(R.id.btn_delete);
        btn_update = view.findViewById(R.id.btn_update);
    }

    private interface  MyCallback{
        void onCallBack(Data d);
    }
}
