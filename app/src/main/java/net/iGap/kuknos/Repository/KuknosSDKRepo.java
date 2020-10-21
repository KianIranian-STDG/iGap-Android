package net.iGap.kuknos.Repository;

import android.os.AsyncTask;
import android.util.Log;

import net.iGap.R;

import org.stellar.sdk.Asset;
import org.stellar.sdk.AssetTypeNative;
import org.stellar.sdk.ChangeTrustOperation;
import org.stellar.sdk.KeyPair;
import org.stellar.sdk.ManageDataOperation;
import org.stellar.sdk.ManageSellOfferOperation;
import org.stellar.sdk.Memo;
import org.stellar.sdk.Network;
import org.stellar.sdk.PaymentOperation;
import org.stellar.sdk.Server;
import org.stellar.sdk.SetOptionsOperation;
import org.stellar.sdk.Transaction;
import org.stellar.sdk.responses.AccountResponse;

import java.io.IOException;
import java.util.Objects;

public class KuknosSDKRepo extends AsyncTask<String, Boolean, String> {

    enum API {
        PAYMENT_SEND, CHANGE_TRUST, MANAGE_OFFER, SET_OPTION
    }

    private static final String KUKNOS_Horizon_Server = "https://hz1-test.kuknos.org" /*"https://horizon.kuknous.ir:8001"*/;
    private static final String PASS_PHRASE = "Kuknos-NET" /*"Kuknos Foundation, Feb 2019"*/;
    private API apiEnum;
    private callBack response;

    KuknosSDKRepo(API apiEnum, callBack response) {
        this.apiEnum = apiEnum;
        this.response = response;
    }

    @Override
    protected void onPreExecute() {
        onProgressUpdate(true);
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        return runSDK(strings);
    }

    @Override
    protected void onPostExecute(String t) {
        onProgressUpdate(false);
        response.getResponseXDR(t);
        super.onPostExecute(t);
    }

    private String runSDK(String... ts) {
        switch (apiEnum) {
            case PAYMENT_SEND:
                return paymentToOtherXDR(ts[0], ts[1], ts[2], ts[3], ts[4], ts[5]);
            case CHANGE_TRUST:
                return trustlineXDR(ts[0], ts[1], ts[2], ts[3]);
            case MANAGE_OFFER:
                return manageOffer(ts[0], ts[1], ts[2], ts[3], ts[4], ts[5], ts[6], Long.parseLong(ts[7]));
            case SET_OPTION:
                return setHomeDomainAndInflation(ts[0]);
        }
        return null;
    }

    private String paymentToOtherXDR(String sourceS, String destinationS, String tokenCode, String tokenIssuer, String amount, String memo) {
        Server server = new Server(KUKNOS_Horizon_Server);
        KeyPair source = KeyPair.fromSecretSeed(sourceS);
        KeyPair destination = KeyPair.fromAccountId(destinationS);

        // First, check to make sure that the destination account exists.
        // You could skip this, but if the account does not exist, you will be charged
        // the transaction fee when the transaction fails.
        // It will throw HttpResponseException if account does not exist or there was another error.
        try {
            server.accounts().account(destination.getAccountId());
        } catch (Exception e) {
            e.printStackTrace();
            return "" + R.string.kuknos_send_errorServer;
        }

        // If there was no error, load up-to-date information on your account.
        AccountResponse sourceAccount;
        try {
            sourceAccount = server.accounts().account(source.getAccountId());
        } catch (Exception e) {
            e.printStackTrace();
            return "" + R.string.kuknos_send_errorServer;
        }

        Asset temp;
        Log.d("amini", "paymentToOtherXDR: " + tokenCode);
        if (!tokenCode.equals("PMN"))
            temp = Asset.createNonNativeAsset(tokenCode, tokenIssuer);
        else
            temp = new AssetTypeNative();

        // Start building the transaction.
        Network network = new Network(PASS_PHRASE);
        Transaction transaction = new Transaction.Builder(Objects.requireNonNull(sourceAccount), network)
                .addOperation(new PaymentOperation.Builder(destination.getAccountId(), temp, amount).build())
                // A memo allows you to add your own metadata to a transaction. It's
                // optional and does not affect how Stellar treats the transaction.
                .addMemo(Memo.text(memo))
                .setOperationFee(50000)
                // Wait a maximum of three minutes for the transaction
                .setTimeout(60)
                .build();
        // Sign the transaction to prove you are actually the person sending it.
        transaction.sign(source);
        return transaction.toEnvelopeXdrBase64();
    }

    private String chargeWalletXDR(String sourceS, String amount, String receiptNumber, String memo) {
        Server server = new Server(KUKNOS_Horizon_Server);
        KeyPair source = KeyPair.fromSecretSeed(sourceS);

        // load up-to-date information on your account.
        AccountResponse sourceAccount = null;
        try {
            sourceAccount = server.accounts().account(source.getAccountId());
        } catch (IOException e) {
            e.printStackTrace();
            return "" + R.string.kuknos_send_errorServer;
        }

        // Start building the transaction.
        Network network = new Network("Kuknos-NET");
        Transaction transaction = new Transaction.Builder(Objects.requireNonNull(sourceAccount), network)
                .addOperation(new ManageDataOperation.Builder("amount", amount.getBytes()).build())
                .addOperation(new ManageDataOperation.Builder("receiptNumber", receiptNumber.getBytes()).build())
                // A memo allows you to add your own metadata to a transaction. It's
                // optional and does not affect how Stellar treats the transaction.
                .addMemo(Memo.text(memo))
                .setOperationFee(1000)
                // Wait a maximum of three minutes for the transaction
                .setTimeout(60)
                .build();
        // Sign the transaction to prove you are actually the person sending it.
        transaction.sign(source);
        return transaction.toEnvelopeXdrBase64();
    }

