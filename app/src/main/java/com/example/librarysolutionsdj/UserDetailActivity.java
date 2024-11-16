// UserDetailActivity.java
package com.example.librarysolutionsdj;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import app.model.User;

public class UserDetailActivity extends AppCompatActivity {

    private EditText usernameEditText, realnameEditText, surname1EditText, surname2EditText, passwordEditText;
    private Spinner userTypeSpinner;
    private Button saveButton, deleteButton;
    private ImageButton backButton;
    private User selectedUser;
    private SessionManager sessionManager;
    private UserService userService;
    private boolean isOwnProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

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
        backButton = findViewById(R.id.back_button);
        deleteButton = findViewById(R.id.delete_button);

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

    private void loadCurrentUserProfile() {
        String sessionId = sessionManager.getSessionId();
        userService.getUserProfile(sessionId, new UserService.UserProfileCallback() {
            @Override
            public void onSuccess(UserProfile profile) {
                // Incluye la contraseña en el objeto seleccionado
                selectedUser = new User(sessionManager.getUserId(), profile.getAlias(), profile.getPassword(),
                        profile.getUsername(), profile.getSurname1(), profile.getSurname2(), profile.getUserType());
                runOnUiThread(() -> populateFields());
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    Log.e("UserDetailActivity", "Error loading user profile: " + errorMessage);
                    Toast.makeText(UserDetailActivity.this, "Error loading user profile", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        });
    }



    private void populateFieldsWithSelectedUser() {
        populateFields();
    }

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

    private void saveChanges() {
        if (passwordEditText.getText().toString().isEmpty()) {
            Toast.makeText(this, "La contraseña no puede estar vacía", Toast.LENGTH_SHORT).show();
            return;
        }

        // Actualizar el objeto usuario con los nuevos datos
        selectedUser.setUsername(usernameEditText.getText().toString());
        selectedUser.setRealname(realnameEditText.getText().toString());
        selectedUser.setSurname1(surname1EditText.getText().toString());
        selectedUser.setSurname2(surname2EditText.getText().toString());
        selectedUser.setPassword(passwordEditText.getText().toString());
        selectedUser.setType(User.stringToUserType(userTypeSpinner.getSelectedItem().toString()));

        new Thread(() -> {
            try (Socket socket = new Socket("10.0.2.2", 12345);
                 PrintWriter commandOut = new PrintWriter(socket.getOutputStream(), true)) {

                commandOut.println("MODIFY_USER");
                commandOut.flush();

                ObjectOutputStream objectOut = new ObjectOutputStream(socket.getOutputStream());
                objectOut.writeInt(selectedUser.getId());
                objectOut.writeObject(selectedUser);
                objectOut.flush();

                BufferedReader responseReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String response = responseReader.readLine();

                if ("MODIFY_USER_OK".equals(response)) {
                    runOnUiThread(() -> {
                        Toast.makeText(UserDetailActivity.this, "Changes saved successfully", Toast.LENGTH_SHORT).show();
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("updatedUser", selectedUser);
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(UserDetailActivity.this, "Error saving changes", Toast.LENGTH_SHORT).show());
                }

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(UserDetailActivity.this, "Error saving changes: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();

    }

    private void deleteUser() {
        new Thread(() -> {
            try (Socket socket = new Socket("10.0.2.2", 12345);
                 PrintWriter commandOut = new PrintWriter(socket.getOutputStream(), true)) {

                commandOut.println("DELETE_USER");
                ObjectOutputStream objectOut = new ObjectOutputStream(socket.getOutputStream());
                objectOut.writeInt(selectedUser.getId());
                objectOut.flush();

                runOnUiThread(() -> {
                    Toast.makeText(UserDetailActivity.this, "User deleted successfully", Toast.LENGTH_SHORT).show();
                    finish();
                });

            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(UserDetailActivity.this, "Error deleting user: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}
