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

import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import app.model.Author;

public class AuthorSelectionActivity extends Activity {

    private ListView authorListView;
    private ArrayAdapter<Author> authorAdapter;
    private ArrayList<Author> allAuthors = new ArrayList<>();
    private ArrayList<Author> selectedAuthors = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author_selection);

        authorListView = findViewById(R.id.author_list_view);
        Button confirmSelectionButton = findViewById(R.id.confirm_selection_button);

        authorAdapter = new AuthorAdapter(this, allAuthors);
        authorListView.setAdapter(authorAdapter);
        authorListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE); // Habilitar selección múltiple

        selectedAuthors = (ArrayList<Author>) getIntent().getSerializableExtra("selectedAuthors");
        selectedAuthors = selectedAuthors != null ? selectedAuthors : new ArrayList<>();

        confirmSelectionButton.setOnClickListener(v -> confirmSelection());

        loadAuthorsFromServer();
    }

    private void loadAuthorsFromServer() {
        new Thread(() -> {
            try {
                Socket socket = new Socket("10.0.2.2", 12345); // IP del servidor
                PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
                out.println("GET_ALL_AUTHORS");

                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                allAuthors = (ArrayList<Author>) in.readObject();

                runOnUiThread(() -> {
                    authorAdapter.clear();
                    authorAdapter.addAll(allAuthors);
                    authorAdapter.notifyDataSetChanged();

                    Set<Author> selectedAuthorSet = new HashSet<>(selectedAuthors);
                    for (int i = 0; i < allAuthors.size(); i++) {
                        if (selectedAuthorSet.contains(allAuthors.get(i))) {
                            authorListView.setItemChecked(i, true);
                        }
                    }
                });

                in.close();
                out.close();
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Error al cargar autores: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }

    private void confirmSelection() {
        SparseBooleanArray checkedItems = authorListView.getCheckedItemPositions();
        ArrayList<Author> newSelectedAuthors = new ArrayList<>();
        ArrayList<Author> deselectedAuthors = new ArrayList<>();

        // Recorrer la lista de autores mostrados en la pantalla de selección
        for (int i = 0; i < authorAdapter.getCount(); i++) {
            Author author = authorAdapter.getItem(i);
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

