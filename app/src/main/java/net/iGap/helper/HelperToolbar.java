package net.iGap.helper;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.AppCompatTextView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityCall;
import net.iGap.activities.ActivityMain;
import net.iGap.interfaces.ICallFinish;
import net.iGap.interfaces.OnComplete;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.module.CircleImageView;
import net.iGap.module.EmojiTextViewE;
import net.iGap.module.MaterialDesignTextView;
import net.iGap.module.MusicPlayer;
import net.iGap.module.enums.ConnectionState;
import net.iGap.viewmodel.ActivityCallViewModel;


/**
 * SAMPLE OF USAGE ARE AT BOTTOM OF THIS FILE
 * 1 = create root view at the xml (LinearLayout)
 * 2 = get create object of this class and set details then call getView()
 * 3 = attach view to xml root toolbar view
 * 4 = implement toolbar listener based on your usage
 */
public class HelperToolbar {

    private AppCompatTextView mLeftBtn, mRightBtn, m2RightBtn, m3RightBtn, m4RightBtn;
    private TextView mTxtLogo, mTxtCounter, mTxtBigAvatarUserName, mTxtCallStatus, mTxtChatSeenStatus;
    private EmojiTextViewE mTxtChatUserName ;
    private CircleImageView mAvatarSmall, mAvatarBig, mAvatarChat, groupAvatar;
    private RelativeLayout mSearchBox;
    private TextView mTxtSearch;
    private AppCompatTextView groupName, groupMemberCount;
    public EditText mEdtSearch;
    private TextView mChatVerifyIcon ;
    private TextView mChatMuteIcon ;
    private CircleImageView mCloudChatIcon ;
    private TextView mBtnClearSearch;

    private LayoutInflater mInflater;
    private Context mContext;
    private ViewGroup mViewGroup = null;
    private ToolbarListener mToolbarListener;

    private int mLeftIcon=0;
    private int[] mRightIcons = {0, 0, 0, 0};
    private boolean isAttachToRoot;
    private boolean isSearchBoxShown;
    private boolean isLogoShown;
    private boolean isBigCenterAvatarShown;
    private boolean isRightSmallAvatarShown;
    private boolean isCounterShown;
    private boolean isInChatRoom;
    private boolean isCallModeEnable;
    private boolean isGroupProfile;
    private boolean isMediaPlayerEnabled;
    private String defaultTitleText = null;
    private boolean isShowEditTextForSearch;
    private View rootView;
    private boolean isSharedMedia;

    private HelperToolbar() {
    }

    public static HelperToolbar create() {
        return new HelperToolbar();
    }

    public HelperToolbar setContext(Context context) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
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

    /*public HelperToolbar setRightIcons(Bitmap... bitmaps) {
        this.mRightIcons = bitmaps;
        return this;
    }*/

    public HelperToolbar setRightIcons(@StringRes int... drawables) {
        System.arraycopy(drawables, 0, mRightIcons, 0, drawables.length);
        return this;
    }

    public HelperToolbar setLeftIcon(@StringRes int icon) {
        this.mLeftIcon = icon;
        if (mLeftBtn != null) mLeftBtn.setText(mLeftIcon);
        return this;
    }

    public HelperToolbar setSearchBoxShown(boolean searchBoxShown , boolean isShowEditTextForSearch ) {
        this.isSearchBoxShown = searchBoxShown;
        this.isShowEditTextForSearch = isShowEditTextForSearch;
        return this;
    }

    public HelperToolbar setSearchBoxShown(boolean searchBoxShown ) {
        this.isSearchBoxShown = searchBoxShown;
        this.isShowEditTextForSearch = true;
        return this;
    }

    public HelperToolbar setLogoShown(boolean logoShown) {
        this.isLogoShown = logoShown;
        return this;
    }

