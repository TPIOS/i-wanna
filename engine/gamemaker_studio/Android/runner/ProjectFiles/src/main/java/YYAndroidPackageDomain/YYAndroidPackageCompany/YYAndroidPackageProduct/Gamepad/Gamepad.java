package ${YYAndroidPackageName};

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.os.ParcelUuid;
import android.os.Build.VERSION;

import java.lang.IllegalAccessException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import android.content.pm.PackageManager;
import com.yoyogames.runner.RunnerJNILib;
import ${YYAndroidPackageName}.IGamepadDevice;
import ${YYAndroidPackageName}.NYKODevice;
import ${YYAndroidPackageName}.GameStickDevice;
import ${YYAndroidPackageName}.iCade;
import android.annotation.SuppressLint;

public class Gamepad
{	
	// Set to the minSdkVersion as specified in the AndroidManifest.xml (should we read this out somehow instead?)
	// If this isn't bumped up to at least 9 via prefs/BLUETOOTH permission then Gamepad support will be disabled
	static public int msMinAPILevel = 7;

	// Redefinition (not great, but API safe) of InputDevice constnaces
	private static final int SOURCE_CLASS_JOYSTICK = 0x00000010;	
	private static final int SOURCE_GAMEPAD = 0x00000401;
	private static final int SOURCE_DPAD = 0x00000201;	

	// List of devices that aren't iCade *specific*
	static ArrayList<IGamepadDevice> msGamepadList = new ArrayList<IGamepadDevice>();
	static ArrayList<Boolean> msGamepadAllocated = new ArrayList<Boolean>();
	// List of devices that behave like an iCade. May contain entries from msGamepadList
	static ArrayList<BluetoothDevice> msiCadeList = new ArrayList<BluetoothDevice>();

	// InputDevice functionality only exists with API level >= 9
	static Method msGetDeviceMethod = null;
	public static IGamepadDevice msMogaDevice = null;			// there is only one Moga device

	// These will only be enumerated if getDevice() resolves for InputEvent
	static HashMap<String, Method> msInputDeviceMethods = null;
	static HashMap<String, Method> msMotionRangeMethods = null;
	static HashMap<String, Method> msMotionEventMethods = null;
	static HashMap<String, IGamepadDevice> msDevice2Gamepad = new HashMap<String, IGamepadDevice>();
	static ArrayList<IGamepadDeviceFactory> msGamepadDeviceFactories = new ArrayList<IGamepadDeviceFactory>();

	/*
	 * Checks that we're on API level >= 9 and sets up Method pointers if available
	 */
	private static void EnumerateAPILevel()
	{
		try {															
			Class<?> inputEventClass = Class.forName("android.view.InputEvent");

			// API level 9
			msGetDeviceMethod = inputEventClass.getDeclaredMethod("getDevice");
			if (msGetDeviceMethod != null) { 				
							
				msMinAPILevel = 9;
				
				Class<?> inputDeviceClass = Class.forName("android.view.InputDevice");
				msInputDeviceMethods = new HashMap<String, Method>();
				msInputDeviceMethods.put("getSources", inputDeviceClass.getDeclaredMethod("getSources"));
				msInputDeviceMethods.put("getName", inputDeviceClass.getDeclaredMethod("getName"));		
			

				Class<?> motionEventClass = Class.forName("android.view.MotionEvent");				
				Class parTypes[] = new Class[1];
        		parTypes[0] = Integer.TYPE;
				msMotionEventMethods = new HashMap<String, Method>();
				msMotionEventMethods.put("getAxisValue", motionEventClass.getDeclaredMethod("getAxisValue", parTypes));					

				Class<?> motionRangeClass = Class.forName("android.view.InputDevice$MotionRange");
				msMotionRangeMethods = new HashMap<String, Method>();
				msMotionRangeMethods.put("getAxis", motionRangeClass.getDeclaredMethod("getAxis"));
				msMotionRangeMethods.put("getRange", motionRangeClass.getDeclaredMethod("getRange"));
				msMotionRangeMethods.put("getMin", motionRangeClass.getDeclaredMethod("getMin"));
				msMotionRangeMethods.put("getMax", motionRangeClass.getDeclaredMethod("getMax"));
				

				Method getMotionRangesMethod = inputDeviceClass.getDeclaredMethod("getMotionRanges");
				if (getMotionRangesMethod != null) {

					msMinAPILevel = 12;
					msInputDeviceMethods.put("getMotionRanges", getMotionRangesMethod);
					msMotionRangeMethods.put("getSource", motionRangeClass.getDeclaredMethod("getSource"));
				}
				
				Method getDescriptorMethod = inputDeviceClass.getDeclaredMethod("getDescriptor");
				if(getDescriptorMethod !=null)
				{
					msMinAPILevel = 16;
					msInputDeviceMethods.put("getDescriptor", getDescriptorMethod);
				}
				
			}
		}
		catch (Exception e) {
			Log.i("yoyo", "ERROR: Enumerating API level " + e.getMessage());
		}
	}

