package kr.co.aiotlab.www;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class SingupActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn_register;
    private Animation frombottom, fromtop;
    private LinearLayout rel;
    private View backView;
    private EditText editText_setpassword, editText_setpassword2, editText_setid, editText_setName;
    private FirebaseAuth mAuth2;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_signup);

        editText_setName = findViewById(R.id.editText_setName);
        editText_setid = findViewById(R.id.editText_setid);
        btn_register = findViewById(R.id.btn_register);
        rel = findViewById(R.id.rel);
        backView = findViewById(R.id.backView);
        editText_setpassword = findViewById(R.id.editText_setpassword);
        editText_setpassword2 = findViewById(R.id.editText_setpassword2);


        btn_register.setOnClickListener(this);


        fromtop = AnimationUtils.loadAnimation(this, R.anim.fromtop);
        frombottom = AnimationUtils.loadAnimation(this, R.anim.frombottom);                     // 애니메이션 기능 선택

        rel.setAnimation(frombottom);                                                                    // rel 을 밑에서 위로 올라오는 애니메이션 on
        backView.setAnimation(fromtop);                                                                  // backView를 위에서 아래로 내리는 애니메이션 on

        mAuth2 = FirebaseAuth.getInstance();

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_register:
                if (editText_setpassword.getText().toString().equals(editText_setpassword2.getText().toString())) {

                    if (editText_setid.getText().toString().isEmpty() || editText_setName.getText().toString().isEmpty()) {
                        Toast.makeText(this, "내용을 전부 입력해주세요.", Toast.LENGTH_SHORT).show();
                    } else {
                        createUser(editText_setid.getText().toString(), editText_setpassword.getText().toString());

                        Toast.makeText(this, "등록되었습니다.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                }
                break;


        }


    }
    // 이메일 아이디 생성
    private void createUser(final String email, final String password){
        mAuth2.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SingupActivity.this, "회원가입 성공", Toast.LENGTH_SHORT).show();
                        } else {
                        }

                        // ...
                    }
                });
    }



}
