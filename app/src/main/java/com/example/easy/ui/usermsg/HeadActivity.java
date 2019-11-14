package com.example.easy.ui.usermsg;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.easy.tool.Globe;
import com.example.easy.R;
import com.example.easy.tool.FileUtil;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HeadActivity extends AppCompatActivity {

    private Uri photouri;
    public String photopath;
    private String username = Globe.getLoginUser();
    private ImageView headimg;
    private String uploadfilename;
    private byte[] fileBuf;
    //private String uploadheaduri = "http://192.168.1.103:3000/changehead";
    private String uploadheaduri = "http://114.55.64.152:3000/changehead";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_head);
        headimg = (ImageView)findViewById(R.id.head_img);
        Button selectheadimg = (Button)findViewById(R.id.selectheadimg);
        Button takeheadphoto = (Button)findViewById(R.id.takeheadphoto);
        Button uploadheadimg = (Button)findViewById(R.id.uploadheadimg);
        selectheadimg.setOnClickListener(new ButtonListener());
        takeheadphoto.setOnClickListener(new ButtonListener());
        uploadheadimg.setOnClickListener(new ButtonListener());
    }

    private class ButtonListener implements View.OnClickListener{
        @RequiresApi(api = Build.VERSION_CODES.O)
        public void onClick(View v){
            switch(v.getId()){
                case R.id.takeheadphoto:
                    try {
                        takePhoto();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.selectheadimg:
                    openGallery();
                    recreate();
                    break;
                case R.id.uploadheadimg:
                    InputStream inputStream = null;
                    try {
                        inputStream = getContentResolver().openInputStream(photouri);
                        fileBuf = convertToBytes(inputStream);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    uploadfilename = getFileName(photopath);
                    upload();
                    break;
                default:
                    break;
            }
        }
    }
    //照相
    public void takePhoto() throws Exception{
        SimpleDateFormat timeoff = new SimpleDateFormat("yyyyMMDDHHmmss");
        String filetime = timeoff.format(new Date()).toString();
        String filename = username + filetime + ".jpg";
        uploadfilename = filename;
        File imgtem = new File(getExternalCacheDir(), uploadfilename);
        imgtem.createNewFile();
        if(Build.VERSION.SDK_INT >= 24){
            photouri = FileProvider.getUriForFile(HeadActivity.this, ".fileprovider", imgtem);
            photopath = imgtem.getPath();
        }
        else{
            photouri = Uri.fromFile(imgtem);
            photopath = imgtem.getPath();
        }
        ActivityCompat.requestPermissions(HeadActivity.this, new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
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
                    Toast.makeText(HeadActivity.this, "读取相册被拒绝", Toast.LENGTH_LONG).show();
                }
        }
    }

    //活动响应
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case 1:
                if(resultCode == RESULT_OK){
//                    try {
//                        Bitmap map = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(photouri));
//                        scoreimg.setImageBitmap(map);
//                        data.setData(photouri);
                    show_picture();
//                    }catch (FileNotFoundException e){
//                        e.printStackTrace();
//                    }
                }
                break;
            case 2:
                if(resultCode == RESULT_OK){
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
        cursor =getContentResolver().query(uri, null, null, null, null);
        if(cursor.moveToFirst()){
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
            uploadfilename = cursor.getString(columnIndex);
            photopath = FileUtil.getFilePathByUri(HeadActivity.this, photouri);
        }
        try{
            //Bitmap pic = PicCompress.SampleRateCompress(photopath);
            Glide.with(HeadActivity.this).load(photouri)
                    .fitCenter()
                    .into(headimg);
        }catch (Exception e){
            e.printStackTrace();
        }
        cursor.close();
    }

    //照片显示
    private void show_picture(){
        //Uri uri = intent.getData();
        try{
//            Glide.with(getActivity())
//                    .load(photouri)
//                    .fitCenter()
//                    .into(scoreimg);
            //Bitmap pic = PicCompress.SampleRateCompress(photopath);
            Glide.with(HeadActivity.this)
                    .load(photouri)
                    .fitCenter()
                    .into(headimg);
        }catch (Exception e){
            e.printStackTrace();
        }
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

    //
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
                        .addFormDataPart("files", uploadfilename, formBody)
                        .build();
                Request request = new Request.Builder().url(uploadheaduri).post(requestBody).build();
                try {
                    Response response = client.newCall(request).execute();
                    String result = "";
                    result = response.body().string();
                    if (result.equals("\"success\"")) {
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(HeadActivity.this,"上传成功" , Toast.LENGTH_SHORT).show();
                            }
                        });
//                        Intent intent = new Intent(HeadActivity.this, UserMsgFragment.class);
//                        startActivity(intent);
                        finish();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    //获取文件名
    public String getFileName(@NotNull String pathandname){
        String[] a = pathandname.split("/");
        return a[a.length-1];
    }
}
