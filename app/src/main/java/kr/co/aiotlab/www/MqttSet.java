package kr.co.aiotlab.www;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttSet {
    private static final String TAG = "MQTT Set Class";

    private String address;
    private String topic;
    private int qos = 1;
    private String clientId = MqttClient.generateClientId();
    private Context context;
    private String mqttData = null;


    public MqttSet(Context context, String address, String topic, int qos) {
        this.address = "tcp://" + address;
        this.context = context;
        this.topic = topic;
        this.qos = qos;
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

                                                switch (topic) {
                                                    case "Sensor/Fire_Motion":
                                                        Message msg_fire = fire_handler.obtainMessage();
                                                        fire_handler.sendMessage(msg_fire);
                                                        break;
                                                    case "Sensor/Dust_DHT22":
                                                        JsonParse jsonParse = new JsonParse(context, mqttData);
                                                        jsonParse.parseData();
                                                        break;
                                                }


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

    @SuppressLint("HandlerLeak")
    final Handler fire_handler = new Handler() {
        public void handleMessage(Message msg) {
            JsonParse jsonParse = new JsonParse(context, mqttData);
            jsonParse.parseData();
        }
    };
}
