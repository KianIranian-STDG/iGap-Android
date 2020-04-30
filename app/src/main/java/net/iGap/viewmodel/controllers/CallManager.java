package net.iGap.viewmodel.controllers;

import net.iGap.helper.IGLog;

public class CallManager extends BaseController {

    private static volatile CallManager instance = null;

    public static CallManager getInstance() {
        CallManager localInstance = instance;
        if (localInstance == null) {
            synchronized (IGLog.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new CallManager();
                }
            }
        }
        return localInstance;
    }


    @Override
    public void cleanUp(boolean withListener) {

    }
}
