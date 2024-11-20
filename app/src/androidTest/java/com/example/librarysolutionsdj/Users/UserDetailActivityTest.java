package com.example.librarysolutionsdj.Users;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static androidx.test.espresso.Espresso.onData;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.librarysolutionsdj.R;
import com.example.librarysolutionsdj.TestUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import app.model.User;

/**
 * Classe de proves per a l'activitat UserDetailActivity.
 * Aquesta classe valida el comportament de la interfície d'usuari i les funcionalitats associades,
 * com la modificació d'usuaris i la gestió d'errors en cas de camps buits.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class UserDetailActivityTest {

    /**
     * Configuració inicial abans de cada prova.
     * Simula una sessió d'usuari activa per assegurar que l'activitat es pot llançar amb un usuari seleccionat.
     */
    @Before
    public void setUp() {
        // Iniciar sessió simulada per a les proves
        TestUtils.iniciarSesionSimulada();
    }

    /**
     * Prova que valida el comportament quan el camp de la contrasenya està buit.
     * Es comprova que l'aplicació mostra un missatge d'error adequat.
     */
    @Test
    public void testSaveWithEmptyFields() {
        // Crear un Intent explícit per llançar UserDetailActivity amb un usuari simulat
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), UserDetailActivity.class);

        // Usuari fictici per la prova
        User fakeUser = new User(1234, "admin", "password", "Admin", "Test", "User", "ADMIN");
        intent.putExtra("selectedUser", fakeUser);

        // Llançar l'activitat
        ActivityScenario<UserDetailActivity> scenario = ActivityScenario.launch(intent);

        // Simular que el camp de contrasenya està buit i fer clic a guardar
        onView(withId(R.id.password_edit_text)).perform(replaceText(""));
        onView(withId(R.id.save_button)).perform(click());

        // Verificar que apareix el missatge d'error
        onView(withText("La contrasenya no pot estar buida"))
                .check(matches(ViewMatchers.isDisplayed()));
    }

    /**
     * Prova que verifica la modificació d'un usuari amb camps vàlids.
     * Es comprova que els canvis s'apliquen correctament i que es mostra un missatge de confirmació.
     */
    @Test
    public void testSaveWithValidFields() {
        // Crear un Intent explícit per llançar UserDetailActivity amb un usuari simulat
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), UserDetailActivity.class);

        // Usuari fictici per la prova
        User fakeUser = new User(1234, "UserProva", "Contrasenya", "NomProva", "Cognom1Prova", "Cognom2Prova", "ADMIN");
        intent.putExtra("selectedUser", fakeUser);

        // Llançar l'activitat
        ActivityScenario<UserDetailActivity> scenario = ActivityScenario.launch(intent);

        // Configurar l'entorn de prova
        scenario.onActivity(activity -> activity.setTestEnvironment(true));

        // Omplir els camps amb dades vàlides
        onView(withId(R.id.username_edit_text)).perform(replaceText("UserProva"));
        onView(withId(R.id.realname_edit_text)).perform(replaceText("NomProva"));
        onView(withId(R.id.surname1_edit_text)).perform(replaceText("Cognom1Prova"));
        onView(withId(R.id.surname2_edit_text)).perform(replaceText("Cognom2Prova"));
        onView(withId(R.id.password_edit_text)).perform(replaceText("Contrasenya"));

        // Seleccionar el tipus d'usuari
        onView(withId(R.id.user_type_spinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("ADMIN"))).perform(click());

        // Fer clic a guardar
        onView(withId(R.id.save_button)).perform(click());

        // Verificar que es mostra el missatge de confirmació
        onView(withText("Canvis aplicats satisfactòriament"))
                .check(matches(ViewMatchers.isDisplayed()));
    }
}
