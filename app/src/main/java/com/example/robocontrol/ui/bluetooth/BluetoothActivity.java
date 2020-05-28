package com.example.robocontrol.ui.bluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.robocontrol.R;
import com.example.robocontrol.ui.main.MainContract;

import java.util.ArrayList;
import java.util.Set;

import static com.example.robocontrol.utils.ViewUtils.toast;

public class BluetoothActivity extends AppCompatActivity {

    /**
     * This is just a small and simple activity with very few of features, so I decided not to use MVP
     */
    private ListView lstDevices;
    private ImageView ivBack;

    private BluetoothAdapter bluetoothAdapter;
    private ArrayList<String> deviceAddesses = new ArrayList<>();

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Landscape mode only
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        // Fullscreen mode
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        setContentView(R.layout.activity_bluetooth);

        initViews();

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            toast(this, "This device does not support bluetooth");
        } else {
            if (!bluetoothAdapter.isEnabled()) {
                enableBluetooth();
            } else {
                displayPairedDevices();
            }
        }
    }

    private void initViews() {
        // Back button
        ivBack = findViewById(R.id.iv_back);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // List paired devices
        lstDevices = findViewById(R.id.lst_devices);
        lstDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra(MainContract.DEVICE_ADDRESS, deviceAddesses.get(position));
                setResult(MainContract.CONNECT_DEVICE_REQ, intent);
                finish();
            }
        });
    }

    private void enableBluetooth() {
        // This application check bluetooth status at the moment we open the app, so basicly we just have to display paired devices.
    }

    private void displayPairedDevices() {
        Set<BluetoothDevice> bluetoothDevices = bluetoothAdapter.getBondedDevices();
        ArrayList<String> deviceNames = new ArrayList<>();

        for (BluetoothDevice bluetoothDevice : bluetoothDevices) {
            deviceNames.add(bluetoothDevice.getAddress() + " " + bluetoothDevice.getName());
            deviceAddesses.add(bluetoothDevice.getAddress());
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, deviceNames);
        lstDevices.setAdapter(arrayAdapter);
    }

}