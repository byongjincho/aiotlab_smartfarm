package www.yonsei.ac.nugaapplication;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import www.nugamedical.com.R;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private TextView profile_email, profile_name;
    private FirebaseAuth auth;

    //인터넷 연결주소 설정
    private  Uri browserUri = Uri.parse("http://www.nugamedical.com/html_2017/");
    private  Intent browser = new Intent(Intent.ACTION_VIEW, browserUri);


    // drawer
    private DrawerLayout mDrawerlayout;
    private ActionBarDrawerToggle mToggle;


    // navigation bottom 버튼 클릭 시 동작 버튼
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {


        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.bottomBarItemOne:
                    showFragment(FirstFragment.newInstance());
                    return true;
                case R.id.bottomBarItemSecond:
                    showFragment(SecondFragment.newInstance());
                    return true;
                case R.id.bottomBarItemThird:
                    showFragment(ThirdFragment.newInstance());
                    return true;
                case R.id.bottomBarItemFourth:
                    showFragment(FourthFragment.newInstance());
                    return true;
                case R.id.bottomBarItemFifth:
                    Intent intent_settings = new Intent(MainActivity.this, SettingActivity.class);
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


        // 처음 시작 화면 default값 (frag1)
        showFragment(FirstFragment.newInstance());

        // bottom 네이게이션 아이디 받아옴
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // drawer 네비게이션 아이디
        mDrawerlayout = findViewById(R.id.drawer);
        mToggle = new ActionBarDrawerToggle(this,mDrawerlayout, R.string.open, R.string.close);
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

        profile_name.setText(auth.getCurrentUser().getDisplayName());
        profile_email.setText(auth.getCurrentUser().getEmail());
    }


    // showFrag 메소드 : 각각의 프래그먼트를 보여줌

    public void showFragment(Fragment f){
        getFragmentManager().beginTransaction().replace(R.id.frame_main, f).commit();
    }


    // /////////  Drawer 좌상단 버튼이 클릭됐을 때/// // / //////
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // drawer를 좌상단 버튼으로 클릭해서 열고 닫게 하기 위한 코드
        if(mToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
                // 달력창으로 가기
            case R.id.calender:
                Intent intent_calendar = new Intent(MainActivity.this, CalendarActivity.class);
                startActivity(intent_calendar);
                break;
                //홈페이지 들어가기
            case R.id.homepage:
                startActivity(browser);
                break;
                //로그아웃
            case R.id.logout:
                auth.signOut();
                Toast.makeText(this,"로그아웃 되었습니다.",Toast.LENGTH_SHORT).show();
                finish();

                break;
        }
        return true;
    }


    // 뒤로가기 버튼이 눌렸을 경우 동작
    public void onBackPressed(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("정말로 종료하시겠습니까?");
        builder.setCancelable(true);
        builder.setNegativeButton("네", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setPositiveButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


}
