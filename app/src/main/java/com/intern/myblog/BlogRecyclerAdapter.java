package com.intern.myblog;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nullable;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class BlogRecyclerAdapter extends RecyclerView.Adapter<BlogRecyclerAdapter.ViewHolder> {

    private static final int STORAGE_PERMISSION_CODE =1 ;
    public List<BlogPost> blog_list;
    Context context;
    public String blogpostid1;
    FirebaseAuth mAuth;
    String postid;
    private FirebaseFirestore firebaseFirestore;
    BlogRecyclerAdapter blogRecyclerAdapter;
    String Userimage;
    String userid2;
     DocumentReference docref;





    public BlogRecyclerAdapter(List<BlogPost> blog_list, String userid2) {
        this.blog_list = blog_list;
        this.userid2=userid2;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewtype) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bloglistitem, parent, false);
        context = parent.getContext();
        firebaseFirestore= FirebaseFirestore.getInstance();

        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final String blogpostid=blog_list.get(position).BlogPostid;
        blogpostid1=blogpostid;
        mAuth= FirebaseAuth.getInstance();

        holder.progressBar.setVisibility(View.VISIBLE);
        final String currentussrid= FirebaseAuth.getInstance().getCurrentUser().getUid();
        final String descdata = blog_list.get(position).getDesc();
        holder.setdesctext(descdata);
        final String imageurl = blog_list.get(position).getImage_url();
        holder.setblogimage(imageurl);

        docref  = firebaseFirestore.collection("Posts").document(blogpostid1);



        holder.progressBar.setVisibility(View.VISIBLE);


        //Getlikescount

        firebaseFirestore.collection("Posts").document(blogpostid).collection("Likes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(!queryDocumentSnapshots.isEmpty()){
                    int count=queryDocumentSnapshots.size();
                    holder.getlikescount(count);
                }
                else
                {
                    holder.getlikescount(0);
                }
            }
        });

        /// Comments Count
        firebaseFirestore.collection("Posts").document(blogpostid).collection("Comments").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(!queryDocumentSnapshots.isEmpty()){
                    int count=queryDocumentSnapshots.size();
                    holder.Commentscount.setText("View"+" "+"All"+" "+count+" "+"Comments");
                }
                else
                {
                    holder.Commentscount.setText("View"+" "+"All"+" "+0+" "+"Comments");
                }
            }
        });

        // On Click Comments to View

         holder.Commentscount.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent intent=new Intent(context,CommentActivity.class);    intent.putExtra("blogpostid",blogpostid);
                 context.startActivity(intent);

             }
         });





//Getlikes

        firebaseFirestore.collection("Posts").document(blogpostid).collection("Likes").document(currentussrid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @SuppressLint("NewApi")
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshot.exists())
                {
                    holder.bloglike.setImageDrawable(context.getDrawable(R.drawable.likeclr));
                }
                else {
                    holder.bloglike.setImageDrawable(context.getDrawable(R.drawable.likebw));
                }
            }
        });

        //likefeatures


        holder.bloglike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseFirestore.collection("Posts").document(blogpostid).collection("Likes").document(currentussrid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(!task.getResult().exists())
                        {
                            Map<String, Object> likemap=new HashMap<>();
                            likemap.put("timestamp", FieldValue.serverTimestamp());
                            firebaseFirestore.collection("Posts").document(blogpostid).collection("Likes").document(currentussrid).set(likemap);

                        }
                        else
                        {
                            firebaseFirestore.collection("Posts").document(blogpostid).collection("Likes").document(currentussrid).delete();
                        }
                    }
                });

            }
        });

        ///Commentsection

        holder.commentpagebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(context,CommentActivity.class);
                i.putExtra("blogpostid",blogpostid);
                context.startActivity(i);
            }
        });
holder.share.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        String a=blog_list.get(position).getImage_url();

        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,STORAGE_PERMISSION_CODE,a);







      }
});












        //timefeatures
//        long millisec = blog_list.get(position).getTimestamp().getTime();
//
//        //timestamp to time convert
//        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
//        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
//        cal.setTimeInMillis(millisec);
//        holder.setTime(formatter.format(cal.getTime()));

        ///postimage

        final String userid = blog_list.get(position).getUser();
        firebaseFirestore.collection("Users").document(userid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    String Username = task.getResult().getString("Name");
                    holder.setuserdata(Username);
                     Userimage = task.getResult().getString("Image");
                    holder.setuserimage(Userimage);



                } else {
                    //Error Handling;
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return blog_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView bloglike;
        TextView descview;
        ImageView blogimageview;
        ImageView delete;
        View mview;
        ImageView commentpagebtn;
        Context context1;
        private TextView blogdate;
        TextView blogusername;
        CircleImageView bloguserimage;
        TextView bloglikecount;
        ProgressBar progressBar;
        ImageView share;
        TextView Commentscount;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mview = itemView;
            bloglike=mview.findViewById(R.id.bloglike);
            commentpagebtn=mview.findViewById(R.id.commentpagebtn);
            bloglikecount=mview.findViewById(R.id.bloglikescount);
share=mview.findViewById(R.id.share);
            progressBar=mview.findViewById(R.id.progressBar5);
            Commentscount=mview.findViewById(R.id.commentcount);

        }

        public void setdesctext(String desctext) {
            descview = mview.findViewById(R.id.blogdesc);
            descview.setText(desctext);
        }

        public void setblogimage(String downloaduri) {
            blogimageview = mview.findViewById(R.id.blogimageview);
            Glide.with(context).load(downloaduri).into(blogimageview);

        }

        public void setTime(String date) {
            blogdate = mview.findViewById(R.id.blogdate);
            blogdate.setText(date);
        }


        public void setuserdata(String Name){


            blogusername=mview.findViewById(R.id.blogusername);
            blogusername.setText(Name);


        }
        public void setuserimage(String Image)
        {
            bloguserimage=mview.findViewById(R.id.bloguserimage);
            Glide.with(context).load(Image).into(bloguserimage);
            progressBar.setVisibility(View.INVISIBLE);
        }
        public void getlikescount(int count)
        {

            bloglikecount.setText(count+" "+"Likes");
        }





    }
    public void checkPermission(String permission, int requestCode,String a)
    {
        if (ContextCompat.checkSelfPermission(context, permission)
                == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions((Activity) context,
                    new String[] { permission },
                    requestCode);
        }
        else {
            Toast.makeText(context,
                    "Permission already granted",
                    Toast.LENGTH_SHORT)
                    .show();
            Glide.with(context)
                    .asBitmap()
                    .load(a)
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.AUTOMATIC))

                    .into(new SimpleTarget<Bitmap>(250, 250) {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @androidx.annotation.Nullable Transition<? super Bitmap> transition) {
                            Intent intent = new Intent(Intent.ACTION_SEND);
                            intent.putExtra(Intent.EXTRA_TEXT, "https://raw.githubusercontent.com/SURAJ1399/MyBlog/master/MyBlog.apk");
                            String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), resource, "", null);
                            Log.i("quoteswahttodo", "is onresoursereddy" + path);

                            Uri screenshotUri = Uri.parse(path);

                            //Log.i("quoteswahttodo", "is onresoursereddy" + screenshotUri);

                            intent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
                            intent.setType("image/*");

                            context.startActivity(Intent.createChooser(intent, "Share image via..."));


                        }



                    }) ;
        }
    }




    // This function is called when the user accepts or decline the permission.
    // Request Code is used to check which permission called this function.
    // This request code is provided when the user is prompt for permission.




}