package com.example.vibration.adapter;

import android.bluetooth.BluetoothDevice;

import java.util.Objects;

public class ListItem {
    private BluetoothDevice bluetoothDevice;

    private String type = BtAdapter.DEF_ITEM_TYPE;

    public String getType() {
        return type;
    }

    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    public void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
    }

    public ListItem(BluetoothDevice bluetoothDevice, String type) {
        this.type = type;
        this.bluetoothDevice =bluetoothDevice;
    }

    public ListItem(BluetoothDevice device){
        this.bluetoothDevice = device;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ListItem() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ListItem listItem = (ListItem) o;
        return bluetoothDevice.equals(listItem.bluetoothDevice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bluetoothDevice);
    }
}
