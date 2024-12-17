package com.example.librarysolutionsdj.Loans;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.librarysolutionsdj.R;

import java.util.ArrayList;
import java.util.Calendar;

import app.model.Author;
import app.model.Media;
import app.model.MediaType;

public class MediaReservaDetailActivity extends AppCompatActivity {

    private EditText titleEditText, yearPublicationEditText, descriptionEditText;
    private EditText mediaTypeEditText; // Campo no editable
    private ListView authorsListView;
    private ArrayAdapter<String> authorsAdapter;
    private Media selectedMedia;

    // Campos para fechas de reserva
    private EditText startDateEditText, endDateEditText;
    private Button reservarButton;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_reserva_detail);

        selectedMedia = (Media) getIntent().getSerializableExtra("selectedMedia");

        titleEditText = findViewById(R.id.title_edit_text);
        yearPublicationEditText = findViewById(R.id.year_publication_edit_text);
        descriptionEditText = findViewById(R.id.description_edit_text);
        mediaTypeEditText = findViewById(R.id.media_type_edit_text); // Ahora es un EditText no editable
        authorsListView = findViewById(R.id.authors_list_view);
        startDateEditText = findViewById(R.id.start_date_edit_text);
        endDateEditText = findViewById(R.id.end_date_edit_text);
        reservarButton = findViewById(R.id.reservar_button);
        backButton = findViewById(R.id.back_button);

        // Configurar ListView de autores
        authorsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
        authorsListView.setAdapter(authorsAdapter);

        if (selectedMedia != null) {
            populateFieldsWithSelectedMedia();
        }

        // Deshabilitar edición de campos
        titleEditText.setEnabled(false);
        yearPublicationEditText.setEnabled(false);
        descriptionEditText.setEnabled(false);
        mediaTypeEditText.setEnabled(false);

        // Configurar selección de fechas
        startDateEditText.setOnClickListener(v -> showDatePickerDialog(true));
        endDateEditText.setOnClickListener(v -> showDatePickerDialog(false));

        // Configurar eventos de botones
        backButton.setOnClickListener(v -> finish());

        // Botón de reservar
        reservarButton.setOnClickListener(v -> {
            // Aquí podrías enviar la solicitud de reserva al servidor
            // Por ahora, solo mostramos un Toast
            Toast.makeText(this, "Reserva solicitada del " +
                    startDateEditText.getText().toString() + " al " +
                    endDateEditText.getText().toString(), Toast.LENGTH_LONG).show();

        });

    }

    private void populateFieldsWithSelectedMedia() {
        titleEditText.setText(selectedMedia.getTitle());
        yearPublicationEditText.setText(String.valueOf(selectedMedia.getYearPublication()));
        descriptionEditText.setText(selectedMedia.getMedia_description());
        mediaTypeEditText.setText(selectedMedia.getMediaTypeAsString());

        // Mostrar lista de autores
        authorsAdapter.clear();
        if (selectedMedia.getAuthors() != null && !selectedMedia.getAuthors().isEmpty()) {
            for (Author author : selectedMedia.getAuthors()) {
                authorsAdapter.add(author.getAuthorname() + " " + author.getSurname1());
            }
        } else {
            authorsAdapter.add("No hay autores asignados");
        }
        authorsAdapter.notifyDataSetChanged();
    }

    private void showDatePickerDialog(boolean isStartDate) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(this, (DatePicker view, int yearSelected, int monthSelected, int daySelected) -> {
            String date = daySelected + "/" + (monthSelected + 1) + "/" + yearSelected;
            if (isStartDate) {
                startDateEditText.setText(date);
            } else {
                endDateEditText.setText(date);
            }
        }, year, month, day);
        dpd.show();
    }
}
