package net.iGap.messenger.ui.fragments;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.BidiFormatter;
import android.text.TextUtils;
import android.util.LayoutDirection;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.zxing.integration.android.IntentIntegrator;

import net.iGap.BuildConfig;
import net.iGap.G;
import net.iGap.R;
import net.iGap.WebSocketClient;
import net.iGap.activities.ActivityEnhanced;
import net.iGap.activities.ActivityMain;
import net.iGap.activities.ActivityRegistration;
import net.iGap.fragments.BaseFragment;
import net.iGap.fragments.BottomNavigationFragment;
import net.iGap.fragments.FragmentChangePhoneNumber;
import net.iGap.fragments.FragmentEditImage;
import net.iGap.fragments.FragmentGallery;
import net.iGap.fragments.FragmentShowAvatars;
import net.iGap.fragments.RegisteredContactsFragment;
import net.iGap.helper.GoToChatActivity;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperImageBackColor;
import net.iGap.helper.HelperLogout;
import net.iGap.helper.HelperPermission;
import net.iGap.helper.HelperTracker;
import net.iGap.helper.HelperUrl;
import net.iGap.helper.ImageHelper;
import net.iGap.helper.LayoutCreator;
import net.iGap.helper.PermissionHelper;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.helper.avatar.ParamWithAvatarType;
import net.iGap.helper.upload.OnUploadListener;
import net.iGap.messenger.theme.Theme;
import net.iGap.messenger.theme.ThemeDescriptor;
import net.iGap.messenger.ui.cell.AccountCell;
import net.iGap.messenger.ui.cell.HeaderCell;
import net.iGap.messenger.ui.cell.IconCell;
import net.iGap.messenger.ui.cell.ShadowSectionCell;
import net.iGap.messenger.ui.cell.TextCell;
import net.iGap.messenger.ui.cell.TextDetailCell;
import net.iGap.messenger.ui.cell.TextInfoPrivacyCell;
import net.iGap.messenger.ui.components.IconView;
import net.iGap.model.AccountUser;
import net.iGap.model.PassCode;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AttachFile;
import net.iGap.module.CircleImageView;
import net.iGap.module.MusicPlayer;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.SUID;
import net.iGap.module.accountManager.AccountHelper;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.accountManager.DbManager;
import net.iGap.module.customView.RecyclerListView;
import net.iGap.module.dialog.account.AccountsDialog;
import net.iGap.module.enums.CallState;
import net.iGap.module.upload.UploadObject;
import net.iGap.module.upload.Uploader;
import net.iGap.observers.eventbus.EventManager;
import net.iGap.observers.interfaces.OnGeoGetConfiguration;
import net.iGap.observers.interfaces.OnGetPermission;
import net.iGap.observers.interfaces.OnUserAvatarResponse;
import net.iGap.observers.interfaces.OnUserIVandGetScore;
import net.iGap.observers.interfaces.OnUserInfoMyClient;
import net.iGap.observers.interfaces.OnUserProfileSetBioResponse;
import net.iGap.observers.interfaces.OnUserProfileSetEmailResponse;
import net.iGap.observers.interfaces.OnUserProfileSetGenderResponse;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoResponse;
import net.iGap.proto.ProtoUserIVandGetScore;
import net.iGap.realm.RealmAvatar;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmUserInfo;
import net.iGap.request.RequestChatGetRoom;
import net.iGap.request.RequestGeoGetConfiguration;
import net.iGap.request.RequestInfoUpdate;
import net.iGap.request.RequestUserAvatarAdd;
import net.iGap.request.RequestUserIVandGetScore;
import net.iGap.request.RequestUserProfileGetBio;
import net.iGap.request.RequestUserProfileGetEmail;
import net.iGap.request.RequestUserProfileGetGender;
import net.iGap.request.RequestUserProfileGetRepresentative;
import net.iGap.request.RequestUserProfileSetGender;
import net.iGap.viewmodel.controllers.CallManager;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static net.iGap.activities.ActivityMain.waitingForConfiguration;
import static net.iGap.fragments.FragmentEditImage.checkItemGalleryList;
import static net.iGap.messenger.ui.fragments.NearbyFragment.mapUrls;
import static net.iGap.observers.eventbus.EventManager.UPDATE_PHONE_NUMBER;

public class ProfileFragment extends BaseFragment implements FragmentEditImage.OnImageEdited, OnUserAvatarResponse, OnUserInfoMyClient, EventManager.EventDelegate {

    private final AvatarHandler avatarHandler = new AvatarHandler();
    private final MutableLiveData<String> userScore = new MutableLiveData<>();
    private final MutableLiveData<String> referralCode = new MutableLiveData<>("");
    public static final MutableLiveData<String> bio = new MutableLiveData<>();
    private final MutableLiveData<String> gender = new MutableLiveData<>();
    public static final MutableLiveData<String> email = new MutableLiveData<>();
    public MutableLiveData<Long> setUserAvatar = new MutableLiveData<>();
    private List<AccountUser> accountUsers = new ArrayList<>();

    private TopView topView;
    private RecyclerListView listView;
    private int expandHeight;
    private RealmUserInfo realmUserInfo;
    private FrameLayout avatarContainer;
    private CircleImageView avatarImageView;
    private TextView nameTextView;
    private TextView statusTextView;
    private IconCell iconCell;
    private IconView addAvatar;
    private AccountCell temporaryAccountCellContainer;
    private String totalScore = "";
    private String referralCodeValue = "";
    private String bioValue = "";
    private String genderValue = "";
    private String emailValue = "";
    private ListAdapter listAdapter;
    private boolean isMenuShow = false;
    private boolean isAddAccountShow = false;
    private long avatarId;
    private String avatarPath;
    private int accountIndex = 0;

