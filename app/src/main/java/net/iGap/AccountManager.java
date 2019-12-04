package net.iGap;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.iGap.fragments.FragmentMain;
import net.iGap.model.AccountUser;
import net.iGap.request.RequestClientGetRoomList;
import net.iGap.response.ClientGetRoomListResponse;

import org.jetbrains.annotations.NotNull;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.paygear.utils.Utils.signOutWallet;

public class AccountManager {

    public final static String defaultDBName = "iGapLocalDatabaseEncrypted.realm";
    private static AccountManager ourInstance = null;

    private SharedPreferences sharedPreferences;
    private String dbEncryptionKey;

    //first item is fake user for handel add new user
    private List<AccountUser> userAccountList;
    private List<String> DbNameList = Arrays.asList("iGapLocalDatabaseEncrypted3.realm", "iGapLocalDatabaseEncrypted2.realm", defaultDBName);
    private int currentUser;

    public static AccountManager getInstance() {
        if (ourInstance != null) {
            return ourInstance;
        } else {
            throw new RuntimeException("first call AccountManager.initial(Context context)");
        }
    }

    public static void initial(Context context) {
        if (ourInstance == null) {
            ourInstance = new AccountManager(context);
        }
    }

    private AccountManager(@NotNull Context context) {
        sharedPreferences = context.getSharedPreferences("iGapUserAccount", Context.MODE_PRIVATE);
        getUserAccountListFromSharedPreferences();
        if (userAccountList == null) {
            userAccountList = new ArrayList<>();
            AccountUser accountUser = new AccountUser(false, "test");
            accountUser.setDbName(getDbName());
            userAccountList.add(accountUser);
            currentUser = 0;
        }
        getCurrentUserFromSharedPreferences();
        SharedPreferences sharedPreferences = context.getSharedPreferences("AES-256", Context.MODE_PRIVATE);
        dbEncryptionKey = sharedPreferences.getString("myByteArray", null);
        if (dbEncryptionKey == null) {
            byte[] key = new byte[64];
            new SecureRandom().nextBytes(key);
            String saveThis = Base64.encodeToString(key, Base64.DEFAULT);
            sharedPreferences.edit().putString("myByteArray", saveThis).apply();
            dbEncryptionKey = saveThis;
        }
        for (int i = 0; i < userAccountList.size(); i++) {
            userAccountList.get(i).setRealmConfiguration(dbEncryptionKey);
        }
    }

    public List<AccountUser> getUserAccountList() {
        return userAccountList;
    }

    public AccountUser getCurrentUser() {
        return userAccountList.get(currentUser);
    }

    public AccountUser getUser(long userId) {
        int i = userAccountList.indexOf(new AccountUser(userId));
        if (i >= 0)
            return userAccountList.get(i);
        return null;
    }

    private void setCurrentUserInSharedPreferences() {
        sharedPreferences.edit().putInt("currentUser", this.currentUser).apply();
    }

    private void getCurrentUserFromSharedPreferences() {
        this.currentUser = sharedPreferences.getInt("currentUser", 0);
    }

    private void getUserAccountListFromSharedPreferences() {
        userAccountList = new Gson().fromJson(sharedPreferences.getString("userList", ""), new TypeToken<List<AccountUser>>() {
        }.getType());
    }

    private void setUserAccountListInSharedPreferences() {
        sharedPreferences.edit().putString("userList", new Gson().toJson(userAccountList, new TypeToken<List<AccountUser>>() {
        }.getType())).apply();
    }

    public void updateCurrentUserName(String name) {
        userAccountList.get(currentUser).setName(name);
        setUserAccountListInSharedPreferences();
    }

    public void setCurrentUser() {
        getCurrentUserFromSharedPreferences();
    }

    public void addAccount(AccountUser accountUser) {
        if (accountUser.getDbName() == null) {
            accountUser.setDbName(getDbName());
            accountUser.setRealmConfiguration(userAccountList.get(0).getRealmConfiguration());
        }
        userAccountList.add(userAccountList.size(), accountUser);
        userAccountList.get(0).setDbName(getDbName());
        userAccountList.get(0).setRealmConfiguration(dbEncryptionKey);
        setUserAccountListInSharedPreferences();
        this.currentUser = userAccountList.size() - 1;
        setCurrentUserInSharedPreferences();
    }

    public boolean isExistThisAccount(long userId) {
        return userAccountList.contains(new AccountUser(userId));
    }

    public boolean isExistThisAccount(String phoneNumber) {
        for (int i = 0; i < userAccountList.size(); i++) {
            if (phoneNumber.equals(userAccountList.get(i).getPhoneNumber())) {
                return true;
            }
        }
        return false;
    }

    public void changeCurrentUserForAddAccount() {
        clearSomeStaticValue();
        currentUser = 0;
    }

    public void changeCurrentUserAccount(long userId) {
        clearSomeStaticValue();
        int t = userAccountList.indexOf(new AccountUser(userId));
        if (t != -1) {
            currentUser = t;
            setCurrentUserInSharedPreferences();
        } else {
            throw new IllegalArgumentException("not exist this user");
        }
    }

    // return true if have current user after remove accountUser
    public boolean removeUser(AccountUser accountUser) {
        if (accountUser.isAssigned()) {
            if (userAccountList.contains(accountUser)) {
                userAccountList.remove(accountUser);
                clearSomeStaticValue();
                currentUser = userAccountList.size() - 1;
                userAccountList.get(0).setDbName(getDbName());
                setCurrentUserInSharedPreferences();
                setUserAccountListInSharedPreferences();
                return userAccountList.get(currentUser).isAssigned();
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private String getDbName() {
        for (int i = DbNameList.size() - 1; i > -1; i--) {
            boolean isExist = false;
            for (int j = 0; j < userAccountList.size(); j++) {
                if (userAccountList.get(j).isAssigned() && userAccountList.get(j).getDbName().equals(DbNameList.get(i))) {
                    isExist = true;
                    break;
                }
            }
            if (!isExist) {
                return DbNameList.get(i);
            }
        }
        return defaultDBName;
    }

    public void clearSomeStaticValue() {
        ClientGetRoomListResponse.roomListFetched = false;
        RequestClientGetRoomList.isPendingGetRoomList = false;
        FragmentMain.mOffset = 0;
        G.serverHashContact = null;
        G.isMplActive = false;
        G.isWalletActive = false;
        G.isWalletRegister = false;
        G.jwt = null;
        G.selectedCard = null;
        G.cardamount = 0;
        G.pullRequestQueueRunned = new AtomicBoolean(false);
    }

    public boolean haveAccount() {
        if (userAccountList.get(currentUser).isAssigned()) {
            return true;
        } else {
            if (userAccountList.size() > 1) {
                currentUser = userAccountList.size() - 1;
                setCurrentUser();
                return userAccountList.get(userAccountList.size() - 1).isAssigned();
            } else {
                return false;
            }
        }
    }

    public void firstStepOfChangeAccount(){
        WebSocketClient.getInstance().disconnectSocket(false);
        G.handler.removeCallbacksAndMessages(null);

        signOutWallet();
    }
}
