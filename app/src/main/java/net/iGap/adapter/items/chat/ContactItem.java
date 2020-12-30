/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.adapter.items.chat;

import android.content.Intent;
import android.provider.ContactsContract;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentActivity;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.MessagesAdapter;
import net.iGap.fragments.FragmentCallAction;
import net.iGap.fragments.FragmentContactsProfile;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.LayoutCreator;
import net.iGap.module.accountManager.DbManager;
import net.iGap.observers.interfaces.IMessageItem;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmRegisteredInfo;

import java.util.List;

public class ContactItem extends AbstractMessage<ContactItem, ContactItem.ViewHolder> {

    private FragmentActivity activity;

    public ContactItem(FragmentActivity activity, MessagesAdapter<AbstractMessage> mAdapter, ProtoGlobal.Room.Type type, IMessageItem messageClickListener) {
        super(mAdapter, true, type, messageClickListener);
        this.activity = activity;
    }

    @Override
    protected void updateLayoutForSend(ViewHolder holder) {
        super.updateLayoutForSend(holder);
        holder.contactName.setTextColor(theme.getSendMessageTextColor(holder.getContext()));
        holder.contactNumberTv.setTextColor(theme.getSendMessageOtherTextColor(holder.getContext()));
    }

    @Override
    protected void updateLayoutForReceive(ViewHolder holder) {
        super.updateLayoutForReceive(holder);

        holder.contactName.setTextColor(theme.getReceivedMessageColor(holder.getContext()));
        holder.contactNumberTv.setTextColor(theme.getReceivedMessageOtherTextColor(holder.getContext()));
    }

    @Override
    public int getType() {
        return R.id.chatSubLayoutContact;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.chat_sub_layout_message;
    }

