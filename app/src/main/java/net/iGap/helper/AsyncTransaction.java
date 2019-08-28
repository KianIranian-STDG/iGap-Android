package net.iGap.helper;

import android.app.ProgressDialog;
import android.content.Context;

import io.realm.Realm;

public class AsyncTransaction {

    public static void executeTransactionWithLoading(Context context, Realm realm, Realm.Transaction transaction, Realm.Transaction.OnSuccess onSuccess) {
        ProgressDialog dialog = ProgressDialog.show(context, "",
                "Loading. Please wait...", true);
        dialog.setCancelable(false);
        realm.executeTransactionAsync(transaction, () -> {
            dialog.dismiss();
            if (onSuccess != null)
                onSuccess.onSuccess();
        });
    }

    public static void executeTransactionWithLoading(Context context, Realm realm, Realm.Transaction transaction) {
        executeTransactionWithLoading(context, realm, transaction, null);
    }
}
