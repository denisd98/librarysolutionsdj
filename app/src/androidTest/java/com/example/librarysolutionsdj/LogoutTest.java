package com.example.librarysolutionsdj;

import android.content.Intent;
import android.os.SystemClock;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.assertion.ViewAssertions.matches;

import com.example.librarysolutionsdj.Users.PanellUsuari;

/**
 * Classe de prova per verificar el comportament de la funcionalitat de logout de l'activitat PanellUsuari.
 * Comprova que en fer logout, es redirigeix a LoginActivity.
 */
@RunWith(AndroidJUnit4.class)
public class LogoutTest {

    /**
     * ActivityTestRule per gestionar el cicle de vida de l'activitat PanellUsuari.
     * Aquesta regla s'encarrega d'iniciar i finalitzar l'activitat segons els requeriments de cada prova.
     * El tercer paràmetre (false) indica que l'activitat no es llançarà automàticament, sinó que es farà de manera manual.
     */
    @Rule
    public ActivityTestRule<PanellUsuari> activityRule = new ActivityTestRule<>(PanellUsuari.class, true, false);

    /**
     * Configuració prèvia a cada prova.
     * Es crea una sessió simulada, s'inicialitzen els intents per rastrejar les activitats llançades,
     * i es configura un intent per injectar el servei simulat FakeUserService a PanellUsuari.
     */
    @Before
    public void setUp() {
        // Inicialitza una sessió simulada en SharedPreferences
        TestUtils.iniciarSesionSimulada();

        // Inicialitza Intents per rastrejar les activitats que es llancen
        Intents.init();

        // Configura un Intent per llançar PanellUsuari amb el FakeUserService injectat
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), PanellUsuari.class);
        intent.putExtra("INJECT_FAKE_SERVICE", true);
        activityRule.launchActivity(intent);
    }

    /**
     * Prova que verifica que, en fer logout, l'usuari és redirigit correctament a LoginActivity.
     * Aquesta prova simula el clic al botó de logout i comprova que l'activitat LoginActivity es mostri
     * verificant la presència del botó d'accés.
     */
    @Test
    public void testLogoutRedirectsToLogin() {
        // Simula un clic al botó de logout
        onView(withId(R.id.logout_btn)).perform(click());

        // Pausa per permetre que LoginActivity es carregui completament
        SystemClock.sleep(1000);

        // Comprova que el botó d'accés de LoginActivity està visible, verificant que l'activitat s'ha carregat
        onView(withId(R.id.access)).check(matches(isDisplayed()));
    }

    /**
     * Mètode que s'executa després de cada prova per finalitzar els intents.
     * Aquest mètode allibera els recursos utilitzats per la gravació de intents.
     */
    @After
    public void tearDown() {
        Intents.release();
    }
}
