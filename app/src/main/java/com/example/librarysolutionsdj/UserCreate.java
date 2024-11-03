package com.example.librarysolutionsdj;

import android.content.SharedPreferences;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class UserCreate extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText, realnameEditText, surname1EditText, surname2EditText;
    private Spinner userTypeSpinner;
    private Button createButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_create);

        // Configurar el botón "Back" para finalizar la actividad y regresar a la anterior
        ImageButton backButton = findViewById(R.id.back);
        backButton.setOnClickListener(view -> finish());

        // Inicializar vistas
        usernameEditText = findViewById(R.id.new_username);
        passwordEditText = findViewById(R.id.new_password);
        realnameEditText = findViewById(R.id.new_realname);
        surname1EditText = findViewById(R.id.new_surname1);
        surname2EditText = findViewById(R.id.new_surname2);
        userTypeSpinner = findViewById(R.id.new_user_type_spinner);
        createButton = findViewById(R.id.create_new_user_btn);

        // Configurar el Spinner con las opciones de tipo de usuario
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.user_types_array, // Este array debe definirse en res/values/arrays.xml
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userTypeSpinner.setAdapter(adapter);

        // Configurar el botón de crear usuario
        createButton.setOnClickListener(view -> createUser());
    }

    private void createUser() {
        new Thread(() -> {
            try {
                // Obtener el sessionId de las preferencias para validar la sesión
                SharedPreferences preferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                String sessionId = preferences.getString("SESSION_ID", null);
                String userType = preferences.getString("USER_TYPE", null);

                // Verificar que el usuario sea un ADMIN y que la sesión sea válida
                if (sessionId == null || userType == null || !userType.equals("ADMIN")) {
                    runOnUiThread(() -> Toast.makeText(UserCreate.this, "Acceso denegado: solo los administradores pueden crear usuarios", Toast.LENGTH_SHORT).show());
                    return;
                }

                // Conexión al servidor para enviar los datos del nuevo usuario
                Socket socket = new Socket("10.0.2.2", 12345); // IP de la máquina virtual Android Studio
                PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                // Enviar el comando "CREATE_USER" y el sessionId junto con los datos del nuevo usuario
                out.println("CREATE_USER");
                out.println(sessionId); // Enviar el sessionId para validar la sesión en el servidor
                out.println(usernameEditText.getText().toString().trim());
                out.println(passwordEditText.getText().toString().trim());
                out.println(realnameEditText.getText().toString().trim());
                out.println(surname1EditText.getText().toString().trim());
                out.println(surname2EditText.getText().toString().trim());
                out.println(userTypeSpinner.getSelectedItem().toString().trim());

                // Leer la respuesta del servidor
                String response = in.readLine();
                runOnUiThread(() -> {
                    if ("CREATE_SUCCESS".equals(response)) {
                        Toast.makeText(UserCreate.this, "Usuario creado exitosamente", Toast.LENGTH_SHORT).show();
                        // Devolver resultado OK a GestioUsuaris
                        setResult(RESULT_OK);
                        finish(); // Volver a la pantalla anterior
                    } else {
                        Toast.makeText(UserCreate.this, "Error al crear usuario: " + response, Toast.LENGTH_SHORT).show();
                    }
                });

                socket.close(); // Cerrar la conexión

            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(UserCreate.this, "Error al crear usuario: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}
