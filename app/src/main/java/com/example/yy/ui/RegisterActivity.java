package com.example.yy.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RegisterActivity extends AppCompatActivity  {

    private int totalUserNum = 0;

    private Gson mGson = new Gson();
    private List<Account> accountList;
    private Set<String> userNamePool;
    private String accountListJsonString;
    private String userNameSetJsonString;

    static final String TAG = "UI";

    private Button mInnerRegisterButton;
    private EditText mRegisterUserNameInput;
    private EditText mRegisterPasswordInput;
    private EditText mRegisterPasswordAgainInput;

    private String mNewUserNameString = "";
    private String mNewPasswordString = "";
    private String mNewPasswordAgainString = "";

    private TextView registerSuccessMsg;
    private TextView improveInfoMsg;
    private TextView userNameDuplicatedMsg;
    //private TextView registerFailedText;

    private SharedPreferences.Editor accountListEditor;
    private SharedPreferences.Editor userNamePoolEditor;
    private SharedPreferences registerAccountListPreferences;
    private SharedPreferences registerUserNameSetPreferences;

    //用于存放用户全部信息的文件和读取时用的键
    private static final String accountPreferencesStoreFileName = "account_info";
    private static final String accountPreferencesKey = "accounts";
    //用于存放用户的用户名信息的文件和读取时用的键
    private static final String userNamePreferencesStoreFileName = "userName_pool";
    private static final String userPreferencesKey = "userNames";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        /* 视图相关成员 */

        // EditText 及相关配置
        mRegisterUserNameInput = (EditText) findViewById(R.id.register_userName_input);
        mRegisterPasswordInput = (EditText) findViewById(R.id.register_password_input);
        mRegisterPasswordAgainInput = (EditText) findViewById(R.id.register_password_again_input);


        //禁止输入空格
        /*setEditTextInhibitInputSpace(mRegisterUserNameInput);
        setEditTextInhibitInputSpace(mRegisterPasswordInput);
        setEditTextInhibitInputSpace(mRegisterPasswordAgainInput);*/

        //给每个EditText加上监听器
        addListenerForEditTexts();


        // dialog显示信息设置
        registerSuccessMsg = new TextView(this);
        registerSuccessMsg.setText("注册成功 !");
        registerSuccessMsg.setTextSize(18);
        registerSuccessMsg.setGravity(Gravity.CENTER);

        improveInfoMsg = new TextView(this);
        improveInfoMsg.setText("你有信息尚未完善，请完善");
        improveInfoMsg.setTextSize(14);
        improveInfoMsg.setGravity(Gravity.CENTER);

        userNameDuplicatedMsg = new TextView(this);
        userNameDuplicatedMsg.setText("该用户名已经注册过，请更换为其他用户名");
        userNameDuplicatedMsg.setTextSize(18);
        userNameDuplicatedMsg.setGravity(Gravity.CENTER);


        /* 数据相关成员*/
        // 从preferences文件读取数据
        readPreferencesData();


        // Button 监听信息
        mInnerRegisterButton = (Button) findViewById(R.id.inner_register_button);
        //mInnerRegisterButton.setOnClickListener(this);
        mInnerRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkEmptyInput() && checkUserName() && checkPassword()){//检查是否有哪一栏为空，用户名是否重复，前后密码是否相同
                    // ***若输入的信息没问题就成功创建新用户***
                    showRegisterSuccessDialog();
                    // 将用户信息保存
                    totalUserNum ++;
                    Account account = new Account(totalUserNum,mRegisterUserNameInput.getText().toString(),
                            mRegisterPasswordInput.getText().toString());
                    //Account account = new Account(1,"yy","123");
                    // 将新增的账户信息add 到accountList，并保存
                    if (accountList == null){
                        accountList = new ArrayList<Account>();
                    }
                    accountList.add(account);
                    String accountString = mGson.toJson(accountList);
                    accountListEditor = getSharedPreferences(accountPreferencesStoreFileName,MODE_PRIVATE).edit();
                    accountListEditor.putString(accountPreferencesKey,accountString);
                    accountListEditor.apply();
                    // 将新增用户的用户名加入用户名池，更新用户名池并保存
                    if (userNamePool == null){
                        userNamePool = new HashSet<String>();
                    }
                    userNamePool.add(account.getUserName());
                    String userNameString = mGson.toJson(userNamePool);
                    userNamePoolEditor = getSharedPreferences(userNamePreferencesStoreFileName,MODE_PRIVATE).edit();
                    userNamePoolEditor.putString(userPreferencesKey,userNameString);
                    userNamePoolEditor.apply();
                }
            }
        });
    }

    // 该方法给EditText加上了监听器
    private void addListenerForEditTexts(){
        //增加监听器
        TextWatcher mUserNameWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mNewUserNameString = mRegisterUserNameInput.getText().toString();
            }
        };

        TextWatcher mPasswordWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mNewPasswordString = mRegisterPasswordInput.getText().toString();
            }
        };

        TextWatcher mAgainPasswordWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mNewPasswordAgainString = mRegisterPasswordAgainInput.getText().toString();
            }
        };
        mRegisterUserNameInput.addTextChangedListener(mUserNameWatcher);
        mRegisterPasswordInput.addTextChangedListener(mPasswordWatcher);
        mRegisterPasswordAgainInput.addTextChangedListener(mAgainPasswordWatcher);
    }

    //该方法从preferences文件中读取数据
    private void readPreferencesData(){
        // accountList读取
        registerAccountListPreferences = getSharedPreferences(accountPreferencesStoreFileName,MODE_PRIVATE);
        accountListJsonString = registerAccountListPreferences.getString(accountPreferencesKey,"");
        accountList = mGson.fromJson(accountListJsonString,new TypeToken<List<Account>>(){}.getType());

        // userNameSet读取
        registerUserNameSetPreferences = getSharedPreferences(userNamePreferencesStoreFileName,MODE_PRIVATE);
        userNameSetJsonString = registerUserNameSetPreferences.getString(userPreferencesKey,"");
        userNamePool = mGson.fromJson(userNameSetJsonString,new TypeToken<Set<String>>(){}.getType());
    }


    // 验证数据
    private boolean checkEmptyInput(){
        if (mNewUserNameString == "" || mNewPasswordString == "" || mNewPasswordAgainString == ""){
            Toast.makeText(RegisterActivity.this,"内容不能为空",Toast.LENGTH_SHORT).show();
            //showPromptMsgDialog(improveInfoMsg);
            return false;
        }else {
            return true;
        }
    }

    // 判断用户名是否被注册过
    private boolean checkUserName(){
        if (userNamePool == null){
            return true;
        }else if (userNamePool.contains(mNewUserNameString)){
            Toast.makeText(RegisterActivity.this,"该用户名已经注册",Toast.LENGTH_SHORT).show();
            //showPromptMsgDialog(userNameDuplicatedMsg);
            return false;
        }else {
            return true;
        }
    }

    // 判断密码是否正确
    private boolean checkPassword(){
        if (checkEmptyInput()){
            if (mNewPasswordString.equals(mNewPasswordAgainString)){
                return true;
            }else {
                Toast.makeText(RegisterActivity.this,"两次输入的密码不一致",Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return false;
    }

    // 弹出的各种对话框
    private void showRegisterSuccessDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setView(registerSuccessMsg);
        builder.setNegativeButton("继续注册", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                restartActivity();
            }
        });

        builder.setPositiveButton("去登录", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
        dialog.getWindow().setLayout(700,330);
    }


    private void showPromptMsgDialog(TextView msgView){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setView(msgView);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
        dialog.getWindow().setLayout(700,330);
    }

    //重启Activity
    private void restartActivity(){
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }


    /***************** 辅助函数 *****************/

    /*
     *  禁止EditText输入空格
     * */
    public static void setEditTextInhibitInputSpace(EditText editText){
        InputFilter filter=new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if(source.equals(" "))
                    return "";
                else
                    return null;
            }
        };
        editText.setFilters(new InputFilter[]{filter});
    }

    /*
     *  禁止EditText输入特殊字符
     * */
    public static void setEditTextInhibitInputSpeChat(EditText editText){

        InputFilter filter=new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                String speChat="[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
                Pattern pattern = Pattern.compile(speChat);
                Matcher matcher = pattern.matcher(source.toString());
                if(matcher.find())return "";
                else return null;
            }
        };
        editText.setFilters(new InputFilter[]{filter});
    }

    /*
    *  getters
    * */

    public static String getAccountPreferencesStoreFileName() {
        return accountPreferencesStoreFileName;
    }

    public static String getUserNamePreferencesStoreFileName() {
        return userNamePreferencesStoreFileName;
    }

    public static String getAccountPreferencesKey() {
        return accountPreferencesKey;
    }

    public static String getUserPreferencesKey() {
        return userPreferencesKey;
    }
}
