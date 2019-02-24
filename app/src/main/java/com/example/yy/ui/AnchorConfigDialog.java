package com.example.yy.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class AnchorConfigDialog extends AlertDialog {

    private Button cancelButton;
    private Button sureButton;
    private TextView titleView;
    private String titleMessage;

    private EditText longitudeInputTextView;
    private EditText latitudeInputTextView;
    private boolean longitudeTextChanged = false;
    private boolean latitudeTextChanged = false;

    private String longitudeStringBeforeConfig;
    private String latitudeStringBeforeConfig;
    private String longitudeStringAfterConfig = "0";
    private String latitudeStringAfterConfig = "0";

    private ButtonOnClickListener cancelButtonListener;
    private ButtonOnClickListener sureButtonListener;

    private static final String TAG = "dialog";


    public AnchorConfigDialog(Context context) {
        super(context,R.style.AnchorConfigDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_config_anchor);
        initView();
        initData();
        setCanceledOnTouchOutside(false);

        TextWatcher longitudeTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            //该方法只有在EditText中的内容改变的时候才会执行
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d(TAG,"CharSequence : " + s);
                Log.d(TAG,"start : " + start);
                Log.d(TAG,"count : " + count);
                longitudeTextChanged = true;
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (longitudeTextChanged){
                    longitudeStringAfterConfig = longitudeInputTextView.getText().toString();
                    longitudeTextChanged = false;
                }else {
                    longitudeStringAfterConfig = longitudeStringBeforeConfig;
                }
            }
        };
        longitudeInputTextView.addTextChangedListener(longitudeTextWatcher);

        TextWatcher latitudeTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                latitudeTextChanged = true;
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (latitudeTextChanged){
                    latitudeStringAfterConfig = latitudeInputTextView.getText().toString();
                    latitudeTextChanged = false;
                }else {
                    latitudeStringAfterConfig = latitudeStringBeforeConfig;
                }

            }
        };
        latitudeInputTextView.addTextChangedListener(latitudeTextWatcher);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cancelButtonListener != null){
                    cancelButtonListener.buttonOnClick();
                }
            }
        });

        sureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sureButtonListener != null){
                    sureButtonListener.buttonOnClick();
                }
            }
        });
    }

    public void setGPSTextBeforeConfig(String longitudeStringBeforeConfig,String latitudeStringBeforeConfig){
        this.longitudeStringBeforeConfig = longitudeStringBeforeConfig;
        this.latitudeStringBeforeConfig = latitudeStringBeforeConfig;
    }

    // 向外界提供可以修改经纬度视图text的方法
    public void updateAnchorGPSTextViewAfterConfig(TextView longitudeTextView,TextView latitudeTextView){
        longitudeTextView.setText(longitudeStringAfterConfig);
        latitudeTextView.setText(latitudeStringAfterConfig);
    }

    public void setAnchorGPS(Anchor anchor){
        anchor.setLongitude(Float.parseFloat(longitudeStringAfterConfig));
        anchor.setLatitude(Float.parseFloat(latitudeStringAfterConfig));
    }

    private void initView(){
        cancelButton = findViewById(R.id.anchor_config_dismiss);
        sureButton = findViewById(R.id.anchor_config_sure);
        titleView = findViewById(R.id.anchor_config_dialog_title);
        longitudeInputTextView = findViewById(R.id.anchor_config_longitude_editText);
        latitudeInputTextView = findViewById(R.id.anchor_config_latitude_editText);
    }

    private void initData(){
        // 根据实际情况设置title
        if (titleMessage != null){
            titleView.setText(titleMessage);
        }
        // 设置显示当前GPS数据
        longitudeInputTextView.setText(longitudeStringBeforeConfig);
        latitudeInputTextView.setText(latitudeStringBeforeConfig);

        longitudeInputTextView.setSelection(longitudeStringBeforeConfig.length());
        latitudeInputTextView.setSelection(latitudeStringBeforeConfig.length());
    }

    //自定义dialog的setTitle方法其实是改变一个TextView的Text
    public void setDialogTitle(String title) {
        titleMessage = title;
    }




    // 提供监听器接口，外部类实现该接口之后再实现buttonOnClick()方法，即可
    public interface ButtonOnClickListener{
        void buttonOnClick();
    }

    // 给外部类提供一个设置监听器的接口，用于为该布局中的button设置监听器
    public void setCancelButtonOnClickListener(ButtonOnClickListener buttonOnClickListener){
        cancelButtonListener = buttonOnClickListener;
    }

    public void setSureButtonOnClickListener(ButtonOnClickListener buttonOnClickListener){
        sureButtonListener = buttonOnClickListener;
    }

}
