package com.julio.smartkey.activity;

import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.julio.smartkey.R;
import com.julio.smartkey.events.IConnectListener;
import com.julio.smartkey.service.BluetoothService;

/**
 * Created by tulv2 on 8/15/2016.
 */
public class OnOffControlActivity extends BaseActivity implements View.OnClickListener, IConnectListener {

    private ImageView btnLock, btnUnLock;
    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_off_control);
        btnLock = (ImageView) findViewById(R.id.btn_lock);
        btnUnLock = (ImageView) findViewById(R.id.btn_unlock);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Connect");
        btnUnLock.setOnClickListener(this);
        btnLock.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        device = (BluetoothDevice) getIntent().getExtras().getParcelable("device");
        toolbar.setTitle("Connect to " + device.getName());
        getApplicationContext().bindService(new Intent(getBaseContext(), BluetoothService.class), serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private BluetoothService bluetoothService;
    BluetoothDevice device;
    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            bluetoothService = ((BluetoothService.LocalBinder) service).getService();
            bluetoothService.setConnectListener(OnOffControlActivity.this);
            bluetoothService.connect(device, true);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private byte[] out = new byte[1];

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_lock:
                out[0] = 0;
                bluetoothService.write(out);
                break;
            case R.id.btn_unlock:
                out[0] = 1;
                bluetoothService.write(out);
                break;
            default:
                break;
        }
    }

    @Override
    public void onConnectSuccess() {
        Log.i("TULV2", "Connected!");
        Intent intent = new Intent(this, OnOffControlActivity.class);
        startActivity(intent);
    }

    @Override
    public void readRawData(byte[] bytes) {
        Log.i("TULV2", "read raw data");
    }

    @Override
    public void onToastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bluetoothService != null) {
            bluetoothService.stop();
        }
    }
}
