package com.example.librarysolutionsdj.Media;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.librarysolutionsdj.R;
import com.google.android.material.snackbar.Snackbar;

import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import app.model.Author;
import app.model.Media;
import app.model.MediaType;

public class MediaCreate extends AppCompatActivity {

    private EditText titleEditText, yearPublicationEditText, descriptionEditText;
    private Spinner mediaTypeSpinner;
    private ListView authorsListView;
    private Button saveButton;
    private ImageButton backButton;
    private Media newMedia;
    private ArrayAdapter<String> authorsAdapter;
    private ArrayList<Author> selectedAuthors = new ArrayList<>();
    private static final int REQUEST_SELECT_AUTHORS = 1;

    private static final String TAG = "MediaCreate";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_detail); // Reutilizamos el mismo layout

        // Inicialización de componentes visuales
        titleEditText = findViewById(R.id.title_edit_text);
        yearPublicationEditText = findViewById(R.id.year_publication_edit_text);
        descriptionEditText = findViewById(R.id.description_edit_text);
        mediaTypeSpinner = findViewById(R.id.media_type_spinner);
        authorsListView = findViewById(R.id.authors_list_view);
        saveButton = findViewById(R.id.save_button);
        backButton = findViewById(R.id.back_button);

        // Configurar Spinner de tipos de media
        ArrayAdapter<MediaType> mediaTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, MediaType.values());
        mediaTypeSpinner.setAdapter(mediaTypeAdapter);

        // Configurar ListView de autores
        authorsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
        authorsListView.setAdapter(authorsAdapter);

        Button selectAuthorButton = findViewById(R.id.select_author_button);

        selectAuthorButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, AuthorSelectionActivity.class);
            intent.putExtra("selectedAuthors", new ArrayList<>(selectedAuthors));
            startActivityForResult(intent, REQUEST_SELECT_AUTHORS);
        });

        saveButton.setOnClickListener(v -> createMedia());
        backButton.setOnClickListener(v -> finish());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SELECT_AUTHORS && resultCode == RESULT_OK) {
            ArrayList<Author> returnedAuthors = (ArrayList<Author>) data.getSerializableExtra("selectedAuthors");

            if (returnedAuthors != null) {
                selectedAuthors.clear();
                selectedAuthors.addAll(returnedAuthors);
                populateAuthorList();
            }
        }
    }

    /**
     * Llena el ListView de autores seleccionados.
     */
    private void populateAuthorList() {
        authorsAdapter.clear();
        if (!selectedAuthors.isEmpty()) {
            for (Author author : selectedAuthors) {
                authorsAdapter.add(author.getAuthorname() + " " + author.getSurname1());
            }
        } else {
            authorsAdapter.add("No hay autores asignados");
        }
        authorsAdapter.notifyDataSetChanged();
    }

    /**
     * Crea un nuevo Media y lo envía al servidor.
     */
    /**
     * Crea un nuevo Media y lo envía al servidor.
     */
    private void createMedia() {
        String title = titleEditText.getText().toString();
        String year = yearPublicationEditText.getText().toString();
        String description = descriptionEditText.getText().toString();
        MediaType mediaType = (MediaType) mediaTypeSpinner.getSelectedItem();

        if (title.isEmpty() || year.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Crear un nuevo objeto Media con valores proporcionados
            newMedia = new Media(
                    title,
                    Integer.parseInt(year),
                    mediaType,
                    description
            );
            newMedia.setAuthors(selectedAuthors);

            // Enviar el nuevo Media al servidor
            new Thread(() -> {
                try (Socket socket = new Socket("10.0.2.2", 12345);
                     PrintWriter commandOut = new PrintWriter(socket.getOutputStream(), true)) {
                    commandOut.println("ADD_MEDIA");
                    commandOut.flush();

                    ObjectOutputStream objectOut = new ObjectOutputStream(socket.getOutputStream());
                    objectOut.writeObject(newMedia);
                    objectOut.flush();

                    runOnUiThread(() -> Snackbar.make(findViewById(android.R.id.content), "Media creada exitosamente", Snackbar.LENGTH_SHORT).show());
                } catch (Exception e) {
                    Log.e(TAG, "Error al crear Media", e);
                    runOnUiThread(() -> Toast.makeText(this, "Error al crear Media", Toast.LENGTH_SHORT).show());
                }
            }).start();
        } catch (NumberFormatException e) {
            Toast.makeText(this, "El año de publicación debe ser un número válido", Toast.LENGTH_SHORT).show();
        }
    }
}
