package kr.co.aiotlab.www;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

//소켓 통신을 하기 위한 모듈
class SocketProtocol extends AsyncTask<String, Void, Void> {
    private Exception exception;
    private String IP_ADRESS;
    private int PORT;


    @Override
    protected Void doInBackground(String... strings) {
        IP_ADRESS = SensorControlActivity.txt_ip.getText().toString();
        PORT = Integer.parseInt(SensorControlActivity.txt_port.getText().toString());
        Log.d("!!!", "doInBackground: " + IP_ADRESS + PORT);
        try {
            try {
                Socket socket = new Socket(IP_ADRESS, PORT);
                PrintWriter printWriter = new PrintWriter(
                        new OutputStreamWriter(
                                socket.getOutputStream()));
                printWriter.print(strings[0]);
                printWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }catch (Exception e){
            this.exception = e;
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);

    }
}
