package net.iGap.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.appcompat.widget.AppCompatTextView;

import net.iGap.R;
import net.iGap.model.paymentPackage.InternetPackage;

import java.util.List;

public class InternetPackageListAdapter extends BaseAdapter {

    private List<InternetPackage> items;

    public InternetPackageListAdapter(List<InternetPackage> items) {
        this.items = items;
    }

    @Override
    public int getCount() {
        return items != null ? items.size() + 1 : 0;
    }

    @Override
    public InternetPackage getItem(int position) {
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
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.spinner_item_internet_package, parent, false);

            holder.txtTitle = convertView.findViewById(R.id.title);
            holder.price = convertView.findViewById(R.id.price);
            convertView.setTag(holder);
        } else {
            holder = (viewHolder) convertView.getTag();
        }
        if (position == 0) {
            holder.txtTitle.setText(holder.txtTitle.getContext().getString(R.string.buy_internet_package_choose_title));
            holder.price.setVisibility(View.GONE);
        } else {
            holder.txtTitle.setText(items.get(position - 1).getDescription());
            holder.price.setText(items.get(position - 1).getCost() + " " + holder.price.getContext().getString(R.string.rial));
            holder.price.setVisibility(View.VISIBLE);
        }
        return convertView;
    }

    private class viewHolder {
        AppCompatTextView txtTitle;
        AppCompatTextView price;
    }
}
