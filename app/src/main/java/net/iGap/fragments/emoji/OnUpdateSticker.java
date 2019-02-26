package net.iGap.fragments.emoji;

import com.vanniktech.emoji.sticker.struct.StructGroupSticker;

import java.util.ArrayList;

public interface OnUpdateSticker {

    void update();
    void updateRecentlySticker(ArrayList<String> structAllStickers);

}
