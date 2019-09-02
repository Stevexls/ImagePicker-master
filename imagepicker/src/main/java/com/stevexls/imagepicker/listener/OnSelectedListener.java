package com.stevexls.imagepicker.listener;

import android.support.annotation.NonNull;

import com.stevexls.imagepicker.bean.Item;

import java.util.List;

public interface OnSelectedListener {
    /**
     * @param itemList the selected item {@link Item} list.
     *
     */
    void onSelected(@NonNull List<Item> itemList);
}