    private int sectionHeaderRow;
    private int phoneNumberRow;
    private int nameRow;
    private int usernameRow;
    private int bioRow;
    private int referralCodeRow;
    private int emailRow;
    private int genderRow;
    private int logoutRow;
    private int sectionDividerRow;
    private int sectionOneHeaderRow;
    private int addAccountRow;
    private int sectionOneDividerRow;
    private int sectionTwoHeaderRow;
    private int scoreRow;
    private int cloudRow;
    private int sectionTwoDividerRow;
    private int sectionThreeHeaderRow;
    private int contactRow;
    private int notificationRow;
    private int privacyRow;
    private int dataRow;
    private int chatRow;
    private int devicesRow;
    private int languageRow;
    private int sectionThreeDividerRow;
    private int sectionFourHeaderRow;
    private int inviteRow;
    private int barcodeRow;
    private int locationRow;
    private int sectionFourDividerRow;
    private int sectionFiveHeaderRow;
    private int faqRow;
    private int updateRow;
    private int versionRow;
    private int rowCount;
    private MaterialDialog uploadingStateDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventManager.getInstance(AccountManager.selectedAccount).addObserver(EventManager.UPDATE_PHONE_NUMBER, this);
        EventManager.getInstance(AccountManager.selectedAccount).addObserver(EventManager.CALL_STATE_CHANGED, this);
        EventManager.getInstance(AccountManager.selectedAccount).addObserver(EventManager.ON_UPLOAD_SERVICE_STOPPED, this);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public View createView(Context context) {
        updateRows();
        expandHeight = LayoutCreator.dp(70);
        fragmentView = new FrameLayout(context) {
            private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

            @Override
            public boolean hasOverlappingRendering() {
                return false;
            }

            @Override
            public void onDraw(Canvas canvas) {
                paint.setColor(Theme.getColor(Theme.key_window_background));
                canvas.drawRect(listView.getLeft(), listView.getTop() + expandHeight, listView.getRight(), listView.getBottom(), paint);
            }
        };
        fragmentView.setBackgroundColor(Theme.getColor(Theme.key_window_background));
        fragmentView.setWillNotDraw(false);
        FrameLayout frameLayout = (FrameLayout) fragmentView;
        listView = new RecyclerListView(context) {
            @Override
            public boolean hasOverlappingRendering() {
                return false;
            }
        };
        listView.setVerticalScrollBarEnabled(false);
        listView.setAdapter(listAdapter = new ListAdapter());
        userScore.observe(getViewLifecycleOwner(), score -> {
            totalScore = score;
            if (listAdapter != null) {
                listAdapter.notifyItemChanged(scoreRow);
            }
        });
        referralCode.observe(getViewLifecycleOwner(), phoneNumber -> {
            referralCodeValue = phoneNumber;
            if (listAdapter != null) {
                listAdapter.notifyItemChanged(referralCodeRow);
            }
        });
        bio.observe(getViewLifecycleOwner(), bioStr -> {
            bioValue = bioStr;
            if (listAdapter != null) {
                listAdapter.notifyItemChanged(bioRow);
            }
        });
        gender.observe(getViewLifecycleOwner(), genderStr -> {
            genderValue = genderStr;
            if (listAdapter != null) {
                listAdapter.notifyItemChanged(genderRow);
            }
        });
        email.observe(getViewLifecycleOwner(), emailStr -> {
            emailValue = emailStr;
            if (listAdapter != null) {
                listAdapter.notifyItemChanged(emailRow);
            }
        });
        listView.setPadding(0, LayoutCreator.dp(70), 0, 0);
        listView.setClipToPadding(false);
        frameLayout.addView(listView);
        listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                onScrollChanged();
            }
        });
        listView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false) {
            @Override
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        });
        listView.setOnItemClickListener((view, position) -> {
            if (view instanceof TextDetailCell) {
                if (position == phoneNumberRow) {
                    replaceFragment(new FragmentChangePhoneNumber());
                } else if (position == nameRow) {
                    replaceFragment(new NameFragment());
                } else if (position == usernameRow) {
                    replaceFragment(new UserNameFragment());
                } else if (position == bioRow) {
                    replaceFragment(new BioFragment());
                } else if (position == referralCodeRow) {
                    if (referralCodeValue.equals("")) {
                        replaceFragment(new ReferralCodeFragment());
                    }
                } else if (position == emailRow) {
                    replaceFragment(new EmailFragment());
                } else if (position == genderRow) {
                    showGenderDialog();
                }
            } else if (view instanceof TextCell) {
                if (position == scoreRow) {
                    replaceFragment(new ScoreFragment());
                } else if (position == cloudRow) {
                    showMyCloud();
                } else if (position == contactRow) {
                    replaceFragment(new RegisteredContactsFragment());
                } else if (position == notificationRow) {
                    replaceFragment(new NotificationAndSoundFragment());
                } else if (position == privacyRow) {
                    replaceFragment(new PrivacyAndSecurityFragment());
                } else if (position == dataRow) {
                    replaceFragment(new DataStorageFragment());
                } else if (position == chatRow) {
                    replaceFragment(new ChatSettingFragment());
                } else if (position == devicesRow) {
                    if (getActivity() != null) {
                        new HelperFragment(getActivity().getSupportFragmentManager(), new ActiveSessionsFragment()).setReplace(false).load();
                    }
                } else if (position == languageRow) {
                    replaceFragment(new LanguageFragment());
                } else if (position == inviteRow) {
                    inviteFriends();
                } else if (position == barcodeRow) {
                    loginWithBarcodeScanner();
                } else if (position == locationRow) {
                    showUsersNearByMe();
                } else if (position == faqRow) {
                    HelperUrl.openBrowser(BuildConfig.FAQ_LINK);
                } else if (position == updateRow) {
                    updateApp();
                } else if (position == logoutRow) {
                    showDialogLogout();
                } else if (position == addAccountRow) {
                    addNewAccount();
                }
            } else if (view instanceof AccountCell) {
                AccountCell accountCell = (AccountCell) view;
                if (AccountManager.getInstance().getCurrentUser().getId() != accountCell.getUserId()) {

                    if (CallManager.getInstance().isUserInCall() || CallManager.getInstance().isCallAlive() || CallManager.getInstance().isRinging()) {
                        temporaryAccountCellContainer = accountCell;
                        new AccountsDialog().ShowMsgDialog(context);
                    } else if (G.context.getSharedPreferences(SHP_SETTING.FILE_NAME, Context.MODE_PRIVATE).getBoolean(SHP_SETTING.KEY_IS_UPLOAD_SERVICE_RUN, false)) {
                        temporaryAccountCellContainer = accountCell;
                        showProfileAlertDialog(context, getString(R.string.you_are_in_uploading_state));
                    } else {
                        new AccountHelper().changeAccount(accountCell.getUserId());
                        ((ActivityMain) getActivity()).updateUiForChangeAccount();
                    }

                }
            }
        });
        topView = new TopView(context);
        topView.setBackgroundColor(Theme.getColor(Theme.key_toolbar_background));
        frameLayout.addView(topView);
        changeListView();
        nameTextView = new TextView(context);
        nameTextView.setText(realmUserInfo.getUserInfo().getDisplayName());
        nameTextView.setTypeface(ResourcesCompat.getFont(context, R.font.main_font_bold));
        nameTextView.setSingleLine(true);
        nameTextView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        nameTextView.setMarqueeRepeatLimit(-1);
        nameTextView.setSingleLine(true);
        nameTextView.setSelected(true);
        nameTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        nameTextView.setTextColor(Theme.getColor(Theme.key_white));
        frameLayout.addView(nameTextView, LayoutCreator.createFrame(150, LayoutCreator.WRAP_CONTENT, Gravity.LEFT, 116, 10, 16, 0));
        statusTextView = new TextView(context);
        statusTextView.setSingleLine(true);
        statusTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
        statusTextView.setTextColor(Theme.getColor(Theme.key_white));
        statusTextView.setTypeface(ResourcesCompat.getFont(context, R.font.main_font_bold));
        statusTextView.setTextDirection(LayoutDirection.LTR);
        statusTextView.setGravity(View.TEXT_ALIGNMENT_TEXT_START);
        String phoneNumber = checkPersianNumber(realmUserInfo.getUserInfo().getPhoneNumber());
        statusTextView.setText(formatPhoneNumber(phoneNumber));
        frameLayout.addView(statusTextView, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, Gravity.LEFT, 116, 38, 0, 0));
        iconCell = new IconCell(context, () -> {
            isMenuShow = !isMenuShow;
            updateRows();
            listAdapter.notifyDataSetChanged();
            listView.scrollToPosition(0);
        });
        if (!isMenuShow) {
            iconCell.setIcon(getResources().getString(R.string.icon_chevron_Down));
        } else {
            iconCell.setIcon(getResources().getString(R.string.icon_chevron_up));
        }
        frameLayout.addView(iconCell, LayoutCreator.createFrame(56, 56, Gravity.RIGHT, 0, 30, 20, 0));
        avatarImageView = new CircleImageView(context);
        avatarImageView.setPivotX(0);
        avatarImageView.setPivotY(0);
        avatarImageView.setOnClickListener(v -> showAvatars());
        setUserAvatar.observe(getViewLifecycleOwner(), userId -> {
            if (userId != null) {
                avatarHandler.getAvatar(new ParamWithAvatarType(avatarImageView, userId).avatarType(AvatarHandler.AvatarType.USER).showMain());
            }
        });
        avatarContainer = new FrameLayout(context);
        avatarContainer.addView(avatarImageView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT));
        addAvatar = new IconView(context);
        addAvatar.setTextColor(Theme.getColor(Theme.key_light_gray));
        addAvatar.setBackground(Theme.createSimpleSelectorCircleDrawable(18, Theme.getColor(Theme.key_theme_color), Theme.getColor(Theme.key_opacity_theme_color)));
        addAvatar.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        addAvatar.setGravity(Gravity.CENTER);
        addAvatar.setSingleLine();
        addAvatar.setText(getString(R.string.icon_add));
        addAvatar.setOnClickListener(v -> showAvatarChangeTypeDialog());
        avatarContainer.addView(addAvatar, LayoutCreator.createFrame(18, 18, Gravity.BOTTOM | Gravity.RIGHT, 0, 0, 0, 5));
        frameLayout.addView(avatarContainer, LayoutCreator.createFrame(78, 78, Gravity.TOP | Gravity.LEFT, 24, 4, 0, 0));
        return fragmentView;
    }

    private void showProfileAlertDialog(Context context, String contentText) {
        uploadingStateDialog = new MaterialDialog.Builder(context)
                .content(contentText)
                .positiveText(context.getResources().getString(R.string.ok))
                .contentGravity(GravityEnum.CENTER)
                .onPositive((dialog, which) -> {
                    //This event is related to upload service that prevents crash when upload service is active and user change account.
                    //Cause dependency injection did not implemented in project, multi account can not be implement in service upload.
                    // TODO: 16/03/22 Add Di in project and refactor this section to implementing multi account in service upload
                    dialog.dismiss();
                }).show();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentShowAvatars.onComplete = (result, messageOne, MessageTow) -> {
            long mAvatarId = 0;
            if (messageOne != null && !messageOne.equals("")) {
                mAvatarId = Long.parseLong(messageOne);
            }
            long finalMAvatarId = mAvatarId;
            G.handler.post(new Runnable() {
                @Override
                public void run() {
                    avatarHandler.avatarDelete(new ParamWithAvatarType(avatarImageView, realmUserInfo.getUserId()).avatarType(AvatarHandler.AvatarType.USER), finalMAvatarId);
                }
            });
        };
        G.onUserAvatarResponse = this;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        requestReferralCode();
        requestUserScore();
        requestUserProfileGetGender();
        requestUserProfileGetEmail();
        requestUserProfileGetBio();
        realmUserInfo = DbManager.getInstance().doRealmTask(realm -> {
            return realm.where(RealmUserInfo.class).findFirst();
        });
        setUserAvatar.setValue(realmUserInfo.getUserId());
        for (int i = 0; i < AccountManager.getInstance().getUserAccountList().size(); i++) {
            if (AccountManager.getInstance().getUserAccountList().get(i).isAssigned() &&
                    AccountManager.getInstance().getCurrentUser().getId() != AccountManager.getInstance().getUserAccountList().get(i).getId()) {
                accountUsers.add(AccountManager.getInstance().getUserAccountList().get(i));
            }
        }
        super.onAttach(context);
    }

    @Override
    public View createToolBar(Context context) {
        return null;
    }

    @Override
    public void profileImageAdd(String path) {
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager().popBackStack(BottomNavigationFragment.class.getName(), 0);
        }
        long lastUploadedAvatarId = avatarId + 1L;
        Uploader.getInstance().upload(UploadObject.createForAvatar(lastUploadedAvatarId, path, null, ProtoGlobal.RoomMessageType.IMAGE, new OnUploadListener() {
            @Override
            public void onProgress(String id, int progress) {
            }

            @Override
            public void onFinish(String id, String token) {
                new RequestUserAvatarAdd().userAddAvatar(token);
            }

            @Override
            public void onError(String id) {
            }

        }));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (PassCode.getInstance().isPassCode()) {
            ActivityMain.isUseCamera = true;
        }
        if (FragmentEditImage.textImageList != null) {
            FragmentEditImage.textImageList.clear();
        }
        if (FragmentEditImage.itemGalleryList != null) {
            FragmentEditImage.itemGalleryList.clear();
        }
        if (requestCode == AttachFile.request_code_TAKE_PICTURE && resultCode == RESULT_OK) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                ImageHelper.correctRotateImage(AttachFile.mCurrentPhotoPath, true);
                FragmentEditImage.insertItemList(AttachFile.mCurrentPhotoPath, false);
            } else {
                avatarPath = G.imageFile.toString() + "_" + System.currentTimeMillis() + "_" + avatarId + ".jpg";
                ImageHelper.correctRotateImage(avatarPath, true);
                FragmentEditImage.insertItemList(avatarPath, false);
            }
            if (getActivity() != null) {
                FragmentEditImage fragmentEditImage = FragmentEditImage.newInstance(null, false, false, 0);
                fragmentEditImage.setOnProfileImageEdited(this);
                new HelperFragment(getActivity().getSupportFragmentManager(), fragmentEditImage).setReplace(false).load();
            }
        }
    }

    @Override
    public void onAvatarAdd(ProtoGlobal.Avatar avatar) {
        /**
         * if another account do this action we haven't avatar source and have
         *  to download avatars . for do this action call HelperAvatar.getAvatar
         */
        if (avatarPath == null) {
            setUserAvatar.setValue(realmUserInfo.getUserId());
        } else {
            avatarHandler.avatarAdd(realmUserInfo.getUserId(), avatarPath, avatar, avatarPath -> G.handler.post(() -> {
                if (checkValidationForRealm(realmUserInfo)) {
                    if (avatarPath == null || !new File(avatarPath).exists()) {
                        avatarImageView.setImageBitmap(HelperImageBackColor.drawAlphabetOnPicture((int) avatarImageView.getContext().getResources().getDimension(R.dimen.dp100), realmUserInfo.getUserInfo().getInitials(), realmUserInfo.getUserInfo().getColor()));
                    } else {
                        G.imageLoader.displayImage(AndroidUtils.suitablePath(avatarPath), avatarImageView);
                    }
                }
            }));
            avatarPath = null;
        }
    }

    @Override
    public void onAvatarAddTimeOut() {
    }

    @Override
    public void onAvatarError() {
    }

    @Override
    public void onUserInfoMyClient() {
        setUserAvatar.setValue(AccountManager.getInstance().getCurrentUser().getId());
    }

    @Override
    public void onUserInfoTimeOut() {

    }

    @Override
    public void onUserInfoError(int majorCode, int minorCode) {

    }

    @Override
    public List<ThemeDescriptor> getThemeDescriptor() {
        List<ThemeDescriptor> themeDescriptors = new ArrayList<>();
        themeDescriptors.add(new ThemeDescriptor(topView, ThemeDescriptor.FLAG_BACKGROUND, Theme.key_toolbar_background));
        themeDescriptors.add(new ThemeDescriptor(nameTextView, ThemeDescriptor.FLAG_TEXTCOLOR, Theme.key_white));
        themeDescriptors.add(new ThemeDescriptor(statusTextView, ThemeDescriptor.FLAG_TEXTCOLOR, Theme.key_white));
        themeDescriptors.add(new ThemeDescriptor(listView, ThemeDescriptor.FLAG_CELLBACKGROUNDCOLOR, Theme.key_window_background));
        themeDescriptors.add(new ThemeDescriptor(listView, ThemeDescriptor.FLAG_BACKGROUND, Theme.key_window_background));
        themeDescriptors.add(new ThemeDescriptor(listView, ThemeDescriptor.FLAG_TEXTCOLOR, Theme.key_default_text));
        themeDescriptors.add(new ThemeDescriptor(toolbar, ThemeDescriptor.FLAG_BACKGROUND, Theme.key_toolbar_background));
        return themeDescriptors;
    }

    @Override
    public void onResume() {
        super.onResume();
        checkViewState();
        isMenuShow = false;
        iconCell.setIcon(getResources().getString(R.string.icon_chevron_Down));
        iconCell.closeMenu();
        updateRows();
        listAdapter.notifyDataSetChanged();
        listView.scrollToPosition(0);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        checkViewState();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (uploadingStateDialog != null && uploadingStateDialog.isShowing()) {
            uploadingStateDialog.dismiss();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        EventManager.getInstance(AccountManager.selectedAccount).removeObserver(EventManager.UPDATE_PHONE_NUMBER, this);
        EventManager.getInstance(AccountManager.selectedAccount).removeObserver(EventManager.CALL_STATE_CHANGED, this);
    }

    private void showAvatars() {
        if (DbManager.getInstance().doRealmTask(realm -> {
            return realm.where(RealmAvatar.class).equalTo("ownerId", realmUserInfo.getUserId()).findFirst();
        }) != null) {
            if (getActivity() != null) {
                FragmentShowAvatars fragment = FragmentShowAvatars.newInstance(realmUserInfo.getUserId(), FragmentShowAvatars.From.setting);
                new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setReplace(false).load();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private String formatPhoneNumber(String phoneNumber) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < phoneNumber.length(); i++) {
            if (i == 2 || i == 5 || i == 7 || i == 9) {
                result.append(" ");
            }
            result.append(phoneNumber.charAt(i));
        }
        String text = "{0}";
        String phone = BidiFormatter.getInstance().unicodeWrap(result.toString());
        return MessageFormat.format(text, phone);
    }

    @SuppressLint("ResourceType")
    private void showDialogLogout() {
        new MaterialDialog.Builder(getActivity()).title(R.string.log_out).positiveText(R.string.B_ok).negativeText(R.string.B_cancel).content(R.string.content_log_out)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        logout();
                    }
                }).show();
    }

    private void logout() {
        new HelperLogout().logoutUserWithRequest(new HelperLogout.LogOutUserCallBack() {
            @Override
            public void onLogOut() {
                //ToDo: foxed it and remove G.handler
                G.handler.post(() -> {
                    boolean haveAnotherAccount = new AccountHelper().logoutAccount();
                    if (haveAnotherAccount) {
                        if (getActivity() instanceof ActivityMain) {
                            //ToDO: handel remove notification for logout account
                            ((ActivityMain) getActivity()).updateUiForChangeAccount();
                        }
                    } else {
                        if (getActivity() != null) {
                            try {
                                NotificationManager nMgr = (NotificationManager) getActivity().getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                                nMgr.cancelAll();
                            } catch (Exception e) {
                                e.getStackTrace();
                            }
                            if (MusicPlayer.mp != null && MusicPlayer.mp.isPlaying()) {
                                MusicPlayer.stopSound();
                                MusicPlayer.closeLayoutMediaPlayer();
                            }
                            WebSocketClient.getInstance().connect(true);
                            startActivity(new Intent(getActivity(), ActivityRegistration.class));
                            getActivity().finish();
                        }
                    }
                });
            }

            @Override
            public void onError() {
            }
        });
    }

    private void addNewAccount() {
        if (getActivity() != null) {
            HelperTracker.sendTracker(HelperTracker.TRACKER_ADD_NEW_ACCOUNT);
            new AccountHelper().addAccount();
            Intent intent = new Intent(getActivity(), ActivityRegistration.class);
            intent.putExtra("add account", true);
            getActivity().startActivity(intent);
            getActivity().finish();
        }
    }

    private void showAvatarChangeTypeDialog() {
        new MaterialDialog.Builder(getActivity())
                .title(R.string.choose_picture)
                .negativeText(R.string.B_cancel)
                .items(R.array.profile)
                .negativeColor(Theme.getColor(Theme.key_button_background))
                .positiveColor(Theme.getColor(Theme.key_button_background))
                .choiceWidgetColor(ColorStateList.valueOf(Theme.getColor(Theme.key_button_background)))
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(final MaterialDialog dialog, View view, int which, CharSequence text) {
                        if (text.toString().equals(getString(R.string.array_From_Camera))) {
                            if (new PermissionHelper(getActivity(), ProfileFragment.this).grantCameraAndStoreagePermission()) {
                                usingCameraForChangingAvatar();
                            }
                        } else {
                            if (new PermissionHelper(getActivity(), ProfileFragment.this).grantReadAndRightStoragePermission()) {
                                selectAvatarFromGallery();
                            }
                        }
                        dialog.dismiss();
                    }
                }).show();
    }

    private void selectAvatarFromGallery() {
        if (getActivity() != null) {
            Fragment fragment = FragmentGallery.newInstance(false, FragmentGallery.GalleryMode.PHOTO, true, getString(R.string.gallery), "-1", new FragmentGallery.GalleryFragmentListener() {
                @Override
                public void openOsGallery() {
                }

                @Override
                public void onGalleryResult(String path) {
                    if (getActivity() != null) {
                        checkItemGalleryList();
                        FragmentEditImage.insertItemList(path, false);
                        FragmentEditImage fragmentEditImage = FragmentEditImage.newInstance(null, false, false, 0);
                        fragmentEditImage.setOnProfileImageEdited(ProfileFragment.this::profileImageAdd);
                        new HelperFragment(getActivity().getSupportFragmentManager(), fragmentEditImage).setReplace(false).load();
                    }
                }
            });
            new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setReplace(false).load();
        }
    }

    private void usingCameraForChangingAvatar() {
        if (getActivity() != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                new AttachFile(getActivity()).dispatchTakePictureIntent(ProfileFragment.this);
            } else {
                if (getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    avatarId = SUID.id().get();
                    avatarPath = G.imageFile.toString() + "_" + System.currentTimeMillis() + "_" + avatarId + ".jpg";
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(avatarPath)));
                    startActivityForResult(intent, AttachFile.request_code_TAKE_PICTURE);
                } else {
                    Toast.makeText(getContext(), R.string.please_check_your_camera, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void updateRows() {
        accountIndex = 0;
        rowCount = 0;
        sectionHeaderRow = isMenuShow ? rowCount++ : -1;
        phoneNumberRow = isMenuShow ? rowCount++ : -1;
        nameRow = isMenuShow ? rowCount++ : -1;
        usernameRow = isMenuShow ? rowCount++ : -1;
        bioRow = isMenuShow ? rowCount++ : -1;
        referralCodeRow = isMenuShow ? rowCount++ : -1;
        emailRow = isMenuShow ? rowCount++ : -1;
        genderRow = isMenuShow ? rowCount++ : -1;
        logoutRow = isMenuShow ? rowCount++ : -1;
        sectionDividerRow = isMenuShow ? rowCount++ : -1;
        sectionOneHeaderRow = rowCount++;
        isAddAccountShow = accountUsers.size() < 2;
        addAccountRow = isAddAccountShow ? rowCount++ : -1;
        rowCount += accountUsers.size();
        sectionOneDividerRow = rowCount++;
        sectionTwoHeaderRow = rowCount++;
        scoreRow = rowCount++;
        cloudRow = rowCount++;
        sectionTwoDividerRow = rowCount++;
        sectionThreeHeaderRow = rowCount++;
        contactRow = rowCount++;
        notificationRow = rowCount++;
        privacyRow = rowCount++;
        dataRow = rowCount++;
        chatRow = rowCount++;
        devicesRow = rowCount++;
        languageRow = rowCount++;
        sectionThreeDividerRow = rowCount++;
        sectionFourHeaderRow = rowCount++;
        inviteRow = rowCount++;
        barcodeRow = rowCount++;
        locationRow = rowCount++;
        sectionFourDividerRow = rowCount++;
        sectionFiveHeaderRow = rowCount++;
        faqRow = rowCount++;
        updateRow = rowCount++;
        versionRow = rowCount++;
    }

    private void showGenderDialog() {
        if (getContext() != null) {
            new MaterialDialog.Builder(getContext())
                    .backgroundColor(Theme.getColor(Theme.key_popup_background))
                    .title(R.string.gender)
                    .titleGravity(GravityEnum.START)
                    .titleColor(getResources().getColor(android.R.color.black))
                    .items(R.array.gender)
                    .negativeColor(Theme.getColor(Theme.key_button_background))
                    .positiveColor(Theme.getColor(Theme.key_button_background))
                    .choiceWidgetColor(ColorStateList.valueOf(Theme.getColor(Theme.key_button_background)))
                    .itemsCallbackSingleChoice(realmUserInfo.getGender().name().equals("FEMALE") ? 0 : 1, (dialog, itemView, which, text) -> false).positiveText(R.string.B_ok)
                    .negativeText(R.string.B_cancel)
                    .onPositive((dialog, which) -> {
                        String genderStr = dialog.getSelectedIndex() == 0 ? "FEMALE" : "MALE";
                        gender.postValue(genderStr);
                        requestSetGender(genderStr);
                    }).show();
        }
    }

    private void updateApp() {
        new RequestInfoUpdate().infoUpdate(BuildConfig.VERSION_CODE, new RequestInfoUpdate.updateInfoCallback() {
            @Override
            public void onSuccess(int lastVersion, String Body) {
                G.runOnUiThread(() -> {
                    MaterialDialog.Builder materialDialog = new MaterialDialog.Builder(getActivity())
                            .cancelable(false)
                            .title(R.string.app_version_change_log)
                            .titleGravity(GravityEnum.CENTER)
                            .titleColor(Theme.getColor(Theme.key_title_text))
                            .contentGravity(GravityEnum.CENTER);
                    if (lastVersion <= BuildConfig.VERSION_CODE) {
                        materialDialog.content(R.string.updated_version_title)
                                .backgroundColor(Theme.getColor(Theme.key_popup_background))
                                .positiveText(R.string.ok)
                                .itemsGravity(GravityEnum.START);
                    } else {
                        materialDialog.content(HtmlCompat.fromHtml(Body, HtmlCompat.FROM_HTML_MODE_LEGACY).toString())
                                .backgroundColor(Theme.getColor(Theme.key_popup_background))
                                .positiveText(R.string.startUpdate)
                                .itemsGravity(GravityEnum.START)
                                .onPositive((dialog, which) -> {
                                    String url = BuildConfig.UPDATE_LINK;
                                    Intent i = new Intent(Intent.ACTION_VIEW);
                                    i.setData(Uri.parse(url));
                                    startActivity(i);
                                })
                                .negativeText(R.string.cancel);
                    }
                    materialDialog.show();
                });
            }

            @Override
            public void onError(int major, int minor) {
            }
        });
    }

    private void showUsersNearByMe() {
        try {
            HelperPermission.getLocationPermission(getActivity(), new OnGetPermission() {
                @Override
                public void Allow() {
                    if (!waitingForConfiguration) {
                        waitingForConfiguration = true;
                        if (mapUrls == null || mapUrls.isEmpty() || mapUrls.size() == 0) {
                            G.onGeoGetConfiguration = new OnGeoGetConfiguration() {
                                @Override
                                public void onGetConfiguration() {
                                    G.handler.postDelayed(() -> waitingForConfiguration = false, 2000);
                                    G.handler.post(() -> new HelperFragment(getActivity().getSupportFragmentManager(), new NearbyFragment()).setReplace(false).load());
                                }

                                @Override
                                public void getConfigurationTimeOut() {
                                    G.handler.postDelayed(() -> waitingForConfiguration = false, 2000);
                                }
                            };
                            new RequestGeoGetConfiguration().getConfiguration();
                        } else {
                            G.handler.postDelayed(() -> waitingForConfiguration = false, 2000);
                            new HelperFragment(getActivity().getSupportFragmentManager(), new NearbyFragment()).setReplace(false).load();
                        }
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

    private void loginWithBarcodeScanner() {
        if (getActivity() instanceof ActivityEnhanced) {
            try {
                HelperPermission.getCameraPermission(getActivity(), new OnGetPermission() {
                    @Override
                    public void Allow() throws IllegalStateException {
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
        }
    }

    private void inviteFriends() {
        String link = "Hey Join iGap : https://www.igap.net I'm waiting for you!";
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, link);
        intent.setType("text/plain");
        Intent openInChooser = Intent.createChooser(intent, "Open in...");
        startActivity(openInChooser);
    }

    private void showMyCloud() {
        RealmRoom realmRoom = DbManager.getInstance().doRealmTask(realm -> {
            return realm.where(RealmRoom.class).equalTo("chatRoom.peer_id", realmUserInfo.getUserId()).findFirst();
        });
        if (realmRoom != null) {
            new GoToChatActivity(realmRoom.getId()).startActivity(getActivity());
        } else {
            new RequestChatGetRoom().chatGetRoom(realmUserInfo.getUserId(), new RequestChatGetRoom.OnChatRoomReady() {
                @Override
                public void onReady(ProtoGlobal.Room room) {
                    RealmRoom.putOrUpdate(room);
                    G.handler.post(() -> {
                        G.refreshRealmUi();
                        new GoToChatActivity(room.getId()).startActivity(getActivity());
                    });
                }

                @Override
                public void onError(int majorCode, int minorCode) {
                }
            });
        }
    }

    private void checkViewState() {
        fragmentView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (fragmentView != null) {
                    onScrollChanged();
                    changeListView();
                    fragmentView.getViewTreeObserver().removeOnPreDrawListener(this);
                }
                return true;
            }
        });
    }

    private void onScrollChanged() {
        View child = listView.getChildAt(0);
        RecyclerListView.Holder holder = (RecyclerListView.Holder) listView.findContainingViewHolder(child);
        int firstViewTop = child.getTop();
        int scrolledTop = 0;
        if (firstViewTop >= 0 && holder != null && holder.getAdapterPosition() == 0) {
            scrolledTop = firstViewTop;
        }
        if (expandHeight != scrolledTop) {
            expandHeight = scrolledTop;
            topView.invalidate();
            changeListView();
        }
        if (firstViewTop > 255) {
            topView.invalidateForce();
        }
    }

    private void changeListView() {
        int topMargin = LayoutCreator.dp(60);
        if (listView != null) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) listView.getLayoutParams();
            if (layoutParams.topMargin != topMargin) {
                layoutParams.topMargin = topMargin;
                listView.setLayoutParams(layoutParams);
            }
        }
        float average = expandHeight / (float) LayoutCreator.dp(44);
        if (avatarContainer != null && nameTextView != null) {
            float avatarY = (1.0f + average) * AndroidUtils.density + 37 * AndroidUtils.density * average;
            avatarContainer.setScaleX((82 + 18 * average) / 82.0f);
            avatarContainer.setScaleY((82 + 18 * average) / 82.0f);
            avatarContainer.setTranslationX(LayoutCreator.dp(10) * average);
            avatarContainer.setTranslationY((float) Math.ceil(avatarY));
            nameTextView.setTranslationX(18 * AndroidUtils.density * average);
            nameTextView.setTranslationY((float) Math.floor(avatarY) - (float) Math.ceil(AndroidUtils.density) + (float) Math.floor(AndroidUtils.density * average));
            float scale = 1.0f + 0.07f * average;
            nameTextView.setScaleX(scale);
            nameTextView.setScaleY(scale);
            statusTextView.setTranslationX(18 * AndroidUtils.density * average);
            statusTextView.setTranslationY((float) Math.floor(avatarY) - (float) Math.ceil(AndroidUtils.density) + (float) Math.floor(2 * AndroidUtils.density * average));
            statusTextView.setScaleX(scale);
            statusTextView.setScaleY(scale);
            iconCell.setScaleX(scale);
            iconCell.setScaleY(scale);
            iconCell.setTranslationY((float) Math.floor(avatarY) - (float) Math.ceil(AndroidUtils.density) + (float) Math.floor(2 * AndroidUtils.density * average));
            addAvatar.setVisibility(View.VISIBLE);
            if (average == 0) {
                addAvatar.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void requestUserScore() {
        new RequestUserIVandGetScore().userIVandGetScore(new OnUserIVandGetScore() {
            @Override
            public void getScore(ProtoUserIVandGetScore.UserIVandGetScoreResponse.Builder score) {
                userScore.postValue(String.valueOf(score.getScore()));
            }

            @Override
            public void onError(int major, int minor) {
                userScore.postValue(checkPersianNumber("-1"));
            }
        });
    }

    private void requestUserProfileGetBio() {
        new RequestUserProfileGetBio().getBio(new OnUserProfileSetBioResponse() {
            @Override
            public void onUserProfileBioResponse(String bioStr) {
                bio.postValue(bioStr);
            }

            @Override
            public void error(int majorCode, int minorCode) {

            }

            @Override
            public void timeOut() {

            }
        });
    }

    private void requestReferralCode() {
        new RequestUserProfileGetRepresentative().userProfileGetRepresentative(new RequestUserProfileGetRepresentative.OnRepresentReady() {
            @Override
            public void onRepresent(String phoneNumber) {
                referralCode.postValue(phoneNumber);
            }

            @Override
            public void onFailed() {
                referralCode.postValue("");
            }
        });
    }

    private void requestUserProfileGetEmail() {
        new RequestUserProfileGetEmail().userProfileGetEmail(new OnUserProfileSetEmailResponse() {
            @Override
            public void onUserProfileEmailResponse(String emailStr, ProtoResponse.Response response) {
                email.postValue(emailStr);
            }

            @Override
            public void Error(int majorCode, int minorCode) {

            }

            @Override
            public void onTimeOut() {

            }
        });
    }

    private void requestUserProfileGetGender() {
        new RequestUserProfileGetGender().userProfileGetGender(new OnUserProfileSetGenderResponse() {
            @Override
            public void onUserProfileGenderResponse(ProtoGlobal.Gender genderStr, ProtoResponse.Response response) {
                gender.postValue(genderStr.name());
            }

            @Override
            public void Error(int majorCode, int minorCode) {

            }

            @Override
            public void onTimeOut() {

            }
        });
    }

    private void requestSetGender(String gender) {
        new RequestUserProfileSetGender().setUserProfileGender(gender.equals("FEMALE") ? ProtoGlobal.Gender.FEMALE : ProtoGlobal.Gender.MALE, new OnUserProfileSetGenderResponse() {
            @Override
            public void onUserProfileGenderResponse(final ProtoGlobal.Gender gender, ProtoResponse.Response response) {
                listAdapter.notifyItemChanged(genderRow);
            }

            @Override
            public void Error(int majorCode, int minorCode) {
                listAdapter.notifyItemChanged(genderRow);
            }

            @Override
            public void onTimeOut() {
                listAdapter.notifyItemChanged(genderRow);
            }
        });
    }

    private String checkPersianNumber(String text) {
        if (HelperCalander.isPersianUnicode) {
            return HelperCalander.convertToUnicodeFarsiNumber(text);
        } else {
            return text;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void receivedEvent(int id, int account, Object... args) {
        if (id == UPDATE_PHONE_NUMBER) {
            String phoneNumber = checkPersianNumber(realmUserInfo.getUserInfo().getPhoneNumber());
            statusTextView.setText(formatPhoneNumber(phoneNumber));
            listAdapter.notifyItemChanged(phoneNumberRow);
        }
        if (id == EventManager.CALL_STATE_CHANGED) {
            G.runOnUiThread(() -> {
                if (temporaryAccountCellContainer != null && CallManager.getInstance().getCurrentSate() == CallState.LEAVE_CALL || CallManager.getInstance().getCurrentSate() == CallState.REJECT) {
                    new AccountHelper().changeAccount(temporaryAccountCellContainer.getUserId());
                    ((ActivityMain) getActivity()).updateUiForChangeAccount();
                }
            });

        }
    }

    private class TopView extends View {
        private final Paint paint;
        private final Drawable drawable;
        public int viewAlpha = 0;
        private int currentColor;

        public TopView(Context context) {
            super(context);
            paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setAlpha(viewAlpha);
            drawable = getResources().getDrawable(R.drawable.background_bot_inline);
        }

        private void invalidateForce() {
            viewAlpha = 0;
            invalidate();
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), LayoutCreator.dp(155));
        }

        @Override
        public void setBackgroundColor(int color) {
            if (color != currentColor) {
                paint.setColor(currentColor = color);
                invalidate();
            }
            super.setBackgroundColor(0);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            int height = getMeasuredHeight() - LayoutCreator.dp(95);
            if (expandHeight >= 0 && expandHeight <= 255) {
                viewAlpha = 255 - expandHeight;
            }
            drawable.setBounds(0, (listView.getTop() + expandHeight) - getMeasuredHeight(), getMeasuredWidth(), listView.getTop() + expandHeight);
            drawable.draw(canvas);
            paint.setAlpha(255);
            canvas.drawRect(0, 0, getMeasuredWidth(), height + expandHeight, paint);
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = null;
            switch (viewType) {
                case 0:
                    view = new HeaderCell(context, Theme.key_title_text, 23, 24, false);
                    break;
                case 1:
                    TextDetailCell textDetailCell = new TextDetailCell(context);
                    textDetailCell.setContentDescriptionValueFirst(true);
                    view = textDetailCell;
                    break;
                case 2:
                    view = new ShadowSectionCell(context, 12, Theme.getColor(Theme.key_line));
                    break;
                case 3:
                    view = new TextCell(context);
                    break;
                case 4:
                    TextInfoPrivacyCell textInfoPrivacyCell = new TextInfoPrivacyCell(context, 10);
                    textInfoPrivacyCell.getTextView().setGravity(Gravity.CENTER_HORIZONTAL);
                    textInfoPrivacyCell.getTextView().setTextColor(Theme.getColor(Theme.key_default_text));
                    textInfoPrivacyCell.getTextView().setMovementMethod(null);
                    textInfoPrivacyCell.setText(getResources().getString(R.string.app_version) + "  " + BuildConfig.VERSION_NAME);
                    textInfoPrivacyCell.getTextView().setPadding(0, LayoutCreator.dp(14), 0, LayoutCreator.dp(14));
                    view = textInfoPrivacyCell;
                    view.setBackground(Theme.getThemedDrawable(context, R.drawable.greydivider_bottom, Theme.key_line));
                    view.setBackgroundColor(Theme.getColor(Theme.key_line));
                    break;
                case 5:
                    AccountCell accountCell = new AccountCell(context);
                    view = accountCell;
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            int viewType = holder.getItemViewType();
            switch (viewType) {
                case 0:
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    if (position == sectionHeaderRow) {
                        headerCell.setText(getString(R.string.view_profile));
                    } else if (position == sectionOneHeaderRow) {
                        headerCell.setText(getString(R.string.account));
                    } else if (position == sectionTwoHeaderRow) {
                        headerCell.setText(getString(R.string.score_icloud));
                    } else if (position == sectionThreeHeaderRow) {
                        headerCell.setText(getString(R.string.settings));
                    } else if (position == sectionFourHeaderRow) {
                        headerCell.setText(getString(R.string.iGap_features));
                    } else if (position == sectionFiveHeaderRow) {
                        headerCell.setText(getString(R.string.help));
                    }
                    break;
                case 1:
                    TextDetailCell textDetailCell = (TextDetailCell) holder.itemView;
                    textDetailCell.setTextColor(Theme.getColor(Theme.key_default_text));
                    if (position == phoneNumberRow) {
                        String text;
                        if (!realmUserInfo.getUserInfo().getPhoneNumber().isEmpty()) {
                            text = checkPersianNumber(realmUserInfo.getUserInfo().getPhoneNumber());
                            textDetailCell.setTextAndValue(text, context.getString(R.string.changePhoneNumber), true);
                        }
                    } else if (position == nameRow) {
                        String text;
                        if (realmUserInfo.getUserInfo().getId() != 0) {
                            text = realmUserInfo.getUserInfo().getDisplayName();
                            textDetailCell.setTextAndValue(text, context.getString(R.string.name), true);
                        }
                    } else if (position == usernameRow) {
                        String text;
                        if (realmUserInfo.getUserInfo().getId() != 0) {
                            text = realmUserInfo.getUserInfo().getUsername();
                            if (!TextUtils.isEmpty(text)) {
                                text = "@" + text;
                            } else {
                                text = "-";
                            }
                            textDetailCell.setTextAndValue(text, context.getString(R.string.Username), true);
                        }
                    } else if (position == bioRow) {
                        if (!TextUtils.isEmpty(bioValue)) {
                            textDetailCell.setTextWithEmojiAndValue(bioValue, context.getString(R.string.UserBio), isMenuShow);
                            textDetailCell.setContentDescriptionValueFirst(true);
                        } else {
                            textDetailCell.setTextAndValue(context.getString(R.string.UserBio), context.getString(R.string.UserBioDetail), true);
                            textDetailCell.setContentDescriptionValueFirst(false);
                        }
                    } else if (position == referralCodeRow) {
                        if (referralCodeValue.equals("")) {
                            textDetailCell.setTextAndValue(context.getString(R.string.referral_code), context.getString(R.string.set_referral_code), true);
                        } else {
                            textDetailCell.setTextAndValue(checkPersianNumber(referralCodeValue), context.getString(R.string.referral_code), true);
                        }
                    } else if (position == emailRow) {
                        if (!TextUtils.isEmpty(emailValue)) {
                            textDetailCell.setTextAndValue(emailValue, context.getString(R.string.email), true);
                        } else {
                            textDetailCell.setTextAndValue(context.getString(R.string.email), context.getString(R.string.set_Email), true);
                        }
                    } else if (position == genderRow) {
                        String text = genderValue.equals("FEMALE") ? context.getString(R.string.female) : context.getString(R.string.male);
                        if (!TextUtils.isEmpty(text)) {
                            textDetailCell.setTextAndValue(text, context.getString(R.string.set_gender), true);
                        } else {
                            textDetailCell.setTextAndValue(context.getString(R.string.gender), context.getString(R.string.set_gender), false);
                        }
                    }
                    break;
                case 3: {
                    TextCell textCell = (TextCell) holder.itemView;
                    textCell.setColors(Theme.key_icon, Theme.key_icon);
                    textCell.setTag(Theme.key_icon);
                    if (position == scoreRow) {
                        textCell.setTextAndValueAndIcon(context.getString(R.string.UserScore), checkPersianNumber(totalScore), R.drawable.menu_score, true);
                    } else if (position == cloudRow) {
                        textCell.setTextAndIcon(context.getString(R.string.UserCloud), R.drawable.menu_cloud, false);
                    } else if (position == contactRow) {
                        textCell.setTextAndIcon(context.getString(R.string.contacts), R.drawable.menu_contact, true);
                    } else if (position == notificationRow) {
                        textCell.setTextAndIcon(context.getString(R.string.NotificationsAndSounds), R.drawable.menu_notifications, true);
                    } else if (position == privacyRow) {
                        textCell.setTextAndIcon(context.getString(R.string.PrivacySettings), R.drawable.menu_security, true);
                    } else if (position == dataRow) {
                        textCell.setTextAndIcon(context.getString(R.string.DataSettings), R.drawable.menu_data, true);
                    } else if (position == chatRow) {
                        textCell.setTextAndIcon(context.getString(R.string.ChatSettings), R.drawable.menu_chats, true);
                    } else if (position == devicesRow) {
                        textCell.setTextAndIcon(context.getString(R.string.ActiveSessions), R.drawable.menu_devices, true);
                    } else if (position == languageRow) {
                        textCell.setTextAndIcon(context.getString(R.string.Language), R.drawable.menu_language, false);
                    } else if (position == inviteRow) {
                        textCell.setTextAndIcon(context.getString(R.string.InviteFriends), R.drawable.menu_invite, true);
                    } else if (position == barcodeRow) {
                        textCell.setTextAndIcon(context.getString(R.string.BarcodeScan), R.drawable.menu_scan, true);
                    } else if (position == locationRow) {
                        textCell.setTextAndIcon(context.getString(R.string.iGapNearBy), R.drawable.menu_nearby, false);
                    } else if (position == faqRow) {
                        textCell.setTextAndIcon(context.getString(R.string.iGapFAQ), R.drawable.menu_faq, true);
                    } else if (position == updateRow) {
                        textCell.setTextAndIcon(context.getString(R.string.iGapUpdate), R.drawable.menu_update, false);
                    } else if (position == logoutRow) {
                        textCell.setTextAndIcon(context.getString(R.string.log_out), R.drawable.logout, false);
                        textCell.setColors(Theme.key_dark_red, Theme.key_dark_red);
                    } else if (position == addAccountRow) {
                        textCell.setTextAndIcon(context.getString(R.string.add_new_account), R.drawable.actions_addmember2, false);
                    }
                    break;
                }
                case 5: {
                    AccountCell accountCell = (AccountCell) holder.itemView;
                    int index = accountIndex >= accountUsers.size() ? accountIndex = 0 : accountIndex++;
                    accountCell.setUserId(accountUsers.get(index).getId());
                    avatarHandler.getAvatar(new ParamWithAvatarType(accountCell.getAvatarImageView(), accountUsers.get(index).getId()).avatarType(AvatarHandler.AvatarType.USER));
                    accountCell.setValues(accountUsers.get(index).getName(), checkPersianNumber(accountUsers.get(index).getPhoneNumber()), true);
                    break;
                }

            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position == sectionHeaderRow || position == sectionOneHeaderRow || position == sectionTwoHeaderRow || position == sectionThreeHeaderRow || position == sectionFourHeaderRow || position == sectionFiveHeaderRow) {
                return 0;
            } else if (position == phoneNumberRow || position == nameRow || position == usernameRow || position == bioRow || (position == referralCodeRow && isMenuShow) || (position == emailRow && isMenuShow) || (position == genderRow && isMenuShow)) {
                return 1;
            } else if (position == sectionDividerRow || position == sectionOneDividerRow || position == sectionTwoDividerRow || position == sectionThreeDividerRow || position == sectionFourDividerRow) {
                return 2;
            } else if (position == scoreRow || position == cloudRow || position == notificationRow || position == privacyRow || position == dataRow ||
                    position == chatRow || position == devicesRow || position == languageRow || position == inviteRow || position == barcodeRow || position == locationRow ||
                    position == faqRow || position == updateRow || position == logoutRow || position == contactRow || (position == addAccountRow && isAddAccountShow)) {
                return 3;
            } else if (position == versionRow) {
                return 4;
            } else {
                return 5;
            }
        }

        @Override
        public int getItemCount() {
            return rowCount;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int type = holder.getItemViewType();
            return type == 1 || type == 3 || type == 6;
        }
    }

    private boolean checkValidationForRealm(RealmUserInfo realmUserInfo) {
        return realmUserInfo != null && realmUserInfo.isValid();
    }
}
