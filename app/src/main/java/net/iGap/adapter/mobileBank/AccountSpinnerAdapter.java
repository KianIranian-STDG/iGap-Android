package net.iGap.adapter.mobileBank;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.appcompat.widget.AppCompatTextView;

import net.iGap.R;
import net.iGap.helper.HelperMobileBank;

import java.util.List;

public class AccountSpinnerAdapter extends BaseAdapter {

    private List<String> items;
    private boolean isCard;

    public AccountSpinnerAdapter(List<String> items, boolean isCard) {
        this.items = items;
        this.isCard = isCard;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public String getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        viewHolder holder;

        if (convertView == null) {

            holder = new viewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.spinner_item_custom, parent, false);

            holder.txtTitle = convertView.findViewById(R.id.item_title);
            convertView.setTag(holder);
        } else {
            holder = (viewHolder) convertView.getTag();
        }

        holder.txtTitle.setGravity(Gravity.CENTER);
        holder.txtTitle.setText(/*isCard ? HelperMobileBank.getCardNumberPattern(items.get(position)) :*/ HelperMobileBank.checkNumbersInMultiLangs(items.get(position)));
        return convertView;
    }

    private class viewHolder {
        AppCompatTextView txtTitle;
    }

}
