package com.example.librarysolutionsdj.Authors;

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

import app.model.Author;

import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Classe que gestiona la visualització i administració dels autors registrats al sistema.
 * Aquesta classe permet visualitzar una llista d'autors, afegir-ne de nous i editar-ne els existents.
 */
public class GestioAutors extends AppCompatActivity {

    // Vista de la llista d'autors i components de la interfície
    private ListView authorListView;
    private ArrayList<Author> authorList;
    private SessionManager sessionManager;
    private ImageButton backButton; // Botó per tornar enrere
    private FloatingActionButton addAuthorButton; // Botó flotant per afegir nous autors

    /**
     * Inicialitza l'activitat i configura la interfície d'usuari per gestionar autors.
     *
     * @param savedInstanceState L'estat guardat anteriorment de l'activitat, si n'hi ha.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestio_autors);
        EdgeToEdge.enable(this);

        // Inicialització de components
        authorListView = findViewById(R.id.author_list_view);
        authorList = new ArrayList<>();
        sessionManager = new SessionManager(this);
        backButton = findViewById(R.id.back_button);
        addAuthorButton = findViewById(R.id.fab_add_author);

        // Configurar el botó per tornar enrere
        backButton.setOnClickListener(v -> finish());

        // Configurar el botó flotant per afegir un nou autor
        addAuthorButton.setOnClickListener(v -> {
            Intent intent = new Intent(GestioAutors.this, AuthorCreate.class);
            startActivity(intent);
        });

        // Configurar el clic sobre un element de la llista per editar un autor
        authorListView.setOnItemClickListener((parent, view, position, id) -> {
            Author selectedAuthor = authorList.get(position);
            Intent intent = new Intent(GestioAutors.this, AuthorDetailActivity.class);
            intent.putExtra("selectedAuthor", selectedAuthor);
            startActivity(intent);
        });
    }

    /**
     * Es crida quan l'activitat es reprèn. Carrega de nou la llista d'autors des del servidor.
     */
    @Override
    protected void onResume() {
        super.onResume();
        getAllAuthors(); // Recàrrega de la llista d'autors
    }

    /**
     * Obté la llista de tots els autors registrats mitjançant una connexió amb un servidor remot.
     * La llista es carrega en un objecte `ListView` per mostrar-la a l'usuari.
     */
    private void getAllAuthors() {
        new Thread(() -> {
            try {
                // Obtenir l'identificador de sessió utilitzant SessionManager
                String sessionId = sessionManager.getSessionId();

                // Comprovar si hi ha una sessió activa
                if (sessionId == null) {
                    runOnUiThread(() -> Toast.makeText(GestioAutors.this, "No hi ha sessió activa", Toast.LENGTH_SHORT).show());
                    return;
                }

                // Connexió al servidor per obtenir la llista d'autors
                Socket socket = new Socket("10.0.2.2", 12345); // IP de l'emulador d'Android Studio
                PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
                out.println("GET_ALL_AUTHORS");

                // Lectura de l'objecte autores des del servidor
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

                int authorCount = in.readInt(); // Nombre d'autors
                authorList.clear();

                // Processar la resposta del servidor per obtenir tots els autors
                for (int i = 0; i < authorCount; i++) {
                    Author author = (Author) in.readObject();
                    authorList.add(author);
                }

                // Actualitzar la interfície d'usuari amb la llista d'autors
                runOnUiThread(() -> {
                    AuthorAdapter adapter = new AuthorAdapter(GestioAutors.this, authorList);
                    authorListView.setAdapter(adapter);
                });

                // Tancar les connexions
                in.close();
                out.close();
                socket.close();

            } catch (Exception e) {
                // En cas d'error, mostrar un missatge a l'usuari
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(GestioAutors.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }
}
