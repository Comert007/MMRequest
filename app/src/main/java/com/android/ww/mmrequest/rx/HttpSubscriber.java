package com.android.ww.mmrequest.rx;

import android.content.Context;
import android.content.DialogInterface;

import cn.pedant.SweetAlert.SweetAlertDialog;
import rx.Subscriber;

/**
 * Created by fighter on 2016/9/6.
 */
public abstract class HttpSubscriber<T> extends Subscriber<T> {
    private SweetAlertDialog dialog;
    private Context context;

    private boolean showDialog;

    public HttpSubscriber(Context context, boolean showDialog) {
        this.context = context;
        this.showDialog = showDialog;
        initDialog();
    }

    private void initDialog() {
        dialog = new SweetAlertDialog(this.context, SweetAlertDialog.PROGRESS_TYPE);
        dialog.setTitleText("loading...");
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (!isUnsubscribed()) {
                    unsubscribe();
                    onCancelRequest();
                }
            }
        });
    }

    private void showDialog() {
        dialog.show();
    }

    private void dismissDialog() {
        dialog.dismiss();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (showDialog) {
            showDialog();
        }
    }

    @Override
    public void onCompleted() {
        dialog.setTitleText("请求成功...")
                .setConfirmText("确认")
                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
        dialog.setTitleText("err:" + e.getMessage())
                .setConfirmText("重新请求")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        onRefreshRequest();
                    }
                }).changeAlertType(SweetAlertDialog.ERROR_TYPE);
    }

    /**
     * 取消请求
     */
    protected void onCancelRequest() {
        dismissDialog();
    }

    /**
     * 刷新请求
     */
    protected void onRefreshRequest() {
        dismissDialog();
    }
}
