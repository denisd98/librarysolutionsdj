package com.example.librarysolutionsdj.Media;

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

import app.model.Media;

import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Clase para gestionar la visualización de todas las obras (Media) registradas en el sistema.
 */
public class GestioMedia extends AppCompatActivity {

    private ListView mediaListView;
    private ArrayList<Media> mediaList;
    private SessionManager sessionManager;
    private ImageButton backButton;
    private FloatingActionButton addMediaButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestio_media);
        EdgeToEdge.enable(this);

        mediaListView = findViewById(R.id.media_list_view);
        mediaList = new ArrayList<>();
        sessionManager = new SessionManager(this);
        backButton = findViewById(R.id.back_button);
        addMediaButton = findViewById(R.id.fab_add_media);

        // Configurar botón de volver
        backButton.setOnClickListener(v -> finish());
/*
        // Configurar botón flotante para añadir una nueva obra
        addMediaButton.setOnClickListener(v -> {
            Intent intent = new Intent(GestioMedia.this, MediaCreate.class);
            startActivity(intent);
        });
        */

        // Configurar clic en un elemento de la lista
        mediaListView.setOnItemClickListener((parent, view, position, id) -> {
            Media selectedMedia = mediaList.get(position);
            Intent intent = new Intent(GestioMedia.this, MediaDetailActivity.class);
            intent.putExtra("selectedMedia", selectedMedia); // Pasar el objeto Media completo
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getAllMedia(); // Recargar la lista de obras desde el servidor al volver a la actividad
    }

    /**
     * Método para obtener todas las obras conectándose al servidor remoto.
     */
    private void getAllMedia() {
        new Thread(() -> {
            try {
                // Verificar si hay sesión activa
                String sessionId = sessionManager.getSessionId();
                if (sessionId == null) {
                    runOnUiThread(() -> Toast.makeText(GestioMedia.this, "No hay sesión activa", Toast.LENGTH_SHORT).show());
                    return;
                }

                // Conexión al servidor
                Socket socket = new Socket("10.0.2.2", 12345);
                PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
                out.println("GET_ALL_MEDIA");

                // Leer respuesta del servidor
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                int mediaCount = in.readInt(); // Número total de obras
                mediaList.clear();

                for (int i = 0; i < mediaCount; i++) {
                    Media media = (Media) in.readObject();
                    mediaList.add(media);
                }

                runOnUiThread(() -> {
                    MediaAdapter adapter = new MediaAdapter(GestioMedia.this, mediaList);
                    mediaListView.setAdapter(adapter);
                });

                in.close();
                out.close();
                socket.close();

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(GestioMedia.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }
}
