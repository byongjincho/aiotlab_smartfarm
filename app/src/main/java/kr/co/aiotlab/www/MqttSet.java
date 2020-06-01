package kr.co.aiotlab.www;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import static kr.co.aiotlab.www.Main_UI.BottomFirstFragment.getHumidityText;
import static kr.co.aiotlab.www.Main_UI.BottomFirstFragment.getTempText;
import static kr.co.aiotlab.www.Main_UI.BottomFirstFragment.txt_insidedust;

public class MqttSet {
    private static final String TAG = "MQTT Set Class";

    private String address;
    private String clientId = MqttClient.generateClientId();
    private Context context;
    private String mqttData = null;


    public MqttSet(Context context, String address) {
        this.address = "tcp://" + address;
        this.context = context;
    }

    public void getDataFromServer() {
        new Thread(new Runnable() {
            final MqttAndroidClient client =
                    new MqttAndroidClient(context, address,
                            clientId);
            @Override
            public void run() {
                try {
                    IMqttToken token = client.connect();
                    Log.d(TAG, "run: 1111" + token.getClient() + token.getTopics() + token.getException());
                    token.setActionCallback(new IMqttActionListener() {
                        @Override
                        public void onSuccess(IMqttToken asyncActionToken) {
                            // We are connected
                            Log.d(TAG, "onSuccess: ");

                            String topic = "Sensor/Dust_DHT22";

                            int qos = 1;
                            try {
                                IMqttToken subToken = client.subscribe(topic, qos);
                                subToken.setActionCallback(new IMqttActionListener() {
                                    @Override
                                    public void onSuccess(IMqttToken asyncActionToken) {
                                        // The message was published
                                        Log.d(TAG, "onSuccess: ");
                                        client.setCallback(new MqttCallback() {
                                            @Override
                                            public void connectionLost(Throwable cause) {

                                            }

                                            @Override
                                            public void messageArrived(String topic, MqttMessage message) throws Exception {
                                                //Json 파싱
                                                mqttData = new String(message.getPayload());

                                                JsonParse jsonParse = new JsonParse(mqttData);
                                                jsonParse.parseData();

                                                Log.d(TAG, "@@@@@@: " + mqttData);
                                            }

                                            @Override
                                            public void deliveryComplete(IMqttDeliveryToken token) {

                                            }
                                        });
                                    }

                                    @Override
                                    public void onFailure(IMqttToken asyncActionToken,
                                                          Throwable exception) {
                                        // The subscription could not be performed, maybe the user was not
                                        // authorized to subscribe on the specified topic e.g. using wildcards
                                        Log.d(TAG, "onFailure: ");
                                    }
                                });
                            } catch (MqttException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                            // Something went wrong e.g. connection timeout or firewall problems
                            Log.d(TAG, "onFailure: ");

                        }
                    });
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
