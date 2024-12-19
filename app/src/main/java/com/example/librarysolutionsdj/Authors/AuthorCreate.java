package com.example.librarysolutionsdj.Authors;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.librarysolutionsdj.R;
import com.example.librarysolutionsdj.ServerConnection.ServerConnectionHelper;
import com.example.librarysolutionsdj.Users.MockServer;
import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import app.model.Author;

/**
 * Activitat per crear un nou autor.
 * L'usuari pot introduir la informació necessària sobre l'autor i guardar-la.
 */
public class AuthorCreate extends AppCompatActivity {

    private EditText authorNameEditText, surname1EditText, surname2EditText, biographyEditText, nationalityEditText, yearBirthEditText;
    private Button createButton;
    private ImageButton backButton;
    private boolean isTestEnvironment = false; // Bandera per indicar si l'entorn és de prova

    /**
     * Inicialitza la interfície d'usuari de l'activitat.
     *
     * @param savedInstanceState Estat anterior de l'activitat, si n'hi ha.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author_create);
        EdgeToEdge.enable(this);

        // Inicialització dels components de la vista
        authorNameEditText = findViewById(R.id.author_name_edit_text);
        surname1EditText = findViewById(R.id.surname1_edit_text);
        surname2EditText = findViewById(R.id.surname2_edit_text);
        biographyEditText = findViewById(R.id.biography_edit_text);
        nationalityEditText = findViewById(R.id.nationality_edit_text);
        yearBirthEditText = findViewById(R.id.year_birth_edit_text);
        createButton = findViewById(R.id.create_button);
        backButton = findViewById(R.id.back_button);

        // Configuració dels botons
        createButton.setOnClickListener(v -> createAuthor());
        backButton.setOnClickListener(v -> finish());
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
     * Recull les dades del formulari i crea un nou autor.
     * Comprova la validesa dels camps obligatoris i envia les dades al servidor.
     */
    private void createAuthor() {
        // Recoger datos del formulario
        String authorName = authorNameEditText.getText().toString().trim();
        String surname1 = surname1EditText.getText().toString().trim();
        String surname2 = surname2EditText.getText().toString().trim();
        String biography = biographyEditText.getText().toString().trim();
        String nationality = nationalityEditText.getText().toString().trim();
        String yearBirthStr = yearBirthEditText.getText().toString().trim();

        // Validación de campos obligatorios
        if (authorName.isEmpty() || nationality.isEmpty() || yearBirthStr.isEmpty()) {
            Snackbar.make(findViewById(android.R.id.content), "Por favor, rellena todos los campos obligatorios.", Snackbar.LENGTH_LONG).show();
            return;
        }

        if (biography.isEmpty()) {
            biography = "Sin biografía"; // Asignar un valor predeterminado
        }

        int yearBirth;
        try {
            yearBirth = Integer.parseInt(yearBirthStr);
        } catch (NumberFormatException e) {
            Snackbar.make(findViewById(android.R.id.content), "El año de nacimiento debe ser un número.", Snackbar.LENGTH_LONG).show();
            return;
        }

        // Crear objeto Author
        Author newAuthor = new Author(0, authorName, surname1, surname2, biography, nationality, yearBirth);

        // Enviar los datos al servidor en un hilo independiente
        new Thread(() -> {
            ServerConnectionHelper connection = new ServerConnectionHelper();
            try {
                if (isTestEnvironment) {
                    // Simulación en entorno de prueba
                    MockServer.simulateCreateAuthorRequest();
                } else {
                    // Conexión real con el servidor
                    connection.connect(); // Establecer la conexión
                    connection.sendEncryptedCommand("ADD_AUTHOR"); // Enviar comando cifrado al servidor
                    connection.sendEncryptedObject(newAuthor); // Enviar el objeto del autor cifrado

                    // Mostrar confirmación en la interfaz de usuario
                    runOnUiThread(() -> Snackbar.make(findViewById(android.R.id.content), "Autor creado correctamente", Snackbar.LENGTH_LONG).show());
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(AuthorCreate.this, "Error creando autor: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            } finally {
                connection.close(); // Cerrar la conexión
            }
        }).start();


    }

}
