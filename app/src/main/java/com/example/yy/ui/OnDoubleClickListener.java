package com.example.yy.ui;

import android.nfc.Tag;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;


class OnDoubleClickListener implements View.OnTouchListener{

    private int mode;
    private int touchCount = 0;
    private long firstClickTime = 0;
    private long secondClickTime = 0;
    private long interval = 200;
    private DoubleOnClickListener mDoubleOnClickListener;
    private static final String TAG = "doubleClick";
    private RelativeLayout boatLayout;

    public OnDoubleClickListener(DoubleOnClickListener doubleOnClickListener){
        mDoubleOnClickListener = doubleOnClickListener;
    }

    //内部回调接口
    public interface DoubleOnClickListener{
        void onDoubleClick();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN){
            touchCount ++;
            Log.e(TAG,"down run");
        }else if (event.getAction() == MotionEvent.ACTION_CANCEL){
            Log.e(TAG,"x : " + event.getX());
            Log.e(TAG,"y : " + event.getY());
        }

        if (touchCount == 1){
            firstClickTime = System.currentTimeMillis();
        }else if (touchCount == 2){
            secondClickTime = System.currentTimeMillis();
            if ((secondClickTime - firstClickTime) <= interval){
                mDoubleOnClickListener.onDoubleClick();
            }
            touchCount = 0;
        }
        return false;
    }
}
