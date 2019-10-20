package net.iGap;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.iGap.model.AccountUser;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AccountManager {

    private static AccountManager ourInstance = null;

    private SharedPreferences sharedPreferences;

    //first item is fake user for handel add new user
    private List<AccountUser> userAccountList;
    private List<String> DbNameList = Arrays.asList("iGapLocalDatabaseEncrypted3.realm", "iGapLocalDatabaseEncrypted2.realm", defaultDBName);
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

    private void setCurrentUser(AccountUser currentUser) {
        sharedPreferences.edit().putString("currentUser", new Gson().toJson(currentUser, AccountUser.class)).apply();
        this.currentUser = currentUser;
    }

    private void setUserList() {
        sharedPreferences.edit().putString("userList", new Gson().toJson(userAccountList, new TypeToken<List<AccountUser>>() {
        }.getType())).apply();
    }

    public void addAccount(AccountUser accountUser) {
        if (accountUser.getDbName() == null) {
            accountUser.setDbName(getDbName());
        }
        userAccountList.add(userAccountList.size(), accountUser);
        userAccountList.get(0).setDbName(getDbName());
        setUserList();
        setCurrentUser(accountUser);
        for (int i = 0; i < userAccountList.size(); i++) {
            Log.wtf(this.getClass().getName(), "account: " + userAccountList.get(i).toString());
        }
    }

    public boolean isExistThisAccount(long userId) {
        return userAccountList.contains(new AccountUser(userId));
    }

    public void changeCurrentUserForAddAccount() {
        currentUser = userAccountList.get(0);
    }

    public void changeCurrentUserAccount(long userId) {
        int t = userAccountList.indexOf(new AccountUser(userId));
        if (t != -1) {
            currentUser = userAccountList.get(t);
            setCurrentUser(currentUser);
        } else {
            Log.wtf(this.getClass().getName(), "not exist this user");
        }
    }

    // return true if have current user after remove accountUser
    public boolean removeUser(AccountUser accountUser) {
        if (accountUser.isAssigned()) {
            if (userAccountList.contains(accountUser)) {
                userAccountList.remove(accountUser);
                currentUser = userAccountList.get(userAccountList.size() - 1);
                setUserList();
                setCurrentUser(currentUser);
                return currentUser.isAssigned();
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
}
