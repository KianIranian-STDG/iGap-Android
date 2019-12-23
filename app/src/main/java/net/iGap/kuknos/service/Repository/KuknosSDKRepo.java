package net.iGap.kuknos.service.Repository;

import net.iGap.R;

import org.stellar.sdk.Asset;
import org.stellar.sdk.AssetTypeCreditAlphaNum4;
import org.stellar.sdk.ChangeTrustOperation;
import org.stellar.sdk.KeyPair;
import org.stellar.sdk.Memo;
import org.stellar.sdk.Network;
import org.stellar.sdk.Server;
import org.stellar.sdk.Transaction;
import org.stellar.sdk.responses.AccountResponse;

import java.io.IOException;
import java.util.Objects;

public class KuknosSDKRepo {

    private static final String KUKNOS_Horizan_Server = "https://hz1-test.kuknos.org" /*"https://horizon-testnet.stellar.org"*/;

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
