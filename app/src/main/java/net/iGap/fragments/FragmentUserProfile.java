package net.iGap.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.crashlytics.android.Crashlytics;
import com.google.zxing.integration.android.IntentIntegrator;

import net.iGap.G;
import net.iGap.R;
import net.iGap.Theme;
import net.iGap.activities.ActivityMain;
import net.iGap.eventbus.EventListener;
import net.iGap.eventbus.EventManager;
import net.iGap.eventbus.socketMessages;
import net.iGap.helper.GoToChatActivity;
import net.iGap.helper.HelperAvatar;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperImageBackColor;
import net.iGap.helper.HelperLogout;
import net.iGap.helper.HelperPermission;
import net.iGap.helper.HelperUrl;
import net.iGap.interfaces.OnAvatarGet;
import net.iGap.interfaces.OnChangeUserPhotoListener;
import net.iGap.interfaces.OnChatGetRoom;
import net.iGap.interfaces.OnGeoGetConfiguration;
import net.iGap.interfaces.OnGetPermission;
import net.iGap.interfaces.OnUserInfoMyClient;
import net.iGap.interfaces.OnUserSessionLogout;
import net.iGap.interfaces.RefreshWalletBalance;
import net.iGap.module.AndroidUtils;
import net.iGap.module.SHP_SETTING;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomFields;
import net.iGap.realm.RealmUserInfo;
import net.iGap.request.RequestChatGetRoom;
import net.iGap.request.RequestGeoGetConfiguration;
import net.iGap.request.RequestUserSessionLogout;
import net.iGap.request.RequestWalletGetAccessToken;
import net.iGap.viewmodel.FragmentThemColorViewModel;

import org.paygear.wallet.WalletActivity;
import org.paygear.wallet.model.Card;
import org.paygear.wallet.web.Web;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import ir.radsense.raadcore.model.Auth;
import ir.radsense.raadcore.web.WebBase;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.os.Looper.getMainLooper;
import static net.iGap.activities.ActivityMain.waitingForConfiguration;
import static net.iGap.fragments.FragmentiGapMap.mapUrls;


public class FragmentUserProfile extends Fragment implements OnUserInfoMyClient , RefreshWalletBalance , EventListener {

    private View mView ;
    
    private CircleImageView mAvatar ;
    private TextView mUserName , mUserPhone , mUserBio ;
    private ViewGroup mCreditBtn , mScoreBtn ;
    private ViewGroup mCloudBtn , mAddBtn , mSettingsBtn ;
    private ViewGroup mInviteFriendBtn , mQrScannerBtn , mNearByBtn , mLanguageBtn ,mDarkThemeBtn , mFaqBtn , mLogoutBtn;
    private TextView mLanguageTxt , mAppVersionTxt , mCreditAmountTxt , mScoreTxt;
    private ToggleButton mDarkModeSwitch ;

    private SharedPreferences sharedPreferences;
    private Realm mRealm;
    private RealmUserInfo userInfo;
    private String phoneNumber;
    private int retryConnectToWallet = 0;


    public FragmentUserProfile() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        
        mView = inflater.inflate(R.layout.fragment_user_profile, container, false);
        
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initViews();
        initData();
        
        getUserInfo();
        
