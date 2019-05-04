package net.iGap.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.AppCompatEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.iGap.R;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.module.MaterialDesignTextView;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * SAMPLE OF USAGE ARE AT BOTTOM OF THIS FILE
 *   1 = create root view at the xml (LinearLayout)
 *   2 = get create object of this class and set details then call getView()
 *   3 = attach view to xml root toolbar view
 *   4 = implement toolbar listener based on your usage
 * */
public class HelperToolbar {

    private ImageView mLeftBtn, mRightBtn, m2RightBtn, m3RightBtn, m4RightBtn;
    private TextView mTxtLogo, mTxtCounter, mTxtBigAvatarUserName, mTxtCallStatus, mTxtChatUserName, mTxtChatSeenStatus;
    private CircleImageView mAvatarSmall, mAvatarBig, mAvatarChat;
    private RelativeLayout mSearchBox;
    private TextView mTxtSearch ;
    private EditText mEdtSearch ;

    private LayoutInflater mInflater;
    private Context mContext;
    private ViewGroup mViewGroup = null;
    private ToolbarListener mToolbarListener;

    private Bitmap mLeftIcon;
    private Bitmap[] mRightIcons = {null, null, null, null};
    private boolean isAttachToRoot;
    private boolean isSearchBoxShown;
    private boolean isLogoShown;
    private boolean isBigCenterAvatarShown;
    private boolean isRightSmallAvatarShown;
    private boolean isCounterShown;
    private boolean isInChatRoom;
    private boolean isCallModeEnable;
    private MaterialDesignTextView mBtnClearSearch;

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

    public HelperToolbar setRightIcons(Bitmap... bitmaps) {
        this.mRightIcons = bitmaps;
        return this;
    }

    public HelperToolbar setRightIcons(int... drawables) {

        for (int i = 0; i < drawables.length; i++) {
            mRightIcons[i] = getConvertToBitmap(drawables[i]);
        }
        return this;
    }

    public HelperToolbar setLeftIcon(Bitmap bitmap) {
        this.mLeftIcon = bitmap;
        return this;
    }

    public HelperToolbar setLeftIcon(int drawable) {
        this.mLeftIcon = getConvertToBitmap(drawable);
        return this;
    }

    public HelperToolbar setSearchBoxShown(boolean searchBoxShown) {
        this.isSearchBoxShown = searchBoxShown;
        return this;
    }

    public HelperToolbar setLogoShown(boolean logoShown) {
        this.isLogoShown = logoShown;
        return this;
    }

    public HelperToolbar setChatRoom(boolean isChatRoom) {
        this.isInChatRoom = isChatRoom;
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
        return this;
    }

    public HelperToolbar setCounterShown(boolean counterShown) {
        //for show number of msg selected in chat room
        this.isCounterShown = counterShown;
        return this;
    }

    public HelperToolbar setListener(ToolbarListener listener) {
        this.mToolbarListener = listener;
        return this;
    }

