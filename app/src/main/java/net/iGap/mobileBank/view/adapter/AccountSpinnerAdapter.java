package net.iGap.mobileBank.view.adapter;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.appcompat.widget.AppCompatTextView;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

import net.iGap.R;
import net.iGap.helper.HelperCalander;

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

            holder.txtTitle = convertView.findViewById(R.id.itemTitle);
            convertView.setTag(holder);
        } else {
            holder = (viewHolder) convertView.getTag();
        }

        holder.txtTitle.setGravity(Gravity.CENTER);
        holder.txtTitle.setText(checkAndSetPersianNumberIfNeeded(items.get(position)));
        return convertView;
    }


    private String checkAndSetPersianNumberIfNeeded(String cardNumber) {
        String number = cardNumber;
        if (HelperCalander.isPersianUnicode)
            number = HelperCalander.convertToUnicodeFarsiNumber(cardNumber);
        if (isCard) {
            try {
                String[] tempArray = Iterables.toArray(Splitter.fixedLength(4).split(number), String.class);
                return tempArray[0] + " - " + tempArray[1] + " - " + tempArray[2] + " - " + tempArray[3];
            } catch (Exception e) {
                return number;
            }
        } else {
            return number;
        }
    }

    private class viewHolder {
        AppCompatTextView txtTitle;
    }

}
