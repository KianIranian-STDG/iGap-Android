package net.iGap.adapter.items.discovery;

import net.iGap.proto.ProtoGlobal;

import java.util.ArrayList;

public class DiscoveryItem {
    public String scale;
    public ArrayList<DiscoveryItemField> discoveryFields;
    public ProtoGlobal.Discovery.DiscoveryModel model;

    public DiscoveryItem(ProtoGlobal.Discovery discovery) {
        model = discovery.getModel();
        discoveryFields = new ArrayList<>();
        for (ProtoGlobal.DiscoveryField discoveryField :discovery.getDiscoveryfieldsList()) {
            discoveryFields.add(new DiscoveryItemField(discoveryField));
        }
        scale = discovery.getScale();
    }

}
