package com.stevexls.imagepicker.filter;

import android.content.Context;

import com.stevexls.imagepicker.bean.MimeType;
import com.stevexls.imagepicker.bean.CustomCause;
import com.stevexls.imagepicker.bean.Item;

import java.util.Set;


/**
 * Time：2019/3/27 10:20
 * Description: 参考Matisse: https://github.com/zhihu/Matisse
 */
public abstract class Filter {

    public int MIN = 0;

    public int MAX = Integer.MAX_VALUE;

    public int K = 1024;

    // 此过滤器应用的mime类型
    protected abstract Set<MimeType> constraintTypes();

    /**
     * Invoked for filtering each item.
     *
     * @return null if selectable, {@link CustomCause} if not selectable.
     */
    public abstract CustomCause filter(Context context, Item item);

    protected boolean needFiltering(Context context, Item item) {
        for (MimeType type : constraintTypes()) {
            if (type.checkType(context.getContentResolver(), item.getPath())) {
                return true;
            }
        }
        return false;
    }
}
