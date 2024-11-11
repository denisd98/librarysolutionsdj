package com.example.librarysolutionsdj;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * Classe que representa la pantalla principal de l'aplicació.
 * Dona la benvinguda als usuaris i els permet iniciar sessió o registrar-se (registre encara no implementat).
 *
 * Aquesta classe extén {@link AppCompatActivity} i utilitza la funcionalitat {@link EdgeToEdge} per gestionar el disseny visual.
 *
 * @author Denys Dyachuk
 * @version 0.3, 21/10/24
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Mètode principal que s'executa en iniciar l'activitat.
     *
     * En aquest mètode es configura la interfície d'usuari i els comportaments dels botons.
     * Configura el sistema EdgeToEdge per a la integració de la interfície i
     * estableix l'acció del botó "login_btn" per iniciar l'activitat de login {@link LoginActivity}.
     *
     * @param savedInstanceState Paràmetre opcional que conté l'estat guardat de l'activitat, si està disponible.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Configura els marges per als insets del sistema.
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Obtenir referència del botó amb l'ID "login_btn"
        Button loginButton = findViewById(R.id.login_btn);

        // Configurar l'esdeveniment onClick del botó per iniciar l'activitat LoginActivity
        loginButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Mètode que s'executa quan es fa clic al botó de login.
             * Llença l'activitat {@link LoginActivity} per gestionar l'inici de sessió de l'usuari.
             *
             * @param v La vista que ha estat clicada.
             */
            @Override
            public void onClick(View v) {
                // Crear un Intent per obrir LoginActivity
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);  // Obrir LoginActivity
            }
        });
    }
}
