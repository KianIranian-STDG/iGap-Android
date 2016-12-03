package com.iGap.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.iGap.Config;
import com.iGap.G;
import com.iGap.R;
import com.iGap.adapter.StickyHeaderAdapter;
import com.iGap.adapter.items.ContactItemGroupProfile;
import com.iGap.fragments.ShowCustomList;
import com.iGap.helper.HelperPermision;
import com.iGap.interfaces.OnChannelAddAdmin;
import com.iGap.interfaces.OnChannelAddMember;
import com.iGap.interfaces.OnChannelAddModerator;
import com.iGap.interfaces.OnChannelKickAdmin;
import com.iGap.interfaces.OnChannelKickMember;
import com.iGap.interfaces.OnChannelKickModerator;
import com.iGap.interfaces.OnGetPermision;
import com.iGap.interfaces.OnMenuClick;
import com.iGap.interfaces.OnSelectedList;
import com.iGap.libs.rippleeffect.RippleView;
import com.iGap.module.AndroidUtils;
import com.iGap.module.Contacts;
import com.iGap.module.MaterialDesignTextView;
import com.iGap.module.SUID;
import com.iGap.module.StructContactInfo;
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
import com.iGap.request.RequestChannelKickMember;
import com.iGap.request.RequestChannelKickModerator;
import com.iGap.request.RequestUserInfo;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.IItemAdapter;
import com.mikepenz.fastadapter.adapters.HeaderAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.iGap.module.MusicPlayer.roomId;
import static com.iGap.realm.enums.RoomType.GROUP;

public class ActivityProfileChannel extends AppCompatActivity implements OnChannelAddMember, OnChannelKickMember, OnChannelAddModerator, OnChannelKickModerator, OnChannelAddAdmin, OnChannelKickAdmin {

