package kr.co.aiotlab.www.LineChart;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import kr.co.aiotlab.www.R;

import static kr.co.aiotlab.www.Main_UI.BottomFourthFragment.txt_day;

public class Linechart_Power_Minute extends Fragment implements View.OnClickListener {

    private LineChart mChart;
    private View view;
    private FirebaseDatabase mDatabase;
    private Date date;
    private TextView txt_backChart, txt_nextChart;
    String time, year, month, day;
    String yearData, monthData, dayData, hourData, minuteData;
    DatabaseReference current1Ref;
    ChildEventListener childEventListener;
    // 저장된 날짜 담아오기
    private String yearString, monthString, dayString;

    public static Linechart_Power_Minute newInstance() {
        Linechart_Power_Minute f = new Linechart_Power_Minute();
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_sensor7, container, false);

        mChart = view.findViewById(R.id.linechart_sensor7);
        mChart.setNoDataText("데이터를 불러오는 중입니다.");
        mChart.setNoDataTextColor(Color.BLUE);
        mDatabase = FirebaseDatabase.getInstance();

        txt_nextChart = view.findViewById(R.id.txt_nextChart);
        txt_backChart = view.findViewById(R.id.txt_backChart);
        txt_nextChart.setOnClickListener(this);
        txt_backChart.setOnClickListener(this);

        getNowTime();
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("TimeSave", Context.MODE_PRIVATE);
        yearString = sharedPreferences.getString("YEAR", year);
        monthString = sharedPreferences.getString("MONTH", month);
        dayString = sharedPreferences.getString("DAY", day);

        showLineChartData();

        return view;
    }

    @Override
    public void onClick(View v) {
        current1Ref.removeEventListener(childEventListener);
        switch (v.getId()) {
            case R.id.txt_backChart:
                dayString = String.valueOf(Integer.parseInt(dayString) - 1);
                if (Integer.parseInt(dayString) < 10) {
                    dayString = "0" + dayString;
                }

                if (Integer.parseInt(dayString) <= 1) {
                    txt_backChart.setVisibility(View.INVISIBLE);
                } else {
                    txt_nextChart.setVisibility(View.VISIBLE);
                }
// TextView 일자 변경
                txt_day.setText(dayString);
                // SharedPreference에 일자 저장
                saveDateDay(dayString);
                showLineChartData();
                break;
            case R.id.txt_nextChart:
                dayString = String.valueOf(Integer.parseInt(dayString) + 1);
                if (Integer.parseInt(dayString) < 10) {
                    dayString = "0" + dayString;
                }

                if (Integer.parseInt(dayString) > 30) {
                    txt_nextChart.setVisibility(View.INVISIBLE);
                } else {

                    txt_backChart.setVisibility(View.VISIBLE);
                }
// TextView 일자 변경
                txt_day.setText(dayString);
                // SharedPreference에 일자 저장
                saveDateDay(dayString);
                showLineChartData();
                break;
        }
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
    private void getNowTime() {
        //현재 시간
        long now = System.currentTimeMillis();
        date = new Date(now);
        SimpleDateFormat sdfYear = new SimpleDateFormat("yyyy");
        SimpleDateFormat sdfMonth = new SimpleDateFormat("MM");
        SimpleDateFormat sdfDay = new SimpleDateFormat("dd");
        SimpleDateFormat sdfHour = new SimpleDateFormat("HH");
        SimpleDateFormat sdfMinute = new SimpleDateFormat("mm");
        yearData = sdfYear.format(date);
        monthData = sdfMonth.format(date);
        dayData = sdfDay.format(date);
        hourData = sdfHour.format(date);
        minuteData = sdfMinute.format(date);
        year = yearData;
        month = monthData;
        day = dayData;
    }

    private void showLineChartData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                current1Ref = mDatabase.getReference("Current_minute_" + yearString + monthString + day);
                final ArrayList<Float> current1_data = new ArrayList<>();
                final ArrayList<String> xEntry = new ArrayList<>();

                childEventListener = new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                        try {

                            Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();

                            float current1 = Float.parseFloat(data.get("Current1").toString());
                            String timestamp = data.get("timestamp").toString();
                            String timestamp_time = timestamp.substring(8, 10) + ":" + timestamp.substring(10, 12);
                            xEntry.add(timestamp_time);

                            current1_data.add(current1);

                            ArrayList<Entry> yData = new ArrayList<>();
                            ArrayList<ILineDataSet> dataSets = new ArrayList<>();

                            float total = 0;
                            //a의 data로 a는 value값을 int형으로 변환한 것. value는 데이터가 바뀔 때의 값으로 업데이트 될 때마다 값이 나온다.
                            for (int j = 0; j < current1_data.size(); j++) {
                                yData.add(new Entry(j, current1_data.get(j) * 220));

                                total = total + current1_data.get(j);
                            }

                            float average = total / current1_data.size() * 220;

                            LineDataSet set1 = new LineDataSet(yData, "평균 전력 = " + average + "W");
                            set1.setFillAlpha(110);
                            set1.setColor(Color.BLACK);
                            set1.setLineWidth(3f);


                            dataSets.add(set1);

                            LineData data2 = new LineData(dataSets);

                            mChart.animateX(1000);
                            //      mChart.setOnChartGestureListener(SecondFragmentThermo.this);
                            //      mChart.setOnChartValueSelectedListener(SecondFragmentThermo.this);

                            mChart.setData(data2);

                            mChart.setDragEnabled(true);
                            mChart.setScaleEnabled(true);

                            // 오른쪽 와이축 없앰
                            mChart.getAxisRight().setEnabled(false);

                            XAxis xAxis = mChart.getXAxis();
                            xAxis.setValueFormatter(new IAxisValueFormatter() {
                                @Override
                                public String getFormattedValue(float value, AxisBase axis) {
                                    String val = null;
                                    try {
                                        val = xEntry.get((int) value);
                                    } catch (IndexOutOfBoundsException e) {
                                        axis.setGranularityEnabled(false);
                                    }
                                    return val;
                                }
                            });
                            xAxis.setGranularity(1);
                            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

                            YAxis leftAxis = mChart.getAxisLeft();

                            //측정 값중 최댓값 구하기
                            float y_max = Collections.max(current1_data);

                            leftAxis.setAxisMaximum(y_max + 50);              //보여지는 최대
                            leftAxis.setAxisMinimum(0f);
                        } catch (Exception e) {

                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                };
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        current1Ref.addChildEventListener(childEventListener);

                    }
                });
            }
        }).start();


    }
}
