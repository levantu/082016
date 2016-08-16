package com.julio.smartkey.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.julio.smartkey.R;
import com.julio.smartkey.adapter.DeviceAdapter;
import com.julio.smartkey.events.OnItemRecyclerViewClick;
import com.julio.smartkey.service.BluetoothService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by tulv2 on 8/5/2016.
 */
public class CreateAccountSuccessActivity extends BaseActivity implements View.OnClickListener, OnItemRecyclerViewClick {

    private final int REQUEST_ENABLE_BT = 100;
    private ImageButton btnDetect;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning = false;
    private Handler mHandler;
    private RecyclerView recyclerview;
    private DeviceAdapter adapter;
    private List<BluetoothDevice> data;
    private Toolbar toolbar;
    private ProgressBar progressBar;
    private TextView txtState;
    private boolean isConnected = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account_success);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        btnDetect = (ImageButton) findViewById(R.id.btn_detect);
        txtState = (TextView) findViewById(R.id.state);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        recyclerview = (RecyclerView) findViewById(R.id.recyclerview);
        data = new ArrayList<>();
        adapter = new DeviceAdapter(data);
        adapter.setOnItemRecyclerViewClick(this);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        recyclerview.setAdapter(adapter);
        btnDetect.setOnClickListener(this);
        toolbar.setTitle("Smart key 2016");
    }

    @Override
    protected void onStart() {
        super.onStart();
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        IntentFilter filter1 = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        IntentFilter filter2 = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(receiver, filter);
        registerReceiver(receiver, filter1);
        registerReceiver(receiver, filter2);
        getApplicationContext().bindService(new Intent(getBaseContext(), BluetoothService.class), connection, Context.BIND_AUTO_CREATE);
    }

    private BluetoothService bluetoothService;
    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            bluetoothService = ((BluetoothService.LocalBinder) service).getService();
            notifyServiceConnected();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            notifyServiceDisConnected();
        }
    };


    protected void notifyServiceConnected() {
        isConnected = true;
//        btnConnect.setVisibility(View.VISIBLE);
    }

    protected void notifyServiceDisConnected() {
        bluetoothService.setConnectListener(null);
        isConnected = false;
//        btnConnect.setVisibility(View.INVISIBLE);
    }

    private static final int TIME_SCAN = 10000;//10s

    private void scanDevice1(boolean enable) {
        if (enable) {
            data.clear();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    txtState.setText("Stop!");
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallBack);
                }
            }, TIME_SCAN);
            mScanning = true;
            txtState.setText("Scanning...");
            mBluetoothAdapter.startLeScan(mLeScanCallBack);
        } else {
            mScanning = false;
            txtState.setText("Stop!");
            mBluetoothAdapter.stopLeScan(mLeScanCallBack);
        }
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallBack = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    data.add(device);
                    adapter.notifyDataSetChanged();
                }
            });
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        if (bluetoothService != null) {
            bluetoothService.stop();
        }
        //if (isConnected) {
        //unbindService(connection);
        //}
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_detect:
                data.clear();
                adapter.notifyDataSetChanged();
                if (mBluetoothAdapter == null) {
//                     Device does not support Bluetooth
                } else {
                    enableBluetooth();
                }
//                scanDevice(true);
                break;
            default:
                break;
        }
    }

    private void enableBluetooth() {
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            getPairedDevices();
        }
    }

    private void enableDiscovering() {
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverableIntent);
    }

    // Querying paired devices
    private void getPairedDevices() {
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice item : pairedDevices) {
                //data.add(item);
                //adapter.notifyDataSetChanged();
                Log.i("TULV2", item.getName() + " - " + item.getAddress());
            }
        }
        Log.i("TULV2", "----------------------------------------------------");
        mBluetoothAdapter.startDiscovery();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                    // Enable success
                    getPairedDevices();
//                    scanDevice(true);
                }
                break;
            default:
                break;
        }
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice item = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.i("TULV2", item.getName() + " - " + item.getAddress());
                data.add(item);
                adapter.notifyDataSetChanged();
            }
            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                progressBar.setVisibility(View.GONE);
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                progressBar.setVisibility(View.VISIBLE);
            }
        }
    };


    @Override
    public void onItemClick(int position) {
        // Thuc hien connect
        BluetoothDevice device = data.get(position);
        Intent intent = new Intent(this, OnOffControlActivity.class);
        intent.putExtra("device", device);
        startActivity(intent);
    }
}
