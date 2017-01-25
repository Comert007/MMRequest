package ww.com.http.rx;


import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by fighter on 2016/9/5.
 */
public class RxHelper {

    private RxHelper() {
        throw new RuntimeException();
    }

    /**
     * 切换线程
     *
     * @param <T>
     * @return
     */
    public static <T> Observable.Transformer<T, T> cutMain() {
        return new Observable.Transformer<T, T>() {
            @Override
            public Observable<T> call(Observable<T> tObservable) {
                return tObservable.subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

}
