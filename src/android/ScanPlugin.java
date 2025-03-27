/**
 */
package com.easi.scannerhik;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.device.ScanDevice;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ScanPlugin extends CordovaPlugin {

  private Activity activity;
  private ScanDevice sm;
  private final static String SCAN_ACTION = "scan.rcv.message";
  private String barcodeStr;
  private CallbackContext callbackContext;

  private BroadcastReceiver mScanReceiver = null;

  private void initScan() {
    IntentFilter filter = new IntentFilter();
    filter.addAction(SCAN_ACTION);
    mScanReceiver = new BroadcastReceiver() {
	    public void onReceive(Context context, Intent intent) {

	        byte[] barocode = intent.getByteArrayExtra("barocode");
	        int barocodelen = intent.getIntExtra("length", 0);
	        byte temp = intent.getByteExtra("barcodeType", (byte) 0);
	        android.util.Log.i("debug", "----codetype--" + temp);
	        barcodeStr = new String(barocode, 0, barocodelen);
	        if (callbackContext != null){
	          callbackContext.success(barcodeStr);
	        }
	    }
	  };
    activity.registerReceiver(mScanReceiver, filter);
  }

  @Override
  public void initialize(CordovaInterface cordova, CordovaWebView webView) {
    super.initialize(cordova, webView);
    sm = new ScanDevice();
    this.activity = cordova.getActivity();
    this.initScan();
  }

  @Override
  public void onResume(boolean multitasking) {
    super.onResume(multitasking);
    this.initScan();
  }

  @Override
  public void onPause(boolean multitasking) {
    super.onPause(multitasking);
    if(sm != null) {
        sm.stopScan();
    }
    activity.unregisterReceiver(mScanReceiver);
  }

  @Override
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

    if (action.equals("register")) {
        this.callbackContext = callbackContext;
        return true;
    } else if (action.equals("openScanner")){
        sm.openScan();
        callbackContext.success("openScanner");
        return true;
    } else if (action.equals("closeScanner")){
        sm.closeScan();
        callbackContext.success("closeScanner");
        return true;
    } else if (action.equals("startDecode")){
        sm.openScan();
        sm.startScan();
        this.callbackContext = callbackContext;
        return true;
    } else if (action.equals("stopDecode")){
        sm.stopScan();
        callbackContext.success("stopDecode");
        return true;
    } else if (action.equals("start_continue")){
        sm.setScanLaserMode(4);
        this.callbackContext = callbackContext;
        return true;
    } else if (action.equals("stop_continue")){
        sm.setScanLaserMode(8);
        callbackContext.success("stop_continue");
        return true;
    }
    return false;
  }

}