	/*
	 * Checks to see if the device name belongs to an iCade device
	 */
	private static boolean iCadeDeviceName(String deviceName)
	{
		if (deviceName == null) return false;

		// iterate over all the IGamepadDeviceFactory in our list (do it backwards so the initial entries are the default ones)
		for( int i=msGamepadDeviceFactories.size()-1 ; i>=0; --i) {
			IGamepadDeviceFactory gdf = msGamepadDeviceFactories.get( i );
			if( gdf.IsiCadeDevice( deviceName )) {
				return true;
			} // end if
		} // end for
		 
		if (deviceName.contains(" 8-bitty ") || 
			deviceName.contains(" iCade ") ||
			deviceName.contains(NYKODevice.DeviceDescriptor)) // spoofs iCades if the switch is set
		{
			return true;
		}
		return false;
	}

	/**
	 * Performed during the Application's onStart/onResume
	 */
	 
	static BluetoothDevice Add_MogaDevice = null;
	@SuppressLint("MissingPermission")
	static public void CheckDeviceSupport(BluetoothDevice bd)
	{
	
	//	Log.i( "yoyo", "BluetoothDevice found - " + bd.getName() + " address " + bd.getAddress() + " uuids:" + bd.getUuids()+ " str:" + bd.toString()
		
	//	+ " hash:" + bd.hashCode()
	//	+" bondState:" + bd.getBondState()
		
	//	);

		// if device (or its name) is null then return...
		if (bd == null) return;
		if (bd.getName() == null) return;
	
		// if this is a Moga device then filter it...
		if (bd.getName().contains( "BD&A" ) || bd.getName().contains( "Moga Pro" )) {
		Log.i("yoyo","Found moga device with name " + bd.getName());
			Add_MogaDevice = bd;
			return;
		} // end if
		
		
		// iterate over all the IGamepadDeviceFactory in our list (do it backwards so the initial entries are the default ones)
		IGamepadDevice device = null;
		for( int i=msGamepadDeviceFactories.size()-1 ; i>=0; --i) {
			IGamepadDeviceFactory gdf = msGamepadDeviceFactories.get( i );
			
		//	Log.i( "yoyo", "Checking factory " + gdf.getClass().getName() + " against " + bd.getName());
	
			
			if( gdf.IsMyDescriptor( bd.getName() )) {
				device = gdf.Create( bd );
				break;
			} // end if
		} // end for
		
		if (bd.getName().contains(NYKODevice.DeviceDescriptor)) {			
			device = new NYKODevice(bd);
		}
		else if (bd.getName().contains(GameStickDevice.DeviceDescriptor)) {	
			device = new GameStickDevice(bd.getName());
		}
		
		if (device == null) {
			//Log.i("yoyo","Creating new generic device 234");
			device = new GenericDevice(bd.getName());
		} // end if
		
		msGamepadList.add( device );
		msGamepadAllocated.add( false );

		device.setBluetoothDevice(bd);
		Log.i("yoyo", "GAMEPAD: Registering device connected " + msGamepadList.size() + "," + device.ButtonCount() + "," + device.AxisCount());			
		RunnerJNILib.registerGamepadConnected(msGamepadList.size(), device.ButtonCount(), device.AxisCount());		
	}
	
