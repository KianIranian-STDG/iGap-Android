package net.iGap.viewmodel.controllers;

import net.iGap.model.AccountUser;
import net.iGap.module.accountManager.AccountManager;

public abstract class BaseController {
    private AccountUser currentUser;
    private long currentUserId;

    public BaseController() {
        currentUser = AccountManager.getInstance().getCurrentUser();
        currentUserId = currentUser.getId();
    }

    public abstract void cleanUp(boolean withListener);
}
