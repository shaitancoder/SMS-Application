package com.smsapplication;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by harshita30 on 20/10/16.
 */
public class SMSAdapter extends RecyclerView.Adapter<SMSAdapter.MyViewHolder> {
    private List<SMSData> smsDataList;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView number, content;

        public MyViewHolder(View view) {
            super(view);
            number = (TextView) view.findViewById(R.id.smsNumberText);
            content = (TextView) view.findViewById(R.id.smsContent);
        }
    }
    public SMSAdapter(List<SMSData> smsDatas){
        this.smsDataList = smsDatas;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.msg_list_item, parent, false);

        return new MyViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        SMSData smsData = smsDataList.get(position);
        holder.number.setText(String.valueOf(smsData.getNumber()));
        holder.content.setText(String.valueOf(smsData.getContent()));
    }
    @Override
    public int getItemCount() {
        return smsDataList.size();
    }
}