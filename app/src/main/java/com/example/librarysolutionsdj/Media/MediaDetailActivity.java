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
import com.google.gson.Gson;

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

        // Inicialización de componentes visuales
        titleEditText = findViewById(R.id.title_edit_text);
        yearPublicationEditText = findViewById(R.id.year_publication_edit_text);
        descriptionEditText = findViewById(R.id.description_edit_text);
        mediaTypeSpinner = findViewById(R.id.media_type_spinner);
        authorsListView = findViewById(R.id.authors_list_view);
        saveButton = findViewById(R.id.save_button);
        deleteButton = findViewById(R.id.delete_button);
        backButton = findViewById(R.id.back_button);

        // Configurar Spinner de tipos de media
        ArrayAdapter<MediaType> mediaTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, MediaType.values());
        mediaTypeSpinner.setAdapter(mediaTypeAdapter);

        // Configurar ListView de autores
        authorsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
        authorsListView.setAdapter(authorsAdapter);

        // Obtener el ID de Media desde el Intent
        int mediaId = getIntent().getIntExtra("MEDIA_ID", -1);
        if (mediaId != -1) {
            fetchMediaDetails(mediaId);
        } else {
            Toast.makeText(this, "ID de Media no válido", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Configurar eventos de botones
        saveButton.setOnClickListener(v -> saveChanges());
        deleteButton.setOnClickListener(v -> deleteMedia());
        backButton.setOnClickListener(v -> finish());
    }

    /**
     * Realiza una solicitud al servidor para obtener los detalles del Media.
     */
    private void fetchMediaDetails(int mediaId) {
        Log.d(TAG, "Iniciando fetchMediaDetails para mediaId: " + mediaId);

        new Thread(() -> {
            try (Socket socket = new Socket("10.0.2.2", 12345); // Cambia la IP según sea necesario
                 PrintWriter commandOut = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader serverInput = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                // Enviar el comando y mediaId como texto
                String commandWithId = "GET_MEDIA_BY_ID " + mediaId;
                commandOut.println(commandWithId);
                Log.d(TAG, "Comando enviado: " + commandWithId);

                // Leer la respuesta del servidor
                Log.d(TAG, "Esperando respuesta del servidor...");
                String response = serverInput.readLine();

                // Agregar log para inspeccionar el JSON recibido
                Log.d(TAG, "JSON recibido del servidor: " + response);

                if (response.equals("NOT_FOUND")) {
                    Log.w(TAG, "Media no encontrado");
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Media no encontrado", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                } else {
                    // Procesar respuesta (asumiendo que el servidor envía los datos del objeto en JSON)
                    Media media = parseMediaResponse(response);
                    if (media != null) {
                        selectedMedia = media;
                        Log.d(TAG, "Media recibido: " + media.getTitle() + " (ID: " + media.getWorkId() + ")");
                        runOnUiThread(this::populateFieldsWithSelectedMedia);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error durante fetchMediaDetails", e);
                runOnUiThread(() -> Toast.makeText(this, "Error al cargar Media", Toast.LENGTH_SHORT).show());
            }
        }).start();

        Log.d(TAG, "Hilo para fetchMediaDetails iniciado");
    }


    /**
     * Método para procesar la respuesta del servidor.
     * Asume que el servidor envía el objeto Media como un JSON string.
     */
    private Media parseMediaResponse(String response) {
        try {
            Media media = new Gson().fromJson(response, Media.class);
            Log.d(TAG, "Media deserializado: " + media.getTitle());
            if (media.getAuthors() != null) {
                for (Author author : media.getAuthors()) {
                    Log.d(TAG, "Autor recibido: " + author.getAuthorname() + " " + author.getSurname1());
                }
            } else {
                Log.w(TAG, "No se recibieron autores para el Media");
            }
            return media;
        } catch (Exception e) {
            Log.e(TAG, "Error al parsear la respuesta del servidor: " + response, e);
            return null;
        }
    }




    /**
     * Llena los campos del formulario con los datos del Media seleccionado.
     */
    private void populateFieldsWithSelectedMedia() {
        if (selectedMedia != null) {
            titleEditText.setText(selectedMedia.getTitle());
            yearPublicationEditText.setText(String.valueOf(selectedMedia.getYearPublication()));
            descriptionEditText.setText(selectedMedia.getMedia_description());
            mediaTypeSpinner.setSelection(selectedMedia.getMediaType().ordinal());

            // Mostrar lista de autores
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

    /**
     * Guarda los cambios realizados en el Media.
     */
    private void saveChanges() {
        String title = titleEditText.getText().toString();
        String year = yearPublicationEditText.getText().toString();
        String description = descriptionEditText.getText().toString();
        MediaType mediaType = (MediaType) mediaTypeSpinner.getSelectedItem();

        if (title.isEmpty() || year.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            try (Socket socket = new Socket("10.0.2.2", 12345);
                 ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {

                // Actualizar el objeto Media
                selectedMedia.setTitle(title);
                selectedMedia.setYearPublication(Integer.parseInt(year));
                selectedMedia.setMedia_description(description);
                selectedMedia.setMediaType(mediaType);

                // Enviar comando y objeto actualizado
                out.writeObject("MODIFY_MEDIA");
                out.writeObject(selectedMedia);
                out.flush();

                runOnUiThread(() -> Toast.makeText(this, "Media guardado con éxito", Toast.LENGTH_SHORT).show());
            } catch (Exception e) {
                Log.e(TAG, "Error al guardar Media", e);
                runOnUiThread(() -> Toast.makeText(this, "Error al guardar Media", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    /**
     * Elimina el Media seleccionado.
     */
    private void deleteMedia() {
        new Thread(() -> {
            try (Socket socket = new Socket("10.0.2.2", 12345);
                 ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {

                // Enviar comando y el ID
                out.writeObject("DELETE_MEDIA");
                out.writeInt(selectedMedia.getWorkId());
                out.flush();

                runOnUiThread(() -> {
                    Toast.makeText(this, "Media eliminado con éxito", Toast.LENGTH_SHORT).show();
                    finish();
                });
            } catch (Exception e) {
                Log.e(TAG, "Error al eliminar Media", e);
                runOnUiThread(() -> Toast.makeText(this, "Error al eliminar Media", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}
