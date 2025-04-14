package com.example;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

public class ScanPlugin extends CordovaPlugin {

    private static final String ACTION_SCAN_DATA = "com.service.scanner.data";

    private Activity activity;
    private CallbackContext callbackContext;
    private BroadcastReceiver scanReceiver;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        this.activity = cordova.getActivity();
        enableDefaultFormats();
        forceCordovaOutputMode();
        registerScanReceiver();
    }

    private void registerScanReceiver() {
        if (scanReceiver != null) return;

        IntentFilter filter = new IntentFilter(ACTION_SCAN_DATA);
        scanReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String code = intent.getStringExtra("ScanCode");
                String type = intent.getStringExtra("ScanCodeType");
                if (callbackContext != null) {
                    PluginResult result = new PluginResult(PluginResult.Status.OK, code);
                    result.setKeepCallback(true); // 👈 again, keep it alive for next scan
                    callbackContext.sendPluginResult(result);
                }
            }
        };
        activity.registerReceiver(scanReceiver, filter);
    }

    private void unregisterScanReceiver() {
        if (scanReceiver != null) {
            activity.unregisterReceiver(scanReceiver);
            scanReceiver = null;
        }
    }

    private void sendIntent(String action) {
        Intent intent = new Intent(action);
        intent.setComponent(new ComponentName(
                "com.hikrobotics.pdaservice",
                "com.pda.service.broadcast.LaunchReceiver"
        ));
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        activity.sendBroadcast(intent);
    }

    private void sendServiceControlIntent(String action, String key, Object value) {
        Intent intent = new Intent(action);
        intent.setComponent(new ComponentName(
                "com.hikrobotics.pdaservice",
                "com.pda.service.broadcast.ServiceControlReceiver"
        ));
        if (value instanceof String)
            intent.putExtra(key, (String) value);
        else if (value instanceof Boolean)
            intent.putExtra(key, (Boolean) value);
        else if (value instanceof Integer)
            intent.putExtra(key, (Integer) value);
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        activity.sendBroadcast(intent);
    }

    private void enableDefaultFormats() {
        Intent intent = new Intent("com.service.scanner.set.parameter.broadcast");
        intent.setComponent(new ComponentName(
                "com.hikrobotics.pdaservice",
                "com.pda.service.broadcast.ParameterSettingReceiver"
        ));
        intent.putExtra("ScanCode39", true);
        intent.putExtra("ScanCode128", true);
        intent.putExtra("ScanQRCode", true);
        intent.putExtra("ScanEAN13", true);
        intent.putExtra("ScanEAN8", true);
        intent.putExtra("ScanUPCA", true);
        intent.putExtra("ScanDataMatrix", true);
        intent.putExtra("ScanPDF417", true);
        intent.putExtra("ScanAudio", true);
        intent.putExtra("ScanVibrate", true);
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        activity.sendBroadcast(intent);
    }


    @Override
    public void onPause(boolean multitasking) {
        super.onPause(multitasking);
        unregisterScanReceiver();
    }

    @Override
    public void onResume(boolean multitasking) {
        super.onResume(multitasking);
        registerScanReceiver();
    } 

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

        switch (action) {
            case "register":
                this.callbackContext = callbackContext; // 👈 seulement ici !
                PluginResult pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
                pluginResult.setKeepCallback(true);
                callbackContext.sendPluginResult(pluginResult);
                return true;

            case "openScanner":
                sendIntent("com.service.scanner.start");
                callbackContext.success("openScanner");
                return true;

            case "closeScanner":
                sendIntent("com.service.scanner.stop");
                callbackContext.success("closeScanner");
                return true;

            case "startDecode":
                sendServiceControlIntent("com.service.scanner.start.read.code.broadcast", "customStartReadCodeBroadcast", "com.service.scanner.start.scanning");
                callbackContext.success("startDecode");
                return true;

            case "stopDecode":
                sendServiceControlIntent("com.service.scanner.stop.read.code.broadcast", "customStopReadCodeBroadcast", "com.service.scanner.stop.scanning");
                callbackContext.success("stopDecode");
                return true;

            case "enableDefaultFormats":
                enableDefaultFormats();
                callbackContext.success("Default symbologies enabled");
                return true;

            default:
                return false;
        }
    }

    private void forceCordovaOutputMode() {
        Intent intent = new Intent("com.service.scanner.outputway");
        intent.setComponent(new ComponentName(
                "com.hikrobotics.pdaservice",
                "com.pda.service.broadcast.ServiceControlReceiver"
        ));
        intent.putExtra("outputWay", "SYMBOL");
        intent.putExtra("scanCodePrefix", "");
        intent.putExtra("scanCodeSuffix", "");
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        activity.sendBroadcast(intent);
    }
}
