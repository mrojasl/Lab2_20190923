package com.example.lab2rojas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.widget.Button;
import android.widget.TextView;
import java.util.Locale;
import com.example.lab2rojas.databinding.ActivityCronometroBinding;
import com.example.lab2rojas.databinding.ActivityMenuBinding;

public class CronometroActivity extends AppCompatActivity {

    ActivityCronometroBinding binding;
    private boolean isRunning = false;
    private long elapsedTime = 0;
    private long startTime = 0;
    private TextView textViewCounter;
    private Button buttonStart, buttonPause, buttonResume, buttonClear;
    private final Handler handler = new Handler();
    private TimerThread timerThread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCronometroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        textViewCounter = binding.textViewCounter;
        buttonStart = binding.buttonStart;
        buttonPause = binding.buttonPause;
        buttonResume = binding.buttonResume;
        buttonClear = binding.buttonClear;

        binding.buttonStart.setOnClickListener(view -> {
            startTimer();
        });

        binding.buttonPause.setOnClickListener(view -> {
            pauseTimer();
        });

        binding.buttonResume.setOnClickListener(view -> {
            resumeTimer();
        });

        binding.buttonClear.setOnClickListener(view -> {
            clearTimer();
        });

        if (savedInstanceState != null) {
            isRunning = savedInstanceState.getBoolean("isRunning");
            startTime = savedInstanceState.getLong("startTime");
            if (isRunning) {
                startTimer();
            }
        }



    }


    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isRunning", isRunning);
        outState.putLong("startTime", startTime);
    }


    private void startTimer() {
        if (!isRunning) {
            if (elapsedTime == 0) {
                startTime = SystemClock.elapsedRealtime();
            } else {
                startTime = SystemClock.elapsedRealtime() - elapsedTime;
            }

            isRunning = true;
            timerThread = new TimerThread();
            timerThread.start();
            buttonStart.setEnabled(false);
            buttonPause.setEnabled(true);
            buttonResume.setEnabled(false);
            buttonClear.setEnabled(false);
        }
    }

    private void pauseTimer() {
        if (isRunning) {
            isRunning = false;
            buttonStart.setEnabled(false);
            buttonPause.setEnabled(false);
            buttonResume.setEnabled(true);
            buttonClear.setEnabled(true);

            elapsedTime = SystemClock.elapsedRealtime() - startTime;

            timerThread.interrupt();
        }
    }

    private void resumeTimer() {
        if (!isRunning) {
            startTime = SystemClock.elapsedRealtime() - elapsedTime;

            isRunning = true;
            timerThread = new TimerThread();
            timerThread.start();

            buttonStart.setEnabled(false);
            buttonPause.setEnabled(true);
            buttonResume.setEnabled(false);
            buttonClear.setEnabled(false);
        }
    }

    private void clearTimer() {
        isRunning = false;
        elapsedTime = 0;
        startTime = SystemClock.elapsedRealtime();
        textViewCounter.setText("00:00");
        buttonStart.setEnabled(true);
        buttonPause.setEnabled(false);
        buttonResume.setEnabled(false);
        buttonClear.setEnabled(false);

        if (timerThread != null && timerThread.isAlive()) {
            timerThread.interrupt();
        }
    }


    private class TimerThread extends Thread {
        @Override
        public void run() {
            while (isRunning) {
                long currentTime = SystemClock.elapsedRealtime();
                long elapsedTime = currentTime - startTime;
                final int seconds = (int) (elapsedTime / 1000);
                final int minutes = seconds / 60;
                final String time = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds % 60);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textViewCounter.setText(time);
                    }
                });

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    return;
                }
            }
        }
    }
}