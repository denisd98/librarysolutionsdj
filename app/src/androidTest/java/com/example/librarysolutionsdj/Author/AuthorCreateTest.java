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

@RunWith(AndroidJUnit4.class)
public class AuthorCreateTest {

    @Rule
    public ActivityTestRule<AuthorCreate> activityRule = new ActivityTestRule<>(AuthorCreate.class, true, false);

    @Test
    public void testCreateAuthorWithValidFields() {
        ActivityScenario<AuthorCreate> scenario = ActivityScenario.launch(AuthorCreate.class);

        scenario.onActivity(activity -> {
            // Activar entorno de prueba
            activity.setTestEnvironment(true);
        });

        // Rellenar campos válidos
        onView(withId(R.id.author_name_edit_text)).perform(replaceText("Author Test"));
        onView(withId(R.id.surname1_edit_text)).perform(replaceText("Surname1"));
        onView(withId(R.id.surname2_edit_text)).perform(replaceText("Surname2"));
        onView(withId(R.id.biography_edit_text)).perform(replaceText("This is a biography."));
        onView(withId(R.id.nationality_edit_text)).perform(replaceText("Spain"));
        onView(withId(R.id.year_birth_edit_text)).perform(replaceText("1980"));

        // Click en el botón de creación
        onView(withId(R.id.create_button)).perform(click());

        // Verificar el mensaje de éxito
        onView(withText("Autor creat correctament"))
                .check(matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testCreateAuthorWithEmptyFields() {
        // Lanzar la actividad
        activityRule.launchActivity(new Intent());

        // Simular entrada de datos incompleta
        onView(withId(R.id.author_name_edit_text)).perform(replaceText("")); // Campo vacío
        onView(withId(R.id.nationality_edit_text)).perform(replaceText("")); // Campo vacío
        onView(withId(R.id.year_birth_edit_text)).perform(replaceText("")); // Campo vacío

        // Click en el botón de creación
        onView(withId(R.id.create_button)).perform(click());

        // Verificar que el mensaje de error se muestra
        onView(withText("Si us plau, omple tots els camps obligatoris."))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testCreateAuthorWithInvalidYear() {
        ActivityScenario<AuthorCreate> scenario = ActivityScenario.launch(AuthorCreate.class);

        scenario.onActivity(activity -> {
            // Activar entorno de prueba
            activity.setTestEnvironment(true);
        });

        // Rellenar campos con un año inválido
        onView(withId(R.id.author_name_edit_text)).perform(replaceText("Author Test"));
        onView(withId(R.id.nationality_edit_text)).perform(replaceText("Spain"));
        onView(withId(R.id.year_birth_edit_text)).perform(replaceText("abcd")); // Año inválido

        // Click en el botón de creación
        onView(withId(R.id.create_button)).perform(click());

        // Verificar que el mensaje de error se muestra
        onView(withText("L'any de naixement ha de ser un número."))
                .check(matches(isDisplayed()));
    }
}
