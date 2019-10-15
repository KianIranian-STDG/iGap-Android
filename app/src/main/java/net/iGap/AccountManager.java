package net.iGap;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.iGap.model.AccountUser;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AccountManager {

    private static AccountManager ourInstance = null;

    private SharedPreferences sharedPreferences;
    private List<AccountUser> userAccountList;
    private AccountUser currentUser;
    public static final String defaultDBName = "iGapLocalDatabaseEncrypted.realm";

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
        Gson gson = new Gson();
        userAccountList = gson.fromJson(sharedPreferences.getString("userList", ""), new TypeToken<List<AccountUser>>() {
        }.getType());
        if (userAccountList == null) {
            userAccountList = new ArrayList<>();
            AccountUser accountUser = new AccountUser(false, "test");
            accountUser.setDbName(getDbName());
            userAccountList.add(accountUser);
            currentUser = accountUser;
        } else {
            currentUser = gson.fromJson(sharedPreferences.getString("currentUser", ""), AccountUser.class);
        }
    }

    public List<AccountUser> getUserAccountList() {
        return userAccountList;
    }

    public AccountUser getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(AccountUser currentUser) {
        sharedPreferences.edit().putString("currentUser", new Gson().toJson(currentUser, AccountUser.class)).apply();
        this.currentUser = currentUser;
    }

    public void addAccount(AccountUser accountUser) {
        AccountUser tmp = userAccountList.remove(userAccountList.size() - 1);
        if (accountUser.getDbName() == null) {
            accountUser.setDbName(getDbName());
        }
        userAccountList.add(userAccountList.size(), accountUser);
        tmp.setDbName(getDbName());
        userAccountList.add(tmp);
        sharedPreferences.edit().putString("userList", new Gson().toJson(userAccountList, new TypeToken<List<AccountUser>>() {
        }.getType())).apply();
        setCurrentUser(accountUser);
        for (int i = 0; i < userAccountList.size(); i++) {
            Log.wtf(this.getClass().getName(), "account: " + userAccountList.get(i).toString());
        }
    }

    public boolean isExistThisAccount(long userId) {
        return userAccountList.contains(new AccountUser(userId));
    }

    public boolean isExistThisAccount(String phoneNumber) {
        Log.wtf(this.getClass().getName(), "contains: " + userAccountList.contains(new AccountUser(phoneNumber)));
        for (int i = 0; i < userAccountList.size(); i++) {
            Log.wtf(this.getClass().getName(), "account: " + userAccountList.get(i).toString());
        }
        return userAccountList.contains(new AccountUser(phoneNumber));
    }

    public void changeCurrentUserForAddAccount() {
        currentUser = userAccountList.get(userAccountList.size() - 1);
    }

    public void changeCurrentUserAccount(long userId) {
        int t = userAccountList.indexOf(new AccountUser(userId));
        if (t != -1) {
            currentUser = userAccountList.get(t);
        }
    }

    private String getDbName() {
        switch (userAccountList.size()) {
            case 0:
                return defaultDBName;
            case 1:
                return "iGapLocalDatabaseEncrypted2.realm";
            default:
                return "iGapLocalDatabaseEncrypted3.realm";
        }
    }
}
