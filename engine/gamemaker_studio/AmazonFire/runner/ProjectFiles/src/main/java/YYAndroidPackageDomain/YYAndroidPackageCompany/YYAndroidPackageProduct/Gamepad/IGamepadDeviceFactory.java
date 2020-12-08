package ${YYAndroidPackageName};

import android.bluetooth.BluetoothDevice;

public abstract class IGamepadDeviceFactory
{
	public IGamepadDeviceFactory()
	{
	}
	
	public boolean IsiCadeDevice( String _descriptor )
	{
		return false;
	}
	
	public boolean IsJoystickDevice( String _descriptor )
	{
		return false;
	}

	public boolean IsMyDescriptor( String _descriptor )
	{
		return false;
	}
	
	public IGamepadDevice Create( BluetoothDevice _bd)
	{
		return null;
	}
	
	public IGamepadDevice CreateJoystickDevice( String _name )
	{
		return null;
	} // end CreaateJoystickDevice
};