package com.example.project.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.project.ChatActivity;
import com.example.project.R;
import com.example.project.model.User;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class UserAdapterChatRow extends RecyclerView.Adapter<UserAdapterChatRow.MyViewHolder>{
    private Context context;
    private List<User> list;

    public UserAdapterChatRow(Context context) {
        this.context = context;
        this.list = new ArrayList<>();
    }

    public void add(User user){
        if(!list.contains(user)){
            list.add(user);
        }
    }

    public void clear(){
        list.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_message, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        User user = list.get(position);
        holder.name.setText(user.getUsername());
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(user.getUid()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if(documentSnapshot.exists()){
                        User user1 = documentSnapshot.toObject(User.class);
                        if (user1.getImage() != null && !user1.getImage().isEmpty()) {
                            Glide.with(holder.itemView.getContext()).load(user1.getImage()).into(holder.userImage);
                        } else {
                            holder.userImage.setImageResource(R.drawable.trang);
                        }
                    }
                });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, ChatActivity.class);
                i.putExtra("id", user.getUid());
                i.putExtra("name", user.getUsername());
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public  List<User> getUserList(){
        return list;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView name;
        private ImageView userImage;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvUsername);
            userImage = itemView.findViewById(R.id.userImage);
        }
    }
}
