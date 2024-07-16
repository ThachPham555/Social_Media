package com.example.project.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.R;
import com.example.project.adapter.UserAdapterChat;
import com.example.project.adapter.UserAdapterChatRow;
import com.example.project.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class fragmentMessage extends Fragment{
    RecyclerView recyclerView, recyclerView1;
    UserAdapterChat userAdapter;
    UserAdapterChatRow userAdapterChatRow;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    public fragmentMessage() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_message, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Toolbar toolbar = view.findViewById(R.id.toolBar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            db.collection("users").document(userId).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                User user = documentSnapshot.toObject(User.class);
                                String userName = user.getUsername();
                                activity.getSupportActionBar().setTitle(userName);
                            }
                        }
                    });
        }
        userAdapter = new UserAdapterChat(getContext());
        recyclerView = view.findViewById(R.id.recycler);
        recyclerView.setAdapter(userAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        userAdapterChatRow = new UserAdapterChatRow(getContext());
        recyclerView1 = view.findViewById(R.id.recyclerViewUsers);
        recyclerView1.setAdapter(userAdapterChatRow);
        recyclerView1.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        db.collection("users")
                .get()
                .addOnCompleteListener(task -> {
                    userAdapter.clear();
                    if (task.isSuccessful()){
                        for(QueryDocumentSnapshot document : task.getResult()){
                            User user = document.toObject(User.class);
                            if(user!=null && user.getUid() != null && !user.getUid().equals(currentUser.getUid())){
                                userAdapterChatRow.add(user);
                            }
                        }
                        userAdapterChatRow.notifyDataSetChanged();
                    }
                });

        DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("chats");
        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> usersId = new ArrayList<>();
                for (DataSnapshot chatSnapshot : dataSnapshot.getChildren()) {
                    DatabaseReference chatReference = chatSnapshot.getRef();
                    String[] a = chatReference.toString().split("/");
                    if(a[a.length-1].contains(currentUser.getUid())){
                        usersId.add(a[a.length-1]);
                        System.out.println(a[a.length-1]);
                    }
                }
                db.collection("users")
                .get()
                .addOnCompleteListener(task -> {
                    userAdapter.clear();
                    if (task.isSuccessful()){
                        for(QueryDocumentSnapshot document : task.getResult()){
                            User user = document.toObject(User.class);
                            for(String u: usersId){
                                if(u.contains(user.getUid()) && !user.getUid().equals(currentUser.getUid())){
                                    userAdapter.add(user);
                                }
                            }
                        }
                        userAdapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
