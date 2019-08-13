package net.iGap.kuknos.service.Repository;

import android.os.AsyncTask;
import android.util.Log;

import net.iGap.api.apiService.ApiResponse;

import org.stellar.sdk.Server;
import org.stellar.sdk.responses.AccountResponse;

import java.io.IOException;

public class KuknosAPIAsync<T> extends AsyncTask<String, Boolean, T> {

    enum API {
        USER_ACCOUNT, USER_BALANCE
    }

    ApiResponse<T> response;
    API apiEnum;
    boolean successStatus = false;
    static final String KUKNOS_Horizan_Server = "https://hz1-test.kuknos.org";

    public KuknosAPIAsync(ApiResponse<T> response, API apiEnum) {
        this.response = response;
        this.apiEnum = apiEnum;
    }

    @Override
    protected void onPreExecute() {
        onProgressUpdate(true);
        successStatus = false;
        super.onPreExecute();
    }

    @Override
    protected T doInBackground(String... ts) {
        return runAPI(ts);
    }

    @Override
    protected void onPostExecute(T t) {
        onProgressUpdate(false);
        if (successStatus)
            response.onResponse(t);
        else
            response.onFailed((String) t);
        super.onPostExecute(t);
    }

    @Override
    protected void onProgressUpdate(Boolean... values) {
        response.setProgressIndicator(values[0]);
        super.onProgressUpdate(values);
    }

    private T runAPI(String... ts) {
        switch (apiEnum) {
            case USER_ACCOUNT:
                return getUserdata(ts[0]);
        }
        return null;
    }

    private T getUserdata(String accountID) {
        Server server = new Server(KUKNOS_Horizan_Server);
        try {
            AccountResponse account = server.accounts().account(accountID);
            successStatus = true;
            return (T) account;
        } catch (Exception e) {
            successStatus = false;
            e.printStackTrace();
            return (T) e.getMessage();
        }
    }

}
