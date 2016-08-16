package com.julio.smartkey.adapter;

import android.bluetooth.BluetoothDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.julio.smartkey.R;
import com.julio.smartkey.events.OnItemRecyclerViewClick;
import com.julio.smartkey.holder.DevicesHolder;

import java.util.List;

/**
 * Created by tulv2 on 8/11/2016.
 */
public class DeviceAdapter extends BaseAdapter<DevicesHolder> {

    private List<BluetoothDevice> devices;

    public DeviceAdapter(List<BluetoothDevice> devices) {
        this.devices = devices;
    }

    @Override
    public int getItemCount() {
        return devices == null ? 0 : devices.size();
    }

    @Override
    public DevicesHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.holder_device, parent, false);
        return new DevicesHolder(view);
    }

    @Override
    public void onBindViewHolder(DevicesHolder holder, final int position) {
        if (devices != null) {
            BluetoothDevice device = devices.get(position);
            holder.txtMacAdress.setText(device.getAddress());
            holder.txtNameDevice.setText(device.getName());
            if (onItemRecyclerViewClick != null) {
                holder.getItemView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemRecyclerViewClick.onItemClick(position);
                    }
                });
            }
        }
    }

    private OnItemRecyclerViewClick onItemRecyclerViewClick;

    public void setOnItemRecyclerViewClick(OnItemRecyclerViewClick onItemRecyclerViewClick) {
        this.onItemRecyclerViewClick = onItemRecyclerViewClick;
    }
}