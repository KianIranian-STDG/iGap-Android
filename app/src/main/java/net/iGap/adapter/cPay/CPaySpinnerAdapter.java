package net.iGap.adapter.cPay;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.appcompat.widget.AppCompatTextView;

import net.iGap.R;

public class CPaySpinnerAdapter extends BaseAdapter {

    private String[] items;

    public CPaySpinnerAdapter(String[] items) {
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.length;
    }

    @Override
    public String getItem(int position) {
        return items[position];
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
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.spinner_item_without_theme, parent, false);

            holder.txtTitle = convertView.findViewById(R.id.itemTitle);
            convertView.setTag(holder);
        } else {
            holder = (viewHolder) convertView.getTag();
        }


        holder.txtTitle.setText("\t" + items[position] + "\t");
        return convertView;
    }

    private class viewHolder {
        AppCompatTextView txtTitle;
    }

}
