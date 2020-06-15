package net.iGap.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Build;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.StringRes;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityCall;
import net.iGap.activities.ActivityMain;
import net.iGap.activities.CallActivity;
import net.iGap.fragments.FragmentWalletAgrement;
import net.iGap.libs.bottomNavigation.Util.Utils;
import net.iGap.model.PassCode;
import net.iGap.module.CircleImageView;
import net.iGap.module.MusicPlayer;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.Theme;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.accountManager.DbManager;
import net.iGap.module.enums.ConnectionState;
import net.iGap.module.webrtc.CallService;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.realm.RealmUserInfo;
import net.iGap.viewmodel.controllers.CallManager;

import org.paygear.WalletActivity;

import static androidx.constraintlayout.widget.ConstraintSet.BOTTOM;
import static androidx.constraintlayout.widget.ConstraintSet.END;
import static androidx.constraintlayout.widget.ConstraintSet.LEFT;
import static androidx.constraintlayout.widget.ConstraintSet.MATCH_CONSTRAINT;
import static androidx.constraintlayout.widget.ConstraintSet.PARENT_ID;
import static androidx.constraintlayout.widget.ConstraintSet.RIGHT;
import static androidx.constraintlayout.widget.ConstraintSet.START;
import static androidx.constraintlayout.widget.ConstraintSet.TOP;
import static androidx.constraintlayout.widget.ConstraintSet.WRAP_CONTENT;
import static net.iGap.activities.ActivityMain.WALLET_REQUEST_CODE;
import static net.iGap.adapter.items.chat.ViewMaker.i_Dp;


/**
 * SAMPLE OF USAGE ARE AT BOTTOM OF THIS FILE
 * 1 = create root view at the xml (LinearLayout)
 * 2 = get create object of this class and set details then call getView()
 * 3 = attach view to xml root toolbar view
 * 4 = implement toolbar listener based on your usage
 */
public class HelperToolbar {

    private AppCompatTextView mLeftBtn, passCodeBtn, scannerBtn, mRightBtn, m2RightBtn, m3RightBtn, m4RightBtn;
    private TextView mTxtLogo, mTxtCounter, mTxtBigAvatarUserName, mTxtCallStatus, mTxtChatSeenStatus;
    private AppCompatTextView mTxtChatUserName;
    private CircleImageView mAvatarSmall, mAvatarBig, mAvatarChat, groupAvatar;
    private RelativeLayout mSearchBox;
    private TextView mTxtSearch;
    private AppCompatTextView groupName, groupMemberCount, profileStatus, profileTell;
    private FloatingActionButton profileFabChat;
    private EditText mEdtSearch;
    private TextView mChatVerifyIcon;
    private TextView mChatMuteIcon;
    private CircleImageView mCloudChatIcon;
    private TextView mBtnClearSearch;
    private View callLayout;
    private LinearLayout userNameLayout;

    private CircleImageView mTabletUserAvatar;
    private TextView mTabletUserName;
    private TextView mTabletUserPhone;

    private TextView mTabletSearchIcon;
    private TextView mTabletAddIcon;
    private TextView mTabletEditIcon;

    private Context mContext;
    private FragmentActivity mFragmentActivity;
    private ViewGroup mViewGroup = null;
    private ToolbarListener mToolbarListener;
    private LifecycleOwner lifecycleOwner;

    private int[] mLeftIcon = {0, 0};
    private int[] mRightIcons = {0, 0, 0, 0};
    private int[] mTabletIcons = {0, 0, 0};
    private boolean isAttachToRoot;
    private boolean isSearchBoxShown;
    private boolean isLogoShown;
    private boolean isRightSmallAvatarShown;
    private boolean isInChatRoom;
    private boolean isCallModeEnable;
    private boolean isGroupProfile;
    private boolean isMediaPlayerEnabled;
    private String defaultTitleText = null;
    private boolean isShowEditTextForSearch;
    private View rootView;
    private boolean isSharedMedia;
    private boolean isContactProfile;
    private boolean isBigSearchBox;
    private boolean isTabletMode;
    private boolean isShowConnectionState = true;
    public boolean isToolbarSearchAnimationInProccess;
    private boolean isScannerEnable;
    private boolean isPassCodeEnable;
    private int mPassCodeIcon;
    private int mScannerIcon;
    private int mAnimationOldPositionItem = 0;
    private boolean isRoundBackground = true;
    private boolean isCheckIGapLogo = true;
    private TextView mTxtCallTimer;
    private BroadcastReceiver callTimerReceiver;

    private HelperToolbar() {
    }

    public static HelperToolbar create() {
        return new HelperToolbar();
    }

    public HelperToolbar setContext(Context context) {
        this.mContext = context;
        return this;
    }

    public HelperToolbar setFragmentActivity(FragmentActivity activity) {
        this.mFragmentActivity = activity;
        return this;
    }

    public HelperToolbar setViewGroup(ViewGroup vg) {
        this.mViewGroup = vg;
        return this;
    }

    public HelperToolbar isAttachToRoot(boolean isAttach) {
        this.isAttachToRoot = isAttach;
        return this;
    }

    public HelperToolbar setRightIcons(@StringRes int... drawables) {
        System.arraycopy(drawables, 0, mRightIcons, 0, drawables.length);
        return this;
    }

    //icons used and set from right side : contain : add , edit , search
    public HelperToolbar setTabletIcons(@StringRes int... icons) {
        System.arraycopy(icons, 0, mTabletIcons, 0, icons.length);
        return this;
    }

    public HelperToolbar setLeftIcon(@StringRes int... icons) {
        System.arraycopy(icons, 0, mLeftIcon, 0, icons.length);
        if (mLeftBtn != null) mLeftBtn.setText(mLeftIcon[0]);
        return this;
    }

    public HelperToolbar setSearchBoxShown(boolean searchBoxShown, boolean isShowEditTextForSearch) {
        this.isSearchBoxShown = searchBoxShown;
        this.isShowEditTextForSearch = isShowEditTextForSearch;
        return this;
    }

    public HelperToolbar setSearchBoxShown(boolean searchBoxShown) {
        this.isSearchBoxShown = searchBoxShown;
        this.isShowEditTextForSearch = true;
        return this;
    }

    public HelperToolbar setSearchBoxShown(boolean searchBoxShown, boolean isShowEditTextForSearch, boolean isBigSearchBox) {
        this.isSearchBoxShown = searchBoxShown;
        this.isShowEditTextForSearch = isShowEditTextForSearch;
        this.isBigSearchBox = isBigSearchBox;
        return this;
    }

    public HelperToolbar setLogoShown(boolean logoShown) {
        this.isLogoShown = logoShown;
        return this;
    }

    public HelperToolbar setIGapLogoCheck(boolean isCheck) {
        this.isCheckIGapLogo = isCheck;
        return this;
    }

    public HelperToolbar setShowConnectionState(boolean enable) {
        this.isShowConnectionState = enable;
        return this;
    }

    public HelperToolbar setRoundBackground(boolean isRound) {
        this.isRoundBackground = isRound;
        return this;
    }

    public HelperToolbar setPlayerEnable(boolean isEnable) {
        this.isMediaPlayerEnabled = isEnable;
        return this;
    }

    public HelperToolbar setScannerVisibility(boolean isVisible, int icon) {
        this.isScannerEnable = isVisible;
        this.mScannerIcon = icon;
        return this;
    }

    public HelperToolbar setPassCodeVisibility(boolean isVisible, int icon) {
        this.isPassCodeEnable = isVisible;
        this.mPassCodeIcon = icon;
        if (passCodeBtn != null) passCodeBtn.setText(icon);

        return this;
    }

    public HelperToolbar setChatRoom(boolean isChatRoom) {
        this.isInChatRoom = isChatRoom;
        return this;
    }

    public HelperToolbar setIsSharedMedia(boolean isSharedMedia) {

        this.isSharedMedia = isSharedMedia;
        return this;
    }

    public HelperToolbar setRightSmallAvatarShown(boolean rightSmallAvatarShown) {
        this.isRightSmallAvatarShown = rightSmallAvatarShown;

        if (mAvatarSmall != null) {
            mAvatarSmall.setVisibility(View.VISIBLE);
            mAvatarSmall.setOnClickListener(v -> mToolbarListener.onSmallAvatarClickListener(v));
        }
        return this;
    }

    public HelperToolbar setDefaultTitle(String title) {

        this.defaultTitleText = title;

        if (mTxtLogo != null) {
            mTxtLogo.setText(title);
            checkIGapFont();
        }

        return this;
    }

    public HelperToolbar setListener(ToolbarListener listener) {
        this.mToolbarListener = listener;
        return this;
    }

    public HelperToolbar setGroupProfile(boolean groupProfile) {
        this.isGroupProfile = groupProfile;
        return this;
    }

    public HelperToolbar setContactProfile(boolean contactProfile) {
        this.isContactProfile = contactProfile;
        return this;
    }

    //just use in landscape tablet mode and listeners are like main fragments listener
    //add : onLeft , edit : onRight , search : onSearchClickListener
    //use this way for set same action to landscape tablet mode and portrait mobile mode
    public HelperToolbar setTabletMode(boolean isTabletMode) {
        this.isTabletMode = isTabletMode;
        return this;
    }

