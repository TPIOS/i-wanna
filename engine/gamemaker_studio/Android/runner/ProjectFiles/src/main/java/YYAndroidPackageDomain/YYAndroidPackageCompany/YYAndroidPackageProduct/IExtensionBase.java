package ${YYAndroidPackageName};

import android.content.Intent;
import android.content.res.Configuration;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.app.Dialog;
import android.view.MotionEvent;

public interface IExtensionBase 
{
	//lifecycle methods
	void onStart();
	void onRestart();
	void onStop();
	void onDestroy();
	void onPause();
	void onResume();
	void onConfigurationChanged(Configuration newConfig);
	void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) ;
	void onActivityResult(int requestCode, int resultCode, Intent data);
	boolean onKeyLongPress(int keyCode, KeyEvent event);
	void onWindowFocusChanged(boolean hasFocus);
	
	boolean onCreateOptionsMenu( Menu menu );
	boolean onOptionsItemSelected( MenuItem item );
	
	boolean onKeyDown( int keyCode, KeyEvent event );
	boolean onKeyUp( int keyCode, KeyEvent event );
	
	Dialog onCreateDialog(int id);
	
	boolean onTouchEvent(final MotionEvent event);
	boolean onGenericMotionEvent(MotionEvent event);
	
	boolean dispatchGenericMotionEvent(MotionEvent event);
	boolean dispatchKeyEvent(KeyEvent event);
	
}