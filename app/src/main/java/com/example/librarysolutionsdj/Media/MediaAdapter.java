package com.example.librarysolutionsdj.Media;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.librarysolutionsdj.R;

import app.model.Media;

import java.util.ArrayList;

/**
 * Adaptador per mostrar una llista d'obres (Media) en un ListView.
 * Aquesta classe permet adaptar una llista d'objectes de tipus Media perquè es puguin visualitzar
 * en un format personalitzat dins d'una vista ListView.
 */
public class MediaAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Media> mediaList;

    /**
     * Constructor de la classe MediaAdapter.
     *
     * @param context   El context de l'activitat o fragment on es mostra el ListView.
     * @param mediaList La llista d'obres que es vol mostrar.
     */
    public MediaAdapter(Context context, ArrayList<Media> mediaList) {
        this.context = context;
        this.mediaList = mediaList;
    }

    /**
     * Retorna el nombre d'ítems en la llista d'obres.
     *
     * @return El nombre total d'obres en la llista.
     */
    @Override
    public int getCount() {
        return mediaList.size();
    }

    /**
     * Retorna l'objecte Media en una posició específica.
     *
     * @param position La posició de l'ítem a la llista.
     * @return L'objecte Media en la posició indicada.
     */
    @Override
    public Object getItem(int position) {
        return mediaList.get(position);
    }

    /**
     * Retorna l'ID de l'ítem en una posició específica.
     *
     * @param position La posició de l'ítem a la llista.
     * @return L'ID de l'ítem (és la mateixa que la posició en aquest cas).
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Proporciona una vista personalitzada per a cada element del ListView.
     *
     * @param position    La posició de l'ítem a la llista.
     * @param convertView La vista reciclada per a l'ítem (pot ser null).
     * @param parent      El ViewGroup pare que conté la llista.
     * @return La vista personalitzada per a l'ítem.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.media_item, parent, false);
        }

        // Obtenir l'objecte Media a la posició actual
        Media media = mediaList.get(position);

        // Configurar els components visuals de la vista
        TextView titleTextView = convertView.findViewById(R.id.media_title_text_view);
        TextView yearTextView = convertView.findViewById(R.id.media_year_text_view);
        TextView typeTextView = convertView.findViewById(R.id.media_type_text_view);

        // Assignar valors als components visuals
        titleTextView.setText(media.getTitle());
        yearTextView.setText(String.valueOf(media.getYearPublication()));
        typeTextView.setText(media.getMediaType().toString());

        return convertView;
    }
}
