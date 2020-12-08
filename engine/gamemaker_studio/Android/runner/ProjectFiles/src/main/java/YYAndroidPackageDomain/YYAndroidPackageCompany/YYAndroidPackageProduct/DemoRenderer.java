package ${YYAndroidPackageName};

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import java.lang.reflect.Field;
import java.lang.System;
import java.io.File;
import java.nio.IntBuffer;
import java.util.Locale;

import java.io.InputStream;
import java.io.IOException;

import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.GLES11Ext;
import android.opengl.GLDebugHelper;
import android.os.Environment;
import android.content.pm.PackageManager;
import android.app.Application;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.res.Configuration;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.ConfigurationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageInfo;
import android.content.pm.Signature;
import android.provider.Settings.Secure;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.Manifest;
import android.view.WindowManager;
import android.util.Log;
import android.os.Bundle;
import android.os.AsyncTask;
import java.util.ArrayList;
import java.util.List;
import android.support.v4.content.ContextCompat;
import android.support.v4.app.ActivityCompat;
//import android.graphics.Color;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.yoyogames.runner.RunnerJNILib;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;

/*
import java.security.MessageDigest;
import android.util.Base64;
import java.security.NoSuchAlgorithmException;
*/

//----------------------------------------------------------------------------------------------------
//----------------------------------------------------------------------------------------------------
//----------------------------------------------------------------------------------------------------
//----------------------------------------------------------------------------------------------------
public class DemoRenderer implements GLSurfaceView.Renderer
{
	public enum eState 
	{
		Startup,
		DownloadGameDroidInit,
		DownloadGameDroidWait,
		Splash,
		Splash2,
		APKExpansionDownload,
		InitRunner,
		WaitForDoStartup,
		WaitOnTimer,
		DoStartup,
		Process,
	};
	public static eState m_state;
	public int m_renderCount;
	private Context m_context;
	private int m_width;
	private int m_height;
	public static String m_apkFilePath;
	private String m_packageName;
	
	public static String m_saveFilesDir;

	private int m_texWidth;
	private int m_texHeight;
	private int m_texRawWidth;
	private int m_texRawHeight;
	public static final String kGameAssetsDROID = "GameAssetsDROID.zip";

	private String m_splashFilePath = "assets/splash.png";

	public static final int RUNNER_STORAGE_PERMISSION_REQUEST = 0x55;

	public boolean m_RequestedPermissions = false;


	public static int m_defaultFrameBuffer = -1;
	public static boolean ms_displayedLoadLibraryFailed = false;
	
	public int m_pausecountdown =-1;
	public boolean m_pauseRunner = false;
	private Map<String, Locale> localeMap;
	
	private long splashEndTime = 0;

	public float m_refreshRate = 60.0f;
	public static volatile Object waiterObject = null;
	public static volatile int elapsedVsyncs = -1;	
	 
	private void initCountryCodeMapping() {
		String[] countries = Locale.getISOCountries();
		localeMap = new HashMap<String, Locale>(countries.length);
		for (String country : countries) {
			Locale locale = new Locale("", country);
			localeMap.put(locale.getISO3Country().toUpperCase(Locale.US), locale);
		}
	}	
	
	private String iso3CountryCodeToIso2CountryCode(String iso3CountryCode) {
		if (localeMap != null) {
			Locale locale = localeMap.get(iso3CountryCode);
			if (locale != null) {
				return locale.getCountry();
			} // end if
			else {
				return iso3CountryCode;
			} // end else
		} // end if
		else {
			return iso3CountryCode;
		}
	}
	
	public DemoRenderer( Context _context )
	{
		waiterObject = new Object();
		m_context = _context;
		m_state = eState.Startup;
		m_renderCount = 0;
		m_packageName = m_context.getPackageName();		
	}	
	
	public InputStream getResourceAsReader( String path)
    {
		System.out.println(path);
		try{
            android.content.res.AssetManager assetManager = m_context.getResources().getAssets();
        	return assetManager.open(path);
		}
		catch(Exception ee)
		{
			System.out.println("Exception while getting Resource");			
			return null;
		}
    }	

	private int getNextPow2(int _val)
	{		
		// Handle case where number is already a power-of-two
		_val--;

		// Smear bits downwards
		_val |= _val >> 1;
		_val |= _val >> 2;
		_val |= _val >> 4;
		_val |= _val >> 8;
		_val |= _val >> 16;

		// Value should be one less than a power-of-two so add 1
		_val++;

		// Finally, handle case where _val is 0
		if (_val == 0)
			_val++;		

		return _val;
	}
	
