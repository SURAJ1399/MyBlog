package com.intern.myblog;

import android.content.Context;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {

    List<Comments> commentList;
    Context context;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    String userid;





    public CommentsAdapter(List<Comments> commentsList)
    {
        this.commentList=commentsList;
    }
    @NonNull
    @Override
    public CommentsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_list_item,parent,false);
                context=parent.getContext();
                firebaseFirestore= FirebaseFirestore.getInstance();
                firebaseAuth= FirebaseAuth.getInstance();
                userid=firebaseAuth.getCurrentUser().getUid();

        return new CommentsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentsAdapter.ViewHolder holder, int position) {
        String commentmessage=commentList.get(position).getMessage();
        holder.setcomment(commentmessage);
        holder.username.setText(commentList.get(position).getName());
        String imageurl=commentList.get(position).getImage();
        if(!TextUtils.isEmpty(imageurl))
        Glide.with(context).load(imageurl).into(holder.userimage);
        else
            holder.userimage.setImageResource(R.drawable.profile);




    }

    @Override
    public int getItemCount() {
        if(commentList!=null)
        {
            return commentList.size();
        }
        else
        {
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View mview;
        public TextView commentmessage;
        ImageView userimage;
        TextView username;





        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mview=itemView;
            userimage=mview.findViewById(R.id.comment_image);
            username=mview.findViewById(R.id.comment_username);

        }

        public void setcomment (String message)
        {
            commentmessage=mview.findViewById(R.id.comment_message);
            commentmessage.setText(message);
        }
    }
}
