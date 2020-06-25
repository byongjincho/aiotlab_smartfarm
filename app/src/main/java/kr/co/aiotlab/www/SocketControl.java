package kr.co.aiotlab.www;

import android.content.Context;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import static kr.co.aiotlab.www.SensorControlActivity.txt_ip;

public class SocketControl {

    private Switch controlSwitch;
    private Context context;
    private String on;
    private String off;

    public SocketControl(Switch controlSwitch, Context context) {
        this.controlSwitch = controlSwitch;
        this.context = context;
    }

    public void setOnOffCommand(String on, String off) {
        this.on = on;
        this.off = off;
    }

    public void commitSocket() {
        controlSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (controlSwitch.isChecked()) {
                    if (txt_ip.getText().toString().equals("")) {
                        Toast.makeText(context, "IP주소를 입력해주세요", Toast.LENGTH_SHORT).show();
                    } else {
                        new SocketProtocol(context).execute(on);
                    }
                } else if (!controlSwitch.isChecked()) {
                    if (txt_ip.getText().toString().equals("")) {
                        Toast.makeText(context, "IP주소를 입력해주세요", Toast.LENGTH_SHORT).show();
                    } else {
                        new SocketProtocol(context).execute(off);
                    }
                }
            }
        });
    }
}
