package com.example.librarysolutionsdj.Users;

import com.example.librarysolutionsdj.ServerConnection.ServerConnectionHelper;

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
            ServerConnectionHelper connection = new ServerConnectionHelper();
            try {
                // Establecer conexión con el servidor
                connection.connect();

                // Enviar comando cifrado y sesión ID
                connection.sendEncryptedCommand("GET_PROFILE");
                connection.sendEncryptedCommand(sessionId);

                // Leer los datos cifrados del perfil de usuario
                String userAlias = connection.receiveEncryptedString();
                String username = connection.receiveEncryptedString();
                String surname1 = connection.receiveEncryptedString();
                String surname2 = connection.receiveEncryptedString();
                String userType = connection.receiveEncryptedString();
                String password = connection.receiveEncryptedString(); // Si se incluye la contraseña

                if (userAlias != null && username != null) {
                    UserProfile profile = new UserProfile(userAlias, username, surname1, surname2, userType, password);
                    callback.onSuccess(profile);
                } else {
                    callback.onError("Perfil no encontrado.");
                }
            } catch (Exception e) {
                callback.onError("Error obteniendo el perfil: " + e.getMessage());
            } finally {
                // Cerrar conexión
                connection.close();
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
            ServerConnectionHelper connection = new ServerConnectionHelper();
            try {
                // Establecer la conexión con el servidor
                connection.connect();

                // Enviar comando cifrado y sesión ID
                connection.sendEncryptedCommand("LOGOUT");
                connection.sendEncryptedCommand(sessionId);

                // Leer la respuesta cifrada del servidor
                String response = connection.receiveEncryptedString();
                if ("LOGOUT_OK".equals(response)) {
                    callback.onLogoutSuccess();
                } else {
                    callback.onLogoutError("Error en tancar la sessió");
                }
            } catch (Exception e) {
                callback.onLogoutError("Error en tancar la sessió: " + e.getMessage());
            } finally {
                // Cerrar la conexión
                connection.close();
            }
        }).start();
    }

}
