package com.iGap.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.iGap.G;
import com.iGap.R;
import com.iGap.adapter.items.ContactItemGroupProfile;
import com.iGap.fragments.FragmentListAdmin;
import com.iGap.fragments.FragmentNotification;
import com.iGap.fragments.FragmentShowAvatars;
import com.iGap.fragments.ShowCustomList;
import com.iGap.helper.HelperAvatar;
import com.iGap.helper.HelperPermision;
import com.iGap.helper.HelperString;
import com.iGap.helper.ImageHelper;
import com.iGap.interfaces.OnAvatarAdd;
import com.iGap.interfaces.OnAvatarDelete;
import com.iGap.interfaces.OnAvatarGet;
import com.iGap.interfaces.OnFileUploadForActivities;
import com.iGap.interfaces.OnGetPermision;
import com.iGap.interfaces.OnGroupAddAdmin;
import com.iGap.interfaces.OnGroupAddMember;
import com.iGap.interfaces.OnGroupAddModerator;
import com.iGap.interfaces.OnGroupAvatarDelete;
import com.iGap.interfaces.OnGroupAvatarResponse;
import com.iGap.interfaces.OnGroupCheckUsername;
import com.iGap.interfaces.OnGroupDelete;
import com.iGap.interfaces.OnGroupEdit;
import com.iGap.interfaces.OnGroupGetMemberList;
import com.iGap.interfaces.OnGroupKickAdmin;
import com.iGap.interfaces.OnGroupKickMember;
import com.iGap.interfaces.OnGroupKickModerator;
import com.iGap.interfaces.OnGroupLeft;
import com.iGap.interfaces.OnGroupRemoveUsername;
import com.iGap.interfaces.OnGroupRevokeLink;
import com.iGap.interfaces.OnGroupUpdateUsername;
import com.iGap.interfaces.OnMenuClick;
import com.iGap.interfaces.OnSelectedList;
import com.iGap.interfaces.OnUserInfoResponse;
import com.iGap.libs.rippleeffect.RippleView;
import com.iGap.module.AndroidUtils;
import com.iGap.module.AttachFile;
import com.iGap.module.CircleImageView;
import com.iGap.module.Contacts;
import com.iGap.module.CustomTextViewMedium;
import com.iGap.module.FileUploadStructure;
import com.iGap.module.MaterialDesignTextView;
import com.iGap.module.OnComplete;
import com.iGap.module.SUID;
import com.iGap.module.StructContactInfo;
import com.iGap.proto.ProtoGlobal;
import com.iGap.proto.ProtoGroupCheckUsername;
import com.iGap.proto.ProtoGroupGetMemberList;
import com.iGap.realm.RealmAvatar;
import com.iGap.realm.RealmAvatarFields;
import com.iGap.realm.RealmGroupRoom;
import com.iGap.realm.RealmMember;
import com.iGap.realm.RealmRegisteredInfo;
import com.iGap.realm.RealmRegisteredInfoFields;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.RealmRoomFields;
import com.iGap.realm.RealmRoomMessage;
import com.iGap.realm.RealmRoomMessageFields;
import com.iGap.realm.RealmUserInfo;
import com.iGap.realm.enums.GroupChatRole;
import com.iGap.request.RequestGroupAddAdmin;
import com.iGap.request.RequestGroupAddMember;
import com.iGap.request.RequestGroupAddModerator;
import com.iGap.request.RequestGroupAvatarAdd;
import com.iGap.request.RequestGroupCheckUsername;
import com.iGap.request.RequestGroupDelete;
import com.iGap.request.RequestGroupEdit;
import com.iGap.request.RequestGroupGetMemberList;
import com.iGap.request.RequestGroupKickAdmin;
import com.iGap.request.RequestGroupKickMember;
import com.iGap.request.RequestGroupKickModerator;
import com.iGap.request.RequestGroupLeft;
import com.iGap.request.RequestGroupRemoveUsername;
import com.iGap.request.RequestGroupRevokeLink;
import com.iGap.request.RequestGroupUpdateUsername;
import com.iGap.request.RequestUserInfo;
import com.mikepenz.fastadapter.AbstractAdapter;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.IItemAdapter;
import com.mikepenz.fastadapter.adapters.HeaderAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.iGap.G.context;
import static com.iGap.R.id.fragmentContainer_group_profile;
import static com.iGap.realm.enums.RoomType.GROUP;

/**
 * Created by android3 on 9/18/2016.
 */
public class ActivityGroupProfile extends ActivityEnhanced implements OnGroupAvatarResponse, OnFileUploadForActivities, OnGroupAvatarDelete, OnGroupRevokeLink {

    LinearLayout layoutSetting;
    LinearLayout layoutSetAdmin;
    LinearLayout layoutSetModereator;
    LinearLayout layoutMemberCanAddMember;
    LinearLayout layoutNotificatin;
    LinearLayout layoutDeleteAndLeftGroup;
    List<StructContactInfo> contacts;
    List<IItem> items;
    ItemAdapter itemAdapter;
    RecyclerView recyclerView;
    AttachFile attachFile;
    private CircleImageView imvGroupAvatar;
    private TextView txtGroupNameTitle;
    private TextView txtGroupName;
    private TextView txtGroupDescription;
    private TextView txtNumberOfSharedMedia;
    private TextView txtMemberNumber;
    private TextView txtMore;
    private AppBarLayout appBarLayout;
    private FloatingActionButton fab;
    private String tmp = "";
    private int numberUploadItem = 5;
    private FastAdapter fastAdapter;
    private long roomId;
    private String title;
    private String description;
    private String initials;
    private String inviteLink;
    private String linkUsername;
    private String color;
    private GroupChatRole role;
    private long noLastMessage;
    private String participantsCountLabel;
    private RealmList<RealmMember> members;
    public static OnMenuClick onMenuClick;
    private Long userID = 0l;
    private boolean isPrivate;
    private TextView txtLinkTitle;
    private TextView txtGroupLink;
    private boolean isPopup = false;
    private ViewGroup ltLink;

    private long startMessageId = 0;

    private PopupWindow popupWindow;
    private ProgressBar prgWait;

