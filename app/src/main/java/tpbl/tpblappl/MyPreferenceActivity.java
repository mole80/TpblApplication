package tpbl.tpblappl;

import android.os.Bundle;

public class MyPreferenceActivity extends android.preference.PreferenceActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preference_screen);
    }
}
