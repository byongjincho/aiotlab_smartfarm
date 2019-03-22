package www.yonsei.ac.nugaapplication;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.print.PrinterId;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import androidx.annotation.Nullable;
import www.nugamedical.com.R;

public class LinechartSensor1 extends Fragment {

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;

    private LineChart mChart;
    private Thread thread;
    private boolean plotData = true;

    private View view;
    private FirebaseDatabase mDatabase;
    private Date date;
    ArrayList<Entry> yData = new ArrayList<>();
    // a 데이터(firebase에서 가져온 값)을 배열에 쌓기 위함
    ArrayList<Integer> aData = new ArrayList<>();

    public static LinechartSensor1 newInstance(){
        LinechartSensor1 f = new LinechartSensor1();
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_sensor1, container, false);
        mDatabase = FirebaseDatabase.getInstance();

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

        int minuteInt = Integer.parseInt(minuteData);
        //2분 전 데이터 탐색하기 위함
        int minute_1 = minuteInt - 2;

        String minuteString = String.valueOf(minute_1);

        //데이터베이스
        final String time, year, month, day;

        year = yearData;
        month = monthData;
        day = dayData;
        time = hourData + " : " + minuteString;

        DatabaseReference temperature = mDatabase.getReference("DHT11_Data").child("Temp").child("Temperature");


        temperature.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    String value = dataSnapshot.getValue(String.class);
                    int a = Integer.parseInt(value);
                    aData.add(a);

                mChart = view.findViewById(R.id.linechart_sensor1);

                //      mChart.setOnChartGestureListener(SecondFragmentThermo.this);
                //      mChart.setOnChartValueSelectedListener(SecondFragmentThermo.this);

                mChart.setDragEnabled(true);
                mChart.setScaleEnabled(true);


                // 오른쪽 와이축 없앰
                mChart.getAxisRight().setEnabled(false);
                //그래프 데이터 입력

                //a의 data로 a는 value값을 int형으로 변환한 것. value는 데이터가 바뀔 때의 값으로 업데이트 될 때마다 값이 나온다.
                for(int j = 0; j < aData.size(); j++){
                    yData.add(new Entry(j, aData.get(j)));
                }

                    LineDataSet set1 = new LineDataSet(yData,"온도센서");
                    set1.setFillAlpha(110);
                    set1.setColor(Color.BLACK);
                    set1.setLineWidth(3f);

                    ArrayList<ILineDataSet> dataSets= new ArrayList<>();
                    dataSets.add(set1);

                    LineData data = new LineData(dataSets);

                    mChart.setData(data);


                    XAxis xAxis = mChart.getXAxis();
                    xAxis.setGranularity(1);
                    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

                    YAxis leftAxis = mChart.getAxisLeft();

                    leftAxis.setAxisMaximum(50f);               //보여지는 최대
                    leftAxis.setAxisMinimum(0f);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }

}
