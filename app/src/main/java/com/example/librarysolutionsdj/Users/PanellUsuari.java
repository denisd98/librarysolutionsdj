package com.example.librarysolutionsdj.Users;

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

import com.example.librarysolutionsdj.Authors.GestioAutors;
import com.example.librarysolutionsdj.Loans.BibliotecaActivity;
import com.example.librarysolutionsdj.Login.LoginActivity;
import com.example.librarysolutionsdj.Media.GestioMedia;
import com.example.librarysolutionsdj.R;
import com.example.librarysolutionsdj.SessionManager.SessionManager;

/**
 * Classe PanellUsuari és una Activity que mostra el panell d'usuari.
 * Gestiona la sessió, visualitza el perfil de l'usuari i permet la desconnexió.
 */
public class PanellUsuari extends AppCompatActivity {

    private static final int REQUEST_CODE_EDIT_PROFILE = 1;  // Código para identificar el resultado de editar perfil
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

    @Override
    protected void onResume() {
        super.onResume();
        loadUserProfile();
    }

    /**
     * Configura la interfície d'usuari, incloent la configuració dels botons i la seva visibilitat.
     */
    private void configureUI() {
        // Configuració dels marges per als sistemes de barres
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button editarPerfilButton = findViewById(R.id.editar_perfil);
        editarPerfilButton.setOnClickListener(view -> {
            Intent intent = new Intent(PanellUsuari.this, UserDetailActivity.class);
            intent.putExtra("isOwnProfile", true);  // Indica que se trata del perfil propio
            intent.putExtra("userId", sessionManager.getUserId()); // Envía el userId del usuario autenticado
            startActivityForResult(intent, REQUEST_CODE_EDIT_PROFILE); // Usar startActivityForResult para recibir el resultado
        });

        Button accedirBibliotecaButton = findViewById(R.id.accedir_biblioteca);
        accedirBibliotecaButton.setOnClickListener(v -> {
            Intent intent = new Intent(PanellUsuari.this, BibliotecaActivity.class);
            startActivity(intent);
        });

        // Inicialització dels botons
        Button gestioUsuarisButton = findViewById(R.id.gestio_usuaris_btn);
        Button gestioObresButton = findViewById(R.id.gestio_obres_btn);
        Button gestioAutorsButton = findViewById(R.id.gestio_autors_btn);

        gestioUsuarisButton.setVisibility(Button.GONE);
        gestioObresButton.setVisibility(Button.GONE);
        gestioAutorsButton.setVisibility(Button.GONE);

        // Configura accions per als botons
        gestioUsuarisButton.setOnClickListener(view -> {
            Intent intent = new Intent(PanellUsuari.this, GestioUsuaris.class);
            startActivity(intent);
        });

        gestioObresButton.setOnClickListener(view -> {
            Intent intent = new Intent(PanellUsuari.this, GestioMedia.class);
            startActivity(intent);
        });

        gestioAutorsButton.setOnClickListener(view -> {
            Intent intent = new Intent(PanellUsuari.this, GestioAutors.class);
            startActivity(intent);
        });

        // Configuració del botó de logout
        Button logoutButton = findViewById(R.id.logout_btn);
        logoutButton.setOnClickListener(view -> performLogout());
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

        // Solicita el perfil del usuario al servidor
        userService.getUserProfile(sessionId, new UserService.UserProfileCallback() {
            @Override
            public void onSuccess(UserProfile profile) {
                // Actualiza la interfaz de usuario con los datos recibidos
                runOnUiThread(() -> updateUIWithProfile(profile));
            }

            @Override
            public void onError(String errorMessage) {
                // Manejo de errores
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
        TextView usernameTextView = findViewById(R.id.user_detail_username);
        TextView realNameTextView = findViewById(R.id.user_detail_realname);
        TextView surname1TextView = findViewById(R.id.nom_lbl);
        TextView surname2TextView = findViewById(R.id.user_detail_surname2);
        TextView userTypeTextView = findViewById(R.id.usertype);

        // Actualiza los campos de texto con los datos del perfil
        usernameTextView.setText(profile.getAlias());
        realNameTextView.setText(profile.getUsername());
        surname1TextView.setText(profile.getSurname1());
        surname2TextView.setText(profile.getSurname2());
        userTypeTextView.setText(profile.getUserType());

        // Configura la visibilitat dels botons segons el tipus d'usuari
        Button gestioUsuarisButton = findViewById(R.id.gestio_usuaris_btn);
        Button gestioObresButton = findViewById(R.id.gestio_obres_btn);
        Button gestioAutorsButton = findViewById(R.id.gestio_autors_btn);

        String userType = profile.getUserType();
        if ("ADMIN".equals(userType)) {
            gestioUsuarisButton.setVisibility(Button.VISIBLE);
        } else if ("WORKER".equals(userType)) {
            gestioObresButton.setVisibility(Button.VISIBLE);
            gestioAutorsButton.setVisibility(Button.VISIBLE);
        } else {
            gestioUsuarisButton.setVisibility(Button.GONE);
            gestioObresButton.setVisibility(Button.GONE);
            gestioAutorsButton.setVisibility(Button.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_EDIT_PROFILE && resultCode == RESULT_OK) {
            // Ignorar los datos locales y forzar la recarga desde el servidor
            loadUserProfile();
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
