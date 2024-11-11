package com.example.librarysolutionsdj;

/**
 * Classe FakeUserService per a proves. Simula el servei de l'usuari retornant perfils d'usuari simulats i
 * simulant la funció de logout sense connexió a un servidor real.
 */
public class FakeUserService extends UserService {

    /**
     * Simula l'obtenció d'un perfil d'usuari en funció de l'identificador de sessió.
     *
     * @param sessionId Identificador de sessió, no utilitzat realment en aquesta simulació.
     * @param callback  Callback que retorna un perfil d'usuari simulat a través de la funció `onSuccess`.
     */
    @Override
    public void getUserProfile(String sessionId, UserProfileCallback callback) {
        // Retorna un perfil d'usuari simulat amb dades fictícies.
        UserProfile fakeProfile = new UserProfile("AliasSimulado", "usuarioPrueba", "Apellido1", "Apellido2", "USER");
        callback.onSuccess(fakeProfile);
    }

    /**
     * Simula la funció de logout d'un usuari.
     *
     * @param sessionId Identificador de sessió de l'usuari (no utilitzat en aquesta simulació).
     * @param callback  Callback que notifica l'èxit de la desconnexió a través de `onLogoutSuccess`.
     */
    @Override
    public void logout(String sessionId, LogoutCallback callback) {
        // Simula que el logout s'ha realitzat correctament.
        callback.onLogoutSuccess();
    }
}
