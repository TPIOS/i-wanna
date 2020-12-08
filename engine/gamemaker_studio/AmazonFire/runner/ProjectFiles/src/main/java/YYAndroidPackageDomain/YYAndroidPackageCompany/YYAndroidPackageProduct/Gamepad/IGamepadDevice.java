package ${YYAndroidPackageName};

import android.bluetooth.BluetoothDevice;
import android.annotation.SuppressLint;

public abstract class IGamepadDevice
{
	// See Gamepad_Class.h defines
	public static final int FACE1 = 0x8001;
	public static final int FACE2 = 0x8002;
	public static final int FACE3 = 0x8003;
	public static final int FACE4 = 0x8004;
	public static final int SHOULDER_LEFT = 0x8005;
	public static final int SHOULDER_RIGHT = 0x8006;
	public static final int SHOULDER_LEFT_BOTTOM = 0x8007;
	public static final int SHOULDER_RIGHT_BOTTOM = 0x8008;
	public static final int SELECT = 0x8009;
	public static final int START = 0x800A;
	public static final int STICK_LEFT = 0x800B;
	public static final int STICK_RIGHT = 0x800C;
	public static final int PAD_UP = 0x800D;
	public static final int PAD_DOWN = 0x800E;
	public static final int PAD_LEFT = 0x800F;
	public static final int PAD_RIGHT = 0x8010;
	public static final int AXIS_LEFT_HORIZONTAL = 0x8011;
	public static final int AXIS_LEFT_VERTICAL = 0x8012;
	public static final int AXIS_RIGHT_HORIZONTAL = 0x8013;
	public static final int AXIS_RIGHT_VERTICAL = 0x8014;

	protected BluetoothDevice mBluetoothDevice;
	public IGamepadDevice(BluetoothDevice device)
	{
		mBluetoothDevice = device;
	}
	@SuppressLint("MissingPermission")
	public String getName()
	{
		return mBluetoothDevice.getName();
	}
	
	public void setBluetoothDevice(BluetoothDevice bd)
	{
		mBluetoothDevice = bd;
	}
	
	
	public String getAddress()
	{
		if(mBluetoothDevice!=null)
			return mBluetoothDevice.getAddress();
		else
			return "null";
	}

	public abstract int ButtonCount();
	public abstract int AxisCount();
	public abstract void onButtonUpdate(int _keyCode, boolean _keyDown);
	public abstract void onAxisUpdate(int _axisId, float _axisValue, float _axisRange, float _axisMin, float _axisMax);
	public abstract float[] GetButtonValues();
	public abstract float[] GetAxesValues();
	public abstract int GetGMLMapping(int _constant);
	
};