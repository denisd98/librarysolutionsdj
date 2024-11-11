package com.example.librarysolutionsdj;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * La classe SessionManager gestiona la sessió de l'usuari utilitzant SharedPreferences.
 * Emmagatzema i recupera l'identificador de sessió, el tipus d'usuari i l'ID d'usuari.
 */
public class SessionManager {

    private static final String PREF_NAME = "AppPrefs";
    public static final String SESSION_ID_KEY = "SESSION_ID";
    public static final String USER_TYPE_KEY = "USER_TYPE";
    public static final String USER_ID_KEY = "USER_ID"; // Nuevo campo para userId
    private SharedPreferences preferences;

    /**
     * Constructor que inicialitza SharedPreferences per a l'emmagatzematge de dades de sessió.
     *
     * @param context el context de l'aplicació
     */
    public SessionManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Desa la sessió de l'usuari en les preferències.
     *
     * @param sessionId identificador únic de sessió de l'usuari
     * @param userType  tipus d'usuari (per exemple, "ADMIN" o "USER")
     * @param userId identificador únic de l'usuari
     */
    public void saveSession(String sessionId, String userType, int userId) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(SESSION_ID_KEY, sessionId);
        editor.putString(USER_TYPE_KEY, userType);
        editor.putInt(USER_ID_KEY, userId);
        editor.apply();
        Log.d("SessionManager", "Session saved with ID: " + sessionId + ", UserType: " + userType + ", UserId: " + userId);
    }

    /**
     * Obté l'identificador de sessió de l'usuari.
     *
     * @return el sessionId de l'usuari o null si no hi ha cap sessió activa
     */
    public String getSessionId() {
        String sessionId = preferences.getString(SESSION_ID_KEY, null);
        Log.d("SessionManager", "Recovered sessionId: " + sessionId);
        return sessionId;
    }

    /**
     * Obté el tipus d'usuari actual.
     *
     * @return el tipus d'usuari o null si no s'ha establert
     */
    public String getUserType() {
        return preferences.getString(USER_TYPE_KEY, null);
    }

    /**
     * Obté l'identificador de l'usuari actual.
     *
     * @return el userId de l'usuari o -1 si no hi ha cap usuari autenticat
     */
    public int getUserId() {
        int userId = preferences.getInt(USER_ID_KEY, -1);
        Log.d("SessionManager", "Recovered userId: " + userId);
        return userId;
    }

    /**
     * Esborra la sessió de l'usuari, eliminant totes les dades emmagatzemades.
     */
    public void clearSession() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
        Log.d("SessionManager", "Session cleared.");
    }
}
