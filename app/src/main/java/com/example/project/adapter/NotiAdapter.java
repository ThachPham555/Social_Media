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
import com.example.project.model.Notification;
import com.example.project.model.Post;
import com.example.project.model.User;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class NotiAdapter extends RecyclerView.Adapter<NotiAdapter.NotiViewHolder>{
    private List<Notification> list;
    private ItemListener2 itemListener;
    private FirebaseFirestore db;

    public NotiAdapter() {
        list = new ArrayList<>();
    }
    public void setList(List<Notification> list) {
        this.list = list;
        notifyDataSetChanged();
    }
    public void setItemListener(ItemListener2 itemListener) {
        this.itemListener = itemListener;
    }
    public Notification getItem(int position){
        return list.get(position);
    }
    @NonNull
    @Override
    public NotiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_noti, parent, false);
        return new NotiViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotiViewHolder holder, int position) {
        Notification noti = list.get(position);
        holder.bind1(noti);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public Notification getnoti(int position){
        return list.get(position);
    }

    public class NotiViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView tvMessage;
        private ImageView userImage;
        public NotiViewHolder(@NonNull View view) {
            super(view);
            tvMessage = view.findViewById(R.id.tvMessage);
            userImage = view.findViewById(R.id.userImage);
            view.setOnClickListener(this);
        }
        public void bind1(Notification noti){
            tvMessage.setText(noti.getMessage());

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users").document(noti.getUserId()).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if(documentSnapshot.exists()){
                            User user = documentSnapshot.toObject(User.class);
                            if (user.getImage() != null && !user.getImage().isEmpty()) {
                                Glide.with(itemView.getContext()).load(user.getImage()).into(userImage);
                            } else {
                                userImage.setImageResource(R.drawable.trang);
                            }
                        }
                    });
        }

        @Override
        public void onClick(View view) {
            if(itemListener != null){
                itemListener.onItemClick(view, getAdapterPosition());
            }
        }
    }
    public interface ItemListener2{
        void onItemClick(View view, int position);
    }
}
