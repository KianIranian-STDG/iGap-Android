package net.iGap.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.daimajia.swipe.SwipeLayout;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityMain;
import net.iGap.adapter.items.chat.ViewMaker;
import net.iGap.databinding.FragmentSyncRegisteredContactsBinding;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperPermission;
import net.iGap.helper.HelperPublicMethod;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.helper.avatar.ParamWithAvatarType;
import net.iGap.interfaces.OnContactsGetList;
import net.iGap.interfaces.OnGetPermission;
import net.iGap.interfaces.OnPhoneContact;
import net.iGap.module.AppUtils;
import net.iGap.module.CircleImageView;
import net.iGap.module.Contacts;
import net.iGap.module.CustomTextViewMedium;
import net.iGap.module.LastSeenTimeUtil;
import net.iGap.module.LoginActions;
import net.iGap.module.structs.StructListOfContact;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoSignalingOffer;
import net.iGap.realm.RealmContacts;
import net.iGap.realm.RealmContactsFields;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.request.RequestUserContactsDelete;
import net.iGap.request.RequestUserContactsGetList;

import java.io.IOException;
import java.util.ArrayList;

import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;

public class FragmentSyncRegisteredContacts extends BaseFragment implements OnPhoneContact, OnContactsGetList {


    private static boolean getPermission = true;
    StickyRecyclerHeadersDecoration decoration;
    RealmResults<RealmContacts> results;
    private TextView menu_txt_titleToolbar;
    private Button skipBtn;
    private RecyclerView realmRecyclerView;
    private Realm realm;
    private ProgressBar prgWaiting;
    private boolean isCallAction = false;
    private ActionMode mActionMode;
    protected ArrayMap<Long, Boolean> selectedList = new ArrayMap<>();
    boolean isMultiSelect = false;
    private FragmentSyncRegisteredContacts.ContactListAdapter contactListAdapter;
    private AppBarLayout toolbar;
    public FragmentSyncRegisteredContacts.onClickRecyclerView onCliclRecyclerView;
    public FragmentSyncRegisteredContacts.onLongClickRecyclerView onLongClickRecyclerView;
    public boolean isLongClick = false;

    public ArrayList<StructListOfContact> phoneContactsList = new ArrayList<>();
    private FragmentSyncRegisteredContactsBinding fragmentSyncRegisteredContactsBinding;
    public LinearLayoutManager layoutManager;

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

        prgWaiting = (ProgressBar) view.findViewById(R.id.prgWaiting_addContact);
        AppUtils.setProgresColler(prgWaiting);

        Bundle bundle = this.getArguments();
        String title = null;
        if (bundle != null) {
            title = bundle.getString("TITLE");
            isCallAction = bundle.getBoolean("ACTION");
        }

        toolbar = view.findViewById(R.id.fc_layot_title);
        toolbar.setBackgroundColor(Color.parseColor(G.appBarColor));
        menu_txt_titleToolbar = (TextView) view.findViewById(R.id.menu_txt_titleToolbar);
        Typeface titleTypeface;
        if (!HelperCalander.isPersianUnicode) {
            titleTypeface = G.typeface_neuropolitical;
        } else {
            titleTypeface = G.typeface_IRANSansMobile;
        }
        menu_txt_titleToolbar.setTypeface(titleTypeface);

        // My Code H.Amini
        //ContactUtils.syncContacts();

        realmRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        realmRecyclerView.setItemViewCacheSize(1000);
        realmRecyclerView.setItemAnimator(null);
        layoutManager = new LinearLayoutManager(G.fragmentActivity);
        realmRecyclerView.setLayoutManager(layoutManager);

        // get all the contacts from realm
        results = getRealm().where(RealmContacts.class).findAll().sort(RealmContactsFields.DISPLAY_NAME);

