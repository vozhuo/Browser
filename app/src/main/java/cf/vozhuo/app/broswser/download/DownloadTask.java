//package cf.vozhuo.app.broswser.download;
//
//import android.content.Intent;
//import android.net.Uri;
//import android.os.AsyncTask;
//import android.util.Log;
//import android.widget.Toast;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.net.HttpURLConnection;
//import java.net.URL;
//
//import cf.vozhuo.app.broswser.MainActivity;
//
//import static android.content.ContentValues.TAG;
//
//public class DownloadTask extends AsyncTask<String, Void, Void> {
//    // 传递两个参数：URL 和 目标路径
//    private String url;
//    private String destPath;
//
//    @Override
//    protected void onPreExecute() {
//        Log.e(TAG, "onPreExecute: ");
//    }
//
//    @Override
//    protected Void doInBackground(String... params) {
//        Log.e("doInBackground", params[0] + params[1]);
//        url = params[0];
//        destPath = params[1];
//        OutputStream out = null;
//        HttpURLConnection urlConnection = null;
//        try {
//            URL url = new URL(params[0]);
//            urlConnection = (HttpURLConnection) url.openConnection();
//            urlConnection.setConnectTimeout(15000);
//            urlConnection.setReadTimeout(15000);
//            InputStream in = urlConnection.getInputStream();
//            out = new FileOutputStream(params[1]);
//            byte[] buffer = new byte[10 * 1024];
//            int len;
//            while ((len = in.read(buffer)) != -1) {
//                out.write(buffer, 0, len);
//            }
//            in.close();
//        } catch (IOException e) {
//            Log.e(TAG, "ERROR");
//        } finally {
//            if (urlConnection != null) {
//                urlConnection.disconnect();
//            }
//            if (out != null) {
//                try {
//                    out.close();
//                } catch (IOException e) {
//                    Log.e(TAG, "ERROR");
//                }
//            }
//        }
//        return null;
//    }
//
//    @Override
//    protected void onPostExecute(Void aVoid) {
//        Toast.makeText(MainActivity.instance, "下载完成", Toast.LENGTH_SHORT).show();
//        Log.e(TAG, "完成下载");
////        log.info("完成下载");
////        Intent handlerIntent = new Intent(Intent.ACTION_VIEW);
////        String mimeType = getMIMEType(url);
////        Uri uri = Uri.fromFile(new File(destPath));
////        log.debug("mimiType:{}, uri:{}", mimeType, uri);
////        handlerIntent.setDataAndType(uri, mimeType);
////        startActivity(handlerIntent);
//    }
//}
