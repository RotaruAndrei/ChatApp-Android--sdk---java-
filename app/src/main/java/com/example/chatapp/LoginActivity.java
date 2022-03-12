package com.example.chatapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextInputEditText edit_user, edit_password;
    private MaterialButton login_btn, signUp_btn;
    private TextView forget_password;

    private FirebaseAuth auth;
    private FirebaseUser currentUser;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initUIComponents();
        auth                = FirebaseAuth.getInstance();

        login_btn.setOnClickListener(view -> {

            String email    = edit_user.getText().toString();
            String password = edit_password.getText().toString();

            if (!(email.isEmpty()) &&!(password.isEmpty())){
                login(email,password);
            }else {
                Toast.makeText(LoginActivity.this, "Please enter credentials", Toast.LENGTH_SHORT).show();
            }
        });

        signUp_btn.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this,SignUpActivity.class);
            startActivity(intent);
        });

        forget_password.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this,ResetPasswordActivity.class);
            startActivity(intent);
        });

    }

    //init components
    public void initUIComponents(){

        imageView       = findViewById(R.id.login_image_view);
        edit_user       = findViewById(R.id.login_user);
        edit_password   = findViewById(R.id.login_password);
        login_btn       = findViewById(R.id.login_login_btn);
        signUp_btn      = findViewById(R.id.login__sign_btn);
        forget_password = findViewById(R.id.login_forgot_password);

    }


    // user log in
    public void login(String email, String password){

        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(task ->{

            if (task.isSuccessful()){
                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
                Toast.makeText(LoginActivity.this, "Log in successfully", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(LoginActivity.this, "Log in failed, please try again", Toast.LENGTH_SHORT).show();
            }

        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = auth.getCurrentUser();

        //if the user account has been created
        if (currentUser !=null){
            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}