package kr.co.aiotlab.www.Main_UI;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

import kr.co.aiotlab.www.R;
import kr.co.aiotlab.www.ThresholdBrightness;
import kr.co.aiotlab.www.ThresholdTemperature;

import static kr.co.aiotlab.www.MainActivity.dust;
import static kr.co.aiotlab.www.MainActivity.humi;
import static kr.co.aiotlab.www.MainActivity.temp;


public class BottomFirstFragment extends Fragment implements View.OnClickListener {

    private SwipeRefreshLayout swipe_frag1;
    private TextView now_small_dust, now_big_dust,
            getCDStext, getGas, txt_getCo2, txt_current1;
    public static TextView txt_insidedust;
    private ImageView weather_icon, th_brightness, th_temp, img_fire, img_guard, img_gas;
    public static TextView getFire;
    private FirebaseDatabase mDatabase;

    public static TextView getTempText, getHumidityText, getInvasion;

    // Json Object
    JSONObject jsonObject, jsonObject_fire = null;
    String jsonMQTT, jsonMQTT_fire;

    // 날씨 Flag
    private boolean findWeatherFlag = true;
    // 날씨, 위치 관련
    private double latitude, longitude;
    private String area1, area2, area3, area4;
    private TextView txt_date, txt_location, txt_weather;
    private TextView txt_temp, txt_humidity, txt_wind;
    private ImageView img_weather;

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

        now_small_dust = view.findViewById(R.id.now_small_dust);
        now_big_dust = view.findViewById(R.id.now_big_dust);
        getTempText = view.findViewById(R.id.getTempText);
        getHumidityText = view.findViewById(R.id.getHumidityText);
        getCDStext = view.findViewById(R.id.getCDStext);
        th_brightness = view.findViewById(R.id.th_brightness);
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

        // 날씨, 위치정보 관련
        txt_location = view.findViewById(R.id.txt_location);
        txt_humidity = view.findViewById(R.id.txt_humidity);
        txt_wind = view.findViewById(R.id.txt_wind);
        txt_temp = view.findViewById(R.id.txt_temperature);
        img_weather = view.findViewById(R.id.img_weather);
        txt_date = view.findViewById(R.id.txt_date);
        txt_weather = view.findViewById(R.id.txt_weather_information);

