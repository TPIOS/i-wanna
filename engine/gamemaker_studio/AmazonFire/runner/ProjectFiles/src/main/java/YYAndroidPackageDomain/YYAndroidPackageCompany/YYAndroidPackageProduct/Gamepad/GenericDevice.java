package ${YYAndroidPackageName};

import android.bluetooth.BluetoothDevice;
import android.view.KeyEvent;
import android.view.KeyCharacterMap;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Iterator;
import android.annotation.SuppressLint;

import ${YYAndroidPackageName}.IGamepadDevice;

public class GenericDevice extends IGamepadDevice
{
	public static final int AXIS_X = 0;
	public static final int AXIS_Y = 1;
	public static final int AXIS_Z = 11;
	public static final int AXIS_RZ = 14;
	public static final int AXIS_GAS = 22;
	public static final int AXIS_BRAKE = 23;
	public static final int AXIS_HAT_X = 15;
	public static final int AXIS_HAT_Y = 16;
	public static final int AXIS_DISTANCE = 24;
	public static final int AXIS_GENERIC_1 = 32;
	public static final int AXIS_GENERIC_2 = 33;
	public static final int AXIS_GENERIC_3 = 34;
	public static final int AXIS_GENERIC_4 = 35;
	public static final int AXIS_GENERIC_5 = 36;
	public static final int AXIS_GENERIC_6 = 37;
	public static final int AXIS_GENERIC_7 = 38;
	public static final int AXIS_GENERIC_8 = 39;
	public static final int AXIS_GENERIC_9 = 40;
	public static final int AXIS_GENERIC_10 = 41;
	public static final int AXIS_GENERIC_11 = 42;
	public static final int AXIS_GENERIC_12 = 43;
	public static final int AXIS_GENERIC_13 = 44;
	public static final int AXIS_GENERIC_14 = 45;
	public static final int AXIS_GENERIC_15 = 46;
	public static final int AXIS_GENERIC_16 = 47;
	public static final int AXIS_HSCROLL = 10;
	public static final int AXIS_LTRIGGER = 17;
	public static final int AXIS_PRESSURE = 2;
	public static final int AXIS_ORIENTATION = 8;
	public static final int AXIS_RTRIGGER = 18;
	public static final int AXIS_RUDDER = 20;
	public static final int AXIS_RX = 12;
	public static final int AXIS_RY = 13;
	public static final int AXIS_SIZE = 3;
	public static final int AXIS_THROTTLE = 19;
	public static final int AXIS_TILT = 25;
	public static final int AXIS_TOOL_MAJOR = 6;
	public static final int AXIS_TOOL_MINOR = 7;
	public static final int AXIS_TOUCH_MAJOR = 4;
	public static final int AXIS_TOUCH_MINOR = 5;
	public static final int AXIS_VSCROLL= 9;
	public static final int AXIS_WHEEL = 21;


	public static final String DeviceDescriptor = "GENERIC DEVICE";		

	// Linked hash map guarantees a predictable mapping order
	LinkedHashMap<Integer, Float> mButtons = null;
	LinkedHashMap<Integer, Float> mAxes = null;
	String mDeviceName;

