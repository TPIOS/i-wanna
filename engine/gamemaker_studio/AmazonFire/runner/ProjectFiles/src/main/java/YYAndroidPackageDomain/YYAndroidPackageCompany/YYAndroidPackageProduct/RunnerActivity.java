package ${YYAndroidPackageName};

import android.app.Application;
import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.BroadcastReceiver;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.util.Log;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.view.InputDevice;
import android.view.WindowManager;
import android.view.Surface;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;
import android.widget.EditText;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothAdapter;
import android.content.IntentFilter;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;

import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.EnumSet;

import ${YYAndroidPackageName}.RunnerKeyboardController;
import ${YYAndroidPackageName}.DemoRenderer;
import com.yoyogames.runner.RunnerJNILib;

import java.io.IOException;
import java.io.FilenameFilter;

import android.view.SurfaceHolder;

//import com.inmobi.adtracker.androidsdk.IMAdTracker;
//import com.inmobi.commons.IMCommonUtil;

import ${YYAndroidPackageName}.Gamepad;
import ${YYAndroidPackageName}.RunnerVsyncHandler;

import org.ini4j.Ini;

//----------------------------------------------------------------------------------------------------
public class RunnerActivity extends FragmentActivity implements SensorEventListener, SurfaceHolder.Callback 
{
	// The Singleton for RunnerActivity
	public static RunnerActivity CurrentActivity;

	public static final String BASE64_PUBLIC_KEY = "${YYAndroidGoogleLicensingPublicKey}";
    public static final byte[] SALT = new byte[] { -5, 12, -68, 7, -12, 67, 3, 4, 4, 19, 6, 7, 16, 11, 9, 51, 71, 34, 19, 16 };

	public static int UIVisibilityFlags = 0x1706;

    public static final int DIALOG_CANNOT_CONNECT_ID = 1;
    public static final int DIALOG_BILLING_NOT_SUPPORTED_ID = 2;
    public static final int DIALOG_BILLING_PURCHASE_ERROR = 3;
    public static final int PREFERENCES_GROUP_ID = 1;
    public static final int SETTINGS_ID = 1;
    public static final int EXIT_ID = 2;
    
	public static RunnerDownloadTask DownloadTask;
	public static DownloadStatus DownloadTaskStatus;

	public static boolean UseAPKExpansionFile = false;	//APK Expansion file to download
	public static boolean APKExpansionFileReady = false;

    public static String InputStringResult;
	public static int  ShowQuestionYesNo; 
	
	public static boolean HasRestarted = false; //for the Player to flag we're re-starting
	public static boolean LaunchedFromPlayer = false; // true if the activity was launched via yyg_player_run
	public static String LaunchedFromPlayerGameFile = "";
	
    public static float AccelX;
    public static float AccelY;
    public static float AccelZ;
    public static int Orientation;
    public static int DefaultOrientation;
    public static int ConfigOrientation;
	public static String m_versionName;
    
    public static Handler ViewHandler;// = new Handler();	//unsafe- static initialiser called when class loaded- not guaranteed to be loaded in main thread
    public static int DisplayWidth;
    public static int DisplayHeight;
    public static boolean XPeriaPlay = false;
    public static boolean YoYoRunner = false;
    public static String SaveFilesDir = null;
    public static boolean FocusOverride  = false;  
	public static boolean HasFocus = false;
    
	public static IniBundle mYYPrefs;    
	public static int AllowedOrientationMask = 0xf;    
		
  
	//public static ISocial mSocial;
	public static Object[] mExtension;
  
	public boolean mbAppSuspended = false;
	
		
	private static IRunnerBilling mRunnerBilling;
	public static IRunnerBilling RunnerBilling() {

		if (mRunnerBilling != null) {
			return mRunnerBilling;
		}
		else {
			Log.i("yoyo", "BILLING: Unsupported or not activated. Check Global Game Settings.");
			throw new NullPointerException();
		}
	}
    
 
    
	
    private DemoGLSurfaceView mGLView;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;

    private static Method mSetSystemUiVisibility = null;
    
    private Handler mHandler = new Handler();

	public Object vsyncHandler = null;

	private RunnerKeyboardController m_keyboardController = null;

	public DemoGLSurfaceView GetGLView(RunnerVsyncHandler.Accessor a)
	{
		a.hashCode();		// only the RunnerVsyncHandler class can call this function (nice 'friend class' trick from here https://stackoverflow.com/questions/182278/is-there-a-way-to-simulate-the-c-friend-concept-in-java)

		return mGLView;
	}
    