        /** GPS 연동을 위한 권한 체크 및 위치정보 찾기 */
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(Objects.requireNonNull(getContext()),
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) getContext(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    0);
        } else {
            // 내위치 검색
            LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

            if (lm != null) {
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        10000,
                        1,
                        gpsLocationListener);
                lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        10000,
                        1,
                        gpsLocationListener);
            }
        }


        th_brightness.setOnClickListener(this);
        th_temp.setOnClickListener(this);

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
        findDust();

        /** 새로고침 */
        swipe_frag1.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            //새로고침할 항목
            @Override
            public void onRefresh() {
                findDust();

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
     * 날씨정보 받아오기
     */
    public void findWeather(String cityName) {

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
                    JSONObject wind_object = response.getJSONObject("wind");
                    JSONObject object = array.getJSONObject(0);
                    String temp = String.valueOf(main_object.getDouble("temp"));
                    String humidity = String.valueOf(main_object.getInt("humidity"));
                    String descroption = object.getString("description");
                    String wind = String.valueOf(wind_object.getString("speed"));
                    String city = response.getString("name");

                    txt_wind.setText(wind);

                    //humidity 소숫점 제거
                    //받아온 데이터를 어떻게 output해줄지 결정
                    if (Integer.parseInt(humidity) > 100) {
                        humidity = "100";
                    }
                    txt_humidity.setText(humidity);

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
                        img_weather.setImageResource(R.drawable.fog_icon);
                        txt_weather.setText("흐림");
                        //맑음
                    } else if (descroption.equals("clear sky")) {
                        img_weather.setImageResource(R.drawable.sunny_icon);
                        txt_weather.setText("맑음");
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
                        img_weather.setImageResource(R.drawable.snow_icon);
                        txt_weather.setText("눈 내림");
                        // 구름
                    } else if (descroption.equals("scattered clouds")
                            || descroption.equals("few clouds")
                            || descroption.equals("broken clouds")
                            || descroption.equals("overcast clouds")) {
                        img_weather.setImageResource(R.drawable.cloudy_icon);
                        txt_weather.setText("구름 많음");
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
                            || descroption.equals("light intensity drizzle")
                            || descroption.equals("heavy intensity shower rain")) {
                        img_weather.setImageResource(R.drawable.thunder_icon);
                        txt_weather.setText("폭우");
                    } else if (descroption.equals("light rain")
                            || descroption.equals("moderate rain")
                            || descroption.equals("shower rain")
                            || descroption.equals("intensity shower rain")
                            || descroption.equals("light intensity shower rain")) {
                        img_weather.setImageResource(R.drawable.heavy_rain_icon);
                        txt_weather.setText("비");

                    }

                    //화씨로 표시된 온도를 도씨로 변경
                    double temp_int = Double.parseDouble(temp);
                    double centi = temp_int - 273.15;
                    centi = Math.round(centi);
                    int i = (int) centi;
                    txt_temp.setText(String.valueOf(i));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "지역 설정을 다시 해주세요.", Toast.LENGTH_SHORT).show();
            }
        });

        /*  캐싱 유무 */
        RequestQueue queue = Volley.newRequestQueue(getContext());
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
        }
    }

    /**
     * 위치 정보 리스너
     */
    final LocationListener gpsLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            double now_longitude = location.getLongitude();
            double now_latitude = location.getLatitude();

            latitude = now_latitude;
            longitude = now_longitude;

            // 프래그먼트가 한 번 열릴때 한 번만 탐색
            if (findWeatherFlag) {
                getAddress(longitude, latitude);
                findWeatherFlag = false;
            }
            try {
                LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

                // 권한 체크
                if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getContext()),
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        10000,
                        1,
                        gpsLocationListener);

                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        10000,
                        1,
                        gpsLocationListener);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }
    };


    /**
     * 네이버 Reverse Geocoding
     */
    private void getAddress(final double longitude, final double latitude) {

        new Thread(new Runnable() {
            String clientId = "r85cr3jwpo";// 애플리케이션 클라이언트 아이디값";
            String clientSecret = "Xr7dhyWeXjeJsQzEog1FeFHIkxfB9LOYQXC2wj1B";// 애플리케이션 클라이언트 시크릿값";
            String json = null;

            @Override
            public void run() {
                try {
                    String apiURL = "https://naveropenapi.apigw.ntruss.com/map-reversegeocode/v2/gc?coords=" + longitude + "," + latitude + "&sourcecrs=epsg:4326&orders=legalcode,admcode,addr,roadaddr&output=json"; // json
                    URL url = new URL(apiURL);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    // 헤더부분 입력
                    con.setRequestProperty("X-NCP-APIGW-API-KEY-ID", clientId);
                    con.setRequestProperty("X-NCP-APIGW-API-KEY", clientSecret);

                    // request 코드가 200이면 정상적으로 호출된다고 나와있다.
                    int responseCode = con.getResponseCode();

                    BufferedReader br = null;

                    if (responseCode == 200) { // 정상 호출
                        //정상적으로 호출이 되면 읽어온다.
                        br = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    } else { // 에러 발생
                    }

                    // json으로 받아내는 코드!
                    String inputLine;
                    StringBuffer response = new StringBuffer();
                    //한 줄 한 줄 읽어들임
                    while ((inputLine = br.readLine()) != null) {
                        response.append(inputLine);
                    }
                    br.close();
                    json = response.toString();

                    // json값이 만약 null값이면 return시킴
                    if (json == null) {
                        return;
                    }

                    //이제 그 결과값 json이 잘 변환되어 들어왔는지 로그를 찍어서 확인해본다.
                    Log.d("TEST2", "json => " + json);

                    // json형식의 데이터를 String으로 변환하는 과정
                    JSONObject jsonObject = new JSONObject(json);

                    // results는 대괄호 []로 감싸져있다. -> Array변환
                    JSONArray resultsArray = jsonObject.getJSONArray("results");

                    JSONObject jsonObject1 = resultsArray.getJSONObject(0);

                    //이제 배열중 region에 area값들이 들어있기 때문에 중괄호 {}로 감싸진 region값을 가져온다.
                    JSONObject dataObject = (JSONObject) jsonObject1.get("region");

                    // region에서 area1, area2, area3, area4를 각각 또 불러와야한다.
                    JSONObject area1Object = (JSONObject) dataObject.get("area1");
                    JSONObject area2Object = (JSONObject) dataObject.get("area2");
                    JSONObject area3Object = (JSONObject) dataObject.get("area3");
                    JSONObject area4Object = (JSONObject) dataObject.get("area4");

                    // 각각 불러온 객체에서 원하는 name 값을 가져오면 끝( area1, area2, area3, area4 는 final 전역변수로 지정
                    area1 = area1Object.getString("name");
                    area2 = area2Object.getString("name");
                    area3 = area3Object.getString("name");
                    area4 = area4Object.getString("name");

                    // 이제 추출한 데이터를 가지고 Ui 변경하기 위해 handler 사용
                    Message msg = handler.obtainMessage();
                    handler.sendMessage(msg);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // 주소 받아오면 처리 메시지
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            txt_location.setText(area1 + " " + area2 + " " + area3 + " " + area4);
            try {
                findWeather(koreanAddressToEng(area1, area2));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    // 영어로 주소 변환
    private String koreanAddressToEng(String mainCity, String koreanAddress) {

        String engAddress = null;

        if (mainCity.contains("인천")) {
            engAddress = "Incheon";
        }else if (mainCity.contains("울산")) {
            engAddress = "Ulsan";
        }else if (mainCity.contains("대전")) {
            engAddress = "Daejeon";
        }else if (mainCity.contains("광주")) {
            engAddress = "Gwangju";
        }else if (mainCity.contains("대구")) {
            engAddress = "Daegu";
        }else if (mainCity.contains("제주")) {
            engAddress = "Jeju";
        }else {
            engAddress = koreanAddress.replace("원주시", "Wonju-si")
                    .replace("강릉시", "Gangneung-si")    // 강원도
                    .replace("동해시", "Donghae-si")
                    .replace("속초시", "Sokcho-si")
                    .replace("춘천시", "Chuncheon-si")
                    .replace("태백시", "Taebaek-si")
                    .replace("고성군", "Goseong-gun")
                    .replace("양구군", "Yanggu-gun")
                    .replace("양양군", "Yangyang-gun")
                    .replace("영월군", "Yeongwol-gun")
                    .replace("인제군", "Inje-gun")
                    .replace("정선군", "Jeongseon-gun")
                    .replace("철원군", "Cheorwon-gun")
                    .replace("평창군", "Pyeongchang-gun")
                    .replace("홍천군", "Hongcheon-gun")
                    .replace("화천군", "Hwacheon-gun")
                    .replace("횡성군", "Hoengseong-gun")
                    // 서울
                    .replace("강남구", "Gangnam-gu")
                    .replace("강동구", "Gangdong-gu")
                    .replace("강북구", "Gangbuk-gu")
                    .replace("강서구", "Gangseo-gu")
                    .replace("관악구", "Gwanak-gu")
                    .replace("광진구", "Gwangjin-gu")
                    .replace("구로구", "Guro-gu")
                    .replace("금천구", "Geumcheon-gu")
                    .replace("도봉구", "Dobong-gu")
                    .replace("동대문구", "Dongdaemun-gu")
                    .replace("동작구", "Dongjak-gu")
                    .replace("마포구", "Mapo-gu")
                    .replace("서대문구", "Seodaemun-gu")
                    .replace("서초구", "Seocho-gu")
                    .replace("성동구", "Seongdong-gu")
                    .replace("성북구", "Seongbuk-gu")
                    .replace("송파구", "Songpa-gu")
                    .replace("양천구", "Yangcheon-gu")
                    .replace("영등포구", "Yeongdeungpo-gu")
                    .replace("용산구", "Yongsan-gu")
                    .replace("은평구", "Eunpyeong-gu")
                    .replace("종로구", "Jongno-gu")
                    .replace("중구", "Jung-gu")
                    .replace("중랑구", "Jungnang-gu")
                    // 부산
                    .replace("강서구", "Gangseo-gu")
                    .replace("금정구", "Gumjung-gu")
                    .replace("남구", "Nam-gu")
                    .replace("동구", "Dong-gu")
                    .replace("동래구", "Dongnae-gu")
                    .replace("부산진구", "Busanjin-gu")
                    .replace("북구", "Buk-gu")
                    .replace("사상구", "Sasang-gu")
                    .replace("사하구", "Saha-gu")
                    .replace("서구", "Seo-gu")
                    .replace("수영구", "Suyeong-gu")
                    .replace("연제구", "Yeonje-gu")
                    .replace("영도구", "Yeongdo-gu")
                    .replace("중구", "Jung-gu")
                    .replace("해운대구", "Haeundae-gu")
                    .replace("기장군", "Gijang-gun")
                    // 경기도
                    .replace("고양시", "Goyang-si")
                    .replace("덕양구", "Deogyang-gu")
                    .replace("일산구", "Ilsan-gu")
                    .replace("과천시", "Gwacheon-si")
                    .replace("광명시", "Gwangmyeong-si")
                    .replace("구리시", "Guri-si")
                    .replace("기장군", "Gunpo-si")
                    .replace("김포시", "Gimpo-si")
                    .replace("남양주시", "Namyangju-si")
                    .replace("동두천시", "Dongducheon-si")
                    .replace("부천시", "Bucheon-si")
                    .replace("소사구", "Sosa-gu")
                    .replace("오정구", "Ojeong-gu")
                    .replace("원미구", "Wonmi-gu")
                    .replace("성남시", "Seongnam-si")
                    .replace("분당구", "Bundang-gu")
                    .replace("수정구", "Sujeong-gu")
                    .replace("중원구", "Jungwon-gu")
                    .replace("수원시", "Suwon-si")
                    .replace("권선구", "Gwonseon-gu")
                    .replace("장안구", "Jangan-gu")
                    .replace("팔달구", "Paldal-gu")
                    .replace("시흥시", "Siheung-si")
                    .replace("안산시", "Ansan-si")
                    .replace("안성시", "Anseong-si")
                    .replace("안양시", "Anyang-si")
                    .replace("동안구", "Dongan-gu")
                    .replace("만안구", "Manan-gu")
                    .replace("오산시", "Osan-si")
                    .replace("용인시", "Yongin-si")
                    .replace("의왕시", "Uiwang-si")
                    .replace("의정부시", "Uijeongbu-si")
                    .replace("이천시", "Icheon-si")
                    .replace("파주시", "Paju-si")
                    .replace("평택시", "Pyeongtaek-si")
                    .replace("하남시", "Hanam-si")
                    .replace("가평군", "Gapyeong-gun")
                    .replace("광주군", "Gwangju-gun")
                    .replace("양주군", "Yangju-gun")
                    .replace("양평군", "Yangpyeong-gun")
                    .replace("여주군", "Yeoju-gun")
                    .replace("연천군", "Yeoncheon-gun")
                    .replace("포천군", "Pocheon-gun")
                    .replace("화성군", "Hwaseong-gun");
        }
        return engAddress;
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