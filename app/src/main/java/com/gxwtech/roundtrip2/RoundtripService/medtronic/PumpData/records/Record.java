package com.gxwtech.roundtrip2.RoundtripService.medtronic.PumpData.records;

import android.os.Bundle;

import com.gxwtech.roundtrip2.RoundtripService.medtronic.PumpModel;
import com.gxwtech.roundtrip2.RoundtripService.medtronic.PumpTimeStamp;

abstract public class Record {
    private static final String TAG = "Record";
    protected PumpModel model;
    protected byte recordOp;
    //protected int length;
    protected int foundAtOffset;
    protected byte[] rawbytes = new byte[0];
    //protected String recordTypeName = this.getClass().getSimpleName();

    public String getRecordTypeName() { return this.getClass().getSimpleName(); }
    public String getShortTypeName() {
        return this.getClass().getSimpleName();
    }
    public void setPumpModel(PumpModel model) { this.model = model; }
    public int getFoundAtOffset() { return foundAtOffset; }

    public Record() {

    }

    public boolean parseWithOffset(byte[] data, PumpModel model, int foundAtOffset) {
        // keep track of where the record was found for later analysis
        this.foundAtOffset = foundAtOffset;
        if (data == null) {
            return false;
        }
        if (data.length < 1) {
            return false;
        }
        recordOp = data[0];
        return parseFrom(data,model);
    }

    public boolean parseFrom(byte[] data, PumpModel model) {
        return true;
    }

    public PumpTimeStamp getTimestamp() {
        return new PumpTimeStamp();
    }

    public int getLength() {
        return 1;
    }

    public byte getRecordOp() {
        return recordOp;
    }

    protected static int asUINT8(byte b) {
        return (b < 0) ? b + 256 : b;
    }

    public Bundle dictionaryRepresentation() {
        Bundle rval = new Bundle();
        writeToBundle(rval);
        return rval;
    }

    public boolean readFromBundle(Bundle in) {
        // length is determined at instantiation
        // record type name is "static"
        // opcode has already been read.
        return true;
    }

    public void writeToBundle(Bundle in) {
        in.putInt("length",getLength());
        in.putInt("foundAtOffset",foundAtOffset);
        in.putInt("_opcode",recordOp);
        in.putString("_type", getRecordTypeName());
        in.putString("_stype", getShortTypeName());
        in.putByteArray("rawbytes",rawbytes);
    }

}
