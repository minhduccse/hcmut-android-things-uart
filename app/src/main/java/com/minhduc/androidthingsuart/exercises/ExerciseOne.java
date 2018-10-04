package com.minhduc.androidthingsuart.exercises;

import android.os.Handler;
import android.util.Log;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManager;

import java.io.IOException;


public class ExerciseOne{
    private static final String TAG = "EX1";
    private String GPIO2 = "BCM2";
    private String GPIO3 = "BCM3";
    private String GPIO4 = "BCM4";

    private static final int INTERVAL_BETWEEN_BLINKS_MS = 1000;

    private Handler mHandler = new Handler();

    private Gpio mLedGpioGreen1;
    private Gpio mLedGpioRed1;
    private Gpio mLedGpioBlue1;

    private int state;

    // This function is just like setup()
    public void onCreate() {
        Log.i(TAG, "Starting BlinkActivity");

        try {
            // Declare GPIO ports
            PeripheralManager manager = PeripheralManager.getInstance();
            mLedGpioRed1 = manager.openGpio(GPIO2);
            mLedGpioGreen1 = manager.openGpio(GPIO3);
            mLedGpioBlue1 = manager.openGpio(GPIO4);

            // Define outputs
            mLedGpioRed1.setDirection(Gpio.DIRECTION_OUT_INITIALLY_HIGH);
            mLedGpioGreen1.setDirection(Gpio.DIRECTION_OUT_INITIALLY_HIGH);
            mLedGpioBlue1.setDirection(Gpio.DIRECTION_OUT_INITIALLY_HIGH);

            // Assign initial states
            mLedGpioRed1.setActiveType(Gpio.ACTIVE_LOW);
            mLedGpioGreen1.setActiveType(Gpio.ACTIVE_LOW);
            mLedGpioBlue1.setActiveType(Gpio.ACTIVE_LOW);

            // Init first state
            state = 1;

            // Post handle
            mHandler.post(mBlinkRunnable);

        } catch (IOException e) {
            Log.e(TAG, "Error on PeripheralIO API", e);
        }

    }

    private Runnable mBlinkRunnable = new Runnable() {
        @Override
        public void run() {
            if (mLedGpioBlue1 == null || mLedGpioGreen1 == null || mLedGpioRed1 == null) {
                Log.w(TAG, "Stopping runnable since Gpio is null");
                return;
            }
            try {
                // State Machine
                switch (state) {
                    case 1:
                        mLedGpioRed1.setValue(true);
                        mLedGpioGreen1.setValue(false);
                        mLedGpioBlue1.setValue(false);
                        state = 2;
                        break;
                    case 2:
                        mLedGpioRed1.setValue(false);
                        mLedGpioGreen1.setValue(true);
                        mLedGpioBlue1.setValue(false);
                        state = 3;
                        break;
                    case 3:
                        mLedGpioRed1.setValue(false);
                        mLedGpioGreen1.setValue(false);
                        mLedGpioBlue1.setValue(true);
                        state = 1;
                        break;
                    default:
                        break;
                }

                // Reschedule runnable in INTERVAL_BETWEEN_BLINKS_MS
                mHandler.postDelayed(mBlinkRunnable, INTERVAL_BETWEEN_BLINKS_MS);

            } catch (IOException e) {
                Log.e(TAG, "Error on PeripheralIO API", e);
            }
        }
    };

    public void onDestroy() {
        // Remove pending blink Runnable
        mHandler.removeCallbacks(mBlinkRunnable);
        // Close Gpio
        Log.i(TAG, "Closing LED GPIO pin");
        if(mLedGpioRed1 != null || mLedGpioBlue1 != null || mLedGpioGreen1 != null) try {
            mLedGpioGreen1.close();
            mLedGpioBlue1.close();
            mLedGpioRed1.close();
        } catch (IOException e) {
            Log.e(TAG, "Error on PeripheralIO API", e);
        } finally {
            mLedGpioRed1 = null;
            mLedGpioBlue1 = null;
            mLedGpioGreen1 = null;
        }
    }
}