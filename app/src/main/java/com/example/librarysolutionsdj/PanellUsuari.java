package com.example.librarysolutionsdj;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class PanellUsuari extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_panell_usuari);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button logoutButton = findViewById(R.id.logout_btn);

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(() -> {
                    try {
                        // Conectarse al servidor
                        Socket socket = new Socket("10.0.2.2", 12345);  // Actualiza la IP y puerto según sea necesario
                        PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

                        // Enviar el comando "LOGOUT" al servidor
                        out.println("LOGOUT");

                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        String response = in.readLine();

                        runOnUiThread(() -> {
                            if ("LOGOUT_OK".equals(response)) {
                                // Logout exitoso: eliminar los datos de sesión locales
                                SharedPreferences preferences = getSharedPreferences("prefs", MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.clear();  // Eliminar los datos de sesión
                                editor.apply();

                                // Redirigir al usuario a la pantalla de login
                                Intent intent = new Intent(PanellUsuari.this, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);  // Eliminar la pila de actividades
                                startActivity(intent);
                                finish();  // Cerrar la actividad actual
                            } else {
                                Toast.makeText(PanellUsuari.this, "Error al cerrar sesión", Toast.LENGTH_SHORT).show();
                            }
                        });

                        socket.close();
                    } catch (Exception e) {
                        runOnUiThread(() -> Toast.makeText(PanellUsuari.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    }
                }).start();
            }
        });
    }
}