	static public InputDevice findBySource(int sourceType) {
        int[] ids = InputDevice.getDeviceIds(); 

        // Return the first matching source we find...
		int i = 0;
        for (i = 0; i < ids.length; i++) {
			InputDevice dev = InputDevice.getDevice(ids[i]);
			int sources = dev.getSources();

			if ((sources & ~InputDevice.SOURCE_CLASS_MASK & sourceType) != 0) {
				return dev;
			}
        }
        
        return null;
	}
	
	//We know this won't be called on old api
	//@SuppressLint("InlinedApi")
	//noinspection AndroidLintInlinedApi
	static public InputDevice findJoystick() {
		return findBySource(InputDevice.SOURCE_JOYSTICK);
	}
	
	@SuppressLint("MissingPermission")
	static public void BluetoothDeviceConnected(BluetoothDevice bd)
	{
		// if device (or its name) is null then return...
		if (bd == null) return;
		if (bd.getName() == null) return;
	
		Log.i("yoyo","BluetoothDevice connected " + bd.getAddress());
				
		for (int n = 0; n < msGamepadList.size(); n++) 
		{		
			IGamepadDevice device = msGamepadList.get(n);
			if(device!=null)
			{
				String dadd = device.getAddress();
				if (dadd.equals( bd.getAddress())) 
				{
					//device.mConnected = true;
					
				//	Log.i("yoyo", "Found bluetoothdevice in our list of gamepads");
					return;
				} // end if
				//else
				//{
				//	Log.i("yoyo","slot " + n + " didn't match our address " +  bd.getAddress() + " to " + dadd);
				//}
				
			}
			//else
			//{
			//	Log.i("yoyo","null device in list");
			//}
		} // end fir
		//add it in if we haven't found it.
		
		CheckDeviceSupport(bd);
		
		//Log.i("yoyo","Unable to find bluetoothdevice in our list of gamepads");
		
		return;
		
		
	}
	
	static public void BluetoothDeviceDisconnected(BluetoothDevice bd)
	{
	
		Log.i("yoyo","BluetoothDevice disconnected " + bd.getAddress());
		
		
		for (int n = 0; n < msGamepadList.size(); n++) 
		{		
			IGamepadDevice device = msGamepadList.get(n);
			if(device!=null)
			{
				String dadd = device.getAddress();
				if (dadd.equals( bd.getAddress())) 
				{
					//device.mConnected = false;
					//Log.i("yoyo", "Found bluetoothdevice in our list of gamepads");
					return;
				} // end if
				//else
				//{
				//	Log.i("yoyo","slot " + n + " didn't match our address " +  bd.getAddress() + " to " + dadd);
				//}
				
			}
			//else
			//{
			//	Log.i("yoyo","null device in list");
			//}
		} // end fir
		
		
		//Log.i("yoyo","Unable to find bluetoothdevice in our list of gamepads");
		
		return;
	
	
	}
	

