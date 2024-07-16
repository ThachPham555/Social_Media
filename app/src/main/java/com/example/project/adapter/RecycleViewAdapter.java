package com.example.project.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.R;
import com.example.project.model.Post;
import com.example.project.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.HomeViewHolder>{
    private List<Post> list;
    private Itemlistener itemlistener;
    private int likesCount;
    private boolean isLiked;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    public RecycleViewAdapter() {
        list = new ArrayList<>();
    }

    public void setList(List<Post> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public void setItemlistener(Itemlistener itemlistener) {
        this.itemlistener = itemlistener;
    }

    public Post getItem(int position){
        return list.get(position);
    }

    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_upload, parent, false);
        return new HomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, int position) {
        db = FirebaseFirestore.getInstance();
        Post post = list.get(position);
        holder.name.setText(post.getName());
        holder.gio.setText(post.getGio());
        holder.date.setText(post.getDate());
        holder.des.setText(post.getDes());
        holder.likeCountTextView.setText(String.valueOf(post.getLike()));
        Picasso.get()
                .load(post.getImage())
                .fit()
                .centerCrop()
                .into(holder.imageView);
        Picasso.get()
                .load(post.getUserImage())
                .fit()
                .centerCrop()
                .into(holder.userImage);
        auth = FirebaseAuth.getInstance();
        db.collection("users").document(auth.getCurrentUser().getUid())
                .get().addOnCompleteListener(tasku -> {
                    DocumentSnapshot documentSnapshotu = tasku.getResult();
                    User useru = documentSnapshotu.toObject(User.class);
                    isLiked = useru.isLikedByCurrentUser();

                    if (isLiked) {
                        holder.likeButton.setImageResource(R.drawable.ic_liked);
                    } else {
                        holder.likeButton.setImageResource(R.drawable.ic_like);
                    }
                    holder.likeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!isLiked) {
                                isLiked = true;
                                holder.likeButton.setImageResource(R.drawable.ic_liked);
                                db.collection("posts")
                                        .whereEqualTo("image", post.getImage())
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (DocumentSnapshot document : task.getResult()) {
                                                        String postId = document.getId();
                                                        Post post2 = document.toObject(Post.class);
                                                        int currentLikes = post2.getLike();
                                                        currentLikes += 1;
                                                        db.collection("posts").document(postId)
                                                                .update("likes", currentLikes)
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                    }
                                                                });
                                                        db.collection("users").document(auth.getCurrentUser().getUid())
                                                                .update("isLikedByCurrentUser", isLiked)
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void unused) {

                                                                    }
                                                                });
                                                    }
                                                }
                                            }
                                        });
                            } else {
                                isLiked = false;
                                holder.likeButton.setImageResource(R.drawable.ic_like);
                                db.collection("posts")
                                        .whereEqualTo("image", post.getImage())
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (DocumentSnapshot document : task.getResult()) {
                                                        String postId = document.getId();
                                                        Post post2 = document.toObject(Post.class);
                                                        int currentLikes = post2.getLike();
                                                        currentLikes -= 1;
                                                        db.collection("posts").document(postId)
                                                                .update("likes", currentLikes)
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                    }
                                                                });
                                                        db.collection("users").document(auth.getCurrentUser().getUid())
                                                                .update("isLikedByCurrentUser", isLiked)
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void unused) {

                                                                    }
                                                                });
                                                    }
                                                }
                                            }
                                        });
                            }
                        }
                    });
                });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    //Buoc 1
    public class HomeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView name, gio, date, des;
        ImageView imageView, userImage;
        private ImageButton likeButton;
        private TextView likeCountTextView;

        public HomeViewHolder(@NonNull View view) {
            super(view);
            name = view.findViewById(R.id.tUsername);
            gio = view.findViewById(R.id.tGio);
            date = view.findViewById(R.id.tDate);
            des = view.findViewById(R.id.tDes);
            imageView = view.findViewById(R.id.image_view);
            userImage = view.findViewById(R.id.userImage);

            likeButton = view.findViewById(R.id.likeButton);
            likeCountTextView = view.findViewById(R.id.likeCountTextView);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(itemlistener != null){
                itemlistener.onItemClick(view, getAdapterPosition());
            }
        }
    }

    public interface Itemlistener{
        void onItemClick(View view, int position);
    }
}
