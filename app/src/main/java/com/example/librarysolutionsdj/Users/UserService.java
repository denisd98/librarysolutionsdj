package com.example.librarysolutionsdj.Users;

import app.crypto.CryptoUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
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
    private static final String PASSWORD = CryptoUtils.getGenericPassword(); // Contraseña genérica para cifrado

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
            try (Socket socket = new Socket(serverHost, serverPort)) {
                // Enviar comando cifrado
                CryptoUtils.sendString(socket.getOutputStream(), "GET_PROFILE", PASSWORD);
                CryptoUtils.sendString(socket.getOutputStream(), sessionId, PASSWORD);

                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                // Leer respuesta cifrada
                String userAlias = CryptoUtils.readString(reader, PASSWORD);
                String username = CryptoUtils.readString(reader, PASSWORD);
                String surname1 = CryptoUtils.readString(reader, PASSWORD);
                String surname2 = CryptoUtils.readString(reader, PASSWORD);
                String userType = CryptoUtils.readString(reader, PASSWORD);
                String password = CryptoUtils.readString(reader, PASSWORD); // Si se incluye la contraseña en la respuesta

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
            try (Socket socket = new Socket(serverHost, serverPort)) {
                // Enviar comando cifrado
                CryptoUtils.sendString(socket.getOutputStream(), "LOGOUT", PASSWORD);
                CryptoUtils.sendString(socket.getOutputStream(), sessionId, PASSWORD);

                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                // Leer respuesta cifrada
                String response = CryptoUtils.readString(reader, PASSWORD);
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
