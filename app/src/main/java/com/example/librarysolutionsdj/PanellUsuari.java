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

/**
 * Classe que gestiona el panell de l'usuari dins l'aplicació.
 * Permet veure i gestionar la informació del perfil de l'usuari i tancar la sessió.
 *
 * Aquesta classe extén {@link AppCompatActivity} i realitza connexions amb un servidor per obtenir
 * el perfil de l'usuari i gestionar el procés de logout.
 *
 * @author Denys Dyachuk
 * @version 0.3, 21/10/24
 */
public class PanellUsuari extends AppCompatActivity {

    /**
     * TextViews per mostrar la informació de l'usuari a la interfície.
     */
    TextView userAliasTextView, usernameTextView, surname1TextView, surname2TextView, userTypeTextView;

    /**
     * Botó de gestió d'usuaris, visible només per a usuaris admin o treballadors.
     */
    Button gestioUsuarisButton;

    /**
     * Mètode principal que s'executa en iniciar l'activitat.
     *
     * Aquest mètode configura la interfície d'usuari, recupera les dades del perfil de l'usuari
     * des d'un servidor remot i configura la funcionalitat de logout.
     *
     * @param savedInstanceState Paràmetre opcional que conté l'estat guardat de l'activitat, si està disponible.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_panell_usuari);

        // Configura els marges per als insets del sistema.
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Referències als TextView del perfil de l'usuari
        userAliasTextView = findViewById(R.id.username);
        usernameTextView = findViewById(R.id.realname);
        surname1TextView = findViewById(R.id.surname1);
        surname2TextView = findViewById(R.id.surname2);
        userTypeTextView = findViewById(R.id.usertype);

        // Referència al botó de gestió d'usuaris
        gestioUsuarisButton = findViewById(R.id.gestio_usuaris);
        gestioUsuarisButton.setVisibility(Button.GONE); // Ocultem el botó per defecte

        // Crida al mètode per obtenir el perfil de l'usuari
        getUserProfile();

        // Funcionalitat del botó de logout
        Button logoutButton = findViewById(R.id.logout_btn);
        logoutButton.setOnClickListener(view -> {
            new Thread(() -> {
                try {
                    // Obtenim el session ID guardat a SharedPreferences
                    SharedPreferences preferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                    String sessionId = preferences.getString("SESSION_ID", null);

                    if (sessionId == null) {
                        // Si no hi ha cap sessió activa, mostrar error
                        runOnUiThread(() -> Toast.makeText(PanellUsuari.this, "No hi ha sessió activa", Toast.LENGTH_SHORT).show());
                        return;
                    }

                    // Connexió al servidor per realitzar el logout
                    Socket socket = new Socket("10.0.2.2", 12345); //IP de la màquina virtual Android Studio
                    PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

                    // Enviar el comandament "LOGOUT" i el session ID al servidor
                    out.println("LOGOUT");
                    out.println(sessionId);

                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String response = in.readLine();

                    runOnUiThread(() -> {
                        if ("LOGOUT_OK".equals(response)) {
                            // Si el logout és exitós, eliminar les dades locals de la sessió
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.clear();
                            editor.apply();

                            // Mostrar missatge de sessió tancada correctament
                            Toast.makeText(PanellUsuari.this, "Sessió tancada amb èxit", Toast.LENGTH_SHORT).show();

                            // Redirigir l'usuari a la pantalla de login
                            Intent intent = new Intent(PanellUsuari.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();  // Tancar l'activitat actual
                        } else {
                            Toast.makeText(PanellUsuari.this, "Error en tancar sessió", Toast.LENGTH_SHORT).show();
                        }
                    });

                    socket.close();  // Tancar la connexió

                } catch (Exception e) {
                    runOnUiThread(() -> Toast.makeText(PanellUsuari.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                }
            }).start();
        });
    }

    /**
     * Mètode per obtenir el perfil de l'usuari des del servidor.
     * Aquest mètode es connecta al servidor per recuperar la informació del perfil
     * de l'usuari, com el nom d'usuari, nom, cognoms i tipus d'usuari.
     */
    private void getUserProfile() {
        new Thread(() -> {
            try {
                // Obtenim el session ID guardat a SharedPreferences
                SharedPreferences preferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                String sessionId = preferences.getString("SESSION_ID", null);

                if (sessionId == null) {
                    runOnUiThread(() -> Toast.makeText(PanellUsuari.this, "No hi ha sessió activa", Toast.LENGTH_SHORT).show());
                    return;
                }

                // Connexió al servidor per obtenir el perfil de l'usuari
                Socket socket = new Socket("10.0.2.2", 12345);//IP de la màquina virtual Android Studio
                PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

                // Enviar el comandament "GET_PROFILE" i el session ID
                out.println("GET_PROFILE");
                out.println(sessionId);

                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                // Llegir la resposta del servidor amb les dades de l'usuari
                String userAlias = in.readLine();
                String username = in.readLine();
                String surname1 = in.readLine();
                String surname2 = in.readLine();
                String userType = in.readLine();

                // Mostrar les dades a la interfície d'usuari
                runOnUiThread(() -> {
                    userAliasTextView.setText(userAlias);
                    usernameTextView.setText(username);
                    surname1TextView.setText(surname1);
                    surname2TextView.setText(surname2);
                    userTypeTextView.setText(userType);

                    // Si l'usuari és Admin o Treballador, fer visible el botó de gestió d'usuaris
                    if ("ADMIN".equals(userType) || "WORKER".equals(userType)) {
                        gestioUsuarisButton.setVisibility(Button.VISIBLE);
                    }
                });

                socket.close();  // Tancar la connexió

            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(PanellUsuari.this, "Error en obtenir el perfil: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}