    private RealmList<RealmMember> memberList;


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_profile);

        Bundle extras = getIntent().getExtras();
        roomId = extras.getLong("RoomId");


        Realm realm = Realm.getDefaultInstance();

        //group info
        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
        RealmGroupRoom realmGroupRoom = realmRoom.getGroupRoom();
        title = realmRoom.getTitle();
        initials = realmRoom.getInitials();
        color = realmRoom.getColor();
        role = realmGroupRoom.getRole();
        inviteLink = realmGroupRoom.getInvite_link();
        linkUsername = realmGroupRoom.getUsername();
        isPrivate = realmGroupRoom.isPrivate();

        try {
            if (realmRoom.getLastMessage() != null) {
                noLastMessage = realmRoom.getLastMessage().getMessageId();
            }
        } catch (NullPointerException e) {
            e.getStackTrace();
        }

        participantsCountLabel = realmGroupRoom.getParticipantsCountLabel();
        members = realmGroupRoom.getMembers();
        memberList = realmGroupRoom.getMembers();
        description = realmGroupRoom.getDescription();


        RealmUserInfo userInfo = realm.where(RealmUserInfo.class).findFirst();
        if (userInfo != null) userID = userInfo.getUserId();

        realm.close();
        initComponent();

        attachFile = new AttachFile(this);

        G.uploaderUtil.setActivityCallbacks(this);
        G.onGroupAvatarResponse = this;
        G.onGroupAvatarDelete = this;
        G.onGroupRevokeLink = this;
    }

    @Override
    protected void onPause() {
        //        int me = setGroupParticipantLable();
        if (ActivityChat.onComplete != null) {

            if (!txtMemberNumber.getText().toString().equals(participantsCountLabel)) {
                ActivityChat.onComplete.complete(true, txtMemberNumber.getText().toString(), "");

                Realm realm = Realm.getDefaultInstance();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
                        realmRoom.getGroupRoom().setParticipantsCountLabel(txtMemberNumber.getText().toString());
                    }
                });
                realm.close();

            }

        }

        LocalBroadcastManager.getInstance(ActivityGroupProfile.this).unregisterReceiver(reciverOnGroupChangeName);

        super.onPause();
    }


    @Override
    protected void onResume() {
        super.onResume();

        ActivityShearedMedia.getCountOfSharedMedia(roomId, txtNumberOfSharedMedia.getText().toString(), new OnComplete() {
            @Override
            public void complete(boolean result, final String messageOne, String MessageTow) {
                txtNumberOfSharedMedia.post(new Runnable() {
                    @Override
                    public void run() {
                        txtNumberOfSharedMedia.setText(messageOne);
                    }
                });
            }
        });

        LocalBroadcastManager.getInstance(this).registerReceiver(reciverOnGroupChangeName, new IntentFilter("Intent_filter_on_change_group_name"));
    }

    private BroadcastReceiver reciverOnGroupChangeName = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {


            String name = intent.getExtras().getString("Name");
            String description = intent.getExtras().getString("Description");

            txtGroupName.setText(name);
            txtGroupDescription.setText(description);
            txtGroupNameTitle.setText(name);


        }
    };


    private void initComponent() {

        MaterialDesignTextView btnBack = (MaterialDesignTextView) findViewById(R.id.agp_btn_back);
        RippleView rippleBack = (RippleView) findViewById(R.id.agp_ripple_back);
        rippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {

            @Override
            public void onComplete(RippleView rippleView) {
                finish();
            }
        });

        MaterialDesignTextView btnMenu = (MaterialDesignTextView) findViewById(R.id.agp_btn_menu);
        final int screenWidth = (int) (getResources().getDisplayMetrics().widthPixels / 1.7);
        RippleView rippleMenu = (RippleView) findViewById(R.id.agp_ripple_menu);
        rippleMenu.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {

            @Override
            public void onComplete(RippleView rippleView) {

                LinearLayout layoutDialog = new LinearLayout(ActivityGroupProfile.this);
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutDialog.setOrientation(LinearLayout.VERTICAL);
                layoutDialog.setBackgroundColor(getResources().getColor(android.R.color.white));
                //TextView text1 = new TextView(ActivityGroupProfile.this);
                TextView text2 = new TextView(ActivityGroupProfile.this);
                TextView text3 = new TextView(ActivityGroupProfile.this);

                //text1.setTextColor(getResources().getColor(android.R.color.black));
                text2.setTextColor(getResources().getColor(android.R.color.black));
                text3.setTextColor(getResources().getColor(android.R.color.black));

                //text1.setText(getResources().getString(R.string.Search));
                text2.setText(getResources().getString(R.string.clear_history));
                if (role == GroupChatRole.OWNER || role == GroupChatRole.ADMIN) {
                    text3.setVisibility(View.VISIBLE);
                    if (isPrivate) {
                        text3.setText(getResources().getString(R.string.group_title_convert_to_public));
                    } else {
                        text3.setText(getResources().getString(R.string.group_title_convert_to_private));
                    }
                } else {
                    text3.setVisibility(View.GONE);
                }


                int dim20 = (int) getResources().getDimension(R.dimen.dp20);
                int dim12 = (int) getResources().getDimension(R.dimen.dp12);
                int sp14_Popup = 14;

                /**
                 * change dpi tp px
                 */
                DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
                int width = displayMetrics.widthPixels;
                int widthDpi = Math.round(width / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));

                if (widthDpi >= 720) {
                    sp14_Popup = 30;
                } else if (widthDpi >= 600) {
                    sp14_Popup = 22;
                } else {
                    sp14_Popup = 15;
                }

                //text1.setTextSize(16);
                text2.setTextSize(sp14_Popup);
                text3.setTextSize(sp14_Popup);

                //text1.setPadding(dim20, dim12, dim12, dim20);
                text2.setPadding(dim20, dim12, dim12, dim12);
                text3.setPadding(dim20, 0, dim12, dim20);

                //layoutDialog.addView(text1, params);
                layoutDialog.addView(text2, params);
                layoutDialog.addView(text3, params);

                popupWindow = new PopupWindow(layoutDialog, screenWidth, ViewGroup.LayoutParams.WRAP_CONTENT, true);
                popupWindow.setBackgroundDrawable(new BitmapDrawable());
                popupWindow.setOutsideTouchable(true);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    popupWindow.setBackgroundDrawable(getResources().getDrawable(R.mipmap.shadow3, ActivityGroupProfile.this.getTheme()));
                } else {
                    popupWindow.setBackgroundDrawable((getResources().getDrawable(R.mipmap.shadow3)));
                }
                if (popupWindow.isOutsideTouchable()) {
                    popupWindow.dismiss();
                }
                popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        //TODO do sth here on dismiss
                    }
                });

                popupWindow.setAnimationStyle(android.R.style.Animation_InputMethod);
                popupWindow.showAtLocation(layoutDialog, Gravity.RIGHT | Gravity.TOP, (int) getResources().getDimension(R.dimen.dp16), (int) getResources().getDimension(R.dimen.dp32));
                //                popupWindow.showAsDropDown(v);

                //text1.setOnClickListener(new View.OnClickListener() {
                //    @Override public void onClick(View view) {
                //
                //        popupWindow.dismiss();
                //    }
                //});
                text2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        new MaterialDialog.Builder(ActivityGroupProfile.this).title(R.string.clear_history).content(R.string.clear_history_content).positiveText(R.string.B_ok).onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                            }
                        }).negativeText(R.string.B_cancel).show();

                        popupWindow.dismiss();
                    }
                });

                text3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        isPopup = true;

                        if (isPrivate) {
                            convertToPublic();
                        } else {
                            convertToPrivate();
                        }
                        popupWindow.dismiss();
                    }
                });
            }
        });

        layoutSetting = (LinearLayout) findViewById(R.id.agp_ll_seetting);
        layoutSetAdmin = (LinearLayout) findViewById(R.id.agp_ll_set_admin);
        layoutSetModereator = (LinearLayout) findViewById(R.id.agp_ll_set_modereator);
        layoutMemberCanAddMember = (LinearLayout) findViewById(R.id.agp_ll_member_can_add_member);
        layoutNotificatin = (LinearLayout) findViewById(R.id.agp_ll_notification);
        layoutDeleteAndLeftGroup = (LinearLayout) findViewById(R.id.agp_ll_delete_and_left_group);
        prgWait = (ProgressBar) findViewById(R.id.agp_prgWaiting_addContact);
        ltLink = (ViewGroup) findViewById(R.id.agp_ll_link);
        prgWait.getIndeterminateDrawable()
                .setColorFilter(getResources().getColor(R.color.toolbar_background), PorterDuff.Mode.MULTIPLY);
        imvGroupAvatar = (CircleImageView) findViewById(R.id.agp_imv_group_avatar);

        txtGroupNameTitle = (TextView) findViewById(R.id.agp_txt_group_name_title);
        txtGroupNameTitle.setText(title);

        txtGroupName = (TextView) findViewById(R.id.agp_txt_group_name);
        txtGroupName.setText(title);

        txtGroupDescription = (TextView) findViewById(R.id.agp_txt_group_description);
        txtGroupDescription.setText(description);

        txtNumberOfSharedMedia = (TextView) findViewById(R.id.agp_txt_number_of_shared_media);

        txtMemberNumber = (TextView) findViewById(R.id.agp_txt_member_number);
        appBarLayout = (AppBarLayout) findViewById(R.id.agp_appbar);

        LinearLayout llGroupName = (LinearLayout) findViewById(R.id.agp_ll_group_name);
        LinearLayout llGroupDescription = (LinearLayout) findViewById(R.id.agp_ll_group_description);

        if (role == GroupChatRole.OWNER || role == GroupChatRole.ADMIN) {
            llGroupName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ChangeGroupName();
                }
            });

            llGroupDescription.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ChangeGroupDescription();
                }
            });
        }


        if (isPrivate && (role == GroupChatRole.OWNER || role == GroupChatRole.ADMIN)) {

            ltLink.setVisibility(View.VISIBLE);
        } else {
            ltLink.setVisibility(View.GONE);
        }

        if (!isPrivate) {
            ltLink.setVisibility(View.VISIBLE);
        }


        LinearLayout llSharedMedia = (LinearLayout) findViewById(R.id.agp_ll_sheared_media);
        llSharedMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ActivityGroupProfile.this, ActivityShearedMedia.class);
                intent.putExtra("RoomID", roomId);
                startActivity(intent);
            }
        });

        final TextView titleToolbar = (TextView) findViewById(R.id.agp_txt_titleToolbar);
        final ViewGroup viewGroup = (ViewGroup) findViewById(R.id.apg_parentLayoutCircleImage);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                if (verticalOffset < -5) {

                    viewGroup.setVisibility(View.GONE);
                    titleToolbar.setVisibility(View.VISIBLE);
                    viewGroup.animate().alpha(0).setDuration(500);
                    titleToolbar.animate().alpha(1).setDuration(250);
                } else {

                    titleToolbar.setVisibility(View.GONE);
                    viewGroup.setVisibility(View.VISIBLE);
                    titleToolbar.animate().alpha(0).setDuration(250);
                    viewGroup.animate().alpha(1).setDuration(500);
                }
            }
        });

        //        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
        //            @Override
        //            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        //
        //                TextView titleToolbar = (TextView) findViewById(R.id.agp_txt_titleToolbar);
        //                if (verticalOffset < -appBarLayout.getTotalScrollRange() / 4) {
        //
        //                    titleToolbar.animate().alpha(1).setDuration(300);
        //                    titleToolbar.setVisibility(View.VISIBLE);
        //                } else {
        //                    titleToolbar.animate().alpha(0).setDuration(500);
        //                    titleToolbar.setVisibility(View.GONE);
        //                }
        //            }
        //        });


        fab = (FloatingActionButton) findViewById(R.id.agp_fab_setPic);

        if (role == GroupChatRole.OWNER || role == GroupChatRole.ADMIN) {
            fab.setVisibility(View.VISIBLE);

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    startDialogSelectPicture(R.array.profile);

                }
            });

        }


        txtMore = (TextView) findViewById(R.id.agp_txt_more);
        txtMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int count = items.size();
                int listSize = contacts.size();

                for (int i = count; i < listSize && i < count + numberUploadItem; i++) {
                    items.add(new ContactItemGroupProfile().setContact(contacts.get(i)).withIdentifier(100 + contacts.indexOf(contacts.get(i))));
                }

                itemAdapter.clear();
                itemAdapter.add(items);

                if (items.size() >= listSize) txtMore.setVisibility(View.GONE);
            }
        });

        ViewGroup layoutAddMember = (ViewGroup) findViewById(R.id.agp_layout_add_member);
        layoutAddMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMemberToGroup();
            }
        });

        TextView txtSetAdmin = (TextView) findViewById(R.id.agp_txt_set_admin);
        txtSetAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setMemberRoleToAdmin();
            }
        });

        TextView txtAddModereator = (TextView) findViewById(R.id.agp_txt_add_modereator);
        txtAddModereator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setMemberRoleToModerator();
            }
        });

        final ToggleButton toggleButton = (ToggleButton) findViewById(R.id.agp_toggle_member_can_add_member);
        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (toggleButton.isChecked()) {
                } else {

                }
            }
        });

        TextView txtNotification = (TextView) findViewById(R.id.agp_txt_str_notification_and_sound);
        txtNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentNotification fragmentNotification = new FragmentNotification();
                Bundle bundle = new Bundle();
                bundle.putString("PAGE", "GROUP");
                bundle.putLong("ID", roomId);
                fragmentNotification.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().addToBackStack(null).setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left).replace(fragmentContainer_group_profile, fragmentNotification).commit();
            }
        });

        TextView txtDeleteGroup = (TextView) findViewById(R.id.agp_txt_str_delete_and_leave_group);

        if (role == GroupChatRole.OWNER || role == GroupChatRole.ADMIN) {
            txtDeleteGroup.setText(getString(R.string.delete_group));
        } else {
            txtDeleteGroup.setText(getString(R.string.left_group));
        }


        txtDeleteGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                groupLeft();
            }
        });

        RippleView rippleCircleImage = (RippleView) findViewById(R.id.agp_ripple_group_avatar);
        rippleCircleImage.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {

                Realm realm = Realm.getDefaultInstance();
                if (realm.where(RealmAvatar.class).equalTo(RealmAvatarFields.OWNER_ID, roomId).findFirst() != null) {
                    FragmentShowAvatars.appBarLayout = fab;

                    FragmentShowAvatars fragment = FragmentShowAvatars.newInstance(roomId, FragmentShowAvatars.From.group);
                    ActivityGroupProfile.this.getSupportFragmentManager().beginTransaction().addToBackStack(null).setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left).replace(R.id.fragmentContainer_group_profile, fragment, null).commit();
                }
                realm.close();

            }
        });

        txtMemberNumber.setText(participantsCountLabel);

        txtLinkTitle = (TextView) findViewById(R.id.agp_txt_link_title);
        txtGroupLink = (TextView) findViewById(R.id.agp_txt_link);


        setTextGroupLik();


        ltLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                isPopup = false;

                if (role == GroupChatRole.OWNER || role == GroupChatRole.ADMIN) {
                    if (isPrivate) {
                        dialogRevoke();
                    } else {
                        setUsername();
                    }
                } else {
                    dialogCopyLink();
                }
            }
        });


        FragmentShowAvatars.onComplete = new OnComplete() {
            @Override
            public void complete(boolean result, String messageOne, String MessageTow) {

                //                showImage();
                long mAvatarId = 0;
                if (messageOne != null && !messageOne.equals("")) {
                    mAvatarId = Long.parseLong(messageOne);
                }

                HelperAvatar.avatarDelete(roomId, mAvatarId, HelperAvatar.AvatarType.ROOM, new OnAvatarDelete() {
                    @Override
                    public void latestAvatarPath(final String avatarPath) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ImageLoader.getInstance().displayImage(AndroidUtils.suitablePath(avatarPath), imvGroupAvatar);
                            }
                        });
                    }

                    @Override
                    public void showInitials(final String initials, final String color) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                imvGroupAvatar.setImageBitmap(com.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) imvGroupAvatar.getContext().getResources().getDimension(R.dimen.dp60), initials, color));
                            }
                        });
                    }
                });


            }
        };


        showAvatar();
        setUiIndependRole();
        initRecycleView();
        getMemberList();
        //TODO [Saeed Mozaffari] [2016-11-29 3:12 PM] - please impalement this callbacks
        onGroupAddMemberCallback();
        onGroupKickMemberCallback();
        onSetAdminCallback();
        onGroupKickAdminCallback();
        onGroupAddModeratorCallback();
        onGroupKickModeratorCallback();
    }

    private void dialogCopyLink() {

        String link = txtGroupLink.getText().toString();

        final LinearLayout layoutGroupLink = new LinearLayout(ActivityGroupProfile.this);
        layoutGroupLink.setOrientation(LinearLayout.VERTICAL);

        final View viewRevoke = new View(ActivityGroupProfile.this);
        LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);

        final TextInputLayout inputGroupLink = new TextInputLayout(ActivityGroupProfile.this);
        EditText edtLink = new EditText(ActivityGroupProfile.this);
        edtLink.setHint(getResources().getString(R.string.group_link_hint_revoke));
        edtLink.setText(link);
        edtLink.setTextColor(getResources().getColor(R.color.text_edit_text));
        edtLink.setHintTextColor(getResources().getColor(R.color.hint_edit_text));
        edtLink.setPadding(0, 8, 0, 8);
        edtLink.setEnabled(false);
        edtLink.setSingleLine(true);
        inputGroupLink.addView(edtLink);
        inputGroupLink.addView(viewRevoke, viewParams);

        viewRevoke.setBackgroundColor(getResources().getColor(R.color.line_edit_text));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            edtLink.setBackground(getResources().getDrawable(android.R.color.transparent));
        }
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        layoutGroupLink.addView(inputGroupLink, layoutParams);

        final MaterialDialog dialog =
                new MaterialDialog.Builder(ActivityGroupProfile.this).title(getResources().getString(R.string.group_link)).positiveText(getResources().getString(R.string.array_Copy)).customView(layoutGroupLink, true).widgetColor(getResources().getColor(R.color.toolbar_background)).negativeText(getResources().getString(R.string.B_cancel)).onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        String copy;
                        copy = txtGroupLink.getText().toString();
                        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("LINK_GROUP", copy);
                        clipboard.setPrimaryClip(clip);
                    }
                }).build();

        dialog.show();

    }

    private void dialogRevoke() {

        String link = txtGroupLink.getText().toString();

        final LinearLayout layoutRevoke = new LinearLayout(ActivityGroupProfile.this);
        layoutRevoke.setOrientation(LinearLayout.VERTICAL);

        final View viewRevoke = new View(ActivityGroupProfile.this);
        LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);

        final TextInputLayout inputRevoke = new TextInputLayout(ActivityGroupProfile.this);
        EditText edtRevoke = new EditText(ActivityGroupProfile.this);
        edtRevoke.setHint(getResources().getString(R.string.group_link_hint_revoke));
        edtRevoke.setText(link);
        edtRevoke.setTextColor(getResources().getColor(R.color.text_edit_text));
        edtRevoke.setHintTextColor(getResources().getColor(R.color.hint_edit_text));
        edtRevoke.setPadding(0, 8, 0, 8);
        edtRevoke.setEnabled(false);
        edtRevoke.setSingleLine(true);
        inputRevoke.addView(edtRevoke);
        inputRevoke.addView(viewRevoke, viewParams);

        viewRevoke.setBackgroundColor(getResources().getColor(R.color.line_edit_text));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            edtRevoke.setBackground(getResources().getDrawable(android.R.color.transparent));
        }
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        layoutRevoke.addView(inputRevoke, layoutParams);

        final MaterialDialog dialog = new MaterialDialog.Builder(ActivityGroupProfile.this).title(getResources().getString(R.string.group_link_hint_revoke))
                .positiveText(getResources().getString(R.string.revoke))
                .customView(layoutRevoke, true)
                .widgetColor(getResources().getColor(R.color.toolbar_background))
                .negativeText(getResources().getString(R.string.B_cancel))
                .neutralText(R.string.array_Copy)
                .onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        String copy;
                        copy = txtGroupLink.getText().toString();
                        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("LINK_GROUP", copy);
                        clipboard.setPrimaryClip(clip);
                    }
                })
                .build();

        final View positive = dialog.getActionButton(DialogAction.POSITIVE);
        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new RequestGroupRevokeLink().groupRevokeLink(roomId);
            }
        });
        dialog.show();
    }

    private void convertToPrivate() {

        G.onGroupRemoveUsername = new OnGroupRemoveUsername() {
            @Override
            public void onGroupRemoveUsername(final long roomId) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        isPrivate = true;
                        setTextGroupLik();
                        Realm realm = Realm.getDefaultInstance();

                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
                                realmRoom.getGroupRoom().setPrivate(true);

                            }
                        });
                        realm.close();
                    }
                });
            }

            @Override
            public void onError(int majorCode, int minorCode) {

            }
        };


        new MaterialDialog.Builder(ActivityGroupProfile.this).title(getString(R.string.group_title_convert_to_private)).content(getString(R.string.group_text_convert_to_private)).positiveText(R.string.B_ok).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                new RequestGroupRemoveUsername().groupRemoveUsername(roomId);

            }
        }).negativeText(R.string.B_cancel).show();
    }

    private void setTextGroupLik() {

        if (isPrivate) {
            txtGroupLink.setText("" + inviteLink);
            txtLinkTitle.setText(getResources().getString(R.string.group_link));

        } else {
            txtGroupLink.setText("" + linkUsername);
            txtLinkTitle.setText(getResources().getString(R.string.st_username));
        }
    }

    private void convertToPublic() {
        new MaterialDialog.Builder(ActivityGroupProfile.this).title(getString(R.string.group_title_convert_to_public)).content(getString(R.string.group_text_convert_to_public)).positiveText(R.string.B_ok).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                dialog.dismiss();
                setUsername();

            }
        }).negativeText(R.string.B_cancel).show();
    }

    private void setUsername() {
        final LinearLayout layoutUserName = new LinearLayout(ActivityGroupProfile.this);
        layoutUserName.setOrientation(LinearLayout.VERTICAL);

        final View viewUserName = new View(ActivityGroupProfile.this);
        LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);

        final TextInputLayout inputUserName = new TextInputLayout(ActivityGroupProfile.this);
        final EditText edtUserName = new EditText(ActivityGroupProfile.this);
        edtUserName.setHint(getResources().getString(R.string.group_title_set_username));

        if (isPopup) {
            edtUserName.setText("iGap.net/");
        } else {
            edtUserName.setText("" + linkUsername);
        }

        edtUserName.setTextColor(getResources().getColor(R.color.text_edit_text));
        edtUserName.setHintTextColor(getResources().getColor(R.color.hint_edit_text));
        edtUserName.setPadding(0, 8, 0, 8);
        edtUserName.setSingleLine(true);
        inputUserName.addView(edtUserName);
        inputUserName.addView(viewUserName, viewParams);

        viewUserName.setBackgroundColor(getResources().getColor(R.color.line_edit_text));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            edtUserName.setBackground(getResources().getDrawable(android.R.color.transparent));
        }
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        layoutUserName.addView(inputUserName, layoutParams);

        final MaterialDialog dialog = new MaterialDialog.Builder(ActivityGroupProfile.this).title(getResources().getString(R.string.st_username)).positiveText(getResources().getString(R.string.save)).customView(layoutUserName, true).widgetColor(getResources().getColor(R.color.toolbar_background)).negativeText(getResources().getString(R.string.B_cancel)).build();

        final View positive = dialog.getActionButton(DialogAction.POSITIVE);
        positive.setEnabled(false);

        G.onGroupCheckUsername = new OnGroupCheckUsername() {
            @Override
            public void onGroupCheckUsername(final ProtoGroupCheckUsername.GroupCheckUsernameResponse.Status status) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (status == ProtoGroupCheckUsername.GroupCheckUsernameResponse.Status.AVAILABLE) {

                            positive.setEnabled(true);
                            inputUserName.setErrorEnabled(true);
                            inputUserName.setError("");
                        } else if (status == ProtoGroupCheckUsername.GroupCheckUsernameResponse.Status.INVALID) {
                            positive.setEnabled(false);
                            inputUserName.setErrorEnabled(true);
                            inputUserName.setError("INVALID");

                        } else if (status == ProtoGroupCheckUsername.GroupCheckUsernameResponse.Status.TAKEN) {
                            positive.setEnabled(false);
                            inputUserName.setErrorEnabled(true);
                            inputUserName.setError("TAKEN");
                        } else if (status == ProtoGroupCheckUsername.GroupCheckUsernameResponse.Status.OCCUPYING_LIMIT_EXCEEDED) {
                            positive.setEnabled(false);
                            inputUserName.setErrorEnabled(true);
                            inputUserName.setError("OCCUPYING LIMIT EXCEEDED");
                        }
                    }
                });
            }

            @Override
            public void onError(int majorCode, int minorCode) {

            }
        };

        edtUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (!editable.toString().contains("iGap.net/")) {
                    edtUserName.setText("iGap.net/");
                    Selection.setSelection(edtUserName.getText(), edtUserName.getText().length());
                }

                if (HelperString.regexCheckUsername(editable.toString().replace("iGap.net/", ""))) {
                    String userName = edtUserName.getText().toString().replace("iGap.net/", "");
                    new RequestGroupCheckUsername().GroupCheckUsername(roomId, userName);


                } else {
                    positive.setEnabled(false);
                    inputUserName.setErrorEnabled(true);
                    inputUserName.setError("INVALID");
                }
            }
        });

        G.onGroupUpdateUsername = new OnGroupUpdateUsername() {
            @Override
            public void onGroupUpdateUsername(final long roomId, final String username) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        isPrivate = false;
                        dialog.dismiss();

                        linkUsername = username;
                        setTextGroupLik();

                        Realm realm = Realm.getDefaultInstance();
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();

                                realmRoom.getGroupRoom().setUsername("iGap.net" + edtUserName.getText().toString());
                                realmRoom.getGroupRoom().setPrivate(false);

                            }
                        });
                        realm.close();

                    }
                });
            }

            @Override
            public void onError(final int majorCode, int minorCode, final int time) {

                switch (majorCode) {
                    case 368:

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (dialog.isShowing()) dialog.dismiss();
                                dialogWaitTime(R.string.error, time, majorCode);
                            }
                        });

                        break;
                }
            }
        };


        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String userName = edtUserName.getText().toString().replace("iGap.net/", "");
                new RequestGroupUpdateUsername().groupUpdateUsername(roomId, userName);
            }
        });


        edtUserName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    viewUserName.setBackgroundColor(getResources().getColor(R.color.toolbar_background));
                } else {
                    viewUserName.setBackgroundColor(getResources().getColor(R.color.line_edit_text));
                }
            }
        });

        // check each word with server

        dialog.show();
    }


    private void getMemberList() {

        G.onUserInfoResponse = new OnUserInfoResponse() {
            @Override
            public void onUserInfo(final ProtoGlobal.RegisteredUser user, String identity) {
                if (identity != null && Long.parseLong(identity) == roomId) {
                    if (!userExistInList(user.getId())) { // if user exist in current list don't add that, because maybe duplicated this user and show twice.
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Realm realm = Realm.getDefaultInstance();
                                RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, user.getId()).findFirst();

                                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
                                RealmGroupRoom realmGroupRoom = realmRoom.getGroupRoom();

                                RealmList<RealmMember> result = realmRoom.getGroupRoom().getMembers();

                                String _Role = "";

                                for (int i = 0; i < result.size(); i++) {
                                    if (result.get(i).getPeerId() == user.getId()) {
                                        _Role = result.get(i).getRole().toString();
                                        break;
                                    }
                                }


                                final StructContactInfo struct = new StructContactInfo(user.getId(), user.getDisplayName(), user.getStatus().toString(), false, false, user.getPhone() + "");
                                if (realmGroupRoom != null) {
                                    struct.role = _Role;
                                }
                                if (realmRegisteredInfo != null) {
                                    struct.avatar = realmRegisteredInfo.getLastAvatar();
                                    struct.initials = realmRegisteredInfo.getInitials();
                                    struct.color = realmRegisteredInfo.getColor();
                                    struct.lastSeen = realmRegisteredInfo.getLastSeen();
                                    struct.status = realmRegisteredInfo.getStatus();
                                    struct.role = role.toString();
                                }


                                IItem item = new ContactItemGroupProfile().setContact(struct).withIdentifier(SUID.id().get());
                                if (struct.role.equals(ProtoGlobal.GroupRoom.Role.OWNER.toString())) {
                                    itemAdapter.add(0, item);

                                } else {
                                    itemAdapter.add(item);
                                }

                                itemAdapter.notifyDataSetChanged();

                                realm.close();
                            }
                        });
                    }
                }
            }

            @Override
            public void onUserInfoTimeOut() {

            }

            @Override
            public void onUserInfoError(int majorCode, int minorCode) {

            }
        };

        G.onGroupGetMemberList = new OnGroupGetMemberList() {
            @Override
            public void onGroupGetMemberList(final List<ProtoGroupGetMemberList.GroupGetMemberListResponse.Member> members) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        txtMemberNumber.setText(members.size() + "");
                        compareMemberList(memberList, members);
                    }
                });

                for (final ProtoGroupGetMemberList.GroupGetMemberListResponse.Member member : members) {
                    new RequestUserInfo().userInfo(member.getUserId(), roomId + "");
                }
            }
        };

        new RequestGroupGetMemberList().getMemberList(roomId);
    }

    private void setAvatarGroup() {

        Realm realm = Realm.getDefaultInstance();
        RealmResults<RealmAvatar> avatars = realm.where(RealmAvatar.class).equalTo(RealmAvatarFields.OWNER_ID, roomId).findAll();

        if (avatars.isEmpty()) {
            imvGroupAvatar.setImageBitmap(com.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) imvGroupAvatar.getContext().getResources().getDimension(R.dimen.dp60), initials, color));
            return;
        }
        RealmAvatar realmAvatar = null;
        for (int i = avatars.size() - 1; i >= 0; i--) {
            RealmAvatar avatar = avatars.get(i);
            if (avatar.getFile() != null) {
                realmAvatar = avatar;
                break;
            }
        }

        if (realmAvatar == null) {
            imvGroupAvatar.setImageBitmap(com.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) imvGroupAvatar.getContext().getResources().getDimension(R.dimen.dp60), initials, color));
            return;
        }

        if (realmAvatar.getFile().isFileExistsOnLocal()) {
            ImageLoader.getInstance().displayImage(AndroidUtils.suitablePath(realmAvatar.getFile().getLocalFilePath()), imvGroupAvatar);
        } else if (realmAvatar.getFile().isThumbnailExistsOnLocal()) {
            ImageLoader.getInstance().displayImage(AndroidUtils.suitablePath(realmAvatar.getFile().getLocalThumbnailPath()), imvGroupAvatar);
        } else {
            imvGroupAvatar.setImageBitmap(com.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) imvGroupAvatar.getContext().getResources().getDimension(R.dimen.dp60), initials, color));
        }

        realm.close();
    }

    private void initRecycleView() {


        onMenuClick = new OnMenuClick() {
            @Override
            public void clicked(View view, StructContactInfo info) {
                new CreatePopUpMessage().show(view, info);
            }
        };


        //create our FastAdapter
        fastAdapter = new FastAdapter<>();
        fastAdapter.withSelectable(true);

        //create our adapters
        final StickyHeaderAdapter stickyHeaderAdapter = new StickyHeaderAdapter();
        final HeaderAdapter headerAdapter = new HeaderAdapter();
        itemAdapter = new ItemAdapter();
        itemAdapter.withFilterPredicate(new IItemAdapter.Predicate<ContactItemGroupProfile>() {
            @Override
            public boolean filter(ContactItemGroupProfile item, CharSequence constraint) {
                return !item.mContact.displayName.toLowerCase().startsWith(String.valueOf(constraint).toLowerCase());
            }
        });
        fastAdapter.withOnClickListener(new FastAdapter.OnClickListener<ContactItemGroupProfile>() {
            @Override
            public boolean onClick(View v, IAdapter adapter, final ContactItemGroupProfile item, final int position) {

                try {
                    HelperPermision.getStoragePermision(ActivityGroupProfile.this, new OnGetPermision() {
                        @Override
                        public void Allow() {
                            ContactItemGroupProfile contactItemGroupProfile = (ContactItemGroupProfile) item;
                            Intent intent = null;

                            if (contactItemGroupProfile.mContact.peerId == userID) {
                                intent = new Intent(ActivityGroupProfile.this, ActivitySetting.class);
                            } else {
                                intent = new Intent(ActivityGroupProfile.this, ActivityContactsProfile.class);

                                long selectedChatRoomID = -1;
                                Realm realm = Realm.getDefaultInstance();
                                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.CHAT_ROOM.PEER_ID, contactItemGroupProfile.mContact.peerId).findFirst();

                                if (realmRoom != null) {
                                    selectedChatRoomID = realmRoom.getId();
                                }


                                intent.putExtra("peerId", contactItemGroupProfile.mContact.peerId);
                                intent.putExtra("RoomId", roomId);
                                intent.putExtra("enterFrom", GROUP.toString());
                            }

                            finish();
                            if (ActivityChat.activityChat != null) ActivityChat.activityChat.finish();

                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);

                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }


                return false;
            }
        });

        fastAdapter.withOnLongClickListener(new FastAdapter.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v, IAdapter adapter, IItem item, int position) {
                ContactItemGroupProfile contactItemGroupProfile = (ContactItemGroupProfile) item;

                if (role == GroupChatRole.OWNER) {

                    if (contactItemGroupProfile.mContact.role.equals(ProtoGlobal.GroupRoom.Role.MEMBER.toString())) {

                        kickMember(contactItemGroupProfile.mContact.peerId);

                    } else if (contacts.get(position).role.equals(ProtoGlobal.GroupRoom.Role.ADMIN.toString())) {

                        kickAdmin(contactItemGroupProfile.mContact.peerId);

                    } else if (contactItemGroupProfile.mContact.role.equals(ProtoGlobal.GroupRoom.Role.MODERATOR.toString())) {

                        kickModerator(contactItemGroupProfile.mContact.peerId);

                    }
                } else if (role == GroupChatRole.ADMIN) {

                    if (contactItemGroupProfile.mContact.role.equals(ProtoGlobal.GroupRoom.Role.MEMBER.toString())) {
                        kickMember(contactItemGroupProfile.mContact.peerId);
                    } else if (contactItemGroupProfile.mContact.role.equals(ProtoGlobal.GroupRoom.Role.MODERATOR.toString())) {
                        kickModerator(contacts.get(position).peerId);
                    }
                } else if (role == GroupChatRole.MODERATOR) {

                    if (contactItemGroupProfile.mContact.role.equals(ProtoGlobal.GroupRoom.Role.MEMBER.toString())) {
                        kickMember(contactItemGroupProfile.mContact.peerId);
                    }
                }

                return true;
            }
        });

        fastAdapter.setHasStableIds(true);

        //get our recyclerView and do basic setup
        recyclerView = (RecyclerView) findViewById(R.id.agp_recycler_view_group_member);
        recyclerView.setLayoutManager(new LinearLayoutManager(ActivityGroupProfile.this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(stickyHeaderAdapter.wrap(itemAdapter.wrap(headerAdapter.wrap(fastAdapter))));

        recyclerView.setNestedScrollingEnabled(false);

        //this adds the Sticky Headers within our list
        final StickyRecyclerHeadersDecoration decoration = new StickyRecyclerHeadersDecoration(stickyHeaderAdapter);
        recyclerView.addItemDecoration(decoration);

        items = new ArrayList<>();

        ContactItemGroupProfile.mainRole = role.toString();
        ContactItemGroupProfile.roomType = ProtoGlobal.Room.Type.GROUP;

        fillItem();

        int listSize = contacts.size();

        //txtMemberNumber.setText(listSize + "");

        for (int i = 0; i < listSize; i++) {

            items.add(new ContactItemGroupProfile().setContact(contacts.get(i)).withIdentifier(100 + contacts.indexOf(contacts.get(i))));
        }

        //if (listSize < 4) txtMore.setVisibility(View.GONE);
        txtMore.setVisibility(View.GONE);

        itemAdapter.add(items);

        //so the headers are aware of changes
        stickyHeaderAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                decoration.invalidateHeaders();
            }
        });
    }

    private boolean userExistInList(long userId) {

        for (StructContactInfo info : contacts) {
            if (info.peerId == userId) {
                return true;
            }
        }

        return false;
    }

    private void fillItem() {

        contacts = new ArrayList<>();

        Realm realm = Realm.getDefaultInstance();

        for (RealmMember member : members) {
            String role = member.getRole();
            long id = member.getPeerId();

            RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, id).findFirst();

            if (realmRegisteredInfo != null) {
                StructContactInfo s = new StructContactInfo(realmRegisteredInfo.getId(), realmRegisteredInfo.getDisplayName(), realmRegisteredInfo.getStatus(), false, false, realmRegisteredInfo.getPhoneNumber() + "");
                s.role = role;
                s.avatar = realmRegisteredInfo.getLastAvatar();
                s.initials = realmRegisteredInfo.getInitials();
                s.color = realmRegisteredInfo.getColor();
                s.lastSeen = realmRegisteredInfo.getLastSeen();
                s.status = realmRegisteredInfo.getStatus();

                if (s.role.equals(ProtoGlobal.GroupRoom.Role.OWNER.toString())) {
                    contacts.add(0, s);

                } else {
                    contacts.add(s);
                }


            }


        }

        realm.close();
    }

    private List<StructContactInfo> getCurrentUser(ProtoGlobal.GroupRoom.Role role) {
        List<ContactItemGroupProfile> items = itemAdapter.getAdapterItems();

        List<StructContactInfo> users = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).mContact.role.equals(role.toString())) {
                users.add(items.get(i).mContact);
            }
        }
        return users;
    }

    private void setUiIndependRole() {

        if (role == GroupChatRole.MEMBER) {

            layoutSetAdmin.setVisibility(View.GONE);
            layoutSetModereator.setVisibility(View.GONE);
            layoutMemberCanAddMember.setVisibility(View.GONE);
        } else if (role == GroupChatRole.MODERATOR) {
            layoutSetAdmin.setVisibility(View.GONE);
            layoutSetModereator.setVisibility(View.GONE);
        } else if (role == GroupChatRole.ADMIN) {

            layoutSetAdmin.setVisibility(View.GONE);
        } else if (role == GroupChatRole.OWNER) {

        }
    }

    private String filePathAvatar;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            String filePath = null;
            long avatarId = SUID.id().get();
            switch (requestCode) {
                case AttachFile.request_code_TAKE_PICTURE:

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        ImageHelper.correctRotateImage(AttachFile.mCurrentPhotoPath, true);
                        filePath = AttachFile.mCurrentPhotoPath;
                        filePathAvatar = filePath;
                    } else {
                        ImageHelper.correctRotateImage(AttachFile.imagePath, true);
                        filePath = AttachFile.imagePath;
                        filePathAvatar = filePath;
                    }
                    break;
                case AttachFile.request_code_image_from_gallery_single_select:
                    if (data.getData() == null) {
                        return;
                    }
                    filePath = AttachFile.getFilePathFromUri(data.getData());
                    filePathAvatar = filePath;

                    break;
            }

            new UploadTask(prgWait, ActivityGroupProfile.this).execute(filePath, avatarId);
        }
    }

    @Override
    public void onFileUploaded(final FileUploadStructure uploadStructure, String identity) {
        new RequestGroupAvatarAdd().groupAvatarAdd(roomId, uploadStructure.token);
    }

    @Override
    public void onFileUploading(FileUploadStructure uploadStructure, String identity, double progress) {
        // TODO: 10/20/2016 [Alireza] update view something like updating progress
    }

    @Override
    public void onUploadStarted(FileUploadStructure struct) {
        showProgressBar();
    }

    @Override
    public void onBadDownload(String token) {
        // empty
    }

    @Override
    public void onFileTimeOut(String identity) {
        hideProgressBar();
    }

    //dialog for choose pic from gallery or camera
    private void startDialogSelectPicture(int r) {

        new MaterialDialog.Builder(this).title(R.string.choose_picture).negativeText(R.string.cansel).items(r).itemsCallback(new MaterialDialog.ListCallback() {
            @Override
            public void onSelection(final MaterialDialog dialog, View view, int which, CharSequence text) {

                if (which == 0) {
                    try {
                        attachFile.requestOpenGalleryForImageSingleSelect();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (which == 1) {
                    if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) { // camera

                        try {
                            HelperPermision.getStoragePermision(ActivityGroupProfile.this, new OnGetPermision() {
                                @Override
                                public void Allow() throws IOException {
                                    HelperPermision.getCameraPermission(ActivityGroupProfile.this, new OnGetPermision() {
                                        @Override
                                        public void Allow() {
                                            dialog.dismiss();
                                            useCamera();
                                        }
                                    });
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(ActivityGroupProfile.this, R.string.please_check_your_camera, Toast.LENGTH_SHORT).show();
                    }
                }
                //                        else if (which == 2) {
                //                            showProgressBar();
                //                            Realm realm = Realm.getDefaultInstance();
                //                            new RequestGroupAvatarDelete().groupAvatarDelete(roomId, getLastAvatar().getId());
                //                            realm.close();
                //                        }
            }
        }).show();
    }

    private void useCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                new AttachFile(ActivityGroupProfile.this).dispatchTakePictureIntent();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                new AttachFile(ActivityGroupProfile.this).requestTakePicture();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //=============get last avatar
    //TODO [Saeed Mozaffari] [2016-12-03 10:18 AM] - in do ta method ro az inja baradram va be surate global estefade konam

    public RealmList<RealmAvatar> getAvatars() {
        RealmList<RealmAvatar> avatars = new RealmList<>();
        Realm realm = Realm.getDefaultInstance();
        for (RealmAvatar avatar : realm.where(RealmAvatar.class).equalTo(RealmAvatarFields.OWNER_ID, roomId).findAllSorted(RealmAvatarFields.ID, Sort.ASCENDING)) {
            avatars.add(avatar);
        }
        realm.close();
        return avatars;
    }

    public RealmAvatar getLastAvatar() {
        RealmList<RealmAvatar> avatars = getAvatars();
        if (avatars.isEmpty()) {
            return null;
        }
        // make sure return last avatar which has attachment
        for (int i = avatars.size() - 1; i >= 0; i--) {
            RealmAvatar avatar = getAvatars().get(i);
            if (avatar.getFile() != null) {
                return avatar;
            }
        }
        return null;
    }

    //=============

    private void addMemberToGroup() {

        List<StructContactInfo> userList = Contacts.retrieve(null);

        /*for (int i = 0; i < contacts.size(); i++) {
            long id = contacts.get(i).peerId;
            for (int j = 0; j < userList.size(); j++) {
                if (userList.get(j).peerId == id) {
                    userList.remove(j);
                    break;
                }
            }
        }*/


        List<ContactItemGroupProfile> items = itemAdapter.getAdapterItems();

        for (int i = 0; i < items.size(); i++) {
            for (int j = 0; j < userList.size(); j++) {
                if (userList.get(j).peerId == items.get(i).mContact.peerId) {
                    userList.remove(j);
                    break;
                }
            }
        }

        Fragment fragment = ShowCustomList.newInstance(userList, new OnSelectedList() {
            @Override
            public void getSelectedList(boolean result, String message, int countForShowLastMessage, final ArrayList<StructContactInfo> list) {

                //    memberRealmAndRequest(list, countForShowLastMessage);

                for (int i = 0; i < list.size(); i++) {

                    new RequestGroupAddMember().groupAddMember(roomId, list.get(i).peerId, startMessageId);
                }

            }
        });


        Bundle bundle = new Bundle();
        bundle.putBoolean("DIALOG_SHOWING", true);
        bundle.putLong("COUNT_MESSAGE", noLastMessage);
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left).addToBackStack(null).replace(fragmentContainer_group_profile, fragment).commit();
    }

    @Override
    public void onGroupRevokeLink(long roomId, final String inviteLink, final String inviteToken) {
        hideProgressBar();

        Realm realm = Realm.getDefaultInstance();
        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
        final RealmGroupRoom realmGroupRoom = realmRoom.getGroupRoom();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realmGroupRoom.setInvite_link(inviteLink);
                realmGroupRoom.setInvite_token(inviteToken);

            }
        });

        realm.close();

    }

    @Override
    public void onError(int majorCode, int minorCode) {
        hideProgressBar();
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                final Snackbar snack = Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.normal_error), Snackbar.LENGTH_LONG);

                snack.setAction(getString(R.string.cancel), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        snack.dismiss();
                    }
                });
                snack.show();
            }
        });
    }

    @Override
    public void onTimeOut() {

        hideProgressBar();
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                final Snackbar snack = Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.time_out), Snackbar.LENGTH_LONG);

                snack.setAction(getString(R.string.cancel), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        snack.dismiss();
                    }
                });
                snack.show();
            }
        });
    }

    private class CreatePopUpMessage {

        private void show(View view, final StructContactInfo info) {
            PopupMenu popup = new PopupMenu(ActivityGroupProfile.this, view, Gravity.TOP);
            popup.getMenuInflater().inflate(R.menu.menu_item_group_profile, popup.getMenu());

            if (role == GroupChatRole.OWNER) {

                if (info.role.equals(ProtoGlobal.GroupRoom.Role.MEMBER.toString())) {
                    popup.getMenu().getItem(2).setVisible(false);
                    popup.getMenu().getItem(3).setVisible(false);
                } else if (info.role.equals(ProtoGlobal.GroupRoom.Role.ADMIN.toString())) {
                    popup.getMenu().getItem(0).setVisible(false);
                    popup.getMenu().getItem(1).setVisible(false);
                    popup.getMenu().getItem(3).setVisible(false);
                    popup.getMenu().getItem(4).setVisible(false);
                } else if (info.role.equals(ProtoGlobal.GroupRoom.Role.MODERATOR.toString())) {
                    popup.getMenu().getItem(1).setVisible(false);
                    popup.getMenu().getItem(2).setVisible(false);
                    popup.getMenu().getItem(4).setVisible(false);
                }
            } else if (role == GroupChatRole.ADMIN) {

                if (info.role.equals(ProtoGlobal.GroupRoom.Role.MEMBER.toString())) {
                    popup.getMenu().getItem(0).setVisible(false);
                    popup.getMenu().getItem(2).setVisible(false);
                    popup.getMenu().getItem(3).setVisible(false);
                } else if (info.role.equals(ProtoGlobal.GroupRoom.Role.MODERATOR.toString())) {
                    popup.getMenu().getItem(0).setVisible(false);
                    popup.getMenu().getItem(1).setVisible(false);
                    popup.getMenu().getItem(2).setVisible(false);
                    popup.getMenu().getItem(4).setVisible(false);
                }
            } else if (role == GroupChatRole.MODERATOR) {

                if (info.role.equals(ProtoGlobal.GroupRoom.Role.MEMBER.toString())) {
                    popup.getMenu().getItem(0).setVisible(false);
                    popup.getMenu().getItem(1).setVisible(false);
                    popup.getMenu().getItem(2).setVisible(false);
                    popup.getMenu().getItem(3).setVisible(false);
                }
            } else {

                return;
            }

            // Setup menu item selection
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.menu_setAdmin:
                            setToAdmin(info.peerId);
                            return true;
                        case R.id.menu_set_moderator:
                            setToModerator(info.peerId);
                            return true;
                        case R.id.menu_remove_admin:
                            kickAdmin(info.peerId);
                            return true;
                        case R.id.menu_remove_moderator:
                            kickModerator(info.peerId);
                            return true;
                        case R.id.menu_kick:
                            kickMember(info.peerId);
                            return true;
                        default:
                            return false;
                    }
                }
            });
            // Handle dismissal with: popup.setOnDismissListener(...);
            // Show the menu
            popup.show();

        }

        private void updateRole(final long memberId, final ProtoGlobal.GroupRoom.Role role) {
            ContactItemGroupProfile.mainRole = role.toString();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    List<ContactItemGroupProfile> items = itemAdapter.getAdapterItems();

                    for (int i = 0; i < items.size(); i++) {
                        if (items.get(i).mContact.peerId == memberId) {
                            items.get(i).mContact.role = role.toString();
                            if (i < itemAdapter.getAdapterItemCount()) {
                                IItem item = (new ContactItemGroupProfile().setContact(items.get(i).mContact).withIdentifier(100 + i));
                                itemAdapter.set(i, item);
                            }
                        }
                    }
                }
            });
        }

        private void setToAdmin(Long peerId) {
            new RequestGroupAddAdmin().groupAddAdmin(roomId, peerId);
        }

        private void setToModerator(Long peerId) {
            new RequestGroupAddModerator().groupAddModerator(roomId, peerId);
        }

    }

    //***********************************************************************************************************************


    private void updateUi(ArrayList<StructContactInfo> list, Long UserId) {

        StructContactInfo item = null;

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).peerId == UserId) {
                item = list.get(i);
                break;
            }
        }

        contacts.add(item);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                txtMemberNumber.setText(contacts.size() + "");
                int count = items.size();
                final int listSize = contacts.size();
                for (int i = count; i < listSize; i++) {
                    items.add(new ContactItemGroupProfile().setContact(contacts.get(i)).withIdentifier(100 + contacts.indexOf(contacts.get(i))));
                }
                itemAdapter.clear();
                itemAdapter.add(items);
                //                itemAdapter.add(new ContactItemGroupProfile().setContact(contacts.get(0)).withIdentifier(100 + contacts.indexOf(contacts.get(0))));
                txtMore.setVisibility(View.GONE);
            }
        });

    }

    /**
     * add member to realm and send request to server for really added this contacts to this group
     */
    private void memberRealmAndRequest(final ArrayList<StructContactInfo> list, int messageCount) {
        Realm realm = Realm.getDefaultInstance();

        if (messageCount == 0) {
            startMessageId = 0;
        } else {
            RealmResults<RealmRoomMessage> realmRoomMessages = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, roomId).findAllSorted(RealmRoomMessageFields.MESSAGE_ID, Sort.DESCENDING);

            if (messageCount >= realmRoomMessages.size()) {
                // if count is bigger than exist messages get first message id that exist
                RealmResults<RealmRoomMessage> realmRoomMessageRealmResults = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, roomId).findAllSorted(RealmRoomMessageFields.MESSAGE_ID, Sort.ASCENDING);
                for (final RealmRoomMessage realmRoomMessage : realmRoomMessageRealmResults) {

                    if (realmRoomMessage != null) {
                        startMessageId = realmRoomMessage.getMessageId();
                        break;
                    }
                }
            } else {

                for (final RealmRoomMessage realmRoomMessage : realmRoomMessages) {
                    messageCount--;
                    if (messageCount == 0) {
                        startMessageId = realmRoomMessage.getMessageId();
                    }
                }
            }
        }

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                final RealmList<RealmMember> members = new RealmList<>();
                for (int i = 0; i < list.size(); i++) {
                    long peerId = list.get(i).peerId;
                    //add member to realm
                    RealmMember realmMember = new RealmMember();

                    realmMember.setId(SUID.id().get());
                    realmMember.setPeerId(peerId);
                    realmMember.setRole(ProtoGlobal.GroupRoom.Role.MEMBER.toString());
                    realmMember = realm.copyToRealm(realmMember);

                    members.add(realmMember);

                    //request for add member
                    new RequestGroupAddMember().groupAddMember(roomId, peerId, startMessageId);
                }

                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
                RealmList<RealmMember> memberList = realmRoom.getGroupRoom().getMembers();

                for (int i = 0; i < members.size(); i++) {
                    long id = members.get(i).getPeerId();
                    boolean canAdd = true;
                    for (int j = 0; j < memberList.size(); j++) {
                        if (memberList.get(j).getPeerId() == id) {
                            canAdd = false;
                            break;
                        }
                    }
                    if (canAdd) {
                        memberList.add(members.get(i));
                    }
                }
            }
        });

        realm.close();
    }

    //***********************************************************************************************************************

    private void ChangeGroupName() {


        final LinearLayout layoutUserName = new LinearLayout(ActivityGroupProfile.this);
        layoutUserName.setOrientation(LinearLayout.VERTICAL);

        final View viewUserName = new View(ActivityGroupProfile.this);
        LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);

        final TextInputLayout inputUserName = new TextInputLayout(ActivityGroupProfile.this);
        final EditText edtUserName = new EditText(ActivityGroupProfile.this);
        edtUserName.setHint(getResources().getString(R.string.st_username));
        edtUserName.setText(txtGroupNameTitle.getText().toString());
        edtUserName.setTextColor(getResources().getColor(R.color.text_edit_text));
        edtUserName.setHintTextColor(getResources().getColor(R.color.hint_edit_text));
        edtUserName.setPadding(0, 8, 0, 8);
        edtUserName.setSingleLine(true);
        inputUserName.addView(edtUserName);
        inputUserName.addView(viewUserName, viewParams);

        viewUserName.setBackgroundColor(getResources().getColor(R.color.line_edit_text));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            edtUserName.setBackground(getResources().getDrawable(android.R.color.transparent));
        }
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        layoutUserName.addView(inputUserName, layoutParams);

        final MaterialDialog dialog = new MaterialDialog.Builder(ActivityGroupProfile.this).title(getResources().getString(R.string.group_name)).positiveText(getResources().getString(R.string.save)).customView(layoutUserName, true).widgetColor(getResources().getColor(R.color.toolbar_background)).negativeText(getResources().getString(R.string.B_cancel)).build();

        final View positive = dialog.getActionButton(DialogAction.POSITIVE);
        positive.setEnabled(false);

        final String finalUserName = txtGroupNameTitle.getText().toString();
        edtUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!edtUserName.getText().toString().equals(finalUserName)) {
                    positive.setEnabled(true);
                } else {
                    positive.setEnabled(false);
                }
            }
        });

        G.onGroupEdit = new OnGroupEdit() {
            @Override
            public void onGroupEdit(long roomId, String name, String description) {
                hideProgressBar();
                txtGroupNameTitle.setText(name);
            }

            @Override
            public void onError(int majorCode, int minorCode) {

            }

            @Override
            public void onTimeOut() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final Snackbar snack = Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.time_out), Snackbar.LENGTH_LONG);

                        snack.setAction(getString(R.string.cancel), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                snack.dismiss();
                            }
                        });
                        snack.show();
                    }
                });
            }
        };

        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new RequestGroupEdit().groupEdit(roomId, edtUserName.getText().toString(), txtGroupDescription.getText().toString());
                dialog.dismiss();
            }
        });

        edtUserName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    viewUserName.setBackgroundColor(getResources().getColor(R.color.toolbar_background));
                } else {
                    viewUserName.setBackgroundColor(getResources().getColor(R.color.line_edit_text));
                }
            }
        });
        dialog.show();

    }

    private void ChangeGroupDescription() {
        MaterialDialog dialog = new MaterialDialog.Builder(ActivityGroupProfile.this).title(R.string.group_description).positiveText(getString(R.string.save)).alwaysCallInputCallback().widgetColor(getResources().getColor(R.color.toolbar_background)).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                G.onGroupEdit = new OnGroupEdit() {
                    @Override
                    public void onGroupEdit(final long roomId, final String name, final String descriptions) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                description = descriptions;
                                txtGroupDescription.setText(descriptions);
                            }
                        });
                    }

                    @Override
                    public void onError(int majorCode, int minorCode) {

                    }

                    @Override
                    public void onTimeOut() {

                    }
                };

                new RequestGroupEdit().groupEdit(roomId, txtGroupName.getText().toString(), tmp);
            }
        }).negativeText(getString(R.string.cancel)).inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_TEXT).input(getString(R.string.please_enter_group_description), txtGroupDescription.getText().toString(), new MaterialDialog.InputCallback() {
            @Override
            public void onInput(MaterialDialog dialog, CharSequence input) {
                // Do something

                View positive = dialog.getActionButton(DialogAction.POSITIVE);
                tmp = input.toString();
                if (!input.toString().equals(txtGroupDescription.getText().toString())) {

                    positive.setClickable(true);
                    positive.setAlpha(1.0f);
                } else {
                    positive.setClickable(false);
                    positive.setAlpha(0.5f);
                }
            }
        }).show();
    }

    private void groupLeft() {

        String text = "";
        if (role == GroupChatRole.OWNER || role == GroupChatRole.ADMIN) {
            text = getString(R.string.do_you_want_to_delete_this_group);
        } else {
            text = getString(R.string.do_you_want_to_leave_this_group);
        }

        new MaterialDialog.Builder(ActivityGroupProfile.this).content(text).positiveText(R.string.ok).negativeText(R.string.cancel).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                G.onGroupLeft = new OnGroupLeft() {
                    @Override
                    public void onGroupLeft(final long roomId, long memberId) {


                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ActivityGroupProfile.this.finish();
                                if (ActivityChat.activityChat != null) {
                                    ActivityChat.activityChat.finish();
                                }
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                prgWait.setVisibility(View.GONE);
                            }
                        });
                    }

                    @Override
                    public void onError(int majorCode, int minorCode) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                prgWait.setVisibility(View.GONE);
                            }
                        });
                    }

                    @Override
                    public void onTimeOut() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                prgWait.setVisibility(View.GONE);
                            }
                        });
                    }
                };

                G.onGroupDelete = new OnGroupDelete() {
                    @Override
                    public void onGroupDelete(final long roomId) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ActivityGroupProfile.this.finish();
                                if (ActivityChat.activityChat != null) {
                                    ActivityChat.activityChat.finish();
                                }

                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                prgWait.setVisibility(View.GONE);
                            }
                        });
                    }

                    @Override
                    public void Error(int majorCode, int minorCode) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                prgWait.setVisibility(View.GONE);
                            }
                        });

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                final Snackbar snack = Snackbar.make(findViewById(android.R.id.content), "Just owner can delete", Snackbar.LENGTH_LONG);
                                snack.setAction("CANCEL", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        snack.dismiss();
                                    }
                                });
                                snack.show();
                            }
                        });

                    }

                    @Override
                    public void onTimeOut() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                prgWait.setVisibility(View.GONE);
                            }
                        });
                    }
                };

                if (role == GroupChatRole.OWNER || role == GroupChatRole.ADMIN) {
                    new RequestGroupDelete().groupDelete(roomId);
                } else {
                    new RequestGroupLeft().groupLeft(roomId);
                }
                prgWait.setVisibility(View.VISIBLE);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            }
        }).show();
    }

    private void updateRole(final long memberId, final ProtoGlobal.GroupRoom.Role role) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {


                List<ContactItemGroupProfile> items = itemAdapter.getAdapterItems();
                for (int i = 0; i < items.size(); i++) {
                    if (items.get(i).mContact.peerId == memberId) {
                        items.get(i).mContact.role = role.toString();
                        if (i < itemAdapter.getAdapterItemCount()) {
                            IItem item = (new ContactItemGroupProfile().setContact(items.get(i).mContact).withIdentifier(100 + i));
                            itemAdapter.set(i, item);
                        }
                    }
                }
            }
        });
    }

    /**
     * if user was admin set  role to member
     */
    private void kickAdmin(final long memberID) {

        new MaterialDialog.Builder(ActivityGroupProfile.this).content(R.string.do_you_want_to_set_admin_role_to_member).positiveText(R.string.ok).negativeText(R.string.cancel).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                new RequestGroupKickAdmin().groupKickAdmin(roomId, memberID);
            }
        }).show();
    }

    /**
     * delete this member from list of member group
     */
    private void kickMember(final long memberID) {

        new MaterialDialog.Builder(ActivityGroupProfile.this).content(R.string.do_you_want_to_kick_this_member).positiveText(R.string.ok).negativeText(R.string.cancel).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {


                new RequestGroupKickMember().groupKickMember(roomId, memberID);
            }
        }).show();
    }

    private void onGroupAddMemberCallback() {
        G.onGroupAddMember = new OnGroupAddMember() {
            @Override
            public void onGroupAddMember(final Long roomIdUser, final Long UserId) {

                final List<ContactItemGroupProfile> items = itemAdapter.getAdapterItems();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        txtMemberNumber.setText((items.size() + 1) + "");
                    }
                });
                        /*runOnUiThread(new Runnable() {
                                          @Override
                                          public void run() {

                                              List<ContactItemGroupProfile> items = itemAdapter.getAdapterItems();

                                              for (int i = 0; i < items.size(); i++) {
                                                  if (items.get(i).mContact.peerId == UserId) {
                                                      items.get(i).mContact.role = role.toString();
                                                      if (i < itemAdapter.getAdapterItemCount()) {
                                                          IItem item = (new ContactItemGroupProfile().setContact(items.get(i).mContact).withIdentifier(100 + i));
                                                          itemAdapter.set(i, item);
                                                      }
                                                  }
                                              }
                                          }
                                      }
                        );*/


                runOnUiThread(new Runnable() { //TODO [Saeed Mozaffari] [2016-11-12 5:15 PM] - get member list from group and add new member . like get member list response
                    @Override
                    public void run() {
                        Realm realm = Realm.getDefaultInstance();
                        RealmRegisteredInfo realmRegistered = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, UserId).findFirst();

                        if (realmRegistered != null) {
                            StructContactInfo struct = new StructContactInfo(realmRegistered.getId(), realmRegistered.getDisplayName(), realmRegistered.getStatus(), false, false, realmRegistered.getPhoneNumber() + "");
                            struct.avatar = realmRegistered.getLastAvatar();
                            struct.initials = realmRegistered.getInitials();
                            struct.color = realmRegistered.getColor();
                            struct.lastSeen = realmRegistered.getLastSeen();
                            struct.status = realmRegistered.getStatus();
                            IItem item = (new ContactItemGroupProfile().setContact(struct).withIdentifier(SUID.id().get()));
                            itemAdapter.add(item);

                        } else {

                            if (roomIdUser == roomId) {
                                new RequestUserInfo().userInfo(UserId, roomId + "");
                            }
                        }

                        realm.close();
                    }
                });


                //                        updateUi(list, UserId);
            }

            @Override
            public void onError(int majorCode, int minorCode) {

            }
        };
    }

    private void onGroupKickMemberCallback() {
        G.onGroupKickMember = new OnGroupKickMember() {
            @Override
            public void onGroupKickMember(final long roomId, final long memberId) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        final List<ContactItemGroupProfile> items = itemAdapter.getAdapterItems();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                txtMemberNumber.setText((items.size() - 1) + "");
                            }
                        });
                        for (int i = 0; i < items.size(); i++) {
                            if (items.get(i).mContact.peerId == memberId) {
                                itemAdapter.remove(i);
                                Realm realm = Realm.getDefaultInstance();
                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
                                        for (RealmMember realmMember : realmRoom.getGroupRoom().getMembers()) {
                                            if (realmMember.getPeerId() == memberId) {
                                                realmMember.deleteFromRealm();
                                                participantsCountLabel = realmRoom.getGroupRoom().getParticipantsCountLabel();
                                                participantsCountLabel = (Integer.parseInt(participantsCountLabel) - 1) + "";
                                                realmRoom.getGroupRoom().setParticipantsCountLabel(participantsCountLabel);
                                                break;
                                            }
                                        }

                                    }
                                });
                                realm.close();
                            }
                        }
                    }
                });

            }

            @Override
            public void onError(int majorCode, int minorCode) {

            }

            @Override
            public void onTimeOut() {

            }
        };
    }

    private void onGroupKickModeratorCallback() {
        G.onGroupKickModerator = new OnGroupKickModerator() {
            @Override
            public void onGroupKickModerator(long roomId, long memberId) {

                updateRole(memberId, ProtoGlobal.GroupRoom.Role.MEMBER);

                if (G.updateListAfterKick != null) {
                    G.updateListAfterKick.updateList(memberId, ProtoGlobal.GroupRoom.Role.MEMBER);
                }

                //                                for (int i = 0; i < contacts.size(); i++) {
                //                                    if (contacts.get(i).peerId == memberId) {
                //                                        contacts.get(i).role =
                //                                                ProtoGlobal.GroupRoom.Role.MEMBER.toString();
                //                                        final int finalI = i;
                //                                        runOnUiThread(new Runnable() {
                //                                            @Override
                //                                            public void run() {
                //                                                IItem item = (new ContactItemGroupProfile().setContact(
                //                                                        contacts.get(finalI))
                //                                                        .withIdentifier(
                //                                                                100 + contacts.indexOf(contacts.get(finalI))));
                //                                                itemAdapter.set(finalI, item);
                //                                            }
                //                                        });
                //
                //                                        break;
                //                                    }
                //                                }
            }

            @Override
            public void onError(int majorCode, final int minorCode) {
            }

            @Override
            public void timeOut() {

            }
        };
    }

    private void onGroupAddModeratorCallback() {
        G.onGroupAddModerator = new OnGroupAddModerator() {
            @Override
            public void onGroupAddModerator(long roomId, final long memberId) {

                updateRole(memberId, ProtoGlobal.GroupRoom.Role.MODERATOR);

                //                    runOnUiThread(new Runnable() {
                //                        @Override
                //                        public void run() {
                //                            for (int i = 0; i < contacts.size(); i++) {
                //                                if (contacts.get(i).peerId == memberId) {
                //                                    contacts.get(i).role = ProtoGlobal.GroupRoom.Role.MODERATOR.toString();
                //
                //                                    if (i < itemAdapter.getAdapterItemCount()) {
                //                                        IItem item = (new ContactItemGroupProfile().setContact(contacts.get(i)).withIdentifier
                //                                                (100 + contacts.indexOf(contacts.get(i))));
                //                                        itemAdapter.set(i, item);
                //                                    }
                //                                    break;
                //                                }
                //                            }
                //                        }
                //                    });
            }

            @Override
            public void onError(int majorCode, final int minorCode) {

            }

            @Override
            public void onTimeOut() {

            }
        };

    }

    private void onGroupKickAdminCallback() {
        G.onGroupKickAdmin = new OnGroupKickAdmin() {
            @Override
            public void onGroupKickAdmin(long roomId, long memberId) {

                updateRole(memberId, ProtoGlobal.GroupRoom.Role.MEMBER);

                if (G.updateListAfterKick != null) {
                    G.updateListAfterKick.updateList(memberId, ProtoGlobal.GroupRoom.Role.MEMBER);
                }
            }

            @Override
            public void onError(int majorCode, final int minorCode) {

            }

            @Override
            public void onTimeOut() {

            }
        };
    }

    private void onSetAdminCallback() {
        G.onGroupAddAdmin = new OnGroupAddAdmin() {
            @Override
            public void onGroupAddAdmin(long roomId, final long memberId) {

                updateRole(memberId, ProtoGlobal.GroupRoom.Role.ADMIN);

                if (G.updateListAfterKick != null) {
                    G.updateListAfterKick.updateList(memberId, ProtoGlobal.GroupRoom.Role.ADMIN);
                }
            }

            @Override
            public void onError(int majorCode, int minorCode) {

            }
        };
    }


    private void kickModerator(final long memberID) {

        new MaterialDialog.Builder(ActivityGroupProfile.this).content(R.string.do_you_want_to_set_modereator_role_to_member).positiveText(R.string.ok).negativeText(R.string.cancel).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                new RequestGroupKickModerator().groupKickModerator(roomId, memberID);
            }
        }).show();
    }

    private void setMemberRoleToModerator() {
        Fragment fragment = FragmentListAdmin.newInstance(getCurrentUser(ProtoGlobal.GroupRoom.Role.MODERATOR));
        Bundle bundle = new Bundle();
        bundle.putString("TYPE", "MODERATOR");
        bundle.putLong("ID", roomId);
        bundle.putString("ROOM_TYPE", ProtoGlobal.Room.Type.GROUP.toString());
        bundle.putBoolean("DIALOG_SHOWING", false);
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left).addToBackStack(null).replace(fragmentContainer_group_profile, fragment).commit();
    }

    private void setMemberRoleToAdmin() {
        Fragment fragment = FragmentListAdmin.newInstance(getCurrentUser(ProtoGlobal.GroupRoom.Role.ADMIN));
        Bundle bundle = new Bundle();
        bundle.putBoolean("DIALOG_SHOWING", false);
        bundle.putLong("ID", roomId);
        bundle.putString("ROOM_TYPE", ProtoGlobal.Room.Type.GROUP.toString());
        bundle.putString("TYPE", "ADMIN");
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left).addToBackStack(null).replace(fragmentContainer_group_profile, fragment).commit();
    }

    private static class UploadTask extends AsyncTask<Object, FileUploadStructure, FileUploadStructure> {

        private ProgressBar prg;
        private Activity myActivityReference;

        public UploadTask(ProgressBar prg, Activity myActivityReference) {
            this.prg = prg;
            this.myActivityReference = myActivityReference;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected FileUploadStructure doInBackground(Object... params) {
            try {
                String filePath = (String) params[0];
                long avatarId = (long) params[1];
                File file = new File(filePath);
                String fileName = file.getName();
                long fileSize = file.length();
                FileUploadStructure fileUploadStructure = new FileUploadStructure(fileName, fileSize, filePath, avatarId);
                fileUploadStructure.openFile(filePath);

                byte[] fileHash = AndroidUtils.getFileHash(fileUploadStructure);
                fileUploadStructure.setFileHash(fileHash);

                return fileUploadStructure;
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(FileUploadStructure result) {
            super.onPostExecute(result);
            G.uploaderUtil.startUploading(result, Long.toString(result.messageId));
        }
    }

    public class StickyHeaderAdapter extends AbstractAdapter implements StickyRecyclerHeadersAdapter {
        @Override
        public long getHeaderId(int position) {
            IItem item = getItem(position);

            //            ContactItemGroupProfile ci=(ContactItemGroupProfile)item;
            //            if(ci!=null){
            //                return ci.mContact.displayName.toUpperCase().charAt(0);
            //            }

            return -1;
        }

        @Override
        public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
            //we create the view for the header
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_header_item, parent, false);
            return new RecyclerView.ViewHolder(view) {
            };
        }

        @Override
        public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
            CustomTextViewMedium textView = (CustomTextViewMedium) holder.itemView;

            IItem item = getItem(position);
            if (((ContactItemGroupProfile) item).mContact != null) {
                //based on the position we set the headers text
                textView.setText(String.valueOf(((ContactItemGroupProfile) item).mContact.displayName.toUpperCase().charAt(0)));
            }
        }


        /**
         * REQUIRED FOR THE FastAdapter. Set order to < 0 to tell the FastAdapter he can ignore
         * this
         * one.
         *
         * @return int
         */
        @Override
        public int getOrder() {
            return -100;
        }

        @Override
        public int getAdapterItemCount() {
            return 0;
        }

        @Override
        public List<IItem> getAdapterItems() {
            return null;
        }

        @Override
        public IItem getAdapterItem(int position) {
            return null;
        }

        @Override
        public int getAdapterPosition(IItem item) {
            return -1;
        }

        @Override
        public int getAdapterPosition(long identifier) {
            return 0;
        }

        @Override
        public int getGlobalPosition(int position) {
            return -1;
        }

    }

    private int setGroupParticipantLable() {

        final int[] _member = {0};

        Realm realm = Realm.getDefaultInstance();

        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
        final RealmGroupRoom realmGroupRoom = realmRoom.getGroupRoom();
        realmGroupRoom.getMembers();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                _member[0] = realmGroupRoom.getMembers().size();
                realmGroupRoom.setParticipantsCountLabel(_member[0] + "");
            }
        });

        realm.close();

        return _member[0];
    }

    //********** compare member list

    private void compareMemberList(final RealmList<RealmMember> memberList, final List<ProtoGroupGetMemberList.GroupGetMemberListResponse.Member> serverLiseMember) {


        final ArrayList<Long> b1 = new ArrayList<>();
        for (RealmMember r : memberList) {
            b1.add(r.getPeerId());
        }
        ArrayList<Long> c1 = new ArrayList<>();
        for (int i = 0; i < memberList.size(); i++) {
            for (int j = 0; j < serverLiseMember.size(); j++) {
                if (serverLiseMember.get(j).getUserId() == memberList.get(i).getPeerId()) {
                    c1.add(memberList.get(i).getPeerId());
                    break;
                }
            }
        }
        b1.removeAll(c1);
        Realm realm = Realm.getDefaultInstance();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
                RealmList<RealmMember> realmMembers = realmRoom.getGroupRoom().getMembers();
                for (int i = 0; i < realmMembers.size(); i++) {
                    for (int j = 0; j < b1.size(); j++) {

                        if (realmMembers.get(i).getPeerId() == b1.get(j)) {
                            realmMembers.get(i).deleteFromRealm();
                            itemAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });
        realm.close();

        final List<ContactItemGroupProfile> items = itemAdapter.getAdapterItems();
        for (int i = 0; i < b1.size(); i++) {
            for (int j = 0; j < items.size(); j++) {
                if (items.get(j).mContact.peerId == b1.get(i)) {
                    itemAdapter.remove(i);
                    itemAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    //********** Avatars

    //***Get Avatar

    private void showAvatar() {
        HelperAvatar.getAvatar(roomId, HelperAvatar.AvatarType.ROOM, new OnAvatarGet() {
            @Override
            public void onAvatarGet(final String avatarPath, long ownerId) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ImageLoader.getInstance().displayImage(AndroidUtils.suitablePath(avatarPath), imvGroupAvatar);
                    }
                });
            }

            @Override
            public void onShowInitials(final String initials, final String color) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imvGroupAvatar.setImageBitmap(com.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) imvGroupAvatar.getContext().getResources().getDimension(R.dimen.dp60), initials, color));
                    }
                });
            }
        });
    }

    //***Add Avatar

    @Override
    public void onAvatarAdd(final long roomId, final ProtoGlobal.Avatar avatar) {
        hideProgressBar();
        /**
         * if another account do this action we haven't avatar source and have
         *  to download avatars . for do this action call HelperAvatar.getAvatar
         */
        if (filePathAvatar == null) {
            showAvatar();
        } else {
            HelperAvatar.avatarAdd(roomId, filePathAvatar, avatar, new OnAvatarAdd() {
                @Override
                public void onAvatarAdd(final String avatarPath) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ImageLoader.getInstance().displayImage(AndroidUtils.suitablePath(avatarPath), imvGroupAvatar);
                        }
                    });
                }
            });
            filePathAvatar = null;
        }
    }

    @Override
    public void onAvatarAddError() {
        hideProgressBar();
    }

    //***Delete Avatar

    @Override
    public void onDeleteAvatar(long roomId, long avatarId) {
        hideProgressBar();
        HelperAvatar.avatarDelete(roomId, avatarId, HelperAvatar.AvatarType.ROOM, new OnAvatarDelete() {
            @Override
            public void latestAvatarPath(final String avatarPath) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ImageLoader.getInstance().displayImage(AndroidUtils.suitablePath(avatarPath), imvGroupAvatar);
                    }
                });
            }

            @Override
            public void showInitials(final String initials, final String color) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imvGroupAvatar.setImageBitmap(com.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) imvGroupAvatar.getContext().getResources().getDimension(R.dimen.dp60), initials, color));
                    }
                });
            }
        });
    }

    @Override
    public void onDeleteAvatarError(int majorCode, int minorCode) {


    }
    //***Show And Hide Progress

    private void showProgressBar() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                prgWait.setVisibility(View.VISIBLE);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        });
    }

    private void hideProgressBar() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                prgWait.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        });
    }

    private void dialogWaitTime(int title, long time, int majorCode) {
        boolean wrapInScrollView = true;
        final MaterialDialog dialog = new MaterialDialog.Builder(ActivityGroupProfile.this)
                .title(title)
                .customView(R.layout.dialog_remind_time, wrapInScrollView)
                .positiveText(R.string.B_ok)
                .autoDismiss(false)
                .canceledOnTouchOutside(false)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .show();

        View v = dialog.getCustomView();

        final TextView remindTime = (TextView) v.findViewById(R.id.remindTime);
        CountDownTimer countWaitTimer = new CountDownTimer(time * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int seconds = (int) ((millisUntilFinished) / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;
                remindTime.setText("" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds));
//                dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
            }

            @Override
            public void onFinish() {
//                dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
            }
        };
        countWaitTimer.start();
    }

}
