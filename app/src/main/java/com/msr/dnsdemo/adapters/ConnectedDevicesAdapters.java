package com.msr.dnsdemo.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.msr.dnsdemo.R;
import com.msr.dnsdemo.model.DeviceInfo;

import java.util.ArrayList;

public class ConnectedDevicesAdapters extends Adapter<ConnectedDevicesAdapters.ViewHolder> {
    Context context;
    private ArrayList<DeviceInfo> deviceInfoArrayList;

    public ConnectedDevicesAdapters(ArrayList<DeviceInfo> deviceInfoArrayList, Context context) {
        this.deviceInfoArrayList = deviceInfoArrayList;
        this.context = context;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.connected_devices_row, parent, false));
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        if (position % 2 == 0) {
            holder.device_row_linearLayout.setBackgroundColor(Color.parseColor("#9EB9D4"));
        }
        holder.name.setText(((DeviceInfo) this.deviceInfoArrayList.get(position)).getName());
        holder.ip.setText(((DeviceInfo) this.deviceInfoArrayList.get(position)).getIP());
        holder.setIsRecyclable(false);
    }

    public int getItemCount() {
        return this.deviceInfoArrayList.size();
    }

    public class ViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder implements OnClickListener {
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
