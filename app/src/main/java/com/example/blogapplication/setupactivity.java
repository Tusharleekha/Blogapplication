package com.example.blogapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class setupactivity extends AppCompatActivity {

    private CircleImageView setupImage;
    private Uri mainImageuri=null;
    private EditText name;
    private Button saveset;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private ProgressBar saveprogress;
    private FirebaseFirestore firebaseFirestore;
    private String user_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setupactivity);

        Toolbar setuptoolbar=findViewById(R.id.setuptoolbar);
        setSupportActionBar(setuptoolbar);
        getSupportActionBar().setTitle("Account Settings");

        firebaseFirestore=FirebaseFirestore.getInstance();

        firebaseAuth=FirebaseAuth.getInstance();

        user_id=firebaseAuth.getCurrentUser().getUid();

        storageReference= FirebaseStorage.getInstance().getReference();

        setupImage=findViewById(R.id.setupimage);
        name=findViewById(R.id.setupnameid);
        saveset=findViewById(R.id.setupbtnid);
        saveprogress=findViewById(R.id.saveprogressid);

        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful())
                {
                    if(task.getResult().exists())
                    {
                       String getname=task.getResult().getString("name");
                       String image=task.getResult().getString("image");

                       name.setText(getname);
                        Glide.with(setupactivity.this).load(image).into(setupImage);
                    }
                }
                else
                {
                    String e=task.getException().getMessage();
                    Toast.makeText(setupactivity.this, e, Toast.LENGTH_SHORT).show();
                }
            }
        });

        saveset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String username=name.getText().toString();
                if(!TextUtils.isEmpty(username)&&mainImageuri!=null)
                {

                   user_id=firebaseAuth.getCurrentUser().getUid();

                   final StorageReference image_path=storageReference.child("profile_image").child(user_id+".jpg");
                   saveprogress.setVisibility(View.VISIBLE);
                   image_path.putFile(mainImageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                       @Override
                       public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                           image_path.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                               @Override
                               public void onSuccess(Uri uri) {
                                   final Uri downloadUrl = uri;
                                   Map<String,String>usermap=new HashMap<>();
                                   usermap.put("name",username);
                                   usermap.put("image",downloadUrl.toString());
                                   firebaseFirestore.collection("Users").document(user_id).set(usermap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                       @Override
                                       public void onComplete(@NonNull Task<Void> task) {

                                           if(task.isSuccessful())
                                           {
                                               Toast.makeText(setupactivity.this, "Settings updated", Toast.LENGTH_SHORT).show();
                                               Intent mainintent=new Intent(setupactivity.this,MainActivity.class);
                                               startActivity(mainintent);
                                               finish();
                                           }
                                           else
                                           {
                                               String e=task.getException().getMessage();
                                               Toast.makeText(setupactivity.this, e, Toast.LENGTH_SHORT).show();
                                           }
                                       }
                                   });

                               }
                           });
                       }
                   });

                }
               else
                {
                    Toast.makeText(setupactivity.this, "Enter all feilds", Toast.LENGTH_SHORT).show();
                }
            }
        });

        setupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
                {

                    if(ContextCompat.checkSelfPermission(setupactivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
                    {

                        Toast.makeText(setupactivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                        ActivityCompat.requestPermissions(setupactivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
                        ActivityCompat.requestPermissions(setupactivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                    }
                    else
                    {
                        CropImage.activity()
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setAspectRatio(1,1)
                                .start(setupactivity.this);
                    }

                }

            }
        });

    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mainImageuri = result.getUri();
                setupImage.setImageURI(mainImageuri);


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

}
