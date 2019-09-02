package com.stevexls.imagepicker.bean;

/**
 * Time：2019/3/27 16:22
 * Description:
 */
public class CaptureStrategy {
    public boolean isPublic;
    public String authority;
    public String directory;

    public CaptureStrategy(boolean isPublic, String authority) {
        this(isPublic, authority, null);
    }

    public CaptureStrategy(boolean isPublic, String authority, String directory) {
        this.isPublic = isPublic;
        this.authority = authority;
        this.directory = directory;
    }
}
