package kr.co.aiotlab.www;


import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;


public class SecondFragmentBrightness extends Fragment {
    public static final String TAG = "SECONDFRAGMENT";

    private LineChart mChart;


    private  View view;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_frag2_brightness, container, false);
        mChart = view.findViewById(R.id.chart_brightness);

        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);

        // 오른쪽 와이축 없앰
        mChart.getAxisRight().setEnabled(false);

        //그래프 데이터 입력
        ArrayList<Entry> yData = new ArrayList<>();
        yData.add(new Entry(0,245));
        yData.add(new Entry(1,195));
        yData.add(new Entry(2,251));
        yData.add(new Entry(3,165));
        yData.add(new Entry(4,216));
        yData.add(new Entry(5,265));
        yData.add(new Entry(6,155));

        LineDataSet set1 = new LineDataSet(yData,"밝기");
        set1.setFillAlpha(110);
        set1.setColor(Color.BLACK);
        set1.setLineWidth(3f);

        ArrayList<ILineDataSet> dataSets= new ArrayList<>();
        dataSets.add(set1);

        LineData data = new LineData(dataSets);

        mChart.setData(data);

        /** x축 데이터 */
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
            Log.d(TAG, "1getFormattedValue: " + values);

        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            Log.d(TAG, "2getFormattedValue: " + value);
            Log.d(TAG, "3getFormattedValue: " +  mValues[(int)value]);
            return mValues[(int)value];
        }
    }

}
