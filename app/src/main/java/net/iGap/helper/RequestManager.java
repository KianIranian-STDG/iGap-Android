package net.iGap.helper;

import android.util.Log;

import net.iGap.controllers.BaseController;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.observers.interfaces.OnResponse;
import net.iGap.request.AbstractObject;
import net.iGap.request.RequestQueue;
import net.iGap.request.RequestWrapper;

public class RequestManager extends BaseController {

    private static volatile RequestManager[] instance = new RequestManager[AccountManager.MAX_ACCOUNT_COUNT];
    private String TAG = getClass().getSimpleName();

    public static RequestManager getInstance(int account) {
        RequestManager localInstance = instance[account];
        if (localInstance == null) {
            synchronized (RequestManager.class) {
                localInstance = instance[account];
                if (localInstance == null) {
                    instance[account] = localInstance = new RequestManager(account);
                }
            }
        }
        return localInstance;
    }

    public RequestManager(int currentAccount) {
        super(currentAccount);
    }

    public String sendRequest(AbstractObject request, OnResponse onResponse) {
        String reqId = null;
        RequestWrapper wrapper = new RequestWrapper(request.getActionId(), request.getProtoObject(), null, onResponse);

        try {
            reqId = RequestQueue.sendRequest(wrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return reqId;
    }

    public void onUpdate(int actionId, Object protoObject) {
        Log.i(TAG, "onUpdate: " + actionId + " " + protoObject);

        if (actionId == -1 || protoObject == null) {
            return;
        }

        AbstractObject object = HelperFillLookUpClass.getInstance().getClassInstance(actionId);
        if (object != null) {
            AbstractObject finalMessage = object.deserializeResponse(actionId, protoObject);
            getMessageController().onUpdate(finalMessage);
        }
    }
}
