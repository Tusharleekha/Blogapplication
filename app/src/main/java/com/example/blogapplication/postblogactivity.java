package com.example.blogapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class postblogactivity extends AppCompatActivity {

    private static final int MAX_LENGTH = 20;
    private Toolbar newposttoolbar;
    private ImageView addnewimage;
    private EditText description;
    private Button postblog;
    private Uri postimage=null;
    private ProgressBar postprogress;
    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    private String currentuserid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postblogactivity);

        storageReference= FirebaseStorage.getInstance().getReference();
        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();

        currentuserid=firebaseAuth.getCurrentUser().getUid();

        newposttoolbar=findViewById(R.id.newposttoolbar);
        setSupportActionBar(newposttoolbar);
        getSupportActionBar().setTitle("Add New Blog");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        addnewimage=findViewById(R.id.addnewimage);
        description=findViewById(R.id.descriptionid);
        postblog=findViewById(R.id.postbtn);
        postprogress=findViewById(R.id.postprogressid);

        addnewimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(postblogactivity.this);
            }
        });

        postblog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String desc=description.getText().toString();
                if(!TextUtils.isEmpty(desc)&&postimage!=null)
                {
                   postprogress.setVisibility(View.VISIBLE);
                   String randomname = getRandomString();
                   final StorageReference imagepath=storageReference.child("post_images").child(randomname+".jpg");

                    imagepath.putFile(postimage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            imagepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                               @Override
                                public void onSuccess(Uri uri) {
                                    final Uri downloadimageurl = uri;
                                    Map<String,Object> postmap=new HashMap<>();
                                    postmap.put("imageurl",downloadimageurl.toString());
                                    postmap.put("desc",desc);
                                    postmap.put("user_id",currentuserid);
                                    postmap.put("timestamp", FieldValue.serverTimestamp());


                                    firebaseFirestore.collection("Posts").add(postmap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentReference> task) {

                                            if(task.isSuccessful())
                                            {
                                                Toast.makeText(postblogactivity.this, "post was added", Toast.LENGTH_SHORT).show();
                                              Intent intent=new Intent(postblogactivity.this,MainActivity.class);
                                               startActivity(intent);
                                              finish();
                                                postprogress.setVisibility(View.INVISIBLE);
                                            }
                                            else
                                            {
                                                String e=task.getException().getMessage();
                                                Toast.makeText(postblogactivity.this, e, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    String error=e.getMessage().toString();
                                    Toast.makeText(postblogactivity.this, error, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            String error=e.getMessage().toString();
                            Toast.makeText(postblogactivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    });


                }
                else
                {
                    Toast.makeText(postblogactivity.this, "Please enter all feilds", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                postimage = result.getUri();
                addnewimage.setImageURI(postimage);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

//    public static String random() {
//        Random generator = new Random();
//        StringBuilder randomStringBuilder = new StringBuilder();
//        int randomLength = generator.nextInt(MAX_LENGTH);
//        char tempChar;
//        for (int i = 0; i < randomLength; i++){
//            tempChar = (char) (generator.nextInt(96) + 26);
//            randomStringBuilder.append(tempChar);
//        }
//        return randomStringBuilder.toString();
//    }
    private static final String ALLOWED_CHARACTERS ="0123456789qwertyuiopasdfghjklzxcvbnm";

    private static String getRandomString()
    {
        final Random random=new Random();
        final StringBuilder sb=new StringBuilder(MAX_LENGTH);
        for(int i=0;i<MAX_LENGTH;++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }
}
