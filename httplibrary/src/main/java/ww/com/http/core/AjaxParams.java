package ww.com.http.core;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import ww.com.http.interfaces.DownloadProgressListener;
import ww.com.http.interfaces.UploadProgressListener;

public class AjaxParams implements Serializable{

    private static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");
    private static final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown; charset=utf-8");

    private String url;
    private RequestMethod method = RequestMethod.POST;

    private List<BaseParams> baseParams;  // post|get 字符参数
    private List<FileParams> fileParams;  // post 流参数
    private Map<String, String> headers;  // http 请求头设置
    private JSONObject jsonParams;        // post body json请求
    private byte[] bufs;                  // post 二进制流

    private UploadProgressListener uploadProgressListener;
    private DownloadProgressListener downloadProgressListener;

    public AjaxParams() {
        headers = new HashMap<>();
    }

    public AjaxParams addParameters(String _key, String _value) {
        if (baseParams == null) {
            baseParams = new ArrayList<>();
        }
        if (_value != null) {
            baseParams.add(new BaseParams(_key, _value));
        }
        return this;
    }

    @Override
    public String toString() {
        String ajaxParams ="";
        for (int i = 0; i < baseParams.size(); i++) {
            ajaxParams= ajaxParams+baseParams.get(i).get_key()+":"+baseParams.get(i).get_value()+",";
        }
        return "AjaxParams{" +
                "baseParams=" + ajaxParams +
                '}';
    }

    public AjaxParams addParametersFile(String _key, File _file, String mimeType) {
        if (fileParams == null) {
            fileParams = new ArrayList<>();
        }
        if (_file != null && _file.exists()) {
            fileParams.add(new FileParams(_key, _file, MediaType
                    .parse(mimeType)));
        }
        return this;
    }

    public AjaxParams addParametersJson(JSONObject json) {
        jsonParams = json;
        return this;
    }

    public JSONObject getJsonParams() {
        if (jsonParams == null) {
            jsonParams = new JSONObject();
        }
        return jsonParams;
    }

    public AjaxParams addBytes(byte[] bufs) {
        this.bufs = bufs;
        return this;
    }

    public AjaxParams addParametersMp4(String _key, File _file) {
        addParametersFile(_key, _file, "video/mp4");
        return this;
    }

    public AjaxParams addParametersJPG(String _key, File _file) {
        addParametersFile(_key, _file, "image/jpg");
        return this;
    }

    public AjaxParams addParametersPNG(String _key, File _file) {
        addParametersFile(_key, _file, "image/png");
        return this;
    }

    public AjaxParams addHeaders(String name, String value) {
        headers.put(name, value);
        return this;
    }

    public UploadProgressListener getUploadProgressListener() {
        return uploadProgressListener;
    }

    public AjaxParams setUploadProgressListener(UploadProgressListener uploadProgressListener) {
        this.uploadProgressListener = uploadProgressListener;
        return this;
    }

    public DownloadProgressListener getDownloadProgressListener() {
        return downloadProgressListener;
    }

    public AjaxParams setDownloadProgressListener(DownloadProgressListener downloadProgressListener) {
        this.downloadProgressListener = downloadProgressListener;
        return this;
    }

    public AjaxParams setBaseUrl(String url) {
        this.url = url;
        return this;
    }

    public AjaxParams setRequestMethod(RequestMethod method) {
        this.method = method;
        return this;
    }

    public Request getRequest() {
        Request.Builder builder = new Request.Builder();
        Set<String> names = headers.keySet();
        for (String key : names) {
            String value = headers.get(key);
            builder.addHeader(key, value);
        }

        String tempUrl = url;

        switch (method) {
            case POST:
                builder.post(getPostRequestBody());
                break;
            case GET:
                tempUrl = getGetUrl();
                break;
            case PROTOBUF:
                builder.post(getByteRequestBody());
                break;
            case JSON:
                builder.post(getJsonRequestBody());
                break;
        }
        builder.url(tempUrl);
        return builder.build();
    }

//    public RequestMethod getRequestMethod() {
//        return method;
//    }

