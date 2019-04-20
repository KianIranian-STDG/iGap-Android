package net.iGap.adapter.items.discovery.holder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.nostra13.universalimageloader.core.DisplayImageOptions;

import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.FragmentIVandActivities;
import net.iGap.fragments.FragmentIVandProfile;
import net.iGap.fragments.FragmentPayment;
import net.iGap.fragments.FragmentPaymentBill;
import net.iGap.fragments.FragmentPaymentCharge;
import net.iGap.fragments.FragmentPaymentInquiry;
import net.iGap.fragments.FragmentWalletAgrement;
import net.iGap.fragments.FragmentWebView;
import net.iGap.fragments.FragmentiGapMap;
import net.iGap.fragments.discovery.DiscoveryFragment;
import net.iGap.adapter.items.discovery.DiscoveryItem;
import net.iGap.adapter.items.discovery.DiscoveryItemField;
import net.iGap.fragments.emoji.FragmentAddStickers;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperPermission;
import net.iGap.helper.HelperUrl;
import net.iGap.interfaces.OnGeoGetConfiguration;
import net.iGap.interfaces.OnGetPermission;
import net.iGap.module.SHP_SETTING;
import net.iGap.realm.RealmUserInfo;
import net.iGap.request.RequestClientSetDiscoveryItemClick;
import net.iGap.request.RequestGeoGetConfiguration;
import net.iGap.viewmodel.FragmentPaymentInquiryViewModel;

import org.paygear.wallet.WalletActivity;

import java.io.IOException;

import io.realm.Realm;

import static net.iGap.activities.ActivityMain.WALLET_REQUEST_CODE;
import static net.iGap.activities.ActivityMain.waitingForConfiguration;
import static net.iGap.fragments.FragmentiGapMap.mapUrls;
import static net.iGap.viewmodel.FragmentIVandProfileViewModel.scanBarCode;


public abstract class BaseViewHolder extends RecyclerView.ViewHolder {
    public static DisplayImageOptions option = new DisplayImageOptions.Builder().cacheOnDisk(true).build();


    BaseViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public abstract void bindView(DiscoveryItem item);

