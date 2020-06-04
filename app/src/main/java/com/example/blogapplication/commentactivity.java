package com.example.blogapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class commentactivity extends AppCompatActivity {

    private Toolbar commenttoolbar;

    private EditText comment_feild;
    private ImageView comment_post_btn;
    private String blog_post_id;
    private String current_user_id;
    private RecyclerView comment_list;
    private CommentsRecyclerAdapter commentsRecyclerAdapter;
    private List<Comments>commentsList;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commentactivity);

        commenttoolbar=findViewById(R.id.comment_toolbar);
        setSupportActionBar(commenttoolbar);
        getSupportActionBar().setTitle("Comments");

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();

        current_user_id=firebaseAuth.getCurrentUser().getUid();

        blog_post_id=getIntent().getStringExtra("blog_post_id");

        comment_feild=findViewById(R.id.comment_feild);
        comment_post_btn=findViewById(R.id.comment_post_btn);
        comment_list=findViewById(R.id.comment_list);

        commentsList=new ArrayList<>();
        commentsRecyclerAdapter=new CommentsRecyclerAdapter(commentsList);
        comment_list.setHasFixedSize(true);
        comment_list.setLayoutManager(new LinearLayoutManager(this));
        comment_list.setAdapter(commentsRecyclerAdapter);

        firebaseFirestore.collection("Posts/"+blog_post_id+"/Comments").addSnapshotListener(commentactivity.this,new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if(!queryDocumentSnapshots.isEmpty())
                {
                    for(DocumentChange doc : queryDocumentSnapshots.getDocumentChanges())
                    {
                        if(doc.getType()==DocumentChange.Type.ADDED)
                        {
                            String commentid = doc.getDocument().getId();
                            Comments comments=doc.getDocument().toObject(Comments.class);
                            commentsList.add(comments);
                            commentsRecyclerAdapter.notifyDataSetChanged();

                        }
                    }
                }
            }
        });

        comment_post_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String comment_message=comment_feild.getText().toString();
                if(comment_message.isEmpty())
                {
                    Toast.makeText(commentactivity.this, "please enter some text ", Toast.LENGTH_SHORT).show();

                }
                else
                {
                    Map<String,Object> commentsMap=new HashMap<>();
                    commentsMap.put("message",comment_message);
                    commentsMap.put("user_id",current_user_id);
                    commentsMap.put("timestamp", FieldValue.serverTimestamp());

                    Toast.makeText(commentactivity.this, blog_post_id, Toast.LENGTH_SHORT).show();

                    firebaseFirestore.collection("Posts/"+blog_post_id+"/Comments").add(commentsMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {

                            if(task.isSuccessful())
                            {
                                Toast.makeText(commentactivity.this, "comment posted", Toast.LENGTH_SHORT).show();
                                comment_feild.setText("");
                            }
                            else
                            {
                                Toast.makeText(commentactivity.this, "comment not posted", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                }
            }
        });
    }
}
