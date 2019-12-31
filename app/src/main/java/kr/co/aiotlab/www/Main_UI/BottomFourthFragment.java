package kr.co.aiotlab.www.Main_UI;

import android.support.v4.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import kr.co.aiotlab.www.LineChart.LinechartSensor1;
import kr.co.aiotlab.www.LineChart.LinechartSensor10;
import kr.co.aiotlab.www.LineChart.LinechartSensor2;
import kr.co.aiotlab.www.LineChart.LinechartSensor3;
import kr.co.aiotlab.www.LineChart.LinechartSensor4;
import kr.co.aiotlab.www.LineChart.LinechartSensor5;
import kr.co.aiotlab.www.LineChart.LinechartSensor6;
import kr.co.aiotlab.www.LineChart.LinechartSensor7;
import kr.co.aiotlab.www.LineChart.LinechartSensor8;
import kr.co.aiotlab.www.LineChart.LinechartSensor9;
import kr.co.aiotlab.www.R;


public class BottomFourthFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private TextView txt_time;
    private Date date;
    private SwipeRefreshLayout swipe_frag4;
    private PieChart mPiechart;
    private Spinner sensor_select, spinner_date_month, spinner_date_year;
    public static String spinner_month, spinner_year;
    private String time, year, month, day;

    public static BottomFourthFragment newInstance() {
        BottomFourthFragment f = new BottomFourthFragment();
        return f;
    }

    View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag4, container, false);
        // 스피너 날짜 설정
        getNowTime();
        spinner_year = year;
        spinner_month = month;

        initSpinner();
        /**

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

        return view;
    }


    /**
     * 스피너 선택
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        switch (position) {
            // 맨 위부터 순서대로 0 ~.. 센서1 선택
            case 0:
                showFragment2(LinechartSensor1.newInstance());
                break;
            case 1:
                showFragment2(LinechartSensor2.newInstance());
                break;
            case 2:
                showFragment2(LinechartSensor3.newInstance());
                break;
            case 3:
                showFragment2(LinechartSensor4.newInstance());
                break;
            case 4:
                showFragment2(LinechartSensor5.newInstance());
                break;
            case 5:
                showFragment2(LinechartSensor6.newInstance());
                break;
            case 6:
                showFragment2(LinechartSensor7.newInstance());
                break;
            case 7:
                showFragment2(LinechartSensor8.newInstance());
                break;
            case 8:
                showFragment2(LinechartSensor9.newInstance());
                break;
            case 9:
                showFragment2(LinechartSensor10.newInstance());
                break;
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

    void initSpinner() {
        spinner_date_month = view.findViewById(R.id.spinnner_date_month);
        spinner_date_year = view.findViewById(R.id.spinnner_date_year);

        spinner_date_month.setSelection(Integer.parseInt(month) - 1);

        spinner_date_year.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("test", "onItemSelected: " + parent.getItemAtPosition(position));
                spinner_year = String.valueOf(parent.getItemAtPosition(position));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(getContext(), "연도를 선택해주세요", Toast.LENGTH_SHORT).show();
            }
        });

        spinner_date_month.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("test", "onItemSelected: " + parent.getItemAtPosition(position));
                spinner_month = String.valueOf(parent.getItemAtPosition(position));
                if (Integer.parseInt(spinner_month ) < 10) {
                    spinner_month = "0" + spinner_month;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(getContext(), "월을 선택해주세요", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void getNowTime(){
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

}
