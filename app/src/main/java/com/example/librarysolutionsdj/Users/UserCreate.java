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

public class UserCreate extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText, realnameEditText, surname1EditText, surname2EditText;
    private Spinner userTypeSpinner;
    private Button createButton;
    private ImageButton backButton;

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
        backButton = findViewById(R.id.back_button); // Botón de volver

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.user_types_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userTypeSpinner.setAdapter(adapter);

        createButton.setOnClickListener(v -> createUser());

        // Configurar el botón de volver atrás
        backButton.setOnClickListener(v -> finish());
    }

    private boolean isTestEnvironment = false; // Bandera para indicar el entorno de prueba

    // Método para configurar el entorno de prueba
    public void setTestEnvironment(boolean isTestEnvironment) {
        this.isTestEnvironment = isTestEnvironment;
    }


    private void createUser() {
        // Recoger datos del formulario
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String realname = realnameEditText.getText().toString().trim();
        String surname1 = surname1EditText.getText().toString().trim();
        String surname2 = surname2EditText.getText().toString().trim();
        String userType = userTypeSpinner.getSelectedItem().toString();

        // Validación de campos requeridos
        if (username.isEmpty() || password.isEmpty() || realname.isEmpty() || surname1.isEmpty()) {
            Snackbar.make(findViewById(android.R.id.content), "Si us plau, omple tots els camps requerits.", Snackbar.LENGTH_LONG).show();
            return;
        }

        User newUser = new User(0, username, password, realname, surname1, surname2, User.stringToUserType(userType));

        new Thread(() -> {
            try {
                String response;

                if (isTestEnvironment) {
                    // Simular respuesta del servidor en entorno de pruebas
                    response = MockServer.simulateCreateUserRequest();
                } else {
                    // Comunicación real con el servidor
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

                // Procesar la respuesta
                if ("USER_CREATED".equals(response)) {
                    runOnUiThread(() -> {
                        Snackbar.make(findViewById(android.R.id.content), "Usuari creat correctament", Snackbar.LENGTH_SHORT).show();
                    });
                } else if (response != null && (response.contains("unique") || response.contains("duplicate"))) {
                    // Mensaje específico si el username ya existe
                    runOnUiThread(() -> Toast.makeText(UserCreate.this, "Username already exists!", Toast.LENGTH_LONG).show());
                } else {
                    // Otros errores generales
                    runOnUiThread(() -> Toast.makeText(UserCreate.this, "Error creating user: " + response, Toast.LENGTH_SHORT).show());
                }

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(UserCreate.this, "Error creating user: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

}
