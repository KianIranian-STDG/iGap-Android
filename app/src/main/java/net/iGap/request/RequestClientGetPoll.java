/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.request;

import net.iGap.fragments.poll.OnPollList;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.network.RequestManager;
import net.iGap.proto.ProtoClientGetPoll;

public class RequestClientGetPoll {


    public boolean getPoll(int pollId, OnPollList onPollList) {

        ProtoClientGetPoll.ClientGetPoll.Builder builder = ProtoClientGetPoll.ClientGetPoll.newBuilder();
        builder.setPollId(pollId);

        RequestWrapper requestWrapper = new RequestWrapper(624, builder, onPollList);
        try {
            if (RequestManager.getInstance(AccountManager.selectedAccount).isUserLogin()) {
                RequestQueue.sendRequest(requestWrapper);
                return true;
            } else {
                return false;
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return false;
        }
    }
}
