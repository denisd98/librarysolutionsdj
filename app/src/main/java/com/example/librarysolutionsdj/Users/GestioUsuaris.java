package com.example.librarysolutionsdj.Users;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.librarysolutionsdj.R;
import com.example.librarysolutionsdj.ServerConnection.ServerConnectionHelper;
import com.example.librarysolutionsdj.SessionManager.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import app.crypto.CryptoUtils;
import app.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * La classe GestioUsuaris permet gestionar la visualització de tots els usuaris registrats en el sistema.
 * Aquesta classe carrega la llista d'usuaris des d'un servidor remot i la mostra en un ListView.
 */
public class GestioUsuaris extends AppCompatActivity {
    private static final String PASSWORD = CryptoUtils.getGenericPassword(); // Contraseña genérica para cifrado

    // Vista de la llista d'usuaris i llista d'usuaris
    private ListView userListView;
    private ArrayList<User> userList;
    private SessionManager sessionManager;
    private UserService userService;
    private ImageButton backButton; // Declaración del botón de volver
    private FloatingActionButton addUserButton; // Botón flotante para añadir usuario

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestio_usuaris);
        EdgeToEdge.enable(this);

        userListView = findViewById(R.id.user_list_view);
        userList = new ArrayList<>();
        sessionManager = new SessionManager(this);
        userService = new UserService();
        backButton = findViewById(R.id.back_button); // Inicialización del botón de volver
        addUserButton = findViewById(R.id.fab_add_user); // Inicialización del botón flotante

        // Configurar el botón de volver atrás
        backButton.setOnClickListener(v -> finish());

        // Configurar el botón flotante para añadir un nuevo usuario
        addUserButton.setOnClickListener(v -> {
            Intent intent = new Intent(GestioUsuaris.this, UserCreate.class);
            startActivity(intent);
        });

        // Configurar el listener de clic para cada elemento en la lista
        userListView.setOnItemClickListener((parent, view, position, id) -> {
            User selectedUser = (User) parent.getItemAtPosition(position);
            Intent intent = new Intent(GestioUsuaris.this, UserDetailActivity.class);
            intent.putExtra("selectedUser", selectedUser);  // Pasar el objeto User completo
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getAllUsers(); // Recarga la lista de usuarios desde el servidor al volver a la actividad
    }

    /**
     * Método que obtiene la lista de todos los usuarios conectándose a un servidor remoto.
     * Utiliza una conexión de socket y ObjectInputStream para deserializar la lista de usuarios.
     */
    private void getAllUsers() {
        new Thread(() -> {
            String sessionId = sessionManager.getSessionId();
            if (sessionId == null) {
                runOnUiThread(() -> Toast.makeText(GestioUsuaris.this, "No hi ha sessió activa", Toast.LENGTH_SHORT).show());
                return;
            }

            // Utilizamos ServerConnectionHelper para simplificar el código
            ServerConnectionHelper connection = new ServerConnectionHelper();
            try {
                connection.connect(); // Establece la conexión con el servidor

                // Enviar el comando "GET_ALL_USERS" cifrado
                connection.sendEncryptedCommand("GET_ALL_USERS");
                System.out.println("Comando 'GET_ALL_USERS' enviado.");

                // Recibir la lista de usuarios cifrada y deserializada
                List<User> users = (List<User>) connection.receiveEncryptedObject();
                System.out.println("Lista de usuarios recibida.");

                // Actualizar la interfaz de usuario
                runOnUiThread(() -> {
                    UserAdapter adapter = new UserAdapter(GestioUsuaris.this, (ArrayList<User>) users);
                    userListView.setAdapter(adapter);
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(GestioUsuaris.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            } finally {
                connection.close();
            }
        }).start();
    }
}
