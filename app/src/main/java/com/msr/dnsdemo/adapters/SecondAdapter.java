package com.msr.dnsdemo.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.msr.dnsdemo.R;
import com.msr.dnsdemo.second.ClientScanResult;

import java.util.ArrayList;

/**
 * Created by SANDEEP on 06-05-2017.
 */

public class SecondAdapter extends RecyclerView.Adapter<SecondAdapter.ViewHolder> {
    Context context;
    private ArrayList<ClientScanResult> deviceInfoArrayList;

    public SecondAdapter(ArrayList<ClientScanResult> deviceInfoArrayList, Context context) {
        this.deviceInfoArrayList = deviceInfoArrayList;
        this.context = context;
    }

    public SecondAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SecondAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.connected_devices_row, parent, false));
    }

    public void onBindViewHolder(SecondAdapter.ViewHolder holder, int position) {
        if (position % 2 == 0) {
            holder.device_row_linearLayout.setBackgroundColor(Color.parseColor("#9EB9D4"));
        }
        holder.name.setText((this.deviceInfoArrayList.get(position)).getName());
        holder.ip.setText((this.deviceInfoArrayList.get(position)).getIpAddr() + "-" + deviceInfoArrayList.get(position).getHWAddr());
        holder.setIsRecyclable(false);
    }

    public int getItemCount() {
        return this.deviceInfoArrayList.size();
    }

    public class ViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder implements View.OnClickListener {
        public LinearLayout device_row_linearLayout;
        public TextView ip;
        public TextView name;

        public ViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            this.name = (TextView) v.findViewById(R.id.textView_device_name);
            this.ip = (TextView) v.findViewById(R.id.textView_device_ip);
            this.device_row_linearLayout = (LinearLayout) v.findViewById(R.id.device_row_linearLayout);
        }

        public void onClick(View v) {
        }
    }
}

