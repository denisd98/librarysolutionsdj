package com.example.librarysolutionsdj.Media;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.librarysolutionsdj.Media.MediaAdapter;
import com.example.librarysolutionsdj.Media.MediaCreate;
import com.example.librarysolutionsdj.Media.MediaDetailActivity;
import com.example.librarysolutionsdj.R;
import com.example.librarysolutionsdj.ServerConnection.ServerConnectionHelper;
import com.example.librarysolutionsdj.SessionManager.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import app.crypto.CryptoUtils;
import app.model.Media; // Asegúrate de tener una clase Media que implementa Serializable
import app.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * La classe GestioMedia permet gestionar la visualització de tots els mitjans registrats en el sistema.
 * Aquesta classe carrega la llista de mitjans des d'un servidor remot i la mostra en un ListView.
 */
public class GestioMedia extends AppCompatActivity {
    private static final String PASSWORD = CryptoUtils.getGenericPassword(); // Contraseña genérica para cifrado

    // Vista de la llista de mitjans i llista de mitjans
    private ListView mediaListView;
    private ArrayList<Media> mediaList;
    private SessionManager sessionManager;
    private ImageButton backButton; // Declaración del botón de volver
    private FloatingActionButton addMediaButton; // Botón flotante para añadir medio

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestio_media);
        EdgeToEdge.enable(this);

        mediaListView = findViewById(R.id.media_list_view);
        mediaList = new ArrayList<>();
        sessionManager = new SessionManager(this);
        backButton = findViewById(R.id.back_button); // Inicialización del botón de volver
        addMediaButton = findViewById(R.id.fab_add_media); // Inicialización del botón flotante

        // Configurar el botón de volver atrás
        backButton.setOnClickListener(v -> finish());

        // Configurar el botón flotante para añadir un nuevo medio
        addMediaButton.setOnClickListener(v -> {
            Intent intent = new Intent(GestioMedia.this, MediaCreate.class);
            startActivity(intent);
        });

        // Configurar el listener de clic para cada elemento en la lista
        mediaListView.setOnItemClickListener((parent, view, position, id) -> {
            Media selectedMedia = mediaList.get(position);
            Intent intent = new Intent(GestioMedia.this, MediaDetailActivity.class);
            intent.putExtra("selectedMedia", selectedMedia);  // Pasar el objeto Media completo
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getAllMedia(); // Recarga la lista de medios desde el servidor al volver a la actividad
    }

    /**
     * Método que obtiene la lista de todos los medios conectándose a un servidor remoto.
     * Utiliza una conexión de socket y ObjectInputStream para deserializar la lista de medios.
     */
    private void getAllMedia() {
        new Thread(() -> {
            String sessionId = sessionManager.getSessionId();
            if (sessionId == null) {
                runOnUiThread(() -> Toast.makeText(GestioMedia.this, "No hi ha sessió activa", Toast.LENGTH_SHORT).show());
                return;
            }

            ServerConnectionHelper connection = new ServerConnectionHelper();
            try {
                connection.connect();
                connection.sendEncryptedCommand("GET_ALL_MEDIA");
                System.out.println("Comando 'GET_ALL_MEDIA' enviado.");

                // Recibir la lista de medios
                List<Media> receivedMediaList = (List<Media>) connection.receiveEncryptedObject();
                System.out.println("Lista de medios recibida.");

                // Actualizar la lista principal
                runOnUiThread(() -> {
                    mediaList.clear();
                    mediaList.addAll(receivedMediaList);
                    MediaAdapter adapter = new MediaAdapter(GestioMedia.this, mediaList);
                    mediaListView.setAdapter(adapter);
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(GestioMedia.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            } finally {
                connection.close();
            }
        }).start();
    }

}