	/**
	 * Performed during the Application's onStart/onResume
	 */
	@SuppressLint("MissingPermission")
	static public void EnumerateDevices(IniBundle _prefs)
	{	
		// RK :: Only do this the first time...	
		if (msGamepadList.size() > 0 )
		{
			return;
		}
		
		// Set up the Gamepad devices - this should really discover all the IGamepadDeviceFactory classes
		// foreach( IGamepadDeviceFactory gdf in factories )
		//    msDescriptor2GamepadDevice.add( gdf );
		
		EnumerateAPILevel(); //Fritz - Need to do this even if we don't have iCadeSupport
		if ((_prefs != null) && (_prefs.getBoolean("YYiCadeSupport"))) {			

			if ((RunnerActivity.CurrentActivity.checkCallingOrSelfPermission("android.permission.BLUETOOTH") == PackageManager.PERMISSION_GRANTED))
			{
				

	
				// Filter the paired devices down to devices we support
				msGamepadList.clear();
				msGamepadAllocated.clear();

				if (BluetoothAdapter.getDefaultAdapter() != null) {
					Log.i("yoyo", "GAMEPAD: Bonded Bluetooth devices read");
					Set pairedDevices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
					Iterator iter = pairedDevices.iterator();
					while (iter.hasNext())
					{					
						BluetoothDevice bd = (BluetoothDevice)iter.next();
						String deviceName = bd.getName();

						Log.i("yoyo", "GAMEPAD: Found Bluetooth device " + deviceName);

						if (iCadeDeviceName(deviceName))
						{
							msiCadeList.add(bd);
						}
						// NB: It is entirely valid for certain devices to appear in both lists (see NYKO iCade spoofing)
						CheckDeviceSupport(bd);
					}
				} // end if
			}
			else {
				Log.i("yoyo", "ERROR: Bluetooth permission not available");
			}
		}
		else {
			Log.i("yoyo", "iCade Support in \"Global Game Settings/Android\" not selected");
		}

		// RK :: Code from nVidia to find joysticks (and gamepads) 
		boolean hasJoystickMethods = false;
		try {
			Method level12Method = KeyEvent.class.getMethod(
				"keyCodeToString", new Class[] { int.class } ); 
			hasJoystickMethods = (level12Method != null);
			Log.d("yoyo", "****** Found API level 12 function! Joysticks supported");
		} catch (NoSuchMethodException nsme) {
			Log.d("yoyo", "****** Did not find API level 12 function! Joysticks NOT supported!");
		}

		if (hasJoystickMethods)
		{
			InputDevice joystick = findJoystick();
			if ((joystick != null) && (!GamepadDeviceExists(joystick.getName()))) {
				Log.i("yoyo", "Joystick found - \"" + joystick.getName() + "\" source=" + joystick.getSources() );

				IGamepadDevice device = null;
				
				// iterate over all the IGamepadDeviceFactory in our list (do it backwards so the initial entries are the default ones)
				for( int i=msGamepadDeviceFactories.size()-1 ; i>=0; --i) {
					IGamepadDeviceFactory gdf = msGamepadDeviceFactories.get( i );
					if( gdf.IsMyDescriptor( joystick.getName() )) {
						device = gdf.CreateJoystickDevice( joystick.getName() );
					} // end if
				} // end for
				
				if (device == null) {
					if (joystick.getName().contains("nvidia_Corporation nvidia_joypad")) {
						 Log.i("yoyo", "Detected NVIDIA gamepad device");
						 device = new nVidiaShieldDevice(joystick.getName());
					}  // end if
					else if (joystick.getName().contains(GameStickDevice.DeviceDescriptor)) {	
						Log.i("yoyo", "Detected joystick gamepad device");
						device = new GameStickDevice(joystick.getName());
					}
					else {
						//Log.i("yoyo","Creating new generic device 567");
						 Log.i("yoyo", "Detected generic gamepad device: " + joystick.getName());
						 device = new GenericDevice(joystick.getName());
					} // end else
				} // end if
				
				if (device != null) {
					msGamepadList.add(device);
					msGamepadAllocated.add(false);
					RunnerJNILib.registerGamepadConnected(msGamepadList.size(), device.ButtonCount(), device.AxisCount());		
				} // end if
			} // end if
		} // end if
		
		if(Add_MogaDevice!=null)
			addMogaDevice(Add_MogaDevice);
			
		Log.i("yoyo", "GAMEPAD: Enumeration complete");
	}

	/*
	 * Works out how many devices GM should display as available
	 */
	static public int DeviceCount()
	{
		// Always count an iCade whether or not we've found one attached
		return msGamepadList.size() + 1;
	}

	static public void ClearGamepads()
	{
		msGamepadList.clear();
		msGamepadAllocated.clear();
	}

