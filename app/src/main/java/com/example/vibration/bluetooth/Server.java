package com.example.vibration.bluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import com.example.vibration.MainActivity;
import com.example.vibration.R;

import java.io.IOException;
import java.net.Socket;
import java.util.UUID;

public class Server extends Thread{

    BluetoothServerSocket serverSocket;

    private RecieveThread recieveThread;

    private BluetoothSocket socket;

    private Context context;


    @SuppressLint("MissingPermission")
    public Server(Context context){
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        try{
            serverSocket = adapter.listenUsingRfcommWithServiceRecord(MainActivity.APP_NAME, UUID.fromString(ConnectThread.UUID));
        }catch (IOException e){
            e.printStackTrace();
        }
        this.context = context;
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
                recieveThread = new RecieveThread(socket,context);
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
}
