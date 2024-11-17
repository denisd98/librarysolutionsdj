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
import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import app.model.Author;

public class AuthorCreate extends AppCompatActivity {

    private EditText authorNameEditText, surname1EditText, surname2EditText, biographyEditText, nationalityEditText, yearBirthEditText;
    private Button createButton;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author_create);
        EdgeToEdge.enable(this);

        authorNameEditText = findViewById(R.id.author_name_edit_text);
        surname1EditText = findViewById(R.id.surname1_edit_text);
        surname2EditText = findViewById(R.id.surname2_edit_text);
        biographyEditText = findViewById(R.id.biography_edit_text);
        nationalityEditText = findViewById(R.id.nationality_edit_text);
        yearBirthEditText = findViewById(R.id.year_birth_edit_text);
        createButton = findViewById(R.id.create_button);
        backButton = findViewById(R.id.back_button);

        createButton.setOnClickListener(v -> createAuthor());
        backButton.setOnClickListener(v -> finish());
    }

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

        Author newAuthor = new Author(0, authorName, surname1, surname2, biography, nationality, yearBirth);

        new Thread(() -> {
            try {
                Socket socket = new Socket("10.0.2.2", 12345);

                PrintWriter commandOut = new PrintWriter(socket.getOutputStream(), true);
                commandOut.println("ADD_AUTHOR");

                ObjectOutputStream objectOut = new ObjectOutputStream(socket.getOutputStream());
                objectOut.writeObject(newAuthor);
                objectOut.flush();

                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String response = in.readLine();

                if ("AUTHOR_CREATED".equals(response)) {
                    runOnUiThread(() -> {
                        Toast.makeText(AuthorCreate.this, "Autor creat correctament", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(AuthorCreate.this, "Error creant autor: " + response, Toast.LENGTH_SHORT).show());
                }

                commandOut.close();
                objectOut.close();
                in.close();
                socket.close();

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(AuthorCreate.this, "Error creant autor: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}
