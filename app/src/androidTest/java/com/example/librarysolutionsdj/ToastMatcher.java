package com.example.librarysolutionsdj;
import android.os.IBinder;
import android.view.WindowManager;
import android.view.View;
import androidx.test.espresso.Root;
import android.util.Log;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class ToastMatcher extends TypeSafeMatcher<Root> {

    @Override
    public void describeTo(Description description) {
        description.appendText("is toast");
    }

    @Override
    public boolean matchesSafely(Root root) {
        int type = root.getWindowLayoutParams().get().type;

        // Consideramos los tipos de ventana para Toast
        if (type == WindowManager.LayoutParams.TYPE_TOAST
                || type == WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY) {
            IBinder windowToken = root.getDecorView().getWindowToken();
            IBinder appToken = root.getDecorView().getApplicationWindowToken();
            return windowToken == appToken; // Toast pertenece a la aplicación.
        }
        return false;
    }
}

