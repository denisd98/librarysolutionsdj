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

    private static final String TAG = "MediaDetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_detail);

        // Inicialización de los campos
        titleEditText = findViewById(R.id.title_edit_text);
        yearPublicationEditText = findViewById(R.id.year_publication_edit_text);
        descriptionEditText = findViewById(R.id.description_edit_text);
        mediaTypeSpinner = findViewById(R.id.media_type_spinner);
        authorsListView = findViewById(R.id.authors_list_view);
        saveButton = findViewById(R.id.save_button);
        deleteButton = findViewById(R.id.delete_button);
        backButton = findViewById(R.id.back_button);

        // Configurar Spinner de MediaType
        ArrayAdapter<MediaType> mediaTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, MediaType.values());
        mediaTypeSpinner.setAdapter(mediaTypeAdapter);

        // Configurar ListView de Autores
        authorsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
        authorsListView.setAdapter(authorsAdapter);

        // Recibir el objeto Media desde el Intent
        selectedMedia = (Media) getIntent().getSerializableExtra("selectedMedia");

        if (selectedMedia != null) {
            populateFieldsWithSelectedMedia();
        } else {
            Toast.makeText(this, "Media ID no válido o no encontrado", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Configurar botones
        saveButton.setOnClickListener(v -> saveChanges());
        deleteButton.setOnClickListener(v -> deleteMedia());
        backButton.setOnClickListener(v -> finish());
    }

    private void loadMediaDetails(int mediaId) {
        new Thread(() -> {
            try (Socket socket = new Socket("10.0.2.2", 12345);
                 PrintWriter commandOut = new PrintWriter(socket.getOutputStream(), true);
                 ObjectInputStream objectIn = new ObjectInputStream(socket.getInputStream())) {

                // Enviar el comando al servidor
                commandOut.println("GET_MEDIA_BY_ID");
                commandOut.println(mediaId);
                commandOut.flush();

                // Recibir el objeto Media desde el servidor
                Media media = (Media) objectIn.readObject();

                if (media != null) {
                    Log.d(TAG, "Media recibido: " + media.getTitle());
                    Log.d(TAG, "Autores recibidos: " + media.getAuthors());
                    runOnUiThread(() -> {
                        selectedMedia = media;
                        populateFieldsWithSelectedMedia();
                    });
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "No se encontró el Media", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                }

            } catch (Exception e) {
                Log.e(TAG, "Error al cargar detalles del Media", e);
                runOnUiThread(() -> Toast.makeText(this, "Error al cargar Media: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void populateFieldsWithSelectedMedia() {
        if (selectedMedia != null) {
            titleEditText.setText(selectedMedia.getTitle());
            yearPublicationEditText.setText(String.valueOf(selectedMedia.getYearPublication()));
            descriptionEditText.setText(selectedMedia.getMedia_description());
            mediaTypeSpinner.setSelection(selectedMedia.getMediaType().ordinal());

            // Procesar y mostrar autores
            authorsAdapter.clear();
            if (selectedMedia.getAuthors() != null && !selectedMedia.getAuthors().isEmpty()) {
                for (Author author : selectedMedia.getAuthors()) {
                    authorsAdapter.add(author.getAuthorname() + " " + author.getSurname1());
                }
            } else {
                authorsAdapter.add("No hay autores asignados");
            }
            authorsAdapter.notifyDataSetChanged();
        }
    }

    private void saveChanges() {
        if (titleEditText.getText().toString().isEmpty()) {
            Toast.makeText(this, "El título es obligatorio", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int year = Integer.parseInt(yearPublicationEditText.getText().toString());
            selectedMedia.setYearPublication(year);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "El año debe ser un número válido", Toast.LENGTH_SHORT).show();
            return;
        }

        selectedMedia.setTitle(titleEditText.getText().toString());
        selectedMedia.setMedia_description(descriptionEditText.getText().toString());
        selectedMedia.setMediaType((MediaType) mediaTypeSpinner.getSelectedItem());

        new Thread(() -> {
            try (Socket socket = new Socket("10.0.2.2", 12345);
                 PrintWriter commandOut = new PrintWriter(socket.getOutputStream(), true)) {

                commandOut.println("MODIFY_MEDIA");
                commandOut.flush();

                ObjectOutputStream objectOut = new ObjectOutputStream(socket.getOutputStream());
                objectOut.writeObject(selectedMedia);
                objectOut.flush();

                BufferedReader responseReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String response = responseReader.readLine();

                if ("MODIFY_MEDIA_OK".equals(response)) {
                    runOnUiThread(() -> Toast.makeText(this, "Cambios guardados correctamente", Toast.LENGTH_SHORT).show());
                } else {
                    runOnUiThread(() -> Toast.makeText(this, "Error al guardar cambios", Toast.LENGTH_SHORT).show());
                }

            } catch (Exception e) {
                Log.e(TAG, "Error al guardar cambios", e);
                runOnUiThread(() -> Toast.makeText(this, "Error al guardar cambios: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void deleteMedia() {
        new Thread(() -> {
            try (Socket socket = new Socket("10.0.2.2", 12345);
                 PrintWriter commandOut = new PrintWriter(socket.getOutputStream(), true)) {

                commandOut.println("DELETE_MEDIA");
                commandOut.flush();

                ObjectOutputStream objectOut = new ObjectOutputStream(socket.getOutputStream());
                objectOut.writeInt(selectedMedia.getWorkId());
                objectOut.flush();

                runOnUiThread(() -> {
                    Toast.makeText(this, "Media eliminado correctamente", Toast.LENGTH_SHORT).show();
                    finish();
                });

            } catch (Exception e) {
                Log.e(TAG, "Error al eliminar Media", e);
                runOnUiThread(() -> Toast.makeText(this, "Error al eliminar Media: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}
