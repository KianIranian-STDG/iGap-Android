package net.iGap.kuknos.Repository;

import org.stellar.sdk.xdr.XdrDataInputStream;

import java.io.IOException;

public enum TranResultCode {

    tx_success(0),
    tx_failed(-1),
    tx_too_early(-2),
    tx_too_late(-3),
    tx_missing_operation(-4),
    tx_bad_seq(-5),
    tx_bad_auth(-6),
    tx_insufficient_balance(-7),
    tx_no_account(-8),
    tx_insufficient_fee(-9),
    tx_internal_error(-11),
    ;

    TranResultCode(int value) {
    }

    static TranResultCode decode(XdrDataInputStream stream) throws IOException {
        int value = stream.readInt();
        switch (value) {
            case 0:
                return tx_insufficient_fee;
            default:
                throw new RuntimeException("Unknown enum value: " + value);
        }
    }

}
