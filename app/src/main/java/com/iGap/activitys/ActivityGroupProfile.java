package com.iGap.activitys;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.iGap.G;
import com.iGap.R;
import com.iGap.adapter.AdapterShearedMedia;
import com.iGap.adapter.items.ContatItemGroupProfile;
import com.iGap.fragments.FragmentNotification;
import com.iGap.fragments.ShowCustomList;
import com.iGap.interface_package.OnFileUploadForActivities;
import com.iGap.interface_package.OnGroupAddAdmin;
import com.iGap.interface_package.OnGroupAddMember;
import com.iGap.interface_package.OnGroupAddModerator;
import com.iGap.interface_package.OnGroupAvatarResponse;
import com.iGap.interface_package.OnGroupEdit;
import com.iGap.interface_package.OnGroupKickAdmin;
import com.iGap.interface_package.OnGroupKickMember;
import com.iGap.interface_package.OnGroupKickModerator;
import com.iGap.interface_package.OnGroupLeft;
import com.iGap.interface_package.OnSelectedList;
import com.iGap.libs.rippleeffect.RippleView;
import com.iGap.module.AttachFile;
import com.iGap.module.CircleImageView;
import com.iGap.module.Contacts;
import com.iGap.module.CustomTextViewMedium;
import com.iGap.module.FileUploadStructure;
import com.iGap.module.StructContactInfo;
import com.iGap.module.Utils;
import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.RealmAttachment;
import com.iGap.realm.RealmAvatar;
import com.iGap.realm.RealmAvatarFields;
import com.iGap.realm.RealmChatHistory;
import com.iGap.realm.RealmChatHistoryFields;
import com.iGap.realm.RealmContacts;
import com.iGap.realm.RealmContactsFields;
import com.iGap.realm.RealmGroupRoom;
import com.iGap.realm.RealmMember;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.RealmRoomFields;
import com.iGap.realm.enums.GroupChatRole;
import com.iGap.request.RequestGroupAddAdmin;
import com.iGap.request.RequestGroupAddMember;
import com.iGap.request.RequestGroupAddModerator;
import com.iGap.request.RequestGroupAvatarAdd;
import com.iGap.request.RequestGroupEdit;
import com.iGap.request.RequestGroupKickAdmin;
import com.iGap.request.RequestGroupKickMember;
import com.iGap.request.RequestGroupKickModerator;
import com.iGap.request.RequestGroupLeft;
import com.mikepenz.fastadapter.AbstractAdapter;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.IItemAdapter;
import com.mikepenz.fastadapter.adapters.HeaderAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
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


/**
 * Created by android3 on 9/18/2016.
 */
public class ActivityGroupProfile extends ActivityEnhanced implements OnGroupAvatarResponse, OnFileUploadForActivities {


    private CircleImageView imvGroupAvatar;
    private TextView txtGroupNameTitle;
    private TextView txtGroupName;
    private TextView txtGroupDescription;
    private TextView txtNumberOfSharedMedia;
    private TextView txtMemberNumber;
    private TextView txtMore;
    private AppBarLayout appBarLayout;
    private FloatingActionButton fab;

    LinearLayout layoutSetting;
    LinearLayout layoutSetAdmin;
    LinearLayout layoutSetModereator;
    LinearLayout layoutMemberCanAddMember;
    LinearLayout layoutNotificatin;
    LinearLayout layoutDeleteAndLeftGroup;

    private String tmp = "";

    private int numberUploadItem = 5;

    List<StructContactInfo> contacts;
    List<IItem> items;
    ItemAdapter itemAdapter;
    RecyclerView recyclerView;
    private FastAdapter fastAdapter;

    AttachFile attachFile;

    private long roomId;
    private String title;
    private String description;
    private String initials;
    private String color;
    private GroupChatRole role;
    private String participantsCountLabel;
    private RealmList<RealmMember> members;

    private int countAddMemberResponse = 0;
    private int countAddMemberRequest = 0;

