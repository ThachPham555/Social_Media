package com.example.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.project.adapter.CommentAdapter;
import com.example.project.model.Comment;
import com.example.project.model.Post;
import com.example.project.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DetailActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private TextView tvName, tvGio, tvDate, tvDes;
    private ImageView userImage, imageView;
    private FloatingActionButton button;
    private ImageButton moreButton;
    private RecyclerView recyclerViewComments;
    private EditText editTextComment;
    private Button buttonSend;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        tvName = findViewById(R.id.tUsername);
        tvGio = findViewById(R.id.tGio);
        tvDate = findViewById(R.id.tDate);
        tvDes = findViewById(R.id.tDes);
        userImage = findViewById(R.id.userImage);
        imageView = findViewById(R.id.image_view);
        moreButton = findViewById(R.id.more);

        recyclerViewComments = findViewById(R.id.recyclerViewComments);
        editTextComment = findViewById(R.id.editTextComment);
        buttonSend = findViewById(R.id.buttonSend);

        recyclerViewComments.setLayoutManager(new LinearLayoutManager(this));
        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(commentList);
        recyclerViewComments.setAdapter(commentAdapter);

        Intent intent = getIntent();
        Post post = (Post) intent.getSerializableExtra("post");
        tvName.setText(post.getName());
        tvGio.setText(post.getGio());
        tvDate.setText(post.getDate());
        tvDes.setText(post.getDes());
        if (post.getImage() != null) {
            Glide.with(this).load(post.getImage()).into(imageView);
        }
        if (post.getUserImage() != null) {
            Glide.with(this).load(post.getUserImage()).into(userImage);
        }

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        db.collection("users").document(currentUser.getUid()).get()
                .addOnCompleteListener(task -> {
                    DocumentSnapshot document = task.getResult();
                    User user = document.toObject(User.class);
                    if (user.getUsername().equalsIgnoreCase(post.getName())) {
                        moreButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String[] op = {"Update", "Delete"};
                                AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
                                builder.setTitle("Choose options").setItems(op, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if (i == 1) {
                                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DetailActivity.this)
                                                    .setTitle("Do you want delete?").setPositiveButton("Yes", (dialogInterface1, i1) -> {
                                                        Toast.makeText(DetailActivity.this, "Delete", Toast.LENGTH_SHORT).show();
                                                        db.collection("posts")
                                                                .whereEqualTo("image", post.getImage())
                                                                .get()
                                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                        if (task.isSuccessful()) {
                                                                            for (DocumentSnapshot document : task.getResult()) {
                                                                                document.getReference().delete()
                                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                            @Override
                                                                                            public void onSuccess(Void aVoid) {
                                                                                                db.collection("notifications")
                                                                                                        .whereEqualTo("image", post.getImage())
                                                                                                        .get()
                                                                                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                                            @Override
                                                                                                            public void onComplete(@NonNull Task<QuerySnapshot> task1) {
                                                                                                                if (task1.isSuccessful()) {
                                                                                                                    for (DocumentSnapshot document1 : task1.getResult()) {
                                                                                                                        document1.getReference().delete();
                                                                                                                    }
                                                                                                                    Intent i = new Intent(DetailActivity.this, HomeActivity.class);
                                                                                                                    startActivity(i);
                                                                                                                    finish();
                                                                                                                } else {
                                                                                                                    Toast.makeText(DetailActivity.this, "Failed to delete notifications", Toast.LENGTH_SHORT).show();
                                                                                                                }
                                                                                                            }
                                                                                                        });
                                                                                            }
                                                                                        });
                                                                            }
                                                                        }
                                                                    }
                                                                });
                                                    }).setNegativeButton("No",(dialogInterface1, i1) -> {
                                                    });
                                            alertDialogBuilder.show();
                                        } else {
                                            Intent ii = new Intent(DetailActivity.this, UpdateActivity.class);
                                            ii.putExtra("post", post);
                                            startActivity(ii);
                                        }
                                    }
                                }).show();
                            }
                        });
                    }
                });

        button = findViewById(R.id.fab);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DetailActivity.this, HomeActivity.class);
                startActivity(i);
            }
        });

        loadComments(post.getImage());
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addComment(post.getImage());
            }
        });
    }

    private void loadComments(String image){
        db.collection("comments").whereEqualTo("image", image)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            commentList.clear();
                            for(DocumentSnapshot document : task.getResult()){
                                Comment comment = document.toObject(Comment.class);
                                commentList.add(comment);
                            }
                            Collections.sort(commentList, new Comparator<Comment>() {
                                @Override
                                public int compare(Comment comment, Comment t1) {
                                    return (int) (t1.getTimestamp()-comment.getTimestamp());
                                }
                            });
                            commentAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }
    private void addComment(String image){
        String content = editTextComment.getText().toString();
        if(content.isEmpty()){
            Toast.makeText(this, "Comment cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        FirebaseUser currentUser = auth.getCurrentUser();
        String userId = currentUser.getUid();
        db.collection("users").document(userId)
                .get().addOnCompleteListener(task -> {
                    DocumentSnapshot document = task.getResult();
                    User user1 = document.toObject(User.class);
                    String username = user1.getUsername();
                    Long timestamp = System.currentTimeMillis();
                    Comment comment = new Comment(image, userId, username, content, timestamp);

                    db.collection("comments").add(comment)
                            .addOnSuccessListener(documentReference -> {
                                Toast.makeText(DetailActivity.this, "Comment added", Toast.LENGTH_SHORT).show();
                                editTextComment.setText("");
                                loadComments(image);
                            });
                });
    }
}