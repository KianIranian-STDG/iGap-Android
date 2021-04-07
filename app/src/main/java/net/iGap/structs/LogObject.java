package net.iGap.structs;

import net.iGap.proto.ProtoGlobal;

public class LogObject {
    public int extraTypeValue;
    public long userId;
    public int typeValue;
    public byte[] data;

    private LogObject() {

    }

    public static LogObject create(ProtoGlobal.RoomMessageLog log) {
        LogObject logObject = new LogObject();
        logObject.extraTypeValue = log.getExtraTypeValue();
        logObject.userId = log.getTargetUser().getId();
        logObject.typeValue = log.getTypeValue();
        return logObject;
    }


    public static LogObject create(byte[] logData) {
        LogObject logObject = new LogObject();
        logObject.data = logData;
        return logObject;
    }
}
