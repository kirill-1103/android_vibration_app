package com.example.vibration;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.vibration.adapter.BtConsts;
import com.example.vibration.bluetooth.BtConnection;
import com.example.vibration.service.BtBackgroundService;

public class MainActivity extends AppCompatActivity {

    public final static String APP_NAME = "BluetoothVibro";

    public final static Long MESSAGE_1 = 1L;
    public final static Long MESSAGE_2 = 2L;
    public final static Long MESSAGE_3 = 3L;
    public final static Long MESSAGE_4 = 4L;
    public final static Long MESSAGE_LONG = 0L;


    private Button button1;

    private Button buttonConnect;

    private Button buttonDevices;

    private Button buttonDisconnection;

    private Button buttonListen;

    private Button buttonLong;

    private Button buttonMess1;

    private Button buttonMess2;
    private Button buttonMess3;
    private Button buttonMess4;
    private Button buttonMessN;

    private EditText numText;

    private TextView infoListening;

    private BluetoothAdapter bluetooth;

    private final int REQUEST_ENABLE_BLUETOOTH = 1;

    private BtConnection connection;

    private Intent serviceIntent;

//    private Server server = new Server();

    private final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.FOREGROUND_SERVICE,
            Manifest.permission.WAKE_LOCK
    };

    private SharedPreferences pref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bluetooth = BluetoothAdapter.getDefaultAdapter();


        checkPermissions();
        enableBluetooth();
        init();


        buttonDevices = findViewById(R.id.button_devices);

        buttonConnect = findViewById(R.id.button_connect);

        buttonDisconnection = findViewById(R.id.button_disconnect);

        buttonListen = findViewById(R.id.button_listen);

        setSenders();

        infoListening = findViewById(R.id.listening_info);

        buttonDevices.setOnClickListener((View view) -> {
            if (!checkBluetooth()) return;

            Intent i = new Intent(MainActivity.this, BtListActivity.class);
            startActivity(i);

        });

        buttonConnect.setOnClickListener(v -> {
            if (!checkBluetooth()) return;
            connection.connect();
        });

        buttonDisconnection.setOnClickListener(v -> {
            if (!checkBluetooth()) return;

            Log.i("MY_LOG", "Disconnecting...");
            connection.disconnect();
        });

        buttonListen.setOnClickListener(v -> {
            if (!checkBluetooth()) return;

            Toast.makeText(this, "Listening...", Toast.LENGTH_SHORT).show();
            Intent serviceIntent = new Intent(this, BtBackgroundService.class);
            startService(serviceIntent);


        });


    }

    private void setSenders() {
        buttonMess1 = findViewById(R.id.button_mess1);
        buttonMess2 = findViewById(R.id.button_mess2);
        buttonMess3 = findViewById(R.id.button_mess3);
        buttonMess4 = findViewById(R.id.button_mess4);
        buttonMessN = findViewById(R.id.button_mess_n);
        buttonLong = findViewById(R.id.button_mess_long);

        numText = findViewById(R.id.num);


        buttonMess1.setOnClickListener(v -> {
            if (!checkBluetooth()) return;
            connection.sendMessage(MESSAGE_1);
            disableSenders(2);
        });
        buttonMess2.setOnClickListener(v -> {
            if (!checkBluetooth()) return;
            connection.sendMessage(MESSAGE_2);
            disableSenders(2);
        });
        buttonMess3.setOnClickListener(v -> {
            if (!checkBluetooth()) return;
            connection.sendMessage(MESSAGE_3);
            disableSenders(2);
        });
        buttonMess4.setOnClickListener(v -> {
            if (!checkBluetooth()) return;
            connection.sendMessage(MESSAGE_4);
            disableSenders(2);
        });
        buttonMessN.setOnClickListener(v -> {
            if (!checkBluetooth()) return;

            try {
                long val = Long.valueOf(numText.getText().toString());
                if (val > 0) {
                    if (val > 255) {
                        val = 255;
                    }
                    connection.sendMessage(val);
                    disableSenders(2);
                }
            } catch (NumberFormatException e) {

            }
        });
        buttonLong.setOnClickListener(v -> {
            if (!checkBluetooth()) return;
            connection.sendMessage(MESSAGE_LONG);
            disableSenders(2);
        });
    }

    private boolean checkBluetooth() {
        if (!bluetooth.isEnabled()) {
            Toast.makeText(this, "You should enable bluetooth", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void enableBluetooth() {
        if (!bluetooth.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BLUETOOTH);
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            }
        }
    }

    private void disableSenders(int sec) {
        LinearLayout layout = findViewById(R.id.buttons);
        int childCount = layout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            Button b = (Button) layout.getChildAt(i);
            b.setEnabled(false);
        }
        new Thread(() -> {
            try {
                Thread.sleep(sec * 1000L);
            } catch (InterruptedException e) {

            }
            for (int i = 0; i < childCount; i++) {
                Button b = (Button) layout.getChildAt(i);
                b.post(() -> {
                    b.setEnabled(true);
                });
            }
        }).start();
    }

    private void init() {
        pref = getSharedPreferences(BtConsts.MY_PREF, Context.MODE_PRIVATE);
        Log.d("MY_LOG", "Bt mac:" + pref.getString(BtConsts.MAC_KEY, "no bt selected"));
        connection = new BtConnection(this, this);
    }

    private void checkPermissions() {
        int p1 = ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT);
        if (p1 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, 1);
        }
    }


}