package net.iGap.kuknos.service.Repository;

import net.iGap.R;

import org.stellar.sdk.Asset;
import org.stellar.sdk.AssetTypeCreditAlphaNum4;
import org.stellar.sdk.AssetTypeNative;
import org.stellar.sdk.ChangeTrustOperation;
import org.stellar.sdk.KeyPair;
import org.stellar.sdk.Memo;
import org.stellar.sdk.Network;
import org.stellar.sdk.PaymentOperation;
import org.stellar.sdk.Server;
import org.stellar.sdk.Transaction;
import org.stellar.sdk.responses.AccountResponse;

import java.io.IOException;
import java.util.Objects;

public class KuknosSDKRepo {

    private static final String KUKNOS_Horizan_Server = "https://hz1-test.kuknos.org" /*"https://horizon-testnet.stellar.org"*/;

    public String paymentToOtherXDR(String sourceS, String destinationS, String amount, String memo) {
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

    public String TrustlineXDR(String AccountSeed, String code, String issuer) {
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
}
