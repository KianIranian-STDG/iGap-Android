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

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.collection.ArrayMap;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperPermission;
import net.iGap.helper.HelperPublicMethod;
import net.iGap.helper.HelperToolbar;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.helper.avatar.ParamWithAvatarType;
import net.iGap.libs.emojiKeyboard.emoji.EmojiManager;
import net.iGap.module.AppUtils;
import net.iGap.module.CircleImageView;
import net.iGap.module.ContactUtils;
import net.iGap.module.Contacts;
import net.iGap.module.LastSeenTimeUtil;
import net.iGap.module.LoginActions;
import net.iGap.module.MaterialDesignTextView;
import net.iGap.module.ScrollingLinearLayoutManager;
import net.iGap.module.StatusBarUtil;
import net.iGap.module.Theme;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.accountManager.DbManager;
import net.iGap.module.dialog.bottomsheet.BottomSheetFragment;
import net.iGap.module.scrollbar.FastScroller;
import net.iGap.module.scrollbar.FastScrollerBarBaseAdapter;
import net.iGap.observers.interfaces.OnContactImport;
import net.iGap.observers.interfaces.OnContactsGetList;
import net.iGap.observers.interfaces.OnGetPermission;
import net.iGap.observers.interfaces.OnUserContactDelete;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoSignalingOffer;
import net.iGap.realm.RealmContacts;
import net.iGap.request.RequestUserContactsDelete;
import net.iGap.request.RequestUserContactsGetList;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.realm.Case;

import static net.iGap.helper.ContactManager.CONTACT_LIMIT;

public class RegisteredContactsFragment extends BaseMainFragments implements ToolbarListener, OnContactImport, OnUserContactDelete, OnContactsGetList {

    public static final int NEW_CHAT = 0;
    public static final int CONTACTS = 1;
    public static final int CALL = 2;
    public static final int ADD = 3;
    private static final String TAG = "aabolfazlContact";
    private static boolean getPermission = true;

    public onClickRecyclerView onClickRecyclerView;
    public onLongClickRecyclerView onLongClickRecyclerView;

    protected ArrayMap<Long, Boolean> selectedList = new ArrayMap<>();
    private List<RealmContacts> results;

    private View btnAddNewChannel;
    private View btnAddNewGroup;
    private View btnAddSecretChat;
    private View btnAddNewGroupCall;
    private View btnAddNewContact;
    private View btnDialNumber;
    private RecyclerView realmRecyclerView;
    private Group vgInviteFriend;
    private FastScroller fastScroller;
    private HelperToolbar mHelperToolbar;
    private ProgressBar prgWaitingLoadList, prgMainLoader;
    private ViewGroup mLayoutMultiSelected;
    private TextView mTxtSelectedCount;
    private ActionMode mActionMode;

    private int mPageMode = NEW_CHAT;
    private boolean isCallAction = false;
    private boolean isContact;
    private boolean isSwipe = false;
    private boolean isMultiSelect = false;
    private boolean isLongClick = false;
    private boolean endPage = false;
    private boolean inSearchMode = false;
    private String searchText = "";
    private boolean isSearchEnabled;
    private int tryRequest;

    public static RegisteredContactsFragment newInstance(boolean isSwipe, boolean isCallAction, int pageMode) {
        RegisteredContactsFragment contactsFragment = new RegisteredContactsFragment();
        contactsFragment.isSwipe = isSwipe;
        contactsFragment.isCallAction = isCallAction;
        contactsFragment.mPageMode = pageMode;
        contactsFragment.isContact = !isSwipe;
        return contactsFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (isSwipe) {
            return attachToSwipeBack(inflater.inflate(R.layout.fragment_contacts, container, false));
        } else {
            return inflater.inflate(R.layout.fragment_contacts, container, false);
        }
    }

    @Override
    public void onViewCreated(@NotNull View view, final @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getContext() != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            StatusBarUtil.setColor(getActivity(), new Theme().getPrimaryDarkColor(getContext()), 50);
        }

        G.onContactImport = this;
        G.onUserContactdelete = this;
        G.onContactsGetList = this;

