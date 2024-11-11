package com.example.librarysolutionsdj;

import java.io.Serializable;

/**
 * Classe que representa una llista simplificada d'usuaris per a la seva visualització.
 * Implementa la interfície Serializable per permetre la transmissió d'objectes de tipus UserList.
 */
public class UserList implements Serializable {

    private int id; // Identificador únic de l'usuari
    private String username; // Nom d'usuari

    /**
     * Constructor per inicialitzar els camps id i username.
     *
     * @param id Identificador únic de l'usuari, consistent amb el servidor
     * @param username Nom d'usuari
     */
    public UserList(int id, String username) {
        this.id = id;
        this.username = username;
    }

    /**
     * Obté l'identificador únic de l'usuari.
     *
     * @return l'ID de l'usuari
     */
    public int getId() {
        return id;
    }

    /**
     * Obté el nom d'usuari.
     *
     * @return el nom d'usuari
     */
    public String getUsername() {
        return username;
    }
}
