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
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import static android.widget.Toast.LENGTH_SHORT;
import static kr.co.aiotlab.www.App.CHANNEL_ID;

public class AppService extends Service {
    private FirebaseDatabase mDatabase;
    int past_CDS, present_CDS;
    int CDSvalueInt;
    int present_settingCDS;
    int past_settingCDS;
    private Thread thread;
    private static final String TAG = "Service";

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
                .setContentTitle("돈사관리가 실행중입니다.")
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.aiotlablogo)
                .build();
        startForeground(3, notification);

        // 측정된 CDS 값을 Firebase Database에서 실시간으로 측정
        RealtimeCDS();

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
                        // global 변수에 세팅한 CDS 값을 저장한다.
                        present_settingCDS = getCDS();
                        //세팅 해놓은 값과 측정된 값을 비교해서 active시켜주는 동작
                        sensorControl_byCDS();
                        //현재 측정된 CDS값을 동작 후 past_CDS로 저장해서 다음 프로세스 진행 시 과거 데이터로 저장.
                        past_CDS = present_CDS;
                        past_settingCDS = present_settingCDS;
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

    //SendMessage의 소켓 통신을 통해 값 전달
    private void goSocketProtocol(String i) {
        new SendMessage().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, i);
    }

    private void RealtimeCDS() {
        //Firebase data 가져오기(실시간)
        mDatabase = FirebaseDatabase.getInstance();
        // 가져올 디렉토리 설정
        DatabaseReference cdsRef = mDatabase.getReference("CDS_Data").child("CDS").child("LUX");
        cdsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Database에 String으로 저장되어있기 때문에 String으로 가져온다.
                String CDSvalue = dataSnapshot.getValue(String.class);
                // 수치를 비교해야하기 떄문에 정수형으로 변환
                if (CDSvalue != null) {
                    CDSvalueInt = Integer.parseInt(CDSvalue);
                }
                // 값을 글로벌 변수에 저장
                present_CDS = CDSvalueInt;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    /**
     * CDS 에 따른 LED 컨트롤
     */
    private void sensorControl_byCDS() {
        SharedPreferences sharedPreferences = getSharedPreferences("Threshold_Bright", MODE_PRIVATE);
        String high = sharedPreferences.getString("BH", "0");
        String low = sharedPreferences.getString("BL", "0");
        //세팅한 CDS의 LUX 값이 실제 측정값보다 크면..
        //입력 값이 공백일 경우 null값을 integer로 변환하려할 때 생기는 에러 방지
        if (!"".equals(high) && !"".equals(low)) {
            if (present_CDS < present_settingCDS - Integer.parseInt(low)) {
                //예외처리(속도 개선)
                if (past_CDS >= present_settingCDS - Integer.parseInt(low)) {
                    Toast.makeText(getApplicationContext(), " 점등하겠습니다.", LENGTH_SHORT).show();
                    // LED를 ON시키는 동작(socket통신으로 파이썬 커맨드에 명령어를 입력)
                    goSocketProtocol("1");
                    goSocketProtocol("5");

                    Log.d(TAG, "LED : 1");
                    Log.d(TAG, "LED ON");
                } else {
                    // 세팅 값을 변경하면 바로 적용될 수 있도록
                    if (present_CDS > past_settingCDS) {
                        Toast.makeText(getApplicationContext(), " 점등하겠습니다.", LENGTH_SHORT).show();
                        goSocketProtocol("1");
                        goSocketProtocol("5");

                        Log.d(TAG, "LED ON");
                    }
                    // Log.d("MainActivity", "pass led on");
                }
            } else if (present_CDS > present_settingCDS + Integer.parseInt(high)) {
                if (past_CDS < present_settingCDS + Integer.parseInt(high)) {
                    // LED OFF
                    goSocketProtocol("0");
                    goSocketProtocol("4");

                    Log.d(TAG, "LED : 0");
                    Log.d(TAG, "LED OFF");
                } else {
                    if (present_CDS < past_settingCDS) {
                        goSocketProtocol("0");
                        goSocketProtocol("4");

                        Log.d(TAG, "LED : 0");
                        Log.d(TAG, "LED OFF");
                    }
                    //Log.d("MainActivity", "pass led off");
                }
            }
        } else {
            Toast.makeText(this, "상한선, 하한선 값을 설정해주세요", LENGTH_SHORT).show();
        }
    }
    //소켓 통신을 하기 위한 모듈
    class SendMessage extends AsyncTask<String, Void, Void> {
        private Exception exception;
        private String IP_ADRESS;
        private int PORT;

        @Override
        protected Void doInBackground(String... strings) {
            // sharedPreference에 저장된 값을 불러와서 IP, PORT에 대입
            SharedPreferences ip_files = getSharedPreferences("IP_files", MODE_PRIVATE);
            SharedPreferences port_files = getSharedPreferences("PORT_files", MODE_PRIVATE);

            String ip_address = ip_files.getString("IP", "0");
            String port_num = port_files.getString("PORT", "0");

            IP_ADRESS = ip_address;
            PORT = Integer.parseInt(port_num);

            try {

                try {
                    Socket socket = new Socket(IP_ADRESS, PORT);
                    PrintWriter printWriter = new PrintWriter(
                            new OutputStreamWriter(
                                    socket.getOutputStream()));
                    printWriter.print(strings[0]);
                    printWriter.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                this.exception = e;
                return null;
            }
            return null;
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
