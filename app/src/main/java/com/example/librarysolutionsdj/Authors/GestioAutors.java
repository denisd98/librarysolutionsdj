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
 * La classe GestioAutors permet gestionar la visualització de tots els autors registrats en el sistema.
 * Aquesta classe carrega la llista d'autors des d'un servidor remot i la mostra en un ListView.
 */
public class GestioAutors extends AppCompatActivity {

    // Vista de la llista d'autors i llista d'autors
    private ListView authorListView;
    private ArrayList<Author> authorList;
    private SessionManager sessionManager;
    private ImageButton backButton; // Declaración del botón de volver
    private FloatingActionButton addAuthorButton; // Botón flotante para añadir autor

    /**
     * Mètode que s'executa en crear l'activitat. Configura la interfície d'usuari i inicialitza la llista d'autors.
     *
     * @param savedInstanceState l'estat de l'activitat guardat anteriorment.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestio_autors);
        EdgeToEdge.enable(this);

        authorListView = findViewById(R.id.author_list_view);
        authorList = new ArrayList<>();
        sessionManager = new SessionManager(this);
        backButton = findViewById(R.id.back_button); // Inicialización del botón de volver
        addAuthorButton = findViewById(R.id.fab_add_author); // Inicialización del botón flotante

        // Configurar el botón de volver atrás
        backButton.setOnClickListener(v -> finish());

        // Configurar el botón flotante para añadir un nuevo autor
        addAuthorButton.setOnClickListener(v -> {
            Intent intent = new Intent(GestioAutors.this, AuthorCreate.class);
            startActivity(intent);
        });

        // Configurar el listener de clic para cada elemento en la lista
        authorListView.setOnItemClickListener((parent, view, position, id) -> {
            Author selectedAuthor = authorList.get(position);
            Intent intent = new Intent(GestioAutors.this, AuthorDetailActivity.class);
            intent.putExtra("selectedAuthor", selectedAuthor); // Pasar el objeto Author completo
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getAllAuthors(); // Recarga la lista de autores desde el servidor al volver a la actividad
    }

    /**
     * Mètode que obté la llista de tots els autors connectant-se a un servidor remot.
     * Utilitza una connexió de socket i un ObjectInputStream per deserialitzar la llista d'autors.
     */
    private void getAllAuthors() {
        new Thread(() -> {
            try {
                // Recuperar el ID de sesión usando SessionManager
                String sessionId = sessionManager.getSessionId();

                // Comprobar si hay una sesión activa
                if (sessionId == null) {
                    runOnUiThread(() -> Toast.makeText(GestioAutors.this, "No hi ha sessió activa", Toast.LENGTH_SHORT).show());
                    return;
                }

                // Conexión al servidor para obtener la lista de autores
                Socket socket = new Socket("10.0.2.2", 12345); // IP de la máquina virtual de Android Studio
                PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
                out.println("GET_ALL_AUTHORS");

                // Lectura del objeto autores directamente
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

                int authorCount = in.readInt(); // Número total de autores
                authorList.clear();

                // Recorremos la respuesta del servidor para obtener todos los autores
                for (int i = 0; i < authorCount; i++) {
                    Author author = (Author) in.readObject();
                    authorList.add(author);
                }

                // Actualiza la interfaz de usuario con la lista de autores
                runOnUiThread(() -> {
                    AuthorAdapter adapter = new AuthorAdapter(GestioAutors.this, authorList);
                    authorListView.setAdapter(adapter);
                });

                // Cerramos las conexiones
                in.close();
                out.close();
                socket.close();

            } catch (Exception e) {
                // En caso de error, muestra el mensaje de error
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(GestioAutors.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }
}
