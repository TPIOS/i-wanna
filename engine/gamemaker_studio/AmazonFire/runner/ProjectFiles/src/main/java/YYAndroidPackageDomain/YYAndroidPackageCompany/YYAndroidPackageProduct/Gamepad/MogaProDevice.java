package ${YYAndroidPackageName};

import android.bluetooth.BluetoothDevice;
import android.view.KeyEvent;
import android.util.Log;
import android.annotation.SuppressLint;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Iterator;

import ${YYAndroidPackageName}.IGamepadDevice;

public class MogaProDevice extends IGamepadDevice
{
	public static final int AXIS_X = 0;
	public static final int AXIS_Y = 1;
	public static final int AXIS_Z = 11;
	public static final int AXIS_RZ = 14;
	public static final int AXIS_LTRIGGER = 17;
	public static final int AXIS_RTRIGGER = 18;
	public static final int AXIS_GAS = 22;
	public static final int AXIS_BRAKE = 23;
	public static final int AXIS_HAT_X = 15;
	public static final int AXIS_HAT_Y = 16;


	public static final String DeviceDescriptor = "Moga Pro HID";		

	// Linked hash map guarantees a predictable mapping order
	LinkedHashMap<Integer, Float> mButtons = null;
	LinkedHashMap<Integer, Float> mAxes = null;
	String mDeviceName;

	public MogaProDevice(String _deviceName)
	{
		super(null);
		mDeviceName = _deviceName;

		SetupButtonMappings();
		SetupAxisMappings();
		
		Gamepad.msMogaDevice = this;
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
		mButtons.put(KeyEvent.KEYCODE_DPAD_UP, 0.0f);
		mButtons.put(KeyEvent.KEYCODE_DPAD_DOWN, 0.0f);
		mButtons.put(KeyEvent.KEYCODE_DPAD_LEFT, 0.0f);
		mButtons.put(KeyEvent.KEYCODE_DPAD_RIGHT, 0.0f);
		mButtons.put(KeyEvent.KEYCODE_BUTTON_X, 0.0f);
		mButtons.put(KeyEvent.KEYCODE_BUTTON_Y, 0.0f);		
		mButtons.put(KeyEvent.KEYCODE_BUTTON_A, 0.0f);
		mButtons.put(KeyEvent.KEYCODE_BUTTON_B, 0.0f);
		mButtons.put(KeyEvent.KEYCODE_BUTTON_START, 0.0f);
		mButtons.put(KeyEvent.KEYCODE_BUTTON_SELECT, 0.0f);
		mButtons.put(KeyEvent.KEYCODE_BUTTON_L1, 0.0f);
		mButtons.put(KeyEvent.KEYCODE_BUTTON_R1, 0.0f);
		mButtons.put(KeyEvent.KEYCODE_BUTTON_L2, 0.0f);
		mButtons.put(KeyEvent.KEYCODE_BUTTON_R2, 0.0f);
		mButtons.put(KeyEvent.KEYCODE_BUTTON_THUMBL, 0.0f); 
		mButtons.put(KeyEvent.KEYCODE_BUTTON_THUMBR, 0.0f); 
	}

	private void SetupAxisMappings()
	{
		mAxes = new LinkedHashMap<Integer, Float>();
		mAxes.put(AXIS_X, 0.0f);
		mAxes.put(AXIS_Y, 0.0f);
		mAxes.put(AXIS_Z, 0.0f);
		mAxes.put(AXIS_RZ, 0.0f);
		mAxes.put(AXIS_LTRIGGER, 0.0f);
		mAxes.put(AXIS_RTRIGGER, 0.0f);
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
		
		if (_axisId == AXIS_LTRIGGER) {
			float buttonValue = ((_axisValue - _axisMin) / _axisRange);
			mButtons.put(KeyEvent.KEYCODE_BUTTON_R2, buttonValue);
		}
		if (_axisId == AXIS_RTRIGGER) {
			float buttonValue = ((_axisValue - _axisMin) / _axisRange);
			mButtons.put(KeyEvent.KEYCODE_BUTTON_L2, buttonValue);
		}
		if (_axisId == AXIS_HAT_X) {
			mButtons.put(KeyEvent.KEYCODE_DPAD_LEFT, (_axisValue < 0) ? 1f : 0f );
			mButtons.put(KeyEvent.KEYCODE_DPAD_RIGHT, (_axisValue > 0) ? 1f : 0f );
		} // end if
		if (_axisId == AXIS_HAT_Y) {
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
			case IGamepadDevice.SHOULDER_RIGHT:
				retValue = GetMapIndex(mButtons, KeyEvent.KEYCODE_BUTTON_R1);
				break;
			case IGamepadDevice.SHOULDER_RIGHT_BOTTOM:				//Fritz these seem to be other way round...
				retValue = GetMapIndex(mButtons, KeyEvent.KEYCODE_BUTTON_L2);
				break;
			case SHOULDER_LEFT_BOTTOM:				//Fritz see above
				retValue = GetMapIndex(mButtons, KeyEvent.KEYCODE_BUTTON_R2);
				break;
			case SELECT:
				retValue = GetMapIndex(mButtons, KeyEvent.KEYCODE_BUTTON_SELECT);
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