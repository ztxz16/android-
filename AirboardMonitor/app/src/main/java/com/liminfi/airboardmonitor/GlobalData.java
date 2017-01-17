package com.liminfi.airboardmonitor;

import android.app.Application;
import android.app.Service;
import android.os.Handler;

/**
 * Created by admin on 2017/1/17.
 */

public class GlobalData extends Application {
    public boolean permission24;
    public int UID;

    public Handler mainThreadHandler; //用来向主线程发送命令
    public FxService service; //用来控制悬浮窗
}
