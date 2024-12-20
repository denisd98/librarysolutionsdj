package com.example.librarysolutionsdj.Loans;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.librarysolutionsdj.R;
import com.example.librarysolutionsdj.ServerConnection.ServerConnectionHelper;
import com.example.librarysolutionsdj.SessionManager.SessionManager;
import com.example.librarysolutionsdj.Users.UserService;
import com.example.librarysolutionsdj.Users.UserProfile;

import java.util.ArrayList;

import app.model.Author;
import app.model.Loan;
import app.model.Media;
import app.model.ModelException;
import app.model.User;

/**
 * Actividad que muestra los detalles de una obra y permite al usuario reservarla.
 */
public class MediaReservaDetailActivity extends AppCompatActivity {

    private static final String TAG = "MediaReservaDetail";

    // Elementos de la interfaz de usuario
    private EditText titleEditText, yearPublicationEditText, descriptionEditText, mediaTypeEditText;
    private ListView authorsListView;
    private ArrayAdapter<String> authorsAdapter;
    private Button reservarButton;
    private ImageButton backButton;

    // Objeto Media seleccionado
    private Media selectedMedia;

    // Gestor de sesión para obtener información del usuario actual
    private SessionManager sessionManager;

    // Servicio para manejar operaciones relacionadas con el usuario
    private UserService userService;

    // Indicador de progreso
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_reserva_detail);

        // Inicializar el gestor de sesión
        sessionManager = new SessionManager(this);

        // Inicializar el UserService
        userService = new UserService();

        // Obtener el objeto Media pasado desde la actividad anterior
        selectedMedia = (Media) getIntent().getSerializableExtra("selectedMedia");

        // Vincular elementos de la interfaz de usuario
        titleEditText = findViewById(R.id.title_edit_text);
        yearPublicationEditText = findViewById(R.id.year_publication_edit_text);
        descriptionEditText = findViewById(R.id.description_edit_text);
        mediaTypeEditText = findViewById(R.id.media_type_edit_text);
        authorsListView = findViewById(R.id.authors_list_view);
        reservarButton = findViewById(R.id.reservar_button);
        backButton = findViewById(R.id.back_button5);

        // Configurar el ListView de autores
        authorsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
        authorsListView.setAdapter(authorsAdapter);
        authorsListView.setChoiceMode(ListView.CHOICE_MODE_NONE); // No es necesario seleccionar autores al reservar

        // Llenar los campos con los detalles de la obra seleccionada
        if (selectedMedia != null) {
            populateFieldsWithSelectedMedia();
        } else {
            Toast.makeText(this, "No se ha seleccionado ninguna obra.", Toast.LENGTH_LONG).show();
            finish(); // Cerrar la actividad si no hay obra seleccionada
        }

        // Deshabilitar edición de campos
        titleEditText.setEnabled(false);
        yearPublicationEditText.setEnabled(false);
        descriptionEditText.setEnabled(false);
        mediaTypeEditText.setEnabled(false);

        // Configurar eventos de los botones
        backButton.setOnClickListener(v -> finish());

        reservarButton.setOnClickListener(v -> {
            // Obtener el sessionId y userId del SessionManager
            String sessionId = sessionManager.getSessionId();
            int userId = sessionManager.getUserId();

            if (sessionId == null || userId == -1) {
                Toast.makeText(this, "Usuario no autenticado.", Toast.LENGTH_LONG).show();
                return;
            }

            // Mostrar el ProgressDialog
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Procesando reserva...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            // Obtener el perfil del usuario actual
            userService.getUserProfile(sessionId, new UserService.UserProfileCallback() {
                @Override
                public void onSuccess(UserProfile profile) {
                    // Crear el objeto User basado en el perfil recibido y el userId de SessionManager
                    User currentUser;
                    try {
                        currentUser = new User(
                                userId,
                                profile.getUsername(),
                                profile.getPassword(), // La contraseña ya está encriptada
                                profile.getAlias(),    // Alias mapeado a realname
                                profile.getSurname1(),
                                profile.getSurname2(),
                                User.stringToUserType(profile.getUserType())
                        );
                    } catch (ModelException e) {
                        runOnUiThread(() -> {
                            progressDialog.dismiss();
                            Toast.makeText(MediaReservaDetailActivity.this, "Error en el tipo de usuario: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        });
                        return;
                    }

                    // Crear el objeto Loan sin fechas (serán asignadas por el servidor)
                    Loan loan = new Loan(selectedMedia, currentUser);

                    // Enviar la solicitud de reserva al servidor en un hilo separado
                    new Thread(() -> {
                        ServerConnectionHelper connection = new ServerConnectionHelper();
                        try {
                            // Establecer conexión con el servidor
                            connection.connect();
                            Log.d(TAG, "Conexión establecida con el servidor.");

                            // Enviar el comando ADD_LOAN cifrado
                            connection.sendEncryptedCommand("ADD_LOAN");
                            Log.d(TAG, "Comando 'ADD_LOAN' enviado.");

                            // Enviar el objeto Loan cifrado
                            connection.sendEncryptedObject(loan);
                            Log.d(TAG, "Objeto Loan enviado al servidor.");

                            // No intentar leer una respuesta del servidor
                            // Asumir que la operación fue exitosa si no hubo excepciones

                            runOnUiThread(() -> {
                                // Despachar el ProgressDialog
                                if (progressDialog.isShowing()) {
                                    progressDialog.dismiss();
                                }

                                // Mostrar mensaje de éxito
                                Toast.makeText(MediaReservaDetailActivity.this, "Reserva enviada correctamente.", Toast.LENGTH_LONG).show();
                                // Opcional: cerrar la actividad o actualizar la UI
                                finish();
                            });

                        } catch (Exception e) {
                            Log.e(TAG, "Error al realizar la reserva: ", e);
                            runOnUiThread(() -> {
                                if (progressDialog.isShowing()) {
                                    progressDialog.dismiss();
                                }
                                Toast.makeText(MediaReservaDetailActivity.this, "Error al realizar la reserva: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            });
                        } finally {
                            // Cerrar la conexión
                            connection.close();
                            Log.d(TAG, "Conexión cerrada.");
                        }
                    }).start();
                }

                @Override
                public void onError(String errorMessage) {
                    runOnUiThread(() -> {
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        Toast.makeText(MediaReservaDetailActivity.this, "Error al obtener el perfil del usuario: " + errorMessage, Toast.LENGTH_LONG).show();
                    });
                }
            });
        });
    }

    /**
     * Llena los campos de la interfaz con los detalles de la obra seleccionada.
     */
    private void populateFieldsWithSelectedMedia() {
        titleEditText.setText(selectedMedia.getTitle());
        yearPublicationEditText.setText(String.valueOf(selectedMedia.getYearPublication()));
        descriptionEditText.setText(selectedMedia.getMedia_description());
        mediaTypeEditText.setText(selectedMedia.getMediaTypeAsString());

        // Mostrar lista de autores
        authorsAdapter.clear();
        if (selectedMedia.getAuthors() != null && !selectedMedia.getAuthors().isEmpty()) {
            for (Author author : selectedMedia.getAuthors()) {
                authorsAdapter.add(author.getAuthorname() + " " + author.getSurname1());
            }
        } else {
            authorsAdapter.add("No hay autores asignados");
        }
        authorsAdapter.notifyDataSetChanged();
    }
}
