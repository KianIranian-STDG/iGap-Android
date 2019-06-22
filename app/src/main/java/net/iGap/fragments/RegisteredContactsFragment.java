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
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.decoration.DashDivider;
import net.iGap.dialog.BottomSheetItemClickCallback;
import net.iGap.dialog.bottomsheet.BottomSheetFragment;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperPermission;
import net.iGap.helper.HelperPublicMethod;
import net.iGap.helper.HelperToolbar;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.helper.avatar.ParamWithAvatarType;
import net.iGap.interfaces.OnGetPermission;
import net.iGap.interfaces.OnPhoneContact;
import net.iGap.interfaces.OnUserContactDelete;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.module.AppUtils;
import net.iGap.module.ContactUtils;
import net.iGap.module.Contacts;
import net.iGap.module.CustomTextViewMedium;
import net.iGap.module.EmojiEditTextE;
import net.iGap.module.LoginActions;
import net.iGap.module.MEditText;
import net.iGap.module.MaterialDesignTextView;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.structs.StructListOfContact;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoSignalingOffer;
import net.iGap.realm.RealmContacts;
import net.iGap.realm.RealmContactsFields;
import net.iGap.request.RequestUserContactsDelete;
import net.iGap.request.RequestUserContactsEdit;
import net.iGap.request.RequestUserContactsGetList;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;

import static android.content.Context.MODE_PRIVATE;

public class RegisteredContactsFragment extends BaseFragment implements ToolbarListener, OnUserContactDelete, OnPhoneContact {

    private static boolean getPermission = true;
    public onClickRecyclerView onCliclRecyclerView;
    public onLongClickRecyclerView onLongClickRecyclerView;
    public boolean isLongClick = false;
    public ArrayList<StructListOfContact> phoneContactsList = new ArrayList<>();
    protected ArrayMap<Long, Boolean> selectedList = new ArrayMap<>();
    /*StickyRecyclerHeadersDecoration decoration;*/
    RealmResults<RealmContacts> results;
    boolean isMultiSelect = false;
    AdapterListContact adapterListContact;
    private TextView menu_txt_titleToolbar;
    private ViewGroup vgInviteFriend;
    private RecyclerView realmRecyclerView, rcvListContact;
    private SharedPreferences sharedPreferences;
    private Realm realm;
    private ProgressBar prgWaiting;
    private ProgressBar prgWaitingLoadContact;
    private EditText edtSearch;
    private boolean isCallAction = false;
    private FastItemAdapter fastItemAdapter;
    private ProgressBar prgWaitingLiadList;
    private NestedScrollView nestedScrollView;
    private ActionMode mActionMode;
    private ContactListAdapter contactListAdapter;
    private AppBarLayout toolbar;
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

    public static RegisteredContactsFragment newInstance(boolean isBackSwipable) {
        RegisteredContactsFragment registeredContactsFragment = new RegisteredContactsFragment();
        //Bundle bundle = new Bundle();
        //bundle.putBoolean("isBackSwipable", isBackSwipable);
        //registeredContactsFragment.setArguments(bundle);
        return registeredContactsFragment;
    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu_multi_select, menu);
            toolbar.setVisibility(View.GONE);

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete:
                    new MaterialDialog.Builder(G.fragmentActivity).title(R.string.to_delete_contact).content(R.string.delete_text).positiveText(R.string.B_ok).onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                            for (ArrayMap.Entry<Long, Boolean> entry : selectedList.entrySet()) {
                                new RequestUserContactsDelete().contactsDelete("" + entry.getKey());
                            }

