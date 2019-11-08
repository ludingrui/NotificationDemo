package com.test.notificationdemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {
    String TAG = "FirebaseInstanceId";
    EditText tvToken;
    TextView price;
    TextView time;
    TextView code;
    TextView name;
    Button confirm;
    Button cancel;
    boolean pullData = false;
    EditText pullTime;
    LineChart lineChart;
    ArrayList<Entry> valsComp1;
    ArrayList<Entry> valsComp2;
    ArrayList<ILineDataSet> dataSets;
    LineDataSet setComp1;
    LineDataSet setComp2;
    LinearLayout llItem;
    WindowManager.LayoutParams layoutParams;
    WindowManager windowManager;
    View subView;
    private static final int REQUEST_CODE = 9999;
    FirebaseDateRe firebaseDateRe;

    Retrofit retrofit=new Retrofit.Builder()
            .baseUrl("https://fakeapi.splatoon.top:4443/price/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    APIService retrofitApi=retrofit.create(APIService.class);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button getToken = findViewById(R.id.getToken);
        tvToken = findViewById(R.id.tv_token);
        price = findViewById(R.id.txPrice);
        time = findViewById(R.id.txTime);
        code = findViewById(R.id.txCode);
        name = findViewById(R.id.txName);

        getToken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if(!task.isSuccessful()){
                            Log.d(TAG,"FirebaseInstanceId failed", task.getException());
                        }
                        String token  = task.getResult().getToken();
                        tvToken.setText(token);
                        Log.d(TAG, token);
                    }
                });
            }
        });
        FirebaseMessaging.getInstance().subscribeToTopic("TopicA");
        initButtons();
        initKLineChat();
        firebaseDateRe = new FirebaseDateRe();
        registerReceiver(firebaseDateRe, new IntentFilter("FirebaseDateRe"));

    }

    private void initKLineChat() {
        lineChart = findViewById(R.id.lineChat);
        //上面右边效果图的部分代码，设置X轴
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // 设置X轴的位置
        xAxis.setEnabled(false);
        xAxis.setAxisMaximum(10.0f);
//        xAxis.setDrawAxisLine(true);
//
//        xAxis.setDrawGridLines(true); // 效果如下图

        lineChart.setContentDescription("0000");
        Description description = new Description();
        description.setText("Time");
        lineChart.setDescription(description);

        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.setAxisMaximum(10000.0f);
        YAxis rightYxis = lineChart.getAxisRight();
        rightYxis.setEnabled(false);

        valsComp1 = new ArrayList<Entry>();
        valsComp2 = new ArrayList<Entry>();

        Entry c1e1 = new Entry(0.000f, 0, "aaa"); // 0 == quarter 1
        valsComp1.add(c1e1);
//        Entry c1e2 = new Entry(1.000f, 5000, "bbb");  // 1 == quarter 2 ...
//        valsComp1.add(c1e2);
//        Entry c1e3 = new Entry(2.000f, 2300, "ccc");  // 1 == quarter 2 ...
//        valsComp1.add(c1e3);
        // and so on ...


        Entry c2e1 = new Entry(0.000f, 0); // 0 == quarter 1
        valsComp2.add(c2e1);
//        Entry c2e2 = new Entry(60.000f, 3600); // 1 == quarter 2 ...
//        valsComp2.add(c2e2);
//        Entry c2e3 = new Entry(100.000f, 1200); // 0 == quarter 1
//        valsComp2.add(c2e3);

        setComp1 = new LineDataSet(valsComp1, "PUSH");
        setComp1.setAxisDependency(YAxis.AxisDependency.LEFT);
        setComp1.setColor(Color.RED);
        setComp1.setLineWidth(2.0f);

        setComp2 = new LineDataSet(valsComp2, "PULL");
        setComp2.setAxisDependency(YAxis.AxisDependency.LEFT);
        setComp2.setColor(Color.GREEN);
        setComp2.setLineWidth(2.0f);

        dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(setComp1);
        dataSets.add(setComp2);



        ArrayList<String> xVals = new ArrayList<String>();
        xVals.add("1.Q"); xVals.add("2.Q"); xVals.add("3.Q"); xVals.add("4.Q");

        LineData data = new LineData(dataSets);

        lineChart.setData(data);
        lineChart.invalidate(); // refresh
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                valsComp2.remove(0);
                valsComp1.remove(0);
                lineChart.invalidate();
            }
        }, 100);
    }

    private void initButtons() {
        Button push = findViewById(R.id.bPush);
        Button pull = findViewById(R.id.bPull);
        final LinearLayout llPush = findViewById(R.id.llPush);
        final LinearLayout llPUll = findViewById(R.id.llPull);
        final CheckBox checked = findViewById(R.id.checked);
        confirm = findViewById(R.id.confirm);
        cancel = findViewById(R.id.cancel);
        pullTime = findViewById(R.id.pullTime);
        push.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llPush.setVisibility(View.VISIBLE);
                llPUll.setVisibility(View.GONE);
                pullData = false;
            }
        });
        pull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llPush.setVisibility(View.GONE);
                llPUll.setVisibility(View.VISIBLE);
                pullData = true;
                getData();
            }
        });
        llItem = findViewById(R.id.llItem);
        Button bItem = findViewById(R.id.bItem);
        Button bLine = findViewById(R.id.bLine);
        bItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llItem.setVisibility(View.VISIBLE);
                lineChart.setVisibility(View.GONE);
            }
        });
        bLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llItem.setVisibility(View.GONE);
                lineChart.setVisibility(View.VISIBLE);
            }
        });
        checked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
