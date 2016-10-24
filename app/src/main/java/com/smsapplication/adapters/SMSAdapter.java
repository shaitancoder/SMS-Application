package com.smsapplication.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.smsapplication.R;
import com.smsapplication.models.SMSData;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by harshita30 on 20/10/16.
 */
public class SMSAdapter extends RecyclerView.Adapter<SMSAdapter.MyViewHolder> {
    private ArrayList<SMSData> smsDataList;
    Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView textView1,textView2,textView3;


        public MyViewHolder(RelativeLayout v) {
            super(v);
            textView1= (TextView) v.findViewById(R.id.address);
            textView2= (TextView) v.findViewById(R.id.date);
            textView3= (TextView) v.findViewById(R.id.body);
        }
    }
    public SMSAdapter(ArrayList<SMSData> data) {
        smsDataList = data;
        notifyDataSetChanged();
    }

    @Override
    public SMSAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                    int viewType)
    {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.msg_list_item, parent, false);
        MyViewHolder vh = new MyViewHolder((RelativeLayout)view);
        return vh;
    }
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        SMSData smsData = smsDataList.get(position);
        holder.textView1.setText(smsData.address);
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy   hh:mm");

        long milliSeconds= Long.parseLong(smsData.date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);

        holder.textView2.setText(formatter.format(calendar.getTime()));
        holder.textView3.setText(smsData.body);

    }
    @Override
    public int getItemCount() {
        return smsDataList.size();
    }

}