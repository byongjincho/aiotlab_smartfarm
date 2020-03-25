package kr.co.aiotlab.www.Main_UI;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import kr.co.aiotlab.www.LineChart.Linehart_Temperature_Minute;
import kr.co.aiotlab.www.LineChart.Linechart_Calorie_Hour;
import kr.co.aiotlab.www.LineChart.Linechart_Temperature_Hour;
import kr.co.aiotlab.www.LineChart.Linechart_Humidity_Minute;
import kr.co.aiotlab.www.LineChart.Linechart_Humidity_Hour;
import kr.co.aiotlab.www.LineChart.Linechart_Brightness_Minute;
import kr.co.aiotlab.www.LineChart.Linechart_Brightness_Hour;
import kr.co.aiotlab.www.LineChart.Linechart_Power_Minute;
import kr.co.aiotlab.www.LineChart.Linechart_Power_Hour;
import kr.co.aiotlab.www.LineChart.Linechart_Calorie_Minute;
import kr.co.aiotlab.www.R;


public class BottomFourthFragment extends Fragment implements AdapterView.OnItemSelectedListener, DatePickerDialog.OnDateSetListener {

    // 시간 표시
    public static TextView txt_month, txt_year, txt_day;
    private String monthString, yearString, dayString;

    private Date date;
    private SwipeRefreshLayout swipe_frag4;
    private PieChart mPiechart;
    private Spinner sensor_select;
    private String time, year, month, day;
    // DatePicker
    DatePickerDialog datePickerDialog;

    //
    private Button btn_nowTime;
    private RadioGroup radioGroup;
    private RadioButton radioButton_minute, radioButton_hour;

    private ImageButton imgbtn_calendar;

    public static BottomFourthFragment newInstance() {
        BottomFourthFragment f = new BottomFourthFragment();
        return f;
    }

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag4, container, false);

        txt_year = view.findViewById(R.id.txt_year_frag4);
        txt_month = view.findViewById(R.id.txt_month_frag4);
        txt_day = view.findViewById(R.id.txt_day_frag4);

        btn_nowTime = view.findViewById(R.id.btn_nowTime_frag4);
        radioGroup = view.findViewById(R.id.radioGroup_frag4);
        radioButton_minute = view.findViewById(R.id.radioBtn_minute_frag4);
        radioButton_hour = view.findViewById(R.id.radioBtn_hour_frag4);

        getNowTime();
        SharedPreferences sharedPreferences = view.getContext().getSharedPreferences("TimeSave", Context.MODE_PRIVATE);
        yearString = sharedPreferences.getString("YEAR", year);
        monthString = sharedPreferences.getString("MONTH", month);
        dayString = sharedPreferences.getString("DAY", day);

        txt_year.setText(yearString);
        txt_month.setText(monthString);
        txt_day.setText(dayString);

        // 현재 시간 클릭시 동작 구현
        btn_nowTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getNowTime();
                saveDateYear(year);
                saveDateMonth(month);
                saveDateDay(day);
                refreshFragment();
            }
        });


        imgbtn_calendar = view.findViewById(R.id.imgbtn_calendar);
        imgbtn_calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getContext(), DatePicker_Fragment4.class);
