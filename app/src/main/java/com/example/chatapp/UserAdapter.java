package com.example.chatapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private List<String> usersList;
    private String userName;
    private Context mContext;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    public UserAdapter(List<String> usersList, String userName, Context mContext) {
        this.usersList    = usersList;
        this.userName     = userName;
        this.mContext     = mContext;
        database          = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view         = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_user,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        databaseReference.child("Users").child(usersList.get(position)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String tempName = snapshot.child("userName").getValue().toString();
                String tempImg  = snapshot.child("image").getValue().toString();

                holder.userName.setText(tempName);

                if (!tempImg.equals("")){

                    Glide.with(mContext)
                            .load(tempImg)
                            .centerCrop()
                            .into(holder.imageView);
                }else {

                    Glide.with(mContext)
                            .load(R.drawable.ic_singup)
                            .centerCrop()
                            .into(holder.imageView);
                }

                holder.cardView.setOnClickListener(listener->{
                    Intent intent = new Intent(mContext,UserChatActivity.class);
                    intent.putExtra("userName",userName);
                    intent.putExtra("otherName",tempName);
                    mContext.startActivity(intent);
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView imageView;
        private TextView userName;
        private CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.card_view_circleImage);
            userName  = itemView.findViewById(R.id.carD_view_userName);
            cardView  = itemView.findViewById(R.id.card_viewId);
        }
    }
}
