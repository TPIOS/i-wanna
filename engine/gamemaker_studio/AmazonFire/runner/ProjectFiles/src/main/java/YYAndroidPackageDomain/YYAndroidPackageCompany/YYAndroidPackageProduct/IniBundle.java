package ${YYAndroidPackageName};

import org.ini4j.Ini;
import android.os.Bundle;
import java.io.InputStream;
import java.io.IOException;
import android.util.Log;

class IniBundle
{
	Bundle m_bundle;
	Ini.Section m_android;

	public IniBundle( Bundle _bundle, InputStream _ini ) {
		m_bundle = _bundle;
		m_android = null;

		if (_ini != null) {
			try {
				Ini ini = new Ini( _ini );
				m_android = ini.get( "Android" );
			} catch( IOException _e ) {
				Log.d( "yoyo", "INI exception " + _e.toString() );
			} // end catch
		} // end if
	} // end IniBundle

	public String getString( String _name )
	{
		String ret = null;
		if ((m_android != null) && m_android.containsKey(_name)) {
			ret = m_android.get( _name );
			// strip off any initial quotes we find
			if (ret.startsWith( "\"" ) && ret.endsWith( "\"" )) {
				ret = ret.substring( 1, ret.length() - 1 );
			} // end if
		} // end if
		else {
			ret = m_bundle.getString( _name );			
			if(ret == null)
			{
				int tst = m_bundle.getInt(_name,-9876543);
				if(tst!=-9876543)
				{
					ret = Integer.toString(tst);
				}
			}
		} // end else

		return ret;
	} // end getString

	public boolean getBoolean( String _name )
	{
		boolean ret = false;
		if ((m_android != null) && m_android.containsKey(_name)) {
			ret = Boolean.parseBoolean(m_android.get( _name ));
		} // end if
		else {
			ret = m_bundle.getBoolean( _name );
		} // end else
		return ret;
	} // end getBoolean

	public int getInt( String _name )
	{
		int ret = 0;
		if ((m_android != null) && m_android.containsKey(_name)) {
			ret = Integer.parseInt(m_android.get( _name ));
		} // end if
		else {
			ret = m_bundle.getInt( _name );
		} // end else
		return ret;
	} // end getInt
	
	public boolean keyExists( String _name)
	{
		if ((m_android != null) && m_android.containsKey(_name)) {
			return true;
		} // end if
		return m_bundle.containsKey(_name);
	}

	public boolean hasAndroidIni()
	{
		return (m_android != null);
	}

	public void setAndroidIni(Ini.Section ini)
	{
		m_android = ini;
	}

	public void setAndroidString(String _name, String _value)
	{
		if(m_android == null)
		{
			Log.d( "yoyo", "Could not setIniString - no INI file in current bundle.");
			return;
		}

		m_android.put(_name, _value);
	}

	public void setAndroidInt(String _name, int _value)
	{
		if(m_android == null)
		{
			Log.d( "yoyo", "Could not setIniString - no INI file in current bundle.");
			return;
		}

		m_android.put(_name, _value);
	}
}