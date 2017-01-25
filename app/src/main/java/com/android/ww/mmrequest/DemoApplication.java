package com.android.ww.mmrequest;

import android.app.Application;

import ww.com.http.OkHttpRequest;

/**
 * Created by fighter on 2016/8/1.
 */
public class DemoApplication extends Application {
    private static DemoApplication instance;

    public static final DemoApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        // 开启网络日志: 设置了上传和下载 progress 监听 日志无效
        OkHttpRequest.setLogging(BuildConfig.DEBUG);
    }

}
