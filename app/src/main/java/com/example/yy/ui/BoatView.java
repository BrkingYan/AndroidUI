package com.example.yy.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/*
*  自定义视图 BoatView 包括船体和绳节点盘
* */
public class BoatView extends RelativeLayout {

    private ImageView mBoatBody;
    private ImageView mRopeDial;

    private LayoutParams mBoatBodyParams;
    private LayoutParams mRopeDialParams;

    public BoatView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setGravity(Gravity.CENTER);
        initView(context);

        // 添加组件
        addView(mBoatBody,mBoatBodyParams);
        addView(mRopeDial,mRopeDialParams);
    }


    private void initView(Context context){
        mBoatBody = new ImageView(context);
        mRopeDial = new ImageView(context);

        //设置组件布局参数
        mBoatBodyParams = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        mRopeDialParams = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);

        //往布局参数中添加属性
        mBoatBodyParams.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE);
        mRopeDialParams.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE);

        //给ImageView添加图片
        mBoatBody.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.boat_big));
        mRopeDial.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.rope_dial));

        //设置id
        mBoatBody.setId(R.id.boat_view);
        mRopeDial.setId(R.id.rope_dial_view);

    }
}
