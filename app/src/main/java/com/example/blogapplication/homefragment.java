package com.example.blogapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;


/**
 * A simple {@link Fragment} subclass.
 */


public class homefragment extends Fragment {

    private RecyclerView blog_list_view;
    private List<blogpost>blog_list;

    private FirebaseFirestore firebaseFirestore;
    private blogrecycleradapter blogrecyclerAdapter;
    private DocumentSnapshot lastvisible;
    private FirebaseAuth firebaseAuth;
    private Boolean firstpagefirstload=true;

    public homefragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_homefragment, container, false);

        blog_list=new ArrayList<>();

        blog_list_view=view.findViewById(R.id.blog_list_view);
        blogrecyclerAdapter=new blogrecycleradapter(blog_list);
        blog_list_view.setLayoutManager(new LinearLayoutManager(getActivity()));
        blog_list_view.setAdapter(blogrecyclerAdapter);

       firebaseFirestore=FirebaseFirestore.getInstance();
       firebaseAuth=FirebaseAuth.getInstance();

//       blog_list_view.addOnScrollListener(new RecyclerView.OnScrollListener() {
//           @Override
//           public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//               super.onScrolled(recyclerView, dx, dy);
//               Boolean isReached=!recyclerView.canScrollVertically(1);
//               if(isReached)
//               {
//
//                   Toast.makeText(container.getContext(), "Reached", Toast.LENGTH_SHORT).show();
//                   loadmorepost();
//               }
//               else
//               {
//
//               }
//           }
//       });

           Query firstquery = firebaseFirestore.collection("Posts").orderBy("timestamp", Query.Direction.DESCENDING);

           firstquery.addSnapshotListener(getActivity(),new EventListener<QuerySnapshot>() {
               @Override
               public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

//                   if(firstpagefirstload){
//                   lastvisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
//                   }
                   for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                       if ((doc.getType() == DocumentChange.Type.ADDED)) {

                           String blogpostId=doc.getDocument().getId();
                           blogpost blogPost = doc.getDocument().toObject(blogpost.class).withId(blogpostId);
                           if(firstpagefirstload) {
                               blog_list.add(blogPost);
                           }
                           else
                           {
                               blog_list.add(0,blogPost);
                           }
                           blogrecyclerAdapter.notifyDataSetChanged();
                       }
                   }
//                   firstpagefirstload=false;
               }
           });

        return view;
    }

    public void loadmorepost()
    {
        Query nextquery=firebaseFirestore.collection("Posts").orderBy("timestamp",Query.Direction.DESCENDING).startAfter(lastvisible).limit(3);

        nextquery.addSnapshotListener(getActivity(),new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    lastvisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            String blogpostId=doc.getDocument().getId();
                            blogpost blogPost = doc.getDocument().toObject(blogpost.class).withId(blogpostId);
                            blog_list.add(blogPost);
                            blogrecyclerAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });
    }
}
