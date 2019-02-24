package com.example.yy.ui;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class Anchor extends Model {
    private int anchorId;//编号
    private float longitude;//经度
    private float latitude;//纬度

    public Anchor(int anchorId, float longitude,float latitude){
        this.anchorId = anchorId;
        this.longitude = longitude;
        this.latitude = latitude;
    }


    public int getAnchorId() {
        return anchorId;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }
}
