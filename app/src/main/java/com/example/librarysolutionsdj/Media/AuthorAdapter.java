package com.example.librarysolutionsdj.Media;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import app.model.Author;

public class AuthorAdapter extends ArrayAdapter<Author> {

    public AuthorAdapter(Context context, List<Author> authors) {
        super(context, android.R.layout.simple_list_item_multiple_choice, authors);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(android.R.layout.simple_list_item_multiple_choice, parent, false);
        }

        Author author = getItem(position);
        TextView textView = convertView.findViewById(android.R.id.text1);

        if (author != null) {
            textView.setText(author.getFullName());
        }

        return convertView;
    }
}
