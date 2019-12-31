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
import android.widget.Toast;

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
import java.util.Date;
import java.util.Map;

import androidx.annotation.Nullable;
import kr.co.aiotlab.www.R;

public class LinechartSensor6 extends Fragment implements View.OnClickListener {

    private LineChart mChart;
    private View view;
    private FirebaseDatabase mDatabase;
    private static final String GETVALUE = "Bright";
    private Date date;
    private TextView txt_backChart, txt_nextChart, txt_chart_date;
    String time, year, month, day;
    String yearData, monthData, dayData, hourData, minuteData;
    DatabaseReference humidRef;
    ChildEventListener childEventListener;

    public static LinechartSensor6 newInstance(){
        LinechartSensor6 f = new LinechartSensor6();
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_sensor6, container, false);

        mChart = view.findViewById(R.id.linechart_sensor6);
        mChart.setNoDataText("데이터를 불러오는 중입니다.");
        mChart.setNoDataTextColor(Color.BLUE);
        mDatabase = FirebaseDatabase.getInstance();

        txt_chart_date = view.findViewById(R.id.txt_chart_date);
        txt_nextChart = view.findViewById(R.id.txt_nextChart);
        txt_backChart = view.findViewById(R.id.txt_backChart);
        txt_nextChart.setOnClickListener(this);
        txt_backChart.setOnClickListener(this);

        getNowTime();
        txt_chart_date.setText(year + "년 " + month + "월");
        showLineChartData();

        return view;
    }

    @Override
    public void onClick(View v) {
        humidRef.removeEventListener(childEventListener);
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
                humidRef = mDatabase.getReference("Bright_day_" + year + month);
                final ArrayList<Float> humid_data = new ArrayList<>();
                final ArrayList<String> xEntry = new ArrayList<>();
                childEventListener = new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                        Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();

                        String timestamp = data.get("timestamp").toString();
                        String timestamp_day = timestamp.substring(6, 8) + "일";
                        xEntry.add(timestamp_day);

                        try {
                            float humid = Float.parseFloat(data.get(GETVALUE).toString());
                            float time = Float.parseFloat(data.get("timestamp").toString());
                            humid_data.add(humid);
                        }catch (NumberFormatException e){
                            Toast.makeText(getContext(), "비정상적인 값 감지", Toast.LENGTH_SHORT).show();
                        }

                        ArrayList<Entry> yData = new ArrayList<>();

                        //a의 data로 a는 value값을 int형으로 변환한 것. value는 데이터가 바뀔 때의 값으로 업데이트 될 때마다 값이 나온다.
                        for(int j = 0; j < humid_data.size(); j++){
                            yData.add(new Entry(j, humid_data.get(j)));
                        }

                        LineDataSet set1 = new LineDataSet(yData,"습도 (%)");
                        set1.setFillAlpha(110);
                        set1.setColor(Color.BLUE);
                        set1.setLineWidth(3f);
                        set1.setDrawFilled(true);

                        ArrayList<ILineDataSet> dataSets= new ArrayList<>();
                        dataSets.add(set1);

                        LineData data2 = new LineData(dataSets);

                        mChart.setData(data2);
                        //      mChart.setOnChartGestureListener(SecondFragmentThermo.this);
                        //      mChart.setOnChartValueSelectedListener(SecondFragmentThermo.this);

                        mChart.animateY(1000);
                        mChart.setDragEnabled(true);
                        mChart.setScaleEnabled(true);

                        /** x축 데이터 */
                        XAxis xAxis = mChart.getXAxis();
                        xAxis.setValueFormatter(new IAxisValueFormatter() {
                            @Override
                            public String getFormattedValue(float value, AxisBase axis) {
                                String val = null;
                                try {
                                    val = xEntry.get((int) value);
                                } catch (IndexOutOfBoundsException e){
                                    axis.setGranularityEnabled(false);
                                }
                                return val;
                            }
                        });
                        xAxis.setGranularity(1);
                        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

                        // 오른쪽 와이축 없앰
                        mChart.getAxisRight().setEnabled(false);

                        YAxis leftAxis = mChart.getAxisLeft();

                        leftAxis.setAxisMaximum(1000f);               //보여지는 최대
                        leftAxis.setAxisMinimum(0f);


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
                        humidRef.addChildEventListener( childEventListener);
                    }
                });
            }
        }).start();
    }
}
