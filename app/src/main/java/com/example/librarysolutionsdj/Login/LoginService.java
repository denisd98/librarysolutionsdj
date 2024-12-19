package com.example.librarysolutionsdj.Login;

import app.crypto.CryptoUtils;
import app.crypto.Stream;
import com.example.librarysolutionsdj.ServerConnection.ServerConnectionHelper;

import java.io.IOException;

public class LoginService {

    private static final String PASSWORD = CryptoUtils.getGenericPassword();

    public interface LoginCallback {
        void onLoginSuccess(String sessionId, String userType, int userId);
        void onLoginFailure(String errorMessage);
    }

    public void login(String username, String password, LoginCallback callback) {
        new Thread(() -> {
            ServerConnectionHelper connection = new ServerConnectionHelper();
            try {
                // Establecer conexión
                connection.connect();

                // Enviar credenciales cifradas
                connection.sendEncryptedCommand("LOGIN");
                connection.sendEncryptedCommand(username);
                connection.sendEncryptedCommand(password);

                // Leer respuestas esperadas
                String response = connection.receiveEncryptedString();
                if ("LOGIN_OK".equals(response)) {
                    String sessionIdLine = connection.receiveEncryptedString();
                    String userTypeLine = connection.receiveEncryptedString();
                    String userIdLine = connection.receiveEncryptedString();

                    if (sessionIdLine.startsWith("SESSION_ID:") && userTypeLine.startsWith("USER_TYPE:") && userIdLine.startsWith("USER_ID:")) {
                        String sessionId = sessionIdLine.split(":", 2)[1];
                        String userType = userTypeLine.split(":", 2)[1];
                        int userId = Integer.parseInt(userIdLine.split(":", 2)[1]);

                        callback.onLoginSuccess(sessionId, userType, userId);
                    } else {
                        callback.onLoginFailure("Invalid server response format.");
                    }
                } else {
                    callback.onLoginFailure("Login failed: " + response);
                }
            } catch (IOException e) {
                callback.onLoginFailure("Connection error: " + e.getMessage());
            } finally {
                connection.close();
            }
        }).start();
    }
}
