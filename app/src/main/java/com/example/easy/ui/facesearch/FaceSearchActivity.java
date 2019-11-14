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
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.easy.R;
import com.example.easy.tool.FileUtil;
import com.example.easy.tool.Globe;
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

public class FaceSearchActivity extends AppCompatActivity {

    private String access_token;
    private ImageView face_img;
    private Uri photouri;
    private String photopath;
    private String result;
    private String imgbase64;
    private byte[] filebuf;
    private String uploadfilename;
    private String[][] user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            access_token = GetCode.getAuth();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_search);

        face_img = (ImageView)findViewById(R.id.face_img);
        Button face_select_img = (Button)findViewById(R.id.face_select_img);
        Button face_takephoto = (Button)findViewById(R.id.face_takephoto);
        Button face_upload_img = (Button)findViewById(R.id.face_upload_img);
        face_select_img.setOnClickListener(new ButtonListener());
        face_takephoto.setOnClickListener(new ButtonListener());
        face_upload_img.setOnClickListener(new ButtonListener());
    }

    public class ButtonListener implements View.OnClickListener{
        @RequiresApi(api = Build.VERSION_CODES.O)
        public void onClick(View v){
            switch (v.getId()){
                case R.id.face_takephoto:
                    try {
                        takePhoto();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.face_select_img:
                    openGallery();
                    recreate();
                    break;
                case R.id.face_upload_img:
                    String img = UriToBase();
                    try {
                        result = FaceSearch.faceSearch(access_token,img);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                    JSONObject jsonObject = null;
                    try{
                        jsonObject = new JSONObject(result);
                        String result_msg = jsonObject.getString("error_msg");
                        if(result_msg.equals("SUCCESS")){
                            String result1 = jsonObject.getString("result");
                            JSONObject json1 = new JSONObject(result1);
                            if(result1.contains("face_num")){
                                String face_num = json1.getString("face_num");
                                String face_list = json1.getString("face_list");
                                JSONArray json2 = new JSONArray(face_list);
                                int length1 = json2.length();
                                user = new String[length1][4];
                                String string = "";
                                for (int i=0;i<length1;i++){
                                    string = json2.getString(i);
                                    JSONObject json3 = new JSONObject(string);
                                    String user_list = json3.getString("user_list");
                                    JSONArray json4 = new JSONArray(user_list);
                                    String string5 = json4.getString(0);
                                    JSONObject json5 = new JSONObject(string5);
                                    String group_id = json5.getString("group_id");
                                    String user_id = json5.getString("user_id");
                                    String user_info = json5.getString("user_info");
                                    String score = json5.getString("score");
                                    user[i][0] = group_id;
                                    user[i][1] = user_id;
                                    user[i][2] = user_info;
                                    user[i][3] = score;
                                }
                            }else {
                                String user_list = json1.getString("user_list");
                                JSONArray json2 = new JSONArray(user_list);
                                String userlist = json2.getString(0);
                                JSONObject json3 = new JSONObject(userlist);
                                String group_id = json3.getString("group_id");
                                String user_id = json3.getString("user_id");
                                String user_info = json3.getString("user_info");
                                String score = json3.getString("score");
                                user = new  String[1][4];
                                user[0][0] = group_id;
                                user[0][1] = user_id;
                                user[0][2] = user_info;
                                user[0][3] = score;
                            }
                            Globe.setUserSearch(user);
                        }else {
                            Toast.makeText(FaceSearchActivity.this, "出错，请跟换图片试试", Toast.LENGTH_SHORT).show();
                            break;
                        }
                        Intent intent = new Intent(FaceSearchActivity.this, FaceResultShowActivity.class);
                        startActivity(intent);
                    }catch (JSONException e){
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
        File imgtem = new File(FaceSearchActivity.this.getExternalCacheDir(), uploadfilename);
        imgtem.createNewFile();
        if(Build.VERSION.SDK_INT >= 24){
            photouri = FileProvider.getUriForFile(FaceSearchActivity.this, ".fileprovider", imgtem);
            photopath = imgtem.getPath();
        }
        else{
            photouri = Uri.fromFile(imgtem);
            photopath = imgtem.getPath();
        }
        ActivityCompat.requestPermissions(FaceSearchActivity.this, new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
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
                    Toast.makeText(FaceSearchActivity.this, "读取相册被拒绝", Toast.LENGTH_LONG).show();
                }
        }
    }

    //活动响应
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case 1:
                if(resultCode == FaceSearchActivity.this.RESULT_OK){
                    show_picture();
                }
                break;
            case 2:
                if(resultCode == FaceSearchActivity.this.RESULT_OK){
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
        cursor = FaceSearchActivity.this.getContentResolver().query(uri, null, null, null, null);
        if(cursor.moveToFirst()){
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
            uploadfilename = cursor.getString(columnIndex);
            photopath = FileUtil.getFilePathByUri(FaceSearchActivity.this, photouri);
        }
        try{
            //Bitmap pic = PicCompress.SampleRateCompress(photopath);
            Glide.with(FaceSearchActivity.this).load(photouri)
                    .fitCenter()
                    .into(face_img);
        }catch (Exception e){
            e.printStackTrace();
        }
        cursor.close();
    }

    //照片显示
    private void show_picture(){
        //Uri uri = intent.getData();
        try{
            Glide.with(FaceSearchActivity.this)
                    .load(photouri)
                    .fitCenter()
                    .into(face_img);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //base64转换
    @RequiresApi(api = Build.VERSION_CODES.O)
    public String UriToBase(){
        Bitmap bitmap = null;
        try {
            InputStream inputStream = FaceSearchActivity.this.getContentResolver().openInputStream(photouri);
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