	/*
	 * Return the non-iCade device index at the GM level
	 */
	static private boolean GamepadDeviceExists(String deviceName)
	{
		boolean ret = false;
		for (int n = 0; n < msGamepadList.size(); n++) {		
			IGamepadDevice device = msGamepadList.get(n);
			if (device.getName().equals(deviceName)) {
				ret = true;
				break;
			} // end if
		} // end fir
		return ret;
	} // end GamepadDeviceExists
	
	/*
	 * Return the non-iCade device index at the GM level
	 */
	static private IGamepadDevice GetGamepadDevice(String deviceName, String deviceId)
	{
		// lets see if we have allocated this deviceId already???
		if (msDevice2Gamepad.containsKey(deviceId)) {
			return msDevice2Gamepad.get(deviceId);
		} // end if
	
	//	Log.i("yoyo", "msDevice2Gamepad:" + msDevice2Gamepad + " looking for " + deviceId);
	
		for (int n = 0; n < msGamepadList.size(); n++) {		

			if (!msGamepadAllocated.get(n)) {
				IGamepadDevice device = msGamepadList.get(n);
				if (device.getName().equals(deviceName)) {
					// remove this one from the list
					msGamepadAllocated.set(n, true );
					// add it to the hashmap for this deviceId
					msDevice2Gamepad.put( deviceId, device );
					
				//	Log.i("yoyo","putting into msDevice2Gamepad " + deviceId + " name " + device.getName());
					
					return device;
				}
				//else
				//{
				//	Log.i("yoyo","device.getName=" + device.getName() + " not equal to " +deviceName);
				//}
			} // end if
			//else
			//{
			//	IGamepadDevice device = msGamepadList.get(n);
			//	Log.i("yoyo","Gamepad already allocated " + device.getName());
			//}
		}
		
		Log.i( "yoyo", "GAMEPAD DEVICE not found! - " + deviceName + " registering it as a generic device");
		
		GenericDevice device = new GenericDevice(deviceName);

		Log.i("yoyo","buttoncount="+device.ButtonCount() + " axiscount=" + device.AxisCount()); 

		msGamepadList.add(device);
		msGamepadAllocated.add(false);
		RunnerJNILib.registerGamepadConnected(msGamepadList.size(), device.ButtonCount(), device.AxisCount());		

				
		return device;
	}

	/** 
	 * Return the name of a valid gamepad device if one is paired
	 */
@SuppressLint("MissingPermission")	 
	public static String GetDescriptor(int deviceIndex)
	{
		if (deviceIndex == 0) {

			if (msiCadeList.size() > 0) {
				return msiCadeList.get(0).getName();
			}
		}
		else {
			return msGamepadList.get(deviceIndex - 1).getName();
		}
		return "";
	}

	/**
	 * Respond to dispatchKeyEvent() from RunnerActivity
	 */
	static public void handleKeyEvent(KeyEvent ev)
	{		
		// Log.i("yoyo", "KEYEVENT: " + ev.getKeyCode() + " " + ev.keyCodeToString(ev.getKeyCode()));
		// Let iCade inputs override general gamepad handling
		if (!iCade.translateKeyEvent(ev)) 
		{
			if (msMinAPILevel >= 9) 
			{				
				try 
				{
					Object inputDevice = msGetDeviceMethod.invoke(ev);					
					Object deviceSources = msInputDeviceMethods.get("getSources").invoke(inputDevice);
					
					InputDevice id = ev.getDevice();
					
					// Nexus-7 volume controls identify themselves as a DPAD and a GAMEPAD and uses the name "gpio-keys"
					// Since GPIO stands for "General purpose input/output" I think we can assume anything identifying
					// as this can be reasonably ignored, whatever they claim themselves to also be
					if (id.getName().equals("gpio-keys")) {
					
						// Log.i("yoyo", "GAMEPAD: Device is 'gpio-keys'... ignoring");
						return;
					}

					// Log.i("yoyo", "GAMEPAD: Keyevent sourced from device with name:" + id.getName()+ /*" descriptor:" + id.getDescriptor() +*/ " desc:" + id.toString());
					if ((((Integer)deviceSources & SOURCE_CLASS_JOYSTICK) != 0) ||
						(((Integer)deviceSources & SOURCE_GAMEPAD) != 0) ||
						(((Integer)deviceSources & SOURCE_DPAD) != 0))
					{					
						// Work out the device index
						if(msMinAPILevel>=16)
						{
							IGamepadDevice device = GetGamepadDevice((String)msInputDeviceMethods.get("getName").invoke(inputDevice), (String)msInputDeviceMethods.get("getDescriptor").invoke(inputDevice));							
							if (device != null) {		
								device.onButtonUpdate(ev.getKeyCode(), ev.getAction() == KeyEvent.ACTION_DOWN);
							}
						}
						else
						{
							IGamepadDevice device = GetGamepadDevice((String)msInputDeviceMethods.get("getName").invoke(inputDevice),Integer.toString(ev.getDeviceId()));							
							if (device != null) {		
								device.onButtonUpdate(ev.getKeyCode(), ev.getAction() == KeyEvent.ACTION_DOWN);
							}
						}
					}
					//else
					//{
					//	Log.i("yoyo","keyevent devicesources failed to find anything "+ (Integer)deviceSources);
					//}
				}
				catch (Exception e) {
					Log.i("yoyo", "ERROR: " + e.getMessage());
				}
			}
			//else
			//	Log.i("yoyo","msminapilevel too low" +msMinAPILevel );
		}
		//else
		//{
		//	Log.i("yoyo","iCade consumed keyevent");
		//}		
	}

	
	