//                startActivity(intent);

                // DatePicker Dialog
                datePickerDialog = DatePickerDialog.newInstance(BottomFourthFragment.this, Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(day));
                datePickerDialog.setThemeDark(false);
                datePickerDialog.showYearPickerFirst(false);
                datePickerDialog.setTitle("원하는 날짜를 선택해주세요");
                datePickerDialog.show(getActivity().getFragmentManager(), "DatePickerDialog");

            }
        });
        /*

        swipe_frag4 = view.findViewById(R.id.swipe_frag4);

        //새로고침
        swipe_frag4.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipe_frag4.setRefreshing(false);
                    }
                }, 1000);
            }
        });
        */

        /** 파이 차트 */

        mPiechart = view.findViewById(R.id.piechart);
        mPiechart.setUsePercentValues(true);
        mPiechart.getDescription().setEnabled(false);
        //offset 설정
        mPiechart.setExtraOffsets(5, 10, 5, 5);

        mPiechart.setDragDecelerationFrictionCoef(0.95f);               //돌아가는 부드러움 설정

        mPiechart.setDrawHoleEnabled(true);                             //여러 효과
        mPiechart.setHoleColor(Color.WHITE);
        mPiechart.setTransparentCircleRadius(61f);

        // 데이터 입력
        ArrayList<PieEntry> yValues = new ArrayList<>();

        // y 축에 데이터 값, 이름 설정
        yValues.add(new PieEntry(34f, "센서1"));
        yValues.add(new PieEntry(31f, "센서2"));
        yValues.add(new PieEntry(54f, "센서3"));
        yValues.add(new PieEntry(43f, "센서4"));
        yValues.add(new PieEntry(56f, "센서5"));
        yValues.add(new PieEntry(84f, "센서6"));
        yValues.add(new PieEntry(64f, "센서7"));
        yValues.add(new PieEntry(74f, "센서8"));
        yValues.add(new PieEntry(24f, "센서9"));
        yValues.add(new PieEntry(44f, "센서10"));

        //설명란
        Description description = new Description();
        description.setText("총 전력량 비율입니다.");
        description.setTextSize(15);
        mPiechart.setDescription(description);

        mPiechart.animateY(1000, Easing.EasingOption.EaseInCubic);       //애니메이션 효과

        PieDataSet dataSet = new PieDataSet(yValues, "전력사용");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);

        PieData data2 = new PieData(dataSet);

        data2.setValueTextColor(Color.YELLOW);
        data2.setValueTextSize(10f);

        mPiechart.setData(data2);

        /** 스피너 */
        sensor_select = view.findViewById(R.id.sensor_select);
        // array의 sensors에 있는 목록들을 보여준다.
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.sensors, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sensor_select.setAdapter(adapter);
        sensor_select.setOnItemSelectedListener(this);


