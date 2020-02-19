package com.intern.myblog;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import io.grpc.Compressor;

public class NewpostActivity extends AppCompatActivity {

    Button newpostbtn;
    EditText newdesctext;
    ImageView addimage;
    Uri postimageuri=null;
    ProgressBar progressBar2;
    FirebaseFirestore firebaseFirestore;
    StorageReference storagerefrence;
    FirebaseAuth mAuth;
    String current_user;
    Compressor compressorimage;
    String downloadurl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newpost);
        addimage=findViewById(R.id.new_post_image);
getSupportActionBar().hide();
        mAuth=FirebaseAuth.getInstance();
        current_user=mAuth.getCurrentUser().getUid();
        storagerefrence=FirebaseStorage.getInstance().getReference();
        firebaseFirestore=FirebaseFirestore.getInstance();
        newdesctext=findViewById(R.id.new_post_desc);
        newpostbtn=findViewById(R.id.post_btn);
        progressBar2=findViewById(R.id.new_post_progress);
        progressBar2.setVisibility(View.INVISIBLE);
        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        addimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent =new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"),1);

            }
        });


        newpostbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String desc=newdesctext.getText().toString();
                if(!TextUtils.isEmpty(desc)&& postimageuri!=null)
                {
                    progressBar2.setVisibility(View.VISIBLE);
                    final String  randomName= UUID.randomUUID().toString();
                    UploadTask filepath=storagerefrence.child("Posts").child(randomName+".jpg").putFile(postimageuri);
                    filepath.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                            if(task.isSuccessful()){

                                task.getResult().getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {

                                        downloadurl=uri.toString();

                                        Map<String,Object>postmap=new HashMap<>();
                                        postmap.put("image_url",downloadurl);
                                        postmap.put("desc",desc);
                                        postmap.put("user",current_user);
                                        postmap.put("timestamp",FieldValue.serverTimestamp());
                                        firebaseFirestore.collection("Posts").add(postmap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentReference> task) {

                                                if(task.isSuccessful())
                                                {
                                                    Toast.makeText(NewpostActivity.this, " Post Is Added",Toast.LENGTH_LONG).show();
                                                    NotificationTask notificationTask = new NotificationTask();
                                                    notificationTask.execute("New Post Found","Check The App For Updates!");



                                                    Intent i=new Intent(NewpostActivity.this,HomeActivity.class);
                                                    startActivity(i);
                                                    finish();


                                                }
                                                else
                                                { String error=task.getException().getMessage();
                                                    Toast.makeText(NewpostActivity.this, " Error:"+error, Toast.LENGTH_LONG).show();

                                                }
                                                progressBar2.setVisibility(View.INVISIBLE);
                                            }
                                        });


                                    }
                                });


                            }
                            else
                            {
                                progressBar2.setVisibility(View.INVISIBLE);
                            }
                        }
                    });

                }
            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 &&resultCode==RESULT_OK && data!=null &&data.getData()!=null)
        {
            postimageuri=data.getData();
          addimage.setImageURI(postimageuri);

        }
    }
    class NotificationTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            try {
                String jsonResponse;

                URL url = new URL("https://onesignal.com/api/v1/notifications");
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                con.setUseCaches(false);
                con.setDoOutput(true);
                con.setDoInput(true);

                con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                con.setRequestProperty("Authorization", "Basic NmU3OTk5YzItMGQxYy00NGNiLWE5ZTgtZTQ0YjgzZDQxOTQ4");
                con.setRequestMethod("POST");
                String strJsonBody = "{"
                        +   "\"app_id\": \"5f38b64d-63d4-4961-b282-049cd6e4af2d\","
                        +   "\"included_segments\": [\"All\"],"
                        +   "\"data\": {\"foo\": \"bar\"},"
                        +   "\"headings\": {\"en\": \"" + strings[0] +"\"},"
                        +   "\"contents\": {\"en\": \"" + strings[1] +"\"},"
                        +   "\"small_icon\":  \"icon\""
                        + "}";

                byte[] sendBytes = strJsonBody.getBytes("UTF-8");
                con.setFixedLengthStreamingMode(sendBytes.length);

                OutputStream outputStream = con.getOutputStream();
                outputStream.write(sendBytes);

                int httpResponse = con.getResponseCode();

                if (  httpResponse >= HttpURLConnection.HTTP_OK
                        && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
                    Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
                    jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                    scanner.close();
                }
                else {
                    Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
                    jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                    scanner.close();
                }
                System.out.println("jsonResponse:\n" + jsonResponse);

            } catch(Throwable t) {
                t.printStackTrace();
            }
            return null;
        }
    }




}
