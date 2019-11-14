package com.example.easy.ui.facesearch;

import com.bumptech.glide.Glide;
import com.example.easy.R;
import com.example.easy.tool.FileUtil;
import com.example.easy.utils.FaceMatch;
import com.example.easy.utils.GetCode;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FaceMatchActivity extends AppCompatActivity {

    private ImageView img1;
    private ImageView img2;
    private String access_token;
    private Uri photouri1 = null;
    private Uri photouri2 = null;
    private String result;
    private String imgbase64_1 = "";
    private String imgbase64_2 = "";
    private byte[] filebuf1;
    private byte[] filebuf2;
    private int RequestCode = 1;
    private TextView text2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            access_token = GetCode.getAuth();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_match);

        img1 = (ImageView) findViewById(R.id.img1);
        img2 = (ImageView) findViewById(R.id.img2);

        text2 = (TextView) findViewById(R.id.text2);

        Button button1 = (Button) findViewById(R.id.button1);
        Button button2 = (Button) findViewById(R.id.button2);
        Button button3 = (Button) findViewById(R.id.button3);
        Button button4 = (Button) findViewById(R.id.button4);
        Button button5 = (Button) findViewById(R.id.button5);
        Button back = (Button) findViewById(R.id.back);

        button1.setOnClickListener(new ButtonListener());
        button2.setOnClickListener(new ButtonListener());
        button3.setOnClickListener(new ButtonListener());
        button4.setOnClickListener(new ButtonListener());
        button5.setOnClickListener(new ButtonListener());
        back.setOnClickListener(new ButtonListener());


    }

    public class ButtonListener implements View.OnClickListener {
        @RequiresApi(api = Build.VERSION_CODES.O)
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.button1:
                    RequestCode = 1;
                    try {
                        takePhoto1();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.button2:
                    RequestCode = 2;
                    try {
                        takePhoto2();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.button3:
                    RequestCode = 3;
                    openGallery1();
                    break;
                case R.id.button4:
                    RequestCode = 4;
                    openGallery2();
                    break;
                case R.id.button5:
                    RequestCode = 5;
                    UriToBase1();
                    UriToBase2();
                    try {
                        if (imgbase64_1 == "" || imgbase64_2 == "") {
                            Toast.makeText(FaceMatchActivity.this, "图片不能为空", Toast.LENGTH_SHORT).show();
                        } else {
                            result = FaceMatch.faceMatch(access_token, imgbase64_1, imgbase64_2);
                        }
                        JSONObject jsonObject = new JSONObject(result);
                        String result_msg = jsonObject.getString("error_msg");
                        if (result_msg.equals("SUCCESS")) {
                            String result = jsonObject.getString("result");
                            JSONObject result1 = new JSONObject(result);
                            String score = result1.getString("score");
                            int i = Integer.parseInt( score );
                            if(i > 80){
                                text2.setText("是同一人，相似度为：" + score);
                            }else {
                                text2.setText("不是同一人，相似度仅为：" + score);
                            }
                            reset();
                        } else {
                            Toast.makeText(FaceMatchActivity.this, "出错", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.back:
                    finish();
                    break;
            }
        }
    }

    //照相
    public void takePhoto1() throws Exception {
        SimpleDateFormat timeoff = new SimpleDateFormat("yyyyMMDDHHmmss");
        String filetime = timeoff.format(new Date()).toString();
        String filename = filetime + ".jpg";
        File imgtem = new File(FaceMatchActivity.this.getExternalCacheDir(), filename);
        imgtem.createNewFile();
        if (Build.VERSION.SDK_INT >= 24) {
            photouri1 = FileProvider.getUriForFile(FaceMatchActivity.this, ".fileprovider", imgtem);
        } else {
            photouri1 = Uri.fromFile(imgtem);
        }
        ActivityCompat.requestPermissions(FaceMatchActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, RequestCode);
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photouri1);
        startActivityForResult(intent, RequestCode);
    }

    //相机2
    public void takePhoto2() throws Exception {
        SimpleDateFormat timeoff = new SimpleDateFormat("yyyyMMDDHHmmss");
        String filetime = timeoff.format(new Date()).toString();
        String filename = filetime + ".jpg";
        File imgtem = new File(FaceMatchActivity.this.getExternalCacheDir(), filename);
        imgtem.createNewFile();
        if (Build.VERSION.SDK_INT >= 24) {
            photouri2 = FileProvider.getUriForFile(FaceMatchActivity.this, ".fileprovider", imgtem);
        } else {
            photouri2 = Uri.fromFile(imgtem);
        }
        ActivityCompat.requestPermissions(FaceMatchActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, RequestCode);
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photouri2);
        startActivityForResult(intent, RequestCode);
    }

    //权限请求
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 3:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openGallery1();
                } else {
                    Toast.makeText(FaceMatchActivity.this, "读取相册被拒绝", Toast.LENGTH_LONG).show();
                }
                break;
            case 4:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openGallery2();
                } else {
                    Toast.makeText(FaceMatchActivity.this, "读取相册被拒绝", Toast.LENGTH_LONG).show();
                }
                break;

        }
    }

    //打开相册进行照片选取
    public void openGallery1() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, RequestCode);
    }

    public void openGallery2() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, RequestCode);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == FaceMatchActivity.this.RESULT_OK) {
                    show_picture(photouri1, img1);
                }
                break;
            case 2:
                if (resultCode == FaceMatchActivity.this.RESULT_OK) {
                    show_picture(photouri2, img2);
                }
                break;
            case 3:
                if (resultCode == FaceMatchActivity.this.RESULT_OK) {
                    HandleSelect1(data);
                    show_picture(photouri1, img1);
                }
                break;
            case 4:
                if (resultCode == FaceMatchActivity.this.RESULT_OK) {
                    HandleSelect2(data);
                    show_picture(photouri2, img2);
                }
                break;
            default:
                break;
        }
    }

    private void show_picture(Uri photouri, ImageView img) {
        //Uri uri = intent.getData();
        try {
            Glide.with(FaceMatchActivity.this)
                    .load(photouri)
                    .fitCenter()
                    .into(img);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //相册处理
    private void HandleSelect1(@NotNull Intent intent) {
        Cursor cursor = null;
        Uri uri = intent.getData();
        photouri1 = uri;
        cursor = FaceMatchActivity.this.getContentResolver().query(uri, null, null, null, null);
        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
        }
        cursor.close();
    }

    //相册处理2
    private void HandleSelect2(@NotNull Intent intent) {
        Cursor cursor = null;
        Uri uri = intent.getData();
        photouri2 = uri;
        cursor = FaceMatchActivity.this.getContentResolver().query(uri, null, null, null, null);
        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
        }
        cursor.close();
    }

    //base64转换
    @RequiresApi(api = Build.VERSION_CODES.O)
    public String UriToBase1() {
        Bitmap bitmap = null;
        try {
            InputStream inputStream = FaceMatchActivity.this.getContentResolver().openInputStream(photouri1);
            filebuf1 = convertToBytes(inputStream);
            imgbase64_1 = Base64.encodeToString(filebuf1, Base64.DEFAULT);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imgbase64_1;
    }

    //base64转换
    @RequiresApi(api = Build.VERSION_CODES.O)
    public String UriToBase2() {
        Bitmap bitmap = null;
        try {
            InputStream inputStream = FaceMatchActivity.this.getContentResolver().openInputStream(photouri2);
            filebuf2 = convertToBytes(inputStream);
            imgbase64_2 = Base64.encodeToString(filebuf2, Base64.DEFAULT);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imgbase64_2;
    }

    //将输入流转换成字节数组
    @NotNull
    private byte[] convertToBytes(InputStream inputStream) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int len = 0;
        while ((len = inputStream.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        out.close();
        inputStream.close();
        return out.toByteArray();
    }

    public void reset(){
        photouri1 = null;
        photouri2 = null;
        imgbase64_1 = "";
        imgbase64_2 = "";
        RequestCode = 1;
    }
}
