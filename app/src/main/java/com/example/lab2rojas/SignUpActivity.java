package com.example.lab2rojas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.lab2rojas.databinding.ActivitySignUpBinding;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import android.os.Bundle;

public class SignUpActivity extends AppCompatActivity {
    ActivitySignUpBinding binding;
    EditText editTextNombre, editTextApellido, editTextCorreo, editTextContrasena;
    CheckBox checkBoxAgree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        editTextNombre = binding.editTextNombre;
        editTextApellido = binding.editTextApellido;
        editTextCorreo = binding.editTextCorreo;
        editTextContrasena = binding.editTextContrasena;
        checkBoxAgree = binding.checkBoxAgree;

        new GetDataTask().execute();
    }

    private class GetDataTask extends AsyncTask<Void, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(Void... voids) {
            try {
                // Realizar la solicitud GET a la URL
                URL url = new URL("https://randomuser.me/api/");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                InputStream inputStream = connection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    response.append(line);
                }
                bufferedReader.close();
                inputStream.close();

                return new JSONObject(response.toString());
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(JSONObject result) {
            super.onPostExecute(result);
            if (result != null) {
                try {
                    JSONArray resultsArray = result.getJSONArray("results");
                    JSONObject userData = resultsArray.getJSONObject(0);

                    String nombre = userData.getJSONObject("name").getString("first");
                    String apellido = userData.getJSONObject("name").getString("last");
                    String correo = userData.getString("email");
                    String contrasena = userData.getJSONObject("login").getString("password");
                    String nombreUsuario = userData.getJSONObject("login").getString("username");
                    String imageUrl = userData.getJSONObject("picture").getString("large");

                    editTextContrasena.setText(contrasena);
                    editTextNombre.setText(nombre);
                    editTextApellido.setText(apellido);
                    editTextCorreo.setText(correo);


                    SharedPreferences preferences = getSharedPreferences("UserData", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("nombre", nombre);
                    editor.putString("apellido", apellido);
                    editor.putString("nombreUsuario", nombreUsuario);
                    editor.apply();

                    binding.btnRegistrar.setOnClickListener(view -> {
                        if (validarEspacios()) {
                            Intent intent = new Intent(SignUpActivity.this, MenuActivity.class);
                            intent.putExtra("profileImageUrl", imageUrl);
                            Toast.makeText(SignUpActivity.this, "Menú", Toast.LENGTH_SHORT).show();
                            startActivity(intent);
                        } else {
                            Toast.makeText(SignUpActivity.this, "Por favor, complete todos los campos y marque la casilla de acuerdo.", Toast.LENGTH_SHORT).show();
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(SignUpActivity.this, "Error al obtener datos", Toast.LENGTH_SHORT).show();
            }
        }
        private boolean validarEspacios() {
            String nombre = editTextNombre.getText().toString().trim();
            String apellido = editTextApellido.getText().toString().trim();
            String correo = editTextCorreo.getText().toString().trim();
            String contrasena = editTextContrasena.getText().toString().trim();
            boolean checkBoxChecked = checkBoxAgree.isChecked();

            return !nombre.isEmpty() && !apellido.isEmpty() && !correo.isEmpty() && !contrasena.isEmpty() && checkBoxChecked;
        }
    }


}