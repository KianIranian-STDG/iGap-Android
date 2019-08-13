package net.iGap.adapter;

import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import net.iGap.R;
import net.iGap.internetpackage.TypeFilter;

public class MySpinnerAdapter extends BaseAdapter {

    private TypeFilter items;

    public MySpinnerAdapter(TypeFilter items) {
        this.items = items;
        Log.wtf(this.getClass().getName(), "Count: " + items.getThreshold());
    }

    @Override
    public int getCount() {
        return items != null ? items.getFilterList().size() + 1 : 0;
    }

    @Override
    public String getItem(int position) {
        return items.getFilterList().get(position);
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

        String tmp;
        if (position == 0) {
            tmp = holder.txtTitle.getContext().getString(R.string.buy_internet_package_choose_title);
        } else {
            if (items.isDaily()) {
                tmp = String.format(holder.txtTitle.getContext().getString(R.string.buy_internet_package_daily_filter_title), items.getFilterList().get(position - 1));
            } else {
                if (position < items.getThreshold()) {
                    tmp = String.format(holder.txtTitle.getContext().getString(R.string.buy_internet_package_volume_filter_mb_title), items.getFilterList().get(position - 1));
                } else {
                    tmp = String.format(holder.txtTitle.getContext().getString(R.string.buy_internet_package_volume_filter_gb_title), items.getFilterList().get(position - 1));
                }
            }
        }

        holder.txtTitle.setText(tmp);
        return convertView;
    }

    private class viewHolder {
        AppCompatTextView txtTitle;
    }

}
