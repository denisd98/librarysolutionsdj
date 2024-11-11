package com.example.librarysolutionsdj;

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
            try {
                Socket socket = new Socket("10.0.2.2", 12345);
                PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

                // Enviar credenciales
                out.println("LOGIN");
                out.println(username);
                out.println(password);

                // Leer respuesta
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String response = in.readLine();

                if ("LOGIN_OK".equals(response)) {
                    String sessionIdLine = in.readLine();
                    String userTypeLine = in.readLine();
                    String userIdLine = in.readLine();

                    // Verificar que cada línea no sea null y dividir
                    if (sessionIdLine != null && userTypeLine != null && userIdLine != null) {
                        String sessionId = sessionIdLine.split(":")[1];
                        String userType = userTypeLine.split(":")[1];
                        int userId = Integer.parseInt(userIdLine.split(":")[1]);

                        callback.onLoginSuccess(sessionId, userType, userId);
                    } else {
                        callback.onLoginFailure("Respuesta incompleta del servidor");
                    }
                } else {
                    callback.onLoginFailure("Credenciales incorrectas");
                }

                socket.close();
            } catch (Exception e) {
                callback.onLoginFailure("ERROR: " + e.getMessage());
            }
        }).start();
    }

}
