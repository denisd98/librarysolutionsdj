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
 * Adaptador para mostrar una lista de obras en un ListView.
 */
public class MediaAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Media> mediaList;

    public MediaAdapter(Context context, ArrayList<Media> mediaList) {
        this.context = context;
        this.mediaList = mediaList;
    }

    @Override
    public int getCount() {
        return mediaList.size();
    }

    @Override
    public Object getItem(int position) {
        return mediaList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.media_item, parent, false);
        }

        Media media = mediaList.get(position);

        TextView titleTextView = convertView.findViewById(R.id.media_title_text_view);
        TextView yearTextView = convertView.findViewById(R.id.media_year_text_view);
        TextView typeTextView = convertView.findViewById(R.id.media_type_text_view);

        titleTextView.setText(media.getTitle());
        yearTextView.setText(String.valueOf(media.getYearPublication()));
        typeTextView.setText(media.getMediaType().toString());

        return convertView;
    }
}
