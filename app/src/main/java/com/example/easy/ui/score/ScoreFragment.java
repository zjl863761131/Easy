package com.example.easy.ui.score;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.easy.utils.FaceDetect;
import com.example.easy.utils.GetCode;
import com.example.easy.tool.Globe;
import com.example.easy.tool.PicCompress;
import com.example.easy.R;
import com.example.easy.tool.FileUtil;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import android.util.Base64;

import java.util.Date;

public class ScoreFragment extends Fragment {
    private String access_token;
    private String username = Globe.getLoginUser();
    private ImageView scoreimg;
    private Uri photouri;
    private String uploadfilename;
    private String result;
    private String imgbase64;
    private byte[] filebuf;
    private String age;
    private String beauty;
    private String photopath;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            access_token = GetCode.getAuth();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        super.onCreate(savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_score, container, false);
        scoreimg = root.findViewById(R.id.score_img);
        Button selectimg = root.findViewById(R.id.selectimg);
        Button takephoto = root.findViewById(R.id.takephoto);
        Button uploadimg = root.findViewById(R.id.uploadimg);
        selectimg.setOnClickListener(new ButtonListener());
        takephoto.setOnClickListener(new ButtonListener());
        uploadimg.setOnClickListener(new ButtonListener());
        return root;
    }

    //按钮监听
    private class ButtonListener implements View.OnClickListener{
        @RequiresApi(api = Build.VERSION_CODES.O)
        public void onClick(View v){
            switch(v.getId()){
                case R.id.takephoto:
                    try {
                        takePhoto();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.selectimg:
                    openGallery();
                    getActivity().recreate();
                    break;
                case R.id.uploadimg:
                    String img = UriToBase();
                    try {
                        result = FaceDetect.faceDetect(access_token, img);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(result);
                        String result1 = jsonObject.getString("result");
                        JSONObject json1 = new JSONObject(result1);
                        String face_list1 = json1.getString("face_list");
                        JSONArray json2 = new JSONArray(face_list1);
                        int length1 = json2.length();
                        String string = "";
                        for(int n=0;n<length1;n++) {
                            string = json2.getString(n);
                         }
                        JSONObject json3 = new JSONObject(string);
                        //String face_shape = json3.getString("face_shape");
                        //String face_type = json3.getString("face_type");
                        String location = json3.getString("location");
                        String angle = json3.getString("angle");
                        beauty = json3.getString("beauty");
                        age = json3.getString("age");
                        String face_probability = json3.getString("face_probability");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (beauty == null){
                        Toast.makeText(getActivity(),"上传的图片不是人脸，请重新上传",Toast.LENGTH_SHORT).show();
                        break;
                    }
                    Intent intent = new Intent(getActivity(), ShowScoreActivity.class);
                    intent.putExtra("age",age);
                    intent.putExtra("beauty",beauty);
                    intent.putExtra("photouri",photouri.toString());
                    intent.putExtra("photopath", photopath);
                    startActivity(intent);
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
        File imgtem = new File(getActivity().getExternalCacheDir(), uploadfilename);
        imgtem.createNewFile();
        if(Build.VERSION.SDK_INT >= 24){
            photouri = FileProvider.getUriForFile(getContext(), ".fileprovider", imgtem);
            photopath = imgtem.getPath();
        }
        else{
            photouri = Uri.fromFile(imgtem);
            photopath = imgtem.getPath();
        }
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
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
                    Toast.makeText(getActivity(), "读取相册被拒绝", Toast.LENGTH_LONG).show();
                }
        }
    }

    //活动响应
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case 1:
                if(resultCode == getActivity().RESULT_OK){
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
                if(resultCode == getActivity().RESULT_OK){
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
        cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
        if(cursor.moveToFirst()){
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
            uploadfilename = cursor.getString(columnIndex);
            photopath = FileUtil.getFilePathByUri(getContext(), photouri);
        }
        try{
            //Bitmap pic = PicCompress.SampleRateCompress(photopath);
            Glide.with(getContext()).load(photouri)
                    .fitCenter()
                    .into(scoreimg);
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
            Glide.with(getActivity())
                    .load(photouri)
                    .fitCenter()
                    .into(scoreimg);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //base64转换
    @RequiresApi(api = Build.VERSION_CODES.O)
    //public byte[ ]UriToBase() {
    public String UriToBase(){
        Bitmap bitmap = null;
        try {
            InputStream inputStream = getActivity().getContentResolver().openInputStream(photouri);
            filebuf = convertToBytes(inputStream);
            //bitmap = BitmapFactory.decodeByteArray(filebuf, 0, filebuf.length);
            imgbase64 = Base64.encodeToString(filebuf,Base64.DEFAULT);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
//        byte bytes[] = bos.toByteArray();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            filebuf = Base64.getEncoder().encode(bytes);
//        }
//        return filebuf;
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
