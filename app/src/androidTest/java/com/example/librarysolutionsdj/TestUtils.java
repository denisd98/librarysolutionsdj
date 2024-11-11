package com.example.librarysolutionsdj;

import android.content.Context;
import androidx.test.core.app.ApplicationProvider;

/**
 * Classe utilitària per a proves, que proporciona mètodes auxiliars per gestionar i simular la sessió d'usuari.
 */
public class TestUtils {

    /**
     * Crea una sessió simulada per a les proves amb un usuari fictici.
     * Aquest mètode guarda les dades de sessió en SharedPreferences utilitzant la classe `SessionManager`.
     *
     * Aquesta sessió simula un usuari de tipus "ADMIN", de manera que les proves es poden executar
     * amb la configuració adequada per aquest tipus d'usuari.
     */
    public static void iniciarSesionSimulada() {
        // Obtenim el context de l'aplicació per accedir a SharedPreferences
        Context context = ApplicationProvider.getApplicationContext();

        // Creem una instància de SessionManager per gestionar la sessió
        SessionManager sessionManager = new SessionManager(context);

        // Guardem una sessió simulada amb un identificador d'usuari fictici, rol d'usuari "ADMIN" i userId fictici
        int fakeUserId = 1234; // ID fictici d'usuari per a les proves
        sessionManager.saveSession("usuarioPrueba", "ADMIN", fakeUserId); // Inclou userId en la sessió simulada
    }
}
