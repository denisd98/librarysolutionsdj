package com.example.librarysolutionsdj.Users;

import android.content.Intent;
import android.util.Log;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.example.librarysolutionsdj.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

/**
 * Classe de proves per a validar la funcionalitat de l'activitat UserCreate.
 * Les proves verifiquen que l'aplicació gestiona correctament la creació d'usuaris, incloent-hi
 * validacions de camps obligatoris i confirmació de la creació.
 */
@RunWith(AndroidJUnit4.class)
public class UserCreateTest {

    /**
     * Rule que gestiona el llançament de l'activitat UserCreate per a cada prova.
     * Aquesta rule assegura que cada test té un escenari consistent.
     */
    @Rule
    public ActivityTestRule<UserCreate> activityRule = new ActivityTestRule<>(UserCreate.class, true, false);

    /**
     * Prova per validar la creació d'un usuari amb tots els camps correctament omplerts.
     * Verifica que es mostra un missatge de confirmació després de completar la creació.
     */
    @Test
    public void testCreateUserWithValidFields() {
        // Llançar l'activitat
        ActivityScenario<UserCreate> scenario = ActivityScenario.launch(UserCreate.class);

        scenario.onActivity(activity -> {
            // Configurar l'entorn de prova
            activity.setTestEnvironment(true);
        });

        // Omplir els camps amb dades vàlides
        onView(withId(R.id.username_edit_text)).perform(replaceText("testuser"));
        onView(withId(R.id.password_edit_text)).perform(replaceText("password123"));
        onView(withId(R.id.realname_edit_text)).perform(replaceText("John"));
        onView(withId(R.id.surname1_edit_text)).perform(replaceText("Doe"));
        onView(withId(R.id.surname2_edit_text)).perform(replaceText("Smith"));

        // Seleccionar el tipus d'usuari
        onView(withId(R.id.user_type_spinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("ADMIN"))).perform(click());

        // Fer clic al botó de crear
        onView(withId(R.id.create_button)).perform(click());

        // Verificar que es mostra el missatge de confirmació
        onView(withText("Usuari creat correctament"))
                .check(matches(ViewMatchers.isDisplayed()));
    }

    /**
     * Prova per verificar que no es permet la creació d'un usuari amb camps obligatoris buits.
     * Es comprova que es mostra un missatge d'error adequat.
     */
    @Test
    public void testCreateUserWithEmptyFields() throws InterruptedException {
        try {
            // Llançar l'activitat
            activityRule.launchActivity(new Intent());

            // Simular entrada de camps buits
            onView(withId(R.id.username_edit_text)).perform(replaceText(""));
            onView(withId(R.id.password_edit_text)).perform(replaceText(""));
            onView(withId(R.id.realname_edit_text)).perform(replaceText(""));
            onView(withId(R.id.surname1_edit_text)).perform(replaceText(""));
            onView(withId(R.id.surname2_edit_text)).perform(replaceText(""));

            // Fer clic al botó de crear
            onView(withId(R.id.create_button)).perform(click());

            // Verificar que es mostra el missatge d'error
            onView(withText("Si us plau, omple tots els camps requerits."))
                    .check(matches(isDisplayed())); // No necessita un Matcher especial per Snackbar
        } catch (Exception e) {
            Log.e("TestError", "Error mentre es provava la visibilitat del Toast: ", e);
            throw e; // Llançar l'excepció per fallar la prova i mostrar el rastre d'errors
        }
    }
}
