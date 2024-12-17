package com.example.librarysolutionsdj.Login;

import android.util.Log;

import com.example.librarysolutionsdj.ServerConnection.ServerConnectionHelper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * LoginService gestiona el procés d'inici de sessió comunicant-se amb un servidor remot.
 * Aquesta classe conté un callback per notificar el resultat de l'inici de sessió (èxit o error).
 */
public class LoginService {

    /**
     * Interfície per gestionar el resultat de l'inici de sessió.
     */
    public interface LoginCallback {
        /**
         * Cridat quan el login té èxit.
         *
         * @param sessionId ID de sessió retornat pel servidor
         * @param userType tipus d'usuari retornat pel servidor
         * @param userId ID de l'usuari retornat pel servidor
         */
        void onLoginSuccess(String sessionId, String userType, int userId);

        /**
         * Cridat quan el login falla.
         *
         * @param errorMessage missatge d'error que indica la raó del fracàs
         */
        void onLoginFailure(String errorMessage);
    }

    /**
     * Realitza l'inici de sessió enviant credencials al servidor.
     *
     * @param username nom d'usuari
     * @param password contrasenya
     * @param callback LoginCallback per notificar el resultat del login
     */
    public void login(String username, String password, LoginCallback callback) {
        new Thread(() -> {
            ServerConnectionHelper connection = new ServerConnectionHelper();
            try {
                // Establecer conexión con el servidor
                connection.connect();

                // Enviar el comando LOGIN
                connection.sendCommand("LOGIN");
                connection.sendCommand(username);
                connection.sendCommand(password);
                Log.d("LOGIN", "Credenciales enviadas");

                // Leer respuesta del servidor
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String response = reader.readLine();

                if ("LOGIN_OK".equals(response)) {
                    String sessionIdLine = reader.readLine();
                    String userTypeLine = reader.readLine();
                    String userIdLine = reader.readLine();

                    // Validar respuesta del servidor
                    if (sessionIdLine != null && userTypeLine != null && userIdLine != null) {
                        String sessionId = sessionIdLine.split(":")[1];
                        String userType = userTypeLine.split(":")[1];
                        int userId = Integer.parseInt(userIdLine.split(":")[1]);

                        // Callback de éxito
                        callback.onLoginSuccess(sessionId, userType, userId);
                    } else {
                        callback.onLoginFailure("Respuesta incompleta del servidor");
                    }
                } else {
                    callback.onLoginFailure("Credenciales incorrectas");
                }
            } catch (Exception e) {
                // Manejar error
                callback.onLoginFailure("ERROR: " + e.getMessage());
            } finally {
                // Cerrar conexión
                connection.close();
            }
        }).start();
    }


}
