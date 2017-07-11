/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;
import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;
import java.util.HashMap;
import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperAvatar;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperPublicMethod;
import net.iGap.interfaces.OnAvatarGet;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AppUtils;
import net.iGap.module.CircleImageView;
import net.iGap.module.CustomTextViewMedium;
import net.iGap.module.LastSeenTimeUtil;
import net.iGap.module.MaterialDesignTextView;
import net.iGap.module.SHP_SETTING;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmContacts;
import net.iGap.realm.RealmContactsFields;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.realm.RealmRegisteredInfoFields;
import net.iGap.request.RequestUserContactsGetList;

import static android.content.Context.MODE_PRIVATE;
import static net.iGap.G.context;
import static net.iGap.G.inflater;
import static net.iGap.R.string.contacts;

public class RegisteredContactsFragment extends Fragment {

    private TextView menu_txt_titleToolbar;
    private ViewGroup vgAddContact, vgRoot;

    private RecyclerView realmRecyclerView;
    private SharedPreferences sharedPreferences;
    private boolean isImportContactList = false;
    StickyRecyclerHeadersDecoration decoration;
    private ProgressBar prgWaiting;
    RealmResults<RealmContacts> results;
    private FragmentActivity mActivity;
    private EditText edtSearch;
    private boolean isCallAction = false;
    private HashMap<Long, CircleImageView> hashMapAvatar = new HashMap<>();


