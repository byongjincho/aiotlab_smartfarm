package kr.co.aiotlab.www;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import static kr.co.aiotlab.www.SwitchNameSetting.remember_state_name;


public class SensorControlActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private Button checkButton;
    public static TextView txt_port;
    public static TextView txt_ip;
    private Switch led_switch1, led_switch2, led_switch3, led_switch4;
    private Switch siren_switch1, siren_switch2;
    private Switch power_switch1, power_switch2, power_switch3, power_switch4, power_switch5, power_switch6, power_switch7;
    private TextView txt_led_switch1, txt_led_switch2, txt_led_switch3, txt_led_switch4, txt_siren_switch1, txt_siren_switch2,
            txt_power_switch1, txt_power_switch2, txt_power_switch3, txt_power_switch4, txt_power_switch5, txt_power_switch6, txt_power_switch7;
    private WebView webView_sensor;
    private String cctv_IP;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_sensorcontrol);

        SharedPreferences sharedPreferences = getSharedPreferences("CCTV", Context.MODE_PRIVATE);
        cctv_IP = sharedPreferences.getString("CCTVIP", "0");

        webView_sensor = findViewById(R.id.webview_sensor);
        WebSettings set = webView_sensor.getSettings();
        set.setJavaScriptEnabled(true);
        webView_sensor.loadUrl("http://" + cctv_IP);


        led_switch1 = findViewById(R.id.LEDswitch1);
        led_switch2 = findViewById(R.id.LEDswitch2);
        led_switch3 = findViewById(R.id.LEDswitch3);
        led_switch4 = findViewById(R.id.LEDswitch4);
        siren_switch1 = findViewById(R.id.Sirenswitch1);
        siren_switch2 = findViewById(R.id.Sirenswitch2);
        power_switch1 = findViewById(R.id.power_switch1);
        power_switch2 = findViewById(R.id.power_switch2);
        power_switch3 = findViewById(R.id.power_switch3);
        power_switch4 = findViewById(R.id.power_switch4);
        power_switch5 = findViewById(R.id.power_switch5);
        power_switch6 = findViewById(R.id.power_switch6);
        power_switch7 = findViewById(R.id.power_switch7);

        txt_led_switch1 = findViewById(R.id.txt_ledswitch1);
        txt_led_switch2 = findViewById(R.id.txt_ledswitch2);
        txt_led_switch3 = findViewById(R.id.txt_ledswitch3);
        txt_led_switch4 = findViewById(R.id.txt_ledswitch4);
        txt_siren_switch1 = findViewById(R.id.txt_Sirenswitch1);
        txt_siren_switch2 = findViewById(R.id.txt_Sirenswitch2);
        txt_power_switch1 = findViewById(R.id.txt_power_switch1);
        txt_power_switch2 = findViewById(R.id.txt_power_switch2);
        txt_power_switch3 = findViewById(R.id.txt_power_switch3);
        txt_power_switch4 = findViewById(R.id.txt_power_switch4);
        txt_power_switch5 = findViewById(R.id.txt_power_switch5);
        txt_power_switch6 = findViewById(R.id.txt_power_switch6);
        txt_power_switch7 = findViewById(R.id.txt_power_switch7);

        txt_ip = findViewById(R.id.txt_ip);
        txt_port = findViewById(R.id.txt_port);

        SharedPreferences ip = getSharedPreferences("IP_files", MODE_PRIVATE);
        SharedPreferences port = getSharedPreferences("PORT_files", MODE_PRIVATE);

        txt_ip.setText(ip.getString("IP", "0"));
        txt_port.setText(port.getString("PORT", "0"));

        // 각 스위치 이름 세팅
        txt_led_switch1.setOnClickListener(this);
        txt_led_switch2.setOnClickListener(this);
        txt_led_switch3.setOnClickListener(this);
        txt_led_switch4.setOnClickListener(this);
        txt_siren_switch1.setOnClickListener(this);
        txt_siren_switch2.setOnClickListener(this);
        txt_power_switch1.setOnClickListener(this);
        txt_power_switch2.setOnClickListener(this);
        txt_power_switch3.setOnClickListener(this);
        txt_power_switch4.setOnClickListener(this);
        txt_power_switch5.setOnClickListener(this);
        txt_power_switch6.setOnClickListener(this);
        txt_power_switch7.setOnClickListener(this);

        SocketControl sc_led1 = new SocketControl(led_switch1, getApplicationContext());
        sc_led1.setOnOffCommand("1", "0");
        sc_led1.commitSocket();

        SocketControl sc_led2 = new SocketControl(led_switch2, getApplicationContext());
        sc_led2.setOnOffCommand("5", "4");
        sc_led2.commitSocket();

        SocketControl sc_led3 = new SocketControl(led_switch3, getApplicationContext());
        sc_led3.setOnOffCommand("", "");
        sc_led3.commitSocket();

        SocketControl sc_led4 = new SocketControl(led_switch4, getApplicationContext());
        sc_led4.setOnOffCommand("", "");
        sc_led4.commitSocket();

        SocketControl sc_siren1 = new SocketControl(siren_switch1, getApplicationContext());
        sc_siren1.setOnOffCommand("7", "6");
        sc_siren1.commitSocket();

        SocketControl sc_siren2 = new SocketControl(siren_switch2, getApplicationContext());
        sc_siren2.setOnOffCommand("3", "2");
        sc_siren2.commitSocket();

        SocketControl sc_power1 = new SocketControl(power_switch1, getApplicationContext());
        sc_power1.setOnOffCommand("11", "00");
        sc_power1.commitSocket();

        SocketControl sc_power2 = new SocketControl(power_switch2, getApplicationContext());
        sc_power2.setOnOffCommand("AA", "aa");
        sc_power2.commitSocket();

        SocketControl sc_power3 = new SocketControl(power_switch3, getApplicationContext());
        sc_power3.setOnOffCommand("BB", "bb");
        sc_power3.commitSocket();

        SocketControl sc_power4 = new SocketControl(power_switch4, getApplicationContext());
        sc_power4.setOnOffCommand("CC", "cc");
        sc_power4.commitSocket();

        SocketControl sc_power5 = new SocketControl(power_switch5, getApplicationContext());
        sc_power5.setOnOffCommand("DD", "dd");
        sc_power5.commitSocket();

        SocketControl sc_power6 = new SocketControl(power_switch6, getApplicationContext());
        sc_power6.setOnOffCommand("EE", "ee");
        sc_power6.commitSocket();

        SocketControl sc_power7 = new SocketControl(power_switch7, getApplicationContext());
        sc_power7.setOnOffCommand("FF", "ff");
        sc_power7.commitSocket();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_ledswitch1:
                remember_state_name = "led1";
                Intent led1 = new Intent(getApplicationContext(), SwitchNameSetting.class);
                startActivity(led1);
                break;
            case R.id.txt_ledswitch2:
                remember_state_name = "led2";
                Intent led2 = new Intent(getApplicationContext(), SwitchNameSetting.class);
                startActivity(led2);
                break;
            case R.id.txt_ledswitch3:
                remember_state_name = "led3";
                Intent led3 = new Intent(getApplicationContext(), SwitchNameSetting.class);
                startActivity(led3);
                break;
            case R.id.txt_ledswitch4:
                remember_state_name = "led4";
                Intent led4 = new Intent(getApplicationContext(), SwitchNameSetting.class);
                startActivity(led4);
                break;
            case R.id.txt_Sirenswitch1:
                remember_state_name = "siren1";
                Intent siren1 = new Intent(getApplicationContext(), SwitchNameSetting.class);
                startActivity(siren1);
                break;
            case R.id.txt_Sirenswitch2:
                remember_state_name = "siren2";
                Intent siren2 = new Intent(getApplicationContext(), SwitchNameSetting.class);
                startActivity(siren2);
                break;
            case R.id.txt_power_switch1:
                remember_state_name = "power1";
                Intent power1 = new Intent(getApplicationContext(), SwitchNameSetting.class);
                startActivity(power1);
                break;
            case R.id.txt_power_switch2:
                remember_state_name = "power2";
                Intent power2 = new Intent(getApplicationContext(), SwitchNameSetting.class);
                startActivity(power2);
                break;
            case R.id.txt_power_switch3:
                remember_state_name = "power3";
                Intent power3 = new Intent(getApplicationContext(), SwitchNameSetting.class);
                startActivity(power3);
                break;
            case R.id.txt_power_switch4:
                remember_state_name = "power4";
                Intent power4 = new Intent(getApplicationContext(), SwitchNameSetting.class);
                startActivity(power4);
                break;
            case R.id.txt_power_switch5:
                remember_state_name = "power5";
                Intent power5 = new Intent(getApplicationContext(), SwitchNameSetting.class);
                startActivity(power5);
                break;
            case R.id.txt_power_switch6:
                remember_state_name = "power6";
                Intent power6 = new Intent(getApplicationContext(), SwitchNameSetting.class);
                startActivity(power6);
                break;
            case R.id.txt_power_switch7:
                remember_state_name = "power7";
                Intent power7 = new Intent(getApplicationContext(), SwitchNameSetting.class);
                startActivity(power7);
                break;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        //ip주소와 port를 입력하면 그 값저장된걸 불러오는 코드
        SharedPreferences ip = getSharedPreferences("IP_files", MODE_PRIVATE);
        SharedPreferences port = getSharedPreferences("PORT_files", MODE_PRIVATE);
        SharedPreferences led_switch1_state = getSharedPreferences("LED_SWITCH1_files", MODE_PRIVATE);
        SharedPreferences led_switch2_state = getSharedPreferences("LED_SWITCH2_files", MODE_PRIVATE);
        SharedPreferences led_switch3_state = getSharedPreferences("LED_SWITCH3_files", MODE_PRIVATE);
        SharedPreferences led_switch4_state = getSharedPreferences("LED_SWITCH4_files", MODE_PRIVATE);

        SharedPreferences siren_switch1_state = getSharedPreferences("SIREN_SWITCH1_files", MODE_PRIVATE);
        SharedPreferences siren_switch2_state = getSharedPreferences("SIREN_SWITCH2_files", MODE_PRIVATE);

        SharedPreferences power_switch1_state = getSharedPreferences("POWER_SWITCH1_files", MODE_PRIVATE);
        SharedPreferences power_switch2_state = getSharedPreferences("POWER_SWITCH2_files", MODE_PRIVATE);
        SharedPreferences power_switch3_state = getSharedPreferences("POWER_SWITCH3_files", MODE_PRIVATE);
        SharedPreferences power_switch4_state = getSharedPreferences("POWER_SWITCH4_files", MODE_PRIVATE);
        SharedPreferences power_switch5_state = getSharedPreferences("POWER_SWITCH5_files", MODE_PRIVATE);
        SharedPreferences power_switch6_state = getSharedPreferences("POWER_SWITCH6_files", MODE_PRIVATE);
        SharedPreferences power_switch7_state = getSharedPreferences("POWER_SWITCH7_files", MODE_PRIVATE);

        //상태 저장
        txt_ip.setText(ip.getString("IP", ""));
        txt_port.setText(port.getString("PORT", ""));
        led_switch1.setChecked(led_switch1_state.getBoolean("led_switch1", false));
        led_switch2.setChecked(led_switch2_state.getBoolean("led_switch2", false));
        led_switch3.setChecked(led_switch3_state.getBoolean("led_switch3", false));
        led_switch4.setChecked(led_switch4_state.getBoolean("led_switch4", false));

        siren_switch1.setChecked(siren_switch1_state.getBoolean("siren_switch1", false));
        siren_switch2.setChecked(siren_switch2_state.getBoolean("siren_switch2", false));

        power_switch1.setChecked(power_switch1_state.getBoolean("power_switch1", true));
        power_switch2.setChecked(power_switch2_state.getBoolean("power_switch2", true));
        power_switch3.setChecked(power_switch3_state.getBoolean("power_switch3", true));
        power_switch4.setChecked(power_switch4_state.getBoolean("power_switch4", true));
        power_switch5.setChecked(power_switch5_state.getBoolean("power_switch5", true));
        power_switch6.setChecked(power_switch6_state.getBoolean("power_switch6", true));
        power_switch7.setChecked(power_switch7_state.getBoolean("power_switch7", true));

        // 이름 저장하면 반영
        SharedPreferences sharedPreferences_led = getSharedPreferences("NAME", MODE_PRIVATE);
        String nameled1 = sharedPreferences_led.getString("led1", "name");
        String nameled2 = sharedPreferences_led.getString("led2", "name");
        String nameled3 = sharedPreferences_led.getString("led3", "name");
        String nameled4 = sharedPreferences_led.getString("led4", "name");
        txt_led_switch1.setText(nameled1);
        txt_led_switch2.setText(nameled2);
        txt_led_switch3.setText(nameled3);
        txt_led_switch4.setText(nameled4);

        SharedPreferences sharedPreferences_siren = getSharedPreferences("NAME", MODE_PRIVATE);
        String namesiren1 = sharedPreferences_siren.getString("siren1", "name");
        String namesiren2 = sharedPreferences_siren.getString("siren2", "name");
        txt_siren_switch1.setText(namesiren1);
        txt_siren_switch2.setText(namesiren2);

        SharedPreferences sharedPreferences_power = getSharedPreferences("NAME", MODE_PRIVATE);
        String namepower1 = sharedPreferences_power.getString("power1", "name");
        String namepower2 = sharedPreferences_power.getString("power2", "name");
        String namepower3 = sharedPreferences_power.getString("power3", "name");
        String namepower4 = sharedPreferences_power.getString("power4", "name");
        String namepower5 = sharedPreferences_power.getString("power5", "name");
        String namepower6 = sharedPreferences_power.getString("power6", "name");
        String namepower7 = sharedPreferences_power.getString("power7", "name");
        txt_power_switch1.setText(namepower1);
        txt_power_switch2.setText(namepower2);
        txt_power_switch3.setText(namepower3);
        txt_power_switch4.setText(namepower4);
        txt_power_switch5.setText(namepower5);
        txt_power_switch6.setText(namepower6);
        txt_power_switch7.setText(namepower7);


    }

    @Override
    protected void onStop() {
        super.onStop();

        //생명주기가 stop일 때 sharedpreference에 ip와 port값 저장
        SharedPreferences ip_address = getSharedPreferences("IP_files", MODE_PRIVATE);
        SharedPreferences port_number = getSharedPreferences("PORT_files", MODE_PRIVATE);
        SharedPreferences led_switch1_state = getSharedPreferences("LED_SWITCH1_files", MODE_PRIVATE);
        SharedPreferences led_switch2_state = getSharedPreferences("LED_SWITCH2_files", MODE_PRIVATE);
        SharedPreferences led_switch3_state = getSharedPreferences("LED_SWITCH3_files", MODE_PRIVATE);
        SharedPreferences led_switch4_state = getSharedPreferences("LED_SWITCH4_files", MODE_PRIVATE);

        SharedPreferences siren_switch1_state = getSharedPreferences("SIREN_SWITCH1_files", MODE_PRIVATE);
        SharedPreferences siren_switch2_state = getSharedPreferences("SIREN_SWITCH2_files", MODE_PRIVATE);

        SharedPreferences power_switch1_state = getSharedPreferences("POWER_SWITCH1_files", MODE_PRIVATE);
        SharedPreferences power_switch2_state = getSharedPreferences("POWER_SWITCH2_files", MODE_PRIVATE);
        SharedPreferences power_switch3_state = getSharedPreferences("POWER_SWITCH3_files", MODE_PRIVATE);
        SharedPreferences power_switch4_state = getSharedPreferences("POWER_SWITCH4_files", MODE_PRIVATE);
        SharedPreferences power_switch5_state = getSharedPreferences("POWER_SWITCH5_files", MODE_PRIVATE);
        SharedPreferences power_switch6_state = getSharedPreferences("POWER_SWITCH6_files", MODE_PRIVATE);
        SharedPreferences power_switch7_state = getSharedPreferences("POWER_SWITCH7_files", MODE_PRIVATE);

        //////////////////////////////////////////////////////////////////////////////////////////////////////////////

        SharedPreferences.Editor editor1 = ip_address.edit();
        SharedPreferences.Editor editor2 = port_number.edit();
        SharedPreferences.Editor editor3 = led_switch1_state.edit();
        SharedPreferences.Editor editor4 = led_switch2_state.edit();
        SharedPreferences.Editor editor5 = led_switch3_state.edit();
        SharedPreferences.Editor editor6 = led_switch4_state.edit();

        SharedPreferences.Editor editor7 = siren_switch1_state.edit();
        SharedPreferences.Editor editor8 = siren_switch2_state.edit();

        SharedPreferences.Editor editor9 = power_switch1_state.edit();
        SharedPreferences.Editor editor10 = power_switch2_state.edit();
        SharedPreferences.Editor editor11 = power_switch3_state.edit();
        SharedPreferences.Editor editor12 = power_switch4_state.edit();
        SharedPreferences.Editor editor13 = power_switch5_state.edit();
        SharedPreferences.Editor editor14 = power_switch6_state.edit();
        SharedPreferences.Editor editor15 = power_switch7_state.edit();

        //////////////////////////////////////////////////////////////////////////////////////////////////////////////

        editor1.putString("IP", txt_ip.getText().toString());
        editor2.putString("PORT", txt_port.getText().toString());
        editor3.putBoolean("led_switch1", led_switch1.isChecked());
        editor4.putBoolean("led_switch2", led_switch2.isChecked());
        editor5.putBoolean("led_switch3", led_switch3.isChecked());
        editor6.putBoolean("led_switch4", led_switch4.isChecked());

        editor7.putBoolean("siren_switch1", siren_switch1.isChecked());
        editor8.putBoolean("siren_switch2", siren_switch2.isChecked());

        editor9.putBoolean("power_switch1", power_switch1.isChecked());
        editor10.putBoolean("power_switch2", power_switch2.isChecked());
        editor11.putBoolean("power_switch3", power_switch3.isChecked());
        editor12.putBoolean("power_switch4", power_switch4.isChecked());
        editor13.putBoolean("power_switch5", power_switch5.isChecked());
        editor14.putBoolean("power_switch6", power_switch6.isChecked());
        editor15.putBoolean("power_switch7", power_switch7.isChecked());

        //////////////////////////////////////////////////////////////////////////////////////////////////////////////

        editor1.commit();
        editor2.commit();
        editor3.commit();
        editor4.commit();
        editor5.commit();
        editor6.commit();
        editor7.commit();
        editor8.commit();
        editor9.commit();
        editor10.commit();
        editor11.commit();
        editor12.commit();
        editor13.commit();
        editor14.commit();
        editor15.commit();


    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    }


}
