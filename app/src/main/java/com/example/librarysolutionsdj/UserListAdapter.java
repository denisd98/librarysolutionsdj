package com.example.librarysolutionsdj;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserViewHolder> {
    private List<UserList> userList; // Cambiado a UserList

    public UserListAdapter(List<UserList> userList) {
        this.userList = userList;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_list_item, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        UserList user = userList.get(position); // Cambiado a UserList
        holder.idTextView.setText(user.getId());
        holder.usuariTextView.setText(user.getUsername());

        // Manejar el clic en el elemento
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), UserDetailActivity.class);
            intent.putExtra("userId", user.getId());  // Enviar el ID del usuario seleccionado
            intent.putExtra("username", user.getUsername());
            view.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        public TextView idTextView, usuariTextView;

        public UserViewHolder(View itemView) {
            super(itemView);
            idTextView = itemView.findViewById(R.id.user_detail_id);
            usuariTextView = itemView.findViewById(R.id.user_detail_username);
        }
    }
}
