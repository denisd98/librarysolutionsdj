package com.example.librarysolutionsdj.Users;

import android.content.Intent;
import android.util.Log;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.IdlingResource;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.example.librarysolutionsdj.R;
import com.example.librarysolutionsdj.ToastIdlingResource;
import com.example.librarysolutionsdj.ToastMatcher;
import com.example.librarysolutionsdj.Users.UserCreate;

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
public class UserCreateTest {

    @Rule
    public ActivityTestRule<UserCreate> activityRule = new ActivityTestRule<>(UserCreate.class, true, false);

    @Test
    public void testCreateUserWithValidData() {
        // Lanzar la actividad
        activityRule.launchActivity(new Intent());

        // Simular entrada de datos
        onView(withId(R.id.username_edit_text)).perform(replaceText("testUser"));
        onView(withId(R.id.password_edit_text)).perform(replaceText("password123"));
        onView(withId(R.id.realname_edit_text)).perform(replaceText("John"));
        onView(withId(R.id.surname1_edit_text)).perform(replaceText("Doe"));
        onView(withId(R.id.surname2_edit_text)).perform(replaceText("Smith"));

        // Seleccionar tipo de usuario del Spinner
        onView(withId(R.id.user_type_spinner)).perform(click());
        onView(withText("USER")).perform(click()); // Cambiar a "USER" u otro valor según los datos

        // Click en el botón de creación
        onView(withId(R.id.create_button)).perform(click());

        // Validar que el mensaje de error se muestra
        onView(withText("Usuari creat correctament"))
                .check(matches(isDisplayed())); // No necesitas un Matcher especial para Snackbar
    }

    @Test
    public void testCreateUserWithEmptyFields() throws InterruptedException {
        try {
            // Lanzar la actividad
            activityRule.launchActivity(new Intent());

            // Simular entrada de datos incompleta
            onView(withId(R.id.username_edit_text)).perform(replaceText("")); // Campo vacío
            onView(withId(R.id.password_edit_text)).perform(replaceText(""));
            onView(withId(R.id.realname_edit_text)).perform(replaceText(""));
            onView(withId(R.id.surname1_edit_text)).perform(replaceText(""));
            onView(withId(R.id.surname2_edit_text)).perform(replaceText(""));

            // Click en el botón de creación
            onView(withId(R.id.create_button)).perform(click());

            // Validar que el mensaje de error se muestra
            onView(withText("Si us plau, omple tots els camps requerits."))
                    .check(matches(isDisplayed())); // No necesitas un Matcher especial para Snackbar
        } catch (Exception e) {
            Log.e("TestError", "Error while testing toast visibility: ", e);
            throw e; // Re-throw para que el test falle y muestre el stacktrace
        }
    }


}
