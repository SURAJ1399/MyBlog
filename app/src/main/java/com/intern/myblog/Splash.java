package com.intern.myblog;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;



import java.util.Timer;
import java.util.TimerTask;

public class Splash extends AppCompatActivity {
    ImageView imageView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
     //   myApplication = MyApplication.getInstance();
        getSupportActionBar().hide();

        imageView = (ImageView)findViewById(R.id.logo);
        //  imageView.startAnimation(medalAnimation);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Intent intent=new Intent(Splash.this,LoginActivity.class);

                startActivity(intent);
                finish();
            }
        }, 2000);


    }
}
