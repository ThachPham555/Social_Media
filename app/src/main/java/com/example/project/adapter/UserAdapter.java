package com.example.project.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.project.R;
import com.example.project.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder>{
    private List<User> list;
    private ItemListener1 itemListener;

    public UserAdapter() {
        list = new ArrayList<>();
    }

    public void setList(List<User> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public void setItemListener(ItemListener1 itemListener) {
        this.itemListener = itemListener;
    }
    public  User getItem(int position){
        return list.get(position);
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = list.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView tvUsername;
        private ImageView userImage;
        public UserViewHolder(@NonNull View view) {
            super(view);
            tvUsername = view.findViewById(R.id.tvUsername);
            userImage = view.findViewById(R.id.userImage);
            view.setOnClickListener(this);
        }
        public void bind(User user) {
            tvUsername.setText(user.getUsername());
            if (user.getImage() != null && !user.getImage().isEmpty()) {
                Glide.with(itemView.getContext()).load(user.getImage()).into(userImage);
            } else {
                userImage.setImageResource(R.drawable.trang);
            }
        }

        @Override
        public void onClick(View view) {
            if(itemListener != null){
                itemListener.onItemClick(view, getAdapterPosition());
            }
        }
    }
    public interface ItemListener1{
        void onItemClick(View view, int position);
    }
}
