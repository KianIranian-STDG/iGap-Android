package net.iGap.kuknos.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

public class KuknosRecieveVM extends ViewModel {

    MutableLiveData<String> clientKey;
    MutableLiveData<String> qrCodeURl;
    // TODO get data
    // we will get byte array and we should save it and then use it -> refer to register fragment

    public KuknosRecieveVM() {
        if (clientKey == null) {
            clientKey = new MutableLiveData<>();
            //TODO hard code here
            clientKey.setValue("amini");
        }
        if (qrCodeURl == null) {
            qrCodeURl = new MutableLiveData<>();
        }
    }

    // Setter and Getter

    public MutableLiveData<String> getClientKey() {
        return clientKey;
    }

    public void setClientKey(MutableLiveData<String> clientKey) {
        this.clientKey = clientKey;
    }

    public MutableLiveData<String> getQrCodeURl() {
        return qrCodeURl;
    }

    public void setQrCodeURl(MutableLiveData<String> qrCodeURl) {
        this.qrCodeURl = qrCodeURl;
    }
}
