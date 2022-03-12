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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends AppCompatActivity {

    private CircleImageView imageView;
    private TextView imageTitle;
    private TextInputEditText update_userName;
    private MaterialButton update_btn;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private Uri imageUri;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private String userImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        initUiComponent();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        getUserInformation();
        setActivityResultLauncherForSelectingImage();


        update_btn.setOnClickListener(listener ->{

            updateUserInformation();
        });

        imageView.setOnClickListener(listener ->{

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},2);
            }else {

                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(intent);
            }
        });

    }

    public void initUiComponent(){
        imageTitle      = findViewById(R.id.update_imageTitle);
        imageView       = findViewById(R.id.update_imageView);
        update_userName = findViewById(R.id.update_userName);
        update_btn      = findViewById(R.id.update_updateBtn);
    }

    public void setActivityResultLauncherForSelectingImage(){
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),result ->{

           int resultCode = result.getResultCode();
           Intent data    = result.getData();

           if (resultCode == RESULT_OK && data !=null){

               imageUri = data.getData();
               Glide.with(this)
                       .load(imageUri)
                       .centerCrop()
                       .into(imageView);
           }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 2 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

            Glide.with(UserProfileActivity.this)
                    .load(imageUri)
                    .centerCrop()
                    .into(imageView);
        }
    }

    public void getUserInformation(){
        databaseReference.child("Users").child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String user = snapshot.child("userName").getValue().toString();
                userImage = snapshot.child("image").getValue().toString();
                update_userName.setText(user);

                if (userImage != null){

                    Glide.with(getApplicationContext())
                            .load(userImage)
                            .centerCrop()
                            .into(imageView);

                }else {
                    imageView.setImageResource(R.drawable.ic_singup);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void updateUserInformation(){

        String updateUserName = update_userName.getText().toString();
        databaseReference.child("Users").child(currentUser.getUid()).child("userName").setValue(updateUserName);

        if (imageUri != null){

            UUID randomImgId = UUID.randomUUID();
            String image     = "images/"+randomImgId+".jpg";

            storageReference.child(image).putFile(imageUri).addOnSuccessListener(result ->{
                StorageReference userStorage = firebaseStorage.getReference(image);
                userStorage.getDownloadUrl().addOnSuccessListener(task ->{
                    String imagePath = task.toString();
                    databaseReference.child("Users").child(currentUser.getUid()).child("image").setValue(imagePath).addOnCompleteListener(listener ->{
                        Toast.makeText(getApplicationContext(), "Image successfully saved in database", Toast.LENGTH_SHORT).show();
                    }).addOnFailureListener(taskResult ->{
                        Toast.makeText(getApplicationContext(), "Images was not saved in database", Toast.LENGTH_SHORT).show();
                    });
                });
            });

        }else {
            databaseReference.child("Users").child(currentUser.getUid()).child("image").setValue(userImage);
        }

        Intent intent = new Intent(UserProfileActivity.this,MainActivity.class);
        intent.putExtra("userName",updateUserName);
        startActivity(intent);
        finish();

    }
}