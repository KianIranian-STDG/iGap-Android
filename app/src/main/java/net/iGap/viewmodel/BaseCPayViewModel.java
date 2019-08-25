package net.iGap.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import net.iGap.R;
import net.iGap.api.errorhandler.ErrorModel;
import net.iGap.api.errorhandler.ResponseCallback;

public abstract class BaseCPayViewModel<T> extends ViewModel implements ResponseCallback<T> {

    private MutableLiveData<Integer> msgToUser = new MutableLiveData<>();
    private MutableLiveData<String> msgToUserString = new MutableLiveData<>();
    private MutableLiveData<Boolean> loaderListener = new MutableLiveData<>();

    public MutableLiveData<Integer> getMessageToUser() {
        return msgToUser;
    }

    public MutableLiveData<String> getMessageToUserText() {
        return msgToUserString;
    }

    public MutableLiveData<Boolean> getLoaderListener() {
        return loaderListener;
    }

    @Override
    public void onError(ErrorModel error) {
        msgToUserString.setValue(error.getMessage());
        loaderListener.setValue(false);
    }

    @Override
    public void onFailed(boolean handShakeError) {
        msgToUser.setValue(R.string.server_do_not_response);
        loaderListener.setValue(false);
    }
}
