package com.liminfi.airboardmonitor;

/**
 * Created by admin on 2017/1/17.
 */

public class MonitorInfo {
    public long upload;
    public long download;

    MonitorInfo(long upload, long download) {
        this.upload = upload;
        this.download = download;
    }
}
