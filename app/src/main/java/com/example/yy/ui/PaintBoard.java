package com.example.yy.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaintBoard extends View {

    private Paint mPaint;
    private Boat boatToBePaint;
    private RopeNodeBoard mRopeNodeBoard;
    private RopeNode mNode1;
    private RopeNode mNode2;
    private RopeNode mNode3;
    private RopeNode mNode4;

    private Map<Integer,Anchor> anchorMap;
    private Anchor mAnchor1;
    private Anchor mAnchor2;
    private Anchor mAnchor3;
    private Anchor mAnchor4;

    private static final String TAG = "PaintBoard";


    public PaintBoard(Context context,AttributeSet attr) {
        super(context,attr);

        //初始化画笔
        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        mPaint.setStrokeWidth(3.0f);
    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 从MainActivity中获取数据容器
        //获取船
        boatToBePaint = MainActivity.getBoat();
        //获取anchor数据包
        anchorMap = MainActivity.getmAnchorMap();

        // 给画图所需的model赋上数据
        setDataForModels();
        //画出绳子
        canvas.drawLine(mAnchor1.getX(),mAnchor1.getY(),mNode1.getX(),mNode1.getY(),mPaint);
        canvas.drawLine(mAnchor2.getX(),mAnchor2.getY(),mNode2.getX(),mNode2.getY(),mPaint);
        canvas.drawLine(mAnchor3.getX(),mAnchor3.getY(),mNode3.getX(),mNode3.getY(),mPaint);
        canvas.drawLine(mAnchor4.getX(),mAnchor4.getY(),mNode4.getX(),mNode4.getY(),mPaint);
    }

    private void setDataForModels(){
        //获取船上的绳盘
        mRopeNodeBoard = boatToBePaint.getRopeNodeBoard();
        //获取绳盘上的节点
        mNode1 = mRopeNodeBoard.getNode1();
        mNode2 = mRopeNodeBoard.getNode2();
        mNode3 = mRopeNodeBoard.getNode3();
        mNode4 = mRopeNodeBoard.getNode4();
        //获取anchors
        mAnchor1 = anchorMap.get(1);
        mAnchor2 = anchorMap.get(2);
        mAnchor3 = anchorMap.get(3);
        mAnchor4 = anchorMap.get(4);
    }
}
