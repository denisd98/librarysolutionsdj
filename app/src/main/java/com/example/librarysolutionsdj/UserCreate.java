package com.example.librarysolutionsdj;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import app.model.User;

public class UserCreate extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText, realnameEditText, surname1EditText, surname2EditText;
    private Spinner userTypeSpinner;
    private Button createButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_create);

        usernameEditText = findViewById(R.id.username_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        realnameEditText = findViewById(R.id.realname_edit_text);
        surname1EditText = findViewById(R.id.surname1_edit_text);
        surname2EditText = findViewById(R.id.surname2_edit_text);
        userTypeSpinner = findViewById(R.id.user_type_spinner);
        createButton = findViewById(R.id.create_button);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.user_types_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userTypeSpinner.setAdapter(adapter);

        createButton.setOnClickListener(v -> createUser());
    }

    private void createUser() {
        // Recoger datos del formulario
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String realname = realnameEditText.getText().toString();
        String surname1 = surname1EditText.getText().toString();
        String surname2 = surname2EditText.getText().toString();
        String userType = userTypeSpinner.getSelectedItem().toString();

        User newUser = new User(0, username, password, realname, surname1, surname2, User.stringToUserType(userType));

        new Thread(() -> {
            try {
                Socket socket = new Socket("10.0.2.2", 12345);

                PrintWriter commandOut = new PrintWriter(socket.getOutputStream(), true);
                commandOut.println("ADD_USER");

                ObjectOutputStream objectOut = new ObjectOutputStream(socket.getOutputStream());
                objectOut.writeObject(newUser);
                objectOut.flush();

                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String response = in.readLine();
                if ("USER_CREATED".equals(response)) {
                    int generatedId = Integer.parseInt(in.readLine());
                    newUser.setId(generatedId); // Actualiza el ID en el objeto User

                    // Notificación de creación exitosa
                    runOnUiThread(() -> {
                        Toast.makeText(UserCreate.this, "User created successfully", Toast.LENGTH_SHORT).show();
                        finish(); // Cierra la actividad de creación
                    });
                }

                commandOut.close();
                objectOut.close();
                in.close();
                socket.close();

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(UserCreate.this, "Error creating user: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

}
