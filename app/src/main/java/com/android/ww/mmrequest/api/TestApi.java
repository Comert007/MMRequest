package com.android.ww.mmrequest.api;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import okhttp3.ResponseBody;
import rx.Observable;
import ww.com.http.OkHttpRequest;
import ww.com.http.core.AjaxParams;
import ww.com.http.core.RequestConstants;
import ww.com.http.core.RequestMethod;
import ww.com.http.interfaces.DownloadProgressListener;
import ww.com.http.interfaces.UploadProgressListener;

/**
 * Created by fighter on 2016/9/5.
 */
public class TestApi {

    private TestApi() {
        throw new RuntimeException();
    }

    /**
     * 上传文件
     *
     * @param url
     * @param params
     * @param progressListener
     * @return
     */
    public static final Observable<ResponseBody> updateFile(String url,
                                                            AjaxParams params,
                                                            @Nullable UploadProgressListener progressListener) {
        params = params.setBaseUrl(url)
                .setUploadProgressListener(progressListener)
                .setRequestMethod(RequestMethod.POST);
        return OkHttpRequest.newObservable(params);
    }

    public static final Observable<ResponseBody> get(String url, AjaxParams params,
                                                          @NonNull DownloadProgressListener progressListener) {
        params = params.setBaseUrl(url)
                .setDownloadProgressListener(progressListener)
                .setRequestMethod(RequestMethod.GET);
        return OkHttpRequest.newObservable(params);
    }

    /**
     * 下载文件
     *
     * @param url
     * @param params
     * @param progressListener
     * @return
     */
    public static final Observable<ResponseBody> downFile(String url, AjaxParams params,
                                                          @NonNull DownloadProgressListener progressListener) {
        params = params.setBaseUrl(url)
                .setDownloadProgressListener(progressListener)
                .setRequestMethod(RequestMethod.GET);
        return OkHttpRequest.newObservable(params);
    }

    /**
     * @param url    请求的地址
     * @param params 请求的参数
     * @return
     */
    public static final Observable<ResponseBody> post(String url,
                                                      AjaxParams params) {
        params = params.setBaseUrl(url)
                .setRequestMethod(RequestMethod.POST);
        return OkHttpRequest.newObservable(params);
    }

    /**
     * @param url    请求的地址
     * @param params 请求的参数
     * @return
     */
    public static final Observable<ResponseBody> json(String url,
                                                         AjaxParams params) {
        params = params.setBaseUrl(url)
                .setRequestMethod(RequestMethod.JSON);
        return OkHttpRequest.newObservable(params);
    }

    public static final AjaxParams getBaseParams() {
        AjaxParams params = new AjaxParams();
        params.addHeaders(RequestConstants.USER_AGENT, "user-agent;android ww request v2.0;use okhttp 3.0");
        return params;
    }
}
