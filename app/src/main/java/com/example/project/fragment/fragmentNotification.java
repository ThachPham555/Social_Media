package com.example.project.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.DetailActivity;
import com.example.project.R;
import com.example.project.adapter.NotiAdapter;
import com.example.project.model.Notification;
import com.example.project.model.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class fragmentNotification extends Fragment implements NotiAdapter.ItemListener2{
    private RecyclerView recyclerView;
    private NotiAdapter adapter;

    private FirebaseFirestore db;
    private List<Notification> list;
    public fragmentNotification() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notification, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerView);
        list = new ArrayList<>();
        adapter = new NotiAdapter();
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();
        db.collection("notifications")
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        for(DocumentSnapshot document : task.getResult()){
                            Notification noti = document.toObject(Notification.class);
                            if(!noti.getUserId().equals(currentUserId)){
                                list.add(noti);
                            }
                        }
                        System.out.println("------------------------");
                        Collections.sort(list, new Comparator<Notification>() {
                            @Override
                            public int compare(Notification o1, Notification o2) {
                                String[] a = o1.getMessage().trim().split(" ");
                                String[] b = o2.getMessage().trim().split(" ");
                                String p1 = a[a.length-1] + " " + a[a.length-3];
                                String p2 = b[b.length-1] + " " + b[b.length-3];
                                System.out.println(p1+"-----------------"+p2);
                                return p2.compareTo(p1);
                            }
                        });
                        adapter.setList(list);
                    }
                });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false ));
        adapter.setList(list);
        recyclerView.setAdapter(adapter);
        adapter.setItemListener(this);
    }

    @Override
    public void onItemClick(View view, int position) {
        Notification no = adapter.getnoti(position);
        db.collection("posts").document(no.getPostId())
                .get().addOnCompleteListener(task -> {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    Post post = documentSnapshot.toObject(Post.class);
                    Intent i = new Intent(getContext(), DetailActivity.class);
                    i.putExtra("post", post);
                    startActivity(i);
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        list = new ArrayList<>();
        adapter.setList(list);
    }
}
