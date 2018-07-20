package com.android.facefilter;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

public class Preview extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preview_activity);
        ImageView imageView = (ImageView) findViewById(R.id.preview_image);
        Intent intent = getIntent();
        intent.getStringExtra("img");

    }
}