    private String chargeWalletOtherCurrencyXDR(String sourceS, String amount, String receiptNumber, String assetCode, String assetType, String memo) {
        Server server = new Server(KUKNOS_Horizon_Server);
        KeyPair source = KeyPair.fromSecretSeed(sourceS);

        // load up-to-date information on your account.
        AccountResponse sourceAccount = null;
        try {
            sourceAccount = server.accounts().account(source.getAccountId());
        } catch (IOException e) {
            e.printStackTrace();
            return "" + R.string.kuknos_send_errorServer;
        }

        // Start building the transaction.
        Network network = new Network("Kuknos-NET");
        Transaction transaction = new Transaction.Builder(Objects.requireNonNull(sourceAccount), network)
                .addOperation(new ManageDataOperation.Builder("amount", amount.getBytes()).build())
                .addOperation(new ManageDataOperation.Builder("receiptNumber", receiptNumber.getBytes()).build())
                .addOperation(new ManageDataOperation.Builder("assetCode", assetCode.getBytes()).build())
                .addOperation(new ManageDataOperation.Builder("assetType", assetType.getBytes()).build())
                // A memo allows you to add your own metadata to a transaction. It's
                // optional and does not affect how Stellar treats the transaction.
                .addMemo(Memo.text(memo))
                .setOperationFee(1000)
                // Wait a maximum of three minutes for the transaction
                .setTimeout(60)
                .build();
        // Sign the transaction to prove you are actually the person sending it.
        transaction.sign(source);
        return transaction.toEnvelopeXdrBase64();
    }

    private String trustlineXDR(String AccountSeed, String code, String issuer, String trustLineLimitByIssuer) {
        Server server = new Server(KUKNOS_Horizon_Server);
        Network network = new Network(PASS_PHRASE);
        KeyPair source = KeyPair.fromSecretSeed(AccountSeed);
        Asset asset = Asset.createNonNativeAsset(code, issuer);

        // If there was no error, load up-to-date information on your account.
        AccountResponse sourceAccount = null;
        try {
            sourceAccount = server.accounts().account(source.getAccountId());
        } catch (Exception e) {
            e.printStackTrace();
            return "" + R.string.kuknos_send_errorServer;
        }

        Transaction transaction = new Transaction.Builder(Objects.requireNonNull(sourceAccount), network)
                .addOperation(new ChangeTrustOperation.Builder(asset, (trustLineLimitByIssuer == null) ? "" + Integer.MAX_VALUE : trustLineLimitByIssuer).build())
                .addMemo(Memo.text(""))
                .setTimeout(60)
                .setOperationFee(50000)
                .build();
        // Sign the transaction to prove you are actually the person sending it.
        transaction.sign(source);
        return transaction.toEnvelopeXdrBase64();
    }

    private String manageOffer(String accountSeed, String sourceCode, String sourceIssuer,
                               String counterCode, String counterIssuer, String amount, String price, long offerID) {
        Server server = new Server(KUKNOS_Horizon_Server);
        Network network = new Network(PASS_PHRASE);
        KeyPair source = KeyPair.fromSecretSeed(accountSeed);
        Asset sourceAsset;
        if (!sourceCode.equals("PMN")) {
            sourceAsset = Asset.createNonNativeAsset(sourceCode, sourceIssuer);
        } else {
            sourceAsset = new AssetTypeNative();
        }
        Asset counterAsset;
        if (!counterCode.equals("PMN")) {
            counterAsset = Asset.createNonNativeAsset(counterCode, counterIssuer);
        } else {
            counterAsset = new AssetTypeNative();
        }

        // If there was no error, load up-to-date information on your account.
        AccountResponse sourceAccount;
        try {
            sourceAccount = server.accounts().account(source.getAccountId());
        } catch (Exception e) {
            e.printStackTrace();
            return "" + R.string.kuknos_send_errorServer;
        }

        Transaction transaction = new Transaction.Builder(Objects.requireNonNull(sourceAccount), network)
                .addOperation(new ManageSellOfferOperation.Builder(sourceAsset, counterAsset, amount, price).setOfferId(offerID).build())
                .addMemo(Memo.text(""))
                .setTimeout(60)
                .setOperationFee(50000)
                .build();
        // Sign the transaction to prove you are actually the person sending it.
        transaction.sign(source);
        return transaction.toEnvelopeXdrBase64();
    }

    private String setHomeDomainAndInflation(String AccountSeed) {
        Server server = new Server(KUKNOS_Horizon_Server);
        Network network = new Network(PASS_PHRASE);
        KeyPair source = KeyPair.fromSecretSeed(AccountSeed);

        // If there was no error, load up-to-date information on your account.
        AccountResponse sourceAccount = null;
        try {
            sourceAccount = server.accounts().account(source.getAccountId());
        } catch (Exception e) {
            e.printStackTrace();
            return "" + R.string.kuknos_send_errorServer;
        }

        Transaction transaction = new Transaction.Builder(Objects.requireNonNull(sourceAccount), network)
                .addOperation(new SetOptionsOperation.Builder()
                        .setHomeDomain("https://pdpco.ir")
                        .setInflationDestination("GAG75QOJLNDST4G7TDGHX6RVJAZJ2IOMRS4BJK4EVKYQYCKILXB5JVJ6")
                        .build())
                .addMemo(Memo.text(""))
                .setTimeout(60)
                .setOperationFee(50000)
                .build();
        // Sign the transaction to prove you are actually the person sending it.
        transaction.sign(source);
        return transaction.toEnvelopeXdrBase64();
    }

    public interface callBack {
        void getResponseXDR(String XDR);
    }
}
