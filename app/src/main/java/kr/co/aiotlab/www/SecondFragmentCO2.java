package kr.co.aiotlab.www;


import android.support.v4.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

import java.util.ArrayList;


public class SecondFragmentCO2 extends Fragment {

    private LineChart mChart;


    private View view;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_frag2_co2, container, false);

        mChart = view.findViewById(R.id.chart_co2);

        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);

        //상한선, 하한선 설정
        // {
        LimitLine upper_limit = new LimitLine(1000,"나쁨");


        upper_limit.setLineWidth(4f);
        upper_limit.setLineColor(Color.YELLOW);
        upper_limit.enableDashedLine(10f,10f,0f);
        upper_limit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        upper_limit.setTextColor(Color.RED);
        upper_limit.setTextSize(15f);


        // 상,하안선 세부설정
        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.removeAllLimitLines();
        leftAxis.addLimitLine(upper_limit);
        leftAxis.setAxisMaximum(1500f);               //보여지는 최대
        leftAxis.setAxisMinimum(100f);               //보여지는 최소
        leftAxis.enableGridDashedLine(10f,10f,0f);
        leftAxis.setDrawLimitLinesBehindData(true);
        //}


        // 오른쪽 와이축 없앰
        mChart.getAxisRight().setEnabled(false);

        //그래프 데이터 입력
        ArrayList<Entry> yData = new ArrayList<>();
        yData.add(new Entry(0,500));
        yData.add(new Entry(1,460));
        yData.add(new Entry(2,864));
        yData.add(new Entry(3,812));
        yData.add(new Entry(4,646));
        yData.add(new Entry(5,297));
        yData.add(new Entry(6,1205));

        LineDataSet set1 = new LineDataSet(yData,"CO2");
        set1.setFillAlpha(110);
        set1.setColor(Color.BLACK);
        set1.setLineWidth(3f);

        ArrayList<ILineDataSet> dataSets= new ArrayList<>();
        dataSets.add(set1);

        LineData data = new LineData(dataSets);

        mChart.setData(data);

        String[] values = new String[]{"월","화","수","목","금","토","일"};

        XAxis xAxis = mChart.getXAxis();
        xAxis.setValueFormatter(new MyXAxisValueFormatter(values));
        xAxis.setGranularity(1);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        return view;
    }

    public class MyXAxisValueFormatter implements IAxisValueFormatter {
        private String[] mValues;

        public MyXAxisValueFormatter(String[] values){
            this.mValues = values;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return mValues[(int)value];
        }
    }

}
