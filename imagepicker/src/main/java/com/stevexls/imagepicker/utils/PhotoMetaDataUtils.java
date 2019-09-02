package com.stevexls.imagepicker.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Build;

import com.stevexls.imagepicker.bean.MimeType;
import com.stevexls.imagepicker.R;
import com.stevexls.imagepicker.bean.CustomCause;
import com.stevexls.imagepicker.bean.Item;
import com.stevexls.imagepicker.bean.SelectionSpec;
import com.stevexls.imagepicker.filter.Filter;

import java.io.File;

/**
 * Time：2019/4/9 15:56
 * Description:
 */
public class PhotoMetaDataUtils {

    // 是否可接受的类型
    public static CustomCause isAcceptable(Context context, Item item) {
        if (!isSelectableType(context, item)) {
            return new CustomCause(context.getString(R.string.error_file_type));
        }

        if (SelectionSpec.getInstance().filters != null) {
            for (Filter filter : SelectionSpec.getInstance().filters) {
                CustomCause customCause = filter.filter(context, item);
                if (customCause != null) {
                    return customCause;
                }
            }
        }
        return null;
    }

    // 是否可选择的类型
    private static boolean isSelectableType(Context context, Item item) {
        if (context == null) {
            return false;
        }

        ContentResolver resolver = context.getContentResolver();
        for (MimeType type : SelectionSpec.getInstance().mimeTypeSet) {
            if (type.checkType(resolver, item.getPath())) {
                return true;
            }
        }
        return false;
    }

    public static Uri getUriFromPath(String path, Context context, String authority) {
        File file = new File(path);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            return Uri.fromFile(file);
        } else {
            return ImagePickerProvider.getUriForFile(context, authority, file);
        }
    }
}
