package com.example.librarysolutionsdj.Authors;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.librarysolutionsdj.R;
import com.example.librarysolutionsdj.Users.MockServer;
import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.InputStreamReader;
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
    private boolean isTestEnvironment = false; // Bandera per indicar si l'entorn és de prova

    /**
     * Inicialitza la interfície d'usuari de l'activitat i carrega les dades de l'autor seleccionat.
     *
     * @param savedInstanceState Estat anterior de l'activitat, si n'hi ha.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author_detail);
        EdgeToEdge.enable(this);

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
     * Configura l'entorn de prova.
     *
     * @param isTestEnvironment Indica si l'activitat s'executa en un entorn de prova.
     */
    public void setTestEnvironment(boolean isTestEnvironment) {
        this.isTestEnvironment = isTestEnvironment;
    }

    /**
     * Desa els canvis realitzats a l'autor.
     * Valida els camps obligatoris i actualitza les dades en el servidor.
     */
    private void saveChanges() {
        // Validar camps obligatoris
        if (authorNameEditText.getText().toString().isEmpty() || nationalityEditText.getText().toString().isEmpty()) {
            Snackbar.make(findViewById(android.R.id.content), "Nom i Nacionalitat són obligatoris", Snackbar.LENGTH_SHORT).show();
            return;
        }

        // Validar l'any de naixement com a número
        int yearBirth;
        try {
            yearBirth = Integer.parseInt(yearBirthEditText.getText().toString());
        } catch (NumberFormatException e) {
            Snackbar.make(findViewById(android.R.id.content), "Any de naixement ha de ser un número", Snackbar.LENGTH_SHORT).show();
            return;
        }

        // Actualitzar l'objecte autor amb les noves dades
        selectedAuthor.setAuthorname(authorNameEditText.getText().toString());
        selectedAuthor.setSurname1(surname1EditText.getText().toString());
        selectedAuthor.setSurname2(surname2EditText.getText().toString());
        selectedAuthor.setBiography(biographyEditText.getText().toString());
        selectedAuthor.setNationality(nationalityEditText.getText().toString());
        selectedAuthor.setYearbirth(yearBirth);

        // Enviar les dades al servidor o simular resposta en entorn de prova
        new Thread(() -> {
            try {
                String response;
                if (isTestEnvironment) {
                    response = MockServer.simulateModifyAuthorRequest();
                } else {
                    try (Socket socket = new Socket("10.0.2.2", 12345);
                         PrintWriter commandOut = new PrintWriter(socket.getOutputStream(), true)) {

                        commandOut.println("MODIFY_AUTHOR");
                        commandOut.flush();

                        ObjectOutputStream objectOut = new ObjectOutputStream(socket.getOutputStream());
                        objectOut.writeInt(selectedAuthor.getAuthorid());
                        objectOut.writeObject(selectedAuthor);
                        objectOut.flush();

                        BufferedReader responseReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        response = responseReader.readLine();
                    }
                }

                if ("MODIFY_AUTHOR_OK".equals(response)) {
                    runOnUiThread(() -> {
                        Snackbar.make(findViewById(android.R.id.content), "Canvis aplicats satisfactoriament", Snackbar.LENGTH_LONG).show();
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("updatedAuthor", selectedAuthor);
                        setResult(RESULT_OK, resultIntent);
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(AuthorDetailActivity.this, "Error guardant canvis", Toast.LENGTH_SHORT).show());
                }

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(AuthorDetailActivity.this, "Error guardant canvis: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    /**
     * Elimina l'autor seleccionat del sistema.
     */
    private void deleteAuthor() {
        new Thread(() -> {
            try (Socket socket = new Socket("10.0.2.2", 12345);
                 PrintWriter commandOut = new PrintWriter(socket.getOutputStream(), true)) {

                commandOut.println("DELETE_AUTHOR");
                ObjectOutputStream objectOut = new ObjectOutputStream(socket.getOutputStream());
                objectOut.writeInt(selectedAuthor.getAuthorid());
                objectOut.flush();

                runOnUiThread(() -> {
                    Toast.makeText(AuthorDetailActivity.this, "Autor eliminat satisfactoriament", Toast.LENGTH_SHORT).show();
                    finish();
                });

            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(AuthorDetailActivity.this, "Error eliminant autor: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}
