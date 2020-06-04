package com.example.robocontrol.ui.main;

import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.example.robocontrol.base.BasePresenter;
import com.example.robocontrol.joystick.JoyStick;
import com.example.robocontrol.utils.BluetoothUtils;

import java.io.IOException;
import java.util.Set;

/**
 * Created by Duy M. Nguyen on 5/14/2020.
 */
public class MainPresenter extends BasePresenter<MainContract.View> implements MainContract.Presenter {

    private final static int STATE_CONNECTING = 10;
    private final static int STATE_CONNECTED = 11;
    private final static int STATE_CONNECTION_FAILED = 12;
    private final static int STATE_DISCONNECTED = 13;

//    private BluetoothAdapter bluetoothAdapter;
//    private ConnectThread connectThread;

    private JoyStick joyStick;

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
    public void setupJoystick(final ViewGroup viewGroup, final int stickDrawable) {
        joyStick = new JoyStick(viewGroup.getContext(), viewGroup, stickDrawable);
        viewGroup.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int width = viewGroup.getMeasuredWidth();
                int height = viewGroup.getMeasuredHeight();
                viewGroup.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) viewGroup.getLayoutParams();

                params.addRule(RelativeLayout.ALIGN_PARENT_START);
                params.removeRule(RelativeLayout.LEFT_OF);
                params.removeRule(RelativeLayout.START_OF);

                viewGroup.setLayoutParams(params);

                float goldenRatio = 2.8f;

                int joyStickSize = (int) (Math.min(width, height) * 0.65f);

                int stickSize = (int) ((float) joyStickSize / goldenRatio);

                joyStick.setStickSize(stickSize, stickSize);
                joyStick.setLayoutSize(joyStickSize, joyStickSize);
                joyStick.setLayoutAlpha(255);
                joyStick.setStickAlpha(255);
                joyStick.setOffset(stickSize / 2);
                joyStick.setMinimumDistance(50);
            }
        });
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

//    @Override
//    public void processJoystickMovement(MotionEvent motionEvent) {
//        joyStick.drawStick(motionEvent);
//        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN || motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
//            int direction = joyStick.get8Direction();
//            switch (direction) {
//                case JoyStick.STICK_UP:
//                    sendMessage("F");
//                    break;
//                case JoyStick.STICK_UPRIGHT:
//                    sendMessage("M");
//                    break;
//                case JoyStick.STICK_RIGHT:
//                    sendMessage("R");
//                    break;
//                case JoyStick.STICK_DOWNRIGHT:
//                    sendMessage("P");
//                    break;
//                case JoyStick.STICK_DOWN:
//                    sendMessage("B");
//                    break;
//                case JoyStick.STICK_DOWNLEFT:
//                    sendMessage("E");
//                    break;
//                case JoyStick.STICK_LEFT:
//                    sendMessage("L");
//                    break;
//                case JoyStick.STICK_UPLEFT:
//                    sendMessage("C");
//                    break;
//
//            }
//        } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
//            int direction = joyStick.get8Direction();
//            if (direction == JoyStick.STICK_NONE) {
//                sendMessage("S");
//            }
//        }
//    }


    @Override
    public void processJoystickMovement(int angle, int strength) {
        if (angle > 0 && angle < 22) {
            sendMessage("R");
        }
        if (angle == 0) {
            sendMessage("S");
        }
        if (angle >= 22 && angle < 67) {
            sendMessage("M");
        }
        if (angle >= 67 && angle < 112) {
            sendMessage("F");
        }
        if (angle >= 112 && angle < 157) {
            sendMessage("C");
        }
        if (angle >= 157 && angle < 202) {
            sendMessage("L");
        }
        if (angle >= 202 && angle < 250) {
            sendMessage("E");
        }
        if (angle >= 250 && angle < 295) {
            sendMessage("B");
        }
        if (angle >= 295 && angle < 330) {
            sendMessage("P");
        }
        if (angle >= 330 && angle < 360) {
            sendMessage("R");
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
