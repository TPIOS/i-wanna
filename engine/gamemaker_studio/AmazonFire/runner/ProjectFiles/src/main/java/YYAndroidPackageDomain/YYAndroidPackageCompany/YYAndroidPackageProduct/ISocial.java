package ${YYAndroidPackageName};

import android.content.Intent;

public interface ISocial extends IExtensionBase
{
	void Init();
	void LoadFriends();
	void GetInfo(String id);
	void Show(int type, String optarg, int numarg);
	void LoadPic(String id);
	void Event(String id);
	void LoadLeaderboard(String id, int minindex,int maxindex,int filter);
	void PostScore(String _leaderboard,int _score);

	void Login();
	void Logout();
	//void onActivityResult(int requestCode, int resultCode, Intent data);

}