package com.example.easy.ui.login;

import com.example.easy.*;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.easy.ui.register.SubmitUser;
import com.example.easy.tool.Globe;
import com.example.easy.ui.register.RegisterActivity;

public class LoginActivity extends AppCompatActivity {
    private static String username = "";
    private static String password = "";
    private String login_uri = "http://114.55.64.152:3000/login";
    //private String login_uri = "http://192.168.1.103:3000/login";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyAplication.getInstance().addActivity(LoginActivity.this);
        setContentView(R.layout.activity_login);
        //用户名获取
        final EditText loginusername = (EditText) findViewById(R.id.login_username);
        loginusername.setOnKeyListener(new EditText.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                username = loginusername.getText().toString();
                return false;
            }
        });
        //密码获取
        final EditText loginpassword = (EditText) findViewById(R.id.login_password);
        loginpassword.setOnKeyListener(new EditText.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                password = loginpassword.getText().toString();
                return false;
            }
        });
        //跳转到注册界面
        Button to_register = (Button)findViewById(R.id.to_register);
        to_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        //登录按钮点击
        Button login_submit = (Button)findViewById(R.id.login_submit);
        login_submit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(username.isEmpty() || password.isEmpty()){
                    Toast.makeText(LoginActivity.this,"用户名或密码不能为空" ,Toast.LENGTH_SHORT).show();
                }else{
                    String result = null;
                    try {
                        result = SubmitUser.Submit(username,password,login_uri);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    resultReact(result);
                }
            }
        });
    }

    public void resultReact(String result){
        if(result.equals("\"success\"")){
            Globe.setLoginUser(username);
            Globe.setPath(getExternalCacheDir().getPath());
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }else if(result.equals("\"usernotexist\"")){
            final Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(LoginActivity.this, "用户名不存在", Toast.LENGTH_SHORT).show();
                    reStartActivity();
                }
            });
        }else if(result.equals("\"passwordwrong\"")){
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(LoginActivity.this, "用户名或者密码错误", Toast.LENGTH_SHORT).show();
                    reStartActivity();
                }
            });
        }
    }

    public void reStartActivity(){
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }
}

