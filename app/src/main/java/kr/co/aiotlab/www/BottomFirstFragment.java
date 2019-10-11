package kr.co.aiotlab.www;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static kr.co.aiotlab.www.MainActivity.dust;
import static kr.co.aiotlab.www.MainActivity.humi;
import static kr.co.aiotlab.www.MainActivity.temp;


public class BottomFirstFragment extends Fragment implements View.OnClickListener {


    private SwipeRefreshLayout swipe_frag1;
    private TextView weather_state, nowTemperature, nowHumidity, nowCity, now_small_dust, now_big_dust,
            getCDStext, searchCity, getGas, txt_getCo2, txt_current1;
    public static TextView txt_insidedust;
    private ImageView weather_icon, th_brightness, th_temp, img_fire, img_guard, img_gas;
    public static TextView getFire;
    private FirebaseDatabase mDatabase;

    public static TextView getTempText, getHumidityText, getInvasion;

    public static BottomFirstFragment newInstance() {
        BottomFirstFragment f = new BottomFirstFragment();
        return f;
    }

    View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.frag1, container, false);

        th_temp = view.findViewById(R.id.th_temp);
        swipe_frag1 = view.findViewById(R.id.swipe_frag1);
        weather_state = view.findViewById(R.id.weather_state);
        nowTemperature = view.findViewById(R.id.nowTemperature);
        nowHumidity = view.findViewById(R.id.nowHumidity);
        nowCity = view.findViewById(R.id.nowCity);
        weather_icon = view.findViewById(R.id.weather_icon);
        now_small_dust = view.findViewById(R.id.now_small_dust);
        now_big_dust = view.findViewById(R.id.now_big_dust);
        getTempText = view.findViewById(R.id.getTempText);
        getHumidityText = view.findViewById(R.id.getHumidityText);
        getCDStext = view.findViewById(R.id.getCDStext);
        th_brightness = view.findViewById(R.id.th_brightness);
        searchCity = view.findViewById(R.id.searchCity);
        getFire = view.findViewById(R.id.getFire);
        getInvasion = view.findViewById(R.id.getInvasion);
        img_fire = view.findViewById(R.id.img_fire);
        img_guard = view.findViewById(R.id.img_guard);
        img_gas = view.findViewById(R.id.img_gas);
        getGas = view.findViewById(R.id.getGas);
        getFire = view.findViewById(R.id.getFire);
        txt_insidedust = view.findViewById(R.id.txt_insidedust);
        txt_current1 = view.findViewById(R.id.txt_current1);
        txt_getCo2 = view.findViewById(R.id.txt_getCo2);

        th_brightness.setOnClickListener(this);
        th_temp.setOnClickListener(this);
        searchCity.setOnClickListener(this);

        SharedPreferences getMQTT = this.getActivity().getSharedPreferences("MQTT_SENSOR", Context.MODE_PRIVATE);
        int dust = getMQTT.getInt("DUST", 0);
        String temp = getMQTT.getString("TEMP", "-");
        String humi = getMQTT.getString("HUMI", "-");
        if (!temp.equals("") && !humi.equals("") && !String.valueOf(dust).equals("")) {
            txt_insidedust.setText(String.valueOf(dust));
            getTempText.setText(temp);
            getHumidityText.setText(humi);
        }

        String time, year, month, day;

        year = "2019";
        month = "03";
        day = "12";
        time = "10 : 32";


        /** 온습도 실시간 가져오기 */
        mDatabase = FirebaseDatabase.getInstance();

        DatabaseReference humidRef = mDatabase.getReference("DHT11_Data").child("Humid").child("Humidity");
        DatabaseReference tempRef = mDatabase.getReference("DHT11_Data").child("Temp").child("Temperature");
        DatabaseReference cdsRef = mDatabase.getReference("CDS_Data").child("CDS").child("LUX");
        DatabaseReference test = mDatabase.getReference("Sensor_Data").child(year).child(month).child(day).child(time).child("Temp_Data");
        DatabaseReference gasRef = mDatabase.getReference("Gas_Data").child("Gas").child("Gas");
        DatabaseReference current1Ref = mDatabase.getReference("Current1_Data").child("Current1").child("Current1");

        // 가스 실시간 가져오기
        gasRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                getGas.setText(value);
                txt_getCo2.setText(value);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // 전류1 실시간 가져오기
        current1Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                double watt1 = Double.parseDouble(value) * 220;
                int watt1_int = (int) watt1;
                txt_current1.setText(String.valueOf(watt1_int));


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        /*
        // Read from the database (humidity)
        humidRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                    getHumidityText.setText(value);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });

        // Threshold 설정한 값에 따른 설정
        SharedPreferences thtemp = this.getActivity().getSharedPreferences("TT", Context.MODE_PRIVATE);
        final String th = thtemp.getString("TH", "0");
        final String tl = thtemp.getString("TL", "0");

        SharedPreferences settemp = PreferenceManager.getDefaultSharedPreferences(getContext());
        final String setTemp = settemp.getString("SETTEMP", "0");

        // Read from the database (temperature)
        tempRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                //온도값 업데이트
                String value = dataSnapshot.getValue(String.class);
                if (!value.equals("")) {
                    //설정 값보다 낮으면 파랑색, 높으면 빨간색
                    if (Integer.parseInt(value) < Integer.parseInt(setTemp) - Integer.parseInt(tl)) {
                        getTempText.setTextColor(Color.BLUE);
                        getTempText.setText(value);
                    } else if (Integer.parseInt(value) > Integer.parseInt(setTemp) + Integer.parseInt(th)) {
                        getTempText.setTextColor(Color.RED);
                        getTempText.setText(value);
                    } else {
                        getTempText.setTextColor(Color.BLACK);
                        getTempText.setText(value);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });
        */
        // Read from the database (CDS)
        cdsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                getCDStext.setText(value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });


        /** 날씨 업데이트 */

        findWeather();
        findDust();

        /** 새로고침 */
        swipe_frag1.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            //새로고침할 항목
            @Override
            public void onRefresh() {
                findDust();
                findWeather();


                //새로고심 실행, 시간설정
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipe_frag1.setRefreshing(false);
                    }
                }, 1000);    //새로고침 바 보이는 시간 설정
            }
        });


        return view;
    }

    /**
     * 날씨
     */
    public void findWeather() {
        // 지역 설정된 값 가져오기
        SharedPreferences cityset = this.getContext().getSharedPreferences("CITY", Context.MODE_PRIVATE);
        String cityName = cityset.getString("CITYNAME", "");
        //open weather api 받아오기
        String Url = "https://api.openweathermap.org/data/2.5/weather?q=" + cityName + "&appid=27b1b8b908d5ad361af19ff8eee92989";
        //JSON형태로 저장된 url 받아오기
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, Url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // JSON 파일 받아올 종류 선정
                try {
                    JSONObject main_object = response.getJSONObject("main");
                    JSONArray array = response.getJSONArray("weather");
                    JSONObject object = array.getJSONObject(0);
                    String temp = String.valueOf(main_object.getDouble("temp"));
                    String humidity = String.valueOf(main_object.getInt("humidity"));
                    String descroption = object.getString("description");
                    String city = response.getString("name");

                    //humidity 소숫점 제거
                    //받아온 데이터를 어떻게 output해줄지 결정
                    if (Integer.parseInt(humidity) > 100) {
                        humidity = "100";
                    }
                    nowHumidity.setText(humidity);

                    nowCity.setText(city);
                    weather_state.setText(descroption);

                    //description(날씨)에 따라 그림 변화
                    // 안개
                    if (descroption.equals("haze")
                            || descroption.equals("smoke")
                            || descroption.equals("mist")
                            || descroption.equals("fog")
                            || descroption.equals("sand, dust whirl")
                            || descroption.equals("sand")
                            || descroption.equals("dust")
                            || descroption.equals("volcanic ash")
                            || descroption.equals("squalls")
                            || descroption.equals("tornado")) {
                        weather_icon.setImageResource(R.drawable.misticon);
                        //맑음
                    } else if (descroption.equals("clear sky")) {
                        weather_icon.setImageResource(R.drawable.sunny);
                        //눈
                    } else if (descroption.equals("light snow")
                            || descroption.equals("snow")
                            || descroption.equals("sleet")
                            || descroption.equals("shower sleet")
                            || descroption.equals("light rain and snow")
                            || descroption.equals("rain and snow")
                            || descroption.equals("light shower snow")
                            || descroption.equals("shower snow")
                            || descroption.equals("heavy shower snow")) {
                        weather_icon.setImageResource(R.drawable.snow);
                        // 구름
                    } else if (descroption.equals("scattered clouds")
                            || descroption.equals("few clouds")
                            || descroption.equals("broken clouds")
                            || descroption.equals("overcast clouds")) {
                        weather_icon.setImageResource(R.drawable.cloudy);
                        // 비
                    } else if (descroption.equals("thunderstorm")
                            || descroption.equals("thunderstorm with light rain")
                            || descroption.equals("thunderstorm with rain")
                            || descroption.equals("thunderstorm with heavy rain")
                            || descroption.equals("light thunderstorm")
                            || descroption.equals("heavy thunderstorm")
                            || descroption.equals("ragged thunderstorm")
                            || descroption.equals("thunderstorm with drizzle")
                            || descroption.equals("thunderstorm with light drizzle")
                            || descroption.equals("thunderstorm with heavy drizzle")
                            || descroption.equals("light intensity drizzle")) {
                        weather_icon.setImageResource(R.drawable.thunderrain);
                    } else if (descroption.equals("light rain")
                            || descroption.equals("moderate rain")
                            || descroption.equals("light intensity shower rain")
                            || descroption.equals("heavy intensity shower rain")) {
                        weather_icon.setImageResource(R.drawable.raining_icon);

                    }

                    //일자
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat adf = new SimpleDateFormat("EEEE-DD-MM");

                    //화씨로 표시된 온도를 도씨로 변경
                    double temp_int = Double.parseDouble(temp);
                    double centi = temp_int - 273.15;
                    centi = Math.round(centi);
                    int i = (int) centi;
                    nowTemperature.setText(String.valueOf(i));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "지역 설정을 다시 해주세요.", Toast.LENGTH_SHORT).show();
            }
        });

        /* 이전 응답 결과가 있어도 새로운 결과를 매 번 보여주기(캐싱 없애기)
        jor.setShouldCache(false);
        */
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(jor);
    }

    /**
     * 미세먼지 관련 코딩
     */
    //findweather와 동일
    public void findDust() {

        String Url = "http://openapi.airkorea.or.kr/openapi/services/rest/ArpltnInforInqireSvc/getCtprvnRltmMesureDnsty?" +
                "serviceKey=KAl20Yu4DWh1E5%2Fvrdrp%2FNvOZBFsJl5UuYusfbRaTPcoy%2F3%2F7kHat%2FUgQM7XlezQcpUQQ46zDj0ohNjQr1bK6g%3D%3D&numOfRows=10&pageNo=1&sidoName=강원&ver=1.3&_returnType=json";      //open weather api 받아오기

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, Url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray array = response.getJSONArray("list");
                    //JSON 형식 원주(중앙동) index
                    JSONObject atmosphere = array.getJSONObject(4);

                    String pm10 = atmosphere.getString("pm10Value");
                    String pm25 = atmosphere.getString("pm25Value");

                    //JASON으로 받아온 APK 값이 측정이 안될경우 -로 표시되는 예외 처리를 위해 int형으로 변환하는 작업
                    if (pm10.equals("-")) {
                        pm10 = "9999";
                    }
                    if (pm25.equals("-")) {
                        pm25 = "9999";
                    }
                    //미세먼지 농도에 따른 색깔

                    int pm10Int = Integer.parseInt(pm10);       //String을 int형으로 변환
                    int pm25int = Integer.parseInt(pm25);

                    if (pm10Int < 30) {
                        now_big_dust.setTextColor(Color.BLUE);
                        now_big_dust.setText(pm10);

                    } else if (pm10Int >= 30 && pm10Int < 80) {
                        now_big_dust.setTextColor(Color.GREEN);
                        now_big_dust.setText(pm10);

                    } else if (pm10Int >= 80 && pm10Int < 150) {
                        now_big_dust.setTextColor(Color.MAGENTA);
                        now_big_dust.setText(pm10);
                        Log.d("pm10", "30~80 : " + pm10);

                    } else if (pm10Int > 150 && pm10Int < 999) {
                        now_big_dust.setTextColor(Color.RED);
                        now_big_dust.setText(pm10);

                    } else if (pm10Int == 9999) {
                        now_big_dust.setText("자료없음");
                    }

                    if (pm25int < 15) {
                        now_small_dust.setTextColor(Color.BLUE);
                        now_small_dust.setText(pm25);
                    } else if (pm25int >= 15 && pm25int < 35) {
                        now_small_dust.setTextColor(Color.GREEN);
                        now_small_dust.setText(pm25);
                    } else if (pm25int >= 35 && pm25int < 75) {
                        now_small_dust.setTextColor(Color.MAGENTA);
                        now_small_dust.setText(pm25);
                    } else if (pm25int > 75 && pm25int < 999) {
                        now_small_dust.setTextColor(Color.RED);
                        now_small_dust.setText(pm25);
                    } else if (pm25int == 9999) {
                        now_small_dust.setText("자료없음");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(jor);
    }

    // Threshold 설정
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.th_brightness:
                Intent intent1 = new Intent(getContext(), ThresholdBrightness.class);
                startActivity(intent1);

                break;
            case R.id.th_temp:
                Intent intent2 = new Intent(getContext(), ThresholdTemperature.class);
                startActivity(intent2);
                break;
            case R.id.searchCity:
                Intent intent_searchCity = new Intent(getContext(), SearchCity.class);
                startActivity(intent_searchCity);
                break;
        }
    }

    @Override
    public void onDetach() {
        //mqtt
        SharedPreferences sharedPreferences_mqtt = this.getActivity().getSharedPreferences("MQTT_SENSOR", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences_mqtt.edit();
        editor.putString("TEMP", temp);
        editor.putString("HUMI", humi);
        editor.putInt("DUST", dust);
        editor.commit();
        super.onDetach();
    }

}