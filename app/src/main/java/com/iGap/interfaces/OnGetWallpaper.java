package com.iGap.interfaces;

import com.iGap.proto.ProtoGlobal;
import java.util.List;

/**
 * Created by android3 on 2/14/2017.
 */

public interface OnGetWallpaper {

    void onGetWallpaperList(List<ProtoGlobal.Wallpaper> list);
}
