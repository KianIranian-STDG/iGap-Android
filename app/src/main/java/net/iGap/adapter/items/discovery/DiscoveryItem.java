package net.iGap.adapter.items.discovery;

import com.google.gson.annotations.SerializedName;

import net.iGap.proto.ProtoGlobal;

import java.util.ArrayList;

public class DiscoveryItem {

    @SerializedName("scale")
    public String scale;

    @SerializedName("discoveryFields")
    public ArrayList<DiscoveryItemField> discoveryFields;

    @SerializedName("model")
    public ProtoGlobal.Discovery.DiscoveryModel model;

    public DiscoveryItem(ProtoGlobal.Discovery discovery) {
        model = discovery.getModel();
        discoveryFields = new ArrayList<>();
        for (ProtoGlobal.DiscoveryField discoveryField : discovery.getDiscoveryfieldsList()) {
            discoveryFields.add(new DiscoveryItemField(discoveryField));
        }
        scale = discovery.getScale();
    }

}
