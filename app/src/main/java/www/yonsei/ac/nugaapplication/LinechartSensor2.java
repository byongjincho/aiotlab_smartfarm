package www.yonsei.ac.nugaapplication;

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

import java.util.ArrayList;

import androidx.annotation.Nullable;
import www.nugamedical.com.R;

public class LinechartSensor2 extends Fragment {

    private LineChart mChart;

    private View view;

    public static LinechartSensor2 newInstance() {
        LinechartSensor2 f = new LinechartSensor2();
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_sensor2, container, false);

        mChart = view.findViewById(R.id.linechart_sensor2);

        //      mChart.setOnChartGestureListener(SecondFragmentThermo.this);
        //      mChart.setOnChartValueSelectedListener(SecondFragmentThermo.this);

        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);


        // 오른쪽 와이축 없앰
        mChart.getAxisRight().setEnabled(false);

        //그래프 데이터 입력
        ArrayList<Entry> yData = new ArrayList<>();
        yData.add(new Entry(0, 342));
        yData.add(new Entry(1, 395));
        yData.add(new Entry(2, 261));
        yData.add(new Entry(3, 155));
        yData.add(new Entry(4, 426));
        yData.add(new Entry(5, 325));
        yData.add(new Entry(6, 215));

        LineDataSet set1 = new LineDataSet(yData, "센서2");
        set1.setFillAlpha(110);
        set1.setColor(Color.BLACK);
        set1.setLineWidth(3f);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        LineData data = new LineData(dataSets);

        mChart.setData(data);

        String[] values = new String[]{"월", "화", "수", "목", "금", "토", "일"};

        XAxis xAxis = mChart.getXAxis();
        xAxis.setValueFormatter(new MyXAxisValueFormatter(values));
        xAxis.setGranularity(1);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis leftAxis = mChart.getAxisLeft();

        leftAxis.setAxisMaximum(1000f);               //보여지는 최대
        leftAxis.setAxisMinimum(0f);               //보여지는 최소

        return view;
    }


    public class MyXAxisValueFormatter implements IAxisValueFormatter {
        private String[] mValues;

        public MyXAxisValueFormatter(String[] values) {
            this.mValues = values;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return mValues[(int) value];
        }
    }
}
