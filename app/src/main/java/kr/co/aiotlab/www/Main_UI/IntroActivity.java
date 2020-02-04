package kr.co.aiotlab.www.Main_UI;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import kr.co.aiotlab.www.EnterIPandPort;
import kr.co.aiotlab.www.R;


public class IntroActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //보여줄 인트로 그림
        setContentView(R.layout.layout_intro);

        Handler handler = new Handler(); // 객체생성
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(IntroActivity.this, EnterIPandPort.class);
                startActivity(intent);
                finish();
            }
        }, 2000);           // 몇 초간 띄우고 다음 액티비티로 넘어갈지 결정

    }
}
