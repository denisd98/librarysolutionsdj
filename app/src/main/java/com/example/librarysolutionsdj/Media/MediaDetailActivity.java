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

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.librarysolutionsdj.R;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import app.model.Author;
import app.model.Media;
import app.model.MediaType;

public class MediaDetailActivity extends AppCompatActivity {

    private EditText titleEditText, yearPublicationEditText, descriptionEditText;
    private Spinner mediaTypeSpinner;
    private ListView authorsListView;
    private Button saveButton, deleteButton;
    private ImageButton backButton;
    private Media selectedMedia;
    private ArrayAdapter<String> authorsAdapter;

    private static final int SELECT_AUTHORS_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_detail);
        EdgeToEdge.enable(this);

        // Recibir el objeto Media desde el Intent
        selectedMedia = (Media) getIntent().getSerializableExtra("selectedMedia");

        // Inicialización de los campos
        titleEditText = findViewById(R.id.title_edit_text);
        yearPublicationEditText = findViewById(R.id.year_publication_edit_text);
        descriptionEditText = findViewById(R.id.description_edit_text);
        mediaTypeSpinner = findViewById(R.id.media_type_spinner);
        authorsListView = findViewById(R.id.authors_list_view);
        saveButton = findViewById(R.id.save_button);
        deleteButton = findViewById(R.id.delete_button);
        backButton = findViewById(R.id.back_button);
        Button selectAuthorButton = findViewById(R.id.select_author_button);

        // Configurar Spinner de MediaType
        ArrayAdapter<MediaType> mediaTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, MediaType.values());
        mediaTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mediaTypeSpinner.setAdapter(mediaTypeAdapter);

        // Configurar ListView de Autores
        authorsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
        authorsListView.setAdapter(authorsAdapter);

        // Si se recibió un Media, rellenar los campos; si no, cargar desde el servidor
        if (selectedMedia != null) {
            populateFieldsWithSelectedMedia();
        } else {
            int mediaId = getIntent().getIntExtra("mediaId", -1);
            if (mediaId != -1) {
                loadMediaDetails(mediaId);
            }
        }

        saveButton.setOnClickListener(v -> saveChanges());
        deleteButton.setOnClickListener(v -> deleteMedia());
        backButton.setOnClickListener(v -> finish());
        selectAuthorButton.setOnClickListener(v -> openAuthorSelection());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_AUTHORS_REQUEST_CODE && resultCode == RESULT_OK) {
            ArrayList<String> selectedAuthorNames = data.getStringArrayListExtra("selectedAuthors");
            if (selectedAuthorNames != null) {
                authorsAdapter.clear();
                authorsAdapter.addAll(selectedAuthorNames); // Añade directamente los nombres
                authorsAdapter.notifyDataSetChanged();
            }
        }
    }



    private void openAuthorSelection() {
        Intent intent = new Intent(this, AuthorSelectionActivity.class);
        startActivityForResult(intent, SELECT_AUTHORS_REQUEST_CODE);
    }

    private void populateFieldsWithSelectedMedia() {
        if (selectedMedia != null) {
            titleEditText.setText(selectedMedia.getTitle());
            yearPublicationEditText.setText(String.valueOf(selectedMedia.getYearPublication()));
            descriptionEditText.setText(selectedMedia.getMedia_description());
            mediaTypeSpinner.setSelection(selectedMedia.getMediaType().ordinal());

            // Manejar lista de autores nula o vacía
            authorsAdapter.clear();
            if (selectedMedia.getAuthors() != null && !selectedMedia.getAuthors().isEmpty()) {
                for (Author author : selectedMedia.getAuthors()) {
                    authorsAdapter.add(String.valueOf(author));
                }
            } else {
                Toast.makeText(this, "No hay autores asociados", Toast.LENGTH_SHORT).show();
            }
            authorsAdapter.notifyDataSetChanged();
        }
    }


    private void loadMediaDetails(int mediaId) {
        new Thread(() -> {
            try (Socket socket = new Socket("10.0.2.2", 12345);
                 PrintWriter commandOut = new PrintWriter(socket.getOutputStream(), true);
                 ObjectInputStream objectIn = new ObjectInputStream(socket.getInputStream())) {

                commandOut.println("GET_MEDIA_BY_ID");
                commandOut.println(mediaId);
                commandOut.flush();

                Media media = (Media) objectIn.readObject();

                if (media != null) {
                    runOnUiThread(() -> {
                        selectedMedia = media;
                        Log.d("MediaDetailActivity", "Autores cargados: " + selectedMedia.getAuthors());
                        populateFieldsWithSelectedMedia();
                    });
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Media not found", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                }

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Error loading media: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }


    private void saveChanges() {
        if (titleEditText.getText().toString().isEmpty()) {
            Toast.makeText(this, "El títol és obligatori", Toast.LENGTH_SHORT).show();
            return;
        }

        int yearPublication;
        try {
            yearPublication = Integer.parseInt(yearPublicationEditText.getText().toString());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "L'any de publicació ha de ser un número", Toast.LENGTH_SHORT).show();
            return;
        }

        // Actualizar el objeto Media
        selectedMedia.setTitle(titleEditText.getText().toString());
        selectedMedia.setYearPublication(yearPublication);
        selectedMedia.setMediaType((MediaType) mediaTypeSpinner.getSelectedItem());
        selectedMedia.setMedia_description(descriptionEditText.getText().toString());

        new Thread(() -> {
            try (Socket socket = new Socket("10.0.2.2", 12345);
                 PrintWriter commandOut = new PrintWriter(socket.getOutputStream(), true)) {

                commandOut.println("MODIFY_MEDIA");
                commandOut.flush();

                ObjectOutputStream objectOut = new ObjectOutputStream(socket.getOutputStream());
                objectOut.writeInt(selectedMedia.getWorkId());
                objectOut.writeObject(selectedMedia);
                objectOut.flush();

                BufferedReader responseReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String response = responseReader.readLine();

                if ("MODIFY_MEDIA_OK".equals(response)) {
                    runOnUiThread(() -> {
                        Toast.makeText(MediaDetailActivity.this, "Changes saved successfully", Toast.LENGTH_SHORT).show();
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("updatedMedia", selectedMedia);
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(MediaDetailActivity.this, "Error saving changes", Toast.LENGTH_SHORT).show());
                }

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(MediaDetailActivity.this, "Error saving changes: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void deleteMedia() {
        new Thread(() -> {
            try (Socket socket = new Socket("10.0.2.2", 12345);
                 PrintWriter commandOut = new PrintWriter(socket.getOutputStream(), true)) {

                commandOut.println("DELETE_MEDIA");
                ObjectOutputStream objectOut = new ObjectOutputStream(socket.getOutputStream());
                objectOut.writeInt(selectedMedia.getWorkId());
                objectOut.flush();

                runOnUiThread(() -> {
                    Toast.makeText(MediaDetailActivity.this, "Media deleted successfully", Toast.LENGTH_SHORT).show();
                    finish();
                });

            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(MediaDetailActivity.this, "Error deleting media: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}
