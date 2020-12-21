package net.iGap.observers.interfaces;

import net.iGap.network.AbstractObject;

public interface OnResponse {
    void onReceived(AbstractObject response, AbstractObject error);
}
