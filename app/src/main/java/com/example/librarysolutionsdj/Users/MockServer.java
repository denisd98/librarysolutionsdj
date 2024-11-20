package com.example.librarysolutionsdj.Users;

/**
 * Classe MockServer que simula respostes del servidor per a proves.
 * Aquesta classe permet emular diferents escenaris de resposta del servidor
 * sense necessitat d'una connexió real.
 */
public class MockServer {

    /**
     * Simula una resposta exitosa per a la modificació d'un usuari.
     *
     * @return Una cadena indicant que l'usuari s'ha modificat correctament.
     */
    public static String simulateModifyUserRequest() {
        return "MODIFY_USER_OK";
    }

    /**
     * Simula una resposta exitosa per a la creació d'un usuari.
     *
     * @return Una cadena indicant que l'usuari s'ha creat correctament.
     */
    public static String simulateCreateUserRequest() {
        return "USER_CREATED";
    }

    /**
     * Simula una resposta d'error per a la modificació d'un usuari.
     *
     * @return Una cadena indicant que hi ha hagut un error en modificar l'usuari.
     */
    public static String simulateModifyUserError() {
        return "MODIFY_USER_ERROR";
    }

    /**
     * Simula una resposta exitosa per a la modificació d'un autor.
     *
     * @return Una cadena indicant que l'autor s'ha modificat correctament.
     */
    public static String simulateModifyAuthorRequest() {
        return "MODIFY_AUTHOR_OK";
    }

    /**
     * Simula una resposta exitosa per a la creació d'un autor.
     *
     * @return Una cadena indicant que l'autor s'ha creat correctament.
     */
    public static String simulateCreateAuthorRequest() {
        return "AUTHOR_CREATED";
    }
}
