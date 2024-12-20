package com.example.librarysolutionsdj.Reservations;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

/**
 * Clase para gestionar las reservas de usuarios de forma local utilizando SharedPreferences.
 */
public class ReservationManager {
    private static final String PREF_NAME = "AppPrefs";
    private static final String KEY_RESERVATIONS_PREFIX = "user_reservations_";

    private SharedPreferences sharedPreferences;

    /**
     * Constructor que inicializa SharedPreferences.
     *
     * @param context Contexto de la aplicación.
     */
    public ReservationManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Obtiene la clave específica para almacenar las reservas de un usuario.
     *
     * @param userId ID del usuario.
     * @return Clave para SharedPreferences.
     */
    private String getReservationKey(int userId) {
        return KEY_RESERVATIONS_PREFIX + userId;
    }

    /**
     * Verifica si el usuario ya tiene una reserva para la obra especificada.
     *
     * @param userId  ID del usuario.
     * @param mediaId ID único de la obra.
     * @return true si ya existe una reserva, false en caso contrario.
     */
    public boolean hasReservation(int userId, String mediaId) {
        Set<String> reservations = sharedPreferences.getStringSet(getReservationKey(userId), new HashSet<>());
        return reservations.contains(mediaId);
    }

    /**
     * Añade una reserva para el usuario.
     *
     * @param userId  ID del usuario.
     * @param mediaId ID único de la obra.
     */
    public void addReservation(int userId, String mediaId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> reservations = new HashSet<>(sharedPreferences.getStringSet(getReservationKey(userId), new HashSet<>()));
        reservations.add(mediaId);
        editor.putStringSet(getReservationKey(userId), reservations);
        editor.apply();
    }

    /**
     * Elimina una reserva para el usuario.
     *
     * @param userId  ID del usuario.
     * @param mediaId ID único de la obra.
     */
    public void removeReservation(int userId, String mediaId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> reservations = new HashSet<>(sharedPreferences.getStringSet(getReservationKey(userId), new HashSet<>()));
        reservations.remove(mediaId);
        editor.putStringSet(getReservationKey(userId), reservations);
        editor.apply();
    }

    /**
     * Obtiene todas las reservas del usuario.
     *
     * @param userId ID del usuario.
     * @return Set de IDs de obras reservadas.
     */
    public Set<String> getReservations(int userId) {
        return sharedPreferences.getStringSet(getReservationKey(userId), new HashSet<>());
    }
}
