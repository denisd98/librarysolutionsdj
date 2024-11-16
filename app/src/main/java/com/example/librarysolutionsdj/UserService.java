package com.example.librarysolutionsdj;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import app.model.User;

/**
 * Classe per gestionar les operacions d'usuari, incloent la recuperació del perfil i el tancament de sessió.
 * Aquesta classe es connecta amb un servidor remot per obtenir dades d'usuari o executar el logout.
 */
public class UserService {

    private final String serverHost = "10.0.2.2"; // IP de la màquina virtual d'Android Studio
    private final int serverPort = 12345;        // Port del servidor

    /**
     * Interfície de callback per manejar la resposta de l'obtenció del perfil d'usuari.
     */
    public interface UserProfileCallback {
        void onSuccess(UserProfile profile);   // Mètode cridat en cas d'èxit
        void onError(String errorMessage);     // Mètode cridat en cas d'error
    }

    /**
     * Interfície de callback per manejar la resposta de logout de l'usuari.
     */
    public interface LogoutCallback {
        void onLogoutSuccess();               // Mètode cridat en cas de logout correcte
        void onLogoutError(String errorMessage); // Mètode cridat en cas d'error en logout
    }

    /**
     * Interfície de callback per manejar la resposta de la llista d'usuaris.
     */
    public interface UserListCallback {
        void onSuccess(ArrayList<User> userList); // Mètode cridat en cas d'èxit
        void onError(String errorMessage);        // Mètode cridat en cas d'error
    }

    /**
     * Obté el perfil d'usuari enviant el sessionId al servidor i rebent la informació d'usuari.
     *
     * @param sessionId Identificador de sessió de l'usuari
     * @param callback  Callback per processar el resultat de l'operació
     */

    public void getUserProfile(String sessionId, UserProfileCallback callback) {
        new Thread(() -> {
            try (Socket socket = new Socket(serverHost, serverPort);
                 PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                // Solicita el perfil del usuario
                out.println("GET_PROFILE");
                out.println(sessionId);

                // Lee la respuesta del servidor
                String userAlias = in.readLine();
                String username = in.readLine();
                String surname1 = in.readLine();
                String surname2 = in.readLine();
                String userType = in.readLine();
                String password = in.readLine(); // Si se incluye la contraseña en la respuesta

                if (userAlias != null && username != null) {
                    UserProfile profile = new UserProfile(userAlias, username, surname1, surname2, userType, password);
                    callback.onSuccess(profile);
                } else {
                    callback.onError("Perfil no encontrado.");
                }

            } catch (Exception e) {
                callback.onError("Error obteniendo el perfil: " + e.getMessage());
            }
        }).start();
    }



    /**
     * Executa el logout de l'usuari enviant el sessionId al servidor.
     *
     * @param sessionId Identificador de sessió de l'usuari
     * @param callback  Callback per processar el resultat de l'operació de logout
     */
    public void logout(String sessionId, LogoutCallback callback) {
        new Thread(() -> {
            try (Socket socket = new Socket(serverHost, serverPort);
                 PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                // Envia la comanda de logout
                out.println("LOGOUT");
                out.println(sessionId);

                // Llegeix la resposta del servidor
                String response = in.readLine();
                if ("LOGOUT_OK".equals(response)) {
                    callback.onLogoutSuccess();
                } else {
                    callback.onLogoutError("Error en tancar la sessió");
                }

            } catch (Exception e) {
                callback.onLogoutError("Error en tancar la sessió: " + e.getMessage());
            }
        }).start();
    }
}
