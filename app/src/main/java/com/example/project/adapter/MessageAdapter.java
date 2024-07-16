package com.example.project.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.ChatActivity;
import com.example.project.R;
import com.example.project.model.Message;
import com.example.project.model.User;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {
    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVER = 2;
    private Context context;
    private List<Message> list;

    public MessageAdapter(Context context) {
        this.context = context;
        this.list = new ArrayList<>();
    }

    public void add(Message message){
        list.add(message);
        notifyDataSetChanged();
    }

    public void clear(){
        list.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MessageAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if(viewType == VIEW_TYPE_SENT){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_row_sent, parent, false);
            return new MyViewHolder(view);
        }else{
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_row_received, parent, false);
            return new MyViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.MyViewHolder holder, int position) {
        Message message = list.get(position);
        if(message.getSenderId().equals(FirebaseAuth.getInstance().getUid())){
            holder.textViewSentMessage.setText(message.getMessage());
        }else{
            holder.textViewReceivedMessage.setText(message.getMessage());
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public  List<Message> getMessageList(){
        return list;
    }

    @Override
    public int getItemViewType(int position) {
        if(list.get(position).getSenderId().equals(FirebaseAuth.getInstance().getUid())){
            return VIEW_TYPE_SENT;
        }else{
            return VIEW_TYPE_RECEIVER;
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView textViewSentMessage, textViewReceivedMessage;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewSentMessage = itemView.findViewById(R.id.textViewSentMessage);
            textViewReceivedMessage = itemView.findViewById(R.id.textViewReceivedMessage);
        }
    }
}
