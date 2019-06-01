package org.paygear;


import org.paygear.fragment.PaymentHistoryFragment;
import org.paygear.model.Card;
import org.paygear.model.SearchedAccount;

import java.util.ArrayList;
import ir.radsense.raadcore.model.Account;


public class RaadApp {

    public static final int NOTIFICATION_TYPE_LIKE = 1;
    public static final int NOTIFICATION_TYPE_COMMENT = 2;
    public static final int NOTIFICATION_TYPE_NEW_MESSAGE = 3;
    public static final int NOTIFICATION_TYPE_PAY_COMPLETE = 4;
    public static final int NOTIFICATION_TYPE_COUPON = 5;
    public static final int NOTIFICATION_TYPE_DELIVERY = 6;
    public static final int NOTIFICATION_TYPE_FOLLOW = 7;
    public static final int NOTIFICATION_TYPE_CLUB = 9;
    public static PaymentHistoryFragment.PaygearHistoryOpenChat paygearHistoryOpenChat;
    public static PaymentHistoryFragment.PaygearHistoryCloseWallet paygearHistoryCloseWallet;

    public static Account me;
    public static Card paygearCard;
    public static ArrayList<Card> cards;
    public static ArrayList<SearchedAccount> merchants;
    public static SearchedAccount selectedMerchant;
}
