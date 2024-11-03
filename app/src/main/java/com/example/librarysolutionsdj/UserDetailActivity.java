package com.example.librarysolutionsdj;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class UserDetailActivity extends AppCompatActivity {

    private EditText userIdTextView, usernameTextView, realnameTextView, surname1TextView, surname2TextView, userTypeTextView;
    private Button editButton, saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        // Inicializar vistas
        userIdTextView = findViewById(R.id.user_detail_id);
        usernameTextView = findViewById(R.id.user_detail_username);
        realnameTextView = findViewById(R.id.user_detail_realname);
        surname1TextView = findViewById(R.id.user_detail_surname1);
        surname2TextView = findViewById(R.id.user_detail_surname2);
        userTypeTextView = findViewById(R.id.user_detail_usertype);
        editButton = findViewById(R.id.edit_button);
        saveButton = findViewById(R.id.save_button);

        // Obtener los datos del intent
        String userId = getIntent().getStringExtra("userId");
        String username = getIntent().getStringExtra("username");
        String realname = getIntent().getStringExtra("realname");
        String surname1 = getIntent().getStringExtra("surname1");
        String surname2 = getIntent().getStringExtra("surname2");
        String userType = getIntent().getStringExtra("userType");

        // Mostrar datos en las vistas
        userIdTextView.setText(userId);
        usernameTextView.setText(username);
        realnameTextView.setText(realname);
        surname1TextView.setText(surname1);
        surname2TextView.setText(surname2);
        userTypeTextView.setText(userType);

        // Botón para habilitar la edición de campos
        editButton.setOnClickListener(v -> enableEditing());

        // Botón para guardar los cambios
        saveButton.setOnClickListener(v -> saveChanges());
    }

    private void enableEditing() {
        // Habilitar los campos para edición
        usernameTextView.setEnabled(true);
        realnameTextView.setEnabled(true);
        surname1TextView.setEnabled(true);
        surname2TextView.setEnabled(true);
        userTypeTextView.setEnabled(true);

        // Mostrar botón de guardar y ocultar botón de editar
        editButton.setVisibility(Button.GONE);
        saveButton.setVisibility(Button.VISIBLE);
    }

    private void saveChanges() {
        // Lógica para guardar los cambios en el servidor o en la base de datos
        Toast.makeText(this, "Cambios guardados", Toast.LENGTH_SHORT).show();

        // Deshabilitar la edición nuevamente
        usernameTextView.setEnabled(false);
        realnameTextView.setEnabled(false);
        surname1TextView.setEnabled(false);
        surname2TextView.setEnabled(false);
        userTypeTextView.setEnabled(false);

        // Mostrar botón de editar y ocultar botón de guardar
        editButton.setVisibility(Button.VISIBLE);
        saveButton.setVisibility(Button.GONE);
    }
}
