package net.iGap.fragments.populaChannel;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.Task;

import net.iGap.BuildConfig;
import net.iGap.G;
import net.iGap.R;
import net.iGap.module.SHP_SETTING;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class RatingDialog {

    private static final String TAG = "RatingAlertDialogTag";
    private static SharedPreferences sharedPreferences;


    @SuppressLint("ResourceType")
    public static void show(Activity activity) {
        sharedPreferences = G.context.getSharedPreferences(SHP_SETTING.FILE_NAME, Context.MODE_PRIVATE);

        long currentTimeStamp = new Date().getTime();
        long loginTimeStamp = sharedPreferences.getLong(SHP_SETTING.KEY_LOGIN_TIME_STAMP, 0);
        long showDialogPeriodTimeMs = BuildConfig.SHOW_RATE_DIALOG_PERIOD_HOURE * 60 * 60 * 1000;
        if (currentTimeStamp - loginTimeStamp >= 30000) {
            MaterialDialog.Builder builder = new MaterialDialog.Builder(activity);
            builder
                    .title(R.string.score_request_text)
                    .titleGravity(GravityEnum.START)
                    .buttonsGravity(GravityEnum.CENTER)
                    .titleColorAttr(R.attr.iGapTitleTextColor)
                    .positiveText(R.string.ok)
                    .positiveColorAttr(R.attr.colorAccent)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                            ReviewManager manager = ReviewManagerFactory.create(activity);
                            Task<ReviewInfo> request = manager.requestReviewFlow();
                            request.addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    // We can get the ReviewInfo object
                                    ReviewInfo reviewInfo = task.getResult();
                                } else {
                                    // There was some problem, log or handle the error code.
                                }
                            });

//                                if (isFromCafeBazaar(getActivity())) {
//                                    Intent intent = new Intent(Intent.ACTION_EDIT);
//                                    intent.setData(Uri.parse("bazaar://details?id=" + getActivity().getPackageName()));
//                                    intent.setPackage("com.farsitel.bazaar");
//                                    getActivity().startActivity(intent);
//
//                                } else {
//                                    ReviewManager manager = ReviewManagerFactory.create(getActivity());
//                                    Task<ReviewInfo> request = manager.requestReviewFlow();
//                                    request.addOnCompleteListener(task -> {
//                                    });
//                                }
                        }
                    })
                    .negativeText(R.string.remind_me_later)
                    .negativeColorAttr(R.attr.colorAccent)
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

//        if (!activity.isFinishing()) {
//            activity.runOnUiThread(() -> {
//                if (activity.hasWindowFocus()) {
//                    new MaterialDialog.Builder(activity)
//                            .title("به آیگپ امتیاز بدهید")
//                            .titleColor(new Theme().getTitleTextColor(activity))
//                            .titleGravity(GravityEnum.CENTER)
//                            .buttonsGravity(GravityEnum.CENTER)
//                            .neutralText("بعدا")
//                            .neutralColor(new Theme().getAccentColor(activity))
//                            .negativeText("هرگز")
//                            .onNeutral((dialog, which) -> {
//
//                            })
//                            .negativeColor(new Theme().getAccentColor(activity))
//                            .onNegative((dialog, which) -> dialog.dismiss())
//                            .positiveText("باشه")
//                            .onPositive((dialog, which) -> {
//                                if (isFromPlayStore(activity)) {
//                                    ReviewManager manager = ReviewManagerFactory.create(activity);
//                                    Task<ReviewInfo> request = manager.requestReviewFlow();
//                                    request.addOnCompleteListener(task -> {
//                                        Log.i(TAG, "rating completed");
//                                    });
//                                } else {
//                                    Intent intent = new Intent(Intent.ACTION_EDIT);
//                                    intent.setData(Uri.parse("bazaar://details?id=" + activity.getPackageName()));
//                                    intent.setPackage("com.farsitel.bazaar");
//                                    activity.startActivity(intent);
//                                }
//                            })
//                            .show();
//                }
//            });
//        }
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
