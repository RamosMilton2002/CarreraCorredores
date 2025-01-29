package com.example.carrerasimula;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    EditText txtNumCorredores, txtDistancia;
    Button btnCrear;
    TextView txtResultado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtNumCorredores = findViewById(R.id.txtNumCorredores);
        txtDistancia = findViewById(R.id.txtDistancia);
        btnCrear = findViewById(R.id.btnComenzar); // Inicializar correctamente el botón
        txtResultado = findViewById(R.id.txtResultado);

        btnCrear.setOnClickListener(v -> {
            String numCorredores = txtNumCorredores.getText().toString().trim();
            String distancia = txtDistancia.getText().toString().trim();

            if (!numCorredores.isEmpty() && !distancia.isEmpty()) {
                String url = "https://frxr2e7a8k.execute-api.us-east-1.amazonaws.com/Carrera/";

                // Enviar la solicitud en formato JSON
                enviarRequest(url, numCorredores, distancia);
            } else {
                Toast.makeText(this, "Por favor, llena todos los campos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void enviarRequest(String urlStr, String numCorredores, String distancia) {
        new Thread(() -> {
            try {
                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                // Crear el cuerpo de la solicitud en formato JSON
                String jsonInputString = String.format(
                        "{\"opcion\": \"post\", \"numCorredores\": \"%s\", \"distancia\": \"%s\"}",
                        numCorredores, distancia);

                // Escribir los datos en la conexión
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                // Leer la respuesta
                int code = conn.getResponseCode();
                if (code == HttpURLConnection.HTTP_OK) {
                    // Aquí podrías leer la respuesta, en este ejemplo, solo mostramos el código de respuesta
                    runOnUiThread(() -> txtResultado.setText("Solicitud exitosa!"));
                } else {
                    runOnUiThread(() -> txtResultado.setText("Error en la solicitud: " + code));
                }

                conn.disconnect();
            } catch (Exception e) {
                Log.e("MainActivity", "Error en la solicitud HTTP", e);
                runOnUiThread(() -> txtResultado.setText("Error en la solicitud"));
            }
        }).start();
    }
}
