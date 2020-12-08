package ${YYAndroidPackageName};

import android.os.Bundle;
import android.preference.PreferenceActivity;



public class RunnerPreferenceActivity extends PreferenceActivity {
	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		
		addPreferencesFromResource( R.xml.preferences );
	}
}