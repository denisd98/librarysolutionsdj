package com.example.librarysolutionsdj;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
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

    // Declaración de los elementos de la interfaz para mostrar la información del usuario
    TextView userAliasTextView, usernameTextView, surname1TextView, surname2TextView, userTypeTextView;
    Button gestioUsuarisButton;

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

        // Referencias a los TextView del perfil del usuario
        userAliasTextView = findViewById(R.id.useralias);
        usernameTextView = findViewById(R.id.username);
        surname1TextView = findViewById(R.id.surname1);
        surname2TextView = findViewById(R.id.surname2);
        userTypeTextView = findViewById(R.id.usertype);

        // Referencia al botón de gestión de usuarios
        gestioUsuarisButton = findViewById(R.id.gestio_usuaris);
        gestioUsuarisButton.setVisibility(Button.GONE); // Ocultar el botón por defecto

        // Llamar al método para obtener los datos del usuario
        getUserProfile();

        // Funcionalidad del botón de logout
        Button logoutButton = findViewById(R.id.logout_btn);
        logoutButton.setOnClickListener(view -> {
            new Thread(() -> {
                try {
                    // Obtener el identificador de sesión guardado en SharedPreferences
                    SharedPreferences preferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                    String sessionId = preferences.getString("SESSION_ID", null);

                    if (sessionId == null) {
                        // Si no se encuentra sesión activa, mostrar error
                        runOnUiThread(() -> Toast.makeText(PanellUsuari.this, "No hay sesión activa", Toast.LENGTH_SHORT).show());
                        return;
                    }

                    // Conectarse al servidor para realizar el logout
                    Socket socket = new Socket("10.0.2.2", 12345);  // Actualiza la IP y puerto según sea necesario
                    PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

                    // Enviar el comando "LOGOUT" y el identificador de sesión al servidor
                    out.println("LOGOUT");
                    out.println(sessionId);

                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String response = in.readLine();

                    runOnUiThread(() -> {
                        if ("LOGOUT_OK".equals(response)) {
                            // Logout exitoso: eliminar los datos de sesión locales
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.clear();  // Eliminar todos los datos guardados, incluyendo el sessionId
                            editor.apply();

                            // Mostrar mensaje de sesión cerrada con éxito
                            Toast.makeText(PanellUsuari.this, "Sessió tancada amb èxit", Toast.LENGTH_SHORT).show();

                            // Redirigir al usuario a la pantalla de login
                            Intent intent = new Intent(PanellUsuari.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);  // Eliminar la pila de actividades
                            startActivity(intent);
                            finish();  // Cerrar la actividad actual
                        } else {
                            Toast.makeText(PanellUsuari.this, "Error al cerrar sesión", Toast.LENGTH_SHORT).show();
                        }
                    });

                    socket.close();  // Cerrar la conexión

                } catch (Exception e) {
                    runOnUiThread(() -> Toast.makeText(PanellUsuari.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                }
            }).start();
        });
    }

    private void getUserProfile() {
        new Thread(() -> {
            try {
                // Obtener el identificador de sesión guardado en SharedPreferences
                SharedPreferences preferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                String sessionId = preferences.getString("SESSION_ID", null);

                if (sessionId == null) {
                    runOnUiThread(() -> Toast.makeText(PanellUsuari.this, "No hay sesión activa", Toast.LENGTH_SHORT).show());
                    return;
                }

                // Conectarse al servidor para obtener el perfil del usuario
                Socket socket = new Socket("10.0.2.2", 12345);  // Actualiza la IP y puerto según sea necesario
                PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

                // Enviar el comando "GET_PROFILE" y el identificador de sesión
                out.println("GET_PROFILE");
                out.println(sessionId);

                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                // Leer la respuesta del servidor con los datos del usuario
                String userAlias = in.readLine();
                String username = in.readLine();
                String surname1 = in.readLine();
                String surname2 = in.readLine();
                String userType = in.readLine();

                // Mostrar los datos en la interfaz de usuario
                runOnUiThread(() -> {
                    userAliasTextView.setText(userAlias);
                    usernameTextView.setText(username);
                    surname1TextView.setText(surname1);
                    surname2TextView.setText(surname2);
                    userTypeTextView.setText(userType);

                    // Si el usuario es Admin o Worker, hacer visible el botón de gestión de usuarios
                    if ("ADMIN".equals(userType) || "WORKER".equals(userType)) {
                        gestioUsuarisButton.setVisibility(Button.VISIBLE);
                    }
                });

                socket.close();  // Cerrar la conexión

            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(PanellUsuari.this, "Error al obtener el perfil: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}
