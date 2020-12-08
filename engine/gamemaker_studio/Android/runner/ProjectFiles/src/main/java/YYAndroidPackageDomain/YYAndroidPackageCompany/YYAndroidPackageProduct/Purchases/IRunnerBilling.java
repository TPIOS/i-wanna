package ${YYAndroidPackageName};

import android.util.Log;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Vector;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.yoyogames.runner.RunnerJNILib;

public abstract class IRunnerBilling
{
	// Enums for the state of communication with the Android market, required to match up with the definition of enum eProductStoreState
	public static int eStoreLoadSuccess = 0;
	public static int eStoreLoadFailure = -1;

	/** Hand built constants for billing success/failure **/
	public static final int BILLING_PURCHASE_FAILED = -3;
	public static final int BILLING_PURCHASE_SUCCEEDED = 0;

	// All routines that need to be supplied by implementations of this class			
	public abstract void Destroy();
	public abstract void loadStore(String[] _productIds);
	public abstract void restorePurchasedItems();
	public abstract void purchaseCatalogItem(String productId, String payload, int _purchaseIndex);
	public abstract void consumeCatalogItem(String productId, String token);
	public abstract void getCatalogItemDetails(String productId);
    protected abstract String getContentPurchasedKey(String contentId);

	/** 
	 * Stores details of a purchase attempt for matching up with store resposses	 
	 */ 
	protected class PurchaseDetails
	{
		public String mProductId;
		public int mPurchaseIndex;
		PurchaseDetails(String _productId, int _purchaseIndex)
		{
			mProductId = _productId;
			mPurchaseIndex = _purchaseIndex;
		}
	};

	/**
	 * General interface for handling billing related callbacks
     */
    public interface IBillingCallback {
        /**
         * Called to notify that a billing operation is complete.
         */
        public void onComplete(int respondeCode, Object callbackData);
    }

	/**
	  * Responds to onActivityResult from the main Activity and deals with it if
	  * the Intent was created from a Billing Request of some sort or other
	  */
	public boolean handleActivityResult(int requestCode, int resultCode, Intent data)
	{
		return false;
	}

	/**
     * Facility to encode a string using MD5
     */
    public static String md5encode(String in)
    {
       	// MD5 this into a string
	    MessageDigest digest;
	    try {
	        digest = MessageDigest.getInstance("MD5");
	        digest.reset();
	        digest.update(in.getBytes());
	        byte[] a = digest.digest();
	        int len = a.length;
	        StringBuilder sb = new StringBuilder(len << 1);
	        for (int i = 0; i < len; i++)
	        {
	        	sb.append(Character.forDigit((a[i] & 0xf0) >> 4, 16));
	        	sb.append(Character.forDigit(a[i] & 0x0f, 16));
	        }
	        	
	        return sb.toString();
	    } 
	    catch (NoSuchAlgorithmException e) {
	        e.printStackTrace();
	    }
	    return "not_encoded";
	}

	/*
	 * iap_activate() leads here to enable the store and related services
	 */
	public void enableInAppPurchases(String[] _productIds)
	{
		// Load the store up
		final String[] productIds = _productIds;
	    RunnerActivity.ViewHandler.post(new Runnable() {
    		 public void run() 
    		 {
				Log.i("yoyo", "BILLING: Loading services...");				
			    loadStore(productIds);			 		
			}
		});
	}

	/**
	 * Stores in shared preferences whether or not a product has been successfully purchased
	 */
	protected void registerContentPurchased(String productId, boolean flag)
	{
		SharedPreferences prefs = RunnerActivity.CurrentActivity.getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor edit = prefs.edit();

        String contentKey = getContentPurchasedKey(productId);
        edit.putBoolean(contentKey, flag);
        edit.apply();
	}


	/** 
	 * Notify GML that the purchase attempt theoretically succeeded even though it didn't come through
	 * from a "normal" sequence of events so we don't have the full suite of information
	 */
	protected void PurchaseSuccessNotification(int _responseCode, String _productId, int _purchaseIndex)
	{
		final String productId = _productId;		
		final int responseCode = _responseCode;
		final int purchaseIndex = _purchaseIndex;

		RunnerActivity.ViewHandler.post(new Runnable() {
			public void run() {									
				String json = "{ \"productId\":\"" + productId + "\"" + 
					", \"response\":" + responseCode + 					
					", \"purchaseIndex\":" + purchaseIndex + 
					", \"purchaseState\":" + BILLING_PURCHASE_SUCCEEDED + " }";

				Log.i("yoyo", "BILLING: Purchase succeeded => " + json);
				RunnerJNILib.IAPProductPurchaseEvent(json);				
			}
		});
	}

	/** 
	 * Notify GML that the purchase attempt failed, given the response code, product Id and order details
	 */
	protected void PurchaseFailureNotification(int _responseCode, String _productId, int _purchaseIndex)
	{		
		final String productId = _productId;
		final int responseCode = _responseCode;
		final int purchaseIndex = _purchaseIndex;

		RunnerActivity.ViewHandler.post(new Runnable() {
			public void run() {									
				String json = "{ \"productId\":\"" + productId + "\"" + 
							  ", \"response\":" + responseCode + 
							  ", \"purchaseIndex\":" + purchaseIndex + 
							  ", \"purchaseState\":" + BILLING_PURCHASE_FAILED + " }";

				Log.i("yoyo", "BILLING: Purchase failed => " + json);
				RunnerJNILib.IAPProductPurchaseEvent(json);				
			}
		});
	}
}