        setListenerToViews();
    }

    private void initData() {

        sharedPreferences = G.context.getSharedPreferences(SHP_SETTING.FILE_NAME, Context.MODE_PRIVATE);
        String textLanguage = sharedPreferences.getString(SHP_SETTING.KEY_LANGUAGE, Locale.getDefault().getDisplayLanguage());
        mLanguageTxt.setText(textLanguage);

    }

    private void setListenerToViews() {

        mCloudBtn.setOnClickListener( v -> {

            chatGetRoom(G.userId);
        });

        mSettingsBtn.setOnClickListener( v -> {
            new HelperFragment(new FragmentSetting()).load();
        });

        mAddBtn.setOnClickListener( v -> {
            final Fragment fragment = RegisteredContactsFragment.newInstance();
            Bundle bundle = new Bundle();
            bundle.putString("TITLE", "ADD");
            fragment.setArguments(bundle);
            try {
                new HelperFragment(fragment).load();
            } catch (Exception e) {
                e.getStackTrace();
            }
        });

        mCreditBtn.setOnClickListener( v -> {

            if (!G.isWalletRegister) {
                new HelperFragment(FragmentWalletAgrement.newInstance(phoneNumber.substring(2))).load();
            } else {
                Intent intent = new Intent(getActivity(), WalletActivity.class);
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
                startActivityForResult(intent, ActivityMain.WALLET_REQUEST_CODE);
            }

        });

        mScoreBtn.setOnClickListener( v -> {

        });

        mInviteFriendBtn.setOnClickListener( v -> {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Hey Join iGap : https://www.igap.net I'm waiting for you!");
            sendIntent.setType("text/plain");
            Intent openInChooser = Intent.createChooser(sendIntent, "Open in...");
            G.context.startActivity(openInChooser);
        });

        mLanguageBtn.setOnClickListener( v -> {

            new HelperFragment(new FragmentLanguage()).setReplace(false).load();
        });

        mLogoutBtn.setOnClickListener( v -> {
            getLogout();
        });

        mQrScannerBtn.setOnClickListener( v -> {
            try {
                HelperPermission.getCameraPermission(getActivity(), new OnGetPermission() {
                    @Override
                    public void Allow() throws IOException, IllegalStateException {
                        IntentIntegrator integrator = new IntentIntegrator(getActivity());
                        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                        integrator.setRequestCode(ActivityMain.requestCodeQrCode);
                        integrator.setBeepEnabled(false);
                        integrator.setPrompt("");
                        integrator.initiateScan();
                    }

                    @Override
                    public void deny() {
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        mNearByBtn.setOnClickListener( v -> {
            openMapFragment();
        });

        mFaqBtn.setOnClickListener( v -> {

            final String blogLink;
            if (HelperCalander.isPersianUnicode) {
                blogLink = "https://blog.igap.net/fa";
            } else {
                blogLink = "https://blog.igap.net";
            }

            HelperUrl.openBrowser(blogLink);
        });

        mDarkThemeBtn.setOnClickListener(v -> mDarkModeSwitch.performClick());

        mDarkModeSwitch.setOnClickListener(v -> {

            SharedPreferences.Editor editor = sharedPreferences.edit();
            if (mDarkModeSwitch.isChecked()) {

                int themeColor = sharedPreferences.getInt(SHP_SETTING.KEY_THEME_COLOR, Theme.CUSTOM);
                editor.putInt(SHP_SETTING.KEY_THEME_COLOR, Theme.DARK);
                editor.putInt(SHP_SETTING.KEY_OLD_THEME_COLOR, themeColor);
                editor.apply();
                Theme.setThemeColor();
                FragmentThemColorViewModel.resetApp();
            } else {
                int themeColor = sharedPreferences.getInt(SHP_SETTING.KEY_OLD_THEME_COLOR, Theme.CUSTOM);

                editor.putInt(SHP_SETTING.KEY_THEME_COLOR, themeColor);
                editor.apply();
                Theme.setThemeColor();
                FragmentThemColorViewModel.resetApp();

            }
        });

    }

    private void getLogout() {

        new MaterialDialog.Builder(getActivity()).title(getResources().getString(R.string.log_out))
                .content(R.string.content_log_out)
                .positiveText(getResources().getString(R.string.B_ok))
                .negativeText(getResources().getString(R.string.B_cancel))
                .iconRes(R.mipmap.exit_to_app_button)
                .maxIconSize((int) getResources().getDimension(R.dimen.dp24))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        G.onUserSessionLogout = new OnUserSessionLogout() {
                            @Override
                            public void onUserSessionLogout() {

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        G.selectedCard = null;
                                        HelperLogout.logout();
                                    }
                                });
                            }

                            @Override
                            public void onError() {

                            }

                            @Override
                            public void onTimeOut() {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        HelperError.showSnackMessage(getResources().getString(R.string.error), false);

                                    }
                                });
                            }
                        };
                        new RequestUserSessionLogout().userSessionLogout();
                    }
                })
                .show();

    }

    private void openMapFragment() {
        try {
            HelperPermission.getLocationPermission(getActivity(), new OnGetPermission() {
                @Override
                public void Allow() throws IOException {
                    try {
                        if (!waitingForConfiguration) {
                            waitingForConfiguration = true;
                            if (mapUrls == null || mapUrls.isEmpty() || mapUrls.size() == 0) {
                                G.onGeoGetConfiguration = new OnGeoGetConfiguration() {
                                    @Override
                                    public void onGetConfiguration() {
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                G.handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        waitingForConfiguration = false;
                                                    }
                                                }, 2000);
                                                new HelperFragment(FragmentiGapMap.getInstance()).load();
                                            }
                                        });
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
                                new HelperFragment(FragmentiGapMap.getInstance()).load();
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
    }

    private void chatGetRoom(final long peerId) {

        final RealmRoom realmRoom = getRealm().where(RealmRoom.class).equalTo(RealmRoomFields.CHAT_ROOM.PEER_ID, peerId).findFirst();

        if (realmRoom != null) {

            new GoToChatActivity(realmRoom.getId()).startActivity();

        } else {

            G.onChatGetRoom = new OnChatGetRoom() {
                @Override
                public void onChatGetRoom(ProtoGlobal.Room room) {

                    new GoToChatActivity(room.getId()).setPeerID(peerId).startActivity();

                    G.onChatGetRoom = null;
                }

                @Override
                public void onChatGetRoomTimeOut() {

                }

                @Override
                public void onChatGetRoomError(int majorCode, int minorCode) {

                }
            };

            new RequestChatGetRoom().chatGetRoom(peerId);
        }

    }

    private void getUserInfo() {

        userInfo = getRealm().where(RealmUserInfo.class).findFirst();
        if (userInfo != null) {
            String username = userInfo.getUserInfo().getDisplayName();
            phoneNumber = userInfo.getUserInfo().getPhoneNumber();
            
            mUserName.setText(username);
            mUserPhone.setText(phoneNumber);
            mUserBio.setText(userInfo.getUserInfo().getBio());
            
            //setPhoneInquiry(phoneNumber);

            if (HelperCalander.isPersianUnicode) {
                mUserPhone.setText(HelperCalander.convertToUnicodeFarsiNumber(mUserPhone.getText().toString()));
                mUserName.setText(HelperCalander.convertToUnicodeFarsiNumber(mUserName.getText().toString()));
            }
          
            setImage();
        }

        //set credit amount
        if (G.selectedCard != null) {
            mCreditAmountTxt.setVisibility(View.VISIBLE);
            mCreditAmountTxt.setText(/*"" + getResources().getString(R.string.wallet_Your_credit) + " " +*/ G.selectedCard.cashOutBalance + " " + getResources().getString(R.string.wallet_Reial));
        } else {
            mCreditAmountTxt.setVisibility(View.INVISIBLE);
        }

        mDarkModeSwitch.setChecked(G.isDarkTheme);

    }

    @Override
    public void setRefreshBalance() {
        try {
            getUserCredit();
        } catch (Exception e) {
        }
    }

    public void getUserCredit() {

        WebBase.apiKey = "5aa7e856ae7fbc00016ac5a01c65909797d94a16a279f46a4abb5faa";
        if (Auth.getCurrentAuth() != null) {
            Web.getInstance().getWebService().getCredit(Auth.getCurrentAuth().getId()).enqueue(new Callback<ArrayList<Card>>() {
                @Override
                public void onResponse(Call<ArrayList<Card>> call, Response<ArrayList<Card>> response) {
                    if (response.body() != null) {
                        retryConnectToWallet = 0;
                        if (response.body().size() > 0)
                            G.selectedCard = response.body().get(0);

                        G.cardamount = G.selectedCard.cashOutBalance;

                        if (G.selectedCard != null) {
                            if (mCreditAmountTxt != null) {
                                mCreditAmountTxt.setVisibility(View.VISIBLE);
                                mCreditAmountTxt.setText("" + getResources().getString(R.string.wallet_Your_credit) + " " + G.cardamount + " " + getResources().getString(R.string.wallet_Reial));
                            }

                        }
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<Card>> call, Throwable t) {

                    if (retryConnectToWallet < 3) {
                        Crashlytics.logException(new Exception(t.getMessage()));
                        getUserCredit();
                        retryConnectToWallet++;
                    }
                }
            });
        }
    }

    @Override
    public void receivedMessage(int id, Object... message) {

        switch (id) {
            case EventManager.ON_ACCESS_TOKEN_RECIVE:
                int response = (int) message[0];
                switch (response) {
                    case socketMessages.SUCCESS:
                        new android.os.Handler(getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                getUserCredit();
                                retryConnectToWallet = 0;
                            }
                        });

                        break;

                    case socketMessages.FAILED:
                        if (retryConnectToWallet < 3) {
                            new RequestWalletGetAccessToken().walletGetAccessToken();
                            retryConnectToWallet++;
                        }

                        break;
                }
                // backthread

        }
    }

    public void setImage() {
        HelperAvatar.getAvatar(G.userId, HelperAvatar.AvatarType.USER, true, new OnAvatarGet() {
            @Override
            public void onAvatarGet(final String avatarPath, long ownerId) {
                if (avatarPath != null) {
                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (G.userId != ownerId)
                                return;
                            G.imageLoader.displayImage(AndroidUtils.suitablePath(avatarPath), mAvatar);
                        }
                    });
                }
            }

            @Override
            public void onShowInitials(final String initials, final String color, final long ownerId) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (G.userId != ownerId)
                            return;
                        mAvatar.setImageBitmap(HelperImageBackColor.drawAlphabetOnPicture((int) mAvatar.getContext().getResources().getDimension(R.dimen.dp100), initials, color));
                    }
                });
            }
        });

        G.onChangeUserPhotoListener = new OnChangeUserPhotoListener() {
            @Override
            public void onChangePhoto(final String imagePath) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (imagePath == null || !new File(imagePath).exists()) {
                            //Realm realm1 = Realm.getDefaultInstance();
                            mAvatar.setImageBitmap(HelperImageBackColor.drawAlphabetOnPicture((int) mAvatar.getContext().getResources().getDimension(R.dimen.dp100), userInfo.getUserInfo().getInitials(), userInfo.getUserInfo().getColor()));
                            //realm1.close();
                        } else {
                            G.imageLoader.displayImage(AndroidUtils.suitablePath(imagePath), mAvatar);
                        }
                    }
                });
            }

            @Override
            public void onChangeInitials(final String initials, final String color) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        mAvatar.setImageBitmap(HelperImageBackColor.drawAlphabetOnPicture((int) mAvatar.getContext().getResources().getDimension(R.dimen.dp100), initials, color));
                    }
                });
            }
        };
    }

    private Realm getRealm() {
        if (mRealm == null || mRealm.isClosed()) {

            mRealm = Realm.getDefaultInstance();
        }

        return mRealm;
    }

    private void initViews() {
        
        mAvatar = mView.findViewById(R.id.fup_user_image);
        mUserName = mView.findViewById(R.id.fup_user_name);
        mUserPhone = mView.findViewById(R.id.fup_user_tel);
        mUserBio = mView.findViewById(R.id.fup_user_bio);
        
        mCloudBtn = mView.findViewById(R.id.fup_btn_cloud);
        mSettingsBtn = mView.findViewById(R.id.fup_btn_settings);
        mAddBtn = mView.findViewById(R.id.fup_btn_add);
        mCreditBtn = mView.findViewById(R.id.fup_btn_credit);
        mScoreBtn = mView.findViewById(R.id.fup_btn_score);
        
        mInviteFriendBtn = mView.findViewById(R.id.fup_btn_invite_friends);
        mDarkThemeBtn = mView.findViewById(R.id.fup_btn_dark_mode);
        mLogoutBtn = mView.findViewById(R.id.fup_btn_logout);
        mQrScannerBtn = mView.findViewById(R.id.fup_btn_qr_scanner);
        mNearByBtn = mView.findViewById(R.id.fup_btn_nearBy);
        mFaqBtn = mView.findViewById(R.id.fup_btn_faq);
        mLanguageBtn = mView.findViewById(R.id.fup_btn_language);

        mDarkModeSwitch = mView.findViewById(R.id.fup_switch_dark_mode);
        
        mLanguageTxt = mView.findViewById(R.id.fup_txt_selected_language);
        mCreditAmountTxt = mView.findViewById(R.id.fup_txt_credit_amount);
        mScoreTxt = mView.findViewById(R.id.fup_txt_score);
        mAppVersionTxt = mView.findViewById(R.id.fup_txt_app_version);
        
    }

    @Override
    public void onUserInfoMyClient() {
        setImage();
    }

    @Override
    public void onUserInfoTimeOut() {
        //empty
    }

    @Override
    public void onUserInfoError(int majorCode, int minorCode) {
        //empty
    }
}
