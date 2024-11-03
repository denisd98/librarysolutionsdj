package com.example.librarysolutionsdj;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class UserDetailActivity extends AppCompatActivity {

    private EditText userIdTextView, usernameTextView, realnameTextView, surname1TextView, surname2TextView, userTypeTextView;
    private Button editButton, saveButton, cancelButton;
    private String userId;  // Variable para almacenar el ID del usuario seleccionado

    // Variables para guardar los valores originales
    private String originalUsername, originalRealname, originalSurname1, originalSurname2, originalUserType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        // Configurar el botón "Back" para finalizar la actividad y regresar a la anterior
        ImageButton backButton = findViewById(R.id.back);
        backButton.setOnClickListener(view -> finish());

        // Inicializar vistas
        userIdTextView = findViewById(R.id.user_detail_id);
        usernameTextView = findViewById(R.id.user_detail_username);
        realnameTextView = findViewById(R.id.user_detail_realname);
        surname1TextView = findViewById(R.id.user_detail_surname1);
        surname2TextView = findViewById(R.id.user_detail_surname2);
        userTypeTextView = findViewById(R.id.user_detail_usertype);
        editButton = findViewById(R.id.edit_button);
        saveButton = findViewById(R.id.save_button);
        cancelButton = findViewById(R.id.cancel_button);

        // Ocultar botones de guardar y cancelar al inicio
        saveButton.setVisibility(Button.GONE);
        cancelButton.setVisibility(Button.GONE);

        // Obtener el ID del usuario pasado por el Intent
        userId = getIntent().getStringExtra("userId");

        // Cargar el perfil del usuario desde el servidor
        getUserProfile();

        // Botón para habilitar la edición de campos
        editButton.setOnClickListener(v -> enableEditing());

        // Botón para guardar los cambios
        saveButton.setOnClickListener(v -> saveChanges());

        // Botón para cancelar los cambios
        cancelButton.setOnClickListener(v -> cancelEditing());
    }

    private void getUserProfile() {
        new Thread(() -> {
            try {
                if (userId == null) {
                    runOnUiThread(() -> Toast.makeText(UserDetailActivity.this, "ID de usuario no válido", Toast.LENGTH_SHORT).show());
                    return;
                }

                // Conexión al servidor para obtener el perfil del usuario seleccionado
                Socket socket = new Socket("10.0.2.2", 12345); // IP de la máquina virtual Android Studio
                PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                // Enviar el comando "GET_USER_PROFILE" y el ID del usuario
                out.println("GET_USER_PROFILE");
                out.println(userId);

                // Leer la respuesta del servidor con los datos del usuario
                String receivedUserId = in.readLine();
                String username = in.readLine();
                String realname = in.readLine();
                String surname1 = in.readLine();
                String surname2 = in.readLine();
                String userType = in.readLine();

                // Guardar los valores originales
                originalUsername = username;
                originalRealname = realname;
                originalSurname1 = surname1;
                originalSurname2 = surname2;
                originalUserType = userType;

                // Mostrar los datos en la interfaz de usuario
                runOnUiThread(() -> {
                    userIdTextView.setText(receivedUserId);
                    usernameTextView.setText(username);
                    realnameTextView.setText(realname);
                    surname1TextView.setText(surname1);
                    surname2TextView.setText(surname2);
                    userTypeTextView.setText(userType);
                });

                socket.close(); // Cerrar la conexión

            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(UserDetailActivity.this, "Error al obtener el perfil: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void enableEditing() {
        // Habilitar los campos para edición (excepto el ID, que no se debe cambiar)
        usernameTextView.setEnabled(true);
        realnameTextView.setEnabled(true);
        surname1TextView.setEnabled(true);
        surname2TextView.setEnabled(true);
        userTypeTextView.setEnabled(true);

        // Cambiar el fondo de los campos a un color diferente para indicar que están en modo de edición
        int backgroundColor = Color.parseColor("#FFFFE0"); // Color amarillo claro
        usernameTextView.setBackgroundColor(backgroundColor);
        realnameTextView.setBackgroundColor(backgroundColor);
        surname1TextView.setBackgroundColor(backgroundColor);
        surname2TextView.setBackgroundColor(backgroundColor);
        userTypeTextView.setBackgroundColor(backgroundColor);

        // Mostrar botones de guardar y cancelar y ocultar botón de editar
        editButton.setVisibility(Button.GONE);
        saveButton.setVisibility(Button.VISIBLE);
        cancelButton.setVisibility(Button.VISIBLE);
    }

    private void saveChanges() {
        new Thread(() -> {
            try {
                // Obtener el sessionId y tipo de usuario de las preferencias
                SharedPreferences preferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                String sessionId = preferences.getString("SESSION_ID", null);
                String userType = preferences.getString("USER_TYPE", null);

                if (sessionId == null || userType == null || !userType.equals("ADMIN")) {
                    runOnUiThread(() -> Toast.makeText(UserDetailActivity.this, "Acceso denegado: solo los administradores pueden guardar cambios", Toast.LENGTH_SHORT).show());
                    return;
                }

                // Conexión al servidor para enviar los cambios
                Socket socket = new Socket("10.0.2.2", 12345); // IP de la máquina virtual Android Studio
                PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                // Enviar el comando "UPDATE_USER_PROFILE" y el sessionId seguido de los datos modificados del usuario
                out.println("UPDATE_USER_PROFILE");
                out.println(sessionId);
                out.println(userId);
                out.println(usernameTextView.getText().toString().trim());
                out.println(realnameTextView.getText().toString().trim());
                out.println(surname1TextView.getText().toString().trim());
                out.println(surname2TextView.getText().toString().trim());
                out.println(userTypeTextView.getText().toString().trim());

                // Leer la respuesta del servidor
                String response = in.readLine();
                runOnUiThread(() -> {
                    if ("UPDATE_SUCCESS".equals(response)) {
                        Toast.makeText(UserDetailActivity.this, "Cambios guardados exitosamente", Toast.LENGTH_SHORT).show();

                        // Actualizar los valores originales después de guardar
                        originalUsername = usernameTextView.getText().toString().trim();
                        originalRealname = realnameTextView.getText().toString().trim();
                        originalSurname1 = surname1TextView.getText().toString().trim();
                        originalSurname2 = surname2TextView.getText().toString().trim();
                        originalUserType = userTypeTextView.getText().toString().trim();
                    } else {
                        Toast.makeText(UserDetailActivity.this, "Error al guardar los cambios: " + response, Toast.LENGTH_SHORT).show();
                    }

                    // Deshabilitar los campos nuevamente y mostrar botón de editar
                    disableEditing();
                });

                socket.close(); // Cerrar la conexión

            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(UserDetailActivity.this, "Error al guardar los cambios: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void cancelEditing() {
        // Restaurar los valores originales
        usernameTextView.setText(originalUsername);
        realnameTextView.setText(originalRealname);
        surname1TextView.setText(originalSurname1);
        surname2TextView.setText(originalSurname2);
        userTypeTextView.setText(originalUserType);

        // Deshabilitar los campos y mostrar el botón de editar
        disableEditing();
    }

    private void disableEditing() {
        usernameTextView.setEnabled(false);
        realnameTextView.setEnabled(false);
        surname1TextView.setEnabled(false);
        surname2TextView.setEnabled(false);
        userTypeTextView.setEnabled(false);

        // Restaurar el fondo de los campos a su color original
        int originalBackgroundColor = Color.TRANSPARENT; // Fondo transparente o color original
        usernameTextView.setBackgroundColor(originalBackgroundColor);
        realnameTextView.setBackgroundColor(originalBackgroundColor);
        surname1TextView.setBackgroundColor(originalBackgroundColor);
        surname2TextView.setBackgroundColor(originalBackgroundColor);
        userTypeTextView.setBackgroundColor(originalBackgroundColor);

        // Ocultar botones de guardar y cancelar y mostrar botón de editar
        saveButton.setVisibility(Button.GONE);
        cancelButton.setVisibility(Button.GONE);
        editButton.setVisibility(Button.VISIBLE);
    }
}
