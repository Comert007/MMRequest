package com.android.ww.mmrequest;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.android.ww.mmrequest.api.TestApi;
import com.android.ww.mmrequest.rx.HttpSubscriber;
import com.trello.rxlifecycle.android.ActivityEvent;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import rx.Subscriber;
import ww.com.http.core.AjaxParams;
import ww.com.http.interfaces.DownloadProgressListener;
import ww.com.http.rx.RxHelper;
import ww.com.http.rx.StreamFunc;
import ww.com.http.rx.StringFunc;

// 测试地址:https://api.github.com/users/fighterwk
public class MainActivity extends RxAppCompatActivity {
    @BindView(R.id.text)
    TextView textView;
    @BindView(R.id.progressBar)
    ProgressBar pbProgress;
    int clickNum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        textView.setMovementMethod(ScrollingMovementMethod.getInstance());

//        // 防止连续点击
//        RxView.clicks(findViewById(R.id.button))
//                .throttleFirst(3, TimeUnit.SECONDS)
//                .subscribe(new Action1<Void>() {
//                    @Override
//                    public void call(Void aVoid) {
//                        final BehaviorSubject subject = BehaviorSubject.create();
////                        textView.postDelayed(new Runnable() {
////                            @Override
////                            public void run() {
////                                System.out.println("发送中止");
////                                subject.onNext("1");
////                            }
////                        }, 1000);
//
//                        Observable.create(new Observable.OnSubscribe<String>() {
//                            @Override
//                            public void call(Subscriber<? super String> subscriber) {
//                                System.out.println("create()");
//                                subscriber.onNext("1");
//                                subscriber.onCompleted();
//                            }
//                        }).delay(10, TimeUnit.SECONDS)
//                                .map(new Func1<String, Integer>() {
//                                    @Override
//                                    public Integer call(String s) {
//                                        System.out.println("1");
//                                        return 200;
//                                    }
//                                }).map(new Func1<Integer, String>() {
//                            @Override
//                            public String call(Integer integer) {
//                                System.out.println("2");
//                                return "200";
//                            }
//                        }).map(new Func1<String, Integer>() {
//                            @Override
//                            public Integer call(String s) {
//                                System.out.println("3");
//                                return 200;
//                            }
//                        }).takeUntil(subject.takeFirst(new Func1<Object, Boolean>() {
//                            @Override
//                            public Boolean call(Object o) {
//                                return true;
//                            }
//                        }))
//                                .subscribe(new Action1<Integer>() {
//                                    @Override
//                                    public void call(Integer integer) {
//                                        System.out.println("call:" + integer);
//                                    }
//                                });
//                    }
//                });
    }

//    private void testHttps() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    new CustomTrust().run();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//    }

    @OnClick(R.id.button)
    public void onJson() {
        AjaxParams params = TestApi.getBaseParams();
        JSONObject json = new JSONObject();
        json.put("name", "nm");
        json.put("age", 10);
        params.addParametersJson(json);

        TestApi.json("http://ota.windward.cn/moreyoung/android", params)
                .compose(RxHelper.<ResponseBody>cutMain())
                .map(new StringFunc())
                .compose(this.<String>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new HttpSubscriber<String>(this, true) {
                    @Override
                    public void onNext(String s) {
                        textView.setText(s);
                    }
                });
    }

    @OnClick(R.id.btn_post)
    public void onPost() {
        AjaxParams params = TestApi.getBaseParams();
        params.addParameters("name", "nm");
        params.addParameters("age", "10");

        TestApi.post("http://ota.windward.cn/moreyoung/android", params)
                .compose(RxHelper.<ResponseBody>cutMain())
                .map(new StringFunc())
                .compose(this.<String>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new HttpSubscriber<String>(this, true) {
                    @Override
                    public void onNext(String s) {
                        textView.setText(s);
                    }
                });
    }

    @OnClick(R.id.btn_down)
    public void onDown() {

        File tagFile = new File(getExternalCacheDir(), "test_down.apk");
        textView.setText("");
        AjaxParams params = TestApi.getBaseParams();
        TestApi.downFile("http://ota.windward.cn/moreyoung/android/moreyoung0906.apk", params,
                new DownloadProgressListener() {
                    @Override
                    public void onProgress(long bytesDownloaded, long totalBytes, boolean done) {
                        int percentage = (int) ((float) bytesDownloaded / (float) totalBytes * 100);
                        Log.d("HttpTest", "percentage : " + percentage);
                        pbProgress.setProgress(percentage);
                    }
                })
                .compose(RxHelper.<ResponseBody>cutMain())
                .map(new StreamFunc(tagFile))
                .compose(this.<Boolean>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {
                        textView.append("下载完成");
                    }

                    @Override
                    public void onError(Throwable e) {
                        textView.append("下载错误: " + e.getMessage());
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        textView.setText("下载:" + (aBoolean ? "成功" : "失败"));
                    }
                });

    }

    @OnClick(R.id.btn_upload)
    public void onUpload() {
        textView.setText("");
        AjaxParams params = TestApi.getBaseParams();
        TestApi.get("https://api.github.com/users/fighterwk", params,
                new DownloadProgressListener() {
                    @Override
                    public void onProgress(long bytesDownloaded, long totalBytes, boolean done) {
                        int percentage = (int) ((float) bytesDownloaded / (float) totalBytes * 100);
                        Log.d("HttpTest", "percentage : " + percentage);
                        pbProgress.setProgress(percentage);
                    }
                })
                .compose(RxHelper.<ResponseBody>cutMain())
                .map(new StringFunc())
                .compose(this.<String>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        textView.append("下载完成");
                    }

                    @Override
                    public void onError(Throwable e) {
                        textView.append("下载错误: " + e.getMessage());
                    }

                    @Override
                    public void onNext(String aBoolean) {
                        textView.setText("下载:\n" + aBoolean);
                    }
                });
    }

}
