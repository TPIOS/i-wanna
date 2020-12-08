package com.yoyogames.runner;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.Thread;
import java.lang.UnsatisfiedLinkError;
import java.net.URLConnection;
import java.net.URL;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.UnknownServiceException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.util.concurrent.CountDownLatch;
import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;
import java.util.Enumeration;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.lang.Class;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Debug;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.view.View;
import android.view.LayoutInflater;
import android.view.Display;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.EditText;
import android.support.v4.content.ContextCompat;
import android.support.v4.app.ActivityCompat;
import android.graphics.Rect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;

//import com.openfeint.api.ui.Dashboard;
import ${YYAndroidPackageName}.RunnerActivity;

import ${YYAndroidPackageName}.RunnerKeyboardController;
import ${YYAndroidPackageName}.RunnerFacebook;
import ${YYAndroidPackageName}.DemoGLSurfaceView;
import ${YYAndroidPackageName}.DemoRenderer;
import ${YYAndroidPackageName}.Gamepad;

import ${YYAndroidPackageName}.R;
import ${YYAndroidPackageName}.ISocial;
import ${YYAndroidPackageName}.IAdExt;

// Wrapper for native library
public class RunnerJNILib {

	public static final int eOF_UserLoggedIn = 0;
	public static final int eOF_UserLoggedOut = 1;
	public static final int eOF_AchievementSendOK = 2;
	public static final int eOF_AchievementSendFail = 3;
	public static final int eOF_HighScoreSendOK = 4;
	public static final int eOF_HighScoreSendFail = 5;	 

    public static Context ms_context;
    public static MediaPlayer ms_mp;
    public static RunnerFacebook m_runnerFacebook;
    public static boolean ms_exitcalled = false;	
	public static String ms_versionName;
	public static boolean ms_loadLibraryFailed = false;


 	// ----------------------------------------------------------------------------------------------------
	// ----------------------------------------------------------------------------------------------------
   public static void Init() {
		try {
			System.loadLibrary("yoyo");
		} 
		catch( UnsatisfiedLinkError e) {
			ms_loadLibraryFailed = true;
			ms_exitcalled = true;
			/*
			*/
		} // end catch
        ms_mp = null;
    } // end Init
     
	// ----------------------------------------------------------------------------------------------------
	// ----------------------------------------------------------------------------------------------------
    public static void Init(Context _context) {
    	ms_context = _context;
    }
    
	// ----------------------------------------------------------------------------------------------------
	// ----------------------------------------------------------------------------------------------------
    // Exit the application in a thread safe way
    public static void ExitApplication() {
		if(!ms_exitcalled)
		{
		Log.i("yoyo", "First exit application called");
			ms_exitcalled = true;
    		RunnerActivity.ViewHandler.post( new Runnable() {
    			public void run() {
    				RunnerActivity.CurrentActivity.finish();
    			}
    		});
    	}
    }

	// ----------------------------------------------------------------------------------------------------
	// ----------------------------------------------------------------------------------------------------
    public static native void Startup(String _apkPath, String _saveFilesDir, String _packageName, int _sleepMargin);
    public static native int Process(int _width, int _height, float _accelX, float _accelY, float _accelZ, int _keypadStatus, int _orientation, float _refreshrate );
    public static native void TouchEvent( int _type, int _index, float _x, float _y);
    public static native void RenderSplash( String _apkPath, String _splashName, int  _screenWidth, int _screenHeight, int _texWidth, int _texHeight, int _pngWidth, int _pngHeight );
    public static native void Resume( int _param );
    public static native void Pause( int _param );
 //   public static native void AddString( String _string );
    public static native void OFNotify( int _enum, String _param1, String _param2, String _param3, String _param4 );
    public static native void BackKeyLongPressEvent( );
    public static native void KeyEvent( int _type, int _keycode, int _keychar, int _eventSource);
    public static native void SetKeyValue( int _type, int _val, String _valString );
    public static native String GetAppID( int _param );
	
    public static native String GetSaveFileName( String _fileName );
    public static native String[] ExpandCompressedFile( String _destLocalPath, String _compressedFileName );
    public static native void HttpResult( byte[] _resultString, int _httpStatus, int _id,String _finalURL, String _responseHeaders);
    public static native void HttpProgress( byte[] _resultString, int _httpStatus, int _id,String _finalURL, String _responseHeaders,int _contentLength);
    public static native void HttpResultString( String _resultString, int _httpStatus, int _id);
    public static native void InputResult( String _resultString, int _httpStatus, int _id);
    public static native void LoginResult( String _userName, String _password, int _id);
    public static native void CloudResultData( byte[] _resultString, int _status, int _id);

    public static native void CloudResultString( String _resultString, int _status, int _id);
    public static native void OnLoginSuccess(String _Name,String _uID,String _s,String _t,String _ts,String _tk,String _sS);
    public static native void onItemPurchase(String itemid,int numitems);
    
    
    public static native int  jCreateDsMap(String[] keys,String[] svals,double[] sdoublevals);
	public static native void DsMapAddDouble(int mapindex, String key, double val);
	public static native void DsMapAddString(int mapindex, String key, String val);
	public static native void CreateAsynEventWithDSMap(int mapindex, int eventindex);
	
	public static native void OnVirtualKeyboardStatus(String status, int height);
	public static native void OnVirtualKeyboardTextInserted(char[] text, int textOffset);
    
//    public static native void OnKakaoLoginSuccess(String _nickname,String _userid,String _message_blocked,String _tokenid,String _imageurl,int _kakaoStatus,String _statusmsg);
//    public static native void OnKakaoMessageResult(int _id, int _statusCode,String _msg);
   // public static native void OnFriendsLoaded(String[] names,String[] ids,String[] urls);
 //   public static native void OnKakaoFriendsLoaded(String[] friends,String[] appfriends);
    
    
 
    public static native void LoadPicForUserWithUrl(String _id, String _Url);

    
    public static native int CreateVersionDSMap(int sdkint,
			String RELEASE,
			String MODEL,
			String DEVICE,
			String MANUFACTURER,
			String CPU_ABI,
			String CPU_ABI2,
			String BOOTLOADER,
			String BOARD,
			String version,
			String region,
			String versionName,
			boolean physicalKeyboardAvailable);
    
    
    
    
    public static native int getGuiHeight();
    public static native int getGuiWidth();
    public static native int dsMapCreate();
    public static native int dsListCreate();
	public static native void dsMapAddString(int _dsMap, String _key, String _value);
	public static native void dsMapAddInt(int _dsMap, String _key, int _value);
	public static native void dsListAddString(int _dsList, String _value);
	public static native void dsListAddInt(int _dsMap, int _value);

	public static native void SetAchievementsAvailable(boolean available);

	public static native void IAPStoreLoadEvent(int _status);
	public static native void IAPProductDetailsReceived(String _jsonData);
	public static native void IAPProductPurchaseEvent(String _jsonData);
	public static native void IAPConsumeEvent(String _jsonData);	
	public static native void IAPRestoreEvent(int _status);
	//public static native void CallInappPurchase(String _ident);	

	public static native void callreward(int _scriptid,int _num,String _type);	

	// Bluetooth controller feedback
	public static native void iCadeEventDispatch(int _button, boolean _down);
	public static native void registerGamepadConnected(int _deviceIndex, int _buttonCount, int _axisCount);

	public static native int initGLFuncs(int _usingGL2);
	public static native boolean canFlip();
	public static native void GCMPushResult( String _dataString, int _type, boolean _bSuccess);

    
    
	// ----------------------------------------------------------------------------------------------------
	// ----------------------------------------------------------------------------------------------------
	public static int GetDefaultFrameBuffer() {
		return DemoRenderer.m_defaultFrameBuffer;
	}

	// ----------------------------------------------------------------------------------------------------
	// ----------------------------------------------------------------------------------------------------
	public static int UsingGL2() {
		return DemoGLSurfaceView.m_usingGL2;
	}

	public static void WaitForVsync() {
		DemoRenderer.WaitForVsync();
	}

	public static int HasVsyncHandler() {
		return RunnerActivity.CurrentActivity.vsyncHandler != null ? 1 : 0;
	}

