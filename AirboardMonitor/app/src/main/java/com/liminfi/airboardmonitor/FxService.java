package com.liminfi.airboardmonitor;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.IBinder;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2017/1/17.
 */

public class FxService extends Service
{
    private static final String TAG = "FxService";
    //切换前后台回调
    MyActivityLifecycleCallbacks myActivityLifecycleCallbacks;

    //定义浮动窗口布局
    LinearLayout mFloatLayout;

    WindowManager.LayoutParams wmParams,lastParm;

    //创建浮动窗口设置布局参数的对象
    WindowManager mWindowManager;
    WindowManager wm;//为了找全局长宽
    ActivityManager activityManager;

    //一些按钮
    public ImageView ButtonMute,ButtonCollapse;
    private ImageView ButtonClose,ButtonChangeCam;
    private ImageView ButtonBig,ButtonSmall;

    //一些参数
    private int defaultWidth=500,defaultHeight=250;

    private int upload = 0;
    private int download = 0;

    @Override
    public void onCreate()
    {
        // TODO Auto-generated method stub
        super.onCreate();
        App.Curr.service = this;
        createFloatView();

        (new Thread(new Runnable() {
            @Override
            public void run() {
            }
        })).start();
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        // TODO Auto-generated method stub
        return null;
    }

    private void createFloatView() {
        wmParams = new WindowManager.LayoutParams();
        lastParm = new WindowManager.LayoutParams();
        //获取WindowManagerImpl.CompatModeWrapper
        mWindowManager = (WindowManager) getApplication().getSystemService(getApplication().WINDOW_SERVICE);
        wm = (WindowManager) getBaseContext().getSystemService(Context.WINDOW_SERVICE);
        activityManager = (ActivityManager) getApplication().getSystemService(Context.ACTIVITY_SERVICE);
        //设置window type
        if (App.Curr.permission24 == true)
            wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        else {
            wmParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        }
        //设置图片格式，效果为背景透明
        wmParams.format = PixelFormat.RGBA_8888;
        //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        wmParams.flags =
//          LayoutParams.FLAG_NOT_TOUCH_MODAL |
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
                        WindowManager.LayoutParams.FLAG_FULLSCREEN |
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED;
//          LayoutParams.FLAG_NOT_TOUCHABLE
        ;
        //调整悬浮窗显示的停靠位置为左侧居中
        wmParams.gravity = Gravity.LEFT | Gravity.CENTER;

        wmParams.x = 0;
        wmParams.y = 0;

        wmParams.width = defaultWidth;
        wmParams.height = defaultHeight;

        //设置悬浮窗口长宽数据
        //wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        //wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        LayoutInflater inflater = LayoutInflater.from(getApplication());
        mFloatLayout = (LinearLayout) inflater.inflate(R.layout.monitor, null);

        //添加mFloatLayout
        mWindowManager.addView(mFloatLayout, wmParams);
    }

    public void updateInfo(MonitorInfo monitorInfo) {
        ((TextView)mFloatLayout.findViewById(R.id.info)).setText(
                String.format("发送: %.1f K/S\n接收: %.1f K/S", (float)monitorInfo.upload / 1024f, (float)monitorInfo.download / 1024f)
        );
    }

    public void stop()
    {
        //3移除Views，很重要，否则退会会崩溃
        if(mFloatLayout != null)
        {
            mFloatLayout.removeAllViews();
            mWindowManager.removeView(mFloatLayout);
        }
    }

    @Override
    public void onDestroy()
    {
        // TODO Auto-generated method stub
        super.onDestroy();
    }
    public class MyActivityLifecycleCallbacks  implements Application.ActivityLifecycleCallbacks {

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        }

        @Override
        public void onActivityStarted(Activity activity) {

        }

        @Override
        public void onActivityResumed(Activity activity) {
        }

        @Override
        public void onActivityPaused(Activity activity) {
        }
        @Override
        public void onActivityStopped(Activity activity) {
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {

        }
    }
}
