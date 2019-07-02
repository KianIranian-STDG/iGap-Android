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
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.AdapterListContact;
import net.iGap.dialog.BottomSheetItemClickCallback;
import net.iGap.dialog.bottomsheet.BottomSheetFragment;
import net.iGap.helper.ContactManager;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperPermission;
import net.iGap.helper.HelperPublicMethod;
import net.iGap.helper.HelperToolbar;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.helper.avatar.ParamWithAvatarType;
import net.iGap.interfaces.OnContactImport;
import net.iGap.interfaces.OnGetPermission;
import net.iGap.interfaces.OnUserContactDelete;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.libs.bottomNavigation.Util.Utils;
import net.iGap.module.ContactUtils;
import net.iGap.module.Contacts;
import net.iGap.module.LoginActions;
import net.iGap.module.MaterialDesignTextView;
import net.iGap.module.structs.StructListOfContact;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoSignalingOffer;
import net.iGap.realm.RealmContacts;
import net.iGap.realm.RealmContactsFields;
import net.iGap.request.RequestUserContactsDelete;
import net.iGap.request.RequestUserContactsGetList;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmResults;

public class RegisteredContactsFragment extends BaseFragment implements ToolbarListener, OnContactImport, OnUserContactDelete {

    private static final String TAG = "aabolfazlContact";
    private static boolean getPermission = true;
    public onClickRecyclerView onCliclRecyclerView;
    public onLongClickRecyclerView onLongClickRecyclerView;
    public boolean isLongClick = false;
    public ArrayList<StructListOfContact> phoneContactsList = new ArrayList<>();
    protected ArrayMap<Long, Boolean> selectedList = new ArrayMap<>();
    boolean isMultiSelect = false;
    AdapterListContact adapterListContact;
    /*StickyRecyclerHeadersDecoration decoration;*/
    private List<RealmContacts> results;
    private TextView menu_txt_titleToolbar;
    private ViewGroup vgInviteFriend;
    private RecyclerView realmRecyclerView, rcvListContact;
    private SharedPreferences sharedPreferences;
    private Realm realm;
    private ProgressBar prgWaiting;
    private EditText edtSearch;
    private boolean isCallAction = false;
    private FastItemAdapter fastItemAdapter;
    private ProgressBar prgWaitingLiadList;
    private NestedScrollView nestedScrollView;
    private ActionMode mActionMode;
    private ContactListAdapter contactListAdapter;
    private HelperToolbar mHelperToolbar;
    private boolean isToolbarInEditMode = false;
    private int mPageMode = 0; // 0 = new chat , 1 = contact , 2 = call , 3 = add new
    private LinearLayout btnAddNewChannel, btnAddNewGroup, btnAddSecretChat;
    private LinearLayout btnAddNewGroupCall, btnAddNewContact, btnDialNumber;

    private ViewGroup mLayoutMultiSelected;
    private TextView mTxtSelectedCount;
    private AppCompatTextView mBtnDeleteSelected;
    private MaterialDesignTextView mBtnCancelSelected;
    private int mNumberOfSelectedCounter;

    private Context context = G.context;

    private View view;
    private boolean isInit = false;
    private LinearLayout layoutAppBarContainer;
    private boolean isContact;
    private boolean isSwipe = false;

    private boolean endPage = false;

    public static RegisteredContactsFragment newInstance() {
        return new RegisteredContactsFragment();
    }

    private Realm getRealm() {
        if (realm == null || realm.isClosed()) {
            realm = Realm.getDefaultInstance();
        }
        return realm;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && !isInit) {
            init();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        if (getArguments() != null) {
            isSwipe = getArguments().getBoolean("isBackSwipable", false);
        }
        if (isSwipe) {
            return attachToSwipeBack(inflater.inflate(R.layout.fragment_contacts, container, false));
        } else {
            return inflater.inflate(R.layout.fragment_contacts, container, false);
        }
    }

    @Override
    public void onViewCreated(@NotNull View view, final @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        G.onContactImport = this;
        G.onUserContactdelete = this;

        realmRecyclerView = view.findViewById(R.id.recycler_view);

        LinearLayout toolbarLayout = view.findViewById(R.id.frg_contact_ll_toolbar_layout);

        Utils.darkModeHandler(toolbarLayout);
        mHelperToolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLeftIcon(R.string.edit_icon)
                .setRightIcons(R.string.add_icon)
                .setSearchBoxShown(true)
                .setLogoShown(true);

        Bundle bundle = getArguments();

        //in contact mode user dont set swipeable or is false
        //for replace back btn instead of edit in every mode except contact use it
        if (bundle != null) {
            isContact = !bundle.getBoolean("isBackSwipable", true);
        } else {
            isContact = true;
        }

        if (!isContact) {
            mHelperToolbar.setLeftIcon(R.string.back_icon);
        }

        toolbarLayout.addView(mHelperToolbar.getView());
        mHelperToolbar.setListener(this);

        init();
    }

