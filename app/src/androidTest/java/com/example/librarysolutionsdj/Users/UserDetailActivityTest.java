package com.example.librarysolutionsdj.Users;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
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
import androidx.test.espresso.IdlingPolicies;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import com.example.librarysolutionsdj.R;
import com.example.librarysolutionsdj.TestUtils;
import com.example.librarysolutionsdj.ToastIdlingResource;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import app.model.User;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class UserDetailActivityTest {

    @Before
    public void setUp() {
        // Iniciar sesión simulada para las pruebas
        TestUtils.iniciarSesionSimulada();
    }

    @Test
    public void testSaveWithEmptyFields() {
        // Crear un Intent explícito para lanzar UserDetailActivity
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), UserDetailActivity.class);

        // Lanzar la actividad con un usuario simulado
        User fakeUser = new User(1234, "admin", "password", "Admin", "Test", "User", "ADMIN");
        intent.putExtra("selectedUser", fakeUser);
        ActivityScenario<UserDetailActivity> scenario = ActivityScenario.launch(intent);

        // Dejar el campo de contraseña vacío y hacer clic en guardar
        onView(withId(R.id.password_edit_text)).perform(replaceText(""));
        onView(withId(R.id.save_button)).perform(click());

        // Verificar que aparece el mensaje de error del Toast
        onView(withText("La contraseña no puede estar vacía"))
                .check(matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testSaveWithValidFields() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), UserDetailActivity.class);

        User fakeUser = new User(1234, "UserProva", "Contrasenya", "NomProva", "Cognom1Prova", "Cognom2Prova", "ADMIN");
        intent.putExtra("selectedUser", fakeUser);

        ActivityScenario<UserDetailActivity> scenario = ActivityScenario.launch(intent);

        scenario.onActivity(activity -> {
            // Activar el entorno de prueba
            activity.setTestEnvironment(true);
        });

        onView(withId(R.id.username_edit_text)).perform(replaceText("UserProva"));
        onView(withId(R.id.realname_edit_text)).perform(replaceText("NomProva"));
        onView(withId(R.id.surname1_edit_text)).perform(replaceText("Cognom1Prova"));
        onView(withId(R.id.surname2_edit_text)).perform(replaceText("Cognom2Prova"));
        onView(withId(R.id.password_edit_text)).perform(replaceText("Contrasenya"));

        onView(withId(R.id.user_type_spinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("ADMIN"))).perform(click());

        onView(withId(R.id.save_button)).perform(click());

        onView(withText("Canvis aplicats satisfactoriament"))
                .check(matches(ViewMatchers.isDisplayed()));
    }

}
