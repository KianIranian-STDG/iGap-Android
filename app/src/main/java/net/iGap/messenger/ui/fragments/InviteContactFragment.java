package net.iGap.messenger.ui.fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.AsyncTask;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.InviteContactAdapter;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.LayoutCreator;
import net.iGap.messenger.ui.toolBar.Toolbar;
import net.iGap.module.Contacts;
import net.iGap.module.EndlessRecyclerViewScrollListener;
import net.iGap.module.accountManager.DbManager;
import net.iGap.module.customView.RecyclerListView;
import net.iGap.module.structs.StructListOfContact;
import net.iGap.observers.interfaces.OnPhoneContact;
import net.iGap.realm.RealmContacts;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmResults;

public class InviteContactFragment extends BaseFragment implements OnPhoneContact {

    private final List<StructListOfContact> phoneContactsList = new ArrayList<>();
    private RecyclerListView recyclerView;
    private InviteContactAdapter adapter;
    private ProgressBar loadingPb;
    private TextView tvEmptySpace;

    private int recyclerRowsHeight = 0;
    private int deviceScreenHeight = 0;

    @Override
    public View createView(Context context) {
        fragmentView = new FrameLayout(context);
        FrameLayout root = (FrameLayout) fragmentView;
        G.onPhoneContact = this;
        Contacts.localPhoneContactId = 0;
        Contacts.getContact = true;

        recyclerRowsHeight = getResources().getDimensionPixelSize(R.dimen.dp60);
        deviceScreenHeight = getDeviceHeight();

        recyclerView = new RecyclerListView(context);
        adapter = new InviteContactAdapter(context);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (Contacts.isEndLocal)
                    return;
                loadingPb.setVisibility(View.VISIBLE);
                new Contacts.FetchContactForClient().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });
        root.addView(recyclerView, LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT);


        loadingPb = new ProgressBar(context);
        root.addView(loadingPb, LayoutCreator.createFrame(32, 32, Gravity.BOTTOM | Gravity.CENTER, 0, 0, 0, 8));


        tvEmptySpace = new TextView(context);
        tvEmptySpace.setText("هیچ مخاطبی ندارید!");
        tvEmptySpace.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        tvEmptySpace.setTextColor(Color.BLACK);
        tvEmptySpace.setTypeface(ResourcesCompat.getFont(context, R.font.main_font_bold));
        tvEmptySpace.setVisibility(View.GONE);
        root.addView(tvEmptySpace, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, Gravity.CENTER));

        return root;
    }


    @Override
    public void onStart() {
        super.onStart();
        new Contacts.FetchContactForClient().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private int getDeviceHeight() {
        if (getActivity() != null) {
            Display display = getActivity().getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            return size.y;
        } else
            return -1;
    }


    @Override
    public View createToolBar(Context context) {
        Toolbar toolbar = new Toolbar(context);
        toolbar.setTitle(getString(R.string.InviteFriends));

        toolbar.setListener(i -> {

        });
        return toolbar;
    }

    @Override
    public void onPhoneContact(ArrayList<StructListOfContact> contacts, boolean isEnd) {
        new AddAsync(contacts).execute();
    }

    //TODO check this out mr abbasi, it says momkene memory leak bede
    private class AddAsync extends AsyncTask<Void, Void, ArrayList<StructListOfContact>> {
        private final ArrayList<StructListOfContact> contacts;

        public AddAsync(ArrayList<StructListOfContact> contacts) {
            this.contacts = contacts;
        }

        @Override
        protected ArrayList<StructListOfContact> doInBackground(Void... params) {
            for (int i = 0; i < contacts.size(); i++) {

                String s = contacts.get(i).getPhone();
                s = s.replaceAll("\\A0|\\+|-?", "");
                if (s.contains(" "))
                    s = s.replace(" ", "");
                if (!s.startsWith("98"))
                    s = "98" + s;
                contacts.get(i).setPhone(s);
            }
            return DbManager.getInstance().doRealmTask(realm -> {
                RealmResults<RealmContacts> mList = realm.where(RealmContacts.class).findAll().sort("display_name");

                ArrayList<StructListOfContact> slc = new ArrayList<>();

                for (int i = 0; i < contacts.size(); i++) {
                    boolean helpIndex = false;
                    for (int j = 0; j < mList.size(); j++) {
                        if (contacts.get(i).getPhone().equalsIgnoreCase(String.valueOf(mList.get(j).getPhone()))) {
                            helpIndex = true;
                            break;
                        }
                    }
                    if (!phoneContactsList.contains(contacts.get(i)) && !helpIndex)
                        slc.add(contacts.get(i));
                }
                return slc;
            });

        }

        @Override
        protected void onPostExecute(ArrayList<StructListOfContact> slc) {
            phoneContactsList.addAll(slc);
            adapter.setContacts(phoneContactsList);
            adapter.notifyDataSetChanged();
            loadingPb.setVisibility(View.GONE);

            int minItem = deviceScreenHeight / recyclerRowsHeight;
            if (minItem < 4) minItem = 5;

            if (phoneContactsList.size() < minItem && !Contacts.isEndLocal) {
                loadingPb.setVisibility(View.VISIBLE);
                new Contacts.FetchContactForClient().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }

            checkLocalContactIsEmpty(phoneContactsList);
            super.onPostExecute(slc);
        }
    }

    private void checkLocalContactIsEmpty(List<StructListOfContact> contacts) {

        if (Contacts.isEndLocal && contacts.size() == 0) {
            tvEmptySpace.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            tvEmptySpace.setVisibility(View.GONE);
        }

    }
}
