package com.minhduc.androidthingsuart.exercises;

import android.os.Handler;
import android.util.Log;

import com.google.android.things.pio.PeripheralManager;
import com.google.android.things.pio.Pwm;

import java.io.IOException;

public class ExerciseThree {
    private static final String TAG = "EX3";
    private Handler mHandler = new Handler();

    // PWM PIN
    private static final String PWM0 = "PWM0";
    private static final String PWM1 = "PWM1";

    private static final double MIN_DUTY_CYCLE = 0;
    private static final double MAX_DUTY_CYCLE = 100;
    private static final double DUTY_CYCLE_CHANGE_PER_STEP = 0.1;
    private static final int STEP = 1;
    private double dutyCycle;
    private boolean isIncreasing = true;

    private Pwm mPwm0;
    private Pwm mPwm1;

    public void onCreate() {
        Log.i(TAG, "Starting Changing PWM");

        try {
            PeripheralManager manager = PeripheralManager.getInstance();
            mPwm0 = manager.openPwm(PWM0);
            mPwm1 = manager.openPwm(PWM1);

            initializePwm(mPwm0);
            initializePwm(mPwm1);

            mHandler.post(changePWMRunnable);
        } catch (IOException e) {
            Log.w(TAG, "Unable to access PWM", e);
        }
    }

    private Runnable changePWMRunnable = new Runnable() {

        @Override
        public void run() {
            if (mPwm0 == null || mPwm1 == null) {
                Log.w(TAG, "Stopping runnable since Pwm is null");
                return;
            }

            if (isIncreasing) {
                dutyCycle += DUTY_CYCLE_CHANGE_PER_STEP;
            } else {
                dutyCycle -= DUTY_CYCLE_CHANGE_PER_STEP;
            }

            if (dutyCycle > MAX_DUTY_CYCLE) {
                dutyCycle = MAX_DUTY_CYCLE;
                isIncreasing = !isIncreasing;
            } else if (dutyCycle < MIN_DUTY_CYCLE) {
                dutyCycle = MIN_DUTY_CYCLE;
                isIncreasing = !isIncreasing;
            }

            //Log.d(TAG, "Changing PWM duty cycle to " + dutyCycle);

            try {
                // Duty cycle is the percentage of active (on) pulse over the total duration of the PWM pulse
                mPwm0.setPwmDutyCycle(dutyCycle);
                mPwm1.setPwmDutyCycle(dutyCycle);
                mHandler.postDelayed(this, STEP);
            } catch (IOException e) {
                Log.e(TAG, "Error on PeripheralIO API", e);
            }
        }
    };

    public void onDestroy() {
        mHandler.removeCallbacks(changePWMRunnable);

        Log.i(TAG, "Closing PWM pin");
        if (mPwm0 != null || mPwm1 != null) {
            try {
                mPwm0.close();
                mPwm1.close();

            } catch (IOException e) {
                Log.w(TAG, "Unable to close PWM", e);
            } finally {
                mPwm0 = null;
                mPwm1 = null;
            }
        }
    }

    private void initializePwm(Pwm pwm) throws IOException {
        pwm.setPwmFrequencyHz(120);
        pwm.setPwmDutyCycle(25);

        // Enable PWM signal
        pwm.setEnabled(true);
    }
}
