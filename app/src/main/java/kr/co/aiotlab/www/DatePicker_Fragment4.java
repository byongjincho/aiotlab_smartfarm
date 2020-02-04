package kr.co.aiotlab.www;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import androidx.annotation.Nullable;
import kr.co.aiotlab.www.Main_UI.BottomFourthFragment;

public class DatePicker_Fragment4 extends Activity {

    private DatePicker datePicker;
    private Button btn_okay, btn_cancel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_datepicker_frag4);

        datePicker = findViewById(R.id.datePicker_frag4);
        btn_cancel = findViewById(R.id.btn_date_cancel);
        btn_okay = findViewById(R.id.btn_date_okay);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            datePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
                @Override
                public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {


                }
            });
        }else {

        }

        btn_okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                finish();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
