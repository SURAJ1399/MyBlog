package com.intern.myblog;

import com.google.firebase.firestore.Exclude;

import androidx.annotation.NonNull;


public class BlogPostid {


@Exclude
    public String BlogPostid;
    public <T extends  BlogPostid> T withId(@NonNull final String id){
        this.BlogPostid=id;
        return (T)this;
    }
}
