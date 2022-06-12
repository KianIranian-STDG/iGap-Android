package net.iGap.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;
import net.iGap.activities.ActivityMain;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperPermission;
import net.iGap.helper.LayoutCreator;
import net.iGap.messenger.theme.Theme;
import net.iGap.messenger.theme.ThemeDescriptor;
import net.iGap.messenger.ui.cell.ContactCell;
import net.iGap.messenger.ui.cell.EmptyCell;
import net.iGap.messenger.ui.cell.TextCell;
import net.iGap.messenger.ui.fragments.InviteContactFragment;
import net.iGap.messenger.ui.toolBar.Toolbar;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.accountManager.DbManager;
import net.iGap.module.customView.RecyclerListView;
import net.iGap.module.dialog.bottomsheet.BottomSheetFragment;
import net.iGap.observers.interfaces.OnGetPermission;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoSignalingOffer;
import net.iGap.realm.RealmContacts;
import net.iGap.request.RequestUserContactsDelete;

import java.util.ArrayList;
import java.util.List;

import static net.iGap.helper.ContactManager.CONTACT_LIMIT;

public class ContactFragment extends BaseFragment {
    public static final String CONTACT_TYPE = "contact_type";
    public static final String CONTACT = "contact";
    public static final String PROFILE = "profile";
    public static final String CHAT = "chat";
    public static final String CALL = "call";
    private RecyclerListView recyclerView;
    private List<RealmContacts> realmContacts = new ArrayList<>();
    private boolean isMultiSelectEnable = false;
    private ContactAdapter contactAdapter;

