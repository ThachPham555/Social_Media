package com.example.project.fragment;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.project.DetailActivity;
import com.example.project.HomeActivity;
import com.example.project.R;
import com.example.project.adapter.RecycleViewAdapter;
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
import com.google.firebase.storage.UploadTask;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class fragmentProfile extends Fragment implements RecycleViewAdapter.Itemlistener{
    private static final int PICK_IMAGE_REQUEST = 1;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private TextView tvName, tvSdt;
    private ImageView userImage;
    private StorageReference storageRef;
    private Uri imageUri;

    private RecyclerView recyclerView;
    private RecycleViewAdapter adapter;

    private List<Post> list;
    public fragmentProfile() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference("uploads");

        tvName = view.findViewById(R.id.tvName);
        tvSdt = view.findViewById(R.id.tvSDT);
        userImage = view.findViewById(R.id.userImage);
        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

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
                                        Glide.with(getActivity()).load(user.getImage()).into(userImage);
                                    }
                                }
                            } else {
                                Toast.makeText(getContext(), "User data not found", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "Error getting user data", Toast.LENGTH_SHORT).show();
                            Log.d("Firestore", "Error getting document", e);
                        }
                    });
        } else {
            Toast.makeText(getContext(), "No user logged in", Toast.LENGTH_SHORT).show();
        }

        recyclerView = view.findViewById(R.id.recycler_view);
        list = new ArrayList<>();
        adapter = new RecycleViewAdapter();
        db = FirebaseFirestore.getInstance();
        if (currentUser != null) {
            String userId = currentUser.getUid();
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
                                                        Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                    adapter.notifyDataSetChanged();
                                }
                            });

        }

        adapter.setList(list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false ));
        recyclerView.setAdapter(adapter);
        adapter.setItemlistener(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            userImage.setImageURI(imageUri);
            uploadImage();
        }
    }

    private void uploadImage() {
        if (imageUri != null) {
            String userId = auth.getCurrentUser().getUid();
            StorageReference fileReference = storageRef.child(userId + ".jpg");

            fileReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String downloadUrl = uri.toString();
                            db.collection("users").document(userId).update("image", downloadUrl).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getContext(), "Image updated successfully", Toast.LENGTH_SHORT).show();
                                    db.collection("users").document(userId).get()
                                                    .addOnSuccessListener(documentSnapshot -> {
                                                        if(documentSnapshot.exists()){
                                                            String userName = documentSnapshot.getString("username");
                                                            updatePostUserImage(userName, downloadUrl);
                                                        }
                                                    });
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), "Failed to upload image", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    private void updatePostUserImage(String userName, String userImage){
        db.collection("posts")
                .whereEqualTo("name", userName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            db.collection("posts").document(document.getId())
                                    .update("userImage", userImage)
                                    .addOnCompleteListener(updateTask -> {
                                        if (updateTask.isSuccessful()) {
                                            Toast.makeText(getContext(), "Post user image updated successfully", Toast.LENGTH_SHORT).show();
                                            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                            fragmentTransaction.addToBackStack(null);
                                            fragmentTransaction.commit();
                                            Intent i = new Intent(getContext(), HomeActivity.class);
                                            startActivity(i);
                                        } else {
                                            Toast.makeText(getContext(), "Failed to update post user image", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        Toast.makeText(getContext(), "Failed to fetch user posts", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onItemClick(View view, int position) {
        Post post = adapter.getItem(position);
        Intent i = new Intent(getContext(), DetailActivity.class);
        i.putExtra("post", post);
        startActivity(i);
    }
    @Override
    public void onResume() {
        super.onResume();
        FirebaseUser currentUser = auth.getCurrentUser();
        List<Post> a = new ArrayList<>();
        if (currentUser != null) {
            String userId = currentUser.getUid();
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
                                                    a.add(post);
                                                }
                                                Collections.sort(a, new Comparator<Post>(){

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
                                                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    });

        }

        adapter.setList(a);
    }
}

