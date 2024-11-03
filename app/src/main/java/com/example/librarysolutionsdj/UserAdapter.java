package com.example.librarysolutionsdj;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private List<User> userList;

    public UserAdapter(List<User> userList) {
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
        User user = userList.get(position);
        holder.idTextView.setText(user.getId());
        holder.usuariTextView.setText(user.getUsername());
        holder.nomTextView.setText(user.getRealname());

        // Manejar el clic en el elemento
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), UserDetailActivity.class);
            intent.putExtra("userId", user.getId());
            intent.putExtra("username", user.getUsername());
            intent.putExtra("realname", user.getRealname());
            view.getContext().startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        public TextView idTextView, usuariTextView, nomTextView;

        public UserViewHolder(View itemView) {
            super(itemView);
            idTextView = itemView.findViewById(R.id.user_detail_id);
            usuariTextView = itemView.findViewById(R.id.user_detail_username);
            nomTextView = itemView.findViewById(R.id.user_detail_realname);
        }
    }
}
