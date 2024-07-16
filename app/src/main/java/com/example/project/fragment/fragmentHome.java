package com.example.project.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.DetailActivity;
import com.example.project.R;
import com.example.project.adapter.RecycleViewAdapter;
import com.example.project.model.Post;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class fragmentHome extends Fragment implements RecycleViewAdapter.Itemlistener{
    private RecyclerView recyclerView;
    private RecycleViewAdapter adapter;
    private FirebaseFirestore db;
    private List<Post> list;

    public fragmentHome() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recycler_view);
        list = new ArrayList<>();
        adapter = new RecycleViewAdapter();
        db = FirebaseFirestore.getInstance();
        db.collection("posts").get()
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
        adapter.setList(list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false ));
        recyclerView.setAdapter(adapter);
        adapter.setItemlistener(this);
    }


    @Override
    public void onItemClick(View view, int position) {
        Post post = list.get(position);
        Intent i = new Intent(getContext(), DetailActivity.class);
        i.putExtra("post", post);
        startActivity(i);
    }

    @Override
    public void onResume() {
        super.onResume();
        list = new ArrayList<>();
        adapter.setList(list);
    }
}
