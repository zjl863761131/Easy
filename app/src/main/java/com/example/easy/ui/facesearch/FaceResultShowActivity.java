package com.example.easy.ui.facesearch;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.easy.R;
import com.example.easy.tool.Globe;

public class FaceResultShowActivity extends AppCompatActivity {

    public String[][] UserSearch = Globe.getUserSearch();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_result_show);
        TextView face_result_show= (TextView)findViewById(R.id.face_result_show);
        int length = UserSearch.length;
        String text = "共有"+length+"个人，其信息分别如下：\n";
        for (int i=0; i<length; i++){
            text += "第"+(i+1)+"人：\n"
                    +"组号："+(UserSearch[i][0]+1)+"\n"
                    +"用户："+UserSearch[i][1]+"\n"
                    +"用户信息："+UserSearch[i][2]+"\n"
                    +"相似度："+UserSearch[i][3]+"\n\n";
        }
        face_result_show.setText(text);
    }
}
