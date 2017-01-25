package ww.com.http.exception;

/**
 * 请求数据失败
 * Created by fighter on 2016/9/5.
 */
public class RequestErr extends CustomException {
    private int errCode;

    public RequestErr(String message, int errCode) {
        super(message);
        this.errCode = errCode;
    }

    public int getErrCode() {
        return errCode;
    }
}
