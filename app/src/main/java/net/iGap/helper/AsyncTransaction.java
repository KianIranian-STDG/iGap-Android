package net.iGap.helper;

import android.content.Context;

import io.realm.Realm;

public class AsyncTransaction {

    public static void executeTransactionWithLoading(iGapLoading dialog, Realm realm, Realm.Transaction transaction, Realm.Transaction.OnSuccess onSuccess) {

        if (!dialog.isShowing())
            dialog.show();

        realm.executeTransactionAsync(transaction, () -> {
            if (dialog.isShowing())
                dialog.dismiss();

            if (onSuccess != null)
                onSuccess.onSuccess();
        });
    }

    public static void executeTransactionWithLoading(iGapLoading dialog, Realm realm, Realm.Transaction transaction) {
        executeTransactionWithLoading(dialog, realm, transaction, null);
    }

    public static void executeTransactionWithLoading(Context context, Realm realm, Realm.Transaction transaction, Realm.Transaction.OnSuccess onSuccess) {
        executeTransactionWithLoading(iGapLoading.createGlobalLoading(context), realm, transaction, onSuccess);
    }

    public static void executeTransactionWithLoading(Context context, Realm realm, Realm.Transaction transaction) {
        executeTransactionWithLoading(context, realm, transaction, null);
    }
}
