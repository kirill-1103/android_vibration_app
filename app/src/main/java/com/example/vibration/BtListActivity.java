package com.example.vibration;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.vibration.adapter.BtAdapter;
import com.example.vibration.adapter.ListItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BtListActivity extends AppCompatActivity {

    private final int BT_REQUEST_PERMISSION = 123;
    private Button buttonBack;

    private Button buttonFind;

    private ListView listView;


    private BtAdapter adapter;

    private BluetoothAdapter btAdapter;

    private List<ListItem> items = new ArrayList<>();

    private boolean isBtPermissionGranted = false;

    private boolean isDiscovery = false;


    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bt_list);

        buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener((View view) -> {
            super.onBackPressed();
        });

        buttonFind = findViewById(R.id.button_find);
        buttonFind.setOnClickListener(v -> {
            if(isDiscovery){
                return;
            }
            isDiscovery = true;
            if(items.stream().noneMatch(it -> it.getType().equals(BtAdapter.TITLE_ITEM_TYPE))){
                ListItem listItem = new ListItem();
                listItem.setType(BtAdapter.TITLE_ITEM_TYPE);
                items.add(listItem);
                adapter.notifyDataSetChanged();
            }

            items.removeIf(it -> it.getType().equals(BtAdapter.DISCOVERY_ITEM_TYPE));

            if(btAdapter.isDiscovering()){
                btAdapter.cancelDiscovery();
            }
            btAdapter.startDiscovery();
            Log.i("MY_LOG","start_search");
        });


        init();
    }

    private void init() {
        listView = findViewById(R.id.listViewBt);


        adapter = new BtAdapter(this, R.layout.bt_list_item, items);
        listView.setAdapter(adapter);
        btAdapter = BluetoothAdapter.getDefaultAdapter();

        getBtPermission();

        getPairedDevices();

        onItemClickListener();
    }

    @SuppressLint("MissingPermission")
    private void onItemClickListener(){
        listView.setOnItemClickListener((parent,view,position,id)->{
            ListItem item = (ListItem) parent.getItemAtPosition(position);
            if(item.getType().equals(BtAdapter.DISCOVERY_ITEM_TYPE)){
                item.getBluetoothDevice().createBond();
            }
        });
    }

    private void getPairedDevices() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {

        }
        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            items.clear();
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceMac = device.getAddress();
                ListItem item = new ListItem(device);
                items.add(item);
            }
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == BT_REQUEST_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                isBtPermissionGranted = true;
            } else {
                Toast.makeText(this, "No permission for location!!", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void getBtPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, BT_REQUEST_PERMISSION);
        } else {
            isBtPermissionGranted = true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter f1 = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        IntentFilter f2 = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        IntentFilter f3 = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(broadcastReceiver, f1);
        registerReceiver(broadcastReceiver, f2);
        registerReceiver(broadcastReceiver, f3);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        @Override
        public void onReceive(Context context, Intent intent) {
            if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                Toast.makeText(context, "Found device name:" + device.getName(), Toast.LENGTH_SHORT).show();
//                device.createBond();
                if(items.contains(new ListItem(device,BtAdapter.DISCOVERY_ITEM_TYPE)) || device.getName() == null){
                    return;
                }
                items.add(new ListItem(device, BtAdapter.DISCOVERY_ITEM_TYPE));
                adapter.notifyDataSetChanged();

            }else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(intent.getAction())){
                isDiscovery = false;
                Toast.makeText(getBaseContext(), "Search finished.", Toast.LENGTH_SHORT).show();
                Log.i("MY_LOG","search has finished");
            }else if(BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(intent.getAction())){
                BluetoothDevice device
                         = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(device.getBondState() == BluetoothDevice.BOND_BONDED){
                    getPairedDevices();
                }
            }
        }
    };

    @SuppressLint("MissingPermission")
    @Override
    protected void onDestroy() {
        super.onDestroy();
        btAdapter.cancelDiscovery();
    }
}