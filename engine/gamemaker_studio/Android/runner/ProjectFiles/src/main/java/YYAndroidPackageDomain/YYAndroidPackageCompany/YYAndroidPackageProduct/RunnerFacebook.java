package ${YYAndroidPackageName};



import android.content.Intent;
import com.yoyogames.runner.RunnerJNILib;
	
public class RunnerFacebook {

	public void initFacebook(String appID)
	{
		Object [] argArray = new Object[1];	
		argArray[0] = appID;
		
		RunnerJNILib.CallExtensionFunction("FacebookExtension","initFacebook",1,argArray);
	}
	
	
	public String getUserId()
	{
		return (String)RunnerJNILib.CallExtensionFunction("FacebookExtension","getUserId",0,null);
	}
	
	public String facebookLoginStatus() 
	{
		return (String)RunnerJNILib.CallExtensionFunction("FacebookExtension","facebookLoginStatus",0,null);
	}
		
	public void setupFacebook(String[] permissions) 
	{		
		Object [] argArray = new Object[1];	
		argArray[0] = permissions;
		
		
		RunnerJNILib.CallExtensionFunction("FacebookExtension","setupFacebook",1,argArray);
	}
  
	public boolean CheckPermission(String _permission)
	{
	
		Object [] argArray = new Object[1];	
		argArray[0] = _permission;
		
		Object ret = RunnerJNILib.CallExtensionFunction("FacebookExtension","CheckPermission",1,argArray);
		boolean bret = (Boolean)ret;
		return bret;
	
	
	}
  
	public int RequestPermissions( String[] _permissions, boolean _bPublishPermission )
	{
			
		Object [] argArray  = new Object[2];	
		argArray[0] = _permissions;
		argArray[1] = _bPublishPermission;
		
		int retval = (Integer)RunnerJNILib.CallExtensionFunction("FacebookExtension","RequestPermissions",2,argArray);
		return retval;
		
	}
	public void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
	
		Object [] argArray  = new Object[3];	
		argArray[0] = requestCode;
		argArray[1] = resultCode;
		argArray[2] = data;
		
		RunnerJNILib.CallExtensionFunction("FacebookExtension","onActivityResult",3,argArray);
	
	}
   
	public void onResume()
	{
		 RunnerJNILib.CallExtensionFunction("FacebookExtension","onResume",0,null);
	}
	
	public String getAccessToken()
	{
		return (String)RunnerJNILib.CallExtensionFunction("FacebookExtension","getAccessToken",0,null);
	}
	
	public void logout()
	{
		RunnerJNILib.CallExtensionFunction("FacebookExtension","logout",0,null);
	}
	
	public void graphRequest(String _graphPath, String _httpMethod, String[] _keyValuePairs, int _dsMapResponse)
	{
		Object [] argArray = new Object[4];	
		argArray[0] = _graphPath;
		argArray[1] = _httpMethod;
		argArray[2] = _keyValuePairs;
		argArray[3] = _dsMapResponse;
		
		RunnerJNILib.CallExtensionFunction("FacebookExtension","graphRequest",4,argArray);
	
	
	} 
	
	public void dialog(String dialogType, String[] keyValuePairs, int dsMapResponse) 
	{
		Object [] argArray = new Object[3];	
		argArray[0] = dialogType;
		argArray[1] = keyValuePairs;
		argArray[2] = dsMapResponse;
		
		
		RunnerJNILib.CallExtensionFunction("FacebookExtension","dialog",3,argArray);
	
	}

	
	public void inviteDialog(String dialogType, String[] keyValuePairs, int dsMapResponse) 
	{
		Object [] argArray = new Object[3];	
		argArray[0] = dialogType;
		argArray[1] = keyValuePairs;
		argArray[2] = dsMapResponse;
		
		
		RunnerJNILib.CallExtensionFunction("FacebookExtension","inviteDialog",3,argArray);
	}
	
	
  
}
