package kr.co.aiotlab.www.Main_UI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import kr.co.aiotlab.www.R;


public class BottomThirdFragment extends Fragment {

    private Button btn_searchHTTP;
    private EditText edt_searchIP;
    private WebView webView;
    private String cctv_IP;

    public static BottomThirdFragment newInstance(){
        BottomThirdFragment f = new BottomThirdFragment();
        return f;
    }

    View view;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag3, container, false);

        btn_searchHTTP = view.findViewById(R.id.btn_searchHTTP);
        edt_searchIP = view.findViewById(R.id.edt_searchIP);
        webView = view.findViewById(R.id.webview);

        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("CCTV", Context.MODE_PRIVATE);
        String cctv_address = sharedPreferences.getString("CCTVIP", "");
        edt_searchIP.setText(cctv_address);

        cctv_IP = edt_searchIP.getText().toString();

        if (!edt_searchIP.getText().toString().equals("")){
            WebSettings webset = webView.getSettings();
            webset.setSupportZoom(true);
            webset.setJavaScriptEnabled(true);
            //webset.setCacheMode(WebSettings.LOAD_NO_CACHE);
            webView.setWebViewClient(new WebViewClient());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                webset.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            }

            webView.loadUrl("http://" + cctv_IP);
        }

        btn_searchHTTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                browserIntent.setData(Uri.parse("http://" + cctv_IP));
                startActivity(browserIntent);

            }
        });
        return view;
    }

    @Override
    public void onStop() {
        super.onStop();

        // Fragment3의 editText정보 저장
        SharedPreferences cctv_ip = this.getActivity().getSharedPreferences("CCTV", Context.MODE_PRIVATE);
        SharedPreferences.Editor cctv_editor = cctv_ip.edit();
        cctv_editor.putString("CCTVIP", edt_searchIP.getText().toString());
        cctv_editor.commit();

    }
}
