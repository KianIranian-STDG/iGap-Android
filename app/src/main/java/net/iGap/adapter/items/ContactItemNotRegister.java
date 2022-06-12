/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.adapter.items;

import android.view.View;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.mikepenz.fastadapter.items.AbstractItem;

import net.iGap.R;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperImageBackColor;
import net.iGap.messenger.theme.Theme;
import net.iGap.module.CircleImageView;
import net.iGap.module.CustomTextViewMedium;
import net.iGap.module.structs.StructContactInfo;

import java.util.List;

/**
 * Contact item used with FastAdapter for Navigation drawer contacts fragment.
 */
public class ContactItemNotRegister extends AbstractItem<ContactItemNotRegister, ContactItemNotRegister.ViewHolder> {
    public StructContactInfo mContact;

    public ContactItemNotRegister setContact(StructContactInfo contact) {
        this.mContact = contact;
        return this;
    }

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.contact_item_not_register;
    }

    @Override
    public void bindView(ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);

        if (mContact.isHeader) {
            holder.topLine.setVisibility(View.VISIBLE);
        } else {
            holder.topLine.setVisibility(View.GONE);
        }

        holder.title.setText(mContact.displayName);

        holder.subtitle.setText(mContact.phone);
        if (HelperCalander.isPersianUnicode) {
            holder.subtitle.setText(holder.subtitle.getText().toString());
        }

        String name = HelperImageBackColor.getFirstAlphabetName(mContact.displayName);
        holder.image.setImageBitmap(HelperImageBackColor.drawAlphabetOnPicture(holder.image.getLayoutParams().width, name, HelperImageBackColor.getColor(name)));
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        protected CircleImageView image;
        protected CustomTextViewMedium title;
        protected CustomTextViewMedium subtitle;
        protected View topLine;
        protected LinearLayout mainContainer;

        public ViewHolder(View view) {
            super(view);
            mainContainer = (LinearLayout) view.findViewById(R.id.mainContainer);
            mainContainer.setBackgroundColor(Theme.getColor(Theme.key_window_background));
            image = (CircleImageView) view.findViewById(R.id.imageView);
            title = (CustomTextViewMedium) view.findViewById(R.id.title);
            title.setTextColor(Theme.getColor(Theme.key_title_text));
            subtitle = (CustomTextViewMedium) view.findViewById(R.id.subtitle);
            subtitle.setTextColor(Theme.getColor(Theme.key_subtitle_text));
            topLine = view.findViewById(R.id.topLine);
            topLine.setBackgroundColor(Theme.getColor(Theme.key_line));
        }
    }
}
