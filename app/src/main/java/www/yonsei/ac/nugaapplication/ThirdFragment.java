package www.yonsei.ac.nugaapplication;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import www.nugamedical.com.R;

public class ThirdFragment extends Fragment {

    private SwipeRefreshLayout swipe_frag3;


    public static ThirdFragment newInstance(){
        ThirdFragment f = new ThirdFragment();
        return f;
    }

    View view;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag3, container, false);

        swipe_frag3 = view.findViewById(R.id.swipe_frag3);
        //새로고침
        swipe_frag3.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                showFragment(ThirdFragment.newInstance());

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipe_frag3.setRefreshing(false);
                    }
                },3000);
            }
        });


        return view;
    }

    public void showFragment(Fragment f){
        getFragmentManager().beginTransaction().replace(R.id.frame_main, f).commit();
    }

}
