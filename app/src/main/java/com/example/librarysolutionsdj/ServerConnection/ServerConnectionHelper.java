// ServerConnectionHelper adaptado
package com.example.librarysolutionsdj.ServerConnection;

import app.crypto.CryptoUtils;
import app.crypto.Stream;
import app.model.User;

import java.io.IOException;
import java.net.Socket;

public class ServerConnectionHelper {

    private static final String SERVER_HOST = "10.0.2.2";
    private static final int SERVER_PORT = 12345;
    private static final String PASSWORD = CryptoUtils.getGenericPassword();

    private Socket socket;
    private Stream stream;

    /**
     * Establece la conexión con el servidor (crea el socket).
     * @throws IOException si falla la conexión
     */
    public void connect() throws IOException {
        socket = new Socket(SERVER_HOST, SERVER_PORT);
        stream = new Stream(socket);
    }

    /**
     * Envía un comando cifrado al servidor.
     * @param command El comando a enviar.
     * @throws IOException si hay problemas al escribir en el socket.
     */
    public void sendEncryptedCommand(String command) throws IOException {
        CryptoUtils.sendString(stream, command, PASSWORD);
    }

    /**
     * Lee una cadena cifrada del servidor.
     * @return La cadena descifrada.
     * @throws IOException si hay problemas al leer del socket.
     */
    public String receiveEncryptedString() throws IOException {
        return CryptoUtils.readString(stream, PASSWORD);
    }

    /**
     * Lee un objeto cifrado del servidor.
     * @return El objeto descifrado.
     * @throws IOException si hay problemas al leer del socket.
     */
    public Object receiveEncryptedObject() throws IOException {
        return CryptoUtils.readObject(stream, PASSWORD);
    }

    /**
     * Envía un objeto cifrado al servidor.
     * @param obj El objeto a enviar.
     * @throws IOException si hay problemas al escribir en el socket.
     */
    public void sendEncryptedObject(Object obj) throws IOException {
        if (obj instanceof User) {
            User user = (User) obj;
            user.setPassword(CryptoUtils.encryptPassword(user.getPassword()));
        }
        CryptoUtils.sendObject(stream, obj, PASSWORD);
    }

    /**
     * Envía un entero cifrado al servidor.
     * @param value El valor entero a enviar.
     * @throws IOException si hay problemas al escribir en el socket.
     */
    public void sendEncryptedInt(int value) throws IOException {
        CryptoUtils.sendInt(stream, value, PASSWORD);
    }

    /**
     * Lee un entero cifrado del servidor.
     * @return El entero descifrado.
     * @throws IOException si hay problemas al leer del socket.
     */
    public int receiveEncryptedInt() throws IOException {
        return CryptoUtils.readInt(stream, PASSWORD);
    }

    /**
     * Cierra la conexión y flujos.
     */
    public void close() {
        if (stream != null) {
            stream.close();
        }
        if (socket != null && !socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Callback para el logout.
     */
    public interface LogoutCallback {
        void onLogoutSuccess();
        void onLogoutError(String errorMessage);
    }
}
