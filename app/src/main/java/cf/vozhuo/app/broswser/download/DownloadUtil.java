package cf.vozhuo.app.broswser.download;

import android.webkit.MimeTypeMap;

import java.text.DecimalFormat;

public class DownloadUtil {
    //https://blog.csdn.net/dhl_1986/article/details/77865670
    public static String getFileSize(long size) {
        StringBuffer bytes = new StringBuffer();
        DecimalFormat format = new DecimalFormat("###.00");
        if (size >= 1024 * 1024 * 1024) {
            double i = (size / (1024.0 * 1024.0 * 1024.0));
            bytes.append(format.format(i)).append(" GB");
        }
        else if (size >= 1024 * 1024) {
            double i = (size / (1024.0 * 1024.0));
            bytes.append(format.format(i)).append(" MB");
        }
        else if (size >= 1024) {
            double i = (size / (1024.0));
            bytes.append(format.format(i)).append(" KB");
        }
        else {
            if (size <= 0) {
                bytes.append("0 B");
            }
            else {
                bytes.append((int) size).append(" B");
            }
        }
        return bytes.toString();
    }

    public static String getFileName(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }
    //https://www.jianshu.com/p/6e38e1ef203a
    public static String getMIMEType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }
}