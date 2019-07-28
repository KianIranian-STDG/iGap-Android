package net.iGap.adapter.items.discovery.holder;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import net.iGap.adapter.items.discovery.DiscoveryItem;
import net.iGap.adapter.items.discovery.DiscoveryItemField;
import net.iGap.fragments.discovery.DiscoveryFragment;
import net.iGap.fragments.discovery.DiscoveryFragmentAgreement;
import net.iGap.fragments.favoritechannel.FavoriteChannelFragment;
import net.iGap.helper.HelperFragment;
import net.iGap.request.RequestClientSetDiscoveryItemClick;


public abstract class BaseViewHolder extends RecyclerView.ViewHolder {
    private long mLastClickTime = 0;
    private FragmentActivity activity;

    BaseViewHolder(@NonNull View itemView, FragmentActivity activity) {
        super(itemView);
        this.activity = activity;
    }

    public abstract void bindView(DiscoveryItem item);

    void loadImage(ImageView imageView, String url) {
        if (url.endsWith(".gif")) {
            Glide.with(imageView.getContext())
                    .asGif()
                    .load(url)
                    .apply(new RequestOptions().fitCenter().format(DecodeFormat.PREFER_ARGB_8888).override(Target.SIZE_ORIGINAL))
                    .into(imageView);
        } else {
            Glide.with(imageView.getContext()).load(url)
                    .apply(new RequestOptions()
                            .fitCenter()
                            .format(DecodeFormat.PREFER_ARGB_8888)
                            .override(Target.SIZE_ORIGINAL)).into(imageView);
        }
    }

