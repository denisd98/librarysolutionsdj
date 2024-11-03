package com.example.librarysolutionsdj;

import static androidx.core.content.ContextCompat.startActivity;

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

/**
 * Classe que gestiona la pantalla d'inici de sessió de l'aplicació.
 * L'usuari introdueix les seves credencials per a accedir al sistema.
 *
 * Aquesta classe extén {@link AppCompatActivity} i maneja la connexió amb un servidor
 * per a validar les credencials mitjançant sockets.
 *
 * @author Denys Dyachuk
 * @version 0.3, 21/10/24
 */
public class LoginActivity extends AppCompatActivity {


    // Camps d'entrada per l'usuari i la contrasenya.
    EditText usernameEditText;
    EditText passwordEditText;

    // Botó d'inici de sessió.
    Button loginButton;

    /**
     * Mètode principal que s'executa en iniciar l'activitat.
     *
     * Configura la interfície d'usuari, els listeners dels botons i la connexió
     * amb el servidor per validar les credencials d'inici de sessió.
     *
     * @param savedInstanceState Paràmetre opcional que conté l'estat guardat de l'activitat, si està disponible.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Configura els marges per als insets del sistema.
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        usernameEditText = findViewById(R.id.user);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.access);


        // Es configura el botó de tornada per tornar a la pantalla principal (MainActivity)
        ImageButton backButton = findViewById(R.id.back);
        backButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Mètode que s'executa quan es fa clic al botó de tornada.
             * Torna a la pantalla principal {@link MainActivity}.
             *
             * @param v La vista que ha estat clicada.
             */
            @Override
            public void onClick(View v) {
                // Crear un Intent per a tornar a MainActivity
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Es configura el listener del botó d'accés per gestionar l'inici de sessió
        loginButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Mètode que s'executa quan es fa clic al botó d'accés.
             * Envia les credencials d'usuari al servidor per a validar-les.
             *
             * @param view La vista que ha estat clicada.
             */
            @Override
            public void onClick(View view) {
                // S'obtenen els camps d'usuari i contrasenya
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                // Es crea un fil per manejar la connexió amb el servidor
                new Thread(() -> {
                    try {
                        // Connexió al servidor amb sockets
                        Socket socket = new Socket("10.0.2.2", 12345); // IP de la màquina virtual d'Android Studio
                        PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

                        // S'envia el comandament LOGIN al servidor
                        out.println("LOGIN");
                        out.println(username);
                        out.println(password);

                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        String response = in.readLine();

                        // Es comprova la resposta del servidor
                        if ("LOGIN_OK".equals(response)) {
                            // S'obté el session ID i el tipus d'usuari
                            String sessionId = in.readLine().split(":")[1];
                            String userType = in.readLine().split(":")[1];

                            // Verifica si sessionId y userType son correctos antes de almacenarlos
                            if (sessionId != null && !sessionId.isEmpty() && userType != null && !userType.isEmpty()) {
                                SharedPreferences preferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("SESSION_ID", sessionId);
                                editor.putString("USER_TYPE", userType);
                                editor.apply();
                            }

                            // Es guarden les dades de sessió a SharedPreferences
                            SharedPreferences preferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("SESSION_ID", sessionId);
                            editor.putString("USER_TYPE", userType);
                            editor.apply();

                            // Actualització de la interfície en el fil principal
                            runOnUiThread(() -> {
                                Toast.makeText(LoginActivity.this, "Login OK", Toast.LENGTH_SHORT).show();

                                // Es redirigeix al panell d'usuari
                                Intent intent = new Intent(LoginActivity.this, PanellUsuari.class);
                                startActivity(intent);
                            });

                        } else {
                            // Si les dades són incorrectes, es mostra un error
                            runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Dades incorrectes", Toast.LENGTH_SHORT).show());
                        }

                        // Es tanca la connexió
                        socket.close();
                    } catch (Exception e) {
                        // Es mostren els errors per pantalla (només per a proves)
                        runOnUiThread(() -> Toast.makeText(LoginActivity.this, "ERROR: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    }
                }).start();
            }
        });

    }

}
