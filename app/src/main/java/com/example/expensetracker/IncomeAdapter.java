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

public class IncomeAdapter extends RecyclerView.Adapter<IncomeAdapter.ViewHolder>{
    Context context;
    ArrayList<Data> datas = new ArrayList<>();
    FragmentManager fm;

    public IncomeAdapter(Context context, FragmentManager fm) {
        this.context = context;
        this.fm = fm;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.income_recycler_data, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        String amount = String.valueOf(datas.get(position).getAmount());
        holder.amount_txt_income.setText("Rs. " + amount);
        holder.note_txt_income.setText(datas.get(position).getNote());
        holder.type_txt_income.setText(datas.get(position).getType());
        holder.date_txt_income.setText(datas.get(position).getDate());

        holder.income_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateData updateData = new UpdateData();
                Bundle bundle = new Bundle();
                bundle.putString("income_id", datas.get(position).getId());
                updateData.setArguments(bundle);
                if(fm != null){
                    updateData.show(fm, "new one");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public void setDatas(ArrayList<Data> datas) {
        this.datas = datas;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView type_txt_income, note_txt_income, amount_txt_income, date_txt_income;
        private ImageButton income_setting;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            date_txt_income = itemView.findViewById(R.id.date_txt_income);
            type_txt_income= itemView.findViewById(R.id.type_txt_income);
            note_txt_income = itemView.findViewById(R.id.note_txt_income);
            amount_txt_income = itemView.findViewById(R.id.amount_txt_income);
            income_setting = itemView.findViewById(R.id.income_settings);
        }
    }
}
