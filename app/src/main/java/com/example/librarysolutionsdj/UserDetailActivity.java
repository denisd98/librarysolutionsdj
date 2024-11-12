// UserDetailActivity.java
package com.example.librarysolutionsdj;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import app.model.User;

public class UserDetailActivity extends AppCompatActivity {

    private EditText usernameEditText, realnameEditText, surname1EditText, surname2EditText;
    private Spinner userTypeSpinner;
    private Button saveButton, deleteButton;
    private ImageButton backButton;
    private User selectedUser;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        // Inicializar SessionManager
        sessionManager = new SessionManager(this);

        // Recuperar el usuario seleccionado desde el Intent
        selectedUser = (User) getIntent().getSerializableExtra("selectedUser");

        // Inicializar los elementos de la interfaz
        usernameEditText = findViewById(R.id.username_edit_text);
        realnameEditText = findViewById(R.id.realname_edit_text);
        surname1EditText = findViewById(R.id.surname1_edit_text);
        surname2EditText = findViewById(R.id.surname2_edit_text);
        userTypeSpinner = findViewById(R.id.user_type_spinner);
        saveButton = findViewById(R.id.save_button);
        backButton = findViewById(R.id.back_button); // Botón de volver
        deleteButton = findViewById(R.id.delete_button); // Botón de eliminar usuario

        // Rellenar los campos con los datos actuales del usuario
        populateFields();

        // Configurar el botón de guardar
        saveButton.setOnClickListener(v -> saveChanges());

        // Configurar el botón de volver atrás
        backButton.setOnClickListener(v -> finish());

        // Configurar el botón de eliminar usuario
        deleteButton.setOnClickListener(v -> deleteUser());
    }

    private void populateFields() {
        if (selectedUser != null) {
            usernameEditText.setText(selectedUser.getUsername());
            realnameEditText.setText(selectedUser.getRealname());
            surname1EditText.setText(selectedUser.getSurname1());
            surname2EditText.setText(selectedUser.getSurname2());

            // Selecciona el tipo de usuario en el Spinner
            String userType = selectedUser.getTypeAsString();
            String[] userTypes = getResources().getStringArray(R.array.user_types);
            for (int i = 0; i < userTypes.length; i++) {
                if (userTypes[i].equals(userType)) {
                    userTypeSpinner.setSelection(i);
                    break;
                }
            }
        }
    }

    private void saveChanges() {
        // Actualizar el objeto usuario con los nuevos datos
        selectedUser.setUsername(usernameEditText.getText().toString());
        selectedUser.setRealname(realnameEditText.getText().toString());
        selectedUser.setSurname1(surname1EditText.getText().toString());
        selectedUser.setSurname2(surname2EditText.getText().toString());
        selectedUser.setType(User.stringToUserType(userTypeSpinner.getSelectedItem().toString()));

        // Enviar los datos actualizados al servidor
        new Thread(() -> {
            try {
                // Única conexión al servidor
                Socket socket = new Socket("10.0.2.2", 12345);

                // Enviar el comando "MODIFY_USER" primero usando PrintWriter
                PrintWriter commandOut = new PrintWriter(socket.getOutputStream(), true);
                commandOut.println("MODIFY_USER");

                // Crear un ObjectOutputStream después de enviar el comando
                ObjectOutputStream objectOut = new ObjectOutputStream(socket.getOutputStream());

                // Enviar el ID del usuario y el objeto User
                objectOut.writeInt(selectedUser.getId());
                objectOut.writeObject(selectedUser);
                objectOut.flush();

                runOnUiThread(() -> Toast.makeText(UserDetailActivity.this, "Changes saved successfully", Toast.LENGTH_SHORT).show());

                // Cerrar conexiones
                commandOut.close();
                objectOut.close();
                socket.close();

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(UserDetailActivity.this, "Error saving changes: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void deleteUser() {
        int currentUserId = sessionManager.getUserId();

        if (selectedUser.getId() == currentUserId) {
            Toast.makeText(this, "Cannot delete the current logged-in user.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Enviar solicitud de eliminación al servidor
        new Thread(() -> {
            try {
                Socket socket = new Socket("10.0.2.2", 12345);

                // Enviar el comando "DELETE_USER" primero usando PrintWriter
                PrintWriter commandOut = new PrintWriter(socket.getOutputStream(), true);
                commandOut.println("DELETE_USER");

                // Crear un ObjectOutputStream después de enviar el comando
                ObjectOutputStream objectOut = new ObjectOutputStream(socket.getOutputStream());

                // Enviar solo el ID del usuario a eliminar
                objectOut.writeInt(selectedUser.getId());
                objectOut.flush();

                runOnUiThread(() -> {
                    Toast.makeText(UserDetailActivity.this, "User deleted successfully", Toast.LENGTH_SHORT).show();
                    finish(); // Cierra la actividad después de la eliminación
                });

                // Cerrar conexiones
                commandOut.close();
                objectOut.close();
                socket.close();

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(UserDetailActivity.this, "Error deleting user: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}
