package net.iGap.fragments.discovery;


import net.iGap.adapter.items.discovery.DiscoveryItem;

import java.util.ArrayList;

public interface OnDiscoveryList {

    void onDiscoveryListReady(ArrayList<DiscoveryItem> discoveryArrayList, String title);
    void onError();
}
