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
        // Recollir dades del formulari
        String authorName = authorNameEditText.getText().toString().trim();
        String surname1 = surname1EditText.getText().toString().trim();
        String surname2 = surname2EditText.getText().toString().trim();
        String biography = biographyEditText.getText().toString().trim();
        String nationality = nationalityEditText.getText().toString().trim();
        String yearBirthStr = yearBirthEditText.getText().toString().trim();

        // Validació de camps obligatoris
        if (authorName.isEmpty() || nationality.isEmpty() || yearBirthStr.isEmpty()) {
            Snackbar.make(findViewById(android.R.id.content), "Si us plau, omple tots els camps obligatoris.", Snackbar.LENGTH_LONG).show();
            return;
        }

        int yearBirth;
        try {
            yearBirth = Integer.parseInt(yearBirthStr);
        } catch (NumberFormatException e) {
            Snackbar.make(findViewById(android.R.id.content), "L'any de naixement ha de ser un número.", Snackbar.LENGTH_LONG).show();
            return;
        }

        // Crear objecte Autor
        Author newAuthor = new Author(0, authorName, surname1, surname2, biography, nationality, yearBirth);

        // Enviar les dades al servidor en un fil independent
        new Thread(() -> {
            try {
                String response;

                if (isTestEnvironment) {
                    // Simulació en entorn de prova
                    response = MockServer.simulateCreateAuthorRequest();
                } else {
                    // Connexió real amb el servidor
                    try (Socket socket = new Socket("10.0.2.2", 12345);
                         PrintWriter commandOut = new PrintWriter(socket.getOutputStream(), true)) {

                        commandOut.println("ADD_AUTHOR");

                        ObjectOutputStream objectOut = new ObjectOutputStream(socket.getOutputStream());
                        objectOut.writeObject(newAuthor);
                        objectOut.flush();

                        Snackbar.make(findViewById(android.R.id.content), "Autor creat correctament", Snackbar.LENGTH_LONG).show();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(AuthorCreate.this, "Error creant autor: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}
