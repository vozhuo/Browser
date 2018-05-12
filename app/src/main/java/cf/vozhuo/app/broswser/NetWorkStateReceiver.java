package cf.vozhuo.app.broswser;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import cf.vozhuo.app.broswser.util.SPUtil;

import static android.content.ContentValues.TAG;

public class NetWorkStateReceiver extends BroadcastReceiver {
    private Boolean state;
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
//            SharedPreferences sp = context.getSharedPreferences("GlobalConfig", Context.MODE_PRIVATE);
//            state = sp.getBoolean("image_state", false);
//            Log.e(TAG, "onReceive: " + state);
//            sp.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
//                @Override
//                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
//                    if(key.equals("image_state")) {
//                        state = sharedPreferences.getBoolean(key, false);
//                    }
//                }
//            });
            if(!wifiNetworkInfo.isConnected() && SPUtil.isNoImageMode(context)) {
                Log.e(TAG, "onReceive: 无图");
                MainActivity.instance.setNoImage(true);
            } else {
                Log.e(TAG, "onReceive: 有图");
                MainActivity.instance.setNoImage(false);
            }
//            if (wifiNetworkInfo.isConnected() && dataNetworkInfo.isConnected()) {
//                Toast.makeText(context, "WIFI已连接,移动数据已连接", Toast.LENGTH_SHORT).show();
//            } else if (wifiNetworkInfo.isConnected() && !dataNetworkInfo.isConnected()) {
//                Toast.makeText(context, "WIFI已连接,移动数据已断开", Toast.LENGTH_SHORT).show();
//            } else if (!wifiNetworkInfo.isConnected() && dataNetworkInfo.isConnected()) {
//                Toast.makeText(context, "WIFI已断开,移动数据已连接", Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(context, "WIFI已断开,移动数据已断开", Toast.LENGTH_SHORT).show();
//            }
        } else {
            //API level >= 21
            //获得ConnectivityManager对象
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            //获取所有网络连接的信息
            Network[] networks = connMgr.getAllNetworks();
            //用于存放网络连接信息
            StringBuilder sb = new StringBuilder();
            //通过循环将网络信息逐个取出来
            for (int i = 0; i < networks.length; i++) {
                //获取ConnectivityManager对象对应的NetworkInfo对象
                NetworkInfo networkInfo = connMgr.getNetworkInfo(networks[i]);
                sb.append(networkInfo.getTypeName()).append(" connect is ").append(networkInfo.isConnected());
            }
            Toast.makeText(context, sb.toString(),Toast.LENGTH_SHORT).show();
        }
    }
}