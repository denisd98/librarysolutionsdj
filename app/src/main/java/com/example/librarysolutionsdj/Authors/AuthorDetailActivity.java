package com.example.librarysolutionsdj.Authors;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.librarysolutionsdj.R;
import com.example.librarysolutionsdj.ServerConnection.ServerConnectionHelper;
import com.google.android.material.snackbar.Snackbar;

import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import app.model.Author;

/**
 * Activitat per visualitzar i modificar els detalls d'un autor seleccionat.
 * Permet actualitzar les dades de l'autor o eliminar-lo de la base de dades.
 */
public class AuthorDetailActivity extends AppCompatActivity {

    private EditText authorNameEditText, surname1EditText, surname2EditText, biographyEditText, nationalityEditText, yearBirthEditText;
    private Button saveButton, deleteButton;
    private ImageButton backButton;
    private Author selectedAuthor;
    private static final String TAG = "AuthorDetailActivity";

    /**
     * Inicialitza la interfície d'usuari de l'activitat i carrega les dades de l'autor seleccionat.
     *
     * @param savedInstanceState Estat anterior de l'activitat, si n'hi ha.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author_detail);

        // Obtenir l'autor seleccionat
        selectedAuthor = (Author) getIntent().getSerializableExtra("selectedAuthor");

        // Inicialitzar els components de la vista
        authorNameEditText = findViewById(R.id.author_name_edit_text);
        surname1EditText = findViewById(R.id.surname1_edit_text);
        surname2EditText = findViewById(R.id.surname2_edit_text);
        biographyEditText = findViewById(R.id.biography_edit_text);
        nationalityEditText = findViewById(R.id.nationality_edit_text);
        yearBirthEditText = findViewById(R.id.year_birth_edit_text);
        saveButton = findViewById(R.id.save_button);
        deleteButton = findViewById(R.id.delete_button);
        backButton = findViewById(R.id.back_button);

        // Carregar les dades de l'autor seleccionat
        if (selectedAuthor != null) {
            populateFieldsWithSelectedAuthor();
        }

        // Configurar els botons
        saveButton.setOnClickListener(v -> saveChanges());
        deleteButton.setOnClickListener(v -> deleteAuthor());
        backButton.setOnClickListener(v -> finish());
    }

    /**
     * Omple els camps del formulari amb les dades de l'autor seleccionat.
     */
    private void populateFieldsWithSelectedAuthor() {
        if (selectedAuthor != null) {
            authorNameEditText.setText(selectedAuthor.getAuthorname());
            surname1EditText.setText(selectedAuthor.getSurname1());
            surname2EditText.setText(selectedAuthor.getSurname2() != null ? selectedAuthor.getSurname2() : "");
            biographyEditText.setText(selectedAuthor.getBiography());
            nationalityEditText.setText(selectedAuthor.getNationality());
            yearBirthEditText.setText(String.valueOf(selectedAuthor.getYearbirth()));
        }
    }

    /**
     * Desa els canvis realitzats a l'autor.
     * Valida els camps obligatoris i actualitza les dades en el servidor.
     */
    private void saveChanges() {
        // Validar campos obligatorios
        if (authorNameEditText.getText().toString().isEmpty() || nationalityEditText.getText().toString().isEmpty()) {
            Snackbar.make(findViewById(android.R.id.content), "Nombre y nacionalidad son obligatorios", Snackbar.LENGTH_SHORT).show();
            return;
        }

        // Validar el año de nacimiento como número
        int yearBirth;
        try {
            yearBirth = Integer.parseInt(yearBirthEditText.getText().toString());
        } catch (NumberFormatException e) {
            Snackbar.make(findViewById(android.R.id.content), "El año de nacimiento debe ser un número", Snackbar.LENGTH_SHORT).show();
            return;
        }

        // Actualizar el objeto Author con los nuevos datos
        selectedAuthor.setAuthorname(authorNameEditText.getText().toString());
        selectedAuthor.setSurname1(surname1EditText.getText().toString());
        selectedAuthor.setSurname2(surname2EditText.getText().toString());
        selectedAuthor.setBiography(biographyEditText.getText().toString());
        selectedAuthor.setNationality(nationalityEditText.getText().toString());
        selectedAuthor.setYearbirth(yearBirth);

        // Enviar datos al servidor
        new Thread(() -> {
            ServerConnectionHelper connection = new ServerConnectionHelper();
            try {
                // Establecer la conexión con el servidor
                connection.connect();
                connection.sendCommand("MODIFY_AUTHOR");
                Log.d(TAG, "Comando enviado: MODIFY_AUTHOR");

                // Enviar el objeto Author
                connection.sendObject(selectedAuthor);
                Log.d(TAG, "Objeto Author enviado: " + selectedAuthor.getAuthorname());

                // Confirmación de éxito
                runOnUiThread(() -> {
                    Snackbar.make(findViewById(android.R.id.content), "Canvis aplicats correctament", Snackbar.LENGTH_SHORT).show();
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("updatedAuthor", selectedAuthor);
                    setResult(RESULT_OK, resultIntent);
                });

            } catch (Exception e) {
                Log.e(TAG, "Error al modificar el autor", e);
                runOnUiThread(() -> Toast.makeText(this, "Error al modificar el autor: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            } finally {
                // Cerrar la conexión
                connection.close();
            }
        }).start();

    }

    /**
     * Elimina l'autor seleccionat del sistema.
     */
    private void deleteAuthor() {
        new Thread(() -> {
            ServerConnectionHelper connection = new ServerConnectionHelper();
            try {
                connection.connect();
                connection.sendCommand("DELETE_AUTHOR");

                // Enviar el ID del autor a eliminar
                connection.sendInt(selectedAuthor.getAuthorid());
                Log.d(TAG, "Autor eliminat: " + selectedAuthor.getAuthorid());

                // Confirmación en la interfaz de usuario
                Snackbar.make(findViewById(android.R.id.content), "Autor eliminat correctament", Snackbar.LENGTH_SHORT).show();

            } catch (Exception e) {
                Log.e(TAG, "Error eliminant autor", e);
                runOnUiThread(() -> Toast.makeText(AuthorDetailActivity.this, "Error eliminant autor: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            } finally {
                // Cerrar la conexión en el bloque finally
                connection.close();
            }
        }).start();
    }

}
