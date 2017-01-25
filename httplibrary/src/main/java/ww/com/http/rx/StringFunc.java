package ww.com.http.rx;

import java.io.IOException;

import okhttp3.ResponseBody;
import rx.functions.Func1;

/**
 * Created by fighter on 2016/9/13.
 */
public class StringFunc implements Func1<ResponseBody, String> {
    @Override
    public String call(ResponseBody responseBody) {
        try {
            return responseBody.string();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
