package com.example.lab2rojas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.lab2rojas.databinding.ActivityContadorBinding;
import com.example.lab2rojas.databinding.ActivityCronometroBinding;

public class ContadorActivity extends AppCompatActivity {
    private TextView textViewCounter;
    private Button buttonIniciar;
    private boolean isRunning = false;
    private int contador = 104;
    private int incremento = 1;
    private final int LIMITE_SUPERIOR = 226;
    private final int INTERVALO_TIEMPO = 10000;
    private MediaPlayer mediaPlayer;

    //TODO: El contador si comienza a ir más rápido cuando presionas muchas veces "iniciar", pero tarda un poco en cargar.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contador);

        textViewCounter = findViewById(R.id.textViewCounter);
        buttonIniciar = findViewById(R.id.buttonIniciar);

        buttonIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isRunning) {
                    isRunning = true;
                    startCounter();
                } else {
                    incremento++;
                }
            }
        });

        mediaPlayer = MediaPlayer.create(this, R.raw.sonido);
    }

    private void startCounter() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isRunning && contador < LIMITE_SUPERIOR) {
                    contador++;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textViewCounter.setText(String.valueOf(contador));
                        }
                    });

                    try {
                        Thread.sleep(INTERVALO_TIEMPO / incremento);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                if (contador >= LIMITE_SUPERIOR) {
                    playAlarm();
                }
            }
        }).start();
    }
    private void playAlarm() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }
}