package www.yonsei.ac.nugaapplication;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import www.nugamedical.com.R;

public class SensorControlActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private Button send_message, checkButton;
    private EditText edt;
    static EditText edt_port;
    static EditText edt_ip;
    private Switch switch1, switch2, switch3, switch4;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_sensorcontrol);

        send_message = findViewById(R.id.send_message);
        checkButton = findViewById(R.id.checkButton);
        switch1 = findViewById(R.id.switch1);
        switch2 = findViewById(R.id.switch2);
        switch3 = findViewById(R.id.switch3);
        switch4 = findViewById(R.id.switch4);
        edt = findViewById(R.id.edt);
        edt_ip = findViewById(R.id.edt_ip);
        edt_port = findViewById(R.id.edt_port);


        send_message.setOnClickListener(this);

        //switch1 이 ON됐을 때와 OFF됐을 때 동작
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (switch1.isChecked()){
                    new SocketProtocol().execute("1");
                    Toast.makeText(SensorControlActivity.this, "switch1 on", Toast.LENGTH_LONG).show();
                }else if (!switch1.isChecked()){
                    new SocketProtocol().execute("0");
                    Toast.makeText(SensorControlActivity.this, "switch1 off", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //switch2 이 ON됐을 때와 OFF됐을 때 동작
        switch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                 if (switch2.isChecked()){
                    new SocketProtocol().execute("3");
                     Toast.makeText(SensorControlActivity.this, "switch2 on", Toast.LENGTH_SHORT).show();
                }else if (!switch2.isChecked()){
                    new SocketProtocol().execute("2");
                    Toast.makeText(SensorControlActivity.this, "switch2 off", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //switch3 이 ON됐을 때와 OFF됐을 때 동작
        switch3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                 if (switch3.isChecked()){
                    new SocketProtocol().execute("5");
                    Toast.makeText(SensorControlActivity.this, "switch3 on", Toast.LENGTH_SHORT).show();
                }else if (!switch3.isChecked()){
                    new SocketProtocol().execute("4");
                    Toast.makeText(SensorControlActivity.this, "switch3 off", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //switch4 이 ON됐을 때와 OFF됐을 때 동작
        switch4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (switch4.isChecked()){
                    new SocketProtocol().execute("7");
                    Toast.makeText(SensorControlActivity.this, "switch4 on", Toast.LENGTH_SHORT).show();
                }else if (!switch4.isChecked()){
                    new SocketProtocol().execute("6");
                    Toast.makeText(SensorControlActivity.this, "switch4 off", Toast.LENGTH_SHORT).show();
                }
            }
        });


        checkButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        String text = edt.getText().toString();

        switch (v.getId()){
            case R.id.send_message:
                new SocketProtocol().execute(text);
                edt.getText().clear();
                break;
            case R.id.checkButton:
                Toast.makeText(this, "IP is : " + edt_ip.getText().toString() + "  and Port is : " + edt_port.getText().toString(), Toast.LENGTH_LONG ).show();
                break;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        //ip주소와 port를 입력하면 그 값저장된걸 불러오는 코드
        SharedPreferences ip = getSharedPreferences("IP_files", MODE_PRIVATE);
        SharedPreferences port = getSharedPreferences("PORT_files", MODE_PRIVATE);
        SharedPreferences switch1_state = getSharedPreferences("SWITCH1_files", MODE_PRIVATE);
        SharedPreferences switch2_state = getSharedPreferences("SWITCH2_files", MODE_PRIVATE);
        SharedPreferences switch3_state = getSharedPreferences("SWITCH3_files", MODE_PRIVATE);
        SharedPreferences switch4_state = getSharedPreferences("SWITCH4_files", MODE_PRIVATE);


        //상태 저장
        edt_ip.setText(ip.getString("IP",""));
        edt_port.setText(port.getString("PORT",""));
        switch1.setChecked(switch1_state.getBoolean("switch1", false));
        switch2.setChecked(switch2_state.getBoolean("switch2", false));
        switch3.setChecked(switch3_state.getBoolean("switch3", false));
        switch4.setChecked(switch4_state.getBoolean("switch4", false));

    }

    @Override
    protected void onStop() {
        super.onStop();

        //생명주기가 stop일 때 sharedpreference에 ip와 port값 저장
        SharedPreferences ip_address = getSharedPreferences("IP_files", MODE_PRIVATE);
        SharedPreferences port_number = getSharedPreferences("PORT_files", MODE_PRIVATE);
        SharedPreferences switch1_state = getSharedPreferences("SWITCH1_files", MODE_PRIVATE);
        SharedPreferences switch2_state = getSharedPreferences("SWITCH2_files", MODE_PRIVATE);
        SharedPreferences switch3_state = getSharedPreferences("SWITCH3_files", MODE_PRIVATE);
        SharedPreferences switch4_state = getSharedPreferences("SWITCH4_files", MODE_PRIVATE);


        SharedPreferences.Editor editor1 = ip_address.edit();
        SharedPreferences.Editor editor2 = port_number.edit();
        SharedPreferences.Editor editor3 = switch1_state.edit();
        SharedPreferences.Editor editor4 = switch2_state.edit();
        SharedPreferences.Editor editor5 = switch3_state.edit();
        SharedPreferences.Editor editor6 = switch4_state.edit();


        editor1.putString("IP", edt_ip.getText().toString());
        editor2.putString("PORT", edt_port.getText().toString());
        editor3.putBoolean("switch1", switch1.isChecked());
        editor4.putBoolean("switch2", switch2.isChecked());
        editor5.putBoolean("switch3", switch3.isChecked());
        editor6.putBoolean("switch4", switch4.isChecked());


        editor1.commit();
        editor2.commit();
        editor3.commit();
        editor4.commit();
        editor5.commit();
        editor6.commit();

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    }


}