	static public void addMogaDevice(BluetoothDevice bd)
	{
		IGamepadDevice device = null;
		
		Object retobj = RunnerJNILib.CallExtensionFunction("MogaExtensionAndroid","MogaVersion",0,null);
		
		
		if(retobj!=null)
		{
			int mogaversion = (Integer)retobj;
			switch( mogaversion) {
			case 1:
				Log.d("yoyo", "Found Moga gamepad!!!");
				device = new MogaDevice( "Moga" );
				break;
			case 2:
				Log.d("yoyo", "Found Moga pro gamepad!!!");
				device = new MogaProDevice( "Moga Pro" );
				break;
			} // end switch
		}
		
		if (device != null) {
			device.mBluetoothDevice = bd;
			msGamepadList.add(device);
			msGamepadAllocated.add(false);
			RunnerJNILib.registerGamepadConnected(msGamepadList.size(), device.ButtonCount(), device.AxisCount());		
		} // end if
	} // end addMogaDevice

	
	//------------------------------------------------------------------------------------------------	
	//------------------------------------------------------------------------------------------------	
	
	/**
	 * Respond to dispatchGenericMotionEvent() from RunnerActivity
	 */
	static public void handleMotionEvent(MotionEvent ev)
	{		
		//Log.i("yoyo", "MOTIONEVENT: " + ev.toString() );
		
		try 
		{			
			if (msMinAPILevel >= 12) 				
			{
				// Something has changed for the InputDevice but we can't tell from the MotionEvent what
				// therefore we're just going to read out all the axes and update GM as such
				Object inputDevice = msGetDeviceMethod.invoke(ev);
				String deviceName = (String)msInputDeviceMethods.get("getName").invoke(inputDevice);	
				int deviceId = ev.getDeviceId();

				InputDevice id = ev.getDevice();
				// Work out the device index
				IGamepadDevice device;
				
				if(msMinAPILevel>=16)
				{			
					device  = GetGamepadDevice(deviceName, (String)msInputDeviceMethods.get("getDescriptor").invoke(id));				
				}
				else
				{
					device  = GetGamepadDevice(deviceName, Integer.toString(deviceId));	
				}
				if (device != null)
				{
					//Log.i("yoyo", "found device - " + deviceName + " our device " + device.getName() );
					List<Object> motionRanges = (List<Object>)(msInputDeviceMethods.get("getMotionRanges").invoke(inputDevice));
					
					//if(device.mConnected == false)
					//	device.mConnected = true;
					
					for (Object motionRange : motionRanges) {
	
						int rangeSource = (Integer)(msMotionRangeMethods.get("getSource").invoke(motionRange));					
				//		Log.i("yoyo","Checking motionRange for rangeSource " + rangeSource);
						if (((rangeSource & SOURCE_CLASS_JOYSTICK) != 0) ||
							((rangeSource & SOURCE_GAMEPAD) != 0))
						{
							int axisId = (Integer)msMotionRangeMethods.get("getAxis").invoke(motionRange);
							
							float axisValue = (Float)msMotionEventMethods.get("getAxisValue").invoke(ev, axisId);
							
							float axisRange = (Float)msMotionRangeMethods.get("getRange").invoke(motionRange);
							
							float axisMin = (Float)msMotionRangeMethods.get("getMin").invoke(motionRange);
							float axisMax = (Float)msMotionRangeMethods.get("getMax").invoke(motionRange);
							// debug output
						//	String axisName = (String)msMotionEventMethods.get("axisToString").invoke(ev, axisId);
						//	Log.i("yoyo", "handleMotionEvent axisId=" + axisId + " axisValue=" + axisValue + 
						//						" axisRange=" + axisRange + " axisMin=" + axisMin + " axisMax=" + axisMax + " calling onAxis Update for device:" + device.getName() );
							
							device.onAxisUpdate(axisId, axisValue, axisRange, axisMin, axisMax);
						}
					}
				}
			}			
		}
		catch (Exception e) {
			Log.i("yoyo", "ERROR: " + e.getMessage());
		}
	}

