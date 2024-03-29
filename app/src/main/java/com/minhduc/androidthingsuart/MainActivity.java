package com.minhduc.androidthingsuart;

import com.minhduc.androidthingsuart.exercises.*;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import com.google.android.things.pio.PeripheralManager;
import com.google.android.things.pio.UartDevice;
import com.google.android.things.pio.UartDeviceCallback;

import java.io.IOException;

public class MainActivity extends Activity {
    private int state = 0;
    String sendComfirm = "App starts ready to receive commands !!!";


    private static final String TAG = "UART";

    // UART Configuration Parameters
    private static final int BAUD_RATE = 115200;
    private static final int DATA_BITS = 8;
    private static final int STOP_BITS = 1;

    private static final int CHUNK_SIZE = 512;

    private HandlerThread mInputThread;
    private Handler mInputHandler;

    private UartDevice uartDevice;

    private static ExerciseOne ex1 = new ExerciseOne();
    private static ExerciseTwo ex2 = new ExerciseTwo();
    private static ExerciseThree ex3 = new ExerciseThree();
    private static ExerciseFour ex4 = new ExerciseFour();
    private static ExerciseFive ex5 = new ExerciseFive();

    private Runnable mTransferUartRunnable = new Runnable() {
        @Override
        public void run() {
            transferUartData();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Activity Created");

        // Create a background looper thread for I/O
        mInputThread = new HandlerThread("InputThread");
        mInputThread.start();
        mInputHandler = new Handler(mInputThread.getLooper());

        // Attempt to access the UART device
        try {
            openUart(BoardDefaults.getUartName(), BAUD_RATE);
            String sendHello = "Android Things Hello from UART";
            uartDevice.write(sendHello.getBytes(), sendHello.length());
            Log.d(TAG, "Opened UART device");
            // Read any initially buffered data
            mInputHandler.post(mTransferUartRunnable);
        } catch (IOException e) {
            Log.e(TAG, "Unable to open UART device", e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Activity Destroyed");

        // Terminate the worker thread
        if (mInputThread != null) {
            mInputThread.quitSafely();
        }

        // Attempt to close the UART device
        try {
            closeUart();
        } catch (IOException e) {
            Log.e(TAG, "Error closing UART device:", e);
        }
    }

    /**
     * Callback invoked when UART receives new incoming data.
     */
    private UartDeviceCallback mCallback = new UartDeviceCallback() {
        @Override
        public boolean onUartDeviceDataAvailable(UartDevice uart) {
            try {
                readUartBuffer(uart);
            } catch (IOException e) {
                Log.w(TAG, "Unable to access UART device", e);
            }
            return true;
        }

        @Override
        public void onUartDeviceError(UartDevice uart, int error) {
            Log.w(TAG, uart + ": Error event " + error);
        }
    };

    private void openUart(String name, int baudRate) throws IOException {
        uartDevice = PeripheralManager.getInstance().openUartDevice(name);
        // Configure the UART
        uartDevice.setBaudrate(baudRate);
        uartDevice.setDataSize(DATA_BITS);
        uartDevice.setParity(UartDevice.PARITY_NONE);
        uartDevice.setStopBits(STOP_BITS);

        uartDevice.registerUartDeviceCallback(mInputHandler, mCallback);
    }

    /**
     * Close the UART device connection, if it exists
     */
    private void closeUart() throws IOException {
        if (uartDevice != null) {
            uartDevice.unregisterUartDeviceCallback(mCallback);
            try {
                uartDevice.close();
            } finally {
                uartDevice = null;
            }
        }
    }

    /**
     * Loop over the contents of the UART RX buffer, transferring each
     * one back to the TX buffer to create a loopback service.
     *
     * Potentially long-running operation. Call from a worker thread.
     */
    private void transferUartData() {
        if (uartDevice != null) {
            // Loop until there is no more data in the RX buffer.
            try {
                byte[] buffer = new byte[CHUNK_SIZE];
                int read;
                while ((read = uartDevice.read(buffer, buffer.length)) > 0) {
                    uartDevice.write(buffer, read);
                }
            } catch (IOException e) {
                Log.w(TAG, "Unable to transfer data over UART", e);
            }
        }
    }

    public void readUartBuffer(UartDevice uart) throws IOException {
        // Maximum amount of data to read at one time
        byte[] buffer = new byte[CHUNK_SIZE];
        int count;
        while ((count = uart.read(buffer, buffer.length)) > 0) {
            //Log.d(TAG, "Read " + count + " bytes from peripheral");
            int key = (int) buffer[0];
            char c = Character.toUpperCase((char) key); //Comment to remove auto upper case
            if(state == 0){
                if(c == 'O') {
                    uartDevice.write(sendComfirm.getBytes(), sendComfirm.length());
                    Log.d(TAG, "App starts ready to recieve commands");
                    state = 1;
                }
            }
            else if(state == 1){
                switch (c) {
                    case '1':
                        Log.d(TAG, "Case 1");
                        ex1.onDestroy();
                        ex2.onDestroy();
                        ex3.onDestroy();
                        ex4.onDestroy();
                        ex5.onDestroy();
                        ex1.onCreate();
                        break;
                    case '2':
                        Log.d(TAG, "Case 2");
                        ex1.onDestroy();
                        ex2.onDestroy();
                        ex3.onDestroy();
                        ex4.onDestroy();
                        ex5.onDestroy();
                        ex2.onCreate();
                        break;
                    case '3':
                        Log.d(TAG, "Case 3");
                        ex1.onDestroy();
                        ex2.onDestroy();
                        ex3.onDestroy();
                        ex4.onDestroy();
                        ex5.onDestroy();
                        ex3.onCreate();
                        break;
                    case '4':
                        Log.d(TAG, "Case 4");
                        ex1.onDestroy();
                        ex2.onDestroy();
                        ex3.onDestroy();
                        ex4.onDestroy();
                        ex5.onDestroy();
                        ex4.onCreate();
                        break;
                    case '5':
                        Log.d(TAG, "Case 5");
                        ex1.onDestroy();
                        ex2.onDestroy();
                        ex3.onDestroy();
                        ex4.onDestroy();
                        ex5.onDestroy();
                        ex5.onCreate();
                        break;
                    case 'F':
                        Log.d(TAG, "Case F");
                        ex1.onDestroy();
                        ex2.onDestroy();
                        ex3.onDestroy();
                        ex4.onDestroy();
                        ex5.onDestroy();
                        state = 0;
                        Log.d(TAG, "App stops any running");
                        break;
                    default:
                        break;
                }
            }
            uartDevice.write(buffer, count);
        }

    }
}
