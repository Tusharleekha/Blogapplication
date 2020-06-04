package com.example.blogapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class CommentsRecyclerAdapter extends RecyclerView.Adapter<CommentsRecyclerAdapter.ViewHolder> {

    public List<Comments> commentsList;
    public Context context;
    private FirebaseFirestore firebaseFirestore;

    public CommentsRecyclerAdapter(List<Comments> commentsList)
    {
        this.commentsList=commentsList;
    }


    @NonNull
    @Override
    public CommentsRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_list_item,parent,false);
        context=parent.getContext();
        firebaseFirestore=FirebaseFirestore.getInstance();
        return new CommentsRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CommentsRecyclerAdapter.ViewHolder holder, int position) {

         holder.setIsRecyclable(false);

         String commentMessage= commentsList.get(position).getMessage();
         holder.setComment_message(commentMessage);

        String user_id=commentsList.get(position).getUser_id();
        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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

    }

    @Override
    public int getItemCount() {

        if(commentsList!=null)
        {
            return commentsList.size();
        }
        else
        {
            return 0;
        }
    }
    public class ViewHolder extends RecyclerView.ViewHolder
    {

        private View mView;
        private TextView comment_message;
        private ImageView comment_user_image;
        private TextView comment_username;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;
        }

        public void setComment_message(String message)
        {
            comment_message=mView.findViewById(R.id.comment_message);
            comment_message.setText(message);
        }

        public void setUserText(String text,String imagetext)
        {
            comment_username=mView.findViewById(R.id.comment_username);
            comment_username.setText(text);
            comment_user_image=mView.findViewById(R.id.comment_user_image);
            Glide.with(context).load(imagetext).into(comment_user_image);

        }
    }
}
