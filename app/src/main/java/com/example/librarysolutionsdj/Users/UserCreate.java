package com.example.librarysolutionsdj.Users;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.librarysolutionsdj.R;
import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import app.model.User;

/**
 * Activitat per a la creació d'un nou usuari.
 * Aquesta classe permet recollir les dades d'un usuari mitjançant un formulari i enviar-les al servidor
 * per afegir el nou usuari al sistema.
 */
public class UserCreate extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText, realnameEditText, surname1EditText, surname2EditText;
    private Spinner userTypeSpinner;
    private Button createButton;
    private ImageButton backButton;

    private boolean isTestEnvironment = false; // Indica si l'activitat està en un entorn de proves

    /**
     * Configura la interfície gràfica de l'activitat i inicialitza els components.
     *
     * @param savedInstanceState estat guardat de l'activitat.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_create);
        EdgeToEdge.enable(this);

        usernameEditText = findViewById(R.id.username_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        realnameEditText = findViewById(R.id.realname_edit_text);
        surname1EditText = findViewById(R.id.surname1_edit_text);
        surname2EditText = findViewById(R.id.surname2_edit_text);
        userTypeSpinner = findViewById(R.id.user_type_spinner);
        createButton = findViewById(R.id.create_button);
        backButton = findViewById(R.id.back_button);

        // Configurar l'Spinner amb els tipus d'usuari
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.user_types_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userTypeSpinner.setAdapter(adapter);

        // Configurar el botó per crear l'usuari
        createButton.setOnClickListener(v -> createUser());

        // Configurar el botó per tornar enrere
        backButton.setOnClickListener(v -> finish());
    }

    /**
     * Configura si l'activitat ha d'executar-se en un entorn de proves.
     *
     * @param isTestEnvironment cert si l'activitat està en mode de proves.
     */
    public void setTestEnvironment(boolean isTestEnvironment) {
        this.isTestEnvironment = isTestEnvironment;
    }

    /**
     * Crea un nou usuari enviant les dades del formulari al servidor.
     * Si l'entorn és de proves, s'utilitza una resposta simulada.
     */
    private void createUser() {
        // Recollir dades del formulari
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String realname = realnameEditText.getText().toString().trim();
        String surname1 = surname1EditText.getText().toString().trim();
        String surname2 = surname2EditText.getText().toString().trim();
        String userType = userTypeSpinner.getSelectedItem().toString();

        // Validar camps obligatoris
        if (username.isEmpty() || password.isEmpty() || realname.isEmpty() || surname1.isEmpty()) {
            Snackbar.make(findViewById(android.R.id.content), "Si us plau, omple tots els camps requerits.", Snackbar.LENGTH_LONG).show();
            return;
        }

        // Crear l'objecte User amb les dades recollides
        User newUser = new User(0, username, password, realname, surname1, surname2, User.stringToUserType(userType));

        // Enviar dades al servidor en un fil secundari
        new Thread(() -> {
            try {
                String response;

                if (isTestEnvironment) {
                    // Simular resposta del servidor en mode proves
                    response = MockServer.simulateCreateUserRequest();
                } else {
                    // Comunicació real amb el servidor
                    try (Socket socket = new Socket("10.0.2.2", 12345);
                         PrintWriter commandOut = new PrintWriter(socket.getOutputStream(), true)) {

                        commandOut.println("ADD_USER");

                        ObjectOutputStream objectOut = new ObjectOutputStream(socket.getOutputStream());
                        objectOut.writeObject(newUser);
                        objectOut.flush();

                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        response = in.readLine();
                    }
                }

                // Processar resposta del servidor
                if ("USER_CREATED".equals(response)) {
                    runOnUiThread(() -> Snackbar.make(findViewById(android.R.id.content), "Usuari creat correctament", Snackbar.LENGTH_SHORT).show());
                } else if (response != null && (response.contains("unique") || response.contains("duplicate"))) {
                    runOnUiThread(() -> Toast.makeText(UserCreate.this, "El nom d'usuari ja existeix!", Toast.LENGTH_LONG).show());
                } else {
                    runOnUiThread(() -> Toast.makeText(UserCreate.this, "Error creant l'usuari: " + response, Toast.LENGTH_SHORT).show());
                }

            } catch (Exception e) {
                Log.e("UserCreate", "Error creant l'usuari", e);
                runOnUiThread(() -> Toast.makeText(UserCreate.this, "Error creant l'usuari: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}