    private void init() {
        if (view == null) {
            return;
        }

        if (!getUserVisibleHint()) {
            return;
        }
        isInit = true;

        if (isContact) {
//            G.onPhoneContact = this;
            Contacts.localPhoneContactId = 0;
            Contacts.getContact = true;

        }
        //sharedPreferences = G.fragmentActivity.getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);

        /**
         * not import contact in every enter to this page
         * for this purpose i comment this code. but not cleared.
         */

        if (results == null)
            results = ContactManager.getContactList(ContactManager.FIRST);

        nestedScrollView = view.findViewById(R.id.nestedScrollContact);

        TextView txtNonUser = view.findViewById(R.id.txtNon_User);
        prgWaitingLiadList = view.findViewById(R.id.prgWaiting_loadList);
        contactListAdapter = new ContactListAdapter(results);
        realmRecyclerView.setAdapter(contactListAdapter);

        vgInviteFriend = view.findViewById(R.id.menu_layout_inviteFriend);

        btnAddNewGroupCall = view.findViewById(R.id.menu_layout_new_group_call);
        btnAddNewContact = view.findViewById(R.id.menu_layout_add_new_contact);
        btnDialNumber = view.findViewById(R.id.menu_layout_btn_dial_number);

        btnAddSecretChat = view.findViewById(R.id.menu_layout_btn_secret_chat);
        btnAddNewGroup = view.findViewById(R.id.menu_layout_add_new_group);
        btnAddNewChannel = view.findViewById(R.id.menu_layout_add_new_channel);
        edtSearch = mHelperToolbar.getEditTextSearch();

        mLayoutMultiSelected = view.findViewById(R.id.fc_layout_selected_mode);
        mTxtSelectedCount = view.findViewById(R.id.fc_selected_mode_txt_counter);
        mBtnDeleteSelected = view.findViewById(R.id.fc_selected_mode_btn_delete);
        mBtnCancelSelected = view.findViewById(R.id.fc_selected_mode_btn_cancel);
        mTxtSelectedCount.setText(0 + " " + G.context.getResources().getString(R.string.item_selected));

        Bundle bundle = this.getArguments();
        String title = null;
        if (bundle != null) {
            title = bundle.getString("TITLE");
            isCallAction = bundle.getBoolean("ACTION");
        }

        if (title != null) {

            switch (title) {
                case "New Chat":
                    mPageMode = 0;
                    break;
                case "Contacts":
                    mPageMode = 1;
                    break;
                case "call":
                    mPageMode = 2;
                    //visible add buttons
                    btnAddNewContact.setVisibility(View.VISIBLE);
                    btnDialNumber.setVisibility(View.VISIBLE);
                    vgInviteFriend.setVisibility(View.GONE);
                    mHelperToolbar.getRightButton().setVisibility(View.GONE);
                    break;
                case "ADD":
                    mPageMode = 3;
                    //visible add buttons
                    btnAddNewChannel.setVisibility(View.VISIBLE);
                    btnAddNewGroup.setVisibility(View.VISIBLE);
                    //btnAddSecretChat.setVisibility(View.VISIBLE);
                    vgInviteFriend.setVisibility(View.GONE);
                    break;
            }
        }

        vgInviteFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    try {
                        Log.i(TAG, "onClick: try");
                        HelperPermission.getContactPermision(getContext(), new OnGetPermission() {
                            @Override
                            public void Allow() throws IOException {
                                new HelperFragment(getActivity().getSupportFragmentManager(), new LocalContactFragment()).load();
                                Log.i(TAG, "Allow: ");
                            }

                            @Override
                            public void deny() {
                                Log.i(TAG, "Permission deny");
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
//                }


            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

        realmRecyclerView = view.findViewById(R.id.recycler_view);
        realmRecyclerView.setLayoutManager(layoutManager);
        realmRecyclerView.setNestedScrollingEnabled(false);

        onCliclRecyclerView = new onClickRecyclerView() {
            @Override
            public void onClick(View view, int position) {
                if (isMultiSelect) {
                    multi_select(position);
                }
            }
        };

        onLongClickRecyclerView = new onLongClickRecyclerView() {
            @Override
            public void onClick(View view, int position) {
                if (!isMultiSelect) {
                    isMultiSelect = true;
                    refreshAdapter(0, true);

                    if (!mLayoutMultiSelected.isShown()) {
                        setPageShowingMode(4);

                    }

                }
                multi_select(position);
            }
        };

        rcvListContact = (RecyclerView) view.findViewById(R.id.rcv_friends_to_invite);
        fastItemAdapter = new FastItemAdapter();

        try {
            if (getPermission && isContact) {
                getPermission = false;
                HelperPermission.getContactPermision(G.fragmentActivity, new OnGetPermission() {
                    @Override
                    public void Allow() throws IOException {
                        /**
                         * if contacts size is zero send request for get contacts list
                         * for insuring that contacts not exist really or not
                         */
                        if (results.size() == 0) {
                            LoginActions.importContact();
                        }
                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                prgWaitingLiadList.setVisibility(View.VISIBLE);
                            }
                        });
                        new Contacts.FetchContactForClient().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }

                    @Override
                    public void deny() {
                        if (results.size() == 0) {
                            new RequestUserContactsGetList().userContactGetList();
                        }
                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                prgWaitingLiadList.setVisibility(View.GONE);
                            }
                        });
                    }
                });
            } else {
                if (results.size() == 0) {
                    new RequestUserContactsGetList().userContactGetList();
                }

                if (isContact) {
                    if (HelperPermission.grantedContactPermission()) {
                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                prgWaitingLiadList.setVisibility(View.VISIBLE);
                            }
                        });
                        new Contacts.FetchContactForClient().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    } else {
                        prgWaitingLiadList.setVisibility(View.GONE);
                    }
                } else {
                    prgWaitingLiadList.setVisibility(View.GONE);

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (isContact) {

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            rcvListContact.setLayoutManager(linearLayoutManager);
            rcvListContact.setItemAnimator(new DefaultItemAnimator());
            adapterListContact = new AdapterListContact(phoneContactsList, getContext());
            rcvListContact.setAdapter(adapterListContact);
            rcvListContact.setNestedScrollingEnabled(false);

        } else {
            rcvListContact.setVisibility(View.GONE);
            prgWaitingLiadList.setVisibility(View.GONE);
            txtNonUser.setVisibility(View.GONE);
        }

        nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {

            if (v.getChildAt(v.getChildCount() - 1) != null) {
                if ((scrollY >= (v.getChildAt(v.getChildCount() - 1).getMeasuredHeight() - v.getMeasuredHeight())) &&
                        scrollY > oldScrollY) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int pastVisiblesItems = layoutManager.findFirstVisibleItemPosition();

                    if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                        if (!endPage)
                            contactListAdapter.addUserList(ContactManager.getContactList(ContactManager.OVER_LOAD));
                    }
                }
            }

            if (Contacts.isEndLocal) {
                return;
            }
            if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                new Contacts.FetchContactForClient().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });

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

                if (FragmentNewGroup.onRemoveFragmentNewGroup != null)
                    FragmentNewGroup.onRemoveFragmentNewGroup.onRemove();
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
                Toast.makeText(context, getString(R.string.no_item_selected), Toast.LENGTH_SHORT).show();
                return;
            }

            new MaterialDialog.Builder(G.fragmentActivity).title(R.string.to_delete_contact).content(R.string.delete_text).positiveText(R.string.B_ok).onPositive(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                    for (ArrayMap.Entry<Long, Boolean> entry : selectedList.entrySet()) {
                        new RequestUserContactsDelete().contactsDelete("" + entry.getKey());
                        contactListAdapter.remove(entry.getKey());
                    }

                    setPageShowingMode(mPageMode);
                    isMultiSelect = false;
                    isLongClick = false;
                    selectedList.clear();
                }
            }).negativeText(R.string.B_cancel).show();

        });

        layoutAppBarContainer = view.findViewById(R.id.fc_layout_appbar_container);

        mBtnCancelSelected.setOnClickListener(v -> {
            setPageShowingMode(mPageMode);
            mActionMode = null;
            isMultiSelect = false;
            isLongClick = false;
            selectedList.clear();
            refreshAdapter(0, true);
        });
    }

    private void setPageShowingMode(int mode) {

        if (mode == 0 || mode == 1) { //contact mode

            btnAddNewGroupCall.setVisibility(View.GONE);
            btnAddNewContact.setVisibility(View.GONE);
            btnDialNumber.setVisibility(View.GONE);
            vgInviteFriend.setVisibility(View.VISIBLE);
            btnAddNewChannel.setVisibility(View.GONE);
            btnAddNewGroup.setVisibility(View.GONE);
            btnAddSecretChat.setVisibility(View.GONE);
            mLayoutMultiSelected.setVisibility(View.GONE);

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
        G.handler.post(() -> G.fragmentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE));
    }

    private void showProgress() {
        G.handler.post(() -> G.fragmentActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Contacts.getContact = false;
        hideProgress();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (realm != null && !realm.isClosed()) {
            realm.close();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

    }

    @Override
    public void onContactInfo(ProtoGlobal.RegisteredUser user) {
        RealmContacts newContact = getRealm().where(RealmContacts.class).equalTo(RealmContactsFields.ID, user.getId()).findFirst();

        RealmResults<RealmContacts> contacts = getRealm()
                .where(RealmContacts.class).limit(ContactManager.CONTACT_LIMIT)
                .sort(RealmContactsFields.DISPLAY_NAME).findAll();
        if (newContact != null)
            for (int i = 0; i < contacts.size(); i++) {
                if (newContact.getId() == contacts.get(i).getId())
                    contactListAdapter.insertContact(newContact, i);
            }
    }

    @Override
    public void onTimeOut() {

    }

    @Override
    public void onContactDelete() {

    }

    @Override
    public void onError(int majorCode, int minorCode) {

    }

//    @Override
//    public void onPhoneContact(final ArrayList<StructListOfContact> contacts, final boolean isEnd) {
//        new AddAsync(contacts, isEnd).execute();
//    }

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
                mTxtSelectedCount.setText(selectedList.size() + " " + G.context.getResources().getString(R.string.item_selected));
            } else {
                mTxtSelectedCount.setText(selectedList.size() + " " + G.context.getResources().getString(R.string.item_selected));
            }
            refreshAdapter(position, false);
        }
    }

    public void refreshAdapter(int position, boolean isAllRefresh) {
        contactListAdapter.usersList = results;
        if (isAllRefresh) {
            contactListAdapter.notifyDataSetChanged();
        } else {
            contactListAdapter.notifyItemChanged(position);
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
        List<String> items = new ArrayList<>();
        items.add(getString(R.string.sync_contact));
        items.add(getString(R.string.mark_as_several));

        new BottomSheetFragment().setData(items, -1, new BottomSheetItemClickCallback() {
            @Override
            public void onClick(int position) {
                if (position == 0) {
                    ContactUtils.syncContacts();
                } else {
                    if (!isMultiSelect) {
                        isMultiSelect = true;
                        refreshAdapter(0, true);

                        if (!mLayoutMultiSelected.isShown()) {
                            setPageShowingMode(4);
                        }
                        isLongClick = true;

                    }
                }
            }
        }).show(getFragmentManager(), "contactToolbar");
    }

    @Override
    public void onSearchClickListener(View view) {

        if (!isToolbarInEditMode) {
            isToolbarInEditMode = true;
            openKeyBoard();
        }

    }

    @Override
    public void onSearchTextChangeListener(View view, String text) {

        fastItemAdapter.filter(text.toLowerCase());

        if (text.length() > 0) {
            results = getRealm().where(RealmContacts.class).contains(RealmContactsFields.DISPLAY_NAME, text, Case.INSENSITIVE).findAll().sort(RealmContactsFields.DISPLAY_NAME);
        } else {
            results = getRealm().where(RealmContacts.class).findAll().sort(RealmContactsFields.DISPLAY_NAME);
        }

        realmRecyclerView.setAdapter(new ContactListAdapter(results));
    }

    @Override //btn add
    public void onRightIconClickListener(View view) {
        if (mActionMode != null) {
            mActionMode.finish();
        }
        if (getActivity() != null) {
            FragmentAddContact fragment = FragmentAddContact.newInstance();
            Bundle bundle = new Bundle();
            bundle.putString("TITLE", G.context.getString(R.string.fac_Add_Contact));
            fragment.setArguments(bundle);
            new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setReplace(false).load();
        }
    }

    @Override
    public void onBtnClearSearchClickListener(View view) {
        if (edtSearch.getText().length() > 0) {
            edtSearch.setText("");
        } else {
            isToolbarInEditMode = false;
            closeKeyboard(getView());
        }
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

    public class ContactListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private List<RealmContacts> usersList;
        private int count;
        private LayoutInflater inflater;


        ContactListAdapter(List<RealmContacts> contacts) {
            inflater = LayoutInflater.from(G.context);
            count = contacts.size();
            usersList = contacts;
        }

        void addUserList(List<RealmContacts> usersList) {
            this.usersList.addAll(count, usersList);
            notifyItemChanged(count, count + ContactManager.LOAD_AVG);
            count = count + ContactManager.LOAD_AVG;
            if (count > this.usersList.size())
                endPage = true;
        }

        public void remove(long num) {
            for (int i = 0; i < usersList.size(); i++) {
                if (usersList.get(i).getPhone() == num) {
                    usersList.remove(i);
                    notifyItemRemoved(i);
                    G.handler.postDelayed(this::notifyDataSetChanged, 1000);
                }
            }
        }

        void insertContact(RealmContacts realmContacts, int i) {
            usersList.add(i, realmContacts);
            notifyItemInserted(i);
        }

        @NotNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup viewGroup, int i) {

            View v;
            if (mPageMode == 2) {//call mode
                v = inflater.inflate(R.layout.item_contact_call, viewGroup, false);
            } else { //new chat and contact
                v = inflater.inflate(R.layout.item_contact_chat, viewGroup, false);
            }

            if (mPageMode == 2)
                return new ViewHolderCall(v);
            else
                return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NotNull final RecyclerView.ViewHolder holder, int i) {

            if (holder instanceof ViewHolder) {

                ViewHolder viewHolder = (ViewHolder) holder;

                final RealmContacts contact = viewHolder.realmContacts = usersList.get(i);
                if (contact == null) {
                    return;
                }

                if (G.isDarkTheme) {
                    viewHolder.subtitle.setTextColor(context.getResources().getColor(R.color.white));
                    viewHolder.title.setTextColor(context.getResources().getColor(R.color.white));
                } else {
                    viewHolder.title.setTextColor(context.getResources().getColor(R.color.black));
                    viewHolder.subtitle.setTextColor(context.getResources().getColor(R.color.black));
                }

                viewHolder.title.setText(contact.getDisplay_name());
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

            } else if (holder instanceof ViewHolderCall) {


                ViewHolderCall viewHolder = (ViewHolderCall) holder;

                final RealmContacts contact = viewHolder.realmContacts = usersList.get(i);
                if (contact == null) {
                    return;
                }

//                String header = contact.getDisplay_name();

//                lastHeader = header;

                if (G.isDarkTheme) {
                    viewHolder.subtitle.setTextColor(context.getResources().getColor(R.color.white));
                    viewHolder.btnVoiceCall.setTextColor(context.getResources().getColor(R.color.white));
                    viewHolder.btnVideoCall.setTextColor(context.getResources().getColor(R.color.white));
                    viewHolder.title.setTextColor(context.getResources().getColor(R.color.white));
                } else {
                    viewHolder.title.setTextColor(context.getResources().getColor(R.color.black));
                    viewHolder.subtitle.setTextColor(context.getResources().getColor(R.color.black));
                    viewHolder.btnVideoCall.setTextColor(context.getResources().getColor(R.color.black));
                    viewHolder.btnVoiceCall.setTextColor(context.getResources().getColor(R.color.black));
                }

                viewHolder.title.setText(contact.getDisplay_name());
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

            }
        }

        @Override
        public int getItemCount() {
            return usersList.size();
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

               /* root.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (onLongClickRecyclerView != null) {
                            onLongClickRecyclerView.onClick(v, getAdapterPosition());
                            isLongClick = true;

                        }

                        return false;
                    }
                });*/


                root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (!isMultiSelect) {
                            if (isCallAction) {
                                //  G.fragmentActivity.getSupportFragmentManager().popBackStack();


                                long userId = realmContacts.getId();
                                if (userId != 134 && G.userId != userId) {


                                    new MaterialDialog.Builder(G.fragmentActivity).items(R.array.calls).itemsCallback(new MaterialDialog.ListCallback() {
                                        @Override
                                        public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

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

                                HelperPublicMethod.goToChatRoom(realmContacts.getId(), new HelperPublicMethod.OnComplete() {
                                    @Override
                                    public void complete() {
                                        hideProgress();
                                        popBackStackFragment();
                                    }
                                }, new HelperPublicMethod.OnError() {
                                    @Override
                                    public void error() {
                                        hideProgress();
                                    }
                                });
                            }
                        } else {
                            if (onCliclRecyclerView != null)
                                onCliclRecyclerView.onClick(v, getAdapterPosition());
                        }
                    }
                });
            }
        }

        public class ViewHolderCall extends RecyclerView.ViewHolder {

            private CircleImageView image;
            private TextView title;
            private TextView subtitle;
            private MaterialDesignTextView btnVoiceCall, btnVideoCall;
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
                btnVideoCall = view.findViewById(R.id.tv_itemContactCall_videoCall);


               /* root.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (onLongClickRecyclerView != null) {
                            onLongClickRecyclerView.onClick(v, getAdapterPosition());
                            isLongClick = true;

                        }

                        return false;
                    }
                });*/

                btnVoiceCall.setOnClickListener(v -> {
                    long userId = realmContacts.getId();
                    if (userId != 134 && G.userId != userId) {
                        CallSelectFragment.call(userId, false, ProtoSignalingOffer.SignalingOffer.Type.VOICE_CALLING);
                        popBackStackFragment();
                    }
                });

                btnVideoCall.setOnClickListener(v -> {
                    long userId = realmContacts.getId();
                    if (userId != 134 && G.userId != userId) {
                        CallSelectFragment.call(userId, false, ProtoSignalingOffer.SignalingOffer.Type.VIDEO_CALLING);
                        popBackStackFragment();
                    }
                });

                root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (!isMultiSelect) {
                            if (isCallAction) {
                                //  G.fragmentActivity.getSupportFragmentManager().popBackStack();


                                long userId = realmContacts.getId();
                                if (userId != 134 && G.userId != userId) {


                                    new MaterialDialog.Builder(G.fragmentActivity).items(R.array.calls).itemsCallback(new MaterialDialog.ListCallback() {
                                        @Override
                                        public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

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

                                HelperPublicMethod.goToChatRoom(realmContacts.getId(), new HelperPublicMethod.OnComplete() {
                                    @Override
                                    public void complete() {
                                        hideProgress();
                                        popBackStackFragment();
                                    }
                                }, new HelperPublicMethod.OnError() {
                                    @Override
                                    public void error() {
                                        hideProgress();
                                    }
                                });
                            }
                        } else {
                            if (onCliclRecyclerView != null)
                                onCliclRecyclerView.onClick(v, getAdapterPosition());
                        }
                    }
                });
            }
        }
    }

    /**
     * ************************************* show all phone contact *************************************
     */

    private class AddAsync extends AsyncTask<Void, Void, ArrayList<StructListOfContact>> {

        private ArrayList<StructListOfContact> contacts;
        private boolean isEnd;

        public AddAsync(ArrayList<StructListOfContact> contacts, boolean isEnd) {
            this.contacts = contacts;
            this.isEnd = isEnd;
        }

        @Override
        protected ArrayList<StructListOfContact> doInBackground(Void... params) {
            for (int i = 0; i < contacts.size(); i++) {

                String s = contacts.get(i).getPhone();
                s = s.replaceAll("\\A0|\\+|\\-?", "");
                if (s.contains(" "))
                    s = s.replace(" ", "");
                if (!s.startsWith("98"))
                    s = "98" + s;
                contacts.get(i).setPhone(s);
            }
            Realm realm = Realm.getDefaultInstance();
            RealmResults<RealmContacts> mList = realm.where(RealmContacts.class).findAll().sort(RealmContactsFields.DISPLAY_NAME);


            ArrayList<StructListOfContact> slc = new ArrayList();

            for (int i = 0; i < contacts.size(); i++) {
                boolean helpIndex = false;
                for (int j = 0; j < mList.size(); j++) {
                    if (contacts.get(i).getPhone().equalsIgnoreCase(String.valueOf(mList.get(j).getPhone()))) {
                        helpIndex = true;
                        break;
                    }
                }
                if (!helpIndex) {
                    slc.add(contacts.get(i));
                }
            }
            realm.close();

            return slc;
        }

        @Override
        protected void onPostExecute(ArrayList<StructListOfContact> slc) {
            phoneContactsList.addAll(slc);

            adapterListContact.notifyDataSetChanged();
            if (isEnd) {
                prgWaitingLiadList.setVisibility(View.GONE);
            }
            super.onPostExecute(slc);
        }
    }
}


