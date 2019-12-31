package kr.co.aiotlab.www.LineChart;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

import androidx.annotation.Nullable;
import kr.co.aiotlab.www.R;

public class LinechartSensor10 extends Fragment implements View.OnClickListener {

    private LineChart mChart;
    private View view;
    private FirebaseDatabase mDatabase;
    private Date date;
    private LimitLine limit_up, limit_down;
    int up_limit_num, down_limit_num;
    private TextView txt_backChart, txt_nextChart, txt_chart_date;
    String time, year, month, day;
    String yearData, monthData, dayData, hourData, minuteData;
    DatabaseReference brightRef;
    ChildEventListener childEventListener;

    public static LinechartSensor10 newInstance(){
        LinechartSensor10 f = new LinechartSensor10();
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_sensor10, container, false);

        mChart = view.findViewById(R.id.linechart_sensor10);
        mChart.setNoDataText("데이터를 불러오는 중입니다.");
        mChart.setNoDataTextColor(Color.BLUE);
        mDatabase = FirebaseDatabase.getInstance();

        txt_chart_date = view.findViewById(R.id.txt_chart_date);
        txt_nextChart = view.findViewById(R.id.txt_nextChart);
        txt_backChart = view.findViewById(R.id.txt_backChart);
        txt_nextChart.setOnClickListener(this);
        txt_backChart.setOnClickListener(this);

        LimitLine limit_high_risk = new LimitLine(1800, "위험");
        LimitLine limit_hot = new LimitLine(1300, "더움");
        LimitLine limit_cold = new LimitLine(900, "추움");


        limit_high_risk.setLineColor(Color.RED);
        limit_high_risk.setLineWidth(2f);
        limit_high_risk.enableDashedLine(10f,10f,0f);
        limit_high_risk.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        limit_high_risk.setTextColor(Color.RED);

        limit_hot.setLineColor(Color.MAGENTA);
        limit_hot.setLineWidth(2f);
        limit_hot.enableDashedLine(10f,10f,0f);
        limit_hot.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        limit_hot.setTextColor(Color.MAGENTA);

        limit_cold.setLineColor(Color.BLUE);
        limit_cold.setLineWidth(2f);
        limit_cold.enableDashedLine(10f,10f,0f);
        limit_cold.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        limit_cold.setTextColor(Color.BLUE);

        // 상,하안선 세부설정
        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.removeAllLimitLines();
        leftAxis.addLimitLine(limit_cold);
        leftAxis.addLimitLine(limit_hot);
        leftAxis.addLimitLine(limit_high_risk); //상한선 추가(왼쪽 축)
        leftAxis.enableGridDashedLine(10f,10f,0f);
        leftAxis.setDrawLimitLinesBehindData(true);

        getNowTime();
        txt_chart_date.setText(year + "년 " + month + "월");
        showLineChartData();

        return view;
    }

    @Override
    public void onClick(View v) {
        brightRef.removeEventListener(childEventListener);
        switch (v.getId()){
            case R.id.txt_backChart:
                month = String.valueOf(Integer.parseInt(month) - 1);
                if (Integer.parseInt(month) < 10){
                    month = "0" + month;
                }

                if (Integer.parseInt(month) <= 1){
                    txt_backChart.setVisibility(View.INVISIBLE);
                }else {
                    txt_nextChart.setVisibility(View.VISIBLE);
                }

                txt_chart_date.setText(year + "년 " + month + "월");
                showLineChartData();
                break;
            case R.id.txt_nextChart:
                month = String.valueOf(Integer.parseInt(month) + 1);
                if (Integer.parseInt(month) < 10){
                    month = "0" + month;
                }

                if (Integer.parseInt(month) > 11){
                    txt_nextChart.setVisibility(View.INVISIBLE);
                }else {

                    txt_backChart.setVisibility(View.VISIBLE);
                }

                txt_chart_date.setText(year + "년 " + month + "월");
                showLineChartData();
                break;
        }
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
        yearData = sdfYear.format(date);
        monthData = sdfMonth.format(date);
        dayData = sdfDay.format(date);
        hourData = sdfHour.format(date);
        minuteData = sdfMinute.format(date);
        year = yearData;
        month = monthData;
        day = dayData;
    }


    private void showLineChartData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                brightRef = mDatabase.getReference("MQTT_Calorie_day_" + year + month);
                final ArrayList<Float> bright_data = new ArrayList<>();
                final ArrayList<String> xEntry= new ArrayList<>();
                childEventListener = new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                        Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();

                        float bright = Float.parseFloat(data.get("calorie").toString());
                        String timestamp = data.get("timestamp").toString();
                        String timestamp_time = timestamp.substring(6, 8) + "일";
                        xEntry.add(timestamp_time);
                        bright_data.add(bright);

                        ArrayList<Entry> yData = new ArrayList<>();
                        ArrayList<ILineDataSet> dataSets= new ArrayList<>();

                        float total  = 0;
                        //a의 data로 a는 value값을 int형으로 변환한 것. value는 데이터가 바뀔 때의 값으로 업데이트 될 때마다 값이 나온다.
                        for(int j = 0; j < bright_data.size(); j++){
                            yData.add(new Entry(j, bright_data.get(j)));

                            total = total + bright_data.get(j);
                        }

                        float average = total/bright_data.size();

                        LineDataSet set1 = new LineDataSet(yData,"평균 열량지수 = "  + average);
                        set1.setFillAlpha(110);
                        set1.setColor(Color.BLACK);
                        set1.setLineWidth(3f);
                        set1.setFillColor(Color.RED);
                        set1.setDrawFilled(true);

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
                        xAxis.setGranularity(1f);
                        xAxis.setValueFormatter(new IAxisValueFormatter() {
                            @Override
                            public String getFormattedValue(float value, AxisBase axis) {
                                String val = null;
                                try{
                                    val = xEntry.get((int) value);
                                } catch (IndexOutOfBoundsException e){
                                    axis.setGranularityEnabled(false);
                                }
                                return val;
                            }
                        });
                        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

                        YAxis leftAxis = mChart.getAxisLeft();
//측정 값중 최댓값 구하기
                        float y_max = Collections.max(bright_data);
                        float y_min = Collections.min(bright_data);

                        leftAxis.setAxisMaximum(y_max + 400);               //보여지는 최대
                        leftAxis.setAxisMinimum(y_min - 400);

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
                        brightRef.addChildEventListener( childEventListener);

                    }
                });
            }
        }).start();


    }
}
