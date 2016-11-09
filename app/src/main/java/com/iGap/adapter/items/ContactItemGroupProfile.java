package com.iGap.adapter.items;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.iGap.R;
import com.iGap.activities.ActivityGroupProfile;
import com.iGap.module.AndroidUtils;
import com.iGap.module.CircleImageView;
import com.iGap.module.CustomTextViewMedium;
import com.iGap.module.MaterialDesignTextView;
import com.iGap.module.StructContactInfo;
import com.iGap.proto.ProtoGlobal;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

public class ContactItemGroupProfile
        extends AbstractItem<ContactItemGroupProfile, ContactItemGroupProfile.ViewHolder> {
    private static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();
    public StructContactInfo mContact;


    public ContactItemGroupProfile setContact(StructContactInfo contact) {
        this.mContact = contact;
        return this;
    }

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.contact_item_group_profile;
    }

    @Override
    public void bindView(final ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);

        if (mContact.isHeader) {
            holder.topLine.setVisibility(View.VISIBLE);
        } else {
            holder.topLine.setVisibility(View.GONE);
        }

        holder.title.setText(mContact.displayName);

        holder.subtitle.setText(R.string.last_seen_recently);

        setRoleStarColor(holder.roleStar);


        if (mContact.avatar.getFile().isFileExistsOnLocal()) {
            ImageLoader.getInstance().displayImage(AndroidUtils.suitablePath(mContact.avatar.getFile().getLocalFilePath()), holder.image);
        } else if (mContact.avatar.getFile().isThumbnailExistsOnLocal()) {
            ImageLoader.getInstance().displayImage(AndroidUtils.suitablePath(mContact.avatar.getFile().getLocalThumbnailPath()), holder.image);
        } else {
            holder.image.setImageBitmap(
                    com.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) holder.image.getContext().getResources().getDimension(R.dimen.dp60), mContact.initials, mContact.color));

        }

        holder.btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityGroupProfile.onMenuClick != null)
                    ActivityGroupProfile.onMenuClick.clicked(v, holder.getAdapterPosition());
            }
        });
    }

    private void setRoleStarColor(MaterialDesignTextView view) {

        int color = Color.BLACK;
        view.setVisibility(View.VISIBLE);

        if (mContact.role.equals(ProtoGlobal.GroupRoom.Role.MEMBER.toString())) {
            color = Color.WHITE;
            view.setVisibility(View.GONE);
        } else if (mContact.role.equals(ProtoGlobal.GroupRoom.Role.MODERATOR.toString())) {
            color = Color.CYAN;
        } else if (mContact.role.equals(ProtoGlobal.GroupRoom.Role.ADMIN.toString())) {
            color = Color.GREEN;
        } else if (mContact.role.equals(ProtoGlobal.GroupRoom.Role.OWNER.toString())) {
            color = Color.BLUE;
        }

        view.setTextColor(color);

    }


    @Override
    public ViewHolderFactory<? extends ViewHolder> getFactory() {
        return FACTORY;
    }

    protected static class ItemFactory implements ViewHolderFactory<ViewHolder> {
        public ViewHolder create(View v) {
            return new ViewHolder(v);
        }
    }


    protected static class ViewHolder extends RecyclerView.ViewHolder {
        protected CircleImageView image;
        protected CustomTextViewMedium title;
        protected CustomTextViewMedium subtitle;
        protected View topLine;
        protected TextView txtNomberOfSharedMedia;
        protected MaterialDesignTextView roleStar;
        protected MaterialDesignTextView btnMenu;

        public ViewHolder(View view) {
            super(view);

            image = (CircleImageView) view.findViewById(R.id.cigp_imv_contact_avatar);
            title = (CustomTextViewMedium) view.findViewById(R.id.cigp_txt_contact_name);
            subtitle = (CustomTextViewMedium) view.findViewById(R.id.cigp_txt_contact_lastseen);
            topLine = view.findViewById(R.id.cigp_view_topLine);
            txtNomberOfSharedMedia = (TextView) view.findViewById(R.id.cigp_txt_nomber_of_shared_media);
            roleStar = (MaterialDesignTextView) view.findViewById(R.id.cigp_txt_member_role);
            btnMenu = (MaterialDesignTextView) view.findViewById(R.id.cigp_moreButton);
        }
    }
}
