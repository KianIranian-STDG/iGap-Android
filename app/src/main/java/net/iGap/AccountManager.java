package net.iGap;

import android.content.Context;
import android.content.SharedPreferences;

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
        }
        currentUser = gson.fromJson(sharedPreferences.getString("currentUser", ""), AccountUser.class);
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
        if (accountUser.getDbName() == null) {
            accountUser.setDbName(getDbName());
        }
        userAccountList.add(accountUser);
        sharedPreferences.edit().putString("userList", new Gson().toJson(userAccountList, new TypeToken<List<AccountUser>>() {
        }.getType())).apply();
        setCurrentUser(accountUser);
    }

    public boolean isFirstAccount() {
        return userAccountList.size() == 0;
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
