package com.example.robocontrol.utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * Created by Duy M. Nguyen on 5/23/2020.
 */
public class BluetoothUtils {

    public enum ConnectStatus {
        STATE_CONNECTED,
        STATE_DISCONNECTED,
        STATE_CONNECTION_FAILED
    }

    public interface OnConnectStatusChangeListener {
        void onConnectStatusChange(ConnectStatus connectStatus);
    }

    /**
     * A background thread where execute tasks like connecting with other device, disconnecting, send messages,...
     */
    public static ConnectThread connectThread;

    /**
     * The name says everything
     */
    public static OnConnectStatusChangeListener onConnectStatusChangeListener;

    private static BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    /**
     * @param bluetoothDevice: Deivce we want to connect to.
     * This method try to connect with a bluetooth device which is passed as paramater.
     */
    public static void connectToDevice(BluetoothDevice bluetoothDevice) {
        connectThread = new ConnectThread(bluetoothDevice);
        connectThread.start();
    }

    /**
     * Disconnect with current device
     */
    public static void disconnect() {
        if (connectThread != null) {
            connectThread.cancel();
        }
    }

    /**
     * @return true if the device does not suppoort bluetooth
     * Android emulator for example
     */
    public static boolean deviceNotSupportBluetooth() {
        return bluetoothAdapter == null;
    }

    /**
     * @return true if the device does suppoort bluetooth
     */
    public static boolean deviceSupportBluetooth() {
        return bluetoothAdapter != null;
    }

    /**
     * @return true if bluetooth is off at the moment
     */
    public static boolean isOff() {
        return !bluetoothAdapter.isEnabled();
    }

    /**
     * @return true if bluetooth is on at the moment
     */
    public static boolean isOn() {
        return bluetoothAdapter.isEnabled();
    }

    /**
     * @return list of paired devices
     */
    public static Set<BluetoothDevice> getPairedDevices() {
        return bluetoothAdapter.getBondedDevices();
    }

    /**
     * This method try to send a message of type String to the connected device
     * @param message: Message that we want to send
     */
    public static void sendMessage(String message) {
        if (connectThread != null)
            connectThread.sendCharacter(message);
    }

    /**
     * Class for some background tasks like connecting, disconnecting, send message
     */
    private static class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket
            // because mmSocket is final.
            BluetoothSocket tmp = null;

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

        @Override
        public void run() {
            // Cancel discovery because it otherwise slows down the connection.
            bluetoothAdapter.cancelDiscovery();

            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and return.
                onConnectStatusChangeListener.onConnectStatusChange(ConnectStatus.STATE_CONNECTION_FAILED);
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                    closeException.printStackTrace();
                }

                return;
            }

            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.
            onConnectStatusChangeListener.onConnectStatusChange(ConnectStatus.STATE_CONNECTED);
        }

        // Closes the client socket and causes the thread to finish.
        void cancel() {
            onConnectStatusChangeListener.onConnectStatusChange(ConnectStatus.STATE_DISCONNECTED);
            try {
                mmSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        void sendCharacter(String character) {
            if (mmSocket != null) {
                try {
                    mmSocket.getOutputStream().write(character.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
