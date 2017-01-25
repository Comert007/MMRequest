package ww.com.http.exception;

/**
 * 自定义异常
 * Created by fighter on 2016/9/5.
 */
public class CustomException extends RuntimeException {
    private String message;  // 异常提示信息

    public CustomException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
