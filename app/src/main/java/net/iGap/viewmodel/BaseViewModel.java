package net.iGap.viewmodel;

import net.iGap.api.apiService.BaseAPIViewModel;
import net.iGap.helper.RequestManager;
import net.iGap.module.accountManager.AccountManager;

public class BaseViewModel extends BaseAPIViewModel implements BaseViewHolder {
    public int currentAccount;

    public BaseViewModel() {
        onCreateViewModel();
        currentAccount = AccountManager.selectedAccount;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        onDestroyViewModel();
    }

    @Override
    public void onCreateViewModel() {

    }

    public RequestManager getRequestManager() {
        return RequestManager.getInstance(currentAccount);
    }
}
