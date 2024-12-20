// UserDetailActivity.java
package com.example.librarysolutionsdj.Users;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.librarysolutionsdj.R;
import com.example.librarysolutionsdj.ServerConnection.ServerConnectionHelper;
import com.example.librarysolutionsdj.SessionManager.SessionManager;
import com.google.android.material.snackbar.Snackbar;

import app.model.User;

/**
 * Activitat que permet gestionar els detalls d'un usuari existent.
 * Inclou funcions per modificar o eliminar un usuari.
 */
public class UserDetailActivity extends AppCompatActivity {

    private EditText usernameEditText, realnameEditText, surname1EditText, surname2EditText, passwordEditText;
    private Spinner userTypeSpinner;
    private Button saveButton, deleteButton;
    private ImageButton backButton;
    private User selectedUser;
    private SessionManager sessionManager;
    private UserService userService;
    private boolean isOwnProfile; // Indica si l'usuari està editant el seu propi perfil
    private boolean isTestEnvironment = false; // Indica si l'activitat està en un entorn de proves

    private static final String TAG = "UserDetailActivity";

    /**
     * Configura la interfície gràfica de l'activitat i inicialitza els components.
     *
     * @param savedInstanceState l'estat de l'activitat guardat anteriorment.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        EdgeToEdge.enable(this);

        sessionManager = new SessionManager(this);
        userService = new UserService();

        isOwnProfile = getIntent().getBooleanExtra("isOwnProfile", false);
        selectedUser = (User) getIntent().getSerializableExtra("selectedUser");

        usernameEditText = findViewById(R.id.username_edit_text);
        realnameEditText = findViewById(R.id.realname_edit_text);
        surname1EditText = findViewById(R.id.surname1_edit_text);
        surname2EditText = findViewById(R.id.surname2_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        userTypeSpinner = findViewById(R.id.user_type_spinner);
        saveButton = findViewById(R.id.save_button);
        backButton = findViewById(R.id.back_button5);
        deleteButton = findViewById(R.id.delete_button);

        // Configurar botons segons el perfil
        deleteButton.setEnabled(!isOwnProfile);

        if (isOwnProfile) {
            loadCurrentUserProfile();
        } else if (selectedUser != null) {
            populateFieldsWithSelectedUser();
        }

        saveButton.setOnClickListener(v -> saveChanges());
        backButton.setOnClickListener(v -> finish());

        if (!isOwnProfile) {
            deleteButton.setOnClickListener(v -> deleteUser());
        }
    }

    /**
     * Carrega el perfil de l'usuari actual des del servidor.
     */
    private void loadCurrentUserProfile() {
        String sessionId = sessionManager.getSessionId();
        userService.getUserProfile(sessionId, new UserService.UserProfileCallback() {
            @Override
            public void onSuccess(UserProfile profile) {
                selectedUser = new User(sessionManager.getUserId(), profile.getAlias(), profile.getPassword(),
                        profile.getUsername(), profile.getSurname1(), profile.getSurname2(), profile.getUserType());
                runOnUiThread(() -> populateFields());
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    Log.e(TAG, "Error carregant el perfil de l'usuari: " + errorMessage);
                    Toast.makeText(UserDetailActivity.this, "Error carregant el perfil de l'usuari", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        });
    }

    /**
     * Omple els camps del formulari amb les dades de l'usuari seleccionat.
     */
    private void populateFieldsWithSelectedUser() {
        populateFields();
    }

    /**
     * Omple els camps del formulari amb les dades de l'usuari.
     */
    private void populateFields() {
        if (selectedUser != null) {
            usernameEditText.setText(selectedUser.getUsername());
            realnameEditText.setText(selectedUser.getRealname());
            surname1EditText.setText(selectedUser.getSurname1());
            surname2EditText.setText(selectedUser.getSurname2());
            passwordEditText.setText(selectedUser.getPassword());

            String userType = selectedUser.getTypeAsString();
            String[] userTypes = getResources().getStringArray(R.array.user_types);
            for (int i = 0; i < userTypes.length; i++) {
                if (userTypes[i].equals(userType)) {
                    userTypeSpinner.setSelection(i);
                    break;
                }
            }
        }
    }

    /**
     * Configura si l'activitat ha d'executar-se en un entorn de proves.
     *
     * @param isTestEnvironment cert si l'activitat està en mode proves.
     */
    public void setTestEnvironment(boolean isTestEnvironment) {
        this.isTestEnvironment = isTestEnvironment;
    }

    /**
     * Guarda els canvis realitzats en l'usuari.
     * Si l'entorn és de proves, s'utilitza una resposta simulada.
     */
    private void saveChanges() {
        if (passwordEditText.getText().toString().isEmpty()) {
            Snackbar.make(findViewById(android.R.id.content), "La contrasenya no pot estar buida", Snackbar.LENGTH_LONG).show();
            return;
        }

        selectedUser.setUsername(usernameEditText.getText().toString());
        selectedUser.setRealname(realnameEditText.getText().toString());
        selectedUser.setSurname1(surname1EditText.getText().toString());
        selectedUser.setSurname2(surname2EditText.getText().toString());
        selectedUser.setPassword(passwordEditText.getText().toString());
        selectedUser.setType(User.stringToUserType(userTypeSpinner.getSelectedItem().toString()));

        new Thread(() -> {
            try {
                if (isTestEnvironment) {
                    // En entorn de proves, simula la resposta
                    String response = MockServer.simulateModifyUserRequest();
                    // Asumimos que MockServer ya simula la respuesta apropiada
                    runOnUiThread(() -> Snackbar.make(findViewById(android.R.id.content), "Canvis aplicats satisfactòriament (mock)", Snackbar.LENGTH_LONG).show());
                } else {
                    ServerConnectionHelper connection = new ServerConnectionHelper();
                    try {
                        connection.connect(); // Establecer conexión
                        connection.sendEncryptedCommand("MODIFY_USER"); // Enviar comando

                        // Enviar el objeto usuario
                        connection.sendEncryptedObject(selectedUser);

                        // Como el servidor no envía una respuesta para MODIFY_USER, asumimos éxito
                        runOnUiThread(() -> Snackbar.make(findViewById(android.R.id.content), "Canvis aplicats satisfactòriament", Snackbar.LENGTH_LONG).show());
                    } catch (Exception e) {
                        Log.e(TAG, "Error guardant els canvis", e);
                        runOnUiThread(() -> Snackbar.make(findViewById(android.R.id.content), "ERROR guardant canvis", Snackbar.LENGTH_LONG).show());
                    } finally {
                        connection.close(); // Cerrar la conexión
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error guardant els canvis", e);
                runOnUiThread(() -> Snackbar.make(findViewById(android.R.id.content), "ERROR guardant canvis", Snackbar.LENGTH_LONG).show());
            }
        }).start();

    }

    /**
     * Elimina l'usuari seleccionat del sistema.
     */
    private void deleteUser() {
        new Thread(() -> {
            ServerConnectionHelper connection = new ServerConnectionHelper();
            try {
                connection.connect(); // Establecer la conexión
                connection.sendEncryptedCommand("DELETE_USER"); // Enviar comando al servidor

                // Enviar el ID del usuario a eliminar
                connection.sendEncryptedInt(selectedUser.getId());

                // Como el servidor no envía una respuesta para DELETE_USER, asumimos éxito
                runOnUiThread(() -> {
                    Snackbar.make(findViewById(android.R.id.content), "Usuari eliminat correctament", Snackbar.LENGTH_SHORT).show();
                    finish(); // Opcional: cerrar la actividad después de eliminar
                });
            } catch (Exception e) {
                Log.e(TAG, "Error eliminant l'usuari", e);
                runOnUiThread(() -> Toast.makeText(UserDetailActivity.this, "Error eliminant l'usuari: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            } finally {
                connection.close(); // Cerrar la conexión
            }
        }).start();
    }

}
