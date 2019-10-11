package kr.co.aiotlab.www;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetWorkStatus {
    public static final int TYPE_WIFI = 1;
    public static final int TYPE_MOBILE = 2;
    public static final int TYPE_NOT_CONNECTED = 3;

    // 시스템 서비스를 참조하려 하면 Context에서 getSystemservice 메서드를 호출해야한다.
    public static int getConnectivityStatus(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo != null) {
            int type = networkInfo.getType();

            if (type == ConnectivityManager.TYPE_MOBILE) {
                return TYPE_MOBILE;
            }else if(type == ConnectivityManager.TYPE_WIFI) {
                return TYPE_WIFI;
            }
        } return TYPE_NOT_CONNECTED;
    }
}
