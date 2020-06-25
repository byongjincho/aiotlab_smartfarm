package kr.co.aiotlab.www;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import static kr.co.aiotlab.www.App.CHANNEL_ID;

public class AppService2 extends Service {
    private FirebaseDatabase mDatabase;

    private Thread thread;
    private static final String TAG = "Service2";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // Service 실행중이면 상태표시바에 어떻게 보일지 표시해주는 세팅
        Intent notificationIntent =  new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("방범모드가 실행중입니다.")
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.aiotlablogo)
                .build();
        startForeground(2, notification);


        if (thread == null){
            thread = new Thread(new BackThread());
            Log.d(TAG, "BackThread Start");
            thread.start();
        }

        return START_STICKY;
    }

    //setting에서 설정한 CDS 제한 값 가져오기
    private int getCDS() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        // key값이 list5로 저장되어있는 CDS설정값을 정수형으로 변환하고 반환해준다.(String으로400 LUX형태로 되어있는 것을 int형으로 바꾸니까 string은 지워짐.)
        return Integer.parseInt(sharedPreferences.getString("SETBRIGHT", "0"));
    }

    //코드를 받아 실행하기 때문에 Runnable을 implements한다.
    class BackThread implements Runnable {
        @Override
        public void run() {
            int i = 0;
            while (true) {
                // 쓰레드의 동작 현황을 확인하기 위한 test code
                i++;
                Log.d(TAG, "" + i);

                //Code를 받아 실행해야하기 때문에 Handler 사용해준다. 여기서 Looper.getMainLooper를 사용해주어야 한다. 쓰레드 간 메시지 전달
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return; // 인터럽트 받을 경우 return됨
                }

            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // serviceStop이 호출될 시
        if (thread != null){
            thread.interrupt();
            thread = null;
        }
    }
}
