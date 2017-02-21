package com.iGap.activities;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
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
import com.iGap.Config;
import com.iGap.G;
import com.iGap.R;
import com.iGap.adapter.StickyHeaderAdapter;
import com.iGap.adapter.items.ContactItemGroupProfile;
import com.iGap.fragments.FragmentListAdmin;
import com.iGap.fragments.FragmentNotification;
import com.iGap.fragments.FragmentShowAvatars;
import com.iGap.fragments.ShowCustomList;
import com.iGap.helper.HelperAvatar;
import com.iGap.helper.HelperCalander;
import com.iGap.helper.HelperPermision;
import com.iGap.helper.HelperString;
import com.iGap.helper.ImageHelper;
import com.iGap.interfaces.OnAvatarAdd;
import com.iGap.interfaces.OnAvatarDelete;
import com.iGap.interfaces.OnAvatarGet;
import com.iGap.interfaces.OnChannelAddAdmin;
import com.iGap.interfaces.OnChannelAddMember;
import com.iGap.interfaces.OnChannelAddModerator;
import com.iGap.interfaces.OnChannelAvatarAdd;
import com.iGap.interfaces.OnChannelAvatarDelete;
import com.iGap.interfaces.OnChannelCheckUsername;
import com.iGap.interfaces.OnChannelDelete;
import com.iGap.interfaces.OnChannelEdit;
import com.iGap.interfaces.OnChannelGetMemberList;
import com.iGap.interfaces.OnChannelKickAdmin;
import com.iGap.interfaces.OnChannelKickMember;
import com.iGap.interfaces.OnChannelKickModerator;
import com.iGap.interfaces.OnChannelLeft;
import com.iGap.interfaces.OnChannelRemoveUsername;
import com.iGap.interfaces.OnChannelRevokeLink;
import com.iGap.interfaces.OnChannelUpdateSignature;
import com.iGap.interfaces.OnChannelUpdateUsername;
import com.iGap.interfaces.OnFileUploadForActivities;
import com.iGap.interfaces.OnGetPermission;
import com.iGap.interfaces.OnMenuClick;
import com.iGap.interfaces.OnSelectedList;
import com.iGap.interfaces.OnUserInfoResponse;
import com.iGap.libs.rippleeffect.RippleView;
import com.iGap.module.AndroidUtils;
import com.iGap.module.AttachFile;
import com.iGap.module.Contacts;
import com.iGap.module.FileUploadStructure;
import com.iGap.module.MaterialDesignTextView;
import com.iGap.module.OnComplete;
import com.iGap.module.SUID;
import com.iGap.module.StructContactInfo;
import com.iGap.proto.ProtoChannelCheckUsername;
import com.iGap.proto.ProtoChannelGetMemberList;
import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.RealmAvatar;
import com.iGap.realm.RealmAvatarFields;
import com.iGap.realm.RealmChannelRoom;
import com.iGap.realm.RealmMember;
import com.iGap.realm.RealmRegisteredInfo;
import com.iGap.realm.RealmRegisteredInfoFields;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.RealmRoomFields;
import com.iGap.realm.RealmUserInfo;
import com.iGap.realm.enums.ChannelChatRole;
import com.iGap.request.RequestChannelAddAdmin;
import com.iGap.request.RequestChannelAddMember;
import com.iGap.request.RequestChannelAddModerator;
import com.iGap.request.RequestChannelAvatarAdd;
import com.iGap.request.RequestChannelCheckUsername;
import com.iGap.request.RequestChannelDelete;
import com.iGap.request.RequestChannelEdit;
import com.iGap.request.RequestChannelGetMemberList;
import com.iGap.request.RequestChannelKickAdmin;
import com.iGap.request.RequestChannelKickMember;
import com.iGap.request.RequestChannelKickModerator;
import com.iGap.request.RequestChannelLeft;
import com.iGap.request.RequestChannelRemoveUsername;
import com.iGap.request.RequestChannelRevokeLink;
import com.iGap.request.RequestChannelUpdateSignature;
import com.iGap.request.RequestChannelUpdateUsername;
import com.iGap.request.RequestUserInfo;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.IItemAdapter;
import com.mikepenz.fastadapter.adapters.HeaderAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.iGap.G.context;
import static com.iGap.realm.enums.RoomType.GROUP;

public class ActivityChannelProfile extends AppCompatActivity implements OnChannelAddMember, OnChannelKickMember, OnChannelAddModerator, OnChannelKickModerator, OnChannelAddAdmin, OnChannelKickAdmin, OnChannelGetMemberList, OnUserInfoResponse, OnChannelDelete, OnChannelLeft, OnChannelEdit, OnFileUploadForActivities, OnChannelAvatarAdd, OnChannelAvatarDelete, OnChannelRevokeLink {

    private AppBarLayout appBarLayout;
    private TextView txtDescription, txtChannelLink, txtChannelNameInfo;
    private MaterialDesignTextView imgPupupMenul;
    private de.hdodenhof.circleimageview.CircleImageView imgCircleImageView;
    private FloatingActionButton fab;
    private PopupWindow popupWindow;
    private TextView titleToolbar;
    private TextView txtChannelName;
    TextView txtSharedMedia;
    private EditText edtRevoke;
    private TextView txtMore;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private String title;
    private String initials;
    private String color;
    private String participantsCountLabel;
    private String description;
    private String inviteLink;
    private String pathSaveImage;
    private ChannelChatRole role;
    private long noLastMessage;
    private long userId;
    private RealmList<RealmMember> members;
    private static ProgressBar prgWait;
    private LinearLayout lytListAdmin;
    private LinearLayout lytListModerator;
    private LinearLayout lytDeleteChannel;
    private LinearLayout lytNotification;
    private AttachFile attachFile;
    private long avatarId;
    private long roomId;
    private boolean isPrivate;
    private String linkUsername;
    private boolean isSignature;
    private TextView txtLinkTitle;
    private boolean isPopup = false;
    //mollareza
    //private int firstLimit = 0;
    //private int lastLimit = 50;

    @Override
    protected void onResume() {

        ActivityShearedMedia.getCountOfSharedMedia(roomId, txtSharedMedia.getText().toString(), new OnComplete() {
            @Override
            public void complete(boolean result, final String messageOne, String MessageTow) {
                txtSharedMedia.post(new Runnable() {
                    @Override
                    public void run() {
                        txtSharedMedia.setText(messageOne);
                        if (HelperCalander.isLanguagePersian) {
                            txtSharedMedia.setText(HelperCalander.convertToUnicodeFarsiNumber(messageOne));
                        } else {
                            txtSharedMedia.setText(messageOne);
                        }
                    }
                });
            }
        });

        super.onResume();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_channel);

        G.uploaderUtil.setActivityCallbacks(this);
        G.onChannelAddMember = this;
        G.onChannelKickMember = this;
        G.onChannelAddAdmin = this;
        G.onChannelKickAdmin = this;
        G.onChannelAddModerator = this;
        G.onChannelKickModerator = this;
        G.onChannelGetMemberList = this;
        G.onUserInfoResponse = this;
        G.onChannelDelete = this;
        G.onChannelLeft = this;
        G.onChannelEdit = this;
        G.onChannelRevokeLink = this;
        G.onChannelAvatarAdd = this;

        //=========Put Extra Start
        Bundle extras = getIntent().getExtras();
        roomId = extras.getLong(Config.PutExtraKeys.CHANNEL_PROFILE_ROOM_ID_LONG.toString());

        Realm realm = Realm.getDefaultInstance();

        //channel info
        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
        if (realmRoom == null || realmRoom.getChannelRoom() == null) {
            //HelperError.showSnackMessage(getClientErrorCode(-2, 0));
            finish();
            return;
        }
        RealmChannelRoom realmChannelRoom = realmRoom.getChannelRoom();
        title = realmRoom.getTitle();
        initials = realmRoom.getInitials();
        color = realmRoom.getColor();
        role = realmChannelRoom.getRole();
        inviteLink = realmChannelRoom.getInviteLink();
        isPrivate = realmChannelRoom.isPrivate();
        linkUsername = realmChannelRoom.getUsername();
        isSignature = realmChannelRoom.isSignature();
        fab = (FloatingActionButton) findViewById(R.id.pch_fab_addToChannel);
        try {
            if (realmRoom.getLastMessage() != null) {
                noLastMessage = realmRoom.getLastMessage().getMessageId();
            }
        } catch (NullPointerException e) {
            e.getStackTrace();
        }
        participantsCountLabel = realmChannelRoom.getParticipantsCountLabel();
        members = realmChannelRoom.getMembers();

