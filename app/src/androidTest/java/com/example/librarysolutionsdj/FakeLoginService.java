package com.example.librarysolutionsdj;

import com.example.librarysolutionsdj.Login.LoginService;

/**
 * Classe simulada de LoginService per a proves. Permet simular el resultat de l'intent de login (èxit o error)
 * sense necessitat de connectar-se a un servidor real.
 */
public class FakeLoginService extends LoginService {
    private boolean shouldSucceed;
    private int userId;  // Nuevo campo para userId simulado

    /**
     * Constructor per a FakeLoginService.
     *
     * @param shouldSucceed Indica si el login ha de ser exitós (true) o fallar (false).
     */
    public FakeLoginService(boolean shouldSucceed) {
        this.shouldSucceed = shouldSucceed;
        this.userId = 1;  // Puedes asignar un ID ficticio para las pruebas exitosas
    }

    public FakeLoginService(boolean shouldSucceed, int userId) {
        this.shouldSucceed = shouldSucceed;
        this.userId = userId;  // Permite personalizar el userId simulado
    }

    /**
     * Mètode de login simulat.
     *
     * @param username Nom d'usuari proporcionat (no es verifica en aquesta simulació).
     * @param password Contrasenya proporcionada (no es verifica en aquesta simulació).
     * @param callback Callback de resultat del login, on s'informa de l'èxit o el fracàs segons el valor de shouldSucceed.
     */
    @Override
    public void login(String username, String password, LoginCallback callback) {
        if (shouldSucceed) {
            // Simula login exitós amb identificador de sessió fals i un userId simulat
            callback.onLoginSuccess("fakeSessionId", "userType", userId);
        } else {
            // Simula error en el login amb un missatge d'error predeterminat
            callback.onLoginFailure("Credenciales incorrectas");
        }
    }
}
