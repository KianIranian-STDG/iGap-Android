package com.iGap.activitys;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.iGap.G;
import com.iGap.R;
import com.iGap.adapter.items.ContatItemGroupProfile;
import com.iGap.fragments.ShowCustomList;
import com.iGap.interface_package.OnFileUpload;
import com.iGap.interface_package.OnFileUploadStatusResponse;
import com.iGap.interface_package.OnGroupAddAdmin;
import com.iGap.interface_package.OnGroupAddMember;
import com.iGap.interface_package.OnGroupAddModerator;
import com.iGap.interface_package.OnGroupEdit;
import com.iGap.interface_package.OnGroupKickAdmin;
import com.iGap.interface_package.OnGroupKickMember;
import com.iGap.interface_package.OnGroupKickModerator;
import com.iGap.interface_package.OnGroupLeft;
import com.iGap.interface_package.OnSelectedList;
import com.iGap.module.AttachFile;
import com.iGap.module.CircleImageView;
import com.iGap.module.Contacts;
import com.iGap.module.CustomTextViewMedium;
import com.iGap.module.FileUploadStructure;
import com.iGap.module.StructContactInfo;
import com.iGap.module.Utils;
import com.iGap.proto.ProtoFileUploadStatus;
import com.iGap.proto.ProtoGlobal;
import com.iGap.proto.ProtoResponse;
import com.iGap.realm.RealmContacts;
import com.iGap.realm.RealmGroupRoom;
import com.iGap.realm.RealmMember;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.enums.GroupChatRole;
import com.iGap.request.RequestFileUpload;
import com.iGap.request.RequestFileUploadInit;
import com.iGap.request.RequestFileUploadStatus;
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
import java.util.concurrent.CopyOnWriteArrayList;

import io.realm.Realm;
import io.realm.RealmList;

/**
 * Created by android3 on 9/18/2016.
 */
