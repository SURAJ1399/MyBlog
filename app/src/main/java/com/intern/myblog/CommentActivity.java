package com.intern.myblog;


import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CommentActivity extends AppCompatActivity {

   ImageView commentsendbtn;
    EditText commenttext;
    List<Comments>commentList=new ArrayList<>();
    String blogpostid;
    ProgressBar progressBar3;
    FirebaseAuth mAuth;
    CommentsAdapter commentsRecylerAdapter;
    FirebaseFirestore firebaseFirestore;
    String image;
    String currentuserid;
    String name;
    RecyclerView commentlist;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        blogpostid=getIntent().getStringExtra("blogpostid");
        mAuth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        getSupportActionBar().hide();

        commentsendbtn=findViewById(R.id.comment_post_btn);
        commentlist=findViewById(R.id.comment_list);
        ///Recylerview firebase list

        commentsRecylerAdapter=new CommentsAdapter(commentList);
        commentlist.setHasFixedSize(true);
        commentlist.setLayoutManager(new LinearLayoutManager(this));
        commentlist.setAdapter(commentsRecylerAdapter);




        currentuserid=mAuth.getCurrentUser().getUid();
        commenttext=findViewById(R.id.comment_field);

        firebaseFirestore.collection("Users").document(currentuserid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {

                        name = task.getResult().getString("Name");
                        image = task.getResult().getString("Image");


                    }
                }
            }
        });












        //comment retrive

        firebaseFirestore.collection("Posts/"+blogpostid+"/Comments").addSnapshotListener(CommentActivity.this,new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(!queryDocumentSnapshots.isEmpty())
                {
                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                        if (doc.getType() == DocumentChange.Type.ADDED) {
                            String commentid = doc.getDocument().getId();
                            Comments comments= doc.getDocument().toObject(Comments.class);
                            commentList.add(comments);
                            commentsRecylerAdapter.notifyDataSetChanged();

                        }
                    }
                }
            }
        });

        // Comment post


        commentsendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String commnetmsg=commenttext.getText().toString();

                if(!commnetmsg.isEmpty())
                {
                    Map<String,Object> commentmap=new HashMap<>();
                    commentmap.put("message",commnetmsg);
                    commentmap.put("currentuserid",currentuserid);
                    commentmap.put("timestamp", FieldValue.serverTimestamp());
                    commentmap.put("image",image);
                    commentmap.put("name",name);
                    firebaseFirestore.collection("Posts/"+blogpostid+"/Comments").add(commentmap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if(!task.isSuccessful())
                            {
                                Toast.makeText(CommentActivity.this, "Error Posting Comment"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            else
                            {  Toast.makeText(CommentActivity.this, "Comment Posted", Toast.LENGTH_SHORT).show();

                                commenttext.setText("");
                            }
                        }
                    });
                }
            }
        });


    }
}
