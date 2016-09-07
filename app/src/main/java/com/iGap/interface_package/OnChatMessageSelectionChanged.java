package com.iGap.interface_package;

import java.util.Set;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 9/7/2016.
 */
public interface OnChatMessageSelectionChanged<Item> {
    void onChatMessageSelectionChanged(int selectedCount, Set<Item> selectedItems);
}
