package kr.co.aiotlab.www.LineChart;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
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
import java.util.Date;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import kr.co.aiotlab.www.R;

import static kr.co.aiotlab.www.Main_UI.BottomFourthFragment.txt_day;

public class Linechart_Brightness_Minute extends Fragment implements View.OnClickListener {

    private LineChart mChart;
    private View view;
    private FirebaseDatabase mDatabase;
    private Date date;
    private static final String GETVALUE = "Bright";
    private TextView txt_backChart, txt_nextChart;
    int up_limit_num, down_limit_num;
    private LimitLine limit_up, limit_down;

    String time, year, month, day;
    String yearData, monthData, dayData, hourData, minuteData;
    DatabaseReference humidRef;
    ChildEventListener childEventListener;
    // 저장된 날짜 담아오기
    private String yearString, monthString, dayString;

    public static Linechart_Brightness_Minute newInstance() {
        Linechart_Brightness_Minute f = new Linechart_Brightness_Minute();
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_sensor5, container, false);

        mChart = view.findViewById(R.id.linechart_sensor5);
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

        // 상한선 설정
        SharedPreferences bright = this.getActivity().getSharedPreferences("Threshold_Bright", Context.MODE_PRIVATE);

        String a = bright.getString("BH", "0");
        String b = bright.getString("BL", "0");

        up_limit_num = getCDS() + Integer.parseInt(a);
        down_limit_num = getCDS() - Integer.parseInt(b);

        limit_up = new LimitLine(up_limit_num, "불꺼짐");
        limit_down = new LimitLine(down_limit_num, "불켜짐");
        limit_up.setLineWidth(2f);
        limit_up.enableDashedLine(10f, 10f, 0f);
        limit_up.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        limit_up.setTextColor(Color.RED);
        limit_up.setTextSize(10f);

        limit_down.setLineWidth(2f);
        limit_down.enableDashedLine(10f, 10f, 0f);
        limit_down.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        limit_down.setTextColor(Color.RED);
        limit_down.setTextSize(10f);

        // 상,하안선 세부설정
        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.removeAllLimitLines();
        leftAxis.addLimitLine(limit_up);
        leftAxis.addLimitLine(limit_down); //상한선 추가(왼쪽 축)
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawLimitLinesBehindData(true);
        return view;
    }

    @Override
    public void onClick(View v) {
        humidRef.removeEventListener(childEventListener);
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
    //setting에서 설정한 CDS 제한 값 가져오기
    private int getCDS() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        // key값이 list5로 저장되어있는 CDS설정값을 정수형으로 변환하고 반환해준다.(String으로400 LUX형태로 되어있는 것을 int형으로 바꾸니까 string은 지워짐.)
        return Integer.parseInt(sharedPreferences.getString("SETBRIGHT", "0"));
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
                humidRef = mDatabase.getReference("Bright_minute_" + yearString + monthString + dayString);
                final ArrayList<Float> bright_data = new ArrayList<>();

                final ArrayList<Float> humid_data = new ArrayList<>();
                final ArrayList<String> xEntry = new ArrayList<>();

                childEventListener = new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                        try {

                            Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();

                            String timestamp = data.get("timestamp").toString();
                            String timestamp_time = timestamp.substring(8, 10) + ":" + timestamp.substring(10, 12);
                            xEntry.add(timestamp_time);

                            try {
                                float humid = Float.parseFloat(data.get(GETVALUE).toString());
                                humid_data.add(humid);
                            } catch (NumberFormatException e) {
                                Toast.makeText(getContext(), "비정상적인 값 감지", Toast.LENGTH_SHORT).show();
                            }

                            ArrayList<Entry> yData = new ArrayList<>();

                            //a의 data로 a는 value값을 int형으로 변환한 것. value는 데이터가 바뀔 때의 값으로 업데이트 될 때마다 값이 나온다.
                            for (int j = 0; j < humid_data.size(); j++) {
                                yData.add(new Entry(j, humid_data.get(j)));
                            }

                            ArrayList<ILineDataSet> dataSets = new ArrayList<>();

                            float total = 0;
                            //a의 data로 a는 value값을 int형으로 변환한 것. value는 데이터가 바뀔 때의 값으로 업데이트 될 때마다 값이 나온다.
                            for (int j = 0; j < bright_data.size(); j++) {
                                yData.add(new Entry(j, bright_data.get(j)));

                                total = total + bright_data.get(j);
                            }

                            float average = total / bright_data.size();

                            LineDataSet set1 = new LineDataSet(yData, "평균 조도 = " + average + "lux");
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
                            xAxis.setGranularity(1f);
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
                            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

                            YAxis leftAxis = mChart.getAxisLeft();

                            leftAxis.setAxisMaximum(1000f);               //보여지는 최대
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
                        humidRef.addChildEventListener(childEventListener);
                    }
                });
            }
        }).start();

    }
}
