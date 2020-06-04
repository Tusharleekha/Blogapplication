package com.example.blogapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class blogrecycleradapter extends RecyclerView.Adapter<blogrecycleradapter.ViewHolder> {

    public List<blogpost>blog_list;
    public Context context;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    public blogrecycleradapter(List<blogpost>blog_list)
    {
       this.blog_list=blog_list;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView descview;
        private TextView username;
        private View mview;
        private ImageView blogImageView;
        private TextView blogdate;
        private TextView blogusername;
        private ImageView bloguserimage;
        private ImageView blog_like_btn;
        private TextView bloglikecount;
        private ImageView blog_comment_btn;
        private TextView blogcmntcount;
        private Button blogDeleteBtn;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            mview=itemView;

            blog_like_btn=mview.findViewById(R.id.blog_like_btn);
            blog_comment_btn=mview.findViewById(R.id.blog_comment_btn);
            blogDeleteBtn=mview.findViewById(R.id.blog_delete_btn);

        }

        public void setDesc(String text)
        {
            descview=mview.findViewById(R.id.blog_desc);
            descview.setText(text);

        }

        public void setUserText(String text,String imagetext)
        {
            blogusername=mview.findViewById(R.id.blog_user_name);
            blogusername.setText(text);
            bloguserimage=mview.findViewById(R.id.blog_user_image);
            Glide.with(context).load(imagetext).into(bloguserimage);

        }
        public void settime(String date)
        {
            blogdate=mview.findViewById(R.id.blog_user_date);
            blogdate.setText(date);
        }

        public void setblogImage(String downloaduri)
        {
            blogImageView=mview.findViewById(R.id.blog_image);
            Glide.with(context).load(downloaduri).into(blogImageView);
        }

        public void updateLikesCount(int count)
        {
            bloglikecount=mview.findViewById(R.id.blog_like_count);
            bloglikecount.setText(count+" likes");
        }


    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_list_item,parent,false);

        context=parent.getContext();
        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        final String blogpostId=blog_list.get(position).blogpostid;
        final String currentUserId=firebaseAuth.getCurrentUser().getUid();

        String desc_data=blog_list.get(position).getDesc();
        holder.setDesc(desc_data);
        String Imageurl=blog_list.get(position).getImageurl();
        holder.setblogImage(Imageurl);
        String blog_user_id=blog_list.get(position).getUser_id();

        if(blog_user_id.equals(currentUserId))
        {

            holder.blogDeleteBtn.setEnabled(true);
            holder.blogDeleteBtn.setVisibility(View.VISIBLE);
        }

        firebaseFirestore.collection("Users").document(blog_user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
               if(task.isSuccessful())
               {
                  String username=task.getResult().getString("name");
                  String userimage=task.getResult().getString("image");
                  holder.setUserText(username,userimage);


               }
            }
        });
        long millisec=blog_list.get(position).getTimestamp().getTime();
        String datestring= DateFormat.format("MM/dd/yyyy",new Date(millisec)).toString();
        holder.settime(datestring);

//        get like count
        firebaseFirestore.collection("Posts/" + blogpostId + "/Likes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if(!queryDocumentSnapshots.isEmpty())
                {
                    int count=queryDocumentSnapshots.size();
                    holder.updateLikesCount(count);
                }
                else
                {
                    holder.updateLikesCount(0);
                }
            }
        });

//       getlikes

        firebaseFirestore.collection("Posts/" + blogpostId + "/Likes").document(currentUserId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                if(documentSnapshot.exists())
                {
                    holder.blog_like_btn.setImageDrawable(context.getDrawable(R.mipmap.action_like_accent));
                }
                else
                {
                    holder.blog_like_btn.setImageDrawable(context.getDrawable(R.mipmap.action_like_gray));
                }
            }
        });

        holder.blog_like_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                firebaseFirestore.collection("Posts/" + blogpostId + "/Likes").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if(!task.getResult().exists())
                        {
                            Map<String,Object> likesMap=new HashMap<>();
                            likesMap.put("timestamp", FieldValue.serverTimestamp());
                            firebaseFirestore.collection("Posts/" + blogpostId + "/Likes").document(currentUserId).set(likesMap);
                        }
                        else
                        {
                            firebaseFirestore.collection("Posts/" + blogpostId + "/Likes").document(currentUserId).delete();
                        }

                    }
                });


            }
        });

        holder.blog_comment_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent commentintent=new Intent(context,commentactivity.class);
                commentintent.putExtra("blog_post_id",blogpostId);
                context.startActivity(commentintent);
            }
        });

        holder.blogDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                firebaseFirestore.collection("Posts").document(blogpostId).delete();
                blog_list.remove(position);
                notifyItemRemoved(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return blog_list.size();
    }



}
