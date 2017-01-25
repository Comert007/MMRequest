package ww.com.http.exception;

import java.util.HashMap;
import java.util.Map;

/**
 * 网络异常
 * <pre>
 *
 * </pre>
 * Created by fighter on 2016/9/5.
 */
public class NetworkException extends CustomException {
    /**
     * 无网络
     */
    public static final int TYPE_NOT_NETWORK = 101;
    /**
     * 服务器错误(500, 400)
     */
    public static final int TYPE_SERVER_ERR = 102;
    /**
     * 连接超时
     */
    public static final int TYPE_TIMEOUT = 103;

    /**
     * 未知错误, 网络异常
     */
    public static final int TYPE_DEFAULT = 100;

    private static Map<Integer, String> errInfoList;

    static {
        errInfoList = new HashMap<>();
        errInfoList.put(TYPE_DEFAULT, "请求失败,网络异常");
        errInfoList.put(TYPE_NOT_NETWORK, "请求失败,请检查网络是否连接正常");
        errInfoList.put(TYPE_TIMEOUT, "请求失败,连接服务器超时");
        errInfoList.put(TYPE_SERVER_ERR, "请求失败,服务器异常");
    }


    public int type;


    public NetworkException(int type) {
        super(errInfoList.get(type));
        this.type = type;
    }

    public int getErrorType() {
        return type;
    }
}
