package com.stevexls.imagepicker.bean;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;

/**
 * Time：2018/12/19 13:43
 * Description:
 */
public class Item implements Parcelable {
    public long id;
    public String name;
    public String path;         // 路径
    public String mimeType;     // 类型
    public long size;
    public long duration;       // only for video, in ms
    public long createTime;     // 创建时间
    public Uri uri;
    public int width;         //图片的宽度
    public int height;        //图片的高度

    public Item() {
    }

    public Item(long id, String name, String path, String mimeType, long size, long duration, long createTime, int width, int height) {
        this.id = id;
        this.name = name;
        this.path = path;
        this.mimeType = mimeType;
        this.size = size;
        this.duration = duration;
        this.createTime = createTime;
        this.uri = getUri(this);
        this.width = width;
        this.height = height;
    }

    protected Item(Parcel in) {
        id = in.readLong();
        name = in.readString();
        path = in.readString();
        mimeType = in.readString();
        size = in.readLong();
        duration = in.readLong();
        createTime = in.readLong();
        uri = in.readParcelable(Uri.class.getClassLoader());
        width = in.readInt();
        height = in.readInt();
    }

    public static final Creator<Item> CREATOR = new Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(path);
        dest.writeString(mimeType);
        dest.writeLong(size);
        dest.writeLong(duration);
        dest.writeLong(createTime);
        dest.writeParcelable(uri, 0);
        dest.writeInt(width);
        dest.writeInt(height);
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public String getMimeType() {
        return mimeType;
    }

    public long getSize() {
        return size;
    }

    public long getDuration() {
        return duration;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Item)) {
            return false;
        }
        Item other = (Item) obj;
        return id == other.id
                && (mimeType != null && mimeType.equals(other.mimeType)
                || (mimeType == null && other.mimeType == null))
                && (path != null && path.equals(other.path)
                || (path == null && other.path == null))
                && size == other.size
                && name.equals(other.name)
                && duration == other.duration
                && createTime == other.createTime
                && (uri != null && uri.equals(other.uri)
                || (uri == null && other.uri == null))
                && width == other.width
                && height == other.height;
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + Long.valueOf(id).hashCode();
        if (mimeType != null) {
            result = 31 * result + mimeType.hashCode();
        }
        result = 31 * result + path.hashCode();
        result = 31 * result + Long.valueOf(size).hashCode();
        result = 31 * result + Long.valueOf(duration).hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + Long.valueOf(createTime).hashCode();
        result = 31 * result + uri.hashCode();
        result = 31 * result + Long.valueOf(width).hashCode();
        result = 31 * result + Long.valueOf(height).hashCode();
        return result;
    }

    public static Item valueOf(Cursor cursor) {
        return new Item(cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns._ID)),
                cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)),
                cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)),
                cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.MIME_TYPE)),
                cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.SIZE)),
                cursor.getLong(cursor.getColumnIndex("duration")),
                cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED)),
                cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.WIDTH)),
                cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.HEIGHT)));
    }

    /**
     * 拍照Item项
     *
     * @return
     */
    public static Item createCaptureItem() {
        return new Item(-1, "capture", "", "", 0, 0, 0, 0, 0);
    }

    // 是否需要拍照
    public boolean isCapture() {
        return id == -1;
    }

    public boolean isImage() {
        return MimeType.isImage(mimeType);
    }

    public boolean isVideo() {
        return MimeType.isVideo(mimeType);
    }

    public boolean isGif() {
        return MimeType.isGif(mimeType);
    }

    private Uri getUri(Item item) {
        Uri contentUri;
        if (item.isImage()) {
            contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        } else if (item.isVideo()) {
            contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        } else {
            contentUri = MediaStore.Files.getContentUri("external");
        }
        return ContentUris.withAppendedId(contentUri, item.getId());
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", mimeType='" + mimeType + '\'' +
                ", size=" + size +
                ", duration=" + duration +
                ", createTime=" + createTime +
                ", uri=" + uri +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}
