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
import android.support.v4.util.ArrayMap;
import android.support.v4.widget.NestedScrollView;
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

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperAvatar;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperPermission;
import net.iGap.helper.HelperPublicMethod;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.OnAvatarGet;
import net.iGap.interfaces.OnGetPermission;
import net.iGap.interfaces.OnPhoneContact;
import net.iGap.interfaces.OnUserContactDelete;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.module.AndroidUtils;
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
import net.iGap.proto.ProtoSignalingOffer;
import net.iGap.realm.RealmContacts;
import net.iGap.realm.RealmContactsFields;
import net.iGap.request.RequestUserContactsDelete;
import net.iGap.request.RequestUserContactsEdit;
import net.iGap.request.RequestUserContactsGetList;

import java.io.IOException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;

import static android.content.Context.MODE_PRIVATE;
import static net.iGap.G.context;
import static net.iGap.R.string.contacts;

public class RegisteredContactsFragment extends BaseFragment implements ToolbarListener, OnUserContactDelete, OnPhoneContact {

    private static boolean getPermission = true;
    StickyRecyclerHeadersDecoration decoration;
    RealmResults<RealmContacts> results;
    private TextView menu_txt_titleToolbar;
    private ViewGroup vgInviteFriend, vgRoot;
    private RecyclerView realmRecyclerView, rcvListContact;
    private SharedPreferences sharedPreferences;
    private boolean isImportContactList = false;
    private Realm realm;
    private ProgressBar prgWaiting;
    private ProgressBar prgWaitingLoadContact;
    private EditText edtSearch;
    private boolean isCallAction = false;
    private FastItemAdapter fastItemAdapter;
    private ProgressBar prgWaitingLiadList;
    //private ContactListAdapterA mAdapter;
    private NestedScrollView nestedScrollView;
    private ActionMode mActionMode;
    protected ArrayMap<Long, Boolean> selectedList = new ArrayMap<>();
    boolean isMultiSelect = false;
    private ContactListAdapter contactListAdapter;
    private AppBarLayout toolbar;
    private long index = 2500;
    public onClickRecyclerView onCliclRecyclerView;
    public onLongClickRecyclerView onLongClickRecyclerView;
    AdapterListContact adapterListContact;
    public boolean isLongClick = false;

    public ArrayList<StructListOfContact> phoneContactsList = new ArrayList<>();


    private HelperToolbar mHelperToolbar;
    private boolean isToolbarInEditMode = false ;
    private int mPageMode = 0 ; // 0 = new chat , 1 = contact , 2 = call , 4 = add new
    private LinearLayout btnAddNewChannel , btnAddNewGroup , btnAddSecretChat ;
    private LinearLayout btnAddNewGroupCall, btnAddNewContact , btnDialNumber ;

    public static RegisteredContactsFragment newInstance() {
        return new RegisteredContactsFragment();
    }

    private Realm getRealm() {
        if (realm == null || realm.isClosed()) {
            realm = Realm.getDefaultInstance();
        }

        return realm;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return attachToSwipeBack(inflater.inflate(R.layout.fragment_contacts, container, false));
    }

    @Override
    public void onViewCreated(View view, final @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        G.onPhoneContact = this;
        Contacts.localPhoneContactId = 0;
        Contacts.getContact = true;

        sharedPreferences = G.fragmentActivity.getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);

        /**
         * not import contact in every enter to this page
         * for this purpose i comment this code. but not cleared.
         */
        //isImportContactList = sharedPreferences.getBoolean(SHP_SETTING.KEY_GET_CONTACT_IN_FRAGMENT, false);
        //if (!isImportContactList) {
        //    try {
        //        HelperPermision.getContactPermision(G.fragmentActivity, new OnGetPermission() {
        //            @Override
        //            public void Allow() throws IOException {
        //                importContactList();
        //            }
        //
        //            @Override
        //            public void deny() {
        //
        //            }
        //        });
        //    } catch (IOException e) {
        //        e.printStackTrace();
        //    }
        //    SharedPreferences.Editor editor = sharedPreferences.edit();
        //    editor.putBoolean(SHP_SETTING.KEY_GET_CONTACT_IN_FRAGMENT, true);
        //    editor.apply();
        //}

        //set interface for get callback here

        nestedScrollView = view.findViewById(R.id.nestedScrollContact);

        TextView txtNonUser = (TextView) view.findViewById(R.id.txtNon_User);
//        txtNonUser.setTextColor(Color.parseColor(G.appBarColor));
        prgWaitingLiadList = (ProgressBar) view.findViewById(R.id.prgWaiting_loadList);
        prgWaitingLoadContact = (ProgressBar) view.findViewById(R.id.prgWaitingLoadContact);

        prgWaiting = (ProgressBar) view.findViewById(R.id.prgWaiting_addContact);
        AppUtils.setProgresColler(prgWaiting);

