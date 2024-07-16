package com.example.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.project.adapter.UserAdapter;
import com.example.project.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements UserAdapter.ItemListener1{
    private SearchView searchView;
    private RecyclerView recyclerView;
    private UserAdapter adapter;
    private List<User> userList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchView = findViewById(R.id.search);
        recyclerView = findViewById(R.id.recyclerView);
        userList = new ArrayList<>();
        adapter = new UserAdapter();

        db = FirebaseFirestore.getInstance();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                performSearch(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                performSearch(s);
                return false;
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false ));
        adapter.setList(userList);
        recyclerView.setAdapter(adapter);
        adapter.setItemListener(this);
    }
    private void performSearch(String s){
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        userList.clear();
                        for(QueryDocumentSnapshot document : task.getResult()){
                            User user = document.toObject(User.class);
                            if(user.getUsername().toLowerCase().contains(s.toLowerCase()) && !user.getUid().equals(currentUserId)){
                                userList.add(user);
                            }
                        }
                        adapter.setList(userList);
                    }
                });
    }

    @Override
    public void onItemClick(View view, int position) {
        User user = adapter.getItem(position);
        Intent i = new Intent(SearchActivity.this, UserInfoActivity.class);
        i.putExtra("user", user);
        startActivity(i);
    }
    @Override
    public void onResume() {
        super.onResume();
        userList = new ArrayList<>();
        adapter.setList(userList);
    }
}