package ${YYAndroidPackageName};



import android.content.Intent;
import android.content.res.Configuration;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.app.Dialog;
import android.view.MotionEvent;

public class RunnerSocial implements ISocial
{
	
	public void Init( )
	{
		Log.i("yoyo","Base extension class init called");
	}
	public void LoadFriends()
	{
	}
	public void GetInfo(String id)
	{
	}
	public void Show(int type, String optarg, int numarg)
	{
	}
	public void LoadPic(String id)
	{
	}
	public void Event(String id)
	{
	}
	public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {};
	
	public void LoadLeaderboard(String id, int minindex,int maxindex,int filter)
	{
	}
	public void PostScore(String _leaderboard,int _score)
	{
	}
	public void onStart()
	{
	}
	public void onRestart()
	{
	}
	public void onStop()
	{
	}
	public void onDestroy()
	{
	}
	public void onPause()
	{
	}
	public void onResume()
	{
	}
	public void onConfigurationChanged(Configuration newConfig)
	{
	}
	public void Logout()
	{
	}
	public void Login()
	{
	}
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
	}
	
	public boolean onKeyLongPress(int keyCode, KeyEvent event)
	{
		return false;
	}
	public void onWindowFocusChanged(boolean hasFocus)
	{
	}
	
	public boolean onCreateOptionsMenu( Menu menu )
	{
		return false;
	}
	public boolean onOptionsItemSelected( MenuItem item )
	{
		return false;
	}
	
	public boolean onKeyDown( int keyCode, KeyEvent event )
	{
		return false;
	}
	public boolean onKeyUp( int keyCode, KeyEvent event )
	{
		return false;
	}
	
	public Dialog onCreateDialog(int id)
	{
		return null;
	}
	
	public boolean onTouchEvent(final MotionEvent event){ return false;};
	public boolean onGenericMotionEvent(MotionEvent event){ return false;};
	
	public boolean dispatchGenericMotionEvent(MotionEvent event)	{ return false;};
	public boolean dispatchKeyEvent(KeyEvent event)				{ return false;};
}



