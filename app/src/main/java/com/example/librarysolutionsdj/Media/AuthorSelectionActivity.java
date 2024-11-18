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

        // Configurar el adaptador de la lista para mostrar nombres básicos
        authorAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, allAuthors);
        authorListView.setAdapter(authorAdapter);
        authorListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        confirmSelectionButton.setOnClickListener(v -> confirmSelection());

        loadBasicAuthors(); // Usar el comando nuevo
    }

    private void loadBasicAuthors() {
        new Thread(() -> {
            try (Socket socket = new Socket("10.0.2.2", 12345);
                 PrintWriter commandOut = new PrintWriter(socket.getOutputStream(), true);
                 ObjectInputStream objectIn = new ObjectInputStream(socket.getInputStream())) {

                commandOut.println("GET_BASIC_AUTHORS");

                int authorCount = objectIn.readInt();
                allAuthors.clear();

                for (int i = 0; i < authorCount; i++) {
                    allAuthors.add((Author) objectIn.readObject());
                }

                runOnUiThread(() -> authorAdapter.notifyDataSetChanged());

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Error loading authors", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }


    private void confirmSelection() {
        SparseBooleanArray checkedItems = authorListView.getCheckedItemPositions();
        selectedAuthors.clear();

        for (int i = 0; i < authorAdapter.getCount(); i++) {
            if (checkedItems.get(i)) {
                selectedAuthors.add(authorAdapter.getItem(i));
            }
        }

        Intent resultIntent = new Intent();
        resultIntent.putExtra("selectedAuthors", selectedAuthors);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

}
