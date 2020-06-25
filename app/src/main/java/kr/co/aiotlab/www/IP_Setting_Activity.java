package kr.co.aiotlab.www;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class IP_Setting_Activity extends AppCompatActivity {

    private EditText edt_cctv_ip;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_ipsetting);

        edt_cctv_ip = findViewById(R.id.edt_cctv_ip);

        SharedPreferences cctv_ip = getSharedPreferences("CCTV", MODE_PRIVATE);

        edt_cctv_ip.setText(cctv_ip.getString("CCTVIP", "0"));
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences cctv_ip = getSharedPreferences("CCTV", MODE_PRIVATE);

        SharedPreferences.Editor cctv_editor = cctv_ip.edit();

        cctv_editor.putString("CCTVIP", edt_cctv_ip.getText().toString());

        cctv_editor.commit();

    }
}
