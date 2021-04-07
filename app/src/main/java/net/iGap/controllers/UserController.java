package net.iGap.controllers;

import net.iGap.module.accountManager.AccountManager;

public class UserController extends BaseController {

    private static volatile UserController[] instance = new UserController[AccountManager.MAX_ACCOUNT_COUNT];
    private String TAG = getClass().getSimpleName();

    public static UserController getInstance(int account) {
        UserController localInstance = instance[account];
        if (localInstance == null) {
            synchronized (UserController.class) {
                localInstance = instance[account];
                if (localInstance == null) {
                    instance[account] = localInstance = new UserController(account);
                }
            }
        }
        return localInstance;
    }

    public UserController(int currentAccount) {
        super(currentAccount);
    }
}
