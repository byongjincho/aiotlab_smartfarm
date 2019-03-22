package www.yonsei.ac.nugaapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import www.nugamedical.com.R;

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

                Intent intent = new Intent(EnterIPandPort.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences ip = getSharedPreferences("IP_files", MODE_PRIVATE);
        SharedPreferences port = getSharedPreferences("PORT_files", MODE_PRIVATE);

        main_edt_ip.setText(ip.getString("IP", ""));
        main_edt_port.setText(port.getString("PORT", ""));

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
        editor2.putString("PORT", main_edt_port .getText().toString());

        editor1.commit();
        editor2.commit();
    }
}
