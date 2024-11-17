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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import app.model.Author;

public class AuthorDetailActivity extends AppCompatActivity {

    private EditText authorNameEditText, surname1EditText, surname2EditText, biographyEditText, nationalityEditText, yearBirthEditText;
    private Button saveButton, deleteButton;
    private ImageButton backButton;
    private Author selectedAuthor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author_detail);
        EdgeToEdge.enable(this);

        selectedAuthor = (Author) getIntent().getSerializableExtra("selectedAuthor");

        authorNameEditText = findViewById(R.id.author_name_edit_text);
        surname1EditText = findViewById(R.id.surname1_edit_text);
        surname2EditText = findViewById(R.id.surname2_edit_text);
        biographyEditText = findViewById(R.id.biography_edit_text);
        nationalityEditText = findViewById(R.id.nationality_edit_text);
        yearBirthEditText = findViewById(R.id.year_birth_edit_text);
        saveButton = findViewById(R.id.save_button);
        deleteButton = findViewById(R.id.delete_button);
        backButton = findViewById(R.id.back_button);

        if (selectedAuthor != null) {
            populateFieldsWithSelectedAuthor();
        }

        saveButton.setOnClickListener(v -> saveChanges());
        deleteButton.setOnClickListener(v -> deleteAuthor());
        backButton.setOnClickListener(v -> finish());
    }

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

    private void saveChanges() {
        // Validar campos obligatorios
        if (authorNameEditText.getText().toString().isEmpty() || nationalityEditText.getText().toString().isEmpty()) {
            Toast.makeText(this, "Nom i Nacionalitat són obligatoris", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validar año de nacimiento como entero
        int yearBirth;
        try {
            yearBirth = Integer.parseInt(yearBirthEditText.getText().toString());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Any de naixement ha de ser un número", Toast.LENGTH_SHORT).show();
            return;
        }

        // Actualizar el objeto autor con los nuevos datos
        selectedAuthor.setAuthorname(authorNameEditText.getText().toString());
        selectedAuthor.setSurname1(surname1EditText.getText().toString());
        selectedAuthor.setSurname2(surname2EditText.getText().toString());
        selectedAuthor.setBiography(biographyEditText.getText().toString());
        selectedAuthor.setNationality(nationalityEditText.getText().toString());
        selectedAuthor.setYearbirth(yearBirth);

        new Thread(() -> {
            try (Socket socket = new Socket("10.0.2.2", 12345);
                 PrintWriter commandOut = new PrintWriter(socket.getOutputStream(), true)) {

                commandOut.println("MODIFY_AUTHOR");
                commandOut.flush();

                ObjectOutputStream objectOut = new ObjectOutputStream(socket.getOutputStream());
                objectOut.writeInt(selectedAuthor.getAuthorid()); // Cambiado a getAuthorid()
                objectOut.writeObject(selectedAuthor);
                objectOut.flush();

                BufferedReader responseReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String response = responseReader.readLine();

                if ("MODIFY_AUTHOR_OK".equals(response)) {
                    runOnUiThread(() -> {
                        Toast.makeText(AuthorDetailActivity.this, "Changes saved successfully", Toast.LENGTH_SHORT).show();
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("updatedAuthor", selectedAuthor);
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(AuthorDetailActivity.this, "Error saving changes", Toast.LENGTH_SHORT).show());
                }

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(AuthorDetailActivity.this, "Error saving changes: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void deleteAuthor() {
        new Thread(() -> {
            try (Socket socket = new Socket("10.0.2.2", 12345);
                 PrintWriter commandOut = new PrintWriter(socket.getOutputStream(), true)) {

                commandOut.println("DELETE_AUTHOR");
                ObjectOutputStream objectOut = new ObjectOutputStream(socket.getOutputStream());
                objectOut.writeInt(selectedAuthor.getAuthorid()); // Cambiado a getAuthorid()
                objectOut.flush();

                runOnUiThread(() -> {
                    Toast.makeText(AuthorDetailActivity.this, "Author deleted successfully", Toast.LENGTH_SHORT).show();
                    finish();
                });

            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(AuthorDetailActivity.this, "Error deleting author: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}