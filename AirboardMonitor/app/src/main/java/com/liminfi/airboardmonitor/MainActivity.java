package com.liminfi.airboardmonitor;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.TrafficStats;
import android.os.Binder;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        App.Curr = (GlobalData) getApplication();
        App.Curr.permission24 = CheckWindowPermisson(24);

        setHandler(App.Curr);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(MainActivity.this, FxService.class);
        startService(intent);

        new Thread(new Runnable() {
            @Override
            public void run() {
                long upload = TrafficStats.getTotalTxBytes();
                long download = TrafficStats.getTotalRxBytes();
                long currentTime = System.currentTimeMillis();

                while (true) {
                    long uploadNew = TrafficStats.getTotalTxBytes();
                    long downloadNew = TrafficStats.getTotalRxBytes();

                    Message msg = new Message();
                    msg.obj = new MonitorInfo(uploadNew - upload, downloadNew - download);
                    try {
                        App.Curr.mainThreadHandler.sendMessage(msg);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    upload = uploadNew;
                    download = downloadNew;

                    long timeNow = System.currentTimeMillis();
                    try {
                        Thread.sleep(1000 - (timeNow - currentTime));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    currentTime = System.currentTimeMillis();
                }
            }
        }).start();
    }

    public void getUID(){
        //获取所有的安装在手机上的应用软件的信息，并且获取这些软件里面的权限信息
        PackageManager pm = getPackageManager();//获取系统应用包管理
        //获取每个包内的androidmanifest.xml信息，它的权限等等
        List<PackageInfo> pinfos=pm.getInstalledPackages
                (PackageManager.GET_UNINSTALLED_PACKAGES | PackageManager.GET_PERMISSIONS);
        //遍历每个应用包信息
        for(PackageInfo info:pinfos){
            if (info.packageName.startsWith("com.liminfi.airboard") && !info.packageName.startsWith("com.liminfi.airboardmonitor")) {
                App.Curr.UID = info.applicationInfo.uid;
            }
        }
    }

    private boolean CheckWindowPermisson(int op)
    {
        AppOpsManager manager = (AppOpsManager) getBaseContext().getSystemService(Context.APP_OPS_SERVICE);
        try {
            Method method = manager.getClass().getDeclaredMethod("checkOp", int.class, int.class, String.class);
            int property = (Integer) method.invoke(manager, op,
                    Binder.getCallingUid(), getBaseContext().getPackageName());

            if (AppOpsManager.MODE_ALLOWED == property) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {

        }
        return false;
    }

    private void setHandler(final GlobalData app) {
        app.mainThreadHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                MonitorInfo monitorInfo = (MonitorInfo)msg.obj;
                if (app.service != null) {
                    app.service.updateInfo(monitorInfo);
                }
            }
        };
    }
}
