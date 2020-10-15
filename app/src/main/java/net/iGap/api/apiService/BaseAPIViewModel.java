package net.iGap.api.apiService;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import net.iGap.module.accountManager.AccountManager;
import net.iGap.network.RequestManager;
import net.iGap.observers.interfaces.HandShakeCallback;

// base view model implements callback for repository and handle on fail and base onError.
// in other view model extends this you should override onSuccess and if have custom onError override it
public abstract class BaseAPIViewModel extends ViewModel implements HandShakeCallback {

    private MutableLiveData<Boolean> updateGooglePlay = new MutableLiveData<>(false);

    public MutableLiveData<Boolean> getUpdateGooglePlay() {
        return updateGooglePlay;
    }

    @Override
    public void onHandShake() {
        updateGooglePlay.setValue(true);
    }

    public RequestManager getRequestManager() {
        return RequestManager.getInstance(AccountManager.selectedAccount);
    }
}
