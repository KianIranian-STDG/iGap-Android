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

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.ArrayMap;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityMain;
import net.iGap.activities.CallActivity;
import net.iGap.fragments.qrCodePayment.fragments.ScanCodeQRCodePaymentFragment;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperPermission;
import net.iGap.helper.HelperPreferences;
import net.iGap.helper.HelperPublicMethod;
import net.iGap.helper.HelperTracker;
import net.iGap.helper.LayoutCreator;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.helper.avatar.ParamWithAvatarType;
import net.iGap.libs.emojiKeyboard.emoji.EmojiManager;
import net.iGap.messenger.ui.components.FragmentMediaContainer;
import net.iGap.messenger.ui.toolBar.BackDrawable;
import net.iGap.messenger.ui.toolBar.NumberTextView;
import net.iGap.messenger.ui.toolBar.ToolBarMenuSubItem;
import net.iGap.messenger.ui.toolBar.Toolbar;
import net.iGap.messenger.ui.toolBar.ToolbarItem;
import net.iGap.messenger.ui.toolBar.ToolbarItems;
import net.iGap.model.PassCode;
import net.iGap.module.AppUtils;
import net.iGap.module.CircleImageView;
import net.iGap.module.ContactUtils;
import net.iGap.module.Contacts;
import net.iGap.module.LastSeenTimeUtil;
import net.iGap.module.LoginActions;
import net.iGap.module.MaterialDesignTextView;
import net.iGap.module.MusicPlayer;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.ScrollingLinearLayoutManager;
import net.iGap.module.StatusBarUtil;
import net.iGap.module.Theme;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.accountManager.DbManager;
import net.iGap.module.customView.CheckBox;
import net.iGap.module.dialog.bottomsheet.BottomSheetFragment;
import net.iGap.module.scrollbar.FastScroller;
import net.iGap.module.scrollbar.FastScrollerBarBaseAdapter;
import net.iGap.observers.interfaces.OnContactImport;
import net.iGap.observers.interfaces.OnContactsGetList;
import net.iGap.observers.interfaces.OnGetPermission;
import net.iGap.observers.interfaces.OnUserContactDelete;
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

public class RegisteredContactsFragment extends BaseMainFragments implements OnContactImport, OnUserContactDelete, OnContactsGetList {

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
    private View btnDialNumber;
    private RecyclerView realmRecyclerView;
    private FastScroller fastScroller;
    private ProgressBar prgWaitingLoadList, prgMainLoader;
    private ActionMode mActionMode;
    private Toolbar contactsToolbar;

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

    private final int addUserTag = 1;
    private final int codeScannerTag = 2;
    private final int passCodeTag = 3;
    private final int multiSelectTag = 4;
    private final int searchTag = 5;
    private ToolBarMenuSubItem addItem;
    private final int syncContactTag = 6;
    private final int inviteContactTag = 7;
    private final int moreItemTag = 8;
    private final int deleteTag = 9;
    private final int editTag = 10;
    private NumberTextView multiSelectCounter;
    private final int selectCounter = 16;
    private ToolbarItems actionToolbar;
    private ArrayList<ToolbarItem> actionModeViews = new ArrayList<>();
    private ToolbarItem deleteItem;
    private ToolbarItem editItem;
    private ToolbarItem searchItem;
    private ToolbarItem passCodeItem;
    private FragmentMediaContainer mediaContainer;

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

        FrameLayout toolbarLayout = view.findViewById(R.id.frg_contact_ll_toolbar_layout);
        contactsToolbar = new Toolbar(getContext());
        mediaContainer = new FragmentMediaContainer(getContext(), this);
        mediaContainer.setListener(i -> {
            switch (i) {
                case FragmentMediaContainer.CALL_TAG:
                    getActivity().startActivity(new Intent(getContext(), CallActivity.class));
                    break;
                case FragmentMediaContainer.MEDIA_TAG:
                    if (!MusicPlayer.isVoice) {
                        Intent intent = new Intent(context, ActivityMain.class);
                        intent.putExtra(ActivityMain.openMediaPlyer, true);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getActivity().startActivity(intent);
                    }
                    break;
                case FragmentMediaContainer.PLAY_TAG:
                    break;
            }
        });
        ToolbarItems toolbarItems = contactsToolbar.createToolbarItems();


