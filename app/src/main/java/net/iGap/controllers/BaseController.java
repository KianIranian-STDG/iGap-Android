package net.iGap.controllers;

import net.iGap.helper.upload.UploadManager;
import net.iGap.module.ChatSendMessageUtil;
import net.iGap.module.downloader.Downloader;
import net.iGap.module.downloader.IDownloader;
import net.iGap.module.upload.IUpload;
import net.iGap.module.upload.Uploader;
import net.iGap.network.RequestManager;
import net.iGap.observers.eventbus.EventManager;

public class BaseController {
    public int currentAccount;

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

    public IDownloader getDownloader() {
        return Downloader.getInstance(currentAccount);
    }

    public IUpload getIUploader() {
        return Uploader.getInstance();
    }

    public ChatSendMessageUtil getSendMessageUtil() {
        return ChatSendMessageUtil.getInstance(currentAccount);
    }
}
