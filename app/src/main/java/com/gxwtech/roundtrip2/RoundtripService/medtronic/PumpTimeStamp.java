package com.gxwtech.roundtrip2.RoundtripService.medtronic;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

/**
 * Created by geoff on 6/4/16.
 * Exists to easily merge 2 byte timestamps and 5 byte timestamps.
 */
public class PumpTimeStamp {
    private LocalDateTime localDateTime;
    public PumpTimeStamp() {
        localDateTime = new LocalDateTime(1973,1,1,1,1);
    }
    public PumpTimeStamp(String stringRepresentation) { localDateTime.parse(stringRepresentation); }
    public PumpTimeStamp(LocalDate localDate) {
        localDateTime = new LocalDateTime(localDate);
    }
    public PumpTimeStamp(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }
    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }
    @Override
    public String toString() {
        return getLocalDateTime().toString();
    }

}
