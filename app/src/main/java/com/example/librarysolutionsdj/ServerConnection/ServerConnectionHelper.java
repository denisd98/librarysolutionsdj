package com.example.librarysolutionsdj.ServerConnection;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerConnectionHelper {

    private static final String SERVER_HOST = "10.0.2.2";
    private static final int SERVER_PORT = 12345;

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
        // Creamos el ObjectOutputStream solo la primera vez que se llame a este método
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


    /**
     * Cierra la conexión y flujos.
     */
    public void close() {
        try {
            if (in != null) in.close();
        } catch (IOException e) { /* Ignorar */}
        if (out != null) out.close();
        try {
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) { /* Ignorar */}
    }
}
