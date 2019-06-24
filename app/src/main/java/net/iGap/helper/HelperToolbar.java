package net.iGap.helper;

import android.arch.lifecycle.LifecycleOwner;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.widget.AppCompatTextView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
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
import net.iGap.activities.ActivityEnhanced;
import net.iGap.activities.ActivityMain;
import net.iGap.interfaces.ICallFinish;
import net.iGap.interfaces.OnComplete;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.libs.bottomNavigation.Util.Utils;
import net.iGap.module.CircleImageView;
import net.iGap.module.EmojiTextViewE;
import net.iGap.module.MaterialDesignTextView;
import net.iGap.module.MusicPlayer;
import net.iGap.module.enums.ConnectionState;
import net.iGap.viewmodel.ActivityCallViewModel;

import static android.support.constraint.ConstraintSet.BOTTOM;
import static android.support.constraint.ConstraintSet.END;
import static android.support.constraint.ConstraintSet.MATCH_CONSTRAINT;
import static android.support.constraint.ConstraintSet.PARENT_ID;
import static android.support.constraint.ConstraintSet.START;
import static android.support.constraint.ConstraintSet.TOP;
import static android.support.constraint.ConstraintSet.WRAP_CONTENT;
import static net.iGap.adapter.items.chat.ViewMaker.i_Dp;


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
    private EmojiTextViewE mTxtChatUserName;
    private CircleImageView mAvatarSmall, mAvatarBig, mAvatarChat, groupAvatar;
    private RelativeLayout mSearchBox;
    private TextView mTxtSearch;
    private AppCompatTextView groupName, groupMemberCount;
    public EditText mEdtSearch;
    private TextView mChatVerifyIcon;
    private TextView mChatMuteIcon;
    private CircleImageView mCloudChatIcon;
    private TextView mBtnClearSearch;

    private Context mContext;
    private ViewGroup mViewGroup = null;
    private ToolbarListener mToolbarListener;

    private int mLeftIcon = 0;
    private int[] mRightIcons = {0, 0, 0, 0};
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

    private HelperToolbar() {
    }

    public static HelperToolbar create() {
        return new HelperToolbar();
    }

    public HelperToolbar setContext(Context context) {
        this.mContext = context;
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

    public HelperToolbar setLeftIcon(@StringRes int icon) {
        this.mLeftIcon = icon;
        if (mLeftBtn != null) mLeftBtn.setText(mLeftIcon);
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

    public View getView() {

        if (mContext == null) throw new IllegalArgumentException("Context can not be null");

        //set default title name if user not set
        if (defaultTitleText == null || defaultTitleText.trim().equals("")) {
            defaultTitleText = mContext.getResources().getString(R.string.app_name);
        }

        typeFaceGenerator();

        ViewMaker viewMaker = new ViewMaker(mContext);
        rootView = viewMaker;
        rootView.setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        initViews(viewMaker);


        if (mLeftIcon != 0) {
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

        if (mSearchBox != null){
            setSearchBoxVisibility( isSearchBoxShown);
        }



        if (isMediaPlayerEnabled){
            setMusicPlayer(viewMaker , isInChatRoom);
        }


        if (mTxtLogo != null){
            toolBarTitleHandler();
            checkIGapFont();
        }
        return rootView;

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


    /*************************************************************/

    private void setMusicPlayer(ViewMaker view, boolean isChat) {

        LinearLayout musicLayout = (LinearLayout) view.getMusicLayout() ;

        LinearLayout stripCallLayout = (LinearLayout) view.getCallLayout();

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
            ActivityCall.stripLayoutChat = view.getCallLayout() ;

            ActivityCallViewModel.txtTimeChat = rootView.findViewById(R.id.cslcs_txt_timer);

            TextView txtCallActivityBack = rootView.findViewById(R.id.cslcs_btn_call_strip);
            txtCallActivityBack.setOnClickListener(v -> mContext.startActivity(new Intent(G.fragmentActivity, ActivityCall.class)));

            checkIsAvailableOnGoingCall();

        } else if (isSharedMedia) {

            MusicPlayer.shearedMediaLayout = musicLayout;

        } else {
            MusicPlayer.mainLayout = musicLayout;
            ActivityCall.stripLayoutMain = view.getCallLayout();


            ActivityCallViewModel.txtTimerMain = rootView.findViewById(R.id.cslcs_txt_timer);

            TextView txtCallActivityBack = rootView.findViewById(R.id.cslcs_btn_call_strip);
            txtCallActivityBack.setOnClickListener(v -> mContext.startActivity(new Intent(G.fragmentActivity, ActivityCall.class)));

        }

        MusicPlayer.setMusicPlayer(musicLayout);
        setMediaLayout();
        //setStripLayoutCall();

        try {
            G.callStripLayoutVisiblityListener.observe(G.fragmentActivity, isVisible -> {

                try {

                    if (isVisible) {
                        if (isChat) {
                            ActivityCall.stripLayoutChat.setVisibility(View.VISIBLE);


                        } else {
                            ActivityCall.stripLayoutMain.setVisibility(View.VISIBLE);

                        }


                        if (MusicPlayer.mainLayout != null) {
                            MusicPlayer.mainLayout.setVisibility(View.GONE);
                        }

                        if (MusicPlayer.chatLayout != null) {
                            MusicPlayer.chatLayout.setVisibility(View.GONE);
                        }
                    } else {
                        if (isChat) {
                            ActivityCall.stripLayoutChat.setVisibility(View.GONE);
                        } else {
                            ActivityCall.stripLayoutMain.setVisibility(View.GONE);
                        }
                    }

                } catch (Exception e) {
                }

            });
        } catch (Exception e) {

        }

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

        if (mTxtLogo.getText().toString().toLowerCase().equals("igap")) {
            Utils.setTextSize(mTxtLogo, R.dimen.toolbar_igap_icon_textSize);
            mTxtLogo.setTypeface(tfFontIcon);
            mTxtLogo.setText(mContext.getString(R.string.igap_en_icon));
        } else if (mTxtLogo.getText().toString().toLowerCase().equals("آیگپ")) {
            mTxtLogo.setTypeface(tfFontIcon);
            Utils.setTextSize(mTxtLogo, R.dimen.toolbar_igap_icon_textSize);
            mTxtLogo.setText(mContext.getString(R.string.igap_fa_icon));

        } else {
            mTxtLogo.setTypeface(tfMain);
        }
    }

    private void toolBarTitleHandler() {

        try {
            connectionStateChecker(G.fragmentActivity);
        } catch (Exception e) {
            try {
                connectionStateChecker(G.currentActivity);
            } catch (Exception e2) {

            }
        }

    }

    private void connectionStateChecker(LifecycleOwner owner) {

        G.connectionStateMutableLiveData.observe(owner, new android.arch.lifecycle.Observer<ConnectionState>() {
            @Override
            public void onChanged(@Nullable ConnectionState connectionState) {

                if (mTxtLogo != null && connectionState != null) {

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

                    checkIGapFont();

                }

            }
        });
    }

    private void setIGapLogoVisibility(boolean visible) {

        if (visible)
            mTxtLogo.setVisibility(View.VISIBLE);
        else
            mTxtLogo.setVisibility(View.GONE);

    }

    private void setSearchBoxVisibility( boolean visible) {

        if (visible && mSearchBox != null) {

            mSearchBox.setOnClickListener(v -> {
                if (isShowEditTextForSearch) setSearchEditableMode(mTxtSearch.isShown());
                mToolbarListener.onSearchClickListener(v);
            });

            mBtnClearSearch.setOnClickListener(v -> {

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
                    mToolbarListener.onSearchTextChangeListener(mEdtSearch, s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

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

    private void initViews(ViewMaker view) {

        mLeftBtn = view.getLeftIcon() ;
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

//        mAvatarSmall = view.findViewById(R.id.view_toolbar_user_small_avatar);

        mSearchBox = view.getSearchLayout();
        mTxtSearch = view.getTvSearch();
        mEdtSearch = view.getEdtSearch();
        mBtnClearSearch = view.getTvClearSearch();

        groupAvatar = view.getCivProfileAvatar();
        groupName = view.getTvProfileName();
        groupMemberCount = view.getTvProfileMemberCount();

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

        */
    }

    private void typeFaceGenerator() {

        if (tfFontIcon == null)
            tfFontIcon = Typeface.createFromAsset(mContext.getAssets(), "fonts/font_icon.ttf");

        if (tfMain == null)
            tfMain = Typeface.createFromAsset(mContext.getAssets(), "fonts/IRANSansMobile.ttf");


    }


    private Typeface tfFontIcon, tfMain;


    private class ViewMaker extends ConstraintLayout {

        private int VALUE_1DP;
        private int VALUE_4DP;
        private int VALUE_10DP;
        private boolean isDark;

        private View musicLayout ;
        private View callLayout ;
        private LinearLayout layoutMedia ;
        private ConstraintLayout mainConstraint ;
        private AppCompatTextView leftIcon = null;
        private AppCompatTextView rightIcon4 = null ;
        private AppCompatTextView rightIcon3 = null;
        private AppCompatTextView rightIcon2 = null;
        private AppCompatTextView rightIcon = null;
        private TextView logo ;
        private RelativeLayout searchLayout ;
        private TextView tvSearch ;
        private TextView tvClearSearch ;
        private EditText edtSearch ;
        private RelativeLayout rlChatAvatar ;
        private CircleImageView civAvatar ;
        private CircleImageView civCloud ;
        private LinearLayout layoutChatName ;
        private EmojiTextViewE tvChatName ;
        private TextView tvChatStatus ;
        private TextView iconChatVerify ;
        private TextView muteChatIcon ;
        private CircleImageView civProfileAvatar = null  ;
        private AppCompatTextView tvProfileName ;
        private AppCompatTextView tvProfileMemberCount ;

        public ViewMaker(Context ctx) {
            super(ctx);
            setupDefaults();
            init();
        }

        private void init() {

            ConstraintSet setRoot = new ConstraintSet();
            ConstraintSet set = new ConstraintSet();

            setLayoutDirection(LAYOUT_DIRECTION_LTR);

            //region media player and ongoing call
            //check and add media player cause of ui and this must be the below of main toolbar view
            if (isMediaPlayerEnabled){

                LayoutInflater inflater = LayoutInflater.from(getContext());

                //music player layout
                musicLayout = inflater.inflate(R.layout.music_layout_small , this , false);
                musicLayout.setId(R.id.view_toolbar_layout_player_music);
                setLayoutParams(musicLayout , LinearLayout.LayoutParams.MATCH_PARENT , LinearLayout.LayoutParams.WRAP_CONTENT , i_Dp(R.dimen.toolbar_search_box_size));
                musicLayout.setVisibility(VISIBLE);

                //online call view
                callLayout = inflater.inflate(R.layout.chat_sub_layout_strip_call , this , false);
                callLayout.setId(R.id.view_toolbar_layout_strip_call);
                setLayoutParams(callLayout , LinearLayout.LayoutParams.MATCH_PARENT , LinearLayout.LayoutParams.WRAP_CONTENT , i_Dp(R.dimen.toolbar_search_box_size));
                callLayout.setVisibility(GONE);

                //player root view for add , handle two above views
                layoutMedia = new LinearLayout(getContext());
                layoutMedia.setId(R.id.view_toolbar_player_layout);
                layoutMedia.setOrientation(LinearLayout.VERTICAL);
                addView(layoutMedia);

                if (isDark){
                    layoutMedia.setBackgroundResource(R.drawable.shape_toolbar_player_dark);
                }else{
                    layoutMedia.setBackgroundResource(R.drawable.shape_toolbar_player);
                }

                layoutMedia.addView(musicLayout);
                layoutMedia.addView(callLayout);

                setRoot.constrainHeight(layoutMedia.getId() , WRAP_CONTENT);
                setRoot.constrainWidth(layoutMedia.getId() , MATCH_CONSTRAINT);

                setRoot.connect(layoutMedia.getId() , START , PARENT_ID , START);
                setRoot.connect(layoutMedia.getId() , END , PARENT_ID , END);
                setRoot.connect(layoutMedia.getId() , TOP , PARENT_ID , TOP , i_Dp(R.dimen.margin_for_below_layouts_of_toolbar));
            }

            //endregion media player and ongoing call

            //region main constraint
            //contain : buttons , logo
            mainConstraint = new ConstraintLayout(getContext());
            mainConstraint.setId(R.id.view_toolbar_main_constraint);
            if (isDark)
                mainConstraint.setBackgroundResource(R.drawable.shape_toolbar_background_dark);
            else
                mainConstraint.setBackgroundResource(R.drawable.shape_toolbar_background);

            setRoot.constrainHeight(mainConstraint.getId(), i_Dp(R.dimen.toolbar_height));
            setRoot.constrainWidth(mainConstraint.getId(), MATCH_CONSTRAINT);
            setRoot.connect(mainConstraint.getId(), START, PARENT_ID, START);
            setRoot.connect(mainConstraint.getId(), END, PARENT_ID, END);
            setRoot.connect(mainConstraint.getId(), TOP, PARENT_ID, TOP);
            addView(mainConstraint);

            //endregion main constraint

            //region left button
            if (mLeftIcon != 0) {
                leftIcon = makeIcon(R.id.view_toolbar_btn_left, mLeftIcon);
                mainConstraint.addView(leftIcon);
                setIconViewSize(leftIcon, set);

                set.setMargin(leftIcon.getId(), START, VALUE_4DP);
                set.setMargin(leftIcon.getId(), TOP, VALUE_4DP);
                set.setMargin(leftIcon.getId(), BOTTOM, VALUE_10DP);

                set.connect(leftIcon.getId(), START, PARENT_ID, START);
                set.connect(leftIcon.getId(), TOP, PARENT_ID, TOP);
                set.connect(leftIcon.getId(), BOTTOM, PARENT_ID, BOTTOM);

            }

            //endregion left button

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

            //region logo
            if (isLogoShown) {

                logo = new TextView(getContext());

                logo.setId(R.id.view_toolbar_logo);
                logo.setText(R.string.app_name);
                logo.setGravity(Gravity.CENTER);
                Utils.setTextSize(logo, R.dimen.standardTextSize);
                logo.setTextColor(getContext().getResources().getColor(R.color.white));
                logo.setTypeface(tfFontIcon);
                InputFilter[] fArray = new InputFilter[1];
                fArray[0] = new InputFilter.LengthFilter(28);
                logo.setFilters(fArray);
                logo.setSingleLine(true);

                mainConstraint.addView(logo);

                set.constrainHeight(logo.getId(), WRAP_CONTENT);
                set.constrainWidth(logo.getId(), WRAP_CONTENT);
                set.connect(logo.getId(), START, PARENT_ID, START);
                set.connect(logo.getId(), END, PARENT_ID, END);
                set.connect(logo.getId(), TOP, PARENT_ID, TOP);
                set.connect(logo.getId(), BOTTOM, PARENT_ID, BOTTOM);

            }
            //endregion logo

            //region search box

            if (isSearchBoxShown) {
                searchLayout = new RelativeLayout(getContext());
                searchLayout.setGravity(Gravity.CENTER_VERTICAL);
                searchLayout.setId(R.id.view_toolbar_search_layout);

                setRoot.constrainHeight(searchLayout.getId(), i_Dp(R.dimen.toolbar_search_box_size));
                setRoot.constrainWidth(searchLayout.getId(), MATCH_CONSTRAINT);

                setRoot.setMargin(searchLayout.getId(), START, i_Dp(R.dimen.dp40));
                setRoot.setMargin(searchLayout.getId(), END, i_Dp(R.dimen.dp40));

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
                tvSearch.setTextColor(Utils.darkModeHandler(getContext()));
                Utils.setTextSize(tvSearch, R.dimen.standardTextSize);
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
                Utils.setTextSize(edtSearch, R.dimen.standardTextSize);
                setLayoutParams(edtSearch, i_Dp(R.dimen.dp20), 0, 0, i_Dp(R.dimen.dp20), i_Dp(R.dimen.dp32), i_Dp(R.dimen.dp32));
                searchLayout.addView(edtSearch);

                tvClearSearch = new TextView(getContext());
                tvClearSearch.setTypeface(tfFontIcon);
                tvClearSearch.setGravity(Gravity.CENTER);
                tvClearSearch.setText(R.string.close_icon);
                tvClearSearch.setVisibility(GONE);
                Utils.setTextSize(tvClearSearch, R.dimen.largeTextSize);
                RelativeLayout.LayoutParams lp = setLayoutParams(tvClearSearch, i_Dp(R.dimen.toolbar_search_box_size), i_Dp(R.dimen.toolbar_search_box_size), i_Dp(R.dimen.dp10), i_Dp(R.dimen.dp10), i_Dp(R.dimen.dp2));
                lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, searchLayout.getId());
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
            }
            //endregion search box

            //region chat

            if (isInChatRoom){

                //region avatar
                rlChatAvatar = new RelativeLayout(getContext());
                rlChatAvatar.setId(R.id.view_toolbar_user_chat_avatar_layout);

                set.constrainWidth(rlChatAvatar.getId() , i_Dp(R.dimen.toolbar_chat_avatar_size) );
                set.constrainHeight(rlChatAvatar.getId() , i_Dp(R.dimen.toolbar_chat_avatar_size) );
                set.connect(rlChatAvatar.getId() , TOP , PARENT_ID , TOP);
                set.connect(rlChatAvatar.getId() , BOTTOM , PARENT_ID , BOTTOM);
                set.setMargin(rlChatAvatar.getId() , END , i_Dp(R.dimen.dp8));
                if (leftIcon != null) {
                    set.connect(rlChatAvatar.getId(), START , leftIcon.getId() , END , i_Dp(R.dimen.dp8));
                }else {
                    set.connect(rlChatAvatar.getId(), START , PARENT_ID , END , i_Dp(R.dimen.dp8));
                }

                civAvatar = new CircleImageView(getContext());
                civCloud  = new CircleImageView(getContext());

                civAvatar.setId(R.id.view_toolbar_user_chat_avatar);
                civCloud.setId(R.id.view_toolbar_user_cloud_avatar);

                civCloud.setImageResource(R.drawable.ic_cloud_space_blue);

                setLayoutParams(civAvatar ,RelativeLayout.LayoutParams.MATCH_PARENT , RelativeLayout.LayoutParams.MATCH_PARENT , 0 , 0 , 0);
                setLayoutParams(civCloud ,RelativeLayout.LayoutParams.MATCH_PARENT , RelativeLayout.LayoutParams.MATCH_PARENT , 0 , 0 , 0);

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

                set.constrainWidth(layoutChatName.getId() , MATCH_CONSTRAINT);
                set.constrainHeight(layoutChatName.getId() , WRAP_CONTENT);

                set.connect(layoutChatName.getId() , START , rlChatAvatar.getId() , END , i_Dp(R.dimen.dp4));
                set.connect(layoutChatName.getId() , TOP , rlChatAvatar.getId() , TOP);
                if (rightIcon4 != null) {
                    set.connect(layoutChatName.getId() , END , rightIcon4.getId() , START , i_Dp(R.dimen.dp8));
                }else if (rightIcon3 != null){
                    set.connect(layoutChatName.getId() , END , rightIcon3.getId() , START , i_Dp(R.dimen.dp8));

                }else if (rightIcon2 != null){
                    set.connect(layoutChatName.getId() , END , rightIcon2.getId() , START , i_Dp(R.dimen.dp8));

                }else if (rightIcon != null){
                    set.connect(layoutChatName.getId() , END , rightIcon.getId() , START , i_Dp(R.dimen.dp8));

                } else {
                    set.connect(layoutChatName.getId() , END , PARENT_ID , END , i_Dp(R.dimen.dp8));
                }

                //chat name
                tvChatName = new EmojiTextViewE(getContext());
                tvChatName.setId(R.id.view_toolbar_chat_txt_userName);
                tvChatName.setTypeface(tfMain);
                tvChatName.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) getContext().getResources().getDimension(R.dimen.standardTextSize));
                tvChatName.setEmojiSize( (int) getContext().getResources().getDimension(R.dimen.standardTextSize));
                tvChatName.setSingleLine();
                tvChatName.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                tvChatName.setGravity(Gravity.LEFT);
                tvChatName.setTextColor(getContext().getResources().getColor(R.color.white));
                setLayoutParams(tvChatName , LinearLayout.LayoutParams.WRAP_CONTENT , LinearLayout.LayoutParams.WRAP_CONTENT);
                tvChatName.setMaxWidth(i_Dp(R.dimen.toolbar_txt_name_max_width));
                layoutChatName.addView(tvChatName);

                //verify icon
                iconChatVerify = makeIcon(R.id.view_toolbar_chat_txt_verify , R.string.verify_icon);
                iconChatVerify.setTextColor(getContext().getResources().getColor(R.color.verify_color));
                Utils.setTextSize(iconChatVerify , R.dimen.smallTextSize);
                iconChatVerify.setVisibility(GONE);
                iconChatVerify.setPadding(i_Dp(R.dimen.dp4) , 0 , 0 , 0);
                setLayoutParams(iconChatVerify , LinearLayout.LayoutParams.WRAP_CONTENT , LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutChatName.addView(iconChatVerify);

                //mute icon
                muteChatIcon = makeIcon(R.id.view_toolbar_chat_txt_isMute, R.string.mute_icon);
                Utils.setTextSize(muteChatIcon , R.dimen.smallTextSize);
                muteChatIcon.setVisibility(GONE);
                muteChatIcon.setPadding(i_Dp(R.dimen.dp4) , 0 , 0 , 0);
                setLayoutParams(muteChatIcon , LinearLayout.LayoutParams.WRAP_CONTENT , LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutChatName.addView(muteChatIcon);

                //status or member count
                tvChatStatus = new TextView(getContext());
                tvChatStatus.setId(R.id.view_toolbar_chat_txt_seen_status);
                tvChatStatus.setTypeface(tfMain);
                tvChatStatus.setTextColor(getContext().getResources().getColor(R.color.white));
                tvChatStatus.setSingleLine();
                tvChatStatus.setGravity(Gravity.LEFT);
                Utils.setTextSize(tvChatStatus , R.dimen.verySmallTextSize);
                mainConstraint.addView(tvChatStatus);

                set.constrainWidth(tvChatStatus.getId() , MATCH_CONSTRAINT);
                set.constrainHeight(tvChatStatus.getId() , WRAP_CONTENT);
                set.setMargin(tvChatStatus.getId() , TOP , i_Dp(R.dimen.dp6));

                set.connect(tvChatStatus.getId() , START , layoutChatName.getId() , START);
                set.connect(tvChatStatus.getId() , BOTTOM , rlChatAvatar.getId() , BOTTOM);
                set.connect(tvChatStatus.getId() , END , layoutChatName.getId() , END);

                //endregion chat titles

            }

            //endregion chat

            //region profile
            if (isGroupProfile){

                //extend root for adding avatar and titles
                setRoot.constrainHeight(mainConstraint.getId(), i_Dp(R.dimen.dp160));

                //profile big avatar
                civProfileAvatar = new CircleImageView(getContext());
                civProfileAvatar.setId(R.id.groupAvatar);
                //civProfileAvatar.setImageResource(R.drawable.ic_cloud_space_blue);
                mainConstraint.addView(civProfileAvatar);

                set.constrainHeight(civProfileAvatar.getId() , i_Dp(R.dimen.dp68));
                set.constrainWidth(civProfileAvatar.getId() , i_Dp(R.dimen.dp68));
                set.connect(civProfileAvatar.getId() , START , PARENT_ID , START , i_Dp(R.dimen.dp14));
                set.connect(civProfileAvatar.getId() , BOTTOM , PARENT_ID , BOTTOM , i_Dp(R.dimen.dp14));
                if (leftIcon != null) {
                    set.connect(civProfileAvatar.getId() , TOP , leftIcon.getId() , BOTTOM );
                }else {
                    set.connect(civProfileAvatar.getId() , TOP , PARENT_ID , TOP , i_Dp(R.dimen.dp52) );
                }

                //titles
                tvProfileName  = new AppCompatTextView(getContext());
                tvProfileName.setId(R.id.groupName);
                tvProfileName.setTypeface(tfMain);
                tvProfileName.setGravity(Gravity.LEFT);
                tvProfileName.setSingleLine();
                Utils.setTextSize(tvProfileName , R.dimen.largeTextSize);
                tvProfileName.setTextColor(getContext().getResources().getColor(R.color.white));
                mainConstraint.addView(tvProfileName);

                set.constrainWidth(tvProfileName.getId() , MATCH_CONSTRAINT);
                set.constrainHeight(tvProfileName.getId() , WRAP_CONTENT);

                set.connect(tvProfileName.getId() , TOP , civProfileAvatar.getId() , TOP);
                set.connect(tvProfileName.getId() , START , civProfileAvatar.getId() , END , i_Dp(R.dimen.dp16));
                set.connect(tvProfileName.getId() , END , PARENT_ID , END , i_Dp(R.dimen.dp16));

                tvProfileMemberCount  = new AppCompatTextView(getContext());
                tvProfileMemberCount.setId(R.id.groupMemberCount);
                tvProfileMemberCount.setTypeface(tfMain);
                tvProfileMemberCount.setGravity(Gravity.LEFT);
                tvProfileMemberCount.setSingleLine();
                Utils.setTextSize(tvProfileMemberCount , R.dimen.largeTextSize);
                tvProfileMemberCount.setTextColor(getContext().getResources().getColor(R.color.white));
                mainConstraint.addView(tvProfileMemberCount);

                set.constrainWidth(tvProfileMemberCount.getId() , MATCH_CONSTRAINT);
                set.constrainHeight(tvProfileMemberCount.getId() , WRAP_CONTENT);

                set.connect(tvProfileMemberCount.getId() , TOP , tvProfileName.getId() , BOTTOM );
                set.connect(tvProfileMemberCount.getId() , START , tvProfileName.getId() , START );
                set.connect(tvProfileMemberCount.getId() , BOTTOM , civProfileAvatar.getId() , BOTTOM );
                set.connect(tvProfileMemberCount.getId() , END , tvProfileName.getId() , END );

                if (leftIcon != null){
                    set.connect(leftIcon.getId(), BOTTOM, civProfileAvatar.getId() , TOP);
                }
                if (rightIcon != null){
                    set.connect(rightIcon.getId(), BOTTOM, civProfileAvatar.getId() , TOP);
                }
                if (logo != null){
                    set.connect(logo.getId(), BOTTOM, civProfileAvatar.getId() , TOP);
                }
            }
            //endregion profile

            setRoot.applyTo(this);
            set.applyTo(mainConstraint);
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

        private void setLayoutParams(View view, int width, int height , int marginTop) {

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width, height);
            lp.setMargins(0 , marginTop , 0 , 0);
            view.setLayoutParams(lp);
        }

        private void setupDefaults() {

            isDark = G.isDarkTheme;

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

        public EmojiTextViewE getTvChatName() {
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

        //endregion getters
    }


}
