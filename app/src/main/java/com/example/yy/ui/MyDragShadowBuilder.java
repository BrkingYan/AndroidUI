package com.example.yy.ui;

import android.graphics.Canvas;
import android.view.View;


/*
*  用来处理拖拽时候船体的阴影显示
* */
public class MyDragShadowBuilder extends View.DragShadowBuilder {
    private View mBoatView;

    public MyDragShadowBuilder(View view) {
        super(view);
        mBoatView = view;
    }

    @Override
    public void onDrawShadow(Canvas canvas) {
        canvas.rotate(180);
        mBoatView.draw(canvas);
    }
}
