package com.example.yy.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class LogActivity extends AppCompatActivity {

    //用户名和密码输入框
    private EditText mLogInUserNameInput;
    private EditText mLogInPasswordInput;
    //用户名和密码输入内容
    private String mLogInUserNameInputString;
    private String mLogInPasswordInputString;

    //按键
    private CheckBox mSaveIdBox;
    private Button mRegisterButton;
    private Button mLogInButton;

    private SharedPreferences logInAccountListPreferences;
    private SharedPreferences logInUserNameSetPreferences;

    private boolean rememberUserName;
    private String rememberedUserName;
    //Preferences文件名
    private String rememberUserNamePreferencesFile = "log_in_preferences";
    //Preferences  KEY名 ，这两个key都是从上面那个文件读写信息的
    private String rememberUserNamePreferencesKey = "remember_username";
    private String rememberedUserNameKey = "remembered_username";


    //下列perferences是从
    //存储账户信息的文件及其key
    private String accountPreferencesFile;
    private String accountPreferencesKey;
    //存储用户名的文件及其key
    private String userNamePreferencesFile;
    private String userNamePreferencesKey;

    private Gson mLogInGson = new Gson();
    private List<Account> logInAccountList = new ArrayList<Account>();
    private String logInAccountListJsonString = "";
    private Set<String> logInUserNameSet = new HashSet<String>();
    private String logInUserNameSetJsonString = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        //初始化视图对象
        findViewByIds();

        //获取sharedPreferences
        getPreferencesFileAndKey();
        // 为EditText设置偏好
        SharedPreferencesUtil.getInstance(LogActivity.this,rememberUserNamePreferencesFile);
        rememberUserName = (boolean)SharedPreferencesUtil.getData(rememberUserNamePreferencesKey,false);
        rememberedUserName = (String)SharedPreferencesUtil.getData(rememberedUserNameKey,"");
        //如果设置了记住账号，就直接显示账号
        if (rememberUserName){
            mLogInUserNameInput.setText(rememberedUserName);
            mLogInUserNameInputString = rememberedUserName;
            mSaveIdBox.setChecked(rememberUserName);
        }




        // 为EditText配置监听器watcher
        TextWatcher LogInUserNameInputWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            // 当输入的用户名变化时，马上记录下来
            @Override
            public void afterTextChanged(Editable s) {
                mLogInUserNameInputString = mLogInUserNameInput.getText().toString();
            }
        };

        TextWatcher LogInPassWordInputWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            // 当输入的密码变化时，马上记录下来
            @Override
            public void afterTextChanged(Editable s) {
                mLogInPasswordInputString = mLogInPasswordInput.getText().toString();
            }
        };

        // 部署 TextWatcher 到 EditText
        mLogInUserNameInput.addTextChangedListener(LogInUserNameInputWatcher);
        mLogInPasswordInput.addTextChangedListener(LogInPassWordInputWatcher);


        // "注册" 按钮配置

        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LogActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });

        // "登录" 按钮配置

        mLogInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkLogInMsg()){
                        //rememberUserNameEditor = getSharedPreferences(rememberUserNamePreferencesFile,MODE_PRIVATE).edit();
                    if (mSaveIdBox.isChecked()){
                        SharedPreferencesUtil.putData(rememberUserNamePreferencesKey,true);
                        SharedPreferencesUtil.putData(rememberedUserNameKey,mLogInUserNameInputString);
                        /*rememberUserNameEditor.putBoolean(rememberUserNamePreferencesKey,true);
                        rememberUserNameEditor.putString(rememberedUserNameKey,mLogInUserNameInputString);*/
                    }/*else {
                        rememberUserNameEditor.clear();
                    }
                    rememberUserNameEditor.apply();*/

                    Intent intent = new Intent(LogActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private void findViewByIds(){
        // CheckBox初始化
        mSaveIdBox = findViewById(R.id.save_id_checkbox);

        // EditText初始化
        mLogInUserNameInput = findViewById(R.id.log_in_username_input);
        mLogInPasswordInput = findViewById(R.id.log_in_password_input);
        //button初始化
        mRegisterButton = findViewById(R.id.register_button);
        mLogInButton = findViewById(R.id.log_in_button);

    }

    private void getPreferencesFileAndKey(){
        accountPreferencesFile = RegisterActivity.getAccountPreferencesStoreFileName();
        accountPreferencesKey = RegisterActivity.getAccountPreferencesKey();
        userNamePreferencesFile = RegisterActivity.getUserNamePreferencesStoreFileName();
        userNamePreferencesKey = RegisterActivity.getUserPreferencesKey();
    }

    private boolean checkLogInMsg(){
        // 从SharedPreferences文件中读取用户数据

        // accountList读取
        logInAccountListPreferences = getSharedPreferences(accountPreferencesFile,MODE_PRIVATE);
        logInAccountListJsonString = logInAccountListPreferences.getString(accountPreferencesKey,"");
        logInAccountList = mLogInGson.fromJson(logInAccountListJsonString,new TypeToken<List<Account>>(){}.getType());

        // userNameSet读取
        logInUserNameSetPreferences = getSharedPreferences(userNamePreferencesFile,MODE_PRIVATE);
        logInUserNameSetJsonString = logInUserNameSetPreferences.getString(userNamePreferencesKey,"");
        logInUserNameSet = mLogInGson.fromJson(logInUserNameSetJsonString,new TypeToken<Set<String>>(){}.getType());


        if ( logInUserNameSet == null || (! logInUserNameSet.contains(mLogInUserNameInputString))){
            Toast.makeText(LogActivity.this,"该用户名不存在",Toast.LENGTH_SHORT).show();
        }else { //用户名存在的话，就看密码对不对
            Iterator<Account> accountIterator = logInAccountList.iterator();
            while (accountIterator.hasNext()){
                Account account = accountIterator.next();
                if (account.getUserName().equals(mLogInUserNameInputString)){
                    if (account.getPassword().equals(mLogInPasswordInputString)){
                        return true;
                    }else {
                        Toast.makeText(LogActivity.this,"密码错误",Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }
            }
        }
        return false;
    }
}
