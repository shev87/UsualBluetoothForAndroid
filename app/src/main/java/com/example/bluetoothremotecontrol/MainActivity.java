package com.example.bluetoothremotecontrol;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    static TextView textView;
    Button button;
    EditText editText;
    static TextView textView2;
    ControllerBluetooth controllerBluetooth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);
        textView2 = findViewById(R.id.textView2);
        button = findViewById(R.id.button);
        editText = findViewById(R.id.editText);
        controllerBluetooth = new ControllerBluetooth();
        try {
            controllerBluetooth.init();

            //controllerBluetooth.write("\r\nIt's from my heart!");
        } catch (Exception e) {
            textView.append("\r\n" + e.getMessage());
        }
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String s = editText.getText().toString();
                    if (!s.isEmpty()) {
                        s += "\r\n";
                        controllerBluetooth.write(s);
                        editText.setText("");
                    }
                } catch (IOException e) {
                    textView.append("\r\nProblems with send data " + e.getMessage());
                }
            }
        };
        button.setOnClickListener(onClickListener);
        //textView2.setText(controllerBluetooth.myBluetoothService.s);
    }



}
