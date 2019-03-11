package com.example.yy.ui;

import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
*  MVC 中的控制器 Control er
* */
public class MainActivity extends AppCompatActivity {

    //参数信息

    //船体旋转的角度
    private double boatAngleRadian = 0; //弧度
    private float boatAngle = (float) (boatAngleRadian / Math.PI )* 180;  //角度

/*******************************/
    private int boatStartX;
    private int boatStartY;

    private float boatCenterX;
    private float boatCenterY;

//    private MyConfigDialog boatMoveDialog;


    // 视图成员
    private ImageView mAnchor1View;
    private ImageView mAnchor2View;
    private ImageView mAnchor3View;
    private ImageView mAnchor4View;


    private BoatView mBoatView;//可旋转的View船体视图对象
    private ImageView mBoatShadowView;// 阴影船


    private RopeDialView mRopeDialView;//绳盘
    private RelativeLayout mPaintLayout;//整个画图区域的layout

    private TextView mAnchor1LongitudeView;
    private TextView mAnchor2LongitudeView;
    private TextView mAnchor3LongitudeView;
    private TextView mAnchor4LongitudeView;

    private TextView mAnchor1LatitudeView;
    private TextView mAnchor2LatitudeView;
    private TextView mAnchor3LatitudeView;
    private TextView mAnchor4LatitudeView;


    // 模型成员
    private static Boat mBoat;
    private RopeNodeBoard mRopeNodeBoard;
    private RopeNode mNode1;
    private RopeNode mNode2;
    private RopeNode mNode3;
    private RopeNode mNode4;
    private Anchor mAnchor1;
    private Anchor mAnchor2;
    private Anchor mAnchor3;
    private Anchor mAnchor4;


    // 容器成员
    // 这些用来存放视图的尺寸
    private List<Float> anchor1ScaleList;
    private List<Float> anchor2ScaleList;
    private List<Float> anchor3ScaleList;
    private List<Float> anchor4ScaleList;
    private List<Float> boatScaleList;
    private List<Float> ropeDialScaleList;
    private List<Integer> paintLayoutScaleList;

    //这些暂时用来存放模型，用于在PaintBoard中读取绘图数据
    private static Map<Integer,Anchor> mAnchorMap;

    //这个用来将anchorId和anchorView绑定
    private Map<Integer,ImageView> IdAnchorViewMap;

    //这个用来存放模型，到时候会放入sharedPreferences中
    private List<Anchor> mAnchorList;

    private static final String TAG = "MainActivity";

    // SharedPreferences成员，用来存放之前配置的信息
    private Gson gsonInMain = new Gson();;
    private SharedPreferences.Editor anchorListEditor;
    private SharedPreferences anchorListPreferences;
    private static final String anchorListPreferencesFileName = "anchor_bank";
    private static final String anchorListPreferencesBankKey = "anchors";

    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化视图成员
        findViewByIds();

        mButton = findViewById(R.id.test_button);




        //给anchorView编号
        IdAnchorViewMap = new HashMap<>();
        IdAnchorViewMap.put(1,mAnchor1View);
        IdAnchorViewMap.put(2,mAnchor2View);
        IdAnchorViewMap.put(3,mAnchor3View);
        IdAnchorViewMap.put(4,mAnchor4View);


        //初始化模型成员
        initModelsOnBoat();//创建它们的对象，并初始化


        setListenerForBoat();

        // 从preferences文件中获取之前设置的anchor经纬度信息
        getAnchorDataFromPreferencesFile();


        // 更新视图的UI坐标数据 ，里面有post方法
        updateImageViewUICoordinates();

        // 为anchor视图添加按键监听器
        addOnClickListenerForAnchorViews(mAnchor1);
        addOnClickListenerForAnchorViews(mAnchor2);
        addOnClickListenerForAnchorViews(mAnchor3);
        addOnClickListenerForAnchorViews(mAnchor4);


