package kr.co.aiotlab.www.LineChart;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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

import androidx.annotation.Nullable;
import kr.co.aiotlab.www.R;

public class
LinechartSensor2 extends Fragment implements View.OnClickListener {
    public static final String TAG = "LINE2";

    private LineChart mChart;
    private FirebaseDatabase mDatabase;
    private View view;
    private Date date;
    private static final String GETVALUE = "temperature";
    private TextView txt_backChart, txt_nextChart, txt_chart_date;
    String time, year, month, day;
    String yearData, monthData, dayData, hourData, minuteData;
    DatabaseReference temp1Ref;
    ChildEventListener childEventListener;

    public static LinechartSensor2 newInstance() {
        LinechartSensor2 f = new LinechartSensor2();
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_sensor2, container, false);

        mChart = view.findViewById(R.id.linechart_sensor2);
        mChart.setNoDataText("데이터를 불러오는 중입니다.");
        mChart.setNoDataTextColor(Color.BLUE);
        mDatabase = FirebaseDatabase.getInstance();

        txt_chart_date = view.findViewById(R.id.txt_chart_date);
        txt_nextChart = view.findViewById(R.id.txt_nextChart);
        txt_backChart = view.findViewById(R.id.txt_backChart);

        getNowTime();
        txt_chart_date.setText(year + "년 " + month + "월");
        showLineChartData();

        txt_nextChart.setOnClickListener(this);
        txt_backChart.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        temp1Ref.removeEventListener(childEventListener);
        switch (v.getId()) {
            case R.id.txt_backChart:
                month = String.valueOf(Integer.parseInt(month) - 1);
                if (Integer.parseInt(month) < 10) {
                    month = "0" + month;
                }

                if (Integer.parseInt(month) <= 1) {
                    txt_backChart.setVisibility(View.INVISIBLE);
                } else {
                    txt_nextChart.setVisibility(View.VISIBLE);
                }

                txt_chart_date.setText(year + "년 " + month + "월");
                showLineChartData();
                break;
            case R.id.txt_nextChart:
                month = String.valueOf(Integer.parseInt(month) + 1);
                if (Integer.parseInt(month) < 10) {
                    month = "0" + month;
                }

                if (Integer.parseInt(month) > 11) {
                    txt_nextChart.setVisibility(View.INVISIBLE);
                } else {

                    txt_backChart.setVisibility(View.VISIBLE);
                }

                txt_chart_date.setText(year + "년 " + month + "월");
                showLineChartData();
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
            @Override
            public void run() {
                temp1Ref = mDatabase.getReference("MQTT_Temperature_day_" + year + month);
                final ArrayList<Float> temp_data = new ArrayList<>();
                final ArrayList<String> xEntrys = new ArrayList<>();
                childEventListener = new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                        Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                        Log.d(TAG, "onChildAdded:" + data.get(GETVALUE).toString());

                        float temp = Float.parseFloat(data.get(GETVALUE).toString());
                        String timestamp = data.get("timestamp").toString();
                        String timestamp_hour = timestamp.substring(6, 8) + "일";

                        temp_data.add(temp);
                        xEntrys.add(timestamp_hour);

                        ArrayList<Entry> yData = new ArrayList<>();
                        ArrayList<ILineDataSet> dataSets = new ArrayList<>();

                        //a의 data로 a는 value값을 int형으로 변환한 것. value는 데이터가 바뀔 때의 값으로 업데이트 될 때마다 값이 나온다.
                        for (int j = 0; j < temp_data.size(); j++) {
                            yData.add(new Entry(j, temp_data.get(j)));
                        }

                        LineDataSet set1 = new LineDataSet(yData, "온도 (℃)");
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
                                    val = xEntrys.get((int) value);
                                } catch (IndexOutOfBoundsException e) {
                                    axis.setGranularityEnabled(false);
                                }
                                return val;
                            }
                        });
                        xAxis.setGranularity(1);
                        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

                        YAxis leftAxis = mChart.getAxisLeft();

                        float y_max = Collections.max(temp_data);
                        float y_min = Collections.min(temp_data);

                        leftAxis.setAxisMaximum(y_max + 3);               //보여지는 최대
                        leftAxis.setAxisMinimum(y_min - 3);

                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @android.support.annotation.Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                };
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        temp1Ref.addChildEventListener(childEventListener);
                    }
                });
            }
        }).start();

    }
}
