package com.example.lab2rojas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.lab2rojas.databinding.ActivityMenuBinding;
import com.example.lab2rojas.databinding.ActivitySignUpBinding;

public class MenuActivity extends AppCompatActivity {
    ActivityMenuBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SharedPreferences preferences = getSharedPreferences("UserData", MODE_PRIVATE);
        String nombre = preferences.getString("nombre", "");
        String apellido = preferences.getString("apellido", "");
        String nombreUsuario = preferences.getString("nombreUsuario", "");

        TextView textViewUsername = findViewById(R.id.textViewUsername);
        TextView textViewNombreApellido = findViewById(R.id.textViewNombreApellido);

        textViewUsername.setText(nombreUsuario);
        textViewNombreApellido.setText(nombre + " " + apellido);

        String profileImageUrl = getIntent().getStringExtra("profileImageUrl");
        ImageView imageViewProfile = findViewById(R.id.imageViewProfile); // Debes agregar un ImageView en tu diseño XML
        Glide.with(this)
                .load(profileImageUrl)
                .into(imageViewProfile);

        binding.buttonCronometro.setOnClickListener(view -> {
            Intent intent = new Intent(MenuActivity.this, CronometroActivity.class);
            startActivity(intent);
        });


        binding.buttonContador.setOnClickListener(view -> {
            Intent intent = new Intent(MenuActivity.this, ContadorActivity.class);
            startActivity(intent);
        });


    }

}