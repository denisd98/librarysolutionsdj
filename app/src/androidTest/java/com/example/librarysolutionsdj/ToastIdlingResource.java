package com.example.librarysolutionsdj;

import androidx.test.espresso.IdlingResource;

public class ToastIdlingResource implements IdlingResource {

    private ResourceCallback resourceCallback;
    private boolean isIdle = false;

    public ToastIdlingResource() {
        new Thread(() -> {
            try {
                Thread.sleep(2000); // Espera el tiempo estimado del Toast
                isIdle = true;
                if (resourceCallback != null) {
                    resourceCallback.onTransitionToIdle();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public String getName() {
        return ToastIdlingResource.class.getName();
    }

    @Override
    public boolean isIdleNow() {
        return isIdle;
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback) {
        this.resourceCallback = callback;
    }
}
