package com.example.carrerasimula;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    EditText txtNumCorredores, txtDistancia;
    Button btnCrear;
    TextView txtResultado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        txtNumCorredores = findViewById(R.id.txtNumCorredores);
        txtDistancia = findViewById(R.id.txtDistancia);

        btnCrear.setOnClickListener(v -> {
            String numCorredores = txtNumCorredores.getText().toString().trim();
            String distancia = txtDistancia.getText().toString().trim();

            if (!numCorredores.isEmpty() && !distancia.isEmpty()) {
                String url = "https://frxr2e7a8k.execute-api.us-east-1.amazonaws.com/Carrera/" + numCorredores + "&distancia=" + distancia;
                ejecutarRequest(url);
            } else {
                Toast.makeText(this, "Por favor, llena todos los campos", Toast.LENGTH_SHORT).show();
            }
        });
    }
}