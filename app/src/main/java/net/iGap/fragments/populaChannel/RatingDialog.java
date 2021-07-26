package net.iGap.fragments.populaChannel;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.Task;
import com.huawei.hms.api.ConnectionResult;
import com.huawei.hms.api.HuaweiApiAvailability;

import net.iGap.G;
import net.iGap.R;
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
                .itemsColorAttr(R.attr.iGapTitleTextColor)
                .itemsGravity(GravityEnum.START)
                .buttonsGravity(GravityEnum.CENTER)
                .positiveText(R.string.ok)
                .positiveColorAttr(R.attr.colorAccent)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        boolean isPhoneHms = ((HuaweiApiAvailability.getInstance().isHuaweiMobileServicesAvailable(context)) == ConnectionResult.SUCCESS);

                        if (isFromCafeBazaar(activity) || isPhoneHms) {
                            Intent intent = new Intent(Intent.ACTION_EDIT);
                            intent.setData(Uri.parse("bazaar://details?id=" + context.getPackageName()));
                            intent.setPackage("com.farsitel.bazaar");
                            activity.startActivity(intent);
                            sharedPreferences.edit().putBoolean(SHP_SETTING.KEY_DO_USER_RATE_APP, true).apply();
                            dialog.dismiss();

                        } else {

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
                                }
                            });
                        }

                    }
                })
                .negativeText(R.string.remind_me_later)
                .negativeColorAttr(R.attr.iGapSubtitleTextColor)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        sharedPreferences.edit().putLong(SHP_SETTING.KEY_LOGIN_TIME_STAMP, currentTimeStamp).apply();
                        dialog.dismiss();
                    }
                });
        MaterialDialog dialog = builder.build();
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
}
