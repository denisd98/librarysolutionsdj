package com.example.librarysolutionsdj;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

public class LoginActivity extends AppCompatActivity {

    EditText usernameEditText;
    EditText passwordEditText;
    Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        usernameEditText = findViewById(R.id.user);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.access);

        //Se identifica el ID del botón para volver a la pantalla de MainActivity
        ImageButton backButton = findViewById(R.id.back);

        // Listener para que al hacer click en el botón redirija a MainActivity
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear un Intent para MainActivity
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();  // Cerrar la actividad actual para que no se pueda volver a ella con el botón "Atrás"
            }
        });

        // Listener para el botón de acceder.
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Se identifican los campos de usuario y contraseña
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                new Thread(() -> {
                    try {
                        Socket socket = new Socket("10.0.2.2", 12345); //si la app se prueba en una máquina virtual de Android Studio, la ip tiene que ser la 10.0.2.2
                        PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

                        // Primero enviar el comando LOGIN para que el servidor pueda identificar el tipo de comando
                        out.println("LOGIN");
                        // Se envían los datos de usuario y contraseña
                        out.println(username);
                        out.println(password);

                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        String response = in.readLine();

                        // Esperar respuesta del servidor
                        if ("LOGIN_OK".equals(response)) { // Si los datos coinciden, recibimos LOGIN OK
                            // Leer el identificador de sesión
                            String sessionId = in.readLine().split(":")[1]; // Formato esperado: SESSION_ID:<id>
                            String userType = in.readLine().split(":")[1];  // Formato esperado: USER_TYPE:<type>

                            // Guardar el identificador de sesión y el tipo de usuario en SharedPreferences
                            SharedPreferences preferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("SESSION_ID", sessionId);  // Guardar session ID
                            editor.putString("USER_TYPE", userType);    // Guardar el tipo de usuario
                            editor.apply();

                            // Actualizar la interfaz en el hilo principal
                            runOnUiThread(() -> {
                                Toast.makeText(LoginActivity.this, "Login OK", Toast.LENGTH_SHORT).show();

                                // Redirigir al panel de usuario. Se gestiona en la clase "PanellUsuari.java"
                                Intent intent = new Intent(LoginActivity.this, PanellUsuari.class);
                                startActivity(intent);
                            });

                        } else {
                            // Si los datos enviados no coinciden, mostramos error
                            runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Dades incorrectes", Toast.LENGTH_SHORT).show());
                        }

                        socket.close(); // Cerramos conexión
                    } catch (Exception e) {
                        // Mostrar errores en pantalla (solo para pruebas)
                        runOnUiThread(() -> Toast.makeText(LoginActivity.this, "ERROR: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    }
                }).start();
            }
        });
    }
}
