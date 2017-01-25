package ww.com.http.rx;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.ResponseBody;
import rx.functions.Func1;
import ww.com.http.utils.StreamUtils;

/**
 * Created by fighter on 2016/9/13.
 */
public class StreamFunc implements Func1<ResponseBody, Boolean> {

    private File tagFile;

    public StreamFunc(File tagFile) {
        this.tagFile = tagFile;
    }

    @Override
    public Boolean call(ResponseBody responseBody) {
        InputStream inputStream = responseBody.byteStream();
        Boolean bool = true;
        try {
            FileOutputStream fos = new FileOutputStream(tagFile);
            StreamUtils.copyStream(inputStream, fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return bool;
    }
}