    void handleDiscoveryFieldsClick(DiscoveryItemField discoveryField) {
        new RequestClientSetDiscoveryItemClick().setDiscoveryClicked(discoveryField.id);
        switch (discoveryField.actionType) {
            case PAGE:/** tested **/
                actionPage(discoveryField.value);
                break;
            case JOIN_LINK:
                int index = discoveryField.value.lastIndexOf("/");
                if (index >= 0 && index < discoveryField.value.length() - 1) {
                    String token = discoveryField.value.substring(index + 1);
                    if (discoveryField.value.toLowerCase().contains("join")) {
                        HelperUrl.checkAndJoinToRoom(token);
                    } else {
                        HelperUrl.checkUsernameAndGoToRoom(token, HelperUrl.ChatEntry.profile);
                    }
                }
                break;
            case WEB_LINK:/** tested **/
                SharedPreferences sharedPreferences = G.context.getSharedPreferences(SHP_SETTING.FILE_NAME, G.context.MODE_PRIVATE);
                int checkedInAppBrowser = sharedPreferences.getInt(SHP_SETTING.KEY_IN_APP_BROWSER, 1);
                if (checkedInAppBrowser == 1 && !HelperUrl.isNeedOpenWithoutBrowser(discoveryField.value)) {
                    HelperUrl.openBrowser(discoveryField.value);
                }else {
                    HelperUrl.openWithoutBrowser(discoveryField.value);
                }
                break;
            case IVAND:
                new HelperFragment(new FragmentIVandProfile()).setReplace(false).load();
                break;
            case IVANDQR:
                scanBarCode(G.currentActivity);
                break;
            case IVANDLIST:
                new HelperFragment(FragmentIVandActivities.newInstance()).setReplace(false).load();
                break;
            case WEB_VIEW_LINK:/** tested title needed**/
                if (HelperUrl.isNeedOpenWithoutBrowser(discoveryField.value)) {
                    HelperUrl.openWithoutBrowser(discoveryField.value);
                } else {
                    new HelperFragment(FragmentWebView.newInstance(discoveryField.value)).setReplace(false).load();
                }
                break;
            case USERNAME_LINK:/** tested **/
                HelperUrl.checkUsernameAndGoToRoomWithMessageId(discoveryField.value.replace("@", ""), HelperUrl.ChatEntry.chat, 0);
                break;
            case TOPUP_MENU:/** tested **/
                new HelperFragment(FragmentPaymentCharge.newInstance()).setReplace(false).load();
                break;
            case BILL_MENU:/** tested **/
                new HelperFragment(FragmentPaymentBill.newInstance(R.string.pay_bills)).setReplace(false).load();
                break;
            case TRAFFIC_BILL_MENU:/** tested **/
                new HelperFragment(FragmentPaymentBill.newInstance(R.string.pay_bills_crime)).setReplace(false).load();
                break;
            case PHONE_BILL_MENU:/** tested **/
                new HelperFragment(FragmentPaymentInquiry.newInstance(FragmentPaymentInquiryViewModel.OperatorType.telecome, null)).setReplace(false).load();
                break;
            case MOBILE_BILL_MENU:/** tested **/
                new HelperFragment(FragmentPaymentInquiry.newInstance(FragmentPaymentInquiryViewModel.OperatorType.mci, null)).setReplace(false).load();
                break;
            case FINANCIAL_MENU:/** tested **/
                new HelperFragment(FragmentPayment.newInstance()).setReplace(false).load();
                break;
            case WALLET_MENU:/** tested **/
                try (Realm realm = Realm.getDefaultInstance()) {
                    RealmUserInfo userInfo = realm.where(RealmUserInfo.class).findFirst();
                    String phoneNumber = userInfo.getUserInfo().getPhoneNumber();
                    if (!G.isWalletRegister) {
                        new HelperFragment(FragmentWalletAgrement.newInstance(phoneNumber.substring(2))).load();
                    } else {
                        Intent intent = new Intent(G.context, WalletActivity.class);
                        intent.putExtra("Language", "fa");
                        intent.putExtra("Mobile", "0" + phoneNumber.substring(2));
                        intent.putExtra("PrimaryColor", G.appBarColor);
                        intent.putExtra("DarkPrimaryColor", G.appBarColor);
                        intent.putExtra("AccentColor", G.appBarColor);
                        intent.putExtra("IS_DARK_THEME", G.isDarkTheme);
                        intent.putExtra(WalletActivity.LANGUAGE, G.selectedLanguage);
                        intent.putExtra(WalletActivity.PROGRESSBAR, G.progressColor);
                        intent.putExtra(WalletActivity.LINE_BORDER, G.lineBorder);
                        intent.putExtra(WalletActivity.BACKGROUND, G.backgroundTheme);
                        intent.putExtra(WalletActivity.BACKGROUND_2, G.backgroundTheme);
                        intent.putExtra(WalletActivity.TEXT_TITLE, G.textTitleTheme);
                        intent.putExtra(WalletActivity.TEXT_SUB_TITLE, G.textSubTheme);
                        G.currentActivity.startActivityForResult(intent, WALLET_REQUEST_CODE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            case NEARBY_MENU:/** tested **/
                try {
                    HelperPermission.getLocationPermission(G.currentActivity, new OnGetPermission() {
                        @Override
                        public void Allow() throws IOException {
                            try {
                                if (!waitingForConfiguration) {
                                    waitingForConfiguration = true;
                                    if (mapUrls == null || mapUrls.isEmpty() || mapUrls.size() == 0) {
                                        G.onGeoGetConfiguration = new OnGeoGetConfiguration() {
                                            @Override
                                            public void onGetConfiguration() {
                                                G.handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        waitingForConfiguration = false;
                                                    }
                                                }, 2000);
                                                new HelperFragment(FragmentiGapMap.getInstance()).setReplace(false).load();

                                            }

                                            @Override
                                            public void getConfigurationTimeOut() {
                                                G.handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        waitingForConfiguration = false;
                                                    }
                                                }, 2000);
                                            }
                                        };
                                        new RequestGeoGetConfiguration().getConfiguration();
                                    } else {
                                        G.handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                waitingForConfiguration = false;
                                            }
                                        }, 2000);
                                        new HelperFragment(FragmentiGapMap.getInstance()).setReplace(false).load();
                                    }
                                }

                            } catch (Exception e) {
                                e.getStackTrace();
                            }
                        }

                        @Override
                        public void deny() {
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case REQUEST_PHONE:
                // this item is for bot
                break;
            case REQUEST_LOCATION:
                //this item is for bot
                break;
            case BOT_ACTION:
                //this item is for bot
                break;
            case PAY_BY_WALLET:
                // coming soon
                break;
            case PAY_DIRECT:
                // coming soon
                break;
            case CALL: /** tested **/
                dialPhoneNumber(G.context, discoveryField.value);
                break;
            case SHOW_ALERT:/** tested **/
                new AlertDialog.Builder(G.currentActivity)
                        .setMessage(discoveryField.value)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .show();
                break;
            case STREAM_PLAY:
                // coming soon
                break;
            case STICKER_SHOP:/** tested **/
                new HelperFragment(FragmentAddStickers.newInstance()).setReplace(false).load();
                break;
            case NONE:
                break;
            case UNRECOGNIZED:
                break;
        }
    }

    private void actionPage(String value) {
        new HelperFragment(DiscoveryFragment.newInstance(Integer.valueOf(value))).setReplace(false).load(false);
    }

    public void dialPhoneNumber(Context context, String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null));
        if (intent.resolveActivity(G.context.getPackageManager()) != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

}