    private Runnable mUpdateTimerTask = new Runnable() {
    	public void run() {
    	}
    };
    
    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) 
    {
        // Write your code here
 
		if(RunnerActivity.mExtension!=null)
		{
			for(int i=0;i<RunnerActivity.mExtension.length;i++)
			{
				if(RunnerActivity.mExtension[i] instanceof IExtensionBase)
				{
					boolean consumed =((IExtensionBase)RunnerActivity.mExtension[i]).onKeyLongPress(keyCode,event);
					if(consumed)
						return consumed;	
				}
			}
		}
		
 
        if (keyCode == KeyEvent.KEYCODE_BACK) 
		{
			RunnerJNILib.BackKeyLongPressEvent();
			//Log.i("yoyo", "Long back press!");
        
			return true; //signal that we have consumed this press
		}
		return super.onKeyLongPress(keyCode, event);
    }    
    public static boolean isLoggedInGooglePlay()
    {
		boolean retval = false;
    
		Object ret = RunnerJNILib.CallExtensionFunction("GooglePlayServicesExtension","isSignedIn",0,null);	
	
		if(ret!=null)
			retval = (Boolean)ret;	
    
		return retval;
    }
    
    public static void ach_login()
    {
		if(RunnerActivity.mYYPrefs!=null)
		{
			if(RunnerActivity.mExtension!=null)
			{
				for(int i=0;i<RunnerActivity.mExtension.length;i++)
				{
					if(RunnerActivity.mExtension[i] instanceof ISocial)
						((ISocial)RunnerActivity.mExtension[i]).Login();
				}
			}
		}
		else
			Log.i("yoyo","login called when prefs null");
		
    }
    
   
   
    
    public boolean isTablet() {
    	try {
    		DisplayMetrics dm = getResources().getDisplayMetrics();
    		float screenWidth = dm.widthPixels / dm.xdpi;
    		float screenHeight = dm.heightPixels / dm.ydpi;
    		double size = Math.sqrt( (screenWidth*screenWidth) + (screenHeight*screenHeight) );
    		return (size >= 6);
    	} catch( Throwable _t ) {
    		Log.i( "yoyo", "Failed to compute screen size" );
    		return false;
    	} // end catch
    } // end isTablet
    
    
    //The BroadcastReceiver that listens for bluetooth broadcasts
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

	//	Log.i("yoyo","BluetoothDevice Receiver for device " + device.getAddress() + " receieved " + action);

        if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
          
          Log.i("yoyo","Bluetooth device connected");
          Gamepad.EnumerateDevices(mYYPrefs);	
          Gamepad.BluetoothDeviceConnected(device);
        }
        else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
         
          Log.i("yoyo","Bluetooth device disconnected");
          Gamepad.EnumerateDevices(mYYPrefs);	
          Gamepad.BluetoothDeviceDisconnected(device);
        }   
    }
	};
	
	
	/** Called when the activity is first created. */
	@TargetApi(11)
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
		this.requestWindowFeature( Window.FEATURE_NO_TITLE );
    	ViewHandler = new Handler();
    	Log.i("yoyo", "onCreate");
        super.onCreate(savedInstanceState);
         
        
		RunnerJNILib.Init( this );
        
        // check to see if we are the YoYoRunner or not.
        YoYoRunner = checkIsYoYoRunner();		
		DownloadTask = new RunnerDownloadTask();        

		try
		{
			m_versionName = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
		}
		catch (NameNotFoundException e)
		{
			m_versionName = "1.0.xxx";
			Log.v("yoyo", e.getMessage());
		}
		RunnerJNILib.ms_versionName = m_versionName;
        Log.i( "yoyo", "###@@@@!!!~~~~###### versionName - " + m_versionName);

		// set the current orientation
		switch( getResources().getConfiguration().orientation ) {
		default:
		case Configuration.ORIENTATION_LANDSCAPE:
		case Configuration.ORIENTATION_SQUARE:
			Orientation = 0;
			break;
		case Configuration.ORIENTATION_PORTRAIT:
			Orientation = 1;
			break;
		} // end switch
        
        // Grab singleton
        CurrentActivity = this;
        
        // Grab the display settings for the game
    	Display display = getWindowManager().getDefaultDisplay();
    	DisplayWidth = display.getWidth();
    	DisplayHeight = display.getHeight();

       // this.requestWindowFeature( Window.FEATURE_ACTION_BAR);

		// Grab the sensor manager and accelerometer        
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER); 
        
        DefaultOrientation  = getDeviceDefaultOrientation();
        Log.i( "yoyo", "###@@@@!!!~~~~###### default orientation - " + DefaultOrientation);
        
        setupIniFile();
        //Moved this here as we need to initialise the extensions before checking for apk expansion...
        setupExtensions();
        
        
        
        
        // Setup the various things needed
        checkXPeriaPlay();
        //checkLicensing();
        
        RunnerJNILib.CallExtensionFunction("GooglePlayLicensingAsExt","checkLicensing",0,null);
        
        //setupView();	
       
        
        //start APK expansion download if required
        Log.i( "yoyo", "!!!!!!! Checking if APK Expansion file required...");
        boolean bDownloadView = false;
		if( UseAPKExpansionFile )
		{
			Object downloadViewNeeded = RunnerJNILib.CallExtensionFunction("PlayAPKExpansionExtension","StartAPKExpansionDownload",0,null);
			if(downloadViewNeeded!=null)
			{
				bDownloadView = (Boolean)downloadViewNeeded;
			}
			
			//bDownloadView = StartAPKExpansionDownload();
		}	
		if(bDownloadView && !HasRestarted) 
		{
			//setupDownloadView();
			RunnerJNILib.CallExtensionFunction("PlayAPKExpansionExtension","setupDownloadView",0,null);
		}	
		else 
		{
			setupView();	
		}
		
		IntentFilter filter1 = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
		//IntentFilter filter2 = new IntentFilter(BluetoothDevice.ACTION_CONNECTION_STATE_CHANGED);
		IntentFilter filter3 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
		this.registerReceiver(mReceiver, filter1);
		//this.registerReceiver(mReceiver, filter2);
		this.registerReceiver(mReceiver, filter3);
		
		if (android.os.Build.VERSION.SDK_INT >= 19 /*android.os.Build.VERSION_CODES.KITKAT*/) 
		{
		 final View decorView = getWindow().getDecorView();
            decorView.setOnSystemUiVisibilityChangeListener (new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) 
            {
				//Log.i("yoyo","onSystemUiVisibilityChange called with "+ visibility);
				setupUiVisibility();
    			setupUiVisibilityDelayed();
    			
            }
        });
		}

		if (android.os.Build.VERSION.SDK_INT >= 16)
		{
			Log.i( "yoyo", "!!!!!!! Using frame count timing if possible...");
			vsyncHandler = new RunnerVsyncHandler();
		}

		// Initialise virtual keyboard controller
		RunnerKeyboardController keyboardController = GetKeyboardController();
		if(keyboardController != null)
		{
			// Update initial physical keyboard status
			boolean physicalKeyboardConnected = (getResources().getConfiguration().hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO ? true : false);
			keyboardController.SetPhysicalKeyboardConnected(physicalKeyboardConnected);
		}
    }
    
    public IniBundle DoSetupIniFile( String _apkFilename )
    {
		Log.i("yoyo", "File Path for INI:: " + _apkFilename );

		// Now load the ini file
		InputStream is = null;
		Bundle bundle = null;
		try {
			ApplicationInfo ai = getPackageManager().getApplicationInfo(this.getComponentName().getPackageName(), PackageManager.GET_META_DATA);
			bundle = ai.metaData;        	 

			ZipFile zip = new ZipFile( _apkFilename );
			Enumeration<? extends ZipEntry > zipEntries = zip.entries();
			
			while( zipEntries.hasMoreElements() ) {
				ZipEntry entry = (ZipEntry)zipEntries.nextElement();

				if (entry.getName().indexOf(".ini") > 0) {
					Log.d( "yoyo", "found INI file - " + entry.getName() );
					is = zip.getInputStream( entry );
					break;
				} // end if

			} // end while

		} catch( Exception _e ) {
			Log.d( "yoyo", "Exception while setting up Ini" +_e.toString() );
		} // end catch
		
		return new IniBundle( bundle, is );
    } // end SetupIniFile

	public void setupIniFile()
	{		
		// If we've launched a game from yyg_player_run, try load its INI file from the play directory
		// The game INI will only overwrite a limited number of relevant properties from the original INI bundle (e.g. orientation)
		if(LaunchedFromPlayer && mYYPrefs != null)
		{
			String configFilePath = LaunchedFromPlayerGameFile;
			int lastIndexForwardSlash = LaunchedFromPlayerGameFile.lastIndexOf("/");
			int lastIndexBackwardSlash = LaunchedFromPlayerGameFile.lastIndexOf("\\");

			configFilePath = configFilePath.substring(0, Math.max(lastIndexForwardSlash, lastIndexBackwardSlash));
			configFilePath += "/options.ini";
			File configFile = new File(configFilePath);

			if(configFile.exists())
			{
				Log.i("yoyo", "Loading game INI file from yyg_player_run directory: "+configFilePath);
				try
				{
					InputStream configInputStream = new FileInputStream(configFile);
					Ini gameIni = new Ini( configInputStream );
					Ini.Section gameIniAndroidSection = gameIni.get("Android");

					if(gameIniAndroidSection != null)
					{
						if(mYYPrefs.hasAndroidIni() == false)
						{
							// If the current config has no ini, set our android section up
							Log.i("yoyo", "Setting up new android INI section from game INI.");
							Ini androidIni = new Ini();
							androidIni.put("Android", "OrientLandscape", -1);
							androidIni.put("Android", "OrientPortrait", -1);
							androidIni.put("Android", "OrientLandscapeFlipped", -1);
							androidIni.put("Android", "OrientPortraitFlipped", -1);
							mYYPrefs.setAndroidIni(androidIni.get("Android"));
						}

						// Overwrite existing INI orientation settings
						// TODO: Overwrite any other game-specific settings for games launched via yyg_player_run here
						mYYPrefs.setAndroidString("OrientLandscape", gameIniAndroidSection.get("OrientLandscape"));
						mYYPrefs.setAndroidString("OrientPortrait", gameIniAndroidSection.get("OrientPortrait"));
						mYYPrefs.setAndroidString("OrientLandscapeFlipped", gameIniAndroidSection.get("OrientLandscapeFlipped"));
						mYYPrefs.setAndroidString("OrientPortraitFlipped", gameIniAndroidSection.get("OrientPortraitFlipped"));
					}

					configInputStream.close();
				} 
				catch(Exception _e)
				{
					Log.i("yoyo", "Exception while trying to load game INI file in yyg_player_run: " + _e.toString());
				}
			}
			else
			{
				Log.i("yoyo", "Could not locate game INI file in yyg_player_run directory: "+configFilePath);	
			}
		}
		else
		{
		// Tidier version
		if (YoYoRunner)
		{
			// Get file path for where things would be if we loaded them from USB
			String apkFilePath = null;
			String saveFilesDir = null;

			saveFilesDir = Environment.getExternalStorageDirectory() + "/GMstudio";			
			saveFilesDir = saveFilesDir + '/';

			apkFilePath = saveFilesDir + DemoRenderer.kGameAssetsDROID;	
			
			// Now test to see if the lock file exists in this location - if it doesn't the data is either not there or is stale
			File fAssets = new File( apkFilePath );
			//Log.i("yoyo", "!!! Asset file - " + m_apkFilePath + " " + fAssets.exists() + " l=" + fAssets.lastModified() );
			File fLock = new File( saveFilesDir + "GameDownload.lock" );
			//Log.i("yoyo", "!!! Lock file - " + fLock.getAbsolutePath() + " " + fLock.exists() + " l=" + fLock.lastModified() );
			if (!fLock.exists() || (fLock.exists() && (fLock.lastModified() < fAssets.lastModified())))
			{
				// I think if it gets to this point then the Runner is going to try and retrieve the asset zip via wifi
				// and therefore there's nothing we can do (we don't have it at this point)
				Log.i("yoyo", "Don't have up-to-date INI file at this point");
			}
			else
			{
				mYYPrefs = DoSetupIniFile( apkFilePath );
				Log.i("yoyo", "INI loaded" );
			}				
		}
		else
		{
    		String packageName = this.getComponentName().getPackageName();
    		Log.i( "yoyo", "#####!!!! package name is " + packageName );

    		if (packageName.equals( "com.yoyogames.runner")) {
				try {
					ApplicationInfo ai = getPackageManager().getApplicationInfo(this.getComponentName().getPackageName(), PackageManager.GET_META_DATA);
					mYYPrefs = DoSetupIniFile( ai.sourceDir );					
					Log.i("yoyo", "INI loaded from assets/Options.INI" );
				} catch( Exception _e ) {
					Log.d( "yoyo", "Exception while setting up Ini" +_e.toString() );
				} // end catch
    		} // end if
    		else {
				// Just use manifest file if this is a package
				Log.i("yoyo", "Loading INI from manifest file" );
				Bundle bundle = null;
				try {
					ApplicationInfo ai = getPackageManager().getApplicationInfo(this.getComponentName().getPackageName(), PackageManager.GET_META_DATA);
					bundle = ai.metaData;   
					
				} catch( Exception _e ) {
					Log.d( "yoyo", "Exception while setting up Ini" +_e.toString() );
				} // end catch

				mYYPrefs = new IniBundle( bundle, null );				
				Log.i("yoyo", "INI loaded from AndroidManifest.xml" );
			} // end else
		}
	}
	}

	public void doSetup( String _zipName )
	{
		Log.d( "yoyo", "doSetup called - " + _zipName );

		// If we haven't successfully loaded the INI file, try again here
		// This should only be the case if this is the Runner and it's pulling the data across wifi
		if (mYYPrefs == null)
		{
			InputStream is = null;
			Bundle bundle = null;
			try {
				ApplicationInfo ai = getPackageManager().getApplicationInfo(this.getComponentName().getPackageName(), PackageManager.GET_META_DATA);
    			bundle = ai.metaData;        	 

				ZipFile zip = new ZipFile( _zipName );
				Enumeration<? extends ZipEntry > zipEntries = zip.entries();
			
				while( zipEntries.hasMoreElements() ) {
					ZipEntry entry = (ZipEntry)zipEntries.nextElement();

					if (entry.getName().indexOf(".ini") > 0) {
						Log.d( "yoyo", "found INI file - " + entry.getName() );
						is = zip.getInputStream( entry );
						break;
					} // end if

				} // end while

			} catch( Exception _e ) {
				Log.d( "yoyo", "Exception while setting up Ini" +_e.toString() );
			} // end catch
			mYYPrefs = new IniBundle( bundle, is );
		}

		// RK :: moved this line so that AmazonGameCircle is available if testing over Wifi...
	//	if (RunnerActivity.mYYPrefs != null) {
	//		//RunnerJNILib.SetKeyValue( 6, RunnerActivity.mYYPrefs.getInt("YYAmazonGameCircle"), "" );
			
	//		boolean gp_set = RunnerActivity.mYYPrefs.getBoolean("YYAndroidGooglePlay");
			
	//		RunnerJNILib.SetKeyValue( 7,gp_set?1:0, "" );
			
		
	//		//Tell 
			
	//	} // end if
	    
	    setupExtensions();
	    setupAdvertising(); 
		
        setupInAppBilling();
        //setupVerizon();
        //setupAmazonGameCircle();
        
        
        
        setupPushNotifications();
		Gamepad.EnumerateDevices(mYYPrefs);
		
		RestrictOrientation(false,false,false,false,true);

		DemoRenderer.m_state = DemoRenderer.eState.WaitOnTimer;
	} // end doSetup

	public void setupPushNotifications()
	{
		Log.i("yoyo", "-----setup Push------");
		boolean bPushEnable = mYYPrefs.keyExists("EnablePushNotification");
		if( bPushEnable )
		{
			String pushID = mYYPrefs.getString("PushNotificationID");
			if( pushID != null )
			{
				//we add "pid" to the front of pushID to make sure it is a string type
				if( pushID.startsWith("pid"))
				{
					pushID = pushID.replaceFirst("pid", "");
				}
			
				Object [] argArray = new Object[1];	
				argArray[0] = pushID;
				Log.i("yoyo", "Push notifications enabled with senderID=" + pushID );
				RunnerJNILib.CallExtensionFunction("GooglePlayServicesExtension","setupPushNotifications",1,argArray);
			}
		}
	}


    
    public void RestrictOrientation(boolean _landscape,boolean _portrait,boolean _landscapeFlipped,boolean _portraitFlipped,boolean _fromPrefs ) 
	{
		if(_fromPrefs)
		{
    		_landscape = false;
    		_portrait = false;
    		_landscapeFlipped = false;
    		_portraitFlipped = false;
    		
    		if( mYYPrefs != null  )
    		{
    			Log.i( "yoyo", "RestrictOrientation setting from YYPrefs");	//remove me
    			_landscape = ( mYYPrefs.getInt("OrientLandscape") != 0 );
    			_portrait = ( mYYPrefs.getInt("OrientPortrait") != 0 );
    			_landscapeFlipped = ( mYYPrefs.getInt("OrientLandscapeFlipped") !=0 );
    			_portraitFlipped = ( mYYPrefs.getInt("OrientPortraitFlipped") !=0 );
    		}
    	}
    	
    
		Log.i( "yoyo", "RestrictOrientation(\"" +_landscape+ "\", \""+_portrait+"\""+_landscapeFlipped+"\""+_portraitFlipped+"\")");
    	
		AllowedOrientationMask = (_landscape ? 1 : 0) | (_portrait ? 2 : 0) | (_landscapeFlipped ? 4 : 0) | (_portraitFlipped ? 8 : 0);
    	
    	
    	if (_landscape && !_portrait && !_landscapeFlipped && !_portraitFlipped) {
    		setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE );
    	} // end if
    	else
    	if (!_landscape && _portrait && !_landscapeFlipped && !_portraitFlipped) {
    		setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT );
    	} // end if
    	else
    	if (!_landscape && !_portrait && _landscapeFlipped && !_portraitFlipped) {
    		setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE );
    	} // end if
    	else
    	if (!_landscape && !_portrait && !_landscapeFlipped && _portraitFlipped) {
    		setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT );
    	} // end if
    	else
    	if  ((_landscape && !_portrait && _landscapeFlipped && !_portraitFlipped) || 
    		 (_landscape && _portrait && _landscapeFlipped && !_portraitFlipped) || 
    		 (_landscape && !_portrait && _landscapeFlipped && _portraitFlipped)
    		){
    		setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE );
    	} // end if
    	else
    	if ((!_landscape && _portrait && !_landscapeFlipped && _portraitFlipped) ||
    		(_landscape && _portrait && !_landscapeFlipped && _portraitFlipped) ||
    		(!_landscape && _portrait && _landscapeFlipped && _portraitFlipped)
    		){
    		setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT );
    	} // end if
    	else {
    		setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED );
    	} // end else
	} // end RestrictOrientation
    
    /* 
     * Called when the activity is being started up 
     */
    @Override
    protected void onStart() {    

    	Log.i("yoyo", "onStart");
        super.onStart();
                
        // registerUnlockReceiver();
        
        //if (Flurry) {
	    //    Log.i( "yoyo", "@@@@@@@ Flurry session started code = " + FlurryCode);
		//	FlurryAgent.setReportLocation(false);
		//	FlurryAgent.setLogEvents(true);
        //	FlurryAgent.onStartSession( this, FlurryCode );
        //} // end if
		//if(mbGoogleAnalytics)
		//{
		//	mGaTracker.sendView("/GameStart");
		//}
		if(RunnerActivity.mExtension!=null)
		{
			for(int i=0;i<RunnerActivity.mExtension.length;i++)
  			{	 
				if(RunnerActivity.mExtension[i] instanceof IExtensionBase )
					((IExtensionBase)RunnerActivity.mExtension[i]).onStart();
				//if(RunnerActivity.mExtension[i] instanceof ISocial )
				//	((ISocial)RunnerActivity.mExtension[i]).onStart();
				//else if(RunnerActivity.mExtension[i] instanceof IAdExt)
				//	((IAdExt)RunnerActivity.mExtension[i]).onStart();
			}
		}
    }
    
    @Override
    protected void onRestart()
    {
		Log.i("yoyo","onRestart");
		super.onRestart();
		
		if (RunnerActivity.mExtension != null) {
			for(int i=0;i<RunnerActivity.mExtension.length;i++)
  			{	 
				if(RunnerActivity.mExtension[i] instanceof IExtensionBase )
					((IExtensionBase)RunnerActivity.mExtension[i]).onRestart();
				//if(RunnerActivity.mExtension[i] instanceof ISocial)
				//	((ISocial)RunnerActivity.mExtension[i]).onRestart();
				//else if(RunnerActivity.mExtension[i] instanceof IAdExt)
				//		((IAdExt)RunnerActivity.mExtension[i]).onRestart();
			}
		} // end if
    }
    
    /**
     * Called when this activity is no longer visible.
     */
    @Override
    protected void onStop() {
    
    	Log.i("yoyo", "onStop");
    	
    	if(mGLView!=null) //Added some protection as this can be stopped by apk expansion downloading before the view has been setup
    	{
    		if(mGLView.mRenderer!=null)
    		{
    			if(mGLView.mRenderer.m_pausecountdown>0)
				{
					mGLView.mRenderer.m_pausecountdown=0;
					mGLView.mRenderer.m_pauseRunner = true;
				}
			}
		}
    	
    
    	
        super.onStop();		
        
		// Not doing this now as onStop() occurs when going off to the Market app and we need to
		// still be listening for a response from the market even whilst stopped or it may send its
		// response before our application has been fully reawoken
        // if (checkCallingOrSelfPermission("com.android.vending.BILLING")==0) {        	
	    //     RunnerBillingResponseHandler.unregister(mBillingPurchaseObserver);	        	        
	    // } 
	    
        //if (Flurry) {
	    //    Log.i( "yoyo", "@@@@@@@ Flurry session stopped code = " + FlurryCode);
        //	FlurryAgent.onEndSession( this );
        //} // end if
        //if(mbGoogleAnalytics)
		//{
		//	// We send an empty event so we get accurate time-on-page/site info.
		//	mGaTracker.sendEvent("", "", "", null);
		//	//do a manual dispatch to make sure all events are sent...
		//	GAServiceManager.getInstance().dispatch();
		//}
		
		if(RunnerActivity.mExtension!=null)
		{
			for(int i=0;i<RunnerActivity.mExtension.length;i++)
  			{	 
				if(RunnerActivity.mExtension[i] instanceof IExtensionBase )
					((IExtensionBase)RunnerActivity.mExtension[i]).onStop();
				//if(RunnerActivity.mExtension[i] instanceof ISocial)
				//	((ISocial)RunnerActivity.mExtension[i]).onStop();
				//else if(RunnerActivity.mExtension[i] instanceof IAdExt)
				//	((IAdExt)RunnerActivity.mExtension[i]).onStop();
			}
		}

		// Hide virtual keyboard if it's been shown
		if(m_keyboardController != null && m_keyboardController.GetVirtualKeyboardVisible() == true)
		{
			m_keyboardController.VirtualKeyboardHide();
		}
    }
    
    
    /**
     * Called when this activity is being destroyed
     */
    @Override
    protected void onDestroy() {
    
    	Log.i("yoyo", "onDestroy");
        super.onDestroy();
        
 
		if (mRunnerBilling != null) {
			mRunnerBilling.Destroy();
		}
	    
	    //if( mbGoogleAnalytics )
	    //{
		//	//GATracker.stopSession();
		//	mGaInstance.closeTracker(mGaTracker);
	    //}
	    if(RunnerActivity.mExtension!=null)
		{
			for(int i=0;i<RunnerActivity.mExtension.length;i++)
  			{	 
				if(RunnerActivity.mExtension[i] instanceof IExtensionBase )
					((IExtensionBase)RunnerActivity.mExtension[i]).onDestroy();
				//if(RunnerActivity.mExtension[i] instanceof ISocial)
				//	((ISocial)RunnerActivity.mExtension[i]).onDestroy();
				//else if(RunnerActivity.mExtension[i] instanceof IAdExt)
				//	((IAdExt)RunnerActivity.mExtension[i]).onDestroy();
			}
		}

		// Hide virtual keyboard if it's been shown
		if(m_keyboardController != null && m_keyboardController.GetVirtualKeyboardVisible() == true)
		{
			m_keyboardController.VirtualKeyboardHide();
		}
	 		
	    // Kill the activity completely!
	    java.lang.System.exit(0);
    }

	protected void resumeApp()
	{
		//!only "resume" when onPause has been called
		//otherwise we can get visibility changes without onPause, resulting in multiple calls to mGLView/JNILib resume & crash from memory shortage on some devices
		//Fixes #0010034: Android: Lazy Mouse crashes on low-spec systems when trying to post to Facebook multiple times
		if( !mbAppSuspended ) 
		{
			//Log.i("yoyo", " x x x resumeApp - already resumed! x x x ");
			return;
		}
		mbAppSuspended = false;
		
		Log.i("yoyo", "resumeApp");
		Gamepad.EnumerateDevices(mYYPrefs);		

    	if (mGLView != null) {
			mGLView.onResume();

		}

		if ((RunnerJNILib.ms_context != null) && !RunnerJNILib.ms_loadLibraryFailed){					
			Log.i("yoyo", "Resuming the C++ Runner/resetting GL state");
			RunnerJNILib.Resume(0);
		}		
	}
    
  
    
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
    		
    	Log.i("yoyo", "onWindowFocusChanged(" + hasFocus + "|" + FocusOverride + ")");
	    super.onWindowFocusChanged(hasFocus);
	
		if (RunnerJNILib.ms_exitcalled) {
			Log.i("yoyo","Ignoring focus change as we are exiting");
			return;
		}
	
		HasFocus = (hasFocus|FocusOverride);
    	FocusOverride = false;
		
		
		setupUiVisibility();
    	setupUiVisibilityDelayed();
    	
		
		
		if (mGLView != null) {
    		if (HasFocus) {
    			
    			
				if (mGLView.mRenderer.m_pauseRunner == true || mGLView.mRenderer.m_pausecountdown>0)
				{
					mGLView.mRenderer.m_pausecountdown=0;
					resumeApp();

					if (RunnerJNILib.ms_context != null) {		
						// Cause the MP3 player to resume playing the MP3 that was playing when paused
						RunnerJNILib.RestoreMP3State();
					}
					
					// Unblock onDrawFrame() that causes the runner to process
					mGLView.mRenderer.m_pauseRunner = false;
				}
				
    		} else {
    			//Log.i("yoyo","Setting pausecountdown to 20");
    			mGLView.mRenderer.m_pausecountdown = 20;
    			RunnerJNILib.Pause(0);
				// Store the state of the MP3 player so that we can resume playing the current MP3 from the same spot on resume
    			RunnerJNILib.StoreMP3State();
				// Stops all sounds and requests that it doesn't attempt to play anymore until we've resumed
				RunnerJNILib.StopMP3();     		    	
    		}
    	}
    	
    	if(RunnerActivity.mExtension!=null)
		{
			for(int i=0;i<RunnerActivity.mExtension.length;i++)
			{
				if(RunnerActivity.mExtension[i] instanceof IExtensionBase)
				{
					((IExtensionBase)RunnerActivity.mExtension[i]).onWindowFocusChanged(hasFocus);
				}
			}
		}
		
		//reapply low-profile/immersive mode when regaining focus
		if( hasFocus )
		{
			//resetUiVisibility();

			// BM: Call Resume through the JNILib to restart the audio.
			if (RunnerJNILib.ms_context != null) {
				RunnerJNILib.Resume(0);
			}
		}		
    }
    
    @Override
    protected void onPause() {		

		Log.i("yoyo", "onPause");
		super.onPause();

    	mSensorManager.unregisterListener(this);
    	
    	Log.i("yoyo", "Pausing the Runner");
		if (!RunnerJNILib.ms_loadLibraryFailed) {
			RunnerJNILib.Pause(0);
		} // end if
		mbAppSuspended = true;	//prevent multiple resumeApp()
       
       
		if (mGLView != null) {
		    //setSystemUiVisibility( 0x0 );
	    	mGLView.onPause();
    	}

		if (mGLView != null) {
			mGLView.mRenderer.m_pauseRunner = true;
		}
		
		if(RunnerActivity.mExtension!=null)
		{
			for(int i=0;i<RunnerActivity.mExtension.length;i++)
  			{	 
				if(RunnerActivity.mExtension[i] instanceof IExtensionBase )
					((IExtensionBase)RunnerActivity.mExtension[i]).onPause();
				//if(RunnerActivity.mExtension[i] instanceof ISocial)
				//	((ISocial)RunnerActivity.mExtension[i]).onPause();
				//else if(RunnerActivity.mExtension[i] instanceof IAdExt)
				//	((IAdExt)RunnerActivity.mExtension[i]).onPause();
			}
		}
		
		DemoRenderer.elapsedVsyncs = -1;

		if (vsyncHandler != null)
		{
			((RunnerVsyncHandler)vsyncHandler).RemoveFrameCallback();
		}

		// Hide virtual keyboard if it's been shown
		if(m_keyboardController != null && m_keyboardController.GetVirtualKeyboardVisible() == true)
		{
			m_keyboardController.VirtualKeyboardHide();
		}
	}
   
   
    @Override
    protected void onResume() {

		// As per https://developer.nvidia.com/sites/default/files/akamai/mobile/docs/android_lifecycle_app_note.pdf:
		// onResume technically means the start of the "foreground" lifespan of the app, but it does not mean that
		// the app is fully visible and should be rendering, so game updating/rendering does not occur here
		Log.i("yoyo", "onResume");
    	
    
    	super.onResume();		

		
		setupUiVisibility();
    	setupUiVisibilityDelayed();
    	
        
		// If we never lost focus, resume
		if (HasFocus) {	
			Log.i("yoyo", "App still has focus");
			if (mGLView != null) {
				if (mGLView.mRenderer.m_pauseRunner == true || mGLView.mRenderer.m_pausecountdown>0)
				{
			    	Orientation = GetOrientation();
			    	mGLView.mRenderer.m_pausecountdown = 0;
					Log.i("yoyo", "Runner is paused - unpausing");
					resumeApp();

    				if (RunnerJNILib.ms_context != null) {		
						// Cause the MP3 player to resume playing the MP3 that was playing when paused
						RunnerJNILib.RestoreMP3State();
					}
					
					// Unblock onDrawFrame() that causes the runner to process
					mGLView.mRenderer.m_pauseRunner = false;
				}
				
			}
		}

		mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
		if(RunnerActivity.mExtension!=null)
		{
			for(int i=0;i<RunnerActivity.mExtension.length;i++)
  			{	 
				if(RunnerActivity.mExtension[i] instanceof IExtensionBase )
					((IExtensionBase)RunnerActivity.mExtension[i]).onResume();
				//if(RunnerActivity.mExtension[i] instanceof ISocial)
				//	((ISocial)RunnerActivity.mExtension[i]).onResume();
				//else if(RunnerActivity.mExtension[i] instanceof IAdExt)
				//	((IAdExt)RunnerActivity.mExtension[i]).onResume();
			}
		}
		
		
		if (RunnerJNILib.m_runnerFacebook != null) 	
			RunnerJNILib.m_runnerFacebook.onResume();
			
		if (vsyncHandler != null)
		{
			//Log.i("yoyo", "onResume: postFrameCallback");
			((RunnerVsyncHandler)vsyncHandler).PostFrameCallback();
			//Log.i("yoyo", "onResume: after postFrameCallback");	
		}
	}		
    
    @Override
    public boolean onCreateOptionsMenu( Menu menu )
    {
    	super.onCreateOptionsMenu( menu );
    	
    	if(RunnerActivity.mExtension!=null)
		{
			for(int i=0;i<RunnerActivity.mExtension.length;i++)
			{
				if(RunnerActivity.mExtension[i] instanceof IExtensionBase)
				{
					boolean consumed =((IExtensionBase)RunnerActivity.mExtension[i]).onCreateOptionsMenu(menu);
					if(consumed)
						return consumed;	
				}
			}
		} 	
    	
    	if (YoYoRunner) {
	    	menu.add( PREFERENCES_GROUP_ID, SETTINGS_ID, 0, R.string.menu_settings );
    		menu.add( PREFERENCES_GROUP_ID, EXIT_ID, 0, R.string.menu_exit);
		} // end if
    	
    	return YoYoRunner;
    }
    
    @Override
    public boolean onOptionsItemSelected( MenuItem item )
    {
    
    	if(RunnerActivity.mExtension!=null)
		{
			for(int i=0;i<RunnerActivity.mExtension.length;i++)
			{
				if(RunnerActivity.mExtension[i] instanceof IExtensionBase)
				{
					boolean consumed =((IExtensionBase)RunnerActivity.mExtension[i]).onOptionsItemSelected(item);
					if(consumed)
						return consumed;	
				}
			}
		} 	
    
    
    	switch( item.getItemId() ) {
		case SETTINGS_ID: 
			Intent settingsActivity = new Intent( getBaseContext(), RunnerPreferenceActivity.class );
			startActivity( settingsActivity );
			break;
		case EXIT_ID:
    		AlertDialog.Builder builder = new AlertDialog.Builder(this);
    		builder.setMessage( "Are you sure you want to exit?")
    			.setCancelable(false)
    			.setPositiveButton( "Yes", new DialogInterface.OnClickListener() {
    				public void onClick( DialogInterface dialog, int id ) {
    					RunnerJNILib.ExitApplication();
    				}
     			})
    			.setNegativeButton( "No", new DialogInterface.OnClickListener() {
    				public void onClick( DialogInterface dialog, int id ) {
    					dialog.cancel();    					
    				}
    			});
    		AlertDialog alert  = builder.create();
    		alert.show();
			break;
    	} // end switch
    	
    	return true;
    } // end onOptionsItemSelected
    
    @Override
    public boolean onKeyDown( int keyCode, KeyEvent event ) 
	{
		int eventSource = event.getSource();
		boolean eventFromPhysicalDevice = (event.getDeviceId() > 0);
		boolean eventFromKeyboard = ((eventSource & InputDevice.SOURCE_KEYBOARD) == InputDevice.SOURCE_KEYBOARD);
		boolean eventFromGamepad = ((eventSource & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD);
		//Log.i("yoyo", "onKeyDown " + keyCode + " - " + event.getUnicodeChar() + ". Device ID: " + event.getDeviceId() + ". Source: "+eventSource+". Physical: "+eventFromPhysicalDevice+". Keyboard: "+ eventFromKeyboard + ". Gamepad: "+eventFromGamepad);

		if(RunnerActivity.mExtension!=null)
		{
			for(int i=0;i<RunnerActivity.mExtension.length;i++)
			{
				if(RunnerActivity.mExtension[i] instanceof IExtensionBase)
				{
					boolean consumed =((IExtensionBase)RunnerActivity.mExtension[i]).onKeyDown(keyCode,event);
					if(consumed)
						return consumed;	
				}
			}
		} 	

		// Handle physical keyboard events
		if(eventFromPhysicalDevice && eventFromKeyboard && !eventFromGamepad)
		{
			//Log.i("yoyo", "[VK] Physical keyboard event detected.");
			RunnerKeyboardController keyboardController = GetKeyboardController();
			if(keyboardController != null)
			{
				keyboardController.OnPhysicalKeyboardKeyEvent(keyCode, event);
			}
		}

    	// record the key events ready to be passed down to the game	
    	if (keyCode != 0) 
		{
	    	RunnerJNILib.KeyEvent(0, keyCode, event.getUnicodeChar(), eventSource);
	    }
    	
    	if(keyCode == KeyEvent.KEYCODE_BACK)
    	{
    		//Log.i("yoyo","Started tracking back key");
    		event.startTracking();
			return true;
        }
    	
    	if ((keyCode == KeyEvent.KEYCODE_VOLUME_UP) || 
    		(keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) || 
    		(keyCode == KeyEvent.KEYCODE_HOME) || 
    		(keyCode == KeyEvent.KEYCODE_MENU) ||
    		(keyCode >= KeyEvent.KEYCODE_HEADSETHOOK))
    		{
				setupUiVisibility();
    			setupUiVisibilityDelayed();
	  			return super.onKeyDown(keyCode, event);  	
	  		}
	  	else
		{
	    	return true;
		}
    }
    
    @Override
    public boolean onKeyUp( int keyCode, KeyEvent event ) 
    {
		//Log.i("yoyo", "onKeyUp " + keyCode + " - " + event.getUnicodeChar());

		if(RunnerActivity.mExtension!=null)
		{
			for(int i=0;i<RunnerActivity.mExtension.length;i++)
			{
				if(RunnerActivity.mExtension[i] instanceof IExtensionBase)
				{
					boolean consumed =((IExtensionBase)RunnerActivity.mExtension[i]).onKeyUp(keyCode,event);
					if(consumed)
						return consumed;	
				}
			}
		} 	

    	RunnerJNILib.KeyEvent( 1, keyCode, event.getUnicodeChar(), event.getSource());

    	if ((keyCode == KeyEvent.KEYCODE_VOLUME_UP) || 
    		(keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)|| 
    		(keyCode == KeyEvent.KEYCODE_HOME)|| 
    		(keyCode == KeyEvent.KEYCODE_MENU)||
    		(keyCode >= KeyEvent.KEYCODE_HEADSETHOOK))
	  		return super.onKeyUp(keyCode, event );  	
	  	else
	    	return true;
    } 

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
	
	public int getDeviceDefaultOrientation(){

		try {
			Class<?> displayClass = Class.forName("android.view.Display");

			// API level 8
			Method getRotationMethod = displayClass.getDeclaredMethod("getRotation");
			if (getRotationMethod != null) { 						
				WindowManager lWindowManager =  (WindowManager) getSystemService(WINDOW_SERVICE);

				Configuration cfg = getResources().getConfiguration();
				int lRotation = lWindowManager.getDefaultDisplay().getRotation();

				if( (((lRotation == Surface.ROTATION_0) ||(lRotation == Surface.ROTATION_180)) &&   
						(cfg.orientation == Configuration.ORIENTATION_LANDSCAPE)) ||
						(((lRotation == Surface.ROTATION_90) ||(lRotation == Surface.ROTATION_270)) &&    
						(cfg.orientation == Configuration.ORIENTATION_PORTRAIT))){
					return Configuration.ORIENTATION_LANDSCAPE;
				}
			} // end if
		}
		catch (Exception e) {
			Log.i("yoyo", "ERROR: Enumerating API level " + e.getMessage());
		}
		return Configuration.ORIENTATION_PORTRAIT;
	}

	@Override
	public void onSensorChanged(SensorEvent event) {			
		switch( DefaultOrientation ) {
		default:
		case Configuration.ORIENTATION_PORTRAIT:
			AccelX = event.values[0] / 9.80665f;
			AccelY = event.values[1] / 9.80665f;
			AccelZ = event.values[2] / 9.80665f;
			break;
		case Configuration.ORIENTATION_LANDSCAPE:
			AccelX = event.values[1] / 9.80665f;
			AccelY = -event.values[0] / 9.80665f;
			AccelZ = event.values[2] / 9.80665f;
			break;				
		} // end switch
	}
	
	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    
    	Log.i("yoyo", "Got activity result: " + resultCode);
    	
    	
		if ((mRunnerBilling==null) || (!mRunnerBilling.handleActivityResult(requestCode, resultCode, data))) {
		
    		
			super.onActivityResult(requestCode, resultCode, data);
			
			if (RunnerJNILib.m_runnerFacebook != null) 	
				RunnerJNILib.m_runnerFacebook.onActivityResult(requestCode,resultCode,data);
			
			
			
			
			if(RunnerActivity.mExtension!=null)
			{
				for(int i=0;i<RunnerActivity.mExtension.length;i++)
  				{	 
					if(RunnerActivity.mExtension[i] instanceof IExtensionBase)
						((IExtensionBase)RunnerActivity.mExtension[i]).onActivityResult(requestCode,resultCode,data);
				}
			}
			
		}
		
		
		setupUiVisibility();
    	setupUiVisibilityDelayed();
    	
		
		Log.i("yoyo", "End Got activity result");
		
		
		
    } 
    
	int EVENT_OTHER_SYSTEM_EVENT = 75;
	
	
    @Override
	public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) 
	{
	
		Log.i("yoyo","onRequestPermissionsResult " +requestCode+ " returned with: " + permissions.toString() + " results:" +grantResults.toString());
		switch (requestCode) 
		{
			case DemoRenderer.RUNNER_STORAGE_PERMISSION_REQUEST: 
			{
				for( int i=0;i<grantResults.length;i++)
				{
					if(grantResults[i]==PackageManager.PERMISSION_DENIED)
					{
					
						Toast toast = Toast.makeText(RunnerActivity.CurrentActivity,"Cannot function as a runner without these permissions, use Create Executable for Target",Toast.LENGTH_LONG);
						toast.show();
					//	RunnerJNILib.ShowMessage("Cannot run as a runner without the requested permissions.");
						return;
					} 
				}
				mGLView.mRenderer.m_RequestedPermissions = false;
				
				mGLView.mRenderer.m_saveFilesDir = Environment.getExternalStorageDirectory() + "/GMstudio";
				File studioDir = new File( mGLView.mRenderer.m_saveFilesDir );
				studioDir.mkdir();
				mGLView.mRenderer.m_saveFilesDir = mGLView.mRenderer.m_saveFilesDir + '/';
			}
		}
		
		int dsMapIndex = RunnerJNILib.jCreateDsMap(null, null, null);
		RunnerJNILib.DsMapAddString( dsMapIndex, "type", "permission_request_result" );
		for(int i=0;i<grantResults.length;i++)
			RunnerJNILib.DsMapAddDouble( dsMapIndex,permissions[i] ,(double)grantResults[i] );
	
		RunnerJNILib.CreateAsynEventWithDSMap(dsMapIndex,EVENT_OTHER_SYSTEM_EVENT);
		
		if(RunnerActivity.mExtension!=null)
		{
			for(int i=0;i<RunnerActivity.mExtension.length;i++)
			{
				if(RunnerActivity.mExtension[i] instanceof IExtensionBase)
				{
					((IExtensionBase)RunnerActivity.mExtension[i]).onRequestPermissionsResult(requestCode,permissions,grantResults);
					
				}
			}
		}
    
		
	}
     
    @Override
    protected Dialog onCreateDialog(int id) 
    {
    
		if(RunnerActivity.mExtension!=null)
		{
			for(int i=0;i<RunnerActivity.mExtension.length;i++)
			{
				if(RunnerActivity.mExtension[i] instanceof IExtensionBase)
				{
					Dialog consumed =((IExtensionBase)RunnerActivity.mExtension[i]).onCreateDialog(id);
					if(consumed!=null)
						return consumed;	
				}
			}
		}
    
    
    
        switch (id) 
        {
        	case DIALOG_CANNOT_CONNECT_ID:
            	return createDialog(R.string.cannot_connect_title, R.string.cannot_connect_message);
            	
        	case DIALOG_BILLING_NOT_SUPPORTED_ID:
            	return createDialog(R.string.billing_not_supported_title, R.string.billing_not_supported_message);
            	
            case DIALOG_BILLING_PURCHASE_ERROR:
            	return createDialog(R.string.billing_failed_title, R.string.billing_failed_message);
            	
	        default:
    	        return null;
        }
    }

	@Override
	public boolean dispatchKeyEvent(KeyEvent event)
	{	
	
		if(RunnerActivity.mExtension!=null)
		{
			for(int i=0;i<RunnerActivity.mExtension.length;i++)
			{
				if(RunnerActivity.mExtension[i] instanceof IExtensionBase)
				{
					boolean consumed =((IExtensionBase)RunnerActivity.mExtension[i]).dispatchKeyEvent(event);
					if(consumed)
						return consumed;	
				}
			}
		}

		Gamepad.handleKeyEvent(event);		
		return super.dispatchKeyEvent(event);
	}

	@Override
	public boolean dispatchGenericMotionEvent(MotionEvent event)
	{
	
		if(RunnerActivity.mExtension!=null)
		{
			for(int i=0;i<RunnerActivity.mExtension.length;i++)
			{
				if(RunnerActivity.mExtension[i] instanceof IExtensionBase)
				{
					boolean consumed =((IExtensionBase)RunnerActivity.mExtension[i]).dispatchGenericMotionEvent(event);
					if(consumed)
						return consumed;	
				}
			}
		}
	
		//Log.i( "yoyo", "MotionEvent - " + event.toString() );
		if ((event.getSource() == InputDevice.SOURCE_JOYSTICK) || (event.getSource() == InputDevice.SOURCE_GAMEPAD)) 
		{
			Gamepad.handleMotionEvent(event);

			// BUGFIX: Prevent consuming gamepad events in order to support system virtual keyboard navigation
			if(m_keyboardController == null || m_keyboardController.VirtualKeyboardGetStatus() == false)
			{
				return true;
			}
		}

		return super.dispatchGenericMotionEvent(event);
	}
    
    /* Checks that this activity really is the YoYo Runner */
    private boolean checkIsYoYoRunner() {
    
    	boolean yoyoRunner = false;
		try {
			InputStream is = getResources().getAssets().open( "game.droid" );
			if (is != null) {
				yoyoRunner = false;
				Log.i( "yoyo", "#######!!!!!!! Checking for runner - found assets " );
				is.close();
			} // end if
			else {
				yoyoRunner = true;
				Log.i( "yoyo", "#######!!!!!!! Checking for runner - not found assets" );
			} // end else
			
		} 
		catch( Exception e ) {
			yoyoRunner = true;
			Log.i( "yoyo", "#######!!!!!!! Checking for runner! failed");
		} // end catch
		
		//Log.i("yoyo", "File Path for INI:: " + _apkFilename );

		
		

		
		
		//for APK expansion - no game.droid in assets - in downloaded .obb
		if( yoyoRunner ) //Disabling for the moment as testing runner with proper package name
		{
			//String packageName = this.getComponentName().getPackageName();
			//Log.i( "yoyo", "#####!!!! package name is " + packageName );
		//	boolean bTestRunnerAPK = true;	//TESTING, obviously - so we can check APK expansion basics in runner
			//if( (!packageName.equals( "com.yoyogames.runner")))  //|| bTestRunnerAPK )
			
			
			Bundle bundle = null;
		try {
			ApplicationInfo ai = getPackageManager().getApplicationInfo(this.getComponentName().getPackageName(), PackageManager.GET_META_DATA);
			bundle = ai.metaData;
			String IsRunner = bundle.getString("IsBuiltAsYoYoRunner");
			
			if(IsRunner!=null && IsRunner.equals("Yes"))
			{
				//We are a runner build, carry on
				Log.i("yoyo","Found Runner flag in manifest, not using APK expansion");
			}
			else
			{
				//no droid file, no runner manifest entry & final package - need to download assets
				Log.i( "yoyo", "#######!!!!!!! using APK Expansion file" );
				yoyoRunner = false;
				this.UseAPKExpansionFile = true;
			}
		} 
		catch( Exception _e ) 
		{
			Log.d( "yoyo", "Exception while reading package meta data" +_e.toString() );
		} // end catch   	 
			
			
		}
		
		
		
		
		
		return yoyoRunner;
    }
    private void setupExtensions()
    {
    
		if(mExtension!=null)
		{
			return; //Already initialised...
		}
    
		if(mYYPrefs==null)
		{
			Log.i("yoyo","Unable to initialise extensions as preferences have not been loaded");
			return;
		}
    
    	int numexts = mYYPrefs.getInt("YYNumExtensionClasses");
        	
    	if(numexts>0)
    		mExtension = new Object[numexts];
    	
    	for(int i=0;i<numexts;i++)
    	{
    		String ExtClass = mYYPrefs.getString("YYExtensionClass"+i);
    		//String ExtInterface = mYYPrefs.getString("YYExtensionClassInterface"+i);
        	
    		if(ExtClass!=null && !ExtClass.isEmpty()) //May not have an android class
    		{
    			
				try{
					String FullClassName = "${YYAndroidPackageName}."+ExtClass;
					Log.i("yoyo","Attempting to initialise extension class " + FullClassName);
					mExtension[i] = (Object)(Class.forName(FullClassName).getConstructor().newInstance());
					
					
					
						try{
							Method method = mExtension[i].getClass().getMethod("Init");
							if(method != null)
							{
								Log.i("yoyo","Method found, attempting to invoke Init");
								method.invoke(mExtension[i]);
							}
						}
						catch(Exception e)
						{
							Log.i("yoyo","No Init method found on extension class:"+ ExtClass + " returned " +e.getMessage());
							//e.printStackTrace();
						}

					}
				catch(Exception e)
				{
					Log.i("yoyo","Exception thrown attempting to create extension class " + e.getMessage());
					e.printStackTrace();
				}
    			
    		}
    	}
        	
    }
    
    /* If advertising is enable for this game set it up using this routine */
    private void setupAdvertising() {
    
			if(RunnerActivity.mExtension!= null)
			{
				Log.i("yoyo","checking " + RunnerActivity.mExtension.length + " extensions for ad interface");
				for(int i=0;i<RunnerActivity.mExtension.length;i++)
    			{	 
    				if(RunnerActivity.mExtension[i] instanceof IAdExt)
    				{
    					Log.i("yoyo","Found advertising extension interface, calling setup");
						((IAdExt)RunnerActivity.mExtension[i]).setup();
					}
				}
			}
			else
			{
				Log.i("yoyo","No extensions defined prior to advertising check");
			}
          
    }
    
  
	
	

    //-----------------------------------------------------------------------------------------------------------
    
    
    /* Sets up Flurry */
    //->extension
    /*private void setupFlurry() 
    {
    	Log.i("yoyo", "---- setupFlurry ---------");
    	String flurryId = mYYPrefs.getString("FlurryId");
    	if(flurryId == null )
    	{
    		flurryId = mYYPrefs.getString("FLURRY_KEY");
    	}
    	
   		if( flurryId != null )
   		{
   			FlurryCode = flurryId;
   			Flurry = true;
   		
   			//since this is called after onStart, we need to start the flurry session here
   			int version = FlurryAgent.getAgentVersion();
   			Log.i( "yoyo", "@@@@@@@ Flurry session started code = " + FlurryCode);
   			Log.i("yoyo", "Flurry Agent Version = " + version );
			FlurryAgent.setReportLocation(false);
			FlurryAgent.setLogEvents(true);
			FlurryAgent.setLogEnabled(true);
			FlurryAgent.setLogLevel(Log.VERBOSE);
        	FlurryAgent.onStartSession( this, FlurryCode );
   		}
    }*/
    
    /* Sets up Google Analytics */
    //...moved to extension ha!
    /*private void setupGoogleAnalytics() 
    {
		String trackingId = mYYPrefs.getString("TrackingID");
		Log.i( "yoyo", "GA tracking ID: " + trackingId );
   		if( trackingId != null )
   		{
   			GACode = trackingId;
   			mbGoogleAnalytics = true;
   		}
		
		if( mbGoogleAnalytics )
		{
			mGaInstance = GoogleAnalytics.getInstance(this);	//V2
			mGaInstance.setDebug(true);
			mGaTracker = mGaInstance.getTracker(GACode);
			
			mGaTracker.sendView("/GameStart");	//->onStart
			Log.i( "yoyo", "@@@@@ started Google Analytics with TrackingID: " + GACode);
		}
    }*/
    
    /*public static void googleAnalyticsEvent(String actionName, String label, int value )
    {
		if( mbGoogleAnalytics )
		{
			Long lvalue = new Long(value);
			mGaTracker.sendEvent(
				"GMEvent",  // Category
				 actionName,  // Action
				 label, // Label
				 lvalue);       // Value
			Log.i( "yoyo", "@@@@@@@ Google Analytics event ext: " + actionName + "," + label + "," + value);
		}
    }*/
    
    /*public static void googleAnalyticsEvent(String actionName )
    {
		if( mbGoogleAnalytics )
		{
			mGaTracker.sendEvent(
				"GMEvent",  // Category
				 actionName,  // Action
				 "", // Label
				 null);       // Value -should be null for no value

			Log.i( "yoyo", "@@@@@@@ Google Analytics event: " + actionName);
		}
    }*/
    
    /* Gets the facebook app ID from the manifest, if it's available */
    public String getFacebookAppId() {
    	    	
    	try {
			ApplicationInfo ai = getPackageManager().getApplicationInfo(this.getComponentName().getPackageName(), PackageManager.GET_META_DATA);
        	Bundle bundle = ai.metaData;
        	
        	//String appID = bundle.getString("YYFacebookAppId");
        	
        	// We add "fb" on to the front of the app ID to stop it from turning it into an integer so strip this
        	//if (appID.startsWith("fb")) {
        	//	appID = appID.replaceFirst("fb", "");
        	//}
        	
        	//appId must be in manifest key com.facebook.sdk.ApplicationId for facebook to work so let's check it is-
        	String appID = bundle.getString("com.facebook.sdk.ApplicationId");
        	Log.i("yoyo", "FOUND facebook appID: " +  appID);
        	
        	//facebook sdk will throw exception if appID is null OR empty 
        	if( appID != null && appID.length() == 0)
        	{
        		appID = null;	
        	}
        	
			return appID;	    		
	    }
	    catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null; 
    }
    
    /* Checks to see if the current device is an XPeria play */
    private void checkXPeriaPlay() {
    
	    Log.i( "yoyo", "@@@@@@@ Build.Display = " + android.os.Build.DISPLAY + " BRAND=" + android.os.Build.BRAND + 
        						" DEVICE=" + android.os.Build.DEVICE + " MANUFACTURER=" + android.os.Build.MANUFACTURER+ 
        						" MODEL=" + android.os.Build.MODEL + " PRODUCT="  + android.os.Build.PRODUCT
        						);
        XPeriaPlay = (android.os.Build.MANUFACTURER.equals("Sony Ericsson")) && (android.os.Build.MODEL.startsWith("R800"));
        Log.i( "yoyo", "@@@@@@@ XPeriaPlay=" + XPeriaPlay + " manufacturer=" + android.os.Build.MANUFACTURER.equals("Sony Ericsson") + " model=" +  android.os.Build.MODEL.startsWith("R800"));
    }

	private Handler mRestoreImmersiveModeHandler = new Handler();

	private Runnable restoreImmersiveModeRunnable = new Runnable()
	{
		public void run() 
		{
			setupUiVisibility();      
		}
	};
	public void setupUiVisibilityDelayed()
	{
		// we restore it now and after 500 ms!
		mRestoreImmersiveModeHandler.postDelayed(restoreImmersiveModeRunnable, 500); 
	}
 
	public void   setupUiVisibility ()
	{
		if (mGLView != null)
		{
			 try {
        
        	Class parTypes[] = new Class[1];
        	parTypes[0] = Integer.TYPE;
	        mSetSystemUiVisibility = android.view.View.class.getMethod("setSystemUiVisibility", parTypes );
	        
			// if we are on 4.x (ice cream sandwich) or above (i.e. SystemUiVisibility is available then at least go into low profile)
			// if 4.4 (kitkat) or above then go into immersive mode
			int vis = 0x1;
			
			//Trying suggested fix of setting to off then on...
			
			
			//Log.i("yoyo", "OS VERSION SDK_INT = " + android.os.Build.VERSION.SDK_INT);
			if (android.os.Build.VERSION.SDK_INT >= 19 /*android.os.Build.VERSION_CODES.KITKAT*/) {
			
				if((RunnerActivity.UIVisibilityFlags&0x100)!=0) /* SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN*/
				{
					vis = RunnerActivity.UIVisibilityFlags&~(0x100);//0x706;
					ourSetSystemUiVisibility( vis );
				
					//Fixes suggested by http://vitiy.info/small-guide-how-to-support-immersive-mode-under-android-4-4/
				}
				Log.i("yoyo","Setting vis flags to "+UIVisibilityFlags);
				vis = RunnerActivity.UIVisibilityFlags; //SYSTEM_UI_FLAG_IMMERSIVE_STICKY|SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION|SYSTEM_UI_FLAG_LAYOUT_STABLE
								//SYSTEM_UI_FLAG_FULLSCREEN|SYSTEM_UI_FLAG_HIDE_NAVIGATION
			} // end if
	        ourSetSystemUiVisibility( vis );
	        
			} 
			catch( Exception e ) {
	    		Log.i( "yoyo", "Exception while getting setSystemUiVisibility :: " + e.toString() );
			}
		}
	
	}
	

    
    /* Sets up the GL view that's reponsible for rendering (and GM processing...) */
    public void setupView() {
    
		Log.i( "yoyo", " + + + + setupView + + + +");
	    setContentView( R.layout.main );
        mGLView = (DemoGLSurfaceView)findViewById(R.id.demogl);
		setupUiVisibility();
		setupUiVisibilityDelayed();

		// start the draw events
		if (vsyncHandler != null)
		{
			((RunnerVsyncHandler)vsyncHandler).PostFrameCallback();
		}
    }

	@Override
    public void surfaceCreated(SurfaceHolder holder) {

		
		if (mGLView != null)
		{
			mGLView.surfaceCreated(holder);
		}

		if (vsyncHandler != null)
		{
			// start the draw events
			((RunnerVsyncHandler)vsyncHandler).PostFrameCallback();
		}
	}

	@Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

		if (mGLView != null)
		{
			mGLView.surfaceChanged(holder, format, width, height);
		}
	}
	
	@Override
    public void surfaceDestroyed(SurfaceHolder holder) {

		if (mGLView != null)
		{
			mGLView.surfaceDestroyed(holder);
		}
        // We need to wait for the render thread to shut down before continuing because we
        // don't want the Surface to disappear out from under it mid-render.  The frame
        // notifications will have been stopped back in onPause(), but there might have
        // been one in progress.        
	}


    private void ourSetSystemUiVisibility( int _vis ) {
    	if (mSetSystemUiVisibility != null) {
    		try {
    			mSetSystemUiVisibility.invoke( mGLView, _vis );
	    //		Log.i( "yoyo", "mSetSystemUiVisibility(" + _vis + ")" );
    		} catch( Exception _e ) {
    			Log.i( "yoyo", "Exception while calling setSystemUiVisibility " + _e.toString() );
    		} // end catch
    	} // end if
    	else {
	    		Log.i( "yoyo", "!!!!Unable to do mSetSystemUiVisibility(" + _vis + ")" );
    	} // end else	
    }


    private Dialog createDialog(int titleId, int messageId) 
    {
        String helpUrl = replaceLanguageAndRegion(getString(R.string.help_url));
        Log.i("yoyo", helpUrl);

        final Uri helpUri = Uri.parse(helpUrl);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(titleId)
            .setIcon(android.R.drawable.stat_sys_warning)
            .setMessage(messageId)
            .setCancelable(false)
            .setPositiveButton(android.R.string.ok, null)
            .setNegativeButton(R.string.learn_more, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, helpUri);
                    startActivity(intent);
                }
            });
        return builder.create();
    }
   
    /**
     * Replaces the language and/or country of the device into the given string.
     * The pattern "%lang%" will be replaced by the device's language code and
     * the pattern "%region%" will be replaced with the device's country code.
     *
     * @param str the string to replace the language/country within
     * @return a string containing the local language and region codes
     */
    private String replaceLanguageAndRegion(String str) {
        // Substitute language and or region if present in string
        if (str.contains("%lang%") || str.contains("%region%")) {
            Locale locale = Locale.getDefault();
            str = str.replace("%lang%", locale.getLanguage().toLowerCase(Locale.US));
            str = str.replace("%region%", locale.getCountry().toLowerCase(Locale.US));
        }
        return str;
    }

	/**     
     */
	protected void SelectGooglePlayBilling() {


		Object RunnerBilling = RunnerJNILib.CallExtensionFunction("GooglePlayServicesExtension","InitRunnerBilling",0,null);


		if(RunnerBilling!=null)
		{
			mRunnerBilling = (IRunnerBilling)RunnerBilling;
		}

		// Only select Google Play billing if the requisite permission is available in AndroidManifest.xml
		//if (checkCallingOrSelfPermission("com.android.vending.BILLING")==0) {					
//
//			Log.i("yoyo", "BILLING: Using Google Play billing");
//			mRunnerBilling = new RunnerBilling(this);
//		}
		else 
		{
			Log.i("yoyo", "BILLING: Google Play permissions not available, selecting NULL billing solution");
			mRunnerBilling = new NullBilling(this);
		}
	}
    
    /**	 
     */
    protected void setupInAppBilling() {		

		Log.i("yoyo", "BILLING: setupInAppBilling");
		try {
			// Amazon IAP does not require any special billing permissions so just use it if option is built into the manifest
			ApplicationInfo ai = RunnerActivity.CurrentActivity.getPackageManager().getApplicationInfo(
				RunnerActivity.CurrentActivity.getComponentName().getPackageName(), PackageManager.GET_META_DATA);
        		
		//	boolean amazonIAPValue = bundle.getBoolean("YYUseAmazonIAP");
			//Log.i("yoyo", "BILLING: Amazon setting: " + amazonIAPValue);

			SelectGooglePlayBilling();			
		}
		catch (Exception e) {
			Log.i("yoyo", "BILLING: Unable to determine billing method via Manifest, selecting Googe Play as fallback" + e.getMessage());
			SelectGooglePlayBilling();
		}
    }
    
    public int GetOrientation()
    {
		int ret = 0;
    	int rotation = 0;
		if (android.os.Build.VERSION.SDK_INT > 7 ) {
    		rotation = getWindowManager().getDefaultDisplay().getRotation();
    	} // end if
    	else {
    		rotation = getWindowManager().getDefaultDisplay().getOrientation();
    	} //end else
		Configuration configuration = getResources().getConfiguration();   
    		
		if(configuration.orientation == Configuration.ORIENTATION_LANDSCAPE &&  
		   (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) ||  
		   configuration.orientation == Configuration.ORIENTATION_PORTRAIT &&   
		   (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270))   
		{   
			ret = rotation;
		} // end if
		else {
			ret = (rotation + 1) & 0x3;
		} // end else
		
		Log.i("yoyo", "calculated orientation - " + Orientation);
		return ret;
    } // end GetOrientation
    
    @Override
    public void onConfigurationChanged( Configuration newConfig ) {
		super.onConfigurationChanged(newConfig);

		Orientation = GetOrientation();
		
		// Update and report initial physical keyboard status
		RunnerKeyboardController keyboardController = GetKeyboardController();
		if(keyboardController != null)
		{
			boolean physicalKeyboardConnected = (getResources().getConfiguration().hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO ? true : false);
			keyboardController.SetPhysicalKeyboardConnected(physicalKeyboardConnected);
			//keyboardController.VirtualKeyboardReportStatus(false);
		}

		if(RunnerActivity.mExtension!=null)
		{
			for(int i=0;i<RunnerActivity.mExtension.length;i++)
  			{	 
				if(RunnerActivity.mExtension[i] instanceof IExtensionBase )
					((IExtensionBase)RunnerActivity.mExtension[i]).onConfigurationChanged(newConfig);
			}
		}
    } // end onConfigurationChanged     

	public void SetLaunchedFromPlayer(String gamePath, boolean fromPlayer)
	{
		LaunchedFromPlayer = fromPlayer;
		LaunchedFromPlayerGameFile = gamePath;

		Log.i("yoyo", "JNI SetLaunchedFromPlayer. Game path: " + gamePath);

		// Load ini file for our game
		setupIniFile();
	}

	public float getRefreshRate()
	{
		float refresh = 60.0f;

		refresh = getWindowManager().getDefaultDisplay().getRefreshRate();

		if (refresh < 10.0f)		// errr - on my Xperia Play for example I got a value of 0.012, and I've read of other people getting numbers like 0.34 :/
		{
			refresh = 60.0f;
		}

		return refresh;
	}

	public RunnerKeyboardController GetKeyboardController()
	{
		if(m_keyboardController == null)
		{
			View activityView = getWindow().getDecorView();
			m_keyboardController = RunnerKeyboardController.Create(this, activityView, ViewHandler);
		}

		return m_keyboardController;
	}
}