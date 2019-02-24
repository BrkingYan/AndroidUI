package com.example.yy.ui;

public class RopeNodeBoard extends Model {
    private RopeNode mNode1;
    private RopeNode mNode2;
    private RopeNode mNode3;
    private RopeNode mNode4;

    public RopeNodeBoard(RopeNode node1,RopeNode node2,RopeNode node3,RopeNode node4){
        mNode1 = node1;
        mNode2 = node2;
        mNode3 = node3;
        mNode4 = node4;
    }

    public RopeNode getNode1() {
        return mNode1;
    }

    public RopeNode getNode2() {
        return mNode2;
    }

    public RopeNode getNode3() {
        return mNode3;
    }

    public RopeNode getNode4() {
        return mNode4;
    }
}