        tryRequest = 0;

        realmRecyclerView = view.findViewById(R.id.recycler_view);

        LinearLayout toolbarLayout = view.findViewById(R.id.frg_contact_ll_toolbar_layout);

        if (isContact) {
            mHelperToolbar = HelperToolbar.create()
                    .setContext(getContext())
                    .setLogoShown(true)
                    .setLifecycleOwner(getViewLifecycleOwner())
                    .setLeftIcon(R.string.edit_icon)
                    .setRightIcons(R.string.add_icon_without_circle_font)
                    .setFragmentActivity(getActivity())
                    .setPassCodeVisibility(true, R.string.unlock_icon)
                    .setScannerVisibility(true, R.string.scan_qr_code_icon)
                    .setSearchBoxShown(true);
        } else {
            mHelperToolbar = HelperToolbar.create()
                    .setContext(getContext())
                    .setLifecycleOwner(getViewLifecycleOwner())
                    .setLogoShown(true)
                    .setLeftIcon(R.string.back_icon)
                    .setRightIcons(R.string.add_icon_without_circle_font)
                    .setSearchBoxShown(true);
        }

        if (mPageMode == CALL) {
            mHelperToolbar.setDefaultTitle(getString(R.string.make_call));
        } else if (mPageMode == ADD) {
            mHelperToolbar.setDefaultTitle(getString(R.string.create_chat));
        }

        toolbarLayout.addView(mHelperToolbar.getView());
        mHelperToolbar.setListener(this);

        if (isContact) {
            Contacts.localPhoneContactId = 0;
            Contacts.getContact = true;
        }


        prgMainLoader = view.findViewById(R.id.fc_loader_main);

        vgInviteFriend = view.findViewById(R.id.menu_layout_inviteFriend);

        btnAddNewGroupCall = view.findViewById(R.id.menu_layout_new_group_call);
        btnAddNewContact = view.findViewById(R.id.menu_layout_add_new_contact);
        btnDialNumber = view.findViewById(R.id.menu_layout_btn_dial_number);

        btnAddSecretChat = view.findViewById(R.id.menu_layout_btn_secret_chat);
        btnAddNewGroup = view.findViewById(R.id.menu_layout_add_new_group);
        btnAddNewChannel = view.findViewById(R.id.menu_layout_add_new_channel);
        fastScroller = view.findViewById(R.id.fs_contact_fastScroller);

        mLayoutMultiSelected = view.findViewById(R.id.fc_layout_selected_mode);
        mTxtSelectedCount = view.findViewById(R.id.fc_selected_mode_txt_counter);
        AppCompatTextView mBtnDeleteSelected = view.findViewById(R.id.fc_selected_mode_btn_delete);
        MaterialDesignTextView mBtnCancelSelected = view.findViewById(R.id.fc_selected_mode_btn_cancel);
        mTxtSelectedCount.setText(0 + " " + getString(R.string.item_selected));


        prgWaitingLoadList = view.findViewById(R.id.prgWaiting_loadList);

        realmRecyclerView.setAdapter(new ContactListAdapter());

        if (!inSearchMode)
            loadContacts();

        switch (mPageMode) {
            case CALL:
                btnAddNewContact.setVisibility(View.VISIBLE);
                btnDialNumber.setVisibility(View.GONE);
                vgInviteFriend.setVisibility(View.GONE);
                mHelperToolbar.getRightButton().setVisibility(View.GONE);
                break;
            case ADD:
                btnAddNewChannel.setVisibility(View.VISIBLE);
                btnAddNewGroup.setVisibility(View.VISIBLE);
                vgInviteFriend.setVisibility(View.GONE);
                break;
            case CONTACTS:

        }

