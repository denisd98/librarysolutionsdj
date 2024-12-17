package com.example.librarysolutionsdj.Loans;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.librarysolutionsdj.Media.MediaAdapter;
import com.example.librarysolutionsdj.R;
import com.example.librarysolutionsdj.ServerConnection.ServerConnectionHelper;

import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import app.model.Media;

public class BibliotecaActivity extends AppCompatActivity {

    private EditText searchEditText;
    private ListView mediaListView;
    private ImageButton backButton2;

    private ArrayList<Media> allMediaList = new ArrayList<>();
    private ArrayList<Media> filteredMediaList = new ArrayList<>();
    private MediaAdapter mediaAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_biblioteca);

        searchEditText = findViewById(R.id.search_edit_text);
        mediaListView = findViewById(R.id.media_list_view);
        backButton2 = findViewById(R.id.back_button2);

        // Botón atrás para cerrar la actividad
        backButton2.setOnClickListener(v -> finish());

        mediaAdapter = new MediaAdapter(this, filteredMediaList);
        mediaListView.setAdapter(mediaAdapter);

        // Aquí colocas el onItemClickListener
        mediaListView.setOnItemClickListener((parent, view, position, id) -> {
            Media selectedMedia = filteredMediaList.get(position);

            // Abrir la nueva actividad de detalles (MediaReservaDetailActivity)
            Intent intent = new Intent(BibliotecaActivity.this, MediaReservaDetailActivity.class);
            intent.putExtra("selectedMedia", selectedMedia);
            startActivity(intent);
        });

        // Cargar la lista completa de Media desde el servidor
        loadMedia();

        // Filtrar en tiempo real según el texto ingresado
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterMedia(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    private void loadMedia() {
        new Thread(() -> {
            ServerConnectionHelper connection = new ServerConnectionHelper();
            try {
                // Establecer la conexión
                connection.connect();

                // Enviar el comando al servidor
                connection.sendCommand("GET_ALL_MEDIA");

                // Recibir la lista de Media desde el servidor
                @SuppressWarnings("unchecked")
                ArrayList<Media> mediaList = connection.receiveObject();

                // Actualizar la lista y la interfaz de usuario
                runOnUiThread(() -> {
                    filteredMediaList.clear();
                    filteredMediaList.addAll(mediaList);
                    mediaAdapter.notifyDataSetChanged();
                });
            } catch (Exception e) {
                // Manejar errores
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(BibliotecaActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
            } finally {
                // Cerrar la conexión
                connection.close();
            }
        }).start();
    }


    private void filterMedia(String query) {
        ArrayList<Media> temp = new ArrayList<>();
        for (Media media : allMediaList) {
            if (media.getTitle().toLowerCase().contains(query.toLowerCase())) {
                temp.add(media);
            }
        }
        filteredMediaList.clear();
        filteredMediaList.addAll(temp);
        mediaAdapter.notifyDataSetChanged();
    }
}