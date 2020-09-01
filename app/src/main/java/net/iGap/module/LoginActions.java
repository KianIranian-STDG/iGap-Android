package net.iGap.module;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.google.gson.Gson;

import net.iGap.G;
import net.iGap.helper.HelperCheckInternetConnection;
import net.iGap.helper.UserStatusController;
import net.iGap.module.accountManager.DbManager;
import net.iGap.module.structs.StructListOfContact;
import net.iGap.observers.interfaces.OnContactFetchForServer;
import net.iGap.observers.interfaces.OnSecuring;
import net.iGap.observers.interfaces.OnUserLogin;
import net.iGap.realm.RealmPhoneContacts;
import net.iGap.realm.RealmUserInfo;
import net.iGap.request.RequestGeoGetRegisterStatus;
import net.iGap.request.RequestUserContactsGetBlockedList;
import net.iGap.request.RequestUserContactsGetList;
import net.iGap.request.RequestUserInfo;
import net.iGap.request.RequestUserLogin;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import static net.iGap.G.firstEnter;

/**
 * all actions that need doing after login
 */
public class LoginActions {

    public LoginActions() {
        initSecureInterface();
    }

    /**
     * try login to server and do common actions
     */
    public static void login() {
        if (!G.ISRealmOK) {
            return;
        }
        G.onUserLogin = new OnUserLogin() {
            @Override
            public void onLogin() {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {

                        /**
                         * in first enter to app client send clientCondition after get room list
                         * but, in another login when user not closed app after login client send
                         * latest state to server without get room list , also if
                         * app in background is running now if firstTimeEnterToApp is false we send
                         * client condition because this field(firstTimeEnterToApp) will be lost value
                         * in close app and after app start running in background we don't send
                         * client condition!!! for avoid from this problem we checked isAppInFg state
                         * app is background send clientCondition (: .
                         */

                        if (firstEnter) {
                            firstEnter = false;
                            new RequestUserContactsGetBlockedList().userContactsGetBlockedList();
                            importContact();
                        }


                        getUserInfo();
                        if (G.isAppInFg) {
                            UserStatusController.getInstance().setOnline();
                        }

                        new RequestGeoGetRegisterStatus().getRegisterStatus();
                        //sendWaitingRequestWrappers();

                        HelperCheckInternetConnection.detectConnectionTypeForDownload();
                    }
                });


            }

            @Override
            public void onLoginError(int majorCode, int minorCode) {

            }
        };
        if (G.isSecure) {
            DbManager.getInstance().doRealmTask(realm -> {
                RealmUserInfo userInfo = realm.where(RealmUserInfo.class).findFirst();
                Log.wtf(LoginActions.class.getName(), "bagi: ");
                if (!G.userLogin) {
                    if (userInfo != null) {
                        if (userInfo.getUserRegistrationState()) {
                            Log.wtf(LoginActions.class.getName(), "LoginActions.login: RequestUserLogin().userLogin");
                            new RequestUserLogin().userLogin(userInfo.getToken());
                        } else {
                            Log.wtf(LoginActions.class.getName(), "LoginActions.login:getUserRegistrationState" + userInfo.getUserRegistrationState());
                        }
                    } else {
                        Log.wtf(LoginActions.class.getName(), "LoginActions.login:userInfo != null");
                    }
                } else {
                    Log.wtf(LoginActions.class.getName(), "LoginActions.login:else");
                }
            });
        } else {
            login();
        }
    }

    private static void getUserInfo() {
        final Long userId;
        userId = DbManager.getInstance().doRealmTask(realm -> {
            try {
                RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
                if (realmUserInfo == null) {
                    throw new Exception("Empty Exception");
                }
                return realmUserInfo.getUserId();
            } catch (Exception e) {
                e.printStackTrace();
                Log.wtf(LoginActions.class.getName(),"catch");
                G.logoutAccount.postValue(true);
                return null;
            }
        });
        if (userId == null)
            return;

        new RequestUserInfo().userInfo(userId);
    }

    public static void importContact() {
        /**
         * just import contact in each enter to app
         * when user login was done
         */

        G.onContactFetchForServer = new OnContactFetchForServer() {
            @Override
            public void onFetch(List<StructListOfContact> contacts, boolean getContactList) {

                String md5Local = md5(new Gson().toJson(contacts));
                G.localHashContact = md5Local;

                if (G.serverHashContact != null && G.serverHashContact.equals(md5Local)) {
                    //request get list

                    new RequestUserContactsGetList().userContactGetList();
                    return;
                }

                RealmPhoneContacts.sendContactList(contacts, false, getContactList);
            }
        };

        if (G.userLogin) {
            /**
             * this can be go in the activity for check permission in api 6+
             */

            try {
                if (ContextCompat.checkSelfPermission(G.context, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            G.isSendContact = true;
                            new Contacts.FetchContactForServer().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        }
                    });
                } else {
                    new RequestUserContactsGetList().userContactGetList();
                }

            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        } else {
            G.handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    importContact();
                }
            }, 2000);
        }

    }

    private static String md5(String s) {

        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte[] messageDigest = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
    /**
     * initialize securing interface for detecting
     * securing is done and continue login actions
     */
    private void initSecureInterface() {
        Log.wtf(this.getClass().getName(), "initSecureInterface");
        G.onSecuring = new OnSecuring() {
            @Override
            public void onSecure() {
                Log.wtf(this.getClass().getName(), "onSecure");
                login();
            }
        };
    }
}