        vgInviteFriend.setOnClickListener(v -> {
            try {
                HelperPermission.getContactPermision(getContext(), new OnGetPermission() {
                    @Override
                    public void Allow() {
                        new HelperFragment(getActivity().getSupportFragmentManager(), new LocalContactFragment()).setReplace(false).load();
                    }

                    @Override
                    public void deny() {

                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }


        });

        realmRecyclerView = view.findViewById(R.id.recycler_view);
        realmRecyclerView.setLayoutManager(new ScrollingLinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false, 1000));
        realmRecyclerView.setNestedScrollingEnabled(false);
        fastScroller.setRecyclerView(realmRecyclerView);


        onClickRecyclerView = (v, position) -> {
            if (isMultiSelect) {
                multi_select(position);
            }
        };

        onLongClickRecyclerView = (v, position) -> {
            if (!isMultiSelect) {
                isMultiSelect = true;
                refreshAdapter(0, true);

                if (!mLayoutMultiSelected.isShown()) {
                    setPageShowingMode(4);
                }
            }
            multi_select(position);
        };

        /*fastItemAdapter = new FastItemAdapter();*/

        try {
            if (getPermission && isContact) {
                getPermission = false;
                HelperPermission.getContactPermision(getContext(), new OnGetPermission() {
                    @Override
                    public void Allow() throws IOException {
                        /**
                         * if contacts size is zero send request for get contacts list
                         * for insuring that contacts not exist really or not
                         */
                        LoginActions.importContact();
                        prgMainLoader.setVisibility(View.GONE);
                    }

                    @Override
                    public void deny() {
                        if (results.size() == 0) {
                            new RequestUserContactsGetList().userContactGetList();
                        }
                        prgMainLoader.setVisibility(View.GONE);
                    }
                });
            } else {
                LoginActions.importContact();
                prgMainLoader.setVisibility(View.GONE);
            }
        } catch (IOException e) {
            e.printStackTrace();
            prgMainLoader.setVisibility(View.GONE);
        }

        btnAddNewChannel.setOnClickListener(v -> {
            if (getActivity() != null) {
                FragmentNewGroup fragment = FragmentNewGroup.newInstance();
                Bundle bundle_ = new Bundle();
                bundle_.putString("TYPE", "NewChanel");
                fragment.setArguments(bundle_);
                new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setReplace(false).load();
            }
        });

        btnAddNewGroup.setOnClickListener(v -> {
            if (getActivity() != null) {
                Fragment fragment = ContactGroupFragment.newInstance();
                Bundle bundle1 = new Bundle();
                bundle1.putLong("RoomId", -127);
                bundle1.putString("LIMIT", "5000");
                bundle1.putString("TYPE", ProtoGlobal.Room.Type.GROUP.name());
                bundle1.putBoolean("NewRoom", true);
                fragment.setArguments(bundle1);
                if (FragmentNewGroup.onRemoveFragmentNewGroup != null) {
                    Log.wtf(this.getClass().getName(), "onRemoveFragmentNewGroup");
                    FragmentNewGroup.onRemoveFragmentNewGroup.onRemove();
                }
                new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setReplace(false).load();
            }
        });

        btnAddSecretChat.setOnClickListener(v -> {

        });

        btnDialNumber.setOnClickListener(v -> {
            if (getActivity() != null) {
                new HelperFragment(getActivity().getSupportFragmentManager(), new DailNumberFragment()).setReplace(false).load();
            }
        });

        btnAddNewContact.setOnClickListener(this::onRightIconClickListener);

        mBtnDeleteSelected.setOnClickListener(v -> {

            if (selectedList.size() == 0) {
                Toast.makeText(getContext(), getString(R.string.no_item_selected), Toast.LENGTH_SHORT).show();
                return;
            }

            new MaterialDialog.Builder(G.fragmentActivity).title(R.string.to_delete_contact).content(R.string.delete_text).positiveText(R.string.B_ok).onPositive(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                    for (ArrayMap.Entry<Long, Boolean> entry : selectedList.entrySet()) {
                        new RequestUserContactsDelete().contactsDelete("" + entry.getKey());
                    }
                    setMultiSelectState(true);
                }
            }).negativeText(R.string.B_cancel).show();

        });


        mBtnCancelSelected.setOnClickListener(v -> {
            setMultiSelectState(isMultiSelect);
        });

