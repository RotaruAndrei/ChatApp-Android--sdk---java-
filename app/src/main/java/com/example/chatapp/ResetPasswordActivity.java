package com.example.chatapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextInputEditText edit_email;
    private MaterialButton reset_btn;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        initUiComponents();
        auth = FirebaseAuth.getInstance();

        reset_btn.setOnClickListener(listener ->{
            String email = edit_email.getText().toString();

            if (!(email.isEmpty())){

                resetUserPassword(email);
            }else {
                edit_email.setError("Email required");
                edit_email.requestFocus();
            }
        });


    }

    //init ui components
    public void initUiComponents(){
        imageView = findViewById(R.id.reset_image_view);
        edit_email = findViewById(R.id.reset_editEmail);
        reset_btn = findViewById(R.id.reset_reset_btn);
    }

    public void resetUserPassword(String email){

        auth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {

            if (task.isSuccessful()){
                Toast.makeText(this, "Email sent to email", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }
}