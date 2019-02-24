package com.example.yy.ui;

public abstract class  Model {
    private float x;//模型在电脑界面中的坐标
    private float y;

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
