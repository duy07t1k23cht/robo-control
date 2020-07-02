package com.example.robocontrol.ui.main;

import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import com.example.robocontrol.base.BasePresenter;
import com.example.robocontrol.utils.BluetoothUtils;

import java.util.Set;

import static com.example.robocontrol.utils.CommandUtils.B;
import static com.example.robocontrol.utils.CommandUtils.C;
import static com.example.robocontrol.utils.CommandUtils.E;
import static com.example.robocontrol.utils.CommandUtils.F;
import static com.example.robocontrol.utils.CommandUtils.L;
import static com.example.robocontrol.utils.CommandUtils.M;
import static com.example.robocontrol.utils.CommandUtils.P;
import static com.example.robocontrol.utils.CommandUtils.R;
import static com.example.robocontrol.utils.CommandUtils.S;

/**
 * Created by Duy M. Nguyen on 5/14/2020.
 */
public class MainPresenter extends BasePresenter<MainContract.View> implements MainContract.Presenter {

    private final static int STATE_CONNECTING = 10;
    private final static int STATE_CONNECTED = 11;
    private final static int STATE_CONNECTION_FAILED = 12;
    private final static int STATE_DISCONNECTED = 13;

    private enum ControlMode {
        MODE_1, MODE_2
    }

    private boolean isShowingMoreOption = false;
    public boolean isConnected = false;

    private ControlMode controlMode = ControlMode.MODE_1;

    @Override
    public void setupBluetoothConnectListener() {
        BluetoothUtils.onConnectStatusChangeListener = new BluetoothUtils.OnConnectStatusChangeListener() {
            @Override
            public void onConnectStatusChange(BluetoothUtils.ConnectStatus connectStatus) {
                if (connectStatus == BluetoothUtils.ConnectStatus.STATE_CONNECTED) {
                    updateStatus(STATE_CONNECTED);
                } else if (connectStatus == BluetoothUtils.ConnectStatus.STATE_DISCONNECTED) {
                    updateStatus(STATE_DISCONNECTED);
                } else if (connectStatus == BluetoothUtils.ConnectStatus.STATE_CONNECTION_FAILED) {
                    updateStatus(STATE_CONNECTION_FAILED);
                }
            }
        };
    }

    @Override
    public void checkBluetoothStatus() {
        if (BluetoothUtils.deviceNotSupportBluetooth()) {
            mView.showMessage("This device does not support bluetooth");
            return;
        }

        if (BluetoothUtils.isOff()) {
            mView.enableBluetooth();
        }
    }

    @Override
    public void showHideMoreOption() {
        isShowingMoreOption = !isShowingMoreOption;
        if (isShowingMoreOption) {
            mView.showMoreOption();
        } else {
            mView.hideMoreOption();
        }
    }

    @Override
    public void switchControlMode() {
        if (controlMode == ControlMode.MODE_1) {
            // Mode showing drive and speed button

            // Change to MODE_2
            controlMode = ControlMode.MODE_2;

            // Show buttons for MODE_2
            mView.showLayoutSwap();
        } else {
            // Mode showing swap horizontal and swap vertical button

            // Change to MODE_1
            controlMode = ControlMode.MODE_1;

            // Show buttons for MODE_1
            mView.showLayoutControl();
        }
    }

    @Override
    public void connectToDevice(String deviceAddress) {
        updateStatus(STATE_CONNECTING);
        Set<BluetoothDevice> pairedDevice = BluetoothUtils.getPairedDevices();
        for (BluetoothDevice bluetoothDevice : pairedDevice) {
            if (bluetoothDevice.getAddress().equals(deviceAddress)) {
                BluetoothUtils.connectToDevice(bluetoothDevice);
            }
        }
    }

    @Override
    public void sendMessage(String character) {
        BluetoothUtils.sendMessage(character);
    }

    @Override
    public void disconnect() {
        BluetoothUtils.disconnect();
    }

    @Override
    public void processJoystickMovement(int angle, int strength) {
        if (angle > 0 && angle < 22) {
            sendMessage(R);
        }
        if (angle == 0) {
            sendMessage(S);
        }
        if (angle >= 22 && angle < 67) {
            sendMessage(M);
        }
        if (angle >= 67 && angle < 112) {
            sendMessage(F);
        }
        if (angle >= 112 && angle < 157) {
            sendMessage(C);
        }
        if (angle >= 157 && angle < 202) {
            sendMessage(L);
        }
        if (angle >= 202 && angle < 250) {
            sendMessage(E);
        }
        if (angle >= 250 && angle < 295) {
            sendMessage(B);
        }
        if (angle >= 295 && angle < 330) {
            sendMessage(P);
        }
        if (angle >= 330 && angle < 360) {
            sendMessage(R);
        }
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {

            switch (msg.what) {
                case STATE_CONNECTING:
                    isConnected = false;
                    mView.showMessage("Connecting...");
                    break;
                case STATE_CONNECTED:
                    isConnected = true;
                    mView.showMessage("Connected");
                    break;
                case STATE_CONNECTION_FAILED:
                    isConnected = false;
                    mView.showMessage("Connection failed");
                    break;
                case STATE_DISCONNECTED:
                    mView.showMessage("Disconnected");
                    isConnected = false;
                    break;
            }
            mView.updateBluetoothButton(isConnected);
            return false;
        }
    });

    void updateStatus(int status) {
        Message message = Message.obtain();
        message.what = status;
        handler.sendMessage(message);
    }
}
