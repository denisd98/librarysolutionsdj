package com.example.librarysolutionsdj.Authors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.librarysolutionsdj.R;

import app.model.Author;

import java.util.ArrayList;

/**
 * Adaptador per mostrar una llista d'autors en una interfície ListView.
 */
public class AuthorAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Author> authorList;

    /**
     * Constructor de l'adaptador.
     *
     * @param context    El context de l'aplicació.
     * @param authorList La llista d'autors a mostrar.
     */
    public AuthorAdapter(Context context, ArrayList<Author> authorList) {
        this.context = context;
        this.authorList = authorList;
    }

    /**
     * Retorna el nombre d'elements a la llista.
     *
     * @return El nombre total d'autors a la llista.
     */
    @Override
    public int getCount() {
        return authorList.size();
    }

    /**
     * Retorna un autor en una posició específica de la llista.
     *
     * @param position La posició de l'autor a retornar.
     * @return L'objecte {@link Author} corresponent a la posició.
     */
    @Override
    public Object getItem(int position) {
        return authorList.get(position);
    }

    /**
     * Retorna l'identificador d'un element en una posició específica.
     *
     * @param position La posició de l'element.
     * @return L'identificador de l'element (que en aquest cas és la seva posició).
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Proporciona una vista per a cada element de la llista.
     *
     * @param position    La posició de l'element dins la llista.
     * @param convertView La vista reutilitzada (si és possible).
     * @param parent      El grup de vistes al qual pertany aquesta vista.
     * @return La vista configurada per a l'element.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.author_item, parent, false);
        }

        Author author = authorList.get(position);

        TextView authorNameTextView = convertView.findViewById(R.id.author_name_text_view);
        TextView authorNationalityTextView = convertView.findViewById(R.id.author_nationality_text_view);
        TextView authorYearTextView = convertView.findViewById(R.id.author_year_text_view);

        // Configura els valors dels TextViews amb les dades de l'autor
        authorNameTextView.setText(author.getAuthorname());
        authorNationalityTextView.setText(author.getNationality());
        authorYearTextView.setText(String.valueOf(author.getYearbirth()));

        return convertView;
    }
}
