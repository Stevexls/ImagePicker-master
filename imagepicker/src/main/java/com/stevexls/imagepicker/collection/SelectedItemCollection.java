package com.stevexls.imagepicker.collection;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.stevexls.imagepicker.R;
import com.stevexls.imagepicker.bean.CustomCause;
import com.stevexls.imagepicker.bean.Item;
import com.stevexls.imagepicker.bean.MimeType;
import com.stevexls.imagepicker.bean.SelectionSpec;
import com.stevexls.imagepicker.utils.CollectionUtils;
import com.stevexls.imagepicker.utils.PhotoMetaDataUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Time：2019/4/8 16:19
 * Description: 已选中item
 */
public class SelectedItemCollection {

    public static String STATE_SELECTION = "state_selection";
    public static String STATE_COLLECTION_TYPE = "state_collection_type";

    /**
     * Empty collection
     */
    public static int COLLECTION_UNDEFINED = 0x00;

    /**
     * Collection only with images
     */
    private int COLLECTION_IMAGE = 0x01;

    /**
     * Collection only with videos
     */
    private int COLLECTION_VIDEO = 0x01 << 1;

    /**
     * Collection with images and videos
     */
    private int COLLECTION_MIXED = COLLECTION_IMAGE | COLLECTION_VIDEO;

    private Context mContext;
    private List<Item> mItems;   // 选中item
    private int mCollectionType = COLLECTION_UNDEFINED;     // 当前已收集的类型

    // 选中列
    private String[] PROJECTION = {
            MediaStore.Files.FileColumns._ID,
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.Images.Media.DATA,           // 图片的真实路径  /storage/emulated/0/pp/downloader/wallpaper/aaa.jpg
            MediaStore.Images.Media.MIME_TYPE,      // 图片的类型     image/jpeg
            MediaStore.Images.Media.SIZE,           // 图片的大小，long型  132492
            "duration",
            MediaStore.Images.Media.DATE_ADDED
    };

    // 过滤(指定单个文件路径，单个类型)
    private final String SELECTION_ITEM =
            MediaStore.Images.Media.DATA + "=?"
                    + " AND " + MediaStore.MediaColumns.SIZE + ">0";

    private String[] getSelectionItemArgs(String path) {
        return new String[]{path};
    }

    public SelectedItemCollection(Context context) {
        mContext = context;
    }

