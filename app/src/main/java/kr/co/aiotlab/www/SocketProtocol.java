package kr.co.aiotlab.www;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import static kr.co.aiotlab.www.SensorControlActivity.txt_ip;
import static kr.co.aiotlab.www.SensorControlActivity.txt_port;

//소켓 통신을 하기 위한 모듈
class SocketProtocol extends AsyncTask<String, Void, Void> {
    private Exception exception;
    private String IP_ADRESS;
    private int PORT;
    private Context context;

    public SocketProtocol(Context context) {
        this.context = context;
    }

    @SuppressLint("WrongThread")
    @Override
    protected Void doInBackground(String... strings) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        IP_ADRESS = sharedPreferences.getString("ip_address", "222.113.57.108");
        PORT = Integer.parseInt(sharedPreferences.getString("port", "4957"));
        Log.d("!!!", "doInBackground: " + IP_ADRESS + PORT);
        try {
            try {
                Socket socket = new Socket(IP_ADRESS, PORT);
                PrintWriter printWriter = new PrintWriter(
                        new OutputStreamWriter(
                                socket.getOutputStream()));
                printWriter.print(strings[0]);
                Log.d("!!!", "doInBackground: " + strings[0]);
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
