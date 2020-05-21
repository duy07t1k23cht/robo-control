package com.example.robocontrol.ui.main;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.example.robocontrol.R;
import com.example.robocontrol.base.BasePresenter;
import com.example.robocontrol.joystick.JoyStick;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Duy M. Nguyen on 5/14/2020.
 */
public class MainPresenter extends BasePresenter<MainContract.View> implements MainContract.Presenter {

    private final static int STATE_CONNECTING = 10;
    private final static int STATE_CONNECTED = 11;
    private final static int STATE_CONNECTION_FAILED = 12;
    private final static int STATE_DISCONNECTED = 13;

    private BluetoothAdapter bluetoothAdapter;
    private ConnectThread connectThread;

    private JoyStick joyStick;

    private enum ControlMode {
        MODE_1, MODE_2
    }

    private boolean isShowingMoreOption = false;
    public boolean isConnected = false;
    private ControlMode controlMode = ControlMode.MODE_1;

    @Override
    public void checkBluetoothStatus() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            mView.showMessage("This device does not support bluetooth");
            return;
        }

        if (!bluetoothAdapter.isEnabled()) {
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

                int joyStickSize = (int) (Math.min(width, height) * 0.85f);

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
        mView.showMessage("Connecting...");
        Set<BluetoothDevice> pairedDevice = bluetoothAdapter.getBondedDevices();
        for (BluetoothDevice bluetoothDevice : pairedDevice) {
            if (bluetoothDevice.getAddress().equals(deviceAddress)) {
                connectThread = new ConnectThread(bluetoothDevice);
                connectThread.start();
            }
        }
    }

    @Override
    public void sendMessage(String character) {
        if (connectThread != null)
            connectThread.sendCharacter(character);
    }

    @Override
    public void disconnect() {
        if (connectThread != null) {
            connectThread.cancel();
        }
    }

    @Override
    public void processJoystickMovement(MotionEvent motionEvent) {
        joyStick.drawStick(motionEvent);
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN || motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
            int direction = joyStick.get8Direction();
            switch (direction) {
                case JoyStick.STICK_UP:
                    sendMessage("F");
                    break;
                case JoyStick.STICK_UPRIGHT:
                    sendMessage("M");
                    break;
                case JoyStick.STICK_RIGHT:
                    sendMessage("R");
                    break;
                case JoyStick.STICK_DOWNRIGHT:
                    sendMessage("P");
                    break;
                case JoyStick.STICK_DOWN:
                    sendMessage("B");
                    break;
                case JoyStick.STICK_DOWNLEFT:
                    sendMessage("E");
                    break;
                case JoyStick.STICK_LEFT:
                    sendMessage("L");
                    break;
                case JoyStick.STICK_UPLEFT:
                    sendMessage("C");
                    break;

            }
        } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            int direction = joyStick.get8Direction();
            if (direction == JoyStick.STICK_NONE) {
                sendMessage("S");
            }
        }
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket
            // because mmSocket is final.
            BluetoothSocket tmp = null;
            mmDevice = device;

            try {
                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                // MY_UUID is the app's UUID string, also used in the server code.
                Method m = device.getClass().getMethod("createRfcommSocket", int.class);
                tmp = (BluetoothSocket) m.invoke(device, 1);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it otherwise slows down the connection.
            bluetoothAdapter.cancelDiscovery();

            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and return.
                updateStatus(STATE_CONNECTION_FAILED);
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                    closeException.printStackTrace();
                }
                return;
            }

            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.
            updateStatus(STATE_CONNECTED);
        }

        // Closes the client socket and causes the thread to finish.
        public void cancel() {
            updateStatus(STATE_DISCONNECTED);
            try {
                mmSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void sendCharacter(String character) {
            if (mmSocket != null) {
                try {
                    mmSocket.getOutputStream().write(character.toString().getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class SendReceive extends Thread {
        private final BluetoothSocket bluetoothSocket;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        public SendReceive(BluetoothSocket bluetoothSocket) {
            this.bluetoothSocket = bluetoothSocket;
            InputStream tempIn = null;
            OutputStream tempOut = null;

            try {
                tempIn = this.bluetoothSocket.getInputStream();
                tempOut = this.bluetoothSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("__E", e.getMessage());
            }

            inputStream = tempIn;
            outputStream = tempOut;
        }

        @Override
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            while (true) {
                try {
                    bytes = inputStream.read(buffer);
//                    handler
//                            .obtainMessage(STATE_MESSAGE_RECEIVED, bytes, -1, buffer)
//                            .sendToTarget();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("__E", "SendReceive.run()\n" + e.getMessage());
                }
            }
        }

        Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {

//            String character = msg.what;
                return false;
            }
        });
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