    private AppBarLayout appBarLayout;
    private TextView txtNameChannel, txtDescription, txtChannelLink, txtPhoneNumber,
            txtNotifyAndSound, txtDeleteCache, txtLeaveChannel, txtReport;
    private MaterialDesignTextView imgPupupMenul;
    private de.hdodenhof.circleimageview.CircleImageView imgCircleImageView;
    private FloatingActionButton fab;
    private PopupWindow popupWindow;
    private Spannable wordtoSpan;
    private MaterialDesignTextView txtBack;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private String title;
    private String initials;
    private String color;
    private ChannelChatRole role;
    private long noLastMessage;
    private String participantsCountLabel;
    private String description;
    private RealmList<RealmMember> members;
    private long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_channel);

        G.onChannelAddMember = this;
        G.onChannelKickMember = this;
        G.onChannelAddAdmin = this;
        G.onChannelKickAdmin = this;
        G.onChannelAddModerator = this;
        G.onChannelKickModerator = this;

        //=========Put Extra Start
        Bundle extras = getIntent().getExtras();
        roomId = extras.getLong(Config.PutExtraKeys.CHANNEL_PROFILE_ROOM_ID_LONG.toString());

        Realm realm = Realm.getDefaultInstance();

        //group info
        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
        Log.i("QQQ", "realmRoom : " + realmRoom);
        RealmChannelRoom realmChannelRoom = realmRoom.getChannelRoom();
        title = realmRoom.getTitle();
        initials = realmRoom.getInitials();
        color = realmRoom.getColor();
        role = realmChannelRoom.getRole();
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
        if (userInfo != null)
            userId = userInfo.getUserId();

        realm.close();
        //=========Put Extra End

        txtBack = (MaterialDesignTextView) findViewById(R.id.pch_txt_back);
        final RippleView rippleBack = (RippleView) findViewById(R.id.pch_ripple_back);
        rippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                finish();
            }
        });
        txtBack.setTypeface(G.flaticon);

        appBarLayout = (AppBarLayout) findViewById(R.id.pch_appbar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                TextView titleToolbar = (TextView) findViewById(R.id.pch_txt_titleToolbar);
                if (verticalOffset < -2) {

                    titleToolbar.animate().alpha(1).setDuration(300);
                    titleToolbar.setVisibility(View.VISIBLE);
                } else {
                    titleToolbar.animate().alpha(0).setDuration(500);
                    titleToolbar.setVisibility(View.GONE);
                }
            }
        });
        final int screenWidth = (int) (getResources().getDisplayMetrics().widthPixels / 1.7);
        imgPupupMenul = (MaterialDesignTextView) findViewById(R.id.pch_img_menuPopup);
        RippleView rippleMenu = (RippleView) findViewById(R.id.pch_ripple_menuPopup);
        rippleMenu.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                LayoutInflater layoutInflater =
                        (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View popupView = layoutInflater.inflate(R.layout.popup_window, null);

                popupWindow =
                        new PopupWindow(popupView, screenWidth, ViewGroup.LayoutParams.WRAP_CONTENT,
                                true);
                popupWindow.setBackgroundDrawable(new BitmapDrawable());
                popupWindow.setOutsideTouchable(true);

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
                popupWindow.showAtLocation(popupView, Gravity.RIGHT | Gravity.TOP, 10, 30);
                popupWindow.showAsDropDown(rippleView);
                TextView remove = (TextView) popupView.findViewById(R.id.popup_txtItem1);
                remove.setText("Remove");
                remove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(ActivityProfileChannel.this, R.string.remove, Toast.LENGTH_SHORT)
                                .show();
                    }
                });

                TextView gone2 = (TextView) popupView.findViewById(R.id.popup_txtItem2);
                gone2.setVisibility(View.GONE);
                TextView gone3 = (TextView) popupView.findViewById(R.id.popup_txtItem3);
                gone3.setVisibility(View.GONE);
                TextView gone4 = (TextView) popupView.findViewById(R.id.popup_txtItem4);
                gone4.setVisibility(View.GONE);
            }
        });

        fab = (FloatingActionButton) findViewById(R.id.pch_fab_addToChannel);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        imgCircleImageView = (de.hdodenhof.circleimageview.CircleImageView) findViewById(R.id.pch_img_circleImage);
        imgCircleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        txtDescription = (TextView) findViewById(R.id.pch_txt_description);
        txtDescription.setMovementMethod(LinkMovementMethod.getInstance());

        String a[] = txtDescription.getText().toString().split(" ");
        SpannableStringBuilder builder = new SpannableStringBuilder();

        for (int i = 0; i < a.length; i++) {
            if (a[i].matches("\\d+")) { //check if only digits. Could also be text.matches("[0-9]+")

                wordtoSpan = new SpannableString(a[i]);
                wordtoSpan.setSpan(new ForegroundColorSpan(Color.BLUE), 0, a[i].length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                wordtoSpan.setSpan(new ClickableSpan() {
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
                        new MaterialDialog.Builder(ActivityProfileChannel.this).items(
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

                wordtoSpan = new SpannableString(a[i]);
                wordtoSpan.setSpan(new ForegroundColorSpan(Color.BLUE), 0, a[i].length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                wordtoSpan.setSpan(new ClickableSpan() {
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
                wordtoSpan = new SpannableString(a[i]);
                wordtoSpan.setSpan(new ForegroundColorSpan(Color.BLACK), 0, a[i].length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            builder.append(wordtoSpan).append(" ");
        }
        txtDescription.setText(builder);

        txtChannelLink = (TextView) findViewById(R.id.st_txt_channelLink);
        txtChannelLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ActivityProfileChannel.this, "txtChannelLink", Toast.LENGTH_SHORT)
                        .show();
            }
        });
        txtPhoneNumber = (TextView) findViewById(R.id.st_txt_phoneNumber);
        txtPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        txtNotifyAndSound = (TextView) findViewById(R.id.pch_txt_notifyAndSound);
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
        });

        TextView txtChannelName = (TextView) findViewById(R.id.txt_channel_name);

        ViewGroup layoutAddMember = (ViewGroup) findViewById(R.id.agp_layout_add_member);
        layoutAddMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMemberToChannel();
            }
        });

        Log.i("TTT", "description : " + description);
        if (description != null && !description.isEmpty()) {
            txtDescription.setText(description);
        }
        txtChannelName.setText(title);


        setAvatarChannel();
        initRecycleView();
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

                HelperPermision.getStoragePermision(ActivityProfileChannel.this, new OnGetPermision() {
                    @Override
                    public void Allow() {
                        ContactItemGroupProfile contactItemGroupProfile = (ContactItemGroupProfile) item;
                        Intent intent = null;

                        if (contactItemGroupProfile.mContact.peerId == userId) {
                            intent = new Intent(ActivityProfileChannel.this, ActivitySetting.class);
                        } else {
                            intent = new Intent(ActivityProfileChannel.this, ActivityContactsProfile.class);
                            intent.putExtra("peerId", contactItemGroupProfile.mContact.peerId);
                            intent.putExtra("RoomId", roomId);
                            intent.putExtra("enterFrom", GROUP.toString());
                        }

                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);

                        finish();
                    }
                });


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

                    if (contactItemGroupProfile.mContact.role.equals(
                            ProtoGlobal.GroupRoom.Role.MEMBER.toString())) {
                        kickMember(contactItemGroupProfile.mContact.peerId);
                    } else if (contactItemGroupProfile.mContact.role.equals(
                            ProtoGlobal.GroupRoom.Role.MODERATOR.toString())) {
                        kickModerator(contacts.get(position).peerId);
                    }
                } else if (role == ChannelChatRole.MODERATOR) {

                    if (contactItemGroupProfile.mContact.role.equals(
                            ProtoGlobal.GroupRoom.Role.MEMBER.toString())) {
                        kickMember(contactItemGroupProfile.mContact.peerId);
                    }
                }

                return true;
            }
        });

        fastAdapter.setHasStableIds(true);

        //get our recyclerView and do basic setup
        recyclerView = (RecyclerView) findViewById(R.id.agp_recycler_view_group_member);
        recyclerView.setLayoutManager(new LinearLayoutManager(ActivityProfileChannel.this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(
                stickyHeaderAdapter.wrap(itemAdapter.wrap(headerAdapter.wrap(fastAdapter))));

        recyclerView.setNestedScrollingEnabled(false);

        //this adds the Sticky Headers within our list
        final StickyRecyclerHeadersDecoration decoration =
                new StickyRecyclerHeadersDecoration(stickyHeaderAdapter);
        recyclerView.addItemDecoration(decoration);

        items = new ArrayList<>();

        ContactItemGroupProfile.mainRole = role.toString();

        fillItem();

        int listSize = contacts.size();

        //txtMemberNumber.setText(listSize + "");

        for (int i = 0; i < listSize; i++) {
            items.add(new ContactItemGroupProfile().setContact(contacts.get(i)).withIdentifier(100 + contacts.indexOf(contacts.get(i))));
        }

        itemAdapter.add(items);

        //so the headers are aware of changes
        stickyHeaderAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                decoration.invalidateHeaders();
            }
        });
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
        bundle.putBoolean("DIALOG_SHOWING", true);
        bundle.putLong("COUNT_MESSAGE", noLastMessage);
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left)
                .addToBackStack(null)
                .replace(R.id.coordinator, fragment).commit();
    }


    //****** create popup

    private class CreatePopUpMessage {

        private void show(View view, final StructContactInfo info) {
            PopupMenu popup = new PopupMenu(ActivityProfileChannel.this, view, Gravity.TOP);
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

    //**********

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
                    } else {
                        new RequestUserInfo().userInfo(userId, roomId + "");
                    }

                    realm.close();
                }
            });
        }
    }

    private void channelKickMember(long roomIdResponse, final long memberId) {
        if (roomIdResponse == roomId) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final List<ContactItemGroupProfile> items = itemAdapter.getAdapterItems();
                    for (int i = 0; i < items.size(); i++) {
                        if (items.get(i).mContact.peerId == memberId) {
                            itemAdapter.remove(i);
                            Realm realm = Realm.getDefaultInstance();
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
                                    for (RealmMember realmMember : realmRoom.getChannelRoom().getMembers()) {
                                        if (realmMember.getPeerId() == memberId) {
                                            realmMember.deleteFromRealm();
                                            participantsCountLabel = realmRoom.getChannelRoom().getParticipantsCountLabel();
                                            participantsCountLabel = (Integer.parseInt(participantsCountLabel) - 1) + "";
                                            realmRoom.getChannelRoom().setParticipantsCountLabel(participantsCountLabel);
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
    }

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
        new RequestChannelKickModerator().channelKickModerator(roomId, peerId);
    }

    //********** interfaces

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
    }

    //***Admin
    @Override
    public void onChannelAddAdmin(long roomId, long memberId) {

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
    }

    //***time out and errors for either of this interfaces

    @Override
    public void onError(int majorCode, int minorCode) {

    }

    @Override
    public void onTimeOut() {

    }

}
