package net.iGap.helper;

import net.iGap.proto.ProtoUserUpdateStatus;
import net.iGap.request.RequestUserUpdateStatus;

public class UserStatusController implements RequestUserUpdateStatus.onUserStatus {

    private static final UserStatusController ourInstance = new UserStatusController();

    public static UserStatusController getInstance() {
        return ourInstance;
    }

    private final Object mutex;
    private boolean isOnlineRequestPending;
    private boolean isOfflineRequestPending;

    private int offlineTry;
    private int onlineTry;

    private UserStatusController() {
        this.mutex = new Object();
        this.offlineTry = 0;
        this.onlineTry = 0;
    }

    public void setOnline() {
        synchronized (mutex) {
            if (isOnlineRequestPending) {
                return;
            }

            if (isOfflineRequestPending) {
                offlineTry = 16;
                LooperThreadHelper.getInstance().getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setOnline();
                    }
                }, 1000);
                return;
            }

            onlineTry = onlineTry + 1;
            if (onlineTry > 20) {
                onlineTry = 0;
                return;
            }

            isOnlineRequestPending = new RequestUserUpdateStatus().userUpdateStatus(ProtoUserUpdateStatus.UserUpdateStatus.Status.ONLINE, this);
            if (!isOnlineRequestPending) {
                LooperThreadHelper.getInstance().getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setOnline();
                    }
                }, 1000);
            }
        }
    }

    public void setOffline() {
        synchronized (mutex) {
            if (isOfflineRequestPending) {
                return;
            }

            if (isOnlineRequestPending) {
                onlineTry = 21;
                LooperThreadHelper.getInstance().getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setOffline();
                    }
                }, 1000);
                return;
            }

            offlineTry = offlineTry + 1;
            if (offlineTry > 15) {
                offlineTry = 0;
                return;
            }

            isOfflineRequestPending = new RequestUserUpdateStatus().userUpdateStatus(ProtoUserUpdateStatus.UserUpdateStatus.Status.OFFLINE, this);
            if (!isOfflineRequestPending) {
                LooperThreadHelper.getInstance().getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setOffline();
                    }
                }, 1000);
            }
        }
    }

    @Override
    public void onUpdateUserStatus() {
        handleResponse(false);
    }

    @Override
    public void onError(int major, int minor) {
        handleResponse(major == 5 && minor == 1);
    }

    private void handleResponse(boolean timeout) {
        boolean isOnlineResponse = false;
        synchronized (mutex) {
            if (isOfflineRequestPending && isOnlineRequestPending) {
                HelperLog.getInstance().setErrorLog(new Exception("Bagi Error Please Check!"));
            }

            if (isOnlineRequestPending) {
                isOnlineResponse = true;
                onlineTry = 0;
                isOnlineRequestPending = false;
            }

            if (isOfflineRequestPending) {
                isOnlineResponse = false;
                offlineTry = 0;
                isOfflineRequestPending = false;
            }
        }

        if (timeout) {
            if (isOnlineResponse) {
                setOnline();
            } else {
                setOffline();
            }
        }
    }
}
