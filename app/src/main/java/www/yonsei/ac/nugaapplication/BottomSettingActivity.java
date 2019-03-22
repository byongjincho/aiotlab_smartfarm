package www.yonsei.ac.nugaapplication;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.widget.CheckBox;

import www.nugamedical.com.R;

public class BottomSettingActivity extends PreferenceActivity {
    SwitchPreference switchPreference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefs);

        switchPreference = (SwitchPreference) findPreference("autoSwitch");
        switchPreference.setSummaryOff("상태 : 수동");
        switchPreference.setSummaryOn("상태 : 자동");

        //설정 하면 설정값 summary 보여주기
        bindSummaryValue(findPreference("list1"));
        bindSummaryValue(findPreference("list2"));
        bindSummaryValue(findPreference("list3"));
        bindSummaryValue(findPreference("list4"));
        bindSummaryValue(findPreference("list5"));

    }

    /** 설정 하면 설정값 summary 보여주기 */
    private static void bindSummaryValue(Preference preference){
        preference.setOnPreferenceChangeListener(listener);
        listener.onPreferenceChange(preference,
                PreferenceManager.getDefaultSharedPreferences(preference.getContext())
                .getString(preference.getKey(),""));
    }
    //설정 하면 설정값 summary 보여주기 listener
    private static Preference.OnPreferenceChangeListener listener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String stringValue = newValue.toString();
            // List Preference에 대한 설정
            if(preference instanceof ListPreference){
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);
                //set summary to reflect new value
                preference.setSummary(index > 0
                        ? listPreference.getEntries()[index]
                        :null);
            }
            return true;
        }
    };
}
