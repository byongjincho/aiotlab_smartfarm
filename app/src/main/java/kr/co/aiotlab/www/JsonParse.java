package kr.co.aiotlab.www;

import android.os.Handler;
import android.os.Looper;

import org.json.JSONException;
import org.json.JSONObject;

import static kr.co.aiotlab.www.MainActivity.dust;
import static kr.co.aiotlab.www.MainActivity.humi;
import static kr.co.aiotlab.www.MainActivity.temp;
import static kr.co.aiotlab.www.Main_UI.BottomFirstFragment.getHumidityText;
import static kr.co.aiotlab.www.Main_UI.BottomFirstFragment.getTempText;
import static kr.co.aiotlab.www.Main_UI.BottomFirstFragment.txt_insidedust;

public class JsonParse {
    private String data;
    private JSONObject jsonObject;

    public JsonParse(String data) {
        this.data = data;
    }

    public void parseData() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
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