                            mActionMode.finish();
                            refreshAdapter(0, true);

                        }
                    }).negativeText(R.string.B_cancel).show();

                    return true;
                default:
                    return false;
            }
        }


        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
            isMultiSelect = false;
            isLongClick = false;
            selectedList.clear();
            refreshAdapter(0, true);
            toolbar.setVisibility(View.VISIBLE);
        }
    };

    public static RegisteredContactsFragment newInstance() {
        return newInstance(true);
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
            G.handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    init();
                }
            }, 800);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        boolean isSwipe = false;
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
        prgWaiting = view.findViewById(R.id.prgWaiting_addContact);
        AppUtils.setProgresColler(prgWaiting);

        prgWaiting.setVisibility(View.GONE);

        LinearLayout toolbarLayout = view.findViewById(R.id.frg_contact_ll_toolbar_layout);

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


        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                init();
            }
        }, 100);
    }

    private void init() {
        if (view == null) {
            return;
        }

        if (!getUserVisibleHint()) {
            if (!isInit) {
            }
            return;
        }
        isInit = true;

        if (isContact){
            G.onPhoneContact = this;
            Contacts.localPhoneContactId = 0;
            Contacts.getContact = true;

        }
        //sharedPreferences = G.fragmentActivity.getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);

        /**
         * not import contact in every enter to this page
         * for this purpose i comment this code. but not cleared.
         */

        nestedScrollView = view.findViewById(R.id.nestedScrollContact);

        TextView txtNonUser = view.findViewById(R.id.txtNon_User);
        prgWaitingLiadList = view.findViewById(R.id.prgWaiting_loadList);
        prgWaitingLoadContact = view.findViewById(R.id.prgWaitingLoadContact);


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

            if (title.equals("New Chat")) {
                title = G.context.getString(R.string.New_Chat);
                mPageMode = 0;
            } else if (title.equals("Contacts")) {
                title = G.fragmentActivity.getString(R.string.contacts);
                mPageMode = 1;
            } else if (title.equals("call")) {
                title = G.context.getString(R.string.call_with);
                mPageMode = 2;

                //visible add buttons
                btnAddNewGroupCall.setVisibility(View.VISIBLE);
                btnAddNewContact.setVisibility(View.VISIBLE);
                btnDialNumber.setVisibility(View.VISIBLE);
                vgInviteFriend.setVisibility(View.GONE);

                mHelperToolbar.getRightButton().setVisibility(View.GONE);

            } else if (title.equals("ADD")) {
                title = G.context.getString(R.string.New_Chat);
                mPageMode = 3;

                //visible add buttons
                btnAddNewChannel.setVisibility(View.VISIBLE);
                btnAddNewGroup.setVisibility(View.VISIBLE);
                //btnAddSecretChat.setVisibility(View.VISIBLE);
                vgInviteFriend.setVisibility(View.GONE);
            }
        }


        toolbar = view.findViewById(R.id.fc_layot_title);
        menu_txt_titleToolbar = (TextView) view.findViewById(R.id.menu_txt_titleToolbar);
        menu_txt_titleToolbar.setText(title);

        final RippleView txtClose = (RippleView) view.findViewById(R.id.menu_ripple_close);
        final TextView txtSearch = (TextView) view.findViewById(R.id.menu_btn_search);
        final TextView txtSync = (TextView) view.findViewById(R.id.menu_btn_sync);
        txtSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtClose.setVisibility(View.VISIBLE);
                edtSearch.setVisibility(View.VISIBLE);
                edtSearch.setFocusable(true);
                menu_txt_titleToolbar.setVisibility(View.GONE);
                txtSearch.setVisibility(View.GONE);
                txtSync.setVisibility(View.GONE);
            }
        });

        txtClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtSearch.getText().length() > 0) {
                    edtSearch.setText("");
                } else {
                    txtClose.setVisibility(View.GONE);
                    edtSearch.setVisibility(View.GONE);
                    mHelperToolbar.getTextViewSearch().setVisibility(View.VISIBLE);
                    menu_txt_titleToolbar.setVisibility(View.VISIBLE);
                    txtSearch.setVisibility(View.VISIBLE);
                    txtSync.setVisibility(View.VISIBLE);
                    InputMethodManager imm = (InputMethodManager) G.fragmentActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        });

        vgInviteFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Hey Join iGap : https://www.igap.net I'm waiting for you!");
                sendIntent.setType("text/plain");
                Intent openInChooser = Intent.createChooser(sendIntent, "Open in...");
                getActivity().startActivity(openInChooser);
            }
        });

        RippleView rippleMenu = (RippleView) view.findViewById(R.id.menu_ripple_txtBack);
        rippleMenu.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                G.fragmentActivity.onBackPressed();
                InputMethodManager imm = (InputMethodManager) G.fragmentActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(rippleView.getWindowToken(), 0);
            }
        });

        RippleView rippleSync = (RippleView) view.findViewById(R.id.menu_sync);
        rippleSync.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                ContactUtils.syncContacts();
            }
        });

        realmRecyclerView = view.findViewById(R.id.recycler_view);
        realmRecyclerView.setItemViewCacheSize(1000);
        realmRecyclerView.setItemAnimator(null);
        realmRecyclerView.setLayoutManager(new LinearLayoutManager(realmRecyclerView.getContext()));
        realmRecyclerView.setNestedScrollingEnabled(false);

        results = getRealm().where(RealmContacts.class).findAll().sort(RealmContactsFields.DISPLAY_NAME);

        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                contactListAdapter = new ContactListAdapter(results);
                realmRecyclerView.setAdapter(contactListAdapter);
                prgWaitingLoadContact.setVisibility(View.GONE);
                realmRecyclerView.setVisibility(View.VISIBLE);

            }
        }, 500);


        //fastAdapter
        //mAdapter = new ContactListAdapterA();
        //for (RealmContacts contact : results) {
        //    mAdapter.add(new ContactItem().setInfo(contact).withIdentifier(contact.getId()));
        //}
        //realmRecyclerView.setAdapter(mAdapter);
        //mAdapter.withOnClickListener(new FastAdapter.OnClickListener() {
        //    @Override
        //    public boolean onClick(View v, IAdapter adapter, IItem item, int position) {
        //        if (isCallAction) {
        //            popBackStackFragment();
        //            long userId = ((ContactItem) item).contact.getId();
        //            if (userId != 134 && G.userId != userId) {
        //                FragmentCall.call(userId, false);
        //            }
        //        } else {
        //            showProgress();
        //            HelperPublicMethod.goToChatRoom(((ContactItem) item).contact.getId(), new HelperPublicMethod.OnComplete() {
        //                @Override
        //                public void complete() {
        //                    hideProgress();
        //                    popBackStackFragment();
        //                }
        //            }, new HelperPublicMethod.OnError() {
        //                @Override
        //                public void error() {
        //                    hideProgress();
        //                }
        //            });
        //        }
        //        return true;
        //    }
        //});

        realmRecyclerView.addItemDecoration(new DashDivider.Builder(getContext()).dashGap((int) getResources().getDimension(R.dimen.dp6))
                .dashLength((int) getResources().getDimension(R.dimen.dp2))
                .orientation(LinearLayoutManager.HORIZONTAL)
                .dashThickness((int) getResources().getDimension(R.dimen.dp2))
                .color(ContextCompat.getColor(getContext(), R.color.gray))
                .textColor(ContextCompat.getColor(getContext(), R.color.light_gray))
                .textSize(getResources().getDimension(R.dimen.dp16))
                .build());

