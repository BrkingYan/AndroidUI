package com.example.yy.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class MyConfigDialog extends AlertDialog {

    private Button cancelButton;
    private Button sureButton;

    private TextView titleView;
    private String titleMessage;

    private TextView firstInputLabelView;
    private String firstLabelMessage;
    private TextView secondInputLabelView;
    private String secondLabelMessage;
    private TextView tipInfoView;
    private String tipInfoMessage;

    private EditText firstInputTextView;
    private EditText secondInputTextView;
    private boolean secondEditTextVisible = true;
    private boolean firstTextChanged = false;
    private boolean secondTextChanged = false;

    private String firstInputStringBeforeConfig = "0";
    private String secondStringBeforeConfig = "0";
    private String firstStringAfterConfig = "0";
    private String secondStringAfterConfig = "0";

    private ButtonOnClickListener cancelButtonListener;
    private ButtonOnClickListener sureButtonListener;

    private static final String TAG = "dialog";


    protected MyConfigDialog(Context context) {
        super(context,R.style.AnchorConfigDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_config);

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
                firstTextChanged = true;
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (firstTextChanged){
                    firstStringAfterConfig = firstInputTextView.getText().toString();
                    firstTextChanged = false;
                }else {
                    firstStringAfterConfig = firstInputStringBeforeConfig;
                }
            }
        };
        firstInputTextView.addTextChangedListener(longitudeTextWatcher);

        TextWatcher latitudeTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                secondTextChanged = true;
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (secondTextChanged){
                    secondStringAfterConfig = secondInputTextView.getText().toString();
                    secondTextChanged = false;
                }else {
                    secondStringAfterConfig = secondStringBeforeConfig;
                }

            }
        };
        secondInputTextView.addTextChangedListener(latitudeTextWatcher);

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

    //TODO 对anchor的信息配置
    public void setGPSTextBeforeConfig(String longitudeStringBeforeConfig,String latitudeStringBeforeConfig){
        this.firstInputStringBeforeConfig = longitudeStringBeforeConfig;
        this.secondStringBeforeConfig = latitudeStringBeforeConfig;
    }

    // 向外界提供可以修改经纬度视图text的方法
    public void updateAnchorGPSTextViewAfterConfig(TextView longitudeTextView,TextView latitudeTextView){
        longitudeTextView.setText(firstStringAfterConfig);
        latitudeTextView.setText(secondStringAfterConfig);
    }

    public void setAnchorGPS(Anchor anchor){
        anchor.setLongitude(Float.parseFloat(firstStringAfterConfig));
        anchor.setLatitude(Float.parseFloat(secondStringAfterConfig));
    }

    //TODO 对船体视图的配置
    public void updateBoatRotationState(ImageView imageView){
        imageView.setRotation(imageView.getRotation() + Integer.parseInt(firstStringAfterConfig));
    }


    private void initView(){
        //Button
        cancelButton = findViewById(R.id.config_dismiss_button);
        sureButton = findViewById(R.id.config_sure_button);
        //TextView
        titleView = findViewById(R.id.config_dialog_title);
        firstInputLabelView = findViewById(R.id.first_label_text_view);
        secondInputLabelView = findViewById(R.id.second_label_text_view);
        tipInfoView = findViewById(R.id.tip_info_view);

        firstInputTextView = findViewById(R.id.config_editText_1);
        secondInputTextView = findViewById(R.id.config_editText_2);
    }

    private void initData(){
        // 根据实际情况设置title
        if (titleMessage != null){
            titleView.setText(titleMessage);
        }
        if (tipInfoMessage != null){
            tipInfoView.setText(tipInfoMessage);
        }
        if (firstLabelMessage != null){
            firstInputLabelView.setText(firstLabelMessage);
        }
        if (secondLabelMessage != null){
            secondInputLabelView.setText(secondLabelMessage);
        }

        if (!secondEditTextVisible){
            secondInputTextView.setVisibility(View.INVISIBLE);
        }

        // 设置显示当前GPS数据
        firstInputTextView.setText(firstInputStringBeforeConfig);
        secondInputTextView.setText(secondStringBeforeConfig);

        firstInputTextView.setSelection(firstInputStringBeforeConfig.length());
        secondInputTextView.setSelection(secondStringBeforeConfig.length());
    }

    //TODO 提供给外界设置视图的方法
    //自定义dialog的setTitle方法其实是改变一个TextView的Text
    public void setDialogTitle(String title) {
        titleMessage = title;
    }

    //设置提示信息
    public void setDialogTipInfo(String tipInfo){
        tipInfoMessage = tipInfo;
    }

    //设置第一个输入框的label
    public void setFirstLabel(String firstLabel){
        firstLabelMessage = firstLabel;
    }

    //设置第二个输入框的label
    public void setSecondLabel(String secondLabel){
        secondLabelMessage = secondLabel;
    }

    public void setSecondEditTextInvisible(){
        secondEditTextVisible = false;
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
