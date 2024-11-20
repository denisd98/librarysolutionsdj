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

/**
 * Activitat per gestionar els detalls d'una obra (Media) específica.
 * Permet veure, modificar i eliminar una obra registrada al sistema.
 */
public class MediaDetailActivity extends AppCompatActivity {

    private EditText titleEditText, yearPublicationEditText, descriptionEditText;
    private Spinner mediaTypeSpinner;
    private ListView authorsListView;
    private Button saveButton, deleteButton;
    private ImageButton backButton;
    private Media selectedMedia;
    private ArrayAdapter<String> authorsAdapter;

    private static final String TAG = "MediaDetailActivity";

    /**
     * S'executa quan es crea l'activitat. Configura la interfície d'usuari
     * i inicialitza els camps amb la informació de l'obra seleccionada.
     *
     * @param savedInstanceState L'estat guardat de l'activitat.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_detail);

        // Inicialització de components visuals
        titleEditText = findViewById(R.id.title_edit_text);
        yearPublicationEditText = findViewById(R.id.year_publication_edit_text);
        descriptionEditText = findViewById(R.id.description_edit_text);
        mediaTypeSpinner = findViewById(R.id.media_type_spinner);
        authorsListView = findViewById(R.id.authors_list_view);
        saveButton = findViewById(R.id.save_button);
        deleteButton = findViewById(R.id.delete_button);
        backButton = findViewById(R.id.back_button);

        // Configurar l'Spinner per seleccionar el tipus de mitjà (MediaType)
        ArrayAdapter<MediaType> mediaTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, MediaType.values());
        mediaTypeSpinner.setAdapter(mediaTypeAdapter);

        // Configurar el ListView per mostrar autors
        authorsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
        authorsListView.setAdapter(authorsAdapter);

        // Rebre l'objecte Media des de l'Intent
        selectedMedia = (Media) getIntent().getSerializableExtra("selectedMedia");

        if (selectedMedia != null) {
            populateFieldsWithSelectedMedia(); // Omplir els camps amb les dades de l'obra
        } else {
            Toast.makeText(this, "Media ID no vàlid o no trobat", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Configurar els botons
        saveButton.setOnClickListener(v -> saveChanges());
        deleteButton.setOnClickListener(v -> deleteMedia());
        backButton.setOnClickListener(v -> finish());
    }

    /**
     * Omple els camps del formulari amb la informació de l'obra seleccionada.
     */
    private void populateFieldsWithSelectedMedia() {
        if (selectedMedia != null) {
            titleEditText.setText(selectedMedia.getTitle());
            yearPublicationEditText.setText(String.valueOf(selectedMedia.getYearPublication()));
            descriptionEditText.setText(selectedMedia.getMedia_description());
            mediaTypeSpinner.setSelection(selectedMedia.getMediaType().ordinal());

            // Processar i mostrar autors
            authorsAdapter.clear();
            if (selectedMedia.getAuthors() != null && !selectedMedia.getAuthors().isEmpty()) {
                for (Author author : selectedMedia.getAuthors()) {
                    authorsAdapter.add(author.getAuthorname() + " " + author.getSurname1());
                }
            } else {
                authorsAdapter.add("No hi ha autors assignats");
            }
            authorsAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Desa els canvis realitzats a l'obra seleccionada al servidor.
     */
    private void saveChanges() {
        if (titleEditText.getText().toString().isEmpty()) {
            Toast.makeText(this, "El títol és obligatori", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int year = Integer.parseInt(yearPublicationEditText.getText().toString());
            selectedMedia.setYearPublication(year);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "L'any ha de ser un número vàlid", Toast.LENGTH_SHORT).show();
            return;
        }

        selectedMedia.setTitle(titleEditText.getText().toString());
        selectedMedia.setMedia_description(descriptionEditText.getText().toString());
        selectedMedia.setMediaType((MediaType) mediaTypeSpinner.getSelectedItem());

        new Thread(() -> {
            try (Socket socket = new Socket("10.0.2.2", 12345);
                 ObjectOutputStream objectOut = new ObjectOutputStream(socket.getOutputStream());
                 ObjectInputStream objectIn = new ObjectInputStream(socket.getInputStream())) {

                // Enviar comandament i dades com a objectes
                objectOut.writeObject("MODIFY_MEDIA"); // Comandament
                objectOut.writeObject(selectedMedia); // Dades actualitzades
                objectOut.flush();

                // Llegir resposta del servidor
                String response = (String) objectIn.readObject();

                if ("MODIFY_MEDIA_OK".equals(response)) {
                    runOnUiThread(() -> Toast.makeText(this, "Canvis guardats correctament", Toast.LENGTH_SHORT).show());
                } else {
                    runOnUiThread(() -> Toast.makeText(this, "Error al guardar canvis", Toast.LENGTH_SHORT).show());
                }

            } catch (Exception e) {
                Log.e(TAG, "Error al guardar canvis", e);
                runOnUiThread(() -> Toast.makeText(this, "Error al guardar canvis: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    /**
     * Elimina l'obra seleccionada del servidor.
     */
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
                    Toast.makeText(this, "Media eliminada correctament", Toast.LENGTH_SHORT).show();
                    finish();
                });

            } catch (Exception e) {
                Log.e(TAG, "Error al eliminar Media", e);
                runOnUiThread(() -> Toast.makeText(this, "Error al eliminar Media: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}