	public GenericDevice(String _deviceName)
	{
		super(null);
		mDeviceName = _deviceName;

		SetupButtonMappings();
		SetupAxisMappings();
	}
	@SuppressLint("MissingPermission")
	@Override
	public String getName()
	{
		if(mBluetoothDevice!=null)
			return mBluetoothDevice.getName();
	
		return mDeviceName;
	}
	@SuppressLint("NewApi")
    static int[] gamepadButtonMapping =
    {
    	KeyEvent.KEYCODE_BUTTON_1,
    	KeyEvent.KEYCODE_BUTTON_2,
    	KeyEvent.KEYCODE_BUTTON_3,
    	KeyEvent.KEYCODE_BUTTON_4,
    	KeyEvent.KEYCODE_BUTTON_5,
    	KeyEvent.KEYCODE_BUTTON_6,
    	KeyEvent.KEYCODE_BUTTON_7,
    	KeyEvent.KEYCODE_BUTTON_8,
    	KeyEvent.KEYCODE_BUTTON_9,
    	KeyEvent.KEYCODE_BUTTON_10,
    	KeyEvent.KEYCODE_BUTTON_11,
    	KeyEvent.KEYCODE_BUTTON_12,
    	KeyEvent.KEYCODE_BUTTON_13,
    	KeyEvent.KEYCODE_BUTTON_14,
    	KeyEvent.KEYCODE_BUTTON_15,
    	KeyEvent.KEYCODE_BUTTON_16,
    	KeyEvent.KEYCODE_BUTTON_A,
    	KeyEvent.KEYCODE_BUTTON_B,
    	KeyEvent.KEYCODE_BUTTON_C,
    	KeyEvent.KEYCODE_BUTTON_X,
    	KeyEvent.KEYCODE_BUTTON_Y,
    	KeyEvent.KEYCODE_BUTTON_Z,
    	KeyEvent.KEYCODE_BUTTON_L1,
    	KeyEvent.KEYCODE_BUTTON_L2,
    	KeyEvent.KEYCODE_BUTTON_R1,
    	KeyEvent.KEYCODE_BUTTON_R2,
    	KeyEvent.KEYCODE_BUTTON_START,
    	KeyEvent.KEYCODE_BUTTON_SELECT,
    	KeyEvent.KEYCODE_BUTTON_MODE,
    	KeyEvent.KEYCODE_BUTTON_THUMBL,
    	KeyEvent.KEYCODE_BUTTON_THUMBR,
    	KeyEvent.KEYCODE_HOME,
    	KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE,
    	KeyEvent.KEYCODE_MEDIA_REWIND,
    	KeyEvent.KEYCODE_MEDIA_FAST_FORWARD,
    };

	public void SetupButtonMappings()
	{
		mButtons = new LinkedHashMap<Integer, Float>();
		for (int j = 0; j < gamepadButtonMapping.length; j++) {
			int button = gamepadButtonMapping[j];
			//if (KeyCharacterMap.deviceHasKey(button)) 
			//{
				mButtons.put(button,0.0f);
			//	Log.i("yoyo","putting button "+ button);
			//}
			//else
			//{
			//	Log.i("yoyo","not putting button "+ button);
			//}
		}		
    	mButtons.put(KeyEvent.KEYCODE_DPAD_UP,0.0f);
    	mButtons.put(KeyEvent.KEYCODE_DPAD_DOWN,0.0f);
    	mButtons.put(KeyEvent.KEYCODE_DPAD_LEFT,0.0f);
    	mButtons.put(KeyEvent.KEYCODE_DPAD_RIGHT,0.0f);
    	mButtons.put(KeyEvent.KEYCODE_DPAD_CENTER,0.0f);
    	
    	//Log.i("yoyo","setting generic device button mappings");
    	
    	
	}

