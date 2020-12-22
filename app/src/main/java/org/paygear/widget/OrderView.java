package org.paygear.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.squareup.picasso.Picasso;

import net.iGap.R;

import org.paygear.WalletActivity;
import org.paygear.model.Order;

import java.util.Calendar;

import ir.radsense.raadcore.model.Auth;
import ir.radsense.raadcore.utils.RaadCommonUtils;
import ir.radsense.raadcore.utils.Typefaces;

import static ir.radsense.raadcore.utils.RaadCommonUtils.getPx;

/**
 * Created by Software1 on 1/13/2018.
 */

public class OrderView extends LinearLayout {

    public ImageView image;
    public TextView title;
    public TextView subtitle;
    public TextView date;
    public TextView price;
    public View badge;


    public OrderView(Context context) {
        super(context);
        init();
    }

    public OrderView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public OrderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        Context context = getContext();

        setOrientation(HORIZONTAL);
        int dp10 = getPx(10, context);
        int dp8 = getPx(8, context);
        setPadding(dp10, dp10, dp10, dp10);

        FrameLayout imageFrame = new FrameLayout(context);
        int dp65 = RaadCommonUtils.getPx(65, context);
        LayoutParams imageFrameParams = new LayoutParams(dp65, dp65);
        imageFrameParams.gravity = Gravity.CENTER_VERTICAL;
        imageFrame.setLayoutParams(imageFrameParams);
        addView(imageFrame);

        image = new AppCompatImageView(context);
        image.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        imageFrame.addView(image);

        View borderView = new View(context);
        borderView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        borderView.setBackgroundResource(R.drawable.image_border);
        imageFrame.addView(borderView);


        LinearLayout textsLayout = new LinearLayout(context);
        LayoutParams textsLayoutParams = new LayoutParams(
                0, LayoutParams.WRAP_CONTENT);
        textsLayoutParams.weight = 1.0f;
        textsLayoutParams.gravity = Gravity.CENTER_VERTICAL;
        textsLayout.setLayoutParams(textsLayoutParams);
        textsLayout.setGravity(Gravity.START);
        textsLayout.setOrientation(LinearLayout.VERTICAL);
        textsLayout.setPadding(dp8, 0, dp8, 0);
        addView(textsLayout);

        title = new AppCompatTextView(context);
        LayoutParams titleParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        //par.gravity = Gravity.CENTER_VERTICAL;
        title.setLayoutParams(titleParams);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        title.setTextColor(Color.parseColor(WalletActivity.textTitleTheme));
        title.setTypeface(Typefaces.get(context, Typefaces.IRAN_LIGHT));
        textsLayout.addView(title);

        subtitle = new AppCompatTextView(context);
        LayoutParams subtitleParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        //par.gravity = Gravity.CENTER_VERTICAL;
        subtitle.setLayoutParams(subtitleParams);
        subtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        subtitle.setTextColor(Color.parseColor(WalletActivity.textSubTheme));
        subtitle.setTypeface(Typefaces.get(context, Typefaces.IRAN_LIGHT));
        textsLayout.addView(subtitle);

        date = new AppCompatTextView(context);
        LayoutParams dateParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        //par.gravity = Gravity.CENTER_VERTICAL;
        date.setLayoutParams(dateParams);
        date.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        date.setTextColor(Color.parseColor(WalletActivity.textTitleTheme));
        date.setTypeface(Typefaces.get(context, Typefaces.IRAN_LIGHT));
        textsLayout.addView(date);

