package net.iGap.helper;

import android.content.Context;
import android.content.Intent;

import net.iGap.G;
import net.iGap.Theme;

import org.paygear.WalletActivity;

import static net.iGap.activities.ActivityMain.WALLET_REQUEST_CODE;

public class HelperWallet {
    public void goToWallet(Context context, String phoneNumber) {
        Intent intent = new Intent(context, WalletActivity.class);
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
        intent.putExtra("isScan", true);
        G.currentActivity.startActivityForResult(intent, WALLET_REQUEST_CODE);
    }
}
