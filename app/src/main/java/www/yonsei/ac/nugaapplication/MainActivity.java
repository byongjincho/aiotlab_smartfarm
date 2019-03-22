package www.yonsei.ac.nugaapplication;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import www.nugamedical.com.R;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private TextView profile_email, profile_name;
    private FirebaseAuth auth;
    private FirebaseDatabase mDatabase;
    private Thread thread;
    int past_CDS, present_CDS;
    int CDSvalueInt;
    int settingCDS;
    private volatile boolean isRunning = true;
    private static final String TAG = "MainActivity";
    /**
     * 인터넷 연결시킬 주소 설정
     */
    // 누가 홈페이지
    private Uri browserUri = Uri.parse("http://www.nugamedical.com/html_2017/");
    private Intent browser = new Intent(Intent.ACTION_VIEW, browserUri);

    // drawer
    private DrawerLayout mDrawerlayout;
    private ActionBarDrawerToggle mToggle;


    // navigation bottom 버튼 클릭 시 동작 버튼
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {


        /** 바텀 네비게이션 아이템 클릭 시 이동경로 */
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.bottomBarItemOne:
                    showFragment(BottomFirstFragment.newInstance());
                    return true;
                case R.id.bottomBarItemSecond:
                    showFragment(BottomSecondFragment.newInstance());
                    return true;
                case R.id.bottomBarItemThird:
                    showFragment(BottomThirdFragment.newInstance());
                    return true;
                case R.id.bottomBarItemFourth:
                    showFragment(BottomFourthFragment.newInstance());
                    return true;
                case R.id.bottomBarItemFifth:
                    Intent intent_settings = new Intent(MainActivity.this, BottomSettingActivity.class);
                    startActivity(intent_settings);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Firebase 로그인 정보 가져오기
        auth = FirebaseAuth.getInstance();

        //첫 화면
        if (savedInstanceState == null) {
            showFragment(BottomFirstFragment.newInstance());
        }
        // bottom 네이게이션 아이디 받아옴
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // drawer 네비게이션 아이디
        mDrawerlayout = findViewById(R.id.drawer);
        mToggle = new ActionBarDrawerToggle(this, mDrawerlayout, R.string.open, R.string.close);
        mDrawerlayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        /////  드로어 창 아이템선택 세팅 (홈페이지, 로그아웃 등등)
        NavigationView nav_View = findViewById(R.id.nav_View);
        nav_View.setNavigationItemSelectedListener(this);

        //
        View view = nav_View.getHeaderView(0);

        profile_email = view.findViewById(R.id.profile_email);
        profile_name = view.findViewById(R.id.profile_name);
        // 로그인하는 유저에 따른 이름과 이메일 정보 가져와서 드로어 네비게이션에 디스플레이
        profile_name.setText(auth.getCurrentUser().getDisplayName());
        profile_email.setText(auth.getCurrentUser().getEmail());

        // onCreate 생성 시 전에 저장된(BottomSettingActivity)의 자동제어 스위치 동작여부에 따른 동작 결정
        //SharedPreference로 저장된 값 불러오기
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        // switch 상태가 ON(자동제어)이면
        if(sharedPreferences.getBoolean("autoSwitch", false) == true) {
            //BackThread 실행
            thread = new Thread(new BackThread());
            thread.start();
            //isRunning을 true로 하여 쓰레드 동작
            isRunning = true;
        }else {
            // switch 상태가 OFF(수동제어)이면 BackThread실행 후 바로 pause상태
            thread = new Thread(new BackThread());
            thread.start();
            //isRunning을 false로 하여 쓰레드 일시중지
            isRunning = false;
        }

        // 측정된 CDS 값을 Firebase Database에서 실시간으로 측정
       RealtimeCDS();
    }

    //getAutorun은 BottomSettingActivity의 스위치 상태를 보고 ON이면 동작, OFF면 일시중지하는 함수로, 생명주기에서 MainActivity로 돌아갈 때(onResume/ onRestart) 확인하는 함수.
    private void getAutorun() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if (sharedPreferences.getBoolean("autoSwitch", false) == true) {
            //쓰레드 동작
            isRunning = true;
            Log.d(TAG, "thread start");

        } else if (sharedPreferences.getBoolean("autoSwitch", false) == false) {
            //쓰레드 일시정지
            isRunning = false;
            Log.d(TAG, "thread paused");
        }
    }

    //setting에서 설정한 CDS 제한 값 가져오기
    private int getCDS() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        // key값이 list5로 저장되어있는 CDS설정값을 정수형으로 변환하고 반환해준다.(String으로400 LUX형태로 되어있는 것을 int형으로 바꾸니까 string은 지워짐.)
        return Integer.parseInt(sharedPreferences.getString("list5", ""));
    }

    //설정한 값을 지속적으로 감시하고 대응하기 위해 Thread 돌리는 작업
    //코드를 받아 실행하기 때문에 Runnable을 implements한다.
    class BackThread implements Runnable {
        @Override
        public void run() {
            int i = 0;
            while(true){
                while(isRunning){ //일시정지를 누르면 멈추도록
                    // 쓰레드의 동작 현황을 확인하기 위한 test code
                    i++;
                    Log.d(TAG, "" + i);

                    //Code를 받아 실행해야하기 때문에 Handler 사용해준다. 여기서 Looper.getMainLooper를 사용해주어야 한다. 쓰레드 간 메시지 전달
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            // global 변수에 세팅한 CDS 값을 저장한다.
                            settingCDS = getCDS();
                            //세티해놓은 값과 측정된 값을 비교해서 active시켜주는 동작
                            sensorControl_byCDS();
                            //현재 측정된 CDS값을 동작 후 past_CDS로 저장해서 다음 프로세스 진행 시 과거 데이터로 저장.
                            past_CDS = present_CDS;
                        }
                    });
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return; // 인터럽트 받을 경우 return됨
                    }
                }
            }
        }
    }

    //SendMessage의 소켓 통신을 통해 값 전달
    private void goSocketProtocol(String i) {
        new SendMessage().execute(i);
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
                CDSvalueInt = Integer.parseInt(CDSvalue);
                // 값을 글로벌 변수에 저장
                present_CDS = CDSvalueInt;
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sensorControl_byCDS(){
        //세팅한 CDS의 LUX 값이 실제 측정값보다 크면..
        if (present_CDS < settingCDS) {
            //예외처리(속도 개선)
            if (past_CDS >= settingCDS) {
                Toast.makeText(getApplicationContext(), "설정한 값(" + settingCDS + " LUX)보다 작습니다. 점등하겠습니다.", Toast.LENGTH_SHORT).show();
                // LED를 ON시키는 동작(socket통신으로 파이썬 커맨드에 명령어를 입력)
                goSocketProtocol("1");
                goSocketProtocol("3");
                goSocketProtocol("5");
                goSocketProtocol("7");
                Log.d(TAG, "LED ON");
            } else {
               // Log.d("MainActivity", "pass led on");
            }
        } else {
            if (past_CDS < settingCDS) {
                // LED OFF
                goSocketProtocol("0");
                goSocketProtocol("2");
                goSocketProtocol("4");
                goSocketProtocol("6");
                Log.d(TAG, "LED OFF");
            } else {
                //Log.d("MainActivity", "pass led off");
            }
        }
    }

    /**
     * showFrag 메소드 : 각각의 프래그먼트를 보여줌
     */
    public void showFragment(Fragment f) {
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_main, f).commit();
    }


    /**
     * Drawer 좌상단 버튼(드로어 열고 닫는 동작)이 클릭됐을 때
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // drawer를 좌상단 버튼으로 클릭해서 열고 닫게 하기 위한 코드
        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            // 달력창으로 가기
            case R.id.calender:
                Intent intent_calendar = new Intent(MainActivity.this, CalendarActivity.class);
                startActivity(intent_calendar);
                break;
            //홈페이지 들어가기
            case R.id.homepage:
                startActivity(browser);
                break;
            case R.id.sensor_control:
                Intent intent = new Intent(MainActivity.this, SensorControlActivity.class);
                startActivity(intent);
                break;
            //로그아웃
            case R.id.logout:
                auth.signOut();
                Toast.makeText(this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
                finish();

                break;
        }
        return true;
    }


    /**
     * 뒤로가기 버튼이 눌렸을 경우 동작
     */
    public void onBackPressed() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("정말로 종료하시겠습니까?");
        builder.setCancelable(true);
        //왼쪽 버튼 설정
        builder.setNegativeButton("네", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //"네" 클릭 시 액티비티 종료
                finish();
            }
        });
        //오른쪽 버튼 설정
        builder.setPositiveButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //다이얼로그 취소(원래 상태로 돌아오기)
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
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

            String ip_address = ip_files.getString("IP", "");
            String port_num = port_files.getString("PORT", "");

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

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("MainActivity", "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("MainActivity", "onStop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getAutorun();

        Log.d("MainActivity", "onRestart");
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d("MainActivity", "onResume");
    }
}
