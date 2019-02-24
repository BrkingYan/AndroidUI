package com.example.yy.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewDebug;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

    // 视图成员
    private ImageView mAnchor1View;
    private ImageView mAnchor2View;
    private ImageView mAnchor3View;
    private ImageView mAnchor4View;

    private BoatView mBoatLayout;//是一个layout 包括船体和绳盘
    private ImageView mBoatView;//船体
    private ImageView mRopeDialView;//绳盘

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化视图成员
        findViewByIds();

        //给anchorView编号
        IdAnchorViewMap = new HashMap<>();
        IdAnchorViewMap.put(1,mAnchor1View);
        IdAnchorViewMap.put(2,mAnchor2View);
        IdAnchorViewMap.put(3,mAnchor3View);
        IdAnchorViewMap.put(4,mAnchor4View);

        //初始化模型成员
        initModelsOnBoat();//创建它们的对象，并初始化


        // 从preferences文件中获取之前设置的anchor经纬度信息
        getAnchorDataFromPreferencesFile();


        // 更新视图的UI坐标数据
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

        mBoatLayout = findViewById(R.id.my_boat_layout);
        mBoatView = findViewById(R.id.boat_view);
        mRopeDialView = findViewById(R.id.rope_dial_view);
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

    private void updateRopeNode(List<Float> ropeDialScaleList){
        float x = mRopeNodeBoard.getX();
        float y = mRopeNodeBoard.getY();
        float width = ropeDialScaleList.get(0);
        float height = ropeDialScaleList.get(1);

        float xNode1 = x - width / 2;
        float yNode1 = y - height / 2;
        mNode1.setX(xNode1);
        mNode1.setY(yNode1);
        Log.d(TAG,"node 1 (" + mNode1.getX() + "," + mNode1.getY() + ")");

        float xNode2 = x + width / 2;
        float yNode2 = y - height / 2;
        mNode2.setX(xNode2);
        mNode2.setY(yNode2);
        Log.d(TAG,"node 2 (" + mNode2.getX() + "," + mNode2.getY() + ")");

        float xNode3 = x + width / 2;
        float yNode3 = y + height / 2;
        mNode3.setX(xNode3);
        mNode3.setY(yNode3);
        Log.d(TAG,"node 3 (" + mNode3.getX() + "," + mNode3.getY() + ")");

        float xNode4 = x - width / 2;
        float yNode4 = y + height / 2;
        mNode4.setX(xNode4);
        mNode4.setY(yNode4);
        Log.d(TAG,"node 4 (" + mNode4.getX() + "," + mNode4.getY() + ")");

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

                setCoordinatesForPackedModels(mBoat,mBoatView,boatScaleList);
            }
        });
        mRopeDialView.post(new Runnable() {
            @Override
            public void run() {
                float viewWidth = mRopeDialView.getWidth();
                ropeDialScaleList.add(viewWidth);
                float viewHeight = mRopeDialView.getHeight();
                ropeDialScaleList.add(viewHeight);

                setCoordinatesForPackedModels(mRopeNodeBoard,mRopeDialView,ropeDialScaleList);

                // 更新船上绳子节点
                updateRopeNode(ropeDialScaleList);
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


/*
 *  此方法用于计算和记录复杂模型视图的坐标位置
 *
 */
    private void setCoordinatesForPackedModels(Model model,View view,List<Float> scaleList){
        //获取视图左上角在InnerLayout上的坐标
        float xxOnInnerLayout = view.getLeft();
        float yyOnInnerLayout = view.getTop();
        float zzOnInnerLayout = view.getBottom();

        //获取InnerLayout左上角在OuterLayout上的位置坐标
        float layoutX = mBoatLayout.getLeft();
        float layoutY = mBoatLayout.getTop();
        float layoutRight = mBoatLayout.getRight();
        float layoutBottom = mBoatLayout.getBottom();
        //获取InnerLayout的尺寸
        float layoutWidth = layoutRight - layoutX;
        float layoutHeight = layoutBottom - layoutY;
        //将视图左上角的坐标系由InnerLayout转换为OuterLayout
        float xxOnOuterLayout = xxOnInnerLayout + layoutX;
        float yyOnOuterLayout = yyOnInnerLayout + layoutY;

        float viewWidth = scaleList.get(0);
        float viewHeight = scaleList.get(1);

        // 转化为视图质心在画布上的坐标
        float xOnOuterLayout = xxOnOuterLayout + viewWidth / 2;
        float yOnOuterLayout = yyOnOuterLayout + viewHeight / 2;

        model.setX(xOnOuterLayout);
        model.setY(yOnOuterLayout);

        Log.d(TAG,"[" + xxOnInnerLayout + "," + yyOnInnerLayout + "]");
        Log.d(TAG,"width : " + viewWidth);
        Log.d(TAG,"height : " + viewHeight);
        Log.d(TAG,"[" + xOnOuterLayout + "," + yOnOuterLayout + "]");

        Log.d(TAG,"heigght :" + "" + (zzOnInnerLayout - yyOnInnerLayout));
        Log.d(TAG,"------------------------");
    }


    private void addOnClickListenerForAnchorViews(final Anchor anchor){
        final int anchorId = anchor.getAnchorId();
        View view = IdAnchorViewMap.get(anchorId);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AnchorConfigDialog configDialog = new AnchorConfigDialog(MainActivity.this);
                configDialog.setDialogTitle("anchor " + anchorId + "  GPS setting");
                // 让configDialog 显示修改之前的GPS坐标
                configDialog.setGPSTextBeforeConfig("" + anchor.getLongitude(),
                        "" + anchor.getLatitude());
                // 让configDialog给自己的确定按钮加上封装过的监听器
                configDialog.setSureButtonOnClickListener(new AnchorConfigDialog.ButtonOnClickListener() {
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
                configDialog.setCancelButtonOnClickListener(new AnchorConfigDialog.ButtonOnClickListener() {
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

    }

    public static Boat getBoat() {
        return mBoat;
    }

    public static Map<Integer, Anchor> getmAnchorMap() {
        return mAnchorMap;
    }
}
