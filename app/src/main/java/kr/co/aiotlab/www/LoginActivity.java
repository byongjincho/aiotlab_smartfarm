package kr.co.aiotlab.www;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn_signin, btn_signup;
    private ImageView mark;
    private TextView nugaTitle, nugaSubtitle;
    private Animation fromright, fromleft, fromtop;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login);

        fromtop = AnimationUtils.loadAnimation(this, R.anim.fromtop);
        fromleft = AnimationUtils.loadAnimation(this, R.anim.fromleft);
        fromright = AnimationUtils.loadAnimation(this, R.anim.fromright);

        nugaTitle = findViewById(R.id.nugaTitle);
        nugaSubtitle = findViewById(R.id.nugaSubtitle);
        mark = findViewById(R.id.mark);
        btn_signin = findViewById(R.id.btn_signin);
        btn_signup = findViewById(R.id.btn_signup);

        btn_signin.setOnClickListener(this);
        btn_signup.setOnClickListener(this);

        mark.setAnimation(fromtop);
        nugaTitle.setAnimation(fromleft);
        nugaSubtitle.setAnimation(fromright);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 회원가입 버튼 클릭 동작
            case R.id.btn_signup:
                //회원가입 화면으로 이동(SingupActivity로 이동)
                Intent intent_gosignup = new Intent(LoginActivity.this, SingupActivity.class);
                startActivity(intent_gosignup);
                break;
            // 로그인 버튼 클릭 동작
            case R.id.btn_signin:
                //로그인 화면으로 이동
                Intent intent_gosignin = new Intent(LoginActivity.this, SigninActivity.class);
                startActivity(intent_gosignin);
                break;
        }
    }
}
