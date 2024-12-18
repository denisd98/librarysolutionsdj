package com.example.librarysolutionsdj.Login;

import app.crypto.CryptoUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class LoginService {
    private static final String PASSWORD = CryptoUtils.getGenericPassword(); // Contraseña genérica para cifrado

    public interface LoginCallback {
        void onLoginSuccess(String sessionId, String userType, int userId);
        void onLoginFailure(String errorMessage);
    }

    public void login(String username, String password, LoginCallback callback) {
        new Thread(() -> {
            try (Socket socket = new Socket("10.0.2.2", 12345)) {
                // Enviar credenciales cifradas
                CryptoUtils.sendString(socket.getOutputStream(), "LOGIN", PASSWORD);
                System.out.println("Sent LOGIN command.");
                CryptoUtils.sendString(socket.getOutputStream(), username, PASSWORD);
                System.out.println("Sent username: " + username);
                CryptoUtils.sendString(socket.getOutputStream(), password, PASSWORD);
                System.out.println("Sent password: " + password);

                // Crear BufferedReader una vez
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                // Leer respuesta cifrada
                String response = CryptoUtils.readString(reader, PASSWORD);
                System.out.println("Received response: " + response);

                if (response == null || response.isEmpty()) {
                    callback.onLoginFailure("Server returned null or empty response for login.");
                    return;
                }

                if ("LOGIN_OK".equals(response)) {
                    // Procesar las respuestas esperadas
                    String sessionIdLine = CryptoUtils.readString(reader, PASSWORD);
                    System.out.println("Received sessionIdLine: " + sessionIdLine);
                    if (sessionIdLine == null || !sessionIdLine.startsWith("SESSION_ID:")) {
                        callback.onLoginFailure("Invalid session ID response.");
                        return;
                    }

                    String userTypeLine = CryptoUtils.readString(reader, PASSWORD);
                    System.out.println("Received userTypeLine: " + userTypeLine);
                    if (userTypeLine == null || !userTypeLine.startsWith("USER_TYPE:")) {
                        callback.onLoginFailure("Invalid user type response.");
                        return;
                    }

                    String userIdLine = CryptoUtils.readString(reader, PASSWORD);
                    System.out.println("Received userIdLine: " + userIdLine);
                    if (userIdLine == null || !userIdLine.startsWith("USER_ID:")) {
                        callback.onLoginFailure("Invalid user ID response.");
                        return;
                    }

                    // Extraer valores después de los dos puntos
                    String sessionId = sessionIdLine.split(":", 2)[1];
                    String userType = userTypeLine.split(":", 2)[1];
                    int userId = Integer.parseInt(userIdLine.split(":", 2)[1]);

                    callback.onLoginSuccess(sessionId, userType, userId);
                } else {
                    callback.onLoginFailure("Invalid credentials.");
                }

            } catch (IOException e) {
                callback.onLoginFailure("ERROR: " + e.getMessage());
            } finally {
                System.out.println("Socket cerrado después de intentar login.");
            }
        }).start();
    }
}
