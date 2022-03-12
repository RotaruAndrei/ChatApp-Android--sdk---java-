package com.example.chatapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private List<ChatModelClass> chatList ;
    private String CurrentUserName;
    private boolean flag;
    private int send;
    private int receive;

    public ChatAdapter(List<ChatModelClass> chatList, String userName) {
        this.chatList = chatList;
        this.CurrentUserName = userName;
        flag          = false;
        send          = 1;
        receive       = 2;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        if (viewType == send){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_chat_send,parent,false);
        }else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_chat_receive,parent,false);
        }

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.message.setText(chatList.get(position).getMessage());
    }


    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView message;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            if (flag){

                message = itemView.findViewById(R.id.card_chat_send_text);

            }else {

                message = itemView.findViewById(R.id.card_chat_receive_text);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {


            if (chatList.get(position).getSender().equals(CurrentUserName)){
                flag  = true;
                return send;
            }else {
                flag = false;
                return receive;
            }
    }
}