    void handleDiscoveryFieldsClick(DiscoveryItemField discoveryField) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        handleDiscoveryFieldsClickStatic(discoveryField, activity);
    }

    public static void handleDiscoveryFieldsClickStatic(DiscoveryItemField discoveryField, FragmentActivity activity) {
        if (activity == null || activity.isFinishing()) {
            return;
        }

        if (discoveryField.agreementSlug != null && discoveryField.agreementSlug.length() > 1) {
            if (!discoveryField.agreement) {
                new HelperFragment(activity.getSupportFragmentManager(), DiscoveryFragmentAgreement.newInstance(discoveryField, discoveryField.agreementSlug)).setReplace(false).load();
                return;
            }
        }

        new RequestClientSetDiscoveryItemClick().setDiscoveryClicked(discoveryField.id);
        new HelperFragment(activity.getSupportFragmentManager(), new FavoriteChannelFragment()).setReplace(false).load();

//        switch (discoveryField.actionType) {
//            case PAGE:/** tested **/
//                actionPage(discoveryField.value, activity);
//                break;
//            case JOIN_LINK:
//                int index = discoveryField.value.lastIndexOf("/");
//                if (index >= 0 && index < discoveryField.value.length() - 1) {
//                    String token = discoveryField.value.substring(index + 1);
//                    if (discoveryField.value.toLowerCase().contains("join")) {
//                        HelperUrl.checkAndJoinToRoom(activity, token);
//                    } else {
//                        HelperUrl.checkUsernameAndGoToRoom(activity, token, HelperUrl.ChatEntry.profile);
//                    }
//                }
//                break;
//            case WEB_LINK:/** tested **/
//                SharedPreferences sharedPreferences = activity.getSharedPreferences(SHP_SETTING.FILE_NAME, Context.MODE_PRIVATE);
//                int checkedInAppBrowser = sharedPreferences.getInt(SHP_SETTING.KEY_IN_APP_BROWSER, 1);
//                if (checkedInAppBrowser == 1 && !HelperUrl.isNeedOpenWithoutBrowser(discoveryField.value)) {
//                    HelperUrl.openBrowser(discoveryField.value);
//                } else {
//                    HelperUrl.openWithoutBrowser(discoveryField.value);
//                }
//                break;
//            case IVAND:
//                new HelperFragment(activity.getSupportFragmentManager(), new FragmentUserScore()).setReplace(false).load();
//                break;
//            case IVANDQR:
//                scanBarCode(activity);
//                break;
//            case IVANDLIST:
//                new HelperFragment(activity.getSupportFragmentManager(), FragmentIVandActivities.newInstance()).setReplace(false).load();
//                break;
//            case WEB_VIEW_LINK:/** tested title needed**/
//                if (HelperUrl.isNeedOpenWithoutBrowser(discoveryField.value)) {
//                    HelperUrl.openWithoutBrowser(discoveryField.value);
//                } else {
//                    new HelperFragment(activity.getSupportFragmentManager(), FragmentWebView.newInstance(discoveryField.value, discoveryField.refresh, discoveryField.param)).setReplace(false).load();
//                }
//                break;
//            case USERNAME_LINK:/** tested **/
//                HelperUrl.checkUsernameAndGoToRoomWithMessageId(activity, discoveryField.value.replace("@", ""), HelperUrl.ChatEntry.chat, 0);
//                break;
//            case TOPUP_MENU:/** tested **/
//                new HelperFragment(activity.getSupportFragmentManager(), FragmentPaymentCharge.newInstance()).setReplace(false).load();
//                break;
//            case BILL_MENU:/** tested **/
//                try {
//                    JSONObject jsonObject = new JSONObject(discoveryField.value);
//                    new HelperFragment(activity.getSupportFragmentManager(), FragmentPaymentBill.newInstance(R.string.pay_bills, jsonObject)).setReplace(false).load();
//                } catch (JSONException e) {
//                    new HelperFragment(activity.getSupportFragmentManager(), FragmentPaymentBill.newInstance(R.string.pay_bills)).setReplace(false).load();
//                }
//                break;
//            case TRAFFIC_BILL_MENU:/** tested **/
//                try {
//                    JSONObject jsonObject = new JSONObject(discoveryField.value);
//                    new HelperFragment(activity.getSupportFragmentManager(), FragmentPaymentBill.newInstance(R.string.pay_bills_crime, jsonObject)).setReplace(false).load();
//                } catch (JSONException e) {
//                    new HelperFragment(activity.getSupportFragmentManager(), FragmentPaymentBill.newInstance(R.string.pay_bills_crime)).setReplace(false).load();
//                }
//                break;
//            case PHONE_BILL_MENU:/** tested **/
//                new HelperFragment(activity.getSupportFragmentManager(), FragmentPaymentInquiry.newInstance(FragmentPaymentInquiryViewModel.OperatorType.telecome, null)).setReplace(false).load();
//                break;
//            case MOBILE_BILL_MENU:/** tested **/
//                new HelperFragment(activity.getSupportFragmentManager(), FragmentPaymentInquiry.newInstance(FragmentPaymentInquiryViewModel.OperatorType.mci, null)).setReplace(false).load();
//                break;
//            case FINANCIAL_MENU:/** tested **/
//                new HelperFragment(activity.getSupportFragmentManager(), FragmentPayment.newInstance()).setReplace(false).load();
//                break;
//            case WALLET_MENU:/** tested **/
//                try (Realm realm = Realm.getDefaultInstance()) {
//                    RealmUserInfo userInfo = realm.where(RealmUserInfo.class).findFirst();
//                    String phoneNumber = userInfo.getUserInfo().getPhoneNumber();
//                    if (!G.isWalletRegister) {
//                        if (discoveryField.value.equals("QR_USER_WALLET")) {
//                            new HelperFragment(activity.getSupportFragmentManager(), FragmentWalletAgrement.newInstance(phoneNumber.substring(2), true)).load();
//                        } else {
//                            new HelperFragment(activity.getSupportFragmentManager(), FragmentWalletAgrement.newInstance(phoneNumber.substring(2), false)).load();
//                        }
//                    } else {
//
//                        Intent intent = new Intent(activity, WalletActivity.class);
//                        intent.putExtra("Language", "fa");
//                        intent.putExtra("Mobile", "0" + phoneNumber.substring(2));
//                        intent.putExtra("PrimaryColor", G.appBarColor);
//                        intent.putExtra("DarkPrimaryColor", G.appBarColor);
//                        intent.putExtra("AccentColor", G.appBarColor);
//                        intent.putExtra("IS_DARK_THEME", G.isDarkTheme);
//                        intent.putExtra(WalletActivity.LANGUAGE, G.selectedLanguage);
//                        intent.putExtra(WalletActivity.PROGRESSBAR, G.progressColor);
//                        intent.putExtra(WalletActivity.LINE_BORDER, G.lineBorder);
//                        intent.putExtra(WalletActivity.BACKGROUND, G.backgroundTheme);
//                        intent.putExtra(WalletActivity.BACKGROUND_2, G.backgroundTheme);
//                        intent.putExtra(WalletActivity.TEXT_TITLE, G.textTitleTheme);
//                        intent.putExtra(WalletActivity.TEXT_SUB_TITLE, G.textSubTheme);
//                        if (discoveryField.value.equals("QR_USER_WALLET")) {
//                            intent.putExtra("isScan", true);
//                        } else {
//                            intent.putExtra("isScan", true);
//                        }
//                        G.currentActivity.startActivityForResult(intent, WALLET_REQUEST_CODE);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//                break;
//            case NEARBY_MENU:/** tested **/
//                try {
//                    HelperPermission.getLocationPermission(activity, new OnGetPermission() {
//                        @Override
//                        public void Allow() throws IOException {
//                            try {
//                                if (!waitingForConfiguration) {
//                                    waitingForConfiguration = true;
//                                    if (mapUrls == null || mapUrls.isEmpty() || mapUrls.size() == 0) {
//                                        G.onGeoGetConfiguration = new OnGeoGetConfiguration() {
//                                            @Override
//                                            public void onGetConfiguration() {
//                                                G.handler.postDelayed(new Runnable() {
//                                                    @Override
//                                                    public void run() {
//                                                        waitingForConfiguration = false;
//                                                    }
//                                                }, 2000);
//                                                new HelperFragment(activity.getSupportFragmentManager(), FragmentiGapMap.getInstance()).setReplace(false).load();
//
//                                            }
//
//                                            @Override
//                                            public void getConfigurationTimeOut() {
//                                                G.handler.postDelayed(new Runnable() {
//                                                    @Override
//                                                    public void run() {
//                                                        waitingForConfiguration = false;
//                                                    }
//                                                }, 2000);
//                                            }
//                                        };
//                                        new RequestGeoGetConfiguration().getConfiguration();
//                                    } else {
//                                        G.handler.postDelayed(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                waitingForConfiguration = false;
//                                            }
//                                        }, 2000);
//                                        new HelperFragment(activity.getSupportFragmentManager(), FragmentiGapMap.getInstance()).setReplace(false).load();
//                                    }
//                                }
//
//                            } catch (Exception e) {
//                                e.getStackTrace();
//                            }
//                        }
//
//                        @Override
//                        public void deny() {
//                        }
//                    });
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                break;
//            case REQUEST_PHONE:
//                // this item is for bot
//                break;
//            case REQUEST_LOCATION:
//                //this item is for bot
//                break;
//            case BOT_ACTION:
//                //this item is for bot
//                break;
//            case PAY_BY_WALLET:
//                // coming soon
//                break;
//            case PAY_DIRECT:
//                try {
//                    JSONObject jsonObject = new JSONObject(discoveryField.value);
//                    DirectPayHelper.directPay(jsonObject);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                break;
//            case CALL: /** tested **/
//                dialPhoneNumber(activity, discoveryField.value, activity);
//                break;
//            case SHOW_ALERT:/** tested **/
//                new MaterialDialog.Builder(activity).content(discoveryField.value).positiveText(R.string.dialog_ok)
//                        .onPositive(new MaterialDialog.SingleButtonCallback() {
//                            @Override
//                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//
//                            }
//                        })
//                        .show();
//                break;
//            case STREAM_PLAY:
//                // coming soon
//                break;
//            case STICKER_SHOP:/** tested **/
//                new HelperFragment(activity.getSupportFragmentManager(), FragmentSettingAddStickers.newInstance()).setReplace(false).load();
//                break;
//            case CARD_TO_CARD:
//                CardToCardHelper.CallCardToCard(activity);
//                break;
//            case IVANDSCORE:
//                ActivityMain.doIvandScore(discoveryField.value, activity);
//                break;
//            case NONE:
//                break;
//            case POLL:
//                new HelperFragment(activity.getSupportFragmentManager(), PollFragment.newInstance(Integer.valueOf(discoveryField.value))).setReplace(false).load();
//                break;
//            case UNRECOGNIZED:
//                break;
//            case FAVORITE_CHANNEL:
//                new HelperFragment(activity.getSupportFragmentManager(), new FavoriteChannelFragment()).setReplace(false).load();
//                break;
//        }
    }

    private static void actionPage(String value, FragmentActivity activity) {
        new HelperFragment(activity.getSupportFragmentManager(), DiscoveryFragment.newInstance(Integer.valueOf(value))).setReplace(false).load(false);
    }

    public static void dialPhoneNumber(Context context, String phoneNumber, FragmentActivity activity) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null));
        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

}
