package com.example.librarysolutionsdj;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class GestioUsuaris extends AppCompatActivity {
    private RecyclerView recyclerView;
    private UserListAdapter adapter;
    private List<UserList> userList = new ArrayList<>(); // Cambiado a UserList

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gestio_usuaris);

        recyclerView = findViewById(R.id.user_recyclerview_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Configurar el botón "Back" para finalizar la actividad y regresar a la anterior
        ImageButton backButton = findViewById(R.id.back);
        backButton.setOnClickListener(view -> finish());

        // Añadir línea divisoria entre los elementos
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        // Cargar usuarios desde el servidor
        loadUsersFromServer();
    }

    private void loadUsersFromServer() {
        new Thread(() -> {
            try {
                // Obtener el sessionId de SharedPreferences
                SharedPreferences preferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                String sessionId = preferences.getString("SESSION_ID", null);

                if (sessionId == null) {
                    runOnUiThread(() -> Toast.makeText(GestioUsuaris.this, "No hi ha sessió activa", Toast.LENGTH_SHORT).show());
                    return;
                }

                // Conexión al servidor
                Socket socket = new Socket("10.0.2.2", 12345);
                PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                // Enviar el comando para obtener el listado de usuarios con el sessionId
                out.println("GET_USERS_LIST");
                out.println(sessionId);

                // Leer el número de usuarios
                int userCount = Integer.parseInt(in.readLine());

                // Leer los datos de cada usuario
                for (int i = 0; i < userCount; i++) {
                    String id = in.readLine();
                    String username = in.readLine();

                    // Crear un objeto UserList con los datos recibidos del servidor
                    userList.add(new UserList(id, username));
                }

                // Actualizar el RecyclerView en la interfaz de usuario principal
                runOnUiThread(() -> {
                    adapter = new UserListAdapter(userList);
                    recyclerView.setAdapter(adapter);
                });

                socket.close();

            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(GestioUsuaris.this, "Error al obtenir usuaris: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}
