package com.example.expensetracker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import Model.Data;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

public class ExpensesAdapter extends RecyclerView.Adapter<ExpensesAdapter.ViewHolder>{
    Context context;
    FragmentManager fm;
    ArrayList<Data> userArraylist;

    public ExpensesAdapter(Context context, FragmentManager fm) {
        this.context = context;
        this.fm = fm;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_recycler_data, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
      Data data = userArraylist.get(position);
       String amount = String.valueOf(userArraylist.get(position).getAmount());
        holder.amount_txt_expense.setText("Rs. " + amount);
        holder.note_txt_expense.setText(userArraylist.get(position).getNote());
        holder.type_txt_expense.setText(userArraylist.get(position).getType());
        holder.date_txt_expense.setText(userArraylist.get(position).getDate());

        holder.expense_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateData updateData = new UpdateData();
                Bundle bundle = new Bundle();
                bundle.putString("expense_id", userArraylist.get(position).getId());
                updateData.setArguments(bundle);
                if(fm != null){
                    updateData.show(fm, "update data");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return userArraylist.size();
    }

    public void setuserArraylist(ArrayList<Data> userArraylist) {
        this.userArraylist = userArraylist;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView type_txt_expense, note_txt_expense, amount_txt_expense, date_txt_expense;
        private ImageButton expense_setting;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            date_txt_expense = itemView.findViewById(R.id.date_txt_expense);
            type_txt_expense = itemView.findViewById(R.id.type_txt_expense);
            note_txt_expense = itemView.findViewById(R.id.note_txt_expense);
            amount_txt_expense = itemView.findViewById(R.id.amount_txt_expense);
            expense_setting = itemView.findViewById(R.id.expense_settings);
        }
    }
}