    public HelperToolbar setPlayerEnable(boolean isEnable) {
        this.isMediaPlayerEnabled = isEnable;
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

    public HelperToolbar setCallEnable(boolean isEnable) {
        this.isCallModeEnable = isEnable;
        return this;
    }

    public HelperToolbar setBigCenterAvatarShown(boolean avatarShown) {
        this.isBigCenterAvatarShown = avatarShown;
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

    public HelperToolbar setCounterShown(boolean counterShown) {
        //for show number of msg selected in chat room
        this.isCounterShown = counterShown;
        return this;
    }

    public HelperToolbar setDefaultTitle(String title){

        this.defaultTitleText = title ;

        if (mTxtLogo != null){
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

    public View getView() {

        if (mContext == null) throw new IllegalArgumentException("Context can not be null");

        //set default title name if user not set
        if (defaultTitleText == null || defaultTitleText.trim().equals("")){
            defaultTitleText = mContext.getResources().getString(R.string.app_name);
        }

        rootView = getInflater(R.layout.view_main_toolbar);
        setNormalSizeToRootViews(rootView);

        initViews(rootView);

        //check and set custom shape for dark mode
        if (G.isDarkTheme)
            rootView.findViewById(R.id.view_toolbar_main_constraint)
                    .setBackground(mContext.getResources().getDrawable(R.drawable.shape_toolbar_background_dark));

        if (mLeftIcon != 0) {
            mLeftBtn.setText(mLeftIcon);
            mLeftBtn.setVisibility(View.VISIBLE);
            mLeftBtn.setOnClickListener(v -> mToolbarListener.onLeftIconClickListener(v));

        } else {
            mLeftBtn.setVisibility(View.GONE);
        }

        if (mRightIcons[0] != 0) {
            mRightBtn.setText(mRightIcons[0]);
            mRightBtn.setVisibility(View.VISIBLE);
            mRightBtn.setOnClickListener(v -> mToolbarListener.onRightIconClickListener(v));

        } else {
            mRightBtn.setVisibility(View.GONE);
        }

        if (mRightIcons[1] != 0) {
            m2RightBtn.setText(mRightIcons[1]);
            m2RightBtn.setVisibility(View.VISIBLE);
            m2RightBtn.setOnClickListener(v -> mToolbarListener.onSecondRightIconClickListener(v));

        } else {
            m2RightBtn.setVisibility(View.GONE);
        }

        if (mRightIcons[2] != 0) {
            m3RightBtn.setText(mRightIcons[2]);
            m3RightBtn.setVisibility(View.VISIBLE);
            m3RightBtn.setOnClickListener(v -> mToolbarListener.onThirdRightIconClickListener(v));

        } else {
            m3RightBtn.setVisibility(View.GONE);
        }

        if (mRightIcons[3] != 0) {
            m4RightBtn.setText(mRightIcons[3]);
            m4RightBtn.setVisibility(View.VISIBLE);
            m4RightBtn.setOnClickListener(v -> mToolbarListener.onFourthRightIconClickListener(v));

        } else {
            m4RightBtn.setVisibility(View.GONE);
        }

        if (!isCounterShown)
            mTxtCounter.setVisibility(View.GONE);
        else
            mTxtCounter.setVisibility(View.VISIBLE);

        if (!isRightSmallAvatarShown) {
            mAvatarSmall.setVisibility(View.GONE);

        } else {
            mAvatarSmall.setVisibility(View.VISIBLE);
            mAvatarSmall.setOnClickListener(v -> mToolbarListener.onSmallAvatarClickListener(v));
        }

        if (isInChatRoom) {
            rootView.findViewById(R.id.view_toolbar_user_chat_avatar_layout).setVisibility(View.VISIBLE);
            rootView.findViewById(R.id.view_toolbar_chat_layout_userName).setVisibility(View.VISIBLE);
            mTxtChatSeenStatus.setVisibility(View.VISIBLE);
            mAvatarChat.setOnClickListener(v -> mToolbarListener.onChatAvatarClickListener(v));
            mCloudChatIcon.setOnClickListener(v -> mToolbarListener.onChatAvatarClickListener(v));
            mTxtChatUserName.setOnClickListener(v -> mToolbarListener.onChatAvatarClickListener(v));
            mTxtChatSeenStatus.setOnClickListener(v -> mToolbarListener.onChatAvatarClickListener(v));

        } else {
            rootView.findViewById(R.id.view_toolbar_user_chat_avatar_layout).setVisibility(View.GONE);
            rootView.findViewById(R.id.view_toolbar_chat_layout_userName).setVisibility(View.GONE);
            mTxtChatSeenStatus.setVisibility(View.GONE);

        }

        setBigAvatarVisibility(rootView, isBigCenterAvatarShown);

        setIGapLogoVisibility(rootView, isLogoShown);

        setSearchBoxVisibility(rootView, isSearchBoxShown);

        //setCallModeVisibility(result, isCallModeEnable);

        setGroupProfileVisibility(rootView, isGroupProfile);

        toolBarTitleHandler();

        if (isMediaPlayerEnabled){
            setMusicPlayer(rootView , isInChatRoom);
        }

        checkIGapFont();

        return rootView;

    }


    public TextView getTextViewCounter() {
        return mTxtCounter;
    }

    public TextView getTextViewCallStatus() {
        return mTxtCallStatus;
    }

    public EmojiTextViewE getTextViewChatUserName() {
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

    public View getRootView() {
        return rootView;
    }

    public TextView getButtonClearSearch() {
        return mBtnClearSearch;
    }

    public TextView getTextViewLogo() {
        return mTxtLogo;
    }

    public AppCompatTextView getLeftButton() {
        return mLeftBtn;
    }

    public AppCompatTextView getRightButton() {
        return mRightBtn;
    }

    public AppCompatTextView getSecondRightButton(){
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

    public AppCompatTextView getGroupName(){
        return groupName;
    }

    public AppCompatTextView getGroupMemberCount() {
        return groupMemberCount;
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

    public CircleImageView getAvatarSmall() {
        return mAvatarSmall;
    }

    /*************************************************************/

    private void setMusicPlayer(View view , boolean isChat) {

        LinearLayout musicLayout = view.findViewById(R.id.view_toolbar_layout_player_music);

        LinearLayout stripCallLayout = view.findViewById(R.id.view_toolbar_layout_strip_call);

        if (!isSearchBoxShown){
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) musicLayout.getLayoutParams();
            params.setMargins( 0 , (int) mContext.getResources().getDimension(R.dimen.dp14) , 0 , 0);
            musicLayout.setLayoutParams(params);

            LinearLayout.LayoutParams paramsCall = (LinearLayout.LayoutParams) stripCallLayout.getLayoutParams();
            paramsCall.setMargins( 0 , (int) mContext.getResources().getDimension(R.dimen.dp14) , 0 , 0);
            stripCallLayout.setLayoutParams(paramsCall);
        }

        if (isChat){
            MusicPlayer.chatLayout = musicLayout;
            ActivityCall.stripLayoutChat = view.findViewById(R.id.view_toolbar_layout_strip_call);

            ActivityCallViewModel.txtTimeChat = rootView.findViewById(R.id.cslcs_txt_timer);

            TextView txtCallActivityBack = rootView.findViewById(R.id.cslcs_btn_call_strip);
            txtCallActivityBack.setOnClickListener(v -> mContext.startActivity(new Intent(G.fragmentActivity, ActivityCall.class)));

            checkIsAvailableOnGoingCall();

        }else if (isSharedMedia){

            MusicPlayer.shearedMediaLayout = musicLayout;

        }else {
            MusicPlayer.mainLayout = musicLayout;
            ActivityCall.stripLayoutMain = view.findViewById(R.id.view_toolbar_layout_strip_call);


            ActivityCallViewModel.txtTimerMain = rootView.findViewById(R.id.cslcs_txt_timer);

            TextView txtCallActivityBack = rootView.findViewById(R.id.cslcs_btn_call_strip);
            txtCallActivityBack.setOnClickListener(v -> mContext.startActivity(new Intent(G.fragmentActivity, ActivityCall.class)));

        }

        MusicPlayer.setMusicPlayer(musicLayout);
        setMediaLayout();
        //setStripLayoutCall();

        G.callStripLayoutVisiblityListener.observe(G.fragmentActivity , isVisible -> {

            try{

                if (isVisible){
                    if (isChat) {
                        ActivityCall.stripLayoutChat.setVisibility(View.VISIBLE);


                    }else {
                        ActivityCall.stripLayoutMain.setVisibility(View.VISIBLE);

                    }


                    if (MusicPlayer.mainLayout != null) {
                        MusicPlayer.mainLayout.setVisibility(View.GONE);
                    }

                    if (MusicPlayer.chatLayout != null) {
                        MusicPlayer.chatLayout.setVisibility(View.GONE);
                    }
                }else {
                    if (isChat) {
                        ActivityCall.stripLayoutChat.setVisibility(View.GONE);
                    }else {
                        ActivityCall.stripLayoutMain.setVisibility(View.GONE);
                    }
                }

            }catch (Exception e){}

        });


    }

    public void checkIsAvailableOnGoingCall() {

        /*if (G.isInCall) {
            rootView.findViewById(R.id.view_toolbar_layout_strip_call).setVisibility(View.VISIBLE);


            G.iCallFinishChat = () -> {
                try {
                    rootView.findViewById(R.id.view_toolbar_layout_strip_call).setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };


            G.iCallFinishMain = () -> {
                try {
                    rootView.findViewById(R.id.view_toolbar_layout_strip_call).setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };

        } else {
            rootView.findViewById(R.id.view_toolbar_layout_strip_call).setVisibility(View.GONE);
        }*/
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
            HelperLog.setErrorLog(e);
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

    private void checkIGapFont() {

        if (mTxtLogo.getText().toString().toLowerCase().equals("igap")){
            mTxtLogo.setTypeface(G.typeface_neuropolitical);
        }else {
            mTxtLogo.setTypeface(G.typeface_IRANSansMobile);
        }
    }

    private void toolBarTitleHandler() {

        G.connectionStateMutableLiveData.observe(G.fragmentActivity, new android.arch.lifecycle.Observer<ConnectionState>() {
            @Override
            public void onChanged(@Nullable ConnectionState connectionState) {

                if (mTxtLogo != null && connectionState != null){

                    if (connectionState == ConnectionState.WAITING_FOR_NETWORK) {
                        mTxtLogo.setTextSize((int) (mContext.getResources().getDimension(R.dimen.dp16) / mContext.getResources().getDisplayMetrics().density));
                        mTxtLogo.setText(R.string.waiting_for_network);

                    } else if (connectionState == ConnectionState.CONNECTING) {
                        mTxtLogo.setTextSize((int) (mContext.getResources().getDimension(R.dimen.dp18) / mContext.getResources().getDisplayMetrics().density));
                        mTxtLogo.setText(R.string.connecting);

                    } else if (connectionState == ConnectionState.UPDATING) {
                        mTxtLogo.setTextSize((int) (mContext.getResources().getDimension(R.dimen.dp18) / mContext.getResources().getDisplayMetrics().density));

                        mTxtLogo.setText(R.string.updating);

                    } else if (connectionState == ConnectionState.IGAP) {
                        mTxtLogo.setTextSize((int) (mContext.getResources().getDimension(R.dimen.dp20) / mContext.getResources().getDisplayMetrics().density));
                        mTxtLogo.setText(defaultTitleText);

                    } else {
                        mTxtLogo.setTextSize((int) (mContext.getResources().getDimension(R.dimen.dp20) / mContext.getResources().getDisplayMetrics().density));
                        mTxtLogo.setText(defaultTitleText);
                    }

                    checkIGapFont();

                }

            }
        });

    }

    private void setNormalSizeToRootViews(View view) {

        if (!isBigCenterAvatarShown && !isSearchBoxShown) {
            view.findViewById(R.id.view_toolbar_main_constraint).setLayoutParams(
                    new ConstraintLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            mContext.getResources().getDimensionPixelSize(R.dimen.toolbar_height)
                    ));
            view.findViewById(R.id.view_toolbar_root_constraint).setLayoutParams(
                    new ConstraintLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT//mContext.getResources().getDimensionPixelSize(R.dimen.toolbar_height)
                    ));
        }

    }

    private void setIGapLogoVisibility(View view, boolean visible) {

        if (visible)
            mTxtLogo.setVisibility(View.VISIBLE);
        else
            mTxtLogo.setVisibility(View.GONE);

    }

    private void setSearchBoxVisibility(View view, boolean visible) {

        if (visible) {
            mSearchBox.setVisibility(View.VISIBLE);

            view.findViewById(R.id.view_toolbar_main_constraint).setLayoutParams(
                    new ConstraintLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            mContext.getResources().getDimensionPixelSize(R.dimen.toolbar_height_with_search)
                    ));
            view.findViewById(R.id.view_toolbar_root_constraint).setLayoutParams(
                    new ConstraintLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT//mContext.getResources().getDimensionPixelSize(R.dimen.toolbar_height_root_with_search)
                    ));

            mSearchBox.setOnClickListener(v ->{
                if (isShowEditTextForSearch) setSearchEditableMode(mTxtSearch.isShown());
                mToolbarListener.onSearchClickListener(v);
            });

            mBtnClearSearch.setOnClickListener(v ->{

                if (!mEdtSearch.getText().toString().trim().equals(""))
                    mEdtSearch.setText("");
                else if (isShowEditTextForSearch)
                    setSearchEditableMode(mTxtSearch.isShown());

                mToolbarListener.onBtnClearSearchClickListener(v);
            });

            mEdtSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    mToolbarListener.onSearchTextChangeListener(mEdtSearch , s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            if (G.isDarkTheme){
                mSearchBox.setBackground(mContext.getResources().getDrawable(R.drawable.shape_toolbar_search_box_dark));
              //  mEdtSearch.setTextColor(mContext.getResources().getColor(R.color.white));
             //   mEdtSearch.setHintTextColor(mContext.getResources().getColor(R.color.gray_f2));
            //    mTxtSearch.setTextColor(mContext.getResources().getColor(R.color.gray_f2));
            }
        } else {
            mSearchBox.setVisibility(View.GONE);

        }
    }

    private void setSearchEditableMode(boolean state) {

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

    private void setCallModeVisibility(View view, boolean visible) {

        if (visible) {
            mTxtCallStatus.setVisibility(View.VISIBLE);

            view.findViewById(R.id.view_toolbar_main_constraint).setLayoutParams(
                    new ConstraintLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            mContext.getResources().getDimensionPixelSize(R.dimen.toolbar_height_call_state)
                    ));
            view.findViewById(R.id.view_toolbar_root_constraint).setLayoutParams(
                    new ConstraintLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            mContext.getResources().getDimensionPixelSize(R.dimen.toolbar_height_call_state)
                    ));
        } else {
            mTxtCallStatus.setVisibility(View.GONE);

        }
    }

    private void setGroupProfileVisibility(View view, boolean visible) {
        if (visible) {
            groupAvatar.setVisibility(View.VISIBLE);
            groupName.setVisibility(View.VISIBLE);
            groupMemberCount.setVisibility(View.VISIBLE);
            view.findViewById(R.id.view_toolbar_main_constraint).setLayoutParams(
                    new ConstraintLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            mContext.getResources().getDimensionPixelSize(R.dimen.dp160)
                    ));
            view.findViewById(R.id.view_toolbar_root_constraint).setLayoutParams(
                    new ConstraintLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            mContext.getResources().getDimensionPixelSize(R.dimen.dp160)
                    ));
        } else {
            groupAvatar.setVisibility(View.GONE);
            groupName.setVisibility(View.GONE);
            groupMemberCount.setVisibility(View.GONE);
        }
    }

    private void setBigAvatarVisibility(View view, boolean isShown) {

        if (isShown) {

            view.findViewById(R.id.view_toolbar_main_constraint).setLayoutParams(
                    new ConstraintLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            mContext.getResources().getDimensionPixelSize(R.dimen.toolbar_height_with_avatar)
                    ));
            view.findViewById(R.id.view_toolbar_root_constraint).setLayoutParams(
                    new ConstraintLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            mContext.getResources().getDimensionPixelSize(R.dimen.toolbar_height_root_with_avatar)
                    ));
            mAvatarBig.setVisibility(View.VISIBLE);
            mTxtBigAvatarUserName.setVisibility(View.VISIBLE);
            mAvatarBig.setOnClickListener(v -> mToolbarListener.onBigAvatarClickListener(v));

        } else {
            mAvatarBig.setVisibility(View.GONE);
            mTxtBigAvatarUserName.setVisibility(View.GONE);

        }
    }

    private AppCompatTextView getImageView(View v, int id) {
        return v.findViewById(id);
    }

    private View getInflater(int resId) {
        return mInflater.inflate(resId, mViewGroup, isAttachToRoot);
    }

    private Bitmap getConvertToBitmap(int id) {

        Drawable drawable = mContext.getResources().getDrawable(id);

        Bitmap bitmap;

        bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;

    }

    private void initViews(View view) {

        mLeftBtn = getImageView(view, R.id.view_toolbar_btn_left);
        mRightBtn = getImageView(view, R.id.view_toolbar_btn_right_1);
        m2RightBtn = getImageView(view, R.id.view_toolbar_btn_right_2);
        m3RightBtn = getImageView(view, R.id.view_toolbar_btn_right_3);
        m4RightBtn = getImageView(view, R.id.view_toolbar_btn_right_4);

        mTxtCounter = view.findViewById(R.id.view_toolbar_txt_counter);
        mTxtChatUserName = view.findViewById(R.id.view_toolbar_chat_txt_userName);
        mChatVerifyIcon = view.findViewById(R.id.view_toolbar_chat_txt_verify);
        mChatMuteIcon = view.findViewById(R.id.view_toolbar_chat_txt_isMute);
        mTxtChatSeenStatus = view.findViewById(R.id.view_toolbar_chat_txt_seen_status);
        mTxtLogo = view.findViewById(R.id.view_toolbar_logo);
        mTxtBigAvatarUserName = view.findViewById(R.id.view_toolbar_txt_below_big_avatar_user_name);
        mTxtCallStatus = view.findViewById(R.id.view_toolbar_txt_call_status);
        mCloudChatIcon = view.findViewById(R.id.view_toolbar_user_cloud_avatar);

        mAvatarSmall = view.findViewById(R.id.view_toolbar_user_small_avatar);
        mAvatarChat = view.findViewById(R.id.view_toolbar_user_chat_avatar);
        mAvatarBig = view.findViewById(R.id.view_toolbar_img_big_avatar);

        mSearchBox = view.findViewById(R.id.view_toolbar_search_layout);
        mTxtSearch = view.findViewById(R.id.view_toolbar_search_layout_txt);
        mEdtSearch = view.findViewById(R.id.view_toolbar_search_layout_edt_input);
        mBtnClearSearch = view.findViewById(R.id.view_toolbar_search_layout_btn_clear);

        groupAvatar = view.findViewById(R.id.groupAvatar);
        groupName = view.findViewById(R.id.groupName);
        groupMemberCount = view.findViewById(R.id.groupMemberCount);

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

        */
    }

}
