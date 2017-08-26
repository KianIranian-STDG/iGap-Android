/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.response;

import net.iGap.G;
import net.iGap.fragments.FragmentRatingBar;
import net.iGap.helper.HelperFragment;
import net.iGap.proto.ProtoPushRateSignaling;

public class PushRateSignalingResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public long id = -1;

    public PushRateSignalingResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();
        ProtoPushRateSignaling.PushRateSignalingResponse.Builder builder = (ProtoPushRateSignaling.PushRateSignalingResponse.Builder) message;

        id = builder.getId();
        showRatingBar(id);
    }

    private void showRatingBar(final long id) {
        if (id > 0 && !G.isShowRatingDialog) {
            G.handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    HelperFragment.loadFragment(FragmentRatingBar.newInstance(id));
                }
            }, 1000);
        }
    }

    @Override
    public void timeOut() {
        super.timeOut();
    }

    @Override
    public void error() {
        super.error();
    }
}


