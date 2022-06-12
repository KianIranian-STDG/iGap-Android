package net.iGap.adapter.igahst;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.messenger.theme.Theme;
import net.iGap.model.igasht.IGashtProvince;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ProvinceSuggestionListAdapter extends ArrayAdapter<IGashtProvince> {

    private List<IGashtProvince> items;
    private List<IGashtProvince> itemsAll;

    public ProvinceSuggestionListAdapter(Context context, List<IGashtProvince> items) {
        super(context, R.layout.custom_row_igasht_province_suggestion_list, items);
        this.items = items;
        this.itemsAll = new ArrayList<>();
        this.itemsAll.addAll(items);
    }

    @NotNull
    @Override
    public View getView(int position, View convertView, @NotNull ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.custom_row_igasht_province_suggestion_list, null);
            v.setBackgroundColor(Theme.getColor(Theme.key_window_background));
        }
        TextView customerNameLabel = v.findViewById(R.id.item_title);
        customerNameLabel.setTextColor(Theme.getColor(Theme.key_title_text));
        if (customerNameLabel != null) {
            customerNameLabel.setText(getProvinceTitle(items.get(position)));
        }
        return v;
    }

    @NotNull
    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    private Filter nameFilter = new Filter() {
        @Override
        public String convertResultToString(Object resultValue) {
            return getProvinceTitle((IGashtProvince) resultValue);
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                ArrayList<IGashtProvince> tmp = new ArrayList<>();
                for (IGashtProvince customer : itemsAll) {
                    if (getProvinceTitle(customer).toLowerCase().startsWith(constraint.toString().toLowerCase())) {
                        tmp.add(customer);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = tmp;
                filterResults.count = tmp.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            ArrayList<IGashtProvince> filteredList = (ArrayList<IGashtProvince>) results.values;
            if (results.count > 0) {
                items.clear();
                items.addAll(filteredList);
                notifyDataSetChanged();
            }
        }
    };

    private String getProvinceTitle(IGashtProvince province) {
        switch (G.selectedLanguage) {
            case "en":
                return province.getEnglishName();
            default:
                return province.getProvinceName();
        }
    }
}