    @Override
    public void bindView(ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);

//        if (messageObject.forwardedMessage != null) {
//            if (messageObject.forwardedMessage.contact != null) {
//                holder.contactName.setText(mMessage.getForwardMessage().getRoomMessageContact().getFirstName() + " " + mMessage.getForwardMessage().getRoomMessageContact().getLastName());
//                holder.contactNumberTv.setText(mMessage.getForwardMessage().getRoomMessageContact().getLastPhoneNumber());
//            }
//        } else {
//            if (mMessage.getRoomMessageContact() != null) {
//                holder.contactName.setText(mMessage.getRoomMessageContact().getFirstName() + " " + mMessage.getRoomMessageContact().getLastName());
//                holder.contactNumberTv.setText(mMessage.getRoomMessageContact().getLastPhoneNumber());
//            }
//        }
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(activity, v);
    }

    protected class ViewHolder extends NewChatItemHolder {

        private static final int IN_CONTACT_AND_HAVE_IGAP = 0;
        private static final int NOT_CONTACT_AND_HAVE_NOT_IGAP = 1;

        private AppCompatTextView contactName;
        private AppCompatTextView contactNumberTv;
        private AppCompatImageView contactImage;
        private ConstraintLayout rootView;
        private ConstraintSet set;
        private Button viewContactBtn;
        private Button contactWithUserBtn;
        private LinearLayout bottomViewContainer;
        private int contactStatus;
        private long contactId;

        public ViewHolder(FragmentActivity activity, View view) {
            super(view);

            contactImage = new AppCompatImageView(getContext());
            contactImage.setId(R.id.iv_contactItem_contact);
            contactImage.setContentDescription(null);
            contactImage.setBackground(theme.tintDrawable(ContextCompat.getDrawable(getContext(), R.drawable.gray_contact), getContext(), R.attr.colorPrimaryDark));

            contactName = new AppCompatTextView(getContext());
            contactName.setId(R.id.tv_contactItem_contactName);
            ViewMaker.setTextSize(contactName, R.dimen.dp14);
            ViewMaker.setTypeFace(contactName);

            contactNumberTv = new AppCompatTextView(getContext());
            contactNumberTv.setId(R.id.tv_contactItem_contactNumber);
            ViewMaker.setTypeFace(contactNumberTv);


            /**
             * if contact found in contact list this button show
             * when click on view get show contact profile
             * */

            viewContactBtn = new Button(getContext());
            viewContactBtn.setText(getContext().getResources().getString(R.string.view_contact));
            viewContactBtn.setBackground(getDrawable(R.drawable.background_view_contact));
            viewContactBtn.setPadding(LayoutCreator.dp(16), 0, LayoutCreator.dp(16), 0);
            viewContactBtn.setTextSize(12);
            viewContactBtn.setTextColor(getColor(R.color.md_blue_500));
            viewContactBtn.setAllCaps(false);
            viewContactBtn.setTypeface(ResourcesCompat.getFont(viewContactBtn.getContext(), R.font.main_font));


            /**
             * if contact have not igap and not found in contact list this button show 
             * and viewContactBtn change text to Add Contact!
             * */

            contactWithUserBtn = new Button(getContext());
            contactWithUserBtn.setText(getResources().getString(R.string.call));
            contactWithUserBtn.setBackground(getDrawable(R.drawable.background_contact));
            contactWithUserBtn.setPadding(LayoutCreator.dp(16), 0, LayoutCreator.dp(16), 0);
            contactWithUserBtn.setTextSize(10);
            contactWithUserBtn.setTextColor(getColor(R.color.grayNew));
            contactWithUserBtn.setAllCaps(false);
            contactWithUserBtn.setTypeface(ResourcesCompat.getFont(viewContactBtn.getContext(), R.font.main_font));


            /**
             * contact button view container
             * */

            bottomViewContainer = new LinearLayout(getContext());
            bottomViewContainer.setId(R.id.btn_contactItem_viewContact);
            bottomViewContainer.setOrientation(LinearLayout.HORIZONTAL);

            G.handler.postDelayed(() -> getContactInfo(getContactNumberTv()
                    .replace(" ", "")
                    .replace("+98", "98")
                    .replace("0", "98")), 30);

//            Minor delay for load view and get information
            G.handler.postDelayed(() -> {

                if (contactStatus == IN_CONTACT_AND_HAVE_IGAP) {
                    bottomViewContainer.addView(viewContactBtn, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT));
                } else if (contactStatus == NOT_CONTACT_AND_HAVE_NOT_IGAP) {
                    bottomViewContainer.setWeightSum(2);
                    viewContactBtn.setText(getResources().getString(R.string.add_to_contact));
                    viewContactBtn.setTextSize(10);
                    bottomViewContainer.addView(viewContactBtn, 0, LayoutCreator.createLinear(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT,
                            0.8f, 0, 0, LayoutCreator.dp(2), 0));
                    bottomViewContainer.addView(contactWithUserBtn, 1, LayoutCreator.createLinear(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT,
                            1.2f));
                }

            }, 40);


            /**
             * root view
             * */

            rootView = new ConstraintLayout(getContext());
            set = new ConstraintSet();


            /**
             * set views dependency
             * */

            set.constrainWidth(contactImage.getId(), LayoutCreator.dp(35));
            set.constrainHeight(contactImage.getId(), LayoutCreator.dp(35));

            set.constrainWidth(contactNumberTv.getId(), ConstraintSet.WRAP_CONTENT);
            set.constrainHeight(contactNumberTv.getId(), ConstraintSet.WRAP_CONTENT);

            set.constrainWidth(contactName.getId(), ConstraintSet.WRAP_CONTENT);
            set.constrainHeight(contactName.getId(), ConstraintSet.WRAP_CONTENT);


            set.connect(contactImage.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, LayoutCreator.dp(8));
            set.connect(contactImage.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, LayoutCreator.dp(8));
            set.connect(contactImage.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, LayoutCreator.dp(8));
            rootView.addView(contactImage);


            set.connect(contactName.getId(), ConstraintSet.LEFT, contactImage.getId(), ConstraintSet.RIGHT, LayoutCreator.dp(8));
            rootView.addView(contactName);

            set.connect(contactNumberTv.getId(), ConstraintSet.LEFT, contactName.getId(), ConstraintSet.LEFT);
            rootView.addView(contactNumberTv);

            int[] chainViews = {contactName.getId(), contactNumberTv.getId()};
            float[] chainWeights = {0, 0};
            set.createVerticalChain(contactImage.getId(), ConstraintSet.TOP, contactImage.getId(), ConstraintSet.BOTTOM,
                    chainViews, chainWeights, ConstraintSet.CHAIN_PACKED);


            set.applyTo(rootView);
            getContentBloke().addView(rootView, 0);
            getContentBloke().addView(bottomViewContainer, 1, LayoutCreator.createFrame(200, 30, Gravity.CENTER,
                    4, 4, 4, 0));


            viewContactBtn.setOnClickListener(v -> {
                if (contactStatus == IN_CONTACT_AND_HAVE_IGAP) {
                    new HelperFragment(activity.getSupportFragmentManager(), FragmentContactsProfile.newInstance(0, contactId, "Others")).setReplace(false).load();
                } else if (contactStatus == NOT_CONTACT_AND_HAVE_NOT_IGAP) {
                    Intent intent = new Intent(Intent.ACTION_INSERT);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
                    intent.putExtra(ContactsContract.Intents.Insert.NAME, getContactName());
                    intent.putExtra(ContactsContract.Intents.Insert.PHONE, getContactNumberTv());
                    intent.putExtra("finishActivityOnSaveCompleted", true);
                    getContext().startActivity(intent);
                }
            });

            contactWithUserBtn.setOnClickListener(v -> {
                if (contactStatus == NOT_CONTACT_AND_HAVE_NOT_IGAP) {
                    FragmentCallAction callAction = new FragmentCallAction();
                    callAction.setPhoneNumber(getContactNumberTv());
                    callAction.show(activity.getSupportFragmentManager(), null);
                }
            });
        }

        private void getContactInfo(String userPhoneNumber) {
            DbManager.getInstance().doRealmTask(realm -> {
                contactId = RealmRegisteredInfo.getUserInfo(realm, userPhoneNumber);

                if (contactId > 0)
                    contactStatus = IN_CONTACT_AND_HAVE_IGAP;
                else
                    contactStatus = NOT_CONTACT_AND_HAVE_NOT_IGAP;
            });

        }

        private String getContactName() {
            return contactName.getText().toString();
        }

        private String getContactNumberTv() {
            return contactNumberTv.getText().toString();
        }


    }
}
