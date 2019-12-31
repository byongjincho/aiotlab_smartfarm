package kr.co.aiotlab.www;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class EnterIPandPort extends AppCompatActivity {

    private EditText main_edt_ip, main_edt_port;
    private Button main_checkButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_ip);

        main_edt_ip = findViewById(R.id.main_edt_ip);
        main_edt_port = findViewById(R.id.main_edt_port);
        main_checkButton = findViewById(R.id.main_checkButton);

        //버튼 동작(Main Activity로 이동)
        main_checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int status = NetWorkStatus.getConnectivityStatus(getApplicationContext());

                if (status != NetWorkStatus.TYPE_NOT_CONNECTED) {

                    if (main_edt_port.getText().toString().equals("")) {
                        Toast.makeText(getApplicationContext(), "포트 번호를 입력해주세요", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(EnterIPandPort.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }else {
                    Snackbar.make(v, "인터넷 연결을 확인해주세요", Snackbar.LENGTH_SHORT).show();
                    Toast toast = Toast.makeText(getApplicationContext(), "인테넷 연결 안됨", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP|Gravity.LEFT,100,0);
                    toast.show();
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences ip = getSharedPreferences("IP_files", MODE_PRIVATE);
        SharedPreferences port = getSharedPreferences("PORT_files", MODE_PRIVATE);

        main_edt_ip.setText(ip.getString("IP", "0"));
        main_edt_port.setText(port.getString("PORT", "0"));

    }

    @Override
    protected void onStop() {
        super.onStop();

        //생명주기가 stop일 때 sharedpreference에 ip와 port값 저장
        SharedPreferences ip_address = getSharedPreferences("IP_files", MODE_PRIVATE);
        SharedPreferences port_number = getSharedPreferences("PORT_files", MODE_PRIVATE);

        SharedPreferences.Editor editor1 = ip_address.edit();
        SharedPreferences.Editor editor2 = port_number.edit();

        editor1.putString("IP", main_edt_ip.getText().toString());
        editor2.putString("PORT", main_edt_port.getText().toString());

        editor1.commit();
        editor2.commit();
    }
}