	/**
	 * Check for device availability
	 */
	static public boolean DeviceConnected(int deviceIndex)
	{
		if (deviceIndex == 0) {
			return (msiCadeList.size() != 0);
		}
		
		IGamepadDevice device = msGamepadList.get(deviceIndex - 1);
			
		if(device == null)
			return false;
			
		return true;//(device.mConnected);
	}

	/**
	 * Check for device availability
	 */
	public static float[] GetButtonValues(int deviceIndex)
	{		
		if (deviceIndex == 0) {
			// WTF, WTF? Don't ask an iCade this :(
			throw new IllegalArgumentException("iCade index not valid for GetButtonValues");
		}
		//return msGamepadList.get(deviceIndex - 1).GetButtonValues();
		IGamepadDevice device = msGamepadList.get(deviceIndex - 1);
		float[] ret = device.GetButtonValues();
		//String debug = "deviceIndex=" + deviceIndex + ", deviceName="+device.getName() + ", val={";
		//for( int n=0; n<ret.length; ++n) {
		//	if (n!=0) debug += ",";
		//	debug += ret[n];
		//} // end for
		//debug += "}";
		//Log.i( "yoyo", debug );
		return ret;
	}

	/**
	 * Check for device availability
	 */
	public static float[] GetAxesValues(int deviceIndex)
	{
		if (deviceIndex == 0) {
			// WTF, WTF? Don't ask an iCade this :(
			throw new IllegalArgumentException("iCade index not valid for GetButtonValues");
		}
		IGamepadDevice device = msGamepadList.get(deviceIndex - 1);
		float[] ret = device.GetAxesValues();
		//String debug = "deviceIndex=" + deviceIndex + ", deviceName="+device.getName() + ", val={";
		//for( int n=0; n<ret.length; ++n) {
		//	if (n!=0) debug += ",";
		//	debug += ret[n];
		//} // end for
		//debug += "}";
		//Log.i( "yoyo", debug );
		return ret;
	}

	public static int GetGamepadGMLMapping(int deviceIndex, int inputId)
	{
		if (deviceIndex == 0) {
			// iCade... no!??
			throw new IllegalArgumentException("iCade index not valid for GetGamepadGMLMapping");
		}
		IGamepadDevice device = msGamepadList.get(deviceIndex - 1);
		int ret = device.GetGMLMapping(inputId);
		//Log.i( "yoyo", "deviceIndex=" + deviceIndex + ", deviceName="+device.getName() + ", inputId=" + inputId + ", val=" + ret + " dev add:" +device.getAddress() );
		return ret;
	}
}