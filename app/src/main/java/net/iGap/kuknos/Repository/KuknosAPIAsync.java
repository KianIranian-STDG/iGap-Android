package net.iGap.kuknos.Repository;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import net.iGap.R;

import org.stellar.sdk.Asset;
import org.stellar.sdk.AssetTypeCreditAlphaNum4;
import org.stellar.sdk.AssetTypeNative;
import org.stellar.sdk.ChangeTrustOperation;
import org.stellar.sdk.KeyPair;
import org.stellar.sdk.ManageSellOfferOperation;
import org.stellar.sdk.Memo;
import org.stellar.sdk.Network;
import org.stellar.sdk.PaymentOperation;
import org.stellar.sdk.Server;
import org.stellar.sdk.Transaction;
import org.stellar.sdk.requests.RequestBuilder;
import org.stellar.sdk.responses.AccountResponse;
import org.stellar.sdk.responses.AssetResponse;
import org.stellar.sdk.responses.OfferResponse;
import org.stellar.sdk.responses.Page;
import org.stellar.sdk.responses.SubmitTransactionResponse;
import org.stellar.sdk.responses.operations.OperationResponse;

import java.io.IOException;
import java.util.Objects;

public class KuknosAPIAsync<T> extends AsyncTask<String, Boolean, T> {

    enum API {
        USER_ACCOUNT, PAYMENT_SEND, PAYMENTS_ACCOUNT, ASSETS, CHANGE_TRUST, OFFERS_LIST, TRADES_LIST, MANAGE_OFFER
    }

    /*private ApiResponse<T> response;*/
    private API apiEnum;
    private boolean successStatus = false;
    private static final String KUKNOS_Horizan_Server = "https://hz1-test.kuknos.org" /*"https://horizon-testnet.stellar.org"*/;

    public KuknosAPIAsync(/*ApiResponse<T> response,*/ API apiEnum) {
        /*this.response = response;*/
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
        /*if (successStatus)
            response.onResponse(t);
        else {
            response.onFailed((String) t);
        }*/
        super.onPostExecute(t);
    }

    @Override
    protected void onProgressUpdate(Boolean... values) {
        /*response.setProgressIndicator(values[0]);*/
        super.onProgressUpdate(values);
    }

