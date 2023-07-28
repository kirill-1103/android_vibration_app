package com.example.vibration.bluetooth;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import com.example.vibration.MainActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class RecieveThread extends Thread{
    private BluetoothSocket socket;
    private InputStream input;
    private OutputStream outputStream;

    private Vibrator vibrator;

    private Context context;


    public RecieveThread(BluetoothSocket socket, Context context){
        this.socket = socket;
        try{
            input = this.socket.getInputStream();
        }catch (IOException e){
            Log.e("MY_LOG","Input stream failed");
        }
        try{
            outputStream = socket.getOutputStream();
        }catch (IOException e){
            Log.e("MY_LOG","Output stream failed");
        }
        this.context = context;
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    public RecieveThread(BluetoothSocket socket, Context context, Vibrator vibrator){
        this(socket,context);
        this.vibrator = vibrator;
    }
    @Override
    public void run() {
        while(true){
            try{
                int code = input.read();
                Log.d("MY_LOG","Received message: " + code);
                VibrationEffect effect;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    if(code != MainActivity.MESSAGE_LONG){
                        effect = VibrationEffect.createOneShot(250,VibrationEffect.DEFAULT_AMPLITUDE);

                        vibrator.cancel();
                        for(int i = 0 ;i<code;i++){
                            vibrator.vibrate(effect);
                            Thread.sleep(500);
                        }
                    }else{
                        Log.i("MY_LOG","long signal");
                        effect = VibrationEffect.createOneShot(1500,VibrationEffect.DEFAULT_AMPLITUDE);
                        vibrator.cancel();
                        vibrator.vibrate(effect);
                    }
                }

            }catch (Exception e){
                break;
            }
        }
    }

    public void sendMessage(long code){
        if(outputStream != null){
            Log.i("MY_LOG","Sending code: "+code);
            try {
                outputStream.write((int) code);
            } catch (IOException e) {
                Log.i("MY_LOG","send error");
            }
        }
    }
}
