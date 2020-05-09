package net.iGap.model;

import java.util.HashMap;

public class OperatorType {

    public enum Type {
        HAMRAH_AVAL, IRANCELL, RITEL
    }

    private HashMap<String, Type> phoneMap = new HashMap<String, Type>() {
        {
            put("0910", Type.HAMRAH_AVAL);
            put("0911", Type.HAMRAH_AVAL);
            put("0912", Type.HAMRAH_AVAL);
            put("0913", Type.HAMRAH_AVAL);
            put("0914", Type.HAMRAH_AVAL);
            put("0915", Type.HAMRAH_AVAL);
            put("0916", Type.HAMRAH_AVAL);
            put("0917", Type.HAMRAH_AVAL);
            put("0918", Type.HAMRAH_AVAL);
            put("0919", Type.HAMRAH_AVAL);
            put("0990", Type.HAMRAH_AVAL);
            put("0991", Type.HAMRAH_AVAL);
            put("0992", Type.HAMRAH_AVAL);
            put("0994", Type.HAMRAH_AVAL);
            put("09930", Type.HAMRAH_AVAL);
            put("09931", Type.HAMRAH_AVAL);

            put("0901", Type.IRANCELL);
            put("0902", Type.IRANCELL);
            put("0903", Type.IRANCELL);
            put("0904", Type.IRANCELL);
            put("0905", Type.IRANCELL);
            put("0930", Type.IRANCELL);
            put("0933", Type.IRANCELL);
            put("0935", Type.IRANCELL);
            put("0936", Type.IRANCELL);
            put("0937", Type.IRANCELL);
            put("0938", Type.IRANCELL);
            put("0939", Type.IRANCELL);

            put("0920", Type.RITEL);
            put("0921", Type.RITEL);
            put("0922", Type.RITEL);

        }
    };

    public Type getOperation(String phoneNumber) {
        return phoneMap.get(phoneNumber);
    }

    public boolean isValidType(String phoneNumber) {
        Type tmp = phoneMap.get(phoneNumber);
        if (tmp != null) {
            return tmp == Type.HAMRAH_AVAL || tmp == Type.RITEL || tmp == Type.IRANCELL;
        }
        return false;
    }

    public boolean isMci(String phoneNumber) {
        Type tmp = phoneMap.get(phoneNumber);
        if (tmp != null) {
            return tmp == Type.HAMRAH_AVAL;
        }
        return false;
    }
}
