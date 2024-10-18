package com.example.librarysolutionsdj;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class LoginActivity extends AppCompatActivity {

    EditText usernameEditText;
    EditText passwordEditText;
    Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        usernameEditText = findViewById(R.id.user);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.access);

        // Troba el ImageButton amb l'ID "back"
        ImageButton backButton = findViewById(R.id.back);

        // Afegeix el listener per a que al fer click en la ImageButton torni a la página anterior
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finaliza la actividad actual y vuelve a la anterior
                finish();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                new Thread(() -> {
                    try {
                        Socket socket = new Socket("10.0.2.2", 12345);
                        PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
                        out.println(username);
                        out.println(password);

                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        String response = in.readLine();

                        runOnUiThread(() -> {
                            if ("LOGIN_OK".equals(response)) {
                                Toast.makeText(LoginActivity.this, "Login OK", Toast.LENGTH_SHORT).show();

                                //Redirigir l'usuari al PanellUsuari.java
                                Intent intent = new Intent(LoginActivity.this, PanellUsuari.class);
                                startActivity(intent);

                            } else {
                                Toast.makeText(LoginActivity.this, "Dades incorrectes", Toast.LENGTH_SHORT).show();
                            }
                        });

                        socket.close();
                    } catch (Exception e){
                        runOnUiThread(() -> Toast.makeText(LoginActivity.this, "ERROR: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    }
                }).start();
            }
        });
    }


}