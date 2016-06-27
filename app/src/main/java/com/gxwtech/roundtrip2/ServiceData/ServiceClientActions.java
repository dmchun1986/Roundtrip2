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

import org.joda.time.LocalDateTime;

import java.util.UUID;

/**
 * Created by geoff on 6/25/16.
 */
public class ServiceClientActions  {
    private static String TAG = "ServiceClientActions";
    private RoundtripServiceClientConnection roundtripServiceClientConnection;
    private Context context = MainApp.instance();

    /*
    *
    *  Functions to work with the RT2 Service
    *
     */
    public ServiceClientActions() {
        roundtripServiceClientConnection = new RoundtripServiceClientConnection(context);

        //Connect to the RT service
        doBindService();
    }
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
    public void sendBLEaccessGranted() { sendIPCMessage(RT2Const.IPC.MSG_BLE_accessGranted); }

    public void sendBLEaccessDenied() { sendIPCMessage(RT2Const.IPC.MSG_BLE_accessDenied); }

    // send one-liner message to RoundtripService
    public void sendIPCMessage(String ipcMsgType) {
        // Create a bundle with the data
        Bundle bundle = new Bundle();
        bundle.putString(RT2Const.IPC.messageKey, ipcMsgType);
        if (sendMessage(bundle)) {
            Log.d(TAG,"sendIPCMessage: sent "+ipcMsgType);
        } else {
            Log.e(TAG,"sendIPCMessage: send failed");
        }
    }

    /*
    *
    *  functions the client can call
    *
     */
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

    private boolean sendMessage(Bundle bundle) {
        return roundtripServiceClientConnection.sendMessage(bundle);
    }





    /*
     *     Set Temp Basal
     *
     *     inputs:
     *     amountUnitsPerHour - temp basal amount, in Units per hour
     *     durationMinutes - temp basal duration, in minutes
     *
     *     result: standard ok/error result
     */
    
    public static String makeRandomID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    public static ServiceCommand makeSetTempBasalCommand(double amountUnitsPerHour, int durationMinutes) {
        ServiceCommand command = new ServiceCommand("SetTempBasal",makeRandomID());
        Bundle b = command.getParameters();
        b.putDouble("amountUnitsPerHour",amountUnitsPerHour);
        b.putInt("durationMinutes",durationMinutes);
        command.setParameters(b);
        return command;
    }

    /*
     *  Read Basal Profile
     *
     *  inputs:
     *  which - "STD", "A", or "B"
     *
     *  result: an ok/error result with a basal profile Bundle.
     *  Get the profile using BasalProfile.initFromServiceResult()
     */

    // 'which' is "STD", "A", or "B"
    public static ServiceCommand makeReadBasalProfileCommand(String which) {
        ServiceCommand command = new ServiceCommand("ReadBasalProfile",makeRandomID());
        Bundle b = command.getParameters();
        b.putString("which",which);
        command.setParameters(b);
        return command;
    }

    public void makeReadPumpClockCommand() {
        ServiceCommand serviceCommand = new ServiceCommand("ReadPumpClock",makeRandomID());
        roundtripServiceClientConnection.sendServiceCommand(serviceCommand);
    }

    public static ServiceCommand makeSendBolusCommand(double amountUnits) {
        ServiceCommand command = new ServiceCommand("SendBolus",makeRandomID());
        Bundle b = command.getParameters();
        b.putDouble("amountInUnits",amountUnits);
        command.setParameters(b);
        return command;
    }

    public static ServiceCommand makeSetPumpClockCommand(LocalDateTime localDateTime) {
        ServiceCommand command = new ServiceCommand("SetPumpClock",makeRandomID());
        Bundle b = command.getParameters();
        b.putString("localDateTime",localDateTime.toString());
        command.setParameters(b);
        return command;
    }

    public static ServiceCommand makeReadISFProfileCommand() {
        return new ServiceCommand("ReadISFProfile",makeRandomID());
    }

    public static ServiceCommand makeReadBolusWizardCarbProfileCommand() {
        return new ServiceCommand("ReadBolusWizardCarbProfile",makeRandomID());
    }

    public static ServiceCommand makeReadDIASettingCommand() {
        return new ServiceCommand("ReadDIASetting",makeRandomID());
    }

    public static ServiceCommand makeReadBatteryLevelCommand() {
        return new ServiceCommand("ReadBatteryLevel",makeRandomID());
    }

    public static ServiceCommand makeReadReservoirLevelCommand() {
        return new ServiceCommand("ReadReservoirLevel",makeRandomID());
    }

}
