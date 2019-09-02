package com.stevexls.imagepicker;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.stevexls.imagepicker.bean.Item;
import com.stevexls.imagepicker.bean.MimeType;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Set;

/**
 * Time：2019/6/16 16:42
 * Description:
 */
public class ImagePicker {

    public static final String EXTRA_RESULT_ORIGINAL_ENABLE = "extra_result_original_enable";
    public static final String EXTRA_RESULT_SELECTION = "extra_result_selection";

    private final WeakReference<Activity> mContext;
    private final WeakReference<Fragment> mFragment;

    private ImagePicker(Activity activity) {
        this(activity, null);
    }

    private ImagePicker(Fragment fragment) {
        this(fragment.getActivity(), fragment);
    }

    private ImagePicker(Activity activity, Fragment fragment) {
        mContext = new WeakReference<>(activity);
        mFragment = new WeakReference<>(fragment);
    }

    public static ImagePicker from(Activity activity) {
        return new ImagePicker(activity);
    }

    public static ImagePicker from(Fragment fragment) {
        return new ImagePicker(fragment);
    }

    public static List<Item> obtainResult(Intent data) {
        if (data == null) {
            return null;
        }
        return data.getParcelableArrayListExtra(EXTRA_RESULT_SELECTION);
    }

    public static boolean obtainOriginalState(Intent data) {
        if (data == null) {
            return false;
        }
        return data.getBooleanExtra(EXTRA_RESULT_ORIGINAL_ENABLE, false);
    }

    public SelectionCreator choose(Set<MimeType> mimeTypes) {
        return this.choose(mimeTypes, true);
    }

    public SelectionCreator choose(Set<MimeType> mimeTypes, boolean mediaTypeExclusive) {
        return new SelectionCreator(this, mimeTypes, mediaTypeExclusive);
    }

    public Activity getActivity() {
        return mContext.get();
    }

    public Fragment getFragment() {
        return mFragment.get();
    }
}
