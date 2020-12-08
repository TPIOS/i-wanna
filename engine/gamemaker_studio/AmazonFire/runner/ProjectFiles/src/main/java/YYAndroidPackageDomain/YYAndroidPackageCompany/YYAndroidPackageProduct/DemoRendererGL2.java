package ${YYAndroidPackageName};

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import java.lang.reflect.Field;
import java.io.File;
import java.nio.IntBuffer;
import java.util.Locale;

import java.io.InputStream;
import java.io.IOException;

import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.os.Environment;

import android.app.Application;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;

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
import android.view.WindowManager;
import android.util.Log;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.yoyogames.runner.RunnerJNILib;

/*
import java.security.MessageDigest;
import android.util.Base64;
import java.security.NoSuchAlgorithmException;
*/

//----------------------------------------------------------------------------------------------------
//----------------------------------------------------------------------------------------------------
//----------------------------------------------------------------------------------------------------
//----------------------------------------------------------------------------------------------------
public class DemoRendererGL2 extends DemoRenderer 
{
	public DemoRendererGL2( Context _context )
	{
		super(_context);
	}
      
    public void onSurfaceCreated(GL10 gl, EGLConfig config) 
    {    	
	    if ((m_state != eState.Startup) && (m_state != eState.DownloadGameDroidWait)) {
	     	Log.i("yoyo", "onSurfaceCreated() aborted on re-create 1, state is currently "+m_state);
	    	return;
	    }
	    
	    
    	IntBuffer intBuffer = IntBuffer.allocate(1);
    	gl.glGetIntegerv(GLES20.GL_FRAMEBUFFER_BINDING, intBuffer);
    	
    	m_defaultFrameBuffer = intBuffer.get(0);
    	Log.i("yoyo", "Renderer instance is gl2.0, framebuffer object is: " + m_defaultFrameBuffer);		
	    

		super.onSurfaceCreated(gl, config);
    }
}
