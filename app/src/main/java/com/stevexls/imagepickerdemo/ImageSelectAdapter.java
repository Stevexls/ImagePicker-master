package com.stevexls.imagepickerdemo;

import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.request.target.Target;
import com.stevexls.imagepicker.bean.Item;
import com.stevexls.imagepicker.bean.SelectionSpec;
import com.stevexls.imagepicker.utils.ScreenUtils;


import java.util.ArrayList;
import java.util.List;

/**
 * Time：2019/5/20 15:43
 * Description:
 * Author:592172833@qq.com
 */
public class ImageSelectAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Item> itemList;
    private Context mContext;
    private int maxSelectCount;
    private OnImageClickListener onImageClickListener;

    private int mImageResize = 0;

    public ImageSelectAdapter(Context context, List<Item> itemList, int maxSelectCount) {
        this.mContext = context;
        this.maxSelectCount = maxSelectCount;
        refreshData(itemList);
    }

    public void refreshData(List<Item> item) {
        if (item == null) {
            itemList = new ArrayList<>();
        } else {
            itemList = new ArrayList<>(item);
        }
        if (getItemCount() < maxSelectCount) {
            Item addImgItem = new Item();
            addImgItem.setId(-1);
            itemList.add(addImgItem);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new SelectImgHolder(LayoutInflater.from(mContext).inflate(R.layout.item_select_result, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        SelectImgHolder holder = (SelectImgHolder) viewHolder;
        final int position = holder.getAdapterPosition();
        final Item item = itemList.get(i);
        if (item.getId() == -1) {
            holder.ivCover.setImageResource(R.drawable.selector_image_add);

        } else {
            SelectionSpec.getInstance().imageEngine.loadThumbnail(mContext, getImageResize(),
                     holder.ivCover, item.uri);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onImageClickListener != null) {
                    onImageClickListener.onImageClick(itemList.size(), item, position);
                }
            }
        });
    }

    private int getImageResize() {
        if (mImageResize == 0) {
            int spanCount = 4;
            int screenWidth = ScreenUtils.getScreenWidth(mContext); // 屏幕宽度
            int availableWidth = screenWidth
                    - (mContext.getResources().getDimensionPixelSize(com.stevexls.imagepicker.R.dimen.media_grid_spacing) * (spanCount - 1)); // 获取除间隔外的可用宽度
            mImageResize = availableWidth / spanCount;  // 获取每个item的宽度
        }
        return mImageResize;
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public interface OnImageClickListener {
        void onImageClick(int totalCount, Item item, int position);
    }

    public void setOnImageClickListener(OnImageClickListener onImageClickListener) {
        this.onImageClickListener = onImageClickListener;
    }

    static class SelectImgHolder extends RecyclerView.ViewHolder {

        ImageView ivCover;

        public SelectImgHolder(@NonNull View itemView) {
            super(itemView);
            ivCover = itemView.findViewById(R.id.iv_cover);
        }
    }
}