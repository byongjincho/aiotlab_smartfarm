package www.yonsei.ac.nugaapplication;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

import www.nugamedical.com.R;

public class FourthFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private SwipeRefreshLayout swipe_frag4;
    private PieChart mPiechart;
    private Spinner sensor_select;

 public  static FourthFragment newInstance(){
        FourthFragment f = new FourthFragment();
        return f;
    }

    View view;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag4, container, false);

        swipe_frag4 = view.findViewById(R.id.swipe_frag4);
        //새로고침
        swipe_frag4.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                showFragment(FourthFragment.newInstance());

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipe_frag4.setRefreshing(false);
                    }
                },3000);
            }
        });

                ////////////////////////////Pie chart///////////////////////////

        mPiechart = view.findViewById(R.id.piechart);
        mPiechart.setUsePercentValues(true);
        mPiechart.getDescription().setEnabled(false);
        mPiechart.setExtraOffsets(5, 10, 5, 5);

        mPiechart.setDragDecelerationFrictionCoef(0.95f);               //돌아가는 부드러움 설정

        mPiechart.setDrawHoleEnabled(true);                             //여러 효과
        mPiechart.setHoleColor(Color.WHITE);
        mPiechart.setTransparentCircleRadius(61f);

        ArrayList<PieEntry> yValues = new ArrayList<>();

        yValues.add(new PieEntry(34f,"센서1"));
        yValues.add(new PieEntry(31f,"센서2"));
        yValues.add(new PieEntry(54f,"센서3"));
        yValues.add(new PieEntry(43f,"센서4"));
        yValues.add(new PieEntry(56f,"센서5"));
        yValues.add(new PieEntry(84f,"센서6"));
        yValues.add(new PieEntry(64f,"센서7"));
        yValues.add(new PieEntry(74f,"센서8"));
        yValues.add(new PieEntry(24f,"센서9"));
        yValues.add(new PieEntry(44f,"센서10"));

        Description description = new Description();                        //설명란
        description.setText("총 전력량 비율입니다.");
        description.setTextSize(15);
        mPiechart.setDescription(description);

        mPiechart.animateY(1000, Easing.EasingOption.EaseInCubic);       //애니메이션 효과

        PieDataSet dataSet = new PieDataSet(yValues,"전력사용");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);

        PieData data2 = new PieData(dataSet);

        data2.setValueTextColor(Color.YELLOW);
        data2.setValueTextSize(10f);

        mPiechart.setData(data2);
///////////////////////////////////////////////////////////////////////////////////

        //////////////////spinner//////////////////
        sensor_select = view.findViewById(R.id.sensor_select);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.sensors, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        sensor_select.setAdapter(adapter);
        sensor_select.setOnItemSelectedListener(this);

//////////////////////////////////////////////////////////////


        return view;
    }

    // 스피너 선택
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        switch (position){
            case 0:
                getFragmentManager().beginTransaction().replace(R.id.frame_linechart, new LinechartSensor1()).commit();    // 센서1
                break;
            case 1:
                getFragmentManager().beginTransaction().replace(R.id.frame_linechart, new LinechartSensor2()).commit();    //센서2
                break;
            case 2:
                getFragmentManager().beginTransaction().replace(R.id.frame_linechart, new LinechartSensor3()).commit();    // 센서3
                break;
            case 3:
                getFragmentManager().beginTransaction().replace(R.id.frame_linechart, new LinechartSensor4()).commit();     // 센서4
                break;
            case 4:
                getFragmentManager().beginTransaction().replace(R.id.frame_linechart, new LinechartSensor5()).commit();     // 센서5
                break;
            case 5:
                getFragmentManager().beginTransaction().replace(R.id.frame_linechart, new LinechartSensor6()).commit();     // 센서6
                break;
            case 6:
                getFragmentManager().beginTransaction().replace(R.id.frame_linechart, new LinechartSensor7()).commit();     // 센서7
                break;
            case 7:
                getFragmentManager().beginTransaction().replace(R.id.frame_linechart, new LinechartSensor8()).commit();     // 센서8
                break;
            case 8:
                getFragmentManager().beginTransaction().replace(R.id.frame_linechart, new LinechartSensor9()).commit();     // 센서9
                break;
            case 9:
                getFragmentManager().beginTransaction().replace(R.id.frame_linechart, new LinechartSensor10()).commit();     // 센서10
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    public void showFragment(Fragment f){
        getFragmentManager().beginTransaction().replace(R.id.frame_main, f).commit();
    }


}
