package net.iGap.kuknos.service.Repository;

import android.os.AsyncTask;

import net.iGap.R;

import org.stellar.sdk.Asset;
import org.stellar.sdk.AssetTypeCreditAlphaNum4;
import org.stellar.sdk.AssetTypeNative;
import org.stellar.sdk.ChangeTrustOperation;
import org.stellar.sdk.KeyPair;
import org.stellar.sdk.ManageDataOperation;
import org.stellar.sdk.ManageSellOfferOperation;
import org.stellar.sdk.Memo;
import org.stellar.sdk.Network;
import org.stellar.sdk.PaymentOperation;
import org.stellar.sdk.Server;
import org.stellar.sdk.Transaction;
import org.stellar.sdk.responses.AccountResponse;

import java.io.IOException;
import java.util.Objects;

public class KuknosSDKRepo extends AsyncTask<String, Boolean, String> {

    enum API {
        PAYMENT_SEND, CHANGE_TRUST, MANAGE_OFFER
    }

    private static final String KUKNOS_Horizan_Server = "https://hz1-test.kuknos.org" /*"https://horizon-testnet.stellar.org"*/;
    private API apiEnum;
    private callBack response;

    public KuknosSDKRepo(API apiEnum, callBack response) {
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
                return paymentToOtherXDR(ts[0], ts[1], ts[2], ts[3]);
            case CHANGE_TRUST:
                return trustlineXDR(ts[0], ts[1], ts[2]);
            case MANAGE_OFFER:
                return manageOffer(ts[0], ts[1], ts[2], ts[3], ts[4], ts[5], ts[6]);
        }
        return null;
    }

    String paymentToOtherXDR(String sourceS, String destinationS, String amount, String memo) {
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
            e.printStackTrace();
            return "" + R.string.kuknos_send_errorServer;
        }

        // If there was no error, load up-to-date information on your account.
        AccountResponse sourceAccount = null;
        try {
            sourceAccount = server.accounts().account(source.getAccountId());
        } catch (IOException e) {
            e.printStackTrace();
            return "" + R.string.kuknos_send_errorServer;
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
        return transaction.toEnvelopeXdrBase64();
    }

    String chargeWalletXDR(String sourceS, String amount, String receiptNumber, String memo) {
        Server server = new Server(KUKNOS_Horizan_Server);
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

    String chargeWalletOtherCurrencyXDR(String sourceS, String amount, String receiptNumber, String assetCode, String assetType, String memo) {
        Server server = new Server(KUKNOS_Horizan_Server);
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

    String trustlineXDR(String AccountSeed, String code, String issuer) {
        Server server = new Server(KUKNOS_Horizan_Server);
        Network network = new Network("Kuknos-NET");
        KeyPair source = KeyPair.fromSecretSeed(AccountSeed);
        Asset asset = new AssetTypeCreditAlphaNum4(code, issuer);

        // If there was no error, load up-to-date information on your account.
        AccountResponse sourceAccount = null;
        try {
            sourceAccount = server.accounts().account(source.getAccountId());
        } catch (IOException e) {
            e.printStackTrace();
            return "" + R.string.kuknos_send_errorServer;
        }

        Transaction transaction = new Transaction.Builder(Objects.requireNonNull(sourceAccount), network)
                .addOperation(new ChangeTrustOperation.Builder(asset, "" + Integer.MAX_VALUE).build())
                .addMemo(Memo.text(""))
                .setTimeout(60)
                .setOperationFee(1000)
                .build();
        // Sign the transaction to prove you are actually the person sending it.
        transaction.sign(source);
        return transaction.toEnvelopeXdrBase64();
    }

    String manageOffer(String accountSeed, String sourceCode, String sourceIssuer,
                       String counterCode, String counterIssuer, String amount, String price) {
        Server server = new Server(KUKNOS_Horizan_Server);
        Network network = new Network("Kuknos-NET");
        KeyPair source = KeyPair.fromSecretSeed(accountSeed);
        Asset sourceAsset = new AssetTypeCreditAlphaNum4(sourceCode, sourceIssuer);
        Asset counterAsset = new AssetTypeCreditAlphaNum4(counterCode, counterIssuer);

        // If there was no error, load up-to-date information on your account.
        AccountResponse sourceAccount = null;
        try {
            sourceAccount = server.accounts().account(source.getAccountId());
        } catch (IOException e) {
            e.printStackTrace();
            return "" + R.string.kuknos_send_errorServer;
        }

        Transaction transaction = new Transaction.Builder(Objects.requireNonNull(sourceAccount), network)
                .addOperation(new ManageSellOfferOperation.Builder(sourceAsset, counterAsset, amount, price).build())
                .addMemo(Memo.text(""))
                .setTimeout(60)
                .setOperationFee(1000)
                .build();
        // Sign the transaction to prove you are actually the person sending it.
        transaction.sign(source);
        return transaction.toEnvelopeXdrBase64();
    }

    public interface callBack {
        void getResponseXDR(String XDR);
    }
}
