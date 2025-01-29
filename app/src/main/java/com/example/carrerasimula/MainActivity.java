package com.example.carrerasimula;

import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    EditText edtCorredores, edtDistancia;
    Button btnIniciar;
    TextView txvResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtCorredores = findViewById(R.id.txtNumCorredores);
        edtDistancia = findViewById(R.id.txtDistancia);
        btnIniciar = findViewById(R.id.btnComenzar);
        txvResults = findViewById(R.id.txtResultado);

        btnIniciar.setOnClickListener(v -> {
            String numCorredores = edtCorredores.getText().toString().trim();
            String distancia = edtDistancia.getText().toString().trim();

            if (!numCorredores.isEmpty() && !distancia.isEmpty()) {
                // Llamar al método que hace la solicitud POST
                servicioAWSCarrera("https://frxr2e7a8k.execute-api.us-east-1.amazonaws.com/Carrera/", numCorredores, distancia);
            } else {
                Toast.makeText(this, "Por favor, llena todos los campos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void servicioAWSCarrera(String url, String numCorredores, String distancia) {
        // Crear una solicitud POST usando Volley
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        // Parsear la respuesta JSON
                        JSONObject jsonResponse = new JSONObject(response);
                        String body = jsonResponse.getString("body");

                        // Limpiar las comillas del cuerpo
                        body = body.replaceAll("^\"|\"$", "");

                        // Convertir el body en un objeto JSON para extraer los datos
                        JSONObject bodyJson = new JSONObject(body);
                        String mensaje = bodyJson.getString("mensaje");
                        JSONArray resultados = bodyJson.getJSONArray("resultados");
                        JSONObject ganador = bodyJson.getJSONObject("ganador");

                        // Crear la visualización de la respuesta
                        SpannableStringBuilder builder = new SpannableStringBuilder();
                        builder.append(mensaje + "\n\n");

                        // Mostrar los resultados
                        builder.append("Resultados:\n");
                        for (int i = 0; i < resultados.length(); i++) {
                            JSONObject resultado = resultados.getJSONObject(i);
                            int tiempo = resultado.getInt("tiempo");
                            JSONArray posiciones = resultado.getJSONArray("posiciones");
                            builder.append("Tiempo: " + tiempo + " segundos\n");

                            for (int j = 0; j < posiciones.length(); j++) {
                                builder.append("Corredor " + (j + 1) + ": " + posiciones.getInt(j) + " metros\n");
                            }
                            builder.append("\n");
                        }

                        // Mostrar al ganador
                        builder.append("Ganador: Corredor " + ganador.getInt("id") + "\n");
                        builder.append("Velocidad: " + ganador.getInt("velocidad") + " m/s\n");
                        builder.append("Posición final: " + ganador.getInt("posicion") + " metros");

                        // Actualizar el TextView con los resultados formateados
                        txvResults.setText(builder);

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Error procesando los datos", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    // Manejar error
                    Toast.makeText(getApplicationContext(), "Error en la solicitud: " + error.toString(), Toast.LENGTH_SHORT).show();
                }) {
            @Override
            public byte[] getBody() {
                // Crear el cuerpo de la solicitud en formato JSON, agregando los parámetros
                String jsonBody = String.format("{\"opcion\":\"post\", \"numCorredores\":\"%s\", \"distancia\":\"%s\"}", numCorredores, distancia);
                return jsonBody.getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
        };

        // Crear una cola de solicitudes y agregar la solicitud
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
