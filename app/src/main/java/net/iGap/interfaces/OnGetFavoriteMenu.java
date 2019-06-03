package net.iGap.interfaces;

import net.iGap.proto.ProtoGlobal;
import java.util.List;

public interface OnGetFavoriteMenu {
    void onGetList(List<ProtoGlobal.Favorite> favoriteList);
}