        description = realmChannelRoom.getDescription();

        RealmUserInfo userInfo = realm.where(RealmUserInfo.class).findFirst();
        if (userInfo != null) userId = userInfo.getUserId();

        //realm.close();
        //=========Put Extra End

        txtSharedMedia = (TextView) findViewById(R.id.txt_shared_media);
        txtChannelNameInfo = (TextView) findViewById(R.id.txt_channel_name_info);
        prgWait = (ProgressBar) findViewById(R.id.agp_prgWaiting);
        LinearLayout lytSharedMedia = (LinearLayout) findViewById(R.id.lyt_shared_media);
        LinearLayout lytChannelName = (LinearLayout) findViewById(R.id.lyt_channel_name);
        LinearLayout lytChannelDescription = (LinearLayout) findViewById(R.id.lyt_description);
        lytListAdmin = (LinearLayout) findViewById(R.id.lyt_list_admin);
        lytListModerator = (LinearLayout) findViewById(R.id.lyt_list_moderator);
        lytDeleteChannel = (LinearLayout) findViewById(R.id.lyt_delete_channel);
        lytNotification = (LinearLayout) findViewById(R.id.lyt_notification);
        txtLinkTitle = (TextView) findViewById(R.id.txt_channel_link_title);
        ViewGroup vgRootAddMember = (ViewGroup) findViewById(R.id.agp_root_layout_add_member);
        ViewGroup ltLink = (ViewGroup) findViewById(R.id.layout_channel_link);
        imgPupupMenul = (MaterialDesignTextView) findViewById(R.id.pch_img_menuPopup);
        txtDescription = (TextView) findViewById(R.id.txt_description);
        if (role == ChannelChatRole.MEMBER) {
            vgRootAddMember.setVisibility(View.GONE);
        }

        ViewGroup vgSignature = (ViewGroup) findViewById(R.id.agp_layout_signature);
        if (role == ChannelChatRole.OWNER) {

            vgSignature.setVisibility(View.VISIBLE);
        } else {
            lytChannelName.setEnabled(false);
            lytChannelDescription.setEnabled(false);
        }

        if (isPrivate && (role == ChannelChatRole.OWNER || role == ChannelChatRole.ADMIN)) {
            ltLink.setVisibility(View.VISIBLE);
        } else {
            ltLink.setVisibility(View.GONE);
        }

        if (!isPrivate) {
            ltLink.setVisibility(View.VISIBLE);
        }

        if (role == ChannelChatRole.OWNER || role == ChannelChatRole.ADMIN) {

            fab.setVisibility(View.VISIBLE);

            imgPupupMenul.setVisibility(View.VISIBLE);
        } else {
            fab.setVisibility(View.GONE);
            imgPupupMenul.setVisibility(View.GONE);
        }


        if (role != ChannelChatRole.OWNER) {
            if (description.length() == 0) {
                lytChannelDescription.setVisibility(View.GONE);
            }
        }

        lytListAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                membersList(ProtoGlobal.ChannelRoom.Role.ADMIN);
            }
        });

        lytListModerator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                membersList(ProtoGlobal.ChannelRoom.Role.MODERATOR);
            }
        });

        lytDeleteChannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteChannel();
            }
        });

        lytNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notificationAndSound();
            }
        });

        final RippleView rippleBack = (RippleView) findViewById(R.id.pch_ripple_back);
        rippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                finish();
            }
        });
        appBarLayout = (AppBarLayout) findViewById(R.id.pch_appbar);
        appBarLayout.setBackgroundColor(Color.parseColor(G.appBarColor));

        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.acp_ll_collapsing_toolbar_layout);
        collapsingToolbarLayout.setBackgroundColor(Color.parseColor(G.appBarColor));
        collapsingToolbarLayout.setContentScrimColor(Color.parseColor(G.appBarColor));

        titleToolbar = (TextView) findViewById(R.id.pch_txt_titleToolbar);
        titleToolbar.setText("" + title);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                ViewGroup viewGroup = (ViewGroup) findViewById(R.id.pch_root_circleImage);
                if (verticalOffset < -5) {
                    viewGroup.animate().alpha(0).setDuration(700);
                    viewGroup.setVisibility(View.GONE);
                    titleToolbar.setVisibility(View.VISIBLE);
                    titleToolbar.animate().alpha(1).setDuration(300);

                } else {
                    viewGroup.setVisibility(View.VISIBLE);
                    viewGroup.animate().alpha(1).setDuration(700);
                    titleToolbar.setVisibility(View.GONE);
                    titleToolbar.animate().alpha(0).setDuration(500);

                }
            }
        });


        RippleView rippleMenu = (RippleView) findViewById(R.id.pch_ripple_menuPopup);
        rippleMenu.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                showPopUp();
            }
        });

        //show option item just for owner

        //        rippleMenu.setVisibility(View.GONE);
        /*if (role != ChannelChatRole.OWNER) {
            imgPupupMenul.setVisibility(View.GONE);
            rippleMenu.setVisibility(View.GONE);
        }*/


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startDialogSelectPicture(R.array.profile);

            }
        });

        imgCircleImageView = (de.hdodenhof.circleimageview.CircleImageView) findViewById(R.id.pch_img_circleImage);
        imgCircleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Realm realm = Realm.getDefaultInstance();
                if (realm.where(RealmAvatar.class).equalTo(RealmAvatarFields.OWNER_ID, roomId).findFirst() != null) {
                    FragmentShowAvatars.appBarLayout = fab;

                    FragmentShowAvatars fragment = FragmentShowAvatars.newInstance(roomId, FragmentShowAvatars.From.channel);
                    ActivityChannelProfile.this.getSupportFragmentManager().beginTransaction().addToBackStack(null).setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left).replace(R.id.fragmentContainer_channel_profile, fragment, null).commit();
                }
                realm.close();
            }
        });



       /* txtDescription.setMovementMethod(LinkMovementMethod.getInstance());

        String a[] = txtDescription.getText().toString().split(" ");
        SpannableStringBuilder builder = new SpannableStringBuilder();

        for (int i = 0; i < a.length; i++) {
            if (a[i].matches("\\d+")) { //check if only digits. Could also be text.matches("[0-9]+")

                wordToSpan = new SpannableString(a[i]);
                wordToSpan.setSpan(new ForegroundColorSpan(Color.BLUE), 0, a[i].length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                wordToSpan.setSpan(new ClickableSpan() {
                    @Override
                    public void onClick(View v) {
                        TextView tv = (TextView) v;
                        if (tv.getText() instanceof Spannable) {

                            String valuesSpan;
                            Spanned s = (Spanned) tv.getText();
                            int start = s.getSpanStart(this);
                            int end = s.getSpanEnd(this);
                            valuesSpan = s.subSequence(start, end).toString();
                        }
                        new MaterialDialog.Builder(ActivityChannelProfile.this).items(
                                R.array.phone_profile_chanel)
                                .negativeText(getString(R.string.cancel))
                                .itemsCallback(new MaterialDialog.ListCallback() {
                                    @Override
                                    public void onSelection(MaterialDialog dialog, View view, int which,
                                                            CharSequence text) {
                                        switch (which) {
                                            case 0:
                                                break;
                                            case 1:
                                                break;
                                            case 2:
                                                break;
                                        }
                                    }
                                })
                                .show();
                    }
                }, 0, a[i].length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else if (a[i].matches("\\@(\\w+)")) {

                wordToSpan = new SpannableString(a[i]);
                wordToSpan.setSpan(new ForegroundColorSpan(Color.BLUE), 0, a[i].length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                wordToSpan.setSpan(new ClickableSpan() {
                    @Override
                    public void onClick(View v) {
                        String valuesSpan;
                        TextView tv = (TextView) v;
                        if (tv.getText() instanceof Spannable) {
                            Spanned s = (Spanned) tv.getText();
                            int start = s.getSpanStart(this);
                            int end = s.getSpanEnd(this);
                            valuesSpan = s.subSequence(start, end).toString();
                        }
                    }
                }, 0, a[i].length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {
                wordToSpan = new SpannableString(a[i]);
                wordToSpan.setSpan(new ForegroundColorSpan(Color.BLACK), 0, a[i].length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            builder.append(wordToSpan).append(" ");
        }
        txtDescription.setText(builder);*/

        txtChannelLink = (TextView) findViewById(R.id.txt_channel_link);

       /* txtNotifyAndSound = (TextView) findViewById(R.id.pch_txt_notifyAndSound);
        txtNotifyAndSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        txtDeleteCache = (TextView) findViewById(R.id.pch_txt_deleteCache);
        txtDeleteCache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        txtLeaveChannel = (TextView) findViewById(R.id.pch_txt_leaveChannel);
        txtLeaveChannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        txtReport = (TextView) findViewById(R.id.pch_txt_Report);
        txtReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });*/

        lytSharedMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityChannelProfile.this, ActivityShearedMedia.class);
                intent.putExtra("RoomID", roomId);
                startActivity(intent);
            }
        });

        lytChannelName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeGroupName();
            }
        });

        lytChannelDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeGroupDescription();
            }
        });


        txtChannelName = (TextView) findViewById(R.id.txt_channel_name);

        ViewGroup layoutAddMember = (ViewGroup) findViewById(R.id.agp_layout_add_member);
        layoutAddMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMemberToChannel();
            }
        });

        if (description != null && !description.isEmpty()) {
            txtDescription.setText(description);
        }
        txtChannelName.setText(title);
        txtChannelNameInfo.setText(title);


        setTextChannelLik();


        ltLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                isPopup = false;
                if (role == ChannelChatRole.OWNER || role == ChannelChatRole.ADMIN) {
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


        //memberNumber.setText(participantsCountLabel);

        //mollareza
        txtMore = (TextView) findViewById(R.id.agp_channel_txt_more);
        //txtMore.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View view) {
        //
        //        firstLimit = 0;
        //        lastLimit += lastLimit;
        //
        //        int listSize = contacts.size();
        //        int count = items.size();
        //
        //        if (lastLimit > listSize) lastLimit = listSize;
        //
        //        items.clear();
        //        for (int i = firstLimit; i < lastLimit; i++) {
        //            items.add(new ContactItemGroupProfile().setContact(contacts.get(i)).withIdentifier(100 + contacts.indexOf(contacts.get(i))));
        //        }
        //
        //        itemAdapter.clear();
        //        itemAdapter.add(items);
        //
        //        if ((listSize - lastLimit) > 0) {
        //            if (items.size() >= listSize) txtMore.setVisibility(View.VISIBLE);
        //        } else {
        //            txtMore.setVisibility(View.GONE);
        //        }
        //    }
        //});
        txtMore.setVisibility(View.GONE);

        attachFile = new AttachFile(this);

        setAvatar();
        setAvatarChannel();
        initRecycleView();
        channelGetMemberList();
        showAdminOrModeratorList();


        FragmentShowAvatars.onComplete = new OnComplete() {
            @Override
            public void complete(boolean result, String messageOne, String MessageTow) {

                //                showImage();
                long mAvatarId = 0;
                if (messageOne != null && !messageOne.equals("")) {
                    mAvatarId = Long.parseLong(messageOne);
                }

                final long finalMAvatarId = mAvatarId;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        HelperAvatar.avatarDelete(roomId, finalMAvatarId, HelperAvatar.AvatarType.ROOM, new OnAvatarDelete() {
                            @Override
                            public void latestAvatarPath(String avatarPath) {
                                setImage(avatarPath);
                            }

                            @Override
                            public void showInitials(final String initials, final String color) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        imgCircleImageView.setImageBitmap(com.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) imgCircleImageView.getContext().getResources().getDimension(R.dimen.dp60), initials, color));
                                    }
                                });
                            }
                        });
                    }
                });
            }
        };

        TextView txtSignature = (TextView) findViewById(R.id.agp_txt_signature);
        final ToggleButton toggleEnableSignature = (ToggleButton) findViewById(R.id.agp_toggle_signature);

        if (isSignature) {
            toggleEnableSignature.setChecked(true);
        } else {
            toggleEnableSignature.setChecked(false);
        }

        G.onChannelUpdateSignature = new OnChannelUpdateSignature() {
            @Override
            public void onChannelUpdateSignatureResponse(final long roomId, final boolean signature) {

                Realm realm = Realm.getDefaultInstance();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
                        realmRoom.getChannelRoom().setSignature(signature);
                    }
                });

                realm.close();


            }

            @Override
            public void onError(int majorCode, int minorCode) {

                runOnUiThread(new Runnable() {
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

                        if (toggleEnableSignature.isChecked()) {
                            toggleEnableSignature.setChecked(false);
                        } else {
                            toggleEnableSignature.setChecked(true);

                        }

                    }
                });
            }
        };

        txtSignature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (toggleEnableSignature.isChecked()) {
                    toggleEnableSignature.setChecked(false);
                    new RequestChannelUpdateSignature().channelUpdateSignature(roomId, false);

                } else {
                    toggleEnableSignature.setChecked(true);
                    new RequestChannelUpdateSignature().channelUpdateSignature(roomId, true);
                }
            }
        });


    }

    private void setTextChannelLik() {

        if (isPrivate) {
            txtChannelLink.setText(inviteLink);
            txtLinkTitle.setText(getResources().getString(R.string.channel_link));

        } else {
            txtChannelLink.setText("" + linkUsername);
            txtLinkTitle.setText(getResources().getString(R.string.st_username));
        }
    }

    private void dialogRevoke() {

        String link = txtChannelLink.getText().toString();

        final LinearLayout layoutRevoke = new LinearLayout(ActivityChannelProfile.this);
        layoutRevoke.setOrientation(LinearLayout.VERTICAL);

        final View viewRevoke = new View(ActivityChannelProfile.this);
        LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);

        final TextInputLayout inputRevoke = new TextInputLayout(ActivityChannelProfile.this);
        edtRevoke = new EditText(ActivityChannelProfile.this);
        edtRevoke.setHint(getResources().getString(R.string.channel_link_hint_revoke));
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

        final MaterialDialog dialog = new MaterialDialog.Builder(ActivityChannelProfile.this).title(getResources().getString(R.string.channel_link_title_revoke))
                .positiveText(getResources().getString(R.string.revoke))
                .customView(layoutRevoke, true)
                .widgetColor(getResources().getColor(R.color.toolbar_background))
                .negativeText(getResources().getString(R.string.B_cancel))
                .neutralText(R.string.array_Copy)
                .onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        String copy;
                        copy = txtChannelLink.getText().toString();
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
                new RequestChannelRevokeLink().channelRevokeLink(roomId);
            }
        });
        dialog.show();
    }

    //    private void editUsername() {
    //        final LinearLayout layoutUserName = new LinearLayout(ActivityChannelProfile.this);
    //        layoutUserName.setOrientation(LinearLayout.VERTICAL);
    //
    //        final View viewUserName = new View(ActivityChannelProfile.this);
    //        LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
    //
    //        final TextInputLayout inputUserName = new TextInputLayout(ActivityChannelProfile.this);
    //        final EditText edtUserName = new EditText(ActivityChannelProfile.this);
    //        edtUserName.setHint(getResources().getString(R.string.st_username));
    //        edtUserName.setText(linkUsername);
    //        edtUserName.setTextColor(getResources().getColor(R.color.text_edit_text));
    //        edtUserName.setHintTextColor(getResources().getColor(R.color.hint_edit_text));
    //        edtUserName.setPadding(0, 8, 0, 8);
    //        edtUserName.setSingleLine(true);
    //        inputUserName.addView(edtUserName);
    //        inputUserName.addView(viewUserName, viewParams);
    //
    //        viewUserName.setBackgroundColor(getResources().getColor(R.color.line_edit_text));
    //        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
    //            edtUserName.setBackground(getResources().getDrawable(android.R.color.transparent));
    //        }
    //        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    //
    //        layoutUserName.addView(inputUserName, layoutParams);
    //
    //        final MaterialDialog dialog = new MaterialDialog.Builder(ActivityChannelProfile.this).title(getResources().getString(R.string.st_username)).positiveText(getResources().getString(R.string.save)).customView(layoutUserName, true).widgetColor(getResources().getColor(R.color.toolbar_background)).negativeText(getResources().getString(R.string.B_cancel)).build();
    //
    //        final View positive = dialog.getActionButton(DialogAction.POSITIVE);
    //        positive.setEnabled(false);
    //
    //        final String finalUserName = inviteLink;
    //        edtUserName.addTextChangedListener(new TextWatcher() {
    //            @Override
    //            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    //
    //            }
    //
    //            @Override
    //            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    //            }
    //
    //            @Override
    //            public void afterTextChanged(Editable editable) {
    //                new RequestChannelCheckUsername().channelCheckUsername(roomId, editable.toString());
    //            }
    //        });
    //
    //
    //        G.onChannelCheckUsername = new OnChannelCheckUsername() {
    //            @Override
    //            public void onChannelCheckUsername(final ProtoChannelCheckUsername.ChannelCheckUsernameResponse.Status status) {
    //                G.handler.post(new Runnable() {
    //                    @Override
    //                    public void run() {
    //                        if (status == ProtoChannelCheckUsername.ChannelCheckUsernameResponse.Status.AVAILABLE) {
    //
    //                            if (!edtUserName.getText().toString().equals(finalUserName)) {
    //                                positive.setEnabled(true);
    //                            } else {
    //                                positive.setEnabled(false);
    //                            }
    //                            inputUserName.setErrorEnabled(true);
    //                            inputUserName.setError("");
    //
    //
    //                        } else if (status == ProtoChannelCheckUsername.ChannelCheckUsernameResponse.Status.INVALID) {
    //                            positive.setEnabled(false);
    //                            inputUserName.setErrorEnabled(true);
    //                            inputUserName.setError("INVALID");
    //
    //                        } else if (status == ProtoChannelCheckUsername.ChannelCheckUsernameResponse.Status.TAKEN) {
    //                            inputUserName.setErrorEnabled(true);
    //                            inputUserName.setError("TAKEN");
    //                            positive.setEnabled(false);
    //                        }
    //                    }
    //                });
    //            }
    //
    //            @Override
    //            public void onError(int majorCode, int minorCode) {
    //
    //            }
    //
    //            @Override
    //            public void onTimeOut() {
    //
    //            }
    //        };
    //
    //
    //        G.onChannelUpdateUsername = new OnChannelUpdateUsername() {
    //            @Override
    //            public void onChannelUpdateUsername(final long roomId, final String username) {
    //
    //                G.handler.post(new Runnable() {
    //                    @Override
    //                    public void run() {
    //                        txtChannelLink.setText("iGap.net/" + username);
    //
    //                        Realm realm = Realm.getDefaultInstance();
    //                        realm.executeTransaction(new Realm.Transaction() {
    //                            @Override
    //                            public void execute(Realm realm) {
    //                                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
    //                                realmRoom.getChannelRoom().setUsername(username);
    //                            }
    //                        });
    //
    //                        realm.close();
    //                    }
    //                });
    //
    //            }
    //
    //            @Override
    //            public void onError(int majorCode, int minorCode ,int time) {
    //
    //            }
    //
    //            @Override
    //            public void onTimeOut() {
    //                runOnUiThread(new Runnable() {
    //                    @Override
    //                    public void run() {
    //                        final Snackbar snack = Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.time_out), Snackbar.LENGTH_LONG);
    //
    //                        snack.setAction(getString(R.string.cancel), new View.OnClickListener() {
    //                            @Override
    //                            public void onClick(View view) {
    //                                snack.dismiss();
    //                            }
    //                        });
    //                        snack.show();
    //                    }
    //                });
    //            }
    //        };
    //
    //        positive.setOnClickListener(new View.OnClickListener() {
    //            @Override
    //            public void onClick(View view) {
    //
    //                new RequestChannelUpdateUsername().channelUpdateUsername(roomId, edtUserName.getText().toString());
    //                dialog.dismiss();
    //            }
    //        });
    //
    //
    //        edtUserName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
    //            @Override
    //            public void onFocusChange(View view, boolean b) {
    //                if (b) {
    //                    viewUserName.setBackgroundColor(getResources().getColor(R.color.toolbar_background));
    //                } else {
    //                    viewUserName.setBackgroundColor(getResources().getColor(R.color.line_edit_text));
    //                }
    //            }
    //        });
    //
    //        // check each word with server
    //
    //        dialog.show();
    //    }

    private void dialogCopyLink() {

        String link = txtChannelLink.getText().toString();

        final LinearLayout layoutChannelLink = new LinearLayout(ActivityChannelProfile.this);
        layoutChannelLink.setOrientation(LinearLayout.VERTICAL);

        final View viewRevoke = new View(ActivityChannelProfile.this);
        LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);

        final TextInputLayout inputChannelLink = new TextInputLayout(ActivityChannelProfile.this);
        EditText edtLink = new EditText(ActivityChannelProfile.this);
        edtLink.setHint(getResources().getString(R.string.channel_public_hint_revoke));
        edtLink.setText(link);
        edtLink.setTextColor(getResources().getColor(R.color.text_edit_text));
        edtLink.setHintTextColor(getResources().getColor(R.color.hint_edit_text));
        edtLink.setPadding(0, 8, 0, 8);
        edtLink.setEnabled(false);
        edtLink.setSingleLine(true);
        inputChannelLink.addView(edtLink);
        inputChannelLink.addView(viewRevoke, viewParams);

        TextView txtLink = new TextView(ActivityChannelProfile.this);
        txtLink.setText("iGap.net/" + link);
        txtLink.setTextColor(getResources().getColor(R.color.gray_6c));

        viewRevoke.setBackgroundColor(getResources().getColor(R.color.line_edit_text));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            edtLink.setBackground(getResources().getDrawable(android.R.color.transparent));
        }
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        layoutChannelLink.addView(inputChannelLink, layoutParams);
        layoutChannelLink.addView(txtLink, layoutParams);

        final MaterialDialog dialog =
                new MaterialDialog.Builder(ActivityChannelProfile.this).title(getResources().getString(R.string.channel_link)).positiveText(getResources().getString(R.string.array_Copy)).customView(layoutChannelLink, true).widgetColor(getResources().getColor(R.color.toolbar_background)).negativeText(getResources().getString(R.string.B_cancel)).onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        String copy;
                        copy = txtChannelLink.getText().toString();
                        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("LINK_GROUP", copy);
                        clipboard.setPrimaryClip(clip);
                    }
                }).build();

        dialog.show();

    }

    private void setAvatar() {
        HelperAvatar.getAvatar(roomId, HelperAvatar.AvatarType.ROOM, new OnAvatarGet() {
            @Override
            public void onAvatarGet(final String avatarPath, long ownerId) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ImageLoader.getInstance().displayImage(AndroidUtils.suitablePath(avatarPath), imgCircleImageView);
                    }
                });
            }

            @Override
            public void onShowInitials(final String initials, final String color) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imgCircleImageView.setImageBitmap(com.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) imgCircleImageView.getContext().getResources().getDimension(R.dimen.dp60), initials, color));
                    }
                });
            }
        });
    }

    private void channelGetMemberList() {
        new RequestChannelGetMemberList().channelGetMemberList(roomId);
    }

    private void setAvatarChannel() {

        Realm realm = Realm.getDefaultInstance();
        RealmResults<RealmAvatar> avatars = realm.where(RealmAvatar.class).equalTo(RealmAvatarFields.OWNER_ID, roomId).findAll();

        if (avatars.isEmpty()) {
            imgCircleImageView.setImageBitmap(com.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) imgCircleImageView.getContext().getResources().getDimension(R.dimen.dp60), initials, color));
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
            imgCircleImageView.setImageBitmap(com.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) imgCircleImageView.getContext().getResources().getDimension(R.dimen.dp60), initials, color));
            return;
        }

        if (realmAvatar.getFile().isFileExistsOnLocal()) {
            ImageLoader.getInstance().displayImage(AndroidUtils.suitablePath(realmAvatar.getFile().getLocalFilePath()), imgCircleImageView);
        } else if (realmAvatar.getFile().isThumbnailExistsOnLocal()) {
            ImageLoader.getInstance().displayImage(AndroidUtils.suitablePath(realmAvatar.getFile().getLocalThumbnailPath()), imgCircleImageView);
        } else {
            imgCircleImageView.setImageBitmap(com.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) imgCircleImageView.getContext().getResources().getDimension(R.dimen.dp60), initials, color));
        }

        realm.close();
    }


    //=============================================================== Channel Members ==============

    public static OnMenuClick onMenuClick;
    private FastAdapter fastAdapter;
    private ItemAdapter itemAdapter;
    private RecyclerView recyclerView;
    private List<IItem> items;
    private List<StructContactInfo> contacts;

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
                    HelperPermision.getStoragePermision(ActivityChannelProfile.this, new OnGetPermission() {
                        @Override
                        public void Allow() {
                            ContactItemGroupProfile contactItemGroupProfile = (ContactItemGroupProfile) item;
                            Intent intent = null;

                            if (contactItemGroupProfile.mContact.peerId == userId) {
                                intent = new Intent(ActivityChannelProfile.this, ActivitySetting.class);
                            } else {
                                intent = new Intent(ActivityChannelProfile.this, ActivityContactsProfile.class);
                                intent.putExtra("peerId", contactItemGroupProfile.mContact.peerId);
                                intent.putExtra("RoomId", roomId);
                                intent.putExtra("enterFrom", GROUP.toString());
                            }

                            if (ActivityChat.activityChat != null) ActivityChat.activityChat.finish();

                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);

                            finish();
                        }

                        @Override
                        public void deny() {

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

                if (role == ChannelChatRole.OWNER) {

                    if (contactItemGroupProfile.mContact.role.equals(ProtoGlobal.GroupRoom.Role.MEMBER.toString())) {

                        kickMember(contactItemGroupProfile.mContact.peerId);

                    } else if (contacts.get(position).role.equals(ProtoGlobal.GroupRoom.Role.ADMIN.toString())) {

                        kickAdmin(contactItemGroupProfile.mContact.peerId);

                    } else if (contactItemGroupProfile.mContact.role.equals(ProtoGlobal.GroupRoom.Role.MODERATOR.toString())) {

                        kickModerator(contactItemGroupProfile.mContact.peerId);

                    }
                } else if (role == ChannelChatRole.ADMIN) {

                    if (contactItemGroupProfile.mContact.role.equals(ProtoGlobal.GroupRoom.Role.MEMBER.toString())) {
                        kickMember(contactItemGroupProfile.mContact.peerId);
                    } else if (contactItemGroupProfile.mContact.role.equals(ProtoGlobal.GroupRoom.Role.MODERATOR.toString())) {
                        kickModerator(contacts.get(position).peerId);
                    }
                } else if (role == ChannelChatRole.MODERATOR) {

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
        recyclerView.setLayoutManager(new LinearLayoutManager(ActivityChannelProfile.this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(stickyHeaderAdapter.wrap(itemAdapter.wrap(headerAdapter.wrap(fastAdapter))));

        recyclerView.setNestedScrollingEnabled(false);

        //this adds the Sticky Headers within our list
        final StickyRecyclerHeadersDecoration decoration = new StickyRecyclerHeadersDecoration(stickyHeaderAdapter);
        recyclerView.addItemDecoration(decoration);

        items = new ArrayList<>();

        ContactItemGroupProfile.mainRole = role.toString();
        ContactItemGroupProfile.roomType = ProtoGlobal.Room.Type.CHANNEL;

        fillItem();

        int listSize = contacts.size();

        //txtMemberNumber.setText(listSize + "");

        for (int i = 0; i < listSize; i++) {
            items.add(new ContactItemGroupProfile().setContact(contacts.get(i)).withIdentifier(100 + contacts.indexOf(contacts.get(i))));
        }

        //if (listSize > lastLimit) {
        //    if (txtMore != null) {
        //        txtMore.setVisibility(View.VISIBLE);
        //
        //        for (int i = firstLimit; i < lastLimit; i++) {
        //            items.add(new ContactItemGroupProfile().setContact(contacts.get(i)).withIdentifier(100 + contacts.indexOf(contacts.get(i))));
        //        }
        //    }
        //} else {
        //
        //    if (txtMore != null) {
        //        txtMore.setVisibility(View.GONE);
        //
        //        for (int i = 0; i < listSize; i++) {
        //            items.add(new ContactItemGroupProfile().setContact(contacts.get(i)).withIdentifier(100 + contacts.indexOf(contacts.get(i))));
        //        }
        //    }
        //}
        itemAdapter.add(items);

        //so the headers are aware of changes
        stickyHeaderAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                decoration.invalidateHeaders();
            }
        });
    }

    //******Add And Moderator List

    private void membersList(ProtoGlobal.ChannelRoom.Role role) {
        Fragment fragment = FragmentListAdmin.newInstance(getCurrentUser(role));
        Bundle bundle = new Bundle();
        bundle.putBoolean("DIALOG_SHOWING", false);
        bundle.putLong("ID", roomId);
        bundle.putString("ROOM_TYPE", ProtoGlobal.Room.Type.CHANNEL.toString());

        if (role.toString().equals(ProtoGlobal.ChannelRoom.Role.MODERATOR.toString())) {
            bundle.putString("TYPE", "MODERATOR");
        } else if (role.toString().equals(ProtoGlobal.ChannelRoom.Role.ADMIN.toString())) {
            bundle.putString("TYPE", "ADMIN");
        }
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left).addToBackStack(null).replace(R.id.fragmentContainer_channel_profile, fragment).commit();
    }

    private List<StructContactInfo> getCurrentUser(ProtoGlobal.ChannelRoom.Role role) {
        List<ContactItemGroupProfile> items = itemAdapter.getAdapterItems();

        List<StructContactInfo> users = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).mContact.role.equals(role.toString())) {
                users.add(items.get(i).mContact);
            }
        }
        return users;
    }

    //****** show admin or moderator list

    private void showAdminOrModeratorList() {
        if ((role == ChannelChatRole.MEMBER) || (role == ChannelChatRole.MODERATOR)) {
            lytListAdmin.setVisibility(View.GONE);
            lytListModerator.setVisibility(View.GONE);
        } else if (role == ChannelChatRole.ADMIN) {
            lytListAdmin.setVisibility(View.GONE);
        }
    }

    //****** user exist in current list checking

    private boolean userExistInList(long userId) {

        for (StructContactInfo info : contacts) {
            if (info.peerId == userId) {
                return true;
            }
        }

        return false;
    }

    //****** add member

    private void addMemberToChannel() {
        List<StructContactInfo> userList = Contacts.retrieve(null);

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

                for (int i = 0; i < list.size(); i++) {
                    new RequestChannelAddMember().channelAddMember(roomId, list.get(i).peerId, 0);
                }
            }
        });


        Bundle bundle = new Bundle();
        bundle.putBoolean("DIALOG_SHOWING", false);
        bundle.putLong("COUNT_MESSAGE", noLastMessage);
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left).addToBackStack(null).replace(R.id.coordinator, fragment).commit();
    }

    //****** create popup

    private class CreatePopUpMessage {

        private void show(View view, final StructContactInfo info) {
            PopupMenu popup = new PopupMenu(ActivityChannelProfile.this, view, Gravity.TOP);
            popup.getMenuInflater().inflate(R.menu.menu_item_group_profile, popup.getMenu());

            if (role == ChannelChatRole.OWNER) {

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
            } else if (role == ChannelChatRole.ADMIN) {

                /**
                 *  ----------- Admin ---------------
                 *  1- admin dose'nt access set another admin
                 *  2- admin can set moderator
                 *  3- can remove moderator
                 *  4- can kick moderator and Member
                 */

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
            } else if (role == ChannelChatRole.MODERATOR) {

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
    }

    //********** fill item

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

    //********** select picture

    private void startDialogSelectPicture(int r) {

        new MaterialDialog.Builder(this).title(R.string.choose_picture).negativeText(R.string.cansel).items(r).itemsCallback(new MaterialDialog.ListCallback() {
            @Override
            public void onSelection(final MaterialDialog dialog, View view, int which, CharSequence text) {
                if (text.toString().equals(getString(R.string.from_camera))) {

                    if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {

                        try {

                            HelperPermision.getStoragePermision(ActivityChannelProfile.this, new OnGetPermission() {
                                @Override
                                public void Allow() throws IOException {
                                    HelperPermision.getCameraPermission(ActivityChannelProfile.this, new OnGetPermission() {
                                        @Override
                                        public void Allow() {
                                            // this dialog show 2 way for choose image : gallery and camera
                                            dialog.dismiss();
                                            useCamera();
                                        }

                                        @Override
                                        public void deny() {

                                        }
                                    });
                                }

                                @Override
                                public void deny() {

                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    } else {
                        Toast.makeText(ActivityChannelProfile.this, R.string.please_check_your_camera, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        new AttachFile(ActivityChannelProfile.this).requestOpenGalleryForImageSingleSelect();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).show();
    }

    private void useCamera() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                new AttachFile(ActivityChannelProfile.this).dispatchTakePictureIntent();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                new AttachFile(ActivityChannelProfile.this).requestTakePicture();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //********** channel Add Member

    private void channelAddMemberResponse(long roomIdResponse, final long userId, final ProtoGlobal.ChannelRoom.Role role) {
        if (roomIdResponse == roomId) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Realm realm = Realm.getDefaultInstance();
                    RealmRegisteredInfo realmRegistered = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, userId).findFirst();
                    if (realmRegistered != null) {
                        StructContactInfo struct = new StructContactInfo(realmRegistered.getId(), realmRegistered.getDisplayName(), realmRegistered.getStatus(), false, false, realmRegistered.getPhoneNumber() + "");
                        struct.avatar = realmRegistered.getLastAvatar();
                        struct.initials = realmRegistered.getInitials();
                        struct.color = realmRegistered.getColor();
                        struct.lastSeen = realmRegistered.getLastSeen();
                        struct.status = realmRegistered.getStatus();
                        struct.role = role.toString();
                        IItem item = (new ContactItemGroupProfile().setContact(struct).withIdentifier(SUID.id().get()));
                        itemAdapter.add(item);
                        //mollareza
                        //contacts.add(struct);
                        //refreshListMember();
                    } else {
                        new RequestUserInfo().userInfo(userId, roomId + "");
                    }

                    realm.close();
                }
            });
        }

        //updateMemberCount(roomIdResponse);
    }

    private void channelKickMember(final long roomIdResponse, final long memberId) {
        if (roomIdResponse == roomId) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final List<ContactItemGroupProfile> items = itemAdapter.getAdapterItems();
                    for (int i = 0; i < items.size(); i++) {
                        if (items.get(i).mContact.peerId == memberId) {
                            itemAdapter.remove(i);
                            //member
                            //contacts.remove(i);
                            //refreshListMember();
                        }
                    }
                    //updateMemberCount(roomIdResponse);
                }
            });
        }
    }

    //********** update member count

    /*private void updateMemberCount(long roomId) {
        Realm realm = Realm.getDefaultInstance();

        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
        String memberCount = realmRoom.getChannelRoom().getMembers().size() + "";
        realmRoom.getChannelRoom().setParticipantsCountLabel(memberCount);
        memberNumber.setText(memberCount);

        realm.close();
    }*/

    //********** update role (to admin, kick admin , kick moderator and all of this things...)

    private void updateRole(long roomIdR, final long memberId, final ProtoGlobal.ChannelRoom.Role role) {
        if (roomIdR == roomId) {
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
    }

    //********** dialog for edit channel

    private String dialogDesc;
    private String dialogName;

    private void ChangeGroupDescription() {
        new MaterialDialog.Builder(ActivityChannelProfile.this).title(R.string.channel_description).positiveText(getString(R.string.save)).alwaysCallInputCallback().widgetColor(getResources().getColor(R.color.toolbar_background)).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                editChannelRequest(txtChannelNameInfo.getText().toString(), dialogDesc);
                showProgressBar();
            }
        }).negativeText(getString(R.string.cancel)).inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_TEXT).input(getString(R.string.please_enter_group_description), txtDescription.getText().toString(), new MaterialDialog.InputCallback() {
            @Override
            public void onInput(MaterialDialog dialog, CharSequence input) {
                // Do something
                View positive = dialog.getActionButton(DialogAction.POSITIVE);
                dialogDesc = input.toString();
                if (!input.toString().equals(txtDescription.getText().toString())) {

                    positive.setClickable(true);
                    positive.setAlpha(1.0f);
                } else {
                    positive.setClickable(false);
                    positive.setAlpha(0.5f);
                }
            }
        }).show();
    }

    private void ChangeGroupName() {
        //        new MaterialDialog.Builder(ActivityChannelProfile.this).title(R.string.channel_name)
        //                .positiveText(getString(R.string.save))
        //                .alwaysCallInputCallback()
        //                .widgetColor(getResources().getColor(R.color.toolbar_background))
        //                .onPositive(new MaterialDialog.SingleButtonCallback() {
        //                    @Override
        //                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
        //                        editChannelRequest(dialogName, txtDescription.getText().toString());
        //
        //                        prgWait.setVisibility(View.VISIBLE);
        //                        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        //                    }
        //                })
        //                .negativeText(getString(R.string.cancel))
        //                .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_TEXT)
        //                .alwaysCallInputCallback()
        //                .input(getString(R.string.please_enter_channel_name), txtChannelNameInfo.getText().toString(), new MaterialDialog.InputCallback() {
        //                    @Override
        //                    public void onInput(MaterialDialog dialog, CharSequence input) {
        //                        // Do something
        //                        View positive = dialog.getActionButton(DialogAction.POSITIVE);
        //                        dialogName = input.toString();
        //                        Log.i("VVVV", "onInput: " + input.toString());
        //                        if (!input.toString().equals(txtChannelNameInfo.getText().toString())) {
        //                            positive.setClickable(true);
        //                            positive.setAlpha(1.0f);
        //                        } else {
        //                            positive.setClickable(false);
        //                            positive.setAlpha(0.5f);
        //                        }
        //
        //
        //                    }
        //                }).show();

        final LinearLayout layoutUserName = new LinearLayout(ActivityChannelProfile.this);
        layoutUserName.setOrientation(LinearLayout.VERTICAL);

        final View viewUserName = new View(ActivityChannelProfile.this);
        LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);

        final TextInputLayout inputUserName = new TextInputLayout(ActivityChannelProfile.this);
        final EditText edtNameChannel = new EditText(ActivityChannelProfile.this);
        edtNameChannel.setHint(getResources().getString(R.string.st_username));
        edtNameChannel.setText(txtChannelNameInfo.getText().toString());
        edtNameChannel.setTextColor(getResources().getColor(R.color.text_edit_text));
        edtNameChannel.setHintTextColor(getResources().getColor(R.color.hint_edit_text));
        edtNameChannel.setPadding(0, 8, 0, 8);
        edtNameChannel.setSingleLine(true);
        inputUserName.addView(edtNameChannel);
        inputUserName.addView(viewUserName, viewParams);

        viewUserName.setBackgroundColor(getResources().getColor(R.color.line_edit_text));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            edtNameChannel.setBackground(getResources().getDrawable(android.R.color.transparent));
        }
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        layoutUserName.addView(inputUserName, layoutParams);

        final MaterialDialog dialog = new MaterialDialog.Builder(ActivityChannelProfile.this).title(getResources().getString(R.string.channel_name)).positiveText(getResources().getString(R.string.save)).customView(layoutUserName, true).widgetColor(getResources().getColor(R.color.toolbar_background)).negativeText(getResources().getString(R.string.B_cancel)).build();

        final View positive = dialog.getActionButton(DialogAction.POSITIVE);

        final String finalChannelName = title;
        positive.setEnabled(false);
        edtNameChannel.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!edtNameChannel.getText().toString().equals(finalChannelName)) {
                    positive.setEnabled(true);
                } else {
                    positive.setEnabled(false);
                }
            }
        });


        G.onChannelEdit = new OnChannelEdit() {
            @Override
            public void onChannelEdit(final long roomId, final String name, final String description) {

                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        hideProgressBar();
                        txtChannelNameInfo.setText(name);
                    }
                });

            }

            @Override
            public void onError(int majorCode, int minorCode) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        hideProgressBar();

                    }
                });
            }

            @Override
            public void onTimeOut() {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        hideProgressBar();
                    }
                });

            }
        };


        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new RequestChannelEdit().channelEdit(roomId, edtNameChannel.getText().toString(), txtDescription.getText().toString());
                dialog.dismiss();
                showProgressBar();
            }
        });

        edtNameChannel.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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


    //********** channel edit name and description

    private void editChannelResponse(long roomIdR, final String name, final String description) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hideProgressBar();
                txtChannelNameInfo.setText(name);
                txtDescription.setText(description);

                prgWait.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        });
    }

    private void editChannelRequest(String name, String description) {
        new RequestChannelEdit().channelEdit(roomId, name, description);
    }

    //********** set roles

    private void setToAdmin(Long peerId) {
        new RequestChannelAddAdmin().channelAddAdmin(roomId, peerId);
    }

    private void setToModerator(Long peerId) {
        new RequestChannelAddModerator().channelAddModerator(roomId, peerId);
    }

    //********* kick user from roles

    private void kickMember(Long peerId) {
        new RequestChannelKickMember().channelKickMember(roomId, peerId);
    }

    private void kickModerator(Long peerId) {
        new RequestChannelKickModerator().channelKickModerator(roomId, peerId);
    }

    private void kickAdmin(Long peerId) {
        new RequestChannelKickAdmin().channelKickAdmin(roomId, peerId);
    }

    //************************************************** interfaces

    //***On Add Avatar Response From Server

    @Override
    public void onAvatarAdd(long roomId, ProtoGlobal.Avatar avatar) {
        /**
         * if another account do this action we haven't avatar source and have
         * to download avatars . for do this action call HelperAvatar.getAvatar
         */
        if (pathSaveImage == null) {
            setAvatar();
        } else {
            HelperAvatar.avatarAdd(roomId, pathSaveImage, avatar, new OnAvatarAdd() {
                @Override
                public void onAvatarAdd(final String avatarPath) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            hideProgressBar();
                            setImage(avatarPath);
                        }
                    });

                }
            });
            pathSaveImage = null;
        }
    }

    @Override
    public void onAvatarAddError() {
        hideProgressBar();
    }

    //***On Avatar Delete

    @Override
    public void onChannelAvatarDelete(final long roomId, final long avatarId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                HelperAvatar.avatarDelete(roomId, avatarId, HelperAvatar.AvatarType.ROOM, new OnAvatarDelete() {
                    @Override
                    public void latestAvatarPath(String avatarPath) {
                        setImage(avatarPath);
                    }

                    @Override
                    public void showInitials(final String initials, final String color) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                imgCircleImageView.setImageBitmap(com.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) imgCircleImageView.getContext().getResources().getDimension(R.dimen.dp60), initials, color));
                            }
                        });
                    }
                });
            }
        });
    }

    //***Upload File Callbacks

    @Override
    public void onFileUploaded(FileUploadStructure uploadStructure, String identity) {
        //if (Long.parseLong(identity) == avatarId) {
        new RequestChannelAvatarAdd().channelAvatarAdd(roomId, uploadStructure.token);
        //}
    }

    @Override
    public void onFileUploading(FileUploadStructure uploadStructure, String identity, double progress) {

    }

    @Override
    public void onUploadStarted(FileUploadStructure struct) {
        showProgressBar();
    }

    @Override
    public void onBadDownload(String token) {

    }

    @Override
    public void onFileTimeOut(String identity) {
        //if (Long.parseLong(identity) == avatarId) {
        hideProgressBar();
        //}
    }

    //***User Info

    @Override
    public void onUserInfo(final ProtoGlobal.RegisteredUser user, final String identity) {
        if (identity != null && Long.parseLong(identity) == roomId) {

            if (!userExistInList(user.getId())) { // if user exist in current list don't add that, because maybe duplicated this user and show twice.
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Realm realm = Realm.getDefaultInstance();
                        RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, user.getId()).findFirst();

                        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
                        RealmChannelRoom realmChannelRoom = null;
                        if (realmRoom != null) {
                            realmChannelRoom = realmRoom.getChannelRoom();
                        }
                        final StructContactInfo struct = new StructContactInfo(user.getId(), user.getDisplayName(), user.getStatus().toString(), false, false, user.getPhone() + "");
                        if (realmChannelRoom != null) {
                            RealmList<RealmMember> result = realmRoom.getChannelRoom().getMembers();

                            String _Role = "";

                            for (int i = 0; i < result.size(); i++) {
                                if (result.get(i).getPeerId() == user.getId()) {
                                    _Role = result.get(i).getRole();
                                    break;
                                }
                            }
                            struct.role = _Role;
                        }
                        if (realmRegisteredInfo != null) {
                            struct.avatar = realmRegisteredInfo.getLastAvatar();
                            struct.initials = realmRegisteredInfo.getInitials();
                            struct.color = realmRegisteredInfo.getColor();
                            struct.lastSeen = realmRegisteredInfo.getLastSeen();
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

    //***Edit Channel

    @Override
    public void onChannelEdit(long roomId, String name, String description) {
        editChannelResponse(roomId, name, description);
    }

    //***Delete Channel

    @Override
    public void onChannelDelete(long roomId) {
        closeActivity();
    }

    //***Left Channel

    @Override
    public void onChannelLeft(long roomId, long memberId) {
        closeActivity();
    }

    //***

    private void closeActivity() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ActivityChannelProfile.this.finish();
                if (ActivityChat.activityChat != null) {
                    ActivityChat.activityChat.finish();
                }

                prgWait.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        });
    }

    //***Get Member List

    @Override
    public void onChannelGetMemberList(final List<ProtoChannelGetMemberList.ChannelGetMemberListResponse.Member> members) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // member count not exist in channel profile view
            }
        });

        for (final ProtoChannelGetMemberList.ChannelGetMemberListResponse.Member member : members) {
            new RequestUserInfo().userInfo(member.getUserId(), roomId + "");
        }

    }

    //***Member
    @Override
    public void onChannelAddMember(Long roomId, Long userId, ProtoGlobal.ChannelRoom.Role role) {
        channelAddMemberResponse(roomId, userId, role);
    }

    @Override
    public void onChannelKickMember(long roomId, long memberId) {
        channelKickMember(roomId, memberId);
    }

    //***Moderator
    @Override
    public void onChannelAddModerator(long roomId, long memberId) {
        updateRole(roomId, memberId, ProtoGlobal.ChannelRoom.Role.MODERATOR);
    }

    @Override
    public void onChannelKickModerator(long roomId, long memberId) {
        updateRole(roomId, memberId, ProtoGlobal.ChannelRoom.Role.MEMBER);

        if (G.updateListAfterKick != null) {
            G.updateListAfterKick.updateList(memberId, ProtoGlobal.GroupRoom.Role.MEMBER);
        }
    }

    //***Admin
    @Override
    public void onChannelAddAdmin(long roomId, long memberId) {

        if (G.updateListAfterKick != null) {
            G.updateListAfterKick.updateList(memberId, ProtoGlobal.GroupRoom.Role.ADMIN);
        }

        //update list is for admin list or moderator list
        //after do that check this interface or create new interface for that

        /*if (G.updateListAfterKick != null) {
            G.updateListAfterKick.updateList(memberId, ProtoGlobal.GroupRoom.Role.ADMIN);
        }*/

        updateRole(roomId, memberId, ProtoGlobal.ChannelRoom.Role.ADMIN);
    }

    @Override
    public void onChannelKickAdmin(long roomId, long memberId) {
        updateRole(roomId, memberId, ProtoGlobal.ChannelRoom.Role.MEMBER);

        if (G.updateListAfterKick != null) {
            G.updateListAfterKick.updateList(memberId, ProtoGlobal.GroupRoom.Role.MEMBER);
        }
    }

    //***time out and errors for either of this interfaces

    @Override
    public void onChannelRevokeLink(long roomId, final String inviteLink, final String inviteToken) {

        hideProgressBar();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                edtRevoke.setText("" + inviteLink);
                txtChannelLink.setText("" + inviteLink);
            }
        });

        Realm realm = Realm.getDefaultInstance();

        //channel info
        final RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
        final RealmChannelRoom realmChannelRoom = realmRoom.getChannelRoom();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                realmChannelRoom.setInviteLink(inviteLink);
                realmChannelRoom.setInvite_token(inviteToken);

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

    private void showPopUp() {

        LinearLayout layoutDialog = new LinearLayout(ActivityChannelProfile.this);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutDialog.setOrientation(LinearLayout.VERTICAL);
        layoutDialog.setBackgroundColor(getResources().getColor(android.R.color.white));

        TextView text3 = new TextView(ActivityChannelProfile.this);
        text3.setTextColor(getResources().getColor(android.R.color.black));


        if (isPrivate) {
            text3.setText(getResources().getString(R.string.channel_title_convert_to_public));
        } else {
            text3.setText(getResources().getString(R.string.channel_title_convert_to_private));
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

        text3.setTextSize(sp14_Popup);
        text3.setPadding(dim20, dim12, dim12, dim12);
        layoutDialog.addView(text3, params);

        int screenWidth = (int) (getResources().getDisplayMetrics().widthPixels / 1.7);
        popupWindow = new PopupWindow(layoutDialog, screenWidth, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            popupWindow.setBackgroundDrawable(getResources().getDrawable(R.mipmap.shadow3, ActivityChannelProfile.this.getTheme()));
        } else {
            popupWindow.setBackgroundDrawable((getResources().getDrawable(R.mipmap.shadow3)));
        }
        if (popupWindow.isOutsideTouchable()) {
            popupWindow.dismiss();
        }

        popupWindow.setAnimationStyle(android.R.style.Animation_InputMethod);
        popupWindow.showAtLocation(layoutDialog, Gravity.RIGHT | Gravity.TOP, (int) getResources()

                .

                        getDimension(R.dimen.dp16), (int) getResources()

                .

                        getDimension(R.dimen.dp32));

        text3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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

    private void convertToPrivate() {

        G.onChannelRemoveUsername = new OnChannelRemoveUsername() {
            @Override
            public void onChannelRemoveUsername(final long roomId) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        isPrivate = true;
                        setTextChannelLik();
                        Realm realm = Realm.getDefaultInstance();

                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
                                RealmChannelRoom realmChannelRoom = realmRoom.getChannelRoom();
                                realmChannelRoom.setPrivate(true);
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


        new MaterialDialog.Builder(ActivityChannelProfile.this).title(getString(R.string.channel_title_convert_to_private)).content(getString(R.string.channel_text_convert_to_private)).positiveText(R.string.B_ok).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                new RequestChannelRemoveUsername().channelRemoveUsername(roomId);

            }
        }).negativeText(R.string.B_cancel).show();
    }

    private void convertToPublic() {

        new MaterialDialog.Builder(ActivityChannelProfile.this).title(getString(R.string.channel_title_convert_to_public)).content(getString(R.string.channel_text_convert_to_public)).positiveText(R.string.B_ok).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                dialog.dismiss();
                setUsername();

            }
        }).negativeText(R.string.B_cancel).show();
    }

    private void setUsername() {
        final LinearLayout layoutUserName = new LinearLayout(ActivityChannelProfile.this);
        layoutUserName.setOrientation(LinearLayout.VERTICAL);

        final View viewUserName = new View(ActivityChannelProfile.this);
        LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);

        final TextInputLayout inputUserName = new TextInputLayout(ActivityChannelProfile.this);
        final EditText edtUserName = new EditText(ActivityChannelProfile.this);
        edtUserName.setHint(getResources().getString(R.string.channel_title_channel_set_username));

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

        final MaterialDialog dialog = new MaterialDialog.Builder(ActivityChannelProfile.this).title(getResources().getString(R.string.st_username)).positiveText(getResources().getString(R.string.save)).customView(layoutUserName, true).widgetColor(getResources().getColor(R.color.toolbar_background)).negativeText(getResources().getString(R.string.B_cancel)).build();

        final View positive = dialog.getActionButton(DialogAction.POSITIVE);
        positive.setEnabled(false);

        G.onChannelCheckUsername = new OnChannelCheckUsername() {
            @Override
            public void onChannelCheckUsername(final ProtoChannelCheckUsername.ChannelCheckUsernameResponse.Status status) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (status == ProtoChannelCheckUsername.ChannelCheckUsernameResponse.Status.AVAILABLE) {

                            positive.setEnabled(true);
                            inputUserName.setErrorEnabled(true);
                            inputUserName.setError("");
                        } else if (status == ProtoChannelCheckUsername.ChannelCheckUsernameResponse.Status.INVALID) {
                            positive.setEnabled(false);
                            inputUserName.setErrorEnabled(true);
                            inputUserName.setError("INVALID");

                        } else if (status == ProtoChannelCheckUsername.ChannelCheckUsernameResponse.Status.TAKEN) {
                            positive.setEnabled(false);
                            inputUserName.setErrorEnabled(true);
                            inputUserName.setError("TAKEN");
                        } else if (status == ProtoChannelCheckUsername.ChannelCheckUsernameResponse.Status.OCCUPYING_LIMIT_EXCEEDED) {
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

            @Override
            public void onTimeOut() {

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
                    new RequestChannelCheckUsername().channelCheckUsername(roomId, userName);


                } else {
                    positive.setEnabled(false);
                    inputUserName.setErrorEnabled(true);
                    inputUserName.setError("INVALID");
                }
            }
        });


        G.onChannelUpdateUsername = new OnChannelUpdateUsername() {
            @Override
            public void onChannelUpdateUsername(final long roomId, final String username) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        isPrivate = false;
                        dialog.dismiss();

                        linkUsername = username;
                        setTextChannelLik();

                        Realm realm = Realm.getDefaultInstance();
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
                                RealmChannelRoom realmChannelRoom = realmRoom.getChannelRoom();
                                realmChannelRoom.setUsername("iGap.net" + edtUserName.getText().toString());
                                realmChannelRoom.setPrivate(false);
                            }
                        });
                        realm.close();

                    }
                });


            }

            @Override
            public void onError(final int majorCode, int minorCode, final int time) {

                switch (majorCode) {
                    case 457:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (dialog.isShowing()) dialog.dismiss();
                                dialogWaitTime(R.string.CHANNEL_UPDATE_USERNAME_UPDATE_LOCK, time, majorCode);
                            }
                        });
                        break;
                }
            }

            @Override
            public void onTimeOut() {

            }
        };

        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String userName = edtUserName.getText().toString().replace("iGap.net/", "");
                new RequestChannelUpdateUsername().channelUpdateUsername(roomId, userName);
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


    //*** show delete channel dialog

    private void deleteChannel() {
        String deleteText = "";
        int title;
        if (role.equals(ChannelChatRole.OWNER)) {
            deleteText = context.getString(R.string.do_you_want_delete_this);
            title = R.string.channel_delete;
        } else {
            deleteText = context.getString(R.string.do_you_want_left_this);
            title = R.string.channel_left;
        }

        new MaterialDialog.Builder(ActivityChannelProfile.this).title(title).content(deleteText).positiveText(R.string.B_ok).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                if (role.equals(ChannelChatRole.OWNER)) {
                    new RequestChannelDelete().channelDelete(roomId);
                } else {
                    new RequestChannelLeft().channelLeft(roomId);
                }

                prgWait.setVisibility(View.VISIBLE);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        }).negativeText(R.string.B_cancel).show();
    }

    //*** set avatar image

    private void setImage(final String imagePath) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (new File(imagePath).exists()) {
                    imgCircleImageView.setPadding(0, 0, 0, 0);
                    ImageLoader.getInstance().displayImage(AndroidUtils.suitablePath(imagePath), imgCircleImageView);
                }
            }
        });
    }

    //*** notification and sounds

    private void notificationAndSound() {
        FragmentNotification fragmentNotification = new FragmentNotification();
        Bundle bundle = new Bundle();
        bundle.putString("PAGE", "CHANNEL");
        bundle.putLong("ID", roomId);
        fragmentNotification.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left).addToBackStack(null).replace(R.id.fragmentContainer_channel_profile, fragmentNotification).commit();
    }

    //*** UploadTask

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

                byte[] fileHash = AndroidUtils.getFileHashFromPath(filePath);
                fileUploadStructure.setFileHash(fileHash);

                return fileUploadStructure;
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

    //*** onActivityResult

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
                        pathSaveImage = filePath;
                    } else {
                        ImageHelper.correctRotateImage(AttachFile.imagePath, true);
                        filePath = AttachFile.imagePath;
                        pathSaveImage = filePath;
                    }

                    break;
                case AttachFile.request_code_image_from_gallery_single_select:

                    if (data.getData() == null) {
                        return;
                    }
                    filePath = AttachFile.getFilePathFromUri(data.getData());
                    pathSaveImage = filePath;
                    break;
            }

            new ActivityChannelProfile.UploadTask(prgWait, ActivityChannelProfile.this).execute(filePath, avatarId);
        }
    }

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
        final MaterialDialog dialog = new MaterialDialog.Builder(ActivityChannelProfile.this).title(title).customView(R.layout.dialog_remind_time, wrapInScrollView).positiveText(R.string.B_ok).autoDismiss(false).canceledOnTouchOutside(false).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                dialog.dismiss();
            }
        }).show();

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
                remindTime.setText("00:00");
            }
        };
        countWaitTimer.start();
    }

    //private void refreshListMember() {
    //
    //    int listSize = contacts.size();
    //
    //    if (lastLimit > listSize) lastLimit = listSize;
    //
    //    items.clear();
    //    for (int i = firstLimit; i < lastLimit; i++) {
    //        items.add(new ContactItemGroupProfile().setContact(contacts.get(i)).withIdentifier(100 + contacts.indexOf(contacts.get(i))));
    //    }
    //
    //    itemAdapter.clear();
    //    itemAdapter.add(items);
    //
    //    if ((listSize - lastLimit) > 0) {
    //        if (items.size() >= listSize) txtMore.setVisibility(View.VISIBLE);
    //    } else {
    //        txtMore.setVisibility(View.GONE);
    //    }
    //}


}