    public static ContactFragment newInstance(String contactType) {
        Bundle bundle = new Bundle();
        bundle.putString(CONTACT_TYPE, contactType);
        ContactFragment fragment = new ContactFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View createView(Context context) {
        fragmentView = new FrameLayout(context);
        fragmentView.setBackgroundColor(Theme.getColor(Theme.key_window_background));
        FrameLayout frameLayout = (FrameLayout) fragmentView;
        recyclerView = new RecyclerListView(context);
        recyclerView.setClipToPadding(false);
        recyclerView.setPadding(0, 0, 0, LayoutCreator.dp(20));
        recyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        frameLayout.addView(recyclerView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT));


        LoadContact loadContact = new LoadContact() {
            @Override
            public void onLoadContact(List<RealmContacts> realmContacts) {
                String contactType = getArguments().getSerializable(CONTACT_TYPE).toString();
                contactAdapter = new ContactAdapter(context, realmContacts);
                recyclerView.setAdapter(contactAdapter);
                recyclerView.setOnItemClickListener((view, position) -> {

                    switch (contactType) {
                        case CALL:
                            if (view instanceof ContactCell) {
                                long userId = realmContacts.get(position - 2).getId();
                                if (userId != 134 && AccountManager.getInstance().getCurrentUser().getId() != userId) {
                                    CallSelectFragment callSelectFragment = CallSelectFragment.getInstance(userId, false, ProtoSignalingOffer.SignalingOffer.Type.VOICE_CALLING);
                                    callSelectFragment.show(getFragmentManager(), null);
                                }
                            } else if (view instanceof TextCell) {
                                addNewContact();
                            }
                            break;
                        case CONTACT:
                            if (view instanceof TextCell) {
                                inviteFriend();
                            } else if (view instanceof ContactCell) {
                                if (isMultiSelectEnable) {
                                    ContactCell contactCell = (ContactCell) view;
                                    contactCell.setCheck();
                                }
                            }
                            break;
                        case CHAT:
                        case PROFILE:
                            if (view instanceof TextCell) {
                                if (position == 0) {
                                    addNewChannel();
                                } else if (position == 1) {
                                    addNewGroup();
                                }
                            } else if (view instanceof ContactCell) {
                                if (isMultiSelectEnable) {
                                    ContactCell contactCell = (ContactCell) view;
                                    contactCell.setCheck();
                                }
                            }
                            break;
                    }
                });
                recyclerView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position) {
                        ContactCell contactCell = (ContactCell) view;
                        if (!isMultiSelectEnable && !contactType.equals(CALL)) {
                            RealmContacts contacts = realmContacts.get(contactType.equals(CONTACT) ? position - 2 : position - 3);
                            showDialogContactLongClicked(position, contactCell, contacts.getId(), contacts.getPhone(), contacts.getFirst_name(), contacts.getLast_name());
                            return true;
                        }
                        return false;
                    }
                });
            }
        };
        getContactList(loadContact);
        return fragmentView;
    }

    private void addNewGroup() {
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
    }

    private void addNewChannel() {
        if (getActivity() != null) {
            FragmentNewGroup fragment = FragmentNewGroup.newInstance();
            Bundle bundle_ = new Bundle();
            bundle_.putString("TYPE", "NewChanel");
            fragment.setArguments(bundle_);
            new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setReplace(false).load();
        }
    }

    private void inviteFriend() {
        try {
            HelperPermission.getContactPermission(getContext(), new OnGetPermission() {
                @Override
                public void Allow() {
                    new HelperFragment(getActivity().getSupportFragmentManager(), new InviteContactFragment()).setReplace(false).load();
                }

                @Override
                public void deny() {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addNewContact() {
        if (getActivity() != null) {
            FragmentAddContact fragment = FragmentAddContact.newInstance(
                    null, FragmentAddContact.ContactMode.ADD
            );
            new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setReplace(false).load();
        }
    }

    @Override
    public View createToolBar(Context context) {
        Toolbar toolbar = new Toolbar(context);
        toolbar.setTitle("Contact");
        return toolbar;
    }

    @Override
    public List<ThemeDescriptor> getThemeDescriptor() {
        List<ThemeDescriptor> themeDescriptors = new ArrayList<>();
        themeDescriptors.add(new ThemeDescriptor(recyclerView, ThemeDescriptor.FLAG_CELLBACKGROUNDCOLOR, Theme.key_window_background));
        themeDescriptors.add(new ThemeDescriptor(recyclerView, ThemeDescriptor.FLAG_BACKGROUND, Theme.key_window_background));
        themeDescriptors.add(new ThemeDescriptor(recyclerView, ThemeDescriptor.FLAG_TEXTCOLOR, Theme.key_default_text));
        themeDescriptors.add(new ThemeDescriptor(recyclerView, ThemeDescriptor.FLAG_IMAGECOLOR, Theme.key_default_text));
        themeDescriptors.add(new ThemeDescriptor(recyclerView, ThemeDescriptor.FLAG_PROGRESSBAR, Theme.key_default_text));
        themeDescriptors.add(new ThemeDescriptor(toolbar, ThemeDescriptor.FLAG_BACKGROUND, Theme.key_toolbar_background));
        return themeDescriptors;
    }

    private void getContactList(LoadContact loadContact) {
        realmContacts = DbManager.getInstance().doRealmTask(realm -> {
            return realm.copyFromRealm(realm.where(RealmContacts.class).limit(CONTACT_LIMIT).sort("display_name").findAll());
        });
        loadContact.onLoadContact(realmContacts);
    }

    private void showDialogContactLongClicked(int selectedPosition, ContactCell contactCell, long id, long phone, String name, String family) {
        if (getFragmentManager() != null) {
            List<String> items = new ArrayList<>();
            items.add(getString(R.string.edit));
            items.add(getString(R.string.delete));
            items.add(getString(R.string.mark_as_several));

            new BottomSheetFragment().setData(items, -1, position -> {
                if (position == 0) {
                    EditContactFragment fragment = EditContactFragment.newInstance(id, phone, name, family);
                    if (getActivity() != null)
                        new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setReplace(false).load();
                } else if (position == 1) {
                    new RequestUserContactsDelete().contactsDelete("" + phone);
                    //this part should change according new implementation and after on success delete request remove from list
                    realmContacts.remove(selectedPosition);
                    contactAdapter.notifyItemRemoved(selectedPosition);
                } else if (position == 2) {
                    //after complete toolbar implementation should complete this section that we can disable multi select and we can delete contact that we selected
                    isMultiSelectEnable = true;
                    contactAdapter.notifyDataSetChanged();
                    contactCell.setCheck();
                }
            }).show(getFragmentManager(), "contactLongClicked");
        }
    }

    public interface LoadContact {
        void onLoadContact(List<RealmContacts> realmContacts);
    }

    public class ContactAdapter extends RecyclerListView.SelectionAdapter {
        private final Context context;
        private final String contactType;
        private final List<RealmContacts> realmContacts;

        public ContactAdapter(Context context, List<RealmContacts> realmContacts) {
            this.context = context;
            this.realmContacts = realmContacts;
            contactType = getArguments().getSerializable(CONTACT_TYPE).toString();
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int viewType = holder.getItemViewType();
            return viewType != 1;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View cellView = null;
            switch (viewType) {
                case 0:
                    TextCell textCell = new TextCell(context);
                    cellView = textCell;
                    break;
                case 1:
                    EmptyCell emptyCell = new EmptyCell(context);
                    cellView = emptyCell;
                    break;
                case 2:
                    ContactCell contactCell = new ContactCell(context, contactType);
                    cellView = contactCell;
                    break;
            }
            return new RecyclerListView.Holder(cellView);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            int viewType = holder.getItemViewType();
            switch (viewType) {
                case 0:
                    TextCell textCell = (TextCell) holder.itemView;
                    textCell.setBackgroundColor(Theme.getColor(Theme.key_window_background));
                   /* if (contactType.equals(ContactFragment.CHAT) || contactType.equals(ContactFragment.PROFILE)) {
                        if (position == 0) {
                            textCell.setText("کانال جدید");
                            textCell.setIconRes(R.string.ic_new_channel);
                        } else if (position == 1) {
                            textCell.setText("گروه جدید");
                            textCell.setIconRes(R.string.ic_new_group);
                        }
                    } else if (contactType.equals(ContactFragment.CALL) && position == 0) {
                        textCell.setText("افزودن مخاطب");
                        textCell.setIconRes(R.string.ic_add_contact);
                    } else if (contactType.equals(ContactFragment.CONTACT) && position == 0) {
                        textCell.setText("دعوت کردن دوستان");
                        textCell.setIconRes(R.string.ic_invite_friend);
                    }*/
                    break;
                case 1:
                    EmptyCell emptyCell = (EmptyCell) holder.itemView;
                    emptyCell.setBackgroundColor(Theme.getColor(Theme.key_window_background));
                    break;
                case 2:
                    ContactCell contactCell = (ContactCell) holder.itemView;
                    contactCell.setMultiSelect(isMultiSelectEnable);
                    contactCell.setBackgroundColor(Theme.getColor(Theme.key_window_background));
                    RealmContacts contacts = new RealmContacts();
                    if (contactType.equals(ContactFragment.CHAT) || contactType.equals(ContactFragment.PROFILE)) {
                        contacts = realmContacts.get(position - 3);
                    } else {
                        contacts = realmContacts.get(position - 2);
                    }
                    contactCell.setValue(contacts);
                    break;
            }
        }

        @Override
        public int getItemViewType(int position) {
            switch (contactType) {
                case ContactFragment.CHAT:
                case ContactFragment.PROFILE:
                    if (position == 0 || position == 1)
                        return 0;
                    else if (position == 2)
                        return 1;
                    break;
                case ContactFragment.CALL:
                case ContactFragment.CONTACT:
                    if (position == 0)
                        return 0;
                    else if (position == 1)
                        return 1;
                    break;
            }
            return 2;
        }

        @Override
        public int getItemCount() {
            int size = realmContacts.size();
            if (contactType.equals(ContactFragment.CHAT) || contactType.equals(ContactFragment.PROFILE)) {
                size += 3;
            } else {
                size += 2;
            }
            return size;
        }
    }
}
