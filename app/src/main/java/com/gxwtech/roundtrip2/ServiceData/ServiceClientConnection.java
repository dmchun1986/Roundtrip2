package com.gxwtech.roundtrip2.ServiceData;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.util.Log;

import com.gxwtech.roundtrip2.MainApp;
import com.gxwtech.roundtrip2.RT2Const;
import com.gxwtech.roundtrip2.RoundtripService.RoundtripService;
import com.gxwtech.roundtrip2.RoundtripServiceClientConnection;

/**
 * Created by Tim on 27/06/2016.
 * Object that interfaces with the RT2 Service and Client Actions
 */
public class ServiceClientConnection {

    private static String TAG = "ServiceClientConnection";
    private RoundtripServiceClientConnection roundtripServiceClientConnection;
    private Context context = MainApp.instance();

    public ServiceClientConnection() {
        roundtripServiceClientConnection = new RoundtripServiceClientConnection(context);

        //Connect to the RT service
        doBindService();
    }

    /*
    *
    *  Functions to work with the RT2 Service
    *
    */
    private void doBindService() {
        context.bindService(new Intent(context,RoundtripService.class),
                roundtripServiceClientConnection.getServiceConnection(),
                Context.BIND_AUTO_CREATE);
        Log.d(TAG,"doBindService: binding.");
    }
    private void doUnbindService() {
        ServiceConnection conn = roundtripServiceClientConnection.getServiceConnection();
        roundtripServiceClientConnection.unbind();
        context.unbindService(conn);
        Log.d(TAG,"doUnbindService: unbinding.");
    }

    // send one-liner message to RoundtripService
    private void sendIPCMessage(String ipcMsgType) {
        // Create a bundle with the data
        Bundle bundle = new Bundle();
        bundle.putString(RT2Const.IPC.messageKey, ipcMsgType);
        if (sendMessage(bundle)) {
            Log.d(TAG,"sendIPCMessage: sent "+ipcMsgType);
        } else {
            Log.e(TAG,"sendIPCMessage: send failed");
        }
    }

    private boolean sendMessage(Bundle bundle) {
        return roundtripServiceClientConnection.sendMessage(bundle);
    }

    /*
    *
    *  functions the client can call
    *
     */
    public void sendBLEaccessGranted() { sendIPCMessage(RT2Const.IPC.MSG_BLE_accessGranted); }

    public void sendBLEaccessDenied() { sendIPCMessage(RT2Const.IPC.MSG_BLE_accessDenied); }

    public void sendBLEuseThisDevice(String address) {
        Bundle bundle = new Bundle();
        bundle.putString(RT2Const.IPC.messageKey, RT2Const.IPC.MSG_BLE_useThisDevice);
        bundle.putString(RT2Const.IPC.MSG_BLE_useThisDevice_addressKey,address);
        sendMessage(bundle);
        Log.d(TAG,"sendIPCMessage: (use this address) "+address);
    }

    public void sendPUMP_useThisDevice(String pumpIDString) {
        Bundle bundle = new Bundle();
        bundle.putString(RT2Const.IPC.messageKey, RT2Const.IPC.MSG_PUMP_useThisAddress);
        bundle.putString(RT2Const.IPC.MSG_PUMP_useThisAddress_pumpIDKey,pumpIDString);
        sendMessage(bundle);
        Log.d(TAG,"sendPUMP_useThisDevice: " + pumpIDString);
    }

    public void doTunePump(){
        sendIPCMessage(RT2Const.IPC.MSG_PUMP_tunePump);
    }

    public void getHistory(){
        sendIPCMessage(RT2Const.IPC.MSG_PUMP_fetchHistory);
    }

    public void getSavedHistory(){
        sendIPCMessage(RT2Const.IPC.MSG_PUMP_fetchSavedHistory);
    }

    public void setTempBasal(double amountUnitsPerHour, int durationMinutes, int uid) {
        ServiceCommand command = ServiceClientActions.makeSetTempBasalCommand(amountUnitsPerHour,durationMinutes);
        roundtripServiceClientConnection.sendServiceCommand(command);
    }

    public void readPumpClock() {
        ServiceCommand command = ServiceClientActions.makeReadPumpClockCommand();
        roundtripServiceClientConnection.sendServiceCommand(command);
    }
}
