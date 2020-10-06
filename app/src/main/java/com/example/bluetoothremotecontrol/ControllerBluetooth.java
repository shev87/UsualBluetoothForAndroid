package com.example.bluetoothremotecontrol;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.ParcelUuid;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ControllerBluetooth {
    private OutputStream outputStream;
    private InputStream inStream;
    private TextView textView;
    private TextView textView2;
    private BluetoothAdapter bluetooth;
    private Set<BluetoothDevice> pairedDevices;
    public BluetoothSocket socket;
    public MyBluetoothService myBluetoothService;

    public InputStream getInStream() {
        return inStream;
    }

    public void init() {
        if (bluetooth != null) {
            if (bluetooth.isEnabled()) {
                if (pairedDevices.size() > 0) {
                    Object[] devices = pairedDevices.toArray();
                    BluetoothDevice device = (BluetoothDevice) devices[0];
                    MainActivity.textView.append("\r\n" + "Our device = " + device.getName());
                    device.fetchUuidsWithSdp();
                    ParcelUuid[] uuids = device.getUuids();
                    textView.append("\r\nUUID our device:");
                    for (ParcelUuid u : uuids) {
                        textView.append("\r\n\t" + u.getUuid());
                    }
                    textView.append("\r\n");
                    try {
                        socket = device.createRfcommSocketToServiceRecord(uuids[0].getUuid());
                        socket.connect();
                        outputStream = socket.getOutputStream();
                        inStream = socket.getInputStream();
                        textView.append("\r\n" + "Connection is OK!!!");

                        myBluetoothService = new MyBluetoothService(socket);

                    } catch (Exception e) {
                        MainActivity.textView.append("\r\n" + e.getMessage() + e.getCause());
                    }
                } else {
                    Log.e("error", "No appropriate paired devices.");
                }
            } else {
                Log.e("error", "Bluetooth is disabled.");
            }
        }
    }

    public void write(String s) throws IOException {
        synchronized (this) {
            outputStream.write(s.getBytes());
        }

    }

//    public void run() {
//        final int BUFFER_SIZE = 1024;
//        byte[] buffer = new byte[BUFFER_SIZE];
//        int bytes = 0;
//        int b = BUFFER_SIZE;
//
//        while (true) {
//            try {
//                bytes = inStream.read(buffer, bytes, BUFFER_SIZE - bytes);
//                if (bytes > 0){
//                    String s = new String(buffer);
//                    textView2.setText(s);
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    private void getBluetooth() {

        if (bluetooth != null) textView.setText("We have bluetooth in smartphone!!!");
        if (!bluetooth.isEnabled()) {
            // Bluetooth выключен. Предложим пользователю включить его.
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            new MainActivity().startActivityForResult(enableBtIntent, 1);
        }
        textView.append("\r\nState is " + bluetooth.getState() + ". 12 means connected!");
        String myDeviceAddress = bluetooth.getAddress();
        String myDeviceName = bluetooth.getName();
        textView.append("\r\nOur smartphone:");
        textView.append("\r\n\tAddress: " + myDeviceName + "\r\n\tDevice name" + myDeviceAddress);
    }

    private void searchPairedDevices() {
        pairedDevices = bluetooth.getBondedDevices();
        // Если список спаренных устройств не пуст
        textView.append("\r\nPaired devices:");
        if (pairedDevices.size() > 0) {
            // проходимся в цикле по этому списку
            for (BluetoothDevice device : pairedDevices) {
                String s = device.getName() + " = " + device.getAddress();
                textView.append("\r\n\t" + s);
            }
        }
        textView.append("\r\n" + "number of paired devices is " + pairedDevices.size());
    }

    public ControllerBluetooth() {
        textView = MainActivity.textView;
        textView2 = MainActivity.textView2;
        bluetooth = BluetoothAdapter.getDefaultAdapter();
        getBluetooth();
        searchPairedDevices();
    }
}
