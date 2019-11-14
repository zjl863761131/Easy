package com.example.easy.ui.usermsg;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.easy.tool.Globe;
import com.example.easy.ui.facesearch.FaceMainActivity;
import com.example.easy.ui.login.Logout;
import com.example.easy.MainActivity;
import com.example.easy.R;
import com.google.gson.Gson;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UserMsgFragment extends Fragment {
    private String username = Globe.getLoginUser();
    private String msg_uri = "http://114.55.64.152:3000/getmsg";
    private String msg_logout = "http://114.55.64.152:3000/logout";
    private String downloadheaduri = "http://114.55.64.152:3000/downloadhead";
    private String result;
//    private String msg_uri = "http://192.168.1.103:3000/getmsg";
//    private String msg_logout = "http://192.168.1.103:3000/logout";
//    private String downloadheaduri = "http://192.168.1.103:3000/downloadhead";
    private InputStream HeadBitMap;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            result = GetUserMsg.GetMsg(username, msg_uri);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Gson gson = new Gson();
        UserMsg user = gson.fromJson(result, UserMsg.class);
        View root = inflater.inflate(R.layout.fragment_usermsg, container, false);
        final TextView msgusername = root.findViewById(R.id.msg_username);
        final TextView msgtime = root.findViewById(R.id.msg_time);
        final ImageButton msgimg = root.findViewById(R.id.msg_img);
        final Button logout = root.findViewById(R.id.logout);
        final Button toface = root.findViewById(R.id.toface);
        final TextView msgnum = root.findViewById(R.id.msg_num);
        msgusername.setText(user.username);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String datetime = format.format(user.createtime);
        msgtime.setText(datetime);
        msgnum.setText(user.num);
        if (user.img != null) {
            String path = Globe.getPath() + "/" + user.img;
            File file = new File(path);
            if (!file.exists()) {
                new Thread() {
                    public void run() {
                        OkHttpClient client = new OkHttpClient();
                        RequestBody formbody = new FormBody.Builder()
                                .add("username", Globe.getLoginUser())
                                .build();
                        Request request = new Request.Builder()
                                .url(downloadheaduri)
                                .post(formbody)
                                .build();
                        try {
                            Response response = client.newCall(request).execute();
                            if (!response.isSuccessful()) {
                                throw new IOException("Unexpected code" + response);
                            }
                            HeadBitMap = response.body().byteStream();
                            Bitmap bitmap = BitmapFactory.decodeStream(HeadBitMap);
                            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
                            bos.flush();
                            bos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
            Glide.with(getActivity()).asBitmap().load(path).centerCrop().into(msgimg);
        }


        msgimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), HeadActivity.class);
                startActivity(intent);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    result = Logout.LogoutUser(msg_logout);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (result.equals("\"logoutsuccess\"")) {
                    Toast.makeText(getActivity(), "注销成功", Toast.LENGTH_SHORT).show();
                    Globe.setLoginUser("");
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });
        toface.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FaceMainActivity.class);
                startActivity(intent);
            }
        });
        return root;
    }

    public class UserMsg {
        public String username;
        public Date createtime;
        //public String filepath;
        public String img;
        public String num;
    }
}
