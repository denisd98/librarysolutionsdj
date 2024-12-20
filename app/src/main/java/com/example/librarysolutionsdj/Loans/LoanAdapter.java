package com.example.librarysolutionsdj.Loans;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.librarysolutionsdj.R;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import app.model.Loan;

public class LoanAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Loan> loansList;
    private LayoutInflater inflater;
    private SimpleDateFormat dateFormat;

    public LoanAdapter(Context context, ArrayList<Loan> loansList) {
        this.context = context;
        this.loansList = loansList;
        this.inflater = LayoutInflater.from(context);
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    }

    @Override
    public int getCount() {
        return loansList.size();
    }

    @Override
    public Object getItem(int position) {
        return loansList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        TextView mediaNameTextView;
        TextView startDateTextView;
        TextView endDateTextView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.loan_item, parent, false);
            holder = new ViewHolder();
            holder.mediaNameTextView = convertView.findViewById(R.id.loan_media_name);
            holder.startDateTextView = convertView.findViewById(R.id.loan_start_date);
            holder.endDateTextView = convertView.findViewById(R.id.loan_end_date);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Obtener el préstamo actual
        Loan loan = loansList.get(position);

        // Asignar valores a los TextViews
        if (loan.getLoanedMedia() != null) {
            holder.mediaNameTextView.setText(loan.getLoanedMedia().getTitle());
        } else {
            holder.mediaNameTextView.setText("Título no disponible");
        }

        // Formatear y asignar fechas
        if (loan.getDateStartLoan() != null) {
            holder.startDateTextView.setText("Inicio: " + dateFormat.format(loan.getDateStartLoan()));
        } else {
            holder.startDateTextView.setText("Inicio: N/A");
        }

        if (loan.getDateEndLoan() != null) {
            holder.endDateTextView.setText("Fin: " + dateFormat.format(loan.getDateEndLoan()));
        } else {
            holder.endDateTextView.setText("Fin: N/A");
        }

        return convertView;
    }
}
