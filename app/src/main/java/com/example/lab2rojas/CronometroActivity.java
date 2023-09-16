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
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.example.lab2rojas.databinding.ActivityMenuBinding;

public class CronometroActivity extends AppCompatActivity {

    ActivityCronometroBinding binding;
    private boolean isRunning = false;
    private long elapsedTime;
    private long startTime;
    private SharedPreferences preferences;

    private TextView textViewCounter;
    private Button buttonStart, buttonPause, buttonResume, buttonClear;
    private final Handler handler = new Handler();
    private TimerThread timerThread;
    private int milliseconds = 0;




    @Override
    //TODO: El cronómetro funciona cuando sales de aplicación, sin embargo, si es que regresas a menú activity, el cronometro se guardará solo si se ha pausado
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

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        elapsedTime = preferences.getLong("elapsedTime", 0);
        startTime = preferences.getLong("startTime", 0);

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


            SharedPreferences.Editor editor = preferences.edit();
            editor.putLong("elapsedTime", elapsedTime);
            editor.putLong("startTime", startTime);
            editor.apply();
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




            SharedPreferences.Editor editor = preferences.edit();
            editor.putLong("elapsedTime", elapsedTime);
            editor.putLong("startTime", startTime);
            editor.apply();
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
        milliseconds = 0;
        textViewCounter.setText("00:00.0");
        buttonStart.setEnabled(true);
        buttonPause.setEnabled(false);
        buttonResume.setEnabled(false);
        buttonClear.setEnabled(false);

        if (timerThread != null && timerThread.isAlive()) {
            timerThread.interrupt();
        }


        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong("elapsedTime", elapsedTime);
        editor.putLong("startTime", startTime);
        editor.apply();
    }


    private class TimerThread extends Thread {
        @Override
        public void run() {
            while (isRunning) {
                long currentTime = SystemClock.elapsedRealtime();
                long elapsedTime = currentTime - startTime;
                milliseconds = (int) (elapsedTime / 100) % 10;
                elapsedTime /= 1000; // Convierte a segundos
                final int seconds = (int) (elapsedTime % 60);
                final int minutes = (int) (elapsedTime / 60);
                final String time = String.format(Locale.getDefault(), "%02d:%02d.%01d", minutes, seconds, milliseconds);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textViewCounter.setText(time);
                    }
                });

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    return;
                }
            }
        }
    }
}