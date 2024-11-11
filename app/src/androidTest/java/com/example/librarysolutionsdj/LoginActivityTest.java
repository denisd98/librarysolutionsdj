package com.example.librarysolutionsdj;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.containsString;

import android.content.Context;

/**
 * Classe de test per a LoginActivity, amb proves per verificar els casos d'èxit i error en el login,
 * així com el funcionament del botó de tornada.
 */
@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule = new ActivityScenarioRule<>(LoginActivity.class);

    private Context context;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        Intents.init();
    }

    /**
     * Prova d'èxit en el procés de login.
     * Verifica que, en introduir credencials vàlides, es navegui correctament a PanellUsuari.
     */
    @Test
    public void testLoginSuccess() {
        activityRule.getScenario().onActivity(activity -> {
            // Injecta un login exitós amb un userId fictici
            activity.setLoginService(new FakeLoginService(true, 1234)); // Asigna un userId de prueba
        });

        onView(withId(R.id.user)).perform(replaceText("validUser"));
        onView(withId(R.id.password)).perform(replaceText("validPassword"));
        onView(withId(R.id.access)).perform(click());

        // Verifica que es navega a PanellUsuari després del login exitós
        intended(hasComponent(PanellUsuari.class.getName()));
    }

    /**
     * Prova de fallada en el procés de login.
     * Verifica que, en introduir credencials incorrectes, es mostri un missatge d'error adequat.
     */
    @Test
    public void testLoginFailure() {
        activityRule.getScenario().onActivity(activity -> {
            activity.setLoginService(new FakeLoginService(false)); // Injecta un login incorrecte sense userId
        });

        onView(withId(R.id.user)).perform(replaceText("invalidUser"));
        onView(withId(R.id.password)).perform(replaceText("invalidPassword"));
        onView(withId(R.id.access)).perform(click());

        // Verifica que es mostra el missatge d'error de credencials incorrectes
        onView(withText(containsString("Credenciales incorrectas"))).check(matches(withText("Credenciales incorrectas")));
    }

    /**
     * Prova del botó de tornada.
     * Verifica que, en fer clic al botó de tornada, es navegui correctament a MainActivity.
     */
    @Test
    public void testBackButton() {
        onView(withId(R.id.back)).perform(click());

        intended(hasComponent(MainActivity.class.getName()));
    }
}
