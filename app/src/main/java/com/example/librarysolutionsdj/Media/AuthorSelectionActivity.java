package com.example.librarysolutionsdj.Media;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.librarysolutionsdj.R;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import app.model.Author;

/**
 * Classe que gestiona la selecció d'autors en una llista de múltiples opcions.
 * Aquesta activitat permet carregar una llista bàsica d'autors des del servidor i seleccionar-ne diversos per retornar-los com a resultat.
 */
public class AuthorSelectionActivity extends Activity {

    private ListView authorListView;
    private ArrayAdapter<Author> authorAdapter;
    private ArrayList<Author> allAuthors = new ArrayList<>();
    private ArrayList<Author> selectedAuthors = new ArrayList<>();

    /**
     * Inicialitza l'activitat i configura la interfície d'usuari per a la selecció d'autors.
     *
     * @param savedInstanceState L'estat guardat de l'activitat, si n'hi ha.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author_selection);

        // Inicialització de components
        authorListView = findViewById(R.id.author_list_view);
        Button confirmSelectionButton = findViewById(R.id.confirm_selection_button);

        // Configurar l'adaptador per mostrar noms bàsics d'autors
        authorAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, allAuthors);
        authorListView.setAdapter(authorAdapter);
        authorListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        // Configurar el botó per confirmar la selecció
        confirmSelectionButton.setOnClickListener(v -> confirmSelection());

        // Carregar la llista bàsica d'autors des del servidor
        loadBasicAuthors();
    }

    /**
     * Carrega una llista bàsica d'autors des del servidor mitjançant una connexió de socket.
     * Aquest mètode utilitza el comando "GET_BASIC_AUTHORS" per obtenir autors amb informació mínima.
     */
    private void loadBasicAuthors() {
        new Thread(() -> {
            try (Socket socket = new Socket("10.0.2.2", 12345);
                 PrintWriter commandOut = new PrintWriter(socket.getOutputStream(), true);
                 ObjectInputStream objectIn = new ObjectInputStream(socket.getInputStream())) {

                // Enviar comando al servidor
                commandOut.println("GET_BASIC_AUTHORS");

                // Llegir el nombre total d'autors i carregar-los a la llista
                int authorCount = objectIn.readInt();
                allAuthors.clear();

                for (int i = 0; i < authorCount; i++) {
                    allAuthors.add((Author) objectIn.readObject());
                }

                // Actualitzar la interfície d'usuari
                runOnUiThread(() -> authorAdapter.notifyDataSetChanged());

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Error loading authors", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    /**
     * Confirma la selecció d'autors per part de l'usuari i retorna el resultat a l'activitat anterior.
     * Els autors seleccionats s'envien com un ArrayList dins d'un intent.
     */
    private void confirmSelection() {
        SparseBooleanArray checkedItems = authorListView.getCheckedItemPositions();
        selectedAuthors.clear();

        // Afegir els autors seleccionats a la llista
        for (int i = 0; i < authorAdapter.getCount(); i++) {
            if (checkedItems.get(i)) {
                selectedAuthors.add(authorAdapter.getItem(i));
            }
        }

        // Retornar el resultat amb els autors seleccionats
        Intent resultIntent = new Intent();
        resultIntent.putExtra("selectedAuthors", selectedAuthors);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}
