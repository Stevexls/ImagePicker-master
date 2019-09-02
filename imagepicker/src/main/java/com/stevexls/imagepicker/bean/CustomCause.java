package com.stevexls.imagepicker.bean;

import android.content.Context;
import android.support.v4.app.FragmentActivity;

import com.stevexls.imagepicker.utils.ToastUtils;
import com.stevexls.imagepicker.widget.CustomDialog;

public class CustomCause {

    public static final int TOAST = 0x00;
    public static final int DIALOG = 0x01;
    public static final int NONE = 0x02;

    private int mForm = TOAST;
    private String title;
    private String message;

    public CustomCause(String message) {
        this.message = message;
    }

    public CustomCause(String title, String message) {
        this.title = title;
        this.message = message;
    }

    public CustomCause(int form, String message) {
        mForm = form;
        this.message = message;
    }

    public CustomCause(int form, String title, String message) {
        mForm = form;
        this.title = title;
        this.message = message;
    }

    public void showCause(Context context) {
        switch (mForm) {
            case NONE:
                // do nothing
                break;
            case DIALOG:
                CustomDialog customDialog = CustomDialog.newInstance(title, message);
                customDialog.show(((FragmentActivity) context).getSupportFragmentManager(), CustomDialog.class.getName());
                break;
            case TOAST:
            default:
                ToastUtils.showToastShort(context, message);
                break;
        }
    }
}
