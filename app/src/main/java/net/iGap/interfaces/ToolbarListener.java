package net.iGap.interfaces;

import android.view.View;

public interface ToolbarListener {

    default void onLeftIconClickListener(View view){}

    default void onSearchClickListener(View view){}

    default void onSmallAvatarClickListener(View view){}

    default void onBigAvatarClickListener(View view){}

    default void onChatAvatarClickListener(View view){}

    default void onRightIconClickListener(View view){}

    default void onSecondRightIconClickListener(View view){}

    default void onThirdRightIconClickListener(View view){}

    default void onFourthRightIconClickListener(View view){}

}
