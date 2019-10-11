package kr.co.aiotlab.www;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;

public class SearchCity extends Activity {

    private Button btn_cityOkay, btn_cityCancel;
    private EditText edt_cityname;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_searchcity);

        edt_cityname = findViewById(R.id.edt_cityname);
        btn_cityOkay = findViewById(R.id.btn_cityOkay);
        btn_cityCancel = findViewById(R.id.btn_cityCancel);

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

        getWindow().setLayout(width/1, height/4);

        SharedPreferences cityname = getSharedPreferences("CITY", MODE_PRIVATE);
        edt_cityname.setText(cityname.getString("CITYNAME", ""));

        btn_cityOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences cityname = getSharedPreferences("CITY", MODE_PRIVATE);
                SharedPreferences.Editor editor = cityname.edit();
                editor.putString("CITYNAME", edt_cityname.getText().toString());
                editor.commit();
                finish();
            }
        });

        btn_cityCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (MotionEvent.ACTION_OUTSIDE == event.getAction()){
            return false;
        }return true;
    }

    @Override
    public void onBackPressed() {
        return;
    }
}
