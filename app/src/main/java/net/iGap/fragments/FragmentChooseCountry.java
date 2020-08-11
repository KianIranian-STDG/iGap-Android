package net.iGap.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mikepenz.fastadapter.IItemAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperToolbar;
import net.iGap.module.CountryListComparator;
import net.iGap.module.CountryReader;
import net.iGap.module.CustomTextViewMedium;
import net.iGap.module.structs.StructCountry;
import net.iGap.observers.interfaces.ToolbarListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static net.iGap.fragments.FragmentAddContact.onCountryCallBack;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentChooseCountry extends BaseFragment implements ToolbarListener {
    private ArrayList<StructCountry> structCountryArrayList = new ArrayList();
    private ArrayList<StructCountry> items = new ArrayList<>();
    private long index = 1500;
    private RecyclerView rcvChooseCountry;
    private HelperToolbar mHelperToolbar;
    private FastItemAdapter fastItemAdapter;

    public FragmentChooseCountry() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return attachToSwipeBack(inflater.inflate(R.layout.fragment_choose_country, container, false));

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewGroup root = view.findViewById(R.id.rootChooseCountry);
        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mHelperToolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLifecycleOwner(getViewLifecycleOwner())
                .setLeftIcon(R.string.back_icon)
                .setLogoShown(true)
                .setDefaultTitle(getContext().getResources().getString(R.string.Select_Country))
                .setSearchBoxShown(true)
                .setListener(this);

        ViewGroup layoutToolbar = view.findViewById(R.id.fcc_layout_toolbar);
        layoutToolbar.addView(mHelperToolbar.getView());

        fastItemAdapter = new FastItemAdapter();
        items = getLIstCountry();
        for (int i = 0; i < items.size(); i++) {
            fastItemAdapter.add(new AdapterChooseCountry(items.get(i)).withIdentifier(index++));
        }
        rcvChooseCountry = view.findViewById(R.id.rcvChooseCountry);
        rcvChooseCountry.setItemViewCacheSize(1000);
        rcvChooseCountry.setItemAnimator(null);
        rcvChooseCountry.setLayoutManager(new LinearLayoutManager(G.fragmentActivity));
        rcvChooseCountry.setNestedScrollingEnabled(false);

        rcvChooseCountry.setAdapter(fastItemAdapter);

        StickyHeader stickyHeader = new StickyHeader(items);
        StickyRecyclerHeadersDecoration decoration = new StickyRecyclerHeadersDecoration(stickyHeader);
        rcvChooseCountry.addItemDecoration(decoration);

        fastItemAdapter.getItemFilter().withFilterPredicate(new IItemAdapter.Predicate<AdapterChooseCountry>() {
            @Override
            public boolean filter(AdapterChooseCountry item, CharSequence constraint) {
                return item.item.getName().toLowerCase().contains(constraint.toString().toLowerCase());

            }
        });


    }

    private ArrayList<StructCountry> getLIstCountry() {
        /**
         * list of country
         */

        CountryReader countryReade = new CountryReader();
        StringBuilder fileListBuilder = countryReade.readFromAssetsTextFile("country.txt", G.fragmentActivity);

        String list = fileListBuilder.toString();
        // Split line by line Into array
        String[] listArray = list.split("\\r?\\n");
        final String[] countryNameList = new String[listArray.length];
        //Convert array
        for (int i = 0; listArray.length > i; i++) {
            StructCountry structCountry = new StructCountry();

            String[] listItem = listArray[i].split(";");
            structCountry.setId(i);
            structCountry.setCountryCode(listItem[0]);
            structCountry.setAbbreviation(listItem[1]);
            structCountry.setName(listItem[2]);

            if (listItem.length > 3) {
                structCountry.setPhonePattern(listItem[3]);
            } else {
                structCountry.setPhonePattern(" ");
            }

            structCountryArrayList.add(structCountry);
        }

        Collections.sort(structCountryArrayList, new CountryListComparator());
        return structCountryArrayList;
    }

    private class StickyHeader implements StickyRecyclerHeadersAdapter {

        private ArrayList<StructCountry> item;

        StickyHeader(ArrayList<StructCountry> realmResults) {
            this.item = realmResults;
        }

        @Override
        public long getHeaderId(int position) {
            return item.get(position).getName().toUpperCase().charAt(0);
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
            textView.setText(items.get(position).getName().toUpperCase().substring(0, 1));
            textView.setPadding(getDimen(R.dimen.dp20), getDimen(R.dimen.dp20), getDimen(R.dimen.dp20), getDimen(R.dimen.dp20));
        }

        private int getDimen(int id) {

            return (int) (G.context.getResources().getDimension(id) / G.context.getResources().getDisplayMetrics().density);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }

    public class AdapterChooseCountry extends AbstractItem<AdapterChooseCountry, AdapterChooseCountry.ViewHolder> {

        public StructCountry item;

        //public String getItem() {
        //    return item;
        //}

        public AdapterChooseCountry(StructCountry item) {
            this.item = item;
        }


        //The unique ID for this type of item
        @Override
        public int getType() {
            return R.id.rootAdapterChooseCountry;
        }

        //The layout to be used for this type of item
        @Override
        public int getLayoutRes() {
            return R.layout.adapter_list_country;
        }

        //The logic to bind your data to the view

        @Override
        public void bindView(ViewHolder holder, List payloads) {
            super.bindView(holder, payloads);


            holder.txtNameCountry.setText(item.getName());
            holder.txtCodeCountry.setText(item.getCountryCode());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onCountryCallBack != null) {
                        removeFromBaseFragment(FragmentChooseCountry.this);
                        closeKeyboard(v);
                        FragmentAddContact.onCountryCallBack.countryName(item.getName(), item.getCountryCode(), item.getPhonePattern());
                    }
                }
            });

        }

        @Override
        public ViewHolder getViewHolder(View v) {
            return new ViewHolder(v);
        }

        //The viewHolder used for this item. This viewHolder is always reused by the RecyclerView so scrolling is blazing fast
        protected class ViewHolder extends RecyclerView.ViewHolder {


            private TextView txtNameCountry;
            private TextView txtCodeCountry;
            private ViewGroup vgListCountry;

            public ViewHolder(View view) {
                super(view);

                txtNameCountry = view.findViewById(R.id.txtNameCountry);
                txtCodeCountry = view.findViewById(R.id.txtCodeCountry);
                vgListCountry = view.findViewById(R.id.vgListCountry);

            }
        }
    }

    @Override
    public void onLeftIconClickListener(View view) {
        removeFromBaseFragment(FragmentChooseCountry.this);
    }

    @Override
    public void onSearchClickListener(View view) {
        openKeyBoard();
    }

    @Override
    public void onBtnClearSearchClickListener(View view) {

    }

    @Override
    public void onSearchTextChangeListener(View view, String text) {
        fastItemAdapter.filter(text);

    }
}
