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
import android.widget.EditText;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothAdapter;
import android.content.IntentFilter;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;


import java.io.File;
import java.io.InputStream;
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

import ${YYAndroidPackageName}.DemoRenderer;
import com.yoyogames.runner.RunnerJNILib;

import java.io.IOException;
import java.io.FilenameFilter;

import android.view.Choreographer;
import android.view.SurfaceHolder;

//import com.inmobi.adtracker.androidsdk.IMAdTracker;
//import com.inmobi.commons.IMCommonUtil;

import ${YYAndroidPackageName}.Gamepad;

import org.ini4j.Ini;

@TargetApi(16)
public class RunnerVsyncHandler implements Choreographer.FrameCallback
{
	public static final class Accessor { private Accessor() {} }
	private static final Accessor accessor = new Accessor();

	@Override
    public void doFrame(long frameTimeNanos) {
        // The events should not start until the RenderThread is created, and should stop
        // before it's nulled out.

		DemoGLSurfaceView mGLView = RunnerActivity.CurrentActivity.GetGLView(accessor);

		if ((mGLView != null) && (mGLView.mRenderer != null))
		{									
			DemoRenderer.elapsedVsyncs++;

			/*if (DemoRenderer.waiterObject != null)
			{
				synchronized(DemoRenderer.waiterObject)
				{
					DemoRenderer.waiterObject.notifyAll();					
				}
			}*/
		}

		//Log.i("yoyo", "doFrame: " + frameTimeNanos);
		Choreographer.getInstance().postFrameCallback(this);
	}

	public void PostFrameCallback()
	{
		Choreographer.getInstance().postFrameCallback(this);
	}

	public void RemoveFrameCallback()
	{
		Choreographer.getInstance().removeFrameCallback(this);
	}
}