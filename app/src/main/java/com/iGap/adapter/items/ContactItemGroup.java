package com.iGap.adapter.items;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;

import com.iGap.R;
import com.iGap.helper.HelperImageBackColor;
import com.iGap.module.CircleImageView;
import com.iGap.module.CustomTextViewMedium;
import com.iGap.module.StructContactInfo;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;

import java.util.List;


/**
 * Contact item used with FastAdapter for Navigation drawer contacts fragment.
 */
public class ContactItemGroup extends AbstractItem<ContactItemGroup, ContactItemGroup.ViewHolder> {
    private static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();
    public StructContactInfo mContact;

    public ContactItemGroup setContact(StructContactInfo contact) {
        this.mContact = contact;
        return this;
    }

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.contact_item_group;
    }

    @Override
    public void bindView(ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);

        if (mContact.isHeader) {
            holder.topLine.setVisibility(View.VISIBLE);
        } else {
            holder.topLine.setVisibility(View.GONE);
        }

        if (mContact.isSelected) {
            holder.checkBoxSelect.setVisibility(View.VISIBLE);
        } else {
            holder.checkBoxSelect.setVisibility(View.INVISIBLE);
        }

        holder.title.setText(mContact.displayName);

        holder.subtitle.setText(mContact.status);

        // TODO: 9/16/2016   code for image picture 

        String name = HelperImageBackColor.getFirstAlphabetName(mContact.displayName);
        holder.image.setImageBitmap(HelperImageBackColor.drawAlphabetOnPicture(holder.image.getLayoutParams().width, name, HelperImageBackColor.getColor(name)));


    }

    protected static class ItemFactory implements ViewHolderFactory<ViewHolder> {
        public ViewHolder create(View v) {
            return new ViewHolder(v);
        }
    }

    @Override
    public ViewHolderFactory<? extends ViewHolder> getFactory() {
        return FACTORY;
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        protected CircleImageView image;
        protected CustomTextViewMedium title;
        protected CustomTextViewMedium subtitle;
        protected View topLine;
        protected CheckBox checkBoxSelect;

        public ViewHolder(View view) {
            super(view);

            image = (CircleImageView) view.findViewById(R.id.imageView);
            title = (CustomTextViewMedium) view.findViewById(R.id.title);
            subtitle = (CustomTextViewMedium) view.findViewById(R.id.subtitle);
            topLine = (View) view.findViewById(R.id.topLine);
            checkBoxSelect = (CheckBox) view.findViewById(R.id.cig_checkBox_select_user);
        }
    }
}
