package ${YYAndroidPackageName};

import android.app.Activity;
import android.view.ViewGroup;
import android.view.View;
import android.view.ViewParent;
import android.view.Display;
import android.widget.FrameLayout;
import android.widget.AbsoluteLayout;
import android.util.Log;
import android.graphics.Color;
import android.view.Gravity;
import com.yoyogames.runner.RunnerJNILib;


class AdvertisingBase implements IAdvertising
{
	public Activity		m_activity;
	public ViewGroup	m_viewGroup;
	public String[]		m_adDefinitions;
	public AdTypes[]		m_adTypes;
	public View			m_view;
	public boolean m_usetestads;

	public AdvertisingBase( Activity _activity, ViewGroup _viewGroup,boolean _usetestads) {
		m_activity = _activity;
		m_viewGroup = _viewGroup;
		m_adDefinitions = new String[ 10 ];
		m_adTypes = new AdTypes[ 10 ];
		m_view = null;
		m_usetestads = _usetestads;
	} // end AdvertisingBase

	static public AdTypes ConvertToAdType( int _type ) 
	{
		AdTypes ret = AdTypes.BANNER;
		switch( _type ) {
			case 0: ret = AdTypes.BANNER; break;
			case 1: ret = AdTypes.MRECT; break;
			case 2: ret = AdTypes.FULL_BANNER; break;
			case 3: ret = AdTypes.LEADERBOARD; break;
			case 4: ret = AdTypes.SKYSCRAPER; break;
			case 5: ret = AdTypes.INTERSTITIAL; break;
		} // end switch
		return ret;		
	} // end 

	public void on_iap_success(String _ident)
	{
	}
	public void on_iap_cancelled(String _ident)
	{
	}
	public void on_iap_failed(String _ident)
	{
	}

	public int getAdWidth( AdTypes _type )
	{
	
		float density = m_activity.getResources().getDisplayMetrics().density;
		float ret = 0;
		if(_type!=null)
		{
			switch( _type ){
			case BANNER:		ret = 320; break;
			case MRECT:			ret = 300; break;
			case FULL_BANNER:	ret = 468; break;
			case LEADERBOARD:	ret = 728; break;
			case SKYSCRAPER:	ret = 120; break;
			} //end switch
		}
		return (int)(ret * density);
	} // end getAdWidth

	public int getAdHeight( AdTypes _type )
	{
		float density = m_activity.getResources().getDisplayMetrics().density;
		float ret = 0;
		if(_type!=null)
		{
			switch( _type ){
			case BANNER:		ret = 53; break;
			case MRECT:			ret = 250; break;
			case FULL_BANNER:	ret = 60; break;
			case LEADERBOARD:	ret = 90; break;
			case SKYSCRAPER:	ret = 600; break;
			} //end switch
		}
		return (int)(ret * density);
	} // end getAdHeight

	public int getAdDisplayWidth(int _index )
	{
		if(_index<0 || _index >= m_adTypes.length)
			return 0;
	
		AdTypes _type = m_adTypes[ _index ];
		int w = getAdWidth( _type );
		Display d = m_activity.getWindowManager().getDefaultDisplay();
		
		int dw = RunnerJNILib.getGuiWidth();
	//	Log.i("yoyo","getGuiWidth returned:" + dw);
		
		return (w*d.getWidth())/dw;
	}

	public void onResume()
	{
	}
	
	public void onPause()
	{
	}

	public int getAdDisplayHeight(int _index )
	{
		if(_index<0 || _index >= m_adTypes.length)
			return 0;
			
		AdTypes _type = m_adTypes[ _index ];
		int h = getAdHeight( _type );
		Display d = m_activity.getWindowManager().getDefaultDisplay();
		int dh = RunnerJNILib.getGuiHeight();
		
	//	Log.i("yoyo","getGuiHeight returned:" + dh + " display height:" + d.getHeight());
	
	//	Log.i("yoyo","getGuiHeight returned:" + dh);
		return (h*d.getHeight())/dh;
		
	}
	
	public void enable_interstitial( int _index )
	{
		Log.i("yoyo","Interstitials not supported for this provider");
	}
	
	public void pc_badge_add(int _x, int _y, int _width, int _height, String _ident){};
	public void pc_badge_move(int _x, int _y, int _width, int _height){};
	public void pc_badge_hide(){};
	public void pc_badge_update(){};
	