    public static RegisteredContactsFragment newInstance() {
        return new RegisteredContactsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contacts, container, false);
    }

    @Override
    public void onViewCreated(View view, final @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        sharedPreferences = mActivity.getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);

        /**
         * not import contact in every enter to this page
         * for this purpose i comment this code. but not cleared.
         */
        //isImportContactList = sharedPreferences.getBoolean(SHP_SETTING.KEY_GET_CONTACT_IN_FRAGMENT, false);
        //if (!isImportContactList) {
        //    try {
        //        HelperPermision.getContactPermision(mActivity, new OnGetPermission() {
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

        prgWaiting = (ProgressBar) view.findViewById(R.id.prgWaiting_addContact);
        AppUtils.setProgresColler(prgWaiting);

        prgWaiting.setVisibility(View.GONE);
        vgAddContact = (ViewGroup) view.findViewById(R.id.menu_layout_addContact);
        vgRoot = (ViewGroup) view.findViewById(R.id.menu_parent_layout);

        Bundle bundle = this.getArguments();
        String title = null;
        if (bundle != null) {
            title = bundle.getString("TITLE");
            isCallAction = bundle.getBoolean("ACTION");
        }

        if (title != null) {

            if (title.equals("New Chat")) {
                title = G.context.getString(R.string.New_Chat);
            } else if (title.equals("Contacts")) {
                title = G.context.getString(contacts);
            } else if (title.equals("call")) {
                title = G.context.getString(R.string.call_with);
            }
        }

        view.findViewById(R.id.fc_layot_title).setBackgroundColor(Color.parseColor(G.appBarColor));

        menu_txt_titleToolbar = (TextView) view.findViewById(R.id.menu_txt_titleToolbar);
        menu_txt_titleToolbar.setText(title);

        final RippleView txtClose = (RippleView) view.findViewById(R.id.menu_ripple_close);
        edtSearch = (EditText) view.findViewById(R.id.menu_edt_search);
        final TextView txtSearch = (TextView) view.findViewById(R.id.menu_btn_search);
        txtSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtClose.setVisibility(View.VISIBLE);
                edtSearch.setVisibility(View.VISIBLE);
                edtSearch.setFocusable(true);
                menu_txt_titleToolbar.setVisibility(View.GONE);
                txtSearch.setVisibility(View.GONE);
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
                    menu_txt_titleToolbar.setVisibility(View.VISIBLE);
                    txtSearch.setVisibility(View.VISIBLE);
                    InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
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

                Realm realm = Realm.getDefaultInstance();

                if (s.length() > 0) {
                    results = realm.where(RealmContacts.class).contains(RealmContactsFields.DISPLAY_NAME, s.toString(), Case.INSENSITIVE).findAllSorted(RealmContactsFields.DISPLAY_NAME);
                } else {
                    results = realm.where(RealmContacts.class).findAllSorted(RealmContactsFields.DISPLAY_NAME);
                }
                realmRecyclerView.setAdapter(new ContactListAdapter(results));

                realmRecyclerView.removeItemDecoration(decoration);
                decoration = new StickyRecyclerHeadersDecoration(new StickyHeader(results));
                realmRecyclerView.addItemDecoration(decoration);

                realm.close();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        vgAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentAddContact fragment = FragmentAddContact.newInstance();
                Bundle bundle = new Bundle();
                bundle.putString("TITLE", G.context.getString(R.string.fac_Add_Contact));
                fragment.setArguments(bundle);
                mActivity.getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left).addToBackStack(null).add(R.id.fragmentContainer, fragment).commit();
            }
        });

        MaterialDesignTextView txtMenu = (MaterialDesignTextView) view.findViewById(R.id.menu_txtBack);
        RippleView rippleMenu = (RippleView) view.findViewById(R.id.menu_ripple_txtBack);
        rippleMenu.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                // close and remove fragment from stack

                //mActivity.getSupportFragmentManager().popBackStack();
                mActivity.onBackPressed();
                InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(rippleView.getWindowToken(), 0);
            }
        });

        Realm realm = Realm.getDefaultInstance();

        realmRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        realmRecyclerView.setItemViewCacheSize(100);
        realmRecyclerView.setItemAnimator(null);
        realmRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));

        results = realm.where(RealmContacts.class).findAllSorted(RealmContactsFields.DISPLAY_NAME);
        realmRecyclerView.setAdapter(new ContactListAdapter(results));

        StickyHeader stickyHeader = new StickyHeader(results);
        decoration = new StickyRecyclerHeadersDecoration(stickyHeader);
        realmRecyclerView.addItemDecoration(decoration);

        realm.close();

        /**
         * if contacts size is zero send request for get contacts list
         * for insuring that contacts not exist really or not
         */
        if (results.size() == 0) {
            new RequestUserContactsGetList().userContactGetList();
        }
    }


    private void hideProgress() {

        G.handler.post(new Runnable() {
            @Override
            public void run() {
                mActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                prgWaiting.setVisibility(View.GONE);
            }
        });
    }

    private void showProgress() {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                mActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                prgWaiting.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (FragmentActivity) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        hideProgress();
    }

    //********************************************************************************************

    public class ContactListAdapter extends RealmRecyclerViewAdapter<RealmContacts, ContactListAdapter.ViewHolder> {

        String lastHeader = "";
        int count;

        ContactListAdapter(RealmResults<RealmContacts> realmResults) {
            super(realmResults, true);
            count = realmResults.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private RealmContacts realmContacts;
            protected CircleImageView image;
            protected CustomTextViewMedium title;
            protected CustomTextViewMedium subtitle;
            protected View topLine;

            public ViewHolder(View view) {
                super(view);

                image = (CircleImageView) view.findViewById(R.id.imageView);
                title = (CustomTextViewMedium) view.findViewById(R.id.title);
                subtitle = (CustomTextViewMedium) view.findViewById(R.id.subtitle);
                topLine = (View) view.findViewById(R.id.topLine);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (isCallAction) {
                            mActivity.getSupportFragmentManager().popBackStack();

                            long userId = realmContacts.getId();
                            if (userId != 134 && G.userId != userId) {
                                FragmentCall.call(userId, false);
                            }


                        } else {
                            showProgress();

                            HelperPublicMethod.goToChatRoom(false, realmContacts.getId(), new HelperPublicMethod.Oncomplet() {
                                @Override
                                public void complete() {
                                    hideProgress();
                                    mActivity.getSupportFragmentManager().beginTransaction().remove(RegisteredContactsFragment.this).commit();
                                }
                            }, new HelperPublicMethod.OnError() {
                                @Override
                                public void error() {
                                    hideProgress();
                                }
                            });
                        }




                    }
                });
            }
        }

        @Override
        public ContactListAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = inflater.inflate(R.layout.contact_item, viewGroup, false);

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

            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final ContactListAdapter.ViewHolder viewHolder, int i) {

            RealmContacts contact = viewHolder.realmContacts = getItem(i);
            if (contact == null) {
                return;
            }

            String header = contact.getDisplay_name();

            if (lastHeader.isEmpty() || (!lastHeader.isEmpty() && !header.isEmpty() && lastHeader.toLowerCase().charAt(0) != header.toLowerCase().charAt(0))) {
                viewHolder.topLine.setVisibility(View.VISIBLE);
            } else {
                viewHolder.topLine.setVisibility(View.GONE);
            }

            lastHeader = header;

            viewHolder.title.setText(contact.getDisplay_name());
            Realm realm = Realm.getDefaultInstance();
            RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, contact.getId()).findFirst();
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

                if (HelperCalander.isLanguagePersian) {
                    viewHolder.subtitle.setText(viewHolder.subtitle.getText().toString());
                }
            }
            realm.close();

            hashMapAvatar.put(contact.getId(), viewHolder.image);
            setAvatar(viewHolder, contact.getId());
        }

        private void setAvatar(final ViewHolder holder, final long userId) {

            HelperAvatar.getAvatar(userId, HelperAvatar.AvatarType.USER, new OnAvatarGet() {
                @Override
                public void onAvatarGet(final String avatarPath, long ownerId) {
                    G.imageLoader.displayImage(AndroidUtils.suitablePath(avatarPath), hashMapAvatar.get(ownerId));
                }

                @Override
                public void onShowInitials(final String initials, final String color) {
                    hashMapAvatar.get(userId).setImageBitmap(net.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) holder.image.getContext().getResources().getDimension(R.dimen.dp60), initials, color));
                }
            });
        }
    }

    //********************************************************************************************

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
}

