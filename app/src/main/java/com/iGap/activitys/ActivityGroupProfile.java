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

import com.afollestad.materialdialogs.MaterialDialog;
import com.iGap.G;
import com.iGap.R;
import com.iGap.adapter.items.ContatItemGroupProfile;
import com.iGap.fragments.ContactGroupFragment;
import com.iGap.module.AttachFile;
import com.iGap.module.CircleImageView;
import com.iGap.module.Contacts;
import com.iGap.module.CustomTextViewMedium;
import com.iGap.module.StructContactInfo;
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

    private int numberOfloadItem = 5;

    List<StructContactInfo> contacts;
    List<IItem> items;
    ItemAdapter itemAdapter;
    RecyclerView recyclerView;
    private FastAdapter fastAdapter;

    AttachFile attachFile;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_profile);

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

                for (int i = count; i < listSize && i < count + numberOfloadItem; i++) {
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

                Fragment fragment = ContactGroupFragment.newInstance();
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,
                        R.anim.slide_in_right, R.anim.slide_out_left).addToBackStack(null).replace(R.id.fragmentContainer_group_profile, fragment).commit();
            }
        });


        initRecycleView();

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
        contacts = Contacts.retrieve(null);


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