//                    checked.setBackground(MainActivity.this.getDrawable(R.mipmap.checkbox_checked));
//                    FirebaseMessaging.getInstance().subscribeToTopic("TopicA");
                }else{
//                    checked.setBackground(MainActivity.this.getDrawable(R.mipmap.checkbox_unchecked));
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("TopicA");
                }
            }
        });


        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pullData = true;
                getData();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pullData = false;
            }
        });
        CheckBox subWindow = findViewById(R.id.subWindow);
        subWindow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    if(!Settings.canDrawOverlays(MainActivity.this)){
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:" + MainActivity.this.getPackageName()));
                        MainActivity.this.startActivityForResult(intent, REQUEST_CODE);
                    }else{
                        createSubWindow();
                    }
                }else{
                    windowManager.removeViewImmediate(subView);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE:
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return;
                if (!Settings.canDrawOverlays(MainActivity.this)) {
                    Toast.makeText(this,"悬浮窗权限未开启，请在设置中手动打开", Toast.LENGTH_SHORT);
                    return;
                }
                createSubWindow();
                break;
        }
    }

    private void getData(){
        if(!pullData) return;

        Call<PriceItem> userCall=retrofitApi.getData(0);
        //发送网络请求(异步)
        userCall.enqueue(new Callback<PriceItem>() {
            //请求成功时回调
            @Override
            public void onResponse(@NonNull Call<PriceItem> call, @NonNull Response<PriceItem> response) {
                //请求处理,输出结果
                assert response.body() != null;
                try {
                    Log.d(TAG,"ResponseBody " + response.body().toString());
                    PriceItem item = response.body();
                    String priceStr = String.valueOf(item.price);
                    Log.d(TAG,"priceStr " + priceStr);
                    String[] ps = priceStr.split("\\.");
                    String priceHandled;
                    if(ps.length>1){
                        priceHandled = ps[0] + "." + (ps[1].length() >= 2 ? ps[1].substring(0, 2) : "00") + "CNY";
                    }else{
                        priceHandled = ps[0] + ".00CNY";
                    }
                    price.setText(priceHandled);
//                    time.setText(item.time.substring(item.time.length() - 8));
                    time.setText(item.time);
                    name.setText(item.name);
                    code.setText(item.id);
                    drawPullData(item.price);
//                    updateSubWindowAndWidget(priceHandled, item.name, item.time.substring(item.time.length() - 8), item.id, true);
                    updateSubWindowAndWidget(priceHandled, item.name, item.time, item.id, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //请求失败时候的回调
            @Override
            public void onFailure(@NonNull Call<PriceItem> call, @NonNull Throwable throwable) {
                System.out.println("连接失败");
            }
        });
        if(pullTime.getText().toString() == null || "".equals(pullTime.getText().toString())){
            return;
        }
        int timeSeconds = Integer.valueOf(pullTime.getText().toString());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getData();
            }
        }, timeSeconds * 1000);
    }



    class FirebaseDateRe extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG,"onReceive1111 " + intent.getAction());
            if(intent.getAction().equals("FirebaseDateRe")){
                String priceStr = intent.getStringExtra("price");
                String timeStr = intent.getStringExtra("time");
                String nameStr = intent.getStringExtra("name");
                String codeStr = intent.getStringExtra("code");
                Log.d(TAG,"priceStr " + priceStr);
                String priceHandled = "";
                if(priceStr != null){
                    String[] ps = priceStr.split("\\.");
                    if(ps.length>1){
                        priceHandled = ps[0] + "." + (ps[1].length() >= 2 ? ps[1].substring(0, 2) : "00") + "CNY";
                    }else{
                        priceHandled = ps[0] + ".00CNY";
                    }
                }
                price.setText(priceHandled);
                time.setText(timeStr);
                name.setText(nameStr);
                code.setText(codeStr);
                drawPushData(Float.valueOf(priceStr));
                updateSubWindowAndWidget(priceHandled, nameStr, timeStr, codeStr, false);
            }
        }
    }

    private void drawPullData(float price){
        if(setComp2 == null){
//            setComp2 = new LineDataSet(valsComp2, "PULL");
//            setComp2.setAxisDependency(YAxis.AxisDependency.LEFT);
//            setComp2.setColor(Color.GREEN);
//            setComp2.setLineWidth(2.0f);
        }
        if(valsComp2.size() < 10){
            Entry c2e1 = new Entry((float) (valsComp2.size()), price); // 0 == quarter 1
            valsComp2.add(c2e1);
        }else{
            valsComp2.remove(0);
            for(int i = 0; i < valsComp2.size(); i++){
               valsComp2.get(i).setX((float)i);
            }
            Entry c2e1 = new Entry(9.0f, price); // 0 == quarter 1
            valsComp2.add(c2e1);
        }
        lineChart.invalidate();
    }

    private void drawPushData(float price){
        if(valsComp1.size() < 10){
            Entry c1e1 = new Entry((float) (valsComp1.size()), price); // 0 == quarter 1
            valsComp1.add(c1e1);
        }else{
            valsComp1.remove(0);
            for(int i = 0; i < valsComp1.size(); i++){
                valsComp1.get(i).setX((float)i);
            }
            Entry c1e1 = new Entry(9.0f, price); // 0 == quarter 1
            valsComp1.add(c1e1);
        }
        lineChart.invalidate();
    }

    private void createSubWindow(){
        subView = LayoutInflater.from(this).inflate(R.layout.item_layout, null);
        subView.setOnTouchListener(this);
        TextView txPrice = subView.findViewById(R.id.txPrice);
        TextView txName = subView.findViewById(R.id.txName);
        TextView txTime = subView.findViewById(R.id.txTime);
        TextView txCode = subView.findViewById(R.id.txCode);
        txPrice.setText(price.getText());
        txName.setText(name.getText());
        txTime.setText(time.getText());
        txCode.setText(code.getText());
        windowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        int screenWidth = 0, screenHeight = 0;
        if (windowManager != null) {
            //获取屏幕的宽和高
            Point point = new Point();
            windowManager.getDefaultDisplay().getSize(point);
            screenWidth = point.x;
            screenHeight = point.y;
            layoutParams = new WindowManager.LayoutParams();
//            layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
//            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            layoutParams.width = 600;
            layoutParams.height = 300;
            //设置type
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                //26及以上必须使用TYPE_APPLICATION_OVERLAY   @deprecated TYPE_PHONE
                layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            } else {
                layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
            }
            //设置flags
            layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
            layoutParams.gravity = Gravity.START | Gravity.TOP;
            //背景设置成透明
            layoutParams.format = PixelFormat.TRANSPARENT;
            layoutParams.x = screenWidth;
            layoutParams.y = screenHeight / 2;
            //将View添加到屏幕上
            windowManager.addView(subView, layoutParams);
        }
    }
    private void updateSubWindowAndWidget(String price,String name,String time,String code, boolean isPull){
        if(subView != null) {
            TextView txPrice = subView.findViewById(R.id.txPrice);
            TextView txName = subView.findViewById(R.id.txName);
            TextView txTime = subView.findViewById(R.id.txTime);
            TextView txCode = subView.findViewById(R.id.txCode);
            txPrice.setText(price);
            txName.setText(name);
            txTime.setText(time);
            txCode.setText(code);
        }
        if(isPull){
            Intent intent = new Intent("android.appwidget.action.APPWIDGET_UPDATE");
//        MyAppWidget.price = price;
//        MyAppWidget.time = time;
//        MyAppWidget.name = name;
//        MyAppWidget.code = code;
            intent.putExtra("price", price);
            intent.putExtra("time", time);
            intent.putExtra("name", name);
            intent.putExtra("code", code);
            sendBroadcast(intent);
        }

    }
    int mLastX;
    int mLastY;
    @Override
    public boolean onTouch(View view, MotionEvent event) {
        int mInScreenX = (int) event.getRawX();
        int mInScreenY = (int) event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastX = (int) event.getRawX();
                mLastY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                layoutParams.x += mInScreenX - mLastX;
                layoutParams.y += mInScreenY - mLastY;
                mLastX = mInScreenX;
                mLastY = mInScreenY;
                windowManager.updateViewLayout(view, layoutParams);
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        windowManager.removeView(subView);
        unregisterReceiver(firebaseDateRe);
    }
}
