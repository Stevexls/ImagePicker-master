package com.stevexls.imagepicker.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.stevexls.imagepicker.bean.Item;
import com.stevexls.imagepicker.ui.MediaPreviewFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Time：2019/6/27 16:30
 * Description:预览
 */
public class MediaPreviewAdapter extends FragmentPagerAdapter {

    private List<Item> mItems;

    public MediaPreviewAdapter(FragmentManager fm, List<Item> itemList) {
        super(fm);
        if (itemList != null) {
            mItems = itemList;
        } else {
            mItems = new ArrayList<>();
        }
    }

    @Override
    public Fragment getItem(int i) {
        return MediaPreviewFragment.newInstance(mItems.get(i));
    }

    @Override
    public int getCount() {
        return mItems.size();
    }
}
