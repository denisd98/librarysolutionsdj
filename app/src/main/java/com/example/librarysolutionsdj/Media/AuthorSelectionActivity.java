package com.example.librarysolutionsdj.Media;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.librarysolutionsdj.R;
import com.example.librarysolutionsdj.ServerConnection.ServerConnectionHelper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import app.model.Author;

public class AuthorSelectionActivity extends Activity {

    private ListView authorListView;
    private ArrayAdapter<Author> authorAdapter;
    private ArrayList<Author> allAuthors = new ArrayList<>();
    private ArrayList<Author> selectedAuthors = new ArrayList<>();

    // **Variables para el adaptador simple**
    private ArrayAdapter<String> simpleAdapter;
    private ArrayList<String> authorNames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author_selection);

        authorListView = findViewById(R.id.author_list_view);
        Button confirmSelectionButton = findViewById(R.id.confirm_selection_button);

        // **Comentar el adaptador personalizado temporalmente**
        // authorAdapter = new AuthorAdapter(this, allAuthors);
        // authorListView.setAdapter(authorAdapter);
        authorListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE); // Habilitar selección múltiple

        selectedAuthors = (ArrayList<Author>) getIntent().getSerializableExtra("selectedAuthors");
        selectedAuthors = selectedAuthors != null ? selectedAuthors : new ArrayList<>();

        confirmSelectionButton.setOnClickListener(v -> confirmSelection());

        loadAuthorsFromServer();
    }

    private void loadAuthorsFromServer() {
        new Thread(() -> {
            ServerConnectionHelper connection = new ServerConnectionHelper();
            try {
                // Establecer conexión con el servidor
                connection.connect();

                // Enviar comando cifrado al servidor
                connection.sendEncryptedCommand("GET_ALL_AUTHORS");

                // Recibir la lista de autores cifrada y deserializada
                @SuppressWarnings("unchecked")
                ArrayList<Author> authors = (ArrayList<Author>) connection.receiveEncryptedObject();

                // Validar que la lista no esté vacía
                if (authors != null && !authors.isEmpty()) {
                    // Log para verificar los datos recibidos
                    for (Author author : authors) {
                        Log.d("AuthorSelectionActivity", "Autor recibido: " + author.getFullName());
                    }

                    // Actualizar la lista local de autores
                    allAuthors.clear();
                    allAuthors.addAll(authors);

                    // **Preparar datos para el adaptador simple**
                    authorNames.clear();
                    for (Author author : authors) {
                        authorNames.add(author.getFullName());
                    }

                    // Actualizar la interfaz de usuario con el adaptador simple
                    runOnUiThread(() -> {
                        // **Configurar el adaptador simple en lugar del adaptador personalizado**
                        simpleAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, authorNames);
                        authorListView.setAdapter(simpleAdapter);
                        authorListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                        simpleAdapter.notifyDataSetChanged();

                        Log.d("AuthorSelectionActivity", "Número de autores en el adaptador simple: " + simpleAdapter.getCount());

                        // **Mantener los elementos seleccionados si es necesario**
                        Set<String> selectedAuthorNames = new HashSet<>();
                        for (Author selectedAuthor : selectedAuthors) {
                            selectedAuthorNames.add(selectedAuthor.getFullName());
                        }
                        for (int i = 0; i < authorNames.size(); i++) {
                            if (selectedAuthorNames.contains(authorNames.get(i))) {
                                authorListView.setItemChecked(i, true);
                            }
                        }
                    });
                } else {
                    Log.d("AuthorSelectionActivity", "Lista de autores vacía o nula.");
                    runOnUiThread(() -> Toast.makeText(this, "No se recibieron autores del servidor.", Toast.LENGTH_LONG).show());
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Error al cargar autores: " + e.getMessage(), Toast.LENGTH_LONG).show());
            } finally {
                // Cerrar la conexión
                connection.close();
            }
        }).start();
    }

    private void confirmSelection() {
        SparseBooleanArray checkedItems = authorListView.getCheckedItemPositions();
        ArrayList<Author> newSelectedAuthors = new ArrayList<>();
        ArrayList<Author> deselectedAuthors = new ArrayList<>();

        // Recorrer la lista de autores mostrados en la pantalla de selección
        for (int i = 0; i < authorNames.size(); i++) {
            String authorName = authorNames.get(i);
            Author author = null;
            // Encontrar el objeto Author correspondiente al nombre
            for (Author a : allAuthors) {
                if (a.getFullName().equals(authorName)) {
                    author = a;
                    break;
                }
            }
            if (author == null) continue; // Si no se encuentra, continuar

            if (checkedItems.get(i)) { // Si el autor está marcado
                if (!selectedAuthors.contains(author)) { // Añadir solo los nuevos autores
                    newSelectedAuthors.add(author);
                }
            } else { // Si el autor no está marcado
                if (selectedAuthors.contains(author)) { // Añadir a deseleccionados si estaba en la lista original
                    deselectedAuthors.add(author);
                }
            }
        }

        // Combinar nuevos seleccionados con la lista original
        selectedAuthors.addAll(newSelectedAuthors);

        Intent resultIntent = new Intent();
        resultIntent.putExtra("selectedAuthors", selectedAuthors); // Lista actualizada de autores
        resultIntent.putExtra("deselectedAuthors", deselectedAuthors); // Autores desmarcados
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}
