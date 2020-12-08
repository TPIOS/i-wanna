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

public class GameStickDevice extends IGamepadDevice
{
	public static final int AXIS_LEFT_X = 0;
	public static final int AXIS_LEFT_Y = 1;
	public static final int AXIS_RIGHT_X = 11;
	public static final int AXIS_RIGHT_Y = 14;
	public static final int AXIS_D_X = 15;
	public static final int AXIS_D_Y = 16;
	

	public static final String DeviceDescriptor = "GameStick Controller";		

	// Linked hash map guarantees a predictable mapping order
	LinkedHashMap<Integer, Float> mButtons = null;
	LinkedHashMap<Integer, Float> mAxes = null;
	String mDeviceName;

	public GameStickDevice(String _deviceName)
	{
		super(null);
		mDeviceName = _deviceName;

		//Log.i("yoyo","Creating gamestick device with name " + mDeviceName);

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

	private void SetupButtonMappings()
	{
		mButtons = new LinkedHashMap<Integer, Float>();
		
		mButtons.put(KeyEvent.KEYCODE_BUTTON_A,0.0f);
    	mButtons.put(KeyEvent.KEYCODE_BUTTON_B,0.0f);
    	mButtons.put(KeyEvent.KEYCODE_BUTTON_X,0.0f);
    	mButtons.put(KeyEvent.KEYCODE_BUTTON_Y,0.0f);
    	mButtons.put(KeyEvent.KEYCODE_BUTTON_R1,0.0f);
    	mButtons.put(KeyEvent.KEYCODE_BUTTON_L1,0.0f);
    	
    	mButtons.put(KeyEvent.KEYCODE_BUTTON_R1,0.0f);
    	
    	mButtons.put(KeyEvent.KEYCODE_BUTTON_START,0.0f);
    	
    	mButtons.put(KeyEvent.KEYCODE_BUTTON_MODE,0.0f);
    	mButtons.put(KeyEvent.KEYCODE_HOME,0.0f);
		
    	mButtons.put(KeyEvent.KEYCODE_DPAD_UP,0.0f);
    	mButtons.put(KeyEvent.KEYCODE_DPAD_DOWN,0.0f);
    	mButtons.put(KeyEvent.KEYCODE_DPAD_LEFT,0.0f);
    	mButtons.put(KeyEvent.KEYCODE_DPAD_RIGHT,0.0f);
    	mButtons.put(KeyEvent.KEYCODE_BUTTON_THUMBL,0.0f);
    	mButtons.put(KeyEvent.KEYCODE_BUTTON_THUMBR,0.0f);
    	mButtons.put(KeyEvent.KEYCODE_BACK,0.0f);
    	
    	
    	
	}

	private void SetupAxisMappings()
	{
		mAxes = new LinkedHashMap<Integer, Float>();
		mAxes.put(AXIS_LEFT_X, 0.0f);
		mAxes.put(AXIS_LEFT_Y, 0.0f);
		mAxes.put(AXIS_RIGHT_X, 0.0f);
		mAxes.put(AXIS_RIGHT_Y, 0.0f);
		mAxes.put(AXIS_D_X, 0.0f);
		mAxes.put(AXIS_D_Y, 0.0f);	
	
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
		//Log.i("yoyo","GameStickDevice onButtonUpdate keycode:" + _keyCode + " down:" + _keyDown);
		mButtons.put(_keyCode, (_keyDown ? 1.0f : 0.0f));
	}

	public void onAxisUpdate(int _axisId, float _axisValue, float _axisRange, float _axisMin, float _axisMax)
	{
		// Pull the axis value into the [-1..1] range
		float value = ((_axisValue - _axisMin) / _axisRange) * 2.0f - 1.0f;		
		mAxes.put(_axisId, value);
		
	
		if (_axisId == AXIS_D_X) {
			mButtons.put(KeyEvent.KEYCODE_DPAD_LEFT, (_axisValue < 0) ? 1f : 0f );
			mButtons.put(KeyEvent.KEYCODE_DPAD_RIGHT, (_axisValue > 0) ? 1f : 0f );
		} // end if
		if (_axisId == AXIS_D_Y) {
			mButtons.put(KeyEvent.KEYCODE_DPAD_UP, (_axisValue < 0) ? 1f : 0f );
			mButtons.put(KeyEvent.KEYCODE_DPAD_DOWN, (_axisValue > 0) ? 1f : 0f );
		} // end if
	}

	private float[] floatArray(Collection<Float> floatCollection)
	{		
		float[] floatArray = new float[floatCollection.size()];
		Iterator<Float> iter = floatCollection.iterator();

		int i = 0;
		while (iter.hasNext())
		{
		    Float f = iter.next();
		    floatArray[i++] = (f != null ? f : 0.0f);
		}
		return floatArray;
	}

	public float[] GetButtonValues()
	{
	//	Log.i("yoyo","GameStickDevice GetButtonValues called");
		Collection<Float> buttonValues = mButtons.values();
		return floatArray(buttonValues);			
	}

	public float[] GetAxesValues()
	{
		//Log.i("yoyo","GameStickDevice GetAxesValues called");
		Collection<Float> axesValues = mAxes.values();
		return floatArray(axesValues);		
	}

	private int GetMapIndex(LinkedHashMap _hashMap, Integer _key)
	{
		int index = 0;
		for (Object key : _hashMap.keySet()) {
			if (((Integer)key).equals(_key)) {
				Log.i("yoyo","GameStickDevice GetMapIndex called for key "+ _key + " returns "+index);
				return index;
			}
			index++;
		}
		//Log.i("yoyo","GameStickDevice GetMapIndex called for key "+ _key + " failed & returns -1");
		return -1;
	}

	public int GetGMLMapping(int _constant)
	{	
		int retValue = -1;
		switch (_constant)
		{
			default:
				break;
			
			case IGamepadDevice.FACE1:
				retValue = GetMapIndex(mButtons, KeyEvent.KEYCODE_BUTTON_A);
				break;
			case IGamepadDevice.FACE2:
				retValue = GetMapIndex(mButtons, KeyEvent.KEYCODE_BUTTON_B);
				break;
			case IGamepadDevice.FACE3:
				retValue = GetMapIndex(mButtons, KeyEvent.KEYCODE_BUTTON_X);
				break;
			case IGamepadDevice.FACE4:
				retValue = GetMapIndex(mButtons, KeyEvent.KEYCODE_BUTTON_Y);
				break;
			case IGamepadDevice.SHOULDER_LEFT:
				retValue = GetMapIndex(mButtons, KeyEvent.KEYCODE_BUTTON_L1);
				break;
			
			case STICK_LEFT:
				retValue = GetMapIndex(mButtons, KeyEvent.KEYCODE_BUTTON_THUMBL);
				break;
			case STICK_RIGHT:
				retValue = GetMapIndex(mButtons, KeyEvent.KEYCODE_BUTTON_THUMBR);
				break;
			
			case IGamepadDevice.SHOULDER_RIGHT:
				retValue = GetMapIndex(mButtons, KeyEvent.KEYCODE_BUTTON_R1);
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
				retValue = GetMapIndex(mAxes, AXIS_LEFT_X);
				break;
			case AXIS_LEFT_VERTICAL:
				retValue = GetMapIndex(mAxes, AXIS_LEFT_Y);
				break;
			case AXIS_RIGHT_HORIZONTAL:
				retValue = GetMapIndex(mAxes, AXIS_RIGHT_X);
				break;
			case AXIS_RIGHT_VERTICAL:
				retValue = GetMapIndex(mAxes, AXIS_RIGHT_Y);
				break;
		}
	//	Log.i("yoyo","GameStickDevice GetGMLMapping  called for value "+ _constant + " returns " +retValue);
		return retValue;
	}
}