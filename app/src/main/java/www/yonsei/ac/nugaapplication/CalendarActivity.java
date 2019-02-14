package www.yonsei.ac.nugaapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.CalendarView;

import www.nugamedical.com.R;

public class CalendarActivity extends AppCompatActivity {


    private static final String TAG = "CalendarActivity";

    private CalendarView mCalendarView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_calendar);   //layout을 layout_calender로 생성


        mCalendarView = findViewById(R.id.calender_view);

        mCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                String date = year + "/" + (month + 1) + "/" + dayOfMonth;
                Log.d(TAG, "onSelectedDayChange: date:" + date);

            }
        });
    }


}
