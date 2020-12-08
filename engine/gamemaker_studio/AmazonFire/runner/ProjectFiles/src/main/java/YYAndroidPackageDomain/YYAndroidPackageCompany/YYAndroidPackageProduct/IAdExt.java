package ${YYAndroidPackageName};

import android.content.Intent;

public interface IAdExt extends IExtensionBase 
{
	void enable( int _x, int _y, int _index );
	void move( int _x, int _y, int _index );
	void disable(int _index);
	void event(String _ident);
	void setup();
	
	int getAdDisplayHeight(int _index );
	int getAdDisplayWidth(int _index );
	//void onActivityResult(int requestCode, int resultCode, Intent data);
}