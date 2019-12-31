package kr.co.aiotlab.www;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

public class IP_Setting_Activity extends AppCompatActivity {

    private EditText edt_sensor_ip, edt_sensor_port, edt_cctv_ip;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_ipsetting);

        edt_sensor_ip = findViewById(R.id.edt_sensor_ip);
        edt_sensor_port = findViewById(R.id.edt_sensor_port);
        edt_cctv_ip = findViewById(R.id.edt_cctv_ip);

        SharedPreferences ip = getSharedPreferences("IP_files", MODE_PRIVATE);
        SharedPreferences port = getSharedPreferences("PORT_files", MODE_PRIVATE);
        SharedPreferences cctv_ip = getSharedPreferences("CCTV", MODE_PRIVATE);

        edt_sensor_ip.setText(ip.getString("IP", "0"));
        edt_sensor_port.setText(port.getString("PORT", "0"));


        edt_cctv_ip.setText(cctv_ip.getString("CCTVIP", "0"));
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences ip = getSharedPreferences("IP_files", MODE_PRIVATE);
        SharedPreferences port = getSharedPreferences("PORT_files", MODE_PRIVATE);
        SharedPreferences cctv_ip = getSharedPreferences("CCTV", MODE_PRIVATE);

        SharedPreferences.Editor sensor_ip_editor = ip.edit();
        SharedPreferences.Editor sensor_port_editor = port.edit();
        SharedPreferences.Editor cctv_editor = cctv_ip.edit();

        sensor_ip_editor.putString("IP", edt_sensor_ip.getText().toString());
        sensor_port_editor.putString("PORT", edt_sensor_port.getText().toString());
        cctv_editor.putString("CCTVIP", edt_cctv_ip.getText().toString());

        sensor_ip_editor.commit();
        sensor_port_editor.commit();
        cctv_editor.commit();

    }
}