	public void reward_callback(int funcid){};
	
	public void enable( int _x, int _y, int _index )
	{
		
		if( m_adTypes[_index]==AdTypes.INTERSTITIAL)
		{
			//Log.i("yoyo","ad type interstitial " +_index );	
			enable_interstitial(_index);
			return;
		}
	
		setView( _index );
		
	
		
	
		final View view = m_view;
		final IAdvertising iad = this;
		final int x = _x;
		final int y = _y;
		final int w = getAdWidth( m_adTypes[ _index ] );
		final int h = getAdHeight( m_adTypes[ _index ] );
		final int index = _index;
		
//		Log.i("yoyo","enabling ad mob ad of type " + m_adTypes[_index]+ " with width " + w + " height " +h );	
		
		if(w==0 ||h==0)
		{
			Log.i("yoyo","error enabling ad with index " +_index );	
			return;
		}
	    RunnerActivity.ViewHandler.post( new Runnable() {
    		public void run() {
    			
    			Log.i("yoyo","adding view for index "+ index); 
    			if(view.getParent()==null)
    			{
    				m_viewGroup.addView( view);
    			}
    		
    			AbsoluteLayout.LayoutParams params = new AbsoluteLayout.LayoutParams(  w, h,x,y);
			
				view.setLayoutParams( params );
				if(!m_usetestads)
					view.setBackgroundColor(Color.TRANSPARENT);
				else
					view.setBackgroundColor(Color.BLUE);
		
				view.requestLayout();
						
				iad.refresh( index );
			
				view.setVisibility( View.VISIBLE );     			
    		} // end run()
    	 });
	} // end enable


	public void move( int _x, int _y, int _index )
	{
		setView( _index );
			
		final View view = m_view;
		final int x = _x;
		final int y = _y;
		final int w = getAdWidth( m_adTypes[ _index ] );
		final int h = getAdHeight( m_adTypes[ _index ] );
		
	//	Log.i("yoyo","enabling ad mob ad with width " + w + " height " +h);	
		
		
	    RunnerActivity.ViewHandler.post( new Runnable() {
    		public void run() {
    	
				AbsoluteLayout.LayoutParams params = new AbsoluteLayout.LayoutParams(  w, h,x,y);
			
				view.setLayoutParams( params );
				
				if(!m_usetestads)
					view.setBackgroundColor(Color.TRANSPARENT);
				else
					view.setBackgroundColor(Color.BLUE);
		
				view.requestLayout();
			
				view.setVisibility( View.VISIBLE );     			
    		} // end run()
    	 });
	} // end enable

	public void event(String _ident)
	{
	}
	
	public void event_preload(String _ident)
	{
	
	}

	public void disable(int _index)
	{
		setView( _index );
		if (m_view != null) {
		
			final View view = m_view;
			RunnerActivity.ViewHandler.post( new Runnable() {
	    		public void run() {
					view.setVisibility( View.GONE );  
					ViewParent vp = view.getParent();
					
					if(vp!=null) 
					{ 			
						m_viewGroup.removeView(view);
					}
    			} // end run()
    		});
		} // end if
	} // end disable

	public void setup(String _userid)
	{
		//Log.i("yoyo","Base setup called");
	}
	
	public boolean interstitial_available()
	{
		return false;
	}
	public boolean interstitial_display()
	{
		return false;
	}
	
	public boolean engagement_available()
	{
	//Log.i("yoyo","Base engagemeent_avail called");
		return false;
	}
	public boolean engagement_active()
	{
		//Log.i("yoyo","Base engagemeent_active called");
		return false;
	}
	public void engagement_launch()
	{
	//	Log.i("yoyo","Base engagemeent_launch called");
	}

	public void setView( int _index )
	{
	} // end setView

	public void refresh(int _index)
	{
	} // end refresh

	// pass in definitions from activity
	public void define( int _index, String _key, AdTypes _type )
	{
		if ((_index >= 0) && (_index < m_adDefinitions.length)) {
			m_adDefinitions[ _index ] = _key;
			m_adTypes[ _index ] = _type;
		} // end if
	} // end define

	// os events
	public void pause()
	{
	} // end pause

	public void resume()
	{
	} // end resume
}