        /************************ for test ******************************/
        /******************* 测试点击图片产生相应功能 *******************/
        /*View.OnTouchListener onTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        float x = event.getX();
                        float y = event.getY();
                        Toast.makeText(MainActivity.this, "x:" + x + "; y:" + y, Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
                return false;
            }
        };
        mBoatVIew.setOnTouchListener(onTouchListener);*/
        /************************ for test ******************************/

    }


    // 初始化视图
    private void findViewByIds(){
        mAnchor1View = findViewById(R.id.anchor1);
        mAnchor2View = findViewById(R.id.anchor2);
        mAnchor3View = findViewById(R.id.anchor3);
        mAnchor4View = findViewById(R.id.anchor4);


        mAnchor1LongitudeView = findViewById(R.id.anchor1_longitude);
        mAnchor2LongitudeView = findViewById(R.id.anchor2_longitude);
        mAnchor3LongitudeView = findViewById(R.id.anchor3_longitude);
        mAnchor4LongitudeView = findViewById(R.id.anchor4_longitude);

        mAnchor1LatitudeView = findViewById(R.id.anchor1_latitude);
        mAnchor2LatitudeView = findViewById(R.id.anchor2_latitude);
        mAnchor3LatitudeView = findViewById(R.id.anchor3_latitude);
        mAnchor4LatitudeView = findViewById(R.id.anchor4_latitude);

        mBoatView = findViewById(R.id.my_boat_view);
        mBoatShadowView = findViewById(R.id.my_boat_shadow);

        mRopeDialView = findViewById(R.id.rope_dial_view);

        mPaintLayout = findViewById(R.id.paint_layout);
    }

    //该方法用于旋转UI中的View，转了
    private void rotateViews(float boatAngle,float pivotX,float pivotY){
        mBoatView.setRotation(boatAngle);
        mRopeDialView.setRotation(boatAngle);
        mBoatShadowView.setRotation(boatAngle);
        Log.d(TAG,"rope dial layout  axis : " + mRopeDialView.getLeft());

    }

    //将anchor装入list准备保存到文件中
    private void addAnchorIntoList(){
        if (mAnchorList == null){
            mAnchorList = new ArrayList<>();
        }else {
            mAnchorList.clear();
        }
        mAnchorList.add(mAnchor1);
        mAnchorList.add(mAnchor2);
        mAnchorList.add(mAnchor3);
        mAnchorList.add(mAnchor4);
    }

    //该方法以map的形式将4个anchor的数据存入preferences文件中
    private void putAnchorDataIntoPreferencesFile(){
        //将4个anchor装入一个list中
        addAnchorIntoList();
        String anchorListJsonString = gsonInMain.toJson(mAnchorList);
        Log.d(TAG,"*** json in PUT *** : " + anchorListJsonString);
        anchorListEditor = getSharedPreferences(anchorListPreferencesFileName,MODE_PRIVATE).edit();
        // 将key与list绑定
        anchorListEditor.putString(anchorListPreferencesBankKey,anchorListJsonString);
        anchorListEditor.apply();
    }

    //该方法从preferences文件中获取anchor的数据
    private void getAnchorDataFromPreferencesFile(){
        mAnchorList = new ArrayList<>();
        anchorListPreferences = getSharedPreferences(anchorListPreferencesFileName,MODE_PRIVATE);
        String anchorListJsonString = anchorListPreferences.getString(anchorListPreferencesBankKey,"");
        Log.d(TAG,anchorListJsonString);
        //从xml文件中获取anchorList
        mAnchorList = gsonInMain.fromJson(anchorListJsonString,new TypeToken<List<Anchor>>(){}.getType());
        //如果从文件中读取到了anchor数据就直接获取
        if (mAnchorList != null){
            mAnchor1 = mAnchorList.get(0);
            mAnchor2 = mAnchorList.get(1);
            mAnchor3 = mAnchorList.get(2);
            mAnchor4 = mAnchorList.get(3);
            Log.d(TAG,"从xml文件获取数据成功");
        }else {
            Log.d(TAG,"xml文件不存在，新初始化数据");
            initAnchorModel();//创建anchor
        }
        //根据读到的Anchor数据更新anchorGPSView的显示数据
        updateAnchorGPSTextView();
    }

    private void updateRopeNode(double boatAngle,float centerX,float centerY,float sideLength){
        Log.d(TAG,"centerX : " + centerX + "... centerY : " + centerY + "... sideLength : " + sideLength);
        float node1X = centerX - sideLength / 2 + sideLength / 2 * (float) (1 - Math.cos(boatAngle) + Math.sin(boatAngle));
        float node1Y = centerY - sideLength / 2 + sideLength / 2 * (float) (1 - Math.cos(boatAngle) - Math.sin(boatAngle));
        Log.d(TAG,"cosAngle : " + Math.cos(boatAngle));

        Log.d(TAG,"node1 : (" + node1X + "," + node1Y + ")");
        mNode1.setX(node1X);
        mNode1.setY(node1Y);
        float node2X = centerX + sideLength / 2 + sideLength / 2 * (float) (-1 + Math.cos(boatAngle) + Math.sin(boatAngle));
        float node2Y = centerY - sideLength / 2 + sideLength / 2 * (float) (1 - Math.cos(boatAngle) + Math.sin(boatAngle));
        Log.d(TAG,"node2 : (" + node2X + "," + node2Y + ")");
        mNode2.setX(node2X);
        mNode2.setY(node2Y);
        float node3X = centerX + sideLength / 2 - sideLength / 2 * (float) (1 - Math.cos(boatAngle) + Math.sin(boatAngle));
        float node3Y = centerY + sideLength / 2 - sideLength / 2 * (float) (1 - Math.cos(boatAngle) - Math.sin(boatAngle));
        Log.d(TAG,"node3 : (" + node3X + "," + node3Y + ")");
        mNode3.setX(node3X);
        mNode3.setY(node3Y);
        float node4X = centerX - sideLength / 2 - sideLength / 2 * (float) (-1 + Math.cos(boatAngle) + Math.sin(boatAngle));
        float node4Y = centerY + sideLength / 2 - sideLength / 2 * (float) (1 - Math.cos(boatAngle) + Math.sin(boatAngle));
        Log.d(TAG,"node4 : (" + node4X + "," + node4Y + ")");
        mNode4.setX(node4X);
        mNode4.setY(node4Y);

    }


    private void updateImageViewUICoordinates(){
        // 初始化所有model的尺寸list
        initScaleLists();

        //数据的获取只能在post中进行，因此视图更新也将在这里面执行，因为数据出去就无效了
        mAnchorMap = new HashMap<>();//创建一个map将anchor与编号绑定，以便在绘图类中通过数据获取anchor对象

        /* 在post方法中获取各个视图的实时动态数据
         * */
        // 在这些线程中更新UI数据
        mAnchor1View.post(new Runnable() {
            @Override
            public void run() {
                // 计算视图的宽和高
                float viewWidth = mAnchor1View.getWidth();
                anchor1ScaleList.add(viewWidth);
                float viewHeight = mAnchor1View.getHeight();
                anchor1ScaleList.add(viewHeight);
                // 计算坐标数据并保存
                setCoordinatesForSingleModels(mAnchor1,mAnchor1View,anchor1ScaleList);
                //将anchor与编号绑定
                mAnchorMap.put(1,mAnchor1);
            }
        });
        mAnchor2View.post(new Runnable() {
            @Override
            public void run() {
                float viewWidth = mAnchor2View.getWidth();
                anchor2ScaleList.add(viewWidth);
                float viewHeight = mAnchor2View.getHeight();
                anchor2ScaleList.add(viewHeight);
                // 计算坐标数据并保存
                setCoordinatesForSingleModels(mAnchor2,mAnchor2View,anchor2ScaleList);
                //将anchor与编号绑定
                mAnchorMap.put(2,mAnchor2);
            }
        });
        mAnchor3View.post(new Runnable() {
            @Override
            public void run() {
                float viewWidth = mAnchor3View.getWidth();
                anchor3ScaleList.add(viewWidth);
                float viewHeight = mAnchor3View.getHeight();
                anchor3ScaleList.add(viewHeight);

                setCoordinatesForSingleModels(mAnchor3,mAnchor3View,anchor3ScaleList);
                //将anchor与编号绑定
                mAnchorMap.put(3,mAnchor3);
            }
        });
        mAnchor4View.post(new Runnable() {
            @Override
            public void run() {
                float viewWidth = mAnchor4View.getWidth();
                anchor4ScaleList.add(viewWidth);
                float viewHeight = mAnchor4View.getHeight();
                anchor4ScaleList.add(viewHeight);

                setCoordinatesForSingleModels(mAnchor4,mAnchor4View,anchor4ScaleList);
                //将anchor与编号绑定
                mAnchorMap.put(4,mAnchor4);
            }
        });
        mBoatView.post(new Runnable() {
            @Override
            public void run() {
                float viewWidth = mBoatView.getWidth();
                boatScaleList.add(viewWidth);
                float viewHeight = mBoatView.getHeight();
                boatScaleList.add(viewHeight);


            }
        });
        mRopeDialView.post(new Runnable() {
            @Override
            public void run() {
                float ropeDialWidth = mRopeDialView.getWidth();
                ropeDialScaleList.add(ropeDialWidth);
                float ropeDialHeight = mRopeDialView.getHeight();
                ropeDialScaleList.add(ropeDialHeight);

                float ropeDialX1 = mRopeDialView.getLeft();
                float ropeDialY1 = mRopeDialView.getTop();

                float ropeDialCenterX = ropeDialX1 + ropeDialWidth / 2;
                float ropeDialCenterY = ropeDialY1 + ropeDialHeight / 2;

                Log.d(TAG,"rope dial 1 :  (" + ropeDialX1 + "," + ropeDialY1 + ")");
                Log.d(TAG,"rope dial width : " + ropeDialWidth + ", rope dial height : " + ropeDialHeight);
                Log.d(TAG,"rope dial center : (" + ropeDialCenterX + "," + ropeDialCenterY + ")");

                updateRopeNode(boatAngleRadian,ropeDialCenterX,ropeDialCenterY,ropeDialWidth);
                rotateViews(boatAngle,ropeDialCenterX,ropeDialCenterY);



            }
        });

        mBoatView.post(new Runnable() {
            @Override
            public void run() {
                float boat1CoordinateX = mBoatView.getLeft();
                float boat1CoordinateY = mBoatView.getTop();
                float boatWidth = mBoatView.getWidth();
                float boatHeight = mBoatView.getHeight();
                float boatCoordinateX = boat1CoordinateX + boatWidth / 2;
                float boatCoodinateY = boat1CoordinateY + boatHeight / 2;
                boatCenterX = boatCoordinateX;
                boatCenterY = boatCoodinateY;
                Log.d(TAG,"boat 1 : (" + boat1CoordinateX + "," + boat1CoordinateY + ")");
                Log.d(TAG,"boat width : " + boatWidth);
                Log.d(TAG,"boat height : " + boatHeight);
                Log.d(TAG,"boat center : (" + boatCoordinateX + "," + boatCoodinateY + ")");
            }
        });

        mPaintLayout.post(new Runnable() {
            @Override
            public void run() {
                int layoutX = mPaintLayout.getWidth();
                int layoutY = mPaintLayout.getHeight();
                paintLayoutScaleList.add(layoutX);
                paintLayoutScaleList.add(layoutY);
            }
        });
    }

    //此方法根据anchor的数据，更新anchor的GPS坐标数据显示
    private void updateAnchorGPSTextView(){
        mAnchor1LongitudeView.setText(mAnchor1.getLongitude()+"");
        mAnchor2LongitudeView.setText(mAnchor2.getLongitude()+"");
        mAnchor3LongitudeView.setText(mAnchor3.getLongitude()+"");
        mAnchor4LongitudeView.setText(mAnchor4.getLongitude()+"");

        mAnchor1LatitudeView.setText(mAnchor1.getLatitude()+"");
        mAnchor2LatitudeView.setText(mAnchor2.getLatitude()+"");
        mAnchor3LatitudeView.setText(mAnchor3.getLatitude()+"");
        mAnchor4LatitudeView.setText(mAnchor4.getLatitude()+"");
    }


    /*
    *  此方法用于计算和记录单个模型视图的坐标位置
    * */
    private void setCoordinatesForSingleModels(Model model,View view,List<Float> scaleList){
        //获取视图左上角在画布上的坐标
        float xx = view.getLeft();
        float yy = view.getTop();
        float zz = view.getBottom();

        float viewWidth = scaleList.get(0);
        float viewHeight = scaleList.get(1);

        // 转化为视图质心在画布上的坐标
        float x = xx + viewWidth / 2;
        float y = yy + viewHeight / 2;
        model.setX(x);
        model.setY(y);

        Log.d(TAG,"[" + xx + "," + yy + "]");
        Log.d(TAG,"width : " + viewWidth);
        Log.d(TAG,"height : " + viewHeight);
        Log.d(TAG,"[" + x + "," + y + "]");

        Log.d(TAG,"heigght :" + "" + (zz - yy));
        Log.d(TAG,"------------------------");
    }





    private void addOnClickListenerForAnchorViews(final Anchor anchor){
        final int anchorId = anchor.getAnchorId();
        View view = IdAnchorViewMap.get(anchorId);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MyConfigDialog configDialog = new MyConfigDialog(MainActivity.this);
                //TODO 设置提示框格式
                configDialog.setDialogTitle("anchor " + anchorId + "  GPS setting");
                configDialog.setDialogTipInfo("请输入GPS坐标");
                configDialog.setFirstLabel("经度: ");
                configDialog.setSecondLabel("纬度: ");
                // 让configDialog 显示修改之前的GPS坐标
                configDialog.setGPSTextBeforeConfig("" + anchor.getLongitude(),
                        "" + anchor.getLatitude());
                // 让configDialog给自己的确定按钮加上封装过的监听器
                configDialog.setSureButtonOnClickListener(new MyConfigDialog.ButtonOnClickListener() {
                    @Override
                    public void buttonOnClick() {
                        //ToDo 更新经纬度显示视图
                        switch (anchorId){
                            case 1:
                            {
                                configDialog.updateAnchorGPSTextViewAfterConfig(mAnchor1LongitudeView,mAnchor1LatitudeView);
                            };
                            break;
                            case 2:
                            {
                                configDialog.updateAnchorGPSTextViewAfterConfig(mAnchor2LongitudeView,mAnchor2LatitudeView);
                            }
                            break;
                            case 3:
                            {
                                configDialog.updateAnchorGPSTextViewAfterConfig(mAnchor3LongitudeView,mAnchor3LatitudeView);
                            }
                            break;
                            case 4:
                            {
                                configDialog.updateAnchorGPSTextViewAfterConfig(mAnchor4LongitudeView,mAnchor4LatitudeView);
                            }
                            break;
                        }
                        // 将dialog中设置的GPS坐标信息赋值给anchor
                        configDialog.setAnchorGPS(anchor);
                        // 将anchor经纬度信息保存到preferences文件中
                        putAnchorDataIntoPreferencesFile();
                        configDialog.dismiss();
                    }
                });
                // 让configDialog给自己的取消按钮加上监听器
                configDialog.setCancelButtonOnClickListener(new MyConfigDialog.ButtonOnClickListener() {
                    @Override
                    public void buttonOnClick() {
                        configDialog.dismiss();
                    }
                });
                configDialog.show();
            }
        });
    }


    //该方法用于初始化Anchor
    private void initAnchorModel(){
        mAnchor1 = new Anchor(1,0,0);
        mAnchor2 = new Anchor(2,0,0);
        mAnchor3 = new Anchor(3,0,0);
        mAnchor4 = new Anchor(4,0,0);
        //由于GPSTextView是根据anchor对象设定数据的，因此，anchorGPSTextView也要更新
        updateAnchorGPSTextView();
    }

    //该方法用于初始化船上的模型
    private void initModelsOnBoat(){

        mNode1 = new RopeNode();
        mNode2 = new RopeNode();
        mNode3 = new RopeNode();
        mNode4 = new RopeNode();

        mRopeNodeBoard = new RopeNodeBoard(mNode1,mNode2,mNode3,mNode4);
        mBoat = new Boat(mRopeNodeBoard);
    }

    private void initScaleLists(){

        anchor1ScaleList = new ArrayList<>();
        anchor2ScaleList = new ArrayList<>();
        anchor3ScaleList = new ArrayList<>();
        anchor4ScaleList = new ArrayList<>();
        boatScaleList = new ArrayList<>();
        ropeDialScaleList = new ArrayList<>();
        paintLayoutScaleList = new ArrayList<>();

    }

    public static Boat getBoat() {
        return mBoat;
    }


    public static Map<Integer, Anchor> getmAnchorMap() {
        return mAnchorMap;
    }


    /*
    *  给船体加上长按监听
    * */
    private void setListenerForBoat(){

        //设置长按监听
        ;mBoatView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //长按之后开始拖拽
                mBoatView.startDrag(null,new View.DragShadowBuilder(v),mPaintLayout,0);
                Log.d(TAG,"long click run");
                return true;
            }
        });

        /*
        * 给画布添加拖拽监听
        * */
        mPaintLayout.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                //Log.d(TAG,"drag event run");
                switch (event.getAction()) {
                    //开始拖动时
                    case DragEvent.ACTION_DRAG_STARTED: {
                        Log.e(TAG, "View开始被拖动");
                        Log.d(TAG,"开始处X : " + event.getX() );
                        Log.d(TAG,"开始处Y : " + event.getY() );
                        break;
                    }
                    case DragEvent.ACTION_DRAG_LOCATION:{
                        /*Log.e(TAG,"运动时X : " + event.getX());
                        Log.e(TAG,"运动时Y : " + event.getY());*/
                        //Log.e(TAG,"View 进入目标区域");
                    }
                    //拖动结束时
                    case DragEvent.ACTION_DRAG_ENDED: {
                        //Log.e(TAG, "View拖动结束");
                        //mBoatView.setVisibility(View.INVISIBLE);
                        break;
                    }
                    case DragEvent.ACTION_DROP:{
                        //TODO 显示幽灵船
                        mBoatShadowView.setVisibility(View.VISIBLE);
                        mBoatView.setVisibility(View.INVISIBLE);
                        Log.e(TAG,"View被放下");
                        Log.d(TAG,"放下处 x : " + event.getX());
                        Log.d(TAG,"放下处 y : " + event.getY());
                        int tranX = (int) (event.getX() - boatCenterX);
                        int tranY = (int) (event.getY() - boatCenterY);
                        mBoatShadowView.setTranslationX(tranX);
                        mBoatShadowView.setTranslationY(tranY);

                        if (mBoatView.getRight() > paintLayoutScaleList.get(0) || mBoatView.getLeft() <= 0 || mBoatView.getTop() <= 0 || mBoatView.getBottom() > paintLayoutScaleList.get(1)){
                            Toast.makeText(MainActivity.this,"船体只能放置在地图内",Toast.LENGTH_SHORT).show();
                        }else {
                            final MyConfigDialog boatMoveDialog = new MyConfigDialog(MainActivity.this);
                            boatMoveDialog.setDialogTitle("船体移动设置");
                            boatMoveDialog.setDialogTipInfo("请输入移动距离");
                            if (tranX >= 0){
                                boatMoveDialog.setFirstLabel("向右: ");
                            }else {
                                boatMoveDialog.setFirstLabel("向左: ");
                            }
                            if (tranY >= 0){
                                boatMoveDialog.setSecondLabel("向下: ");
                            }else {
                                boatMoveDialog.setSecondLabel("向上: ");
                            }
                            boatMoveDialog.setSureButtonOnClickListener(new MyConfigDialog.ButtonOnClickListener() {
                                @Override
                                public void buttonOnClick() {
                                    boatMoveDialog.dismiss();
                                }
                            });

                            boatMoveDialog.setCancelButtonOnClickListener(new MyConfigDialog.ButtonOnClickListener() {
                                @Override
                                public void buttonOnClick() {
                                    boatMoveDialog.dismiss();
                                }
                            });
                            boatMoveDialog.show();
                        }

                        break;
                    }
                    //拖动完成时
                    case DragEvent.ACTION_DRAG_EXITED: {
                        Toast.makeText(MainActivity.this,"船体只能放在地图区域范围内",Toast.LENGTH_SHORT).show();
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

        /*
        * 给船体添加双击监听
        * */
        mBoatView.setOnTouchListener(new OnDoubleClickListener(new OnDoubleClickListener.DoubleOnClickListener() {
            @Override
            public void onDoubleClick() {
                Log.d(TAG,"双击成功");
                //TODO 弹出提示框
                final MyConfigDialog boatRotateDialog = new MyConfigDialog(MainActivity.this);
                boatRotateDialog.setDialogTitle("船体旋转设置");
                boatRotateDialog.setDialogTipInfo("请输入船体旋转角度");
                boatRotateDialog.setFirstLabel("角度: ");
                boatRotateDialog.setSecondEditTextInvisible();
                boatRotateDialog.setSureButtonOnClickListener(new MyConfigDialog.ButtonOnClickListener() {
                    @Override
                    public void buttonOnClick() {
                        boatRotateDialog.updateBoatRotationState(mBoatShadowView);
                        boatRotateDialog.dismiss();
                        mBoatShadowView.setVisibility(View.VISIBLE);
                    }
                });

                boatRotateDialog.setCancelButtonOnClickListener(new MyConfigDialog.ButtonOnClickListener() {
                    @Override
                    public void buttonOnClick() {
                        boatRotateDialog.dismiss();
                    }
                });
                boatRotateDialog.show();
            }
        }));


    }
}
