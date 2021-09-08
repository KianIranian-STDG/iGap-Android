package net.iGap.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.appcompat.widget.AppCompatTextView;

import net.iGap.R;
import net.iGap.model.paymentPackage.InternetPackageFilter;

import java.util.List;

public class PackagesFilterSpinnerAdapter extends BaseAdapter {

    private List<InternetPackageFilter> items;

    public PackagesFilterSpinnerAdapter(List<InternetPackageFilter> items) {
        this.items = items;
    }

    @Override
    public int getCount() {
        return items != null ? items.size() + 1 : 0;
    }

    @Override
    public InternetPackageFilter getItem(int position) {
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

        String tmp;
        if (position == 0) {
            tmp = holder.txtTitle.getContext().getString(R.string.buy_internet_package_choose_title);
        } else {
            tmp = items.get(position - 1).getTitle();
        }

        holder.txtTitle.setText(tmp);
        return convertView;
    }

    private class viewHolder {
        AppCompatTextView txtTitle;
    }

}
