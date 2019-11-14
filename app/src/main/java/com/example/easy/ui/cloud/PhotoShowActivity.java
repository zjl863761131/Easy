package com.example.easy.ui.cloud;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.easy.R;

public class PhotoShowActivity extends AppCompatActivity {

    public String username = null;
    public String filename = null;
    //public String filepath = null;
    public String uploadtime = null;
    public String age = null;
    public String score = null;
    public String path = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_show);

        Intent intent = getIntent();
        age = intent.getStringExtra("age");
        score = intent.getStringExtra("score");
        username = intent.getStringExtra("username");
        path = intent.getStringExtra("path");
        uploadtime = intent.getStringExtra("uploadtime");
        filename = intent.getStringExtra("filename");

        ImageView photo_show_img = (ImageView)findViewById(R.id.photo_show_img);
        TextView photo_show_text = (TextView)findViewById(R.id.photo_show_text);
        Button photo_show_button = (Button)findViewById(R.id.photo_show_button);

        Glide.with(PhotoShowActivity.this).load(path).fitCenter().into(photo_show_img);
        photo_show_text.setText("照片名：" + filename + "\n" + "上传者：" + username + "\n" + "评分：" + score + "\n" + "年龄" + age + "\n" + "上传时间：" + uploadtime);
        photo_show_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
