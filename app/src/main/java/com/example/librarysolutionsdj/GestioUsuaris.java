package com.example.librarysolutionsdj;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
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
    private UserAdapter adapter;
    private List<User> userList = new ArrayList<>();

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
                // Conexión al servidor
                Socket socket = new Socket("10.0.2.2", 12345);
                PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                // Enviar el comando para obtener usuarios
                out.println("GET_USERS");

                // Leer el número de usuarios
                int userCount = Integer.parseInt(in.readLine());

                // Leer los datos de cada usuario
                for (int i = 0; i < userCount; i++) {
                    String id = in.readLine();
                    String username = in.readLine();
                    String realname = in.readLine();
                    // Crear un objeto User con los datos recibidos del servidor
                    userList.add(new User(id, username, realname));
                }

                // Actualizar el RecyclerView en la interfaz de usuario principal
                runOnUiThread(() -> {
                    adapter = new UserAdapter(userList);
                    recyclerView.setAdapter(adapter);
                });

                socket.close();

            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(GestioUsuaris.this, "Error al obtener usuarios: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}