        //todo: fixed it ,effect in load time
        if (isMultiSelect) {
            refreshAdapter(0, true);
            if (!mLayoutMultiSelected.isShown()) {
                Log.wtf(this.getClass().getName(), "setPageShowingMode 4");
                setPageShowingMode(4);
            }
            isLongClick = true;
        }
    }

    private void setPageShowingMode(int mode) {
        if (mode == 0 || mode == 1) { //contact mode
            btnAddNewGroupCall.setVisibility(View.GONE);
            btnAddNewContact.setVisibility(View.GONE);
            btnDialNumber.setVisibility(View.GONE);
            btnAddNewChannel.setVisibility(View.GONE);
            btnAddNewGroup.setVisibility(View.GONE);
            btnAddSecretChat.setVisibility(View.GONE);
            mLayoutMultiSelected.setVisibility(View.GONE);
            vgInviteFriend.setVisibility(View.VISIBLE);
        } else if (mode == 2) { // call mode

            btnAddNewGroupCall.setVisibility(View.VISIBLE);
            btnAddNewContact.setVisibility(View.VISIBLE);
            btnDialNumber.setVisibility(View.VISIBLE);
            vgInviteFriend.setVisibility(View.GONE);
            btnAddNewChannel.setVisibility(View.GONE);
            btnAddNewGroup.setVisibility(View.GONE);
            btnAddSecretChat.setVisibility(View.GONE);
            mLayoutMultiSelected.setVisibility(View.GONE);

        } else if (mode == 3) { // add mode

            btnAddNewChannel.setVisibility(View.VISIBLE);
            btnAddNewGroup.setVisibility(View.VISIBLE);
            btnAddSecretChat.setVisibility(View.VISIBLE);
            vgInviteFriend.setVisibility(View.GONE);
            btnAddNewGroupCall.setVisibility(View.GONE);
            btnAddNewContact.setVisibility(View.GONE);
            btnDialNumber.setVisibility(View.GONE);
            mLayoutMultiSelected.setVisibility(View.GONE);

        } else if (mode == 4) {//edit mode
            btnAddNewChannel.setVisibility(View.GONE);
            btnAddNewGroup.setVisibility(View.GONE);
            btnAddSecretChat.setVisibility(View.GONE);
            vgInviteFriend.setVisibility(View.GONE);
            btnAddNewGroupCall.setVisibility(View.GONE);
            btnAddNewContact.setVisibility(View.GONE);
            btnDialNumber.setVisibility(View.GONE);
            mLayoutMultiSelected.setVisibility(View.VISIBLE);
        }
    }

    private void hideProgress() {
        G.handler.post(() -> {
            if (getActivity() != null) {
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        });
    }

    private void showProgress() {
        G.handler.post(() -> {
            if (getActivity() != null) {
                getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Contacts.getContact = false;
        hideProgress();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (isSearchEnabled && mHelperToolbar != null)
            mHelperToolbar.getmSearchBox().performClick();
        if (isContact && mHelperToolbar != null) mHelperToolbar.checkPassCodeVisibility();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

    }

    @Override
    public void onContactInfo(ProtoGlobal.RegisteredUser user) {
        loadContacts();
    }

    @Override
    public void onTimeOut() {

    }

    @Override
    public void onContactDelete() {
        if (inSearchMode) {
            G.handler.postDelayed(() -> loadContact(searchText), 200);
        } else
            G.handler.postDelayed(this::loadContacts, 200);
    }

    @Override
    public void onError(int majorCode, int minorCode) {

    }

    public void multi_select(int position) {
        if (mLayoutMultiSelected.isShown()) {

            if (results.get(position) == null) {
                return;
            }

            if (selectedList.containsKey(results.get(position).getPhone())) {
                selectedList.remove(results.get(position).getPhone());
            } else {
                selectedList.put(results.get(position).getPhone(), true);
            }

            if (selectedList.size() > 0) {
                mTxtSelectedCount.setText(selectedList.size() + " " + getString(R.string.item_selected));
            } else {
                mTxtSelectedCount.setText(selectedList.size() + " " + getString(R.string.item_selected));
            }
            refreshAdapter(position, false);
        }
    }

    public void refreshAdapter(int position, boolean isAllRefresh) {
        if (realmRecyclerView.getAdapter() != null) {
            if (isAllRefresh) {
                realmRecyclerView.getAdapter().notifyDataSetChanged();
            } else {
                realmRecyclerView.getAdapter().notifyItemChanged(position);
            }
        }
    }

    @Override //btn edit or back
    public void onLeftIconClickListener(View view) {
        if (!isContact)
            popBackStackFragment();
        else
            showDialog();
    }

    // Add/Remove the item from/to the list

    private void showDialog() {
        if (getFragmentManager() != null) {
            List<String> items = new ArrayList<>();
            items.add(getString(R.string.sync_contact));
            items.add(getString(R.string.mark_as_several));

            new BottomSheetFragment().setData(items, -1, position -> {
                if (position == 0) {
                    if (isMultiSelect) setMultiSelectState(true);
                    ContactUtils.syncContacts();
                } else {
                    if (!isMultiSelect) setMultiSelectState(false);
                }
            }).show(getFragmentManager(), "contactToolbar");
        }
    }

    private void showDialogContactLongClicked(int itemPosition, long id, long phone, String name, String family) {
        if (getFragmentManager() != null) {
            List<String> items = new ArrayList<>();
            items.add(getString(R.string.edit));
            items.add(getString(R.string.delete));
            items.add(getString(R.string.mark_as_several));

            new BottomSheetFragment().setData(items, -1, position -> {
                if (position == 0) {
                    FragmentAddContact fragment = FragmentAddContact.newInstance(
                            id, "+" + phone, name, family, FragmentAddContact.ContactMode.EDIT, (name1, family1) -> loadContacts()
                    );
                    if (getActivity() != null)
                        new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setReplace(false).load();
                } else if (position == 1) {
                    new RequestUserContactsDelete().contactsDelete("" + phone);
                } else if (position == 2) {
                    setMultiSelectState(isMultiSelect);
                    multi_select(itemPosition);
                }
            }).show(getFragmentManager(), "contactLongClicked");
        }
    }

    private void setMultiSelectState(boolean state) {

        if (state) {
            setPageShowingMode(mPageMode);
            mActionMode = null;
            isMultiSelect = false;
            isLongClick = false;
            selectedList.clear();
            mTxtSelectedCount.setText("0 " + getString(R.string.item_selected));
            refreshAdapter(0, true);
        } else {
            isMultiSelect = true;
            refreshAdapter(0, true);

            if (!mLayoutMultiSelected.isShown()) {
                setPageShowingMode(4);
            }
            isLongClick = true;
        }
    }

    @Override
    public void onSearchClickListener(View view) {
        isSearchEnabled = true;
        inSearchMode = true;
    }

    @Override
    public void onSearchTextChangeListener(View view, String text) {
        if (text.length() > 0) {
            searchText = text;
            loadContact(text);
        } else {
            loadContacts();
        }
    }

    @Override //btn add
    public void onRightIconClickListener(View view) {
        if (mActionMode != null) {
            mActionMode.finish();
        }
        if (getActivity() != null) {
            FragmentAddContact fragment = FragmentAddContact.newInstance(
                    null, FragmentAddContact.ContactMode.ADD
            );
            new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setReplace(false).load();
        }
    }

    @Override
    public void onBtnClearSearchClickListener(View view) {
        isSearchEnabled = false;
    }

    @Override
    public void onSearchBoxClosed() {
        inSearchMode = false;
    }

    @Override
    public void onContactsGetList() {
        loadContacts();
        prgMainLoader.setVisibility(View.GONE);
    }

    @Override
    public void onContactsGetListTimeOut() {
        if (tryRequest < 3) {
            G.handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    tryRequest++;
                    new RequestUserContactsGetList().userContactGetList();
                }
            }, 1000);
        }
    }

    @Override
    public boolean isAllowToBackPressed() {
        if (isMultiSelect) {
            setMultiSelectState(true);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void scrollToTopOfList() {
        if (realmRecyclerView != null) realmRecyclerView.smoothScrollToPosition(0);
    }

    public void loadContacts() {
        results = DbManager.getInstance().doRealmTask(realm -> {
            return realm.copyFromRealm(realm.where(RealmContacts.class).limit(CONTACT_LIMIT).sort("display_name").findAll());
        });
        if (realmRecyclerView.getAdapter() != null)
            ((ContactListAdapter) realmRecyclerView.getAdapter()).adapterUpdate(results);
    }

    private void loadContact(String key) {
        results = DbManager.getInstance().doRealmTask(realm -> {
            return realm.copyFromRealm(realm.where(RealmContacts.class).contains("display_name", key, Case.INSENSITIVE).findAll().sort("display_name"));
        });
        if (realmRecyclerView.getAdapter() != null)
            ((ContactListAdapter) realmRecyclerView.getAdapter()).adapterUpdate(results);
    }


    private interface onClickRecyclerView {
        void onClick(View view, int position);
    }

    private interface onLongClickRecyclerView {
        void onClick(View view, int position);
    }

    /**
     * **********************************************************************************
     * ********************************** RealmAdapter **********************************
     * **********************************************************************************
     */

    public class ContactListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements FastScrollerBarBaseAdapter {

        private List<RealmContacts> usersList = new ArrayList<>();

        void adapterUpdate(List<RealmContacts> contacts) {
            usersList = contacts;
            prgWaitingLoadList.setVisibility(View.INVISIBLE);

            if (contacts.size() > 0) {
                fastScroller.setVisibility(View.VISIBLE);
            } else {
                fastScroller.setVisibility(View.GONE);
            }

            notifyDataSetChanged();
        }

        @Override
        public String getBubbleText(int position) {
            if (usersList.size() > position) {
                return usersList.get(position).getDisplay_name().substring(0, 1).toUpperCase();
            } else {
                return "-";
            }
        }

        @Override
        public int getItemViewType(int position) {

            if (position != usersList.size()) {
                return 0;
            } else {
                return 1;
            }

        }

        @NotNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup viewGroup, int type) {

            View v;
            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

            if (type == 1) {
                v = inflater.inflate(R.layout.row_contact_counter, viewGroup, false);
                return new ViewHolderCounter(v);
            }

            if (mPageMode == CALL) {//call mode
                v = inflater.inflate(R.layout.item_contact_call, viewGroup, false);
                return new ViewHolderCall(v);
            } else { //new chat and contact
                v = inflater.inflate(R.layout.item_contact_chat, viewGroup, false);
                return new ViewHolder(v);
            }

        }

        @Override
        public void onBindViewHolder(@NotNull final RecyclerView.ViewHolder holder, int i) {

            if (holder instanceof ViewHolder) {

                ViewHolder viewHolder = (ViewHolder) holder;

                final RealmContacts contact = viewHolder.realmContacts = usersList.get(i);
                if (contact == null) {
                    return;
                }

                viewHolder.title.setText(EmojiManager.getInstance().replaceEmoji(contact.getDisplay_name(), viewHolder.title.getPaint().getFontMetricsInt()));
                viewHolder.subtitle.setText(
                        setUserStatus(
                                viewHolder.subtitle.getContext(),
                                contact.getStatus() == null ? null : AppUtils.getStatsForUser(contact.getStatus()),
                                contact.getId(),
                                contact.getLast_seen())
                );


                if (selectedList.containsKey(usersList.get(i).getPhone())) {
                    viewHolder.animateCheckBox.setVisibility(View.VISIBLE);
                    viewHolder.animateCheckBox.setChecked(true);
                } else {
                    if (isLongClick) {
                        viewHolder.animateCheckBox.setChecked(false);
                        viewHolder.animateCheckBox.setVisibility(View.VISIBLE);
                    } else {
                        viewHolder.animateCheckBox.setChecked(false);
                        viewHolder.animateCheckBox.setVisibility(View.GONE);
                    }
                }

                setAvatar(viewHolder, contact.getId());

            } else if (holder instanceof ViewHolderCall) {


                ViewHolderCall viewHolder = (ViewHolderCall) holder;

                final RealmContacts contact = viewHolder.realmContacts = usersList.get(i);
                if (contact == null) {
                    return;
                }

                viewHolder.title.setText(EmojiManager.getInstance().replaceEmoji(contact.getDisplay_name(), viewHolder.title.getPaint().getFontMetricsInt()));
                viewHolder.subtitle.setText("+" + contact.getPhone());

                if (selectedList.containsKey(usersList.get(i).getPhone())) {
                    viewHolder.animateCheckBox.setVisibility(View.VISIBLE);
                    viewHolder.animateCheckBox.setChecked(true);

                } else {
                    if (isLongClick) {
                        viewHolder.animateCheckBox.setChecked(false);
                        viewHolder.animateCheckBox.setVisibility(View.VISIBLE);
                    } else {
                        viewHolder.animateCheckBox.setChecked(false);
                        viewHolder.animateCheckBox.setVisibility(View.GONE);
                    }
                }

                setAvatar(viewHolder, contact.getId());

            } else if (holder instanceof ViewHolderCounter) {
                ((ViewHolderCounter) holder).setCount(usersList.size());
            }
        }

        @Override
        public int getItemCount() {
            return usersList.size() + 1; // +1 because add counter below of list
        }

        private void setAvatar(final RecyclerView.ViewHolder viewHolder, final long userId) {
            if (viewHolder instanceof ViewHolder) {
                ViewHolder holder = (ViewHolder) viewHolder;
                avatarHandler.getAvatar(new ParamWithAvatarType(holder.image, userId).avatarType(AvatarHandler.AvatarType.USER));

            } else if (viewHolder instanceof ViewHolderCall) {
                ViewHolderCall holder = (ViewHolderCall) viewHolder;
                avatarHandler.getAvatar(new ParamWithAvatarType(holder.image, userId).avatarType(AvatarHandler.AvatarType.USER));
            }
        }

        private String setUserStatus(Context context, String userStatus, long userId, long time) {

            if (userStatus != null) {
                if (userStatus.equals(ProtoGlobal.RegisteredUser.Status.EXACTLY.toString())) {
                    return LastSeenTimeUtil.computeTime(context, userId, time, false);
                } else {
                    return userStatus;
                }
            } else {
                return LastSeenTimeUtil.computeTime(context, userId, time, false);
            }
        }

        public String getItemCharacter(int position) {
            return String.valueOf(usersList.get(position).getDisplay_name().toUpperCase().charAt(0));
        }

        public boolean showCharacter(int position) {
            if (position > 0) {
                return usersList.get(position).getDisplay_name().toUpperCase().charAt(0) != usersList.get(position - 1).getDisplay_name().toUpperCase().charAt(0);
            } else {
                return true;
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private CircleImageView image;
            private TextView title;
            private TextView subtitle;
            private RealmContacts realmContacts;
            private ConstraintLayout root;
            private CheckBox animateCheckBox;

            public ViewHolder(View view) {
                super(view);

                root = view.findViewById(R.id.iv_itemContactChat_root);
                animateCheckBox = view.findViewById(R.id.iv_itemContactChat_checkBox);
                image = view.findViewById(R.id.iv_itemContactChat_profileImage);
                title = view.findViewById(R.id.tv_itemContactChat_userName);
                subtitle = view.findViewById(R.id.tv_itemContactChat_userPhoneNumber);

                if (G.isAppRtl) {
                    title.setGravity(Gravity.RIGHT);
                    subtitle.setGravity(Gravity.RIGHT);
                } else {
                    title.setGravity(Gravity.LEFT);
                    subtitle.setGravity(Gravity.LEFT);
                }

                root.setOnLongClickListener(v -> {
                    if (!isMultiSelect) {
                        showDialogContactLongClicked(getAdapterPosition(), realmContacts.getId(), realmContacts.getPhone(), realmContacts.getFirst_name(), realmContacts.getLast_name());
                    }
                    return true;
                });

                root.setOnClickListener(v -> {

                    if (!isMultiSelect) {
                        if (isCallAction) {
                            long userId = realmContacts.getId();
                            if (userId != 134 && AccountManager.getInstance().getCurrentUser().getId() != userId) {


                                new MaterialDialog.Builder(G.fragmentActivity).items(R.array.calls).itemsCallback(new MaterialDialog.ListCallback() {
                                    @Override
                                    public void onSelection(MaterialDialog dialog, View view1, int which, CharSequence text) {

                                        switch (which) {
                                            case 0:
                                                CallSelectFragment.call(userId, false, ProtoSignalingOffer.SignalingOffer.Type.VOICE_CALLING);
                                                popBackStackFragment();
                                                break;
                                            case 1:
                                                CallSelectFragment.call(userId, false, ProtoSignalingOffer.SignalingOffer.Type.VIDEO_CALLING);

                                                popBackStackFragment();
                                                break;
                                        }

                                        dialog.dismiss();
                                    }
                                }).show();
                            }

                        } else {
                            showProgress();

                            HelperPublicMethod.goToChatRoom(realmContacts.getId(), () -> {
                                hideProgress();
                                /*popBackStackFragment();*/
                            }, () -> hideProgress());
                        }
                    } else {
                        if (onClickRecyclerView != null)
                            onClickRecyclerView.onClick(v, getAdapterPosition());
                    }
                });
            }
        }

        public class ViewHolderCall extends RecyclerView.ViewHolder {

            private CircleImageView image;
            private TextView title;
            private TextView subtitle;
            private MaterialDesignTextView btnVoiceCall;
            private RealmContacts realmContacts;
            private ConstraintLayout root;
            private CheckBox animateCheckBox;

            public ViewHolderCall(View view) {
                super(view);

                root = view.findViewById(R.id.iv_itemContactCall_root);
                animateCheckBox = view.findViewById(R.id.iv_itemContactCall_checkBox);
                image = view.findViewById(R.id.iv_itemContactCall_profileImage);
                title = view.findViewById(R.id.tv_itemContactCall_userName);
                subtitle = view.findViewById(R.id.tv_itemContactCall_userPhoneNumber);
                btnVoiceCall = view.findViewById(R.id.tv_itemContactCall_voiceCall);

                if (G.isAppRtl) {
                    title.setGravity(Gravity.RIGHT);
                    subtitle.setGravity(Gravity.RIGHT);
                } else {
                    title.setGravity(Gravity.LEFT);
                    subtitle.setGravity(Gravity.LEFT);
                }

                btnVoiceCall.setOnClickListener(v -> {
                    long userId = realmContacts.getId();
                    if (userId != 134 && AccountManager.getInstance().getCurrentUser().getId() != userId) {
                        CallSelectFragment.call(userId, false, ProtoSignalingOffer.SignalingOffer.Type.VOICE_CALLING);
                        popBackStackFragment();
                    }
                });

                root.setOnClickListener(v -> {

                    if (!isMultiSelect) {
                        if (isCallAction) {
                            long userId = realmContacts.getId();
                            if (userId != 134 && AccountManager.getInstance().getCurrentUser().getId() != userId) {
                                CallSelectFragment callSelectFragment = CallSelectFragment.getInstance(userId, false, ProtoSignalingOffer.SignalingOffer.Type.VOICE_CALLING);
                                callSelectFragment.show(getFragmentManager(), null);
                            }

                        } else {
                            showProgress();

                            HelperPublicMethod.goToChatRoom(realmContacts.getId(), () -> {
                                hideProgress();
                                popBackStackFragment();
                            }, () -> hideProgress());
                        }
                    } else {
                        if (onClickRecyclerView != null)
                            onClickRecyclerView.onClick(v, getAdapterPosition());
                    }
                });

            }
        }

        public class ViewHolderCounter extends RecyclerView.ViewHolder {

            TextView txtCounter;

            public ViewHolderCounter(@NonNull View itemView) {
                super(itemView);

                txtCounter = itemView.findViewById(R.id.row_contact_counter_txt);
            }

            public void setCount(int count) {

                String countStr = String.valueOf(count);

                if (HelperCalander.isPersianUnicode) {
                    countStr = HelperCalander.convertToUnicodeFarsiNumber(countStr);
                }


                txtCounter.setText(countStr + " " + getString(R.string.am_contact) + (G.selectedLanguage.equals("en") ? "s" : ""));
            }
        }
    }
}