        contactsToolbar.setTitle(G.isAppRtl ? R.string.logo_igap_fa : R.string.logo_igap_en);
        if (isContact) {
            ToolbarItem moreItem = toolbarItems.addItemWithWidth(moreItemTag, R.string.icon_other_vertical_dots, 54);
            addItem = moreItem.addSubItem(addUserTag, R.string.icon_add, getResources().getString(R.string.menu_add_contact));
            moreItem.addSubItem(syncContactTag, R.string.icon_beeptunes_sync, getResources().getString(R.string.sync_contact));
            moreItem.addSubItem(inviteContactTag, R.string.icon_add_contact, getResources().getString(R.string.Invite_Friends));
            if (PassCode.getInstance().isPassCode()) {
                passCodeItem = toolbarItems.addItemWithWidth(passCodeTag, R.string.icon_unlock, 54);
            }
        }
        searchItem = toolbarItems.addItemWithWidth(searchTag, R.string.icon_search, 54).setIsSearchBox(true).setActionBarMenuItemSearchListener(new ToolbarItem.ActionBarMenuItemSearchListener() {
            @Override
            public void onSearchExpand() {
                isSearchEnabled = true;
                inSearchMode = true;
                contactsToolbar.setBackIcon(new BackDrawable(true));
            }

            @Override
            public void onSearchCollapse() {
                isSearchEnabled = false;
                inSearchMode = false;
                loadContacts();
                if (isContact) {
                    contactsToolbar.setBackIcon(null);
                } else {
                    contactsToolbar.setBackIcon(new BackDrawable(false));
                }
            }

            @Override
            public void onTextChanged(EditText editText) {
                String text = editText.getText().toString();
                if (text.length() > 0) {
                    searchText = text;
                    loadContact(text);
                } else {
                    loadContacts();
                }
            }
        });