//        realmRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(G.fragmentActivity, realmRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//                if (isMultiSelect) {
//                    multi_select(position);
//                }
//            }
//
//
//            @Override
//            public void onItemLongClick(View view, int position) {
//                if (!isMultiSelect) {
//                    isMultiSelect = true;
//                    refreshAdapter(0, true);
//                    if (mActionMode == null) {
//                        mActionMode = G.fragmentActivity.startActionMode(mActionModeCallback);
//                    }
//                }
//                multi_select(position);
//            }
//        }));

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

                if (isContact){
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
                }else {
                    prgWaitingLiadList.setVisibility(View.GONE);

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (isContact){

            rcvListContact.setLayoutManager(new LinearLayoutManager(getContext()));
            rcvListContact.setItemAnimator(new DefaultItemAnimator());
            adapterListContact = new AdapterListContact(phoneContactsList);
            rcvListContact.setAdapter(adapterListContact);
            rcvListContact.setNestedScrollingEnabled(false);

        }else {
            rcvListContact.setVisibility(View.GONE);
            prgWaitingLiadList.setVisibility(View.GONE);
            txtNonUser.setVisibility(View.GONE);
        }

        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (Contacts.isEndLocal) {
                    return;
                }
                if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                    new Contacts.FetchContactForClient().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
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

        btnAddNewGroupCall.setOnClickListener(v -> {
        });

        btnAddNewContact.setOnClickListener(this::onRightIconClickListener);

        mBtnDeleteSelected.setOnClickListener(v -> {

            if (selectedList.size() == 0 ){
                Toast.makeText(context, getString(R.string.no_item_selected), Toast.LENGTH_SHORT).show();
                return;
            }

            new MaterialDialog.Builder(G.fragmentActivity).title(R.string.to_delete_contact).content(R.string.delete_text).positiveText(R.string.B_ok).onPositive(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                    for (ArrayMap.Entry<Long, Boolean> entry : selectedList.entrySet()) {
                        new RequestUserContactsDelete().contactsDelete("" + entry.getKey());
                    }

                    setPageShowingMode(mPageMode);
                    isMultiSelect = false;
                    isLongClick = false;
                    selectedList.clear();
                    refreshAdapter(0, true);

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
            toolbar.setVisibility(View.VISIBLE);
        });
    }

    private void setPageShowingMode(int mode) {

        //set once when mode changes between contact and multi select first set in all mode then if
        //that was multi select remove it and show at top in selected mode
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) layoutAppBarContainer.getLayoutParams();
        params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
                | AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED);
        layoutAppBarContainer.setLayoutParams(params);

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

            //remove snap mode from multi select and always show layout at top
            params.setScrollFlags(0);
            layoutAppBarContainer.setLayoutParams(params);
        }
    }

    private void hideProgress() {

        G.handler.post(new Runnable() {
            @Override
            public void run() {
                G.fragmentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                prgWaiting.setVisibility(View.GONE);
            }
        });
    }

    private void showProgress() {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                G.fragmentActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                prgWaiting.setVisibility(View.VISIBLE);
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
    public void onDestroy() {
        super.onDestroy();
        if (realm != null && !realm.isClosed()) {
            realm.close();
        }
        G.onUserContactdelete = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

    }

    @Override
    public void onContactDelete() {

    }

    @Override
    public void onError(int majorCode, int minorCode) {

    }

    @Override
    public void onPhoneContact(final ArrayList<StructListOfContact> contacts, final boolean isEnd) {
        new AddAsync(contacts, isEnd).execute();
    }

    private void dialogEditContact(String header, long phone, final long userId) {

        final LinearLayout layoutNickname = new LinearLayout(G.fragmentActivity);
        layoutNickname.setOrientation(LinearLayout.VERTICAL);

        String[] splitNickname = header.split(" ");
        String firsName = "";
        String lastName = "";
        StringBuilder stringBuilder = null;
        if (splitNickname.length > 1) {

            lastName = splitNickname[splitNickname.length - 1];
            stringBuilder = new StringBuilder();
            for (int i = 0; i < splitNickname.length - 1; i++) {

                stringBuilder.append(splitNickname[i]).append(" ");
            }
            firsName = stringBuilder.toString();
        } else {
            firsName = splitNickname[0];
        }
        final View viewFirstName = new View(G.fragmentActivity);
        viewFirstName.setBackgroundColor(G.context.getResources().getColor(R.color.line_edit_text));

        LinearLayout.LayoutParams viewParams = new AppBarLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2);

        TextInputLayout inputFirstName = new TextInputLayout(G.fragmentActivity);
        final EmojiEditTextE edtFirstName = new EmojiEditTextE(G.fragmentActivity);
        edtFirstName.setHint(R.string.first_name);
        edtFirstName.setImeOptions(EditorInfo.IME_ACTION_DONE | EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        edtFirstName.setTypeface(G.typeface_IRANSansMobile);
        edtFirstName.setText(firsName);
        edtFirstName.setTextSize(TypedValue.COMPLEX_UNIT_PX, G.context.getResources().getDimension(R.dimen.dp14));
        edtFirstName.setTextColor(G.context.getResources().getColor(R.color.text_edit_text));
        edtFirstName.setHintTextColor(G.context.getResources().getColor(R.color.hint_edit_text));
        edtFirstName.setPadding(0, 8, 0, 8);
        edtFirstName.setSingleLine(true);
        inputFirstName.addView(edtFirstName);
        inputFirstName.addView(viewFirstName, viewParams);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            edtFirstName.setBackground(G.context.getResources().getDrawable(android.R.color.transparent));
        }

        final View viewLastName = new View(G.fragmentActivity);
        viewLastName.setBackgroundColor(G.context.getResources().getColor(R.color.line_edit_text));

        TextInputLayout inputLastName = new TextInputLayout(G.fragmentActivity);
        final MEditText edtLastName = new MEditText(G.fragmentActivity);
        edtLastName.setHint(R.string.last_name);
        edtLastName.setTypeface(G.typeface_IRANSansMobile);
        edtLastName.setText(lastName);
        edtLastName.setTextSize(TypedValue.COMPLEX_UNIT_PX, G.context.getResources().getDimension(R.dimen.dp14));
        edtLastName.setTextColor(G.context.getResources().getColor(R.color.text_edit_text));
        edtLastName.setHintTextColor(G.context.getResources().getColor(R.color.hint_edit_text));
        edtLastName.setPadding(0, 8, 0, 8);
        edtLastName.setSingleLine(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            edtLastName.setBackground(G.context.getResources().getDrawable(android.R.color.transparent));
        }
        inputLastName.addView(edtLastName);
        inputLastName.addView(viewLastName, viewParams);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, 0, 15);
        LinearLayout.LayoutParams lastNameLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lastNameLayoutParams.setMargins(0, 15, 0, 10);

        layoutNickname.addView(inputFirstName, layoutParams);
        layoutNickname.addView(inputLastName, lastNameLayoutParams);

        final MaterialDialog dialog = new MaterialDialog.Builder(G.fragmentActivity).title(G.fragmentActivity.getResources().getString(R.string.pu_nikname_profileUser))
                .positiveText(G.fragmentActivity.getResources().getString(R.string.B_ok))
                .customView(layoutNickname, true)
                .widgetColor(Color.parseColor(G.appBarColor))
                .negativeText(G.fragmentActivity.getResources().getString(R.string.B_cancel))
                .build();

        final View positive = dialog.getActionButton(DialogAction.POSITIVE);
        positive.setEnabled(false);

        edtFirstName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    viewFirstName.setBackgroundColor(Color.parseColor(G.appBarColor));
                } else {
                    viewFirstName.setBackgroundColor(G.context.getResources().getColor(R.color.line_edit_text));
                }
            }
        });

        edtLastName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    viewLastName.setBackgroundColor(Color.parseColor(G.appBarColor));
                } else {
                    viewLastName.setBackgroundColor(G.context.getResources().getColor(R.color.line_edit_text));
                }
            }
        });

        final String finalFirsName = firsName;
        edtFirstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (!edtFirstName.getText().toString().equals(finalFirsName)) {
                    positive.setEnabled(true);
                } else {
                    positive.setEnabled(false);
                }
            }
        });

        final String finalLastName = lastName;
        edtLastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!edtLastName.getText().toString().equals(finalLastName)) {
                    positive.setEnabled(true);
                } else {
                    positive.setEnabled(false);
                }
            }
        });

        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String firstName = edtFirstName.getText().toString().trim();
                String lastName = edtLastName.getText().toString().trim();
                new RequestUserContactsEdit().contactsEdit(userId, phone, firstName, lastName);
                dialog.dismiss();
            }
        });

        dialog.show();
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

    public class ContactListAdapter extends RealmRecyclerViewAdapter<RealmContacts, RecyclerView.ViewHolder> {

        String lastHeader = "";
        int count;
        RealmResults<RealmContacts> usersList;
        private boolean isSwipe = false;
        private LayoutInflater inflater;


        ContactListAdapter(RealmResults<RealmContacts> realmResults) {
            super(realmResults, true);
            inflater = LayoutInflater.from(G.context);
            count = realmResults.size();
            usersList = realmResults;
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
            //View v = ViewMaker.getViewRegisteredContacts();

            if (getData() != null && count != getData().size()) {
                count = getData().size();
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

                final RealmContacts contact = viewHolder.realmContacts = getItem(i);
                if (contact == null) {
                    return;
                }

                String header = contact.getDisplay_name();
                lastHeader = header;


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

                final RealmContacts contact = viewHolder.realmContacts = getItem(i);
                if (contact == null) {
                    return;
                }

                String header = contact.getDisplay_name();

                lastHeader = header;

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
    private class StickyHeader implements StickyRecyclerHeadersAdapter {

        RealmResults<RealmContacts> realmResults;
        private ViewGroup mParent;

        StickyHeader(RealmResults<RealmContacts> realmResults) {
            this.realmResults = realmResults;
        }

        @Override
        public long getHeaderId(int position) {
            return realmResults.get(position).getDisplay_name().toUpperCase().charAt(0);
        }

        @Override
        public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
            mParent = parent;
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_header_item, parent, false);
            return new RecyclerView.ViewHolder(view) {
            };
        }

        @Override
        public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {

            CustomTextViewMedium textView = (CustomTextViewMedium) holder.itemView;
            textView.setText(realmResults.get(position).getDisplay_name().toUpperCase().substring(0, 1));

            if (G.isDarkTheme)
                textView.setTextColor(context.getResources().getColor(R.color.white));
            else
                textView.setTextColor(context.getResources().getColor(R.color.black));

        }

        @Override
        public int getItemCount() {
            return realmResults.size();
        }

        private void fixLayoutSize(View view, ViewGroup parent) {
            int widthSpec = View.MeasureSpec.makeMeasureSpec(parent.getWidth(),
                    View.MeasureSpec.EXACTLY);
            int heightSpec = View.MeasureSpec.makeMeasureSpec(parent.getHeight(),
                    View.MeasureSpec.UNSPECIFIED);

            int childWidth = ViewGroup.getChildMeasureSpec(widthSpec,
                    parent.getPaddingLeft() + parent.getPaddingRight(),
                    view.getLayoutParams().width);
            int childHeight = ViewGroup.getChildMeasureSpec(heightSpec,
                    parent.getPaddingTop() + parent.getPaddingBottom(),
                    view.getLayoutParams().height);

            view.measure(childWidth,
                    childHeight);

            view.layout(0,
                    0,
                    view.getMeasuredWidth(),
                    view.getMeasuredHeight());
        }
    }

    public class AdapterListContact extends RecyclerView.Adapter<AdapterListContact.ViewHolder> {

        public String item;
        public String phone;
        ArrayList<StructListOfContact> mPhoneContactList;

        public AdapterListContact(String item, String phone) {
            this.item = item;
            this.phone = phone;
        }

        public AdapterListContact(ArrayList<StructListOfContact> mPhoneContactList) {
            this.mPhoneContactList = mPhoneContactList;

        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(R.layout.adapter_list_cobtact, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            holder.title.setText(mPhoneContactList.get(position).getDisplayName());
            holder.subtitle.setText(mPhoneContactList.get(position).getPhone());


            holder.mRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new MaterialDialog.Builder(G.fragmentActivity)
                            .title(G.fragmentActivity.getResources()
                                    .getString(R.string.igap))
                            .content(G.fragmentActivity.getResources().getString(R.string.invite_friend))
                            .positiveText(G.fragmentActivity.getResources().getString(R.string.ok)).negativeText(G.fragmentActivity.getResources().getString(R.string.cancel))
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                    Intent sendIntent = new Intent();
                                    sendIntent.setAction(Intent.ACTION_SEND);
                                    sendIntent.putExtra("address", phone);
                                    sendIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.invitation_message) + G.userId);
                                    sendIntent.setType("text/plain");
                                    sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    G.context.startActivity(sendIntent);
                                }
                            }).show();


                }
            });


        }


        @Override
        public int getItemCount() {
            return mPhoneContactList.size();
        }

        //The viewHolder used for this item. This viewHolder is always reused by the RecyclerView so scrolling is blazing fast
        protected class ViewHolder extends RecyclerView.ViewHolder {


            private TextView title;
            private TextView subtitle;
            private ViewGroup mRoot;

            public ViewHolder(View view) {
                super(view);

                mRoot = (ViewGroup) view.findViewById(R.id.liContactItem);
                title = (TextView) view.findViewById(R.id.title);
                subtitle = (TextView) view.findViewById(R.id.subtitle);

            }
        }
    }

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


