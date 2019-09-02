package com.stevexls.imagepicker.bean;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;

import com.stevexls.imagepicker.collection.AlbumCollection;

/**
 * Time：2019/1/23 15:04
 * Description:相册
 */
public class Album implements Parcelable {
    private String bucketId;    // 相册id
    private String coverPath;   // 封面
    private String displayName; // 名称
    private long count;       // 统计
    private int mediaType;    // 类型

    public Album(){

    }

    public Album(String bucketId, String coverPath, String displayName, long count, int mediaType) {
        this.bucketId = bucketId;
        this.coverPath = coverPath;
        this.displayName = displayName;
        this.count = count;
        this.mediaType = mediaType;
    }

    public Album(Parcel source) {
        bucketId = source.readString();
        coverPath = source.readString();
        displayName = source.readString();
        count = source.readLong();
        mediaType = source.readInt();
    }

    public static final Creator<Album> CREATOR = new Creator<Album>() {
        @Override
        public Album createFromParcel(Parcel source) {
            return new Album(source);
        }

        @Override
        public Album[] newArray(int size) {
            return new Album[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(bucketId);
        dest.writeString(coverPath);
        dest.writeString(displayName);
        dest.writeLong(count);
        dest.writeInt(mediaType);
    }

    public String getBucketId() {
        return bucketId;
    }

    public void setBucketId(String bucketId) {
        this.bucketId = bucketId;
    }

    public String getCoverPath() {
        return coverPath;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public int getMediaType() {
        return mediaType;
    }

    public void setMediaType(int mediaType) {
        this.mediaType = mediaType;
    }

    // 是否图片和视频选项
    public boolean isAll() {
        return AlbumCollection.ALBUM_ID_ALL.equals(bucketId);
    }

    // 是否所有视频选项
    public boolean isVideo() {
        return AlbumCollection.ALBUM_ID_VIDEO.equals(bucketId);
    }

    public boolean isEmpty() {
        return count == 0;
    }

    public void addCaptureCount() {
        count++;
    }

    public static Album valueOf(Cursor cursor) {
        return new Album(
                cursor.getString(cursor.getColumnIndex("bucket_id")),
                cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA)),
                cursor.getString(cursor.getColumnIndex("bucket_display_name")),
                cursor.getLong(cursor.getColumnIndex("count")),
                cursor.getInt(cursor.getColumnIndex(MediaStore.Files.FileColumns.MEDIA_TYPE)));
    }

    @Override
    public String toString() {
        return "album bucketId = " + bucketId
                + ", path = " + coverPath
                + ",displayName = " + displayName
                + ", count = " + count
                + ", mediaType = " + mediaType;

    }
}