    public View getView() {

        if (mContext == null) throw new IllegalArgumentException("Context can not be null");

        View result = getInflater(R.layout.view_main_toolbar);
        setNormalSizeToRootViews(result);

        initViews(result);

        if (mLeftIcon != null) {
            mLeftBtn.setImageBitmap(mLeftIcon);
            mLeftBtn.setVisibility(View.VISIBLE);
            mLeftBtn.setOnClickListener(v -> mToolbarListener.onLeftIconClickListener(v));

        } else {
            mLeftBtn.setVisibility(View.GONE);
        }

        if (mRightIcons[0] != null) {
            mRightBtn.setImageBitmap(mRightIcons[0]);
            mRightBtn.setVisibility(View.VISIBLE);
            mRightBtn.setOnClickListener(v -> mToolbarListener.onRightIconClickListener(v));

        } else {
            mRightBtn.setVisibility(View.GONE);
        }

        if (mRightIcons[1] != null) {
            m2RightBtn.setImageBitmap(mRightIcons[1]);
            m2RightBtn.setVisibility(View.VISIBLE);
            m2RightBtn.setOnClickListener(v -> mToolbarListener.onSecondRightIconClickListener(v));

        } else {
            m2RightBtn.setVisibility(View.GONE);
        }

        if (mRightIcons[2] != null) {
            m3RightBtn.setImageBitmap(mRightIcons[2]);
            m3RightBtn.setVisibility(View.VISIBLE);
            m3RightBtn.setOnClickListener(v -> mToolbarListener.onThirdRightIconClickListener(v));

        } else {
            m3RightBtn.setVisibility(View.GONE);
        }

        if (mRightIcons[3] != null) {
            m4RightBtn.setImageBitmap(mRightIcons[3]);
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
            mAvatarSmall.setOnClickListener(v -> mToolbarListener.onSmallAvatarClickListener(v));

        } else {
            mAvatarSmall.setVisibility(View.VISIBLE);
        }

        if (isInChatRoom) {
            mAvatarChat.setVisibility(View.VISIBLE);
            mTxtChatUserName.setVisibility(View.VISIBLE);
            mTxtChatSeenStatus.setVisibility(View.VISIBLE);
            mAvatarChat.setOnClickListener(v -> mToolbarListener.onChatAvatarClickListener(v));

        } else {
            mAvatarChat.setVisibility(View.GONE);
            mTxtChatUserName.setVisibility(View.GONE);
            mTxtChatSeenStatus.setVisibility(View.GONE);
        }

        setBigAvatarVisibility(result, isBigCenterAvatarShown);

        setIGapLogoVisibility(result, isLogoShown);

        setSearchBoxVisibility(result, isSearchBoxShown);

        setCallModeVisibility(result, isCallModeEnable);

        return result;

    }

    public TextView getTextViewCounter() {
        return mTxtCounter;
    }

    public TextView getTextViewCallStatus() {
        return mTxtCallStatus;
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

    public MaterialDesignTextView getButtonClearSearch() {
        return mBtnClearSearch;
    }

    /*************************************************************/

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
                            mContext.getResources().getDimensionPixelSize(R.dimen.toolbar_height)
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
                            mContext.getResources().getDimensionPixelSize(R.dimen.toolbar_height_root_with_search)
                    ));

            mSearchBox.setOnClickListener(v -> mToolbarListener.onSearchClickListener(v));
            mBtnClearSearch.setOnClickListener(v -> mToolbarListener.onBtnClearSearchClickListener(v));

        } else {
            mSearchBox.setVisibility(View.GONE);

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

    private ImageView getImageView(View v, int id) {
        return ((ImageView) v.findViewById(id));
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
        mTxtChatSeenStatus = view.findViewById(R.id.view_toolbar_chat_txt_seen_status);
        mTxtLogo = view.findViewById(R.id.view_toolbar_logo);
        mTxtBigAvatarUserName = view.findViewById(R.id.view_toolbar_txt_below_big_avatar_user_name);
        mTxtCallStatus = view.findViewById(R.id.view_toolbar_txt_call_status);

        mAvatarSmall = view.findViewById(R.id.view_toolbar_user_small_avatar);
        mAvatarChat = view.findViewById(R.id.view_toolbar_user_chat_avatar);
        mAvatarBig = view.findViewById(R.id.view_toolbar_img_big_avatar);

        mSearchBox = view.findViewById(R.id.view_toolbar_search_layout);
        mTxtSearch = view.findViewById(R.id.view_toolbar_search_layout_txt);
        mEdtSearch = view.findViewById(R.id.view_toolbar_search_layout_edt_input);
        mBtnClearSearch  = view.findViewById(R.id.view_toolbar_search_layout_btn_clear);
    }

    /**
     * samples of using this helper based on new design 1398/2/10
     * 1 = create root view at the xml (LinearLayout)
     * 2 = get create object of this class and set details then call getView()
     * 3 = attach view to xml root toolbar view
     * 4 = implement toolbar listener based on your usage
     */
    private void sampleOfUsage(){

/*
        mToolbarLayout = findViewById(R.id.main_activity_toolbar);

        //chat view when user select items in chat room
        View toolbarView = new HelperToolbar()
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