    public void onCreate(Bundle bundle) {
        if (bundle == null) {
            mItems = new ArrayList<>();
        } else {
            List<Item> saved = bundle.getParcelableArrayList(STATE_SELECTION);  // 获取已保存的Item
            mItems = new ArrayList<>();
            if (saved != null) {
                mItems.addAll(saved);
            }
            mCollectionType = bundle.getInt(STATE_COLLECTION_TYPE, COLLECTION_UNDEFINED);
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(STATE_SELECTION, new ArrayList<>(mItems));
        outState.putInt(STATE_COLLECTION_TYPE, mCollectionType);
    }

    public Bundle getDataWithBundle() {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(STATE_SELECTION, new ArrayList<>(mItems));
        bundle.putInt(STATE_COLLECTION_TYPE, mCollectionType);
        return bundle;
    }

    public boolean add(Item item) {
        if (typeConflict(item)) {
            throw new IllegalArgumentException("Can't select images and videos at the same time.");
        }
        boolean added = mItems.add(item);
        if (added) {
            if (mCollectionType == COLLECTION_UNDEFINED) {
                if (MimeType.isImage(item)) {
                    mCollectionType = COLLECTION_IMAGE;
                } else if (MimeType.isVideo(item)) {
                    mCollectionType = COLLECTION_VIDEO;
                }
            } else if (mCollectionType == COLLECTION_IMAGE) {
                if (MimeType.isVideo(item)) {
                    mCollectionType = COLLECTION_MIXED;
                }
            } else if (mCollectionType == COLLECTION_VIDEO) {
                if (MimeType.isImage(item)) {
                    mCollectionType = COLLECTION_MIXED;
                }
            }
        }
        return added;
    }

    public void addAll(List<Item> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        for (Item item : list) {
            add(item);
        }
    }

    public boolean remove(Item item) {
        boolean removed = mItems.remove(item);
        if (removed) {
            if (mItems.size() == 0) {
                mCollectionType = COLLECTION_UNDEFINED;
            } else {
                if (mCollectionType == COLLECTION_MIXED) {
                    refineCollectionType();
                }
            }
        }
        return removed;
    }

    public void overwrite(ArrayList<Item> items, int collectionType) {
        if (items.size() == 0) {
            mCollectionType = COLLECTION_UNDEFINED;
        } else {
            mCollectionType = collectionType;
        }
        mItems.clear();
        mItems.addAll(items);
    }

    public ArrayList<Item> getList() {
        return new ArrayList<>(mItems);
    }

    public ArrayList<String> asListOfString() {
        ArrayList<String> paths = new ArrayList<>();
        for (Item item : mItems) {
            paths.add(item.getPath());
        }
        return paths;
    }

    public boolean isEmpty() {
        return CollectionUtils.isEmpty(mItems);
    }

    public boolean isSelected(Item item) {
        return mItems != null && mItems.contains(item);
    }

    public CustomCause isAcceptable(Item item) {
        if (maxSelectableReached()) {   // 达到最大可选择数量
            int maxSelectable = currentMaxSelectable();
            String cause = mContext.getResources().getQuantityString(
                    R.plurals.error_over_count,
                    maxSelectable,
                    maxSelectable);
            return new CustomCause(cause);
        } else if (typeConflict(item)) {    // 类型冲突
            return new CustomCause(mContext.getString(R.string.error_type_conflict));
        }
        return PhotoMetaDataUtils.isAcceptable(mContext, item);
    }

    // 是否达到最大可选择数量
    public boolean maxSelectableReached() {
        return mItems.size() == currentMaxSelectable();
    }

    private int currentMaxSelectable() {
        SelectionSpec spec = SelectionSpec.getInstance();
        if (spec.maxSelectable > 0) {
            return spec.maxSelectable;
        } else if (mCollectionType == COLLECTION_IMAGE) {
            return spec.maxImageSelectable;
        } else if (mCollectionType == COLLECTION_VIDEO) {
            return spec.maxVideoSelectable;
        } else {
            return spec.maxSelectable;
        }
    }

    public int getCollectionType() {
        return mCollectionType;
    }

    // 重新确认type
    private void refineCollectionType() {
        boolean hasImage = false;
        boolean hasVideo = false;
        for (Item i : mItems) {
            if (MimeType.isImage(i) && !hasImage) {
                hasImage = true;
            }
            if (MimeType.isVideo(i) && !hasVideo) {
                hasVideo = true;
            }
        }
        if (hasImage && hasVideo) {
            mCollectionType = COLLECTION_MIXED;
        } else if (hasImage) {
            mCollectionType = COLLECTION_IMAGE;
        } else if (hasVideo) {
            mCollectionType = COLLECTION_VIDEO;
        }
    }

    public boolean typeConflict(Item item) {
        return SelectionSpec.getInstance().mediaTypeExclusive
                && ((MimeType.isImage(item) && (mCollectionType == COLLECTION_VIDEO || mCollectionType == COLLECTION_MIXED))
                || (MimeType.isVideo(item) && (mCollectionType == COLLECTION_IMAGE || mCollectionType == COLLECTION_MIXED)));
    }

    public int count() {
        return mItems.size();
    }

    public int checkedNumOf(Item item) {
        int index = mItems.indexOf(item);
        return index == -1 ? Integer.MIN_VALUE : index + 1;
    }

    // 获取单个
    public Item getCurrentPhotoItem(String path) {
        ContentResolver contentResolver = mContext.getContentResolver();
        String[] selectionArgs = getSelectionItemArgs(path);
        Cursor cursor = contentResolver.query(MediaStore.Files.getContentUri("external"), PROJECTION, SELECTION_ITEM, selectionArgs, null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            Item item = Item.valueOf(cursor);
            cursor.close();
            return item;
        }
        return null;
    }

    // 根据path获取详细的Item信息，并按照原来的排序返回
    public List<Item> getCurrentPhotoItems(List<String> paths) {
        if (CollectionUtils.isEmpty(paths)) {
            return new ArrayList<>();
        }
        ContentResolver contentResolver = mContext.getContentResolver();
        String selection = MediaStore.MediaColumns.SIZE + ">0"
                + " AND " + MediaStore.Images.Media.DATA + " in(" + "\"" + TextUtils.join("\",\"", paths) + "\"" + ")";
        Cursor cursor = contentResolver.query(MediaStore.Files.getContentUri("external"), PROJECTION, selection, null, null);
        List<Item> itemList = new ArrayList<>();
        if (cursor == null || cursor.getCount() == 0) {
            return itemList;
        }
        Map<String, Item> itemMap = new HashMap<>();
        while (cursor.moveToNext()) {
            Item item = Item.valueOf(cursor);
            itemMap.put(item.getPath(), item);
        }
        for (String path : paths) {
            itemList.add(itemMap.get(path));
        }
        cursor.close();
        return itemList;
    }
}
