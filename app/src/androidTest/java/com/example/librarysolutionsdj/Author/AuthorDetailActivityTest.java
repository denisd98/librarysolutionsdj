package com.example.librarysolutionsdj.Authors;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.librarysolutionsdj.R;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import app.model.Author;

/**
 * Classe de proves per validar la funcionalitat de l'activitat AuthorDetailActivity.
 * Les proves asseguren que es realitzi correctament la validació i gestió de dades dels autors.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class AuthorDetailActivityTest {

    /**
     * Configuració inicial abans d'executar cada prova.
     * Aquí es pot afegir la inicialització de mocks o dades necessàries.
     */
    @Before
    public void setUp() {
        // Configuració addicional si és necessària, com inicialitzar dades o mocks.
    }

    /**
     * Prova per verificar que no es permet guardar un autor amb camps obligatoris buits.
     * Es comprova que es mostri el missatge d'error adequat.
     */
    @Test
    public void testSaveWithMissingFields() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), AuthorDetailActivity.class);

        // Crear un autor simulat amb dades inicials
        Author fakeAuthor = new Author(1, "Autor Prova", "Cognom1", "Cognom2", "Biografia", "Espanya", 1990);
        intent.putExtra("selectedAuthor", fakeAuthor);

        // Llançar l'activitat amb les dades simulades
        ActivityScenario<AuthorDetailActivity> scenario = ActivityScenario.launch(intent);

        // Buida els camps obligatoris
        onView(withId(R.id.author_name_edit_text)).perform(replaceText(""));
        onView(withId(R.id.nationality_edit_text)).perform(replaceText(""));

        // Intentar guardar l'autor
        onView(withId(R.id.save_button)).perform(click());

        // Verificar que es mostra el missatge d'error
        onView(withText("Nom i Nacionalitat són obligatoris"))
                .check(matches(ViewMatchers.isDisplayed()));
    }

    /**
     * Prova per verificar que no es permet guardar un autor amb un any de naixement invàlid.
     * Es comprova que es mostri el missatge d'error adequat.
     */
    @Test
    public void testSaveWithInvalidYear() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), AuthorDetailActivity.class);

        // Crear un autor simulat amb dades inicials
        Author fakeAuthor = new Author(1, "Autor Prova", "Cognom1", "Cognom2", "Biografia", "Espanya", 1990);
        intent.putExtra("selectedAuthor", fakeAuthor);

        // Llançar l'activitat amb les dades simulades
        ActivityScenario<AuthorDetailActivity> scenario = ActivityScenario.launch(intent);

        // Introdueix un any de naixement invàlid
        onView(withId(R.id.year_birth_edit_text)).perform(replaceText("abcd"));

        // Intentar guardar l'autor
        onView(withId(R.id.save_button)).perform(click());

        // Verificar que es mostra el missatge d'error
        onView(withText("Any de naixement ha de ser un número"))
                .check(matches(ViewMatchers.isDisplayed()));
    }

    /**
     * Prova per verificar que es pot guardar un autor amb tots els camps correctament omplerts.
     * Es comprova que es mostri el missatge de confirmació.
     */
    @Test
    public void testSaveWithValidFields() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), AuthorDetailActivity.class);

        // Crear un autor simulat amb dades inicials
        Author fakeAuthor = new Author(1, "Autor Prova", "Cognom1", "Cognom2", "Biografia", "Espanya", 1990);
        intent.putExtra("selectedAuthor", fakeAuthor);

        // Llançar l'activitat amb les dades simulades
        ActivityScenario<AuthorDetailActivity> scenario = ActivityScenario.launch(intent);

        scenario.onActivity(activity -> {
            // Configurar l'entorn de prova
            activity.setTestEnvironment(true);
        });

        // Omplir els camps amb dades vàlides
        onView(withId(R.id.author_name_edit_text)).perform(replaceText("Autor Actualitzat"));
        onView(withId(R.id.surname1_edit_text)).perform(replaceText("Cognom1 Actualitzat"));
        onView(withId(R.id.surname2_edit_text)).perform(replaceText("Cognom2 Actualitzat"));
        onView(withId(R.id.biography_edit_text)).perform(replaceText("Nova Biografia"));
        onView(withId(R.id.nationality_edit_text)).perform(replaceText("França"));
        onView(withId(R.id.year_birth_edit_text)).perform(replaceText("1985"));

        // Intentar guardar l'autor
        onView(withId(R.id.save_button)).perform(click());

        // Verificar que es mostra el missatge de confirmació
        onView(withText("Canvis aplicats satisfactoriament"))
                .check(matches(ViewMatchers.isDisplayed()));
    }
}
