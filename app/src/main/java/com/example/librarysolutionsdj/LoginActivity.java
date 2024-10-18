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


//La pantalla de Login.
//Implementada la conexión con el servidor de sockets con conector JDBC.

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

        //Se identifica el ID del botón para volver a la pantalla anterior
        ImageButton backButton = findViewById(R.id.back);

        // Listener para que al hacer click en el botón se abra la pantalla anterior
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finaliza la actividad actual y vuelve a la anterior
                finish();
            }
        });


        // Listener para el boton de acceder.
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // se indentifican los campos de usuario y contraseña
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                new Thread(() -> {
                    try {
                        Socket socket = new Socket("10.0.2.2", 12345); //si la app se prueba en una máquina virtual de Android Studio, la ip debe ser la 10.0.2.2
                        PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

                        // se envían los datos de usuario y contraseña
                        out.println(username);
                        out.println(password);

                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        String response = in.readLine();
                        // esperamos respuesta del servidor a nuestra consulta

                        runOnUiThread(() -> {
                            if ("LOGIN_OK".equals(response)) { // si los datos coinciden, nos da LOGIN OK
                                Toast.makeText(LoginActivity.this, "Login OK", Toast.LENGTH_SHORT).show();

                                //Y se redirige al usuario a su panel de usuario. Se gestiona en la clase "PanellUsuari.java"
                                Intent intent = new Intent(LoginActivity.this, PanellUsuari.class);
                                startActivity(intent);

                            } else {
                                // si los datos enviado no coinciden, mostramos error de datos incorrectos.
                                Toast.makeText(LoginActivity.this, "Dades incorrectes", Toast.LENGTH_SHORT).show();
                            }
                        });

                        socket.close(); // cerramos conexión
                    } catch (Exception e){
                        // mostramos errores en pantalla (solo para pruebas)
                        runOnUiThread(() -> Toast.makeText(LoginActivity.this, "ERROR: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    }
                }).start();
            }
        });
    }


}