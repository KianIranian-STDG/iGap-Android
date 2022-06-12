/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.fragments;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.IItemAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.listeners.OnClickListener;
import com.pchmn.materialchips.ChipsInput;
import com.pchmn.materialchips.model.ChipInterface;

import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityMain;
import net.iGap.adapter.items.ContactItemGroup;
import net.iGap.helper.GoToChatActivity;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.messenger.theme.Theme;
import net.iGap.messenger.ui.components.ProgressDialog;
import net.iGap.module.ContactChip;
import net.iGap.module.Contacts;
import net.iGap.module.LoginActions;
import net.iGap.module.ScrollingLinearLayoutManager;
import net.iGap.module.scrollbar.FastScroller;
import net.iGap.module.structs.StructContactInfo;
import net.iGap.observers.interfaces.OnChannelAddMember;
import net.iGap.observers.interfaces.OnContactsGetList;
import net.iGap.observers.interfaces.OnGroupAddMember;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmRoom;
import net.iGap.request.RequestChannelAddMember;
import net.iGap.request.RequestGroupAddMember;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ContactGroupFragment extends BaseFragment implements OnContactsGetList, ToolbarListener {

    ItemAdapter itemAdapter;
    private FastAdapter fastAdapter;
    private ChipsInput chipsInput;
    private long roomId;
    private int countAddMemberResponse = 0;
    private int countMember = 0;
    private int countAddMemberRequest = 0;
    private String typeCreate;
    private List<ContactChip> mContactList = new ArrayList<>();
    private List<StructContactInfo> contacts = new ArrayList<>();
    public static List<StructContactInfo> selectedContacts = new ArrayList<>();
    private boolean isRemove = true;
    private ProgressDialog progressDialog;

    public static ContactGroupFragment newInstance() {
        selectedContacts.clear();
        return new ContactGroupFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return attachToSwipeBack(inflater.inflate(R.layout.fragment_contact_group, container, false));
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ConstraintLayout mainContainer = view.findViewById(R.id.mainContainer);
        mainContainer.setBackgroundColor(Theme.getColor(Theme.key_window_background));
        progressDialog = new ProgressDialog(getContext());

        TextView add_member = view.findViewById(R.id.fcg_lbl_add_member);
        add_member.setTextColor(Theme.getColor(Theme.key_title_text));

        // to disable swipe in channel creation mode
        if (typeCreate != null) {
            if (typeCreate.equals("CHANNEL"))
                getSwipeBackLayout().setEnableGesture(false);
        }


        selectedContacts.clear();
        G.onContactsGetList = this;

        hideProgressBar(); // some times touch screen remind lock so this method do unlock

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            roomId = bundle.getLong("RoomId");
            typeCreate = bundle.getString("TYPE");
        }

        HelperToolbar mHelperToolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLifecycleOwner(getViewLifecycleOwner())
                .setLeftIcon(R.string.icon_back)
                .setRightIcons(R.string.icon_sent)
                .setDefaultTitle(G.context.getResources().getString(R.string.new_group))
                .setListener(this)
                .setLogoShown(true);

        LinearLayout toolbarLayout = view.findViewById(R.id.fcg_layout_toolbar);
        toolbarLayout.addView(mHelperToolbar.getView());

        /**
         * for some problem in theme we created 2 layout and check theme then add at run time
         * library does not support change text color or background color at run time until 1.0.8
         */
        ViewGroup layoutChips = view.findViewById(R.id.fcg_layout_search);

        //todo:// use material chips
        if (Theme.isDark() || Theme.isNight()) {
            layoutChips.addView(getLayoutInflater().inflate(R.layout.item_chips_layout_dark, null));
        } else {
            layoutChips.addView(getLayoutInflater().inflate(R.layout.item_chips_layout, null));
        }

        if (typeCreate.equals("CHANNEL")) {
            mHelperToolbar.setDefaultTitle(G.context.getResources().getString(R.string.new_channel));
        }

        chipsInput = view.findViewById(R.id.chips_input);
        chipsInput.setBackgroundColor(Theme.getColor(Theme.key_window_background));
       /* PorterDuffColorFilter greyFilter = new PorterDuffColorFilter(Theme.getColor(Theme.key_window_background), PorterDuff.Mode.MULTIPLY);
        chipsInput.getBackground().setColorFilter(greyFilter);*/

        //get our recyclerView and do basic setup
        RecyclerView rv = view.findViewById(R.id.fcg_recycler_view_add_item_to_group);
        rv.setLayoutManager(new ScrollingLinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false, 1000));
        rv.setItemAnimator(new DefaultItemAnimator());
        itemAdapter = new ItemAdapter();
        fastAdapter = FastAdapter.with(itemAdapter);
        fastAdapter.withSelectable(true);
        fastAdapter.setHasStableIds(true);
        rv.setAdapter(fastAdapter);
        addItems();

        FastScroller fastScroller = view.findViewById(R.id.fast_scroller);
        fastScroller.setRecyclerView(rv);

        itemAdapter.getItemFilter().withFilterPredicate(new IItemAdapter.Predicate<ContactItemGroup>() {
            @Override
            public boolean filter(ContactItemGroup item, CharSequence constraint) {
                return !item.mContact.displayName.toLowerCase().startsWith(String.valueOf(constraint).toLowerCase());
            }
        });
        fastAdapter.withOnClickListener(new OnClickListener<ContactItemGroup>() {
            @Override
            public boolean onClick(View v, IAdapter adapter, ContactItemGroup item, int position) {

                if (item.mContact.isSelected) {
                    chipsInput.removeChipByLabel(item.mContact.displayName);
                    selectedContacts.remove(item.mContact);
                } else {

                    Uri uri = null;
                    if (item.mContact.avatar != null && item.mContact.avatar.getFile() != null && item.mContact.avatar.getFile().getLocalThumbnailPath() != null) {
                        uri = Uri.fromFile(new File(item.mContact.avatar.getFile().getLocalThumbnailPath()));
                    }
                    if (uri == null) {

                        Drawable d = new BitmapDrawable(getResources(), net.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) G.context.getResources().getDimension(R.dimen.dp60), item.mContact.initials, item.mContact.color));
                        chipsInput.addChip(item.mContact.peerId, d, item.mContact.displayName, "");
                    } else {
                        chipsInput.addChip(item.mContact.peerId, uri, item.mContact.displayName, "");
                    }
                }


                if (isRemove) {
                    notifyAdapter(item, position);
                }


                return false;
            }
        });

        chipsInput.addChipsListener(new ChipsInput.ChipsListener() {
            @Override
            public void onChipAdded(ChipInterface chip, int newSize) {
                // chip added
                // newSize is the size of the updated selected chip list

                try {

                    if (chip != null) {
                        ContactItemGroup contactInfo = ((ContactItemGroup) fastAdapter.getItem(fastAdapter.getPosition((Long) chip.getId())));
                        selectedContacts.add(contactInfo.mContact);
                        notifyAdapter(contactInfo, fastAdapter.getPosition((Long) chip.getId()));
                        isRemove = false;
                    }

                } catch (Exception e) {

                }
            }

            @Override
            public void onChipRemoved(ChipInterface chip, int newSize) {
                if (chip.getId() != null) {
                    ContactItemGroup contactInfo = ((ContactItemGroup) fastAdapter.getItem(fastAdapter.getPosition((Long) chip.getId())));
                    notifyAdapter(contactInfo, fastAdapter.getPosition((Long) chip.getId()));
                    isRemove = false;
                    selectedContacts.remove(contactInfo.mContact);
                }
            }

            @Override
            public void onTextChanged(CharSequence text) {
                // text changed
            }
        });

        initContactRemoveListener();
        //restore selections (this has to be done after the items were added
        fastAdapter.withSavedInstanceState(savedInstanceState);
    }

    private void initContactRemoveListener() {

        FragmentNewGroup.removeSelectedContact = new FragmentNewGroup.RemoveSelectedContact() {
            @Override
            public void onRemoved(StructContactInfo item) {

                try {
                    chipsInput.removeChipByLabel(item.displayName);
                    selectedContacts.remove(item);
                } catch (Exception e) {

                }

            }
        };
    }

    @Override
    public void onStop() {
        super.onStop();
        G.onContactsGetList = null;
    }

    private void addItems() {
        List<IItem> items = new ArrayList<>();
        contacts = Contacts.retrieve(null);
        if (contacts.size() == 0) {
            LoginActions.importContact();
        } else {
            for (StructContactInfo contact : contacts) {
                if (contact != null) {
                    items.add(new ContactItemGroup(avatarHandler).setContact(contact).withIdentifier(contact.peerId));

                    Uri uri = null;
                    if (contact.avatar != null && contact.avatar.getFile() != null && contact.avatar.getFile().getLocalThumbnailPath() != null) {
                        uri = Uri.fromFile(new File(contact.avatar.getFile().getLocalThumbnailPath()));
                    }
                    ContactChip contactChip;
                    if (uri == null) {
                        Drawable d = new BitmapDrawable(getResources(), net.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) G.context.getResources().getDimension(R.dimen.dp60), contact.initials, contact.color));
                        contactChip = new ContactChip(contact.peerId, d, contact.displayName);
                    } else {
                        contactChip = new ContactChip(contact.peerId, uri, contact.displayName);
                    }

                    mContactList.add(contactChip);
                }
            }
            chipsInput.setFilterableList(mContactList);
            itemAdapter.add(items);
        }
    }

    private void addMember(long roomId, ProtoGlobal.Room.Type roomType) {
        RealmRoom.addOwnerToDatabase(roomId);
        RealmRoom.updateMemberCount(roomId, roomType, countMember + 1); // plus with 1 , for own account
        if (getActivity() instanceof ActivityMain && isAdded()) {
            ((ActivityMain) getActivity()).removeAllFragmentFromMain();
            new GoToChatActivity(roomId).startActivity(getActivity());
        }
    }

    private void notifyAdapter(ContactItemGroup item, int position) {
        item.mContact.isSelected = !item.mContact.isSelected;
        fastAdapter.notifyItemChanged(position);
        G.handler.postDelayed(() -> isRemove = true, 50);

    }

    private ArrayList<Long> getSelectedList() {
        ArrayList<Long> list = new ArrayList<>();

        for (int i = 0; i < contacts.size(); i++) {
            if (contacts.get(i).isSelected) {
                countAddMemberRequest++;
                list.add(contacts.get(i).peerId);
            }
        }

        return list;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the adapter to the bundle
        outState = fastAdapter.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    private void showProgressBar() {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                if (G.currentActivity != null) {
                    G.currentActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
            }
        });
    }

    private void hideProgressBar() {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                if (G.currentActivity != null) {
                    G.currentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
            }
        });
    }

    @Override
    public void onContactsGetList() {
        addItems();
    }

    @Override
    public void onContactsGetListTimeOut() {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                if (getContext() != null) {
                    HelperError.showSnackMessage(getString(R.string.connection_error), false);
                }
            }
        });
    }

    @Override
    public void onLeftIconClickListener(View view) {
        G.currentActivity.onBackPressed();
    }

    @Override
    public boolean onBackPressed() {
        if (typeCreate.equals("CHANNEL")) {
            ((ActivityMain) getActivity()).removeAllFragmentFromMain();
            return true;
        } else
            return false;
    }

    @Override
    public void onRightIconClickListener(View view) {
        fixChipsLayoutShowingState();

        if (typeCreate.equals("CHANNEL")) {
            G.onChannelAddMember = new OnChannelAddMember() {
                @Override
                public void onChannelAddMember(Long RoomId, Long UserId, ProtoGlobal.ChannelRoom.Role role) {
                    G.runOnUiThread(() -> {
                        progressDialog.dismiss();
                        countAddMemberResponse++;
                        addMember(RoomId, ProtoGlobal.Room.Type.CHANNEL);
                        countMember++;
                        if (countAddMemberResponse == countAddMemberRequest) {
                            addMember(RoomId, ProtoGlobal.Room.Type.CHANNEL);
                        }
                    });
                }

                @Override
                public void onError(int majorCode, int minorCode) {
                    G.runOnUiThread(() -> {
                        progressDialog.dismiss();
                        countAddMemberResponse++;
                        if (countAddMemberResponse == countAddMemberRequest) {
                            addMember(roomId, ProtoGlobal.Room.Type.CHANNEL);
                        }
                        HelperError.showSnackMessage(requireContext().getResources().getString(R.string.error), false);
                    });
                }

                @Override
                public void onTimeOut() {
                    G.runOnUiThread(() -> {
                        progressDialog.dismiss();
                        HelperError.showSnackMessage(requireContext().getResources().getString(R.string.time_out), false);
                    });
                }
            };

            ArrayList<Long> list = getSelectedList();
            if (list.size() > 0) {
                for (long peerId : list) {
                    new RequestChannelAddMember().channelAddMember(roomId, peerId);
                    progressDialog.show();
                    progressDialog.setCancelable(false);
                }
            } else {
                if (isAdded()) {
                    if (getActivity() instanceof ActivityMain) {
                        ((ActivityMain) getActivity()).removeAllFragmentFromMain();
                        new GoToChatActivity(ContactGroupFragment.this.roomId).startActivity(getActivity());
                    }
                }
            }

        } else if (typeCreate.equals("GROUP")) {

            if (roomId == -127) {
                if (getActivity() != null) {
                    FragmentNewGroup fragment = FragmentNewGroup.newInstance();
                    Bundle bundle_ = new Bundle();
                    bundle_.putString("TYPE", "NewGroup");
                    fragment.setArguments(bundle_);
                    new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setReplace(false).load(true);
                    return;
                }
            }

            G.onGroupAddMember = new OnGroupAddMember() {
                @Override
                public void onGroupAddMember(Long roomId, Long UserId) {
                    countAddMemberResponse++;
                    countMember++;
                    if (countAddMemberResponse == countAddMemberRequest) {
                        addMember(roomId, ProtoGlobal.Room.Type.GROUP);
                    }
                }

                @Override
                public void onError(int majorCode, int minorCode) {
                    countAddMemberResponse++;
                    if (countAddMemberResponse == countAddMemberRequest) {
                        addMember(roomId, ProtoGlobal.Room.Type.GROUP);
                    }
                }
            };


            /**
             * request add member for group
             *
             */
            ArrayList<Long> list = getSelectedList();
            if (list.size() > 0) {
                for (long peerId : list) {
                    new RequestGroupAddMember().groupAddMember(roomId, peerId, 0);
                }
            } else {

                if (getActivity() != null && isAdded()) {
                    removeFromBaseFragment(ContactGroupFragment.this);
                    new GoToChatActivity(ContactGroupFragment.this.roomId).startActivity(getActivity());
                }

            }
        }

    }

    private void setupSelectedList() {
        for (int i = 0; i < contacts.size(); i++) {
            if (contacts.get(i).isSelected) {

                selectedContacts.add(contacts.get(i));
            }
        }
    }

    @Override
    public void onPause() {

        fixChipsLayoutShowingState();
        super.onPause();
    }

    private void fixChipsLayoutShowingState() {

        //this code added for close chips layout
        if (chipsInput != null && chipsInput.getSelectedChipList().size() > 0) {
            try {
                chipsInput.addChip("", "");
                chipsInput.removeChipByLabel("");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
