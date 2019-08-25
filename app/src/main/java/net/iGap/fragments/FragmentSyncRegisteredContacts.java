package net.iGap.fragments;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityMain;
import net.iGap.databinding.FragmentSyncRegisteredContactsBinding;
import net.iGap.dialog.DefaultRoundDialog;
import net.iGap.helper.ContactManager;
import net.iGap.helper.HelperPermission;
import net.iGap.helper.HelperPublicMethod;
import net.iGap.helper.HelperToolbar;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.helper.avatar.ParamWithAvatarType;
import net.iGap.interfaces.OnContactsGetList;
import net.iGap.interfaces.OnGetPermission;
import net.iGap.interfaces.OnPhoneContact;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.libs.bottomNavigation.Util.Utils;
import net.iGap.module.AppUtils;
import net.iGap.module.Contacts;
import net.iGap.module.EmojiTextViewE;
import net.iGap.module.FastScroller;
import net.iGap.module.LastSeenTimeUtil;
import net.iGap.module.LoginActions;
import net.iGap.module.ScrollingLinearLayoutManager;
import net.iGap.module.structs.StructListOfContact;
import net.iGap.proto.ProtoSignalingOffer;
import net.iGap.realm.RealmContacts;
import net.iGap.realm.RealmContactsFields;
import net.iGap.request.RequestUserContactsGetList;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class FragmentSyncRegisteredContacts extends BaseFragment implements OnPhoneContact, OnContactsGetList, ToolbarListener {

    private static boolean getPermission = true;
    RealmResults<RealmContacts> results;
    private Button skipBtn;
    private RecyclerView realmRecyclerView;
    private Realm realm;
    private ProgressBar prgWaiting;
    private boolean isCallAction = false;
    boolean isMultiSelect = false;
    private ContactListAdapter2 contactListAdapter2;
    public FragmentSyncRegisteredContacts.onClickRecyclerView onCliclRecyclerView;

    public ArrayList<StructListOfContact> phoneContactsList = new ArrayList<>();
    private FragmentSyncRegisteredContactsBinding fragmentSyncRegisteredContactsBinding;
    public LinearLayoutManager layoutManager;
    public final static String ARG_USER_ID = "arg_user_id";
    private HelperToolbar mHelperToolbar;
    private long userID;

    public static FragmentSyncRegisteredContacts newInstance() {
        return new FragmentSyncRegisteredContacts();
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
        realm = Realm.getDefaultInstance();

        fragmentSyncRegisteredContactsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_sync_registered_contacts, container, false);
        return fragmentSyncRegisteredContactsBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, final @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        G.onPhoneContact = this;
        G.onContactsGetList = this;
        Contacts.localPhoneContactId = 0;
        Contacts.getContact = true;

        userID = this.getArguments().getLong(ARG_USER_ID);

        //Toolbar Initial
        LinearLayout toolbarLayout = view.findViewById(R.id.frg_contact_ll_toolbar_layout);
        Utils.darkModeHandler(toolbarLayout);
        mHelperToolbar = HelperToolbar.create()
                .setContext(getContext())
                .setSearchBoxShown(true)
                .setLogoShown(true)
                .setDefaultTitle(getString(R.string.str_frag_sync_contactWelcome));
        toolbarLayout.addView(mHelperToolbar.getView());
        mHelperToolbar.setListener(this);

        prgWaiting = (ProgressBar) view.findViewById(R.id.prgWaiting_addContact);
        AppUtils.setProgresColler(prgWaiting);

        Bundle bundle = this.getArguments();
        String title = null;
        if (bundle != null) {
            title = bundle.getString("TITLE");
            isCallAction = bundle.getBoolean("ACTION");
        }

        // My Code H.Amini
        //ContactUtils.syncContacts();

        realmRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        realmRecyclerView.setItemViewCacheSize(1000);
        realmRecyclerView.setItemAnimator(null);
        layoutManager = new LinearLayoutManager(realmRecyclerView.getContext());
        realmRecyclerView.setLayoutManager(layoutManager);

        // get all the contacts from realm
        results = getRealm().where(RealmContacts.class).findAll().sort(RealmContactsFields.DISPLAY_NAME);

        results.addChangeListener(new RealmChangeListener<RealmResults<RealmContacts>>() {
            @Override
            public void onChange(RealmResults<RealmContacts> realmContacts) {
                contactListAdapter2.notifyDataSetChanged();
            }
        });
        // Load all of the data

        //contactListAdapter = new FragmentSyncRegisteredContacts.ContactListAdapter(results);
        contactListAdapter2 = new ContactListAdapter2(results);

        //realmRecyclerView.setAdapter(contactListAdapter);
        realmRecyclerView.setAdapter(contactListAdapter2);
        realmRecyclerView.setVisibility(View.VISIBLE);
        realmRecyclerView.setLayoutManager(new ScrollingLinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false, 1000));
        realmRecyclerView.setNestedScrollingEnabled(false);
        FastScroller fastScroller = view.findViewById(R.id.fast_scroller);
        fastScroller.setRecyclerView(realmRecyclerView);


        // going to app directly
        skipBtn = view.findViewById(R.id.frag_sync_skipbtn);
        skipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (getActivity() == null || getActivity().isFinishing()) {
                    return;
                }

                Intent intent = new Intent(getContext(), ActivityMain.class);
                intent.putExtra(ARG_USER_ID, userID);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                G.context.startActivity(intent);
                G.fragmentActivity.finish();

            }
        });

        /*FragmentSyncRegisteredContacts.StickyHeader stickyHeader = new FragmentSyncRegisteredContacts.StickyHeader(results);
        decoration = new StickyRecyclerHeadersDecoration(stickyHeader);
        realmRecyclerView.addItemDecoration(decoration);*/

        onCliclRecyclerView = new FragmentSyncRegisteredContacts.onClickRecyclerView() {
            @Override
            public void onClick(View view, int position) {
                if (isMultiSelect) {
                }
            }
        };

        //get permission for contacts
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
                    }

                    @Override
                    public void deny() {
                        /*if (results.size() == 0) {
                            new RequestUserContactsGetList().userContactGetList();
                        }*/
                        if (getActivity() == null || getActivity().isFinishing()) {
                            return;
                        }

                        Intent intent = new Intent(getContext(), ActivityMain.class);
                        intent.putExtra(ARG_USER_ID, userID);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        G.context.startActivity(intent);
                        G.fragmentActivity.finish();
                    }
                });
            }
            else {
                if (results.size() == 0) {
                    new RequestUserContactsGetList().userContactGetList();
                }
                else {
                    hideProgress();
                }

                /*if (HelperPermission.grantedContactPermission()) {
                    new Contacts.FetchContactForClient().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    hideProgress();
                }*/
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void hideProgress() {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                if (prgWaiting.getVisibility() == View.VISIBLE)
                    prgWaiting.setVisibility(View.GONE);
            }
        });
    }

    private void showProgress() {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                if (prgWaiting.getVisibility() == View.GONE)
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
    public void onDestroyView() {
        super.onDestroyView();

        G.onUserContactdelete = null;
        G.onContactsGetList = null;

        realm.close();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //Override onSaveInstanceState method and comment 'super' from avoid from "Can not perform this action after onSaveInstanceState" error
        //super.onSaveInstanceState(outState); //
    }

    @Override
    public void onPhoneContact(final ArrayList<StructListOfContact> contacts, final boolean isEnd) {
        new FragmentSyncRegisteredContacts.AddAsync(contacts, isEnd).execute();
    }

    @Override
    public void onContactsGetList() {

        if (results == null || results.size() == 0) {
            results = getRealm().where(RealmContacts.class).limit(ContactManager.CONTACT_LIMIT).findAll().sort(RealmContactsFields.DISPLAY_NAME);
            contactListAdapter2 = new ContactListAdapter2(results);
            realmRecyclerView.setAdapter(contactListAdapter2);
            if (results.size() == 0) {
                // No Contacts Exist Go to Main
                if (getActivity() == null || getActivity().isFinishing()) {
                    return;
                }

                Intent intent = new Intent(getContext(), ActivityMain.class);
                intent.putExtra(ARG_USER_ID, userID);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                G.context.startActivity(intent);
                G.fragmentActivity.finish();
            }
        }
        hideProgress();
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
            try (Realm realm = Realm.getDefaultInstance()) {
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

                return slc;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<StructListOfContact> slc) {
            phoneContactsList.addAll(slc);

            //adapterListContact.notifyDataSetChanged();
            if (isEnd) {

            }
            super.onPostExecute(slc);
        }
    }

    private interface onClickRecyclerView {
        void onClick(View view, int position);
    }

    @Override
    public void onSearchClickListener(View view) {
        openKeyBoard();
    }

    @Override
    public void onSearchTextChangeListener(View view, String text) {
        if (text.length() > 0) {
            results = getRealm().where(RealmContacts.class).contains(RealmContactsFields.DISPLAY_NAME, text, Case.INSENSITIVE).findAll().sort(RealmContactsFields.DISPLAY_NAME);
        } else {
            results = getRealm().where(RealmContacts.class).findAll().sort(RealmContactsFields.DISPLAY_NAME);
        }
//        contactListAdapter.usersList = results;
//        contactListAdapter.notify();
//        contactListAdapter = new FragmentSyncRegisteredContacts.ContactListAdapter(results);
//        realmRecyclerView.setAdapter(contactListAdapter);
        contactListAdapter2 = new ContactListAdapter2(results);
        realmRecyclerView.setAdapter(contactListAdapter2);
        /*FragmentSyncRegisteredContacts.StickyHeader stickyHeader = new FragmentSyncRegisteredContacts.StickyHeader(results);
        decoration = new StickyRecyclerHeadersDecoration(stickyHeader);
        realmRecyclerView.addItemDecoration(decoration);*/
    }


    public class ContactListAdapter2 extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private List<RealmContacts> usersList;
        private int count;
        private LayoutInflater inflater;


        ContactListAdapter2(List<RealmContacts> contacts) {
            inflater = LayoutInflater.from(G.context);
            count = contacts.size();
            usersList = contacts;
        }

        public String getBubbleText(int position) {
            if (usersList.size() == 0 || position > (usersList.size()-1) || position == -1)
                return "-";
            else {
                return usersList.get(position).getDisplay_name().substring(0, 1).toUpperCase();
            }
        }

        @NotNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup viewGroup, int i) {

            View v = inflater.inflate(R.layout.item_contact_chat, viewGroup, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NotNull final RecyclerView.ViewHolder holder, int i) {

            if (holder instanceof ViewHolder) {

                if (i == 0) {
                    hideProgress();
                }

                ViewHolder viewHolder = (ViewHolder) holder;

                final RealmContacts contact = viewHolder.realmContacts = usersList.get(i);
                if (contact == null) {
                    return;
                }

                viewHolder.title.setTextColor(getResources().getColor(R.color.black));
                viewHolder.subtitle.setTextColor(getResources().getColor(R.color.gray_4c));

                viewHolder.title.setText(contact.getDisplay_name());
                viewHolder.subtitle.setText(LastSeenTimeUtil.computeTime(contact.getId(), contact.getLast_seen(), false));

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

            private de.hdodenhof.circleimageview.CircleImageView image;
            private EmojiTextViewE title;
            private TextView subtitle;
            private RealmContacts realmContacts;
            private ConstraintLayout root;
            private CheckBox animateCheckBox;

            public ViewHolder(View view) {
                super(view);

                root = view.findViewById(R.id.iv_itemContactChat_root);
                animateCheckBox = view.findViewById(R.id.iv_itemContactChat_checkBox);
                animateCheckBox.setVisibility(View.GONE);
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

                root.setOnClickListener(v -> {

                    if (!isMultiSelect) {
                        if (isCallAction) {
                            long userId = realmContacts.getId();
                            if (userId != 134 && G.userId != userId) {


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

                        }
                        else {
                            showProgress();
                            HelperPublicMethod.goToChatRoomFromFirstContact(realmContacts.getId(), new HelperPublicMethod.OnComplete() {
                                        @Override
                                        public void complete() {
                                            hideProgress();
                                            popBackStackFragment();
                                            G.fragmentActivity.finish();
                                        }
                                    }, new HelperPublicMethod.OnError() {
                                        @Override
                                        public void error() {
                                            hideProgress();
                                            G.handler.post(()->{
                                                new DefaultRoundDialog(getContext())
                                                        .setTitle(R.string.warning)
                                                        .setMessage(R.string.str_frag_sync_error)
                                                        // Specifying a listener allows you to take an action before dismissing the dialog.
                                                        // The dialog is automatically dismissed when a dialog button is clicked.
                                                        .setPositiveButton(R.string.dialog_ok, null)
                                                        .show();
                                            });
                                        }
                                    });

                                }
                    } else {
                        /*if (onClickRecyclerView != null)
                            onClickRecyclerView.onClick(v, getAdapterPosition());*/
                    }
                });
            }
        }

    }
}