    public View getView() {

        if (mContext == null) throw new IllegalArgumentException("Context can not be null");

        //set default title name if user not set
        if (defaultTitleText == null || defaultTitleText.trim().equals("")) {
            defaultTitleText = mContext.getString(R.string.app_name);
        }

        typeFaceGenerator();

        ViewMaker viewMaker = new ViewMaker(mContext);
        rootView = viewMaker;
        rootView.setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        initViews(viewMaker);


        if (mLeftIcon[0] != 0) {
            mLeftBtn.setOnClickListener(v -> mToolbarListener.onLeftIconClickListener(v));
        }

        if (mRightIcons[0] != 0) {
            mRightBtn.setOnClickListener(v -> mToolbarListener.onRightIconClickListener(v));
        }

        if (mRightIcons[1] != 0) {
            m2RightBtn.setOnClickListener(v -> mToolbarListener.onSecondRightIconClickListener(v));
        }

        if (mRightIcons[2] != 0) {
            m3RightBtn.setOnClickListener(v -> mToolbarListener.onThirdRightIconClickListener(v));
        }

        if (mRightIcons[3] != 0) {
            m4RightBtn.setOnClickListener(v -> mToolbarListener.onFourthRightIconClickListener(v));
        }

        if (isInChatRoom) {
            rootView.setOnClickListener(v -> mToolbarListener.onChatAvatarClickListener(v));
        }

        if (mSearchBox != null) {
            setSearchBoxVisibility(isSearchBoxShown);
        }

        if (isMediaPlayerEnabled) {
            setMusicPlayer(viewMaker, isInChatRoom);
        }

        if (isRightSmallAvatarShown) {
            mAvatarSmall.setOnClickListener(v -> mToolbarListener.onSmallAvatarClickListener(v));
        }

        if (isPassCodeEnable) {
            checkPassCodeVisibility();
            passCodeBtn.setOnClickListener(v -> onPassCodeButtonClickListener());
        }

        if (isScannerEnable) {
            scannerBtn.setOnClickListener(v -> onScannerClickListener());
        }

        if (isTabletMode) {
            mTabletSearchIcon = viewMaker.gettIconSearch();
            mTabletAddIcon = viewMaker.gettIconAdd();
            mTabletEditIcon = viewMaker.gettIconEdit();
            mTabletEditIcon.setOnClickListener(v -> mToolbarListener.onLeftIconClickListener(v));
            mTabletAddIcon.setOnClickListener(v -> mToolbarListener.onRightIconClickListener(v));
            mTabletSearchIcon.setOnClickListener(v -> mToolbarListener.onSearchClickListener(v));
        }

        if (mTxtLogo != null) {
            mTxtLogo.setOnClickListener(v -> mToolbarListener.onToolbarTitleClickListener(v));
            toolBarTitleHandler();
            checkIGapFont();
        }

        return rootView;

    }

    public void changeDefaultTitle(String title) {
        this.defaultTitleText = title;
    }

    //offset must be negative or zero
    public void animateSearchBox(boolean isGone, int lastItemPosition, int offset) {

        if (mSearchBox == null) return;

        if (lastItemPosition == 0) {

            mAnimationOldPositionItem = 0;
            mSearchBox.setVisibility(View.VISIBLE);
            mSearchBox.clearAnimation();

        } else if ((lastItemPosition - mAnimationOldPositionItem) > Math.abs(offset)) {

            mAnimationOldPositionItem = lastItemPosition;
            if (!mSearchBox.isShown() && isGone) return;
            if (isToolbarSearchAnimationInProccess) return;
            setAnimation(isGone);

        } else if ((lastItemPosition - mAnimationOldPositionItem) < offset) {

            mAnimationOldPositionItem = lastItemPosition;
            if (mSearchBox.isShown() && !isGone) return;
            if (isToolbarSearchAnimationInProccess) return;
            setAnimation(isGone);

        }

    }

    public void resizeSearchBoxWithAnimation(final boolean bigView, final boolean isOpenKeyboard) {


        if (!isOpenKeyboard) {
            setSearchEditableMode(false);
        }

        Animation animation;

        if (bigView) {

            animation = new ScaleAnimation(
                    1f, 1.09f,
                    1f, 1.01f,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f
            );

        } else {

            animation = new ScaleAnimation(
                    1.09f, 1f,
                    1.01f, 1f,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f
            );

        }

        animation.setDuration(400);
        animation.setFillAfter(true);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                if (isOpenKeyboard) {
                    setSearchEditableMode(true);

                    G.handler.postDelayed(() -> {
                        openKeyboard();
                    }, 200);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        mSearchBox.startAnimation(animation);
    }

    public void unRegisterTimerBroadcast() {
        if (mContext == null || callTimerReceiver == null) return;
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(callTimerReceiver);
    }

    public void registerTimerBroadcast() {
        if (mContext == null || callTimerReceiver == null) {
            return;
        }
        IntentFilter intentFilter = new IntentFilter(CallActivity.CALL_TIMER_BROADCAST);
        LocalBroadcastManager.getInstance(mContext).registerReceiver(callTimerReceiver, intentFilter);
    }

    private void openKeyboard() {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mEdtSearch, InputMethodManager.SHOW_IMPLICIT);
    }

