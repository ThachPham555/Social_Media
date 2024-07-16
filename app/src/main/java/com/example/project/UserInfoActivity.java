package com.example.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.project.adapter.RecycleViewAdapter;
import com.example.project.adapter.UserAdapter;
import com.example.project.model.Post;
import com.example.project.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UserInfoActivity extends AppCompatActivity implements RecycleViewAdapter.Itemlistener {
    private static final int PICK_IMAGE_REQUEST = 1;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private TextView tvName, tvSdt, fb;
    private ImageView userImage;
    private StorageReference storageRef;
    private Uri imageUri;

    private RecyclerView recyclerView;
    private RecycleViewAdapter adapter;

    private List<Post> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference("uploads");

        tvName = findViewById(R.id.tvName);
        tvSdt = findViewById(R.id.tvSDT);
        userImage = findViewById(R.id.userImage);
        fb = findViewById(R.id.textViewFacebook);
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(UserInfoActivity.this, HomeActivity.class);
                startActivity(i);
                finish();
            }
        });

        Intent intent = getIntent();
        User user = (User) intent.getSerializableExtra("user");

        if (user != null) {
            String userId = user.getUid();

            db.collection("users").document(userId).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                User user = documentSnapshot.toObject(User.class);
                                if (user != null) {
                                    tvName.setText(user.getUsername());
                                    tvSdt.setText("SĐT: " + user.getPhone());
                                    if (user.getImage() != null && !user.getImage().isEmpty()) {
                                        Glide.with(UserInfoActivity.this).load(user.getImage()).into(userImage);
                                    }
                                }
                            } else {
                                Toast.makeText(UserInfoActivity.this, "User data not found", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UserInfoActivity.this, "Error getting user data", Toast.LENGTH_SHORT).show();
                            Log.d("Firestore", "Error getting document", e);
                        }
                    });
        } else {
            Toast.makeText(UserInfoActivity.this, "No user logged in", Toast.LENGTH_SHORT).show();
        }

        recyclerView = findViewById(R.id.recycler_view);
        list = new ArrayList<>();
        adapter = new RecycleViewAdapter();
        db = FirebaseFirestore.getInstance();
        if (user != null) {
            String userId = user.getUid();
            db.collection("users").document(userId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String userName = documentSnapshot.getString("username");
                            db.collection("posts")
                                    .whereEqualTo("name", userName). get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if(task.isSuccessful()){
                                                for(QueryDocumentSnapshot document : task.getResult()){
                                                    Post post = document.toObject(Post.class);
                                                    list.add(post);
                                                }
                                                Collections.sort(list, new Comparator<Post>(){

                                                    @Override
                                                    public int compare(Post p1, Post p2) {
                                                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                                                        try {
                                                            Date date1 = dateFormat.parse(p1.getDate() + " " + p1.getGio());
                                                            Date date2 = dateFormat.parse(p2.getDate() + " " + p2.getGio());
                                                            return date2.compareTo(date1);
                                                        } catch (ParseException e){
                                                            e.printStackTrace();
                                                        }
                                                        return 0;
                                                    }
                                                });
                                                adapter.notifyDataSetChanged();
                                            }else{
                                                Toast.makeText(UserInfoActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    });

        }

        adapter.setList(list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(UserInfoActivity.this, RecyclerView.VERTICAL, false ));
        recyclerView.setAdapter(adapter);
        adapter.setItemlistener(this);
    }

    @Override
    public void onItemClick(View view, int position) {
        Post post = adapter.getItem(position);
        Intent i = new Intent(UserInfoActivity.this, DetailActivity.class);
        i.putExtra("post", post);
        startActivity(i);
    }
}