package com.example.librarysolutionsdj.Login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.librarysolutionsdj.MainActivity;
import com.example.librarysolutionsdj.R;
import com.example.librarysolutionsdj.SessionManager.SessionManager;
import com.example.librarysolutionsdj.Users.PanellUsuari;

/**
 * LoginActivity gestiona la pantalla d'inici de sessió de l'aplicació.
 * Permet que els usuaris introdueixin les seves credencials per accedir.
 */
public class LoginActivity extends AppCompatActivity {

    private LoginService loginService;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView errorMessageTextView;
    private SessionManager sessionManager;

    /**
     * Inicialitza la interfície d'usuari i els serveis quan es crea l'activitat.
     *
     * @param savedInstanceState estat guardat de l'activitat, si n'hi ha.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Configura marges per als insets del sistema (barra de navegació, estat, etc.)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicialitza els camps de text i botons
        usernameEditText = findViewById(R.id.user);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.access);
        errorMessageTextView = findViewById(R.id.error_message);

        loginService = new LoginService(); // Instància predeterminada, pot ser injectada per a proves
        sessionManager = new SessionManager(this);

        configureBackButton();
        configureLoginButton();
    }

    /**
     * Permet injectar un LoginService personalitzat per a proves.
     *
     * @param loginService instància de LoginService per a gestionar el login
     */
    public void setLoginService(LoginService loginService) {
        this.loginService = loginService;
    }

    /**
     * Configura el botó de tornada per retornar a l'activitat principal.
     */
    private void configureBackButton() {
        ImageButton backButton = findViewById(R.id.back);
        backButton.setOnClickListener(aav -> {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        });
    }

    /**
     * Configura el botó de login per gestionar l'inici de sessió quan es fa clic.
     */
    private void configureLoginButton() {
        loginButton.setOnClickListener(view -> {
            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            performLogin(username, password);
        });
    }

    /**
     * Realitza el procés de login amb les credencials proporcionades.
     *
     * @param username nom d'usuari introduït
     * @param password contrasenya introduïda
     */
    private void performLogin(String username, String password) {
        errorMessageTextView.setVisibility(View.GONE);

        loginService.login(username, password, new LoginService.LoginCallback() {
            @Override
            public void onLoginSuccess(String sessionId, String userType, int userId) {
                // Guarda la sessió en SharedPreferences i mostra missatge d'èxit
                sessionManager.saveSession(sessionId, userType, userId); // Guardamos también userId
                runOnUiThread(() -> {
                    errorMessageTextView.setVisibility(View.GONE);
                    TextView successMessageTextView = findViewById(R.id.success_message);
                    successMessageTextView.setText("Login OK");
                    successMessageTextView.setVisibility(View.VISIBLE);

                    // Navega a PanellUsuari després de l'èxit
                    startActivity(new Intent(LoginActivity.this, PanellUsuari.class));
                    finish();
                });
            }

            @Override
            public void onLoginFailure(String errorMessage) {
                // Mostra el missatge d'error en cas de fallada en el login
                runOnUiThread(() -> {
                    errorMessageTextView.setText(errorMessage);
                    errorMessageTextView.setVisibility(View.VISIBLE);
                });
            }
        });
    }
}