    private String getGetUrl() {
        if (TextUtils.isEmpty(url)) {
            throw new RuntimeException("base url is null");
        }
        String tempUrl = url;
        HttpUrl.Builder hb = HttpUrl.parse(tempUrl)
                .newBuilder();
        if (baseParams != null) {
            for (AjaxParams.BaseParams baseParam : baseParams) {
                hb.addEncodedQueryParameter(baseParam.get_key(), baseParam.get_value());
            }
        }
        return hb.build().toString();
    }

    private RequestBody getJsonRequestBody() {
        String jsonStr = "{}";
        if (jsonParams != null) {
            jsonStr = jsonParams.toJSONString();
        }
        return RequestBody.create(JSON_MEDIA_TYPE, jsonStr);
    }

    private RequestBody getByteRequestBody() {
        if (bufs == null) {
            throw new NullPointerException("byte is null");
        }

        return RequestBody.create(MEDIA_TYPE_MARKDOWN, bufs);
    }

    private RequestBody getPostRequestBody() {
        RequestBody body = null;
        if (fileParams != null && !fileParams.isEmpty()) {
            MultipartBody.Builder mb = new MultipartBody.Builder();
            mb.setType(MultipartBody.FORM);
            for (AjaxParams.FileParams params : fileParams) {
                String key = params.get_key();
                String fileName = params.getFileName();

                mb.addFormDataPart(
                        key,
                        fileName,
                        RequestBody.create(params.getMediaType(),
                                params.getFile()));
            }

            if (baseParams != null) {
                for (AjaxParams.BaseParams params : baseParams) {
                    String key = params.get_key();
                    String value = params.get_value();
                    mb.addFormDataPart(key, value);
                }
            }

            body = mb.build();
        } else {
            FormBody.Builder fb = new FormBody.Builder();
            if (baseParams != null) {
                for (AjaxParams.BaseParams params : baseParams) {
                    String key = params.get_key();
                    String value = params.get_value();
                    fb.add(key, value);
                }
            }
            body = fb.build();
        }
        return body;
    }

    /**
     * 转换 base 到 map
     *
     * @return
     */
    public Map<String, Object> convertParamsBaseToMap() {
        Map<String, Object> keys = new HashMap<>();
        if (baseParams != null) {
            for (BaseParams baseParam : baseParams) {
                keys.put(baseParam.get_key(), baseParam.get_value());
            }
        }
        return keys;
    }

    /**
     * 转换参数 base 到 json
     *
     * @return
     */
    public JSONObject convertParamsBaseToJson() {
        return new JSONObject(convertParamsBaseToMap());
    }

    public List<BaseParams> getBaseParams() {
        return baseParams;
    }

    public List<FileParams> getFileParams() {
        return fileParams;
    }

    public Map<String, String> getHeader() {
        return headers;
    }

    public byte[] getBufs() {
        return this.bufs;
    }

    /**
     * 基础参数
     */
    public static final class BaseParams {
        private String _key;
        private String _value;

        private BaseParams(String _key, String _value) {
            super();
            this._key = _key;
            this._value = _value;
        }

        public String get_key() {
            return _key;
        }

        public String get_value() {
            return _value;
        }

    }

    /**
     * 文件参数
     */
    public static final class FileParams {
        private String _key;
        private File file;
        private MediaType mediaType;

        private FileParams(String _key, File file, MediaType mediaType) {
            super();
            this._key = _key;
            this.file = file;
            this.mediaType = mediaType;
        }

        public String get_key() {
            return _key;
        }

        public File getFile() {
            return file;
        }

        public MediaType getMediaType() {
            return mediaType;
        }


        public String getFileName() {
            if (file == null) {
                return "unknown_filename";
            }

            return file.getName();
        }
    }

}