    public int getScreenOrientation()
	{
		return m_context.getResources().getConfiguration().orientation;
	}
	
    public void onSurfaceCreated(GL10 gl, EGLConfig config) 
    {    	
		//GLDebugHelper.wrap(gl, GLDebugHelper.CONFIG_CHECK_GL_ERROR, null);
		
		
	    if ((m_state != eState.Startup) && (m_state != eState.DownloadGameDroidWait)) {
	    	Log.i("yoyo", "onSurfaceCreated() aborted on re-create, state is currently "+m_state);
	    	return;
	    }
	    
		// Try and retrieve the current framebuffer if we haven't already got it
		if (m_defaultFrameBuffer == -1)
		{
			if (gl instanceof GL11) {
	    		IntBuffer intBuffer = IntBuffer.allocate(1);
	    		gl.glGetIntegerv(GLES11Ext.GL_FRAMEBUFFER_BINDING_OES, intBuffer);
		    	
	    		m_defaultFrameBuffer = intBuffer.get(0);
	    		Log.i("yoyo", "Renderer instance is gl1.1, framebuffer object is: " + m_defaultFrameBuffer);
			}
		}
    
 		if (RunnerActivity.YoYoRunner) 
 		{
 			List<String> permissionstoreq = new ArrayList<String>();
 			
 		
 			if (android.os.Build.VERSION.SDK_INT >  android.os.Build.VERSION_CODES.JELLY_BEAN)
 			{
 				if(ContextCompat.checkSelfPermission(RunnerActivity.CurrentActivity,Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
 				{
 					permissionstoreq.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
 				}
	 			
 				if(ContextCompat.checkSelfPermission(RunnerActivity.CurrentActivity,Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
 				{
 					permissionstoreq.add(Manifest.permission.READ_EXTERNAL_STORAGE);
 				}
 			}
 		
 			
 			if(permissionstoreq.size()>0)
 			{
 				String []perms = new String[permissionstoreq.size()];
 				permissionstoreq.toArray(perms);
 				ActivityCompat.requestPermissions(RunnerActivity.CurrentActivity,perms,RUNNER_STORAGE_PERMISSION_REQUEST);
 				m_saveFilesDir = "";
 				m_RequestedPermissions=true;
 			}	
 			else
 			{
 		
    			m_saveFilesDir = Environment.getExternalStorageDirectory() + "/GMstudio";
				File studioDir = new File( m_saveFilesDir );
				studioDir.mkdir();
				m_saveFilesDir = m_saveFilesDir + '/';
			}
		}
		else
		{		
			m_saveFilesDir = m_context.getFilesDir().getAbsolutePath() + "/";
		} // end else
    	
		m_apkFilePath = null;
		ApplicationInfo appInfo = null;
		PackageManager packMgmr = m_context.getPackageManager();
		try {
			appInfo = packMgmr.getApplicationInfo("${YYAndroidPackageName}", 0);
		} 
		catch (NameNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException("Unable to locate assets, aborting...");
		} // end catch
		m_apkFilePath = appInfo.sourceDir;
		
		Log.i("yoyo", "APK File Path :: " + m_apkFilePath );
		
		// load splash screen in and bind it 
        int[] textures = new int[1];
        gl.glGenTextures(1, textures, 0);

       
        gl.glBindTexture(GL10.GL_TEXTURE_2D,  textures[0]);

        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

        //gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        //gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);

        //gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_REPLACE);
        
        ActivityManager activityMgmr = (ActivityManager)m_context.getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo info = activityMgmr.getDeviceConfigurationInfo();
        if (info.reqGlEsVersion >= 0x20000) {
        	Log.i("yoyo", "OpenGL ES-2.0 is supported: " + info.reqGlEsVersion);
        }
        else {
        	Log.i("yoyo", "OpenGL ES-CM 1.1 is supported: " + info.reqGlEsVersion);
        }
        
        // load the actual texture in
        InputStream is;
		if (getScreenOrientation() == Configuration.ORIENTATION_LANDSCAPE ) {
			is = getResourceAsReader( "splash.png" );
		} else {
			m_splashFilePath = "assets/portrait_splash.png";
			is = getResourceAsReader( "portrait_splash.png" );
		}
        Bitmap bitmap = null;
        try {
        	BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inDither=false;
            opt.inPreferredConfig = Bitmap.Config.ARGB_8888;
        	bitmap = BitmapFactory.decodeStream( is, null, opt );
        	m_texWidth = bitmap.getWidth();
        	m_texHeight = bitmap.getHeight();
        } 
        finally 
        {
        	try {
        		is.close();
        	} 
        	catch ( IOException _e ) {
        		// do nothing
        	} // end catch
        	
        }
		
		m_texRawWidth = getNextPow2(m_texWidth);
		m_texRawHeight = getNextPow2(m_texHeight);
		//Log.i("yoyo", "Splashscreen w" + m_texWidth + " h" + m_texHeight);
		//Log.i("yoyo", "Rawtex w" + m_texRawWidth + " h" + m_texRawHeight);
        Bitmap pow2Bitmap = Bitmap.createBitmap(m_texRawWidth, m_texRawHeight, Bitmap.Config.ARGB_8888);
    	GLUtils.texImage2D( GL10.GL_TEXTURE_2D, 0, pow2Bitmap, 0);
    	GLUtils.texSubImage2D(GL10.GL_TEXTURE_2D, 0, 0, 0, bitmap);
    	bitmap.recycle();

		/*if (m_context == null)
		{
			Log.i("yoyo", "Context is null!!!!!");
		}

		if (m_context.getCacheDir() == null)
		{
			Log.i("yoyo", "Context.getCacheDir() is null!!!!!");
		}

		if (m_context.getCacheDir().getAbsolutePath() == null)
		{
			Log.i("yoyo", "Context.getCacheDir().getAbsolutePath() is null!!!!!");
		}*/
    	
    	initCountryCodeMapping();
		if (RunnerJNILib.ms_loadLibraryFailed) {
			return;
		} // end if
		RunnerJNILib.SetKeyValue( 0, RunnerActivity.CurrentActivity.isTablet() ? 1 : 0, "" );
		RunnerJNILib.SetKeyValue( 1, 0, m_context.getCacheDir().getAbsolutePath() );
		RunnerJNILib.SetKeyValue( 2, 0, Locale.getDefault().getLanguage() );
		RunnerJNILib.SetKeyValue( 3, m_context.getResources().getDisplayMetrics().densityDpi, "" );
		RunnerJNILib.SetKeyValue( 4, m_context.getResources().getDisplayMetrics().densityDpi, "" );
		RunnerJNILib.SetKeyValue( 5, android.os.Build.VERSION.SDK_INT, android.os.Build.VERSION.RELEASE );
		try {
			RunnerJNILib.SetKeyValue( 8, 0, iso3CountryCodeToIso2CountryCode(Locale.getDefault().getISO3Country()) );
		}
		catch (MissingResourceException e) {
			RunnerJNILib.SetKeyValue( 8, 0, "zz" );
		}		
    }

    public void onSurfaceChanged(GL10 gl, int w, int h) 
    {    
    	m_width = w;
    	m_height = h;
        gl.glViewport(0, 0, w, h);
    	Log.i("yoyo", "onSurfaceChanged :: width="+m_width+" height="+m_height);
    }			

	public static void WaitForVsync()
	{
		long starttime = System.nanoTime();
		int currVsync = elapsedVsyncs;
		while((elapsedVsyncs != -1) && (currVsync == elapsedVsyncs))
		{
			long currtime = System.nanoTime();

			if ((currtime - starttime) > (100 * 1000 * 1000)) // tenth of a second
			{
				Log.i("yoyo", "vsync timeout...");
				break;
			}
		}

		/*synchronized(waiterObject)
		{
			while(currVsync == elapsedVsyncs)
			{
				try
				{
					waiterObject.wait();
				}
				catch (InterruptedException e)
				{
					if (currVsync == elapsedVsyncs)
					{
						Log.i("yoyo", "Thread interrupted prematurely");
					}
                    Thread.currentThread().interrupt();
				}
			}
		}*/
	}

    public void onDrawFrame(GL10 gl) {
    
		if (RunnerJNILib.ms_loadLibraryFailed) {
			if (!ms_displayedLoadLibraryFailed) {
				ms_displayedLoadLibraryFailed = true;
    			RunnerActivity.ViewHandler.post( new Runnable() {
    				 public void run() {
    	    				AlertDialog.Builder builder = new AlertDialog.Builder(RunnerJNILib.ms_context);
    	    				builder.setMessage("Unable to find library for this devices architecture, which is " + System.getProperty("os.arch") + ", ensure you have included the correct architecture in your APK")
    	    					.setCancelable(false)
    	    					.setPositiveButton( "OK", new DialogInterface.OnClickListener() {
    	    						public void onClick( DialogInterface dialog, int id ) {
										RunnerActivity.CurrentActivity.finish();
    	    						}
    	     					});
    	    				AlertDialog alert  = builder.create();
    	    				alert.show();
    				 }
    			});
			} // end if
			return;
		} // end if
    
		if(m_pausecountdown>0)
		{
			m_pausecountdown--;
			if(m_pausecountdown<=0)
			{
				m_pauseRunner = true;
				//Log.i("yoyo","Setting m_pauseRunner due to alarm countdown ("+m_pausecountdown+")");
			}
		}
    
    	// if we are paused then do go to sleep and don't do anything
    	if (m_pauseRunner) {
        	try {	
    			Thread.sleep(100);
    		} catch (InterruptedException e) {
    		    Log.i("yoyo", "Paused runner has thrown an exception!");
    		    e.printStackTrace();
    		} // end catch
    		return;
    	}
    	
    	switch( m_state ) {
    		case Startup:
				m_state = eState.Splash;
				Log.i("yoyo", "State->Splash");
	    		gl.glClearColor( 0, 0, 0, 0 );
    			gl.glClear( GL10.GL_COLOR_BUFFER_BIT );
    			break;
	    	case Splash:
	    		if (RunnerActivity.mYYPrefs==null)	    	
	    		{
	    			splashEndTime = System.currentTimeMillis() + (1000);
	    		}
	    		else
	    		{
    				splashEndTime = System.currentTimeMillis() + (RunnerActivity.mYYPrefs.getInt("SplashscreenTime")*1000);
    			}
				Log.i("yoyo", "State->Splash    time: "+System.currentTimeMillis());
				Log.i("yoyo", "State->Splash endTime: "+splashEndTime);
    			if (RunnerActivity.YoYoRunner) {
					m_state = eState.Splash2;
					Log.i("yoyo", "State->Splash2");
    			} // end if
	    		else if(RunnerActivity.UseAPKExpansionFile ) {
	    			m_state = eState.APKExpansionDownload;
	    			Log.i("yoyo", "State->APKExpansionDownload");
	    		}else {
	    			Log.i("yoyo", "State->InitRunner");
		    		m_state = eState.InitRunner;
    			} // end else
    			RunnerJNILib.RenderSplash( m_apkFilePath, m_splashFilePath, m_width, m_height, m_texRawWidth, m_texRawHeight, m_texWidth, m_texHeight );
	    		break;
    		case Splash2:
    			
    			if(!m_RequestedPermissions)
    			{
					m_state = eState.DownloadGameDroidInit;
    				RunnerJNILib.RenderSplash( m_apkFilePath, m_splashFilePath, m_width, m_height, m_texRawWidth, m_texRawHeight, m_texWidth, m_texHeight );
    			}
    			
	    		break;
    		case DownloadGameDroidInit:	    	
    			RunnerJNILib.RenderSplash( m_apkFilePath, m_splashFilePath, m_width, m_height, m_texRawWidth, m_texRawHeight, m_texWidth, m_texHeight );
    			m_apkFilePath = m_saveFilesDir + kGameAssetsDROID;
				File fAssets = new File( m_apkFilePath );
	  		    Log.i("yoyo", "!!! Asset file - " + m_apkFilePath + " " + fAssets.exists() + " l=" + fAssets.lastModified() );
				File fLock = new File( m_saveFilesDir + "GameDownload.lock" );
	  		    Log.i("yoyo", "!!! Lock file - " + fLock.getAbsolutePath() + " " + fLock.exists() + " l=" + fLock.lastModified() );
	  		    
	  		    if(RunnerActivity.CurrentActivity.HasRestarted)
	  		    {
	  				m_state = eState.InitRunner;
	  		    }
				else if (!fLock.exists() || (fLock.exists() && (fLock.lastModified() < fAssets.lastModified()))) 
				{
					m_saveFilesDir = m_context.getFilesDir().getAbsolutePath() + "/";
					m_apkFilePath = m_saveFilesDir + kGameAssetsDROID;
					
					Log.i("yoyo", "APK File Path DGDI:: " + m_apkFilePath );
	
	    			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( m_context );
		    		String ipAddress = prefs.getString( "hostIpAddress", "none" );
	    			String portNumber = prefs.getString( "hostPortNumber", "none" );
	    			
	    			
	    			Log.i("yoyo","Found ipAddress "+ ipAddress + " in prefs");
					Log.i("yoyo","Found portNumber "+ portNumber + " in prefs");
			
	    			if(ipAddress.equals("none") || portNumber.equals("none"))
	    			{
	    				//Parse the .ini file that we have to grab the port & ip address 
						Log.i("yoyo","Checking for ipaddress in inifile");
						// Just use manifest file if this is a package
						Bundle bundle = null;
						try {
							ApplicationInfo ai = m_context.getPackageManager().getApplicationInfo(RunnerActivity.CurrentActivity.getComponentName().getPackageName(), PackageManager.GET_META_DATA);
							bundle = ai.metaData;   
							
						} catch( Exception _e ) {
							Log.d( "yoyo", "Exception while setting up Ini" +_e.toString() );
						} // end catch

						if(bundle!=null)
						{	
							IniBundle lPrefs = new IniBundle( bundle, null );	
								
							ipAddress = lPrefs.getString("YYHostIP");
							
							if(ipAddress==null)
								ipAddress="none";
							
							
							int iportNumber = lPrefs.getInt("YYHostPort");
							if(iportNumber!=0)
								portNumber = Integer.toString(iportNumber);
							else
								portNumber = "none";
							Log.i("yoyo","Found ipAddress "+ ipAddress + " in our manifest");
							Log.i("yoyo","Found portNumber "+ portNumber + " in our manifest");
						}
						else
							Log.d("yoyo","Bundle not found");
		
	    			} 
	    			
	    			
	    			final String url = "http://" + ipAddress + ":" + portNumber + "/" + kGameAssetsDROID;
	    			RunnerActivity.DownloadTaskStatus = DownloadStatus.NotConnected;
	    			m_state = eState.DownloadGameDroidWait;
					RunnerActivity.ViewHandler.post( new Runnable() 
					{
						public void run() {
						
						
							boolean needstart = false;
							if(RunnerActivity.DownloadTask == null)
							{
								RunnerActivity.DownloadTask = new RunnerDownloadTask();
								needstart = true;
							}
							Log.i("yoyo","Attempting to download " + url + " to " +m_apkFilePath);
							RunnerActivity.DownloadTask.TargetURL = url;
							RunnerActivity.DownloadTask.DestPath = m_apkFilePath;
							
							if((RunnerActivity.DownloadTask.getStatus()!= AsyncTask.Status.RUNNING) && (RunnerActivity.DownloadTask.getStatus()!= AsyncTask.Status.FINISHED))
							{
								needstart = true;
							}
								
							if(needstart)
							{
								RunnerActivity.DownloadTask.execute( url, m_apkFilePath  );
			    				Log.i("yoyo","Done execute downloadtask");
			    			}
			    			
			    	
						}
					 });
				} // end if
				else {
		  		    Log.i("yoyo", "GameDownload.lock exists, about to delete...");
					fLock.delete();
					
					Log.i("yoyo","After delete flock.exists() returns "+fLock.exists());
	    			m_state = eState.InitRunner;
				} // end else
    			break;
	    	case DownloadGameDroidWait:	    	
    			RunnerJNILib.RenderSplash( m_apkFilePath, m_splashFilePath, m_width, m_height, m_texRawWidth, m_texRawHeight, m_texWidth, m_texHeight );
    			
    			
				switch( RunnerActivity.DownloadTaskStatus ) {
				case Error:
				case SettingsChanged:
    				m_state = eState.DownloadGameDroidInit;
    				Log.i("yoyo","In state DownloadGameDroidWait going to DownloadGameDroidInit");
    				break;
	    		case Complete:
	    			m_state = eState.InitRunner;
	    			Log.i("yoyo","In state DownloadGameDroidWait going to InitRunner");
    	
    				break;
    			} // end switch
    			break;
    		
    		case APKExpansionDownload:
    			//Log.i("yoyo", "APKExpansionDownload...");
    			//where is apkFilePath pointing to here...? splash should be assets regardless...i think...
    			RunnerJNILib.RenderSplash( m_apkFilePath, m_splashFilePath, m_width, m_height, m_texRawWidth, m_texRawHeight, m_texWidth, m_texHeight );
    			if( RunnerActivity.APKExpansionFileReady ) 
    			{
    				//set the new apk path
    				//m_apkFilePath = RunnerActivity.CurrentActivity.GetExpansionAPKFilename();
    				
    				Object apkFilePath = RunnerJNILib.CallExtensionFunction("PlayAPKExpansionExtension","GetExpansionAPKFilename",0,null);
    				m_apkFilePath = (String)apkFilePath;
    				
    				Log.i("yoyo", "Download complete- path is:" + m_apkFilePath );
    				m_state = eState.InitRunner;
    			}
    			break;
    			
    		case InitRunner:
    			RunnerJNILib.RenderSplash( m_apkFilePath, m_splashFilePath, m_width, m_height, m_texRawWidth, m_texRawHeight, m_texWidth, m_texHeight );
	    		m_state = eState.WaitForDoStartup;
				RunnerActivity.ViewHandler.post( new Runnable() {
					public void run() {
						RunnerActivity.CurrentActivity.doSetup( m_apkFilePath );
					}
	    		});
    			break;
    			
			case WaitForDoStartup:
    			RunnerJNILib.RenderSplash( m_apkFilePath, m_splashFilePath, m_width, m_height, m_texRawWidth, m_texRawHeight, m_texWidth, m_texHeight );
				break;
    			
    		case WaitOnTimer:
    			RunnerJNILib.RenderSplash( m_apkFilePath, m_splashFilePath, m_width, m_height, m_texRawWidth, m_texRawHeight, m_texWidth, m_texHeight );
    			long time = System.currentTimeMillis();
				if ( time >= splashEndTime )
    				m_state = eState.DoStartup;
    			break;
    			
			case DoStartup:
	    		// free the textures
    			int[] textures = new int[1];
    			gl.glDeleteTextures(1, textures, 0);
	    		if (RunnerActivity.mYYPrefs==null)	    	
						RunnerJNILib.Startup(m_apkFilePath, m_saveFilesDir, m_packageName, 0);
	    		else
				{
						Log.i("yoyo","Sleepmargin: " + (RunnerActivity.mYYPrefs.getInt("SleepMargin")));
						RunnerJNILib.Startup(m_apkFilePath, m_saveFilesDir, m_packageName, (RunnerActivity.mYYPrefs.getInt("SleepMargin")));
				}
	    		m_state = eState.Process;
    			break;
    			
    		case Process:
				//gl.glClearColor( 0, 0, 0, 0 );
    			//gl.glClear( GL10.GL_COLOR_BUFFER_BIT|GL10.GL_DEPTH_BUFFER_BIT|GL10.GL_STENCIL_BUFFER_BIT );
				if(!RunnerJNILib.ms_exitcalled)
				{
    				int keypadStatus = 0;
	    			if (RunnerActivity.XPeriaPlay && (m_context.getResources().getConfiguration().navigation == 2) &&  
    					(m_context.getResources().getConfiguration().navigationHidden == 1)) {
    					keypadStatus = 1;
    				} // end if
					do{
	    				//Log.i("yoyo","keypad status = " +  m_context.getResources().getConfiguration().navigationHidden + " nav = " + m_context.getResources().getConfiguration().navigation + " status = " + keypadStatus);
						m_refreshRate = RunnerActivity.CurrentActivity.getRefreshRate();
						//Log.i("yoyo", "refreshrate = "+m_refreshRate);
		    			
    					int ret = RunnerJNILib.Process(m_width, m_height, RunnerActivity.AccelX, RunnerActivity.AccelY, RunnerActivity.AccelZ, keypadStatus, RunnerActivity.Orientation, m_refreshRate );
    					if(ret ==0 )
    					{            	
    						Log.i("yoyo","RunnerJNILib.Process returned 0");
        	    			RunnerJNILib.ExitApplication();
            			} 
            			else if(ret == 2)
            			{
            				Log.i("yoyo","RunnerJNILib.Process has returned that it is due to restart");
            				m_state = eState.Startup;
            				RunnerActivity.CurrentActivity.HasRestarted = true;
            			}
						//Log.i("yoyo", "GameDownload.lock exists...");
					} while( RunnerJNILib.canFlip() == false && m_state == eState.Process);					
				}
	    		break;
    	}
        m_renderCount--;
	}
}
