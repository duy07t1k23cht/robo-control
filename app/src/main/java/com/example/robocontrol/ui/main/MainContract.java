package com.example.robocontrol.ui.main;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.DrawableRes;

import com.example.robocontrol.base.BaseView;

/**
 * Created by Duy M. Nguyen on 5/14/2020.
 */
public class MainContract {

    public final static int CONNECT_DEVICE_REQ = 123;
    public final static int ENABLE_BLUETOOTH_REQ = 456;

    public final static String DEVICE_ADDRESS = "DeviceAddress";

    interface View extends BaseView {
        void showMessage(String message);

        void enableBluetooth();

        void showMoreOption();

        void hideMoreOption();

        void showLayoutControl();

        void showLayoutSwap();

        void disableButtons(android.view.View view);

        void enableButtons(android.view.View view);

        void updateBluetoothButton(boolean isConnect);

        void connectBluetooth();
    }

    interface Presenter {
        void setupBluetoothConnectListener();

        void checkBluetoothStatus();

        void showHideMoreOption();

        void switchControlMode();

        void connectToDevice(String deviceAddress);

        void sendMessage(String character);

        void disconnect();

        void processJoystickMovement(int angle, int strength);
    }
}
