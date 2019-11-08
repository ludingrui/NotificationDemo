package com.test.notificationdemo;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

public class MyAppWidget extends AppWidgetProvider {

//    public static String price = "";
//    public static String time = "";
//    public static String name = "";
//    public static String code = "";
    /**
     * 接收窗口小部件点击时发送的广播
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (intent.getAction().equals("android.appwidget.action.APPWIDGET_UPDATE") || intent.getAction().equals("FirebaseDateRe")){
//            Toast.makeText(context, "Clicked it", Toast.LENGTH_SHORT).show();
            Log.i("AppWidget", "开始了更新");
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.item_layout);

            //这里获得当前的包名，并且用AppWidgetManager来向NewAppWidget.class发送广播。
            AppWidgetManager manager = AppWidgetManager.getInstance(context);
            ComponentName cn = new ComponentName(context, MyAppWidget.class);
            String priceStr = intent.getStringExtra("price");
            String priceHandled = "";
            if(priceStr != null){
                String[] ps = priceStr.split("\\.");
                if(ps.length>1){
                    priceHandled = ps[0] + "." + (ps[1].length() >= 2 ? ps[1].substring(0, 2) : "00") + "CNY";
                }else{
                    priceHandled = ps[0] + ".00CNY";
                }
            }
            rv.setTextViewText(R.id.txPrice, priceHandled);
            rv.setTextViewText(R.id.txName, intent.getStringExtra("name"));
            rv.setTextViewText(R.id.txTime, intent.getStringExtra("time"));
            rv.setTextViewText(R.id.txCode, intent.getStringExtra("code"));
            manager.updateAppWidget(cn, rv);
        }
    }


    /**
     * 每次窗口小部件被更新都调用一次该方法
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.item_layout);

        //这里获得当前的包名，并且用AppWidgetManager来向NewAppWidget.class发送广播。
        AppWidgetManager manager = AppWidgetManager.getInstance(context);

        ComponentName cn = new ComponentName(context, MyAppWidget.class);
//        rv.setTextViewText(R.id.txPrice, String.valueOf(price));
//        rv.setTextViewText(R.id.txName, String.valueOf(name));
//        rv.setTextViewText(R.id.txTime, String.valueOf(time));
//        rv.setTextViewText(R.id.txCode, String.valueOf(code));

        manager.updateAppWidget(cn, rv);
    }
    /**
     * 每删除一次窗口小部件就调用一次
     */
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        //context.stopService(new Intent(context, WidgetService.class));
        Log.i("AppWidget", "删除成功！");
    }
    /**
     * 当该窗口小部件第一次添加到桌面时调用该方法
     */
    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        // Intent mTimerIntent = new Intent(context, WidgetService.class);
        // context.startService(mTimerIntent);
        Log.i("AppWidget", "创建成功！");
    }
    /**
     * 当最后一个该窗口小部件删除时调用该方法
     */
    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        //  Intent mTimerIntent = new Intent(context, WidgetService.class);
        // context.stopService(mTimerIntent);
        Log.i("AppWidget", "删除成功！");
    }
    /**
     * 当小部件大小改变时
     */
    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }
    /**
     * 当小部件从备份恢复时调用该方法
     */
    @Override
    public void onRestored(Context context, int[] oldWidgetIds, int[] newWidgetIds) {
        super.onRestored(context, oldWidgetIds, newWidgetIds);
    }


}
