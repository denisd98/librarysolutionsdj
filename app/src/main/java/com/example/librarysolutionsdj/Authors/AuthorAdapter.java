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
 * Clase adaptador para mostrar una lista de autores en una interfaz ListView.
 */
public class AuthorAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Author> authorList;

    public AuthorAdapter(Context context, ArrayList<Author> authorList) {
        this.context = context;
        this.authorList = authorList;
    }

    @Override
    public int getCount() {
        return authorList.size();
    }

    @Override
    public Object getItem(int position) {
        return authorList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.author_item, parent, false);
        }

        Author author = authorList.get(position);

        TextView authorNameTextView = convertView.findViewById(R.id.author_name_text_view);
        TextView authorNationalityTextView = convertView.findViewById(R.id.author_nationality_text_view);
        TextView authorYearTextView = convertView.findViewById(R.id.author_year_text_view);

        authorNameTextView.setText(author.getAuthorname());
        authorNationalityTextView.setText(author.getNationality());
        authorYearTextView.setText(String.valueOf(author.getYearbirth()));

        return convertView;
    }
}
