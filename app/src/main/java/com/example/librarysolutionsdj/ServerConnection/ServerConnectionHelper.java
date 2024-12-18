package com.example.librarysolutionsdj.ServerConnection;

import app.crypto.CryptoUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerConnectionHelper {

    private static final String SERVER_HOST = "10.0.2.2";
    private static final int SERVER_PORT = 12345;
    private static final String PASSWORD = CryptoUtils.getGenericPassword();

    private Socket socket;
    private PrintWriter out;
    private ObjectInputStream in;
    private ObjectOutputStream objectOut;

    /**
     * Establece la conexión con el servidor (crea el socket).
     * @throws IOException si falla la conexión
     */
    public void connect() throws IOException {
        socket = new Socket(SERVER_HOST, SERVER_PORT);
    }

    /**
     * Envía un comando al servidor. Debe llamarse después de connect().
     * @param command El comando a enviar.
     * @throws IOException si hay problemas al escribir en el socket.
     */
    public void sendCommand(String command) throws IOException {
        if (socket == null) {
            throw new IllegalStateException("Debes llamar a connect() antes de sendCommand()");
        }
        if (out == null) {
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
        }
        out.println(command);
        out.flush();
    }

    /**
     * Lee un objeto devuelto por el servidor. Debe llamarse después de sendCommand().
     * @param <T> Tipo esperado del objeto.
     * @return Objeto leído del servidor.
     * @throws IOException si hay problemas en la lectura.
     * @throws ClassNotFoundException si la clase del objeto no puede encontrarse.
     */
    @SuppressWarnings("unchecked")
    public <T> T receiveObject() throws IOException, ClassNotFoundException {
        if (socket == null) {
            throw new IllegalStateException("Debes haber llamado a connect() antes de receiveObject()");
        }
        if (in == null) {
            in = new ObjectInputStream(socket.getInputStream());
        }
        return (T) in.readObject();
    }

    public void sendObject(Object obj) throws Exception {
        if (objectOut == null) {
            objectOut = new ObjectOutputStream(socket.getOutputStream());
        }
        objectOut.writeObject(obj);
        objectOut.flush();
    }

    public void sendInt(int value) throws Exception {
        if (objectOut == null) {
            objectOut = new ObjectOutputStream(socket.getOutputStream());
        }
        objectOut.writeInt(value);
        objectOut.flush();
    }

    public InputStream getInputStream() throws Exception {
        if (socket == null || socket.isClosed()) {
            throw new IllegalStateException("Debes llamar a connect() antes de getInputStream()");
        }
        return socket.getInputStream();
    }

    // Métodos encriptados usando CryptoUtils

    /**
     * Envía un comando cifrado al servidor.
     * @param command El comando a enviar.
     * @throws IOException si hay problemas al escribir en el socket.
     */
    public void sendEncryptedCommand(String command) throws IOException {
        if (socket == null) {
            throw new IllegalStateException("Debes llamar a connect() antes de sendEncryptedCommand()");
        }
        CryptoUtils.sendString(socket.getOutputStream(), command, PASSWORD);
    }

    /**
     * Envía un objeto cifrado al servidor.
     * @param obj El objeto a enviar.
     * @throws IOException si hay problemas al escribir en el socket.
     */
    public void sendEncryptedObject(Object obj) throws IOException {
        CryptoUtils.sendObject(socket.getOutputStream(), obj, PASSWORD);
    }

    /**
     * Lee una cadena cifrada del servidor.
     * @return La cadena descifrada.
     * @throws IOException si hay problemas al leer del socket.
     */
    public String receiveEncryptedString() throws IOException {
        if (socket == null) {
            throw new IllegalStateException("Debes llamar a connect() antes de receiveEncryptedString()");
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        return CryptoUtils.readString(reader, PASSWORD);

    }

    /**
     * Lee un objeto cifrado del servidor.
     * @return El objeto descifrado.
     * @throws IOException si hay problemas al leer del socket.
     */
    public Object receiveEncryptedObject() throws IOException {
        return CryptoUtils.readObject(socket.getInputStream(), PASSWORD);
    }

    /**
     * Envía un entero cifrado al servidor.
     * @param value El valor entero a enviar.
     * @throws IOException si hay problemas al escribir en el socket.
     */
    public void sendEncryptedInt(int value) throws IOException {
        CryptoUtils.sendInt(socket.getOutputStream(), value, PASSWORD);
    }

    /**
     * Lee un entero cifrado del servidor.
     * @return El valor entero descifrado.
     * @throws IOException si hay problemas al leer del socket.
     */
    public int receiveEncryptedInt() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        return CryptoUtils.readInt(reader, PASSWORD);
    }

    /**
     * Cierra la conexión y flujos.
     */
    public void close() {
        // Cerrar InputStream
        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace(); // Registra el error si ocurre
            }
        }

        // Cerrar PrintWriter
        if (out != null) {
            out.close(); // PrintWriter no lanza IOException, por lo que no es necesario un catch
        }

        // Cerrar el socket
        if (socket != null && !socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace(); // Registra el error si ocurre
            }
        }
    }

}
