package com.gxwtech.roundtrip2.RoundtripService.medtronic.PumpData.records;


import android.os.Bundle;

import com.gxwtech.roundtrip2.RoundtripService.medtronic.PumpModel;
import com.gxwtech.roundtrip2.RoundtripService.medtronic.PumpTimeStamp;
import com.gxwtech.roundtrip2.RoundtripService.medtronic.TimeFormat;

public class BolusNormalPumpEvent extends TimeStampedRecord {
    private final static String TAG = "BolusNormalPumpEvent";

    private double programmedAmount = 0.0;
    private double deliveredAmount = 0.0;
    private int duration = 0;
    private double unabsorbedInsulinTotal = 0.0;
    private String bolusType = "Unset";

    public BolusNormalPumpEvent() {
    }

    @Override
    public int getLength() { return PumpModel.isLargerFormat(model) ? 13 : 9; }

    @Override
    public String getShortTypeName() {
        return "Normal Bolus";
    }

    private double insulinDecode(int a, int b) {
        return ((a << 8) + b) / 40.0;
    }

    @Override
    public boolean parseFrom(byte[] data, PumpModel model) {
        if (getLength() > data.length) {
            return false;
        }
        if (PumpModel.isLargerFormat(model)) {
            programmedAmount = insulinDecode(asUINT8(data[1]),asUINT8(data[2]));
            deliveredAmount = insulinDecode(asUINT8(data[3]),asUINT8(data[4]));
            unabsorbedInsulinTotal = insulinDecode(asUINT8(data[5]),asUINT8(data[6]));
            duration = asUINT8(data[7]) * 30;
            timestamp = new PumpTimeStamp(TimeFormat.parse5ByteDate(data,8));
        } else {
            programmedAmount = asUINT8(data[1]) / 10.0f;
            deliveredAmount = asUINT8(data[2]) / 10.0f;
            duration = asUINT8(data[3]) * 30;
            unabsorbedInsulinTotal = 0;
            timestamp = new PumpTimeStamp(TimeFormat.parse5ByteDate(data,4));
        }

        bolusType = (duration > 0) ? "square" : "normal";
        return true;
    }

    @Override
    public boolean readFromBundle(Bundle in) {
        programmedAmount = in.getDouble("programmedAmount",0.0);
        deliveredAmount = in.getDouble("deliveredAmount",0.0);
        duration = in.getInt("duration",0);
        unabsorbedInsulinTotal = in.getDouble("unabsorbedInsulinTotal",0.0);
        bolusType = in.getString("bolusType","Unset");
        return super.readFromBundle(in);
    }

    @Override
    public void writeToBundle(Bundle in) {
        super.writeToBundle(in);
        in.putDouble("programmedAmount",programmedAmount);
        in.putDouble("deliveredAmount",deliveredAmount);
        in.putInt("duration",duration);
        in.putDouble("unabsorbedInsulinTotal",unabsorbedInsulinTotal);
        in.putString("bolusType",bolusType);
    }

}