        prgWaiting.setVisibility(View.GONE);
        vgInviteFriend =  view.findViewById(R.id.menu_layout_inviteFriend);
        vgRoot = (ViewGroup) view.findViewById(R.id.menu_parent_layout);
        vgRoot.setBackgroundColor(context.getResources().getColor(R.color.white));

        LinearLayout toolbarLayout = view.findViewById(R.id.frg_contact_ll_toolbar_layout);

        mHelperToolbar = HelperToolbar.create()
                .setContext(context)
                .setLeftIcon(R.drawable.ic_edit_toolbar)
                .setRightIcons(R.drawable.ic_add_toolbar)
                .setSearchBoxShown(true)
                .setLogoShown(true)
                .setListener(this);

        toolbarLayout.addView(mHelperToolbar.getView());

        btnAddNewGroupCall = view.findViewById(R.id.menu_layout_new_group_call);
        btnAddNewContact = view.findViewById(R.id.menu_layout_add_new_contact);
        btnDialNumber = view.findViewById(R.id.menu_layout_btn_dial_number);

        btnAddSecretChat = view.findViewById(R.id.menu_layout_btn_secret_chat);
        btnAddNewGroup = view.findViewById(R.id.menu_layout_add_new_group);
        btnAddNewChannel = view.findViewById(R.id.menu_layout_add_new_channel);
        edtSearch = mHelperToolbar.getEditTextSearch();

        Bundle bundle = this.getArguments();
        String title = null;
        if (bundle != null) {
            title = bundle.getString("TITLE");
            isCallAction = bundle.getBoolean("ACTION");
        }

        if (title != null) {

            if (title.equals("New Chat")) {
                title = G.context.getString(R.string.New_Chat);
                mPageMode = 0 ;
            } else if (title.equals("Contacts")) {
                title = G.context.getString(contacts);
                mPageMode = 1 ;
            } else if (title.equals("call")) {
                title = G.context.getString(R.string.call_with);
                mPageMode = 2 ;

                //visible add buttons
                btnAddNewGroupCall.setVisibility(View.VISIBLE);
                btnAddNewContact.setVisibility(View.VISIBLE);
                btnDialNumber.setVisibility(View.VISIBLE);
                vgInviteFriend.setVisibility(View.GONE);

                mHelperToolbar.getRightButton().setVisibility(View.GONE);

            } else if (title.equals("ADD")) {
                title = G.context.getString(R.string.New_Chat);
                mPageMode = 3 ;

                //visible add buttons
                btnAddNewChannel.setVisibility(View.VISIBLE);
                btnAddNewGroup.setVisibility(View.VISIBLE);
                btnAddSecretChat.setVisibility(View.VISIBLE);
                vgInviteFriend.setVisibility(View.GONE);
            }
        }



        toolbar = view.findViewById(R.id.fc_layot_title);
        //toolbar.setBackgroundColor(Color.parseColor(G.appBarColor));
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

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                fastItemAdapter.filter(s.toString().toLowerCase());

                if (s.length() > 0) {
                    results = getRealm().where(RealmContacts.class).contains(RealmContactsFields.DISPLAY_NAME, s.toString(), Case.INSENSITIVE).findAll().sort(RealmContactsFields.DISPLAY_NAME);
                } else {
                    results = getRealm().where(RealmContacts.class).findAll().sort(RealmContactsFields.DISPLAY_NAME);
                }

                realmRecyclerView.setAdapter(new ContactListAdapter(results));

                // fastAdapter
                //mAdapter.clear();
                //for (RealmContacts contact : results) {
                //    mAdapter.add(new ContactItem().setInfo(contact).withIdentifier(contact.getId()));
                //}
                //realmRecyclerView.setAdapter(mAdapter);

