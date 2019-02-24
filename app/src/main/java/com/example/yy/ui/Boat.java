package com.example.yy.ui;

public class Boat extends Model {
    private RopeNodeBoard mRopeNodeBoard;

    public Boat(RopeNodeBoard ropeNodeBoard){
        mRopeNodeBoard = ropeNodeBoard;
    }

    public RopeNodeBoard getRopeNodeBoard() {
        return mRopeNodeBoard;
    }
}
