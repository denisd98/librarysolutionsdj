package com.example.librarysolutionsdj.Loans;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.librarysolutionsdj.R;
import com.example.librarysolutionsdj.ServerConnection.ServerConnectionHelper;
import com.example.librarysolutionsdj.SessionManager.SessionManager;

import java.util.ArrayList;
import java.util.List;

import app.model.Loan;

public class UserLoansActivity extends AppCompatActivity {

    private static final String TAG = "UserLoansActivity";

    private ListView loansListView;
    private LoanAdapter loanAdapter;
    private ArrayList<Loan> loansList;
    private SessionManager sessionManager;
    private ProgressDialog progressDialog;
    private Button backButton; // Declaración del botón de volver atrás

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_loans);

        // Inicializar componentes
        loansListView = findViewById(R.id.loans_list_view);
        loansList = new ArrayList<>();
        loanAdapter = new LoanAdapter(this, loansList);
        loansListView.setAdapter(loanAdapter);

        // Inicializar SessionManager
        sessionManager = new SessionManager(this);

        // Inicializar el botón de volver atrás
        backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish()); // Finaliza la actividad al hacer clic

        // Obtener y mostrar los préstamos del usuario
        fetchUserLoans();
    }

    /**
     * Método para obtener los préstamos del usuario desde el servidor.
     */
    private void fetchUserLoans() {
        String sessionId = sessionManager.getSessionId();
        int userId = sessionManager.getUserId();

        if (sessionId == null || userId == -1) {
            Toast.makeText(this, "Usuario no autenticado.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Mostrar ProgressDialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Cargando tus préstamos...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        new Thread(() -> {
            ServerConnectionHelper connection = new ServerConnectionHelper();
            try {
                // Establecer conexión con el servidor
                connection.connect();
                Log.d(TAG, "Conexión establecida con el servidor.");

                // Enviar el comando GET_ALL_LOANS cifrado
                connection.sendEncryptedCommand("GET_ALL_LOANS");
                Log.d(TAG, "Comando 'GET_ALL_LOANS' enviado.");

                // Recibir la lista de préstamos cifrada
                List<Loan> allLoans = (List<Loan>) connection.receiveEncryptedObject();

                // Filtrar los préstamos para el usuario actual
                List<Loan> userLoans = new ArrayList<>();
                for (Loan loan : allLoans) {
                    if (loan.getUser() != null && loan.getUser().getId() == userId && !loan.isIsReturned()) {
                        userLoans.add(loan);
                    }
                }

                // Actualizar la lista y el adaptador en el hilo principal
                runOnUiThread(() -> {
                    loansList.clear();
                    loansList.addAll(userLoans);
                    loanAdapter.notifyDataSetChanged();

                    // Despachar el ProgressDialog
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }

                    if (userLoans.isEmpty()) {
                        Toast.makeText(UserLoansActivity.this, "No tienes préstamos activos.", Toast.LENGTH_LONG).show();
                    }
                });

            } catch (Exception e) {
                Log.e(TAG, "Error al obtener los préstamos: ", e);
                runOnUiThread(() -> {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    Toast.makeText(UserLoansActivity.this, "Error al cargar los préstamos: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            } finally {
                // Cerrar la conexión
                connection.close();
                Log.d(TAG, "Conexión cerrada.");
            }
        }).start();
    }
}
