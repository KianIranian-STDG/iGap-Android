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

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
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
import net.iGap.adapter.items.ContactItemGroup;
import net.iGap.helper.HelperToolbar;
import net.iGap.messenger.theme.Theme;
import net.iGap.module.ContactChip;
import net.iGap.module.ScrollingLinearLayoutManager;
import net.iGap.module.scrollbar.FastScroller;
import net.iGap.module.structs.StructContactInfo;
import net.iGap.observers.interfaces.OnSelectedList;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.proto.ProtoGlobal;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ShowCustomList extends BaseFragment implements ToolbarListener {
    private List<StructContactInfo> contacts;
    private OnSelectedList onSelectedList;
    private FastAdapter fastAdapter;
    private boolean dialogShowing = false;
    private long lastId = 0;
    private int count = 0;
    private boolean singleSelect = false;
    // private RippleView rippleDown;
    private List<ContactChip> mContactList = new ArrayList<>();
    private ChipsInput chipsInput;
    private boolean isRemove = true;
    private HelperToolbar mHelperToolbar;
    private static ProtoGlobal.Room.Type mRoomType;

    public void setFields(ProtoGlobal.Room.Type roomType, List<StructContactInfo> list, OnSelectedList onSelectedListResult){
        onSelectedList = onSelectedListResult;
        contacts = list;
        mRoomType = roomType;

        for (int i = 0; i < contacts.size(); i++) {
            contacts.get(i).isSelected = false;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return attachToSwipeBack(inflater.inflate(R.layout.fragment_contact_group, container, false));
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ConstraintLayout mainContainer= view.findViewById(R.id.mainContainer);
        mainContainer.setBackgroundColor(Theme.getColor(Theme.key_window_background));
        LinearLayout fcg_layout_toolbar= view.findViewById(R.id.fcg_layout_toolbar);
        fcg_layout_toolbar.setBackgroundColor(Theme.getColor(Theme.key_window_background));
        LinearLayout fcg_layout_search= view.findViewById(R.id.fcg_layout_search);
        fcg_layout_search.setBackgroundColor(Theme.getColor(Theme.key_window_background));

        TextView add_member = view.findViewById(R.id.fcg_lbl_add_member);
        add_member.setTextColor(Theme.getColor(Theme.key_title_text));

        View fcg_splitter_add_member =  view.findViewById(R.id.fcg_splitter_add_member);
        fcg_splitter_add_member.setBackgroundColor(Theme.getColor(Theme.key_line));


        Bundle bundle = this.getArguments();
        if (bundle != null) {
            dialogShowing = bundle.getBoolean("DIALOG_SHOWING");
            if (bundle.getLong("COUNT_MESSAGE") != 0) {
                lastId = bundle.getLong("COUNT_MESSAGE");
            }

            singleSelect = bundle.getBoolean("SINGLE_SELECT");
        }

        mHelperToolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLifecycleOwner(getViewLifecycleOwner())
                .setLeftIcon(R.string.icon_back)
                .setRightIcons(R.string.icon_sent)
                .setDefaultTitle(getString(R.string.add_new_member))
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

        chipsInput = view.findViewById(R.id.chips_input);
        chipsInput.setBackgroundColor(Theme.getColor(Theme.key_window_background));
        final ItemAdapter itemAdapter = new ItemAdapter();
        fastAdapter = FastAdapter.with(itemAdapter);
        fastAdapter.withSelectable(true);
        fastAdapter.setHasStableIds(true);

        //get our recyclerView and do basic setup
        RecyclerView rv = view.findViewById(R.id.fcg_recycler_view_add_item_to_group);
        rv.setLayoutManager(new ScrollingLinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false, 1000));
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setAdapter(fastAdapter);

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

        List<IItem> items = new ArrayList<>();

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

        chipsInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        chipsInput.setFilterableList(mContactList);

        itemAdapter.add(items);

        chipsInput.addChipsListener(new ChipsInput.ChipsListener() {
            @Override
            public void onChipAdded(ChipInterface chip, int newSize) {
                // chip added
                // newSize is the size of the updated selected chip list

                try {

                    if (chip != null) {
                        notifyAdapter(((ContactItemGroup) fastAdapter.getItem(fastAdapter.getPosition((Long) chip.getId()))), fastAdapter.getPosition((Long) chip.getId()));
                        isRemove = false;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onChipRemoved(ChipInterface chip, int newSize) {
                notifyAdapter(((ContactItemGroup) fastAdapter.getItem(fastAdapter.getPosition((Long) chip.getId()))), fastAdapter.getPosition((Long) chip.getId()));
                isRemove = false;
            }

            @Override
            public void onTextChanged(CharSequence text) {
                // text changed
            }
        });

        //restore selections (this has to be done after the items were added
        fastAdapter.withSavedInstanceState(savedInstanceState);

    }

    private void notifyAdapter(ContactItemGroup item, int position) {

        item.mContact.isSelected = !item.mContact.isSelected;
        fastAdapter.notifyItemChanged(position);
        if (singleSelect) {
            if (onSelectedList != null) {
                onSelectedList.getSelectedList(true, "", 0, getSelectedList());
            }
            // G.fragmentActivity.getSupportFragmentManager().popBackStack();

            popBackStackFragment();
        }
        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                isRemove = true;
            }
        }, 50);

    }

    @SuppressLint("ResourceAsColor")
    private void showDialog(ProtoGlobal.Room.Type roomType) {
        if (roomType == ProtoGlobal.Room.Type.GROUP) {

            new MaterialDialog.Builder(G.fragmentActivity)
                    .backgroundColor(Theme.getColor(Theme.key_popup_background))
                    .title(R.string.show_message_count)
                    .items(R.array.numberCountGroup)
                    .negativeColor(Theme.getColor(Theme.key_button_background))
                    .positiveColor(Theme.getColor(Theme.key_button_background))
                    .choiceWidgetColor(ColorStateList.valueOf(Theme.getColor(Theme.key_button_background)))
                    .itemsCallback(new MaterialDialog.ListCallback() {
                @Override
                public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                    switch (which) {
                        case 0:
                            count = 0;
                            if (onSelectedList != null) {
                                onSelectedList.getSelectedList(true, "fromBegin", count, getSelectedList());
                            }
                            // G.fragmentActivity.getSupportFragmentManager().popBackStack();

                            popBackStackFragment();
                            break;
                        case 1:
                            count = 0;
                            if (onSelectedList != null) {

                                onSelectedList.getSelectedList(true, "fromNow", count, getSelectedList());
                            }
                            //  G.fragmentActivity.getSupportFragmentManager().popBackStack();

                            popBackStackFragment();

                            break;
                        case 2:
                            count = 50;
                            if (onSelectedList != null) {
                                onSelectedList.getSelectedList(true, "", count, getSelectedList());
                            }
                            // G.fragmentActivity.getSupportFragmentManager().popBackStack();

                            popBackStackFragment();

                            break;
                        case 3:
                            dialog.dismiss();
                            new MaterialDialog.Builder(G.fragmentActivity)
                                    .backgroundColor(Theme.getColor(Theme.key_popup_background))
                                    .title(R.string.customs).positiveText(G.fragmentActivity.getResources().getString(R.string.B_ok)).alwaysCallInputCallback().widgetColor(Theme.getColor(Theme.key_toolbar_background)).onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    if (onSelectedList != null) {
                                        onSelectedList.getSelectedList(true, "", count, getSelectedList());
                                    }
                                    //  G.fragmentActivity.getSupportFragmentManager().popBackStack();

                                    popBackStackFragment();

                                }
                            }).inputType(InputType.TYPE_CLASS_NUMBER).input(G.fragmentActivity.getResources().getString(R.string.count_of_show_message), null, new MaterialDialog.InputCallback() {
                                @Override
                                public void onInput(MaterialDialog dialog, CharSequence input) {
                                    if (input.toString() != null && !input.toString().isEmpty()) {
                                        if (input.length() < 5) {
                                            count = Integer.parseInt(input.toString());
                                        } else {
                                            count = 0;
                                        }

                                    } else {
                                        count = 0;
                                    }
                                }
                            }).show();
                            break;
                    }
                }
            }).show();

        } else if (roomType == ProtoGlobal.Room.Type.CHANNEL) {

            new MaterialDialog.Builder(G.fragmentActivity)
                    .backgroundColor(Theme.getColor(Theme.key_popup_background))
                    .items(G.context.getResources().getString(R.string.are_you_sure_add_follower))
                    .itemsColor(Theme.getColor(Theme.key_title_text))
                    .itemsGravity(GravityEnum.START)
                    .buttonsGravity(GravityEnum.CENTER)
                    .positiveText(R.string.yes)
                    .positiveColorAttr(R.attr.colorAccent)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            if (onSelectedList != null) {
                                onSelectedList.getSelectedList(true, "fromBegin", count, getSelectedList());
                            }
                            popBackStackFragment();
                        }
                    }).negativeText(R.string.no)
                    .negativeColor(Theme.getColor(Theme.key_subtitle_text))
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            popBackStackFragment();
                        }
                    }).show();
        }
    }

    private ArrayList<StructContactInfo> getSelectedList() {

        ArrayList<StructContactInfo> list = new ArrayList<>();

        for (int i = 0; i < contacts.size(); i++) {
            if (contacts.get(i).isSelected) {
                list.add(contacts.get(i));
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

    @Override
    public void onLeftIconClickListener(View view) {
        popBackStackFragment();
    }

    @Override
    public void onRightIconClickListener(View view) {

        fixChipsLayoutShowingState();
        if (dialogShowing) {
            showDialog(mRoomType);
        } else {
            if (onSelectedList != null) {
                onSelectedList.getSelectedList(true, "", 0, getSelectedList());
            }
            popBackStackFragment();
        }
    }

    @Override
    public void onPause() {

        fixChipsLayoutShowingState();
        super.onPause();
    }


    private void fixChipsLayoutShowingState() {

        //this code added for close chips layout
        if (chipsInput != null) {
            try {
                chipsInput.addChip("", "");
                chipsInput.removeChipByLabel("");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
