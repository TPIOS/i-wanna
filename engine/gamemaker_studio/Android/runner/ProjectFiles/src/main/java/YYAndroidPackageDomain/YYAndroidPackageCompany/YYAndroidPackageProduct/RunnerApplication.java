package ${YYAndroidPackageName};

import android.app.Application;
import android.util.Log;
import android.support.multidex.MultiDexApplication;
//import com.openfeint.api.OpenFeint;
//import com.openfeint.api.OpenFeintDelegate;
//import com.openfeint.api.OpenFeintSettings;
//import com.openfeint.api.resource.User;
//import com.openfeint.api.resource.CurrentUser;
import com.yoyogames.runner.RunnerJNILib;

//----------------------------------------------------------------------------------------------------
//----------------------------------------------------------------------------------------------------
//----------------------------------------------------------------------------------------------------
//----------------------------------------------------------------------------------------------------
public class RunnerApplication extends MultiDexApplication
{
    //public static final String OFGameName = ""; // *** OpenFeint name here
    //public static final String OFGameID = ""; // *** OpenFeint ID here
    //public static final String OFGameKey= ""; // *** OpenFeint Product Key here
    //public static final String OFGameSecret= ""; // *** OpenFeint Secret Key here
    
	@Override
	public void onCreate()
	{
		super.onCreate();
		
		// initialise the native side of the fence
		if (!RunnerJNILib.ms_loadLibraryFailed) {
	       	RunnerJNILib.Init();
		} // end if
       	
        // Check for OpenFeint
        //if (OFGameName != "") {
        //	OpenFeintSettings settings = new OpenFeintSettings( OFGameName, OFGameKey, OFGameSecret, OFGameID ); 
        //	OpenFeint.initialize( this, settings, new OpenFeintDelegate() {
        //	
        //		public void userLoggedIn( CurrentUser _user ) {
        //			Log.i( "yoyo", "User logged in " + _user.name );
        //			RunnerJNILib.OFNotify( RunnerJNILib.eOF_UserLoggedIn, _user.name, "", "", "" );
        //		} // end userLoggedIn
        //		
        //		public void userLoggedOut( User _user ) {
        //			Log.i( "yoyo", "User logged out " + _user.name );
        //			RunnerJNILib.OFNotify( RunnerJNILib.eOF_UserLoggedOut, _user.name, "", "", "" );
        //		} // end userLoggedOut
        //		
        //	} );
        //} // end if
	} // end onCreate
} // end RunnerApplication