	public void SetupAxisMappings()
	{
		mAxes = new LinkedHashMap<Integer, Float>();
		mAxes.put(AXIS_BRAKE, 0.0f);
		mAxes.put(AXIS_DISTANCE, 0.0f);
		mAxes.put(AXIS_GAS, 0.0f);
		mAxes.put(AXIS_GENERIC_1, 0.0f);
		mAxes.put(AXIS_GENERIC_2, 0.0f);
		mAxes.put(AXIS_GENERIC_3, 0.0f);
		mAxes.put(AXIS_GENERIC_4, 0.0f);
		mAxes.put(AXIS_GENERIC_5, 0.0f);
		mAxes.put(AXIS_GENERIC_6, 0.0f);
		mAxes.put(AXIS_GENERIC_7, 0.0f);
		mAxes.put(AXIS_GENERIC_8, 0.0f);
		mAxes.put(AXIS_GENERIC_9, 0.0f);
		mAxes.put(AXIS_GENERIC_10, 0.0f);
		mAxes.put(AXIS_GENERIC_11, 0.0f);
		mAxes.put(AXIS_GENERIC_12, 0.0f);
		mAxes.put(AXIS_GENERIC_13, 0.0f);
		mAxes.put(AXIS_GENERIC_14, 0.0f);
		mAxes.put(AXIS_GENERIC_15, 0.0f);
		mAxes.put(AXIS_GENERIC_16, 0.0f);
		mAxes.put(AXIS_HAT_X, 0.0f);
		mAxes.put(AXIS_HAT_Y, 0.0f);
		mAxes.put(AXIS_HSCROLL, 0.0f);
		mAxes.put(AXIS_LTRIGGER, 0.0f);
		mAxes.put(AXIS_PRESSURE, 0.0f);
		mAxes.put(AXIS_ORIENTATION, 0.0f);
		mAxes.put(AXIS_RTRIGGER, 0.0f);
		mAxes.put(AXIS_RUDDER, 0.0f);
		mAxes.put(AXIS_RX, 0.0f);
		mAxes.put(AXIS_RY, 0.0f);
		mAxes.put(AXIS_RZ, 0.0f);
		mAxes.put(AXIS_SIZE, 0.0f);
		mAxes.put(AXIS_THROTTLE, 0.0f);
		mAxes.put(AXIS_TILT, 0.0f);
		mAxes.put(AXIS_TOOL_MAJOR, 0.0f);
		mAxes.put(AXIS_TOOL_MINOR, 0.0f);
		mAxes.put(AXIS_TOUCH_MAJOR, 0.0f);
		mAxes.put(AXIS_TOUCH_MINOR, 0.0f);
		mAxes.put(AXIS_VSCROLL, 0.0f);
		mAxes.put(AXIS_WHEEL, 0.0f);
		mAxes.put(AXIS_X, 0.0f);
		mAxes.put(AXIS_Y, 0.0f);
		mAxes.put(AXIS_Z, 0.0f);
		
		//Log.i("yoyo","setting generic axis mappings");
	}

	public int ButtonCount()
	{
		return mButtons.size();
	}

	public int AxisCount()
	{
		return mAxes.size();
	}

	public void onButtonUpdate(int _keyCode, boolean _keyDown)
	{
		mButtons.put(_keyCode, (_keyDown ? 1.0f : 0.0f));
		
	}

	public void onAxisUpdate(int _axisId, float _axisValue, float _axisRange, float _axisMin, float _axisMax)
	{
		// Pull the axis value into the [-1..1] range
		float value = ((_axisValue - _axisMin) / _axisRange) * 2.0f - 1.0f;		
		mAxes.put(_axisId, value);
		
		
		if (_axisId == AXIS_HAT_X) {
			mButtons.put(KeyEvent.KEYCODE_DPAD_LEFT, (_axisValue < 0) ? 1f : 0f );
			mButtons.put(KeyEvent.KEYCODE_DPAD_RIGHT, (_axisValue > 0) ? 1f : 0f );
		} // end if
		if (_axisId == AXIS_HAT_Y) {
			mButtons.put(KeyEvent.KEYCODE_DPAD_UP, (_axisValue < 0) ? 1f : 0f );
			mButtons.put(KeyEvent.KEYCODE_DPAD_DOWN, (_axisValue > 0) ? 1f : 0f );
		} // end if
		
	//	Log.i("yoyo","genericdevice onaxisupdate called for " + _axisId + " with value " + _axisValue);
		
		if (_axisId == AXIS_GAS) {
			float buttonValue = ((_axisValue - _axisMin) / _axisRange);
			mButtons.put(KeyEvent.KEYCODE_BUTTON_R2, buttonValue);
		}
		if (_axisId == AXIS_BRAKE) {
			float buttonValue = ((_axisValue - _axisMin) / _axisRange);
			mButtons.put(KeyEvent.KEYCODE_BUTTON_L2, buttonValue);
		}
	}

