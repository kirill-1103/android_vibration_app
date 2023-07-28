package com.example.vibration.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vibration.R;
import com.example.vibration.adapter.BtConsts;

public class BtConnection {
    private final Context context;
    private final SharedPreferences pref;
    private final BluetoothAdapter btAdapter;
    private BluetoothDevice device;

    private ConnectThread connectThread;

    private final Activity activity;

    public BtConnection(Context context, Activity activity) {
        this.context = context;
        pref = context.getSharedPreferences(BtConsts.MY_PREF, Context.MODE_PRIVATE);
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        this.activity =activity;
    }

    public void connect(){
        String mac = pref.getString(BtConsts.MAC_KEY, "");
        if(!btAdapter.isEnabled()){
            Toast.makeText(context, "You should enable bluetooth",Toast.LENGTH_SHORT).show();
            return;
        }else if(mac.isEmpty()){
            Toast.makeText(context, "Device not found",Toast.LENGTH_SHORT).show();
            return;
        }
        device = btAdapter.getRemoteDevice(mac);
        if(device == null){
            Toast.makeText(context, "Device not found",Toast.LENGTH_SHORT).show();
            return;
        }
        connectThread = new ConnectThread(context, btAdapter, device, activity);

        TextView info = activity.findViewById(R.id.connecting_info);
        connectThread.start();
    }

    public void disconnect(){
        if(connectThread!=null){
            connectThread.closeConnection();
        }
    }

    public void sendMessage(Long code){
        try{
            if(connectThread!=null && connectThread.getThread()!=null){
                connectThread.getThread().sendMessage(code);
            }
        }catch (Exception e){
            Log.d("MY_LOG","send error");
        }
    }
}
