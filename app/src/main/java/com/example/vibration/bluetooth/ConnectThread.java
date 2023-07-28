package com.example.vibration.bluetooth;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.example.vibration.R;

import java.io.IOException;

public class ConnectThread extends Thread{
    private Context context;
    private BluetoothAdapter btAdapter;
    private BluetoothDevice device;
    private BluetoothSocket socket;

    private Activity activity;

    private TextView info;

    private Button buttonConnection;

    private Button buttonDisconnection;

    private RecieveThread thread;

    public static final String UUID = "0000000-0000-1000-8000-00805F9B34FB";
    @SuppressLint("MissingPermission")
    public ConnectThread(Context context, BluetoothAdapter btAdapter, BluetoothDevice device, Activity activity){
        this.context = context;
        this.btAdapter = btAdapter;
        this.device = device;
        try{
            socket = device.createInsecureRfcommSocketToServiceRecord(java.util.UUID.fromString(UUID));
        }catch (IOException e){
            Log.d("MY_LOG","ERROR WHEN CREATE SOCKET: "+e.getMessage());
        }
        this.activity =activity;
        this.info = activity.findViewById(R.id.connecting_info);
        this.buttonConnection = activity.findViewById(R.id.button_connect);
        this.buttonDisconnection = activity.findViewById(R.id.button_disconnect);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void run() {
        if(btAdapter.isDiscovering()){
            btAdapter.cancelDiscovery();
        }
        if(socket.isConnected()){
            closeConnection();
        }
        try {
            info.post(()->{
                info.setText("Connecting...");
            });
            buttonConnection.post(()->{
                buttonConnection.setEnabled(false);
            });

            socket.connect();

            thread = new RecieveThread(socket,context);
            thread.start();

            Log.i("MY_LOG","Connection to device "+ device.getName() + " with mac: " + device.getAddress() + " enabled.");

            info.post(()->{
                info.setText("Connection success!");
            });

            buttonDisconnection.post(()->{
                buttonDisconnection.setEnabled(true);
            });
        }catch (IOException e){
            info.post(()->{
               info.setText("Connection failed :(");
            });
            Log.e("MY_LOG","ERROR CONNECTION:"+e.getMessage());
            closeConnection();
            buttonConnection.post(()->{
                buttonConnection.setEnabled(true);
            });
            buttonDisconnection.post(()->{
                buttonDisconnection.setEnabled(false);
            });
        }
    }

    public RecieveThread getThread() {
        return thread;
    }

    @SuppressLint("MissingPermission")
    public void closeConnection(){
        try{
            socket.close();
            Log.i("MY_LOG","Connection to device "+ device.getName() + " with mac: " + device.getAddress() + " disabled.");
            buttonConnection.post(()->{
                buttonConnection.setEnabled(true);
            });
            buttonDisconnection.post(()->{
                buttonDisconnection.setEnabled(false);
            });

        }catch (IOException e1){
            e1.printStackTrace();
            Log.i("MY_TAG","Не удалось закрыть соединение");
        }
    }

}
