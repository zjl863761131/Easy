package com.example.easy.ui.facesearch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.easy.MainActivity;
import com.example.easy.R;
import com.example.easy.utils.FaceAdd;

public class FaceMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_main);
        Button face_add = (Button)findViewById(R.id.face_add);
        Button face_search = (Button)findViewById(R.id.face_search);
        Button face_match = (Button)findViewById(R.id.face_match);
        Button face_back = (Button)findViewById(R.id.face_back);


        face_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FaceMainActivity.this, FaceAddActivity.class);
                startActivity(intent);
            }
        });

        face_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FaceMainActivity.this, FaceSearchActivity.class);
                startActivity(intent);
            }
        });

        face_match.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FaceMainActivity.this,FaceMatchActivity .class);
                startActivity(intent);
            }
        });

        face_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FaceMainActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
