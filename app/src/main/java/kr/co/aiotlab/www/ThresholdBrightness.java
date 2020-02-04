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

public class ThresholdBrightness extends Activity {

    private EditText edt_brightHigh, edt_brightLow;
    private Button btn_brightOkay, btn_brightCancel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.threshold_brightness);
        // 팝업창이 뜨면 뒷 배경 블러처리
        WindowManager.LayoutParams  layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags  = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount  = 0.7f;
        getWindow().setAttributes(layoutParams);

        // 팝업창 크기 설정
////        DisplayMetrics dm = new DisplayMetrics();
////        getWindowManager().getDefaultDisplay().getMetrics(dm);
//
//        int width = dm.widthPixels;
//        int height = dm.heightPixels;
//
//        getWindow().setLayout((int) (width/1.9), (int) (height/2.8));

        btn_brightCancel = findViewById(R.id.btn_brightCancel);
        btn_brightOkay = findViewById(R.id.btn_brightOkay);
        edt_brightHigh = findViewById(R.id.edt_brightHigh);
        edt_brightLow = findViewById(R.id.edt_brightLow);

        SharedPreferences bright = getSharedPreferences("Threshold_Bright", MODE_PRIVATE);

        String a = bright.getString("BH", "0");
        String b = bright.getString("BL", "0");

        edt_brightHigh.setText(a);
        edt_brightLow.setText(b);

        btn_brightOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String bright_high = edt_brightHigh.getText().toString();
                String bright_low = edt_brightLow.getText().toString();

                SharedPreferences bright = getSharedPreferences("Threshold_Bright", MODE_PRIVATE);
                SharedPreferences.Editor editor = bright.edit();

                if(bright_high.equals(null)){
                    edt_brightHigh.setText("0");
                }else if(bright_low.equals(null)){
                    edt_brightLow.setText("0");
                }else {
                    editor.putString("BH", bright_high);
                    editor.putString("BL", bright_low);
                    editor.commit();
                }
                finish();
            }
        });
        btn_brightCancel.setOnClickListener(new View.OnClickListener() {
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
