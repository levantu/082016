package com.julio.smartkey.holder;

import android.view.View;
import android.widget.TextView;

import com.julio.smartkey.R;

/**
 * Created by tulv2 on 8/11/2016.
 */
public class DevicesHolder extends BaseHolder {
    public TextView txtNameDevice, txtMacAdress;

    public DevicesHolder(View itemView) {
        super(itemView);
        txtNameDevice = (TextView) itemView.findViewById(R.id.txt_name_device);
        txtMacAdress = (TextView) itemView.findViewById(R.id.txt_mac_address);
    }
}
