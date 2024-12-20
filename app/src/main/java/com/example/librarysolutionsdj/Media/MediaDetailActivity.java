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
import com.example.librarysolutionsdj.ServerConnection.ServerConnectionHelper;
import com.google.android.material.snackbar.Snackbar;

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
    private static final int REQUEST_SELECT_AUTHORS = 1;

    private static final String TAG = "MediaDetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_detail);

        selectedMedia = (Media) getIntent().getSerializableExtra("selectedMedia");

        // Inicialización de componentes visuales
        titleEditText = findViewById(R.id.title_edit_text);
        yearPublicationEditText = findViewById(R.id.year_publication_edit_text);
        descriptionEditText = findViewById(R.id.description_edit_text);
        mediaTypeSpinner = findViewById(R.id.media_type_spinner);
        authorsListView = findViewById(R.id.authors_list_view);
        saveButton = findViewById(R.id.save_button);
        deleteButton = findViewById(R.id.delete_button);
        backButton = findViewById(R.id.back_button5);

        // Configurar Spinner de tipos de media
        ArrayAdapter<MediaType> mediaTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, MediaType.values());
        mediaTypeSpinner.setAdapter(mediaTypeAdapter);

        // Configurar ListView de autores
        authorsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
        authorsListView.setAdapter(authorsAdapter);

        Button selectAuthorButton = findViewById(R.id.select_author_button);

        selectAuthorButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, AuthorSelectionActivity.class);
            // Pasar autores ya seleccionados
            intent.putExtra("selectedAuthors", new ArrayList<>(selectedMedia.getAuthors()));
            startActivityForResult(intent, REQUEST_SELECT_AUTHORS);
        });


        if (selectedMedia != null){
            populateFieldsWithSelectedMedia();
        }



        // Configurar eventos de botones
        saveButton.setOnClickListener(v -> saveChanges());
        deleteButton.setOnClickListener(v -> deleteMedia());
        backButton.setOnClickListener(v -> finish());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SELECT_AUTHORS) {
            if (resultCode == RESULT_OK) {
                ArrayList<Author> returnedAuthors = (ArrayList<Author>) data.getSerializableExtra("selectedAuthors");
                ArrayList<Author> deselectedAuthors = (ArrayList<Author>) data.getSerializableExtra("deselectedAuthors");

                if (selectedMedia.getAuthors() == null) {
                    // Inicializar la lista si está vacía
                    selectedMedia.setAuthors(new ArrayList<>());
                }

                if (returnedAuthors != null) {
                    for (Author author : returnedAuthors) {
                        if (!selectedMedia.getAuthors().contains(author)) {
                            selectedMedia.getAuthors().add(author); // Añadir nuevos autores
                        }
                    }
                }

                if (deselectedAuthors != null) {
                    selectedMedia.getAuthors().removeAll(deselectedAuthors); // Eliminar autores desmarcados
                }

                // Actualizar la UI con los cambios
                populateFieldsWithSelectedMedia();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "No se realizaron cambios en los autores", Toast.LENGTH_SHORT).show();
            }
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
            ServerConnectionHelper connection = new ServerConnectionHelper();
            try {
                // Establecer la conexión con el servidor
                connection.connect();

                // Enviar comando MODIFY_MEDIA
                connection.sendEncryptedCommand("MODIFY_MEDIA");
                Log.d(TAG, "Comando enviado: MODIFY_MEDIA");

                // Actualizar los valores del objeto Media
                selectedMedia.setTitle(title);
                selectedMedia.setYearPublication(Integer.parseInt(year));
                selectedMedia.setMedia_description(description);
                selectedMedia.setMediaType(mediaType);

                // Enviar el objeto Media al servidor
                connection.sendEncryptedObject(selectedMedia);
                Log.d(TAG, "Objeto Media enviado.");

                // Mostrar confirmación en la interfaz de usuario
                runOnUiThread(() -> {
                    Snackbar.make(findViewById(android.R.id.content),
                            "Canvis aplicats correctament", Snackbar.LENGTH_SHORT).show();
                });
            } catch (Exception e) {
                Log.e(TAG, "Error al guardar el Media", e);
                runOnUiThread(() -> Toast.makeText(this,
                        "Error al guardar la obra: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            } finally {
                // Cerrar la conexión
                connection.close();
            }
        }).start();

    }

    /**
     * Elimina el Media seleccionado.
     */
    private void deleteMedia() {
        new Thread(() -> {
            ServerConnectionHelper connection = new ServerConnectionHelper();
            try {
                // Establecer conexión con el servidor
                connection.connect();

                // Enviar el comando DELETE_MEDIA
                connection.sendEncryptedCommand("DELETE_MEDIA");
                Log.d(TAG, "Comando enviado: DELETE_MEDIA");

                // Enviar el ID de la obra (workId)
                connection.sendEncryptedInt(selectedMedia.getWorkId());
                Log.d(TAG, "ID de la obra eliminada: " + selectedMedia.getWorkId());

                // Mostrar confirmación en la UI
                runOnUiThread(() -> Snackbar.make(findViewById(android.R.id.content),
                        "Obra eliminada amb èxit", Snackbar.LENGTH_SHORT).show());
            } catch (Exception e) {
                Log.e(TAG, "Error al eliminar Media", e);
                runOnUiThread(() -> Toast.makeText(this,
                        "Error al eliminar Media: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            } finally {
                // Cerrar la conexión
                connection.close();
            }
        }).start();
    }

}
