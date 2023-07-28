package com.example.vibration.bluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Vibrator;
import android.util.Log;

import com.example.vibration.MainActivity;

import java.io.IOException;
import java.util.UUID;

public class Server extends Thread{

    BluetoothServerSocket serverSocket;

    private RecieveThread recieveThread;

    private BluetoothSocket socket;

    private Context context;

    private Vibrator vibrator;



    @SuppressLint("MissingPermission")
    public Server(Context context, Vibrator vibrator){
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        try{
            serverSocket = adapter.listenUsingRfcommWithServiceRecord(MainActivity.APP_NAME, UUID.fromString(ConnectThread.UUID));
        }catch (IOException e){
            e.printStackTrace();
        }
        this.context = context;
        this.vibrator = vibrator;
    }

    @Override
    public void run() {
        Log.i("MY_LOG","start listening...");
        socket = null;
        while(true){
            try{
                socket = serverSocket.accept();
            }catch (IOException e){
                Log.e("MY_LOG","serverSocket error:"+e.getMessage());
                break;
            }
            if(socket!=null){
                recieveThread = new RecieveThread(socket,context,vibrator);
                recieveThread.start();

                try{
                    serverSocket.close();
                }catch (IOException e){
                    Log.e("MY_LOG","serverSocket error:"+e.getMessage());
                }
                break;
            }
            if(Thread.currentThread().isInterrupted()){
                destruct();
                break;
            }
        }
    }
    private void destruct(){
        recieveThread.destroy();
        try {
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void stopSocket(){
        if(socket!=null){
            try {
                socket.close();
                Log.i("MY_LOG","socket has been closed");
                socket = null;
            } catch (IOException e) {
                Log.e("MY_LOG","error when close socket");
            }
        }
        if(serverSocket != null){
            try {
                Log.i("MY_LOG","server socket has been closed");
                serverSocket.close();
            } catch (IOException e) {
                Log.e("MY_LOG","error when close socket");
            }
        }
    }
}