public class ActivityGroupProfile extends ActivityEnhanced implements OnFileUploadStatusResponse, OnFileUpload {


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


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_profile);

        Bundle extras = getIntent().getExtras();
        roomId = extras.getLong("RoomId");

        Realm realm = Realm.getDefaultInstance();

        //group info
        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("id", roomId).findFirst();
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
        G.onFileUploadStatusResponse = this;
    }

    private void initComponent() {

        Button btnBack = (Button) findViewById(R.id.agp_btn_back);
        btnBack.setTypeface(G.fontawesome);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Button btnMenu = (Button) findViewById(R.id.agp_btn_menu);
        btnMenu.setTypeface(G.fontawesome);
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });


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

                Log.e("ddd", "shared media click");

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
            }
        });


        TextView txtDeleteGroup = (TextView) findViewById(R.id.agp_txt_str_delete_and_leave_group);
        txtDeleteGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                groupLeft();
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

            RealmContacts rc = realm.where(RealmContacts.class).equalTo("id", id).findFirst();

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
            mSelectedFiles.add(result);
            G.uploaderUtil.startUploading(result.fileSize, Long.toString(result.messageId));
        }
    }

    @Override
    public void onFileUploadStatus(ProtoFileUploadStatus.FileUploadStatusResponse.Status status, double progress, int recheckDelayMS, final String identity, ProtoResponse.Response response) {
        final FileUploadStructure fileUploadStructure = getSelectedFile(identity);
        if (fileUploadStructure == null) {
            return;
        }
        if (status == ProtoFileUploadStatus.FileUploadStatusResponse.Status.PROCESSED && progress == 100D) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    imvGroupAvatar.setImageURI(Uri.fromFile(new File(fileUploadStructure.filePath)));
                }
            });

            Log.i("AVATAR", "fileUploadStructure.token : " + fileUploadStructure.token + "  ||  roomId : " + roomId);
            new RequestGroupAvatarAdd().groupAvatarAdd(roomId, fileUploadStructure.token);
            // TODO: 10/8/2016 [Alireza] harkari mesle update view

            // remove from selected files to prevent calling this method multiple times
            // multiple calling may occurs because of the server
            try {
                // FIXME: 9/19/2016 [Alireza Eskandarpour Shoferi] uncomment plz
                //removeFromSelectedFiles(identity);
            } catch (Exception e) {
                Log.i("BreakPoint", e.getMessage());
                e.printStackTrace();
            }

            // close file into structure
            try {
                if (fileUploadStructure != null) {
                    fileUploadStructure.closeFile();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (status == ProtoFileUploadStatus.FileUploadStatusResponse.Status.PROCESSING) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    new RequestFileUploadStatus().fileUploadStatus(fileUploadStructure.token, identity);
                }
            }, recheckDelayMS);
        } else {
            G.uploaderUtil.startUploading(fileUploadStructure.fileSize, Long.toString(fileUploadStructure.messageId));
        }
    }

    @Override
    public void OnFileUploadOption(int firstBytesLimit, int lastBytesLimit, int maxConnection, String fileHashAsIdentity, ProtoResponse.Response response) {
        try {
            FileUploadStructure fileUploadStructure = getSelectedFile(fileHashAsIdentity);
            // getting bytes from file as server said
            byte[] bytesFromFirst = Utils.getBytesFromStart(fileUploadStructure, firstBytesLimit);
            byte[] bytesFromLast = Utils.getBytesFromEnd(fileUploadStructure, lastBytesLimit);
            // make second request
            new RequestFileUploadInit().fileUploadInit(bytesFromFirst, bytesFromLast, fileUploadStructure.fileSize, fileUploadStructure.fileHash, Long.toString(fileUploadStructure.messageId), fileUploadStructure.fileName);
        } catch (IOException e) {
            Log.i("BreakPoint", e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void OnFileUploadInit(String token, final double progress, long offset, int limit, final String identity, ProtoResponse.Response response) {
        // token needed for requesting upload
        // updating structure with new token
        FileUploadStructure fileUploadStructure = getSelectedFile(identity);
        fileUploadStructure.token = token;

        // not already uploaded
        if (progress != 100.0) {
            try {
                byte[] bytes = Utils.getNBytesFromOffset(fileUploadStructure, (int) offset, limit);
                // make third request for first time
                new RequestFileUpload().fileUpload(token, offset, bytes, identity);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                // TODO: 10/8/2016 [Alireza] harkari mesle update view

                if (isFileExistInList(Long.parseLong(identity))) {
                    // handle when the file has already uploaded
                    onFileUploadComplete(identity, response);
                }
            } catch (Exception e) {
                Log.i("BreakPoint", e.getMessage());
            }
        }
    }

    /**
     * does file exist in the list
     * useful for preventing from calling onFileUploadComplete() multiple for a file
     *
     * @param messageId file hash
     * @return boolean
     */
    private boolean isFileExistInList(long messageId) {
        for (FileUploadStructure fileUploadStructure : mSelectedFiles) {
            if (fileUploadStructure.messageId == messageId) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onFileUpload(final double progress, long nextOffset, int nextLimit, final String identity, ProtoResponse.Response response) {
        final long startOnFileUploadTime = System.currentTimeMillis();

        // for specific views, tags must be set with files hashes
        // get the view which has provided hash string
        // then do anything you want to do wit that view

        try {
            // update progress
            Log.i("SOC", "************************************ identity : " + identity + "  ||  progress : " + progress);
            Log.i("BreakPoint", identity + " > bad az update progress");

            if (progress != 100.0) {
                // TODO: 10/8/2016 [Alireza] harkari mesle update view

                Log.i("BreakPoint", identity + " > 100 nist");
                FileUploadStructure fileUploadStructure = getSelectedFile(identity);
                Log.i("BreakPoint", identity + " > fileUploadStructure");
                final long startGetNBytesTime = System.currentTimeMillis();
                byte[] bytes = Utils.getNBytesFromOffset(fileUploadStructure, (int) nextOffset, nextLimit);

                fileUploadStructure.getNBytesTime += System.currentTimeMillis() - startGetNBytesTime;

                Log.i("BreakPoint", identity + " > after bytes");
                // make request till uploading has finished
                final long startSendReqTime = System.currentTimeMillis();

                new RequestFileUpload().fileUpload(fileUploadStructure.token, nextOffset, bytes, identity);
                fileUploadStructure.sendRequestsTime += System.currentTimeMillis() - startSendReqTime;
                Log.i("BreakPoint", identity + " > after fileUpload request");

                fileUploadStructure.elapsedInOnFileUpload += System.currentTimeMillis() - startOnFileUploadTime;
            } else {
                if (isFileExistInList(Long.parseLong(identity))) {
                    // handle when the file has already uploaded
                    onFileUploadComplete(identity, response);
                }
            }
        } catch (IOException e) {
            Log.i("BreakPoint", e.getMessage());
            e.printStackTrace();
        }
    }

    // selected files (paths)
    private static CopyOnWriteArrayList<FileUploadStructure> mSelectedFiles = new CopyOnWriteArrayList<>();

    @Override
    public void onFileUploadComplete(String fileHashAsIdentity, ProtoResponse.Response response) {
        final FileUploadStructure fileUploadStructure = getSelectedFile(fileHashAsIdentity);

        new RequestFileUploadStatus().fileUploadStatus(fileUploadStructure.token, fileHashAsIdentity);
    }


    /**
     * get file with hash string
     *
     * @param identity file hash
     * @return FileUploadStructure
     */
    @Nullable
    private FileUploadStructure getSelectedFile(String identity) {
        for (FileUploadStructure structure : mSelectedFiles) {
            if (structure.messageId == Long.parseLong(identity)) {
                return structure;
            }
        }
        return null;
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
                .title("Choose Picture")
                .negativeText("CANCEL")
                .items(r)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                        if (text.toString().equals("From Camera")) {

                            if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {

                                attachFile.requestTakePicture();

                                dialog.dismiss();

                            } else {
                                Toast.makeText(ActivityGroupProfile.this, "Please check your Camera", Toast.LENGTH_SHORT).show();
                            }

                        } else if (text.toString().equals("Delete photo")) {
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

        Fragment fragment = ShowCustomList.newInstance(Contacts.retrieve(null), new OnSelectedList() {
            @Override
            public void getSelectedList(boolean result, String message, final ArrayList<StructContactInfo> list) {


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

                Realm realm = Realm.getDefaultInstance();
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
                            new RequestGroupAddMember().groupAddMemeber(roomId, peerId, 0, ProtoGlobal.GroupRoom.Role.MEMBER);
                        }

                        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("id", roomId).findFirst();

                        for (RealmMember member : realmRoom.getGroupRoom().getMembers()) {
                            members.add(member);
                        }

                        realmRoom.getGroupRoom().setMembers(members);
                    }
                });

                realm.close();

            }
        });

        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,
                R.anim.slide_in_right, R.anim.slide_out_left).addToBackStack(null).replace(R.id.fragmentContainer_group_profile, fragment).commit();


    }

    private void ChangeGroupName() {

        MaterialDialog dialog = new MaterialDialog.Builder(ActivityGroupProfile.this)
                .title("Group Name")
                .positiveText("SAVE")
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
                .negativeText("CANCEL")
                .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_TEXT)
                .input("please Enter Group Name", txtGroupName.getText().toString(), new MaterialDialog.InputCallback() {
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
                .title("Group Descripton")
                .positiveText("SAVE")
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
                .negativeText("CANCEL")
                .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_TEXT)
                .input("please Enter Group Description", txtGroupDescription.getText().toString(), new MaterialDialog.InputCallback() {
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
                .content("do you want to delete this group ")
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
                .content("do you want to set admin role to member")
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
                .content("do you want to kick this member ")
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
                .content("do you want to set modereator role to member")
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
            public void getSelectedList(boolean result, String message, final ArrayList<StructContactInfo> list) {

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

        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,
                R.anim.slide_in_right, R.anim.slide_out_left).addToBackStack(null).replace(R.id.fragmentContainer_group_profile, fragment).commit();


    }

    private void setMemberRoleToAdmin() {

        Fragment fragment = ShowCustomList.newInstance(contacts, new OnSelectedList() {
            @Override
            public void getSelectedList(boolean result, String message, ArrayList<StructContactInfo> list) {


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

        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,
                R.anim.slide_in_right, R.anim.slide_out_left).addToBackStack(null).replace(R.id.fragmentContainer_group_profile, fragment).commit();

    }


}
