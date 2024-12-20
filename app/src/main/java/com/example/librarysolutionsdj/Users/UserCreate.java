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
import com.example.librarysolutionsdj.ServerConnection.ServerConnectionHelper;
import com.google.android.material.snackbar.Snackbar;

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
        backButton = findViewById(R.id.back_button5);

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
                    // Simular respuesta del servidor en modo prueba
                    response = MockServer.simulateCreateUserRequest();
                    runOnUiThread(() -> Snackbar.make(findViewById(android.R.id.content), "Usuari creat correctament (mock)", Snackbar.LENGTH_LONG).show());
                } else {
                    // Comunicación real con el servidor usando ServerConnectionHelper
                    ServerConnectionHelper connection = new ServerConnectionHelper();
                    try {
                        connection.connect(); // Conectar con el servidor
                        connection.sendEncryptedCommand("ADD_USER"); // Enviar el comando cifrado

                        // Ahora enviamos el objeto del usuario cifrado
                        connection.sendEncryptedObject(newUser);

                        // Confirmar éxito en la IU
                        runOnUiThread(() -> Snackbar.make(findViewById(android.R.id.content), "Usuari creat correctament", Snackbar.LENGTH_LONG).show());
                    } catch (Exception e) {
                        Log.e("UserCreate", "Error creant l'usuari", e);
                        runOnUiThread(() -> Toast.makeText(UserCreate.this, "Error creant l'usuari: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    } finally {
                        connection.close(); // Cerrar la conexión
                    }
                }
            } catch (Exception e) {
                Log.e("UserCreate", "Error creant l'usuari", e);
                runOnUiThread(() -> Toast.makeText(UserCreate.this, "Error creant l'usuari: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();


    }
}