    private void closeKeyboard() {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEdtSearch.getWindowToken(), 0);

    }

    private void setAnimation(boolean isGone) {

        if (!isGone) mSearchBox.setVisibility(View.VISIBLE);

        Animation animation;

        if (isGone) {
            animation = new ScaleAnimation(
                    1f, 0f,
                    1f, 0f,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f
            );
        } else {
            animation = new ScaleAnimation(
                    0f, 1f,
                    0f, 1f,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f
            );
        }

        animation.setDuration(200);
        animation.setFillAfter(false);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                isToolbarSearchAnimationInProccess = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isToolbarSearchAnimationInProccess = false;

                if (isGone)
                    mSearchBox.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        mSearchBox.startAnimation(animation);

    }

    public TextView getTextViewChatUserName() {
        return mTxtChatUserName;
    }

    public TextView getTextViewChatSeenStatus() {
        return mTxtChatSeenStatus;
    }

    public TextView getTextViewSearch() {
        return mTxtSearch;
    }

    public EditText getEditTextSearch() {
        return mEdtSearch;
    }

    public RelativeLayout getmSearchBox() {
        return mSearchBox;
    }

    public TextView getmBtnClearSearch() {
        return mBtnClearSearch;
    }

    public View getRootView() {
        return rootView;
    }

    public TextView getButtonClearSearch() {
        return mBtnClearSearch;
    }

    public View getCallLayout() {
        return callLayout;
    }

    public TextView getTextViewLogo() {
        return mTxtLogo;
    }

    public AppCompatTextView getLeftButton() {
        return mLeftBtn;
    }

    public AppCompatTextView getPassCodeButton() {
        return passCodeBtn;
    }

    public AppCompatTextView getScannerButton() {
        return scannerBtn;
    }

    public AppCompatTextView getRightButton() {
        return mRightBtn;
    }

    public AppCompatTextView getSecondRightButton() {
        return m2RightBtn;
    }

    public AppCompatTextView getThirdRightButton() {
        return m3RightBtn;
    }

    public AppCompatTextView getFourthRightButton() {
        return m4RightBtn;
    }

    public CircleImageView getUserAvatarChat() {
        return mAvatarChat;
    }

    public CircleImageView getCloudChatIcon() {
        return mCloudChatIcon;
    }

    public AppCompatTextView getGroupName() {
        return groupName;
    }

    public AppCompatTextView getProfileStatus() {
        return profileStatus;
    }

    public AppCompatTextView getProfileTell() {
        return profileTell;
    }

    public AppCompatTextView getGroupMemberCount() {
        return groupMemberCount;
    }

    public FloatingActionButton getProfileFabChat() {
        return profileFabChat;
    }

    public CircleImageView getGroupAvatar() {
        return groupAvatar;
    }

    public TextView getChatVerify() {
        return mChatVerifyIcon;
    }

    public TextView getChatMute() {
        return mChatMuteIcon;
    }

    public ImageView getAvatarSmall() {
        return mAvatarSmall;
    }

    public LinearLayout getUserNameLayout() {
        return userNameLayout;
    }

    public CircleImageView getTabletUserAvatar() {
        return mTabletUserAvatar;
    }

    public TextView getTabletUserName() {
        return mTabletUserName;
    }

    public TextView getTabletUserPhone() {
        return mTabletUserPhone;
    }

    public TextView getTabletSearchIcon() {
        return mTabletSearchIcon;
    }

    public TextView getTabletAddIcon() {
        return mTabletAddIcon;
    }

    public TextView getTabletEditIcon() {
        return mTabletEditIcon;
    }

    public HelperToolbar setLifecycleOwner(LifecycleOwner lifecycleOwner) {
        this.lifecycleOwner = lifecycleOwner;
        return this;
    }

    public void checkPassCodeVisibility() {
        if (passCodeBtn != null) {
            if (PassCode.getInstance().isPassCode()) {
                passCodeBtn.setVisibility(View.VISIBLE);
                ActivityMain.isLock = HelperPreferences.getInstance().readBoolean(SHP_SETTING.FILE_NAME, SHP_SETTING.KEY_LOCK_STARTUP_STATE);

                if (ActivityMain.isLock) {
                    passCodeBtn.setText(mContext.getString(R.string.lock_icon));
                } else {
                    passCodeBtn.setText(mContext.getString(R.string.unlock_icon));
                }
            } else {
                passCodeBtn.setVisibility(View.GONE);
            }
        }
    }

    /*************************************************************/

    private void setMusicPlayer(ViewMaker view, boolean isChat) {

        LinearLayout musicLayout = (LinearLayout) view.getMusicLayout();

        LinearLayout stripCallLayout = (LinearLayout) view.getCallLayout();

        callTimerReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction() == null || intent.getExtras() == null) return;
                if (intent.getAction().equals(CallActivity.CALL_TIMER_BROADCAST)) {
                    String time = intent.getExtras().getString(CallActivity.TIMER_TEXT, "");
                    mTxtCallTimer.setText(time);
                }
            }
        };

        if (!isSearchBoxShown) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) musicLayout.getLayoutParams();
            params.setMargins(0, (int) mContext.getResources().getDimension(R.dimen.dp14), 0, 0);
            musicLayout.setLayoutParams(params);

            LinearLayout.LayoutParams paramsCall = (LinearLayout.LayoutParams) stripCallLayout.getLayoutParams();
            paramsCall.setMargins(0, (int) mContext.getResources().getDimension(R.dimen.dp14), 0, 0);
            stripCallLayout.setLayoutParams(paramsCall);
        }

        if (isChat) {
            MusicPlayer.chatLayout = musicLayout;
            ActivityCall.stripLayoutChat = view.getCallLayout();

            TextView txtCallActivityBack = rootView.findViewById(R.id.cslcs_btn_call_strip);
            txtCallActivityBack.setOnClickListener(v -> {
                if (CallService.getInstance() != null) {
                    mContext.startActivity(new Intent(G.fragmentActivity, CallActivity.class));
                }
            });
//            txtCallActivityBack.setOnClickListener(v -> mContext.startActivity(new Intent(G.fragmentActivity, ActivityCall.class)));
            checkIsAvailableOnGoingCall();

        } else if (isSharedMedia) {

            MusicPlayer.shearedMediaLayout = musicLayout;

        } else {
            MusicPlayer.mainLayout = musicLayout;
            ActivityCall.stripLayoutMain = view.getCallLayout();

            TextView txtCallActivityBack = rootView.findViewById(R.id.cslcs_btn_call_strip);
            txtCallActivityBack.setOnClickListener(v -> {
                if (CallService.getInstance() != null) {
                    mContext.startActivity(new Intent(G.fragmentActivity, CallActivity.class));
                }
            });
//            txtCallActivityBack.setOnClickListener(v -> mContext.startActivity(new Intent(G.fragmentActivity, ActivityCall.class)));
        }

        MusicPlayer.setMusicPlayer(musicLayout);
        setMediaLayout();
    }

    public void checkIsAvailableOnGoingCall() {
        callLayout.setVisibility(CallManager.getInstance().isCallAlive() ? View.VISIBLE : View.GONE);
    }

    private void setMediaLayout() {
        try {
            if (MusicPlayer.mp != null) {

                if (MusicPlayer.shearedMediaLayout != null) {
                    MusicPlayer.initLayoutTripMusic(MusicPlayer.shearedMediaLayout);

                    if (MusicPlayer.chatLayout != null) {
                        MusicPlayer.chatLayout.setVisibility(View.GONE);
                    }

                    if (MusicPlayer.mainLayout != null) {
                        MusicPlayer.mainLayout.setVisibility(View.GONE);
                    }
                } else if (MusicPlayer.chatLayout != null) {
                    MusicPlayer.initLayoutTripMusic(MusicPlayer.chatLayout);

                    if (MusicPlayer.mainLayout != null) {
                        MusicPlayer.mainLayout.setVisibility(View.GONE);
                    }
                } else if (MusicPlayer.mainLayout != null) {
                    MusicPlayer.initLayoutTripMusic(MusicPlayer.mainLayout);
                }
            } else {

                if (MusicPlayer.mainLayout != null) {
                    MusicPlayer.mainLayout.setVisibility(View.GONE);
                }

                if (MusicPlayer.chatLayout != null) {
                    MusicPlayer.chatLayout.setVisibility(View.GONE);
                }

                if (MusicPlayer.shearedMediaLayout != null) {
                    MusicPlayer.shearedMediaLayout.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            HelperLog.getInstance().setErrorLog(e);
        }
    }

    private void setStripLayoutCall() {
        if (G.isInCall) {
            if (ActivityCall.stripLayoutChat != null) {
                ActivityCall.stripLayoutChat.setVisibility(View.VISIBLE);

                if (ActivityCall.stripLayoutMain != null) {
                    ActivityCall.stripLayoutMain.setVisibility(View.GONE);
                }
            } else {
                if (ActivityCall.stripLayoutMain != null) {
                    ActivityCall.stripLayoutMain.setVisibility(View.VISIBLE);
                }
            }
        } else {

            if (ActivityCall.stripLayoutMain != null) {
                ActivityCall.stripLayoutMain.setVisibility(View.GONE);
            }

            if (ActivityCall.stripLayoutChat != null) {
                ActivityCall.stripLayoutChat.setVisibility(View.GONE);
            }
        }
    }

    public void checkIGapFont() {

        if (mTxtLogo == null)
            return;

        if (mTxtLogo.getText().toString().equals(mContext.getString(R.string.waiting_for_network))) {
            Utils.setTextSize(mTxtLogo, R.dimen.standardTextSize);

        } else if (mTxtLogo.getText().toString().equals(mContext.getString(R.string.connecting))) {
            Utils.setTextSize(mTxtLogo, R.dimen.largeTextSize);

        } else if (mTxtLogo.getText().toString().equals(mContext.getString(R.string.updating))) {
            Utils.setTextSize(mTxtLogo, R.dimen.largeTextSize);

        } else {
            Utils.setTextSize(mTxtLogo, R.dimen.largeTextSize);
            mTxtLogo.setText(defaultTitleText);
        }

        if (isCheckIGapLogo) {

            if (mTxtLogo.getText().toString().toLowerCase().equals("igap")) {
                Utils.setTextSize(mTxtLogo, R.dimen.toolbar_igap_icon_textSize);
                mTxtLogo.setTypeface(tfFontIcon);
                mTxtLogo.setText(mContext.getString(R.string.igap_en_icon));
            } else if (mTxtLogo.getText().toString().toLowerCase().equals("آیگپ") || mTxtLogo.getText().toString().equals("آیکب")) {
                Utils.setTextSize(mTxtLogo, R.dimen.toolbar_igap_icon_textSize);
                mTxtLogo.setTypeface(tfFontIcon);
                mTxtLogo.setText(mContext.getString(R.string.igap_fa_icon));
            } else {
                mTxtLogo.setTypeface(tfMain);
            }

        } else {
            mTxtLogo.setTypeface(tfMain);
        }

        mTxtLogo.requestLayout();
    }

    private void toolBarTitleHandler() {

        if (!isShowConnectionState) return;

        try {
            if (lifecycleOwner != null)
                connectionStateChecker(lifecycleOwner);
            else
                connectionStateChecker(G.fragmentActivity);
        } catch (Exception e) {
            try {
                if (lifecycleOwner != null)
                    connectionStateChecker(lifecycleOwner);
                else
                    connectionStateChecker(G.currentActivity);
            } catch (Exception e2) {

            }
        }

    }

    private void connectionStateChecker(LifecycleOwner owner) {

        //check first time state then for every changes observer will change title
        if (G.connectionState != null) {
            if (G.connectionState == ConnectionState.CONNECTING) {
                mTxtLogo.setText(R.string.connecting);
                checkIGapFont();
            } else if (G.connectionState == ConnectionState.WAITING_FOR_NETWORK) {
                mTxtLogo.setText(R.string.waiting_for_network);
                checkIGapFont();
            }
        }

        G.connectionStateMutableLiveData.observe(owner, connectionState -> {

            if (mTxtLogo != null) {

                if (connectionState != null) {
                    if (connectionState == ConnectionState.WAITING_FOR_NETWORK) {
                        mTxtLogo.setText(R.string.waiting_for_network);
                    } else if (connectionState == ConnectionState.CONNECTING) {
                        mTxtLogo.setText(R.string.connecting);
                    } else if (connectionState == ConnectionState.UPDATING) {
                        mTxtLogo.setText(R.string.updating);
                    } else if (connectionState == ConnectionState.IGAP) {
                        mTxtLogo.setText(defaultTitleText);
                    } else {
                        mTxtLogo.setText(defaultTitleText);
                    }
                } else {
                    mTxtLogo.setText(defaultTitleText);
                }

                checkIGapFont();
            }

        });
    }

    private void setIGapLogoVisibility(boolean visible) {

        if (visible)
            mTxtLogo.setVisibility(View.VISIBLE);
        else
            mTxtLogo.setVisibility(View.GONE);

    }

    private void setSearchBoxVisibility(boolean visible) {

        if (visible && mSearchBox != null) {

            mSearchBox.setOnClickListener(v -> {
                if (isShowEditTextForSearch) {
                    if (!mBtnClearSearch.isShown()) resizeSearchBoxWithAnimation(true, true);
                    //setSearchEditableMode(mTxtSearch.isShown());
                }
                mToolbarListener.onSearchClickListener(v);
            });

            mBtnClearSearch.setOnClickListener(v -> {

                if (!mEdtSearch.getText().toString().trim().equals(""))
                    mEdtSearch.setText("");
                else if (isShowEditTextForSearch) {
                    closeKeyboard();
                    G.handler.postDelayed(() -> {
                        if (mToolbarListener != null)
                            mToolbarListener.onSearchBoxClosed();
                        resizeSearchBoxWithAnimation(false, false);
                    }, 200);
                }
                G.handler.postDelayed(() -> {
                    mToolbarListener.onBtnClearSearchClickListener(v);
                }, 500);
            });

            mEdtSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    mToolbarListener.onSearchTextChangeListener(mEdtSearch, s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            mEdtSearch.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) closeKeyboard();
                return true;
            });
        }
    }

    public void setSearchEditableMode(boolean state) {

        if (state) {

            mTxtSearch.setVisibility(View.GONE);
            mEdtSearch.setVisibility(View.VISIBLE);
            mBtnClearSearch.setVisibility(View.VISIBLE);
            mEdtSearch.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
            mEdtSearch.requestFocus();

        } else {

            //mEdtSearch.setText("");
            mBtnClearSearch.setVisibility(View.GONE);
            mEdtSearch.setVisibility(View.GONE);
            mTxtSearch.setVisibility(View.VISIBLE);

        }

    }

    private void onPassCodeButtonClickListener() {

        if (ActivityMain.isLock) {
            passCodeBtn.setText(mContext.getResources().getString(R.string.unlock_icon));
            ActivityMain.isLock = false;
            HelperPreferences.getInstance().putBoolean(SHP_SETTING.FILE_NAME, SHP_SETTING.KEY_LOCK_STARTUP_STATE, false);

        } else {
            passCodeBtn.setText(mContext.getResources().getString(R.string.lock_icon));
            ActivityMain.isLock = true;
            HelperPreferences.getInstance().putBoolean(SHP_SETTING.FILE_NAME, SHP_SETTING.KEY_LOCK_STARTUP_STATE, true);
        }

        //main fragment onResume not called cause of usage algorithm , we get min activity and update button
        if (mFragmentActivity instanceof ActivityMain) {
            ((ActivityMain) mFragmentActivity).updatePassCodeState();
        }

    }

    private void onScannerClickListener() {
        DbManager.getInstance().doRealmTask(realm -> {
            String phoneNumber = "";
            RealmUserInfo userInfo = realm.where(RealmUserInfo.class).findFirst();
            try {
                if (userInfo != null) {
                    phoneNumber = userInfo.getUserInfo().getPhoneNumber().substring(2);
                } else {
                    phoneNumber = AccountManager.getInstance().getCurrentUser().getPhoneNumber().substring(2);
                }
            } catch (Exception e) {
                //maybe exception was for realm substring
                try {
                    phoneNumber = AccountManager.getInstance().getCurrentUser().getPhoneNumber().substring(2);
                } catch (Exception ex) {
                    //nothing
                }
            }

            if (userInfo == null || !userInfo.isWalletRegister()) {
                new HelperFragment(mFragmentActivity.getSupportFragmentManager(), FragmentWalletAgrement.newInstance(phoneNumber)).load();
            } else {
                mFragmentActivity.startActivityForResult(new HelperWallet().goToWallet(mContext, new Intent(mFragmentActivity, WalletActivity.class), "0" + phoneNumber, true), WALLET_REQUEST_CODE);
            }

        });
    }

    private void initViews(ViewMaker view) {

        mLeftBtn = view.getLeftIcon();
        passCodeBtn = view.getpassCodeIcon();
        scannerBtn = view.getScannerIcon();
        mRightBtn = view.getRightIcon();
        m2RightBtn = view.getRightIcon2();
        m3RightBtn = view.getRightIcon3();
        m4RightBtn = view.getRightIcon4();

        mTxtChatUserName = view.getTvChatName();
        mChatVerifyIcon = view.getIconChatVerify();
        mChatMuteIcon = view.getMuteChatIcon();
        mTxtChatSeenStatus = view.getTvChatStatus();
        mTxtLogo = view.getLogo();
        mCloudChatIcon = view.getCivCloud();
        mAvatarChat = view.getCivAvatar();
        userNameLayout = view.getLayoutChatName();

        mAvatarSmall = view.getSmallAvatar();

        mSearchBox = view.getSearchLayout();
        mTxtSearch = view.getTvSearch();
        mEdtSearch = view.getEdtSearch();
        mBtnClearSearch = view.getTvClearSearch();
        callLayout = view.getCallLayout();

        groupAvatar = view.getCivProfileAvatar();
        groupName = view.getTvProfileName();
        groupMemberCount = view.getTvProfileMemberCount();
        profileStatus = view.getTvProfileStatus();
        profileTell = view.getTvProfileTell();
        profileFabChat = view.getFabChat();

        mTabletUserAvatar = view.gettUserAvatar();
        mTabletUserName = view.gettUserName();
        mTabletUserPhone = view.gettUserPhone();

        mTxtCallTimer = rootView.findViewById(R.id.cslcs_txt_timer);

        if (mTxtLogo != null)
            mTxtLogo.setText(defaultTitleText);
    }

    /**
     * samples of using this helper based on new design 1398/2/10
     * 1 = create root view at the xml (LinearLayout)
     * 2 = get create object of this class and set details then call getView()
     * 3 = attach view to xml root toolbar view
     * 4 = implement toolbar listener based on your usage
     */
    private void sampleOfUsage() {

/*
        mToolbarLayout = findViewById(R.id.main_activity_toolbar);

        //chat view when user select items in chat room
        View toolbarView = new HelperToolbar.create()
                .setContext(this)
                .setLeftIcon(R.drawable.close)
                .setRightIcons(R.drawable.add, R.drawable.back, R.drawable.close, R.drawable.edit)
                .setLogoShown(false)
                .setCounterShown(true)
                .getView();


        //big avatar
        View toolbarView = HelperToolbar.create()
                .setContext(this)
                .setLeftIcon(R.drawable.edit)
                .setLogoShown(true)
                .setBigCenterAvatarShown(true)
                .getView();

        //chat room normal view with user name , seen status , avatar
        View toolbarView = HelperToolbar.create()
                .setContext(this)
                .setLeftIcon(R.drawable.back)
                .setRightIcons(R.drawable.add, R.drawable.close, R.drawable.edit)
                .setChatRoom(true)
                .getView();

        //discovery toolbar
        View toolbarView = HelperToolbar.create()
                .setContext(this)
                .setLeftIcon(R.drawable.edit)
                .setLogoShown(true)
                .setSearchBoxShown(true)
                .setRightSmallAvatarShown(true)
                .getView();


        //main toolbar
        View toolbarView = HelperToolbar.create()
                .setContext(this)
                .setLeftIcon(R.drawable.edit)
                .setRightIcons(R.drawable.add)
                .setLogoShown(true)
                .setSearchBoxShown(true)
                .setListener(this)
                .getView();

        //call or video call mode
        View toolbarView = HelperToolbar.create()
                .setContext(this)
                .setLogoShown(true)
                .setCallEnable(true)
                .getView();

        mToolbarLayout.addView(toolbarView);


        //tablet mode
        HelperToolbar t = HelperToolbar.create()
                .setContext(getContext())
                .setTabletIcons(R.string.add_icon , R.string.edit_icon , R.string.search_icon)
                .setTabletMode(true)
                .setListener(this);

        */
    }

    private void typeFaceGenerator() {

        if (tfFontIcon == null)
            tfFontIcon = ResourcesCompat.getFont(mContext, R.font.font_icon);

        if (tfMain == null)
            tfMain = ResourcesCompat.getFont(mContext, R.font.main_font);


    }


    private Typeface tfFontIcon, tfMain;

    private class ViewMaker extends ConstraintLayout {

        private int VALUE_1DP;
        private int VALUE_4DP;
        private int VALUE_10DP;
        private boolean isDark;

        private View musicLayout;
        private View callLayout;
        private LinearLayout layoutMedia;
        private ConstraintLayout mainConstraint;
        private AppCompatTextView leftIcon = null;
        private AppCompatTextView passCodeIcon = null;
        private AppCompatTextView rightIcon4 = null;
        private AppCompatTextView rightIcon3 = null;
        private AppCompatTextView rightIcon2 = null;
        private AppCompatTextView rightIcon = null;
        private TextView logo;
        private RelativeLayout searchLayout;
        private TextView tvSearch;
        private TextView tvClearSearch;
        private EditText edtSearch;
        private RelativeLayout rlChatAvatar;
        private CircleImageView civAvatar;
        private CircleImageView civCloud;
        private LinearLayout layoutChatName;
        private AppCompatTextView tvChatName;
        private TextView tvChatStatus;
        private TextView iconChatVerify;
        private TextView muteChatIcon;
        private CircleImageView civProfileAvatar = null;
        private AppCompatTextView tvProfileName;
        private AppCompatTextView tvProfileMemberCount;
        private AppCompatTextView tvProfileTell;
        private AppCompatTextView tvProfileStatus;
        private FloatingActionButton fabChat;
        private CircleImageView tUserAvatar;
        private TextView tUserName;
        private TextView tUserPhone;
        private AppCompatTextView tIconAdd;
        private AppCompatTextView tIconEdit;
        private AppCompatTextView tIconSearch;
        private AppCompatTextView scannerIcon;
        private CircleImageView smallAvatar;

        public ViewMaker(Context ctx) {
            super(ctx);
            setupDefaults();
            init();
        }

        private void init() {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                setLayoutDirection(LAYOUT_DIRECTION_LTR);
            }

            if (!isTabletMode) {

                ConstraintSet setRoot = new ConstraintSet();
                ConstraintSet set = new ConstraintSet();


                //region media player and ongoing call
                //check and add media player cause of ui and this must be the below of main toolbar view
                if (isMediaPlayerEnabled) {

                    LayoutInflater inflater = LayoutInflater.from(getContext());

                    //music player layout
                    musicLayout = inflater.inflate(R.layout.music_layout_small, this, false);
                    musicLayout.setId(R.id.view_toolbar_layout_player_music);
                    setLayoutParams(musicLayout, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, i_Dp(R.dimen.toolbar_search_box_size));
                    musicLayout.setVisibility(VISIBLE);

                    //online call view
                    callLayout = inflater.inflate(R.layout.chat_sub_layout_strip_call, this, false);
                    callLayout.setId(R.id.view_toolbar_layout_strip_call);
                    setLayoutParams(callLayout, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, i_Dp(R.dimen.toolbar_search_box_size));
                    callLayout.setVisibility(GONE);

                    //player root view for add , handle two above views
                    layoutMedia = new LinearLayout(getContext());
                    layoutMedia.setId(R.id.view_toolbar_player_layout);
                    layoutMedia.setOrientation(LinearLayout.VERTICAL);
                    addView(layoutMedia);

                    if (isDark) {
                        layoutMedia.setBackgroundResource(R.drawable.shape_toolbar_player_dark);
                    } else {
                        layoutMedia.setBackgroundResource(R.drawable.shape_toolbar_player);
                    }

                    layoutMedia.addView(musicLayout);
                    layoutMedia.addView(callLayout);

                    setRoot.constrainHeight(layoutMedia.getId(), WRAP_CONTENT);
                    setRoot.constrainWidth(layoutMedia.getId(), MATCH_CONSTRAINT);

                    setRoot.connect(layoutMedia.getId(), START, PARENT_ID, START);
                    setRoot.connect(layoutMedia.getId(), END, PARENT_ID, END);
                    setRoot.connect(layoutMedia.getId(), TOP, PARENT_ID, TOP, i_Dp(R.dimen.margin_for_below_layouts_of_toolbar));
                }

                //endregion media player and ongoing call

                //region main constraint
                //contain : buttons , logo
                mainConstraint = new ConstraintLayout(getContext());
                mainConstraint.setId(R.id.view_toolbar_main_constraint);

                if (isRoundBackground) {
                    mainConstraint.setBackgroundResource(new Theme().getToolbarDrawable(mContext));
                } else {
                    mainConstraint.setBackgroundResource(new Theme().getToolbarDrawableSharpe(mContext));
                }
                setRoot.constrainHeight(mainConstraint.getId(), i_Dp(R.dimen.toolbar_height));
                setRoot.constrainWidth(mainConstraint.getId(), MATCH_CONSTRAINT);
                setRoot.connect(mainConstraint.getId(), START, PARENT_ID, START);
                setRoot.connect(mainConstraint.getId(), END, PARENT_ID, END);
                setRoot.connect(mainConstraint.getId(), TOP, PARENT_ID, TOP);
                addView(mainConstraint);

                //endregion main constraint

                //region logo
                if (isLogoShown) {

                    logo = new TextView(getContext());

                    logo.setId(R.id.view_toolbar_logo);
                    logo.setText(R.string.app_name);
                    logo.setGravity(Gravity.CENTER);
                    Utils.setTextSize(logo, R.dimen.standardTextSize);
                    logo.setTextColor(getContext().getResources().getColor(R.color.white));
                    logo.setTypeface(tfFontIcon);
                    logo.setPadding(0, 0, 0, i_Dp(R.dimen.dp4));
                    InputFilter[] fArray = new InputFilter[1];
                    fArray[0] = new InputFilter.LengthFilter(31);
                    logo.setFilters(fArray);
                    logo.setSingleLine(true);

                    mainConstraint.addView(logo);

                    set.constrainHeight(logo.getId(), WRAP_CONTENT);
                    set.constrainWidth(logo.getId(), MATCH_CONSTRAINT);
                    set.connect(logo.getId(), START, PARENT_ID, START);
                    set.connect(logo.getId(), END, PARENT_ID, END);
                    set.connect(logo.getId(), TOP, PARENT_ID, TOP);
                    set.connect(logo.getId(), BOTTOM, PARENT_ID, BOTTOM);

                }
                //endregion logo

                //region small avatar
                if (isRightSmallAvatarShown) {
                    smallAvatar = new CircleImageView(getContext());
                    smallAvatar.setId(R.id.view_toolbar_user_small_avatar);
                    smallAvatar.setPadding(VALUE_4DP, VALUE_4DP, VALUE_4DP, VALUE_4DP);
                    mainConstraint.addView(smallAvatar);

                    set.constrainWidth(smallAvatar.getId(), i_Dp(R.dimen.toolbar_icon_size));
                    set.constrainHeight(smallAvatar.getId(), i_Dp(R.dimen.toolbar_icon_size));
                    set.connect(smallAvatar.getId(), END, PARENT_ID, END, VALUE_4DP);
                    set.connect(smallAvatar.getId(), BOTTOM, PARENT_ID, BOTTOM, VALUE_10DP);
                    set.connect(smallAvatar.getId(), TOP, PARENT_ID, TOP, VALUE_4DP);
                }
                //endregion small avatar

                //region left buttons
                if (mLeftIcon[0] != 0) {
                    leftIcon = makeIcon(R.id.view_toolbar_btn_left, mLeftIcon[0]);
                    mainConstraint.addView(leftIcon);
                    setIconViewSize(leftIcon, set);

                    set.connect(leftIcon.getId(), START, PARENT_ID, START, VALUE_4DP);
                    set.connect(leftIcon.getId(), TOP, PARENT_ID, TOP, VALUE_4DP);
                    set.connect(leftIcon.getId(), BOTTOM, PARENT_ID, BOTTOM, VALUE_10DP);

                }
                //endregion left buttons

                //region right icons
                if (mRightIcons[0] != 0) {
                    //region first right
                    rightIcon = makeIcon(R.id.view_toolbar_btn_right_1, mRightIcons[0]);
                    mainConstraint.addView(rightIcon);
                    setIconViewSize(rightIcon, set);

                    set.setMargin(rightIcon.getId(), END, VALUE_4DP);
                    set.setMargin(rightIcon.getId(), TOP, VALUE_4DP);
                    set.setMargin(rightIcon.getId(), BOTTOM, VALUE_10DP);

                    set.connect(rightIcon.getId(), END, PARENT_ID, END);
                    set.connect(rightIcon.getId(), TOP, PARENT_ID, TOP);
                    set.connect(rightIcon.getId(), BOTTOM, PARENT_ID, BOTTOM);
                    //endregion first right

                    if (mRightIcons[1] != 0) {

                        //region 2nd right
                        rightIcon2 = makeIcon(R.id.view_toolbar_btn_right_2, mRightIcons[1]);
                        mainConstraint.addView(rightIcon2);
                        setIconViewSize(rightIcon2, set);

                        set.setMargin(rightIcon2.getId(), END, VALUE_1DP);

                        set.connect(rightIcon2.getId(), END, rightIcon.getId(), START);
                        set.connect(rightIcon2.getId(), TOP, rightIcon.getId(), TOP);
                        set.connect(rightIcon2.getId(), BOTTOM, rightIcon.getId(), BOTTOM);
                        //endregion 2nd right

                        if (mRightIcons[2] != 0) {
                            //region 3nd right
                            rightIcon3 = makeIcon(R.id.view_toolbar_btn_right_3, mRightIcons[2]);
                            mainConstraint.addView(rightIcon3);
                            setIconViewSize(rightIcon3, set);

                            set.setMargin(rightIcon3.getId(), END, VALUE_1DP);

                            set.connect(rightIcon3.getId(), END, rightIcon2.getId(), START);
                            set.connect(rightIcon3.getId(), TOP, rightIcon.getId(), TOP);
                            set.connect(rightIcon3.getId(), BOTTOM, rightIcon.getId(), BOTTOM);
                            //endregion 3nd right

                            if (mRightIcons[3] != 0) {
                                //region 4nd right
                                rightIcon4 = makeIcon(R.id.view_toolbar_btn_right_4, mRightIcons[3]);
                                mainConstraint.addView(rightIcon4);
                                setIconViewSize(rightIcon4, set);

                                set.setMargin(rightIcon4.getId(), END, VALUE_1DP);

                                set.connect(rightIcon4.getId(), END, rightIcon3.getId(), START);
                                set.connect(rightIcon4.getId(), TOP, rightIcon.getId(), TOP);
                                set.connect(rightIcon4.getId(), BOTTOM, rightIcon.getId(), BOTTOM);
                                //endregion 3nd right
                            }
                        }


                    }

                }
                //endregion right icons

                //region search box

                if (isSearchBoxShown) {

                    searchLayout = new RelativeLayout(getContext());
                    searchLayout.setGravity(Gravity.CENTER_VERTICAL);
                    searchLayout.setId(R.id.view_toolbar_search_layout);

                    if (isBigSearchBox)
                        setRoot.constrainHeight(searchLayout.getId(), i_Dp(R.dimen.toolbar_search_box_big_size));
                    else
                        setRoot.constrainHeight(searchLayout.getId(), i_Dp(R.dimen.toolbar_search_box_size));

                    setRoot.constrainWidth(searchLayout.getId(), MATCH_CONSTRAINT);

                    if (isBigSearchBox) {
                        setRoot.setMargin(searchLayout.getId(), LEFT, i_Dp(R.dimen.dp40));
                        setRoot.setMargin(searchLayout.getId(), RIGHT, i_Dp(R.dimen.dp40));
                        setRoot.setMargin(searchLayout.getId(), START, i_Dp(R.dimen.dp40));
                        setRoot.setMargin(searchLayout.getId(), END, i_Dp(R.dimen.dp40));
                    } else {
                        setRoot.setMargin(searchLayout.getId(), LEFT, i_Dp(R.dimen.dp52));
                        setRoot.setMargin(searchLayout.getId(), RIGHT, i_Dp(R.dimen.dp52));
                        setRoot.setMargin(searchLayout.getId(), START, i_Dp(R.dimen.dp52));
                        setRoot.setMargin(searchLayout.getId(), END, i_Dp(R.dimen.dp52));
                    }

                    setRoot.connect(searchLayout.getId(), START, mainConstraint.getId(), START);
                    setRoot.connect(searchLayout.getId(), END, mainConstraint.getId(), END);
                    setRoot.connect(searchLayout.getId(), TOP, mainConstraint.getId(), BOTTOM);
                    setRoot.connect(searchLayout.getId(), BOTTOM, mainConstraint.getId(), BOTTOM);
                    addView(searchLayout);

                    tvSearch = new TextView(getContext());
                    tvSearch.setId(R.id.view_toolbar_search_layout_txt);
                    tvSearch.setText(R.string.search);
                    tvSearch.setGravity(Gravity.CENTER);
                    tvSearch.setVisibility(VISIBLE);
                    tvSearch.setTypeface(tfMain);
                    tvSearch.setTextColor(new Theme().getTitleTextColor(tvSearch.getContext()));
                    Utils.setTextSize(tvSearch, R.dimen.smallTextSize);
                    setLayoutParams(tvSearch, i_Dp(R.dimen.dp20), 0, 0, i_Dp(R.dimen.dp20), 0, 0);
                    searchLayout.addView(tvSearch);

                    edtSearch = new EditText(getContext());
                    edtSearch.setId(R.id.view_toolbar_search_layout_edt_input);
                    edtSearch.setBackgroundResource(android.R.color.transparent);
                    edtSearch.setGravity(Gravity.CENTER);
                    edtSearch.setVisibility(GONE);
                    edtSearch.setTypeface(tfMain);
                    edtSearch.setHint(R.string.search);
                    edtSearch.setSingleLine();
                    edtSearch.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
                    Utils.setTextSize(edtSearch, R.dimen.smallTextSize);
                    setLayoutParams(edtSearch, i_Dp(R.dimen.dp20), 0, 0, i_Dp(R.dimen.dp20), i_Dp(R.dimen.dp32), i_Dp(R.dimen.dp32));
                    searchLayout.addView(edtSearch);

                    tvClearSearch = new TextView(getContext());
                    tvClearSearch.setTypeface(tfFontIcon);
                    tvClearSearch.setGravity(Gravity.CENTER);
                    tvClearSearch.setText(R.string.close_icon);
                    tvClearSearch.setVisibility(GONE);
                    Utils.setTextSize(tvClearSearch, R.dimen.largeTextSize);
                    RelativeLayout.LayoutParams lp = setLayoutParams(tvClearSearch, WRAP_CONTENT, i_Dp(R.dimen.toolbar_search_box_size), i_Dp(R.dimen.dp20), i_Dp(R.dimen.dp20));
                    lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, searchLayout.getId());
                    lp.addRule(RelativeLayout.CENTER_VERTICAL, searchLayout.getId());
                    tvClearSearch.setLayoutParams(lp);
                    searchLayout.addView(tvClearSearch);

                    if (isDark) {
                        searchLayout.setBackgroundResource(R.drawable.shape_toolbar_search_box_dark);
                        tvClearSearch.setTextColor(getContext().getResources().getColor(R.color.white));
                        edtSearch.setTextColor(getContext().getResources().getColor(R.color.white));
                        edtSearch.setHintTextColor(getContext().getResources().getColor(R.color.gray_f2));
                        tvSearch.setTextColor(getContext().getResources().getColor(R.color.gray_f2));
                    } else {
                        searchLayout.setBackgroundResource(R.drawable.shape_toolbar_search_box);
                        tvClearSearch.setTextColor(getContext().getResources().getColor(R.color.black));
                        edtSearch.setTextColor(getContext().getResources().getColor(R.color.black));
                        edtSearch.setHintTextColor(getContext().getResources().getColor(R.color.gray_9d));
                        tvSearch.setTextColor(getContext().getResources().getColor(R.color.gray_9d));
                    }

                    if (isBigSearchBox) {
                        Utils.setTextSize(tvSearch, R.dimen.standardTextSize);
                        Utils.setTextSize(edtSearch, R.dimen.standardTextSize);
                        Utils.setTextSize(tvClearSearch, R.dimen.xlargeTextSize);
                    }
                }
                //endregion search box

                //region chat

                if (isInChatRoom) {

                    //region avatar
                    rlChatAvatar = new RelativeLayout(getContext());
                    rlChatAvatar.setId(R.id.view_toolbar_user_chat_avatar_layout);

                    set.constrainWidth(rlChatAvatar.getId(), i_Dp(R.dimen.toolbar_chat_avatar_size));
                    set.constrainHeight(rlChatAvatar.getId(), i_Dp(R.dimen.toolbar_chat_avatar_size));
                    set.connect(rlChatAvatar.getId(), TOP, PARENT_ID, TOP);
                    set.connect(rlChatAvatar.getId(), BOTTOM, PARENT_ID, BOTTOM);
                    set.setMargin(rlChatAvatar.getId(), END, i_Dp(R.dimen.dp8));
                    if (leftIcon != null) {
                        set.connect(rlChatAvatar.getId(), START, leftIcon.getId(), END, i_Dp(R.dimen.dp8));
                    } else {
                        set.connect(rlChatAvatar.getId(), START, PARENT_ID, END, i_Dp(R.dimen.dp8));
                    }

                    civAvatar = new CircleImageView(getContext());
                    civCloud = new CircleImageView(getContext());

                    civAvatar.setScaleType(ImageView.ScaleType.CENTER_CROP);

                    civAvatar.setId(R.id.view_toolbar_user_chat_avatar);
                    civCloud.setId(R.id.view_toolbar_user_cloud_avatar);

                    civCloud.setImageResource(R.drawable.ic_cloud_space_blue);

                    setLayoutParams(civAvatar, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, 0, 0, 0);
                    setLayoutParams(civCloud, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, 0, 0, 0);

                    civAvatar.setVisibility(GONE);
                    civCloud.setVisibility(GONE);

                    rlChatAvatar.addView(civAvatar);
                    rlChatAvatar.addView(civCloud);

                    mainConstraint.addView(rlChatAvatar);
                    //endregion avatar

                    //region chat titles
                    layoutChatName = new LinearLayout(getContext());
                    layoutChatName.setGravity(Gravity.CENTER_VERTICAL);
                    layoutChatName.setId(R.id.view_toolbar_chat_layout_userName);
                    layoutChatName.setOrientation(LinearLayout.HORIZONTAL);
                    layoutChatName.setGravity(Gravity.LEFT);
                    mainConstraint.addView(layoutChatName);

                    set.constrainWidth(layoutChatName.getId(), MATCH_CONSTRAINT);
                    set.constrainHeight(layoutChatName.getId(), WRAP_CONTENT);

                    set.connect(layoutChatName.getId(), START, rlChatAvatar.getId(), END, i_Dp(R.dimen.dp4));
                    set.connect(layoutChatName.getId(), TOP, rlChatAvatar.getId(), TOP);
                    if (rightIcon4 != null) {
                        set.connect(layoutChatName.getId(), END, rightIcon4.getId(), START, i_Dp(R.dimen.dp8));
                    } else if (rightIcon3 != null) {
                        set.connect(layoutChatName.getId(), END, rightIcon3.getId(), START, i_Dp(R.dimen.dp8));

                    } else if (rightIcon2 != null) {
                        set.connect(layoutChatName.getId(), END, rightIcon2.getId(), START, i_Dp(R.dimen.dp8));

                    } else if (rightIcon != null) {
                        set.connect(layoutChatName.getId(), END, rightIcon.getId(), START, i_Dp(R.dimen.dp8));

                    } else {
                        set.connect(layoutChatName.getId(), END, PARENT_ID, END, i_Dp(R.dimen.dp8));
                    }

                    //chat name
                    tvChatName = new AppCompatTextView(getContext());
                    tvChatName.setId(R.id.view_toolbar_chat_txt_userName);
                    tvChatName.setTypeface(tfMain);
                    tvChatName.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) getContext().getResources().getDimension(R.dimen.standardTextSize));
                    tvChatName.setSingleLine();
                    tvChatName.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                    tvChatName.setMarqueeRepeatLimit(-1);
                    tvChatName.setHorizontalScrollBarEnabled(true);
                    tvChatName.setSelected(true);
                    tvChatName.setGravity(Gravity.LEFT);
                    tvChatName.setTextColor(getContext().getResources().getColor(R.color.white));
                    setLayoutParams(tvChatName, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    tvChatName.setMaxWidth(i_Dp(R.dimen.toolbar_txt_name_max_width));
                    layoutChatName.addView(tvChatName);

                    //verify icon
                    iconChatVerify = makeIcon(R.id.view_toolbar_chat_txt_verify, R.string.verify_icon);
                    iconChatVerify.setTextColor(getContext().getResources().getColor(R.color.verify_color));
                    Utils.setTextSize(iconChatVerify, R.dimen.smallTextSize);
                    iconChatVerify.setVisibility(GONE);
                    iconChatVerify.setPadding(i_Dp(R.dimen.dp4), 0, 0, 0);
                    setLayoutParams(iconChatVerify, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    layoutChatName.addView(iconChatVerify);

                    //mute icon
                    muteChatIcon = makeIcon(R.id.view_toolbar_chat_txt_isMute, R.string.mute_icon);
                    Utils.setTextSize(muteChatIcon, R.dimen.smallTextSize);
                    muteChatIcon.setVisibility(GONE);
                    muteChatIcon.setPadding(i_Dp(R.dimen.dp4), 0, 0, 0);
                    setLayoutParams(muteChatIcon, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    layoutChatName.addView(muteChatIcon);

                    //status or member count
                    tvChatStatus = new TextView(getContext());
                    tvChatStatus.setId(R.id.view_toolbar_chat_txt_seen_status);
                    tvChatStatus.setTypeface(tfMain);
                    tvChatStatus.setTextColor(getContext().getResources().getColor(R.color.white));
                    tvChatStatus.setSingleLine();
                    tvChatStatus.setGravity(Gravity.LEFT);
                    Utils.setTextSize(tvChatStatus, R.dimen.verySmallTextSize);
                    mainConstraint.addView(tvChatStatus);

                    set.constrainWidth(tvChatStatus.getId(), MATCH_CONSTRAINT);
                    set.constrainHeight(tvChatStatus.getId(), WRAP_CONTENT);
                    set.setMargin(tvChatStatus.getId(), TOP, i_Dp(R.dimen.dp6));

                    set.connect(tvChatStatus.getId(), START, layoutChatName.getId(), START);
                    set.connect(tvChatStatus.getId(), BOTTOM, rlChatAvatar.getId(), BOTTOM);
                    set.connect(tvChatStatus.getId(), END, layoutChatName.getId(), END);

                    //endregion chat titles

                }

                //endregion chat

                //region profile
                if (isGroupProfile) {

                    //extend root for adding avatar and titles
                    setRoot.constrainHeight(mainConstraint.getId(), i_Dp(R.dimen.toolbar_height_root_with_profile));

                    //profile big avatar
                    civProfileAvatar = new CircleImageView(getContext());
                    civProfileAvatar.setId(R.id.groupAvatar);
                    //civProfileAvatar.setImageResource(R.drawable.ic_cloud_space_blue);
                    mainConstraint.addView(civProfileAvatar);

                    set.constrainHeight(civProfileAvatar.getId(), i_Dp(R.dimen.dp68));
                    set.constrainWidth(civProfileAvatar.getId(), i_Dp(R.dimen.dp68));
                    set.connect(civProfileAvatar.getId(), START, PARENT_ID, START, i_Dp(R.dimen.dp14));
                    set.connect(civProfileAvatar.getId(), BOTTOM, PARENT_ID, BOTTOM, i_Dp(R.dimen.dp14));
                    if (leftIcon != null) {
                        set.connect(civProfileAvatar.getId(), TOP, leftIcon.getId(), BOTTOM);
                    } else {
                        set.connect(civProfileAvatar.getId(), TOP, PARENT_ID, TOP, i_Dp(R.dimen.dp52));
                    }

                    //titles
                    tvProfileName = new AppCompatTextView(getContext());
                    tvProfileName.setId(R.id.groupName);
                    tvProfileName.setTypeface(tfMain);
                    tvProfileName.setGravity(Gravity.LEFT);
                    tvProfileName.setSingleLine();
                    Utils.setTextSize(tvProfileName, R.dimen.largeTextSize);
                    tvProfileName.setTextColor(getContext().getResources().getColor(R.color.white));
                    mainConstraint.addView(tvProfileName);

                    set.constrainWidth(tvProfileName.getId(), MATCH_CONSTRAINT);
                    set.constrainHeight(tvProfileName.getId(), WRAP_CONTENT);

                    set.connect(tvProfileName.getId(), TOP, civProfileAvatar.getId(), TOP);
                    set.connect(tvProfileName.getId(), START, civProfileAvatar.getId(), END, i_Dp(R.dimen.dp16));
                    set.connect(tvProfileName.getId(), END, PARENT_ID, END, i_Dp(R.dimen.dp16));

                    tvProfileMemberCount = new AppCompatTextView(getContext());
                    tvProfileMemberCount.setId(R.id.groupMemberCount);
                    tvProfileMemberCount.setTypeface(tfMain);
                    tvProfileMemberCount.setGravity(Gravity.LEFT);
                    tvProfileMemberCount.setSingleLine();
                    Utils.setTextSize(tvProfileMemberCount, R.dimen.largeTextSize);
                    tvProfileMemberCount.setTextColor(getContext().getResources().getColor(R.color.white));
                    mainConstraint.addView(tvProfileMemberCount);

                    set.constrainWidth(tvProfileMemberCount.getId(), MATCH_CONSTRAINT);
                    set.constrainHeight(tvProfileMemberCount.getId(), WRAP_CONTENT);

                    set.connect(tvProfileMemberCount.getId(), TOP, tvProfileName.getId(), BOTTOM);
                    set.connect(tvProfileMemberCount.getId(), START, tvProfileName.getId(), START);
                    set.connect(tvProfileMemberCount.getId(), BOTTOM, civProfileAvatar.getId(), BOTTOM);
                    set.connect(tvProfileMemberCount.getId(), END, tvProfileName.getId(), END);

                    if (leftIcon != null) {
                        set.connect(leftIcon.getId(), BOTTOM, civProfileAvatar.getId(), TOP);
                    }
                    if (rightIcon != null) {
                        set.connect(rightIcon.getId(), BOTTOM, civProfileAvatar.getId(), TOP);
                    }
                    if (logo != null) {
                        set.connect(logo.getId(), BOTTOM, civProfileAvatar.getId(), TOP);
                    }
                }

                if (isContactProfile) {

                    //extend root for adding avatar and titles
                    setRoot.constrainHeight(mainConstraint.getId(), i_Dp(R.dimen.toolbar_height_root_with_profile));

                    //profile big avatar
                    civProfileAvatar = new CircleImageView(getContext());
                    civProfileAvatar.setId(R.id.groupAvatar);
                    //civProfileAvatar.setImageResource(R.drawable.ic_cloud_space_blue);
                    addView(civProfileAvatar);

                    setRoot.constrainHeight(civProfileAvatar.getId(), i_Dp(R.dimen.dp120));
                    setRoot.constrainWidth(civProfileAvatar.getId(), i_Dp(R.dimen.dp120));
                    setRoot.connect(civProfileAvatar.getId(), START, PARENT_ID, START, i_Dp(R.dimen.dp14));
                    setRoot.connect(civProfileAvatar.getId(), BOTTOM, mainConstraint.getId(), BOTTOM);
                    setRoot.connect(civProfileAvatar.getId(), TOP, mainConstraint.getId(), BOTTOM);

                    //button to go on chat room
                    fabChat = new FloatingActionButton(getContext());
                    fabChat.setId(R.id.chi_fab_setPic);

                    fabChat.setImageResource(R.drawable.ic_chat_message);
                    fabChat.setSize(FloatingActionButton.SIZE_MINI);
                    addView(fabChat);

                    setRoot.constrainHeight(fabChat.getId(), WRAP_CONTENT);
                    setRoot.constrainWidth(fabChat.getId(), WRAP_CONTENT);

                    setRoot.connect(fabChat.getId(), END, PARENT_ID, END, i_Dp(R.dimen.dp16));
                    setRoot.connect(fabChat.getId(), TOP, mainConstraint.getId(), BOTTOM);
                    setRoot.connect(fabChat.getId(), BOTTOM, mainConstraint.getId(), BOTTOM);


                    //titles
                    tvProfileMemberCount = new AppCompatTextView(getContext());
                    tvProfileMemberCount.setId(R.id.groupMemberCount);
                    tvProfileMemberCount.setTypeface(tfMain);
                    tvProfileMemberCount.setGravity(Gravity.LEFT);
                    tvProfileMemberCount.setSingleLine();
                    Utils.setTextSize(tvProfileMemberCount, R.dimen.smallTextSize);
                    tvProfileMemberCount.setTextColor(getContext().getResources().getColor(R.color.white));
                    addView(tvProfileMemberCount);

                    setRoot.constrainWidth(tvProfileMemberCount.getId(), MATCH_CONSTRAINT);
                    setRoot.constrainHeight(tvProfileMemberCount.getId(), WRAP_CONTENT);

                    setRoot.connect(tvProfileMemberCount.getId(), START, civProfileAvatar.getId(), END, i_Dp(R.dimen.dp8));
                    setRoot.connect(tvProfileMemberCount.getId(), BOTTOM, mainConstraint.getId(), BOTTOM, i_Dp(R.dimen.dp1));
                    setRoot.connect(tvProfileMemberCount.getId(), END, fabChat.getId(), START, i_Dp(R.dimen.dp4));

                    tvProfileName = new AppCompatTextView(getContext());
                    tvProfileName.setId(R.id.groupName);
                    tvProfileName.setTypeface(tfMain);
                    tvProfileName.setGravity(Gravity.LEFT);
                    tvProfileName.setSingleLine();
                    Utils.setTextSize(tvProfileName, R.dimen.standardTextSize);
                    tvProfileName.setTextColor(getContext().getResources().getColor(R.color.white));
                    addView(tvProfileName);

                    setRoot.constrainWidth(tvProfileName.getId(), MATCH_CONSTRAINT);
                    setRoot.constrainHeight(tvProfileName.getId(), WRAP_CONTENT);

                    setRoot.connect(tvProfileName.getId(), BOTTOM, tvProfileMemberCount.getId(), TOP);
                    setRoot.connect(tvProfileName.getId(), START, tvProfileMemberCount.getId(), START);
                    setRoot.connect(tvProfileName.getId(), END, tvProfileMemberCount.getId(), END);

                    tvProfileTell = new AppCompatTextView(getContext());
                    tvProfileTell.setId(R.id.phoneNumber);
                    tvProfileTell.setTypeface(tfMain);
                    tvProfileTell.setGravity(Gravity.LEFT);
                    tvProfileTell.setSingleLine();
                    Utils.setTextSize(tvProfileTell, R.dimen.smallTextSize);

                    addView(tvProfileTell);

                    setRoot.constrainWidth(tvProfileTell.getId(), MATCH_CONSTRAINT);
                    setRoot.constrainHeight(tvProfileTell.getId(), WRAP_CONTENT);

                    setRoot.connect(tvProfileTell.getId(), TOP, mainConstraint.getId(), BOTTOM, i_Dp(R.dimen.dp2));
                    setRoot.connect(tvProfileTell.getId(), START, tvProfileMemberCount.getId(), START);
                    setRoot.connect(tvProfileTell.getId(), END, tvProfileMemberCount.getId(), END);

                    tvProfileStatus = new AppCompatTextView(getContext());
                    tvProfileStatus.setId(R.id.status);
                    tvProfileStatus.setTypeface(tfMain);
                    tvProfileStatus.setGravity(Gravity.LEFT);
                    tvProfileStatus.setSingleLine();
                    Utils.setTextSize(tvProfileStatus, R.dimen.smallTextSize);

                    addView(tvProfileStatus);


                    setRoot.constrainWidth(tvProfileStatus.getId(), MATCH_CONSTRAINT);
                    setRoot.constrainHeight(tvProfileStatus.getId(), WRAP_CONTENT);

                    setRoot.connect(tvProfileStatus.getId(), TOP, tvProfileTell.getId(), BOTTOM, i_Dp(R.dimen.dp2));
                    setRoot.connect(tvProfileStatus.getId(), START, tvProfileMemberCount.getId(), START);
                    setRoot.connect(tvProfileStatus.getId(), END, tvProfileMemberCount.getId(), END);

                    if (leftIcon != null) {
                        set.connect(leftIcon.getId(), BOTTOM, civProfileAvatar.getId(), TOP);
                    }
                    if (rightIcon != null) {
                        set.connect(rightIcon.getId(), BOTTOM, civProfileAvatar.getId(), TOP);
                    }
                    if (logo != null) {
                        set.connect(logo.getId(), BOTTOM, civProfileAvatar.getId(), TOP);
                    }
                }


                //endregion profile

                //region PassCode

                if (isPassCodeEnable) {
                    passCodeIcon = makeIcon(R.id.view_toolbar_btn_passCode, mPassCodeIcon);
                    mainConstraint.addView(passCodeIcon);
                    setIconViewSize(passCodeIcon, set);

                    if (leftIcon != null) {
                        set.connect(passCodeIcon.getId(), START, leftIcon.getId(), END);
                        set.connect(passCodeIcon.getId(), TOP, leftIcon.getId(), TOP);
                        set.connect(passCodeIcon.getId(), BOTTOM, leftIcon.getId(), BOTTOM);
                    } else {
                        set.connect(passCodeIcon.getId(), START, PARENT_ID, START, VALUE_4DP);
                        set.connect(passCodeIcon.getId(), TOP, PARENT_ID, TOP, VALUE_4DP);
                        set.connect(passCodeIcon.getId(), BOTTOM, PARENT_ID, BOTTOM, VALUE_10DP);
                    }
                }

                //endregion PassCode

                //region scanner
                if (isScannerEnable) {

                    scannerIcon = makeIcon(R.id.view_toolbar_btn_scanner, mScannerIcon);
                    mainConstraint.addView(scannerIcon);
                    setIconViewSize(scannerIcon, set);

                    if (rightIcon != null) {
                        set.connect(scannerIcon.getId(), END, rightIcon.getId(), START);
                        set.connect(scannerIcon.getId(), TOP, rightIcon.getId(), TOP);
                        set.connect(scannerIcon.getId(), BOTTOM, rightIcon.getId(), BOTTOM);
                    } else if (smallAvatar != null) {
                        set.connect(scannerIcon.getId(), END, smallAvatar.getId(), START);
                        set.connect(scannerIcon.getId(), TOP, smallAvatar.getId(), TOP);
                        set.connect(scannerIcon.getId(), BOTTOM, smallAvatar.getId(), BOTTOM);
                    } else {
                        set.connect(scannerIcon.getId(), END, PARENT_ID, END, VALUE_4DP);
                        set.connect(scannerIcon.getId(), TOP, PARENT_ID, TOP, VALUE_4DP);
                        set.connect(scannerIcon.getId(), BOTTOM, PARENT_ID, BOTTOM, VALUE_10DP);
                    }
                }
                //endregion scanner

                setRoot.applyTo(this);
                set.applyTo(mainConstraint);

            }/* else {

                ConstraintSet set = new ConstraintSet();

                if (isDark) {
                    setBackgroundResource(R.color.background_setting_dark_2);
                } else {
                    setBackgroundResource(R.color.white);
                }

                //avatar in tablet toolbar
                tUserAvatar = new CircleImageView(getContext());
                tUserAvatar.setId(R.id.toolbar_tablet_user_avatar);
                tUserAvatar.setImageResource(R.drawable.ic_cloud_space_blue);
                addView(tUserAvatar);

                set.constrainWidth(tUserAvatar.getId(), i_Dp(R.dimen.dp60));
                set.constrainHeight(tUserAvatar.getId(), i_Dp(R.dimen.dp60));
                set.connect(tUserAvatar.getId(), TOP, PARENT_ID, TOP, i_Dp(R.dimen.dp20));
                set.connect(tUserAvatar.getId(), START, PARENT_ID, START, i_Dp(R.dimen.dp10));
                set.setMargin(tUserAvatar.getId(), END, i_Dp(R.dimen.dp10));

                //user name
                tUserName = new TextView(getContext());
                tUserName.setId(R.id.toolbar_tablet_user_name);
                tUserName.setTypeface(tfMain);
                tUserName.setText("Alireza Nazari");
                Utils.darkModeHandler(tUserName);
                tUserName.setSingleLine();
                tUserName.setGravity(Gravity.LEFT);
                Utils.setTextSize(tUserName, R.dimen.standardTextSize);
                addView(tUserName);

                set.constrainWidth(tUserName.getId(), MATCH_CONSTRAINT);
                set.constrainHeight(tUserName.getId(), WRAP_CONTENT);

                set.connect(tUserName.getId(), START, tUserAvatar.getId(), END, i_Dp(R.dimen.dp10));
                set.connect(tUserName.getId(), TOP, tUserAvatar.getId(), TOP, i_Dp(R.dimen.dp6));
                set.connect(tUserName.getId(), END, PARENT_ID, END, i_Dp(R.dimen.dp10));

                //phone number
                tUserPhone = new TextView(getContext());
                tUserPhone.setId(R.id.toolbar_tablet_user_phone);
                tUserPhone.setTypeface(tfMain);
                Utils.darkModeHandler(tUserPhone);
                tUserPhone.setSingleLine();
                tUserPhone.setText("0910 267 7509");
                tUserPhone.setGravity(Gravity.LEFT);
                Utils.setTextSize(tUserPhone, R.dimen.smallTextSize);
                addView(tUserPhone);

                set.constrainWidth(tUserPhone.getId(), MATCH_CONSTRAINT);
                set.constrainHeight(tUserPhone.getId(), WRAP_CONTENT);

                set.connect(tUserPhone.getId(), START, tUserName.getId(), START);
                set.connect(tUserPhone.getId(), BOTTOM, tUserAvatar.getId(), BOTTOM, i_Dp(R.dimen.dp6));
                set.connect(tUserPhone.getId(), END, tUserName.getId(), END);

                //vertical splitter line
                View tViewSplitter1 = new View(getContext());
                tViewSplitter1.setId(R.id.toolbar_tablet_splitter1);
                Utils.setBackgroundColorGray(tViewSplitter1);
                addView(tViewSplitter1);

                set.constrainWidth(tViewSplitter1.getId(), MATCH_CONSTRAINT);
                set.constrainHeight(tViewSplitter1.getId(), 1);

                set.connect(tViewSplitter1.getId(), START, tUserAvatar.getId(), START);
                set.connect(tViewSplitter1.getId(), END, PARENT_ID, END);
                set.connect(tViewSplitter1.getId(), TOP, tUserAvatar.getId(), BOTTOM, i_Dp(R.dimen.dp10));

                //add button
                tIconAdd = makeIcon(R.id.toolbar_tablet_btn_add, mTabletIcons[0]);
                tIconAdd.setTextColor(getContext().getResources().getColor(R.color.green));
                addView(tIconAdd);
                setIconViewSize(tIconAdd, set);

                set.connect(tIconAdd.getId(), END, tViewSplitter1.getId(), END, VALUE_4DP);
                set.connect(tIconAdd.getId(), TOP, tViewSplitter1.getId(), BOTTOM, VALUE_4DP);

                //edit button
                tIconEdit = makeIcon(R.id.toolbar_tablet_btn_edit, mTabletIcons[1]);
                tIconEdit.setTextColor(getContext().getResources().getColor(R.color.green));
                addView(tIconEdit);
                setIconViewSize(tIconEdit, set);

                set.connect(tIconEdit.getId(), END, tIconAdd.getId(), START, VALUE_1DP);
                set.connect(tIconEdit.getId(), TOP, tIconAdd.getId(), TOP);
                set.connect(tIconEdit.getId(), BOTTOM, tIconAdd.getId(), BOTTOM);


                //horizontal splitter line
                View tViewSplitter2 = new View(getContext());
                tViewSplitter2.setId(R.id.toolbar_tablet_splitter2);
                Utils.setBackgroundColorGray(tViewSplitter2);
                addView(tViewSplitter2);

                set.constrainWidth(tViewSplitter2.getId(), 1);
                set.constrainHeight(tViewSplitter2.getId(), MATCH_CONSTRAINT);

                set.connect(tViewSplitter2.getId(), END, tIconEdit.getId(), START, VALUE_4DP);
                set.connect(tViewSplitter2.getId(), TOP, tIconEdit.getId(), TOP);
                set.connect(tViewSplitter2.getId(), BOTTOM, tIconEdit.getId(), BOTTOM);


                //edit button
                tIconSearch = makeIcon(R.id.toolbar_tablet_btn_search, mTabletIcons[2]);
                tIconSearch.setTextColor(getContext().getResources().getColor(R.color.green));
                addView(tIconSearch);
                setIconViewSize(tIconSearch, set);

                set.connect(tIconSearch.getId(), END, tViewSplitter2.getId(), START, VALUE_1DP);
                set.connect(tIconSearch.getId(), TOP, tIconAdd.getId(), TOP);
                set.connect(tIconSearch.getId(), BOTTOM, tIconAdd.getId(), BOTTOM);


                //vertical second splitter line
                View tViewSplitter3 = new View(getContext());
                tViewSplitter3.setId(R.id.toolbar_tablet_splitter3);
                Utils.setBackgroundColorGray(tViewSplitter3);
                addView(tViewSplitter3);

                set.constrainWidth(tViewSplitter3.getId(), MATCH_CONSTRAINT);
                set.constrainHeight(tViewSplitter3.getId(), 1);

                set.connect(tViewSplitter3.getId(), START, tViewSplitter1.getId(), START);
                set.connect(tViewSplitter3.getId(), END, tViewSplitter1.getId(), END);
                set.connect(tViewSplitter3.getId(), TOP, tIconAdd.getId(), BOTTOM, i_Dp(R.dimen.dp6));

                set.applyTo(this);
            }*/
        }

        private void setIconViewSize(View v, ConstraintSet set) {
            set.constrainWidth(v.getId(), i_Dp(R.dimen.toolbar_icon_size));
            set.constrainHeight(v.getId(), i_Dp(R.dimen.toolbar_icon_size));
        }

        private AppCompatTextView makeIcon(int id, int icon) {

            AppCompatTextView vIcon = new AppCompatTextView(getContext());
            vIcon.setText(icon);
            vIcon.setGravity(Gravity.CENTER);
            vIcon.setId(id);
            setIconStyle(vIcon);

            return vIcon;

        }

        private void setIconStyle(AppCompatTextView view) {

            view.setTypeface(tfFontIcon);
            view.setTextColor(getContext().getResources().getColor(R.color.white));
            Utils.setTextSize(view, R.dimen.dp22);

        }

        private void setLayoutParams(View view, int mlef, int mtop, int mbottom, int mright, int lpadding, int rpadding) {

            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            lp.setMargins(mlef, mtop, mright, mbottom);
            view.setLayoutParams(lp);
            view.setPadding(lpadding, 0, rpadding, 0);

        }

        private RelativeLayout.LayoutParams setLayoutParams(View view, int width, int height, int lpadding, int rpadding) {

            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(width, height);
            view.setLayoutParams(lp);
            view.setPadding(lpadding, 0, rpadding, 0);

            return lp;
        }

        private RelativeLayout.LayoutParams setLayoutParams(View view, int width, int height, int mlef, int mright, int padding) {

            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(width, height);
            lp.setMargins(mlef, 0, mright, 0);
            view.setPadding(padding, padding, padding, padding);
            return lp;
        }

        private void setLayoutParams(View view, int width, int height) {

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width, height);
            view.setLayoutParams(lp);
        }

        private void setLayoutParams(View view, int width, int height, int marginTop) {

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width, height);
            lp.setMargins(0, marginTop, 0, 0);
            view.setLayoutParams(lp);
        }

        private void setupDefaults() {

            isDark = G.themeColor == Theme.DARK;

            VALUE_1DP = i_Dp(R.dimen.dp1);
            VALUE_4DP = i_Dp(R.dimen.dp4);
            VALUE_10DP = i_Dp(R.dimen.dp10);

        }

        //region getters

        public View getMusicLayout() {
            return musicLayout;
        }

        public View getCallLayout() {
            return callLayout;
        }

        public LinearLayout getLayoutMedia() {
            return layoutMedia;
        }

        public ConstraintLayout getMainConstraint() {
            return mainConstraint;
        }

        public AppCompatTextView getLeftIcon() {
            return leftIcon;
        }

        public AppCompatTextView getScannerIcon() {
            return scannerIcon;
        }

        public AppCompatTextView getpassCodeIcon() {
            return passCodeIcon;
        }

        public AppCompatTextView getRightIcon4() {
            return rightIcon4;
        }

        public AppCompatTextView getRightIcon3() {
            return rightIcon3;
        }

        public AppCompatTextView getRightIcon2() {
            return rightIcon2;
        }

        public AppCompatTextView getRightIcon() {
            return rightIcon;
        }

        public TextView getLogo() {
            return logo;
        }

        public RelativeLayout getSearchLayout() {
            return searchLayout;
        }

        public TextView getTvSearch() {
            return tvSearch;
        }

        public TextView getTvClearSearch() {
            return tvClearSearch;
        }

        public EditText getEdtSearch() {
            return edtSearch;
        }

        public RelativeLayout getRlChatAvatar() {
            return rlChatAvatar;
        }

        public CircleImageView getCivAvatar() {
            return civAvatar;
        }

        public CircleImageView getCivCloud() {
            return civCloud;
        }

        public LinearLayout getLayoutChatName() {
            return layoutChatName;
        }

        public AppCompatTextView getTvChatName() {
            return tvChatName;
        }

        public TextView getTvChatStatus() {
            return tvChatStatus;
        }

        public TextView getIconChatVerify() {
            return iconChatVerify;
        }

        public TextView getMuteChatIcon() {
            return muteChatIcon;
        }

        public CircleImageView getCivProfileAvatar() {
            return civProfileAvatar;
        }

        public AppCompatTextView getTvProfileName() {
            return tvProfileName;
        }

        public AppCompatTextView getTvProfileMemberCount() {
            return tvProfileMemberCount;
        }

        public AppCompatTextView getTvProfileTell() {
            return tvProfileTell;
        }

        public AppCompatTextView getTvProfileStatus() {
            return tvProfileStatus;
        }

        public FloatingActionButton getFabChat() {
            return fabChat;
        }

        public CircleImageView gettUserAvatar() {
            return tUserAvatar;
        }

        public TextView gettUserName() {
            return tUserName;
        }

        public TextView gettUserPhone() {
            return tUserPhone;
        }

        public AppCompatTextView gettIconAdd() {
            return tIconAdd;
        }

        public AppCompatTextView gettIconEdit() {
            return tIconEdit;
        }

        public AppCompatTextView gettIconSearch() {
            return tIconSearch;
        }

        public CircleImageView getSmallAvatar() {
            return smallAvatar;
        }

        //endregion getters
    }


}
