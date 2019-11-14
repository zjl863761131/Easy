package com.example.easy.ui.score;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.easy.tool.Globe;
import com.example.easy.R;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ShowScoreActivity extends AppCompatActivity {

    private String uploadUrl = "http://114.55.64.152:3000/upload";
//    private String uploadUrlTmp = "http://114.55.64.152:3000/uploadtmp";
//private String uploadUrl = "http://192.168.1.103:3000/upload";
    //private String uploadUrlTmp = "http://192.168.1.103:3000/uploadtmp";
    private String username;
    private String age;
    private String beauty;
    private byte[] fileBuf;
    private String photouri;
    private String uploadFileName;
    private String photopath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_score);
        Intent intent = getIntent();
        age = intent.getStringExtra("age");
        beauty = intent.getStringExtra("beauty");
        photouri = intent.getStringExtra("photouri");
        photopath = intent.getStringExtra("photopath");

        username = Globe.getLoginUser();

        ImageView showimg = (ImageView)findViewById(R.id.showimg);
        TextView score = (TextView)findViewById(R.id.score);
        TextView tage = (TextView)findViewById(R.id.age);
        Button submittocloud = (Button)findViewById(R.id.submittocloud);
        Button returnback = (Button)findViewById(R.id.returnback);

        Glide.with(ShowScoreActivity.this).load(Uri.parse(photouri)).fitCenter().into(showimg);
        score.setText("得分："+ beauty);
        tage.setText("预测年龄："+age);

        submittocloud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputStream inputStream = null;
                try {
                    inputStream = getContentResolver().openInputStream(Uri.parse(photouri));
                    fileBuf = convertToBytes(inputStream);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                uploadFileName = getFileName(photouri);
                upload();
            }
        });

        returnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    //文件上传处理
    public void upload(){
        new Thread(){
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                RequestBody formBody = RequestBody.create(fileBuf, MediaType.parse("image/jpeg"));
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("username",username)
                        .addFormDataPart("filename", uploadFileName)
                        .addFormDataPart("age",age)
                        .addFormDataPart("score",beauty)
                        .addFormDataPart("files", uploadFileName, formBody)
                        .build();
                Request request = new Request.Builder().url(uploadUrl).post(requestBody).build();
                //生成缩略图
//                Bitmap pic = PicCompress.SampleRateCompress(photopath);
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                pic.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//                InputStream isBm = new ByteArrayInputStream(baos .toByteArray());
//                byte[] filebuff = new byte[0];
//                try {
//                    filebuff = convertToBytes(isBm);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                Log.i("原大小", String.valueOf(fileBuf.length));
//                Log.i("压缩后大小", String.valueOf(filebuff.length));
//                String imgbase64 = Base64.encodeToString(filebuff,Base64.DEFAULT);
//                //RequestBody formBodyTmp = RequestBody.create(fileBufTmp, MediaType.parse("image/jpeg"));
//                RequestBody requestBodyTmp = new FormBody.Builder()
//                        .add("username",username)
//                        .add("filename", uploadFileName)
//                        .add("age",age)
//                        .add("score",beauty)
//                        .add("files",imgbase64)
//                        .build();
//                Request requestTmp = new Request.Builder().url(uploadUrlTmp).post(requestBodyTmp).build();
                try {
                    Response response = client.newCall(request).execute();
                    //Response responseTmp = client.newCall(requestTmp).execute();
                    String result = "";
                    //String result2 = "";
                    result = response.body().string();
                    if (result.equals("\"success\"")) {
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ShowScoreActivity.this,"上传成功" , Toast.LENGTH_SHORT).show();
                            }
                        });
                        //Intent intent = new Intent(ShowScoreActivity.this, LoginActivity.class);
                        //startActivity(intent);
                        finish();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @NotNull
    private byte[] convertToBytes(InputStream inputStream)throws Exception{
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int len = 0;
        while((len = inputStream.read(buf)) > 0 ){
            out.write(buf, 0, len);
        }
        out.close();
        inputStream.close();
        return out.toByteArray();
    }

    public String getFileName(@NotNull String pathandname){
        String[] a = pathandname.split("/");
//        int start=pathandname.lastIndexOf("/");
//        int end=pathandname.lastIndexOf(".");
//        if (start!=-1 && end!=-1) {
//            return pathandname.substring(start+1, end);
//        }
//        else {
//            return null;
//        }
//        System.out.println(pathandname);
//        System.out.println(a[a.length-1]);
        return a[a.length-1];
     }
}