        if (!isContact) {
            contactsToolbar.setBackIcon(new BackDrawable(false));
            toolbarItems.addItemWithWidth(addUserTag, R.string.icon_add_contact, 54);
        } else {
            toolbarItems.addItemWithWidth(codeScannerTag, R.string.icon_QR_code, 54);
        }
        if (mPageMode == CALL) {
            contactsToolbar.setTitle(getString(R.string.make_call));
        } else if (mPageMode == ADD) {
            contactsToolbar.setTitle(getString(R.string.create_chat));
        }
        createActionMode();
        toolbarLayout.addView(mediaContainer, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, 38, Gravity.BOTTOM, 0, 60, 0, 0));
        toolbarLayout.addView(contactsToolbar);
        contactsToolbar.setListener(i -> {
            switch (i) {
                case -1:
                    if (!isContact) {
                        if (!contactsToolbar.isSearchFieldVisible()) {
                            popBackStackFragment();
                        }
                    } else {
                        if (isMultiSelect) {
                            contactsToolbar.hideActionToolbar();
                            contactsToolbar.setBackIcon(null);
                            setMultiSelectState(isMultiSelect);
                        }
                    }
                    break;
                case editTag:
                    if (results != null) {
                        for (RealmContacts realmContacts : results) {
                            if (realmContacts.getPhone() == (Long) selectedList.keySet().toArray()[0]) {
                                FragmentAddContact fragment = FragmentAddContact.newInstance(
                                        realmContacts.getId(), "+" + realmContacts.getPhone(), realmContacts.getFirst_name(), realmContacts.getLast_name(), FragmentAddContact.ContactMode.EDIT, (name1, family1) -> loadContacts()
                                );
                                if (getActivity() != null)
                                    new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setReplace(false).load();
                                contactsToolbar.hideActionToolbar();
                                setMultiSelectState(isMultiSelect);
                                contactsToolbar.setBackIcon(null);
                                return;
                            }
                        }
                    }
                    break;
                case deleteTag:
                    new MaterialDialog.Builder(G.fragmentActivity).title(R.string.to_delete_contact).content(R.string.delete_text).positiveText(R.string.B_ok).onPositive((dialog, which) -> {

                        for (ArrayMap.Entry<Long, Boolean> entry : selectedList.entrySet()) {
                            new RequestUserContactsDelete().contactsDelete("" + entry.getKey());
                        }
                        setMultiSelectState(true);
                        contactsToolbar.hideActionToolbar();
                        contactsToolbar.setBackIcon(null);
                    }).negativeText(R.string.B_cancel).show();
                    break;
                case addUserTag:
                    if (mActionMode != null) {
                        mActionMode.finish();
                    }
                    if (getActivity() != null) {
                        FragmentAddContact fragment = FragmentAddContact.newInstance(
                                null, FragmentAddContact.ContactMode.ADD
                        );
                        new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setReplace(false).load();
                    }
                    break;
                case multiSelectTag:
                    showDialog();
                    break;
                case codeScannerTag:
                    onCodeScannerClickListener();
                    break;
                case syncContactTag:
                    if (isMultiSelect) setMultiSelectState(true);
                    ContactUtils.syncContacts();
                    break;
                case inviteContactTag:
                    try {
                        HelperPermission.getContactPermision(getContext(), new OnGetPermission() {
                            @Override
                            public void Allow() {
                                HelperTracker.sendTracker(HelperTracker.TRACKER_INVITE_FRIEND);
                                new HelperFragment(getActivity().getSupportFragmentManager(), new LocalContactFragment()).setReplace(false).load();
                            }

                            @Override
                            public void deny() {

                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case passCodeTag:
                    if (passCodeItem == null) {
                        return;
                    }
                    if (ActivityMain.isLock) {
                        passCodeItem.setIcon(R.string.icon_unlock);
                        ActivityMain.isLock = false;
                        HelperPreferences.getInstance().putBoolean(SHP_SETTING.FILE_NAME, SHP_SETTING.KEY_LOCK_STARTUP_STATE, false);
                    } else {
                        passCodeItem.setIcon(R.string.icon_lock);
                        ActivityMain.isLock = true;
                        HelperPreferences.getInstance().putBoolean(SHP_SETTING.FILE_NAME, SHP_SETTING.KEY_LOCK_STARTUP_STATE, true);
                    }

                    checkPassCodeVisibility();
                    break;
            }
        });
        if (isContact) {
            Contacts.localPhoneContactId = 0;
            Contacts.getContact = true;
        }


        prgMainLoader = view.findViewById(R.id.fc_loader_main);
        btnAddNewGroupCall = view.findViewById(R.id.menu_layout_new_group_call);
        btnDialNumber = view.findViewById(R.id.menu_layout_btn_dial_number);
        btnAddSecretChat = view.findViewById(R.id.menu_layout_btn_secret_chat);
        btnAddNewGroup = view.findViewById(R.id.menu_layout_add_new_group);
        btnAddNewChannel = view.findViewById(R.id.menu_layout_add_new_channel);
        fastScroller = view.findViewById(R.id.fs_contact_fastScroller);
        prgWaitingLoadList = view.findViewById(R.id.prgWaiting_loadList);

        realmRecyclerView.setAdapter(new ContactListAdapter());

        if (!inSearchMode)
            loadContacts();

        switch (mPageMode) {
            case CALL:
                btnDialNumber.setVisibility(View.GONE);
                if (addItem != null) {
//                    addItem.setVisibility(View.GONE);
                }
                break;
            case ADD:
                btnAddNewChannel.setVisibility(View.VISIBLE);
                btnAddNewGroup.setVisibility(View.VISIBLE);
                break;
            case CONTACTS:

        }

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

                if (!contactsToolbar.isInActionMode()) {
                    setPageShowingMode(4);
                }
            }
            multi_select(position);
        };

        try {
            if (getPermission && isContact) {
                getPermission = false;
                HelperPermission.getContactPermision(getContext(), new OnGetPermission() {
                    @Override
                    public void Allow() throws IOException {
                        /**
                         * if contacts size is zero send request for get contacts list
                         * for insure that contacts not exist really or not
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
                if (results.size() == 0) {
                    LoginActions.importContact();
                }
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

        //todo: fixed it ,effect in load time
        if (isMultiSelect) {
            refreshAdapter(0, true);
            if (!contactsToolbar.isInActionMode()) {
                Log.wtf(this.getClass().getName(), "setPageShowingMode 4");
                setPageShowingMode(4);
            }
            isLongClick = true;
        }
    }

    private void setPageShowingMode(int mode) {
        if (mode == 0 || mode == 1) { //contact mode
            btnAddNewGroupCall.setVisibility(View.GONE);
            btnDialNumber.setVisibility(View.GONE);
            btnAddNewChannel.setVisibility(View.GONE);
            btnAddNewGroup.setVisibility(View.GONE);
            btnAddSecretChat.setVisibility(View.GONE);
        } else if (mode == 2) { // call mode
            btnAddNewGroupCall.setVisibility(View.VISIBLE);
            btnDialNumber.setVisibility(View.VISIBLE);
            btnAddNewChannel.setVisibility(View.GONE);
            btnAddNewGroup.setVisibility(View.GONE);
            btnAddSecretChat.setVisibility(View.GONE);
        } else if (mode == 3) { // add mode

            btnAddNewChannel.setVisibility(View.VISIBLE);
            btnAddNewGroup.setVisibility(View.VISIBLE);
            btnAddSecretChat.setVisibility(View.VISIBLE);
            btnAddNewGroupCall.setVisibility(View.GONE);
            btnDialNumber.setVisibility(View.GONE);
        } else if (mode == 4) {//edit mode
            btnAddNewChannel.setVisibility(View.GONE);
            btnAddNewGroup.setVisibility(View.GONE);
            btnAddSecretChat.setVisibility(View.GONE);
            btnAddNewGroupCall.setVisibility(View.GONE);
        }
    }

    private void createActionMode() {
        if (contactsToolbar.isInActionMode())
            return;

        actionToolbar = contactsToolbar.createActionToolbar(null);

        deleteItem = actionToolbar.addItemWithWidth(deleteTag, R.string.icon_delete, 54);
        editItem = actionToolbar.addItemWithWidth(editTag, R.string.icon_edit, 54);

        multiSelectCounter = new NumberTextView(getContext());
        multiSelectCounter.setTextSize(18);
        multiSelectCounter.setTypeface(ResourcesCompat.getFont(getContext(), R.font.main_font_bold));
        multiSelectCounter.setTextColor(Theme.getInstance().getPrimaryTextColor(getContext()));
        multiSelectCounter.setTag(selectCounter);
        actionToolbar.addView(multiSelectCounter, LayoutCreator.createLinear(0, LayoutCreator.MATCH_PARENT, 1.0f, 72, 0, 0, 0));

        actionModeViews.add(deleteItem);
        actionModeViews.add(editItem);
    }

    private void showActionMode() {
        contactsToolbar.showActionToolbar();
        BackDrawable backDrawable = new BackDrawable(true);
        backDrawable.setRotation(1, true);
        backDrawable.setRotatedColor(Theme.getInstance().getPrimaryTextColor(getContext()));
        contactsToolbar.setBackIcon(backDrawable);

        AnimatorSet animatorSet = new AnimatorSet();
        ArrayList<Animator> animators = new ArrayList<>();
        for (int a = 0; a < actionModeViews.size(); a++) {
            View view = actionModeViews.get(a);
            view.setPivotY(Toolbar.getCurrentActionBarHeight() / 2);
            animators.add(ObjectAnimator.ofFloat(view, View.SCALE_Y, 0.1f, 1.0f));
        }
        animatorSet.playTogether(animators);
        animatorSet.setDuration(180);
        animatorSet.start();
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

    public void checkPassCodeVisibility() {
        if (PassCode.getInstance().isPassCode()) {
            if (passCodeItem == null) {
                passCodeItem = toolbar.addItem(passCodeTag, R.string.icon_unlock, Color.WHITE);
            }

            ActivityMain.isLock = HelperPreferences.getInstance().readBoolean(SHP_SETTING.FILE_NAME, SHP_SETTING.KEY_LOCK_STARTUP_STATE);
            if (ActivityMain.isLock) {
                passCodeItem.setIcon(R.string.icon_lock);
            } else {
                passCodeItem.setIcon(R.string.icon_unlock);
            }
        } else if (passCodeItem != null) {
            passCodeItem.setVisibility(View.GONE);
        }
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

        if (isSearchEnabled && contactsToolbar != null)
            searchItem.performClick();
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
        if (contactsToolbar.isInActionMode()) {

            if (results.get(position) == null) {
                return;
            }

            if (selectedList.containsKey(results.get(position).getPhone())) {
                selectedList.remove(results.get(position).getPhone());
            } else {
                selectedList.put(results.get(position).getPhone(), true);
            }
            if (selectedList.size() > 0) {
                if (selectedList.size() > 1) {
                    editItem.setVisibility(View.GONE);
                } else {
                    editItem.setVisibility(View.VISIBLE);
                }
                multiSelectCounter.setNumber(selectedList.size(), true);
            } else {
                contactsToolbar.hideActionToolbar();
                contactsToolbar.setBackIcon(null);
                setMultiSelectState(isMultiSelect);
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

            setMultiSelectState(isMultiSelect);
            showActionMode();
            multi_select(itemPosition);

            new BottomSheetFragment().setData(items, -1, position -> {
                if (position == 0) {

                } else if (position == 1) {

                } else if (position == 2) {

                }
            });
        }
    }

    private void setMultiSelectState(boolean state) {

        if (state) {
            setPageShowingMode(mPageMode);
            mActionMode = null;
            isMultiSelect = false;
            isLongClick = false;
            selectedList.clear();
            refreshAdapter(0, true);
        } else {
            isMultiSelect = true;
            refreshAdapter(0, true);

            if (!contactsToolbar.isInActionMode()) {
                setPageShowingMode(4);
            }
            isLongClick = true;
        }
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
            contactsToolbar.setBackIcon(null);
            contactsToolbar.hideActionToolbar();
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

    private void onCodeScannerClickListener() {

        new HelperFragment(getActivity().getSupportFragmentManager(), ScanCodeQRCodePaymentFragment.newInstance())
                .setAddToBackStack(true)
                .setReplace(false)
                .load();

//        DbManager.getInstance().doRealmTask(realm -> {
//            String phoneNumber = "";
//            RealmUserInfo userInfo = realm.where(RealmUserInfo.class).findFirst();
//            try {
//                if (userInfo != null) {
//                    phoneNumber = userInfo.getUserInfo().getPhoneNumber().substring(2);
//                } else {
//                    phoneNumber = AccountManager.getInstance().getCurrentUser().getPhoneNumber().substring(2);
//                }
//            } catch (Exception e) {
//                //maybe exception was for realm substring
//                try {
//                    phoneNumber = AccountManager.getInstance().getCurrentUser().getPhoneNumber().substring(2);
//                } catch (Exception ex) {
//                    //nothing
//                }
//            }
//
//            if (userInfo == null || !userInfo.isWalletRegister()) {
//                new HelperFragment(getActivity().getSupportFragmentManager(), FragmentWalletAgrement.newInstance(phoneNumber)).load();
//            } else {
//                getActivity().startActivityForResult(new HelperWallet().goToWallet(getContext(), new Intent(getActivity(), WalletActivity.class), "0" + phoneNumber, true), WALLET_REQUEST_CODE);
//            }
//
//        });
    }

    /**
     * **********************************************************************************
     * ********************************** RealmAdapter **********************************
     * **********************************************************************************
     */

    public class ContactListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements FastScrollerBarBaseAdapter {

        private List<RealmContacts> usersList = new ArrayList<>();
        public boolean isClickable = true;

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

                viewHolder.root.setOnClickListener(view -> {
                    if (!isMultiSelect) {
                        if (isCallAction) {
                            long userId = contact.getId();
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
                            if (isClickable) {
                                isClickable = false;
                                HelperPublicMethod.goToChatRoom(contact.getId(), () -> {
                                    isClickable = true;
                                }, () -> {
                                    isClickable = true;
                                });
                            }
                        }
                    } else {
                        if (onClickRecyclerView != null)
                            onClickRecyclerView.onClick(view, i);
                    }
                });
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
                    viewHolder.animateCheckBox.setChecked(true, true);
                } else {
                    if (isLongClick) {
                        viewHolder.animateCheckBox.setChecked(false, true);
                        viewHolder.animateCheckBox.setVisibility(View.VISIBLE);
                    } else {
                        viewHolder.animateCheckBox.setChecked(false, true);
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
                    viewHolder.animateCheckBox.setChecked(true, true);

                } else {
                    if (isLongClick) {
                        viewHolder.animateCheckBox.setChecked(false, true);
                        viewHolder.animateCheckBox.setVisibility(View.VISIBLE);
                    } else {
                        viewHolder.animateCheckBox.setChecked(false, true);
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
            private net.iGap.module.customView.CheckBox animateCheckBox;

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


