package net.iGap.module.accountManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.SparseArray;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.iGap.G;
import net.iGap.fragments.MainFragment;
import net.iGap.helper.FileLog;
import net.iGap.model.AccountUser;
import net.iGap.network.RequestManager;
import net.iGap.request.RequestClientGetRoomList;
import net.iGap.response.ClientGetRoomListResponse;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class AccountManager {

    public final static String defaultDBName = "iGapLocalDatabaseEncrypted.realm";
    private static AccountManager ourInstance = null;

    private SharedPreferences sharedPreferences;
    private String dbEncryptionKey;

    private List<AccountUser> userAccountList;
    private SparseArray<String> databaseNames = new SparseArray<>(3);
    public static int selectedAccount;
    public static final int MAX_ACCOUNT_COUNT = 3;

    public static AccountManager getInstance() {
        if (ourInstance == null) {
            ourInstance = new AccountManager();
        }
        return ourInstance;
    }

    private AccountManager() {
        databaseNames.put(0, "iGapLocalDatabaseEncrypted.realm");
        databaseNames.put(1, "iGapLocalDatabaseEncrypted2.realm");
        databaseNames.put(2, "iGapLocalDatabaseEncrypted3.realm");

        sharedPreferences = G.context.getSharedPreferences("iGapUserAccount", Context.MODE_PRIVATE);
        getUserAccountListFromSharedPreferences();
        if (userAccountList == null) {
            userAccountList = new ArrayList<>();
            for (int i = 0; i < MAX_ACCOUNT_COUNT; i++) {
                AccountUser accountUser = new AccountUser("Account" + i);
                accountUser.setDbName(getDbName(i));
                userAccountList.add(accountUser);
            }
            selectedAccount = 0;
        }
        getCurrentUserFromSharedPreferences();

        SharedPreferences realmPreferences = G.context.getSharedPreferences("AES-256", Context.MODE_PRIVATE);
        dbEncryptionKey = realmPreferences.getString("myByteArray", null);
        if (dbEncryptionKey == null) {
            byte[] key = new byte[64];
            new SecureRandom().nextBytes(key);
            String saveThis = Base64.encodeToString(key, Base64.DEFAULT);
            realmPreferences.edit().putString("myByteArray", saveThis).apply();
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
        return userAccountList.get(selectedAccount);
    }

    public AccountUser getUser(long userId) {
        int i = indexOfUser(userId);
        if (i >= 0)
            return userAccountList.get(i);
        return null;
    }

    private void setCurrentUserInSharedPreferences() {
        sharedPreferences.edit().putInt("currentUser", selectedAccount).apply();
    }

    private void getCurrentUserFromSharedPreferences() {
        selectedAccount = sharedPreferences.getInt("currentUser", 0);
    }

    private void getUserAccountListFromSharedPreferences() {
        userAccountList = new Gson().fromJson(sharedPreferences.getString("userList", ""), new TypeToken<List<AccountUser>>() {
        }.getType());

        if (userAccountList != null && userAccountList.get(0).getName().equalsIgnoreCase("test")) {
            try {
                getCurrentUserFromSharedPreferences();

                FileLog.i("AccountManager", "Start migration with account size " + userAccountList.size() + " and selected account " + selectedAccount);

                for (int i = 0; i < userAccountList.size(); i++) {
                    FileLog.i("AccountManager", "user " + i + " dbname -> " + userAccountList.get(i).getDbName());
                }

                userAccountList.remove(0);
                selectedAccount = selectedAccount - 1;

                for (int i = userAccountList.size(); i < MAX_ACCOUNT_COUNT; i++) {
                    AccountUser accountUser = new AccountUser("Account_" + (i));
                    String dbName = getDbName(i);
                    accountUser.setDbName(dbName);
                    accountUser.setRealmConfiguration(userAccountList.get(0).getRealmConfiguration()); // in all account realm config just once construct and use for other account
                    userAccountList.add(accountUser);
                }

                setCurrentUserInSharedPreferences();
                setUserAccountListInSharedPreferences();

                FileLog.i("AccountManager", "Migration done current account size " + userAccountList.size() + " and selected account " + selectedAccount
                        + "\n account 0 db name is -> " + userAccountList.get(0).getDbName()
                        + "\n account 1 db name is -> " + userAccountList.get(1).getDbName()
                        + "\n account 2 db name is -> " + userAccountList.get(2).getDbName());

            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public int getActiveAccountCount() {
        int count = 0;
        for (int i = 0; i < MAX_ACCOUNT_COUNT; i++) {
            if (userAccountList.get(i).isAssigned()) {
                count++;
            }
        }
        return count;
    }

    private void setUserAccountListInSharedPreferences() {
        sharedPreferences.edit().putString("userList", new Gson().toJson(userAccountList, new TypeToken<List<AccountUser>>() {
        }.getType())).apply();
    }

    public void updateCurrentUserName(String name) {
        userAccountList.get(selectedAccount).setName(name);
        setUserAccountListInSharedPreferences();
    }

    public void updatePhoneNumber(String phoneNumber) {
        userAccountList.get(selectedAccount).setPhoneNumber(phoneNumber);
        setUserAccountListInSharedPreferences();
    }

    public void setCurrentUser() {
        getCurrentUserFromSharedPreferences();
    }

    public void addNewUser(long userId, String phoneNumber) {
        addNewUser(userId, phoneNumber, null);
    }

    public void addNewUser(long userId, String phoneNumber, String name) {
        AccountUser newUser = userAccountList.get(selectedAccount);
        newUser.setAssigned(true);
        newUser.setName(name == null ? "" : name);
        newUser.setPhoneNumber(phoneNumber);
        newUser.setId(userId);
        newUser.setDbName(getDbName(selectedAccount));
        newUser.setName("Account " + selectedAccount);
        newUser.setLoginTime(System.currentTimeMillis());
        setCurrentUserInSharedPreferences();
    }

    public boolean isExistThisAccount(long userId) {
        for (int i = 0; i < userAccountList.size(); i++) {
            return userAccountList.get(i).getId() == userId;
        }
        return false;
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

        for (int i = 0; i < MAX_ACCOUNT_COUNT; i++) {
            if (!userAccountList.get(i).isAssigned()) {
                selectedAccount = i;
                break;
            }
        }
    }

    public void changeCurrentUserAccount(long userId) {
        clearSomeStaticValue();
        int t = indexOfUser(userId);
        if (t != -1) {
            selectedAccount = t;
            setCurrentUserInSharedPreferences();
        } else {
            throw new IllegalArgumentException("not exist this user");
        }
    }

    private int indexOfUser(long userId) {
        for (int i = 0; i < userAccountList.size(); i++) {
            if (userAccountList.get(i).getId() == userId) {
                return i;
            }
        }
        return 0;
    }

    public boolean removeUser(AccountUser accountUser) {
        if (accountUser.isAssigned()) {
            if (userAccountList.contains(accountUser)) {
                AccountUser removedUser = userAccountList.get(selectedAccount);
                removedUser.clearData();
                clearSomeStaticValue();

                int account = 0;
                for (int i = 0; i < MAX_ACCOUNT_COUNT; i++) {
                    if (userAccountList.get(i).isAssigned()) {
                        account = i;
                        break;
                    }
                }

                selectedAccount = account;

                setCurrentUserInSharedPreferences();
                setUserAccountListInSharedPreferences();
                return userAccountList.get(selectedAccount).isAssigned();
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private String getDbName(int i) {
        return databaseNames.get(i);
    }

    public void clearSomeStaticValue() {
        ClientGetRoomListResponse.roomListFetched = false;
        RequestClientGetRoomList.isPendingGetRoomList = false;
        MainFragment.mOffset = 0;
        G.serverHashContact = null;
        G.selectedCard = null;
        G.nationalCode = null;
        RequestManager.getInstance(AccountManager.selectedAccount).setPullRequestQueueRunned(false);
    }

    public boolean haveAccount() {
        if (userAccountList.get(selectedAccount).isAssigned()) {
            return true;
        } else {
            if (userAccountList.size() > 1) {
                selectedAccount = userAccountList.size() - 1;
                setCurrentUser();
                return userAccountList.get(userAccountList.size() - 1).isAssigned();
            } else {
                return false;
            }
        }
    }
}
