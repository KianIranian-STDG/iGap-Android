package net.iGap.network;

public abstract class AbstractObject {
    public String resId;

    public Object getProtoObject() {
        return null;
    }

    public int getActionId() {
        return 0;
    }

    public AbstractObject deserializeResponse(int constructor, byte[] message) {
        return null;
    }

    public void readParams(byte[] message) throws Exception {

    }

    public String getResId() {
        return resId;
    }
}
