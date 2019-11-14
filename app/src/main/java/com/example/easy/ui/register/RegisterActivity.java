package com.example.easy.ui.register;

import com.example.easy.*;
import com.example.easy.tool.Globe;
import com.example.easy.ui.login.LoginActivity;

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

public class RegisterActivity extends AppCompatActivity {
    private static String username = "";
    private static String password = "";
    private String register_uri = "http://114.55.64.152:3000/register";
    //private String register_uri = "http://192.168.1.103:3000/register";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyAplication.getInstance().addActivity(RegisterActivity.this);
        setContentView(R.layout.activity_register);
        //跳转到注册界面
        Button to_login = (Button)findViewById(R.id.to_login);
        to_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        //用户名获取
        final EditText registerusername = (EditText) findViewById(R.id.register_username);
        registerusername.setOnKeyListener(new EditText.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                username = registerusername.getText().toString();
                return false;
            }
        });
        //密码获取
        final EditText registerpassword = (EditText) findViewById(R.id.register_password);
        registerpassword.setOnKeyListener(new EditText.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                password = registerpassword.getText().toString();
                return false;
            }
        });
        //登录按钮点击
        Button register_submit = (Button)findViewById(R.id.register_submit);
        register_submit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(username.isEmpty() || password.isEmpty()){
                    Toast.makeText(RegisterActivity.this,"用户名或密码不能为空" ,Toast.LENGTH_SHORT).show();
                }else{
                    String result = null;
                    try {
                        result = SubmitUser.Submit(username,password,register_uri);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    resultReact(result);
                }
            }
        });
    }

    public void resultReact(String result){
        if(result.equals("\"userexist\"")){
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(RegisterActivity.this, "用户名已经被注册过了", Toast.LENGTH_SHORT).show();
                    reStartActivity();
                }
            });
        }else if(result.equals("\"somethingwrong\"")){
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(RegisterActivity.this, "未知错误，请重试或退出", Toast.LENGTH_SHORT).show();
                    reStartActivity();
                }
            });
        }else if(result.equals("\"success\"")){
            Globe.setLoginUser(username);
            Globe.setPath(getExternalCacheDir().getPath());
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
    //刷新界面
    public void reStartActivity(){
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }
}
