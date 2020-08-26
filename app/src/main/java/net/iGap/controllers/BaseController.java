package net.iGap.controllers;

import net.iGap.helper.RequestManager;
import net.iGap.helper.upload.UploadManager;
import net.iGap.observers.eventbus.EventManager;

public class BaseController {
    private int currentAccount;

    public BaseController(int currentAccount) {
        this.currentAccount = currentAccount;
    }

    public EventManager getEventManager() {
        return EventManager.getInstance();
    }

    public MessageController getMessageController() {
        return MessageController.getInstance(currentAccount);
    }

    public MessageDataStorage getMessageDataStorage() {
        return MessageDataStorage.getInstance(currentAccount);
    }

    public RequestManager getRequestManager() {
        return RequestManager.getInstance(currentAccount);
    }

    public UploadManager getUploadManager() {
        return UploadManager.getInstance();
    }
}
