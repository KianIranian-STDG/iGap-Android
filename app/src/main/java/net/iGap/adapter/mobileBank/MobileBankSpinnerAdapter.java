package net.iGap.adapter.mobileBank;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

import net.iGap.R;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperMobileBank;
import net.iGap.model.mobileBank.BankCardModel;

import java.util.ArrayList;
import java.util.List;

public class MobileBankSpinnerAdapter extends ArrayAdapter<BankCardModel> {

    private List<BankCardModel> cards;
    private ListFilter listFilter = new ListFilter();
    private List<BankCardModel> dataListAllItems;

    public MobileBankSpinnerAdapter(Context mContext, int layoutResourceId, List<BankCardModel> data) {
        super(mContext, layoutResourceId, data);
        if (cards == null)
            cards = new ArrayList<>();
        cards.addAll(data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View layout = convertView;

        if (layout == null) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            layout = inflater.inflate(R.layout.mobile_bank_preview_spinner_item, parent, false);
        }

        TextView bankName = layout.findViewById(R.id.title);
        TextView cardNum = layout.findViewById(R.id.cardNumber);
        ImageView bankLogo = layout.findViewById(R.id.logo);

        bankName.setText(HelperMobileBank.bankName(cards.get(position).getPan()));
        cardNum.setText(checkAndSetPersianNumberIfNeeded(cards.get(position).getPan()));
        bankLogo.setImageResource(HelperMobileBank.bankLogo(cards.get(position).getPan()));

        return layout;
    }

    private String checkAndSetPersianNumberIfNeeded(String cardNumber) {
        String number = cardNumber;
        if (HelperCalander.isPersianUnicode)
            number = HelperCalander.convertToUnicodeFarsiNumber(cardNumber);
        try {
            String[] tempArray = Iterables.toArray(Splitter.fixedLength(4).split(number), String.class);
            return tempArray[0] + " - " + tempArray[1] + " - " + tempArray[2] + " - " + tempArray[3];
        } catch (Exception e) {
            return number;
        }
    }

    @Override
    public int getCount() {
        return cards.size();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Nullable
    @Override
    public BankCardModel getItem(int position) {
        return cards.get(position);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return listFilter;
    }

    public class ListFilter extends Filter {
        private Object lock = new Object();

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();
            if (dataListAllItems == null) {
                synchronized (lock) {
                    dataListAllItems = new ArrayList<>(cards);
                }
            }

            if (prefix == null || prefix.length() == 0) {
                synchronized (lock) {
                    results.values = dataListAllItems;
                    results.count = dataListAllItems.size();
                }
            } else {
                final String searchStrLowerCase = prefix.toString().toLowerCase();

                ArrayList<BankCardModel> matchValues = new ArrayList<>();

                for (BankCardModel dataItem : dataListAllItems) {
                    if (dataItem.getPan().toLowerCase().contains(searchStrLowerCase)) {
                        matchValues.add(dataItem);
                    }
                }

                results.values = matchValues;
                results.count = matchValues.size();
                Log.d("amini", "performFiltering: " + matchValues.size());
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.values != null) {
                cards = (ArrayList<BankCardModel>) results.values;
            } else {
                cards = null;
            }
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }

    }
}
