package com.example.librarysolutionsdj;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import app.model.User;

import java.util.ArrayList;

/**
 * Classe adaptador per mostrar una llista d'usuaris en una interfície de tipus {@link ListView}.
 */
public class UserAdapter extends BaseAdapter {

    private Context context;         // Context de l'activitat on es mostra la llista
    private ArrayList<User> userList; // Llista d'usuaris que s'ha de mostrar

    /**
     * Constructor per inicialitzar el context i la llista d'usuaris.
     *
     * @param context el context de l'activitat
     * @param userList la llista d'usuaris a mostrar
     */
    public UserAdapter(Context context, ArrayList<User> userList) {
        this.context = context;
        this.userList = userList;
    }



    /**
     * Retorna el nombre d'elements a la llista.
     *
     * @return el nombre total d'usuaris
     */
    @Override
    public int getCount() {
        return userList.size();
    }

    /**
     * Retorna l'usuari en una posició específica de la llista.
     *
     * @param position la posició de l'usuari a obtenir
     * @return l'usuari en la posició indicada
     */
    @Override
    public Object getItem(int position) {
        return userList.get(position);
    }

    /**
     * Retorna l'ID de l'element en una posició específica.
     *
     * @param position la posició de l'element
     * @return l'ID de l'element a la posició indicada
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Infla i retorna la vista per a cada element de la llista.
     *
     * @param position    la posició de l'element a la llista
     * @param convertView la vista reciclada que es pot reutilitzar
     * @param parent      el grup de vistes pare a la que aquesta vista s'afegirà
     * @return la vista configurada per a l'element de la llista
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Infla la vista si és la primera vegada que es crea
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);
        }

        // Obté l'usuari a la posició actual
        User user = userList.get(position);

        // Inicialitza els TextViews per mostrar la informació de l'usuari
        TextView usernameTextView = convertView.findViewById(R.id.username_text_view);
        TextView realnameTextView = convertView.findViewById(R.id.realname_text_view);
        TextView userTypeTextView = convertView.findViewById(R.id.usertype_text_view);

        // Assigna els valors de l'usuari a cada TextView
        usernameTextView.setText(user.getUsername());
        realnameTextView.setText(user.getRealname() + " " + user.getSurname1() +
                (user.getSurname2() != null && !user.getSurname2().isEmpty() ? " " + user.getSurname2() : ""));
        userTypeTextView.setText(user.getTypeAsString());

        return convertView;
    }
}
