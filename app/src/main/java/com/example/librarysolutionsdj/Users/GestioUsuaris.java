package com.example.librarysolutionsdj.Users;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.librarysolutionsdj.R;
import com.example.librarysolutionsdj.SessionManager.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import app.model.User;

import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

/**
 * La classe GestioUsuaris permet gestionar la visualització de tots els usuaris registrats en el sistema.
 * Aquesta classe carrega la llista d'usuaris des d'un servidor remot i la mostra en un ListView.
 */
public class GestioUsuaris extends AppCompatActivity {

    // Vista de la llista d'usuaris i llista d'usuaris
    private ListView userListView;
    private ArrayList<User> userList;
    private SessionManager sessionManager;
    private UserService userService;
    private ImageButton backButton; // Declaración del botón de volver
    private FloatingActionButton addUserButton; // Botón flotante para añadir usuario

    /**
     * Mètode que s'executa en crear l'activitat. Configura la interfície d'usuari i inicialitza la llista d'usuaris.
     *
     * @param savedInstanceState l'estat de l'activitat guardat anteriorment.
     */
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
            User selectedUser = userList.get(position);
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
     * Mètode que obté la llista de tots els usuaris connectant-se a un servidor remot.
     * Utilitza una connexió de socket i un ObjectInputStream per deserialitzar la llista d'usuaris.
     */
    private void getAllUsers() {
        new Thread(() -> {
            try {
                // Recuperar el ID de sesión usando SessionManager
                String sessionId = sessionManager.getSessionId();

                // Comprobar si hay una sesión activa
                if (sessionId == null) {
                    runOnUiThread(() -> Toast.makeText(GestioUsuaris.this, "No hi ha sessió activa", Toast.LENGTH_SHORT).show());
                    return;
                }

                // Conexión al servidor para obtener la lista de usuarios
                Socket socket = new Socket("10.0.2.2", 12345); // IP de la máquina virtual de Android Studio
                PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
                out.println("GET_ALL_USERS");

                // Lectura del objeto usuarios directamente
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

                // Lee directamente la lista completa de usuarios
                userList = (ArrayList<User>) in.readObject();

                // Actualiza la interfaz de usuario con la lista de usuarios
                runOnUiThread(() -> {
                    UserAdapter adapter = new UserAdapter(GestioUsuaris.this, userList);
                    userListView.setAdapter(adapter);
                });

                // Cerramos las conexiones
                in.close();
                out.close();
                socket.close();

            } catch (Exception e) {
                // En caso de error, muestra el mensaje de error
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(GestioUsuaris.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }
}
