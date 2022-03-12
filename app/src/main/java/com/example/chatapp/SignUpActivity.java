package com.example.chatapp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpActivity extends AppCompatActivity {

    private CircleImageView circleImageView;
    private TextInputEditText singUp_user, singUp_password, singUp_email;
    private TextView imageTitle;
    private MaterialButton singUp_btn;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private Uri image;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        initUiComponents();
        setActivityResultLauncherForImagePick();
        auth              = FirebaseAuth.getInstance();
        database          = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();
        firebaseStorage   = FirebaseStorage.getInstance();
        storageReference  = firebaseStorage.getReference();

        circleImageView.setOnClickListener(view -> {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE )!= PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
            }else {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(intent);

            }
        });

        singUp_btn.setOnClickListener(listener ->{

            String userName   = singUp_user.getText().toString();
            String userEmail  = singUp_email.getText().toString();
            String userPass   = singUp_password.getText().toString();

            if (!(userEmail.isEmpty()) && (!userName.isEmpty()) && (!userPass.isEmpty())){
                signUp(userEmail,userPass,userName);

            }else {
                Toast.makeText(this, "Please fill all the information", Toast.LENGTH_SHORT).show();
            }


        });
    }

    private void signUp(String userEmail, String userPass, String userName) {


        auth.createUserWithEmailAndPassword(userEmail,userPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){

                    databaseReference.child("Users").child(auth.getUid()).child("userName").setValue(userName);

                    if (image !=null){

                        UUID randomImageId = UUID.randomUUID();
                        String imageNameId = "images/"+randomImageId + ".jpg";

                        storageReference.child(imageNameId).putFile(image).addOnSuccessListener(listener -> {
                            StorageReference myStorage = firebaseStorage.getReference(imageNameId);
                            myStorage.getDownloadUrl().addOnSuccessListener(uri ->{
                                String imagePath = uri.toString();
                                databaseReference.child("Users").child(auth.getUid()).child("image").setValue(imagePath).addOnSuccessListener(taskResult ->{
                                    Toast.makeText(SignUpActivity.this, "Image successfully saved", Toast.LENGTH_SHORT).show();
                                }).addOnFailureListener(taskResult ->{
                                    Toast.makeText(SignUpActivity.this, "Images could not be save, please try again", Toast.LENGTH_SHORT).show();
                                });

                            });
                        });


                    }else {

                        databaseReference.child("Users").child(auth.getUid()).child("image").setValue("null");
                    }

                    Intent intent = new Intent(SignUpActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();

                }else {
                    Toast.makeText(SignUpActivity.this, "Unsuccessful signUp", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    //init ui components
    public void initUiComponents(){
        circleImageView = findViewById(R.id.singUp_imageView);
        singUp_user     = findViewById(R.id.singUp_edit_user);
        singUp_password = findViewById(R.id.singUp_edit_password);
        singUp_email    = findViewById(R.id.singUp_edit_email);
        singUp_btn      = findViewById(R.id.singUp_signUp_btn);
        imageTitle      = findViewById(R.id.singUp_imageTitle);
    }

    public void setActivityResultLauncherForImagePick (){

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),result->{

            int resultCode = result.getResultCode();
            Intent data    = result.getData();

            if (resultCode == RESULT_OK && data != null){

            image = data.getData();
                Glide.with(this)
                        .load(image)
                        .centerCrop()
                        .into(circleImageView);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

            Glide.with(this)
                    .load(image)
                    .centerCrop()
                    .into(circleImageView);
        }
    }
}