/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.appcompat.widget.AppCompatTextView;

import net.iGap.R;
import net.iGap.module.CircleImageView;
import net.iGap.module.structs.StructCountry;

import java.util.ArrayList;

public class AdapterDialog extends BaseAdapter implements Filterable {

    public static int mSelectedVariation = -1;
    private ArrayList<StructCountry> countryList;
    private ArrayList<StructCountry> mStringFilterList;
    private ValueFilter valueFilter;

    public AdapterDialog(ArrayList<StructCountry> countryList) {
        this.countryList = countryList;
        mStringFilterList = countryList;
    }

    @Override
    public int getCount() {
        return countryList.size();
    }

    @Override
    public StructCountry getItem(int position) {
        return countryList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return countryList.indexOf(getItem(position));
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        StructCountry structCountry = countryList.get(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.rg_adapter_dialog, parent, false);
            viewHolder.countyImage = convertView.findViewById(R.id.countyImage);
            viewHolder.countryCode = convertView.findViewById(R.id.countryCode);
            viewHolder.countryName = convertView.findViewById(R.id.countryName);
            // Cache the viewHolder object inside the fresh view
            convertView.setTag(viewHolder);
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.countryCode.setText(structCountry.getCountryCode());
        viewHolder.countryName.setText(structCountry.getName());
        viewHolder.countyImage.setImageResource(R.mipmap.icon_rounded/*structCountry.getFlag()*/);
        viewHolder.countyImage.setVisibility(View.INVISIBLE);
        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (valueFilter == null) {
            valueFilter = new ValueFilter();
        }
        return valueFilter;
    }

    private class ValueFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint != null && constraint.length() > 0) {
                ArrayList<StructCountry> filterList = new ArrayList<>();
                for (int i = 0; i < mStringFilterList.size(); i++) {
                    if ((mStringFilterList.get(i).getName().toUpperCase()).contains(constraint.toString().toUpperCase())) {
                        StructCountry structCountry = new StructCountry();
                        structCountry.setId(mStringFilterList.get(i).getId());
                        structCountry.setName(mStringFilterList.get(i).getName());
                        structCountry.setCountryCode(mStringFilterList.get(i).getCountryCode());
                        structCountry.setPhonePattern(mStringFilterList.get(i).getPhonePattern());
                        structCountry.setAbbreviation(mStringFilterList.get(i).getAbbreviation());
                        filterList.add(structCountry);
                    }
                }
                results.count = filterList.size();
                results.values = filterList;
            } else {
                results.count = mStringFilterList.size();
                results.values = mStringFilterList;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            countryList = (ArrayList<StructCountry>) results.values;
            notifyDataSetChanged();
        }
    }

    private class ViewHolder {
        private CircleImageView countyImage;
        private AppCompatTextView countryName, countryCode;
    }

}
