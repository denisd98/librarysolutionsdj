package com.example.librarysolutionsdj;

/**
 * Classe que representa el perfil complet d'un usuari amb informació rellevant com l'àlies, nom d'usuari,
 * cognoms i tipus d'usuari.
 */
public class UserProfile {

    private String alias;       // Àlies de l'usuari
    private String username;    // Nom d'usuari únic
    private String surname1;    // Primer cognom de l'usuari
    private String surname2;    // Segon cognom de l'usuari (opcional)
    private String userType;    // Tipus d'usuari, com ara "ADMIN" o "USER"

    /**
     * Constructor que inicialitza els camps del perfil d'usuari.
     *
     * @param alias Àlies de l'usuari
     * @param username Nom d'usuari únic
     * @param surname1 Primer cognom de l'usuari
     * @param surname2 Segon cognom de l'usuari (opcional)
     * @param userType Tipus d'usuari
     */
    public UserProfile(String alias, String username, String surname1, String surname2, String userType) {
        this.alias = alias;
        this.username = username;
        this.surname1 = surname1;
        this.surname2 = surname2;
        this.userType = userType;
    }

    /**
     * Obté l'àlies de l'usuari.
     *
     * @return L'àlies de l'usuari
     */
    public String getAlias() { return alias; }

    /**
     * Obté el nom d'usuari.
     *
     * @return El nom d'usuari
     */
    public String getUsername() { return username; }

    /**
     * Obté el primer cognom de l'usuari.
     *
     * @return El primer cognom de l'usuari
     */
    public String getSurname1() { return surname1; }

    /**
     * Obté el segon cognom de l'usuari.
     *
     * @return El segon cognom de l'usuari
     */
    public String getSurname2() { return surname2; }

    /**
     * Obté el tipus d'usuari.
     *
     * @return El tipus d'usuari (per exemple, "ADMIN" o "USER")
     */
    public String getUserType() { return userType; }
}
