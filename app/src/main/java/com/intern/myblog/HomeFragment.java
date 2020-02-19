package com.intern.myblog;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.intern.myblog.BlogPost;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    private RecyclerView bloglistview;
    private List<BlogPost> blog_list;
    ProgressDialog progressDialog;
    int time=5000;
    FirebaseAuth mAuth;
    String userid2;
    FirebaseFirestore firebaseFirestore;
    BlogRecyclerAdapter blogRecyclerAdapter;
    int i=-1;
RecyclerView.LayoutManager layoutManager;
    public HomeFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View  view= inflater.inflate(R.layout.fragment_home, container, false);
        mAuth=FirebaseAuth.getInstance();
        userid2=mAuth.getCurrentUser().getUid();

        blog_list=new ArrayList<>();
        bloglistview=view.findViewById(R.id.bloglistview);
        //bloglistview=getActivity().findViewById(R.id.bloglistview);
        layoutManager = new LinearLayoutManager(getActivity());
        bloglistview.setLayoutManager(layoutManager);
        blogRecyclerAdapter=new BlogRecyclerAdapter(blog_list,userid2);

        bloglistview.setAdapter(blogRecyclerAdapter);






        firebaseFirestore=FirebaseFirestore.getInstance();
        progressDialog=new ProgressDialog(getActivity());
        final ProgressDialog progressDialog=new ProgressDialog(getActivity());
        progressDialog.setMessage("Connecting To Server...");

        progressDialog.setMax(100);
        //  progressDialog.setTitle("Fetching Post" +"                           "+

        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        progressDialog.show();


        final Handler handler2=new Handler();
        Runnable r=new Runnable() {
            @Override
            public void run() {

                progressDialog.dismiss();

            }
        };
        handler2.postDelayed(r,3000);





        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
            //     blogRecyclerAdapter.notifyDataSetChanged();
            Query firstquery = firebaseFirestore.collection("Posts").orderBy("timestamp", Query.Direction.DESCENDING);
            firstquery.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                        if (doc.getType() == DocumentChange.Type.ADDED) {
                            String blogpostid=doc.getDocument().getId();
                            //  Toast.makeText(getContext(), ""+doc.getDocument().get("desc"), Toast.LENGTH_SHORT).show();
                            BlogPost blogPost = doc.getDocument().toObject(BlogPost.class).withId(blogpostid);
                            blog_list.add(blogPost);
                            blogRecyclerAdapter.notifyDataSetChanged();

                        }
                    }
                }
            });
        }
    }
}
