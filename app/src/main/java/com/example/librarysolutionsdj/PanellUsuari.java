package com.example.librarysolutionsdj;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * Classe PanellUsuari és una Activity que mostra el panell d'usuari.
 * Gestiona la sessió, visualitza el perfil de l'usuari i permet la desconnexió.
 */
public class PanellUsuari extends AppCompatActivity {

    private SessionManager sessionManager;
    private UserService userService;

    /**
     * Constructor per defecte per a inicialització sense dependències
     */
    public PanellUsuari() { }

    /**
     * Constructor per a injectar dependències, útil per a proves.
     *
     * @param sessionManager gestiona la sessió de l'usuari
     * @param userService servei d'usuari per a obtenir perfils i gestionar la desconnexió
     */
    public PanellUsuari(SessionManager sessionManager, UserService userService) {
        this.sessionManager = sessionManager;
        this.userService = userService;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_panell_usuari);

        // Inicialització de SessionManager i UserService o FakeUserService per a proves
        if (sessionManager == null) {
            sessionManager = new SessionManager(this);
        }
        if (userService == null) {
            if (getIntent().getBooleanExtra("INJECT_FAKE_SERVICE", false)) {
                userService = new FakeUserService();
            } else {
                userService = new UserService();
            }
        }

        // Verifica si hi ha una sessió activa; si no, redirigeix a LoginActivity
        if (sessionManager.getSessionId() == null) {
            Intent intent = new Intent(PanellUsuari.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        // Configura la interfície d'usuari
        configureUI();
    }

    /**
     * Configura la interfície d'usuari, incloent la configuració del botó de logout i el de gestió d'usuaris.
     */
    private void configureUI() {
        // Configuració dels marges per als sistemes de barres
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicialització dels elements de la UI
        Button gestioUsuarisButton = findViewById(R.id.gestio_usuaris_btn);
        gestioUsuarisButton.setVisibility(Button.GONE);

        // Carrega el perfil de l'usuari
        loadUserProfile();

        // Configuració del botó de logout
        Button logoutButton = findViewById(R.id.logout_btn);
        logoutButton.setOnClickListener(view -> performLogout());

        // Acció del botó de "Gestió d'usuaris" si està visible
        gestioUsuarisButton.setOnClickListener(view -> {
            Intent intent = new Intent(PanellUsuari.this, GestioUsuaris.class);
            startActivity(intent);
        });
    }

    /**
     * Mètode per injectar dependències, útil per a proves.
     *
     * @param sessionManager el gestor de sessió a injectar
     * @param userService el servei d'usuari a injectar
     */
    public void injectDependencies(SessionManager sessionManager, UserService userService) {
        this.sessionManager = sessionManager;
        this.userService = userService;
    }

    /**
     * Carrega el perfil d'usuari mitjançant UserService i actualitza la UI.
     */
    private void loadUserProfile() {
        String sessionId = sessionManager.getSessionId();
        if (sessionId == null) {
            showError("No hi ha sessió activa");
            return;
        }

        userService.getUserProfile(sessionId, new UserService.UserProfileCallback() {
            @Override
            public void onSuccess(UserProfile profile) {
                runOnUiThread(() -> updateUIWithProfile(profile));
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> showError(errorMessage));
            }
        });
    }

    /**
     * Actualitza la UI amb la informació del perfil de l'usuari.
     *
     * @param profile objecte UserProfile amb la informació de l'usuari
     */
    private void updateUIWithProfile(UserProfile profile) {
        ((TextView) findViewById(R.id.user_detail_username)).setText(profile.getAlias());
        ((TextView) findViewById(R.id.user_detail_realname)).setText(profile.getUsername());
        ((TextView) findViewById(R.id.nom_lbl)).setText(profile.getSurname1());
        ((TextView) findViewById(R.id.user_detail_surname2)).setText(profile.getSurname2());
        ((TextView) findViewById(R.id.usertype)).setText(profile.getUserType());

        Button gestioUsuarisButton = findViewById(R.id.gestio_usuaris_btn);
        if ("ADMIN".equals(profile.getUserType()) || "WORKER".equals(profile.getUserType())) {
            gestioUsuarisButton.setVisibility(Button.VISIBLE);
        }
    }

    /**
     * Executa el procés de logout per l'usuari i redirigeix a LoginActivity en cas d'èxit.
     */
    private void performLogout() {
        String sessionId = sessionManager.getSessionId();
        if (sessionId == null) {
            showError("No hi ha sessió activa");
            return;
        }

        userService.logout(sessionId, new UserService.LogoutCallback() {
            @Override
            public void onLogoutSuccess() {
                sessionManager.clearSession();
                runOnUiThread(() -> {
                    showSuccess("Logout exitoso");

                    // Redirigeix a LoginActivity
                    Intent intent = new Intent(PanellUsuari.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                });
            }

            @Override
            public void onLogoutError(String errorMessage) {
                runOnUiThread(() -> showError(errorMessage));
            }
        });
    }

    /**
     * Mostra un missatge d'error a l'usuari.
     *
     * @param errorMessage el missatge d'error a mostrar
     */
    private void showError(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
    }

    /**
     * Mostra un missatge d'èxit a l'usuari.
     *
     * @param successMessage el missatge d'èxit a mostrar
     */
    private void showSuccess(String successMessage) {
        Toast.makeText(this, successMessage, Toast.LENGTH_LONG).show();
    }
}
