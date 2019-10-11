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

public class ThresholdTemperature extends Activity {

    private EditText edt_tempHigh, edt_tempLow;
    private Button btn_tempOkay, btn_tempCancel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.threshold_temperature);

        // 팝업창이 뜨면 뒷 배경 블러처리
        WindowManager.LayoutParams  layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags  = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount  = 0.7f;
        getWindow().setAttributes(layoutParams);


        //팝업창 크기 설정
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width/1.9), (int) (height/2.8));

        edt_tempHigh = findViewById(R.id.edt_tempHigh);
        edt_tempLow = findViewById(R.id.edt_tempLow);
        btn_tempCancel = findViewById(R.id.btn_tempCancel);
        btn_tempOkay = findViewById(R.id.btn_tempOkay);

        SharedPreferences sharedPreferences = getSharedPreferences("TT", MODE_PRIVATE);
        String tmpHigh = sharedPreferences.getString("TH", "");
        String tmpLow = sharedPreferences.getString("TL", "");


        edt_tempHigh.setText(tmpHigh);
        edt_tempLow.setText(tmpLow);

        btn_tempOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String tempHigh = edt_tempHigh.getText().toString();
                String tempLow = edt_tempLow.getText().toString();

                SharedPreferences sharedPreferences = getSharedPreferences("TT", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                if(edt_tempHigh.equals(null)){
                    edt_tempHigh.setText("0");
                }else if(edt_tempLow.equals(null)){
                    edt_tempLow.setText("0");
                }else {
                    editor.putString("TH", tempHigh);
                    editor.putString("TL", tempLow);
                    editor.commit();
                }
            finish();
            }
        });
        btn_tempCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        return;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(MotionEvent.ACTION_OUTSIDE == event.getAction()){
            return false;
        }
        return true;
    }
}
