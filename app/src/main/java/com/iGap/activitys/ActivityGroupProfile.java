package com.iGap.activitys;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.afollestad.materialdialogs.MaterialDialog;
import com.iGap.G;
import com.iGap.R;
import com.iGap.adapter.items.ContatItemGroupProfile;
import com.iGap.fragments.ShowCustomList;
import com.iGap.interface_package.OnGroupAddMember;
import com.iGap.interface_package.OnSelectedList;
import com.iGap.module.AttachFile;
import com.iGap.module.CircleImageView;
import com.iGap.module.Contacts;
import com.iGap.module.CustomTextViewMedium;
import com.iGap.module.StructContactInfo;
import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.RealmContacts;
import com.iGap.realm.RealmGroupRoom;
import com.iGap.realm.RealmMember;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.enums.GroupChatRole;
import com.iGap.request.RequestGroupAddAdmin;
import com.iGap.request.RequestGroupAddMember;
import com.mikepenz.fastadapter.AbstractAdapter;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.IItemAdapter;
import com.mikepenz.fastadapter.adapters.HeaderAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;

/**
 * Created by android3 on 9/18/2016.
 */
public class ActivityGroupProfile extends ActivityEnhanced {


    private CircleImageView imvGroupAvatar;
    private TextView txtGroupNameTitle;
    private TextView txtGroupName;
    private TextView txtNumberOfSharedMedia;
    private TextView txtMemberNumber;
    private TextView txtMore;
    private AppBarLayout appBarLayout;
    private FloatingActionButton fab;

    private int numberUploadItem = 5;

    List<StructContactInfo> contacts;
    List<IItem> items;
    ItemAdapter itemAdapter;
    RecyclerView recyclerView;
    private FastAdapter fastAdapter;

    AttachFile attachFile;

    private long roomId;
    private String title;
    private String initials;
    private String color;
    private GroupChatRole role;
    private String participantsCountLabel;
    private RealmList<RealmMember> members;

    private int countAddMemberResponse = 0;
    private int countAddMemberRequest = 0;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
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

        realm.close();

        initComponent();
        attachFile = new AttachFile(this);
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


        imvGroupAvatar = (CircleImageView) findViewById(R.id.agp_imv_group_avatar);
        txtGroupNameTitle = (TextView) findViewById(R.id.agp_txt_group_name_title);
        txtGroupName = (TextView) findViewById(R.id.agp_txt_group_name);
        txtNumberOfSharedMedia = (TextView) findViewById(R.id.agp_txt_number_of_shared_media);
        txtMemberNumber = (TextView) findViewById(R.id.agp_txt_number_of_shared_media);
        appBarLayout = (AppBarLayout) findViewById(R.id.agp_appbar);

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
                    startDialog(R.array.profile);
                } else {
                    startDialog(R.array.profile_delete);
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

                Fragment fragment = ShowCustomList.newInstance(Contacts.retrieve(null), new OnSelectedList() {
                    @Override
                    public void getSelectedList(boolean result, String message, ArrayList<StructContactInfo> list) {

                        addMemberToGroup(list);

                        // reset activity
                        finish();
                        startActivity(getIntent());
                    }
                });

                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,
                        R.anim.slide_in_right, R.anim.slide_out_left).addToBackStack(null).replace(R.id.fragmentContainer_group_profile, fragment).commit();
            }
        });


        TextView txtSetAdmin = (TextView) findViewById(R.id.agp_txt_set_admin);
        txtSetAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = ShowCustomList.newInstance(contacts, new OnSelectedList() {
                    @Override
                    public void getSelectedList(boolean result, String message, ArrayList<StructContactInfo> list) {

                        for (int i = 0; i < list.size(); i++) {
                            new RequestGroupAddAdmin().groupAddAdmin(roomId, list.get(i).peerId);


                            Log.e("ddd", list.get(i).peerId + "");
                        }


                    }
                });

                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,
                        R.anim.slide_in_right, R.anim.slide_out_left).addToBackStack(null).replace(R.id.fragmentContainer_group_profile, fragment).commit();
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
                Log.e("ddd", "txtDeleteGroup");
            }
        });

        imvGroupAvatar.setImageBitmap(com.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) imvGroupAvatar.getContext().getResources().getDimension(R.dimen.dp60), initials, color));
        txtMemberNumber.setText(participantsCountLabel);

        if (role == GroupChatRole.MEMBER) {


        }

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


    private void addMemberToGroup(final ArrayList<StructContactInfo> list) {


        countAddMemberResponse = 0;
        countAddMemberRequest = list.size();


        G.onGroupAddMember = new OnGroupAddMember() {
            @Override
            public void onGroupAddMember() {
                countAddMemberResponse++;
                Log.i("XXX", "countAddMemberResponse : " + countAddMemberResponse);
                Log.i("XXX", "countAddMemberRequest : " + countAddMemberRequest);
                if (countAddMemberResponse >= countAddMemberRequest) {

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

    private void fillItem() {

        contacts = new ArrayList<>();

        Realm realm = Realm.getDefaultInstance();

        for (RealmMember member : members) {
            String role = member.getRole();
            long id = member.getPeerId();

            RealmContacts rc = realm.where(RealmContacts.class).equalTo("id", id).findFirst();

            if (rc != null) {
                contacts.add(new StructContactInfo(rc.getId(), rc.getDisplay_name() + "  " + role, rc.getStatus(), false, false, rc.getPhone() + ""));
            }


        }

        realm.close();

    }


    //dialog for choose pic from gallery or camera
    private void startDialog(int r) {

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

    //=====================================================================================result from camera , gallery and crop
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case AttachFile.request_code_TAKE_PICTURE:
                    Log.e("ddd", AttachFile.imagePath + "     image path");
                    break;
                case AttachFile.request_code_media_from_gallary:
                    Log.e("ddd", AttachFile.getFilePathFromUri(data.getData()) + "    gallary file path");
                    break;

            }

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


}