//        /**쓰레스 시작*/
//        BackThread thread = new BackThread();
//        thread.setDaemon(true);
//        thread.start();

        SharedPreferences radioState = this.getActivity().getSharedPreferences("TimeSave", Context.MODE_PRIVATE);
        if (radioState.getString("RADIOBUTTON", "MINUTE").equals("MINUTE"))
            radioButton_minute.setChecked(true);
        else if (radioState.getString("RADIOBUTTON", "MINUTE").equals("HOUR"))
            radioButton_hour.setChecked(true);

        /**
         * 라디오 그룹에서 버튼 선택
         */
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioBtn_minute_frag4:
                        if (sensor_select.getSelectedItemId() == 0)
                            showFragment2(Linehart_Temperature_Minute.newInstance());
                        else if (sensor_select.getSelectedItemId() == 1)
                            showFragment2(Linechart_Humidity_Minute.newInstance());
                        else if (sensor_select.getSelectedItemId() == 2)
                            showFragment2(Linechart_Brightness_Minute.newInstance());
                        else if (sensor_select.getSelectedItemId() == 3)
                            showFragment2(Linechart_Power_Minute.newInstance());
                        else if (sensor_select.getSelectedItemId() == 4)
                            showFragment2(Linechart_Calorie_Minute.newInstance());
                        // 라디오버튼 상태 저장
                        saveRadioGroupState("MINUTE");
                        break;
                    case R.id.radioBtn_hour_frag4:
                        if (sensor_select.getSelectedItemId() == 0)
                            showFragment2(Linechart_Temperature_Hour.newInstance());
                        else if (sensor_select.getSelectedItemId() == 1)
                            showFragment2(Linechart_Humidity_Hour.newInstance());
                        else if (sensor_select.getSelectedItemId() == 2)
                            showFragment2(Linechart_Brightness_Hour.newInstance());
                        else if (sensor_select.getSelectedItemId() == 3)
                            showFragment2(Linechart_Power_Hour.newInstance());
                        else if (sensor_select.getSelectedItemId() == 4)
                            showFragment2(Linechart_Calorie_Hour.newInstance());
                        // 라디오버튼 상태 저장
                        saveRadioGroupState("HOUR");
                        break;
                }
            }
        });

        return view;
    }

    /**
     * 스피너 선택
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if (radioButton_minute.isChecked()) {
            switch (position) {
                // 맨 위부터 순서대로 0 ~.. 센서1 선택
                case 0:
                    showFragment2(Linehart_Temperature_Minute.newInstance());
                    break;
                case 1:
                    showFragment2(Linechart_Humidity_Minute.newInstance());
                    break;
                case 2:
                    showFragment2(Linechart_Brightness_Minute.newInstance());
                    break;
                case 3:
                    showFragment2(Linechart_Power_Minute.newInstance());
                    break;
                case 4:
                    showFragment2(Linechart_Calorie_Minute.newInstance());
                    break;

            }
        } else if (radioButton_hour.isChecked()) {
            switch (position) {
                // 맨 위부터 순서대로 0 ~.. 센서1 선택
                case 0:
                    showFragment2(Linechart_Temperature_Hour.newInstance());
                    break;
                case 1:
                    showFragment2(Linechart_Humidity_Hour.newInstance());
                    break;
                case 2:
                    showFragment2(Linechart_Brightness_Hour.newInstance());
                    break;
                case 3:
                    showFragment2(Linechart_Power_Hour.newInstance());
                    break;
                case 4:
                    showFragment2(Linechart_Calorie_Hour.newInstance());
                    break;

            }
        }


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void showFragment2(Fragment f) {
        getFragmentManager().beginTransaction().replace(R.id.frame_linechart, f).commit();
    }

//    //백엔드 쓰레드로 현재시간 표시
//    class BackThread extends Thread {
//        @Override
//        public void run() {
//            while (true) {
//                handler.sendEmptyMessage(0);
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }

//    //현재시간(측정)
//    Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            if (msg.what == 0) {
//                long now = System.currentTimeMillis();
//                date = new Date(now);
//                txt_time = view.findViewById(R.id.txt_time);
//                SimpleDateFormat sdfNow = new SimpleDateFormat("HH:mm");
//                String formatData = sdfNow.format(date);
//                txt_time.setText(formatData);
//            }
//        }
//    };

    private void getNowTime() {
        //현재 시간
        long now = System.currentTimeMillis();
        date = new Date(now);

        SimpleDateFormat sdfYear = new SimpleDateFormat("yyyy");
        SimpleDateFormat sdfMonth = new SimpleDateFormat("MM");
        SimpleDateFormat sdfDay = new SimpleDateFormat("dd");
        SimpleDateFormat sdfHour = new SimpleDateFormat("HH");
        SimpleDateFormat sdfMinute = new SimpleDateFormat("mm");


        String yearData = sdfYear.format(date);
        String monthData = sdfMonth.format(date);
        String dayData = sdfDay.format(date);
        String hourData = sdfHour.format(date);
        String minuteData = sdfMinute.format(date);

        year = yearData;
        month = monthData;
        day = dayData;
    }

    // DatePicker 선택했을 때
    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        int month = monthOfYear + 1;
        int day = dayOfMonth;
        String m;
        String y = String.valueOf(year);
        String d;

        saveDateYear(y);

        if (month < 10) {
            m = "0" + month;
            saveDateMonth(m);
        } else {
            m = String.valueOf(month);
            saveDateMonth(m);
        }
        if (day < 10) {
            d = "0" + day;
            saveDateDay(d);
        } else {
            d = String.valueOf(day);
            saveDateDay(d);
        }

        refreshFragment();
    }

    private void refreshFragment() {

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();

    }

    private void saveDateYear(String yearString) {
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("TimeSave", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("YEAR", yearString);
        editor.apply();
    }

    private void saveDateMonth(String monthString) {
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("TimeSave", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (Integer.parseInt(yearString) < 10) {
            editor.putString("MONTH", "0" + monthString);
        } else {
            editor.putString("MONTH", monthString);
        }
        editor.apply();
    }

    private void saveDateDay(String dayString) {
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("TimeSave", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (Integer.parseInt(yearString) < 10) {
            editor.putString("DAY", "0" + dayString);
        } else {
            editor.putString("DAY", dayString);
        }
        editor.apply();
    }
    private void saveRadioGroupState(String radioState) {
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("TimeSave", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("RADIOBUTTON", radioState);
        editor.apply();
    }
}
