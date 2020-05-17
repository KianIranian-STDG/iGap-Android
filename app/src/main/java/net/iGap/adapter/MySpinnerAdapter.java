package net.iGap.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.appcompat.widget.AppCompatTextView;

import net.iGap.R;
import net.iGap.model.paymentPackage.MciInternetPackageFilter;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MySpinnerAdapter extends BaseAdapter {

    private List<MciInternetPackageFilter> items;

    public MySpinnerAdapter(List<MciInternetPackageFilter> items) {
        this.items = items;
    }

    @Override
    public int getCount() {
        return items != null ? items.size() + 1 : 0;
    }

    @Override
    public MciInternetPackageFilter getItem(int position) {
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

        String tmp;
        if (position == 0) {
            tmp = holder.txtTitle.getContext().getString(R.string.buy_internet_package_choose_title);
        } else {
            if (items.get(position - 1).getCategory().getType().toLowerCase().equals("duration")) {
                tmp = String.format(holder.txtTitle.getContext().getString(getStringDurationResId(items.get(position - 1))), items.get(position - 1).getCategory().getValue());
            } else {
                tmp = String.format(holder.txtTitle.getContext().getString(getStringTrafficResId(items.get(position - 1))), items.get(position - 1).getCategory().getValue());
            }
        }

        holder.txtTitle.setText(tmp);
        return convertView;
    }

    private class viewHolder {
        AppCompatTextView txtTitle;
    }

    private int getStringTrafficResId(@NotNull MciInternetPackageFilter filter) {
        if (filter.getCategory().getSubType().equals("GB")) {
            return R.string.buy_internet_package_volume_filter_gb_title;
        } else {
            return R.string.buy_internet_package_volume_filter_mb_title;
        }
    }

    private int getStringDurationResId(MciInternetPackageFilter filter) {
        switch (filter.getCategory().getSubType()) {
            case "MONTH":
                return R.string.buy_internet_package_month_filter_title;
            case "DAY":
                return R.string.buy_internet_package_daily_filter_title;
            case "WEEK":
                return R.string.buy_internet_package_weekly_filter_title;
            case "YEAR":
                return R.string.buy_internet_package_year_filter_title;
            default:
                return R.string.buy_internet_package_daily_filter_title;
        }
    }

}
