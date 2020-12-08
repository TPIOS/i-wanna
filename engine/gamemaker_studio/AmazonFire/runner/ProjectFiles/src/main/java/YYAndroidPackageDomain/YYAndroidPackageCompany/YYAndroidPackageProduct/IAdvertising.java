package ${YYAndroidPackageName};


public interface IAdvertising {
	public enum AdTypes
	{
		BANNER,
		MRECT,
		FULL_BANNER,
		LEADERBOARD,
		SKYSCRAPER,
		INTERSTITIAL
	}

	void enable( int _x, int _y, int _index );
	void move( int _x, int _y, int _index );
	void disable(int _index);
	void event(String _ident);
	void event_preload(String _ident);
	void refresh(int _index);
	void setView( int _index);
	int getAdDisplayHeight(int _index );
	int getAdDisplayWidth(int _index );
	
	void onResume();
	void onPause();
	
	void setup(String _userid);
	boolean engagement_available();
	boolean engagement_active();
	boolean interstitial_available();
	boolean interstitial_display();
	void engagement_launch();
	void on_iap_success(String _ident);
	void on_iap_cancelled(String _ident);
	void on_iap_failed(String _ident);
	
	
	
	void pc_badge_add(int _x, int _y, int _width, int _height, String _ident);
	void pc_badge_move(int _x, int _y, int _width, int _height);
	void pc_badge_hide();
	void pc_badge_update();
	
	void reward_callback(int _scriptid);

	// pass in definitions from 
	void define( int _index, String _key, AdTypes _type );

	// os events
	void pause();
	void resume();
}