                realmRecyclerView.removeItemDecoration(decoration);
                decoration = new StickyRecyclerHeadersDecoration(new StickyHeader(results));
                realmRecyclerView.addItemDecoration(decoration);
            }

            @Override
            public void afterTextChanged(Editable s) {

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

        realmRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        realmRecyclerView.setItemViewCacheSize(1000);
        realmRecyclerView.setItemAnimator(null);
        realmRecyclerView.setLayoutManager(new LinearLayoutManager(G.fragmentActivity));
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

        StickyHeader stickyHeader = new StickyHeader(results);
        decoration = new StickyRecyclerHeadersDecoration(stickyHeader);
        realmRecyclerView.addItemDecoration(decoration);

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
                    if (mActionMode == null) {
                        mActionMode = G.fragmentActivity.startActionMode(mActionModeCallback);
                    }
                }
                multi_select(position);
            }
        };

        rcvListContact = (RecyclerView) view.findViewById(R.id.rcv_friends_to_invite);
        fastItemAdapter = new FastItemAdapter();

        try {
            if (getPermission) {
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
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        rcvListContact.setLayoutManager(new LinearLayoutManager(getContext()));
        rcvListContact.setItemAnimator(new DefaultItemAnimator());
        adapterListContact = new AdapterListContact(phoneContactsList);
        rcvListContact.setAdapter(adapterListContact);
        rcvListContact.setNestedScrollingEnabled(false);

/*
        fastItemAdapter.getItemFilter().withFilterPredicate(new IItemAdapter.Predicate<AdapterListContact>() {
            @Override
            public boolean filter(AdapterListContact item, CharSequence constraint) {
                return item.item.toLowerCase().startsWith(String.valueOf(constraint));
            }
        });
*/

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

        btnAddNewChannel.setOnClickListener( v -> {

            FragmentNewGroup fragment = FragmentNewGroup.newInstance();
            Bundle bundle_ = new Bundle();
            bundle_.putString("TYPE", "NewChanel");
            fragment.setArguments(bundle_);
            new HelperFragment(fragment).setReplace(false).load();

        });

        btnAddNewGroup.setOnClickListener( v -> {

            /*try {
                G.fragmentActivity.onBackPressed();
            } catch (Exception e) {
                e.getStackTrace();
            }*/

            FragmentNewGroup fragment = FragmentNewGroup.newInstance();
            Bundle bundle_ = new Bundle();
            bundle_.putString("TYPE", "NewGroup");
            fragment.setArguments(bundle_);
            new HelperFragment(fragment).setReplace(false).load();



        });

        btnAddSecretChat.setOnClickListener( v -> {

        });

        btnDialNumber.setOnClickListener(v -> {});

        btnAddNewGroupCall.setOnClickListener(v -> {});

        btnAddNewContact.setOnClickListener(this::onRightIconClickListener);
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
        //Override onSaveInstanceState method and comment 'super' from avoid from "Can not perform this action after onSaveInstanceState" error
        //super.onSaveInstanceState(outState); //
    }

    @Override
    public void onContactDelete() {
        //G.handler.post(new Runnable() {
        //    @Override
        //    public void run() {
        //        mAdapter.remove(mAdapter.getPosition(userId));
        //        edtSearch.setText("");
        //    }
        //});
    }

    @Override
    public void onError(int majorCode, int minorCode) {

    }

    @Override
    public void onPhoneContact(final ArrayList<StructListOfContact> contacts, final boolean isEnd) {
        new AddAsync(contacts, isEnd).execute();
    }

    /**
     * ***********************************************************************************
     * *********************************** FastAdapter ***********************************
     * ***********************************************************************************
     */
    //+ manually update
    //public class ContactListAdapterA<Item extends ContactItem> extends FastItemAdapter<Item> {
    //    @Override
    //    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    //        return super.onCreateViewHolder(parent, viewType);
    //    }
    //}
    //
    //public class ContactItem extends AbstractItem<ContactItem, ContactItem.ViewHolder> {
    //    String lastHeader = "";
    //    RealmContacts contact;
    //
    //    public ContactItem setInfo(RealmContacts contact) {
    //        this.contact = contact;
    //        return this;
    //    }
    //
    //    @Override
    //    public void bindView(final ViewHolder viewHolder, List payloads) throws IllegalStateException {
    //        super.bindView(viewHolder, payloads);
    //
    //        if (contact == null || !contact.isValid()) {
    //            return;
    //        }
    //
    //        if (viewHolder.itemView.findViewById(R.id.mainContainer) == null) {
    //            ((ViewGroup) viewHolder.itemView).addView(ViewMaker.getViewRegisteredContacts());
    //        }
    //
    //        viewHolder.image = (CircleImageView) viewHolder.itemView.findViewById(R.id.imageView);
    //        viewHolder.title = (TextView) viewHolder.itemView.findViewById(R.id.title);
    //        viewHolder.subtitle = (TextView) viewHolder.itemView.findViewById(R.id.subtitle);
    //        viewHolder.topLine = (View) viewHolder.itemView.findViewById(R.id.topLine);
    //
    //        String header = contact.getDisplay_name();
    //
    //        if (lastHeader.isEmpty() || (!lastHeader.isEmpty() && !header.isEmpty() && lastHeader.toLowerCase().charAt(0) != header.toLowerCase().charAt(0))) {
    //            viewHolder.topLine.setVisibility(View.VISIBLE);
    //        } else {
    //            viewHolder.topLine.setVisibility(View.GONE);
    //        }
    //
    //        lastHeader = header;
    //
    //        viewHolder.title.setText(contact.getDisplay_name());
    //
    //        final RealmRegisteredInfo realmRegisteredInfo = getRealm().where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, contact.getId()).findFirst();
    //        if (realmRegisteredInfo != null) {
    //            viewHolder.subtitle.setTextColor(ContextCompat.getColor(context, R.color.room_message_gray));
    //            if (realmRegisteredInfo.getStatus() != null) {
    //                if (realmRegisteredInfo.getStatus().equals(ProtoGlobal.RegisteredUser.Status.EXACTLY.toString())) {
    //                    viewHolder.subtitle.setText(LastSeenTimeUtil.computeTime(contact.getId(), realmRegisteredInfo.getLastSeen(), false));
    //                } else {
    //                    if (realmRegisteredInfo.getMainStatus().equals(ProtoGlobal.RegisteredUser.Status.ONLINE.toString())) {
    //                        viewHolder.subtitle.setTextColor(ContextCompat.getColor(context, R.color.room_message_blue));
    //                    }
    //                    viewHolder.subtitle.setText(realmRegisteredInfo.getStatus());
    //                }
    //            }
    //
    //            if (HelperCalander.isPersianUnicode) {
    //                viewHolder.subtitle.setText(viewHolder.subtitle.getText().toString());
    //            }
    //        }
    //
    //        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
    //            @Override
    //            public boolean onLongClick(View v) {
    //
    //                new MaterialDialog.Builder(G.fragmentActivity).title(R.string.to_delete_contact).content(R.string.delete_text).positiveText(R.string.B_ok).onPositive(new MaterialDialog.SingleButtonCallback() {
    //                    @Override
    //                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
    //
    //                        new RequestUserContactsDelete().contactsDelete(realmRegisteredInfo.getPhoneNumber());
    //                    }
    //                }).negativeText(R.string.B_cancel).show();
    //
    //                return false;
    //            }
    //        });
    //
    //        hashMapAvatar.put(contact.getId(), viewHolder.image);
    //        setAvatar(viewHolder, contact.getId());
    //    }
    //
    //    private void setAvatar(final ViewHolder holder, final long userId) {
    //        HelperAvatar.getAvatar(userId, HelperAvatar.AvatarType.USER, false, new OnAvatarGet() {
    //            @Override
    //            public void onAvatarGet(final String avatarPath, long ownerId) {
    //                G.imageLoader.displayImage(AndroidUtils.suitablePath(avatarPath), hashMapAvatar.get(ownerId));
    //            }
    //
    //            @Override
    //            public void onShowInitials(final String initials, final String color) {
    //                hashMapAvatar.get(userId).setImageBitmap(net.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) holder.image.getContext().getResources().getDimension(R.dimen.dp60), initials, color));
    //            }
    //        });
    //    }
    //
    //    @Override
    //    public int getType() {
    //        return 0;
    //    }
    //
    //    @Override
    //    public int getLayoutRes() {
    //        return R.layout.contact_item_code;
    //    }
    //
    //    @Override
    //    public ViewHolder getViewHolder(View viewGroup) {
    //        //View v = ViewMaker.getViewRegisteredContacts();
    //        //
    //        //if (getData() != null && count != getData().size()) {
    //        //    count = getData().size();
    //        //
    //        //    realmRecyclerView.post(new Runnable() {
    //        //        @Override
    //        //        public void run() {
    //        //            realmRecyclerView.removeItemDecoration(decoration);
    //        //            decoration = new StickyRecyclerHeadersDecoration(new StickyHeader(getData().sort(RealmContactsFields.DISPLAY_NAME)));
    //        //            realmRecyclerView.addItemDecoration(decoration);
    //        //        }
    //        //    });
    //        //}
    //        //View v = ViewMaker.getViewRegisteredContacts();
    //        return new ViewHolder(viewGroup);
    //    }
    //
    //    public class ViewHolder extends RecyclerView.ViewHolder {
    //
    //        private RealmContacts realmContacts;
    //        protected CircleImageView image;
    //        protected TextView title;
    //        protected TextView subtitle;
    //        protected View topLine;
    //
    //        public ViewHolder(View view) {
    //            super(view);
    //
    //            //image = (CircleImageView) view.findViewById(R.id.imageView);
    //            //title = (TextView) view.findViewById(R.id.title);
    //            //subtitle = (TextView) view.findViewById(R.id.subtitle);
    //            //topLine = (View) view.findViewById(R.id.topLine);
    //            //
    //            //itemView.setOnClickListener(new View.OnClickListener() {
    //            //    @Override
    //            //    public void onClick(View v) {
    //            //
    //            //        if (isCallAction) {
    //            //            //  G.fragmentActivity.getSupportFragmentManager().popBackStack();
    //            //
    //            //            popBackStackFragment();
    //            //
    //            //            long userId = realmContacts.getId();
    //            //            if (userId != 134 && G.userId != userId) {
    //            //                FragmentCall.call(userId, false);
    //            //            }
    //            //
    //            //
    //            //        } else {
    //            //            showProgress();
    //            //
    //            //            HelperPublicMethod.goToChatRoom(realmContacts.getId(), new HelperPublicMethod.OnComplete() {
    //            //                @Override
    //            //                public void complete() {
    //            //                    hideProgress();
    //            //                    //  G.fragmentActivity.getSupportFragmentManager().beginTransaction().remove(RegisteredContactsFragment.this).commit();
    //            //
    //            //                    popBackStackFragment();
    //            //
    //            //                }
    //            //            }, new HelperPublicMethod.OnError() {
    //            //                @Override
    //            //                public void error() {
    //            //                    hideProgress();
    //            //                }
    //            //            });
    //            //        }
    //            //    }
    //            //});
    //        }
    //    }
    //}

    /**
     * **********************************************************************************
     * ********************************** RealmAdapter **********************************
     * **********************************************************************************
     */

    public class ContactListAdapter extends RealmRecyclerViewAdapter<RealmContacts, RecyclerView.ViewHolder> {

        String lastHeader = "";
        int count;
        private boolean isSwipe = false;
        RealmResults<RealmContacts> usersList;
        private LayoutInflater inflater;


        ContactListAdapter(RealmResults<RealmContacts> realmResults) {
            super(realmResults, true);
            inflater = LayoutInflater.from(context);
            count = realmResults.size();
            usersList = realmResults;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

            View v;
            if (mPageMode == 2){//call mode
                v = inflater.inflate(R.layout.item_contact_call , viewGroup , false);
            }else { //new chat and contact
                v = inflater.inflate(R.layout.item_contact_chat, viewGroup, false);
            }
            //View v = ViewMaker.getViewRegisteredContacts();

            if (getData() != null && count != getData().size()) {
                count = getData().size();

                realmRecyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        realmRecyclerView.removeItemDecoration(decoration);
                        decoration = new StickyRecyclerHeadersDecoration(new StickyHeader(getData().sort(RealmContactsFields.DISPLAY_NAME)));
                        realmRecyclerView.addItemDecoration(decoration);
                    }
                });
            }

            if (mPageMode == 2)
                return new ViewHolderCall(v);
            else
                return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, int i) {

            if (holder instanceof  ViewHolder) {

                ViewHolder viewHolder = (ViewHolder) holder ;

                final RealmContacts contact = viewHolder.realmContacts = getItem(i);
                if (contact == null) {
                    return;
                }

                String header = contact.getDisplay_name();
            /*if (!isMultiSelect) {
                if (lastHeader.isEmpty() || (!lastHeader.isEmpty() && !header.isEmpty() && lastHeader.toLowerCase().charAt(0) != header.toLowerCase().charAt(0))) {
                    viewHolder.topLine.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.topLine.setVisibility(View.GONE);
                }
            }*/
                lastHeader = header;

                viewHolder.title.setText(contact.getDisplay_name());
                viewHolder.subtitle.setText("+" + contact.getPhone());
            /*final RealmRegisteredInfo realmRegisteredInfo = RealmRegisteredInfo.getRegistrationInfo(realm, contact.getId());
            if (realmRegisteredInfo != null) {
                viewHolder.subtitle.setTextColor(ContextCompat.getColor(context, R.color.room_message_gray));
                if (realmRegisteredInfo.getStatus() != null) {
                    if (realmRegisteredInfo.getStatus().equals(ProtoGlobal.RegisteredUser.Status.EXACTLY.toString())) {
                        viewHolder.subtitle.setText(LastSeenTimeUtil.computeTime(contact.getId(), realmRegisteredInfo.getLastSeen(), false));
                    } else {
                        if (realmRegisteredInfo.getMainStatus().equals(ProtoGlobal.RegisteredUser.Status.ONLINE.toString())) {
                            viewHolder.subtitle.setTextColor(ContextCompat.getColor(context, R.color.room_message_blue));
                        }
                        viewHolder.subtitle.setText(realmRegisteredInfo.getStatus());
                    }
                }

                if (HelperCalander.isPersianUnicode) {
                    viewHolder.subtitle.setText(viewHolder.subtitle.getText().toString());
                }
            }*/

            /*if (isMultiSelect) {
                viewHolder.swipeLayout.setSwipeEnabled(false);

            } else {
                viewHolder.swipeLayout.setSwipeEnabled(true);
            }*/


           /* viewHolder.txtDelete.setOnClickListener((View v) -> {
                MaterialDialog dialog = new MaterialDialog.Builder(G.fragmentActivity).title(R.string.to_delete_contact).content(R.string.delete_text).positiveText(R.string.B_ok).onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        new RequestUserContactsDelete().contactsDelete(realmRegisteredInfo.getPhoneNumber());
                    }
                }).negativeText(R.string.B_cancel).onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        viewHolder.swipeLayout.close();
                    }
                }).build();

                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        viewHolder.swipeLayout.close();
                    }
                });
                dialog.show();

            });

            viewHolder.txtEdit.setOnClickListener(v -> {

                dialogEditContact(header, contact.getPhone(), contact.getId());


            });*/


                if (selectedList.containsKey(usersList.get(i).getPhone())) {
                    viewHolder.animateCheckBox.setVisibility(View.VISIBLE);
                    //  viewHolder.animateCheckBox.setChecked(true);
                    //  viewHolder.animateCheckBox.setLineColor(G.context.getResources().getColor(R.color.white));
                    viewHolder.animateCheckBox.setChecked(true);

//                viewHolder.root.setBackgroundColor(ContextCompat.getColor(G.context, R.color.gray_9d));
                } else {
                    //    viewHolder.animateCheckBox.setCircleColor(G.context.getResources().getColor(R.color.green));
                    if (isLongClick) {
                        viewHolder.animateCheckBox.setChecked(false);
                        viewHolder.animateCheckBox.setVisibility(View.VISIBLE);
                    } else {
                        viewHolder.animateCheckBox.setChecked(false);
                        viewHolder.animateCheckBox.setVisibility(View.GONE);
                    }
//                viewHolder.root.setBackgroundColor(ContextCompat.getColor(G.context, R.color.white));
                }

                setAvatar(viewHolder, contact.getId());

            }
            else if (holder instanceof  ViewHolderCall){


                ViewHolderCall viewHolder = (ViewHolderCall) holder ;

                final RealmContacts contact = viewHolder.realmContacts = getItem(i);
                if (contact == null) {
                    return;
                }

                String header = contact.getDisplay_name();

                lastHeader = header;

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
                HelperAvatar.getAvatar(userId, HelperAvatar.AvatarType.USER, false, new OnAvatarGet() {
                    @Override
                    public void onAvatarGet(final String avatarPath, long ownerId) {
                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (userId == ownerId)
                                    G.imageLoader.displayImage(AndroidUtils.suitablePath(avatarPath), holder.image);
                            }
                        });
                    }

                    @Override
                    public void onShowInitials(final String initials, final String color, final long ownerId) {
                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (userId == ownerId)
                                    holder.image.setImageBitmap(net.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) holder.image.getContext().getResources().getDimension(R.dimen.dp60), initials, color));
                            }
                        });
                    }
                });

            }else if (viewHolder instanceof ViewHolderCall){

                ViewHolderCall holder = (ViewHolderCall) viewHolder;
                HelperAvatar.getAvatar(userId, HelperAvatar.AvatarType.USER, false, new OnAvatarGet() {
                        @Override
                        public void onAvatarGet(final String avatarPath, long ownerId) {
                            G.handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (userId == ownerId)
                                        G.imageLoader.displayImage(AndroidUtils.suitablePath(avatarPath), holder.image);
                                }
                            });
                        }

                        @Override
                        public void onShowInitials(final String initials, final String color, final long ownerId) {
                            G.handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (userId == ownerId)
                                        holder.image.setImageBitmap(net.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) holder.image.getContext().getResources().getDimension(R.dimen.dp60), initials, color));
                                }
                            });
                        }
                    });
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private CircleImageView image;
            private TextView title;
            private TextView subtitle;
         //   private ViewGroup txtDelete;
           //private ViewGroup txtEdit;
           // protected View topLine;
            private RealmContacts realmContacts;
           // private SwipeLayout swipeLayout;
            private ConstraintLayout root;
            private CheckBox animateCheckBox;

            public ViewHolder(View view) {
                super(view);

                root = view.findViewById(R.id.iv_itemContactChat_root);
                animateCheckBox =  view.findViewById(R.id.iv_itemContactChat_checkBox);
                image =  view.findViewById(R.id.iv_itemContactChat_profileImage);
                title =  view.findViewById(R.id.tv_itemContactChat_userName);
                //txtDelete = (ViewGroup) view.findViewById(R.id.swipeDelete);
               // txtEdit = (ViewGroup) view.findViewById(R.id.swipeEdit);
                subtitle =  view.findViewById(R.id.tv_itemContactChat_userPhoneNumber);
               // topLine = (View) view.findViewById(R.id.topLine);
                //swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipeRevealLayout);


                root.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (onLongClickRecyclerView != null) {
                            onLongClickRecyclerView.onClick(v, getAdapterPosition());
                            isLongClick = true;

                        }

                        return false;
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
                                                    FragmentCall.call(userId, false, ProtoSignalingOffer.SignalingOffer.Type.VOICE_CALLING);
                                                    popBackStackFragment();
                                                    break;
                                                case 1:
                                                    FragmentCall.call(userId, false, ProtoSignalingOffer.SignalingOffer.Type.VIDEO_CALLING);
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
            private MaterialDesignTextView btnVoiceCall , btnVideoCall ;
            private RealmContacts realmContacts;
            private ConstraintLayout root;
            private CheckBox animateCheckBox;

            public ViewHolderCall(View view) {
                super(view);

                root = view.findViewById(R.id.iv_itemContactCall_root);
                animateCheckBox =  view.findViewById(R.id.iv_itemContactCall_checkBox);
                image =  view.findViewById(R.id.iv_itemContactCall_profileImage);
                title =  view.findViewById(R.id.tv_itemContactCall_userName);
                subtitle =  view.findViewById(R.id.tv_itemContactCall_userPhoneNumber);
                btnVoiceCall = view.findViewById(R.id.tv_itemContactCall_voiceCall);
                btnVideoCall = view.findViewById(R.id.tv_itemContactCall_videoCall);


                root.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (onLongClickRecyclerView != null) {
                            onLongClickRecyclerView.onClick(v, getAdapterPosition());
                            isLongClick = true;

                        }

                        return false;
                    }
                });

                btnVoiceCall.setOnClickListener( v -> {
                    long userId = realmContacts.getId();
                    if (userId != 134 && G.userId != userId) {
                        FragmentCall.call(userId, false, ProtoSignalingOffer.SignalingOffer.Type.VOICE_CALLING);
                        popBackStackFragment();
                    }
                });

                btnVideoCall.setOnClickListener( v -> {
                    long userId = realmContacts.getId();
                    if (userId != 134 && G.userId != userId) {
                        FragmentCall.call(userId, false, ProtoSignalingOffer.SignalingOffer.Type.VIDEO_CALLING);
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
                                                    FragmentCall.call(userId, false, ProtoSignalingOffer.SignalingOffer.Type.VOICE_CALLING);
                                                    popBackStackFragment();
                                                    break;
                                                case 1:
                                                    FragmentCall.call(userId, false, ProtoSignalingOffer.SignalingOffer.Type.VIDEO_CALLING);
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

    private void dialogEditContact(String header, long phone, final long userId) {

        final LinearLayout layoutNickname = new LinearLayout(G.fragmentActivity);
        layoutNickname.setOrientation(LinearLayout.VERTICAL);

        String splitNickname[] = header.split(" ");
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

    /**
     * ************************************* show all phone contact *************************************
     */
    private class StickyHeader implements StickyRecyclerHeadersAdapter {

        RealmResults<RealmContacts> realmResults;
        private ViewGroup mParent ;

        StickyHeader(RealmResults<RealmContacts> realmResults) {
            this.realmResults = realmResults;
        }

        @Override
        public long getHeaderId(int position) {
            return realmResults.get(position).getDisplay_name().toUpperCase().charAt(0);
        }

        @Override
        public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
            mParent = parent ;
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_header_item, parent, false);
            return new RecyclerView.ViewHolder(view) {
            };
        }

        @Override
        public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {

            CustomTextViewMedium textView = (CustomTextViewMedium) holder.itemView;
            //fixLayoutSize(textView , mParent);
            textView.setText(realmResults.get(position).getDisplay_name().toUpperCase().substring(0, 1));
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

/*    private class ContactListStickyHeader implements StickyRecyclerHeadersAdapter {

        ArrayList<StructListOfContact> contactLists;

        ContactListStickyHeader(ArrayList<StructListOfContact> contactLists) {
            this.contactLists = contactLists;
        }

        @Override
        public long getHeaderId(int position) {
            return contactLists.get(position).getDisplayName().toUpperCase().charAt(0);
        }

        @Override
        public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_header_item, parent, false);
            return new RecyclerView.ViewHolder(view) {
            };
        }

        @Override
        public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {

            CustomTextViewMedium textView = (CustomTextViewMedium) holder.itemView;
            textView.setText(contactLists.get(position).getDisplayName().toUpperCase().substring(0, 1));
        }

        @Override
        public int getItemCount() {
            return contactLists.size();
        }
    }*/

    public class AdapterListContact extends RecyclerView.Adapter<AdapterListContact.ViewHolder> {

        public String item;
        public String phone;
        ArrayList<StructListOfContact> mPhoneContactList;
        //public String getItem() {
        //    return item;
        //}

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
            //  G.imageLoader.displayImage(AndroidUtils.suitablePath(holder.image), hashMapAvatar.get(mPhoneContactList.get(position).getDisplayName().charAt(0)));


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

    /*        rcvListContact.post(new Runnable() {
                @Override
                public void run() {
                    rcvListContact.removeItemDecoration(decoration);
                    decoration = new StickyRecyclerHeadersDecoration(new ContactListStickyHeader(mPhoneContactList));
                    rcvListContact.addItemDecoration(decoration);
                }
            });*/

        }

/*        @Override
        public long getHeaderId(int position) {
            return position;
        }

        @Override
        public ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
            return null;
        }

        @Override
        public void onBindHeaderViewHolder(ViewHolder holder, int position) {

        }*/

        @Override
        public int getItemCount() {
            return mPhoneContactList.size();
        }

        //The viewHolder used for this item. This viewHolder is always reused by the RecyclerView so scrolling is blazing fast
        protected class ViewHolder extends RecyclerView.ViewHolder {


           // private CircleImageView imgContact;
            private TextView title;
            private TextView subtitle;
            /*  private ViewGroup txtDelete;
              private ViewGroup txtEdit;*/
          /*  protected View topLine;
            private RealmContacts realmContacts;
            private SwipeLayout swipeLayout;*/
            private ViewGroup mRoot;
            // private AnimateCheckBox animateCheckBox;

            public ViewHolder(View view) {
                super(view);

                mRoot = (ViewGroup) view.findViewById(R.id.liContactItem);
              //  imgContact = (CircleImageView) view.findViewById(R.id.imgContact);
                title = (TextView) view.findViewById(R.id.title);
                subtitle = (TextView) view.findViewById(R.id.subtitle);
              /*  topLine = (View) view.findViewById(R.id.topLine);
                topLine.setVisibility(View.VISIBLE);*/

            }
        }
    }

    private class AddAsync extends AsyncTask<Void, Void, Void> {

        private ArrayList<StructListOfContact> contacts;
        private boolean isEnd;

        public AddAsync(ArrayList<StructListOfContact> contacts, boolean isEnd) {
            this.contacts = contacts;
            this.isEnd = isEnd;
        }

        @Override
        protected Void doInBackground(Void... params) {
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            for (int i = 0; i < contacts.size(); i++) {
                //   fastItemAdapter.add(new AdapterListContact(contacts.get(i).getDisplayName(), contacts.get(i).getPhone()).withIdentifier(index++));

                String s = contacts.get(i).getPhone();
                s = s.replaceAll("\\A0|\\+|\\-?", "");
                if (s.contains(" "))
                    s = s.replace(" ", "");
                if (!s.startsWith("98"))
                    s = "98" + s;
                contacts.get(i).setPhone(s);
                //  phoneContactsList.add(contacts.get(i));

            }

            //   getRealm().where(RealmContacts.class).findAll().sort(RealmContactsFields.DISPLAY_NAME);
            RealmResults<RealmContacts> mList = getRealm().where(RealmContacts.class).findAll().sort(RealmContactsFields.DISPLAY_NAME);


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


            //  phoneContactsList.clear();
            phoneContactsList.addAll(slc);

     /*       Collections.sort(phoneContactsList, new Comparator<StructListOfContact>() {
                @Override
                public int compare(StructListOfContact o1, StructListOfContact o2) {
                    String s1 = o1.displayName;
                    String s2 = o2.displayName;
                    return s1.compareToIgnoreCase(s2);
                }
            });*/

            adapterListContact.notifyDataSetChanged();
            if (isEnd) {
                prgWaitingLiadList.setVisibility(View.GONE);
            }
            super.onPostExecute(aVoid);
        }
    }

    // Add/Remove the item from/to the list

    public void multi_select(int position) {
        if (mActionMode != null) {

            if (results.get(position) == null) {
                return;
            }

            if (selectedList.containsKey(results.get(position).getPhone())) {
                selectedList.remove(results.get(position).getPhone());
            } else {
                selectedList.put(results.get(position).getPhone(), true);
            }

            if (selectedList.size() > 0) {
                mActionMode.setTitle("" + selectedList.size());
            } else {
                mActionMode.setTitle("");
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

    private interface onClickRecyclerView {
        void onClick(View view, int position);
    }

    private interface onLongClickRecyclerView {
        void onClick(View view, int position);
    }

    //todo : nazari

    @Override //btn edit
    public void onLeftIconClickListener(View view) {

    }

    @Override
    public void onSearchClickListener(View view) {

        if (!isToolbarInEditMode){
            isToolbarInEditMode = mHelperToolbar.setSearchEditableMode(true);
            openKeyBoard();
        }

    }

    @Override //btn add
    public void onRightIconClickListener(View view) {
        if (mActionMode != null) {
            mActionMode.finish();
        }
        FragmentAddContact fragment = FragmentAddContact.newInstance();
        Bundle bundle = new Bundle();
        bundle.putString("TITLE", G.context.getString(R.string.fac_Add_Contact));
        fragment.setArguments(bundle);
        new HelperFragment(fragment).setReplace(false).load();
    }

    @Override
    public void onBtnClearSearchClickListener(View view) {
        if (edtSearch.getText().length() > 0) {
            edtSearch.setText("");
        } else {
            isToolbarInEditMode = mHelperToolbar.setSearchEditableMode(false) ;
            closeKeyboard(getView());
        }
    }
}


