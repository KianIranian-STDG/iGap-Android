package net.iGap.fragments.dashboard;

import net.iGap.proto.ProtoGlobal;

import java.util.ArrayList;

public interface OnDiscoveryList {

    void onDiscoveryListReady(ArrayList<ProtoGlobal.Discovery> discoveryArrayList, String title);
}
