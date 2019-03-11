package com.example.yy.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/*
*  自定义视图 BoatView 包括船体和绳节点盘
* */
public class BoatView extends android.support.v7.widget.AppCompatImageView  {

    private static final String TAG = "BoatView";

    private boolean longClick = false;
    private boolean isTimeToShowBoatShadow = false;

    private float startX = 0;
    private float startY = 0;

    private float xOffset;
    private float yOffset;

    private int lastX;
    private int lastY;
    private int maxRight;
    private int maxBottom;
    private RelativeLayout parentView;

    private Matrix mMatrix = new Matrix();


    //private ImageView boatShadowView;



    public BoatView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //boatShadowView = MainActivity.getBoatShadowView();


        //addListeners();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        //TODO 旋转船体

    }



    private void addListeners(){

/*

        //给船体添加长按事件监听
        this.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                v.startDrag(null,new DragShadowBuilder(BoatView.this),BoatView.this,0);
                Log.d(TAG,"long click");
                longClick = true;
                return false;
            }
        });


        //添加拖动事件监听
        this.setOnDragListener(new OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                switch (event.getAction()) {
                    //开始拖动时
                    case DragEvent.ACTION_DRAG_STARTED: {
                        Log.e(TAG, "View开始被拖动");

                        break;
                    }
                    //拖动结束时
                    case DragEvent.ACTION_DRAG_ENDED: {
                        Log.e(TAG, "View拖动结束");
                        //TODO 显示幽灵船
                        isTimeToShowBoatShadow = true;
                        boatShadowView.setVisibility(VISIBLE);
                        break;
                    }
                    case DragEvent.ACTION_DROP:{
                        Log.d(TAG,"x : " + event.getX());
                        Log.d(TAG,"y : " + event.getX());
                        break;
                    }
                    //拖动完成时
                    case DragEvent.ACTION_DRAG_EXITED: {
                        Log.e(TAG, "View拖动退出");
                        break;
                    }
                    default: {
                        break;
                    }
                }
                return true;
            }
        });
*/



        //给船体添加双击事件监听
        /*this.setOnTouchListener(new OnDoubleClickListener(new OnDoubleClickListener.DoubleOnClickListener() {
            @Override
            public void onDoubleClick() {
                Log.d(TAG,"双击成功");
            }
        }));*/

        /*
        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float endX = 0;
                float endY = 0;
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    Log.d(TAG,"action down");
                    startX = event.getRawX();
                    startY = event.getRawY();
                    Log.d(TAG,"down location :");
                    Log.d(TAG,"start x : " + startX + "start y : " + startY);
                }else if (event.getAction() == MotionEvent.ACTION_UP){
                    endX = event.getRawX();
                    endY = event.getRawY();
                    Log.d(TAG,"action up");
                    Log.d(TAG,"up location :");
                    Log.d(TAG,"end x : " + endX + "end y : " + endY);
                    //TODO 计算船体图片移动的相对坐标变化
                    xOffset = endX - startX;
                    yOffset = endY - startY;
                    Log.d(TAG,"x offset : " + xOffset);
                    Log.d(TAG,"y offset : " + yOffset);

                }
                return true;
            }
        });*/
    }



    /*class MatrixOnTouchListener implements View.OnTouchListener {

        private Matrix mCurrentMatrix = new Matrix();

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_MOVE){
                mCurrentMatrix.set(getImageMatrix());
                mCurrentMatrix.postTranslate(100,100);
                setImageMatrix(mCurrentMatrix);
            }
            return false;
        }
    }*/

}


