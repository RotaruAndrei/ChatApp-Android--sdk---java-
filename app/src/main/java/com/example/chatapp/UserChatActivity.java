package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserChatActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView userName;
    private RecyclerView recyclerView;
    private EditText message;
    private FloatingActionButton btn;
    private String chat_userName, chat_partyMember;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private DatabaseReference anotherReference;
    private ChatAdapter adapter;
    private List<ChatModelClass> chatList;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_chat);

        initUiComponents();

        database          = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();
        anotherReference  = database.getReference();
        chat_userName     = getIntent().getStringExtra("userName");
        chat_partyMember  = getIntent().getStringExtra("otherName");
        userName.setText(chat_partyMember);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatList          = new ArrayList<>();
        getMessages();

        imageView.setOnClickListener(view -> {
            Intent intent = new Intent(UserChatActivity.this,MainActivity.class);
            startActivity(intent);
        });

        btn.setOnClickListener(view -> {

            String tempMessage = message.getText().toString();

            if (!tempMessage.equals("")){
                sendMessage(tempMessage);
                message.setText("");
            }
        });




    }

    //init Ui components
    public void initUiComponents(){
        imageView    = findViewById(R.id.chat_topImageView);
        userName     = findViewById(R.id.chat_topTextView);
        recyclerView = findViewById(R.id.chat_recyclerView);
        message      = findViewById(R.id.chat_bottom_messageText);
        btn          = findViewById(R.id.chat_bottom_floatBtn);
    }

    // save the user message into database
    private void sendMessage(String msg){

        String key = databaseReference.child("Messages").child(chat_userName).child(chat_partyMember).push().getKey();
        Map<String,Object> msgList = new HashMap<>();
        msgList.put("message",msg);
        msgList.put("sender",chat_userName);
        databaseReference.child("Messages").child(chat_userName).child(chat_partyMember).child(key).setValue(msgList).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){

                    anotherReference.child("Messages").child(chat_partyMember).child(chat_userName).child(key).setValue(msgList);

                }
            }
        });
    }

    public void getMessages(){

        databaseReference.child("Messages").child(chat_userName).child(chat_partyMember).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {


                ChatModelClass obj = snapshot.getValue(ChatModelClass.class);
                chatList.add(obj);
                adapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(chatList.size() - 1);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        adapter = new ChatAdapter(chatList,chat_userName);
        recyclerView.setAdapter(adapter);

    }
}