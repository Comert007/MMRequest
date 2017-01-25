package ww.com.http;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import rx.Observable;
import rx.Subscriber;
import ww.com.http.body.RequestProgressBody;
import ww.com.http.body.ResponseProgressBody;
import ww.com.http.core.AjaxParams;
import ww.com.http.core.RequestConstants;
import ww.com.http.exception.NetworkException;
import ww.com.http.interfaces.DownloadProgressListener;
import ww.com.http.interfaces.UploadProgressListener;
import ww.com.http.rx.RxHelper;
import ww.com.http.rx.StringFunc;
import ww.com.http.utils.Utils;

public class OkHttpRequest {

    private static final HttpLoggingInterceptor loggingInterceptor;

    static {
        loggingInterceptor =
                new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                    @Override
                    public void log(String message) {
                        try {
                            if (message != null && message.startsWith("{")) {
                                Log.d(TAG, new JSONObject(message).toString(4));
                            } else {
                                Log.d(TAG, message);
                            }
                        } catch (Exception e) {
                            Log.d(TAG, message);
                        }
                    }
                });
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
    }

    private static OkHttpClient sHttpClient = getDefaultClient();

    private static boolean IS_LOGGING_ENABLED = false;
    private static final String TAG = ">>>OkHttpRequest";

    private OkHttpRequest() {
    }

    public static Observable<String> newStringObservable(AjaxParams params) {
        return newObservable(params)
                .map(new StringFunc())
                .compose(RxHelper.<String>cutMain());
    }

    public static Observable<ResponseBody> newObservable(final AjaxParams params) {
        return Observable.create(new Observable.OnSubscribe<ResponseBody>() {
            @Override
            public void call(Subscriber<? super ResponseBody> subscriber) {
                if (!subscriber.isUnsubscribed()) {
                    try {
                        okhttp3.Response response = requestCall(params).execute();
                        if (response != null && response.isSuccessful()) {
                            subscriber.onNext(response.body());
                        } else {
                            subscriber.onError(new NetworkException(NetworkException.TYPE_SERVER_ERR));
                        }
                    } catch (IOException e) {
                        if (e instanceof SocketTimeoutException) {
                            subscriber.onError(new NetworkException(NetworkException.TYPE_TIMEOUT));
                        } else {
                            subscriber.onError(e);
                        }
                    }
                    subscriber.onCompleted();
                }
            }
        });
    }

    public static Call requestCall(AjaxParams params) {
        if (IS_LOGGING_ENABLED) {
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        } else {
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
        }
        Request okHttpRequest = params.getRequest();
        final UploadProgressListener uploadProgressListener = params.getUploadProgressListener();
        final DownloadProgressListener downloadProgressListener = params.getDownloadProgressListener();

        OkHttpClient okHttpClient;

        if (uploadProgressListener != null) {
            // 上传文件的时候 关闭 日志
//            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
            RequestBody body = okHttpRequest.body();
            if (body != null) {
                okHttpRequest = okHttpRequest.newBuilder()
                        .post(new RequestProgressBody(body, uploadProgressListener))
                        .build();
            }
            okHttpClient = getDefaultBuilder().build();
        } else if (downloadProgressListener != null) {
            // 下载文件的时候 关闭 日志
//            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
            okHttpClient = getDefaultBuilder().addNetworkInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Response originalResponse = chain.proceed(chain.request());
                    if (originalResponse.body() == null) {
                        return originalResponse;
                    }
                    return originalResponse.newBuilder()
                            .body(new ResponseProgressBody(originalResponse.body(), downloadProgressListener))
                            .build();
                }
            }).build();
        } else {
            okHttpClient = sHttpClient;
        }

        return okHttpClient.newCall(okHttpRequest);
    }


    public static OkHttpClient getDefaultClient() {
        if (sHttpClient == null) {
            return getDefaultBuilder().addInterceptor(loggingInterceptor)
                    .build();
        }
        return sHttpClient;
    }

    public static OkHttpClient.Builder getDefaultBuilder() {
        return new OkHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS);
    }

    public static void setClientWithCache(Context context) {
        sHttpClient = new OkHttpClient().newBuilder()
                .cache(Utils.getCache(context, RequestConstants.MAX_CACHE_SIZE, RequestConstants.CACHE_DIR_NAME))
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor)
                .build();
    }

    public static void setClient(OkHttpClient okHttpClient) {
        sHttpClient = okHttpClient;
    }

    public static void setLogging(boolean debug) {
        IS_LOGGING_ENABLED = debug;
    }

}