	private float[] floatArray(Collection<Float> floatCollection)
	{		
		float[] floatArray = new float[floatCollection.size()];
		Iterator<Float> iter = floatCollection.iterator();

		int i = 0;
		while (iter.hasNext())
		{
		    Float f = (Float)iter.next();
		    floatArray[i++] = (f != null ? f : 0.0f);
		}
		return floatArray;
	}

	public float[] GetButtonValues()
	{
		Collection<Float> buttonValues = mButtons.values();
		
				
		return floatArray(buttonValues);			
	}

	public float[] GetAxesValues()
	{
		Collection<Float> axesValues = mAxes.values();
		return floatArray(axesValues);		
	}

	private int GetMapIndex(LinkedHashMap _hashMap, Integer _key)
	{
		int index = 0;
		for (Object key : _hashMap.keySet()) {
			if (((Integer)key).equals(_key)) {
				return index;
			}
			index++;
		}
		return -1;
	}

	public int GetGMLMapping(int _constant)
	{	
	
		int retValue = -1;
		switch (_constant)
		{
			default:
			case STICK_LEFT:
				retValue = GetMapIndex(mButtons, KeyEvent.KEYCODE_BUTTON_THUMBL);
				break;
				
			case STICK_RIGHT:
				retValue = GetMapIndex(mButtons, KeyEvent.KEYCODE_BUTTON_THUMBR);
				break;

			case FACE1:
				retValue = GetMapIndex(mButtons, KeyEvent.KEYCODE_BUTTON_A);
				break;
			case FACE2:
				retValue = GetMapIndex(mButtons, KeyEvent.KEYCODE_BUTTON_B);
				break;
			case FACE3:
				retValue = GetMapIndex(mButtons, KeyEvent.KEYCODE_BUTTON_X);
				break;
			case FACE4:
				retValue = GetMapIndex(mButtons, KeyEvent.KEYCODE_BUTTON_Y);
				break;
			case SHOULDER_LEFT:
				retValue = GetMapIndex(mButtons, KeyEvent.KEYCODE_BUTTON_L1);
				break;
			case SHOULDER_RIGHT:
				retValue = GetMapIndex(mButtons, KeyEvent.KEYCODE_BUTTON_R1);
				break;
			case SHOULDER_LEFT_BOTTOM:				
				retValue = GetMapIndex(mButtons, KeyEvent.KEYCODE_BUTTON_L2);
				break;
			case SHOULDER_RIGHT_BOTTOM:				
				retValue = GetMapIndex(mButtons, KeyEvent.KEYCODE_BUTTON_R2);
				break;
			case SELECT:
				retValue = GetMapIndex(mButtons, KeyEvent.KEYCODE_BACK);
				break;
			case START:
				retValue = GetMapIndex(mButtons, KeyEvent.KEYCODE_BUTTON_START);
				break;
			case PAD_UP:
				retValue = GetMapIndex(mButtons, KeyEvent.KEYCODE_DPAD_UP);
				break;
			case PAD_DOWN:
				retValue = GetMapIndex(mButtons, KeyEvent.KEYCODE_DPAD_DOWN);
				break;
			case PAD_LEFT:
				retValue = GetMapIndex(mButtons, KeyEvent.KEYCODE_DPAD_LEFT);
				break;
			case PAD_RIGHT:
				retValue = GetMapIndex(mButtons, KeyEvent.KEYCODE_DPAD_RIGHT);
				break;

			case AXIS_LEFT_HORIZONTAL:
				retValue = GetMapIndex(mAxes, AXIS_X);
				break;
			case AXIS_LEFT_VERTICAL:
				retValue = GetMapIndex(mAxes, AXIS_Y);
				break;
			case AXIS_RIGHT_HORIZONTAL:
				retValue = GetMapIndex(mAxes, AXIS_Z);
				break;
			case AXIS_RIGHT_VERTICAL:
				retValue = GetMapIndex(mAxes, AXIS_RZ);
				break;
		}
		return retValue;
	}
}