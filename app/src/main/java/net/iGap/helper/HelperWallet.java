package net.iGap.helper;

import android.content.Context;
import android.content.Intent;

import net.iGap.G;
import net.iGap.module.Theme;

import org.paygear.WalletActivity;
import org.paygear.model.Payment;

public class HelperWallet {
    public Intent goToWallet(Context context, Intent intent, String phoneNumber, boolean goToScanner) {
        intent.putExtra("Language", "fa");
        intent.putExtra("Mobile", phoneNumber);
        intent.putExtra("PrimaryColor", String.format("#%06X", 0xFFFFFF & new Theme().getPrimaryColor(context)));
        intent.putExtra("DarkPrimaryColor", String.format("#%06X", 0xFFFFFF & new Theme().getPrimaryDarkColor(context)));
        intent.putExtra("AccentColor", String.format("#%06X", 0xFFFFFF & new Theme().getAccentColor(context)));
        intent.putExtra("IS_DARK_THEME", G.themeColor == Theme.DARK);
        intent.putExtra(WalletActivity.LANGUAGE, G.selectedLanguage);
        intent.putExtra(WalletActivity.PROGRESSBAR, String.format("#%06X", 0xFFFFFF & new Theme().getAccentColor(context)));
        intent.putExtra(WalletActivity.LINE_BORDER, String.format("#%06X", 0xFFFFFF & new Theme().getDividerColor(context)));
        intent.putExtra(WalletActivity.BACKGROUND, String.format("#%06X", 0xFFFFFF & new Theme().getRootColor(context)));
        intent.putExtra(WalletActivity.BACKGROUND_2, String.format("#%06X", 0xFFFFFF & new Theme().getRootColor(context)));
        intent.putExtra(WalletActivity.TEXT_TITLE, String.format("#%06X", 0xFFFFFF & new Theme().getTitleTextColor(context)));
        intent.putExtra(WalletActivity.TEXT_SUB_TITLE, String.format("#%06X", 0xFFFFFF & new Theme().getSubTitleColor(context)));
        intent.putExtra("isScan", goToScanner);
        return intent;
    }

    public Intent goToWallet(Payment payment, Context context, Intent intent, String phoneNumber, boolean goToScanner) {
        Intent intent1 = goToWallet(context, intent, phoneNumber, goToScanner);
        intent1.putExtra("Payment", payment);
        intent1.putExtra("IsP2P", true);
        return intent1;
    }
}