        price = new AppCompatTextView(context);
        LayoutParams priceParams = new LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        priceParams.gravity = Gravity.CENTER_VERTICAL;
        price.setLayoutParams(priceParams);
        price.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        price.setTextColor(Color.parseColor(WalletActivity.textSubTheme));
        price.setGravity(Gravity.END);
        price.setTypeface(Typefaces.get(context, Typefaces.IRAN_LIGHT));
        addView(price);
    }

    public void setOrder(Order order) {
        String img = null;
        int imgRes = 0;
        Boolean isPay = order.isPay();
        if (isPay == null) {

        } else if (isPay) { //pardakhti

            if (order.orderType == Order.ORDER_TYPE_CASH_OUT) {
//                title.setText(order.isPaid ? R.string.settled : R.string.settled_pending);
                if (order.state==0){
                    title.setText(R.string.settled_pending);
                    title.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_payment_pending, 0);
                }else if (order.state==1){
                    title.setText(R.string.settled);
                    title.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_payment_out, 0);
                }else if (order.state==5){
                    title.setText(R.string.refound);
                    title.setCompoundDrawablesWithIntrinsicBounds(0, 0,R.drawable.ic_payment_in, 0);
                }else {
                    title.setText(order.isPaid ? R.string.settled : R.string.settled_pending);
                    title.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_payment_out, 0);
                }

                subtitle.setText(R.string.paygear_card);
                imgRes = R.drawable.ic_history;
                price.setTextColor(Color.parseColor(order.isPaid ? WalletActivity.textTitleTheme : WalletActivity.textSubTheme));
            } else if (order.orderType == Order.ORDER_TYPE_CHARGE_CREDIT
                    && order.receiver.id.equals(Auth.getCurrentAuth().getId())) {

                title.setText(R.string.charge_wallet);
                title.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                subtitle.setText(R.string.paygear_card);
                imgRes = R.drawable.ic_history;
                price.setTextColor(Color.parseColor(WalletActivity.textTitleTheme));
            } else {
                if (order.transactionType == Order.TRANSACTION_TYPE_DIRECT_CARD) {
                    if (order.orderType == Order.ORDER_TYPE_DEFAULT ||
                            order.orderType == Order.ORDER_TYPE_P2P ||
                            order.orderType == Order.ORDER_TYPE_REQUEST_MONEY)
                        title.setText(R.string.pay_to);
                    else if (order.orderType == Order.ORDER_TYPE_CHARGE_CREDIT)
                        title.setText(R.string.pay_charge_for_raad_card_to);
                } else if (order.transactionType == Order.TRANSACTION_TYPE_CREDIT) {
                    title.setText(R.string.pay_with_raad_card_to);
                }

                if (order.orderType == Order.ORDER_TYPE_BUY_CLUB_PLAN) {
                    title.setText(R.string.purchase_club_plan_from);
                }

                title.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_payment_out, 0);
                subtitle.setText(order.receiver.getName());
                img = order.receiver.profilePicture;
                price.setTextColor(Color.parseColor(WalletActivity.textTitleTheme));
            }

        } else { //daryafti
            if (order.sender != null) {
                if (order.orderType == Order.ORDER_TYPE_DEFAULT ||
                        order.orderType == Order.ORDER_TYPE_P2P ||
                        order.orderType == Order.ORDER_TYPE_REQUEST_MONEY)
                    title.setText(R.string.receive_from);
                else if (order.orderType == Order.ORDER_TYPE_SALE_SHARE)
                    title.setText(R.string.receive_sale_share_from);
                else if (order.orderType == Order.ORDER_TYPE_CHARGE_CREDIT)
                    title.setText(R.string.receive_charge_for_raad_card_from);

                title.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_payment_in, 0);
                subtitle.setText(order.sender.getName());
                img = order.sender.profilePicture;
                price.setTextColor(Color.parseColor(WalletActivity.textTitleTheme));
            } else {
                title.setText(R.string.payment_pending);
                title.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                        order.isPaid ? R.drawable.ic_payment_out : R.drawable.ic_payment_pending, 0);
                subtitle.setText("-");
                imgRes = R.drawable.ic_money;
                price.setTextColor(Color.parseColor(WalletActivity.textSubTheme));
            }

        }
        if (order.state==5) {
            title.setText(R.string.refound);
            title.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_payment_in, 0);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(order.createdMicroTime);
        date.setText(RaadCommonUtils.getLocaleFullDateTime(calendar));


        price.setText(RaadCommonUtils.formatPrice(order.amount, true, "\n"));

        if (imgRes > 0) {
            Picasso.get().load(imgRes)
                    .fit()
                    .centerCrop()
                    .into(image);
        } else {
            Picasso.get().load(RaadCommonUtils.getImageUrl(img))
                    .error(R.drawable.ic_person_outline2_white_24dp)
                    .placeholder(R.drawable.ic_person_outline2_white_24dp)
                    .fit()
                    .centerCrop()
                    .into(image);
        }

    }


}
