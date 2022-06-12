package net.iGap.fragments.populaChannel;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.Task;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.FileLog;
import net.iGap.helper.HelperLog;
import net.iGap.messenger.theme.Theme;
import net.iGap.module.SHP_SETTING;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static net.iGap.G.context;

public class RatingDialog {

    private static SharedPreferences sharedPreferences;

    @SuppressLint("ResourceType")
    public static void show(Activity activity, long currentTimeStamp) {
        sharedPreferences = G.context.getSharedPreferences(SHP_SETTING.FILE_NAME, Context.MODE_PRIVATE);

        MaterialDialog.Builder builder = new MaterialDialog.Builder(activity);
        builder
                .items(activity.getResources().getString(R.string.score_request_text))
                .backgroundColor(Theme.getColor(Theme.key_popup_background))
                .itemsColor(Theme.getColor(Theme.key_title_text))
                .itemsGravity(GravityEnum.START)
                .buttonsGravity(GravityEnum.CENTER)
                .positiveText(R.string.ok)
                .positiveColorAttr(R.attr.colorAccent)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        if (isFromPlayStore(activity)) {
                            ReviewManager manager = ReviewManagerFactory.create(activity);
                            Task<ReviewInfo> request = manager.requestReviewFlow();
                            request.addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    // We can get the ReviewInfo object
                                    Task<Void> flow = manager.launchReviewFlow(activity, task.getResult());
                                    flow.addOnCompleteListener(task1 -> {
                                        sharedPreferences.edit().putBoolean(SHP_SETTING.KEY_DO_USER_RATE_APP, true).apply();
                                        dialog.dismiss();
                                    });
                                } else {
                                    Toast.makeText(activity, R.string.please_try_later, Toast.LENGTH_LONG).show();
                                    sharedPreferences.edit().putLong(SHP_SETTING.KEY_LOGIN_TIME_STAMP, currentTimeStamp).apply();
                                    dialog.dismiss();
                                }
                            });
                        } else if (isFromCafeBazaar(activity)) {
                            Intent intent = new Intent(Intent.ACTION_EDIT);
                            intent.setData(Uri.parse("bazaar://details?id=" + context.getPackageName()));
                            intent.setPackage("com.farsitel.bazaar");
                            try {
                                activity.startActivity(intent);
                            } catch (ActivityNotFoundException e) {
                                FileLog.e(e);
                            }
                            sharedPreferences.edit().putBoolean(SHP_SETTING.KEY_DO_USER_RATE_APP, true).apply();
                            dialog.dismiss();
                        } else {
                            openChooseMarketDialog(activity, dialog);
                        }
                    }
                })
                .negativeText(R.string.remind_me_later)
                .negativeColor(Theme.getColor(Theme.key_subtitle_text))
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        sharedPreferences.edit().putLong(SHP_SETTING.KEY_LOGIN_TIME_STAMP, currentTimeStamp).apply();
                        dialog.dismiss();
                    }
                });
        MaterialDialog dialog = builder.negativeColor(Theme.getColor(Theme.key_button_background))
                .positiveColor(Theme.getColor(Theme.key_button_background)).build();
        dialog.show();
    }

    private static boolean isFromPlayStore(Activity activity) {
        List<String> validInstallers = new ArrayList<>(Arrays.asList("com.android.vending", "com.google.android.feedback"));
        final String installer = activity.getPackageManager().getInstallerPackageName(activity.getPackageName());
        return installer != null && validInstallers.contains(installer);
    }

    private static boolean isFromCafeBazaar(Activity activity) {
        List<String> validInstallers = new ArrayList<>(Arrays.asList("com.farsitel.bazaar"));
        final String installer = activity.getPackageManager().getInstallerPackageName(activity.getPackageName());
        return installer != null && validInstallers.contains(installer);
    }

    private static void openChooseMarketDialog(Activity activity, Dialog dialog) {
        try {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + context.getPackageName())));
        } catch (android.content.ActivityNotFoundException anfe) {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + context.getPackageName())));
        } finally {
            sharedPreferences.edit().putBoolean(SHP_SETTING.KEY_DO_USER_RATE_APP, true).apply();
            dialog.dismiss();
        }
    }

}
