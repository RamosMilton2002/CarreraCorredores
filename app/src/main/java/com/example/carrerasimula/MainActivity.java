package com.example.carrerasimula;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    EditText edtCorredores, edtDistancia;
    Button btnIniciar;
    TextView txvResults;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtCorredores = findViewById(R.id.txtNumCorredores);
        edtDistancia = findViewById(R.id.txtDistancia);
        btnIniciar = findViewById(R.id.btnComenzar);
        txvResults = findViewById(R.id.txtResultado);

        requestQueue = Volley.newRequestQueue(this);

        btnIniciar.setOnClickListener(v -> {
            String numCorredores = edtCorredores.getText().toString().trim();
            String distancia = edtDistancia.getText().toString().trim();

            if (!numCorredores.isEmpty() && !distancia.isEmpty()) {
                int corredores = Integer.parseInt(numCorredores);
                int dist = Integer.parseInt(distancia);
                servicioAWSCarrera("https://frxr2e7a8k.execute-api.us-east-1.amazonaws.com/Carrera/", corredores, dist);
            } else {
                Toast.makeText(this, "Por favor, llena todos los campos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void servicioAWSCarrera(String url, int numCorredores, int distancia) {
        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("opcion", "post");
            requestBody.put("numCorredores", numCorredores);
            requestBody.put("distancia", distancia);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    requestBody,
                    response -> {
                        try {
                            if (response.has("body")) {
                                JSONObject body = response.getJSONObject("body");

                                if (body.has("ganador") && body.has("corredores")) {
                                    JSONObject ganador = body.getJSONObject("ganador");
                                    JSONArray corredores = body.getJSONArray("corredores");


                                    StringBuilder builder = new StringBuilder();
                                    builder.append("🏆 Ganador: Corredor ").append(ganador.getInt("corredor")).append("\n");
                                    builder.append("Tiempo: ").append(ganador.getString("tiempo")).append(" segundos\n");
                                    builder.append("Velocidad: ").append(ganador.getString("velocidad")).append("\n\n");
                                    builder.append("📊 Resultados de la carrera:\n");

                                    for (int i = 0; i < corredores.length(); i++) {
                                        JSONObject corredor = corredores.getJSONObject(i);
                                        builder.append("Corredor ").append(corredor.getInt("corredor")).append(":\n");
                                        builder.append("Tiempo: ").append(corredor.getString("tiempo")).append(" segundos\n");
                                        builder.append("Velocidad: ").append(corredor.getString("velocidad")).append("\n\n");
                                    }

                                    
                                    txvResults.setText(builder.toString());
                                } else {
                                    Toast.makeText(getApplicationContext(), "Formato de respuesta incorrecto", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "Respuesta sin cuerpo válido", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Error procesando los datos", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> {
                        String errorMsg = "Error en la solicitud: " + error.toString();
                        if (error.networkResponse != null) {
                            errorMsg += " Código: " + error.networkResponse.statusCode;
                            try {
                                String responseBody = new String(error.networkResponse.data, "utf-8");
                                errorMsg += "\nRespuesta del servidor: " + responseBody;
                            } catch (Exception e) {
                                errorMsg += "\nNo se pudo leer la respuesta.";
                            }
                        }
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };

            requestQueue.add(jsonObjectRequest);

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al crear la solicitud", Toast.LENGTH_SHORT).show();
        }
    }

}
