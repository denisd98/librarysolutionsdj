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
import com.example.librarysolutionsdj.TestUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import app.model.Author;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class AuthorDetailActivityTest {

    @Before
    public void setUp() {
        // Configuración adicional si es necesaria, como inicializar datos o mocks.
    }

    @Test
    public void testSaveWithMissingFields() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), AuthorDetailActivity.class);

        // Crear un autor simulado
        Author fakeAuthor = new Author(1, "Autor Prova", "Cognom1", "Cognom2", "Biografia", "Espanya", 1990);
        intent.putExtra("selectedAuthor", fakeAuthor);

        ActivityScenario<AuthorDetailActivity> scenario = ActivityScenario.launch(intent);

        // Dejar campos obligatorios vacíos
        onView(withId(R.id.author_name_edit_text)).perform(replaceText(""));
        onView(withId(R.id.nationality_edit_text)).perform(replaceText(""));

        // Intentar guardar
        onView(withId(R.id.save_button)).perform(click());

        // Verificar el mensaje del Snackbar
        onView(withText("Nom i Nacionalitat són obligatoris"))
                .check(matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testSaveWithInvalidYear() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), AuthorDetailActivity.class);

        // Crear un autor simulado
        Author fakeAuthor = new Author(1, "Autor Prova", "Cognom1", "Cognom2", "Biografia", "Espanya", 1990);
        intent.putExtra("selectedAuthor", fakeAuthor);

        ActivityScenario<AuthorDetailActivity> scenario = ActivityScenario.launch(intent);

        // Ingresar un año inválido
        onView(withId(R.id.year_birth_edit_text)).perform(replaceText("abcd"));

        // Intentar guardar
        onView(withId(R.id.save_button)).perform(click());

        // Verificar el mensaje del Snackbar
        onView(withText("Any de naixement ha de ser un número"))
                .check(matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testSaveWithValidFields() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), AuthorDetailActivity.class);

        // Crear un autor simulado
        Author fakeAuthor = new Author(1, "Autor Prova", "Cognom1", "Cognom2", "Biografia", "Espanya", 1990);
        intent.putExtra("selectedAuthor", fakeAuthor);

        ActivityScenario<AuthorDetailActivity> scenario = ActivityScenario.launch(intent);

        scenario.onActivity(activity -> {
            // Configurar el entorno de prueba, si es necesario
            activity.setTestEnvironment(true);
        });

        // Rellenar campos válidos
        onView(withId(R.id.author_name_edit_text)).perform(replaceText("Autor Actualitzat"));
        onView(withId(R.id.surname1_edit_text)).perform(replaceText("Cognom1 Actualitzat"));
        onView(withId(R.id.surname2_edit_text)).perform(replaceText("Cognom2 Actualitzat"));
        onView(withId(R.id.biography_edit_text)).perform(replaceText("Nova Biografia"));
        onView(withId(R.id.nationality_edit_text)).perform(replaceText("França"));
        onView(withId(R.id.year_birth_edit_text)).perform(replaceText("1985"));

        // Intentar guardar
        onView(withId(R.id.save_button)).perform(click());

        // Verificar el mensaje del Snackbar
        onView(withText("Canvis aplicats satisfactoriament"))
                .check(matches(ViewMatchers.isDisplayed()));
    }
}
