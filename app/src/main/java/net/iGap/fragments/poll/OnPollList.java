package net.iGap.fragments.poll;


import net.iGap.adapter.items.poll.PollItem;

import java.util.ArrayList;

public interface OnPollList {

    void onPollListReady(ArrayList<PollItem> pollArrayList, String title);

    void onError(int major, int minor);
}
