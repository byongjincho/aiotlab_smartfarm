package kr.co.aiotlab.www;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import static kr.co.aiotlab.www.MainActivity.dust;
import static kr.co.aiotlab.www.MainActivity.fire_state;
import static kr.co.aiotlab.www.MainActivity.humi;
import static kr.co.aiotlab.www.MainActivity.temp;
import static kr.co.aiotlab.www.Main_UI.BottomFirstFragment.getFire;
import static kr.co.aiotlab.www.Main_UI.BottomFirstFragment.getHumidityText;
import static kr.co.aiotlab.www.Main_UI.BottomFirstFragment.getTempText;
import static kr.co.aiotlab.www.Main_UI.BottomFirstFragment.txt_insidedust;


public class JsonParse {
    private static final String TAG = "Json Parse Class";

    private String data;
    private JSONObject jsonObject;
    private Context context;

    public JsonParse(Context context, String data) {
        this.data = data;
        this.context = context;
    }

    public void parseData() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (data.contains("fire")) {
                    try {
                        jsonObject = new JSONObject(data);
                        String fire = jsonObject.getString("fire");

                        if (fire.equals("1")) {
                            getFire.setText("안전");
                            getFire.setTextColor(Color.parseColor("#28BBED"));
                            if (fire_state.equals("ON")) {
                                new SocketProtocol(context).execute("6");
                                fire_state = "OFF";
                                Log.d(TAG, "@@@@: " + "안전");
                            }

                        } else {
                            getFire.setText("불꽃 감지");
                            getFire.setTextColor(Color.RED);
                            if (fire_state.equals("OFF")) {
                                new SocketProtocol(context).execute("7");
                                fire_state = "ON";
                                Log.d(TAG, "@@@@: " + "위험");
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    jsonObject = new JSONObject(data);
                    temp = jsonObject.getString("temperature");
                    int windchilltemp = jsonObject.getInt("windchill");
                    humi = jsonObject.getString("humidity");
                    dust = jsonObject.getInt("dust");

                    txt_insidedust.setText(String.valueOf(dust));
                    getTempText.setText(temp);
                    getHumidityText.setText(humi);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
