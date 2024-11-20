package com.example.librarysolutionsdj.Author;

import android.content.Intent;
import android.util.Log;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.example.librarysolutionsdj.Authors.AuthorCreate;
import com.example.librarysolutionsdj.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

/**
 * Classe de proves per validar la funcionalitat de la pantalla de creació d'autors (AuthorCreate).
 * Les proves asseguren que es mostrin els missatges correctes i que la validació funcioni adequadament.
 */
@RunWith(AndroidJUnit4.class)
public class AuthorCreateTest {

    /**
     * Regla per llançar l'activitat de prova.
     * El tercer paràmetre indica si l'activitat es llança automàticament.
     */
    @Rule
    public ActivityTestRule<AuthorCreate> activityRule = new ActivityTestRule<>(AuthorCreate.class, true, false);

    /**
     * Prova que valida la creació d'un autor amb camps correctes.
     * Es comprova que es mostri un missatge d'èxit quan tots els camps són vàlids.
     */
    @Test
    public void testCreateAuthorWithValidFields() {
        ActivityScenario<AuthorCreate> scenario = ActivityScenario.launch(AuthorCreate.class);

        scenario.onActivity(activity -> {
            // Activar entorn de prova
            activity.setTestEnvironment(true);
        });

        // Omplir els camps amb valors vàlids
        onView(withId(R.id.author_name_edit_text)).perform(replaceText("Author Test"));
        onView(withId(R.id.surname1_edit_text)).perform(replaceText("Surname1"));
        onView(withId(R.id.surname2_edit_text)).perform(replaceText("Surname2"));
        onView(withId(R.id.biography_edit_text)).perform(replaceText("This is a biography."));
        onView(withId(R.id.nationality_edit_text)).perform(replaceText("Spain"));
        onView(withId(R.id.year_birth_edit_text)).perform(replaceText("1980"));

        // Fer clic al botó de creació
        onView(withId(R.id.create_button)).perform(click());

        // Comprovar que es mostri el missatge d'èxit
        onView(withText("Autor creat correctament"))
                .check(matches(ViewMatchers.isDisplayed()));
    }

    /**
     * Prova que valida que no es permet crear un autor amb camps buits.
     * Es comprova que es mostri un missatge d'error adequat.
     */
    @Test
    public void testCreateAuthorWithEmptyFields() {
        // Llançar l'activitat
        activityRule.launchActivity(new Intent());

        // Omplir alguns camps amb valors buits
        onView(withId(R.id.author_name_edit_text)).perform(replaceText("")); // Camp buit
        onView(withId(R.id.nationality_edit_text)).perform(replaceText("")); // Camp buit
        onView(withId(R.id.year_birth_edit_text)).perform(replaceText("")); // Camp buit

        // Fer clic al botó de creació
        onView(withId(R.id.create_button)).perform(click());

        // Comprovar que es mostri el missatge d'error
        onView(withText("Si us plau, omple tots els camps obligatoris."))
                .check(matches(isDisplayed()));
    }

    /**
     * Prova que valida que no es permet crear un autor amb un any de naixement invàlid.
     * Es comprova que es mostri un missatge d'error adequat.
     */
    @Test
    public void testCreateAuthorWithInvalidYear() {
        ActivityScenario<AuthorCreate> scenario = ActivityScenario.launch(AuthorCreate.class);

        scenario.onActivity(activity -> {
            // Activar entorn de prova
            activity.setTestEnvironment(true);
        });

        // Omplir els camps amb un any invàlid
        onView(withId(R.id.author_name_edit_text)).perform(replaceText("Author Test"));
        onView(withId(R.id.nationality_edit_text)).perform(replaceText("Spain"));
        onView(withId(R.id.year_birth_edit_text)).perform(replaceText("abcd")); // Any invàlid

        // Fer clic al botó de creació
        onView(withId(R.id.create_button)).perform(click());

        // Comprovar que es mostri el missatge d'error
        onView(withText("L'any de naixement ha de ser un número."))
                .check(matches(isDisplayed()));
    }
}