    private T runAPI(String... ts) {
        switch (apiEnum) {
            case USER_ACCOUNT:
                return getUserdata(ts[0]);
            case PAYMENT_SEND:
                return paymentToOther(ts[0], ts[1], ts[2], ts[3]);
            case PAYMENTS_ACCOUNT:
                return paymentsUser(ts[0]);
            case ASSETS:
                return getAssets();
            case CHANGE_TRUST:
                return addTrustline(ts[0], ts[1], ts[2]);
            case OFFERS_LIST:
                return getOffersList(ts[0]);
            case TRADES_LIST:
                return getTrades(ts[0]);
            case MANAGE_OFFER:
                return manangeOffer(ts[0], ts[1], ts[2], ts[3], ts[4], ts[5], ts[6]);
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

    private T paymentToOther(String sourceS, String destinationS, String amount, String memo) {
        Server server = new Server(KUKNOS_Horizan_Server);
        KeyPair source = KeyPair.fromSecretSeed(sourceS);
        KeyPair destination = KeyPair.fromAccountId(destinationS);

        // First, check to make sure that the destination account exists.
        // You could skip this, but if the account does not exist, you will be charged
        // the transaction fee when the transaction fails.
        // It will throw HttpResponseException if account does not exist or there was another error.
        try {
            server.accounts().account(destination.getAccountId());
        } catch (Exception e) {
            successStatus = false;
            e.printStackTrace();
            Log.d("amini", "paymentToOther: " + e.getMessage());
            return (T) ("" + R.string.kuknos_send_errorServer);
        }

        // If there was no error, load up-to-date information on your account.
        AccountResponse sourceAccount;
        try {
            sourceAccount = server.accounts().account(source.getAccountId());
        } catch (IOException e) {
            successStatus = false;
            e.printStackTrace();
            return (T) ("" + R.string.kuknos_send_errorServer);
        }

        // todo add other currency and base fee ** very IMP.
        // Start building the transaction.
        Network network = new Network("Kuknos-NET");
        Transaction transaction = new Transaction.Builder(Objects.requireNonNull(sourceAccount), network)
                .addOperation(new PaymentOperation.Builder(destination.getAccountId(), new AssetTypeNative(), amount).build())
                // A memo allows you to add your own metadata to a transaction. It's
                // optional and does not affect how Stellar treats the transaction.
                .addMemo(Memo.text(memo))
                .setOperationFee(1000)
                // Wait a maximum of three minutes for the transaction
                .setTimeout(60)
                .build();
        // Sign the transaction to prove you are actually the person sending it.
        transaction.sign(source);

        // And finally, send it off to Stellar!
        try {
            SubmitTransactionResponse response = server.submitTransaction(transaction);
            //todo clean this hard code for monitoring
            Gson gson = new Gson();
            Log.d("amini", "paymentToOther: " + gson.toJson(response) + "\n" + response.isSuccess());
            if (response.isSuccess()) {
                successStatus = true;
                return (T) response;
            } else {
                successStatus = false;
                return (T) checkResponseCode(response);
            }
        } catch (Exception e) {
            successStatus = false;
            e.printStackTrace();
            return (T) ("" + R.string.kuknos_send_errorServer);
        }

    }

    private String checkResponseCode(SubmitTransactionResponse response) {
        TranResultCode trc = TranResultCode.valueOf(response.getExtras().getResultCodes().getTransactionResultCode());
        String error;
        switch (trc) {
            case tx_failed:
                error = "" + R.string.Kuknos_transaction_error1;
                break;
            case tx_internal_error:
                error = "" + R.string.Kuknos_transaction_error2;
                break;
            case tx_bad_seq:
                error = "" + R.string.Kuknos_transaction_error3;
                break;
            case tx_bad_auth:
                error = "" + R.string.Kuknos_transaction_error4;
                break;
            case tx_no_account:
                error = "" + R.string.Kuknos_transaction_error5;
                break;
            case tx_insufficient_fee:
                error = "" + R.string.Kuknos_transaction_error6;
                break;
            case tx_insufficient_balance:
                error = "" + R.string.Kuknos_transaction_error7;
                break;
            default:
                error = "" + R.string.Kuknos_transaction_error8;
                break;
        }
        return error;
    }

    private T paymentsUser(String accountID) {
        Server server = new Server(KUKNOS_Horizan_Server);
        try {
            Page<OperationResponse> response = server.payments().forAccount(accountID).limit(100).order(RequestBuilder.Order.DESC).execute();
            successStatus = true;
            return (T) response;
        } catch (IOException e) {
            successStatus = false;
            e.printStackTrace();
            return (T) e.getMessage();
        }
    }

    private T getAssets() {
        Server server = new Server(KUKNOS_Horizan_Server);
        try {
            Page<AssetResponse> response = server.assets().execute();
            successStatus = true;
            return (T) response;
        } catch (IOException e) {
            successStatus = false;
            e.printStackTrace();
            return (T) e.getMessage();
        }
    }

    private T addTrustline(String AccountSeed, String code, String issuer) {
        Server server = new Server(KUKNOS_Horizan_Server);
        Network network = new Network("Kuknos-NET");
        KeyPair source = KeyPair.fromSecretSeed(AccountSeed);
        Asset asset = new AssetTypeCreditAlphaNum4(code, issuer);

        // If there was no error, load up-to-date information on your account.
        AccountResponse sourceAccount;
        try {
            sourceAccount = server.accounts().account(source.getAccountId());
        } catch (IOException e) {
            successStatus = false;
            e.printStackTrace();
            return (T) ("" + R.string.kuknos_send_errorServer);
        }

        Transaction transaction = new Transaction.Builder(Objects.requireNonNull(sourceAccount), network)
                .addOperation(new ChangeTrustOperation.Builder(asset, "" + Integer.MAX_VALUE).build())
                .addMemo(Memo.text(""))
                .setTimeout(60)
                .setOperationFee(1000)
                .build();
        // Sign the transaction to prove you are actually the person sending it.
        transaction.sign(source);
        try {
            SubmitTransactionResponse response = server.submitTransaction(transaction);
            //todo clean this hard code for monitoring
            Gson gson = new Gson();
            Log.d("amini", "paymentToOther: " + gson.toJson(response) + "\n" + response.isSuccess());
            if (response.isSuccess()) {
                successStatus = true;
                return (T) response;
            } else {
                // todo change response code for this option
                successStatus = false;
                return (T) checkResponseCode(response);
            }
        } catch (Exception e) {
            // todo change response code for this option
            successStatus = false;
            e.printStackTrace();
            return (T) ("" + R.string.kuknos_send_errorServer);
        }
    }

    private T getOffersList(String accountID) {
        Server server = new Server(KUKNOS_Horizan_Server);
        try {
            Page<OfferResponse> response = server.offers().forAccount(accountID).limit(100).order(RequestBuilder.Order.DESC).execute();
            successStatus = true;
            return (T) response;
        } catch (Exception e) {
            successStatus = false;
            e.printStackTrace();
            return (T) e.getMessage();
        }
    }

    private T getTrades(String accountID) {
        Server server = new Server(KUKNOS_Horizan_Server);
        try {
//            server.trades().forAccount(accountID).limit(100).execute();
            Page<OfferResponse> response = server.offers().forAccount(accountID).limit(100).order(RequestBuilder.Order.DESC).execute();
            successStatus = true;
            return (T) response;
        } catch (IOException e) {
            successStatus = false;
            e.printStackTrace();
            return (T) e.getMessage();
        }
    }

    private T manangeOffer(String accountSeed, String sourceCode, String sourceIssuer,
                           String counterCode, String counterIssuer, String amount, String price) {
        Server server = new Server(KUKNOS_Horizan_Server);
        Network network = new Network("Kuknos-NET");
        KeyPair source = KeyPair.fromSecretSeed(accountSeed);
        Asset sourceAsset = new AssetTypeCreditAlphaNum4(sourceCode, sourceIssuer);
        Asset counterAsset = new AssetTypeCreditAlphaNum4(counterCode, counterIssuer);

        // If there was no error, load up-to-date information on your account.
        AccountResponse sourceAccount;
        try {
            sourceAccount = server.accounts().account(source.getAccountId());
        } catch (IOException e) {
            successStatus = false;
            e.printStackTrace();
            return (T) ("" + R.string.kuknos_send_errorServer);
        }

        Transaction transaction = new Transaction.Builder(Objects.requireNonNull(sourceAccount), network)
                .addOperation(new ManageSellOfferOperation.Builder(sourceAsset, counterAsset, amount, price).build())
                .addMemo(Memo.text(""))
                .setTimeout(60)
                .setOperationFee(1000)
                .build();
        // Sign the transaction to prove you are actually the person sending it.
        transaction.sign(source);

        try {
            SubmitTransactionResponse response = server.submitTransaction(transaction);
            //todo clean this hard code for monitoring
            Gson gson = new Gson();
            Log.d("amini", "paymentToOther: " + gson.toJson(response) + "\n" + response.isSuccess());
            if (response.isSuccess()) {
                successStatus = true;
                return (T) response;
            } else {
                // todo change response code for this option
                successStatus = false;
                return (T) checkResponseCode(response);
            }
        } catch (Exception e) {
            // todo change response code for this option
            successStatus = false;
            e.printStackTrace();
            return (T) ("" + R.string.kuknos_send_errorServer);
        }
    }

}