    private long startMessageId = 0;

    private PopupWindow popupWindow;


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
        participantsCountLabel = realmGroupRoom.getParticipantsCountLabel();
        members = realmGroupRoom.getMembers();
        description = realmGroupRoom.getDescription();

        realm.close();

        initComponent();

        attachFile = new AttachFile(this);

        G.uploaderUtil.setActivityCallbacks(this);
        G.onGroupAvatarResponse = this;
    }

    private void initComponent() {

        Button btnBack = (Button) findViewById(R.id.agp_btn_back);
        btnBack.setTypeface(G.fontawesome);
//
        RippleView rippleBack = (RippleView) findViewById(R.id.agp_ripple_back);
        rippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {

            @Override
            public void onComplete(RippleView rippleView) {
                finish();
            }

        });

        Button btnMenu = (Button) findViewById(R.id.agp_btn_menu);
        btnMenu.setTypeface(G.fontawesome);

        final int screenWidth = (int) (getResources().getDisplayMetrics().widthPixels / 1.7);
        RippleView rippleMenu = (RippleView) findViewById(R.id.agp_ripple_menu);
        rippleMenu.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {

            @Override
            public void onComplete(RippleView rippleView) {

                LinearLayout layoutDialog = new LinearLayout(ActivityGroupProfile.this);
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutDialog.setOrientation(LinearLayout.VERTICAL);
                layoutDialog.setBackgroundColor(getResources().getColor(android.R.color.white));
                TextView text1 = new TextView(ActivityGroupProfile.this);
                TextView text2 = new TextView(ActivityGroupProfile.this);
                TextView text3 = new TextView(ActivityGroupProfile.this);

                text1.setText(getResources().getString(R.string.Edit_Name));
                text2.setText(getResources().getString(R.string.Delete_and_leave_Group));
                text3.setText(getResources().getString(R.string.chi_popup_shortcut));

                text1.setTextColor(getResources().getColor(android.R.color.black));
                text2.setTextColor(getResources().getColor(android.R.color.black));
                text3.setTextColor(getResources().getColor(android.R.color.black));

                text1.setText(getResources().getString(R.string.Search));
                text2.setText(getResources().getString(R.string.clear_history));
                text3.setText(getResources().getString(R.string.delete_chat));

                int dim20 = (int) getResources().getDimension(R.dimen.dp20);
                int dim12 = (int) getResources().getDimension(R.dimen.dp12);

                text1.setTextSize(16);
                text2.setTextSize(16);
                text3.setTextSize(16);

                text1.setPadding(dim20, dim12, dim12, dim20);
                text2.setPadding(dim20, 0, dim12, dim20);
                text3.setPadding(dim20, 0, dim12, dim20);

                text1.setPadding(10, 10, 10, 10);
                text2.setPadding(10, 0, 10, 0);
                text3.setPadding(10, 10, 10, 10);
                layoutDialog.addView(text1, params);
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
                popupWindow.showAtLocation(layoutDialog,
                        Gravity.RIGHT | Gravity.TOP, (int) getResources().getDimension(R.dimen.dp16), (int) getResources().getDimension(R.dimen.dp32));
//                popupWindow.showAsDropDown(v);

                text1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(G.context, R.string.edit_name, Toast.LENGTH_SHORT).show();
                        popupWindow.dismiss();
                    }
                });
                text2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(G.context, R.string.delete_and_leave_group, Toast.LENGTH_SHORT).show();
                        popupWindow.dismiss();
                    }
                });
                text3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(G.context, R.string.add_shortcut, Toast.LENGTH_SHORT).show();
                        popupWindow.dismiss();
                    }
                });
            }

        });
