package kr.co.aiotlab.www.LineChart;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.LimitLine;
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

public class Linehart_Temperature_Minute extends Fragment implements View.OnClickListener {
    public static final String TAG = "LINE1";

    private LineChart mChart;

    private View view;
    private FirebaseDatabase mDatabase;
    private Date date;
    private TextView txt_backChart, txt_nextChart;
    private static TextView txt_chart_date;
    String time, year, month, day;
    int set_temp;
    String yearData, monthData, dayData, hourData, minuteData;
    DatabaseReference temp_minute_1Ref, ref;
    ChildEventListener childEventListener;

    // 저장된 날짜 담아오기
    private String yearString, monthString, dayString;

    public static Linehart_Temperature_Minute newInstance() {
        Linehart_Temperature_Minute f = new Linehart_Temperature_Minute();
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_sensor1, container, false);
        mDatabase = FirebaseDatabase.getInstance();
        mChart = view.findViewById(R.id.linechart_sensor1);
        mChart.setNoDataText("데이터를 불러오는 중입니다.");
        mChart.setNoDataTextColor(Color.BLUE);

        txt_nextChart = view.findViewById(R.id.txt_nextChart);
        txt_backChart = view.findViewById(R.id.txt_backChart);
        txt_nextChart.setOnClickListener(this);
        txt_backChart.setOnClickListener(this);

        SharedPreferences set_temp_pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        set_temp = Integer.parseInt(set_temp_pref.getString("SETTEMP", "0"));
        LimitLine limit_high_risk = new LimitLine(set_temp, "설정 온도");

        limit_high_risk.setLineColor(Color.RED);
        limit_high_risk.setLineWidth(2f);
        limit_high_risk.enableDashedLine(10f, 10f, 0f);
        limit_high_risk.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        limit_high_risk.setTextColor(Color.RED);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.removeAllLimitLines();
        leftAxis.addLimitLine(limit_high_risk); //상한선 추가(왼쪽 축)
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawLimitLinesBehindData(true);

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
        temp_minute_1Ref.removeEventListener(childEventListener);
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

                try {
                    showLineChartData();
                } catch (IndexOutOfBoundsException e) {
                    Toast.makeText(getContext(), "데이터가 없습니다.", Toast.LENGTH_SHORT).show();
                }

                Log.d(TAG, "onClick: day - 1 = " + dayString);
                // TextView 일자 변경
                txt_day.setText(dayString);
                // SharedPreference에 일자 저장
                saveDateDay(dayString);
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

                try {
                    showLineChartData();
                } catch (NullPointerException e) {
                    Toast.makeText(getContext(), "데이터가 없습니다.", Toast.LENGTH_SHORT).show();
                }
                Log.d(TAG, "onClick: day + 1 = " + dayString);
                // TextView 일자 변경
                txt_day.setText(dayString);
                // SharedPreference에 일자 저장
                saveDateDay(dayString);
                break;
        }
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
            final ArrayList<Float> temp_data_minute = new ArrayList<>();
            final ArrayList<String> xEntrys = new ArrayList<>();

            @Override
            public void run() {
                // 데이터 받아오기, 갱신
                // Log.d(TAG, "MQTT_Temperature_minute_" + year + month + day);
                temp_minute_1Ref = mDatabase.getReference("MQTT_Temperature_minute_" + yearString + monthString + dayString);
                childEventListener = new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                        try {
                            Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();

                            //Log.d(TAG, "onChildAdded:" + data.get("temperature").toString());
                            //Log.d(TAG, "onChildAdded:" + data.get("timestamp").toString());


                            float temp_minute = Float.parseFloat(data.get("temperature").toString());
                            String timestamp = (data.get("timestamp").toString());

                            String timestamp_hour = timestamp.substring(8, 10);
                            String timestamp_minute = timestamp.substring(10, 12);
                            String xAxis_time = timestamp_hour + ":" + timestamp_minute;

                            temp_data_minute.add(temp_minute);
                            xEntrys.add(xAxis_time);

                            //Log.d(TAG, "xaxisSize: " + xEntrys.size());
                            //Log.d(TAG, "yaxisSize: " + watt_data.size());

                            final String[] xaxes = new String[xEntrys.size()];

                            final ArrayList<Entry> yData = new ArrayList<>();

                            ArrayList<ILineDataSet> dataSets = new ArrayList<>();

                            float total = 0;
                            //a의 data로 a는 value값을 int형으로 변환한 것. value는 데이터가 바뀔 때의 값으로 업데이트 될 때마다 값이 나온다.
                            for (int j = 0; j < temp_data_minute.size(); j++) {
                                yData.add(new Entry(j, temp_data_minute.get(j)));
                                xaxes[j] = xEntrys.get(j);
                                // 평균 사용량
                                total = total + temp_data_minute.get(j);
                            }

                            float average = total / temp_data_minute.size();

                            //Log.d(TAG, "Ysize:" + yData.size());
                            //Log.d(TAG, "Xsize:" + xaxes.length);

                            //Line data setting
                            LineDataSet set1 = new LineDataSet(yData, "온도 (℃)," + "  평균 : " + average + "℃");
                            set1.setFillAlpha(110);
                            set1.setColor(Color.BLACK);
                            set1.setLineWidth(3f);

                            dataSets.add(set1);

                            LineData data2 = new LineData(dataSets);

                            //mChart.setVisibleXRange(10, 20);
                            //mChart.moveViewToX(watt_data.size());
                            mChart.animateX(1000);

                            //      mChart.setOnChartGestureListener(SecondFragmentThermo.this);
                            //      mChart.setOnChartValueSelectedListener(SecondFragmentThermo.this);

                            mChart.setData(data2);

                            mChart.setDragEnabled(true);
                            mChart.setScaleEnabled(true);

                            // 오른쪽 와이축 없앰
                            mChart.getAxisRight().setEnabled(false);

                            XAxis xAxis = mChart.getXAxis();
                            xAxis.setGranularity(1f);
                            xAxis.setValueFormatter(new IAxisValueFormatter() {
                                @Override
                                public String getFormattedValue(float value, AxisBase axis) {
                                    String val = null;
                                    try {
                                        val = xEntrys.get((int) value);
                                    } catch (IndexOutOfBoundsException e) {
                                        axis.setGranularityEnabled(false);
                                    }
                                    return val;
                                }
                            });

                            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                /*xAxis.setValueFormatter(new IAxisValueFormatter() {
                   @Override
                   public String getFormattedValue(float value, AxisBase axis) {
                       return xaxes[(int) value];
                   }
               }); */

                            YAxis leftAxis = mChart.getAxisLeft();

                            float y_max = Collections.max(temp_data_minute);
                            float y_min = Collections.min(temp_data_minute);

                            leftAxis.setAxisMaximum(y_max + 3);               //보여지는 최대
                            leftAxis.setAxisMinimum(y_min - 3);

                        } catch (Exception e) {
                            e.printStackTrace();
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
                        temp_minute_1Ref.addChildEventListener(childEventListener);

                    }
                });
            }
        }).start();
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
}
