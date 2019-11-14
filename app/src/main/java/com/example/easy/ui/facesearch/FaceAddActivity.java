package com.example.easy.ui.facesearch;

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
import android.provider.MediaStore;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.easy.R;
import com.example.easy.tool.FileUtil;
import com.example.easy.tool.Globe;
import com.example.easy.utils.FaceAdd;
import com.example.easy.utils.FaceSearch;
import com.example.easy.utils.GetCode;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FaceAddActivity extends AppCompatActivity {

    private String access_token;
    private ImageView faceadd_img;
    private Uri photouri;
    private String photopath;
    private String result;
    private String imgbase64 = "";
    private byte[] filebuf;
    private String uploadfilename;
    private String userid = "";
    private String userinfo = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            access_token = GetCode.getAuth();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_add);

        faceadd_img = (ImageView)findViewById(R.id.faceadd_img);
        Button faceadd_select_img = (Button)findViewById(R.id.faceadd_select_img);
        Button faceadd_takephoto = (Button)findViewById(R.id.faceadd_takephoto);
        Button faceadd_upload_img = (Button)findViewById(R.id.faceadd_upload_img);
        EditText faceadd_userid = (EditText)findViewById(R.id.faceadd_userid);
        EditText faceadd_userinfo = (EditText)findViewById(R.id.faceadd_userinfo);
        faceadd_select_img.setOnClickListener(new ButtonListener());
        faceadd_takephoto.setOnClickListener(new ButtonListener());
        faceadd_upload_img.setOnClickListener(new ButtonListener());
        faceadd_userid.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                userid = faceadd_userid.getText().toString();
                return false;
            }
        });
        faceadd_userinfo.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                userinfo = faceadd_userinfo.getText().toString();
                return false;
            }
        });
    }

    public class ButtonListener implements View.OnClickListener{
        @RequiresApi(api = Build.VERSION_CODES.O)
        public void onClick(View v){
            switch (v.getId()){
                case R.id.faceadd_takephoto:
                    try {
                        takePhoto();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.faceadd_select_img:
                    openGallery();
                    recreate();
                    break;
                case R.id.faceadd_upload_img:
                    UriToBase();
                    try {
                        if(userid == ""|| imgbase64 == ""){
                            Toast.makeText(FaceAddActivity.this,"用户组或图片不能为空", Toast.LENGTH_SHORT).show();
                        }else {
                        result = FaceAdd.add(access_token,imgbase64,userinfo,userid);}
                        JSONObject jsonObject = new JSONObject(result);
                        String result_msg = jsonObject.getString("error_msg");
                        if(result_msg.equals("SUCCESS")){
                            Toast.makeText(FaceAddActivity.this, "上传成功",Toast.LENGTH_SHORT).show();
                            finish();
                        }else {
                            Toast.makeText(FaceAddActivity.this,"出错", Toast.LENGTH_SHORT).show();
                        }
                    }catch (JSONException e){
                        e.printStackTrace();
                    } catch (InterruptedException e){
                        e.printStackTrace();
                    }
            }
        }
    }

    //照相
    public void takePhoto() throws Exception{
        SimpleDateFormat timeoff = new SimpleDateFormat("yyyyMMDDHHmmss");
        String filetime = timeoff.format(new Date()).toString();
        String filename = filetime + ".jpg";
        uploadfilename = filename;
        File imgtem = new File(FaceAddActivity.this.getExternalCacheDir(), uploadfilename);
        imgtem.createNewFile();
        if(Build.VERSION.SDK_INT >= 24){
            photouri = FileProvider.getUriForFile(FaceAddActivity.this, ".fileprovider", imgtem);
            photopath = imgtem.getPath();
        }
        else{
            photouri = Uri.fromFile(imgtem);
            photopath = imgtem.getPath();
        }
        ActivityCompat.requestPermissions(FaceAddActivity.this, new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photouri);
        startActivityForResult(intent, 1);
    }

    //权限请求
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    openGallery();
                }else{
                    Toast.makeText(FaceAddActivity.this, "读取相册被拒绝", Toast.LENGTH_LONG).show();
                }
        }
    }

    //活动响应
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case 1:
                if(resultCode == FaceAddActivity.this.RESULT_OK){
                    show_picture();
                }
                break;
            case 2:
                if(resultCode == FaceAddActivity.this.RESULT_OK){
                    HandleSelect(data);
                }
                break;
            default:
                break;
        }
    }

    //打开相册进行照片选取
    public void openGallery(){
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, 2);
    }

    //选取照片后的读取工作
    private void HandleSelect(@NotNull Intent intent){
        Cursor cursor = null;
        Uri uri = intent.getData();
        photouri = uri;
        cursor = FaceAddActivity.this.getContentResolver().query(uri, null, null, null, null);
        if(cursor.moveToFirst()){
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
            uploadfilename = cursor.getString(columnIndex);
            photopath = FileUtil.getFilePathByUri(FaceAddActivity.this, photouri);
        }
        try{
            //Bitmap pic = PicCompress.SampleRateCompress(photopath);
            Glide.with(FaceAddActivity.this).load(photouri)
                    .fitCenter()
                    .into(faceadd_img);
        }catch (Exception e){
            e.printStackTrace();
        }
        cursor.close();
    }

    //照片显示
    private void show_picture(){
        //Uri uri = intent.getData();
        try{
            Glide.with(FaceAddActivity.this)
                    .load(photouri)
                    .fitCenter()
                    .into(faceadd_img);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //base64转换
    @RequiresApi(api = Build.VERSION_CODES.O)
    public String UriToBase(){
        Bitmap bitmap = null;
        try {
            InputStream inputStream = FaceAddActivity.this.getContentResolver().openInputStream(photouri);
            filebuf = convertToBytes(inputStream);
            imgbase64 = Base64.encodeToString(filebuf,Base64.DEFAULT);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imgbase64;
    }

    //将输入流转换成字节数组
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
}
