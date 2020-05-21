package com.example.robocontrol.navigation;

import android.app.Activity;
import android.content.Intent;

import com.example.robocontrol.ui.bluetooth.BluetoothActivity;
import com.example.robocontrol.ui.main.MainContract;

/**
 * Created by Duy M. Nguyen on 5/21/2020.
 */
public class Navigation {

    public static void toBluetoothConnecting(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, BluetoothActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void toSpeechControl(Activity activity, int requestCode) {
        
    }
}
