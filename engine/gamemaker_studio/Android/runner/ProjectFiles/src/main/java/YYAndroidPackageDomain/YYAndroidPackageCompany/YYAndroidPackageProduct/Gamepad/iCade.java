package ${YYAndroidPackageName};

import android.util.Log;
import android.view.KeyEvent;

import com.yoyogames.runner.RunnerJNILib;

public class iCade
{
	// The following must match up with IO/Android/GamepadM.cpp enum
	static final int face1 = 0;
    static final int face2 = 1;
    static final int face3 = 2;
    static final int face4 = 3;
    
    static final int shoulderL = 4;
    static final int shoulderR = 5;
    
    static final int shoulderLB = 6;
    static final int shoulderRB = 7;
    
	static final int joystickLeft = 8;
    static final int joystickRight = 9;
    static final int joystickUp = 10;
    static final int joystickDown = 11;
	

	static final int KEY_NONE = 0;
	static final int KEY_CODE_MASK = 0x00FF;
	static final int KEY_ACTION_MASK = 0xFF00;
	static final int KEY_ACTION_DOWN =  0x0100;
	static final int KEY_ACTION_UP = 0x0200;	

	// Mappings between iCade keys A-Z and Android game keys
	static final int[] keymap = new int[]
	{
		KEY_ACTION_DOWN | joystickLeft,						// A
		KEY_NONE, 
		KEY_ACTION_UP | joystickRight,						// C
		KEY_ACTION_DOWN | joystickRight,  					// D
		KEY_ACTION_UP | joystickUp,							// E
		KEY_ACTION_UP | face4,								// F
		KEY_ACTION_UP | shoulderLB,							// G
		KEY_ACTION_DOWN | face1,							// H
		KEY_ACTION_DOWN | shoulderL,						// I
		KEY_ACTION_DOWN | face2,							// J
		KEY_ACTION_DOWN | shoulderR,						// K
		KEY_ACTION_DOWN | shoulderRB,						// L
		KEY_ACTION_UP | shoulderL,							// M
		KEY_ACTION_UP | face2,								// N
		KEY_ACTION_DOWN | shoulderLB,						// O
		KEY_ACTION_UP | shoulderR,							// P
		KEY_ACTION_UP | joystickLeft,						// Q
		KEY_ACTION_UP | face1,								// R
		KEY_NONE,
		KEY_ACTION_UP | face3,								// T
		KEY_ACTION_DOWN | face4,							// U
		KEY_ACTION_UP | shoulderRB,							// V
		KEY_ACTION_DOWN | joystickUp,						// W
		KEY_ACTION_DOWN | joystickDown,						// X
		KEY_ACTION_DOWN | face3,							// Y
		KEY_ACTION_UP | joystickDown						// Z
	};

	/**
	 * Turns Bluetooth keyboard events into iCade buttons
	 * This means that if it's actually a keyboard attached
	 * it effectively spoofs an iCade as a GM Gamepad
	 */
	public static boolean translateKeyEvent(KeyEvent event)
	{
		int keyCode = event.getKeyCode();		

		// iCade events are only ever counted when it's a key down!
		// iCade button events translate to a set of keyboard letter presses
		if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
			(keyCode >= KeyEvent.KEYCODE_A) && 
			(keyCode <= KeyEvent.KEYCODE_Z))
		{			
			int result = keymap[keyCode - KeyEvent.KEYCODE_A];
			if (result != KEY_NONE)
			{
				RunnerJNILib.iCadeEventDispatch(result & KEY_CODE_MASK, (result & KEY_ACTION_MASK) == KEY_ACTION_DOWN);
				return true;
			}
		}
		return false;
	}
}
