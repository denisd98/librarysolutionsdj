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
 * Classe per gestionar la visualització de totes les obres (Media) registrades en el sistema.
 * Aquesta activitat permet obtenir una llista de totes les obres des del servidor i visualitzar-les en un ListView.
 */
public class GestioMedia extends AppCompatActivity {

    private ListView mediaListView;
    private ArrayList<Media> mediaList;
    private SessionManager sessionManager;
    private ImageButton backButton;
    private FloatingActionButton addMediaButton;

    /**
     * Mètode que s'executa en crear l'activitat. Configura la interfície d'usuari i inicialitza la llista d'obres.
     *
     * @param savedInstanceState L'estat guardat de l'activitat, si n'hi ha.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestio_media);
        EdgeToEdge.enable(this);

        // Inicialitzar els components de la interfície d'usuari
        mediaListView = findViewById(R.id.media_list_view);
        mediaList = new ArrayList<>();
        sessionManager = new SessionManager(this);
        backButton = findViewById(R.id.back_button);
        addMediaButton = findViewById(R.id.fab_add_media);

        // Configurar el botó de tornar enrere
        backButton.setOnClickListener(v -> finish());

        /*
        // Configurar el botó flotant per afegir una nova obra
        addMediaButton.setOnClickListener(v -> {
            Intent intent = new Intent(GestioMedia.this, MediaCreate.class);
            startActivity(intent);
        });
        */

        // Configurar el listener de clic per cada element de la llista
        mediaListView.setOnItemClickListener((parent, view, position, id) -> {
            Media selectedMedia = mediaList.get(position);
            Intent intent = new Intent(GestioMedia.this, MediaDetailActivity.class);
            intent.putExtra("selectedMedia", selectedMedia); // Passar l'objecte Media complet
            startActivity(intent);
        });
    }

    /**
     * Mètode que s'executa quan l'activitat torna a estar en primer pla.
     * Recàrrega la llista d'obres des del servidor.
     */
    @Override
    protected void onResume() {
        super.onResume();
        getAllMedia(); // Recàrrega la llista d'obres
    }

    /**
     * Mètode per obtenir totes les obres connectant-se a un servidor remot.
     * Utilitza sockets per enviar un comandament i rebre una llista d'obres.
     */
    private void getAllMedia() {
        new Thread(() -> {
            try {
                // Comprovar si hi ha una sessió activa
                String sessionId = sessionManager.getSessionId();
                if (sessionId == null) {
                    runOnUiThread(() -> Toast.makeText(GestioMedia.this, "No hi ha sessió activa", Toast.LENGTH_SHORT).show());
                    return;
                }

                // Conexió al servidor
                Socket socket = new Socket("10.0.2.2", 12345);
                PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
                out.println("GET_ALL_MEDIA");

                // Llegir la resposta del servidor
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                int mediaCount = in.readInt(); // Nombre total d'obres
                mediaList.clear();

                // Afegir les obres a la llista
                for (int i = 0; i < mediaCount; i++) {
                    Media media = (Media) in.readObject();
                    mediaList.add(media);
                }

                // Actualitzar la interfície d'usuari
                runOnUiThread(() -> {
                    MediaAdapter adapter = new MediaAdapter(GestioMedia.this, mediaList);
                    mediaListView.setAdapter(adapter);
                });

                // Tancar les connexions
                in.close();
                out.close();
                socket.close();

            } catch (Exception e) {
                // Mostrar un missatge d'error en cas de fallada
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(GestioMedia.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }
}
