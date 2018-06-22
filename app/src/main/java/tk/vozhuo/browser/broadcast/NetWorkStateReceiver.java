package tk.vozhuo.browser.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import tk.vozhuo.browser.ui.activity.MainActivity;
import tk.vozhuo.browser.utils.SPUtil;

import static android.content.ContentValues.TAG;

public class NetWorkStateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //检测API < 21
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {

            //获得ConnectivityManager对象
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            //获取ConnectivityManager对象对应的NetworkInfo对象
            //获取WIFI连接的信息
            NetworkInfo wifiNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            //获取移动数据连接的信息
            NetworkInfo dataNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if(!wifiNetworkInfo.isConnected() && SPUtil.isNoImageMode(context)) {
                Log.e(TAG, "onReceive: 无图");
                MainActivity.instance.setNoImage(true);
            } else {
                Log.e(TAG, "onReceive: 有图");
                MainActivity.instance.setNoImage(false);
            }
        } else {
            //API level >= 21
            //获得ConnectivityManager对象
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            //获取所有网络连接的信息
            Network[] networks = connMgr.getAllNetworks();
            //通过循环将网络信息逐个取出来
            for (Network network : networks) {
                //获取ConnectivityManager对象对应的NetworkInfo对象
                NetworkInfo networkInfo = connMgr.getNetworkInfo(network);
                if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI && !networkInfo.isConnected()) {
                    Log.e(TAG, "onReceive: 无图");
                    MainActivity.instance.setNoImage(true);
                } else {
                    Log.e(TAG, "onReceive: 有图");
                    MainActivity.instance.setNoImage(false);
                }
            }
        }
    }
}