        // Load all of the data
        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                contactListAdapter = new FragmentSyncRegisteredContacts.ContactListAdapter(results);
                realmRecyclerView.setAdapter(contactListAdapter);
                realmRecyclerView.setVisibility(View.VISIBLE);
            }
        }, 500);

        // going to app directly
        skipBtn = view.findViewById(R.id.frag_sync_skipbtn);
        skipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (getActivity() == null || getActivity().isFinishing()) {
                    return;
                }

                Intent intent = new Intent(getContext(), ActivityMain.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                G.context.startActivity(intent);
                G.fragmentActivity.finish();

            }
        });

        FragmentSyncRegisteredContacts.StickyHeader stickyHeader = new FragmentSyncRegisteredContacts.StickyHeader(results);
        decoration = new StickyRecyclerHeadersDecoration(stickyHeader);
        realmRecyclerView.addItemDecoration(decoration);

        onCliclRecyclerView = new FragmentSyncRegisteredContacts.onClickRecyclerView() {
            @Override
            public void onClick(View view, int position) {
                if (isMultiSelect) {
                    multi_select(position);
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
                        new Contacts.FetchContactForClient().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }

                    @Override
                    public void deny() {
                        if (results.size() == 0) {
                            new RequestUserContactsGetList().userContactGetList();
                        }
                    }
                });
            }
            else {
                if (results.size() == 0) {
                    new RequestUserContactsGetList().userContactGetList();
                }

                if (HelperPermission.grantedContactPermission()) {
                    new Contacts.FetchContactForClient().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    hideProgress();
                }
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
    public void onDestroy() {
        super.onDestroy();
        if (realm != null && !realm.isClosed()) {
            realm.close();
        }
        G.onUserContactdelete = null;
        G.onContactsGetList = null;
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
        if (results.size() == 0) {
            // No Contacts Exist Go to Main
            if (getActivity() == null || getActivity().isFinishing()) {
                return;
            }

            Intent intent = new Intent(getContext(), ActivityMain.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            G.context.startActivity(intent);
            G.fragmentActivity.finish();
        }
    }

    /**
     * **********************************************************************************
     * ********************************** RealmAdapter **********************************
     * **********************************************************************************
     */

    public class ContactListAdapter extends RealmRecyclerViewAdapter<RealmContacts, FragmentSyncRegisteredContacts.ContactListAdapter.ViewHolder> {

        String lastHeader = "";
        int count;
        private boolean isSwipe = false;
        RealmResults<RealmContacts> usersList;


        ContactListAdapter(RealmResults<RealmContacts> realmResults) {
            super(realmResults, true);
            count = realmResults.size();
            usersList = realmResults;
        }

        @Override
        public FragmentSyncRegisteredContacts.ContactListAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            // View v = inflater.inflate(R.layout.contact_item, viewGroup, false);

            View v = ViewMaker.getViewRegisteredContacts();

            if (getData() != null && count != getData().size()) {
                count = getData().size();

                realmRecyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        realmRecyclerView.removeItemDecoration(decoration);
                        decoration = new StickyRecyclerHeadersDecoration(new FragmentSyncRegisteredContacts.StickyHeader(getData().sort(RealmContactsFields.DISPLAY_NAME)));
                        realmRecyclerView.addItemDecoration(decoration);
                    }
                });
            }

            return new FragmentSyncRegisteredContacts.ContactListAdapter.ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final FragmentSyncRegisteredContacts.ContactListAdapter.ViewHolder viewHolder, int i) {

            if (i == 0) {
                hideProgress();
            }

            final RealmContacts contact = viewHolder.realmContacts = getItem(i);
            if (contact == null) {
                return;
            }

            String header = contact.getDisplay_name();
            if (!isMultiSelect) {
                if (lastHeader.isEmpty() || (!lastHeader.isEmpty() && !header.isEmpty() && lastHeader.toLowerCase().charAt(0) != header.toLowerCase().charAt(0))) {
                    viewHolder.topLine.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.topLine.setVisibility(View.GONE);
                }
            }
            lastHeader = header;

            viewHolder.title.setText(contact.getDisplay_name());

            final RealmRegisteredInfo realmRegisteredInfo = RealmRegisteredInfo.getRegistrationInfo(realm, contact.getId());
            if (realmRegisteredInfo != null) {
                viewHolder.subtitle.setTextColor(ContextCompat.getColor(G.context, R.color.room_message_gray));
                if (realmRegisteredInfo.getStatus() != null) {
                    if (realmRegisteredInfo.getStatus().equals(ProtoGlobal.RegisteredUser.Status.EXACTLY.toString())) {
                        viewHolder.subtitle.setText(LastSeenTimeUtil.computeTime(contact.getId(), realmRegisteredInfo.getLastSeen(), false));
                    } else {
                        if (realmRegisteredInfo.getMainStatus().equals(ProtoGlobal.RegisteredUser.Status.ONLINE.toString())) {
                            viewHolder.subtitle.setTextColor(ContextCompat.getColor(G.context, R.color.room_message_blue));
                        }
                        viewHolder.subtitle.setText(realmRegisteredInfo.getStatus());
                    }
                }

                if (HelperCalander.isPersianUnicode) {
                    viewHolder.subtitle.setText(viewHolder.subtitle.getText().toString());
                }
            }

            viewHolder.swipeLayout.setSwipeEnabled(false);


            viewHolder.txtDelete.setOnClickListener((View v) -> {
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


            });


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
                    viewHolder.animateCheckBox.setVisibility(View.INVISIBLE);
                }
//                viewHolder.root.setBackgroundColor(ContextCompat.getColor(G.context, R.color.white));
            }

            setAvatar(viewHolder, contact.getId());
        }

        private void setAvatar(final FragmentSyncRegisteredContacts.ContactListAdapter.ViewHolder holder, final long userId) {
            avatarHandler.getAvatar(new ParamWithAvatarType(holder.image, userId).avatarType(AvatarHandler.AvatarType.USER));
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private CircleImageView image;
            private TextView title;
            private TextView subtitle;
            private ViewGroup txtDelete;
            private ViewGroup txtEdit;
            protected View topLine;
            private RealmContacts realmContacts;
            private SwipeLayout swipeLayout;
            private LinearLayout root;
            private CheckBox animateCheckBox;

            public ViewHolder(View view) {
                super(view);

                root = (LinearLayout) view.findViewById(R.id.mainContainer);
                animateCheckBox = (CheckBox) view.findViewById(R.id.animateCheckBoxContact);
                image = (CircleImageView) view.findViewById(R.id.imageView);
                title = (TextView) view.findViewById(R.id.title);
                txtDelete = (ViewGroup) view.findViewById(R.id.swipeDelete);
                txtEdit = (ViewGroup) view.findViewById(R.id.swipeEdit);
                subtitle = (TextView) view.findViewById(R.id.subtitle);
                topLine = (View) view.findViewById(R.id.topLine);
                swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipeRevealLayout);
                swipeLayout.setSwipeEnabled(false);

                root.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (onLongClickRecyclerView != null) {
                            onLongClickRecyclerView.onClick(v, getAdapterPosition());
                            isLongClick = true;
                            ;
                        }

                        return false;
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

        StickyHeader(RealmResults<RealmContacts> realmResults) {
            this.realmResults = realmResults;
        }

        @Override
        public long getHeaderId(int position) {
            return realmResults.get(position).getDisplay_name().toUpperCase().charAt(0);
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
            textView.setText(realmResults.get(position).getDisplay_name().toUpperCase().substring(0, 1));
        }

        @Override
        public int getItemCount() {
            return realmResults.size();
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

            //adapterListContact.notifyDataSetChanged();
            if (isEnd) {

            }
            super.onPostExecute(slc);
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
    
    
}