//        btnMenu.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//            }
//        });


        layoutSetting = (LinearLayout) findViewById(R.id.agp_ll_seetting);
        layoutSetAdmin = (LinearLayout) findViewById(R.id.agp_ll_set_admin);
        layoutSetModereator = (LinearLayout) findViewById(R.id.agp_ll_set_modereator);
        layoutMemberCanAddMember = (LinearLayout) findViewById(R.id.agp_ll_member_can_add_member);
        layoutNotificatin = (LinearLayout) findViewById(R.id.agp_ll_notification);
        layoutDeleteAndLeftGroup = (LinearLayout) findViewById(R.id.agp_ll_delete_and_left_group);


        imvGroupAvatar = (CircleImageView) findViewById(R.id.agp_imv_group_avatar);

        txtGroupNameTitle = (TextView) findViewById(R.id.agp_txt_group_name_title);
        txtGroupNameTitle.setText(title);

        txtGroupName = (TextView) findViewById(R.id.agp_txt_group_name);
        txtGroupName.setText(title);

        txtGroupDescription = (TextView) findViewById(R.id.agp_txt_group_description);
        txtGroupDescription.setText(description);

        txtNumberOfSharedMedia = (TextView) findViewById(R.id.agp_txt_number_of_shared_media);
        txtNumberOfSharedMedia.setText(AdapterShearedMedia.getCountOfSheareddMedia(roomId) + "");
        txtMemberNumber = (TextView) findViewById(R.id.agp_txt_member_number);
        appBarLayout = (AppBarLayout) findViewById(R.id.agp_appbar);


        LinearLayout llGroupName = (LinearLayout) findViewById(R.id.agp_ll_group_name);
        llGroupName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChangeGroupName();
            }
        });

        LinearLayout llGroupDescription = (LinearLayout) findViewById(R.id.agp_ll_group_description);
        llGroupDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChangeGroupDescription();
            }
        });


        LinearLayout llSharedMedia = (LinearLayout) findViewById(R.id.agp_ll_sheared_media);
        llSharedMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ActivityGroupProfile.this, ActivityShearedMedia.class);
                intent.putExtra("RoomID", roomId);
                startActivity(intent);

            }
        });

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                TextView titleToolbar = (TextView) findViewById(R.id.agp_txt_titleToolbar);
                if (verticalOffset < -appBarLayout.getTotalScrollRange() / 4) {

                    titleToolbar.animate().alpha(1).setDuration(300);
                    titleToolbar.setVisibility(View.VISIBLE);

                } else {
                    titleToolbar.animate().alpha(0).setDuration(500);
                    titleToolbar.setVisibility(View.GONE);
                }
            }
        });


        fab = (FloatingActionButton) findViewById(R.id.agp_fab_setPic);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!G.imageFile.exists()) {
                    startDialogSelectPicture(R.array.profile);
                } else {
                    startDialogSelectPicture(R.array.profile_delete);
                }
            }
        });


        txtMore = (TextView) findViewById(R.id.agp_txt_more);
        txtMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int count = items.size();
                int listSize = contacts.size();

                for (int i = count; i < listSize && i < count + numberUploadItem; i++) {
                    items.add(new ContatItemGroupProfile().setContact(contacts.get(i)).withIdentifier(100 + contacts.indexOf(contacts.get(i))));
                }

                itemAdapter.clear();
                itemAdapter.add(items);

                if (items.size() >= listSize)
                    txtMore.setVisibility(View.GONE);

            }
        });


        CircleImageView imvAddMember = (CircleImageView) findViewById(R.id.agp_imv_add_member);
        imvAddMember.setOnClickListener(new View.OnClickListener() {
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
                    Log.e("ddd", "toggle button on");
                } else {
                    Log.e("ddd", "toggle button off");
                }
            }
        });


        TextView txtNotification = (TextView) findViewById(R.id.agp_txt_str_notification_and_sound);
        txtNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("ddd", "Notification clicked");

                FragmentNotification fragmentNotification = new FragmentNotification();
                Bundle bundle = new Bundle();
                bundle.putString("PAGE", "GROUP");
                bundle.putLong("ID", roomId);
                fragmentNotification.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left).replace(R.id.fragmentContainer_group_profile, fragmentNotification).commit();

            }
        });


        TextView txtDeleteGroup = (TextView) findViewById(R.id.agp_txt_str_delete_and_leave_group);
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

            }

        });

        imvGroupAvatar.setImageBitmap(com.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) imvGroupAvatar.getContext().getResources().getDimension(R.dimen.dp60), initials, color));
        txtMemberNumber.setText(participantsCountLabel);


        setUiIndependRole();

        initRecycleView();
    }

    private void initRecycleView() {

        //create our FastAdapter
        fastAdapter = new FastAdapter();
        fastAdapter.withSelectable(true);

        //create our adapters
        final StickyHeaderAdapter stickyHeaderAdapter = new StickyHeaderAdapter();
        final HeaderAdapter headerAdapter = new HeaderAdapter();
        itemAdapter = new ItemAdapter();
        itemAdapter.withFilterPredicate(new IItemAdapter.Predicate<ContatItemGroupProfile>() {
            @Override
            public boolean filter(ContatItemGroupProfile item, CharSequence constraint) {
                return !item.mContact.displayName.toLowerCase().startsWith(String.valueOf(constraint).toLowerCase());
            }
        });
        fastAdapter.withOnClickListener(new FastAdapter.OnClickListener<ContatItemGroupProfile>() {
            @Override
            public boolean onClick(View v, IAdapter adapter, ContatItemGroupProfile item, int position) {

                Log.e("dddd", " invite click  " + position);
                // TODO: 9/14/2016 nejati     go into clicked user page

                return false;
            }
        });

        fastAdapter.withOnLongClickListener(new FastAdapter.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v, IAdapter adapter, IItem item, int position) {

                if (role == GroupChatRole.OWNER) {

                    if (contacts.get(position).role.equals(ProtoGlobal.GroupRoom.Role.MEMBER.toString())) {
                        kickMember(contacts.get(position).peerId);
                    } else if (contacts.get(position).role.equals(ProtoGlobal.GroupRoom.Role.ADMIN.toString())) {
                        kickAdmin(contacts.get(position).peerId);
                    } else if (contacts.get(position).role.equals(ProtoGlobal.GroupRoom.Role.MODERATOR.toString())) {
                        kickModerator(contacts.get(position).peerId);
                    }
                } else if (role == GroupChatRole.ADMIN) {

                    if (contacts.get(position).role.equals(ProtoGlobal.GroupRoom.Role.MEMBER.toString())) {
                        kickMember(contacts.get(position).peerId);
                    } else if (contacts.get(position).role.equals(ProtoGlobal.GroupRoom.Role.MODERATOR.toString())) {
                        kickModerator(contacts.get(position).peerId);
                    }
                } else if (role == GroupChatRole.MODERATOR) {

                    if (contacts.get(position).role.equals(ProtoGlobal.GroupRoom.Role.MEMBER.toString())) {
                        kickMember(contacts.get(position).peerId);
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


        fillItem();


        int listSize = contacts.size();

        txtMemberNumber.setText(listSize + "");


        for (int i = 0; i < listSize && i < 3; i++) {
            items.add(new ContatItemGroupProfile().setContact(contacts.get(i)).withIdentifier(100 + contacts.indexOf(contacts.get(i))));
        }


        if (listSize < 4)
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

    private void fillItem() {

        contacts = new ArrayList<>();


        Realm realm = Realm.getDefaultInstance();

        for (RealmMember member : members) {
            String role = member.getRole();
            long id = member.getPeerId();

            RealmContacts rc = realm.where(RealmContacts.class).equalTo(RealmContactsFields.ID, id).findFirst();

            if (rc != null) {

                StructContactInfo s = new StructContactInfo(rc.getId(), rc.getDisplay_name(), rc.getStatus(), false, false, rc.getPhone() + "");
                s.role = role;

                contacts.add(s);
            }


        }

        realm.close();

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            String filePath = null;
            long avatarId = System.nanoTime();
            switch (requestCode) {
                case AttachFile.request_code_TAKE_PICTURE:
                    filePath = AttachFile.imagePath;
                    Log.e("ddd", filePath + "     image path");
                    break;
                case AttachFile.request_code_media_from_gallary:
                    filePath = AttachFile.getFilePathFromUri(data.getData());
                    Log.e("ddd", filePath + "    gallary file path");
                    break;

            }

            new UploadTask().execute(filePath, avatarId);
        }
    }

    @Override
    public void onAvatarAdd(final long roomId, final ProtoGlobal.Avatar avatar) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmAvatar realmAvatar = realm.where(RealmAvatar.class).equalTo(RealmAvatarFields.OWNER_ID, roomId).findFirst();
                if (realmAvatar == null) {
                    realmAvatar = realm.createObject(RealmAvatar.class);
                    realmAvatar.setId(System.nanoTime());
                    realmAvatar.setOwnerId(roomId);
                }

                realmAvatar.setFile(RealmAttachment.build(avatar.getFile()));
            }
        });
        realm.close();
    }

    @Override
    public void onFileUploaded(final FileUploadStructure uploadStructure, String identity) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                imvGroupAvatar.setImageURI(Uri.fromFile(new File(uploadStructure.filePath)));
            }
        });

        new RequestGroupAvatarAdd().groupAvatarAdd(roomId, uploadStructure.token);
    }

    @Override
    public void onFileUploading(FileUploadStructure uploadStructure, String identity, double progress) {
        // TODO: 10/20/2016 [Alireza] update view something like updating progress
    }

    private static class UploadTask extends AsyncTask<Object, FileUploadStructure, FileUploadStructure> {
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

                byte[] fileHash = Utils.getFileHash(fileUploadStructure);
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

//            ContatItemGroupProfile ci=(ContatItemGroupProfile)item;
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
            if (((ContatItemGroupProfile) item).mContact != null) {
                //based on the position we set the headers text
                textView.setText(String.valueOf(((ContatItemGroupProfile) item).mContact.displayName.toUpperCase().charAt(0)));
            }

        }

        /**
         * REQUIRED FOR THE FastAdapter. Set order to < 0 to tell the FastAdapter he can ignore this one.
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
        public int getGlobalPosition(int position) {
            return -1;
        }
    }

    //***********************************************************************************************************************

    //dialog for choose pic from gallery or camera
    private void startDialogSelectPicture(int r) {

        new MaterialDialog.Builder(this)
                .title(R.string.choose_picture)
                .negativeText(R.string.cansel)
                .items(r)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                        if (text.toString().equals(getString(R.string.from_camera))) {

                            if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {

                                attachFile.requestTakePicture();

                                dialog.dismiss();

                            } else {
                                Toast.makeText(ActivityGroupProfile.this, R.string.please_check_your_camera, Toast.LENGTH_SHORT).show();
                            }

                        } else if (text.toString().equals(getString(R.string.delete_photo))) {
                            // TODO: 9/20/2016  delete  group image

                        } else {
                            attachFile.requestOpenGalleryForImage();
                        }

                    }
                })
                .show();
    }


    //***********************************************************************************************************************

    private void addMemberToGroup() {


        List<StructContactInfo> userList = Contacts.retrieve(null);

        for (int i = 0; i < contacts.size(); i++) {
            long id = contacts.get(i).peerId;
            for (int j = 0; j < userList.size(); j++) {
                if (userList.get(j).peerId == id) {
                    userList.remove(j);
                    break;
                }
            }
        }


        Fragment fragment = ShowCustomList.newInstance(userList, new OnSelectedList() {
            @Override
            public void getSelectedList(boolean result, String message, int countForShowLastMessage, final ArrayList<StructContactInfo> list) {


                countAddMemberResponse = 0;
                countAddMemberRequest = list.size();

                G.onGroupAddMember = new OnGroupAddMember() {
                    @Override
                    public void onGroupAddMember() {
                        countAddMemberResponse++;

                        if (countAddMemberResponse >= countAddMemberRequest) {

                            for (int i = 0; i < list.size(); i++) {
                                contacts.add(list.get(i));
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    txtMemberNumber.setText(contacts.size() + "");
                                    int count = items.size();
                                    final int listSize = contacts.size();
                                    for (int i = count; i < listSize; i++) {
                                        items.add(new ContatItemGroupProfile().setContact(contacts.get(i)).withIdentifier(100 + contacts.indexOf(contacts.get(i))));
                                    }
                                    itemAdapter.clear();
                                    itemAdapter.add(items);
                                    txtMore.setVisibility(View.GONE);

                                }
                            });
                        }
                    }
                };

                memberRealmAndRequest(list, countForShowLastMessage);
            }
        });
        Bundle bundle = new Bundle();
        bundle.putBoolean("DIALOG_SHOWING", true);
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left)
                .addToBackStack(null).replace(R.id.fragmentContainer_group_profile, fragment)
                .commit();

    }

    /**
     * add member to realm and send request to server for really added this contacts to this group
     */
    private void memberRealmAndRequest(final ArrayList<StructContactInfo> list, int messageCount) {
        Realm realm = Realm.getDefaultInstance();

        if (messageCount == 0) {
            startMessageId = 0;
        } else {
            RealmResults<RealmChatHistory> realmChatHistories = realm.where(RealmChatHistory.class).equalTo(RealmChatHistoryFields.ROOM_ID, roomId).findAllSorted("id", Sort.DESCENDING);

            if (messageCount >= realmChatHistories.size()) {
                // if count is bigger than exist messages get first message id that exist
                RealmResults<RealmChatHistory> realmChatHistoriesAscending = realm.where(RealmChatHistory.class).equalTo(RealmChatHistoryFields.ROOM_ID, roomId).findAllSorted("id", Sort.ASCENDING);
                for (final RealmChatHistory chatHistory : realmChatHistoriesAscending) {

                    if (chatHistory.getRoomMessage() != null) {
                        startMessageId = chatHistory.getRoomMessage().getMessageId();
                        Log.i("HHH", "startMessageId1 : " + startMessageId);
                        Log.i("HHH", "getMessage1 : " + chatHistory.getRoomMessage().getMessage());
                        break;
                    }
                }

            } else {

                for (final RealmChatHistory chatHistory : realmChatHistories) {
                    messageCount--;
                    if (messageCount == 0) {
                        startMessageId = chatHistory.getRoomMessage().getMessageId();
                        Log.i("HHH", "startMessageId2 : " + startMessageId);
                        Log.i("HHH", "getMessage2 : " + chatHistory.getRoomMessage().getMessage());
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
                    int autoIncrement = 0;
                    if (realm.where(RealmMember.class).max("id") != null) {
                        autoIncrement = realm.where(RealmMember.class).max("id").intValue() + 1;
                    }
                    realmMember.setId(autoIncrement);
                    realmMember.setPeerId(peerId);
                    realmMember.setRole(ProtoGlobal.GroupRoom.Role.MEMBER.toString());
                    realmMember = realm.copyToRealm(realmMember);

                    members.add(realmMember);

                    //request for add member
                    new RequestGroupAddMember().groupAddMember(roomId, peerId, startMessageId, ProtoGlobal.GroupRoom.Role.MEMBER);
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


    private void ChangeGroupName() {

        MaterialDialog dialog = new MaterialDialog.Builder(ActivityGroupProfile.this)
                .title(R.string.group_name)
                .positiveText(R.string.save)
                .alwaysCallInputCallback()
                .widgetColor(getResources().getColor(R.color.toolbar_background))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        G.onGroupEdit = new OnGroupEdit() {
                            @Override
                            public void onGroupEdit(final long roomId, final String name, final String description) {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        title = name;
                                        txtGroupNameTitle.setText(name);
                                        txtGroupName.setText(name);

                                    }
                                });


                            }
                        };

                        new RequestGroupEdit().groupEdit(roomId, tmp, txtGroupDescription.getText().toString());


                    }
                })
                .negativeText(getString(R.string.cancel))
                .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_TEXT)
                .input(getString(R.string.please_enter_group_name), txtGroupName.getText().toString(), new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        // Do something

                        View positive = dialog.getActionButton(DialogAction.POSITIVE);
                        tmp = input.toString();
                        if (!input.toString().equals(txtGroupName.getText().toString())) {

                            positive.setClickable(true);
                            positive.setAlpha(1.0f);
                        } else {
                            positive.setClickable(false);
                            positive.setAlpha(0.5f);
                        }

                    }

                }).show();
    }

    private void ChangeGroupDescription() {
        MaterialDialog dialog = new MaterialDialog.Builder(ActivityGroupProfile.this)
                .title(R.string.group_description)
                .positiveText(getString(R.string.save))
                .alwaysCallInputCallback()
                .widgetColor(getResources().getColor(R.color.toolbar_background))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
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
                        };

                        new RequestGroupEdit().groupEdit(roomId, txtGroupName.getText().toString(), tmp);


                    }
                })
                .negativeText(getString(R.string.cancel))
                .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_TEXT)
                .input(getString(R.string.please_enter_group_description), txtGroupDescription.getText().toString(), new MaterialDialog.InputCallback() {
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

        new MaterialDialog.Builder(ActivityGroupProfile.this)
                .content(R.string.do_you_want_to_delete_this_group)
                .positiveText(R.string.ok)
                .negativeText(R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        G.onGroupLeft = new OnGroupLeft() {
                            @Override
                            public void onGroupLeft(final long roomId, long memberId) {

                                ActivityGroupProfile.this.finish();

                                if (ActivityChat.activityChat != null)
                                    ActivityChat.activityChat.finish();

                            }
                        };

                        new RequestGroupLeft().groupLeft(roomId);

                    }
                })
                .show();
    }

    /**
     * if user was admin set  role to member
     *
     * @param memberID
     */
    private void kickAdmin(final long memberID) {

        new MaterialDialog.Builder(ActivityGroupProfile.this)
                .content(R.string.do_you_want_to_set_admin_role_to_member)
                .positiveText(R.string.ok)
                .negativeText(R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        G.onGroupKickAdmin = new OnGroupKickAdmin() {
                            @Override
                            public void onGroupKickAdmin(long roomId, long memberId) {


                                for (int i = 0; i < contacts.size(); i++) {
                                    if (contacts.get(i).peerId == memberId) {
                                        contacts.get(i).role = ProtoGlobal.GroupRoom.Role.MEMBER.toString();
                                        final int finalI = i;
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                IItem item = (new ContatItemGroupProfile().setContact(contacts.get(finalI)).withIdentifier(100 + contacts.indexOf(contacts.get(finalI))));
                                                itemAdapter.set(finalI, item);
                                            }
                                        });

                                        break;
                                    }
                                }


                            }
                        };

                        new RequestGroupKickAdmin().groupKickAdmin(roomId, memberID);

                    }
                })
                .show();


    }

    /**
     * delete this member from list of member group
     *
     * @param memberID
     */
    private void kickMember(final long memberID) {

        new MaterialDialog.Builder(ActivityGroupProfile.this)
                .content(R.string.do_you_want_to_kick_this_member)
                .positiveText(R.string.ok)
                .negativeText(R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        G.onGroupKickMember = new OnGroupKickMember() {
                            @Override
                            public void onGroupKickMember(long roomId, final long memberId) {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        for (int j = 0; j < contacts.size(); j++) {
                                            if (contacts.get(j).peerId == memberId) {
                                                contacts.remove(j);
                                                itemAdapter.remove(j);

                                                break;
                                            }
                                        }

                                        txtMemberNumber.setText(contacts.size() + "");

                                    }
                                });


                            }
                        };

                        new RequestGroupKickMember().groupKickMember(roomId, memberID);
                    }
                })
                .show();


    }

    private void kickModerator(final long memberID) {

        new MaterialDialog.Builder(ActivityGroupProfile.this)
                .content(R.string.do_you_want_to_set_modereator_role_to_member)
                .positiveText(R.string.ok)
                .negativeText(R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        G.onGroupKickModerator = new OnGroupKickModerator() {
                            @Override
                            public void onGroupKickModerator(long roomId, long memberId) {

                                for (int i = 0; i < contacts.size(); i++) {
                                    if (contacts.get(i).peerId == memberId) {
                                        contacts.get(i).role = ProtoGlobal.GroupRoom.Role.MEMBER.toString();
                                        final int finalI = i;
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                IItem item = (new ContatItemGroupProfile().setContact(contacts.get(finalI)).withIdentifier(100 + contacts.indexOf(contacts.get(finalI))));
                                                itemAdapter.set(finalI, item);
                                            }
                                        });

                                        break;
                                    }
                                }
                            }


                        };

                        new RequestGroupKickModerator().groupKickModerator(roomId, memberID);

                    }
                })
                .show();

    }

    private void setMemberRoleToModerator() {

        Fragment fragment = ShowCustomList.newInstance(contacts, new OnSelectedList() {
            @Override
            public void getSelectedList(boolean result, String message, int count, final ArrayList<StructContactInfo> list) {

                G.onGroupAddModerator = new OnGroupAddModerator() {
                    @Override
                    public void onGroupAddModerator(long roomId, final long memberId) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                for (int i = 0; i < contacts.size(); i++) {
                                    if (contacts.get(i).peerId == memberId) {
                                        contacts.get(i).role = ProtoGlobal.GroupRoom.Role.MODERATOR.toString();

                                        if (i < itemAdapter.getAdapterItemCount()) {
                                            IItem item = (new ContatItemGroupProfile().setContact(contacts.get(i)).withIdentifier(100 + contacts.indexOf(contacts.get(i))));
                                            itemAdapter.set(i, item);
                                        }
                                        break;
                                    }
                                }

                            }
                        });
                    }
                };


                for (int i = 0; i < list.size(); i++) {
                    new RequestGroupAddModerator().groupAddModerator(roomId, list.get(i).peerId);
                }
            }
        });
        Bundle bundle = new Bundle();
        bundle.putBoolean("DIALOG_SHOWING", false);
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,
                R.anim.slide_in_right, R.anim.slide_out_left).addToBackStack(null).replace(R.id.fragmentContainer_group_profile, fragment).commit();


    }

    private void setMemberRoleToAdmin() {

        Fragment fragment = ShowCustomList.newInstance(contacts, new OnSelectedList() {
            @Override
            public void getSelectedList(boolean result, String message, int count, ArrayList<StructContactInfo> list) {


                G.onGroupAddAdmin = new OnGroupAddAdmin() {
                    @Override
                    public void onGroupAddAdmin(long roomId, final long memberId) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                for (int i = 0; i < contacts.size(); i++) {
                                    if (contacts.get(i).peerId == memberId) {
                                        contacts.get(i).role = ProtoGlobal.GroupRoom.Role.ADMIN.toString();

                                        if (i < itemAdapter.getAdapterItemCount()) {
                                            IItem item = (new ContatItemGroupProfile().setContact(contacts.get(i)).withIdentifier(100 + contacts.indexOf(contacts.get(i))));
                                            itemAdapter.set(i, item);
                                        }

                                        break;
                                    }
                                }
                            }
                        });
                    }
                };


                for (int i = 0; i < list.size(); i++) {
                    new RequestGroupAddAdmin().groupAddAdmin(roomId, list.get(i).peerId);
                }


            }
        });
        Bundle bundle = new Bundle();
        bundle.putBoolean("DIALOG_SHOWING", false);
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,
                R.anim.slide_in_right, R.anim.slide_out_left).addToBackStack(null).replace(R.id.fragmentContainer_group_profile, fragment).commit();

    }


}
