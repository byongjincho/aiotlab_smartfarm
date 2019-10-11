package kr.co.aiotlab.www;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;

public class SwitchNameSetting_LED2 extends Activity {

    private EditText edt_led2;
    private Button btn_led2Okay, btn_led2Cancel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.nameset_led2);
        // 팝업창이 뜨면 뒷 배경 블러처리
        WindowManager.LayoutParams  layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags  = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount  = 0.7f;
        getWindow().setAttributes(layoutParams);

        // 팝업창 크기 설정
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width/1.9), (int) (height/4));

        btn_led2Okay = findViewById(R.id.btn_led2Okay);
        btn_led2Cancel = findViewById(R.id.btn_led2Cancel);
        edt_led2= findViewById(R.id.edt_led2);


        SharedPreferences led2name = getSharedPreferences("NAME_LED", MODE_PRIVATE);

        String a = led2name.getString("led2", "name");

        edt_led2.setText(a);

        btn_led2Okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String bright_high = edt_led2.getText().toString();

                SharedPreferences bright = getSharedPreferences("NAME_LED", MODE_PRIVATE);
                SharedPreferences.Editor editor = bright.edit();

                editor.putString("led2", bright_high);

                editor.commit();

                finish();
            }
        });
        btn_led2Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 바깥 터치해도 창이 닫히지 않도록
        if (event.getAction() == MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        //뒤로가기키 안먹히게 하기 위함
        return;
    }
}