	// ----------------------------------------------------------------------------------------------------
	// ----------------------------------------------------------------------------------------------------
    public static void OpenURL( String _url ) {
    	try {
    		Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(_url));
    		ms_context.startActivity(myIntent);
    	} catch (Exception e) {
    		Log.i("yoyo", "OpenURL failed: " + e);
    	}
    } // end OpenURL
    
    
    private static boolean mPlaybackStateStored = false;
    private static int mStoredPlaybackPosition;
    private static int mStoredPlaybackSessionId;
    private static boolean mStoredPlaybackLoop;
    private static long mStoredPlaybackOffset;
    private static long mStoredPlaybackSize;
    private static float mStoredVolume=1.0f;
     
	// ----------------------------------------------------------------------------------------------------
	// ----------------------------------------------------------------------------------------------------
    public static void PlayMP3( String _mp3, int _loop ) {
    	_mp3 = _mp3.replace(' ', '_');
		Log.i( "yoyo", "Request to play mp3 - \"" + _mp3 + "\"");
    	if (ms_mp != null) {
    		StopMP3();
    	} // end if
    	
    	boolean fSuccessful = false;
    	try {
    		Class resRaw = Class.forName("${YYAndroidPackageName}.R$raw");    		
			Field field = resRaw.getField("mp3_" + _mp3);
			int id = field.getInt(null);
			if (id != 0) {
				Log.i( "yoyo", "Playing mp3 - \"" + _mp3 + "\" id="+id);
				mStoredPlaybackSessionId = id;
				ms_mp = MediaPlayer.create( ms_context, id);
				ms_mp.setLooping( (_loop != 0) );
				//ms_mp.prepare();
				ms_mp.start();        
		  		fSuccessful = true;
			} // end if
    	} // end try
 		catch ( Exception e ) {
 			//Log.e( "yoyo", "unable to play mp3 - \"" + _mp3 + "\"", e);
	    	fSuccessful = false;
 		} // end else		
 		
		if (!fSuccessful) {
			try {
				// find the mp3 in the zip
				Log.i( "yoyo", "Request to play zip - \"" + DemoRenderer.m_apkFilePath + "\"");
				ZipFile zip = new ZipFile( DemoRenderer.m_apkFilePath );
				ZipEntry zipEntry = zip.getEntry( "assets/" + _mp3.toLowerCase(Locale.US) + ".mp3" );
				if (zipEntry != null) {
				
					// find the offset within the zip file
					Enumeration<? extends ZipEntry > zipEntries = zip.entries();
					long offset = 0;
			    	fSuccessful = false;
					while( zipEntries.hasMoreElements() ) {
						ZipEntry entry = (ZipEntry)zipEntries.nextElement();
						long fileSize = 0;
						long extra = (entry.getExtra() == null) ? 0 : entry.getExtra().length;
						offset += 30 + entry.getName().length() + extra;
						if (!entry.isDirectory()) {
							fileSize = entry.getCompressedSize();
						} // end if
						if (entry.getCrc() == zipEntry.getCrc()) {
							fSuccessful = true;
							break;
						} // end if
						offset += fileSize;
					} // end while
					
					if (fSuccessful) {
						// open the MediaPlayer on the file...
						mStoredPlaybackSessionId = -1;
						mStoredPlaybackOffset = offset;
						mStoredPlaybackSize = zipEntry.getSize();
						File inputFile = new File( DemoRenderer.m_apkFilePath );
						FileInputStream is = new FileInputStream( inputFile );
						
			    		//Log.i("yoyo", "Starting MP3 state. mStoredPlaybackOffset: " + mStoredPlaybackOffset + " mStoredPlaybackSize: " + mStoredPlaybackSize);
						ms_mp = new MediaPlayer();
						ms_mp.setDataSource( is.getFD(), mStoredPlaybackOffset, mStoredPlaybackSize );
						ms_mp.setLooping( (_loop != 0) );
						ms_mp.prepare();
						ms_mp.start();						
						
						is.close();
					} // end if
								
				} // end if
				
				zip.close();
				zip = null;
			} // end try
			catch( Exception e ) {
				Log.i( "yoyo", "Exception while opening mp3 - " + e );
			} // end catch
		} // end if
    } // end PlayMP3
     
    public static void PauseMP3()
    {
     if (ms_mp != null) {
   		Log.i( "yoyo", "pause mp3");
			try {
		    	ms_mp.pause();
		    }
			catch( Exception e ) {
				Log.i( "yoyo", "Exception while pausing mp3 - " + e );
			} // end catch
		} // end if
    }
    
    public static void ResumeMP3()
    {
	if (ms_mp != null) {
   		Log.i( "yoyo", "resume mp3");
			try {
		    	ms_mp.start();
		    }
			catch( Exception e ) {
				Log.i( "yoyo", "Exception while resuming mp3 - " + e );
			} // end catch
		} // end if
    } 
     
	// ----------------------------------------------------------------------------------------------------
	// ----------------------------------------------------------------------------------------------------
    public static void StopMP3() {
    	 if (ms_mp != null) {
   			Log.i( "yoyo", "stop mp3");
			try {
		    	ms_mp.stop();
		    	ms_mp.release();
		    }
			catch( Exception e ) {
				Log.i( "yoyo", "Exception while stopping mp3 - " + e );
			} // end catch
	    	ms_mp = null;
		} // end if
	} // end StopMP3
     
	// ----------------------------------------------------------------------------------------------------
	// ----------------------------------------------------------------------------------------------------
	public static boolean PlayingMP3() {
    	 boolean ret = false;
    	 if (ms_mp != null) {   			 
 			ret = ms_mp.isPlaying();
     	 }
     	 else {
     	 	Log.i("yoyo", "PlayingMP3(): ms_mp is NULL");
     	 }
    	 return ret;
    } // end PlayingMP3
     
	// ----------------------------------------------------------------------------------------------------
	// ----------------------------------------------------------------------------------------------------
    public static void SetMP3Volume( float _vol ) {
    	if (ms_mp != null) {
    		ms_mp.setVolume( _vol, _vol );
    		mStoredVolume = _vol;
    	} // end if
    } // SetMP3Volume

	// ----------------------------------------------------------------------------------------------------
	// ----------------------------------------------------------------------------------------------------
    public static void StoreMP3State() {
   		//Log.i("yoyo", "Storing MP3 state");
    	if (ms_mp != null) {
    		if (ms_mp.isPlaying()) {
	    		mStoredPlaybackPosition = ms_mp.getCurrentPosition();
    			mStoredPlaybackLoop = ms_mp.isLooping();
    			mPlaybackStateStored = true;
    		}
    	}
   		//Log.i("yoyo", "Stored MP3 state mPlaybackStateStored=" + mPlaybackStateStored + " ms_mp=" + ms_mp);
    } // StoreMP3State
    
	// ----------------------------------------------------------------------------------------------------
	// ----------------------------------------------------------------------------------------------------
    public static void RestoreMP3State() {
    
   		//Log.i("yoyo", "Restoring MP3 state mPlaybackStateStored=" + mPlaybackStateStored + " ms_mp=" + ms_mp);
    	if (mPlaybackStateStored && (ms_mp == null)) {    		
    		FileInputStream is = null;
    		if (mStoredPlaybackSessionId != -1) {
	    		ms_mp = MediaPlayer.create(ms_context, mStoredPlaybackSessionId);
	    	} // end if
	    	else {
	    		try {
					//Log.i( "yoyo", "Request to restore mp3 playback from zip - \"" + DemoRenderer.m_apkFilePath + "\"");
					File inputFile = new File( DemoRenderer.m_apkFilePath );
					is = new FileInputStream( inputFile );
					ms_mp = new MediaPlayer();
		    		//Log.i("yoyo", "Restoring MP3 state. mStoredPlaybackOffset: " + mStoredPlaybackOffset + " mStoredPlaybackSize: " + mStoredPlaybackSize);
					ms_mp.setDataSource( is.getFD(), mStoredPlaybackOffset, mStoredPlaybackSize );
				} // end try
				catch( Exception e ) {
					Log.i( "yoyo", "Exception while opening mp3 - " + e );
				} // end catch
	    	} // end else	    	
	    	try {
    			ms_mp.setLooping(mStoredPlaybackLoop);
    			ms_mp.setVolume(mStoredVolume,mStoredVolume);
    			if (is != null) {
    				ms_mp.prepare();
    			} // end if
    			ms_mp.seekTo(mStoredPlaybackPosition );
    			ms_mp.start(); 
    			if (is != null) {
	    			is.close();
    			} // end if
    		} catch( Exception e ) {
					Log.i( "yoyo", "Exception while opening mp3 - " + e );
    		} // end catch
    		
    		
    		//Log.i("yoyo", "Restoring MP3 state. ms_mp: " + ms_mp + " Loop: " + mStoredPlaybackLoop + " seeking to: " + mStoredPlaybackPosition  + " session id: " + mStoredPlaybackSessionId);
    		
    		mPlaybackStateStored = false;
    	}
    }

	// ----------------------------------------------------------------------------------------------------
	// ----------------------------------------------------------------------------------------------------
    private static int mAdX;
    private static int mAdY;
    private static int mAdNum;
    
    public static void MoveAds( int _x, int _y, int _num )
    {	
		if(RunnerActivity.mExtension!=null)
		{
			for(int i=0;i<RunnerActivity.mExtension.length;i++)
			{
				if(RunnerActivity.mExtension[i] instanceof IAdExt)
					((IAdExt)RunnerActivity.mExtension[i]).move(_x,_y,_num);
			}
		}
  
    } 
    
    public static void EnableAds( int _x, int _y, int _num )
    {
    
    
		if(RunnerActivity.mExtension!=null)
		{
			for(int i=0;i<RunnerActivity.mExtension.length;i++)
			{
				if(RunnerActivity.mExtension[i] instanceof IAdExt)
					((IAdExt)RunnerActivity.mExtension[i]).enable(_x,_y,_num);
			}
		}
	
    
    	
    } // end EnableAds
    
  
    public static void AdsSetup(String userid)
    {
		
    }
    
	// ----------------------------------------------------------------------------------------------------
	// ----------------------------------------------------------------------------------------------------
	
	public static int AdsDisplayWidth(int slot)
	{
		if(RunnerActivity.mExtension!=null)
		{
			for(int i=0;i<RunnerActivity.mExtension.length;i++)
			{
				if(RunnerActivity.mExtension[i] instanceof IAdExt)
					return ((IAdExt)RunnerActivity.mExtension[i]).getAdDisplayWidth(slot);
			}
		}
	
	
	 	
    	return 0;
	}
	public static int AdsDisplayHeight(int slot)
	{
		if(RunnerActivity.mExtension!=null)
		{
			for(int i=0;i<RunnerActivity.mExtension.length;i++)
			{
				if(RunnerActivity.mExtension[i] instanceof IAdExt)
					return ((IAdExt)RunnerActivity.mExtension[i]).getAdDisplayHeight(slot);
			}
		}
	
	
	   
    	return 0;
	}
	
    public static void DisableAds(int _slot)
    {
    	Log.i("yoyo", "DisableAds");
    	
    	if(RunnerActivity.mExtension!=null)
		{
			for(int i=0;i<RunnerActivity.mExtension.length;i++)
			{
				if(RunnerActivity.mExtension[i] instanceof IAdExt)
					((IAdExt)RunnerActivity.mExtension[i]).disable(_slot);
			}
		}
    	
    
    } // end DisableAds
    
    public static void AdsEvent(String _ident)
    {
    
		if(RunnerActivity.mExtension!=null)
		{
			for(int i=0;i<RunnerActivity.mExtension.length;i++)
			{
				if(RunnerActivity.mExtension[i] instanceof IAdExt)
					((IAdExt)RunnerActivity.mExtension[i]).event(_ident);
			}
		}
    
    }
     
    public static void AdsEventPreload(String _ident)
    {
		
    }
	// ----------------------------------------------------------------------------------------------------
	// ----------------------------------------------------------------------------------------------------
    public static void SetThreadPriority( int _priority ) {
    	Log.i("yoyo", "SetThreadPriority("+_priority);
 		Thread th = Thread.currentThread();
 		th.setPriority(_priority);
    }
     
	// ----------------------------------------------------------------------------------------------------
	// ----------------------------------------------------------------------------------------------------
    public static void LeaveRating( String _text, String _yes, String _no, String _url )
    {
    	Log.i( "yoyo", "LeaveRating("+_text+", "+_yes+", "+_no+", "+_url+")");
    	final String sText = _text;
    	final String sYes = _yes;
    	final String sNo = _no;
    	final String sUrl = _url;
    	RunnerActivity.ViewHandler.post( new Runnable() {
    		 public void run() {
    	    		AlertDialog.Builder builder = new AlertDialog.Builder(ms_context);
    	    		builder.setMessage( sText)
    	    			.setCancelable(false)
    	    			.setPositiveButton( sYes, new DialogInterface.OnClickListener() {
    	    				public void onClick( DialogInterface dialog, int id ) {
    	    					// goto URL
    	    					OpenURL( sUrl );
    	    				}
    	     			})
    	    			.setNegativeButton( sNo, new DialogInterface.OnClickListener() {
    	    				public void onClick( DialogInterface dialog, int id ) {
    	    					dialog.cancel();
    	    				}
    	    			});
    	    		AlertDialog alert  = builder.create();
    	    		alert.show();    		 
    		 }
    	});
    } // end LeaveRating
     
	// ----------------------------------------------------------------------------------------------------
	// ----------------------------------------------------------------------------------------------------
    public static void OpenAchievements()
    {
		if(RunnerActivity.isLoggedInGooglePlay())
    	{
    		RunnerActivity.ViewHandler.post( new Runnable() {
    			 public void run() {
    			 
    				RunnerJNILib.CallExtensionFunction("GooglePlayServicesExtension","onShowGSAchievements",0,null);
    			 
    			 
    			//	RunnerActivity.CurrentActivity.onShowGSAchievements();
    				
    			}});
    			
    	}
    	
    } 
    
    public static void AchievementLoadFriends()
    {
		
			RunnerActivity.ViewHandler.post( new Runnable() {
    			public void run() {
    			if(RunnerActivity.mExtension!=null)
				{
    				for(int i=0;i<RunnerActivity.mExtension.length;i++)
    				{
    					if(RunnerActivity.mExtension[i] instanceof ISocial)
							((ISocial)RunnerActivity.mExtension[i]).LoadFriends();
					}
				}
			}});
		
		
    
    }
    
    public static void AchievementGetInfo(String _id)
    {
		final String id = _id;
		
			RunnerActivity.ViewHandler.post( new Runnable() {
    			 public void run() {
    			 
					Log.i("yoyo","Calling to social interface to get info for id " + id);
					if(RunnerActivity.mExtension!=null)
					{
						for(int i=0;i<RunnerActivity.mExtension.length;i++)
    					{
    						if(RunnerActivity.mExtension[i] instanceof ISocial)
								((ISocial)RunnerActivity.mExtension[i]).GetInfo(id);
						}
					}
			}});
		
    }
    
    public static void AchievementShow(int _type, String _optarg, int _numarg)
    {
		
		
			final int type = _type;
			final String optarg = _optarg;
			final int numarg = _numarg ;
		
			RunnerActivity.ViewHandler.post( new Runnable() {
    			public void run() {
    			if(RunnerActivity.mExtension!=null)
				{
    				for(int i=0;i<RunnerActivity.mExtension.length;i++)
    				{ 
    					if(RunnerActivity.mExtension[i] instanceof ISocial)
							((ISocial)RunnerActivity.mExtension[i]).Show(type,optarg,numarg);
					}
				}		
			}});
		
    
    }
    
    public static void AchievementLoadPic(String _id)
    {
		
			final String id = _id;
		
			RunnerActivity.ViewHandler.post( new Runnable() {
    			 public void run() {
					Log.i("yoyo","Calling to social interface to load pic for id " + id);
					if(RunnerActivity.mExtension!=null)
					{
						for(int i=0;i<RunnerActivity.mExtension.length;i++)
    					{ 
    						if(RunnerActivity.mExtension[i] instanceof ISocial)
								((ISocial)RunnerActivity.mExtension[i]).LoadPic(id);
						}
					}
			}});
		
    }
    
    public static void AchievementEvent(String _id)
    {
	//	if(RunnerActivity.mSocial!=null)
	//	{
			final String id = _id;
			RunnerActivity.ViewHandler.post( new Runnable() {
    			 public void run() {
					Log.i("yoyo","Calling to social interface to register event " + id);
					if(RunnerActivity.mExtension!=null)
					{
						for(int i=0;i<RunnerActivity.mExtension.length;i++)
    					{ 
    						if(RunnerActivity.mExtension[i] instanceof ISocial)
								((ISocial)RunnerActivity.mExtension[i]).Event(id);
						}
					}
			}});
	//	}
    }
    
    
    public static void AchievementLoadLeaderboard(String _id, int _minindex,int _maxindex,int _filter)
    {
		
	//	if(RunnerActivity.mSocial!=null)
	//	{
			final String id = _id;
			final int minindex = _minindex;
			final int maxindex = _maxindex;
			final int filter = _filter;
		
			RunnerActivity.ViewHandler.post( new Runnable() {
    			public void run() {
    				if(RunnerActivity.mExtension!=null)
					{
    					for(int i=0;i<RunnerActivity.mExtension.length;i++)
    					{	 
    						if(RunnerActivity.mExtension[i] instanceof ISocial)
								((ISocial)RunnerActivity.mExtension[i]).LoadLeaderboard(id,minindex,maxindex,filter);
						}
					}
			}});
	//	}
		
    }
    
    
    public static int OsGetInfo()
    {
		//get info & ping back to create the ds map, then return the index of that map...
		
		String version = System.getProperty("os.version",""); //Returns empty string if not found
		String region = System.getProperty("user.region","");
		
		boolean physicalKeyboardAvailable = false;		
		RunnerKeyboardController keyboardController = RunnerActivity.CurrentActivity.GetKeyboardController();
		if(keyboardController != null)
		{
			physicalKeyboardAvailable = keyboardController.GetPhysicalKeyboardConnected();
		}
		
	/*	java.util.Properties props = System.getProperties();
		java.util.Enumeration e = props.propertyNames();
		while (e.hasMoreElements()) {
			String k = (String) e.nextElement();
			String v = props.getProperty(k);
			Log.i("yoyo","Sys prop:" + k + " val:" +v);
		}
		
		Log.i("yoyo","os.BOARD:"+android.os.Build.BOARD);
		Log.i("yoyo","os.BOOTLOADER:"+android.os.Build.BOOTLOADER);
		Log.i("yoyo","os.BRAND:"+android.os.Build.BRAND);
		Log.i("yoyo","os.CPU_ABI:"+android.os.Build.CPU_ABI);
		Log.i("yoyo","os.CPU_ABI2:"+android.os.Build.CPU_ABI2);
		Log.i("yoyo","os.DEVICE:"+android.os.Build.DEVICE);
		Log.i("yoyo","os.DISPLAY:"+android.os.Build.DISPLAY);
		Log.i("yoyo","os.FINGERPRINT:"+android.os.Build.FINGERPRINT);
		Log.i("yoyo","os.HARDWARE:"+android.os.Build.HARDWARE);
		Log.i("yoyo","os.HOST:"+android.os.Build.HOST);
		Log.i("yoyo","os.MANUFACTURER:"+android.os.Build.MANUFACTURER);
		Log.i("yoyo","os.MODEL:"+android.os.Build.MODEL);
		Log.i("yoyo","os.PRODUCT:"+android.os.Build.PRODUCT);
			Log.i("yoyo","os.TYPE:"+android.os.Build.TYPE);
			Log.i("yoyo","os.USER:"+android.os.Build.USER);
	
			Log.i("yoyo","os.TAGS:"+android.os.Build.TAGS);
	
	
		Log.i("yoyo","Build.version.sdk_int"+ android.os.Build.VERSION.SDK_INT);
		Log.i("yoyo","Build.release" + android.os.Build.VERSION.RELEASE);
		
	*/
	
		int dsmapid =RunnerJNILib.CreateVersionDSMap(
			android.os.Build.VERSION.SDK_INT,
			android.os.Build.VERSION.RELEASE,
			android.os.Build.MODEL,
			android.os.Build.DEVICE,
			android.os.Build.MANUFACTURER,
			android.os.Build.CPU_ABI,
			android.os.Build.CPU_ABI2,
			android.os.Build.BOOTLOADER,
			android.os.Build.BOARD,
			version,
			region,
			ms_versionName,
			physicalKeyboardAvailable);
	
		return dsmapid;
    }
     
	// ----------------------------------------------------------------------------------------------------
	// ----------------------------------------------------------------------------------------------------
    public static void OpenLeaderboards()
    {
		if(RunnerActivity.isLoggedInGooglePlay())
    	{
    		RunnerActivity.ViewHandler.post( new Runnable() {
    			 public void run() {
    			//	RunnerActivity.CurrentActivity.onShowGSLeaderboards();
    			
    			RunnerJNILib.CallExtensionFunction("GooglePlayServicesExtension","onShowGSLeaderboards",0,null);
    			
    			}});
    			
    	}
    } // end OpenLeaderboards
     
    public static void AchievementLogout()
    {
		Log.i( "yoyo", "AchievementLogout()");
		
		
		
			RunnerActivity.ViewHandler.post( new Runnable() {
    		 public void run() {
    		 
    				//if(RunnerActivity.mSocial!=null)
					//{
						if(RunnerActivity.mExtension!=null)
						{
							for(int i=0;i<RunnerActivity.mExtension.length;i++)
    						{	 
    							if(RunnerActivity.mExtension[i] instanceof ISocial)
									((ISocial)RunnerActivity.mExtension[i]).Logout();
							}
						}
						
					//}
					//else
    				//	RunnerActivity.CurrentActivity.signOut();
    		 }
			});
		
    }
     
    public static void AchievementLogin()
    {
		Log.i( "yoyo", "AchievementLogin()");
		
		
		RunnerActivity.ViewHandler.post( new Runnable() {
    			 public void run() {
    					RunnerActivity.ach_login();
    			 }
				});
		
    } 
     
    public static int AchievementLoginStatus()
    {
		//Log.i("yoyo","AchievementLoginStatus()
		if(RunnerActivity.isLoggedInGooglePlay())
		{
			return 1;
		}
		return 0;
    }
     
     
	// ----------------------------------------------------------------------------------------------------
	// ----------------------------------------------------------------------------------------------------
    public static void SendAchievement( String _achievement, float _percentageDone )
    {
		if(RunnerActivity.isLoggedInGooglePlay())
		{
			final String ach = _achievement;
			final float incval = _percentageDone;
			RunnerActivity.ViewHandler.post( new Runnable() {
    		
    		public void run() {
    		
    			Object [] argArray = new Object[2];	
				argArray[0] = ach;
				argArray[1] = incval;
				
    			CallExtensionFunction("GooglePlayServicesExtension","onPostAchievement",2,argArray);
    		
    			//RunnerActivity.CurrentActivity.onPostAchievement(ach,incval);
    			}
			});
		}
	}
	
	public static void IncrementAchievement( String _achievement, float _incval )
	{
		if(RunnerActivity.isLoggedInGooglePlay())
		{
			final String ach = _achievement;
			final float incval = _incval;
			RunnerActivity.ViewHandler.post( new Runnable() {
    		
    		public void run() {
    		
    			Object [] argArray = new Object[2];	
				argArray[0] = ach;
				argArray[1] = incval;
				
    			CallExtensionFunction("GooglePlayServicesExtension","onIncrementAchievement",2,argArray);
    		
    		
    		
    		//	RunnerActivity.CurrentActivity.onIncrementAchievement(ach,incval);
    			}
			});
		}
		else
		{
			Log.i("yoyo","Current achievements system does not support incrementing achievements");
		}
	}
     
	// ----------------------------------------------------------------------------------------------------
	// ----------------------------------------------------------------------------------------------------
    public static void SendHighScore( String _leaderboard, int _score )
    {
    	Log.i( "yoyo", "SendHighScore(" + _leaderboard + "," + _score + ")");
    	
		{
			final String lb = _leaderboard;
			final int sc = _score;
			RunnerActivity.ViewHandler.post( new Runnable() {
    			 public void run() {
					
						if(RunnerActivity.mExtension!=null)
						{
							for(int i=0;i<RunnerActivity.mExtension.length;i++)
    						{	 
    							if(RunnerActivity.mExtension[i] instanceof ISocial)
									((ISocial)RunnerActivity.mExtension[i]).PostScore(lb,sc);
							}
						}
			}});
			
		}
		//else 
		//{
		//	Log.i( "yoyo", "SendHighScore called whilst not logged in to valid service");
		//}
    } 
    
	public static Object CallExtensionFunction(String classname, String methodname, int argcount,Object [] args)
    {
		Object retObject = null;
		if(RunnerActivity.mExtension==null)
		{
			Log.i("yoyo","Attempting to call extension function with no extensions loaded " + methodname + " on class " + classname); 
			return retObject;
		}
		
		if(classname==null || methodname == null)
		{
			if(classname==null)
				Log.i("yoyo","Attempting to call extension function with null classname method:"+methodname);
			else if(methodname==null)
				Log.i("yoyo","Attempting to call extension function with null methodname on class:"+classname);
			
			
			return retObject;
		}
    
	//	Log.i("yoyo","Java side attempting to call extension function " + methodname + " on class " + classname); 
		try{
			Class<?> ReqClass = null;
		
			try{
				ReqClass = Class.forName("${YYAndroidPackageName}."+classname);
			}
			catch (ClassNotFoundException e)
			{
				Log.i("yoyo","Extension Class not found: " + "${YYAndroidPackageName}."+classname + " attempting to call " + methodname);
			}
		
			if(ReqClass !=null)
			{
	//		Log.i("yoyo","Found " + RunnerActivity.mExtension.length + " extensions");
			for( int j=0;j<RunnerActivity.mExtension.length;j++)
			{
					if(RunnerActivity.mExtension[j]!=null)
					{
				if(ReqClass.isInstance(RunnerActivity.mExtension[j]))
				{
					if(argcount >0)
					{
						Class [] cArg = new Class[argcount];
						
						for(int i=0;i<argcount;i++)
						{		
							cArg[i] = args[i].getClass();				
						}			
						
						try{
							Method method = RunnerActivity.mExtension[j].getClass().getMethod(methodname,cArg);
						
									Log.i("yoyo","Method found, attempting to invoke " + methodname);
							retObject = method.invoke(RunnerActivity.mExtension[j],args);
							break;
						}
						catch(Exception e)
						{
									Log.i("yoyo","Exception thrown calling method on extension class:"+e + " looking for " + methodname + " on " + classname + " " +e.getMessage());
							
							for(int ac=0;ac<argcount;ac++)
							{
								Log.i("yoyo","Argument " + ac+ " of type " + cArg[ac].toString());
							}
							e.printStackTrace();
							
							Method [] classMethods = RunnerActivity.mExtension[j].getClass().getMethods();
							
							for(int xx = 0;xx<classMethods.length;xx++)
							{
								Log.i("yoyo","Found method " + classMethods[xx].toString());
							}
							
							
						}
						
					}
					else
					{
						try{
							Method method = RunnerActivity.mExtension[j].getClass().getMethod(methodname);
						
					//		Log.i("yoyo","Method found, attempting to invoke "+methodname);
							retObject = method.invoke(RunnerActivity.mExtension[j]);
							break;
						}
						catch(Exception e)
						{
						//	Log.i("yoyo","Can't find argfree method on extension class:"+e.getMessage());
									Log.i("yoyo","Exception thrown calling argfree method on extension class:"+e + " looking for " + methodname + " on " + classname + " " +e.getMessage());
						
							e.printStackTrace();
						}
						
					}
						}
					
				}
				}
		
			}
		
		}
		catch(Exception e)
		{
			
			Log.i("yoyo","Exception thrown trying to call method "+ methodname + " on " + classname + " " +e.getMessage());
			e.printStackTrace();
		}	
		return retObject;
			
    }
    
    
    public static Object CallExtensionFunction(String classname, String methodname, int argcount,  double [] dblargs,Object []objargs)
    {
		Object retObject = null;
		if(RunnerActivity.mExtension==null)
		{
			Log.i("yoyo","Attempting to call extension function with no extensions loaded " + methodname + " on class " + classname); 
			return retObject;
		}
		
		if(classname==null || methodname == null)
		{
			if(classname==null)
				Log.i("yoyo","Attempting to call extension function with null classname method:"+methodname);
			else if(methodname==null)
				Log.i("yoyo","Attempting to call extension function with null methodname on class:"+classname);
			
			
			return retObject;
		}
    
		//Log.i("yoyo","Java side attempting to call extension function " + methodname + " on class " + classname); 
		try{
		
		Class<?> ReqClass = null;

			try{
				ReqClass = Class.forName("${YYAndroidPackageName}."+classname);
			}
			catch (ClassNotFoundException e)
			{
				Log.i("yoyo","Extension Class not found: " + "${YYAndroidPackageName}."+classname + " attempting to call " + methodname);
			}
		
			if(ReqClass !=null)
			{
				int i;
			//Log.i("yoyo","Found " + RunnerActivity.mExtension.length + " extensions");
			for( int j=0;j<RunnerActivity.mExtension.length;j++)
			{
				if(ReqClass.isInstance(RunnerActivity.mExtension[j]))
				{
					if(argcount >0)
					{
						Class [] cArg = new Class[argcount];
						Object []Args = new Object[argcount];
						

							for(i=0;i<argcount;i++)
						{
								if(objargs[i]!=null)//argtypes[i]==3)
							{
									cArg[i] = objargs[i].getClass();
									Args[i] = objargs[i];
							}
							else 
							{
								cArg[i] = double.class;
								Args[i] = dblargs[i];
							}
						}
						
						
							Method[] methods = ReqClass.getMethods();
							Method toInvoke = null;
							for(Method method :methods)
							{
								if(!methodname.equals(method.getName()))
									continue;

								Class<?>[] paramTypes = method.getParameterTypes();
								if(paramTypes==null || paramTypes.length!=argcount)
									continue;

								for(i=0;i<cArg.length;i++)
								{
									if (!paramTypes[i].isAssignableFrom(cArg[i]))
										break;
								}
								if(i!=cArg.length)
									continue;

								toInvoke = method;
								break;
							}


							if(toInvoke!=null)
							{
						try{
									retObject = toInvoke.invoke(RunnerActivity.mExtension[j],Args);
						}
						catch(Exception e)
						{
									Log.i("yoyo","Exception3 thrown trying to call method "+ methodname + " on " + classname);
									Log.i("yoyo","Exception:" + e.getMessage());
									Method [] classMethods = RunnerActivity.mExtension[j].getClass().getMethods();

									for(int xx = 0;xx<classMethods.length;xx++)
									{
										Log.i("yoyo","Found method " + classMethods[xx].toString());
									}
						}
						
					}
					else
					{
								Log.i("yoyo","Unable to find method to invoke matching methodname:" + methodname +" on class:"+classname+ " with params:" );
								for(int p=0;p<cArg.length;p++)
								{
									Log.i("yoyo","param:"+p+":" + cArg[p]);
								}
							}
						}
						else
						{
						try{
							Method method = RunnerActivity.mExtension[j].getClass().getMethod(methodname);
								if(method == null)
								{
									Log.i("yoyo","Can't find argfree method on extension class:");
								}
								else
							retObject = method.invoke(RunnerActivity.mExtension[j]);
						}
						catch(Exception e)
						{
								Log.i("yoyo","Exception thrown trying to call " + methodname+ " on class " + classname+ " with no arguments:"+e.getMessage());
							e.printStackTrace();
						}
					}
					
				}
				}
		
			}
		
		}
		catch(Exception e)
		{
			
			Log.i("yoyo","Exception thrown trying to call method "+ methodname + " on " + classname);
			Log.i("yoyo",e.getMessage());


			e.printStackTrace();
		}	
		return retObject;
			
    }
    
    
	// ----------------------------------------------------------------------------------------------------
	// ----------------------------------------------------------------------------------------------------     
    public static void EnableInAppBilling(String[] _productIds)
    {
		RunnerActivity.CurrentActivity.RunnerBilling().enableInAppPurchases(_productIds);
    } // end EnableInAppBilling
    
    public static void RestoreInAppPurchases()
    {		
    	RunnerActivity.CurrentActivity.RunnerBilling().restorePurchasedItems();
    } // RestoreInAppPurchases
    
    public static void AcquireInAppPurchase(String _productId, String _payload, int _purchaseIndex)
    {
		RunnerActivity.CurrentActivity.RunnerBilling().purchaseCatalogItem(_productId, _payload, _purchaseIndex);		
    } // AcquireInAppPurchase
    
    public static void ConsumeInAppPurchase(String _productId, String _token)
    {
		RunnerActivity.CurrentActivity.RunnerBilling().consumeCatalogItem(_productId, _token);
    } // ConsumeInAppPurchase 

	public static void GetInAppPurchaseDetail(String _productId)
	{
		RunnerActivity.CurrentActivity.RunnerBilling().getCatalogItemDetails(_productId);
	} // GetInAppPurchaseDetail
    
	// ----------------------------------------------------------------------------------------------------
	// ----------------------------------------------------------------------------------------------------
    public static boolean DownloadFileTo( String _urlFile, String _to )
    {
    	boolean ret = false;
    	try {
			Log.i( "yoyo", "DownloadFileTo( " + _urlFile + " , " + _to + " )" );
			URL url = new URL( _urlFile );
			URLConnection connection = url.openConnection();
			connection.setUseCaches( false );
			connection.connect();
			FileOutputStream fs = new FileOutputStream( new File( _to ) );
			InputStream in = connection.getInputStream();
			byte[] buffer = new byte[1024];
			int len1 = 0;
			while( (len1 = in.read(buffer)) > 0) {
				Log.i( "yoyo", "downloaded " + len1 + " bytes" );
				fs.write( buffer, 0, len1 );
			} // end while
			fs.close();
			ret = true;
		} // end try
		catch( MalformedURLException _e) {
			Log.i( "yoyo", "MalformedURLException on DownloadFileTo" + _e );
		} // end catch
		catch( ProtocolException _e) {
			Log.i( "yoyo", "ProtocolException on DownloadFileTo" + _e );
		} // end catch
		catch( FileNotFoundException _e) {
			Log.i( "yoyo", "FileNotFoundException on DownloadFileTo" + _e );
		} // end catch
		catch( IOException _e) {
			Log.i( "yoyo", "IOException on DownloadFileTo" + _e );
		} // end catch
		
		return ret;
    } // end DownloadFileTo

	// ----------------------------------------------------------------------------------------------------
	// ----------------------------------------------------------------------------------------------------
	public static void FacebookInit(String _appID_unused) 
	{
			
		//app id MUST be in manifest - we don't use passed in appID
		String appId = RunnerActivity.CurrentActivity.getFacebookAppId();
		if( appId != null )
		{
			if (m_runnerFacebook == null) 
			{
				m_runnerFacebook = new RunnerFacebook();
				m_runnerFacebook.initFacebook( appId );
    		}
		}
		else
		{
			Log.i("yoyo", "facebook_init failed: no application id supplied. Ensure facebook is enabled in Global Game Settings");
		}		
	}
    
	private static boolean checkFBInitialised( String _msg )
	{
		if( m_runnerFacebook == null ) {
			Log.i("yoyo", _msg + " : facebook_init was not called or facebook appId is missing");
			return false;
		}
		return true;
	}
	
    public static String FacebookAccessToken() {
		if( !checkFBInitialised("facebook_accesstoken")) {
			return "";
		}
		return m_runnerFacebook.getAccessToken();
    }
    
    public static String FacebookUserId() {
   
		if( !checkFBInitialised("facebook_user_id")) {
			return "";
		}
		return m_runnerFacebook.getUserId();
    }
	
	// ----------------------------------------------------------------------------------------------------
	// ----------------------------------------------------------------------------------------------------
	public static boolean FacebookCheckPermission( String _permission)
	{
		if( !checkFBInitialised("facebook_user_id")) {
			return false;
		}
		
		return m_runnerFacebook.CheckPermission( _permission );
	}
	
	public static int FacebookRequestPermissions( String[] _permissions, boolean _bPublishPerms )
	{
		if( !checkFBInitialised("facebook_request_read/publish_permissions")) {
			return -1;
		}
		return m_runnerFacebook.RequestPermissions( _permissions, _bPublishPerms );
	}
    
    public static String FacebookLoginStatus() 
	{		
    	if( !checkFBInitialised("facebook_status")) {
    		return "IDLE";
    	}
    	 
		return m_runnerFacebook.facebookLoginStatus();		
	}
    
    // Called by GM to request that we login to Facebook and request permissions
    public static void FacebookLogin(String[] permissions) {
    
    	if( !checkFBInitialised("facebook_login")) {
    		return;
    	}
    
    	Log.i( "yoyo", "Logging into Facebook");    	    	
		m_runnerFacebook.setupFacebook(permissions);
	}
	
	// ----------------------------------------------------------------------------------------------------
	// ----------------------------------------------------------------------------------------------------
	public static void FacebookLogout() {
	
		if( !checkFBInitialised("facebook_logout")) {
    		return;
    	}
		
		Log.i( "yoyo", "Logging out of Facebook"); 
		if(m_runnerFacebook!= null)
			m_runnerFacebook.logout();
	}
	
	// ----------------------------------------------------------------------------------------------------
	// ----------------------------------------------------------------------------------------------------
	// Pass over a Facebook graph request			
	public static void FacebookGraphRequest(String graphPath, String httpMethod, String[] keyValuePairs, int dsMapResponse) {
	
		if( !checkFBInitialised("facebook_post_message/facebook_graph_request")) {
    		return;
    	}

		m_runnerFacebook.graphRequest(graphPath, httpMethod, keyValuePairs, dsMapResponse);
	}
    
 	// ----------------------------------------------------------------------------------------------------
	// ----------------------------------------------------------------------------------------------------
    // Pass over a request for a Facebook dialog    
	public static void FacebookDialog(String dialogType, String[] keyValuePairs, int dsMapResponse) {
	
		if( !checkFBInitialised("facebook_dialog")) {
    		return;
    	}

		m_runnerFacebook.dialog(dialogType, keyValuePairs, dsMapResponse);
    }	
    
    public static void FacebookInviteDialog(String dialogType, String[] keyValuePairs, int dsMapResponse) {
	
    	if( !checkFBInitialised("facebook_send_invite")) {
    		return;
    	}
    	
		m_runnerFacebook.inviteDialog(dialogType, keyValuePairs, dsMapResponse);
    }	
    
 	// ----------------------------------------------------------------------------------------------------
	// ----------------------------------------------------------------------------------------------------
	public static void ShowMessage(String _message ) {
    	Log.i( "yoyo", "ShowMessage(\""+_message+"\")");
    	
    	final String sMessage = _message;
    	final CountDownLatch latch = new CountDownLatch(1);
    	RunnerActivity.ViewHandler.post( new Runnable() {
    		 public void run() {
    	    		AlertDialog.Builder builder = new AlertDialog.Builder(ms_context);
    	    		builder.setMessage( sMessage)
    	    			.setCancelable(false)
    	    			.setPositiveButton( "OK", new DialogInterface.OnClickListener() {
    	    				public void onClick( DialogInterface dialog, int id ) {
    	    					latch.countDown();
    	    				}
    	     			});
    	    		AlertDialog alert  = builder.create();
    	    		alert.show();    		 
    		 }
    	});
    	
    	try {
    		latch.await();
    	} catch( InterruptedException e ) {
    		Thread.currentThread().interrupt();
    	} // end catch
	} // end ShowMessage
	
 	// ----------------------------------------------------------------------------------------------------
	// ----------------------------------------------------------------------------------------------------
	public static void ShowMessageAsync(String _message, int _id ) {
    	Log.i( "yoyo", "ShowMessageAsync(\""+_message+"\","+_id+")");
    	
    	final String sMessage = _message;
    	final int idDialog = _id;
    	RunnerActivity.FocusOverride = true;
    	RunnerActivity.ViewHandler.post( new Runnable() {
    		 public void run() {
    	    		AlertDialog.Builder builder = new AlertDialog.Builder(ms_context);
    	    		builder.setMessage( sMessage)
    	    			.setCancelable(false)
    	    			.setPositiveButton( "OK", new DialogInterface.OnClickListener() {
    	    				public void onClick( DialogInterface dialog, int id ) {
    	    					RunnerJNILib.InputResult( "OK", 1,  idDialog);	
    	    				}
    	     			});
    	    		AlertDialog alert  = builder.create();
    	    		alert.show();    		 
    		 }
    	});
    	
	} // end ShowMessage
	
	
 	// ----------------------------------------------------------------------------------------------------
	// ----------------------------------------------------------------------------------------------------
	public static String InputString( String _message, String _default ) {
    	Log.i( "yoyo", "InputString(\"" +_message+ "\", \""+_default+"\")");
    	
    	final String sMessage = _message;
    	final String sDefault = _default;
    	final CountDownLatch latch = new CountDownLatch(1);
    	RunnerActivity.ViewHandler.post( new Runnable() 
    	{
    		 public void run() {
    	    		AlertDialog.Builder builder = new AlertDialog.Builder(ms_context);
    	    		final EditText input = new EditText(ms_context);
    	    		input.setText(sDefault);
					builder.setView(input);
    	    		builder.setMessage(sMessage)
    	    			.setCancelable(false)
    	    			.setPositiveButton( "OK", new DialogInterface.OnClickListener() {
    	    				public void onClick( DialogInterface dialog, int id ) {
    	    					RunnerActivity.InputStringResult = input.getText().toString();
    	    					latch.countDown();
    	    				}
    	     			});
					builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							RunnerActivity.InputStringResult = sDefault;
    	    				latch.countDown();
							return;   
						}
					});

    	    		AlertDialog alert  = builder.create();
    	    		alert.show();    		 
    		 }
    	});
    	
    	try {
    		latch.await();
    	} catch( InterruptedException e ) {
    		Thread.currentThread().interrupt();
    	} // end catch
    	return RunnerActivity.InputStringResult;
	} // end ShowMessage	
	
 	// ----------------------------------------------------------------------------------------------------
	// ----------------------------------------------------------------------------------------------------
	public static void InputStringAsync( String _message, String _default, int _id ) {
    	Log.i( "yoyo", "InputStringAsync(\"" +_message+ "\", \""+_default+"\","+_id+")");
    	
    	final String sMessage = _message;
    	final String sDefault = _default;
    	final int idDialog = _id;
    	RunnerActivity.FocusOverride = true;
    	RunnerActivity.ViewHandler.post( new Runnable() 
    	{
    		 public void run() {
    	    		AlertDialog.Builder builder = new AlertDialog.Builder(ms_context);
    	    		final EditText input = new EditText(ms_context);
    	    		input.setText(sDefault);
					builder.setView(input);
    	    		builder.setMessage(sMessage)
    	    			.setCancelable(false)
    	    			.setPositiveButton( "OK", new DialogInterface.OnClickListener() {
    	    				public void onClick( DialogInterface dialog, int id ) {
    	    					RunnerActivity.InputStringResult = input.getText().toString();
    	    					RunnerJNILib.InputResult( RunnerActivity.InputStringResult, 1,  idDialog);	
    	    				}
    	     			});
					builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							RunnerActivity.InputStringResult = sDefault;
   	    					RunnerJNILib.InputResult( RunnerActivity.InputStringResult, 0,  idDialog);	
						}
					});

    	    		AlertDialog alert  = builder.create();
    	    		alert.show();    		 
    		 }
    	});    	
	} // end InputStringAsync	
	
	
 	// ----------------------------------------------------------------------------------------------------
	// ----------------------------------------------------------------------------------------------------
	public static int ShowQuestion(String _message ) {
    	Log.i( "yoyo", "ShowQuestion(\""+_message+"\")");
    	
    	final String sMessage = _message;
    	final CountDownLatch latch = new CountDownLatch(1);
		RunnerActivity.ShowQuestionYesNo = 0;
    	RunnerActivity.ViewHandler.post( new Runnable() {
    		 public void run() {
    	    		AlertDialog.Builder builder = new AlertDialog.Builder(ms_context);
    	    		builder.setMessage( sMessage)
    	    			.setCancelable(false)
    	    			.setPositiveButton( "Yes", new DialogInterface.OnClickListener() {
    	    				public void onClick( DialogInterface dialog, int id ) {
								RunnerActivity.ShowQuestionYesNo = 1;
    	    					latch.countDown();
    	    				}
    	     			});
					builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							RunnerActivity.ShowQuestionYesNo = 0;
    	    				latch.countDown();
							return;   
						}
					});
    	    		AlertDialog alert  = builder.create();
    	    		alert.show();    		 
    		 }
    	});
    	
    	try {
    		latch.await();
    	} catch( InterruptedException e ) {
    		Thread.currentThread().interrupt();
    	} // end catch
    	return RunnerActivity.ShowQuestionYesNo;
	} // end ShowMessage
	
 	// ----------------------------------------------------------------------------------------------------
	// ----------------------------------------------------------------------------------------------------
	public static void ShowQuestionAsync(String _message, int _id ) {
    	Log.i( "yoyo", "ShowQuestionAsync(\""+_message+"\","+_id+")");
    	
    	
    	final String sMessage = _message;
    	final int idDialog = _id;
    	RunnerActivity.FocusOverride = true;
    	RunnerActivity.ViewHandler.post( new Runnable() {
    		 public void run() {
    	    		AlertDialog.Builder builder = new AlertDialog.Builder(ms_context);
    	    		builder.setMessage( sMessage)
    	    			.setCancelable(false)
    	    			.setPositiveButton( "Yes", new DialogInterface.OnClickListener() {
    	    				public void onClick( DialogInterface dialog, int id ) {
	   	    					RunnerJNILib.InputResult( "1", 1,  idDialog);	
    	    				}
    	     			});
					builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
   	    					RunnerJNILib.InputResult( "0", 0,  idDialog);	
						}
					});
    	    		AlertDialog alert  = builder.create();
    	    		alert.show();    		 
    		 }
    	});    	
	} // end ShowQuestionAsync
	
	
	
 	// ----------------------------------------------------------------------------------------------------
	// ----------------------------------------------------------------------------------------------------
	private static String HttpGetHeaders(HttpURLConnection _conn)
	{
		String headers = "";
		for (Map.Entry<String, List<String>> k : _conn.getHeaderFields().entrySet()) {
			for (String v : k.getValue()) {
				if(k.getKey()!=null)
					headers = headers + k.getKey() + ": " + v + "\r\n";				
				else
					headers = headers + "nokey" + ": " + v + "\r\n";	
			}
		}		
		return headers;
	}
	
	public static void HttpGet(String _url, int _id ) {
    	Log.i( "yoyo", "HttpGet(\""+_url+"\", "+_id+")");
		// lets run this on the main UI thread...
		final String url = _url;
		final int id = _id;
    	new Thread( new Runnable() {
    		 public void run() {
    		 
    			HttpURLConnection conn;
    			try 
    			{
    				URL connectURL = new URL(url);
    				conn = (HttpURLConnection)connectURL.openConnection();
    			}
    			catch( MalformedURLException _mue ) {
    				RunnerJNILib.HttpResultString( "MalformedURLException", 404,  id);	
    				conn = null;
    			}
				catch( IOException _ioe) {
					RunnerJNILib.HttpResultString( "IOException", 404,  id);	
    				conn = null;
				} 
    			if (conn != null) {
    				try
    				{   
    					conn.setRequestProperty("Accept-Encoding", "identity");
    					conn.setDoInput(true);
    					conn.setDoOutput(false); 					    					
    					conn.setUseCaches(false);//? why?
    					conn.connect();
						
	    				String finalurl= null;
    					int responseCode = conn.getResponseCode();
    					byte[] result = null;
    					if (responseCode == 200) 
    					{
    						InputStream is = conn.getInputStream();
    						byte[] buffer = new byte[ 4096 ];
    						int n = -1;
    						finalurl = conn.getURL().toString();
    						while( (n=is.read(buffer)) != -1 ) 
    						{
    							
    						//	Log.i("yoyo","Writing " + n + " bytes to ByteArrayOutputStream");
    							RunnerJNILib.HttpProgress( buffer, n, id, finalurl, "",conn.getContentLength());	
    							
    					//		Log.i("yoyo","Connection content length=" + conn.getContentLength());
    						
    						} // end while
    						
    						
    					} // end if
    					else
    					{
    						Log.i("yoyo","Received responseCode " + responseCode);
    					}
    				//	else {
    				//		result = new byte[0];
    				//	} // end else
    					
	    							    				
					   //	Log.i( "yoyo", "http_get result = \"" + result+"\", responseCode=" + responseCode + ", id=" + id + " url=" + );
					   	Log.i( "yoyo", "http_get responseCode=" + responseCode + ", id=" + id+", finalurl="+finalurl+" headers=" +  HttpGetHeaders(conn));
    					RunnerJNILib.HttpResult( null, responseCode, id, finalurl, HttpGetHeaders(conn));	
    					
    					conn.disconnect();
    					conn = null;
    				}
    				catch( Exception _e) {
					   	Log.i( "yoyo", "Exception = " + _e.toString());
    					RunnerJNILib.HttpResultString( "IOException", 404,  id);	
    					conn = null;
    				}
    			} // end if    		 
    		 } // end run
    	}).start();
	} // end HttpGet
	
	public static void HttpPost(String _url, String _post, int _id ) {
    	Log.i( "yoyo", "HttpPost(\""+_url+"\", \""+_post+"\", "+_id+")");
		// lets run this on the main UI thread...
		final String url = _url;
		final String post = _post;
		final int id = _id;
    	new Thread( new Runnable() {
    		 public void run() {
    		 
    			HttpURLConnection conn;
    			try 
    			{
    				URL connectURL = new URL(url);
    				conn = (HttpURLConnection)connectURL.openConnection();
    			}
    			catch( MalformedURLException _mue ) {
    				RunnerJNILib.HttpResultString( "MalformedURLException", 404,  id);	
    				conn = null;
    			}
				catch( IOException _ioe) {
					RunnerJNILib.HttpResultString( "IOException", 404,  id);	
    				conn = null;
				} 
    			if (conn != null) {
    				try
    				{
    					conn.setDoInput(true);
    					conn.setDoOutput(true);
    					conn.setUseCaches(false);
    					conn.setRequestMethod("POST");
	    				
    					conn.connect();
	    				
    					byte[] postBytes = post.getBytes("UTF-8");
    					conn.getOutputStream().write(postBytes);
    					conn.getOutputStream().flush();
    					conn.getOutputStream().close();
    						    				
    					int responseCode = conn.getResponseCode();
    					Log.i("yoyo", "HttpPost: Got response code '" + responseCode + "'");
    					byte[] result = null;
    					String finalurl=null;
						try {    						
    						InputStream is = conn.getInputStream();    						
    						ByteArrayOutputStream out = new ByteArrayOutputStream();
    						byte[] buffer = new byte[ 4096 ];
    						int n = -1;    						
    						while ((n = is.read(buffer)) != -1) {    							
    							out.write(buffer, 0, n );
    						} // end while    						
    						result = out.toByteArray();
    						out.close();    						
    						finalurl = conn.getURL().toString();
    					}    					
    					catch (IOException _ioe) {
    						// e.g. a 405 response may have occurred, which is fine
    						Log.i("yoyo", "HttpPost: IO exception");
    					}

    					// Trick the runner into seeing this as a valid result whatever's happened by ensuring a true for (nDownloadOffset > 0)    					
    					if ((result == null) || (result.length == 0)) {    						
    						result = new byte[]{ '\0' };
    					}
	    					    				
    					RunnerJNILib.HttpResult( result, responseCode, id, finalurl, HttpGetHeaders(conn));	
    					
    					conn.disconnect();
    					conn = null;
    				}
    				catch( Exception _e) {
    					RunnerJNILib.HttpResultString( "IOException", 404,  id);	
    					conn = null;
    				}
    			} // end if    		 
    		 } // end run
    	}).start();
	} // end HttpPost
	
		
	public static void HttpRequest(String _url, String _method, String _headers, byte[] _post, int _id ) {
	
		Log.i( "yoyo", "HttpRequest(\""+_url+"\", \""+_method+"\", \""+_post+"\", "+_id+")");
		
		// lets run this on the main UI thread...
		final String url = _url;
		final String method = _method;
		final String headers = _headers;
		final byte[] post = _post;
		final int id = _id;		
		
    	new Thread( new Runnable() {
    		 public void run() {    		 
    			HttpURLConnection conn;
    			try 
    			{
    				URL connectURL = new URL(url);
    				conn = (HttpURLConnection)connectURL.openConnection();
    			}
    			catch( MalformedURLException _mue ) {
    				RunnerJNILib.HttpResultString("MalformedURLException", 404,  id);	
    				conn = null;
    			}
				catch( IOException _ioe) {
					RunnerJNILib.HttpResultString("IOException", 404,  id);	
    				conn = null;
				} 
    			if (conn != null) {
    				try
    				{
    					conn.setDoInput(true);
    					
    					if( (method.equals("GET")) || (method.equals("HEAD")) )
    					//if(post==null || post.isEmpty())
    					{
    						conn.setDoOutput(false);
    						Log.i( "yoyo", "Setting do output to false");
	
    					}
    					else 
    					{
    						Log.i( "yoyo", "Setting do output to true");
    						conn.setDoOutput(true); // theoretically only POST and PUT requests should have data but I'm not going to rely on it
    					}
    					conn.setUseCaches(false);
    					conn.setRequestMethod(method);
    					
						// Add set of custom headers now
						String[] kvps = headers.split("\r\n");
						for (String s : kvps) {
							String[] kvp = s.split(": ");
							if (kvp.length == 2) {
								Log.i("yoyo", "HttpRequest: Found header " + kvp[0] + ": " + kvp[1]);
								conn.setRequestProperty(kvp[0], kvp[1]);
							}
							else {
								Log.i("yoyo", "HttpRequest: Malformed header " + s);
							}
						}
												
						// Connect to the remote object				
    					conn.connect();
						
						// Add POST data if it's available once connection has been made
						if ((post != null) && (!method.equals("GET"))) 
						{
    						conn.getOutputStream().write(post);
    						conn.getOutputStream().flush();
    						conn.getOutputStream().close();
    					}    					
    					    					
    					
    					int responseCode = conn.getResponseCode();
    					Log.i("yoyo", "HttpRequest: Got response code '" + responseCode + "'");

						// Attempt to read response data
						byte[] result = null;
						InputStream is = null;
						try {    						
							is = conn.getInputStream();
						}catch(IOException _e) {;}
						
						try{
							//check error stream for response if no input stream
							if( is == null )
								is = conn.getErrorStream();
							if( is != null)
							{
    							ByteArrayOutputStream out = new ByteArrayOutputStream();
    							byte[] buffer = new byte[ 4096 ];
    							int n = -1;    						
    							while ((n = is.read(buffer)) != -1) {    							
    								out.write(buffer, 0, n );
    							} // end while    						
    							result = out.toByteArray();
    							out.close();    						
							}
    					}    					
    					catch (IOException _ioe) {
    						// e.g. a 405 response may have occurred, which is fine
    						Log.i("yoyo", "HttpRequest: IO exception:" + _ioe);
    					}
    					    					
    					// Trick the runner into seeing this as a valid result whatever's happened by ensuring a true for (nDownloadOffset > 0)    					
    					if ((result == null) || (result.length == 0)) {    						
    						result = new byte[]{ '\0' };
    					}
    					
						String finalurl = conn.getURL().toString();						
    					RunnerJNILib.HttpResult(result, responseCode, id, finalurl, HttpGetHeaders(conn));	
    					    					
    					conn.disconnect();
    					conn = null;    					
    				}
    				catch (SocketTimeoutException _se) {
   						Log.i("yoyo", "HttpRequest: request timed out");
    					RunnerJNILib.HttpResultString("HTTP request timed out", 404, id);
    					conn = null;
    				}
    				catch( Exception _e) {
   						Log.i("yoyo", "HttpRequest: exception:"+_e);
    					RunnerJNILib.HttpResultString("HTTP request exception", 404, id);
    					conn = null;
    				}
    			} // end if    		 
    		 } // end run
    	}).start();
		
	} // HttpRequest
	
	
	
 	// ----------------------------------------------------------------------------------------------------
	// ----------------------------------------------------------------------------------------------------
	@SuppressLint("InflateParams")
	public static void ShowLogin(String _defaultUsername, String _defaultPassword, int _id ) {
    	Log.i( "yoyo", "LoginDialog(\"" +_defaultUsername+ "\", \""+_defaultPassword+"\","+_id+")");
    	
    	final String sDefaultUserName = _defaultUsername;
    	final String sDefaultPassword = _defaultPassword;
    	final int idDialog = _id;
    	RunnerActivity.FocusOverride = true;
    	RunnerActivity.ViewHandler.post( new Runnable() 
    	{
    		 public void run() {
    	    		AlertDialog.Builder builder = new AlertDialog.Builder(RunnerActivity.CurrentActivity);
    	    		LayoutInflater factory = LayoutInflater.from(RunnerActivity.CurrentActivity);
    	  //  		final View textEntryView = factory.inflate( R.layout.userpasslayout, (ViewGroup)(RunnerActivity.CurrentActivity.findViewById(R.id.demogl).getParent()) );
    				final View textEntryView = factory.inflate( R.layout.userpasslayout, null );
					builder.setView(textEntryView);
					final EditText userNameEditText = (EditText)textEntryView.findViewById(R.id.username);
					final EditText passwordEditText = (EditText)textEntryView.findViewById(R.id.password);
					userNameEditText.setText( sDefaultUserName );
					passwordEditText.setText( sDefaultPassword );
    	    		builder.setCancelable(false)
    	    			.setPositiveButton( "OK", new DialogInterface.OnClickListener() {
    	    				public void onClick( DialogInterface dialog, int id ) {
    	    					RunnerJNILib.LoginResult( userNameEditText.getText().toString() + '#' + passwordEditText.getText().toString(), userNameEditText.getText().toString(), idDialog ); 
    	    				}
    	     			});
					builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							RunnerJNILib.LoginResult( "", "", idDialog );
						}
					});

    	    		AlertDialog alert  = builder.create();
    	    		alert.show();    		 
    		 }
    	});
	} // end ShowLogin
	
	public static int CheckPermission(String _permission)
	{
		Log.i("yoyo","Checking permission: " +_permission);
	
		PackageManager packMgmr = ms_context.getPackageManager();
		
		int permissionCheck = ContextCompat.checkSelfPermission(RunnerActivity.CurrentActivity,_permission);
		if(permissionCheck==PackageManager.PERMISSION_GRANTED)
		{
			Log.i("yoyo","permission granted: " +_permission);
	
			return 1;
		}
		
		boolean sshow = ActivityCompat.shouldShowRequestPermissionRationale(RunnerActivity.CurrentActivity,_permission);	
			
		if(sshow)	
		{
			Log.i("yoyo","permission denied but not prevented from asking: " +_permission);
	
			return 0;
		}
	
		Log.i("yoyo","permission denied and shouldn't be requested: " +_permission);
		return -1;
			
	}
	
	
	public static void RequestPermission(String _permission)
	{
	//	Log.i("yoyo","requesting permission:" +_permission);
	
		ActivityCompat.requestPermissions(RunnerActivity.CurrentActivity,new String[]{_permission},97);
	}
	
	@SuppressLint("MissingPermission") // permission will be added if GMS detects os_is_network_connected being used.
	public static boolean isNetworkConnected()
	{
        ConnectivityManager conMan = (ConnectivityManager) ms_context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(conMan!=null)
        {
			NetworkInfo activeNetwork = conMan.getActiveNetworkInfo();
			return (activeNetwork != null) && activeNetwork.isConnected();
		}
		return false;
	} // end isNetworkConnected

	public static void setSystemUIVisibilityFlags( int _flags)
	{
		final int flags = _flags;
		Log.i("yoyo","Calling setSystemUIVisibilityFlags");
		RunnerActivity.ViewHandler.post( new Runnable() {
			public void run(){
			
					RunnerActivity.UIVisibilityFlags = flags;
					RunnerActivity.CurrentActivity.setupUiVisibility();
					RunnerActivity.CurrentActivity.setupUiVisibilityDelayed();
			}
		});
		
	
	}

	public static void powersaveEnable( boolean _enable )
	{
		final boolean enable = _enable;
		RunnerActivity.ViewHandler.post( new Runnable() {
			public void run() {
				if (enable) {
					RunnerActivity.CurrentActivity.getWindow().clearFlags( android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON );
				} // end if
				else {
					RunnerActivity.CurrentActivity.getWindow().addFlags( android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON );
				} // end else
			}
		});
	} // end powersaveEnable

	public static void MoveTaskToBack()
	{
		RunnerActivity.CurrentActivity.runOnUiThread(new Runnable() {
	        @Override
	        public void run() {
				RunnerActivity.CurrentActivity.moveTaskToBack(true);
			}
		});
	}


	public static void analyticsEvent( String _string )
	{
		//if( RunnerActivity.Flurry )
		//{
		//	Log.i( "yoyo", "Flurry Analytics event: " + _string );
		//	FlurryAgent.logEvent( _string );
		//}
		//else if( RunnerActivity.mbGoogleAnalytics )
		//{
		//	RunnerActivity.googleAnalyticsEvent( _string );
		//}
		
	} // end analyticsEvent
	
	public static void analyticsEventExt( String _event, String[] keyValuePairs )
	{
		//if( RunnerActivity.Flurry )
		//{
		//	Log.i( "yoyo", "Flurry Analytics Extended event: " + _event );
		//	Map<String, String> params = new HashMap<String, String>();
		//	for (int n = 0; n < keyValuePairs.length; n += 2) 
		//	{
        //  		params.put(keyValuePairs[n], keyValuePairs[n+1]);
        //  		Log.i( "yoyo", keyValuePairs[n] + " " + keyValuePairs[n+1] );
    	//	}
		//	FlurryAgent.logEvent(_event, params);
		//}
		//else if( RunnerActivity.mbGoogleAnalytics )
		//{
		//	String label = keyValuePairs[0];
		//	int value = Integer.parseInt( keyValuePairs[1] );
		//	RunnerActivity.googleAnalyticsEvent( _event, label, value );
		//}
	}
	
	public static void cloudStringSave( String _data, String _desc, int _id )
	{
		//Log.i("yoyo", "cloudStringSave: " + _data + " " + _desc + " " + _id );
		if(RunnerActivity.isLoggedInGooglePlay())
    	{
    		Object [] argArray = new Object[3];	
			argArray[0] = _data;
			argArray[1] = _desc;
			argArray[2] = _id;
				
    		CallExtensionFunction("GooglePlayServicesExtension","onGSStringSave",3,argArray);
    	}
		else
			Log.i("yoyo", "cloud_string_save() called when not logged in to appropriate service" );
		
	}
	
	public static void cloudSynchronise( int _id)
	{
		//Log.i("yoyo", "cloudSynchronise" );
		//RunnerActivity.wsSynchroniseFiles();
	
		if(RunnerActivity.isLoggedInGooglePlay())
    	{
    	
    		Object [] argArray = new Object[1];	
			argArray[0] = _id;
			
				
    		CallExtensionFunction("GooglePlayServicesExtension","onGSCloudSync",1,argArray);
    	}
		else
			Log.i("yoyo", "cloudSynchronise called when not logged in to appropriate service" );
		
	}

	// ----------------------------------------------------------------------------------------------------
	// ----------------------------------------------------------------------------------------------------
    public static void DumpUsedMemory() {
    	Debug.MemoryInfo memoryInfo = new Debug.MemoryInfo();
		Debug.getMemoryInfo(memoryInfo);

		String memMessage = String.format(Locale.US,"App Memory: Pss=%.2f MB\nPrivate=%.2f MB\nShared=%.2f MB",
		memoryInfo.getTotalPss() / 1024.0,
		memoryInfo.getTotalPrivateDirty() / 1024.0,
		memoryInfo.getTotalSharedDirty() / 1024.0);

		Log.i( "yoyo", memMessage );
	} // end DumpUsedMemory

	
	// ----------------------------------------------------------------------------------------------------
	// ----------------------------------------------------------------------------------------------------
	public static Context GetApplicationContext()
	{
		return ms_context;
	}

	// ----------------------------------------------------------------------------------------------------
	// ----------------------------------------------------------------------------------------------------
	public static int GamepadsCount()
	{
		return Gamepad.DeviceCount();
	}

	public static void ClearGamepads()
	{
		Gamepad.ClearGamepads();
	}

	public static boolean GamepadConnected(int deviceIndex)
	{
		return Gamepad.DeviceConnected(deviceIndex);
	}

	public static String GamepadDescription(int deviceIndex)
	{
		return Gamepad.GetDescriptor(deviceIndex);
	}

	public static float[] GamepadAxesValues(int deviceIndex)
	{
		return Gamepad.GetAxesValues(deviceIndex);
	}

	public static float[] GamepadButtonValues(int deviceIndex)
	{
		return Gamepad.GetButtonValues(deviceIndex);
	}

	public static int GamepadGMLMapping(int deviceIndex, int inputId)
	{
		return Gamepad.GetGamepadGMLMapping(deviceIndex, inputId);
	}
	
	public static void PushLocalNotification( float fireTime, String title, String message, String data )
	{
	
		Object [] argArray = new Object[4];	
		argArray[0] = fireTime;
		argArray[1] = title;
		argArray[2] = message;
		argArray[3] = data;
	
		CallExtensionFunction("GooglePlayServicesExtension","PushLocalNotification",4,argArray);
	
		//RunnerActivity.PushLocalNotification( fireTime, title, message, data );
	}

	public static int PushGetLocalNotification( int iIndex, int dsMap )
	{
		Object [] argArray = new Object[2];	
		argArray[0] = iIndex;
		argArray[1] = dsMap;
		
		Object ret = CallExtensionFunction("GooglePlayServicesExtension","PushGetLocalNotification",2,argArray);
		
		if(ret!=null)
		{
			return (Integer)ret;
		}
		return -1;
	}
	
	public static int PushCancelLocalNotification( int iIndex )
	{	
		Object [] argArray = new Object[1];	
		argArray[0] = iIndex;
		
		Object ret = CallExtensionFunction("GooglePlayServicesExtension","pushCancelLocalNotification",1,argArray);
		
		if(ret!=null)
		{
			return (Integer)ret;
		}
		return -1;
		
	}
	
	public static void RestrictOrientation(boolean _landscape,boolean _portrait,boolean _landscapeFlipped,boolean _portraitFlipped,boolean _fromPrefs)
	{
		RunnerActivity.CurrentActivity.RestrictOrientation(_landscape,_portrait,_landscapeFlipped,_portraitFlipped,_fromPrefs);
	} 
	
	
	// ----------------------------------------------------------------------------------------------------
	// ----------------------------------------------------------------------------------------------------
	public static String GetUDID()
	{
		// Just a warning: There is a known problem with using the following call for a certain class of devices
		// See: https://groups.google.com/forum/#!topic/android-developers/U4mOUI-rRPY
		// However, I'm inclined towards feeling that approaches to the issue such as
		// the one at http://appdreams.blogspot.co.uk/2010/12/android-and-unique-phone-id.html
		// are not adequate replacements to get a supposedly "secure" value and as 
		// such I'm sticking with what this gives you, consequences be damned
		String id = Secure.getString(ms_context.getContentResolver(), Secure.ANDROID_ID);
		if (id == null) {
			id = "UDID NOT AVAILABLE";
		}
		return id;
	}

	public static void SetLaunchedFromPlayer(String _fileName, boolean _fromPlayer)
	{
		RunnerActivity.CurrentActivity.SetLaunchedFromPlayer(_fileName, _fromPlayer);
	}


	// ----------------------------------------------------------------------------------------------------
	// ----------------------------------------------------------------------------------------------------

	// Toggles the virtual keyboard on/off
	public static void VirtualKeyboardToggle(boolean _toggleOn, int _keyboardType, int _returnKeyType, int _autoCapitalizationType, 
											 boolean _predictiveTextEnabled, String _inputString)
	{
		RunnerKeyboardController controller = RunnerActivity.CurrentActivity.GetKeyboardController();
		if(controller != null)
		{
			controller.VirtualKeyboardToggle(_toggleOn, _keyboardType, _returnKeyType, _autoCapitalizationType, _predictiveTextEnabled, _inputString);
		}
	}

	// Returns the current cached status of the virtual keyboard
	public static boolean VirtualKeyboardGetStatus()
	{
		RunnerKeyboardController controller = RunnerActivity.CurrentActivity.GetKeyboardController();
		if(controller != null)
		{
			return controller.VirtualKeyboardGetStatus();
		}

		return false;
	}
	
	// Returns the cached virtual keyboard screen height
	public static int VirtualKeyboardGetHeight()
	{
		RunnerKeyboardController controller = RunnerActivity.CurrentActivity.GetKeyboardController();
		if(controller != null)
		{
			return controller.GetVirtualKeyboardHeightCached();
		}

		return 0;
	}

	// Called when the keyboard_string property is set in GMS
	public static void OnKeyboardStringSet(final String _newString)
	{
		RunnerActivity.ViewHandler.post( new Runnable() 
		{
			public void run() 
			{
				RunnerKeyboardController controller = RunnerActivity.CurrentActivity.GetKeyboardController();
				if(controller != null)
				{
					controller.SetInputString(_newString, true);
				}
			}
		});
	}
}
